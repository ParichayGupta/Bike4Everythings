package com.b4ebusinessdriver.DistanceCalculate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;


public class FusedLocationReceiverBackgroundHigh extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
        Location location = (Location) extras.get(FusedLocationProviderApi.KEY_LOCATION_CHANGED);
        if (!Utils.mockLocationEnabled(location) && location != null && GpsDistanceCalculator.gpsLocationUpdate != null) {
            GpsDistanceCalculator.gpsLocationUpdate.onGPSLocationChanged(location);
        }
    }
}
