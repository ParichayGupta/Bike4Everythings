package com.b4edriver.DistanceCalculate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.Toast;

import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.b4edriver.CommonClasses.Utils.MapUtils;
import com.b4edriver.Database.Database;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;


public class LocationReceiverDriver extends BroadcastReceiver {
    public static final double FREE_MAX_ACCURACY = 200.0d;
    public static final double MAX_TIME_WINDOW = 3600000.0d;
    private static int PI_REQUEST_CODE = 1234;
    private static final String RESTART_SERVICE = "com.bike4everything.DistanceCalculate.RESTART_SERVICE";
    public final String TAG = LocationReceiverDriver.class.getSimpleName();
    private float HIGH_ACCURACY_ACCURACY_CHECK = 200.0f;
    private long LOCATION_UPDATE_TIME_PERIOD = 10000;

    public void onReceive(Context context, Intent intent) {
        try {
            Bundle extras = intent.getExtras();
            FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
            final Location location = (Location) extras.get(FusedLocationProviderApi.KEY_LOCATION_CHANGED);

            if (!Utils.mockLocationEnabled(location) && location != null) {
                Log.m616i(this.TAG, location.getLatitude()+","+location.getLongitude());
                AppPreferencesDriver.setLatLong(context, location.getLatitude(), location.getLongitude());
                Location oldlocation = Database.getInstance(context).getDriverCurrentLocation(context);
                double speed_1 = MapUtils.distance(oldlocation, location) / ((double) ((System.currentTimeMillis() - oldlocation.getTime()) / 1000));
                if (speed_1 > 20.0d) {
                    Log.m616i(this.TAG, "onReceive DriverLocationUpdateService restarted speed_1=" + speed_1);
                    context.stopService(new Intent(context, DriverLocationUpdateService.class));
                    setAlarm(context);
                    //Database2.getInstance(context).insertUSLLog(Constants.EVENT_LR_SPEED_20PLUS_RESTART);
                    return;
                }
                if (((double) location.getAccuracy()) > 200.0d) {
                    Prefs.with(context).save(SPLabels.BAD_ACCURACY_COUNT, Prefs.with(context).getInt(SPLabels.BAD_ACCURACY_COUNT, 0) + 1);
                }
                long timeLapse = System.currentTimeMillis() - Prefs.with(context).getLong(SPLabels.ACCURACY_SAVED_TIME, 0);
                if (((double) timeLapse) > MAX_TIME_WINDOW || Prefs.with(context).getInt(SPLabels.TIME_WINDOW_FLAG, 0) != 0) {
                    if (((double) timeLapse) > MAX_TIME_WINDOW) {
                        Prefs.with(context).save(SPLabels.ACCURACY_SAVED_TIME, System.currentTimeMillis());
                        Prefs.with(context).save(SPLabels.BAD_ACCURACY_COUNT, 0);
                        Prefs.with(context).save(SPLabels.TIME_WINDOW_FLAG, 0);
                    }
                } else if (5 <= Prefs.with(context).getInt(SPLabels.BAD_ACCURACY_COUNT, 0)) {
                    location.setAccuracy(3000.001f);
                   // Database2.getInstance(context).insertUSLLog(Constants.EVENT_LR_5_LOC_BAD_ACCURACY);
                    Prefs.with(context).save(SPLabels.BAD_ACCURACY_COUNT, 0);
                    Prefs.with(context).save(SPLabels.TIME_WINDOW_FLAG, 1);
                }
                final Context context2 = context;
                new Thread(new Runnable() {
                    public void run() {
                        Database.getInstance(context2).updateDriverCurrentLocation(context2, location);
                        //new DriverLocationDispatcher().sendLocationToServer(context2);
                        Looper.prepare();
                        Toast.makeText(context2, location.getLatitude()+"", Toast.LENGTH_LONG).show();
                    }
                }).start();
                int currentapiVersion = Build.VERSION.SDK_INT;
                if (Prefs.with(context).getInt(Constants.IS_OFFLINE, 0) == 1) {
                    Prefs.with(context).save(Constants.FREE_STATE_UPDATE_TIME_PERIOD, Prefs.with(context).getLong(Constants.OFFLINE_UPDATE_TIME_PERIOD, 180000));
                    context.stopService(new Intent(context, DriverLocationUpdateService.class));
                    setAlarm(context);
                } else if (Utils.isBatteryCharging(context)) {
                    if (currentapiVersion >= 21) {
                        if (Prefs.with(context).getLong(Constants.FREE_STATE_UPDATE_TIME_PERIOD, 120000) != Prefs.with(context).getLong(Constants.FREE_STATE_UPDATE_TIME_PERIOD_CHARGING_V5, LOCATION_UPDATE_TIME_PERIOD)) {
                            Prefs.with(context).save(Constants.FREE_STATE_UPDATE_TIME_PERIOD, Prefs.with(context).getLong(Constants.FREE_STATE_UPDATE_TIME_PERIOD_CHARGING_V5, LOCATION_UPDATE_TIME_PERIOD));
                            context.stopService(new Intent(context, DriverLocationUpdateService.class));
                            setAlarm(context);
                        }
                    } else if (Prefs.with(context).getLong(Constants.FREE_STATE_UPDATE_TIME_PERIOD, 120000) != Prefs.with(context).getLong(Constants.FREE_STATE_UPDATE_TIME_PERIOD_CHARGING, LOCATION_UPDATE_TIME_PERIOD)) {
                        Prefs.with(context).save(Constants.FREE_STATE_UPDATE_TIME_PERIOD, Prefs.with(context).getLong(Constants.FREE_STATE_UPDATE_TIME_PERIOD_CHARGING, LOCATION_UPDATE_TIME_PERIOD));
                        context.stopService(new Intent(context, DriverLocationUpdateService.class));
                        setAlarm(context);
                    }
                } else if (location.getAccuracy() > HIGH_ACCURACY_ACCURACY_CHECK) {
                    Log.m616i(this.TAG, "onReceive DriverLocationUpdateService restarted location.getAccuracy()=" + location.getAccuracy());
                    context.stopService(new Intent(context, DriverLocationUpdateService.class));
                    setAlarm(context);
                   // Database2.getInstance(context).insertUSLLog(Constants.EVENT_LR_LOC_BAD_ACCURACY_RESTART);
                } else if (Prefs.with(context).getLong(Constants.FREE_STATE_UPDATE_TIME_PERIOD, 120000) != Prefs.with(context).getLong(Constants.FREE_STATE_UPDATE_TIME_PERIOD_NON_CHARGING, 120000)) {
                    Prefs.with(context).save(Constants.FREE_STATE_UPDATE_TIME_PERIOD, Prefs.with(context).getLong(Constants.FREE_STATE_UPDATE_TIME_PERIOD_NON_CHARGING, 120000));
                    context.stopService(new Intent(context, DriverLocationUpdateService.class));
                    setAlarm(context);
                }
                if (Utils.compareDouble(oldlocation.getLatitude(), location.getLatitude()) == 0 && Utils.compareDouble(oldlocation.getLongitude(), location.getLongitude()) == 0) {
                  //  Database2.getInstance(context).insertUSLLog(Constants.EVENT_LRD_STALE_GPS_RESTART_SERVICE);
                    context.stopService(new Intent(context, DriverLocationUpdateService.class));
                    setAlarm(context);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAlarm(Context context) {
        if (PendingIntent.getBroadcast(context, PI_REQUEST_CODE, new Intent(context, DriverServiceRestartReceiver.class).setAction(RESTART_SERVICE), PendingIntent.FLAG_NO_CREATE) != null) {
            cancelAlarm(context);
        }
        Intent intent = new Intent(context, DriverServiceRestartReceiver.class);
        intent.setAction(RESTART_SERVICE);
        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 20000, PendingIntent.getBroadcast(context, PI_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, DriverServiceRestartReceiver.class);
        intent.setAction(RESTART_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, PI_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).cancel(pendingIntent);
        pendingIntent.cancel();
    }

    /*public void onReceive(Context context, Intent intent) {
        try {


            Bundle extras = intent.getExtras();
            FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
            final Location location = (Location) extras.get(FusedLocationProviderApi.KEY_LOCATION_CHANGED);
            if ( location != null) {
                final Location oldlocation = Database.getInstance(context).getDriverCurrentLocation(context);
                final double speed_1 = MapUtils.distance(oldlocation, location) / ((double) ((System.currentTimeMillis() - oldlocation.getTime()) / 1000));
                Logger.log(this.TAG, (Database.getInstance(context).getTotalDistance()/1000)+" km\n"
                        +MapUtils.distance(oldlocation, location)+"\n"
                        +speed_1+"\n"
                        +"isRunning : "+ ActivityReceiverDriver.isRunning);
                if (speed_1 > 20.0d) {
                    Logger.log(this.TAG, "onReceive DriverLocationUpdateService restarted speed_1=" + speed_1);
                    context.stopService(new Intent(context, DriverLocationUpdateService.class));
                    setAlarm(context);

                   // return;
                }

                final Context context2 = context;
                new Thread(new Runnable() {
                    public void run() {
                        String driverStatus = AppPreferencesDriver.getDriverStatus(context2);
                        if(driverStatus.equals(DriverStatus.ON_TRIP) && ((double) location.getAccuracy()) < 200.0d && ActivityReceiverDriver.isRunning){

                            double distanceDiff = MapUtils.distance(oldlocation, location);


                            long timediffer1 = location.getTime() - oldlocation.getTime();
                            DateFormat format = new SimpleDateFormat("ss");
                            Date dateAA = new Date(timediffer1);
                            int diffTime = Integer.parseInt(format.format(dateAA));

                            //165  330

                            int tA = diffTime * 33;


                            double tempspeed = (distanceDiff / diffTime) * 3600/1000;


                            if(tA > 0 && tA >= distanceDiff && tempspeed > 1){

                                double oldDist = Database.getInstance(context2).getTotalDistance();
                                double newDist = oldDist + distanceDiff;
                                Database.getInstance(context2).updateTotalDistance(newDist);
                                Database.getInstance(context2).updateDriverCurrentLocation(context2, location);
                            }


                        }


                    }
                }).start();

            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setAlarm(Context context) {
        if (PendingIntent.getBroadcast(context, PI_REQUEST_CODE, new Intent(context, DriverServiceRestartReceiver.class).setAction(RESTART_SERVICE), PendingIntent.FLAG_NO_CREATE) != null) {
            cancelAlarm(context);
        }
        Intent intent = new Intent(context, DriverServiceRestartReceiver.class);
        intent.setAction(RESTART_SERVICE);
        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 20000, PendingIntent.getBroadcast(context, PI_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, DriverServiceRestartReceiver.class);
        intent.setAction(RESTART_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, PI_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).cancel(pendingIntent);
        pendingIntent.cancel();
    }*/
}
