package com.b4ebusinessdriver.Model;

/**
 * Created by manishsingh on 05/12/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderDetail implements Parcelable
{

    @SerializedName("business_id")
    @Expose
    private String businessId;
    @SerializedName("shipping_street")
    @Expose
    private String shippingStreet;
    @SerializedName("total_amt")
    @Expose
    private String totalAmt;
    @SerializedName("shipLong")
    @Expose
    private String shipLong;
    @SerializedName("shipLat")
    @Expose
    private String shipLat;
    @SerializedName("up_cust_latitude")
    @Expose
    private String upCustLatitude;
    @SerializedName("up_cust_longitude")
    @Expose
    private String upCustLongitude;
    @SerializedName("cust_name")
    @Expose
    private String custName;
    @SerializedName("cust_contact")
    @Expose
    private String custContact;
    @SerializedName("customer_id")
    @Expose
    private String customerId;
    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("shipping_locality")
    @Expose
    private String shippingLocality;
    public final static Creator<OrderDetail> CREATOR = new Creator<OrderDetail>() {


        @SuppressWarnings({
                "unchecked"
        })
        public OrderDetail createFromParcel(Parcel in) {
            return new OrderDetail(in);
        }

        public OrderDetail[] newArray(int size) {
            return (new OrderDetail[size]);
        }

    }
            ;

    protected OrderDetail(Parcel in) {
        this.businessId = ((String) in.readValue((String.class.getClassLoader())));
        this.shippingStreet = ((String) in.readValue((String.class.getClassLoader())));
        this.totalAmt = ((String) in.readValue((String.class.getClassLoader())));
        this.shipLong = ((String) in.readValue((String.class.getClassLoader())));
        this.shipLat = ((String) in.readValue((String.class.getClassLoader())));
        this.upCustLatitude = ((String) in.readValue((String.class.getClassLoader())));
        this.upCustLongitude = ((String) in.readValue((String.class.getClassLoader())));
        this.custName = ((String) in.readValue((String.class.getClassLoader())));
        this.custContact = ((String) in.readValue((String.class.getClassLoader())));
        this.customerId = ((String) in.readValue((String.class.getClassLoader())));
        this.orderId = ((String) in.readValue((String.class.getClassLoader())));
        this.shippingLocality = ((String) in.readValue((String.class.getClassLoader())));
    }

    public OrderDetail() {
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getShippingStreet() {
        return shippingStreet;
    }

    public void setShippingStreet(String shippingStreet) {
        this.shippingStreet = shippingStreet;
    }

    public String getTotalAmt() {
        return totalAmt;
    }

    public void setTotalAmt(String totalAmt) {
        this.totalAmt = totalAmt;
    }

    public String getShipLong() {
        return shipLong;
    }

    public void setShipLong(String shipLong) {
        this.shipLong = shipLong;
    }

    public String getShipLat() {
        return shipLat;
    }

    public void setShipLat(String shipLat) {
        this.shipLat = shipLat;
    }

    public String getUpCustLatitude() {
        return upCustLatitude;
    }

    public void setUpCustLatitude(String upCustLatitude) {
        this.upCustLatitude = upCustLatitude;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustContact() {
        return custContact;
    }

    public void setCustContact(String custContact) {
        this.custContact = custContact;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getShippingLocality() {
        return shippingLocality;
    }

    public void setShippingLocality(String shippingLocality) {
        this.shippingLocality = shippingLocality;
    }

    public String getUpCustLongitude() {
        return upCustLongitude;
    }

    public void setUpCustLongitude(String upCustLongitude) {
        this.upCustLongitude = upCustLongitude;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(businessId);
        dest.writeValue(shippingStreet);
        dest.writeValue(totalAmt);
        dest.writeValue(shipLong);
        dest.writeValue(shipLat);
        dest.writeValue(upCustLatitude);
        dest.writeValue(upCustLongitude);
        dest.writeValue(custName);
        dest.writeValue(custContact);
        dest.writeValue(customerId);
        dest.writeValue(orderId);
        dest.writeValue(shippingLocality);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "businessId='" + businessId + '\'' +
                "shippingStreet='" + shippingStreet + '\'' +
                ", totalAmt='" + totalAmt + '\'' +
                ", shipLong='" + shipLong + '\'' +
                ", shipLat='" + shipLat + '\'' +
                ", upCustLatitude='" + upCustLatitude + '\'' +
                ", upCustLongitude='" + upCustLongitude + '\'' +
                ", custName='" + custName + '\'' +
                ", custContact='" + custContact + '\'' +
                ", customerId='" + customerId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", shippingLocality='" + shippingLocality + '\'' +
                '}';
    }
}