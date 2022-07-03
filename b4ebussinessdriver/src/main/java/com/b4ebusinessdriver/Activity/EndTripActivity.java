package com.b4ebusinessdriver.Activity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.b4ebusinessdriver.Model.DropAddress;
import com.b4ebusinessdriver.Model.FareCard;
import com.b4ebusinessdriver.Model.LatLngs;
import com.b4ebusinessdriver.Model.OrderDetails;
import com.b4ebusinessdriver.R;
import com.b4ebusinessdriver.Utils.AppConstant;
import com.b4ebusinessdriver.Utils.AppPreferences;
import com.b4ebusinessdriver.Utils.DirectionsJSONParser;
import com.b4ebusinessdriver.Utils.Function;
import com.b4ebusinessdriver.Utils.Helper;
import com.b4ebusinessdriver.Utils.ProgressDialog;
import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.b4elibrary.Logger;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.GeoApiContext;
import com.google.maps.RoadsApi;
import com.google.maps.model.SnappedPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class EndTripActivity extends BaseActivity {

    private static final int PAGE_SIZE_LIMIT = 100;

    private static final int PAGINATION_OVERLAP = 5;
    LinearLayout dropdetails;
    Button endTrip;
    List<String> waypoints = new ArrayList<>();

    List<LatLng> mCapturedLocations;
    List<SnappedPoint> mSnappedPoints;
    float distanceInMeters = 0f;
    String estfareFinal;
    List<OrderDetails> orderDetails;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_trip);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        dropdetails = (LinearLayout) findViewById(R.id.dropdetails);
        endTrip = findViewById(R.id.endTrip);

        AppPreferences.setEndTrip(EndTripActivity.this, true);


        orderDetails = databaseHandler.getAllOrders();

        mCapturedLocations = new ArrayList<>();

        List<LatLngs> latLngsList = databaseHandler.getAllDistance();
        for(int i=0; i< latLngsList.size(); i++){
            mCapturedLocations.add(new LatLng(latLngsList.get(i).getCurrentLat(),latLngsList.get(i).getCurrentLng()));
        }


        distanceCalculate();


        endTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(TextUtils.isEmpty(estfareFinal)){
                    distanceCalculate();
                }else{
                    ProgressDialog.getInstance(EndTripActivity.this).show();
                    endTrip.setEnabled(false);
                    new FinishTrip().execute(estfareFinal);



                }
            }
        });

    }

    private void distanceCalculate() {
        ProgressDialog.getInstance(EndTripActivity.this).show();



        LatLng pickupLatLng = null, dropLatLng = null;

        for(int i = 0; i<orderDetails.size(); i++){
            pickupLatLng = new LatLng(orderDetails.get(i).getPickLat(),orderDetails.get(i).getPickLng());
            float totalAmount = 0f;
            List<DropAddress> dropAddressList = orderDetails.get(i).getDropAddressList();
            for(int k=0; k<dropAddressList.size(); k++){
                if(k == dropAddressList.size()-1){
                    dropLatLng = new LatLng(dropAddressList.get(k).getDropLat(), dropAddressList.get(k).getDropLng());

                }else{
                    waypoints.add(dropAddressList.get(k).getDropLat()+","+dropAddressList.get(k).getDropLng());

                }

                totalAmount +=  dropAddressList.get(k).getDropAmount().equals("") ? 0f : Float.parseFloat(dropAddressList.get(k).getDropAmount());
                LinearLayout linearLayout = new LinearLayout(EndTripActivity.this);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                TextView dropName = new TextView(EndTripActivity.this);
                dropName.setTextColor(getResources().getColor(R.color.black));
                dropName.setTextSize(18f);
                TextView dropamount = new TextView(EndTripActivity.this);
                dropamount.setTextColor(getResources().getColor(R.color.black));
                dropamount.setTextSize(18f);

                dropName.setText(dropAddressList.get(k).getDropName() +" : Rs. ");
                dropamount.setText(""+dropAddressList.get(k).getDropAmount());


                linearLayout.addView(dropName);
                linearLayout.addView(dropamount);
                dropdetails.addView(linearLayout);

                if(k == dropAddressList.size()-1){

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
                    params.setMargins(0,10,0,10);
                    View view = new View(EndTripActivity.this);
                    view.setLayoutParams(params);
                    view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                    LinearLayout totallinearLayout = new LinearLayout(EndTripActivity.this);
                    totallinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    TextView totaldropName = new TextView(EndTripActivity.this);
                    totaldropName.setTextColor(getResources().getColor(R.color.black));
                    totaldropName.setTextSize(18f);
                    TextView totaldropamount = new TextView(EndTripActivity.this);
                    totaldropamount.setTextColor(getResources().getColor(R.color.colorPrimary));
                    totaldropamount.setTextSize(20f);

                    totaldropName.setText("Total : ");
                    totaldropamount.setText("Rs. "+totalAmount);


                    totallinearLayout.addView(totaldropName);
                    totallinearLayout.addView(totaldropamount);
                    dropdetails.addView(view);
                    dropdetails.addView(totallinearLayout);
                }
            }

        }

         distanceInMeters = Float.parseFloat(AppPreferences.getTotalDistance(EndTripActivity.this));



        if(distanceInMeters == 0 || distanceInMeters == -1) {

            String url = Function.getDirectionsUrlWaypont(pickupLatLng, dropLatLng,waypoints);
            // Logger.log("routes", url);
            RoutesDownloadTask downloadTask = new RoutesDownloadTask(EndTripActivity.this);
            downloadTask.execute(url);
        }else {
            fareCalculate();
        }
    }


    private class FinishTrip extends AsyncTask<String, Void, String> {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        public FinishTrip(){

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject data = new JSONObject();
                data.put("method", AppConstant.END_DELIVERY);
                data.put("driver_id", AppPreferences.getUserId(EndTripActivity.this));
                data.put("delivery_id", AppPreferences.getDeliveryId(EndTripActivity.this));
                data.put("amount", params[0]);
                data.put("distanceInMeters", distanceInMeters);
                data.put("waypoints", waypoints);

                Log.e("Response_Response", data.toString());
                //json.put("notification", dataJson);
                RequestBody body = RequestBody.create(JSON, data.toString());
                Request request = new Request.Builder()
                        .url(AppConstant.B4E_DRIVER_BUSINESS_MANAGE_DELIVERY)
                        .post(body)
                        .build();
                okhttp3.Response response = client.newCall(request).execute();
                result = response.body().string();


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String finalResponse) {
            super.onPostExecute(finalResponse);
            Log.e("Response_Response", finalResponse);
            String s = finalResponse;
            try {
                JSONObject jsonObject = new JSONObject(s);

                if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                    AppPreferences.setEndTrip(EndTripActivity.this, false);
                    AppPreferences.setDeliveryId(EndTripActivity.this,0);
                    AppPreferencesDriver.setTripId(EndTripActivity.this,"");

                    AppPreferences.setOntrip(EndTripActivity.this, false);
                    databaseHandler.deleteAllLatlngs();
                    databaseHandler.deleteAllOrders();
                    Logger.log("delivery_id end", AppPreferences.getDeliveryId(EndTripActivity.this)+"");
                   // isStart = true;
                    AppPreferences.setTotalDistance(EndTripActivity.this, "0");
                    Intent intent = new Intent(EndTripActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ActivityOptions options =
                                ActivityOptions.makeCustomAnimation(EndTripActivity.this, R.anim.slide_down, R.anim.slide_down);
                        startActivity(intent, options.toBundle());
                    } else {
                        startActivity(intent);
                    }
                    finish();
                } else  {
                    showAlertDialog(EndTripActivity.this,"Error!",jsonObject.getJSONArray("result").getJSONObject(0).getString("msg"), "OK");

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }catch (Exception e) {
                e.printStackTrace();
            }
            ProgressDialog.getInstance(EndTripActivity.this).dismiss();

            endTrip.setEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {

    }






    public class RoutesDownloadTask extends AsyncTask<String, Void, String> {

        Context context;
        String distanceTime;

        public RoutesDownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Logger.log("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            parserTask.execute(result);
        }

        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.connect();

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
                Logger.log("routes", "111");

                try {

                    jObject = new JSONObject(jsonData[0]);
                    DirectionsJSONParser parser = new DirectionsJSONParser();

                    // Starts parsing data
                    routes = parser.parse(jObject);
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
                return routes;
            }

            // Executes in UI thread, after the parsing process
            @Override
            protected void onPostExecute(List<List<HashMap<String, String>>> result) {
                PolylineOptions lineOptions = null;
                ArrayList<LatLng> points = null;
                String distance = "0";
                String duration = "1";
                String durationValue = "1";
                String distanceValue = "0";

                try {
                    if (result.size() < 1) {
                        //Toast.makeText(ParserTask.this, "No Points", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NullPointerException e) {
                    return;
                }
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
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
                                distanceValue = point.get("value");
                                continue;
                            } else if (j == 1) { // Get duration from the list
                                duration = point.get("duration");
                                durationValue = point.get("value");
                                Logger.log("duration ss", duration + "::" + durationValue);
                                continue;
                            } else if (j == 2) { // Get duration from the list

                                continue;
                            }

                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);
                            //Logger.log("routes",position.toString());
                            points.add(position);
                            LatLng mapPoint =
                                    new LatLng(lat, lng);
                            builder.include(mapPoint);


                            mCapturedLocations.add(mapPoint);

                        }

                    }
                }
                distanceInMeters = Float.parseFloat(distanceValue);
                fareCalculate();
                //mTaskSnapToRoads.execute();

            }
        }

    }

    public void fareCalculate(){


        FareCard fareCard = databaseHandler.getFareCard();
        int limitKm = Integer.parseInt(fareCard.getLimitKm());
        int perKm = Integer.parseInt(fareCard.getPerKmFare());
        int baseFare = Integer.parseInt(fareCard.getBaseFare());
        int returnFare = Integer.parseInt(fareCard.getReturnFare());
        int gstCharge = Integer.parseInt(fareCard.getGst());


        List<FareCard.Delivery> dropPointList = fareCard.getDeliveries();


        double aa = Double.valueOf(distanceInMeters);

        double bb = aa/1000;

        double cc = 0;

        if(bb <= limitKm){
            int tptalDroppoints = waypoints.size();
            int dropPoint = 0;
            int dropFare = 0;
            if(tptalDroppoints != 0) {
                for (int i = 0; i < dropPointList.size(); i++) {
                    try {
                        dropFare = Integer.parseInt(dropPointList.get(i).getFare());
                        dropPoint = Integer.parseInt(dropPointList.get(i).getDropPoint());

                    }catch (NumberFormatException e) {
                        cc = baseFare + (tptalDroppoints * dropFare);
                        break;
                    }
                    if (tptalDroppoints <= dropPoint) {
                        cc = baseFare + (tptalDroppoints * dropFare);
                        break;
                    } else {
                        cc = baseFare + (tptalDroppoints * dropFare);
                    }
                }
            }else{
                cc = baseFare;
            }
            Logger.log("ESTFAREACTU True", "tptalDroppoints: "+tptalDroppoints+"\n"
                    +"(tptalDroppoints <= dropPoint && tptalDroppoints != 0): "+(tptalDroppoints <= dropPoint && tptalDroppoints != 0)+"\n"
            );

        }else {
            double dd = bb - limitKm;
            int tptalDroppoints = waypoints.size();
            int dropPoint = 0;
            int dropFare = 0;
            if(tptalDroppoints != 0) {
                for (int i = 0; i < dropPointList.size(); i++) {
                    try {
                        dropFare = Integer.parseInt(dropPointList.get(i).getFare());
                        dropPoint = Integer.parseInt(dropPointList.get(i).getDropPoint());

                    }catch (NumberFormatException e) {
                        cc = (perKm * dd) + baseFare + (tptalDroppoints * dropFare);
                        break;
                    }
                    if (tptalDroppoints <= dropPoint) {
                        cc = (perKm * dd) + baseFare + (tptalDroppoints * dropFare);
                        Logger.log("ESTFAREACTU False", "\n"
                                +"(perKm * dd) + baseFare + (tptalDroppoints * dropFare): "+(perKm * dd) + baseFare + (tptalDroppoints * dropFare)+"\n"

                                +"cc: "+cc+"\n"
                                +"perKm: "+perKm+"\n"
                                +"dd: "+dd+"\n"
                                +"tptalDroppoints: "+tptalDroppoints+"\n"
                                +"dropPoint: "+dropPoint+"\n"
                                +"dropFare: "+dropFare+"\n"
                        );
                        break;
                    } else {
                        cc = (perKm * dd) + baseFare;
                    }
                }
            }else{
                cc = (perKm * dd) + baseFare;
            }

            Logger.log("ESTFAREACTU False", "tptalDroppoints: "+tptalDroppoints+"\n"
                    +"(tptalDroppoints <= dropPoint): "+(tptalDroppoints <= dropPoint)+"\n"
                    +"bb: "+bb+"-"+"limitKm: "+limitKm+"\n"
                    +"=dd: "+dd+"\n"
                    +"dropFare: "+dropFare+"\n"
            );
        }


        double estfare = cc + (orderDetails.get(0).isReturnrequired() ? returnFare : 0);

        double gst = (estfare * gstCharge / 100);

        estfare = estfare + gst;
        DecimalFormat df = new DecimalFormat("#");
         estfareFinal = df.format(estfare);
        Logger.log("ESTFAREACTU", "limitKm"+limitKm+"\n"
                +"perKm: "+perKm+"\n"
                +"baseFare: "+baseFare+"\n"
                +"returnFare: "+returnFare+"\n"
                +"gstCharge: "+gstCharge+"\n"
                +"dropPointList: "+dropPointList.size()+"\n"
                +"aa meter: "+aa+"\n"
                +"bb km: "+bb+"\n"
                +"cc fare: "+cc+"\n"
                +"gst fare: "+gst+"\n"
                +"estfare fare: "+estfare+"\n"
                +"estfareFinal fare: "+estfareFinal+"\n"
                +"bb <= limitKm: "+(bb <= limitKm)+"\n"
        );

        ProgressDialog.getInstance(EndTripActivity.this).dismiss();
    }


    ///  total distance  = bill amount = total drop amount = per drop point distance = cash to collected amount =
}
