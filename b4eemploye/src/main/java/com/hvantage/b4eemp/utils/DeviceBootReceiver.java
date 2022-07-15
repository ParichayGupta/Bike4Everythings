package com.hvantage.b4eemp.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.b4elibrary.Logger;

public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            /* Setting the alarm here */

            context.startService(new Intent(context,TimeService.class));

            Logger.log("AlarmTesting", "Alarm Set DeviceBootReceiver : ");
            Toast.makeText(context, "Alarm Set 111", Toast.LENGTH_SHORT).show();
        }
    }
}