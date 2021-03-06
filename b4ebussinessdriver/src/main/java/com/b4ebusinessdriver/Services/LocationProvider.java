package com.b4ebusinessdriver.Services;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

/**
 * Created by Marcel on 2/22/2016.
 */
public class LocationProvider implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final LocationCallback mLocationCallback;
    private final GoogleApiClient mGoogleApiClient;
    private final LocationRequest mLocationRequest;
    private final Context mContext;


    public static final String TAG = LocationProvider.class.getSimpleName();

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public interface LocationCallback {
        void handleInitialLocation(Location location);
        void handleNewLocation(Location location);
        void locationSettingsResult(PendingResult<LocationSettingsResult> result);
    }

    public LocationProvider(Context context, LocationCallback callback) {

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationCallback = callback;

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(7 * 1000)
                .setFastestInterval(2 * 1000);

        mContext = context;
        Log.e("HomeActivity", " LocationProvider");
    }

    public void connect() {
        mGoogleApiClient.connect();
    }

    public void disconnect() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    public void refreshLocation(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        mLocationCallback.handleInitialLocation(location);
        startPeriodicUpdates();
    }

    public void changeSetting(int priority, long setInterval, long fastInterval){

        mLocationRequest.setPriority(priority);
        mLocationRequest.setInterval(setInterval);
        mLocationRequest.setFastestInterval(fastInterval);

    }



    @Override
    public void onConnected(Bundle bundle) {
        checkGpsEnable();
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        mLocationCallback.handleInitialLocation(location);
        startPeriodicUpdates();
    }

    private void checkGpsEnable() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings(mGoogleApiClient, builder.build());
        mLocationCallback.locationSettingsResult(result);
    }

    private void startPeriodicUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);


    }
    public void locationEnabledSuccess(){
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        mLocationCallback.handleInitialLocation(location);
    }
    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (connectionResult.hasResolution() && mContext instanceof Activity) {
            try {
                Activity activity = (Activity)mContext;
                connectionResult.startResolutionForResult(activity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged  " + location.toString());
        mLocationCallback.handleNewLocation(location);
    }
}
