package com.b4ebusinessdriver.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by manishsingh on 17/01/18.
 */

public class LatLngs implements Parcelable {

    @SerializedName("current_lat")
    @Expose
    private double currentLat;
    @SerializedName("current_lng")
    @Expose
    private double currentLng;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("extra")
    @Expose
    private String extra;
    @SerializedName("iswalking")
    @Expose
    private boolean iswalking;

    public LatLngs(){}


    protected LatLngs(Parcel in) {
        currentLat = in.readDouble();
        currentLng = in.readDouble();
        distance = in.readString();
        time = in.readString();
        extra = in.readString();
        iswalking = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(currentLat);
        dest.writeDouble(currentLng);
        dest.writeString(distance);
        dest.writeString(time);
        dest.writeString(extra);
        dest.writeByte((byte) (iswalking ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LatLngs> CREATOR = new Creator<LatLngs>() {
        @Override
        public LatLngs createFromParcel(Parcel in) {
            return new LatLngs(in);
        }

        @Override
        public LatLngs[] newArray(int size) {
            return new LatLngs[size];
        }
    };

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(double currentLng) {
        this.currentLng = currentLng;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public boolean isIswalking() {
        return iswalking;
    }

    public void setIswalking(boolean iswalking) {
        this.iswalking = iswalking;
    }
}
