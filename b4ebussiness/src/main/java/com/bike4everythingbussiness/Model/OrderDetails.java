package com.bike4everythingbussiness.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manishsingh on 06/12/17.
 */

public class OrderDetails implements Parcelable {

    @SerializedName("delivery_id")
    @Expose
    private String deliveryId;
    @SerializedName("orderStatus")
    @Expose
    private int orderStatus;
    @SerializedName("userId")
    @Expose
    private String userId;
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
    @SerializedName("dropAddressList")
    @Expose
    private List<DropAddress> dropAddressList = new ArrayList<>();
    @SerializedName("schedule")
    @Expose
    private String schedule;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("returnrequired")
    @Expose
    private boolean returnrequired;
    @SerializedName("returnrequiredValue")
    @Expose
    private String  returnrequiredValue;
    @SerializedName("added_on")
    @Expose
    private String addedOn;
    @SerializedName("delivery_status")
    @Expose
    private String deliveryStatus;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("driver_name")
    @Expose
    private String driverName;

    private boolean updateOnServer;

    public OrderDetails(){}

    protected OrderDetails(Parcel in) {
        orderStatus = in.readInt();
        deliveryId = in.readString();
        userId = in.readString();
        pickName = in.readString();
        pickMobile = in.readString();
        pickAddress = in.readString();
        pickAddressName = in.readString();
        pickLat = in.readDouble();
        pickLng = in.readDouble();
        in.readList(this.dropAddressList, (DropAddress.class.getClassLoader()));
        schedule = in.readString();
        addedOn = in.readString();
        note = in.readString();
        deliveryStatus = in.readString();
        returnrequiredValue = in.readString();
        amount = in.readString();
        distance = in.readString();
        driverName = in.readString();
        returnrequired = in.readByte() != 0;
        updateOnServer = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(orderStatus);
        dest.writeString(deliveryId);
        dest.writeString(userId);
        dest.writeString(pickName);
        dest.writeString(pickMobile);
        dest.writeString(pickAddress);
        dest.writeString(pickAddressName);
        dest.writeDouble(pickLat);
        dest.writeDouble(pickLng);
        dest.writeList(dropAddressList);
        dest.writeString(schedule);
        dest.writeString(addedOn);
        dest.writeString(note);
        dest.writeString(deliveryStatus);
        dest.writeString(returnrequiredValue);
        dest.writeString(amount);
        dest.writeString(distance);
        dest.writeString(driverName);
        dest.writeByte((byte) (returnrequired ? 1 : 0));
        dest.writeByte((byte) (updateOnServer ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public final static Parcelable.Creator<OrderDetails> CREATOR = new Creator<OrderDetails>() {


        @SuppressWarnings({
                "unchecked"
        })
        public OrderDetails createFromParcel(Parcel in) {
            return new OrderDetails(in);
        }

        public OrderDetails[] newArray(int size) {
            return (new OrderDetails[size]);
        }

    }
            ;

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public List<DropAddress> getDropAddressList() {
        return dropAddressList;
    }

    public void setDropAddressList(List<DropAddress> dropAddressList) {
        this.dropAddressList = dropAddressList;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(String addedOn) {
        this.addedOn = addedOn;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isReturnrequired() {
        return returnrequired;
    }

    public void setReturnrequired(boolean returnrequired) {
        this.returnrequired = returnrequired;
    }

    public boolean isUpdateOnServer() {
        return updateOnServer;
    }

    public void setUpdateOnServer(boolean updateOnServer) {
        this.updateOnServer = updateOnServer;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getReturnrequiredValue() {
        return returnrequiredValue;
    }

    public void setReturnrequiredValue(String returnrequiredValue) {
        this.returnrequiredValue = returnrequiredValue;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    @Override
    public String toString() {
        return "OrderDetails{" +
                "deliveryId='" + deliveryId + '\'' +
                ", orderStatus=" + orderStatus +
                ", userId='" + userId + '\'' +
                ", pickName='" + pickName + '\'' +
                ", pickMobile='" + pickMobile + '\'' +
                ", pickAddress='" + pickAddress + '\'' +
                ", pickAddressName='" + pickAddressName + '\'' +
                ", pickLat=" + pickLat +
                ", pickLng=" + pickLng +
                ", dropAddressList=" + dropAddressList +
                ", schedule='" + schedule + '\'' +
                ", note='" + note + '\'' +
                ", returnrequired=" + returnrequired +
                ", returnrequiredValue='" + returnrequiredValue + '\'' +
                ", addedOn='" + addedOn + '\'' +
                ", deliveryStatus='" + deliveryStatus + '\'' +
                ", amount='" + amount + '\'' +
                ", distance='" + distance + '\'' +
                ", updateOnServer=" + updateOnServer +
                '}';
    }
}
