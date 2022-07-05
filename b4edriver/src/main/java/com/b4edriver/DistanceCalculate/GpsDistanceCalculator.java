package com.b4edriver.DistanceCalculate;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;

import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.b4edriver.CommonClasses.Utils.DriverStatus;
import com.b4edriver.CommonClasses.Utils.Function;
import com.b4edriver.CommonClasses.Utils.MapUtils;
import com.b4edriver.Database.Database;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by manishsingh on 13/03/18.
 */

public class GpsDistanceCalculator {
    private static final long ALARM_REPEAT_INTERVAL = 30000;
    private static final String CHECK_LOCATION = "com.bike4everything.CHECK_LOCATION";
    private static long LOCATION_UPDATE_INTERVAL = 2000;
    public static final double MAX_ACCURACY = 200.0d;
    private static final double MAX_DISPLACEMENT_THRESHOLD = 200.0d;
    public static final double MAX_SPEED_THRESHOLD = 15.0d;
    //private static int METERING_PI_REQUEST_CODE = MeteringService.UPLOAD_PATH_PI_REQUEST_CODE;
    private static final String TAG = GpsDistanceCalculator.class.getSimpleName();
    public static final long WAITING_WINDOW_TIME_MILLIS = 5000;
    public static FusedLocationUpdate fusedLocationUpdate;
    public static GPSLocationUpdate gpsLocationUpdate;
    private static GpsDistanceCalculator instance;
    public static long lastLocationTime;
    public final double DISTANCE_RESET_TOLERANCE = 100.0d;
    LocationListener GSMlistener = new C07105();
    private LocationManager GSMmgr;
    public final double WAIT_TIME_RESET_TOLERANCE = 10000.0d;
    private double accumulativeSpeed;
    private Context context;
    private ArrayList<LatLngPair> deltaLatLngPairs = new ArrayList();
    private ArrayList<DirectionsAsyncTask> directionsAsyncTasks = new ArrayList();
    private FusedLocationFetcherBackgroundBalanced fusedLocationFetcherBackgroundBalanced;
    private GpsDistanceTimeUpdater gpsDistanceUpdater;
    private FusedLocationFetcherBackgroundHigh gpsForegroundLocationFetcher;
    public Location gsmLocation;
    public Location lastFusedLocation;
    public Location lastGPSLocation;
    private long lastWaitWindowTime;
    private int speedCounter;
    public double totalDistance;
    public double totalHaversineDistance;

    class C07083 implements Runnable {
        C07083() {
        }

        public void run() {
            GpsDistanceCalculator.this.connectGPSListener(GpsDistanceCalculator.this.context);
        }
    }

    class C07105 implements LocationListener {
        C07105() {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }

        public void onLocationChanged(Location location) {
            if (12 == Prefs.with(GpsDistanceCalculator.this.context).getInt(SPLabels.GPS_GSM_DISTANCE_COUNT, 0)) {
                GpsDistanceCalculator.this.GSMmgr.removeUpdates(GpsDistanceCalculator.this.GSMlistener);
                GpsDistanceCalculator.this.GSMmgr = null;
                Prefs.with(GpsDistanceCalculator.this.context).save(SPLabels.GPS_GSM_DISTANCE_COUNT, Prefs.with(GpsDistanceCalculator.this.context).getInt(SPLabels.GPS_GSM_DISTANCE_COUNT, 0) + 1);
            }
            GpsDistanceCalculator.this.gsmLocation = location;
        }
    }

    private class DirectionsAsyncTask extends AsyncTask<Void, Void, String> {
        Location currentLocation;
        LatLng destination;
        double displacementToCompare;
        long rowId;
        LatLng source;

        public DirectionsAsyncTask(LatLng source, LatLng destination, double displacementToCompare, Location currentLocation, long rowId) {
            this.source = source;
            this.destination = destination;
            this.displacementToCompare = displacementToCompare;
            this.currentLocation = currentLocation;
            this.rowId = rowId;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(Void... params) {
            try {
                LatLng source = new LatLng(this.source.latitude, this.source.longitude);
                LatLng destination = new LatLng(this.destination.latitude,this.destination.longitude);
                String url = Function.getDirectionsUrl(source, destination);

                return downloadUrl(url);
                //new String(((TypedByteArray) RestClient.getGoogleApiServices().getDirections(this.source.latitude + ","
                // + this.source.longitude, this.destination.latitude + "," + this.destination.longitude, Boolean.valueOf(false), "driving", Boolean.valueOf(false)).getBody()).getBytes());
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                GpsDistanceCalculator.this.updateGAPIDistance(result, this.displacementToCompare, this.source, this.destination, this.currentLocation, this.rowId);
            }
            GpsDistanceCalculator.this.directionsAsyncTasks.remove(this);
        }

        public boolean equals(Object o) {
            try {
                DirectionsAsyncTask matchO = (DirectionsAsyncTask) o;
                return (Utils.compareDouble(matchO.source.latitude, this.source.latitude) == 0 && Utils.compareDouble(matchO.source.longitude, this.source.longitude) == 0) || (Utils.compareDouble(matchO.destination.latitude, this.destination.latitude) == 0 && Utils.compareDouble(matchO.destination.longitude, this.destination.longitude) == 0);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
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

    class C15091 implements GPSLocationUpdate {
        C15091() {
        }

        public void onGPSLocationChanged(Location location) {
            try {
                if (((double) location.getAccuracy()) < 200.0d) {
                    if (10 >= Prefs.with(GpsDistanceCalculator.this.context).getInt(SPLabels.GPS_GSM_DISTANCE_COUNT, 0)) {
                        if (!(MapUtils.distance(GpsDistanceCalculator.this.gsmLocation, location) >= 2000.0d || Utils.compareDouble(location.getLatitude(), 0.0d) == 0 || Utils.compareDouble(location.getLongitude(), 0.0d) == 0)) {
                            GpsDistanceCalculator.this.drawLocationChanged(location);
                        }
                        Prefs.with(GpsDistanceCalculator.this.context).save(SPLabels.GPS_GSM_DISTANCE_COUNT, Prefs.with(GpsDistanceCalculator.this.context).getInt(SPLabels.GPS_GSM_DISTANCE_COUNT, 0) + 1);
                    } else {
                        if (!(Utils.compareDouble(location.getLatitude(), 0.0d) == 0 || Utils.compareDouble(location.getLongitude(), 0.0d) == 0)) {
                            GpsDistanceCalculator.this.drawLocationChanged(location);
                        }
                        try {
                            if (11 == Prefs.with(GpsDistanceCalculator.this.context).getInt(SPLabels.GPS_GSM_DISTANCE_COUNT, 0)) {
                                Prefs.with(GpsDistanceCalculator.this.context).save(SPLabels.GPS_GSM_DISTANCE_COUNT, Prefs.with(GpsDistanceCalculator.this.context).getInt(SPLabels.GPS_GSM_DISTANCE_COUNT, 0) + 1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Database.getInstance(GpsDistanceCalculator.this.context).updateDriverCurrentLocation(GpsDistanceCalculator.this.context, location);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                GpsDistanceCalculator.this.gpsDistanceUpdater.updateDistanceTime(GpsDistanceCalculator.this.totalDistance, GpsDistanceCalculator.this.getElapsedMillis(), GpsDistanceCalculator.getWaitTimeFromSP(GpsDistanceCalculator.this.context), GpsDistanceCalculator.this.lastGPSLocation, GpsDistanceCalculator.this.lastFusedLocation, GpsDistanceCalculator.this.totalHaversineDistance, true);
            } catch (Exception e22) {
                e22.printStackTrace();
            }
        }

        public void refreshLocationFetchers(Context context) {
            GpsDistanceCalculator.this.reconnectGPSHandler();
        }
    }

    class C15102 implements FusedLocationUpdate {
        C15102() {
        }

        public void onFusedLocationChanged(Location location) {
            GpsDistanceCalculator.this.lastFusedLocation = location;
            GpsDistanceCalculator.this.gpsDistanceUpdater.updateDistanceTime(GpsDistanceCalculator.this.totalDistance, GpsDistanceCalculator.this.getElapsedMillis(), GpsDistanceCalculator.getWaitTimeFromSP(GpsDistanceCalculator.this.context), GpsDistanceCalculator.this.lastGPSLocation, GpsDistanceCalculator.this.lastFusedLocation, GpsDistanceCalculator.this.totalHaversineDistance, false);
        }
    }

    private GpsDistanceCalculator(Context context, GpsDistanceTimeUpdater gpsDistanceUpdater, double totalDistance, long lastLocationTime, double totalHaversineDistance) {
        this.context = context;
        this.totalDistance = totalDistance;
        this.lastGPSLocation = null;
        this.totalHaversineDistance = totalHaversineDistance;
        this.lastFusedLocation = null;
        this.lastLocationTime = lastLocationTime;
        this.gpsDistanceUpdater = gpsDistanceUpdater;
        this.deltaLatLngPairs = new ArrayList();
        this.directionsAsyncTasks = new ArrayList();
        this.accumulativeSpeed = 0.0d;
        this.speedCounter = 0;
        this.lastWaitWindowTime = System.currentTimeMillis();
        disconnectGPSListener();
        this.gpsForegroundLocationFetcher = null;
        this.fusedLocationFetcherBackgroundBalanced = null;
        initializeGPSForegroundLocationFetcher(context);
       // start();
    }

    public static GpsDistanceCalculator getInstance(Context context, GpsDistanceTimeUpdater gpsDistanceUpdater, double totalDistance, long lastLocationTime, double totalHaversineDistance) {
        if (instance == null) {
            instance = new GpsDistanceCalculator(context, gpsDistanceUpdater, totalDistance, lastLocationTime, totalHaversineDistance);
        }
        instance.context = context;
        instance.gpsDistanceUpdater = gpsDistanceUpdater;
        instance.totalDistance = totalDistance;
        instance.totalHaversineDistance = totalHaversineDistance;
        instance.lastLocationTime = lastLocationTime;
        return instance;
    }

    public void start() {
        /*if (!isMeteringStateActive(this.context)) {
            saveTrackingToSP(this.context, 1);
            saveStartTimeToSP(this.context, System.currentTimeMillis());
            saveWaitTimeToSP(this.context, 0);
            saveTotalDistanceToSP(this.context, -1.0d);
            saveLastLocationTimeToSP(this.context, System.currentTimeMillis());
            Prefs.with(this.context).save(SPLabels.GPS_GSM_DISTANCE_COUNT, 0);
        }*/

        saveTrackingToSP(this.context, 1);
        saveStartTimeToSP(this.context, System.currentTimeMillis());
        saveWaitTimeToSP(this.context, 0);
        saveTotalDistanceToSP(this.context, -1.0d);
        saveLastLocationTimeToSP(this.context, System.currentTimeMillis());
        Prefs.with(this.context).save(SPLabels.GPS_GSM_DISTANCE_COUNT, 0);

        connectGPSListener(this.context);
        //setupMeteringAlarm(this.context);
        this.gpsDistanceUpdater.updateDistanceTime(this.totalDistance, getElapsedMillis(), getWaitTimeFromSP(this.context), this.lastGPSLocation, this.lastFusedLocation, this.totalHaversineDistance, true);
        // MyApplication.getInstance().writePathLogToFile("m", "totalDistance at start =" + this.totalDistance);
    }

    public void saveState() {
        saveData(this.context, this.lastGPSLocation, lastLocationTime);
    }

    public void stop() {
        disconnectGPSListener();
        //cancelMeteringAlarm(this.context);
        saveStartTimeToSP(this.context, System.currentTimeMillis());
        saveWaitTimeToSP(this.context, 0);
        saveLatLngToSP(this.context, 0.0d, 0.0d);
        saveTotalDistanceToSP(this.context, -1.0d);
        saveLastLocationTimeToSP(this.context, System.currentTimeMillis());
        saveTrackingToSP(this.context, 0);
        instance.totalDistance = -1.0d;
        instance.totalHaversineDistance = -1.0d;
        GpsDistanceCalculator gpsDistanceCalculator = instance;
        lastLocationTime = System.currentTimeMillis();
        instance.lastGPSLocation = null;
        instance.lastFusedLocation = null;
        instance.accumulativeSpeed = 0.0d;
        instance.speedCounter = 0;
        instance.lastWaitWindowTime = System.currentTimeMillis();
        // MyApplication.getInstance().writePathLogToFile("m", "totalDistance at stop =" + this.totalDistance);
    }


    private void initializeGPSForegroundLocationFetcher(Context context) {
        if (this.gpsForegroundLocationFetcher == null) {
            this.gpsForegroundLocationFetcher = new FusedLocationFetcherBackgroundHigh(context, LOCATION_UPDATE_INTERVAL);
        }
        initializeFusedLocationFetcherBackgroundBalanced(context);
        this.GSMmgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        this.GSMmgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0.0f, this.GSMlistener);
        initializeGpsLocationUpdate();
    }

    private void initializeFusedLocationFetcherBackgroundBalanced(Context context) {
        if (this.fusedLocationFetcherBackgroundBalanced == null) {
            this.fusedLocationFetcherBackgroundBalanced = new FusedLocationFetcherBackgroundBalanced(context, LOCATION_UPDATE_INTERVAL);
        }
    }

    private void initializeGpsLocationUpdate() {
        if (gpsLocationUpdate == null) {
            gpsLocationUpdate = new C15091();
        }
        initializeFusedLocationUpdate();
    }

    private void initializeFusedLocationUpdate() {
        if (fusedLocationUpdate == null) {
            fusedLocationUpdate = new C15102();
        }
    }

    private void connectGPSListener(Context context) {
        disconnectGPSListener();
        try {
            initializeGPSForegroundLocationFetcher(context);
            this.gpsForegroundLocationFetcher.connect();
            this.fusedLocationFetcherBackgroundBalanced.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void disconnectGPSListener() {
        try {
            if (this.gpsForegroundLocationFetcher != null) {
                this.gpsForegroundLocationFetcher.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (this.fusedLocationFetcherBackgroundBalanced != null) {
                this.fusedLocationFetcherBackgroundBalanced.destroy();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private synchronized void drawLocationChanged(Location location) {
        try {
            LatLng lastLatLng;
            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            long newLocationTime = System.currentTimeMillis();
            if (Utils.compareDouble(this.totalDistance, -1.0d) == 0) {
                this.totalDistance = 0.0d;
                this.totalHaversineDistance = 0.0d;
                this.lastGPSLocation = null;
                lastLocationTime = System.currentTimeMillis();
            }
            if (this.lastGPSLocation != null) {
                lastLatLng = new LatLng(this.lastGPSLocation.getLatitude(), this.lastGPSLocation.getLongitude());
            } else {
                this.gpsDistanceUpdater.drawOldPath();
                lastLatLng = getSavedLatLngFromSP(this.context);
            }
            long secondsDiff = (newLocationTime - lastLocationTime) / 1000;
            double displacement = MapUtils.distance(lastLatLng, currentLatLng);
            if (!(Utils.compareDouble(lastLatLng.latitude, 0.0d) == 0 || Utils.compareDouble(lastLatLng.longitude, 0.0d) == 0)) {
                this.totalHaversineDistance += displacement;
                saveTotalHaversineDistanceToSP(this.context, this.totalHaversineDistance);
            }
            double speedMPS = 0.0d;
            if (secondsDiff > 0) {
                speedMPS = displacement / ((double) secondsDiff);
            }
            if (speedMPS > MAX_SPEED_THRESHOLD) {
                reconnectGPSHandler();
            } else if (Utils.compareDouble(lastLatLng.latitude, 0.0d) == 0 || Utils.compareDouble(lastLatLng.longitude, 0.0d) == 0) {
                lastLocationTime = System.currentTimeMillis();
                saveData(this.context, this.lastGPSLocation, lastLocationTime);
                this.lastGPSLocation = location;
            } else {
                calculateWaitTime(speedMPS);
                boolean locationAccepted = addLatLngPathToDistance(lastLatLng, currentLatLng, location);
                if (this.lastGPSLocation == null) {
                   // MyApplication.getInstance().insertRideDataToEngagements("" + lastLatLng.latitude, "" + lastLatLng.longitude, "" + System.currentTimeMillis());
                  //  MyApplication.getInstance().writePathLogToFile("m", "first time lastLatLng =" + lastLatLng);
                }
                if (locationAccepted) {
                    this.lastGPSLocation = location;
                }
            }
           // MyApplication.getInstance().writePathLogToFile("m", "speedMPS=" + speedMPS + " currentLatLng =" + currentLatLng);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calculateWaitTime(double speedMPS) {
        if (System.currentTimeMillis() - this.lastWaitWindowTime < WAITING_WINDOW_TIME_MILLIS) {
            this.accumulativeSpeed += speedMPS;
            this.speedCounter++;
            return;
        }
        if (this.accumulativeSpeed / ((double) this.speedCounter) < 1.4d) {
            saveWaitTimeToSP(this.context, getWaitTimeFromSP(this.context) + WAITING_WINDOW_TIME_MILLIS);
        }
        this.accumulativeSpeed = 0.0d;
        this.speedCounter = 0;
        this.lastWaitWindowTime = System.currentTimeMillis();
    }

    public void reconnectGPSHandler() {
        disconnectGPSListener();
        new Handler().postDelayed(new C07083(), WAITING_WINDOW_TIME_MILLIS);
    }

    private synchronized boolean addLatLngPathToDistance(LatLng lastLatLng, LatLng currentLatLng, Location currentLocation) {
        boolean z;
        try {
            double displacement = MapUtils.distance(lastLatLng, currentLatLng);
            Log.m615e("Min disp", Prefs.with(this.context).getString(Constants.KEY_SP_METER_DISP_MIN_THRESHOLD, String.valueOf(14.0d)));
            if (Utils.compareDouble(displacement, Double.parseDouble(Prefs.with(this.context).getString(Constants.KEY_SP_METER_DISP_MIN_THRESHOLD, String.valueOf(14.0d)))) == 1 && Utils.compareDouble(displacement, Double.parseDouble(Prefs.with(this.context).getString(Constants.KEY_SP_METER_DISP_MAX_THRESHOLD, String.valueOf(200.0d)))) == -1) {
                if (updateTotalDistance(lastLatLng, currentLatLng, displacement, currentLocation)) {
                    if (AppPreferencesDriver.getDriverStatus(this.context).equalsIgnoreCase(DriverStatus.ON_TRIP)) {
                        Database.getInstance(this.context).insertCurrentPathItem(-1, lastLatLng.latitude, lastLatLng.longitude, currentLatLng.latitude, currentLatLng.longitude, 0, 0);
                    }
                    this.gpsDistanceUpdater.updateDistanceTime(this.totalDistance, getElapsedMillis(), getWaitTimeFromSP(this.context), this.lastGPSLocation, this.lastFusedLocation, this.totalHaversineDistance, true);
                    this.gpsDistanceUpdater.addPathToMap(new PolylineOptions().add(lastLatLng, currentLatLng));
                }
                z = true;
            } else if (Utils.compareDouble(displacement, Double.parseDouble(Prefs.with(this.context).getString(Constants.KEY_SP_METER_DISP_MAX_THRESHOLD, String.valueOf(200.0d)))) >= 0) {
                long rowId = -1;
                if (AppPreferencesDriver.getDriverStatus(this.context).equalsIgnoreCase(DriverStatus.ON_TRIP)) {
                    rowId = Database.getInstance(this.context).insertCurrentPathItem(-1, lastLatLng.latitude, lastLatLng.longitude, currentLatLng.latitude, currentLatLng.longitude, 1, 1);
                }
                callGoogleDirectionsAPI(lastLatLng, currentLatLng, displacement, currentLocation, rowId);
                z = true;
            } else {
                z = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            z = true;
        }
        return z;
    }

    private synchronized boolean updateTotalDistance(LatLng lastLatLng, LatLng currentLatLng, double deltaDistance, Location currentLocation) {
        boolean validDistance;
        validDistance = false;
        if (deltaDistance > 0.0d && deltaDistance < 20001.0d) {
            try {
                LatLngPair latLngPair = new LatLngPair(lastLatLng, currentLatLng, deltaDistance);
                if (this.deltaLatLngPairs == null) {
                    this.deltaLatLngPairs = new ArrayList();
                }
                if (!this.deltaLatLngPairs.contains(latLngPair) && this.totalDistance < 200001.0d) {
                    this.totalDistance += deltaDistance;
                    this.deltaLatLngPairs.add(latLngPair);
                    validDistance = true;
                    lastLocationTime = System.currentTimeMillis();
                   // MyApplication.getInstance().insertRideDataToEngagements("" + currentLatLng.latitude, "" + currentLatLng.longitude, "" + System.currentTimeMillis());
                   // MyApplication.getInstance().writePathLogToFile("m", DateOperations.getTimeStampFromMillis(currentLocation.getTime()) + "," + currentLatLng.latitude + "," + currentLatLng.longitude + "," + currentLocation.getAccuracy() + "," + this.totalDistance + "," + deltaDistance + "," + lastLatLng.latitude + "," + lastLatLng.longitude + "," + DateOperations.getTimeStampFromMillis(lastLocationTime) + "," + this.totalHaversineDistance);
                    saveData(this.context, this.lastGPSLocation, lastLocationTime);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return validDistance;
    }

    private synchronized void callGoogleDirectionsAPI(LatLng lastLatLng, LatLng currentLatLng, double displacement, Location currentLocation, long rowId) {
        if (this.directionsAsyncTasks == null) {
            this.directionsAsyncTasks = new ArrayList();
        }
        DirectionsAsyncTask directionsAsyncTask = new DirectionsAsyncTask(lastLatLng, currentLatLng, displacement, currentLocation, rowId);
        if (!this.directionsAsyncTasks.contains(directionsAsyncTask)) {
            this.directionsAsyncTasks.add(directionsAsyncTask);
            directionsAsyncTask.execute();
        }
    }

    private synchronized void updateGAPIDistance(String result, double displacementToCompare, LatLng source, LatLng destination, Location currentLocation, long rowId) {
        double distanceOfPath = Double.MAX_VALUE;
        try {
            JSONObject jSONObject = new JSONObject(result);
            if ("OK".equalsIgnoreCase(jSONObject.getString("status"))) {
                distanceOfPath = jSONObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject(Constants.KEY_DISTANCE).getDouble(FirebaseAnalytics.Param.VALUE);
            }
            if (Utils.compareDouble(distanceOfPath, 1.5d * displacementToCompare) <= 0) {
                if (updateTotalDistance(source, destination, distanceOfPath, currentLocation)) {
                    this.gpsDistanceUpdater.updateDistanceTime(this.totalDistance, getElapsedMillis(), getWaitTimeFromSP(this.context), this.lastGPSLocation, this.lastFusedLocation, this.totalHaversineDistance, true);
                    List<LatLng> list = MapUtils.decodeDirectionsPolyline(jSONObject.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline").getString("points"));
                    PolylineOptions polylineOptions = new PolylineOptions();
                    for (int z = 0; z < list.size() - 1; z++) {
                        LatLng src = list.get(z);
                        LatLng dest = list.get(z + 1);
                        polylineOptions.add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude));
                        if (rowId != -1) {
                            if (AppPreferencesDriver.getDriverStatus(this.context).equalsIgnoreCase(DriverStatus.ON_TRIP)) {
                                Database.getInstance(this.context).insertCurrentPathItem(rowId, src.latitude, src.longitude, dest.latitude, dest.longitude, 0, 0);
                            }
                        }
                    }
                    if (rowId != -1) {
                        Database.getInstance(this.context).updateCurrentPathItemSectionIncomplete(rowId, 0);
                    }
                    this.gpsDistanceUpdater.addPathToMap(polylineOptions);
                }
               // MyApplication.getInstance().writePathLogToFile("m", "gapi case successful");
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (updateTotalDistance(source, destination, displacementToCompare, currentLocation)) {
                this.gpsDistanceUpdater.updateDistanceTime(this.totalDistance, getElapsedMillis(), getWaitTimeFromSP(this.context), this.lastGPSLocation, this.lastFusedLocation, this.totalHaversineDistance, true);
                this.gpsDistanceUpdater.addPathToMap(new PolylineOptions().add(source, destination));
                if (rowId != -1) {
                    Database.getInstance(this.context).updateCurrentPathItemSectionIncompleteAndGooglePath(rowId, 0, 0);
                }
            }
           // MyApplication.getInstance().writePathLogToFile("m", "gapi case unsuccessful");
        }
    }

    public long getElapsedMillis() {
        long timeDiff = System.currentTimeMillis() - getStartTimeFromSP(this.context);
        if (timeDiff > 0) {
            return timeDiff;
        }
        return 0;
    }

    public void saveData(Context context, Location location, long lastLocationTime) {
        if (location != null) {
            saveLatLngToSP(context, location.getLatitude(), location.getLongitude());
            double savedTotalDist = getTotalDistanceFromSP(context);
            if (this.totalDistance < savedTotalDist) {
                this.totalDistance = savedTotalDist;
            } else {
                saveTotalDistanceToSP(context, this.totalDistance);
            }
            saveLastLocationTimeToSP(context, lastLocationTime);
        }
    }

    public static synchronized void saveLatLngToSP(Context context, double latitude, double longitude) {
        synchronized (GpsDistanceCalculator.class) {
            Prefs.with(context).save(SPLabels.LOCATION_LAT, "" + latitude);
            Prefs.with(context).save(SPLabels.LOCATION_LNG, "" + longitude);
        }
    }

    public static synchronized LatLng getSavedLatLngFromSP(Context context) {
        LatLng latLng;
        synchronized (GpsDistanceCalculator.class) {
            latLng = new LatLng(Double.parseDouble(Prefs.with(context).getString(SPLabels.LOCATION_LAT, "0")), Double.parseDouble(Prefs.with(context).getString(SPLabels.LOCATION_LNG, "0")));
        }
        return latLng;
    }

    public static synchronized void saveTotalDistanceToSP(Context context, double totalDistance) {
        synchronized (GpsDistanceCalculator.class) {
            if(AppPreferencesDriver.getDriverStatus(context).equalsIgnoreCase(DriverStatus.ON_TRIP)) {
                Prefs.with(context).save(SPLabels.TOTAL_DISTANCE, "" + totalDistance);
                Database.getInstance(context).updateTotalDistance(totalDistance);
            }
        }
    }

    public static synchronized double getTotalDistanceFromSP(Context context) {
        double spDistance;
        synchronized (GpsDistanceCalculator.class) {
            spDistance = Double.parseDouble(Prefs.with(context).getString(SPLabels.TOTAL_DISTANCE, "-1"));
            double dbDistance = Database.getInstance(context).getTotalDistance();
            if (spDistance < dbDistance) {
                spDistance = dbDistance;
            }
        }
        return spDistance;
    }

    public static synchronized void saveTotalHaversineDistanceToSP(Context context, double totalHaversineDistance) {
        synchronized (GpsDistanceCalculator.class) {
            Prefs.with(context).save(SPLabels.TOTAL_HAVERSINE_DISTANCE, "" + totalHaversineDistance);
        }
    }

    public static synchronized double getTotalHaversineDistanceFromSP(Context context) {
        double parseDouble;
        synchronized (GpsDistanceCalculator.class) {
            parseDouble = Double.parseDouble(Prefs.with(context).getString(SPLabels.TOTAL_HAVERSINE_DISTANCE, "-1"));
        }
        return parseDouble;
    }

    public static synchronized void saveLastLocationTimeToSP(Context context, long lastLocationTime) {
        synchronized (GpsDistanceCalculator.class) {
            Prefs.with(context).save(SPLabels.LOCATION_TIME, "" + lastLocationTime);
        }
    }

    public static synchronized long getLastLocationTimeFromSP(Context context) {
        long parseLong;
        synchronized (GpsDistanceCalculator.class) {
            parseLong = Long.parseLong(Prefs.with(context).getString(SPLabels.LOCATION_TIME, "" + System.currentTimeMillis()));
        }
        return parseLong;
    }

    public static synchronized void saveStartTimeToSP(Context context, long startTime) {
        synchronized (GpsDistanceCalculator.class) {
            Prefs.with(context).save(SPLabels.START_TIME, "" + startTime);
        }
    }

    public static synchronized long getStartTimeFromSP(Context context) {
        long parseLong;
        synchronized (GpsDistanceCalculator.class) {
            parseLong = Long.parseLong(Prefs.with(context).getString(SPLabels.START_TIME, "0"));
        }
        return parseLong;
    }

    public static synchronized void saveWaitTimeToSP(Context context, long waitTime) {
        synchronized (GpsDistanceCalculator.class) {
            Prefs.with(context).save("wait_time", "" + waitTime);
            Database.getInstance(context).updateWaitTime(String.valueOf(waitTime));
        }
    }

    public static synchronized long getWaitTimeFromSP(Context context) {
        long waitTimeFromDB;
        synchronized (GpsDistanceCalculator.class) {
            try {
                long waitTimeFromSP = Long.parseLong(Prefs.with(context).getString("wait_time", "0"));
                Log.m615e("invalid long", Database.getInstance(context).getWaitTimeFromDB());
                waitTimeFromDB = Long.parseLong(Database.getInstance(context).getWaitTimeFromDB());
                if (waitTimeFromDB < waitTimeFromSP) {
                    waitTimeFromDB = waitTimeFromSP;
                }
            } catch (Exception e) {
                e.printStackTrace();
                waitTimeFromDB = 0;
            }
        }
        return waitTimeFromDB;
    }

    public static synchronized void saveTrackingToSP(Context context, int tracking) {
        synchronized (GpsDistanceCalculator.class) {
            Prefs.with(context).save(SPLabels.TRACKING, tracking);
        }
    }

    public static synchronized int getTrackingFromSP(Context context) {
        int i = 0;
        synchronized (GpsDistanceCalculator.class) {
            try {
                i = Prefs.with(context).getInt(SPLabels.TRACKING, 0);
            } catch (Exception e) {
                e.printStackTrace();
                saveTrackingToSP(context, 0);
            }
        }
        return i;
    }

   /* public static synchronized boolean isMeteringStateActive(Context context) {
        boolean z = true;
        synchronized (GpsDistanceCalculator.class) {
            if (1 != getTrackingFromSP(context)) {
                if (Prefs.with(context).contains(SPLabels.TRACKING)) {
                    z = false;
                } else {
                    String meteringState = Database2.getInstance(context).getMetringState();
                    String meteringStateSp = Prefs.with(context).getString(SPLabels.METERING_STATE, Database2.OFF);
                    if (Database2.ON.equalsIgnoreCase(meteringState) || Database.ON.equalsIgnoreCase(meteringStateSp)) {
                        saveTrackingToSP(context, 1);
                    } else {
                        z = false;
                    }
                }
            }
        }
        return z;
    }*/

   /* public void updateDistanceInCaseOfReset(double distance, long rideTime, long waitTime) {
        MyApplication.getInstance().writePathLogToFile("m", "updateDistanceInCaseOfReset func distance from server:" + distance + " & totalDistance:" + this.totalDistance + " & waitTime:" + waitTime + " & rideTime:" + rideTime);
        if (distance > this.totalDistance + 100.0d) {
            this.totalDistance += distance;
            saveTotalDistanceToSP(this.context, this.totalDistance);
            MyApplication.getInstance().writePathLogToFile("m", "updateDistanceInCaseOfReset func totalDistance updated:" + this.totalDistance);
        }
        long spElapsedTime = getElapsedMillis();
        if (((double) rideTime) > ((double) spElapsedTime) + 10000.0d) {
            saveStartTimeToSP(this.context, (System.currentTimeMillis() - rideTime) - spElapsedTime);
            MyApplication.getInstance().writePathLogToFile("m", "updateDistanceInCaseOfReset func rideTime updated:" + ((System.currentTimeMillis() - rideTime) - spElapsedTime));
        }
        long spWaitTime = getWaitTimeFromSP(this.context);
        if (((double) waitTime) > ((double) spWaitTime) + 10000.0d) {
            saveWaitTimeToSP(this.context, waitTime + spWaitTime);
            MyApplication.getInstance().writePathLogToFile("m", "updateDistanceInCaseOfReset func waitTime updated:" + (waitTime + spWaitTime));
        }
        final double d = distance;
        final long j = rideTime;
        final long j2 = waitTime;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                Toast.makeText(GpsDistanceCalculator.this.context, "Distance reset case => " + d + ", " + j + ", " + j2, 1).show();
            }
        });
    }
*/
   /* public static synchronized void saveDriverScreenModeMetering(Context context, DriverScreenMode driverScreenMode) {
        synchronized (GpsDistanceCalculator.class) {
            Prefs.with(context).save(SPLabels.DRIVER_SCREEN_MODE_METERING, driverScreenMode.getOrdinal());
        }
    }

    public static synchronized int getDriverScreenModeSP(Context context) {
        int state;
        synchronized (GpsDistanceCalculator.class) {
            state = Prefs.with(context).getInt(SPLabels.DRIVER_SCREEN_MODE_METERING, DriverScreenMode.D_IN_RIDE.getOrdinal());
        }
        return state;
    }*/
}
