package com.b4edriver.CommonClasses.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.b4edriver.CommonClasses.Utils.AppConstantDriver;
import com.b4elibrary.Logger;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by MAX on 7/18/2017.
 */

public class DetectedActivitiesIntentService  extends IntentService {

    protected static final String TAG = "DetectedActivitiesIS";

    public static boolean isRunning;

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public DetectedActivitiesIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Handles incoming intents.
     * @param intent The Intent is provided (inside a PendingIntent) when requestActivityUpdates()
     *               is called.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        Intent localIntent = new Intent(AppConstantDriver.BROADCAST_ACTION);

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.
        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

        final boolean isrun = updateDetectedActivitiesList(getApplicationContext(),detectedActivities);

        Logger.log(TAG, "isrun : "+isrun +" : isRunning: "+isRunning);

        // Log each activity.
        Log.i(TAG, "activities detected");
        for (DetectedActivity da: detectedActivities) {
            Log.i(TAG, AppConstantDriver.getActivityString(
                    getApplicationContext(),
                    da.getType()) + " " + da.getConfidence() + "%"
            );
        }

        // Broadcast the list of detected activities.
        localIntent.putExtra(AppConstantDriver.ACTIVITY_EXTRA, detectedActivities);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    protected boolean updateDetectedActivitiesList(Context context, List<DetectedActivity> detectedActivities) {

        HashMap<Integer, Integer> detectedActivitiesMap = new HashMap<>();
        for (DetectedActivity activity : detectedActivities) {
            detectedActivitiesMap.put(activity.getType(), activity.getConfidence());
            //   Logger.log("DetectedActivity1", activity.getType() +">>>"+activity.getConfidence());
            Log.i(TAG, AppConstantDriver.getActivityString(
                    context,
                    activity.getType()) + " " + activity.getConfidence() + "%");
            if((activity.getType() == DetectedActivity.STILL) &&
                    activity.getConfidence() > 65){

                isRunning = false;
                return false;
                // break;
            }else{
                isRunning = true;
            }
        }
        return true;
    }
}