package com.b4edriver.CommonClasses.BroadCastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.b4edriver.CommonClasses.ServerConnection.JSONParser;
import com.b4edriver.CommonClasses.ServerConnection.VolleyCallBack;
import com.b4edriver.CommonClasses.Services.AlarmServicesDriver;
import com.b4edriver.CommonClasses.Services.FusedLocationService;
import com.b4edriver.CommonClasses.Utils.AppConstantDriver;
import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.b4edriver.CommonClasses.Utils.Function;
import com.b4edriver.CommonClasses.Utils.Logs;
import com.b4elibrary.Logger;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JAI on 9/3/2016.
 */
public class LocationReceiver extends BroadcastReceiver {




    @Override
    public void onReceive(final Context context, final Intent calledIntent) {
        Logger.log("LOC_RECEIVER", "Location RECEIVED!");
        double latitude, longitude, speed, distance;
        String batteryStatus;
        if (calledIntent.getAction().equals("com.b4edriver.action.LOCATION")){
            latitude = calledIntent.getDoubleExtra("latitude", -1);
        longitude = calledIntent.getDoubleExtra("longitude", -1);
        speed = calledIntent.getFloatExtra("speed", 0);
            distance = calledIntent.getFloatExtra("distance", 0.0f);
        batteryStatus = AppPreferencesDriver.getBatteryStatus(context);//calledIntent.getStringExtra("batteryStatus");
        Logger.log("Re loacation update2", latitude + ">>" + longitude+"\n"+calledIntent.getDoubleExtra("latitude", -1));

            if(!AppPreferencesDriver.getWaitApi(context)) {
                updateLocationTaskDriver(context, latitude, longitude, speed, batteryStatus,distance);
            }

    }

    }

    private void updateRemote(final Context context, final double latitude, final double longitude, final double speed, final String batteryStatus )
    {
        //HERE YOU CAN PUT YOUR ASYNCTASK TO UPDATE THE LOCATION ON YOUR SERVER
        if(!AppPreferencesDriver.getWaitApi(context)) {
          //  updateLocationTaskDriver(context, latitude, longitude, speed, batteryStatus);


        }/*else{
            Handler  handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    AppPreferencesDriver.setWaitApi(context, false);
                }
            };
            handler.postDelayed(runnable, 10000);
        }*/
    }

    public void updateLocationTaskDriver(final Context context, double latitude,
                                         double longitude, double speed, String batteryStatus, double distance) {

        context.sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
        context.sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));
        Logger.log("Re loacation update1", latitude +">>"+ longitude);
        final JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("method", AppConstantDriver.METHOD.TRIP_LOG);
            jsonObj.put("driverId", AppPreferencesDriver.getDriverId(context));
            jsonObj.put("tripId", AppPreferencesDriver.getTripId(context));
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

            Logger.log("Re loacation send", jsonObj.toString());

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