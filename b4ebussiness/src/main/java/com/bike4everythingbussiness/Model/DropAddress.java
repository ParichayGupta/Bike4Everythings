package com.bike4everythingbussiness.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by manishsingh on 06/12/17.
 */

public class DropAddress implements Parcelable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("dropName")
    @Expose
    private String dropName;
    @SerializedName("dropMobile")
    @Expose
    private String dropMobile;
    @SerializedName("dropAddress")
    @Expose
    private String dropAddress;
    @SerializedName("dropAmount")
    @Expose
    private String dropAmount;
    @SerializedName("dropLat")
    @Expose
    private double dropLat;
    @SerializedName("dropLng")
    @Expose
    private double dropLng;

    private boolean select;

    public DropAddress(){}

    protected DropAddress(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.dropName = ((String) in.readValue((String.class.getClassLoader())));
        this.dropMobile = ((String) in.readValue((String.class.getClassLoader())));
        this.dropAddress = ((String) in.readValue((String.class.getClassLoader())));
        this.dropAmount = ((String) in.readValue((String.class.getClassLoader())));
        this.dropLat = ((Double) in.readValue((Double.class.getClassLoader())));
        this.dropLng = ((Double) in.readValue((Double.class.getClassLoader())));
        this.select = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(dropName);
        dest.writeValue(dropMobile);
        dest.writeValue(dropAddress);
        dest.writeValue(dropAmount);
        dest.writeValue(dropLat);
        dest.writeValue(dropLng);
        dest.writeValue(select);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public final static Parcelable.Creator<DropAddress> CREATOR = new Creator<DropAddress>() {


        @SuppressWarnings({
                "unchecked"
        })
        public DropAddress createFromParcel(Parcel in) {
            return new DropAddress(in);
        }

        public DropAddress[] newArray(int size) {
            return (new DropAddress[size]);
        }

    }
            ;
   /* public static final Creator<DropAddress> CREATOR = new Creator<DropAddress>() {
        @Override
        public DropAddress createFromParcel(Parcel in) {
            return new DropAddress(in);
        }

        @Override
        public DropAddress[] newArray(int size) {
            return new DropAddress[size];
        }
    };
*/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDropName() {
        return dropName;
    }

    public void setDropName(String dropName) {
        this.dropName = dropName;
    }

    public String getDropMobile() {
        return dropMobile;
    }

    public void setDropMobile(String dropMobile) {
        this.dropMobile = dropMobile;
    }

    public String getDropAddress() {
        return dropAddress;
    }

    public void setDropAddress(String dropAddress) {
        this.dropAddress = dropAddress;
    }

    public String getDropAmount() {
        return dropAmount;
    }

    public void setDropAmount(String dropAmount) {
        this.dropAmount = dropAmount;
    }

    public double getDropLat() {
        return dropLat;
    }

    public void setDropLat(double dropLat) {
        this.dropLat = dropLat;
    }

    public double getDropLng() {
        return dropLng;
    }

    public void setDropLng(double dropLng) {
        this.dropLng = dropLng;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
