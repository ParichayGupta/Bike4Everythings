package com.b4edriver.DriverApp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.b4edriver.CommonClasses.ServerConnection.Util;
import com.b4edriver.CommonClasses.ServerConnection.VolleyCallBack;
import com.b4edriver.Adapter.MyRideAdapterDriver;
import com.b4edriver.Model.TripDriver;
import com.b4edriver.CommonClasses.Utils.AppConstantDriver;
import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.b4edriver.CommonClasses.Utils.DialogManagerDriver;
import com.b4edriver.CommonClasses.ServerConnection.JSONParser;
import com.b4edriver.CommonClasses.ServerConnection.ServerErrorCallBack;
import com.b4edriver.R;
import com.b4elibrary.Logger;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class MyTripRideActivityDriver extends AppCompatActivity implements VolleyCallBack {

    ListView listView;
    MyRideAdapterDriver myRideAdapter;
    MyTripRideActivityDriver instance = null;
    List<TripDriver> modelList;
    DialogManagerDriver dialogManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ride_driver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow);
        instance = this;

        dialogManager = new DialogManagerDriver();

        modelList = new ArrayList<TripDriver>();
        listView = (ListView) findViewById(R.id.listView);

        myRideAdapter = new MyRideAdapterDriver(instance,modelList);
        listView.setAdapter(myRideAdapter);



        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("method", AppConstantDriver.METHOD.MYRIDE_DRIVER);
            jsonObject.put("driver_id", String.valueOf(AppPreferencesDriver.getDriverId(instance)));
            Logger.log("MYRIDE_USER","00000");
            //new GetMyRideTask(jsonObject).execute();
            Logger.log("MYRIDE_USER","111111");

            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObject);

            JSONParser jsonParser = new JSONParser(getApplicationContext());
            dialogManager.showProcessDialog(instance,"",false);
            jsonParser.parseVollyJSONObject(AppConstantDriver.URL.ONDEMAND_DRIVER_DRIVERRIDEDETAILS, 1, jsonObject, "", this, new ServerErrorCallBack() {
                @Override
                public void error(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg = jsonObject.getString("vollymsg");
                        errorDialog(instance,msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void errorDialog(Context context, String msg) {

        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(msg)
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        finish();
                    }
                })
                .show();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.left_to_right,
                        R.anim.right_to_left);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void success(String json) {
        dialogManager.stopProcessDialog();
        Logger.log("response", json);
        if(json.equalsIgnoreCase("error")){
        
        }else{
            try {
                JSONObject jsonObject = new JSONObject(json);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("200")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    for(int i=0; i<jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            /*{"message":"Success","status":"200","result":[{

                            "actual_km":"",
                            "payment_mode":"cash",
                            "actual_fare":"",

                            "tripStatus":"400"}]}*/
                        TripDriver trips = new TripDriver();
                        trips.setId(Long.parseLong(jsonObject1.getString("trip_id")));
                        trips.setSourceAddress(jsonObject1.getString("pickup_address"));
                        trips.setDestinationAddress(jsonObject1.getString("drop_address"));
                        trips.setSourceLatitude(new Double(jsonObject1.getString("pickup_lat")));
                        trips.setSourcelogitude(new Double(jsonObject1.getString("pickup_lng")));
                        trips.setDestinationLatitude(new Double(jsonObject1.getString("drop_lat").equalsIgnoreCase("")?"0.0":jsonObject1.getString("drop_lat")));
                        trips.setDestinationLogitude(new Double(jsonObject1.getString("drop_lng").equalsIgnoreCase("")?"0.0":jsonObject1.getString("drop_lng")));

                        trips.setDistance(jsonObject1.getString("est_km"));
                        trips.setMonth(jsonObject1.getString("status"));
                        trips.setPickCno(jsonObject1.getString("mobile"));
                        trips.setPickCname(jsonObject1.getString("userName"));
                        trips.setDate(jsonObject1.getString("added_on"));
                        trips.setFare(jsonObject1.getString("actual_fare"));
                        trips.setTripStatus(jsonObject1.getString("tripStatus"));
                        trips.setCustomerImage("");

                        modelList.add(trips);
                    }
                    myRideAdapter.notifyDataSetChanged();
                }else if(status.equalsIgnoreCase("900")){
                    Intent intent = new Intent(instance, DialogActivityDriver.class);
                    intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction("loginSessionExpire");
                    intent.putExtra("msg",jsonObject.getString("message"));
                    startActivity(intent);
                }else{
                    JSONObject object = jsonObject.getJSONObject("result");
                    errorDialog(instance,object.getString("msg"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }


}
