package com.bike4everythingbussiness.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bike4everythingbussiness.MyApplication;
import com.bike4everythingbussiness.R;
import com.bike4everythingbussiness.Utils.AppPreferance;
import com.bike4everythingbussiness.Utils.CirclePageIndicator;
import com.bike4everythingbussiness.Utils.ConnectivityReceiver;
import com.bike4everythingbussiness.Utils.Logger;
import com.bike4everythingbussiness.Utils.Slider;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SplashActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,ConnectivityReceiver.ConnectivityReceiverListener{
    private static final long SPLASH_TIME = 2000;

    GoogleApiClient googleApiClient;
    LinearLayout logingscreen,wifisignel;
    RelativeLayout splashscreen;
    Button signup_btn,signin_btn, refresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.content_splash);







        logingscreen = findViewById(R.id.logingscreen);
        splashscreen = findViewById(R.id.splashscreen);
        signup_btn = findViewById(R.id.signup_btn);
        signin_btn = findViewById(R.id.signin_btn);
        refresh = findViewById(R.id.refresh);
        wifisignel = findViewById(R.id.wifisignel);

        signin_btn.setOnClickListener(signIn);
        signup_btn.setOnClickListener(signUp);
        refresh.setOnClickListener(refreshConnection);

        View add_view = findViewById(R.id.add_view);
        final ViewPager adds_pager = add_view.findViewById(R.id.adds_pager);
        CirclePageIndicator adds_indicator = add_view.findViewById(R.id.adds_indicator);
        Slider.ViewMethodAdd(this, adds_indicator, adds_pager);

        if (ContextCompat.checkSelfPermission(SplashActivity.this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{android.Manifest.permission.READ_SMS}, 11);
        }

        if (ContextCompat.checkSelfPermission(SplashActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {

                //  Toast.makeText(instance, "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();

            } else {
                //  Toast.makeText(instance, "GPS permission not allows.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(SplashActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            }
        } else {
            // Toast.makeText(instance, "GPS permission ", Toast.LENGTH_LONG).show();
        }

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(SplashActivity.this).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            startAction();

                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(SplashActivity.this, 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }

                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // mHandler.postDelayed(goToNextScreen, DELAY_MILLIS);

                            break;

                    }
                }
            });


        }

    }

    public void startAction() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isConnected = ConnectivityReceiver.isConnected();
                if(isConnected) {
                    if(AppPreferance.getUserid(SplashActivity.this) == 0){
                        startLoginScreen();
                    }else {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }
                }else {
                    wifisignel.setVisibility(View.VISIBLE);
                }
            }
        }, SPLASH_TIME);
    }

    private void startLoginScreen() {
        Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);

        logingscreen.setVisibility(View.VISIBLE);
        // splashscreen.startAnimation(slide_up);
        logingscreen.startAnimation(slide_up);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == 10) {
            // BEGIN_INCLUDE(permission_result)

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                startAction();
            } else {


            }
            // END_INCLUDE(permission_result)

        }  else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // This log is never called
        Logger.log("CLICKONLOCATION", Integer.toString(resultCode));

        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);

        switch (requestCode) {
            case 1000:
                switch (resultCode) {
                    case Activity.RESULT_OK: {

                        // All required changes were successfully made
                        startAction();
                        //appExecute();


                        // mHandler.postDelayed(goToNextScreen, DELAY_MILLIS);
                        // Toast.makeText(instance, "Location enabled by user!", Toast.LENGTH_LONG).show();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {

                        finish();
                        break;
                    }
                    default: {

                        break;
                    }
                }
                break;
        }
    }


    View.OnClickListener signUp = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(SplashActivity.this, SignupActivity.class));
        }
    };
    View.OnClickListener signIn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(SplashActivity.this, SigninActivity.class));
        }
    };

    View.OnClickListener refreshConnection = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent("android.settings.WIFI_SETTINGS");
            startActivity(intent);
        }
    };

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(isConnected){
            wifisignel.setVisibility(View.GONE);
            if(AppPreferance.getUserid(SplashActivity.this) == 0){
                startLoginScreen();
            }else {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }else {
            logingscreen.setVisibility(View.GONE);
            wifisignel.setVisibility(View.VISIBLE);
        }
    }
}
