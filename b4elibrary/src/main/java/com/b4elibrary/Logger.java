package com.b4elibrary;

import android.util.Log;

public class Logger {
    public static void log(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }
}
