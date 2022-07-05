package com.b4edriver.CommonClasses.Services;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.b4edriver.CommonClasses.ServerConnection.JSONParser;
import com.b4edriver.CommonClasses.ServerConnection.VolleyCallBack;
import com.b4edriver.CommonClasses.Utils.AppConstantDriver;
import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.b4edriver.CommonClasses.Utils.DriverStatus;
import com.b4edriver.CommonClasses.Utils.Function;
import com.b4edriver.CommonClasses.Utils.Logs;
import com.b4edriver.Database.DBAdapter_Driver;
import com.b4edriver.DriverApp.TripStartedActivityDriver;
import com.b4edriver.Model.DistanceTravelled;
import com.b4edriver.Model.UserDriver;
import com.b4edriver.R;
import com.b4elibrary.Logger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


//import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Sanjay on 6/11/2016.
 */
public class FusedLocationService extends Service implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    public static FusedLocationService instance;
    private static final long INTERVAL = 5 * 1000;//1000
    private static final long FASTEST_INTERVAL = 2 * 1000;//500
    public static GoogleApiClient mGoogleApiClient;
    public static Location mPreviousLocation = null, mCurrentLocation = null;
    public Location mImmediatePreviousLatLng = null, mImmediatePreviousLatLng1 = null;
    public static String batteryStatus = "50", gps = "true";
    public static double totalDistance = 0, freeDistance = 0, toCustomerDistance = 0, tripDistance = 0; // it should be set to 0 when booking is completed
    public static double temTripDistance = 0.0;
    Location tempCurrentLocation;
    final String TAG = "FusedLocationService";
    //	Activity locationActivity;
    Context context;
    double distanceBW2Points = 0.0;
    int count = 0, countZeroSpeed = 0;
    String driverStatus = "";
    DBAdapter_Driver dataBaseHelper;
    DistanceTravelled distanceTravelled;
    int accuracy = 10;
    Handler handler;
    Runnable runnable;
    Location[] locationArray = new Location[10];
    Location[] MiddlelocationArray = new Location[10];
    CountDownTimer countDownTimer, countDownTimerForTripLog;
    double distance = 0.0;
    public static boolean isRunning = false;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
    protected ActivityDetectionBroadcastReceiver mBroadcastReceiver;
    private ArrayList<DetectedActivity> mDetectedActivities;
    public static List<String> sendDataMeFromFused = new ArrayList<>();
    int sendLocationTimer = 1 * 60 * 1000;
    float tempspeed;
    int countTimer = 0;
    Location sendLocation, prevSendLocation;
    int onLocationChangeCount = 0;
    int startId = 0;


    public FusedLocationService() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (instance == null) {
            instance = new FusedLocationService(this);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.startId = startId;
        return Service.START_NOT_STICKY;
    }

    private DetectedActivity walkingOrRunning(ArrayList<DetectedActivity> probableActivities) {
        DetectedActivity myActivity = null;
        int confidence = 65;//80;
        if (!probableActivities.isEmpty()) {
            for (DetectedActivity activity : probableActivities) {
            /*if (activity.getType() == DetectedActivity.RUNNING && activity.getType() == DetectedActivity.WALKING)
                continue;*/

                if (activity.getConfidence() > confidence)
                    myActivity = activity;
            }
        }

        return myActivity;
    }

    public FusedLocationService(final Context context) {
        this.context = context;
        //mLocationRequest = new LocationRequest();
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        /*
        ActivityRecognitionResult
        */
        mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();

        LocalBroadcastManager.getInstance(context).registerReceiver(mBroadcastReceiver,
                new IntentFilter(AppConstantDriver.BROADCAST_ACTION));
        /*
        ActivityRecognitionResult
        */


        dataBaseHelper = new DBAdapter_Driver(context);

        // in case if mobile switched off and on again then distance data will be recovered...
        DistanceTravelled travelled = dataBaseHelper.getDistance();
        if (travelled != null) {
            tripDistance = travelled.getDistanceTrip();
            totalDistance = travelled.getDistanceTotal();
            freeDistance = travelled.getDistanceFree();
            toCustomerDistance = travelled.getDistanceToCustomer();
            sendDataMeFromFused.add("Database action old tripDistance: " + tripDistance + " == " + travelled.getDistanceTrip());
        }


        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                updateLocation();
            }
        };
        handler.postDelayed(runnable, 2000); //2000


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                countTimer += 1;

                //Logger.log("sendLocationTimer", countTimer+" ");

                if (mCurrentLocation != null) {
                    Logger.log("sendLocationTimer", countTimer + " " + mCurrentLocation.getLatitude() + "");
                }
                if (driverStatus.equals(DriverStatus.ON_TRIP)) {
                    sendDataMeFromFused.add("" + onLocationChangeCount + " serviceId:" + startId);
                }
                if (sendLocation != null) {
                    if (prevSendLocation == null) {
                        prevSendLocation = sendLocation;
                    }

                    sendMyLocation(sendLocation);


                }

                //updateLocation();

            }
        }, 0, sendLocationTimer);

        sendDataMeFromFused.add("Start fused");


    }

    public static FusedLocationService getInstance(Context context) {
        if (instance == null) {
            instance = new FusedLocationService(context);
        }
        return instance;
    }

    public void googleClientReConnect() {

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
            Log.e(TAG, "Fused location service is started ..............");

        }
    }

    public void requestActivityUpdatesButtonHandler() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(context, (R.string.not_connected),
                    Toast.LENGTH_SHORT).show();
            // return;
        }
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient,
                AppConstantDriver.DETECTION_INTERVAL_IN_MILLISECONDS,
                getActivityDetectionPendingIntent()
        ).setResultCallback(FusedLocationService.this);
    }

    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            // Toggle the status of activity updates requested, and save in shared preferences.
            //  boolean requestingUpdates = !getUpdatesRequestedState();
            //  setUpdatesRequestedState(requestingUpdates);

            // Update the UI. Requesting activity updates enables the Remove Activity Updates
            // button, and removing activity updates enables the Add Activity Updates button.
            //setButtonsEnabledState();


        } else {
            Log.e(TAG, "Error adding or removing activity detection: " + status.getStatusMessage());
        }
    }

    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(context, DetectedActivitiesIntentService.class);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<DetectedActivity> updatedActivities =
                    intent.getParcelableArrayListExtra(AppConstantDriver.ACTIVITY_EXTRA);

            updateDetectedActivitiesList(updatedActivities);
            updateLocation();
        }
    }

    protected void updateDetectedActivitiesList(ArrayList<DetectedActivity> detectedActivities) {
        //mAdapter.updateActivities(detectedActivities);
        HashMap<Integer, Integer> detectedActivitiesMap = new HashMap<>();
        for (DetectedActivity activity : detectedActivities) {
            detectedActivitiesMap.put(activity.getType(), activity.getConfidence());
            Logger.log("DetectedActivity1", activity.getType() + ">>>" + activity.getConfidence());

            if ((activity.getType() == DetectedActivity.STILL) &&
                    activity.getConfidence() > 65) {

                isRunning = false;
                break;
            } else {
                isRunning = true;
            }
        }
        ArrayList<DetectedActivity> tempList = new ArrayList<>();
        mDetectedActivities = tempList;
        for (int i = 0; i < AppConstantDriver.MONITORED_ACTIVITIES.length; i++) {
            int confidence = detectedActivitiesMap.containsKey(AppConstantDriver.MONITORED_ACTIVITIES[i]) ?
                    detectedActivitiesMap.get(AppConstantDriver.MONITORED_ACTIVITIES[i]) : 0;

            tempList.add(new DetectedActivity(AppConstantDriver.MONITORED_ACTIVITIES[i],
                    confidence));

            Log.i("DetectedActivity2", AppConstantDriver.getActivityString(
                    context,
                    AppConstantDriver.MONITORED_ACTIVITIES[i]) + " " + confidence + "%");

        }
        //requestActivityUpdatesButtonHandler();
    }

    public static Location getLocation() {
        if (mCurrentLocation == null) {
            mCurrentLocation = new Location("A");
            mCurrentLocation.setLatitude(0.0);
            mCurrentLocation.setLongitude(0.0);
        }
        return mCurrentLocation;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG, "Fused location service is connected ..............");
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
        mCurrentLocation = fusedLocationProviderApi.getLastLocation(mGoogleApiClient);
        requestActivityUpdatesButtonHandler();
        //if current location is not null we have to put it into shared preference
        if (mCurrentLocation != null) {
           // AppPreferencesDriver.setLatLong(context, mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        }

       /* fusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);*/
        fusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);

        /*PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);*/
    }

    @Override
    public void onLocationChanged(Location location) {
        sendLocation = location;
        handler.removeCallbacks(runnable);
        /*if (UtilityMethods.isForeground(context) && MyAlarmReceiver.isRunning) {
            Log.e("check status", MyAlarmReceiver.isRunning + " : " + UtilityMethods.isForeground(context));
            handler.postDelayed(runnable, 5000);
        }*/


        Logger.log("RLOC_SERVICE", "Service RUNNING!" + location.toString() + " id:" + AppPreferencesDriver.getDriverId(context)
        +" :: onLocationChangeCount "+onLocationChangeCount);
        onLocationChangeCount +=1;


        if (!mGoogleApiClient.isConnected() && Function.isForeground(context)) {
            Log.e("check status 1", mGoogleApiClient.isConnected() + " : " + Function.isForeground(context));
            mGoogleApiClient.connect();
        } else if (!mGoogleApiClient.isConnected() && Function.isForeground(context) && !AppPreferencesDriver.getDriverStatus(context).equals(DriverStatus.LOGOUT)) {
            mGoogleApiClient.connect();
        } else if (AppPreferencesDriver.getDriverStatus(context).equals(DriverStatus.LOGOUT) && !Function.isForeground(context)) {//(!MyAlarmReceiver.isRunning && !UtilityMethods.isForeground(context) ) {
//            Log.e("check status", MyAlarmReceiver.isRunning + " : " + UtilityMethods.isForeground(context));
            if (mGoogleApiClient.isConnected())
                fusedLocationProviderApi.removeLocationUpdates(mGoogleApiClient, this);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mGoogleApiClient.disconnect();
                }
            }, 2000);
//            fusedLocationProviderApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        Log.e(TAG, "Location changed.... driver1"+ location.getLatitude());
        if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
            //store this location to preference
            mCurrentLocation = location;

            if (mPreviousLocation != null) {

                count = 0;
                countZeroSpeed  =0;
                for (int i = 0; i < locationArray.length; i++) {
                    if (locationArray[i] != null) {
                        if (locationArray[i].getLatitude() == location.getLatitude() && locationArray[i].getLongitude() == location.getLongitude()) {
                            count++;
                        }

                        if (locationArray[i].getSpeed() == 0.0) {
                            countZeroSpeed++;
                        }
                    }
                }
                if(count > 2 || countZeroSpeed  > 8){

                    if (mGoogleApiClient.isConnected()) {
                        fusedLocationProviderApi.removeLocationUpdates(mGoogleApiClient, this);
                        mGoogleApiClient.disconnect();
                    }

                    for(int i=0; i<10; i++){
                        locationArray[i] = null;
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(!mGoogleApiClient.isConnected()){
                                mGoogleApiClient.connect();
                            }
                        }
                    },2000);
                }

                driverStatus = AppPreferencesDriver.getDriverStatus(context);
                distanceBW2Points = 0.0;
                distanceBW2Points = mPreviousLocation.distanceTo(mCurrentLocation);

                if(tempCurrentLocation == null){
                    tempCurrentLocation = location;
                }

                if(location.getAccuracy() < 18 && driverStatus.equals(DriverStatus.ON_TRIP) && isRunning){

                    float distanceDiff = tempCurrentLocation.distanceTo(location);


                    long timediffer1 = location.getTime() - tempCurrentLocation.getTime();
                    DateFormat format = new SimpleDateFormat("ss");
                    Date dateAA = new Date(timediffer1);
                    int diffTime = Integer.parseInt(format.format(dateAA));

                    //165  330

                    int tA = diffTime * 23;


                     tempspeed = (distanceDiff / diffTime) * 3600/1000;


                    if(tA > 0 && tA >= distanceDiff && tempspeed > 1){

                        temTripDistance = temTripDistance + distanceDiff;
                       // Database.getInstance(context).updateTotalDistance(temTripDistance);
                        tempCurrentLocation = location;
                    }


                }


                if (mCurrentLocation.distanceTo(mImmediatePreviousLatLng) > 25) {
                    mImmediatePreviousLatLng1 = mImmediatePreviousLatLng;
                    mImmediatePreviousLatLng = mCurrentLocation;
//                    Logs.setLog(context, "Without Conditions return 1 ->", " cLat : " + location.getLatitude() + " cLng : " + location.getLongitude() + " pLat : " + mPreviousLocation.getLatitude() + " pLng : " + mPreviousLocation.getLongitude() +
//                            " DB2P : " + distanceBW2Points  + " cStatus : " + AppPreferencesDriver.getDriverStatus(context) +
//                            " pStatus : " + driverStatus + " Accuracy : " + location.getAccuracy() +
//                            " Speed : " + location.getSpeed() + " Time : " + new Date(location.getTime()));
                    return;
                } else if (mImmediatePreviousLatLng.distanceTo(mImmediatePreviousLatLng1) > 25) {
                    mImmediatePreviousLatLng1 = mImmediatePreviousLatLng;
                    mImmediatePreviousLatLng = mCurrentLocation;
                   /* Logs.setLog(context, "Without Conditions return 2->", " cLat : " + location.getLatitude() + " cLng : " + location.getLongitude() + " pLat : " + mPreviousLocation.getLatitude() + " pLng : " + mPreviousLocation.getLongitude() +
                            " DB2P : " + distanceBW2Points + " cStatus : " + AppPreferencesDriver.getDriverStatus(context) +
                            " pStatus : " + driverStatus + " Accuracy : " + location.getAccuracy() +
                            " Speed : " + location.getSpeed() +" getBearing : " + location.getBearing() + " Time : " + new Date(location.getTime()));*/
                    return;
                }


                Logger.log("Skipped Time", (location.getTime() - mPreviousLocation.getTime()) + "");


                if(mDetectedActivities != null) {
                    DetectedActivity detectedActivity = walkingOrRunning(mDetectedActivities);
                }


                AppPreferencesDriver.setLatLong(context, location.getLatitude(), location.getLongitude());
                if (location.getBearing()!=0.0 && mImmediatePreviousLatLng.getSpeed() != 0.0 && (location.getSpeed() > 0.224)
                        && ((((distanceBW2Points > 20 && distanceBW2Points <30)  || (distanceBW2Points >= 50))
                        && accuracy <= 30 && location.getAccuracy() != 0.0) ||
                        ((location.getTime() - mPreviousLocation.getTime()) >= 10000 && distance > 230))
                        && (driverStatus.equals(DriverStatus.FREE) || driverStatus.equals(DriverStatus.ON_TRIP)) && isRunning) {

                   // AppPreferencesDriver.setLatLong(context, location.getLatitude(), location.getLongitude());
                    Log.e(TAG, "Distance between two points " + distanceBW2Points);
                    //totalDistance = totalDistance + distanceBW2Points;
                    /*if (!AppPreference.getDriverStatus(context).equals(driverStatus)) {
                        totalDistance = 0;
                    }*/
                    if (AppPreferencesDriver.getDriverId(context) != 0) {
                        updateTripLog(location);
                    }

                    switch (AppPreferencesDriver.getDriverStatus(context)) {
                        case DriverStatus.FREE:
                            freeDistance = freeDistance + distanceBW2Points;
                            /*if (NoBookingActivity.textViewDistance != null) {
                                NoBookingActivity.textViewDistance.setText(String.valueOf(UtilityMethods.roundOneDecimalsForDistance(freeDistance)) + " मी.");
                            }*/
                            break;

                        case DriverStatus.TO_CUSTOMER:
                          toCustomerDistance = toCustomerDistance + distanceBW2Points;
                            /*if (PickupDetailsActivity.textViewDistanceToCustomer != null) {
                                PickupDetailsActivity.textViewDistanceToCustomer.setText(UtilityMethods.roundOneDecimalsForDistance(toCustomerDistance) + " मी.");
                            }*/
                            break;

                        case DriverStatus.ON_TRIP:
                            tripDistance = tripDistance + distanceBW2Points;

                            if (TripStartedActivityDriver.menuItem != null) {
                                TripStartedActivityDriver.menuItem.setTitle(TripStartedActivityDriver.roundTwoDecimals(tripDistance / 1000) + " KM");
                            }
                            if (TripStartedActivityDriver.mInstance != null){
                                TripStartedActivityDriver.updateNavMarker(mPreviousLocation,mCurrentLocation);
                            }
                            UserDriver user = new UserDriver();
                            user.setId(Long.valueOf(AppPreferencesDriver.getDriverId(context)));
                            user.setCurrentLatitude(mCurrentLocation.getLatitude());
                            user.setCurrentLongitude(mCurrentLocation.getLongitude());
                            dataBaseHelper.insertLocation(user);
                            /*if (TripActivity.textViewCurrentDistance != null) {
                                TripActivity.textViewCurrentDistance.setText(String.valueOf(UtilityMethods.roundOneDecimalsForDistance(tripDistance)) + " मी.");
                            }*/

                            distanceTravelled = new DistanceTravelled(1, driverStatus,
                                    freeDistance + toCustomerDistance + tripDistance,
                                    freeDistance,
                                    toCustomerDistance,
                                    tripDistance,
                                    AppPreferencesDriver.getTripId(context));
                            boolean isSave = dataBaseHelper.setDistance(distanceTravelled);

                            break;

                        default:
                            break;
                    }

                    /*Logs.setLog(context, "With Conditions ->", " cLat : " + location.getLatitude() + " cLng : " + location.getLongitude() + " pLat : " + mPreviousLocation.getLatitude() + " pLng : " + mPreviousLocation.getLongitude() +
                            " DB2P : " + distanceBW2Points + " cStatus : " + AppPreferencesDriver.getDriverStatus(context) +
                            " pStatus : " + driverStatus + " Accuracy : " + location.getAccuracy() +
                            " Speed : " + location.getSpeed() +" getBearing : " + location.getBearing()+ " Time : " + new Date(location.getTime()) + " disF : " + freeDistance + " disTC : " + toCustomerDistance +
                            " disTP : " + tripDistance / 1000);
*/




                    mPreviousLocation = location;
                } else if (driverStatus.equals(DriverStatus.RINGING) || driverStatus.equals(DriverStatus.LOADING) ||
                        driverStatus.equals(DriverStatus.UNLOADING) || driverStatus.equals(DriverStatus.BILLING) ||
                        driverStatus.equals(DriverStatus.RATING) || driverStatus.equals(DriverStatus.POD) ||
                        driverStatus.equals(DriverStatus.COMPLETED) || driverStatus.equals(DriverStatus.LOGOUT) ) {

                    if (mPreviousLocation.getLatitude() == location.getLatitude() && mGoogleApiClient.isConnected()) {
                        fusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient,
                                mLocationRequest, this);
                        //mGoogleApiClient = null;
                    }

                    mPreviousLocation = location;
                   // AppPreferencesDriver.setLatLong(context, location.getLatitude(), location.getLongitude());
                    if (AppPreferencesDriver.getDriverId(context) != 0) {
                        updateTripLog(location);
                    }

                }else{
                  //  AppPreferencesDriver.setLatLong(context, location.getLatitude(), location.getLongitude());
                    if (AppPreferencesDriver.getDriverId(context) != 0) {

                        updateTripLog(location);
                    }
                    /*Logs.setLog(context, "ReWithout countDownTimer ->", " cLat : " + location.getLatitude() + " cLng : " + location.getLongitude() + " pLat : " + mPreviousLocation.getLatitude() + " pLng : " + mPreviousLocation.getLongitude() +
                            " DB2P : " + distanceBW2Points + " cStatus : " + AppPreferencesDriver.getDriverStatus(context) +
                            " pStatus : " + driverStatus + " Accuracy : " + location.getAccuracy() +
                            " Speed : " + location.getSpeed() +" getBearing : " + location.getBearing()+ " Time : " + new Date(location.getTime()));*/
                }


                mImmediatePreviousLatLng1 = mImmediatePreviousLatLng;
                mImmediatePreviousLatLng = location;
                saveLocation(location);
            } else {
                mImmediatePreviousLatLng1 = location;
                mPreviousLocation = location;
                mImmediatePreviousLatLng = location;

            }



        }
    }



    public void sendMyLocation(Location location){
        final double latitude, longitude, speed, distance;

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        //speed = location.getSpeed();
        speed = tempspeed;
        distance = prevSendLocation.distanceTo(sendLocation);
        Logger.log("Re loacation update", latitude +">>"+ longitude);
        final Intent filterRes = new Intent();
        filterRes.setAction("com.b4edriver.action.LOCATION");
        filterRes.putExtra("latitude", latitude);
        filterRes.putExtra("longitude", longitude);
        filterRes.putExtra("speed", speed);
        filterRes.putExtra("batteryStatus", batteryStatus);
        filterRes.putExtra("distance", distance);

        prevSendLocation = sendLocation;

        //context.sendBroadcast(filterRes);
        Logger.log("Re loacation update ok", latitude +">>"+ longitude);
        if(!AppPreferencesDriver.getWaitApi(context)) {
            updateLocationTaskDriver(context, latitude, longitude, speed, batteryStatus,distance);
        }
    }

    public void updateTripLog(Location location) {




        gps = "true";
       // context.sendBroadcast(filterRes);
        /*if(countDownTimerForTripLog==null) {
            countDownTimerForTripLog = new CountDownTimer(50000, 20000) {
                @Override
                public void onTick(long l) {
                    Logger.log("countDownTripLog", l + "");
                    if (AppPreferencesDriver.getDriverId(context) != 0) {

                        context.sendBroadcast(filterRes);
                    }
                }

                @Override
                public void onFinish() {
                    countDownTimerForTripLog.start();
                }
            };

            countDownTimerForTripLog.start();
        }*/

        final Intent intent2 = new Intent("Gpsindecator");
        intent2.putExtra("status", "200");
        intent2.putExtra("msg", "GPS working fine");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(1000 * 60, 5000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished < 40000 && AppPreferencesDriver.getDriverId(context) != 0) {
                    Logger.log("countDownTimer", millisUntilFinished + "");
                    gps = "false";
                    intent2.putExtra("status", "300");
                    intent2.putExtra("msg", context.getString(R.string.gps_alert));
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);
                    //context.sendBroadcast(filterRes);
                }
            }

            @Override
            public void onFinish() {
                Logger.log("countDownTimer>>Finish", "");

                countDownTimer.start();
            }
        };
        countDownTimer.start();

        //context.sendBroadcast(filterRes);
    }

    private void saveLocation(Location location) {
        for (int i = locationArray.length - 1; i >= 0; i--) {
            if (i != 0) {
                locationArray[i] = locationArray[i - 1];
            } else {
                locationArray[0] = location;
            }
           /* Log.e("ABCDE" + i, "*********************** " + (locationArray[i] != null ? locationArray[i].getLatitude() : "abc"));*/
        }
    }

    private void saveMiddleLocation(Location location) {
        for (int i = MiddlelocationArray.length - 1; i >= 0; i--) {
            if (i != 0) {
                MiddlelocationArray[i] = MiddlelocationArray[i - 1];
            } else {
                MiddlelocationArray[0] = location;
            }
           /* Log.e("ABCDE" + i, "*********************** " + (locationArray[i] != null ? locationArray[i].getLatitude() : "abc"));*/
        }
    }

    private void updateLocation() {
        if (mGoogleApiClient == null) {
            //new FusedLocationService(context);
            //FusedLocationService.getInstance(context);
            googleClientReConnect();
        } else {
            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mGoogleApiClient.isConnected()) {

                            fusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient,
                                    mLocationRequest, FusedLocationService.this);
                        }
                    }
                }, 2000);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Logger.log(TAG, "Fused location client is reconnecting");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Logger.log(TAG, "Fused location client is retrying to connect");
        mGoogleApiClient.connect();
    }


    public void updateLocationTaskDriver(final Context context, double latitude,
                                         double longitude, double speed, String batteryStatus, double distance) {

        context.sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
        context.sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));
        Logger.log("Re loacation update1 local ", latitude +">>"+ longitude);
        final JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("method", AppConstantDriver.METHOD.TRIP_LOG);
            jsonObj.put("driverId", AppPreferencesDriver.getDriverId(context));
            jsonObj.put("tripId", AppPreferencesDriver.getTripId(context));
            jsonObj.put("driver_type", AppPreferencesDriver.getDriverType(context));
            jsonObj.put("latitude", latitude);
            jsonObj.put("longitude", longitude);
            jsonObj.put("date", Function.getCurrentDateTime());
            jsonObj.put("type", AppPreferencesDriver.getDrivertype(context));
            jsonObj.put("batterystatus", batteryStatus);
            jsonObj.put("speed", speed);
            jsonObj.put("distance", distance);
            jsonObj.put("gps", FusedLocationService.gps);
            jsonObj.put("activity", AlarmServicesDriver.myActivity);
            jsonObj.put("device_id", FirebaseInstanceId.getInstance().getToken());
            jsonObj.put("status", AppPreferencesDriver.getTripstatusForDriver(context));
            jsonObj.put("ring_Trip", AppPreferencesDriver.getPendingTripId(context));
            jsonObj.put("isConnectionFast", Function.isConnectionFast(context));

            Logs.setLog(context,"triplog" ,jsonObj.toString());


            if(AppPreferencesDriver.getDriverId(context) != 0) {
                JSONParser jsonParser = new JSONParser(context);
                jsonParser.parseVollyForLog(AppConstantDriver.URL.ONDEMAND_USER_DRIVERTRIPLOGUPDATE, 1, jsonObj, new VolleyCallBack() {
                    @Override
                    public void success(String response) {
                        Logs.setLog(context, "triplog", response.toString());
                    }
                });
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
