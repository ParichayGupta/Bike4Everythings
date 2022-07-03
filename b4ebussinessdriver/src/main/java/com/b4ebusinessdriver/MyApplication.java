package com.b4ebusinessdriver;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.b4ebusinessdriver.Reciver.ConnectivityReceiver;


/**
 * Created by manishsingh on 02/01/18.
 */

public class MyApplication extends MultiDexApplication {

    private static MyApplication mInstance;
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }


    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}