package com.b4ebusinessdriver.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.b4ebusinessdriver.DistanceCalculate.DriverLocationUpdateService;
import com.b4ebusinessdriver.MyApplication;
import com.b4ebusinessdriver.R;
import com.b4ebusinessdriver.Utils.AppConstant;
import com.b4ebusinessdriver.Reciver.ConnectivityReceiver;
import com.b4ebusinessdriver.Utils.Function;
import com.b4ebusinessdriver.Utils.ProgressDialog;
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

public class SignupActivity extends BaseActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.name_error)
    TextView nameError;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.email_error)
    TextView emailError;
    @BindView(R.id.mobile)
    EditText mobile;
    @BindView(R.id.mobile_error)
    TextView mobileError;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.password_error)
    TextView passwordError;
    @BindView(R.id.bikenumber)
    TextView bikenumber;
    @BindView(R.id.bikenumber_error)
    TextView bikenumberError;

    @BindView(R.id.signup_btn)
    Button signupBtn;
    @BindView(R.id.termsofuse)
    TextView termsofuse;
    @BindView(R.id.mobile_exits_error)
    TextView mobileExitsError;

    String categoryId = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addTextChangedListener();
        if (ContextCompat.checkSelfPermission(SignupActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SignupActivity.this, new String[]{Manifest.permission.READ_SMS}, 11);
        }

        if(!Function.isServiceRunning(SignupActivity.this, DriverLocationUpdateService.class.getName())) {

            startService(new Intent(SignupActivity.this, DriverLocationUpdateService.class));
        }

    }



    private void addTextChangedListener() {
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0)
                    nameError.setText("");
                else {
                    nameError.startAnimation(shake);
                    nameError.setText("required");
                }
            }
        });
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
        mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                mobileExitsError.setVisibility(View.GONE);
                if (editable.length() > 0) {
                    mobileError.setText("");
                }else {
                    mobileError.startAnimation(shake);
                    mobileError.setText("required");
                }
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0)
                    passwordError.setText("");
                else {
                    passwordError.startAnimation(shake);
                    passwordError.setText("required");
                }
            }
        });
        bikenumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0)
                    bikenumberError.setText("");
                else {
                    bikenumberError.startAnimation(shake);
                    bikenumberError.setText("required");
                }
            }
        });

        termsofuse.setMovementMethod(LinkMovementMethod.getInstance());

    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isShowingNetworkDialog()) {
            dismissNetworkDialog();
        }
        if (!isConnected) {
            showNetworkDialog(SignupActivity.this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @OnClick(R.id.signup_btn)
    public void onViewClicked() {
        if (TextUtils.isEmpty(name.getText())) {
            nameError.startAnimation(shake);
            return;
        }  else if (TextUtils.isEmpty(mobile.getText())) {
            mobileError.startAnimation(shake);
            return;
        } else if (TextUtils.isEmpty(password.getText())) {
            passwordError.startAnimation(shake);
            return;
        } else if (TextUtils.isEmpty(bikenumber.getText())) {
            bikenumberError.startAnimation(shake);
            return;
        } else {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("method", AppConstant.DRIVER_BUSINESS_SIGNUP);
                jsonObject.put("name", name.getText().toString());
               // jsonObject.put("email_id", email.getText().toString());
                jsonObject.put("contact", mobile.getText().toString());
                jsonObject.put("password", password.getText().toString());
                jsonObject.put("bike_number", bikenumber.getText().toString());
                jsonObject.put("fcm_token_id", FirebaseInstanceId.getInstance().getToken());
                new SignUpTask(jsonObject).execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    class SignUpTask extends AsyncTask<Void, Void, String> {

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject;

        public SignUpTask(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
            ProgressDialog.getInstance(SignupActivity.this).show();
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
            ProgressDialog.getInstance(SignupActivity.this).dismiss();

            if (!s.equalsIgnoreCase("")) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    JSONArray array = jsonObject.getJSONArray("result");
                    JSONObject object = array.getJSONObject(0);
                    if (jsonObject.getString("status").equalsIgnoreCase("200")) {

                      //  String id = object.getString("driver_id");

                        showAlertDialog(SignupActivity.this,"Hello "+name.getText().toString(),object.getString("msg"), "OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(SignupActivity.this, SigninActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });


                    } else {
                        String msg = object.getString("msg");
                        mobileExitsError.setVisibility(View.VISIBLE);
                        mobileExitsError.setText(msg);
                        mobileExitsError.startAnimation(shake);
                        mobile.requestFocus();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }



}
