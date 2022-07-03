package com.b4ebusinessdriver.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.SystemClock;
import android.provider.Settings;

import java.util.Calendar;

/**
 * Created by Peter on 11-Jul-17.
 */

    public class AppPreferences {

    public static final String MBPREFERENCES = "mealgaadi";

    public static final String USERID = "userid";
        public static final String USERNAME = "username";
    public static final String MOBILE = "mobile";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String ISACTIVE = "isactive";
    public static final String PROFILE_PIC= "profile_pic";
    public static final String CUR_LAT= "cur_lat";
    public static final String CUR_LONG= "cur_long";
    public static final String END_TRIP= "end_trip";
    public static final String DELIVERY_ID= "delivery_id";
    public static final String ONTRIP= "ontrip";
    public static final String STARTTIME= "starttime";
    public static final String TOTAL_DISTANCE= "total_distance";

    private static AppPreferences instance;
    private final SharedPreferences sharedPreferences;
    private final Editor editor;

    public AppPreferences(Context context) {
        instance = this;
        String prefsFile = context.getPackageName();
        sharedPreferences = context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static void setUserId(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(USERID, id);
        editor.commit();
    }

    public static String getUserId(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(USERID, "");
    }

    public static void setMobileNo(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(MOBILE, id);
        editor.commit();
    }

    public static String getMobileNo(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(MOBILE, "");
    }

    public static void setUsername(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(USERNAME, id);
        editor.commit();
    }


    public static String getEmail(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(EMAIL, "");
    }

    public static void setEmail(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(EMAIL, id);
        editor.commit();
    }


    public static String getPassword(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(PASSWORD, "");
    }

    public static void setPassword(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(PASSWORD, id);
        editor.commit();
    }

    public static String getUsername(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(USERNAME, "");
    }

    public static void setActive(Context context, boolean id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putBoolean(ISACTIVE, id);
        editor.commit();
    }

    public static boolean getIsactive(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getBoolean(ISACTIVE, true);
    }

    public static void setProfilePic(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(PROFILE_PIC, id);
        editor.commit();
    }

    public static String getProfilePic(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(PROFILE_PIC, "");
    }



    public static void setCurLat(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(CUR_LAT, id);
        editor.commit();
    }

    public static String getCurLat(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(CUR_LAT, "0.0");
    }
    public static void setCurLong(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(CUR_LONG, id);
        editor.commit();
    }

    public static String getCurLong(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(CUR_LONG, "0.0");
    }

    public static void setEndTrip(Context context, boolean id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putBoolean(END_TRIP, id);
        editor.commit();
    }

    public static boolean getEndTrip(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getBoolean(END_TRIP, false);
    }
    public static void setDeliveryId(Context context, int id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putInt(DELIVERY_ID, id);
        editor.commit();
    }

    public static int getDeliveryId(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getInt(DELIVERY_ID, 0);
    }
    public static void setOntrip(Context context, boolean id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putBoolean(ONTRIP, id);
        editor.commit();
    }

    public static boolean getOntrip(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getBoolean(ONTRIP, false);
    }

    public static void setStartTime(Context context, long time) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putLong(STARTTIME, time);
        editor.commit();
    }

    public static long getStartTime(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getLong(STARTTIME, Calendar.getInstance().getTimeInMillis());
    }

    public static void setTotalDistance(Context context, String distance) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(TOTAL_DISTANCE, distance);
        editor.commit();
    }

    public static String getTotalDistance(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(TOTAL_DISTANCE, "0");
    }


}
