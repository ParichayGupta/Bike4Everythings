package com.b4edriver.DistanceCalculate;

import android.location.Location;

public interface FusedLocationUpdate {
    void onFusedLocationChanged(Location location);
}
