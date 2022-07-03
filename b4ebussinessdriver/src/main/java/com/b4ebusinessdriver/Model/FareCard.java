package com.b4ebusinessdriver.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by manishsingh on 06/12/17.
 */

public class FareCard implements Parcelable {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("limit_km")
    @Expose
    private String limitKm;
    @SerializedName("base_fare")
    @Expose
    private String baseFare;
    @SerializedName("return_fare")
    @Expose
    private String returnFare;
    @SerializedName("per_km_fare")
    @Expose
    private String perKmFare;
    @SerializedName("gst")
    @Expose
    private String gst;
    @SerializedName("deliveries")
    @Expose
    private List<Delivery> deliveries = null;
    public final static Creator<FareCard> CREATOR = new Creator<FareCard>() {


        @SuppressWarnings({
                "unchecked"
        })
        public FareCard createFromParcel(Parcel in) {
            return new FareCard(in);
        }

        public FareCard[] newArray(int size) {
            return (new FareCard[size]);
        }

    }
            ;

    protected FareCard(Parcel in) {
        this.status = ((String) in.readValue((String.class.getClassLoader())));
        this.limitKm = ((String) in.readValue((String.class.getClassLoader())));
        this.baseFare = ((String) in.readValue((String.class.getClassLoader())));
        this.returnFare = ((String) in.readValue((String.class.getClassLoader())));
        this.perKmFare = ((String) in.readValue((String.class.getClassLoader())));
        this.gst = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.deliveries, (Delivery.class.getClassLoader()));
    }

    public FareCard() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLimitKm() {
        return limitKm;
    }

    public void setLimitKm(String limitKm) {
        this.limitKm = limitKm;
    }

    public String getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(String baseFare) {
        this.baseFare = baseFare;
    }

    public String getReturnFare() {
        return returnFare;
    }

    public void setReturnFare(String returnFare) {
        this.returnFare = returnFare;
    }

    public String getPerKmFare() {
        return perKmFare;
    }

    public void setPerKmFare(String perKmFare) {
        this.perKmFare = perKmFare;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

    public List<Delivery> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(List<Delivery> deliveries) {
        this.deliveries = deliveries;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(status);
        dest.writeValue(limitKm);
        dest.writeValue(baseFare);
        dest.writeValue(returnFare);
        dest.writeValue(perKmFare);
        dest.writeValue(gst);
        dest.writeList(deliveries);
    }

    public int describeContents() {
        return 0;
    }

    public class Delivery implements Parcelable
    {

        @SerializedName("drop_point")
        @Expose
        private String dropPoint;
        @SerializedName("fare")
        @Expose
        private String fare;
        public final  Creator<Delivery> CREATOR = new Creator<Delivery>() {


            @SuppressWarnings({
                    "unchecked"
            })
            public Delivery createFromParcel(Parcel in) {
                return new Delivery(in);
            }

            public Delivery[] newArray(int size) {
                return (new Delivery[size]);
            }

        }
                ;

        protected Delivery(Parcel in) {
            this.dropPoint = ((String) in.readValue((String.class.getClassLoader())));
            this.fare = ((String) in.readValue((String.class.getClassLoader())));
        }

        public Delivery() {
        }

        public String getDropPoint() {
            return dropPoint;
        }

        public void setDropPoint(String dropPoint) {
            this.dropPoint = dropPoint;
        }

        public String getFare() {
            return fare;
        }

        public void setFare(String fare) {
            this.fare = fare;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(dropPoint);
            dest.writeValue(fare);
        }

        public int describeContents() {
            return 0;
        }

        @Override
        public String toString() {
            return "{" +
                    "drop_point='" + dropPoint + '\'' +
                    ", fare='" + fare + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "FareCard{" +
                "status='" + status + '\'' +
                ", limitKm='" + limitKm + '\'' +
                ", baseFare='" + baseFare + '\'' +
                ", returnFare='" + returnFare + '\'' +
                ", perKmFare='" + perKmFare + '\'' +
                ", gst='" + gst + '\'' +
                ", deliveries=" + deliveries +
                '}';
    }
}
