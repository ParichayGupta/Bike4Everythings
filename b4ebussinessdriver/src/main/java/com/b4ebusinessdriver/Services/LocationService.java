package com.b4ebusinessdriver.Services;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.b4ebusinessdriver.Activity.HomeActivity;
import com.b4ebusinessdriver.Database.DatabaseHandler;
import com.b4ebusinessdriver.Model.LatLngs;
import com.b4ebusinessdriver.R;
import com.b4ebusinessdriver.Utils.AppConstant;
import com.b4ebusinessdriver.Utils.AppPreferences;
import com.b4ebusinessdriver.Utils.Helper;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.b4ebusinessdriver.Utils.Function.getBatteryLevel;
import static com.b4ebusinessdriver.Utils.Function.isGPSEnabled;

/**
 * Created by Manish on 9/11/2016.
 */
public class LocationService extends Service implements LocationProvider.LocationCallback{

    //private static final String TAG = Location.class.getSimpleName();
    private static final int NOTIFICATION_ID = 11;
    private static final int UPDATE_INTERVAL = 1000 * 60 * 2;
    private LocationProvider mLocationProvider;
    private PermisstionCallback permisstionCallback;
    private boolean isUserWalking;
    private Location mCurrentLocation;
    private Location mPreviousLocation;
    private Location mTempPreviousLocation;
    private boolean isBroadcastAllow;
    private float mDistanceCovered;
    private float mSpeed, distanceDiff;
    final Handler handler = new Handler();
    ArrayList<LatLng> latLngArrayList = new ArrayList<>();

    private Intent mIntentService;
    private PendingIntent mPendingIntent;
    private ActivityRecognitionClient mActivityRecognitionClient;
    protected ActivityDetectionBroadcastReceiver mBroadcastReceiver;
    private long startTime;
    boolean isRunning;
    int countCheck = 0;
    float speed = 0.0f;
    int changeLocation = 0;

    DateFormat format = new SimpleDateFormat("ss");

    private IBinder mIBinder = new LocalBinder();

    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("activity_intent")) {
             //   int type = intent.getIntExtra("type", -1);
               // int confidence = intent.getIntExtra("confidence", 0);
                ArrayList<DetectedActivity> activities = intent.getParcelableArrayListExtra("detectedActivities");
                //isUserRunning(type, confidence);
                isUserRunningAcivity(activities);
            }
        }
    }

    private void isUserRunningAcivity(ArrayList<DetectedActivity> detectedActivities) {
        for (DetectedActivity da: detectedActivities) {
            if ((da.getType() == DetectedActivity.TILTING ||
                    da.getType() == DetectedActivity.STILL)
                    && da.getConfidence() > 65) {
                isRunning = false;
                break;
            }else{
                isRunning = true;
            }
        }
    }


    public interface PermisstionCallback {
        void locationSettingsResult(PendingResult<LocationSettingsResult> result);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LatLngs distance = new DatabaseHandler(this).getTotalDistance();
        if(distance != null) {
            if (distance.isIswalking()) {
                startUserWalk();
            }
        }

        mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter("activity_intent"));

        mLocationProvider = new LocationProvider(this, this);
        mLocationProvider.connect();
        isUserWalking = false;
        startTime = 0;
        Log.e("HomeActivity", " onCreate Locationservice");
        final Runnable update = new Runnable() {
            @Override
            public void run() {

                if (mCurrentLocation != null) {
                    // mPrevLocation = mCurrentLocation;
                   // new sendLocation().execute();
                }
            }
        };


        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 500, 60000);


        mActivityRecognitionClient = new ActivityRecognitionClient(this);
        mIntentService = new Intent(this, DetectedActivitiesIntentService.class);
        mPendingIntent = PendingIntent.getService(this, 1, mIntentService, PendingIntent.FLAG_UPDATE_CURRENT);
        requestActivityUpdatesButtonHandler();


    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    @Override
    public void handleInitialLocation(Location location) {
        mCurrentLocation = location;
        mTempPreviousLocation = location;
    }

    public void locationEnabledSuccess(){
        mLocationProvider.locationEnabledSuccess();
    }

    long time = 0L;
    @Override
    public void handleNewLocation(Location location) {

        if(mCurrentLocation == null){
            mCurrentLocation = location;
            mTempPreviousLocation = location;
        }

        changeLocation +=1;

       // showMove(mPreviousLocation, location);


        if(latLngArrayList.isEmpty()){
            latLngArrayList.add(new LatLng(location.getLatitude(), location.getLongitude()));
        }



        Log.e("LocationService",
                " "+location.toString()+
                "\ngetLatitude="+location.getLatitude()+
                "\ngetLongitude="+location.getLongitude()+
                "\ngetTime="+time+
                "\ngetSpeed="+location.getSpeed()+
                "\ngetProvider="+location.getProvider()+
                "\ngetAccuracy="+location.getAccuracy()+
                "\ngetAltitude="+location.getAltitude()+
                "\ngetBearing="+location.getBearing()+
                "\ndistanceDiff="+distanceDiff+
               /* "\ntime="+location.getTime()+
                "-"+mTempPreviousLocation.getTime()+
                "="+formattedAA+*/
                "\nisRunning="+isRunning+
                "\nchangeLocation="+changeLocation
        );
        mTempPreviousLocation = location;
        if(isRunning && location.getAccuracy() <= 16) {

            int count = latLngArrayList.size();
            for(int i=0; i<count; i++){
                Log.e("LocationService",i +" : store"+latLngArrayList.get(i).lat+" : cur"+location.getLatitude());
                if(i == 6){

                    mLocationProvider.refreshLocation();
                    latLngArrayList.clear();
                    break;
                }

                if(latLngArrayList.get(i).lat == location.getLatitude() || latLngArrayList.get(i).lng == location.getLongitude()){
                    latLngArrayList.add(new LatLng(location.getLatitude(), location.getLongitude()));

                }

            }


            float distanceDiff = mCurrentLocation.distanceTo(location);


            long timediffer1 = location.getTime() - mCurrentLocation.getTime();

            Date dateAA = new Date(timediffer1);
            int diffTime = Integer.parseInt(format.format(dateAA));

            //165  330

            int tA = diffTime * 33;


             speed = (distanceDiff / diffTime) * 3600/1000;


            if(tA > 0 && tA >= distanceDiff && speed > 1){


                mCurrentLocation = location;
                calculateDistance();
                broadCastLocation(mCurrentLocation);
                if (AppPreferences.getOntrip(this))
                    saveDistance(mCurrentLocation, String.valueOf(speed));
            }else {
                countCheck = countCheck + 1;
            }



            Intent in = new Intent("checkcondition");
            in.putExtra("distanceDiff",distanceDiff);
            in.putExtra("timediffer",diffTime);
            in.putExtra("tA",tA);
            in.putExtra("countCheck",countCheck);
            in.putExtra("speed",speed);
            in.putExtra("Accuracy",location.getAccuracy());
            LocalBroadcastManager.getInstance(this).sendBroadcast(in);


        }else{


            float distanceDiff = mCurrentLocation.distanceTo(location);


            long timediffer1 = location.getTime() - mCurrentLocation.getTime();

            Date dateAA = new Date(timediffer1);
            int diffTime = Integer.parseInt(format.format(dateAA));

            int tA = diffTime * 33;

             speed = (distanceDiff / diffTime) * 3600/1000;

           /* Intent in = new Intent("checkcondition");
            in.putExtra("distanceDiff",distanceDiff);
            in.putExtra("timediffer",diffTime);
            in.putExtra("tA",tA);
            in.putExtra("speed",speed);
            in.putExtra("Accuracy",location.getAccuracy());
            LocalBroadcastManager.getInstance(this).sendBroadcast(in);*/
        }
       /* boolean isLatlngExits = false;
        for(int i=0; i<latLngArrayList.size(); i++){
            if(location.getLatitude() == latLngArrayList.get(i).lat  || location.getLongitude() == latLngArrayList.get(i).lng){
                isLatlngExits = true;
                break;
            }else{  6,49  7 4
                isLatlngExits = false;
            }
        }

        if(!isLatlngExits){


            latLngArrayList.add(new LatLng(location.getLatitude(), location.getLongitude()));
        }*/

    }

    public float distanceDiff(){
        return distanceDiff;
    }

    @Override
    public void locationSettingsResult(PendingResult<LocationSettingsResult> result) {
        if(permisstionCallback != null)
        permisstionCallback.locationSettingsResult(result);
    }

    private void saveDistance(Location userLocation, String extra) {
        LatLngs latLngs = new LatLngs();
        latLngs.setCurrentLat(userLocation.getLatitude());
        latLngs.setCurrentLng(userLocation.getLongitude());
        latLngs.setDistance(getString(R.string.daily_dist_data, distanceCovered()));
        latLngs.setTime(Helper.secondToHHMMSS(elapsedTime()));
        latLngs.setExtra(extra);

        new DatabaseHandler(this).addDistance(latLngs);
    }


    // Assume this algorithm calculates precise distance
    private void calculateDistance(){
        if(isUserWalking) {
            float distanceDiff = mPreviousLocation.distanceTo(mCurrentLocation); // Return meter unit
            mDistanceCovered = mDistanceCovered + distanceDiff;

            int speedIs10MetersPerMinute = 10;
            float estimatedDriveTimeInMinutes = distanceDiff / speedIs10MetersPerMinute;

            mSpeed = distanceDiff/estimatedDriveTimeInMinutes;
            mPreviousLocation = mCurrentLocation;
        }
    }

    public void startBroadcasting() {
        //Log.e(TAG, " start broadcast");
        isBroadcastAllow = true;
        broadcastFirstLocation();
    }

    private void broadcastFirstLocation() {
        if(mCurrentLocation != null){
            broadCastLocation(mCurrentLocation);
        }
    }

    public void stopBroadcasting() {
        //Log.e(TAG, " stop broadcast");
        isBroadcastAllow = false;
    }

    @Override
    public void onDestroy() {
        //Log.e(TAG, "onDestroy");
       // isUserWalking = false;
       // mLocationProvider.disconnect();
      //  LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }


    private void broadCastLocation(Location location) {

        if(isBroadcastAllow){
            broadcastUserLocation(location);
        }

    }

    private void broadcastUserLocation(Location location) {

        Intent in = new Intent(Helper.ACTION_NAME_SPACE);
        in.putExtra(Helper.INTENT_EXTRA_RESULT_CODE, Activity.RESULT_OK);
        in.putExtra(Helper.INTENT_USER_LAT_LNG, location);
        in.putExtra(Helper.INTENT_USER_PRE_LAT_LNG, mPreviousLocation);
        in.putExtra(Helper.INTENT_USER_DISTANCE, mDistanceCovered);
        LocalBroadcastManager.getInstance(this).sendBroadcast(in);
    }

    public long elapsedTime() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    public float distanceCovered() {
        return mDistanceCovered;
    }
    public float getSpeed() {
        return mSpeed;
    }

    public boolean isUserWalking() {
        return isUserWalking;
    }

    public void startUserWalk() {
        if(!isUserWalking){
            LatLngs distance = new DatabaseHandler(this).getTotalDistance();
            startTime = System.currentTimeMillis();
            mPreviousLocation = mCurrentLocation;
            mDistanceCovered = distance == null ? 0 : Float.parseFloat(distance.getDistance());
            isUserWalking = true;
        }
    }

    public void stopUserWalk() {
        if(isUserWalking){
            isUserWalking = false;
        }
    }

    public Location getUserLocation() {
        return mCurrentLocation;
    }

    public boolean isUserRunnning() {
        return isRunning;
    }


    public class LocalBinder extends Binder {
        public LocationService getService(PermisstionCallback callback){
            permisstionCallback = callback;
            return LocationService.this;
        }
    }

    // Prevent system killing the background service
    public void startForeground() {
        startForeground(NOTIFICATION_ID, createNotification());
    }

    public void stopNotification() {
        stopForeground(true);
    }


    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("Keep Walking")
                .setContentText("Tap to return to walk activity")
                .setSmallIcon(R.mipmap.ic_launcher);

        Intent resultIntent = new Intent(this, HomeActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this, 0, resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        return builder.build();
    }



    public void requestActivityUpdatesButtonHandler() {
        Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
                Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                mPendingIntent);

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(getApplicationContext(),
                        "Successfully requested activity updates",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),
                        "Requesting activity updates failed to start",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    public void removeActivityUpdatesButtonHandler() {
        Task<Void> task = mActivityRecognitionClient.removeActivityUpdates(
                mPendingIntent);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(getApplicationContext(),
                        "Removed activity updates successfully!",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to remove activity updates!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }




}
