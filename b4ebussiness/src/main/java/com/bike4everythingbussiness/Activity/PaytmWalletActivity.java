package com.bike4everythingbussiness.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bike4everythingbussiness.Model.Wallet;
import com.bike4everythingbussiness.R;
import com.bike4everythingbussiness.Utils.AppConstant;
import com.bike4everythingbussiness.Utils.AppPreferance;
import com.bike4everythingbussiness.Utils.Logger;
import com.bike4everythingbussiness.Utils.PaytmEnvironment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PaytmWalletActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    String state;
    @BindView(R.id.mobile)
    EditText mobile;
    @BindView(R.id.requestOtp)
    Button requestOtp;
    @BindView(R.id.mobileView)
    LinearLayout mobileView;
    @BindView(R.id.mobileTxt)
    TextView mobileTxt;
    @BindView(R.id.Otp)
    EditText Otp;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.resendOtp)
    Button resendOtp;
    @BindView(R.id.OtpView)
    LinearLayout OtpView;

    PaytmEnvironment paytmEnvironment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paytm_wallet);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        paytmEnvironment = PaytmEnvironment.LIVE;

        /*String token = AppPreferance.getPaytmtoken(PaytmWalletActivity.this);

        if (token.equalsIgnoreCase("")) {
            new CheckPaytmBalance().execute();
        } else {
            ValidateToken(token);
        }*/

    }


    @OnClick(R.id.requestOtp)
    public void onRequestOtpClicked() {
        String mob = mobile.getText().toString();
        if (TextUtils.isEmpty(mob)) {
            mobile.startAnimation(shake);
        } else {
            new RequestOtp().execute(mob);
        }
    }

    @OnClick(R.id.submit)
    public void onSubmitClicked() {
        String otp = Otp.getText().toString();
        if (TextUtils.isEmpty(otp)) {
            Otp.startAnimation(shake);
        } else {
            new CheckOtp().execute(otp);
        }
    }

    @OnClick(R.id.resendOtp)
    public void onResendOtpClicked() {
        String mob = mobile.getText().toString();
        new RequestOtp().execute(mob);
    }


    private class RequestOtp extends AsyncTask<String, Void, String> {

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... params) {

            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject data = new JSONObject();
                data.put("phone", params[0]);
                data.put("clientId", paytmEnvironment.getClientID());
                data.put("scope", "wallet");
                data.put("responseType", "token");

                Log.e("Response_Response", paytmEnvironment.getAccountUrl() + "signin/otp" + "\n" + data.toString());
                //json.put("notification", dataJson);
                RequestBody body = RequestBody.create(JSON, data.toString());
                Request request = new Request.Builder()
                        .url(paytmEnvironment.getAccountUrl() + "signin/otp")
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                return result;


            } catch (JSONException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            }


            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(!s.equalsIgnoreCase("")){
                try {
                    Logger.log("state", s);
                    JSONObject jsonObject1 = new JSONObject(s);
                    if (jsonObject1.getString("status").equalsIgnoreCase("SUCCESS")) {
                        state = jsonObject1.getString("state");
                        mobileView.setVisibility(View.GONE);
                        OtpView.setVisibility(View.VISIBLE);
                        mobileTxt.setText(mobile.getText().toString());

                    }else{
                        Toast.makeText(PaytmWalletActivity.this, jsonObject1.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private class CheckOtp extends AsyncTask<String, Void, Void> {

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject data = new JSONObject();
                data.put("otp",params[0]);
                data.put("state",state);

                Log.e("Response_Response", paytmEnvironment.getAccountUrl()+"signin/validate/otp" + "\n" + data.toString());
                //json.put("notification", dataJson);
                RequestBody body = RequestBody.create(JSON, data.toString());
                Request request = new Request.Builder()
                        .url(paytmEnvironment.getAccountUrl()+"signin/validate/otp")
                        .post(body)
                        .addHeader("authorization",PaytmEnvironment.LIVE.getAuth())
                        .build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();

                try {
                    JSONObject object = new JSONObject(result);
                    if(object.toString().contains("access_token")) {
                        String token = object.getString("access_token");

                        Intent intent = new Intent();
                        intent.putExtra("token", token);
                        intent.putExtra("state", state);
                        intent.putExtra("mobile", mobileTxt.getText().toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    }else{

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }






}
