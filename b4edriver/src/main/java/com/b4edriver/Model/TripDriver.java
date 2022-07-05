package com.b4edriver.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MAX on 11/12/2015.
 */
public class TripDriver implements Parcelable {
    Long id;
    String distance="", fare="", date="",  customerImage="", userName="",userNumber="",
            sourceAddress="", destinationAddress="", month="", tripStatus="", driverName="", driverNumber="",service_id="";
    Double sourceLatitude=0.0, sourcelogitude=0.0, destinationLatitude=0.0, destinationLogitude=0.0;

    String pickCname,pickCno,dropCname,dropCno;
    String businessName;

    public TripDriver(){}


    protected TripDriver(Parcel in) {
        id = in.readLong();
        distance = in.readString();
        fare = in.readString();
        date = in.readString();
        sourceLatitude = in.readDouble();
        sourcelogitude = in.readDouble();
        destinationLatitude = in.readDouble();
        destinationLogitude = in.readDouble();
        sourceAddress = in.readString();
        destinationAddress = in.readString();
        month = in.readString();
        customerImage = in.readString();
        tripStatus = in.readString();
        pickCname = in.readString();
        pickCno = in.readString();
        dropCname = in.readString();
        dropCno = in.readString();
        driverName = in.readString();
        driverNumber = in.readString();
        service_id = in.readString();
        userName = in.readString();
        userNumber = in.readString();
        businessName = in.readString();
    }

    public static final Creator<TripDriver> CREATOR = new Creator<TripDriver>() {
        @Override
        public TripDriver createFromParcel(Parcel in) {
            return new TripDriver(in);
        }

        @Override
        public TripDriver[] newArray(int size) {
            return new TripDriver[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

   /* public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }*/

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverNumber() {
        return driverNumber;
    }

    public void setDriverNumber(String driverNumber) {
        this.driverNumber = driverNumber;
    }

    public Double getSourceLatitude() {
        return sourceLatitude;
    }

    public void setSourceLatitude(Double sourceLatitude) {
        this.sourceLatitude = sourceLatitude;
    }

    public Double getSourcelogitude() {
        return sourcelogitude;
    }

    public void setSourcelogitude(Double sourcelogitude) {
        this.sourcelogitude = sourcelogitude;
    }

    public Double getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(Double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public Double getDestinationLogitude() {
        return destinationLogitude;
    }

    public void setDestinationLogitude(Double destinationLogitude) {
        this.destinationLogitude = destinationLogitude;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    // month as a use title for new trip

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

   /* public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }*/

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }



    public String getCustomerImage() {
        return customerImage;
    }

    public void setCustomerImage(String customerImage) {
        this.customerImage = customerImage;
    }


    public String getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(String tripStatus) {
        this.tripStatus = tripStatus;
    }



    public String getPickCname() {
        return pickCname;
    }

    public void setPickCname(String pickCname) {
        this.pickCname = pickCname;
    }

    public String getPickCno() {
        return pickCno;
    }

    public void setPickCno(String pickCno) {
        this.pickCno = pickCno;
    }

    public String getDropCname() {
        return dropCname;
    }

    public void setDropCname(String dropCname) {
        this.dropCname = dropCname;
    }

    public String getDropCno() {
        return dropCno;
    }

    public void setDropCno(String dropCno) {
        this.dropCno = dropCno;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(distance);
        dest.writeString(fare);
        dest.writeString(date);
        dest.writeDouble(sourceLatitude);
        dest.writeDouble(sourcelogitude);
        dest.writeDouble(destinationLatitude);
        dest.writeDouble(destinationLogitude);
        dest.writeString(sourceAddress);
        dest.writeString(destinationAddress);
        dest.writeString(month);
        dest.writeString(customerImage);
        dest.writeString(tripStatus);
        dest.writeString(pickCname);
        dest.writeString(pickCno);
        dest.writeString(dropCname);
        dest.writeString(dropCno);
        dest.writeString(driverName);
        dest.writeString(driverNumber);
        dest.writeString(service_id);
        dest.writeString(userName);
        dest.writeString(userNumber);
        dest.writeString(businessName);
    }

    @Override
    public String toString() {
        return "TripDriver{" +
                "id=" + id +
                ", distance='" + distance + '\'' +
                ", fare='" + fare + '\'' +
                ", date='" + date + '\'' +
                ", customerImage='" + customerImage + '\'' +
                ", userName='" + userName + '\'' +
                ", userNumber='" + userNumber + '\'' +
                ", sourceAddress='" + sourceAddress + '\'' +
                ", destinationAddress='" + destinationAddress + '\'' +
                ", month='" + month + '\'' +
                ", tripStatus='" + tripStatus + '\'' +
                ", driverName='" + driverName + '\'' +
                ", driverNumber='" + driverNumber + '\'' +
                ", service_id='" + service_id + '\'' +
                ", sourceLatitude=" + sourceLatitude +
                ", sourcelogitude=" + sourcelogitude +
                ", destinationLatitude=" + destinationLatitude +
                ", destinationLogitude=" + destinationLogitude +
                ", pickCname='" + pickCname + '\'' +
                ", pickCno='" + pickCno + '\'' +
                ", dropCname='" + dropCname + '\'' +
                ", dropCno='" + dropCno + '\'' +
                ", businessName='" + businessName + '\'' +
                '}';
    }
}
