package com.b4edriver.b4edrivers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by Peter on 11-Jul-17.
 */

    public class AppPreferences {

    public static final String MBPREFERENCES = "b4edrivers";

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




}
