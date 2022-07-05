package com.b4edriver.CommonClasses.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.b4edriver.CommonClasses.ServerConnection.Util;
import com.b4edriver.CommonClasses.Utils.DirectionsJSONParser;
import com.b4edriver.CommonClasses.Utils.Function;
import com.b4elibrary.Logger;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by MAX on 10-Sep-16.
 */
public class CreateRoutesServices extends IntentService {

    public static final String RECEIVER = "RECEIVER";
    public static int resultCode = 11;
    protected ResultReceiver mReceiver;

    public CreateRoutesServices() {
        super("CreateRoutesServices");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mReceiver = intent.getParcelableExtra(RECEIVER);
        LatLng sourceLocation = new LatLng(intent.getDoubleExtra("pick_lat", 0.0), intent.getDoubleExtra("pick_lng", 0.0));
        LatLng destinationLocation = new LatLng(intent.getDoubleExtra("drop_lat", 0.0), intent.getDoubleExtra("drop_lng", 0.0));

        String url = Function.getDirectionsUrl(sourceLocation, destinationLocation);
        RoutesDownloadTask downloadTask = new RoutesDownloadTask(this);
        downloadTask.execute(url);

        return super.onStartCommand(intent, flags, startId);
    }

    //deliverResultToReceiver(points,distances);
    private void deliverResultToReceiver(ArrayList<LatLng> points, String distances) {

        Bundle bundle = new Bundle();
        bundle.putInt("resultCode", resultCode);
        bundle.putParcelableArrayList("points", points);
        bundle.putString("distances", distances);
        mReceiver.send(resultCode, bundle);

        // stopSelf();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    public class RoutesDownloadTask extends AsyncTask<String, Void, String> {

        Context context;
        String distanceTime;

        public RoutesDownloadTask(Context context) {
            this.context = context;
        }

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Logger.log("Background Task data ", url[0]);
            } catch (Exception e) {
                Logger.log("Background Task", e.toString());
            }
            return data;
        }


        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);


        }

        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();

                br.close();

            } catch (Exception e) {
                //Logger.log("Exception while downloading url", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }


        public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

            // Parsing the data in non-ui thread
            @Override
            protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

                JSONObject jObject;
                List<List<HashMap<String, String>>> routes = null;


                try {
                    jObject = new JSONObject(jsonData[0]);

                    DirectionsJSONParser parser = new DirectionsJSONParser();

                    // Starts parsing data
                    routes = parser.parse(jObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return routes;
            }

            // Executes in UI thread, after the parsing process
            @Override
            protected void onPostExecute(List<List<HashMap<String, String>>> result) {
                PolylineOptions lineOptions = null;
                ArrayList<LatLng> points = null;
                String distance = "";
                String duration = "";

                try {
                    if (result.size() < 1) {
                        //Toast.makeText(ParserTask.this, "No Points", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }catch (NullPointerException e){
                    return;
                }
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    Logger.log("(CreateRoutesServices", result.size()+"");
                    if (i == 1) {
                        lineOptions = new PolylineOptions();
                        points = new ArrayList<LatLng>();
                        // Fetching i-th route
                        List<HashMap<String, String>> path = result.get(i);
                        // Fetching all the points in i-th route
                        for (int j = 0; j < path.size(); j++) {
                            HashMap<String, String> point = path.get(j);

                            if (j == 0) {    // Get distance from the list
                                distance = point.get("distance");
                                continue;
                            } else if (j == 1) { // Get duration from the list
                                duration = point.get("duration");
                                continue;
                            }else if(j == 2){
                                continue;
                            }else if(j == 3){
                                continue;
                            }

                            //Logger.log("(CreateRoutesServices",point.get("lat")+"");


                                double lat = Double.parseDouble(point.get("lat"));
                                double lng = Double.parseDouble(point.get("lng"));
                                LatLng position = new LatLng(lat, lng);

                                points.add(position);

                                com.google.android.gms.maps.model.LatLng mapPoint =
                                        new com.google.android.gms.maps.model.LatLng(lat, lng);
                                builder.include(mapPoint);


                        }
                        lineOptions.addAll(points);
                        lineOptions.width(4);
                        lineOptions.color(Color.BLUE);

                        String distances = distance.replace("km", "").replace("m", "").replace(" ", "").replace(",", "");
                        // map.addPolyline(lineOptions);
                        // map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 0));
                        deliverResultToReceiver(points, distances);
                    }
                    // distanceTxt.setText(distance + " - " + duration);


                }
            }
        }
    }
}
