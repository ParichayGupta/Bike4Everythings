package com.b4ebusinessdriver.DistanceCalculate;

import android.location.Location;

public interface FusedLocationUpdate {
    void onFusedLocationChanged(Location location);
}
