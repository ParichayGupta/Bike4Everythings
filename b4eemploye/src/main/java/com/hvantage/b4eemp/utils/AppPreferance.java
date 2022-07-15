package com.hvantage.b4eemp.utils;

import android.content.Context;
import android.content.SharedPreferences;


import com.b4erental.Model.User;

import org.json.JSONArray;

/**
 * Created by Mainsh Singh on 04-11-2017.
 */

public class AppPreferance {
    public static final String SHARED_PREFERENCE_NAME = "b4eemppres";
    public static final String USERID = "USERID";
    public static final String USERNAME = "USERNAME";
    public static final String LOCATION_ID = "LOCATION_ID";
    public static final String LOCATION_NAME = "LOCATION_NAME";
    public static final String TRACKING_URL = "TRACKING_URL";
    public static final String TOKEN = "TOKEN";


    public static void setUser(Context context, String...  value) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USERID, value[0]);
        editor.putString(USERNAME, value[1]);
        editor.putString(LOCATION_ID, value[2]);
        editor.putString(LOCATION_NAME, value[3]);
        editor.putString(TRACKING_URL, value[4]);
        editor.putString(TOKEN, value[5]);
        editor.commit();
    }

    public static User getUserIdName(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        User user = new User();
        user.setUserId(preferences.getString(USERID, ""));
        user.setName(preferences.getString(USERNAME, ""));
        return user;
    }

    public static String getUserid(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getString(USERID, "");
    }

    public static String getLocationId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getString(LOCATION_ID, "");
    }
    public static String getTrackingUrl(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getString(TRACKING_URL, "");
    }
    public static String getToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getString(TOKEN, "");
    }




    public static boolean isUserLogedin(Context context){
        return !getUserid(context).equalsIgnoreCase("");
    }


}
