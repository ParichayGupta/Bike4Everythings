package com.b4ebusinessdriver.DistanceCalculate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.b4ebusinessdriver.Utils.Function;
import com.b4elibrary.Logger;


public class DriverLocationUpdateService extends Service {
    private static final long ALARM_REPEAT_INTERVAL = 180000;
    private static int DRIVER_LOCATION_PI_REQUEST_CODE = 111;
    private static final String SEND_LOCATION = "com.b4ebusinessdriver.DistanceCalculate.SEND_LOCATION";
    LocationFetcherDriver locationFetcherDriver;

    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onCreate() {
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    public void onStart(Intent intent, int startId) {
        try {
            if (Function.isServiceRunning(this, DriverLocationUpdateService.class.getName())) {
              //  updateServerData(this);

                    if (this.locationFetcherDriver != null) {
                        this.locationFetcherDriver.destroy();
                        this.locationFetcherDriver = null;
                    }
                    this.locationFetcherDriver = new LocationFetcherDriver(this, 10000);

                setupLocationUpdateAlarm();
                return;
            }
            stopService(new Intent(this, DriverLocationUpdateService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onTaskRemoved(Intent rootIntent) {
        try {

            if (Function.isServiceRunning(this, DriverLocationUpdateService.class.getName())) {
                Logger.log("driverLocation", "yes");
                Logger.log("driverLocation", "yes");
                Intent restartService = new Intent(getApplicationContext(), getClass());
                restartService.setPackage(getPackageName());
                ((AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE)).set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT));
                return;
            }
            stopService(new Intent(this, DriverLocationUpdateService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroyLocationFetcher() {
        if (this.locationFetcherDriver != null) {
            this.locationFetcherDriver.destroy();
            this.locationFetcherDriver = null;
        }
    }

    public void onDestroy() {
        destroyLocationFetcher();
        if (!Function.isServiceRunning(this, DriverLocationUpdateService.class.getName())) {
            cancelLocationUpdateAlarm();
        }
    }

    public void onTrimMemory(int level) {
       // Database.getInstance(this).insertUSLLog("DLD_TRIM_MEMORY_" + level);
        super.onTrimMemory(level);
    }

    public void onLowMemory() {
     //   Database.getInstance(this).insertUSLLog(com.b4ebusinessdriver.Utils.Constants.EVENT_DLD_LOW_MEMORY);
        super.onLowMemory();
    }

    public void setupLocationUpdateAlarm() {
        if (PendingIntent.getBroadcast(this, DRIVER_LOCATION_PI_REQUEST_CODE, new Intent(this, DriverLocationUpdateAlarmReceiver.class).setAction(SEND_LOCATION), PendingIntent.FLAG_NO_CREATE) != null) {
            cancelLocationUpdateAlarm();
        }
        Intent intent = new Intent(this, DriverLocationUpdateAlarmReceiver.class);
        intent.setAction(SEND_LOCATION);
        ((AlarmManager) getSystemService(ALARM_SERVICE)).setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), ALARM_REPEAT_INTERVAL, PendingIntent.getBroadcast(this, DRIVER_LOCATION_PI_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    public void cancelLocationUpdateAlarm() {
        Intent intent = new Intent(this, DriverLocationUpdateAlarmReceiver.class);
        intent.setAction(SEND_LOCATION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, DRIVER_LOCATION_PI_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ((AlarmManager) getSystemService(ALARM_SERVICE)).cancel(pendingIntent);
        pendingIntent.cancel();
    }
}
