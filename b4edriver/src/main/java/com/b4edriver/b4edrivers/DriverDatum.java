package com.b4edriver.b4edrivers;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DriverDatum implements Parcelable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("driver_id")
    @Expose
    private String driverId;
    @SerializedName("start_image")
    @Expose
    private String startImage;
    @SerializedName("start_meter")
    @Expose
    private String startMeter;
    @SerializedName("start_remark")
    @Expose
    private String startRemark;
    @SerializedName("start_lat")
    @Expose
    private String startLat;
    @SerializedName("start_lng")
    @Expose
    private String startLng;
    @SerializedName("reach_image")
    @Expose
    private String reachImage;
    @SerializedName("reach_meter")
    @Expose
    private String reachMeter;
    @SerializedName("reach_remark")
    @Expose
    private String reachRemark;
    @SerializedName("reach_lat")
    @Expose
    private String reachLat;
    @SerializedName("reach_lng")
    @Expose
    private String reachLng;
    @SerializedName("SR_km_meter")
    @Expose
    private String sRKmMeter;
    @SerializedName("SR_km_google")
    @Expose
    private String sRKmGoogle;
    @SerializedName("delivery_start_image")
    @Expose
    private String deliveryStartImage;
    @SerializedName("delivery_start_meter")
    @Expose
    private String deliveryStartMeter;
    @SerializedName("delivery_start_remark")
    @Expose
    private String deliveryStartRemark;
    @SerializedName("delivery_start_lat")
    @Expose
    private String deliveryStartLat;
    @SerializedName("delivery_start_lng")
    @Expose
    private String deliveryStartLng;
    @SerializedName("end_image")
    @Expose
    private String endImage;
    @SerializedName("end_meter")
    @Expose
    private String endMeter;
    @SerializedName("end_remark")
    @Expose
    private String endRemark;
    @SerializedName("end_lat")
    @Expose
    private String endLat;
    @SerializedName("end_lng")
    @Expose
    private String endLng;
    @SerializedName("DE_km_meter")
    @Expose
    private String dEKmMeter;
    @SerializedName("DE_km_google")
    @Expose
    private String dEKmGoogle;
    @SerializedName("km_meter")
    @Expose
    private String kmMeter;
    @SerializedName("km_google")
    @Expose
    private String kmGoogle;
    @SerializedName("added_on")
    @Expose
    private String addedOn;
    @SerializedName("completeOTPOrder")
    @Expose
    private String completeOTPOrder;
    public final static Creator<DriverDatum> CREATOR = new Creator<DriverDatum>() {


        @SuppressWarnings({
                "unchecked"
        })
        public DriverDatum createFromParcel(Parcel in) {
            return new DriverDatum(in);
        }

        public DriverDatum[] newArray(int size) {
            return (new DriverDatum[size]);
        }

    }
            ;

    protected DriverDatum(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.driverId = ((String) in.readValue((String.class.getClassLoader())));
        this.startImage = ((String) in.readValue((String.class.getClassLoader())));
        this.startMeter = ((String) in.readValue((String.class.getClassLoader())));
        this.startRemark = ((String) in.readValue((String.class.getClassLoader())));
        this.startLat = ((String) in.readValue((String.class.getClassLoader())));
        this.startLng = ((String) in.readValue((String.class.getClassLoader())));
        this.reachImage = ((String) in.readValue((String.class.getClassLoader())));
        this.reachMeter = ((String) in.readValue((String.class.getClassLoader())));
        this.reachRemark = ((String) in.readValue((String.class.getClassLoader())));
        this.reachLat = ((String) in.readValue((String.class.getClassLoader())));
        this.reachLng = ((String) in.readValue((String.class.getClassLoader())));
        this.sRKmMeter = ((String) in.readValue((String.class.getClassLoader())));
        this.sRKmGoogle = ((String) in.readValue((String.class.getClassLoader())));
        this.deliveryStartImage = ((String) in.readValue((String.class.getClassLoader())));
        this.deliveryStartMeter = ((String) in.readValue((String.class.getClassLoader())));
        this.deliveryStartRemark = ((String) in.readValue((String.class.getClassLoader())));
        this.deliveryStartLat = ((String) in.readValue((String.class.getClassLoader())));
        this.deliveryStartLng = ((String) in.readValue((String.class.getClassLoader())));
        this.endImage = ((String) in.readValue((String.class.getClassLoader())));
        this.endMeter = ((String) in.readValue((String.class.getClassLoader())));
        this.endRemark = ((String) in.readValue((String.class.getClassLoader())));
        this.endLat = ((String) in.readValue((String.class.getClassLoader())));
        this.endLng = ((String) in.readValue((String.class.getClassLoader())));
        this.dEKmMeter = ((String) in.readValue((String.class.getClassLoader())));
        this.dEKmGoogle = ((String) in.readValue((String.class.getClassLoader())));
        this.kmMeter = ((String) in.readValue((String.class.getClassLoader())));
        this.kmGoogle = ((String) in.readValue((String.class.getClassLoader())));
        this.addedOn = ((String) in.readValue((String.class.getClassLoader())));
        this.completeOTPOrder = ((String) in.readValue((String.class.getClassLoader())));
    }

    public DriverDatum() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getStartImage() {
        return startImage;
    }

    public void setStartImage(String startImage) {
        this.startImage = startImage;
    }

    public String getStartMeter() {
        return startMeter;
    }

    public void setStartMeter(String startMeter) {
        this.startMeter = startMeter;
    }

    public String getStartRemark() {
        return startRemark;
    }

    public void setStartRemark(String startRemark) {
        this.startRemark = startRemark;
    }

    public String getStartLat() {
        return startLat;
    }

    public void setStartLat(String startLat) {
        this.startLat = startLat;
    }

    public String getStartLng() {
        return startLng;
    }

    public void setStartLng(String startLng) {
        this.startLng = startLng;
    }

    public String getReachImage() {
        return reachImage;
    }

    public void setReachImage(String reachImage) {
        this.reachImage = reachImage;
    }

    public String getReachMeter() {
        return reachMeter;
    }

    public void setReachMeter(String reachMeter) {
        this.reachMeter = reachMeter;
    }

    public String getReachRemark() {
        return reachRemark;
    }

    public void setReachRemark(String reachRemark) {
        this.reachRemark = reachRemark;
    }

    public String getReachLat() {
        return reachLat;
    }

    public void setReachLat(String reachLat) {
        this.reachLat = reachLat;
    }

    public String getReachLng() {
        return reachLng;
    }

    public void setReachLng(String reachLng) {
        this.reachLng = reachLng;
    }

    public String getSRKmMeter() {
        return sRKmMeter;
    }

    public void setSRKmMeter(String sRKmMeter) {
        this.sRKmMeter = sRKmMeter;
    }

    public String getSRKmGoogle() {
        return sRKmGoogle;
    }

    public void setSRKmGoogle(String sRKmGoogle) {
        this.sRKmGoogle = sRKmGoogle;
    }

    public String getDeliveryStartImage() {
        return deliveryStartImage;
    }

    public void setDeliveryStartImage(String deliveryStartImage) {
        this.deliveryStartImage = deliveryStartImage;
    }

    public String getDeliveryStartMeter() {
        return deliveryStartMeter;
    }

    public void setDeliveryStartMeter(String deliveryStartMeter) {
        this.deliveryStartMeter = deliveryStartMeter;
    }

    public String getDeliveryStartRemark() {
        return deliveryStartRemark;
    }

    public void setDeliveryStartRemark(String deliveryStartRemark) {
        this.deliveryStartRemark = deliveryStartRemark;
    }

    public String getDeliveryStartLat() {
        return deliveryStartLat;
    }

    public void setDeliveryStartLat(String deliveryStartLat) {
        this.deliveryStartLat = deliveryStartLat;
    }

    public String getDeliveryStartLng() {
        return deliveryStartLng;
    }

    public void setDeliveryStartLng(String deliveryStartLng) {
        this.deliveryStartLng = deliveryStartLng;
    }

    public String getEndImage() {
        return endImage;
    }

    public void setEndImage(String endImage) {
        this.endImage = endImage;
    }

    public String getEndMeter() {
        return endMeter;
    }

    public void setEndMeter(String endMeter) {
        this.endMeter = endMeter;
    }

    public String getEndRemark() {
        return endRemark;
    }

    public void setEndRemark(String endRemark) {
        this.endRemark = endRemark;
    }

    public String getEndLat() {
        return endLat;
    }

    public void setEndLat(String endLat) {
        this.endLat = endLat;
    }

    public String getEndLng() {
        return endLng;
    }

    public void setEndLng(String endLng) {
        this.endLng = endLng;
    }

    public String getDEKmMeter() {
        return dEKmMeter;
    }

    public void setDEKmMeter(String dEKmMeter) {
        this.dEKmMeter = dEKmMeter;
    }

    public String getDEKmGoogle() {
        return dEKmGoogle;
    }

    public void setDEKmGoogle(String dEKmGoogle) {
        this.dEKmGoogle = dEKmGoogle;
    }

    public String getKmMeter() {
        return kmMeter;
    }

    public void setKmMeter(String kmMeter) {
        this.kmMeter = kmMeter;
    }

    public String getKmGoogle() {
        return kmGoogle;
    }

    public void setKmGoogle(String kmGoogle) {
        this.kmGoogle = kmGoogle;
    }

    public String getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(String addedOn) {
        this.addedOn = addedOn;
    }

    public String getCompleteOTPOrder() {
        return completeOTPOrder;
    }

    public void setCompleteOTPOrder(String completeOTPOrder) {
        this.completeOTPOrder = completeOTPOrder;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(driverId);
        dest.writeValue(startImage);
        dest.writeValue(startMeter);
        dest.writeValue(startRemark);
        dest.writeValue(startLat);
        dest.writeValue(startLng);
        dest.writeValue(reachImage);
        dest.writeValue(reachMeter);
        dest.writeValue(reachRemark);
        dest.writeValue(reachLat);
        dest.writeValue(reachLng);
        dest.writeValue(sRKmMeter);
        dest.writeValue(sRKmGoogle);
        dest.writeValue(deliveryStartImage);
        dest.writeValue(deliveryStartMeter);
        dest.writeValue(deliveryStartRemark);
        dest.writeValue(deliveryStartLat);
        dest.writeValue(deliveryStartLng);
        dest.writeValue(endImage);
        dest.writeValue(endMeter);
        dest.writeValue(endRemark);
        dest.writeValue(endLat);
        dest.writeValue(endLng);
        dest.writeValue(dEKmMeter);
        dest.writeValue(dEKmGoogle);
        dest.writeValue(kmMeter);
        dest.writeValue(kmGoogle);
        dest.writeValue(addedOn);
        dest.writeValue(completeOTPOrder);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "DriverDatum{" +
                "id='" + id + '\'' +
                ", driverId='" + driverId + '\'' +
                ", startImage='" + startImage + '\'' +
                ", startMeter='" + startMeter + '\'' +
                ", startRemark='" + startRemark + '\'' +
                ", startLat='" + startLat + '\'' +
                ", startLng='" + startLng + '\'' +
                ", reachImage='" + reachImage + '\'' +
                ", reachMeter='" + reachMeter + '\'' +
                ", reachRemark='" + reachRemark + '\'' +
                ", reachLat='" + reachLat + '\'' +
                ", reachLng='" + reachLng + '\'' +
                ", sRKmMeter='" + sRKmMeter + '\'' +
                ", sRKmGoogle='" + sRKmGoogle + '\'' +
                ", deliveryStartImage='" + deliveryStartImage + '\'' +
                ", deliveryStartMeter='" + deliveryStartMeter + '\'' +
                ", deliveryStartRemark='" + deliveryStartRemark + '\'' +
                ", deliveryStartLat='" + deliveryStartLat + '\'' +
                ", deliveryStartLng='" + deliveryStartLng + '\'' +
                ", endImage='" + endImage + '\'' +
                ", endMeter='" + endMeter + '\'' +
                ", endRemark='" + endRemark + '\'' +
                ", endLat='" + endLat + '\'' +
                ", endLng='" + endLng + '\'' +
                ", dEKmMeter='" + dEKmMeter + '\'' +
                ", dEKmGoogle='" + dEKmGoogle + '\'' +
                ", kmMeter='" + kmMeter + '\'' +
                ", kmGoogle='" + kmGoogle + '\'' +
                ", addedOn='" + addedOn + '\'' +
                ", completeOTPOrder='" + completeOTPOrder + '\'' +
                '}';
    }
}
