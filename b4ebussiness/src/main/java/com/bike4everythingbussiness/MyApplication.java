package com.bike4everythingbussiness;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.bike4everythingbussiness.Utils.ConnectivityReceiver;

/**
 * Created by manishsingh on 02/01/18.
 */

public class MyApplication extends Application {

    private static MyApplication mInstance;
    private static int BOOKING_REQUIRED_AMOUNT = 100;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    public static void setBookingRequiredAmount(int amount){
        BOOKING_REQUIRED_AMOUNT = amount;
    }
    public static int getBookingRequiredAmount(){
        return BOOKING_REQUIRED_AMOUNT;
    }
}