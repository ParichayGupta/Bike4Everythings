package com.bike4everythingbussiness.Utils;

import android.util.Log;

import com.bike4everythingbussiness.BuildConfig;

public class Logger {
    public static void log(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }
}
