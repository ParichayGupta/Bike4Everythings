package com.b4ebusinessdriver.DistanceCalculate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DriverServiceRestartReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, DriverLocationUpdateService.class));
    }
}