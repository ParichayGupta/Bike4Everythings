package com.b4edriver.DriverApp;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.b4edriver.Adapter.DriverLoginTimeAdapter;
import com.b4edriver.CommonClasses.ServerConnection.JSONParser;
import com.b4edriver.CommonClasses.ServerConnection.ServerErrorCallBack;
import com.b4edriver.CommonClasses.ServerConnection.VolleyCallBack;
import com.b4edriver.CommonClasses.Utils.AppConstantDriver;
import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.b4edriver.CommonClasses.Utils.DialogManagerDriver;
import com.b4edriver.CommonClasses.Utils.Function;
import com.b4edriver.Model.DriverLoginTime;
import com.b4edriver.R;
import com.b4elibrary.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class LoginTimeActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView from_date, to_date;
    private FloatingActionButton fab;
    private List<DriverLoginTime> driverLoginTimes;
    private RecyclerView recyclerView;
    private DriverLoginTimeAdapter timeAdapter;
    private DialogManagerDriver dialogManagerDriver;
    private Calendar myCalendar;
    DatePickerDialog.OnDateSetListener fromdate = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub

            from_date.setText(String.format("%02d",year)+"-"+String.format("%02d",(monthOfYear +1))+"-"+String.format("%02d",dayOfMonth));
        }

    };
    DatePickerDialog.OnDateSetListener todate = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            to_date.setText(String.format("%02d",year)+"-"+String.format("%02d",(monthOfYear +1))+"-"+String.format("%02d",dayOfMonth));
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_time);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        from_date = (TextView) findViewById(R.id.from_date);
        to_date = (TextView) findViewById(R.id.to_date);

        from_date.setOnClickListener(this);
        to_date.setOnClickListener(this);

        myCalendar = Calendar.getInstance();

        dialogManagerDriver = new DialogManagerDriver();

        driverLoginTimes = new ArrayList<DriverLoginTime>();
        timeAdapter = new DriverLoginTimeAdapter(LoginTimeActivity.this, driverLoginTimes);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(LoginTimeActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(timeAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDriverLoginTime(from_date.getText().toString(), to_date.getText().toString());
            }
        });

        int mYear = myCalendar.get(Calendar.YEAR);
        int mMonth = myCalendar.get(Calendar.MONTH);
        int mDay = myCalendar.get(Calendar.DAY_OF_MONTH);

        from_date.setText(String.format("%02d",mYear)+"-"+String.format("%02d",(mMonth +1))+"-"+String.format("%02d",mDay));
        to_date.setText(String.format("%02d",mYear)+"-"+String.format("%02d",(mMonth +1))+"-"+String.format("%02d",mDay));

        getDriverLoginTime(from_date.getText().toString(), to_date.getText().toString());
    }

    private void getDriverLoginTime(String fromDate, String toDate) {
        if (Function.isConnectingToInternet(LoginTimeActivity.this)) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("method", AppConstantDriver.METHOD.DRIVERLOGSDETAILS);
                jsonObject.put("user_id", AppPreferencesDriver.getDriverId(LoginTimeActivity.this));
                jsonObject.put("from_date", fromDate);
                jsonObject.put("to_date", toDate);
                Logger.log("Request", jsonObject.toString());
                getDriverLoginTimeTask(jsonObject);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            dialogManagerDriver.showAlertDialog(LoginTimeActivity.this, "Bike4Everything", getResources().getString(R.string.error_check_internet_connection), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
        }
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.from_date) {
            new DatePickerDialog(LoginTimeActivity.this, fromdate, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        } else if (i == R.id.to_date) {
            new DatePickerDialog(LoginTimeActivity.this, todate, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.left_to_right,
                        R.anim.right_to_left);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.left_to_right,
                R.anim.right_to_left);
    }

    private void getDriverLoginTimeTask(JSONObject jsonObject) {
        dialogManagerDriver.showProcessDialog(LoginTimeActivity.this, "", false);
        JSONParser jsonParser = new JSONParser(LoginTimeActivity.this);
        jsonParser.parseVollyJSONObject(AppConstantDriver.URL.ONDEMAND_DRIVER_DRIVERLOGS, 1, jsonObject, "Please wait a few second...", new VolleyCallBack() {
            @Override
            public void success(String response) {
                if (response != null) {

                    JSONObject jsonObject = null;
                    try {

                        jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (status.equalsIgnoreCase("200")) {
                            Type listType = new TypeToken<ArrayList<DriverLoginTime>>() {
                            }.getType();
                            driverLoginTimes = new Gson().fromJson(jsonObject.getJSONArray("result").toString(), listType);
                            Logger.log("driverLoginTime", driverLoginTimes.toString());
                            timeAdapter = new DriverLoginTimeAdapter(LoginTimeActivity.this, driverLoginTimes);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(LoginTimeActivity.this);
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(timeAdapter);

                        } else {
                            dialogManagerDriver.showAlertDialog(LoginTimeActivity.this, "Alert", "login data is not available", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                        }

                        dialogManagerDriver.stopProcessDialog();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        }, new ServerErrorCallBack() {
            @Override
            public void error(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String msg = jsonObject.getString("vollymsg");
                    showDialog(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void showDialog(String msg) {

        new SweetAlertDialog(LoginTimeActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(msg)
                .setConfirmText("Ok")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();

                    }
                })
                .show();

    }

}
