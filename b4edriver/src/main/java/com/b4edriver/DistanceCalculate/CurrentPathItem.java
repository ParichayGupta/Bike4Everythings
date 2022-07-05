package com.b4edriver.DistanceCalculate;

import com.b4edriver.CommonClasses.Utils.MapUtils;
import com.google.android.gms.maps.model.LatLng;


public class CurrentPathItem {
    public int acknowledged;
    public LatLng dLatLng;
    public int googlePath;
    public long id;
    public long parentId;
    public LatLng sLatLng;
    public int sectionIncomplete;

    public CurrentPathItem(long id, long parentId, double slat, double slng, double dlat, double dlng, int sectionIncomplete, int googlePath, int acknowledged) {
        this.id = id;
        this.parentId = parentId;
        this.sLatLng = new LatLng(slat, slng);
        this.dLatLng = new LatLng(dlat, dlng);
        this.sectionIncomplete = sectionIncomplete;
        this.googlePath = googlePath;
        this.acknowledged = acknowledged;
    }

    public double distance() {
        return MapUtils.distance(this.sLatLng, this.dLatLng);
    }

    public boolean equals(Object o) {
        try {
            return ((CurrentPathItem) o).id == this.id;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String toString() {
        //return this.id + "," + this.parentId + "," + this.sectionIncomplete + "," + this.googlePath + "," + this.acknowledged;
        return this.sLatLng.latitude + "," + this.sLatLng.longitude +"-";
              //  + "-"
               // + this.dLatLng.latitude + "," + this.dLatLng.longitude ;
    }



}
