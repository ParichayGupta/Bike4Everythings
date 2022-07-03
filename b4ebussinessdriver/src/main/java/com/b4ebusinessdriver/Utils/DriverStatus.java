package com.b4ebusinessdriver.Utils;

/**
 * Created by manishsingh on 15/03/18.
 */

public enum DriverStatus {
    D_OFFLINE(-1),
    D_INITIAL(0),
    D_ACCEPT(1),
    D_ARRIVED(2),
    D_START_RIDE(3),
    D_IN_RIDE(4),
    D_RIDE_END(5);

    private int status;

    DriverStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }
}
