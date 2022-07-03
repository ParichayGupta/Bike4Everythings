package com.b4ebusinessdriver.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.b4ebusinessdriver.MyApplication;
import com.b4ebusinessdriver.R;
import com.b4ebusinessdriver.Reciver.ConnectivityReceiver;
import com.b4ebusinessdriver.Utils.AppConstant;
import com.b4ebusinessdriver.Utils.AppPreferences;
import com.b4ebusinessdriver.Utils.ProgressDialog;

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

public class ForgotPassActivity extends BaseActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.email_error)
    TextView emailError;
    @BindView(R.id.resetpass_btn)
    Button resetpassBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0)
                    emailError.setText("");
                else {
                    emailError.startAnimation(shake);
                    emailError.setText("required");
                }
            }
        });
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isShowingNetworkDialog()) {
            dismissNetworkDialog();
        }
        if (!isConnected) {
            showNetworkDialog(ForgotPassActivity.this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @OnClick(R.id.resetpass_btn)
    public void onViewClicked() {
        if(TextUtils.isEmpty(email.getText())){
            emailError.startAnimation(shake);
            return;
        }else {
            JSONObject object = new JSONObject();
            try {
                object.put("method", AppConstant.DRIVER_FORGOT_PASSWORD);
                object.put("contact_no", email.getText().toString());
                new ResetPasswordTask(object).execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    class ResetPasswordTask extends AsyncTask<Void, Void, String> {

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject;

        public ResetPasswordTask(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
            ProgressDialog.getInstance(ForgotPassActivity.this).show();
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
                        .url(AppConstant.B4E_DRIVER_BUSINESS_FORGOT_PASSWORD)
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
            ProgressDialog.getInstance(ForgotPassActivity.this).dismiss();

            if (!s.equalsIgnoreCase("")) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    JSONArray array = jsonObject.getJSONArray("result");
                    JSONObject object = array.getJSONObject(0);
                    if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                        new AlertDialog.Builder(ForgotPassActivity.this)
                                .setTitle("Success")
                                .setMessage(object.getString("msg"))
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        Intent intent = new Intent(ForgotPassActivity.this, SigninActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .show();

                    } else {
                        String msg = object.getString("msg");
                        showAlertDialog(ForgotPassActivity.this,"Error!",msg, "Ok");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else{
                showAlertDialog(ForgotPassActivity.this,"Error!","Something went wrong please inform to admin", "Ok");
            }
        }
    }
}
