package com.b4edriver.CommonClasses.Classes;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;

import com.b4edriver.DriverApp.TripStartedActivityDriver;
import com.b4edriver.Database.DBAdapter_Driver;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DecimalFormat;

public class FusedLocationServiceDriver implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final long INTERVAL = 0;
    private static final long FASTEST_INTERVAL = 0;
    private static final String TAG = "FusedLocationServiceDriver";
    public static double lastlistenerlat = 0.0d;
    public static double lastlistenerlong = 0.0d;
    Context context;
    DBAdapter_Driver dbAdapter_driver;
    Location preLoc;
    Location curLoc;
    long preTime;
    long curTime;
    double preSpeed = 0.0;
    double curSpeed = 0.0;
    double distance = 0.0;
    double totalDistance = 0.0;
    float accuracy = 0.0f;
    double distanceBW2Points = 0.0;
    Handler handler;
    Runnable runnable;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private Location mCurrentLocation = new Location("0.0"), mPreviousLocation = null, mImmediatePreviousLatLng = null;
    private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;

    public FusedLocationServiceDriver(Context context) {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setSmallestDisplacement(1);
        locationRequest.setInterval(INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        this.context = context;
        dbAdapter_driver = new DBAdapter_Driver(context);

        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        //System.out.println("google api client is on fused location provider is --$$$$$$$$--------------- "+googleApiClient);

        if (googleApiClient != null) {
            googleApiClient.connect();
        }


    }

    public static double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        Location currentLocation = fusedLocationProviderApi.getLastLocation(googleApiClient);
        mCurrentLocation = currentLocation;
        if (currentLocation != null) {
            /*AppPreferencesDriver.setLatitude(locationActivity.getApplicationContext(),
                    "" + currentLocation.getLatitude());
			AppPreferencesDriver.setLongitude(
					locationActivity.getApplicationContext(), "" + currentLocation.getLongitude());	*/
        } else {
            mCurrentLocation = new Location("0.0");
        }

        fusedLocationProviderApi.requestLocationUpdates(googleApiClient, locationRequest, this);


        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                updateLocation();
            }
        };
        handler.postDelayed(runnable, 5000);
    }

    private void updateLocation() {
        if (googleApiClient == null) {
            new FusedLocationServiceDriver(TripStartedActivityDriver.mInstance);
        } else {
            if (!googleApiClient.isConnected()) {
                googleApiClient.connect();
            }
        }
        if (googleApiClient.isConnected()) {
            fusedLocationProviderApi.requestLocationUpdates(googleApiClient,
                    locationRequest, this);
        }
    }

    @Override
    public void onLocationChanged(final Location location) {

        this.mCurrentLocation = location;
        /*AppPreferencesDriver.setSpeed(context, "" + location.getSpeed());
        AppPreferencesDriver.setDestilatitude(context, String.valueOf(location.getLatitude()));
        AppPreferencesDriver.setDestilogitude(context, String.valueOf(location.getLongitude()));


        if (AppPreferencesDriver.getTripStart(context).equalsIgnoreCase("yes") && location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {


            if (mPreviousLocation != null) {

                *//*if (driverStatus.equals(DriverStatus.FREE)) {
                    AppPreference.setLatLong(context, location.getLatitude(), location.getLongitude());
                }*//*


                distanceBW2Points = 0.0;
                distanceBW2Points = mPreviousLocation.distanceTo(mCurrentLocation);

                Logger.log("Without Conditions ->", " cLat : " + location.getLatitude() + " cLng : " + location.getLongitude() + " pLat : " + mPreviousLocation.getLatitude() + " pLng : " + mPreviousLocation.getLongitude() +
                        " DB2P : " + distanceBW2Points + " Accuracy : " + location.getAccuracy() +
                        " Speed : " + location.getSpeed() + " Time : " + new Date(location.getTime()) +
                        "Skipped Time : " + (location.getTime() - mPreviousLocation.getTime()));


                if (mImmediatePreviousLatLng.getAccuracy() != 0.0 && (location.getSpeed() > 0.224) && ((distanceBW2Points >= 10 && accuracy <= 30 && location.getAccuracy() != 0.0) || ((location.getTime() - mPreviousLocation.getTime()) >= 10000 && distance > 230))) {

                    Log.e(TAG, "Distance between two points " + distanceBW2Points);
                    //totalDistance = totalDistance + distanceBW2Points;
                    *//*if (!AppPreference.getDriverStatus(context).equals(driverStatus)) {
                        totalDistance = 0;
                    }*//*

                    totalDistance = totalDistance + distanceBW2Points;

                    if (TripStartedActivityDriver.already_paid_tv != null) {
                        TripStartedActivityDriver.already_paid_tv.setText(String.valueOf(roundTwoDecimals(totalDistance/1000)));
                    }


*//*
                    Logs.setLog("With Conditions ->", " cLat : " + location.getLatitude() + " cLng : " + location.getLongitude() + " pLat : " + mPreviousLocation.getLatitude() + " pLng : " + mPreviousLocation.getLongitude() +
                            " DB2P : " + distanceBW2Points + " cStatus : " + AppPreference.getDriverStatus(context) +
                            " pStatus : " + driverStatus + " Accuracy : " + location.getAccuracy() +
                            " Speed : " + location.getSpeed() + " Time : " + new Date(location.getTime()) + " disF : " + freeDistance + " disTC : " + toCustomerDistance +
                            " disTP : " + tripDistance);*//*


                    //dbAdapter_driver.insertDistance(AppPreferencesDriver.getTripId(context), String.valueOf(totalDistance));
                    mPreviousLocation = location;
                } else {
                    if (TripStartedActivityDriver.already_paid_tv != null && mPreviousLocation != null) {
                        *//*TripStartedActivityDriver.already_paid_tv.setText("totalDistance : " + String.valueOf(roundTwoDecimals(totalDistance)) + "\ncLat : " + location.getLatitude() + " cLng : " + location.getLongitude() + "\npLat : " + mPreviousLocation.getLatitude() + " pLng : " + mPreviousLocation.getLongitude() +
                                "\nDB2P : " + distanceBW2Points + "\nAccuracy : " + location.getAccuracy() +
                                "\nSpeed : " + location.getSpeed() + "\nTime : " + new Date(location.getTime()) +
                                "\nSkipped Time : " + (location.getTime() - mPreviousLocation.getTime()));*//*
                    }
                }
                mImmediatePreviousLatLng = location;
            } else {
                mPreviousLocation = location;
                mImmediatePreviousLatLng = location;
            }
        }*/


    }

    public Location getLocation() {
        return this.mCurrentLocation;
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //System.out.println("-----------------------	fused location provider onconnection failed listener ------------------------------------");
        googleApiClient.connect();
    }


}