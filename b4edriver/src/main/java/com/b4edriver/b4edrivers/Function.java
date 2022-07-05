package com.b4edriver.b4edrivers;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by manishsingh on 06/01/18.
 */

public class Function {




    public static boolean isServiceRunning(Context context, String serviceClassName){
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
                return true;
            }
        }
        return false;
    }

    public static String  tripAnountFrom_S_R(String meterDistance){
        double km_meter = Double.parseDouble(meterDistance);

        double amountfor_biker;

        amountfor_biker = km_meter * 1.4;

        final String amountfor_biker_str = Utils.getDecimalFormat().format(amountfor_biker);
        return amountfor_biker_str;
    }

    public static String  tripAnountForBike(String meterDistance){
        double km_meter = Double.parseDouble(meterDistance);

        double amountfor_biker;

        if (km_meter <= 3) {
            amountfor_biker = 20;
        } else if (km_meter <= 5) {
            amountfor_biker = 25;
        } else {
            amountfor_biker = 25 + ((km_meter - 5) * 2.8);
        }
        final String amountfor_biker_str = Utils.getDecimalFormat().format(amountfor_biker);
        return amountfor_biker_str;
    }

    public static String  tripAnountForB4E(String meterDistance){
        double km_meter = Double.parseDouble(meterDistance);

        double amountfor_b4e;

        if(km_meter <= 3){
            amountfor_b4e = 35;
        }else if(km_meter <= 5){
            amountfor_b4e = 45;
        }else {
            amountfor_b4e = 45 + ((km_meter - 5) * 8);
        }
        final String amountfor_b4e_str = Utils.getDecimalFormat().format(amountfor_b4e);
        return amountfor_b4e_str;
    }

    public static String  isValidVehicleNumber(TextView tv){
        if (tv.getText() == null || TextUtils.isEmpty(tv.getText())) {
            return  "This field can't be empty.!";
        } else {

            Pattern pattern;
            Matcher matcher;
            final String VEHICLE_PATTERN = "^[A-Z]{2}\\s[0-9]{2}\\s[A-Z]{2}\\s[0-9]{4}$";
            pattern = Pattern.compile(VEHICLE_PATTERN);
            matcher = pattern.matcher(tv.getText().toString());
            boolean isMatch = matcher.matches();
            if (isMatch) {
                return "true";
            } else {
                return  "Invalid Vehicle No.";
            }
        }
    }


}
