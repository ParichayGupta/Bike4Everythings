package com.b4edriver.Model;

/**
 * Created by MAX on 29-08-2017.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DriverLoginTime {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("driver_id")
    @Expose
    private String driverId;
    @SerializedName("vehicle_id")
    @Expose
    private String vehicleId;
    @SerializedName("login_in")
    @Expose
    private String loginIn;
    @SerializedName("login_out")
    @Expose
    private String loginOut;
    @SerializedName("total_time")
    @Expose
    private String totalTime;
    @SerializedName("date_d")
    @Expose
    private String dateD;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getLoginIn() {
        return loginIn;
    }

    public void setLoginIn(String loginIn) {
        this.loginIn = loginIn;
    }

    public String getLoginOut() {
        return loginOut;
    }

    public void setLoginOut(String loginOut) {
        this.loginOut = loginOut;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getDateD() {
        return dateD;
    }

    public void setDateD(String dateD) {
        this.dateD = dateD;
    }

    @Override
    public String toString() {
        return "DriverLoginTime{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", driverId='" + driverId + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                ", loginIn='" + loginIn + '\'' +
                ", loginOut='" + loginOut + '\'' +
                ", totalTime='" + totalTime + '\'' +
                ", dateD='" + dateD + '\'' +
                '}';
    }
}
