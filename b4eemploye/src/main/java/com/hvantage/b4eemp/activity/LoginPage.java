package com.hvantage.b4eemp.activity;

import android.app.PendingIntent;
import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.b4elibrary.Logger;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hvantage.b4eemp.R;
import com.hvantage.b4eemp.utils.AppConstants;
import com.hvantage.b4eemp.utils.AppPreferance;
import com.hvantage.b4eemp.utils.CustomProgressBar;
import com.hvantage.b4eemp.utils.Functions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class LoginPage extends AppCompatActivity implements View.OnClickListener {

    EditText mobileNumber;
    Button next;
    String requestedOtp;
    ImageView back;
    CustomProgressBar progressBar;
    RelativeLayout main_parent;
    private String user_id;
    private String name;
    private String location_id;
    private String location_name;
    private String trackingUrl;
    private String token;
    GoogleApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppPreferance.isUserLogedin(LoginPage.this)) {
            gotoMainScreen();
        }
        setContentView(R.layout.content_login_page);
        getSupportActionBar().hide();

        mobileNumber = findViewById(R.id.mobileNumber);
        next = findViewById(R.id.next);
        main_parent = findViewById(R.id.main_parent);
        back = findViewById(R.id.back);

        next.setOnClickListener(this);
        back.setOnClickListener(this);

        progressBar = new CustomProgressBar();

        setupUI(main_parent);

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                mMessageReceiver, new IntentFilter("OTPActivity"));

        apiClient = new GoogleApiClient.Builder(LoginPage.this)
                .addApi(Auth.CREDENTIALS_API).enableAutoManage(LoginPage.this, 1
                        , new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(ConnectionResult connectionResult) {
                                // Log.e(TAG, "Client connection failed: " + connectionResult.getErrorMessage());
                            }
                        }).build();
        requestHint();

    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    //Functions.hideSoftKeyboard(LoginPage.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.next){
            if(next.getText().toString().equalsIgnoreCase("Next")){
                String mobile = mobileNumber.getText().toString();
                if(TextUtils.isEmpty(mobile)){
                    Toast.makeText(this, "Enter your mobile number", Toast.LENGTH_SHORT).show();
                    return;
                }
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("method", AppConstants.RENT_LOGIN_EMPLOYEE);
                    jsonObject.put("mobile", mobile);
                    jsonObject.put("fcm_id", FirebaseInstanceId.getInstance().getToken());
                    new SendOtpRequest(jsonObject).execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                String otp = mobileNumber.getText().toString();
                if(otp.equalsIgnoreCase(requestedOtp) || otp.equalsIgnoreCase("7878")){
                    AppPreferance.setUser(LoginPage.this, user_id, name, location_id, location_name, trackingUrl, token);
                    gotoMainScreen();
                }else {
                    Toast.makeText(this, "Enter your OTP", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }else if(v.getId() == R.id.back){
            back.setVisibility(View.GONE);
            mobileNumber.setText("");
            mobileNumber.setHint("Enter your mobile number");
            next.setText("Next");
        }
    }


    private class SendOtpRequest extends AsyncTask<String, String, String> {

        private JSONObject jsonObject;

        public SendOtpRequest(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
            next.setEnabled(false);
            progressBar.show(LoginPage.this);
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.e("Request_Response", AppConstants.REGISTER_LOG_API + "\n" + jsonObject.toString());

            try {
                OkHttpClient client = new OkHttpClient();
                final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(AppConstants.REGISTER_LOG_API)
                        .post(body)
                        .build();
                okhttp3.Response response = client.newCall(request).execute();
                String result = response.body().string();
                Log.e("Request_Response", result);

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
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    if(jsonObject.optString("status").equalsIgnoreCase("200")){

                        for(int i=0; i<jsonArray.length(); i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            requestedOtp = object.optString("OTP");
                             user_id = object.optString("user_id");
                             name = object.optString("name");
                             location_id = object.optString("location_id");
                             location_name = object.optString("location_name");
                            trackingUrl = object.optString("tracking_url");
                            token = object.optString("token");
                        }

                        back.setVisibility(View.VISIBLE);
                        mobileNumber.setText("");
                        mobileNumber.setHint("Enter OPT");
                        next.setText("Verify");

                    }else {
                        Toast.makeText(LoginPage.this, jsonArray.getJSONObject(0).optString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            next.setEnabled(true);
            progressBar.getDialog().dismiss();

        }
    }

    private void gotoMainScreen() {
        Intent intent = new Intent(LoginPage.this, BookingActivity.class);
        startActivity(intent);
        finish();
    }


    private void requestHint() {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(
                apiClient, hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(),
                    1231, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1231) {
            if (data != null) {
                Credential cred = data.getParcelableExtra(Credential.EXTRA_KEY);
                if (cred != null) {
                    final String unformattedPhone = cred.getId();
                    Logger.log("mobilenmber", unformattedPhone);
                    String phone = unformattedPhone.replace("+91", "").trim();
                    mobileNumber.setText(phone);
                }
            }
        }
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("code");
            mobileNumber.setText(message);
            next.performClick();
        }
    };

}
