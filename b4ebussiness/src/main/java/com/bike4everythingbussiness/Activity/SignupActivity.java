package com.bike4everythingbussiness.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bike4everythingbussiness.Adapter.CategoryAdapter;
import com.bike4everythingbussiness.Model.Items;
import com.bike4everythingbussiness.MyApplication;
import com.bike4everythingbussiness.R;
import com.bike4everythingbussiness.Utils.AppConstant;
import com.bike4everythingbussiness.Utils.ConnectivityReceiver;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
    @BindView(R.id.business)
    EditText business;
    @BindView(R.id.business_error)
    TextView businessError;
    @BindView(R.id.gst_number)
    EditText gstNumber;
    @BindView(R.id.city)
    EditText city;
    @BindView(R.id.category)
    Spinner category;
    @BindView(R.id.signupcode)
    EditText signupcode;
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addTextChangedListener();

         setupCategory();

    }

    private void setupCategory() {
        CategoryAdapter categoryAdapter = null;
        try {
            categoryAdapter = new CategoryAdapter(SignupActivity.this, 0, new GetList().execute().get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        category.setAdapter(categoryAdapter);
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    Items item = (Items) adapterView.getSelectedItem();
                    categoryId = item.getId();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
        business.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0)
                    businessError.setText("");
                else {
                    businessError.startAnimation(shake);
                    businessError.setText("required");
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
        } else if (TextUtils.isEmpty(email.getText())) {
            emailError.startAnimation(shake);
            return;
        } else if (TextUtils.isEmpty(mobile.getText())) {
            mobileError.startAnimation(shake);
            return;
        } else if (TextUtils.isEmpty(password.getText())) {
            passwordError.startAnimation(shake);
            return;
        } else if (TextUtils.isEmpty(business.getText())) {
            businessError.startAnimation(shake);
            return;
        }  else if (categoryId.equalsIgnoreCase("0")) {
            category.startAnimation(shake);
            return;
        } else {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("method", AppConstant.SIGNUP);
                jsonObject.put("name", name.getText().toString());
                jsonObject.put("email_id", email.getText().toString());
                jsonObject.put("contact", mobile.getText().toString());
                jsonObject.put("password", password.getText().toString());
                jsonObject.put("business_name", business.getText().toString());
                jsonObject.put("gst_number", gstNumber.getText().toString());
                jsonObject.put("city", city.getText().toString());
                jsonObject.put("category", categoryId);
                jsonObject.put("signupcode", signupcode.getText().toString());
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
            if (!isShowingProgressDialog()) {
                showProgressDialog(SignupActivity.this);
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
                        String otp = object.getString("otp");
                        String msg = object.getString("msg");

                        Intent intent = new Intent(SignupActivity.this, OtpActivity.class);
                        intent.putExtra("id", id);
                        intent.putExtra("msg", msg);
                        intent.putExtra("otp", otp);
                        intent.putExtra("name", name.getText().toString());
                        startActivity(intent);
                        finish();
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

    class GetList extends AsyncTask<String, Void, List<Items>> {
        List<Items> itemsLists = new ArrayList<>();


        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        public GetList() {
            if (!isShowingProgressDialog()) {
                showProgressDialog(SignupActivity.this);
            }
            Items items = new Items();
            items.setId("0");
            items.setName("Select Category");
            itemsLists.add(items);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected List<Items> doInBackground(String... params) {
            String result = "";
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject data = new JSONObject();
                data.put("method", AppConstant.BUSINESS_CATEGORY);

                Log.e("Request ", data.toString());
                //json.put("notification", dataJson);
                RequestBody body = RequestBody.create(JSON, data.toString());
                Request request = new Request.Builder()
                        .url(AppConstant.B4E_BUSINESS_CATEGORY)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                result = response.body().string();
                Log.e("Request_Response", result.toString());
                JSONObject jsonObject = new JSONObject(result);
                if(jsonObject.getString("status").equalsIgnoreCase("200")){
                    JSONArray array = jsonObject.getJSONArray("result");

                    for(int i=0; i<array.length(); i++){
                        JSONObject object = array.getJSONObject(i);
                        Items items = new Items();
                        items.setId(object.getString("id"));
                        items.setName(object.getString("name"));
                        itemsLists.add(items);
                    }
                }
                if (isShowingProgressDialog()) {
                    dismissProgressDialog();
                }

                return itemsLists;

            } catch (JSONException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            }

            if (isShowingProgressDialog()) {
                dismissProgressDialog();
            }
            return itemsLists;
        }

    }

}
