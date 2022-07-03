package com.b4ebusinessdriver.DistanceCalculate;

import android.location.Location;

import com.google.android.gms.maps.model.PolylineOptions;

public interface GpsDistanceTimeUpdater {
    void addPathToMap(PolylineOptions polylineOptions);

    void drawOldPath();

    void updateDistanceTime(double d, long j, long j2, Location location, Location location2, double d2, boolean z);
}
