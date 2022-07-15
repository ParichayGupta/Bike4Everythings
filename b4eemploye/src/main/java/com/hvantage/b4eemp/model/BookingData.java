package com.hvantage.b4eemp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class BookingData implements Parcelable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("customerImage")
    @Expose
    private String customerImage;
    @SerializedName("altCustomerImage")
    @Expose
    private String altCustomerImage;
    @SerializedName("idProofImage")
    @Expose
    private String idProofImage;
    @SerializedName("altCustomerImageBytes")
    @Expose
    private byte[] altCustomerImageBytes;
    @SerializedName("idProofImageBytes")
    @Expose
    private byte[] idProofImageBytes;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("altName")
    @Expose
    private String altName;
    @SerializedName("altMobile")
    @Expose
    private String altMobile;
    @SerializedName("driver_licence")
    @Expose
    private String driverLicence;
    @SerializedName("service_name")
    @Expose
    private String serviceName;
    @SerializedName("bikeName")
    @Expose
    private String bikeName;
    @SerializedName("bikeNumber")
    @Expose
    private String bikeNumber;
    @SerializedName("pickupAddress")
    @Expose
    private String pickupAddress;
    @SerializedName("dropAddress")
    @Expose
    private String dropAddress;
    @SerializedName("selectIdProof")
    @Expose
    private String selectIdProof;
    @SerializedName("addedon")
    @Expose
    private String addedon;
    @SerializedName("issues_date")
    @Expose
    private String issuesDate;
    @SerializedName("dropoff_date")
    @Expose
    private String dropoffDate;
    @SerializedName("dropoff_time")
    @Expose
    private String dropoffTime;
    @SerializedName("pickup_date")
    @Expose
    private String pickupDate;
    @SerializedName("pickup_time")
    @Expose
    private String pickupTime;
    @SerializedName("payment_type")
    @Expose
    private String paymentType;
    @SerializedName("payment")
    @Expose
    private String payment;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("booking_status")
    @Expose
    private int bookingStatus;
    @SerializedName("driver_app")
    @Expose
    private Biker biker;


    public BookingData(){}


    protected BookingData(Parcel in) {
        id = in.readString();
        customerImage = in.readString();
        altCustomerImage = in.readString();
        idProofImage = in.readString();
        altCustomerImageBytes = in.createByteArray();
        idProofImageBytes = in.createByteArray();
        name = in.readString();
        mobile = in.readString();
        altName = in.readString();
        altMobile = in.readString();
        driverLicence = in.readString();
        serviceName = in.readString();
        bikeName = in.readString();
        bikeNumber = in.readString();
        pickupAddress = in.readString();
        dropAddress = in.readString();
        selectIdProof = in.readString();
        addedon = in.readString();
        issuesDate = in.readString();
        dropoffDate = in.readString();
        dropoffTime = in.readString();
        pickupDate = in.readString();
        pickupTime = in.readString();
        paymentType = in.readString();
        payment = in.readString();
        duration = in.readString();
        bookingStatus = in.readInt();
        biker = in.readParcelable(Biker.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(customerImage);
        dest.writeString(altCustomerImage);
        dest.writeString(idProofImage);
        dest.writeByteArray(altCustomerImageBytes);
        dest.writeByteArray(idProofImageBytes);
        dest.writeString(name);
        dest.writeString(mobile);
        dest.writeString(altName);
        dest.writeString(altMobile);
        dest.writeString(driverLicence);
        dest.writeString(serviceName);
        dest.writeString(bikeName);
        dest.writeString(bikeNumber);
        dest.writeString(pickupAddress);
        dest.writeString(dropAddress);
        dest.writeString(selectIdProof);
        dest.writeString(addedon);
        dest.writeString(issuesDate);
        dest.writeString(dropoffDate);
        dest.writeString(dropoffTime);
        dest.writeString(pickupDate);
        dest.writeString(pickupTime);
        dest.writeString(paymentType);
        dest.writeString(payment);
        dest.writeString(duration);
        dest.writeInt(bookingStatus);
        dest.writeParcelable(biker, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BookingData> CREATOR = new Creator<BookingData>() {
        @Override
        public BookingData createFromParcel(Parcel in) {
            return new BookingData(in);
        }

        @Override
        public BookingData[] newArray(int size) {
            return new BookingData[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerImage() {
        return customerImage;
    }

    public void setCustomerImage(String customerImage) {
        this.customerImage = customerImage;
    }

    public String getAltCustomerImage() {
        return altCustomerImage;
    }

    public void setAltCustomerImage(String altCustomerImage) {
        this.altCustomerImage = altCustomerImage;
    }

    public String getIdProofImage() {
        return idProofImage;
    }

    public void setIdProofImage(String idProofImage) {
        this.idProofImage = idProofImage;
    }

    public byte[] getAltCustomerImageBytes() {
        return altCustomerImageBytes;
    }

    public void setAltCustomerImageBytes(byte[] altCustomerImageBytes) {
        this.altCustomerImageBytes = altCustomerImageBytes;
    }

    public byte[] getIdProofImageBytes() {
        return idProofImageBytes;
    }

    public void setIdProofImageBytes(byte[] idProofImageBytes) {
        this.idProofImageBytes = idProofImageBytes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAltName() {
        return altName;
    }

    public void setAltName(String altName) {
        this.altName = altName;
    }

    public String getAltMobile() {
        return altMobile;
    }

    public void setAltMobile(String altMobile) {
        this.altMobile = altMobile;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getBikeName() {
        return bikeName;
    }

    public void setBikeName(String bikeName) {
        this.bikeName = bikeName;
    }

    public String getBikeNumber() {
        return bikeNumber;
    }

    public void setBikeNumber(String bikeNumber) {
        this.bikeNumber = bikeNumber;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getDropAddress() {
        return dropAddress;
    }

    public void setDropAddress(String dropAddress) {
        this.dropAddress = dropAddress;
    }

    public String getSelectIdProof() {
        return selectIdProof;
    }

    public void setSelectIdProof(String selectIdProof) {
        this.selectIdProof = selectIdProof;
    }

    public String getAddedon() {
        return addedon;
    }

    public void setAddedon(String addedon) {
        this.addedon = addedon;
    }

    public String getIssuesDate() {
        return issuesDate;
    }

    public void setIssuesDate(String issuesDate) {
        this.issuesDate = issuesDate;
    }

    public int getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(int bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public Biker getBiker() {
        return biker;
    }

    public void setBiker(Biker biker) {
        this.biker = biker;
    }

    public String getDropoffDate() {
        return dropoffDate;
    }

    public void setDropoffDate(String dropoffDate) {
        this.dropoffDate = dropoffDate;
    }

    public String getDropoffTime() {
        return dropoffTime;
    }

    public void setDropoffTime(String dropoffTime) {
        this.dropoffTime = dropoffTime;
    }

    public String getDriverLicence() {
        return driverLicence;
    }

    public void setDriverLicence(String driverLicence) {
        this.driverLicence = driverLicence;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
