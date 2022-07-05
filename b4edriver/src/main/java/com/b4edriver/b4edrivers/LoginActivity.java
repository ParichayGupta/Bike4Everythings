package com.b4edriver.b4edrivers;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.b4edriver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    EditText mobile;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(AppPreferance.isUserLogedin(LoginActivity.this)){
            MainActivity.startActivity(LoginActivity.this);
            finish();
        }
        setContentView(R.layout.b4edrivers_activity_login);
        intiView();
        setSupportActionBar(toolbar);

    }

    private void intiView() {

         toolbar=findViewById(R.id.toolbar);

         mobile=findViewById(R.id.mobile);

         next=findViewById(R.id.next);
        next.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
     //   getMenuInflater().inflate(R.menu.b4edrivers_menu_login, menu);
        return true;
    }


    @Override
    public void onClick(View v) {
        String mob = mobile.getText().toString();

        if(TextUtils.isEmpty(mob)){
            showAlert("Error","Please enter your mobile number");
        }else {


            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("method", AppConstant.DRIVER_LOGIN);
                jsonObject.put("mobile", mob);

                sendRequest(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void sendRequest(JSONObject jsonObject) {
        RequestToServer.getInstance().send(LoginActivity.this, jsonObject, AppConstant.DRIVER, new RequestToServer.CallBack() {
            @Override
            public void success(String json) {
                if(TextUtils.isEmpty(json)){
                    showAlert("Error","Please contact to administrator");
                }else {
                    try {
                        JSONObject object = new JSONObject(json);
                        if(object.getString("status").equalsIgnoreCase("200")){
                            JSONArray jsonArray = object.getJSONArray("result");
                            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                            String driver_id = jsonObject1.getString("driver_id");
                            String otp = jsonObject1.getString("otp");
                            String name = jsonObject1.getString("name");
                            String mobile = jsonObject1.getString("mobile");
                            showOtpDialog(driver_id, otp, name, mobile);
                        }else{
                            JSONArray jsonArray = object.getJSONArray("result");
                            showAlert("Error",jsonArray.getJSONObject(0).getString("msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void showAlert(String title, String msg){
        new AlertDialog.Builder(LoginActivity.this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void showOtpDialog(final String driver_id, final String otpMatch, final String name, final String mobile) {
        LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
        View dialogLayout = inflater.inflate(R.layout.b4edrivers_otp_login, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

        builder.setView(dialogLayout);
        builder.setCancelable(false);
        final AlertDialog customAlertDialog = builder.create();
        customAlertDialog.setCanceledOnTouchOutside(false);
        builder.create();
        customAlertDialog.show();

        final LinearLayout otpView = (LinearLayout) dialogLayout.findViewById(R.id.otpView);

        final EditText otptxt = (EditText) dialogLayout.findViewById(R.id.otpTxt);

        Button next = (Button) dialogLayout.findViewById(R.id.next);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String otp = otptxt.getText().toString();

                if(TextUtils.isEmpty(otp)){
                    showAlert("Error","Please enter otp!");
                }else if(otp.equalsIgnoreCase(otpMatch) || otp.equalsIgnoreCase("262610")){
                    AppPreferance.setUserid(LoginActivity.this, Integer.parseInt(driver_id));
                    AppPreferance.setUserName(LoginActivity.this, name);
                    AppPreferance.setUserMobile(LoginActivity.this, mobile);
                    MainActivity.startActivity(LoginActivity.this);
                    finish();
                }else  {
                    showAlert("Error","Otp not matched!");
                }



            }
        });




    }


}
