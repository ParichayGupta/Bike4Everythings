package com.b4edriver.DistanceCalculate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.b4edriver.CommonClasses.Utils.Function;
import com.b4elibrary.Logger;


public class DriverLocationUpdateAlarmReceiver extends BroadcastReceiver {
    private static final long MAX_TIME_BEFORE_LOCATION_UPDATE = 180000;
    private static final String SEND_LOCATION = "com.bike4everything.DistanceCalculate.SEND_LOCATION";
    private final String TAG = DriverLocationUpdateAlarmReceiver.class.getSimpleName();

    public void onReceive(final Context context, Intent intent) {
        try {

            if (Function.isServiceRunning(context, DriverLocationUpdateService.class.getName())) {

                if (SEND_LOCATION.equals(intent.getAction())) {
                    /*try {
                      //  long lastTime = Database.getInstance(context).getDriverLastLocationTime();
                      //  DriverLocationUpdateService.updateServerData(context);
                        if (System.currentTimeMillis() >= MAX_TIME_BEFORE_LOCATION_UPDATE + lastTime) {
                           // Database.getInstance(context).insertUSLLog(Constants.EVENT_DL_ALARM_LOC_NOT_SENT_TILL_3_MIN);
                            new Thread(new Runnable() {
                                public void run() {
                                    new DriverLocationDispatcher().sendLocationToServer(context);
                                }
                            }).start();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }
                if (!Function.isServiceRunning(context, DriverLocationUpdateService.class.getName())) {
                    Logger.log(this.TAG, "onReceive startDriverService called");
                    context.startService(new Intent(context, DriverLocationUpdateService.class));
                    return;
                }
                return;
            }
            context.stopService(new Intent(context, DriverLocationUpdateService.class));
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
}
