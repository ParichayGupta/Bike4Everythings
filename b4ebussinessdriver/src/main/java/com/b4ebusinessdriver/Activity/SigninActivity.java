package com.b4ebusinessdriver.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.b4ebusinessdriver.DistanceCalculate.DriverLocationUpdateService;
import com.b4ebusinessdriver.MyApplication;
import com.b4ebusinessdriver.R;
import com.b4ebusinessdriver.Utils.AppConstant;
import com.b4ebusinessdriver.Utils.AppPreferences;
import com.b4ebusinessdriver.Reciver.ConnectivityReceiver;
import com.b4ebusinessdriver.Utils.Function;
import com.b4ebusinessdriver.Utils.ProgressDialog;
import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.google.android.gms.auth.api.signin.SignInAccount;
import com.google.firebase.iid.FirebaseInstanceId;

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


public class SigninActivity extends BaseActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    @BindView(R.id.mobile)
    EditText mobile;
    @BindView(R.id.mobile_error)
    TextView mobileError;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.signin_btn)
    Button signinBtn;
    @BindView(R.id.signup_btn)
    Button signupBtn;
    @BindView(R.id.forget)
    TextView forget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addTextChangedListener();

        if(!Function.isServiceRunning(SigninActivity.this, DriverLocationUpdateService.class.getName())) {

            startService(new Intent(SigninActivity.this, DriverLocationUpdateService.class));
        }

    }

    private void addTextChangedListener() {
        mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0)
                    mobileError.setText("");
                else {
                    mobileError.startAnimation(shake);
                    mobileError.setText("required");
                }
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(SigninActivity.this.isFinishing())
            return;
        if (isShowingNetworkDialog()) {
            dismissNetworkDialog();
        }
        if (!isConnected) {
            showNetworkDialog(SigninActivity.this);
        }
    }

    @OnClick({R.id.signin_btn, R.id.signup_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.signin_btn:
                if(TextUtils.isEmpty(mobile.getText())){
                    mobileError.startAnimation(shake);
                    return;
                }else if(TextUtils.isEmpty(password.getText())){
                    return;
                }else{

                    String android_id = Settings.Secure.getString(getContentResolver(),
                            Settings.Secure.ANDROID_ID);

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("method", AppConstant.DRIVER_BUSINESS_SIGNIN);
                        jsonObject.put("contact", mobile.getText().toString());
                        jsonObject.put("password", password.getText().toString());
                        jsonObject.put("android_id", android_id);
                        jsonObject.put("fcm_token_id", FirebaseInstanceId.getInstance().getToken());

                        AppPreferences.setPassword(SigninActivity.this, password.getText().toString());
                        new SignInTask(jsonObject).execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
                break;
            case R.id.signup_btn:
                startActivity(new Intent(SigninActivity.this, SignupActivity.class));
                finish();
                break;
        }
    }

    @OnClick(R.id.forget)
    public void onViewClicked() {
        startActivity(new Intent(SigninActivity.this, ForgotPassActivity.class));
    }

    class SignInTask extends AsyncTask<Void, Void, String> {

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject;

        public SignInTask(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
            ProgressDialog.getInstance(SigninActivity.this).show();
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
            ProgressDialog.getInstance(SigninActivity.this).dismiss();

            if (!s.equalsIgnoreCase("")) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    JSONArray array = jsonObject.getJSONArray("result");
                    JSONObject object = array.getJSONObject(0);
                    if (jsonObject.getString("status").equalsIgnoreCase("200")) {

                        String id = object.getString("driver_id");
                        String b2c_driver_id = object.optString("b2c_driver_id");
                        String name = object.getString("name");
                        String mobile = object.getString("contact_no");
                        String profile_image = object.getString("profile_image");
                        String email = object.optString("email");

                        AppPreferences.setUserId(SigninActivity.this, id);
                        AppPreferences.setUsername(SigninActivity.this, name);
                        AppPreferences.setMobileNo(SigninActivity.this, mobile);
                        AppPreferences.setProfilePic(SigninActivity.this, profile_image);
                        AppPreferences.setEmail(SigninActivity.this, email);
                        if(TextUtils.isEmpty(b2c_driver_id)){
                            AppPreferencesDriver.setDriverId(SigninActivity.this, 0);
                        }else {
                            AppPreferencesDriver.setDriverId(SigninActivity.this, Integer.parseInt(b2c_driver_id));
                        }


                        Intent intent = new Intent(SigninActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        String msg = object.getString("msg");
                        showAlertDialog(SigninActivity.this,"Under Review",msg, "Ok");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
