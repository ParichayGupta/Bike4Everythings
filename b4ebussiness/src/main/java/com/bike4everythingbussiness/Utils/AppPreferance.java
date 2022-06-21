package com.bike4everythingbussiness.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Kirti Tiwari on 04-11-2017.
 */

public class AppPreferance {
    public static final String SHARED_PREFERENCE_NAME = "b4eb2b.db";
    public static final String USERID = "USERID";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_MOBILE = "USER_MOBILE";

    public static final String TEMP_DROP = "TEMP_DROP";
    public static final String TEMP_PICKUP = "TEMP_PICKUP";
    public static final String PAYTM_TOKEN = "paytm_token";


    public static void setUserid(Context context, int value) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(USERID, value);
        editor.commit();
    }

    public static int getUserid(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, 0);
        return preferences.getInt(USERID, 0);
    }

    public static void setPaytmtoken(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PAYTM_TOKEN, value);
        editor.commit();
    }

    public static String getPaytmtoken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, 0);
        return preferences.getString(PAYTM_TOKEN, "0");
    }

    public static void setUserName(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_NAME, value);
        editor.commit();
    }

    public static String getUserName(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, 0);
        return preferences.getString(USER_NAME, "");
    }


    public static void setUserMobile(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_MOBILE, value);
        editor.commit();
    }

    public static String getUserMobile(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, 0);
        return preferences.getString(USER_MOBILE, "");
    }


    public static void setTempDrop(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TEMP_DROP, value);
        editor.commit();
    }

    public static String getTempDrop(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, 0);
        return preferences.getString(TEMP_DROP, "[]");
    }

    public static void setTempPickup(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TEMP_PICKUP, value);
        editor.commit();
    }

    public static String getTempPickup(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, 0);
        return preferences.getString(TEMP_PICKUP, "[]");
    }

}
