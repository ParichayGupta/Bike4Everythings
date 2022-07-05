package com.b4edriver.b4edrivers;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.b4edriver.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HostoryActivity extends AppCompatActivity {

    static View.OnClickListener myOnClickListener;
    private static CustomAdapter adapter;
    private static RecyclerView recyclerView;
    private static ArrayList<DriverDatum> data;
    
    TextView tripamountbike;
    
    TextView tripamountb4e;
    
    TextView googleDistance;
    
    TextView meterDistance;
    
    CardView header;
    
    TextView empty;
    private RecyclerView.LayoutManager layoutManager;
    private Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
            System.out.println(dt.format(myCalendar.getTime()));
            filter(dt.format(myCalendar.getTime()));
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b4edrivers_activity_hostory);
        initView();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myOnClickListener = new MyOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<DriverDatum>();

        adapter = new CustomAdapter(data);
        recyclerView.setAdapter(adapter);

        requestData();

        myCalendar = Calendar.getInstance();


    }

    private void initView() {


         tripamountbike=findViewById(R.id.tripamountbike);

         tripamountb4e=findViewById(R.id.tripamountb4e);

         googleDistance=findViewById(R.id.googleDistance);

         meterDistance=findViewById(R.id.meterDistance);

         header=findViewById(R.id.header);

         empty=findViewById(R.id.empty);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.b4edrivers_menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            new DatePickerDialog(HostoryActivity.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }else if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    void filter(String text) {
        ArrayList<DriverDatum> temp = new ArrayList();
        double amountfor_biker_str = 0;
        double amountfor_b4e_str = 0;

        double dis_meter = 0;
        double dis_google = 0;

        for (DriverDatum d : data) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getAddedOn().contains(text)) {
                temp.add(d);
                amountfor_biker_str = amountfor_biker_str + Double.parseDouble(Function.tripAnountForBike(d.getDEKmMeter()));
                amountfor_b4e_str = amountfor_b4e_str + Double.parseDouble(Function.tripAnountForB4E(d.getDEKmMeter()));
                dis_meter = dis_meter + Double.parseDouble(d.getDEKmMeter());
                dis_google = dis_google + Double.parseDouble(d.getDEKmGoogle());
            }
        }
        //update recyclerview
        adapter.updateList(temp);

        tripamountbike.setText(getString(R.string.trip_amount_for_bike, amountfor_biker_str + ""));
        tripamountb4e.setText(getString(R.string.trip_amount_for_b4e, amountfor_b4e_str + ""));

        meterDistance.setText(dis_meter + " KM");
        googleDistance.setText(dis_google + " KM");

        if (temp.isEmpty()) {
            header.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        } else {
            header.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        }
    }

    private void requestData() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", AppConstant.TRIP_HISTORY);
            jsonObject.put("driver_id", AppPreferencesDriver.getDriverId(HostoryActivity.this));

            RequestToServer.getInstance().send(HostoryActivity.this, jsonObject, AppConstant.DRIVER, new RequestToServer.CallBack() {
                @Override
                public void success(String json) {
                    if (TextUtils.isEmpty(json)) {
                        showAlert("Error", "Please contact to administrator");
                    } else {
                        try {
                            JSONObject object = new JSONObject(json);
                            if (object.getString("status").equalsIgnoreCase("200")) {
                                JSONArray jsonArray = object.getJSONArray("result");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                    DriverDatum driverDatum = new Gson().fromJson(jsonObject1.toString(), DriverDatum.class);
                                    data.add(driverDatum);
                                }

                                // adapter.updateList(data);
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
                                filter(dt.format(calendar.getTime()));

                            } else {
                                JSONArray jsonArray = object.getJSONArray("result");
                                showAlert("Error", jsonArray.getJSONObject(0).getString("msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String msg) {
        new AlertDialog.Builder(HostoryActivity.this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private static class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            int selectedItemPosition = recyclerView.getChildPosition(v);
            Intent intent = new Intent(context, DeatilsActivity.class);
            intent.putExtra("details", data.get(selectedItemPosition));
            context.startActivity(intent);
        }

    }


}
