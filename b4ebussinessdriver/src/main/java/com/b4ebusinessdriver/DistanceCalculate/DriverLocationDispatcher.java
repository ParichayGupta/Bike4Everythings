package com.b4ebusinessdriver.DistanceCalculate;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.util.*;

import com.b4ebusinessdriver.Activity.SplashActivity;
import com.b4ebusinessdriver.Database.Database;
import com.b4ebusinessdriver.Services.LocationService;
import com.b4ebusinessdriver.Utils.AppConstant;
import com.b4ebusinessdriver.Utils.AppPreferences;
import com.b4edriver.CommonClasses.Services.FusedLocationService;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.b4ebusinessdriver.Utils.Function.getBatteryLevel;
import static com.b4ebusinessdriver.Utils.Function.isGPSEnabled;


public class DriverLocationDispatcher {
    private final String TAG = DriverLocationDispatcher.class.getSimpleName();

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void sendLocationToServer(Context context, Location location, double speed_1) {
        new sendLocation(context, location, speed_1).execute();
        if (FusedLocationService.mGoogleApiClient == null) {
            //new FusedLocationService(instance);
            FusedLocationService locationService = FusedLocationService.getInstance(context);
            locationService.googleClientReConnect();
        } else {
            if (!FusedLocationService.mGoogleApiClient.isConnected() || FusedLocationService.mGoogleApiClient.isConnecting()) {
                FusedLocationService.mGoogleApiClient.connect();
            }
        }
    }

    public class sendLocation extends AsyncTask<String, Void, String>
    {

        Location mCurrentLocation;
        double speed;

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Context context;
        public sendLocation(Context context, Location location, double speed_1) {
            this.context = context;
            mCurrentLocation = location;
            speed = speed_1;
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject data = new JSONObject();
                data.put("method", AppConstant.DRIVER_LOGS_UPDATE);
                data.put("driver_id", AppPreferences.getUserId(context));
                data.put("delivery_id", AppPreferences.getDeliveryId(context));
                data.put("isOnTrip", AppPreferences.getOntrip(context));
                data.put("fcm_token_id", FirebaseInstanceId.getInstance().getToken());
                data.put("latitude", mCurrentLocation.getLatitude());
                data.put("longitude", mCurrentLocation.getLongitude());
                data.put("batterystatus", getBatteryLevel(context));
                data.put("speed", speed);
                data.put("gps", isGPSEnabled(context));

                android.util.Log.e("Response_Response", data.toString()+"\n");
                //json.put("notification", dataJson);
                RequestBody body = RequestBody.create(JSON, data.toString());
                Request request = new Request.Builder()
                        .url(AppConstant.B4E_DRIVER_BUSINESS_UPDATE_LOGS)
                        .post(body)
                        .build();
                okhttp3.Response response = client.newCall(request).execute();
                result = response.body().string();


            } catch (NullPointerException e) {
                e.printStackTrace();
            }  catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String finalResponse) {
            super.onPostExecute(finalResponse);
            android.util.Log.e("Response_Response", AppConstant.B4E_DRIVER_BUSINESS_UPDATE_LOGS +"\n"+finalResponse);
            String s = finalResponse;
            try {
                JSONObject jsonObject = new JSONObject(s);

                if (jsonObject.getString("status").equalsIgnoreCase("200")) {

                } else if (jsonObject.get("status").equals("400")) {
                }

            } catch (JSONException e) {
                e.printStackTrace();
                FirebaseCrash.report(e);
            }



        }

    }


}
