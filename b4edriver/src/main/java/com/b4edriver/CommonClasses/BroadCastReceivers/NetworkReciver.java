package com.b4edriver.CommonClasses.BroadCastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.b4edriver.CommonClasses.ServerConnection.JSONParser;
import com.b4edriver.CommonClasses.ServerConnection.ServerErrorCallBack;
import com.b4edriver.CommonClasses.ServerConnection.VolleyCallBack;
import com.b4edriver.CommonClasses.Utils.AppConstantDriver;
import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.b4edriver.CommonClasses.Utils.Function;
import com.b4edriver.DriverApp.DialogActivityDriver;
import com.b4elibrary.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by JAI on 9/3/2016.
 */
public class NetworkReciver extends BroadcastReceiver {


    @Override
    public void onReceive(final Context context, final Intent calledIntent)
    {
        Logger.log("NetworkReciver", "NetworkReciver !");
        if(AppPreferencesDriver.getTripId(context).equalsIgnoreCase("")) {
            CheckUserSession(context);

            if(Function.isOnline(context)){
                AppPreferencesDriver.setIsonline(context, true);
                String totalDuration = "0000-00-00 00:00:00";
                try {
                    totalDuration = Function.getTotalDateDiffrence(AppPreferencesDriver.getTotalDriverDuration(context),AppPreferencesDriver.getCurrentDriverDuration(context));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
               /* int totalDuration = Integer.parseInt(AppPreferencesDriver.getTotalDriverDuration(context)) +
                        Integer.parseInt(AppPreferencesDriver.getCurrentDriverDuration(context));*/
                AppPreferencesDriver.setTotalDriverDuration(context,String.valueOf(totalDuration));
                AppPreferencesDriver.setCurrentDriverDuration(context,"0000-00-00 00:00:00");
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date_time = simpleDateFormat.format(calendar.getTime());
                AppPreferencesDriver.setTimerData(context, date_time);
            }else{
                AppPreferencesDriver.setIsonline(context, false);
            }
        }

    }

    public void CheckUserSession(final Context context) {

        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("method", AppConstantDriver.METHOD.CHECKUSERDRIVERSTATUS);
            jsonObj.put("user_id", AppPreferencesDriver.getDriverId(context));
            JSONParser jsonParser = new JSONParser(context);
            jsonParser.parseJSONObjectwithoutProgress(AppConstantDriver.URL.CHECKUSERDRIVERSTATUS, 1, jsonObj, new VolleyCallBack() {
                @Override
                public void success(String response) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString("status").equalsIgnoreCase("400")){
                            Intent intent = new Intent(context, DialogActivityDriver.class);
                            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction("loginSessionExpire");
                            intent.putExtra("msg",object.getString("message"));
                            context.startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new ServerErrorCallBack() {
                @Override
                public void error(String response) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}