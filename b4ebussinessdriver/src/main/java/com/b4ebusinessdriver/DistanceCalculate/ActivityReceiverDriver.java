package com.b4ebusinessdriver.DistanceCalculate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.b4elibrary.Logger;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.HashMap;
import java.util.List;


public class ActivityReceiverDriver extends BroadcastReceiver {
    public final String TAG = ActivityReceiverDriver.class.getSimpleName();
    public static boolean isRunning;

    public void onReceive(final Context context, Intent intent) {
        try {

            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            List<DetectedActivity> detectedActivities =  result.getProbableActivities();


            final boolean isrun = updateDetectedActivitiesList(context,detectedActivities);

            Logger.log(TAG, "isrun : "+isrun +" : isRunning: "+isRunning);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                   // Toast.makeText(context, "isRunning "+isrun, Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean updateDetectedActivitiesList(Context context, List<DetectedActivity> detectedActivities) {

        HashMap<Integer, Integer> detectedActivitiesMap = new HashMap<>();
        for (DetectedActivity activity : detectedActivities) {
            detectedActivitiesMap.put(activity.getType(), activity.getConfidence());

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
