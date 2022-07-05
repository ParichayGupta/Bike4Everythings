package com.b4edriver.CommonClasses.Services;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.b4edriver.CommonClasses.Utils.Function;
import com.b4edriver.DriverApp.TripDetailActivityDriver;
import com.b4edriver.GCM.MyNotification;
import com.b4edriver.Model.TripDriver;
import com.b4elibrary.Logger;

import java.util.ArrayList;

/**
 * Created by MAX on 28-Apr-16.
 */
public class AlarmServicesDriver extends Service {
    public static String myActivity = "";
    public static ArrayList<TripDriver> pendingTrip = new ArrayList<TripDriver>();
    CountDownTimer countDownTimer;

    public AlarmServicesDriver() {
        //super("AlarmServicesDriver");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(3600000, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Logger.log("AlarmService", millisUntilFinished + ": ps" + pendingTrip.size());

                TripDriver tripDriver = AppPreferencesDriver.getPendingTrip(AlarmServicesDriver.this);

                if (tripDriver != null) {
                    Logger.log("AlarmService", tripDriver.toString());
                    //Logger.log("PendingTrip", pendingTrip.size()+"::"+ pendingTrip.get(0).getId().toString());
                    if (TripDetailActivityDriver.instance == null && Function.isConnectingToInternet(AlarmServicesDriver.this)
                            && AppPreferencesDriver.getTripId(AlarmServicesDriver.this).equalsIgnoreCase("")) {

                        MyNotification.getInstance(AlarmServicesDriver.this).newTripNotify(tripDriver);
                    }
                }

                if (pendingTrip.size() == 0) {
                    stopService(new Intent(AlarmServicesDriver.this, AlarmServicesDriver.class));
                }

                /*if (FusedLocationService.mGoogleApiClient == null) {
                    new FusedLocationService(AlarmServicesDriver.this);
                } else {
                    if (!FusedLocationService.mGoogleApiClient.isConnected() || FusedLocationService.mGoogleApiClient.isConnecting()) {
                        FusedLocationService.mGoogleApiClient.connect();
                    }
                }*/


                try {
                    if (Function.isAppIsInBackground(AlarmServicesDriver.this)) {
                        Logger.log("isAppIsInBackground", Function.isAppIsInBackground(AlarmServicesDriver.this) + " Background");
                        myActivity = "Not Active";
                    } else {
                        Logger.log("isAppIsInBackground", Function.isAppIsInBackground(AlarmServicesDriver.this) + " Forground");
                        myActivity = "Active";
                    }
                } catch (NullPointerException e) {
                }
            }

            @Override
            public void onFinish() {
                Logger.log("AlarmService", "onFinish");
                /*Intent intent = new Intent(AlarmServicesDriver.this,RingAlarmActivityDriver.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/

                /*AppPreferencesDriver.setTripId(AlarmServicesDriver.this,"");
                AppPreferencesDriver.setTripstatusForDriver(AlarmServicesDriver.this, "0");

                Intent intent11 = new Intent(AlarmServicesDriver.this, NavigationDrawerDriver.class);
                intent11.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                intent11.setAction("");
                startActivity(intent11);
                stopSelf();*/

                countDownTimer.start();
            }
        };
        countDownTimer.start();


       /* while (true){
            Logger.log("PendingTrip", pendingTrip.size()+":<><>>while<><><:");
        }*/
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
