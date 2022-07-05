package com.b4edriver.b4edrivers;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

/**
 * Created by Kirti Tiwari on 04-11-2017.
 */

public class AppPreferance {
    public static final String SHARED_PREFERENCE_NAME = "b42drivers";
    public static final String USERID = "USERID";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_MOBILE = "USER_MOBILE";
    public static final String START_LOCATION = "START_LOCATION";
    public static final String TRIP_STATUS = "TRIP_STATUS";
    public static final String TRIP_ID = "TRIP_ID";
    public static final String PICKUP_ADDRESS = "PICKUP_ADDRESS";
    public static final String CUSTOMER_MOB_VERYFY = "CUSTOMER_MOB_VERYFY";
    public static final String METER_READING = "METER_READING";


    public static void setUserid(Context context, int value) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(USERID, value);
        editor.commit();
    }

    public static int getUserid(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(USERID, 0);
    }

    public static void setMeterReading(Context context, float value) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(METER_READING, value);
        editor.commit();
    }

    public static float getMeterReading(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getFloat(METER_READING, 0f);
    }



    public static void setUserName(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_NAME, value);
        editor.commit();
    }

    public static String getUserName(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getString(USER_NAME, "");
    }


    public static void setUserMobile(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_MOBILE, value);
        editor.commit();
    }

    public static String getUserMobile(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getString(USER_MOBILE, "");
    }

    public static String getTripId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getString(TRIP_ID, "");
    }


    public static void setTripId(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TRIP_ID, value);
        editor.commit();
    }

    public static boolean isCustomerMobVeryfy(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(CUSTOMER_MOB_VERYFY, false);
    }


    public static void setCustomerMobVeryfy(Context context, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(CUSTOMER_MOB_VERYFY, value);
        editor.commit();
    }

    public static String getPickupAddress(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getString(PICKUP_ADDRESS, "");
    }


    public static void setPickupAddress(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PICKUP_ADDRESS, value);
        editor.commit();
    }

    public static void setTripStatus(Context context, int value) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(TRIP_STATUS, value);
        editor.commit();
    }

    public static int getTripStatus(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(TRIP_STATUS, 0);
    }

    public static void setStartLocation(Context context, String key, double lat, double lng) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(START_LOCATION+"lat"+key, (float) lat);
        editor.putFloat(START_LOCATION+"lng"+key, (float) lng);
        editor.commit();
    }

    public static Location getStartLocation(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        float lat = preferences.getFloat(START_LOCATION+"lat"+key, 0.0f);
        float lng = preferences.getFloat(START_LOCATION+"lng"+key, 0.0f);
        //new Double(lat).doubleValue(), new Double(lng).doubleValue()
        Location location = new Location("");
        location.setLatitude(lat);
        location.setLatitude(lng);
        return location;
    }
    public static void removeStartLocation(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(START_LOCATION+"lat"+key);
        editor.commit();

    }


    public static boolean isUserLogedin(Context context){
        return getUserid(context) == 0 ? false : true;
    }

}
