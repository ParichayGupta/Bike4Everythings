package com.b4ebusinessdriver.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.b4ebusinessdriver.DistanceCalculate.DriverLocationUpdateService;
import com.b4ebusinessdriver.MyApplication;
import com.b4ebusinessdriver.R;
import com.b4ebusinessdriver.Reciver.ConnectivityReceiver;
import com.b4ebusinessdriver.Services.LocationService;
import com.b4ebusinessdriver.Utils.AppConstant;
import com.b4ebusinessdriver.Utils.AppPreferences;
import com.b4ebusinessdriver.Utils.CirclePageIndicator;
import com.b4ebusinessdriver.Utils.Function;
import com.b4ebusinessdriver.Utils.ProgressDialog;
import com.b4ebusinessdriver.Utils.Slider;
import com.b4ebusinessdriver.Widgets.SignInView;
import com.b4elibrary.Logger;
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
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SplashActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener
        , LocationService.PermisstionCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final long SPLASH_TIME = 500;

    LinearLayout wifisignel;
    RelativeLayout splashscreen;
    Button refresh;
    GoogleApiClient googleApiClient;
    String currentVersion, latestVersion;
    Dialog dialog;
    SignInView signinview;
    View.OnClickListener refreshConnection = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent("android.settings.WIFI_SETTINGS");
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_splash);

        splashscreen = findViewById(R.id.splashscreen);
        refresh = findViewById(R.id.refresh);
        wifisignel = findViewById(R.id.wifisignel);
        signinview = findViewById(R.id.signinview);

        refresh.setOnClickListener(refreshConnection);

        Logger.log("driverId", AppPreferences.getUserId(SplashActivity.this));

        //AppPreferences.setUserId(SplashActivity.this, "26");
        //AppPreferencesDriver.setDriverId(SplashActivity.this, 26);


        signinview.hide(false);

        View add_view = findViewById(R.id.add_view);
        final ViewPager adds_pager = add_view.findViewById(R.id.adds_pager);
        CirclePageIndicator adds_indicator = add_view.findViewById(R.id.adds_indicator);
        Slider.ViewMethodAdd(this, adds_indicator, adds_pager);


        if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                //  Toast.makeText(instance, "GPS permission not allows.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            }
        } else {
            // Toast.makeText(instance, "GPS permission ", Toast.LENGTH_LONG).show();

            gpsEnable();
        }


    }

    public void gpsEnable() {
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
                            getCurrentVersion();

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

                            // mHandler.postDelayed(goToNextScreen, DELAY_MILLIS);
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
                if (isConnected) {
                    String android_id = Settings.Secure.getString(getContentResolver(),
                            Settings.Secure.ANDROID_ID);

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("method", AppConstant.DRIVER_BUSINESS_SIGNIN);
                        jsonObject.put("android_id", android_id);
                        //new CheckLogin(jsonObject).execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (AppPreferences.getEndTrip(SplashActivity.this)) {
                        startActivity(new Intent(SplashActivity.this, EndTripActivity.class));
                        finish();
                    } else if (AppPreferences.getUserId(SplashActivity.this).equalsIgnoreCase("")) {
                        startLoginScreen();
                    } else {
                        startServices();
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                        finish();
                    }

                } else {

                    // logingscreen.setVisibility(View.GONE);
                    wifisignel.setVisibility(View.VISIBLE);
                }
            }
        }, SPLASH_TIME);
    }

    private void startLoginScreen() {
        Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up1);

        // logingscreen.setVisibility(View.VISIBLE);
        wifisignel.setVisibility(View.GONE);
        // splashscreen.startAnimation(slide_up);
        //logingscreen.startAnimation(slide_up);
        signinview.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == 0) {

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                gpsEnable();

            } else {
                gpsEnable();

            }
            // END_INCLUDE(permission_result)

        } else {
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
            case 0:
                switch (resultCode) {
                    case RESULT_OK: {
                        getCurrentVersion();
                        break;
                    }
                    case RESULT_CANCELED: {

                        break;
                    }
                    default: {

                        break;
                    }
                }
                break;
            case 1000:
                switch (resultCode) {
                    case Activity.RESULT_OK: {

                        getCurrentVersion();
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

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            startAction();
        } else {
            //logingscreen.setVisibility(View.GONE);
            wifisignel.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void locationSettingsResult(PendingResult<LocationSettingsResult> result) {
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result
                        .getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        getCurrentVersion();

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {

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

    private void getCurrentVersion() {
        PackageManager pm = this.getPackageManager();
        PackageInfo pInfo = null;

        try {
            pInfo = pm.getPackageInfo(this.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        currentVersion = pInfo.versionName;

        new GetLatestVersion().execute();

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void showUpdateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("");
        builder.setMessage("There is newer version of this application available, click UPDATE to upgrade now?");
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                        ("market://details?id=com.b4ebusinessdriver")));

                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setCancelable(false);
        dialog = builder.show();

    }

    private void startServices() {
        if (!Function.isServiceRunning(SplashActivity.this, DriverLocationUpdateService.class.getName())) {

            startService(new Intent(SplashActivity.this, DriverLocationUpdateService.class));
        }
    }

    private class GetLatestVersion extends AsyncTask<String, String, JSONObject> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String urlOfAppFromPlayStore = "https://play.google.com/store/apps/details?id=com.b4ebusinessdriver";
                //It retrieves the latest version by scraping the content of current version from play store at runtime
                Document doc = Jsoup.connect(urlOfAppFromPlayStore).get();
                latestVersion = doc.getElementsByAttributeValue
                        ("itemprop", "softwareVersion").first().text();

            } catch (Exception e) {
                e.printStackTrace();

            } catch (NoClassDefFoundError e) {
                latestVersion = currentVersion;
            }
            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (latestVersion != null) {

                Boolean updateNeeded = false;
                String[] currentVersionCodeArray = currentVersion.split("\\.");
                String[] storeVersionCodeArray = latestVersion.split("\\.");

                int maxLength = currentVersionCodeArray.length;
                if (storeVersionCodeArray.length > maxLength) {
                    maxLength = storeVersionCodeArray.length;
                }
                for (int i = 0; i < maxLength; i++) {
                    Logger.log("VERSIONCHECK", storeVersionCodeArray[i] + " : " + currentVersionCodeArray[i]);
                    try {
                        if (Integer.parseInt(storeVersionCodeArray[i]) > Integer.parseInt(currentVersionCodeArray[i])) {
                            updateNeeded = true;
                            break;
                        }
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                        //store version code length > current version length = version needs to be updated
                        //if store version length is shorter, the if-statement already did the job
                        if (storeVersionCodeArray.length > currentVersionCodeArray.length) {
                            updateNeeded = true;
                        }
                    }


                }

                if (updateNeeded) {
                    showUpdateDialog();
                } else {
                    startAction();
                }


            } else {
                Logger.log("latestVersion", "latestVersion null");
                startAction();
            }
            super.onPostExecute(jsonObject);
        }
    }

    private class CheckLogin extends AsyncTask<Void, Void, String> {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject;

        public CheckLogin(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = "";
            try {
                OkHttpClient client = new OkHttpClient();

                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(AppConstant.B4E_DRIVER_BUSINESS_REGISTER)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                result = response.body().string();
                Log.e("Request_Response", jsonObject.toString() + "\n" + result.toString());

                return result;

            } catch (IOException e) {
                e.printStackTrace();

            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(TextUtils.isEmpty(s)){
            }else {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    JSONArray array = jsonObject.optJSONArray("result");
                    JSONObject object = array.optJSONObject(0);
                    if (jsonObject.optString("status").equalsIgnoreCase("200")) {


                    } else {
                        String msg = object.optString("msg");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
