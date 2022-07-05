package com.b4edriver.CommonClasses.Services;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.b4edriver.DriverApp.DialogActivityDriver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.b4edriver.CommonClasses.Services.FusedLocationService.isRunning;
import static com.b4edriver.CommonClasses.Services.FusedLocationService.mPreviousLocation;

public class WorkTimerService extends Service {

    public static String str_receiver = "com.countdowntimerservice.receiver";

    private Handler mHandler = new Handler();
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    String strDate;
    Date date_current, date_diff;
    SharedPreferences mpref;
    SharedPreferences.Editor mEditor;

    private Timer mTimer = null;
    public static final long NOTIFY_INTERVAL = 1000;
    Intent intent;
    int timecount = 0;
    private Location checkLoaction;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mEditor = mpref.edit();
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 1, NOTIFY_INTERVAL);
        intent = new Intent(str_receiver);


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                if(checkLoaction == null){
                    checkLoaction = mPreviousLocation;
                }else {

                    if ( mPreviousLocation.getLatitude() == checkLoaction.getLatitude()
                           && isRunning && !AppPreferencesDriver.getTripId(getApplicationContext()).equalsIgnoreCase("") ) {
                        Intent intent = new Intent(getApplicationContext(), DialogActivityDriver.class);
                        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction("gps_error");
                        //startActivity(intent);
                    } else {
                        checkLoaction = mPreviousLocation;
                    }
                }


            }
        },500, 60000);

    }


    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {

                @Override
                public void run() {

                    if(AppPreferencesDriver.isonline(getApplicationContext())) {
                        calendar = Calendar.getInstance();
                        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        strDate = simpleDateFormat.format(calendar.getTime());
                        //Log.e("WorkTimer strDate", strDate);
                        twoDatesBetweenTime();
                    }

                  //  Log.e("WorkTimer isForeg: ", Function.isForeground(getApplicationContext())+"\nloc ser "
                  //  +"\n");
                    if (FusedLocationService.mGoogleApiClient == null) {
                         //new FusedLocationService(WorkTimerService.this);
                       FusedLocationService locationService =  FusedLocationService.getInstance(WorkTimerService.this);
                        locationService.googleClientReConnect();
                    } else {
                        if (!FusedLocationService.mGoogleApiClient.isConnected() || FusedLocationService.mGoogleApiClient.isConnecting()) {
                            FusedLocationService.mGoogleApiClient.connect();
                        }
                    }





                }

            });
        }

    }

    public String twoDatesBetweenTime() {

        try {
            date_current = simpleDateFormat.parse(strDate);
        } catch (Exception e) {
          //  Log.e("WorkTimer ex1", strDate);
            e.printStackTrace();
        }

        try {
            date_diff = simpleDateFormat.parse(AppPreferencesDriver.getTimerData(WorkTimerService.this));
        } catch (Exception e) {
           // Log.e("WorkTimer ex2", strDate);
            e.printStackTrace();
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date_time = simpleDateFormat.format(calendar.getTime());
            AppPreferencesDriver.setTimerData(WorkTimerService.this, date_time);
            try {
                date_diff = simpleDateFormat.parse(date_time);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
        }

        try {

            long diff = date_diff.getTime() - date_current.getTime();
            int int_hours = Integer.valueOf(mpref.getString("hours", "0"));

            long int_timer = TimeUnit.HOURS.toMillis(int_hours);
            long long_hours = int_timer - diff;

            long diffSeconds2 = long_hours / 1000 % 60;
            long diffMinutes2 = long_hours / (60 * 1000) % 60;
            long diffHours2 = long_hours / (60 * 60 * 1000) % 24;

            String str_testing = date_current.getYear()+"-"+
                    date_current.getMonth()+"-"+
                    date_current.getDay()+" "+String.format("%02d",diffHours2)
                    + ":" + String.format("%02d",diffMinutes2)
                    + ":" + String.format("%02d",diffSeconds2);

          //  Log.e("WorkTimer 111", str_testing);

            fn_update(str_testing);

            /*if (long_hours > 0) {
                String str_testing = String.format("%02d",diffHours2)
                        + ":" + String.format("%02d",diffMinutes2)
                        + ":" + String.format("%02d",diffSeconds2);

                Log.e("TIME", str_testing);

                fn_update(str_testing);
            } else {
                mEditor.putBoolean("finish", true).commit();
                mTimer.cancel();
            }*/
        }catch (Exception e){
            e.printStackTrace();
           // Log.e("WorkTimer ex3", e.getMessage());
            mTimer.cancel();
            mTimer.purge();
        }

        return "";

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Service finish","Finish");
    }

    private void fn_update(String str_time){
        AppPreferencesDriver.setCurrentDriverDuration(getApplicationContext(),str_time);
        intent.putExtra("time",str_time);
        sendBroadcast(intent);
    }
}