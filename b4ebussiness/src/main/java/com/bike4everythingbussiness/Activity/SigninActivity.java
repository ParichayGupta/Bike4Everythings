package com.bike4everythingbussiness.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bike4everythingbussiness.Model.User;
import com.bike4everythingbussiness.MyApplication;
import com.bike4everythingbussiness.R;
import com.bike4everythingbussiness.Utils.AppConstant;
import com.bike4everythingbussiness.Utils.AppPreferance;
import com.bike4everythingbussiness.Utils.ConnectivityReceiver;
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

import static com.bike4everythingbussiness.Services.SyncDBService.syncDatabase;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addTextChangedListener();

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
    public void onNetworkConnectionChanged(boolean isConnected) {
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


                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("method", AppConstant.BUSINESS_SIGNIN);
                        jsonObject.put("contact", mobile.getText().toString());
                        jsonObject.put("password", password.getText().toString());
                        jsonObject.put("fcm_token_id", FirebaseInstanceId.getInstance().getToken());
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
            if (!isShowingProgressDialog()) {
                showProgressDialog(SigninActivity.this);
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

                        String id = object.getString("business_id");
                        String name = object.getString("name");
                        String mobile = object.getString("contact_no");
                        String profile_image = object.getString("profile_image");

                        AppPreferance.setUserid(SigninActivity.this, Integer.parseInt(id));

                        User contact = new User();
                        contact.setId(Integer.parseInt(id));
                        contact.setName(name);
                        contact.setPhone_number(mobile);
                        contact.setProfile_image(profile_image);
                        contact.setEmail("");
                        contact.setPassword(password.getText().toString());

                        User user = databaseHandler.getContact(Integer.parseInt(id));
                        if(user.getId() == 0){
                            databaseHandler.addContact(contact);
                        }else{
                            databaseHandler.updateContact(contact);
                        }

                        Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        String msg = object.getString("msg");
                        showAlertDialog(SigninActivity.this,"Error!",msg, "Ok");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
