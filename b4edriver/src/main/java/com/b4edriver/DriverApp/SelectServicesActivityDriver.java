package com.b4edriver.DriverApp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.b4edriver.CommonClasses.ServerConnection.VolleyCallBack;
import com.b4edriver.Adapter.SelectBusnTypeAdapterDriver;
import com.b4edriver.Model.BusinessTypeDriver;
import com.b4edriver.CommonClasses.Utils.AppConstantDriver;
import com.b4edriver.CommonClasses.ServerConnection.JSONParser;
import com.b4edriver.CommonClasses.ServerConnection.ServerErrorCallBack;
import com.b4edriver.R;
import com.b4elibrary.Logger;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SelectServicesActivityDriver extends AppCompatActivity {

    SelectBusnTypeAdapterDriver selectBusnTypeAdapter;
    RecyclerView recyclerView;
    public static ArrayList<BusinessTypeDriver> businessTypeList;

    String userId, driverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_services_driver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        businessTypeList = new ArrayList<BusinessTypeDriver>();

        userId = getIntent().getStringExtra("userId");
        driverId = getIntent().getStringExtra("driverId");




        selectBusnTypeAdapter = new SelectBusnTypeAdapterDriver(getApplicationContext(),businessTypeList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(5));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(selectBusnTypeAdapter);


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", AppConstantDriver.METHOD.SERVICESLIST);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        selectBusinessTypeTask(jsonObject);


    }

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int mVerticalSpaceHeight;

        public VerticalSpaceItemDecoration(int mVerticalSpaceHeight) {
            this.mVerticalSpaceHeight = mVerticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = mVerticalSpaceHeight;
        }
    }

    public void selectBusinessTypeTask(JSONObject jsonObject){
        JSONParser jsonParser = new JSONParser(SelectServicesActivityDriver.this);
        jsonParser.parseVollyJSONObject(AppConstantDriver.URL.ONDEMAND_DRIVER_DRIVERREGISTRATION, 1, jsonObject, "", new VolleyCallBack() {
            @Override
            public void success(String response) {
                if(response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {

                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                BusinessTypeDriver businessType = new BusinessTypeDriver();
                                businessType.setId(object.getString("id"));
                                businessType.setName(object.getString("name"));
                                businessType.setKey(object.getString("key"));
                                businessType.setSelected(true);
                                if(object.getString("id").equalsIgnoreCase("1")){
                                    businessType.setImage(R.drawable.food);
                                }else if(object.getString("id").equalsIgnoreCase("2")){
                                    businessType.setImage(R.drawable.laundry);
                                }else if(object.getString("id").equalsIgnoreCase("3")){
                                    businessType.setImage(R.drawable.logistic);
                                }else if(object.getString("id").equalsIgnoreCase("4")){
                                    businessType.setImage(R.drawable.taxi);
                                }else if(object.getString("id").equalsIgnoreCase("5")){
                                    businessType.setImage(R.drawable.health_beauty);
                                }else if(object.getString("id").equalsIgnoreCase("6")){
                                    businessType.setImage(R.drawable.travel);
                                }else if(object.getString("id").equalsIgnoreCase("7")){
                                    businessType.setImage(R.drawable.homeservice);
                                }else if(object.getString("id").equalsIgnoreCase("8")){
                                    businessType.setImage(R.drawable.electritian);
                                }else if(object.getString("id").equalsIgnoreCase("9")){
                                    businessType.setImage(R.drawable.carpainter);
                                }else if(object.getString("id").equalsIgnoreCase("10")){
                                    businessType.setImage(R.drawable.shirt);
                                }else if(object.getString("id").equalsIgnoreCase("11")){
                                    businessType.setImage(R.drawable.grocery);
                                }
                                businessTypeList.add(businessType);
                                selectBusnTypeAdapter.notifyDataSetChanged();
                            }

                        } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                            errorDialog(SelectServicesActivityDriver.this,jsonObject1.getString("msg"));

                        }
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
                    refreshDialog(SelectServicesActivityDriver.this,msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.servicetype_driver, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ArrayList<String> serviceTypeList = new ArrayList<String>();
        int i1 = item.getItemId();
        if (i1 == R.id.select) {
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < businessTypeList.size(); i++) {
                Logger.log("serviceList", businessTypeList.get(i).isSelected() + "");
                if (businessTypeList.get(i).isSelected()) {
                    stringBuilder.append(businessTypeList.get(i).getName() + ",");
                    serviceTypeList.add(businessTypeList.get(i).getId());
                }
            }

            Logger.log("serviceList", stringBuilder + "\n" + serviceTypeList.toString());

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("method", AppConstantDriver.METHOD.SERVICE_DETAILS);
                jsonObject.put("serviceslist", serviceTypeList.toString());
                jsonObject.put("userId", userId);
                jsonObject.put("driverId", driverId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

           ServicesListTask(jsonObject);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void ServicesListTask(JSONObject jsonObject){
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(jsonObject);

        JSONParser jsonParser = new JSONParser(SelectServicesActivityDriver.this);
        jsonParser.parseVollyJSONObject(AppConstantDriver.URL.ONDEMAND_DRIVER_DRIVERREGISTRATION, 1, jsonObject, "", new VolleyCallBack() {
            @Override
            public void success(String response) {
                if (response != null) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            Intent intent = new Intent(SelectServicesActivityDriver.this, ThankyouActivityDriver.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            overridePendingTransition(R.anim.left_to_right,
                                    R.anim.right_to_left);
                            finish();
                        } else {
                            errorDialog(SelectServicesActivityDriver.this,jsonObject.getString("status") + "::" + " Try Again ");

                        }
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
                    errorDialog(SelectServicesActivityDriver.this,msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void refreshDialog(Context context, String msg) {

        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(msg)
                .setConfirmText("Refresh")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("method", AppConstantDriver.METHOD.SERVICESLIST);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        selectBusinessTypeTask(jsonObject);
                    }
                })
                .show();

    }
    public void errorDialog(Context context,String msg) {

        AlertDialog.Builder  builder = new AlertDialog.Builder(SelectServicesActivityDriver.this);
        builder.setTitle("Oops...");
        builder.setMessage(msg);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.left_to_right,
                R.anim.right_to_left);
    }
}
