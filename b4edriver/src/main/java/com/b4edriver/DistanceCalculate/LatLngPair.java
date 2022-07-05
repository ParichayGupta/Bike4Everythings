package com.b4edriver.DistanceCalculate;

import com.google.android.gms.maps.model.LatLng;

public class LatLngPair {
    public double deltaDistance;
    public LatLng destination;
    public LatLng source;

    public LatLngPair(LatLng source, LatLng destination, double deltaDistance) {
        this.source = source;
        this.destination = destination;
        this.deltaDistance = deltaDistance;
    }

    public boolean equals(Object o) {
        try {
            LatLngPair matchO = (LatLngPair) o;
            return (Utils.compareDouble(matchO.source.latitude, this.source.latitude) == 0 && Utils.compareDouble(matchO.source.longitude, this.source.longitude) == 0) || (Utils.compareDouble(matchO.destination.latitude, this.destination.latitude) == 0 && Utils.compareDouble(matchO.destination.longitude, this.destination.longitude) == 0);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String toString() {
        return this.source + " " + this.destination + " " + this.deltaDistance;
    }
}
