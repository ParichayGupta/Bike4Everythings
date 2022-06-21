package com.bike4everythingbussiness.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by manishsingh on 06/12/17.
 */

public class PickAddress implements Parcelable {
    @SerializedName("pick_id")
    @Expose
    private int id;
    @SerializedName("pickName")
    @Expose
    private String pickName;
    @SerializedName("pickMobile")
    @Expose
    private String pickMobile;
    @SerializedName("pickAddress")
    @Expose
    private String pickAddress;
    @SerializedName("pickAddressName")
    @Expose
    private String pickAddressName;
    @SerializedName("pickLat")
    @Expose
    private double pickLat;
    @SerializedName("pickLng")
    @Expose
    private double pickLng;
    @SerializedName("select")
    @Expose
    private boolean select;


    public PickAddress(){}

    protected PickAddress(Parcel in) {
        id = in.readInt();
        pickName = in.readString();
        pickMobile = in.readString();
        pickAddress = in.readString();
        pickAddressName = in.readString();
        pickLat = in.readDouble();
        pickLng = in.readDouble();
        select = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(pickName);
        dest.writeString(pickMobile);
        dest.writeString(pickAddress);
        dest.writeString(pickAddressName);
        dest.writeDouble(pickLat);
        dest.writeDouble(pickLng);
        dest.writeByte((byte) (select ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PickAddress> CREATOR = new Creator<PickAddress>() {
        @Override
        public PickAddress createFromParcel(Parcel in) {
            return new PickAddress(in);
        }

        @Override
        public PickAddress[] newArray(int size) {
            return new PickAddress[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPickName() {
        return pickName;
    }

    public void setPickName(String pickName) {
        this.pickName = pickName;
    }

    public String getPickMobile() {
        return pickMobile;
    }

    public void setPickMobile(String pickMobile) {
        this.pickMobile = pickMobile;
    }

    public String getPickAddress() {
        return pickAddress;
    }

    public void setPickAddress(String pickAddress) {
        this.pickAddress = pickAddress;
    }

    public String getPickAddressName() {
        return pickAddressName;
    }

    public void setPickAddressName(String pickAddressName) {
        this.pickAddressName = pickAddressName;
    }

    public double getPickLat() {
        return pickLat;
    }

    public void setPickLat(double pickLat) {
        this.pickLat = pickLat;
    }

    public double getPickLng() {
        return pickLng;
    }

    public void setPickLng(double pickLng) {
        this.pickLng = pickLng;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
