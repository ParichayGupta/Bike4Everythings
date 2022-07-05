package com.b4edriver.Model;

/**
 * Created by System7 on 06/07/2016.
 */
public class DistanceTravelled {
    int id; // Auto Increment
    String driverStatus;
    double distanceTotal, distanceFree, distanceToCustomer, distanceTrip=0.00;
    String bookingID;

    public DistanceTravelled() {
    }

    public DistanceTravelled(int id, String driverStatus, double distanceTotal, double distanceFree, double distanceToCustomer, double distanceTrip, String bookingID) {
        this.id = id;
        this.driverStatus = driverStatus;
        this.distanceTotal = distanceTotal;
        this.distanceFree = distanceFree;
        this.distanceToCustomer = distanceToCustomer;
        this.distanceTrip = distanceTrip;
        this.bookingID = bookingID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDriverStatus() {
        return driverStatus;
    }

    public void setDriverStatus(String driverStatus) {
        this.driverStatus = driverStatus;
    }

    public double getDistanceTotal() {
        return distanceTotal;
    }

    public void setDistanceTotal(double distanceTotal) {
        this.distanceTotal = distanceTotal;
    }

    public double getDistanceFree() {
        return distanceFree;
    }

    public void setDistanceFree(double distanceFree) {
        this.distanceFree = distanceFree;
    }

    public double getDistanceToCustomer() {
        return distanceToCustomer;
    }

    public void setDistanceToCustomer(double distanceToCustomer) {
        this.distanceToCustomer = distanceToCustomer;
    }

    public double getDistanceTrip() {
        return distanceTrip;
    }

    public void setDistanceTrip(double distanceTrip) {
        this.distanceTrip = distanceTrip;
    }

    public String getBookingID() {
        return bookingID;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }

    @Override
    public String toString() {
        return distanceFree +"\t" + distanceToCustomer + "\t" +distanceTrip + "\t" +distanceTotal  ;
    }
}
