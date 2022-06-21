package com.bike4everythingbussiness.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bike4everythingbussiness.MyApplication;
import com.bike4everythingbussiness.R;
import com.bike4everythingbussiness.Utils.AppConstant;
import com.bike4everythingbussiness.Utils.ConnectivityReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OtpActivity extends BaseActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    @BindView(R.id.messageTxt)
    TextView messageTxt;
    @BindView(R.id.otp)
    EditText otp;
    @BindView(R.id.otp_error)
    TextView otpError;
    @BindView(R.id.otp_btn)
    Button otpBtn;
    @BindView(R.id.error_msg)
    TextView errorMsg;

    String name;
    @BindView(R.id.otp_resend_btn)
    Button otpResendBtn;
    @BindView(R.id.otpForm)
    LinearLayout otpForm;
    @BindView(R.id.home_btn)
    Button homeBtn;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                mMessageReceiver, new IntentFilter("OTPActivity"));

        messageTxt.setText(getIntent().getStringExtra("msg"));

        name = getIntent().getStringExtra("name");

        countDownTimer = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                otpResendBtn.setVisibility(View.VISIBLE);

            }

        };
        countDownTimer.start();
    }

    @OnClick({R.id.otp_btn, R.id.otp_resend_btn, R.id.home_btn})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.otp_btn:
                if (TextUtils.isEmpty(otp.getText())) {
                    otpError.startAnimation(shake);
                } else {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("method", AppConstant.OTP_VERIFICATION);
                        jsonObject.put("business_id", getIntent().getStringExtra("id"));
                        jsonObject.put("opt_code", otp.getText().toString());
                        new OtpVerifyTask(jsonObject).execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.otp_resend_btn:
                otpResendBtn.setVisibility(View.GONE);
                countDownTimer.start();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("method", AppConstant.RESEND_OTP);
                    jsonObject.put("business_id", getIntent().getStringExtra("id"));
                    new ResendOtpTask(jsonObject).execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.home_btn:
                Intent intent = new Intent(OtpActivity.this, SigninActivity.class);
                startActivity(intent);
                finish();
                break;
        }

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isShowingNetworkDialog()) {
            dismissNetworkDialog();
        }
        if (!isConnected) {
            showNetworkDialog(OtpActivity.this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("code");
            otp.setText(message);
            otpBtn.performClick();
        }
    };




    class OtpVerifyTask extends AsyncTask<Void, Void, String> {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject;

        public OtpVerifyTask(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
            if (!isShowingProgressDialog()) {
                showProgressDialog(OtpActivity.this);
            }
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
                        .url(AppConstant.B4E_BUSINESS_REGISTER)
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
            if (isShowingProgressDialog()) {
                dismissProgressDialog();
            }

            if (!s.equalsIgnoreCase("")) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    JSONArray array = jsonObject.getJSONArray("result");
                    JSONObject object = array.getJSONObject(0);
                    if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                        String msg = object.getString("msg");
                        errorMsg.setVisibility(View.GONE);
                        otpForm.setVisibility(View.GONE);
                        homeBtn.setVisibility(View.VISIBLE);
                        messageTxt.setText(msg);
                        getSupportActionBar().setTitle("Hello Mr. "+name);

//                        String id = object.getString("business_id");
//                        AppPreferance.setUserid(OtpActivity.this, Integer.parseInt(id));
//
//                        User contact = new User();
//                        contact.setID(Integer.parseInt(id));
//                        contact.setName(name);
//                        contact.setPhoneNumber(mobile);
//
//                        databaseHandler.addContact(contact);
//
//                        Intent intent = new Intent(OtpActivity.this, MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                        finish();

                    } else {
                        String msg = object.getString("msg");
                        errorMsg.setText(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    class ResendOtpTask extends AsyncTask<Void, Void, String> {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject;

        public ResendOtpTask(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
            if (!isShowingProgressDialog()) {
                showProgressDialog(OtpActivity.this);
            }
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
                        .url(AppConstant.B4E_BUSINESS_REGISTER)
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
            if (isShowingProgressDialog()) {
                dismissProgressDialog();
            }

            if (!s.equalsIgnoreCase("")) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    JSONArray array = jsonObject.getJSONArray("result");
                    JSONObject object = array.getJSONObject(0);
                    String msg = object.getString("msg");
                    errorMsg.setText(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
