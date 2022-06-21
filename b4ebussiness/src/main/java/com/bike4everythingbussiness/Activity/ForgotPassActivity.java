package com.bike4everythingbussiness.Activity;

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

import com.bike4everythingbussiness.MyApplication;
import com.bike4everythingbussiness.R;
import com.bike4everythingbussiness.Services.RequestToServer;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
                object.put("method", AppConstant.BUSINESS_FORGOT_PASSWORD);
                object.put("contact_no", email.getText().toString());

                forgotPassword(object);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private void forgotPassword(JSONObject jsonObject){

        RequestToServer.getInstance().send(ForgotPassActivity.this,jsonObject, AppConstant.B4E_BUSINESS_FORGOT_PASSWORD, new RequestToServer.CallBack() {
            @Override
            public void success(String json) {

                if (!json.equalsIgnoreCase("")) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(json);
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
        });
    }


}
