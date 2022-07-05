package com.b4edriver.DistanceCalculate;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;

import com.b4elibrary.Logger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.analytics.FirebaseAnalytics.Param;

public class FusedLocationFetcherBackgroundBalanced implements ConnectionCallbacks, OnConnectionFailedListener {
    private static final int LOCATION_PI_ID = 6980;
    private Context context;
    private GoogleApiClient googleApiClient;
    private PendingIntent locationIntent;
    private LocationRequest locationrequest;
    private long requestInterval;

    public FusedLocationFetcherBackgroundBalanced(Context context, long requestInterval) {
        this.context = context;
        this.requestInterval = requestInterval;
    }

    public boolean isConnected() {
        if (this.googleApiClient != null) {
            return this.googleApiClient.isConnected();
        }
        return false;
    }

    public boolean isLocationEnabled(Context context) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            boolean gpsStatus = Secure.isLocationProviderEnabled(contentResolver, "gps");
            boolean netStatus = Secure.isLocationProviderEnabled(contentResolver, "network");
            return gpsStatus || netStatus;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void connect() {
        destroy();
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.context) == 0 && isLocationEnabled(this.context)) {
            buildGoogleApiClient(this.context);
        }
    }

    public void destroy() {
        try {
            Logger.log(Param.LOCATION, "destroy");
            if (this.googleApiClient == null) {
                return;
            }
            if (this.googleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(this.googleApiClient, this.locationIntent);
                this.locationIntent.cancel();
                this.googleApiClient.disconnect();
            } else if (this.googleApiClient.isConnecting()) {
                this.googleApiClient.disconnect();
            }
        } catch (Exception e) {
            Logger.log("e", "=" + e.toString());
        }
    }

    protected void createLocationRequest(long interval) {
        this.locationrequest = new LocationRequest();
        this.locationrequest.setInterval(interval);
        this.locationrequest.setFastestInterval(interval / 2);
        this.locationrequest.setPriority(102);
    }

    protected synchronized void buildGoogleApiClient(Context context) {
        this.googleApiClient = new Builder(context).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        this.googleApiClient.connect();
    }

    protected void startLocationUpdates(long interval) {
        try {
            createLocationRequest(interval);
            this.locationIntent = PendingIntent.getBroadcast(this.context, LOCATION_PI_ID, new Intent(this.context, FusedLocationReceiverBackgroundBalanced.class), PendingIntent.FLAG_UPDATE_CURRENT);
            LocationServices.FusedLocationApi.requestLocationUpdates(this.googleApiClient, this.locationrequest, this.locationIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onConnected(Bundle connectionHint) {
        startLocationUpdates(this.requestInterval);
    }

    public void onConnectionSuspended(int i) {
        destroy();
        connect();
    }

    public void onConnectionFailed(ConnectionResult result) {
        destroy();
        connect();
    }
}
