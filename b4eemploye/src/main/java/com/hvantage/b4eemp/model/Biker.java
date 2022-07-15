package com.hvantage.b4eemp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Biker implements Parcelable {
    @SerializedName("driver_id")
    @Expose
    private String driver_id;
    @SerializedName("driver_name")
    @Expose
    private String driver_name;
    @SerializedName("driver_mobile")
    @Expose
    private String driver_mobile;
    @SerializedName("bike_number")
    @Expose
    private String bike_number;

    public Biker(){}

    protected Biker(Parcel in) {
        driver_id = in.readString();
        driver_name = in.readString();
        driver_mobile = in.readString();
        bike_number = in.readString();
    }

    public static final Creator<Biker> CREATOR = new Creator<Biker>() {
        @Override
        public Biker createFromParcel(Parcel in) {
            return new Biker(in);
        }

        @Override
        public Biker[] newArray(int size) {
            return new Biker[size];
        }
    };

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getDriver_mobile() {
        return driver_mobile;
    }

    public void setDriver_mobile(String driver_mobile) {
        this.driver_mobile = driver_mobile;
    }

    public String getBike_number() {
        return bike_number;
    }

    public void setBike_number(String bike_number) {
        this.bike_number = bike_number;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(driver_id);
        dest.writeString(driver_name);
        dest.writeString(driver_mobile);
        dest.writeString(bike_number);
    }
}
