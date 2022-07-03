package com.b4ebusinessdriver.Activity;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.b4ebusinessdriver.R;
import com.b4ebusinessdriver.Utils.AppConstant;
import com.b4ebusinessdriver.Utils.AppPreferences;
import com.b4elibrary.Logger;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewOrderActivity extends AppCompatActivity {

    @BindView(R.id.txtName)
    TextView txtName;
    @BindView(R.id.pickupMobile)
    TextView pickupMobile;
    @BindView(R.id.pickupAddress)
    TextView pickupAddress;
    @BindView(R.id.reject)
    Button reject;
    @BindView(R.id.accept)
    Button accept;
    @BindView(R.id.businessname)
    TextView businessname;
    @BindView(R.id.amount)
    TextView amount;

    MediaPlayer mp;
    Vibrator vibrator;
    PhoneCustomStateListener psListener;
    TelephonyManager telephonyManager;
    @BindView(R.id.battery)
    TextView battery;
    @BindView(R.id.network)
    TextView network;
    @BindView(R.id.gpssignal)
    TextView gpssignal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        businessname.setText(getIntent().getStringExtra("buiness_name"));
        txtName.setText(getIntent().getStringExtra("pickup_name"));
        pickupAddress.setText(getIntent().getStringExtra("pickAddress"));
        pickupMobile.setText(getIntent().getStringExtra("number"));
        amount.setText("₹ "+getIntent().getStringExtra("amount"));

        // Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.car_alarm);

        mp = MediaPlayer.create(getBaseContext(), R.raw.car_alarm);
        playSound(this);
        startVibrate();

        psListener = new PhoneCustomStateListener();
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(psListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        battery.setText(getBatteryLevel(NewOrderActivity.this)+"%");

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            GpsStatus gpsStatus = locationManager.getGpsStatus(null);
            Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();
            Iterator<GpsSatellite> satI = satellites.iterator();
            while (satI.hasNext()) {
                GpsSatellite satellite = satI.next();
                Logger.log("STATUS>>>", "Satellite: snr=" + satellite.getSnr() + ", elevation=" + satellite.getElevation());

            }
            Logger.log("STATUS>>>", gpsStatus.toString() + "::" + gpsStatus.getTimeToFirstFix());
            gpssignal.setText("GPS");
        } else {
            gpssignal.setText("No Gps signal");
        }


    }

    private void playSound(final Context context) {


        Thread background = new Thread(new Runnable() {
            public void run() {
                try {
                    mp.setLooping(true);
                    mp.start();

                } catch (Throwable t) {
                    Log.i("Animation", "Thread  exception " + t);
                }
            }
        });
        background.start();
    }

    public void startVibrate() {
        long pattern[] = {0, 100, 200, 300, 400};
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, 0);
    }

    public void stopVibrate() {
        vibrator.cancel();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.stop();
        stopVibrate();
        cancelNotification(NewOrderActivity.this, 1);
    }

    @OnClick(R.id.reject)
    public void onRejectClicked() {
        cancelNotification(NewOrderActivity.this, 1);
        finish();
    }

    @OnClick(R.id.accept)
    public void onAcceptClicked() {


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", AppConstant.ACCEPT_DELIVERY);
            jsonObject.put("driver_id", AppPreferences.getUserId(NewOrderActivity.this));
            jsonObject.put("delivery_id", getIntent().getStringExtra("delivery_id"));
            jsonObject.put("fcm_token_id", FirebaseInstanceId.getInstance().getToken());
            new AcceptTask(NewOrderActivity.this, jsonObject).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

    }

    private class AcceptTask extends AsyncTask<Void, Void, String> {

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject;
        Context context;

        public AcceptTask(Context context, JSONObject jsonObject) {
            this.context = context;
            this.jsonObject = jsonObject;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            try {
                OkHttpClient client = new OkHttpClient();

                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(AppConstant.B4E_DRIVER_BUSINESS_MANAGE_DELIVERY)
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


            if (!s.equalsIgnoreCase("")) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    JSONArray array = jsonObject.getJSONArray("result");
                    JSONObject object = array.getJSONObject(0);
                    if (jsonObject.getString("status").equalsIgnoreCase("200")) {


                        Intent intent1 = new Intent(context, HomeActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent1);
                        finish();

                    } else {
                        String msg = object.getString("msg");
                        //  showAlertDialog(SigninActivity.this,"Under Review",msg, "Ok");
                        Intent intent = new Intent(context, AlertActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("message", msg);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

    public class PhoneCustomStateListener extends PhoneStateListener {

        public int signalSupport = 0;

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);

            signalSupport = signalStrength.getGsmSignalStrength();
            Logger.log(getClass().getCanonicalName(), "------ gsm signal --> " + signalSupport);

            if (signalSupport > 30) {
                Logger.log(getClass().getCanonicalName(), "Signal GSM : Good");
            network.setText("Good");

            } else if (signalSupport > 20 && signalSupport < 30) {
                Logger.log(getClass().getCanonicalName(), "Signal GSM : Avarage");
                network.setText("Avarage");

            } else if (signalSupport < 20 && signalSupport > 3) {
                Logger.log(getClass().getCanonicalName(), "Signal GSM : Week");
                network.setText("Week");

            } else if (signalSupport < 3) {
                Logger.log(getClass().getCanonicalName(), "Signal GSM : Very week");

                network.setText("Very week");
            }
        }
    }

    public int getBatteryLevel(Context context)
    {
        Intent intent  = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int    level   = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int    scale   = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        int    percent = (level*100)/scale;
        return percent;
    }

}
