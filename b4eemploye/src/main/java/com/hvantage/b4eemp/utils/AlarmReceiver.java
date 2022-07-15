package com.hvantage.b4eemp.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.b4elibrary.Logger;
import com.google.gson.Gson;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent resultIntent = new Intent(context, AlarmService.class);
        context.startService(resultIntent);
        // For our recurring task, we'll just display a message
        Logger.log("AlarmTesting", "I'm running : ");
    }
}

