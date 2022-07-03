package com.b4ebusinessdriver.Model;

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
    private int deliveryId = 0;
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
    @SerializedName("productImage")
    @Expose
    private List<ProductImage> productImage = new ArrayList<>();
    @SerializedName("schedule")
    @Expose
    private String schedule;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("estTotalAmount")
    @Expose
    private String estTotalAmount;
    @SerializedName("returnrequired")
    @Expose
    private boolean returnrequired;
    @SerializedName("returnrequiredValue")
    @Expose
    private String  returnrequiredValue;
    @SerializedName("added_on")
    @Expose
    private String addedOn;
    @SerializedName("isClick")
    @Expose
    private boolean isStart;
    @SerializedName("delivery_status")
    @Expose
    private String deliveryStatus;

    private boolean updateOnServer;

    public OrderDetails(){}


    protected OrderDetails(Parcel in) {
        deliveryId = in.readInt();
        orderStatus = in.readInt();
        userId = in.readString();
        pickName = in.readString();
        pickMobile = in.readString();
        pickAddress = in.readString();
        pickAddressName = in.readString();
        pickLat = in.readDouble();
        pickLng = in.readDouble();
        dropAddressList = in.createTypedArrayList(DropAddress.CREATOR);
        productImage = in.createTypedArrayList(ProductImage.CREATOR);
        schedule = in.readString();
        note = in.readString();
        amount = in.readString();
        estTotalAmount = in.readString();
        returnrequired = in.readByte() != 0;
        returnrequiredValue = in.readString();
        addedOn = in.readString();
        isStart = in.readByte() != 0;
        deliveryStatus = in.readString();
        updateOnServer = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(deliveryId);
        dest.writeInt(orderStatus);
        dest.writeString(userId);
        dest.writeString(pickName);
        dest.writeString(pickMobile);
        dest.writeString(pickAddress);
        dest.writeString(pickAddressName);
        dest.writeDouble(pickLat);
        dest.writeDouble(pickLng);
        dest.writeTypedList(dropAddressList);
        dest.writeTypedList(productImage);
        dest.writeString(schedule);
        dest.writeString(note);
        dest.writeString(amount);
        dest.writeString(estTotalAmount);
        dest.writeByte((byte) (returnrequired ? 1 : 0));
        dest.writeString(returnrequiredValue);
        dest.writeString(addedOn);
        dest.writeByte((byte) (isStart ? 1 : 0));
        dest.writeString(deliveryStatus);
        dest.writeByte((byte) (updateOnServer ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderDetails> CREATOR = new Creator<OrderDetails>() {
        @Override
        public OrderDetails createFromParcel(Parcel in) {
            return new OrderDetails(in);
        }

        @Override
        public OrderDetails[] newArray(int size) {
            return new OrderDetails[size];
        }
    };

    public int getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(int deliveryId) {
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

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
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

    public List<ProductImage> getProductImage() {
        return productImage;
    }

    public void setProductImage(List<ProductImage> productImage) {
        this.productImage = productImage;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getEstTotalAmount() {
        return estTotalAmount;
    }

    public void setEstTotalAmount(String estTotalAmount) {
        this.estTotalAmount = estTotalAmount;
    }
}
