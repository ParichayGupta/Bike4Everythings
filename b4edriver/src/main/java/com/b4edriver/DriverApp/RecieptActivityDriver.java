package com.b4edriver.DriverApp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.b4edriver.BuildConfig;
import com.b4edriver.CommonClasses.ServerConnection.JSONParser;
import com.b4edriver.CommonClasses.ServerConnection.ServerErrorCallBack;
import com.b4edriver.CommonClasses.ServerConnection.VolleyCallBack;
import com.b4edriver.CommonClasses.Services.Mail;
import com.b4edriver.CommonClasses.Utils.AppConstantDriver;
import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.b4edriver.CommonClasses.Utils.DialogManagerDriver;
import com.b4edriver.CommonClasses.Utils.Function;
import com.b4edriver.Database.DBAdapter_Driver;
import com.b4edriver.Database.Database;
import com.b4edriver.DistanceCalculate.GpsDistanceTimeUpdater;
import com.b4edriver.DistanceCalculate.Prefs;
import com.b4edriver.DistanceCalculate.SPLabels;
import com.b4edriver.R;
import com.b4elibrary.Logger;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



import static com.b4edriver.CommonClasses.Services.FusedLocationService.sendDataMeFromFused;
import static com.b4edriver.CommonClasses.Services.FusedLocationService.temTripDistance;
import static com.b4edriver.CommonClasses.Services.FusedLocationService.tripDistance;

public class RecieptActivityDriver extends AppCompatActivity implements View.OnClickListener {

    public static RecieptActivityDriver instance = null;
    public String trip_distance = "", Totalpayment = "", payment = "", trip_time = "", drop_Address = "",
            pickup_Address = "", trip_id = "", user_id = "";
    double discountFare = 0.0;
    Button btn_another_ride;
    String driverName = "", paycode = "0";
    TextView pick_address, drop_address, date_tv, distance, amount, discount, paidAmount, driverShare, bfeShare;
    String billId = "";
    RadioGroup radiogroup;
    RadioButton radioPaytm, radioCash;
    DialogManagerDriver dialogManager;
    DBAdapter_Driver dbAdapter_driver;
    View catureBill;
    TextView version;

    public static double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciept_driver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        instance = this;

        getSupportActionBar().hide();

        AppPreferencesDriver.setActivity(RecieptActivityDriver.this, getClass().getName());
        init();
        listener();

       /* sendDataMeFromFused.add("mobiledata : "+ android.os.Build.VERSION.SDK_INT);
        sendDataMeFromFused.add("tripDistance : "+ tripDistance/1000 );
        sendDataMeFromFused.add("temTripDistance : "+ temTripDistance/1000 );
        double disss1 = Database.getInstance(RecieptActivityDriver.this).getTotalDistance();
        double dista1 = disss1/1000;
        sendDataMeFromFused.add("temTripDistance Other : "+ dista1 +" km");
        sendDataMeFromFused.add("getWaitTimeFromDB : "+ Database.getInstance(RecieptActivityDriver.this).getWaitTimeFromDB());
        sendDataMeFromFused.add("getCurrentPathItemsSaved : "+ Database.getInstance(RecieptActivityDriver.this).getCurrentPathItemsSaved().toString());

        Logger.log("sendDataMeFromFused", sendDataMeFromFused.toString());*/

        driverName = getIntent().getStringExtra("driver_name");

        dialogManager = new DialogManagerDriver();

        dbAdapter_driver = new DBAdapter_Driver(instance);

//        dbAdapter_driver.deleteDistance();

        trip_distance = getIntent().getStringExtra("trip_distance");
        payment = getIntent().getStringExtra("payment");
        trip_time = getIntent().getStringExtra("datetime");
        drop_Address = getIntent().getStringExtra("drop_address");
        pickup_Address = getIntent().getStringExtra("pickup_address");
        trip_id = getIntent().getStringExtra("trip_id");

        if (getIntent().getStringExtra("paycode") != null) {
            String paycode = getIntent().getStringExtra("paycode");
            String respp = getIntent().getStringExtra("respp");
            if (paycode.equalsIgnoreCase("1")) {
                successDialog(RecieptActivityDriver.this, respp, SweetAlertDialog.SUCCESS_TYPE);
            } else if (paycode.equalsIgnoreCase("2")) {
                successDialog(RecieptActivityDriver.this, respp, SweetAlertDialog.ERROR_TYPE);
            }
        }


        String totalDistance1 = getIntent().getStringExtra("totalDistance1");

        // date_tv.setText(totalDistance1+"");

        Logger.log("trip_id", trip_id);


        //btn_another_ride.setText("Home");


        pick_address.setText(pickup_Address.trim());
        drop_address.setText(drop_Address.trim());

        distance.setText(roundTwoDecimals(Double.parseDouble(trip_distance)) + " km");
        amount.setText("Total : Rs " + payment);

        String discountAmount = AppPreferencesDriver.getPromocode(RecieptActivityDriver.this);
        String referalDiscount = AppPreferencesDriver.getRefferalcode(RecieptActivityDriver.this);

        double discountA = 0.0;

        if (!discountAmount.equalsIgnoreCase("0")) {
            discountA = new Double(payment) * Integer.parseInt(discountAmount) / 100;
            discount.setText("Discount : " + discountAmount + "%");
        } else {
            discountA = new Double(payment) * Integer.parseInt(referalDiscount) / 100;
            discount.setText("Discount : " + referalDiscount + "%");
        }


        // double discountA = new Double(payment) * Integer.parseInt(discountAmount) / 100;

        double discountB = new Double(payment) - discountA;

        DecimalFormat twoDForm = new DecimalFormat("#.##");
        discountFare = Double.valueOf(twoDForm.format(discountB));

        paidAmount.setText("Paid Amount : Rs " + discountFare);

        Totalpayment = String.valueOf(discountFare);


        /*20% -> 1000 * 20 / 100 = 200 -- Driver

        80% -> 1000-200 = 800  -- B4E


        TripDriver Amount - 1000
        Driver share - 200
        B4E share - 800*/
        double driveramount1 = new Double(Totalpayment) * 20 / 100;

        double driveramount = (double) Math.round(driveramount1 * 100) / 100;

        double b4eAmount1 = new Double(Totalpayment) - driveramount;

        double b4eAmount = (double) Math.round(b4eAmount1 * 100) / 100;

        driverShare.setText("Driver share - Rs " + b4eAmount);
        bfeShare.setText("B4E share - Rs " + driveramount);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = formatter.parse(trip_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
            String setDate = sdf.format(date.getTime());
            date_tv.setText(setDate);
        } catch (Exception e) {
        }



        billGenerate();

        sendDataMeFromFused.add("mobiledata : "+ android.os.Build.VERSION.SDK_INT);
        sendDataMeFromFused.add("tripDistance : "+ tripDistance/1000 );
        sendDataMeFromFused.add("temTripDistance : "+ temTripDistance/1000 );
        double disss = Database.getInstance(RecieptActivityDriver.this).getTotalDistance();
        double dista = disss/1000;
        sendDataMeFromFused.add("temTripDistance Other : "+ roundTwoDecimals(dista) +" km");
        sendDataMeFromFused.add("getWaitTimeFromDB : "+ Database.getInstance(RecieptActivityDriver.this).getWaitTimeFromDB());
        sendDataMeFromFused.add("getCurrentPathItemsSaved : "+ Database.getInstance(RecieptActivityDriver.this).getCurrentPathItemsSaved().toString());


        sendDataMeFromFused.add("isConnectionFast : "+ Function.isConnectionFast(RecieptActivityDriver.this) );
        new SendMail().execute();



        version.setText("V "+BuildConfig.VERSION_NAME +" #"+AppPreferencesDriver.getTripId(RecieptActivityDriver.this)
        +" "+ roundTwoDecimals(dista) +" km");


    }


    private void init() {
        btn_another_ride = (Button) findViewById(R.id.btn_another_ride);
        radiogroup = (RadioGroup) findViewById(R.id.radiogroup);
        radioCash = (RadioButton) findViewById(R.id.radioCash);
        radioPaytm = (RadioButton) findViewById(R.id.radioPaytm);
        pick_address = (TextView) findViewById(R.id.pick_address);
        drop_address = (TextView) findViewById(R.id.drop_address);
        date_tv = (TextView) findViewById(R.id.date);
        distance = (TextView) findViewById(R.id.distance);
        amount = (TextView) findViewById(R.id.amount);
        discount = (TextView) findViewById(R.id.discount);
        paidAmount = (TextView) findViewById(R.id.paidAmount);
        driverShare = (TextView) findViewById(R.id.driverShare);
        bfeShare = (TextView) findViewById(R.id.bfeShare);
        version = (TextView) findViewById(R.id.version);
        catureBill = (View) findViewById(R.id.ll_1);
    }

    private void listener() {
        btn_another_ride.setOnClickListener(this);

    }


    private class SendMail extends AsyncTask<String, Integer, Void> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }

        protected Void doInBackground(String... params) {
            Mail m = new Mail("hvantageproject1@gmail.com", "hvantage@123");

            String[] toArr = {"hvantageproject1@gmail.com"};
            m.setTo(toArr);
            m.setFrom("hvantageproject1@gmail.com");
            m.setSubject("Ver "+BuildConfig.VERSION_NAME
                    +" TripID : " + AppPreferencesDriver.getTripId(RecieptActivityDriver.this)
                    +" DriverID : " + AppPreferencesDriver.getDriverId(RecieptActivityDriver.this));
           // m.setBody(getIntent().getStringExtra("sendDataMe"));
            m.setBody(getIntent().getStringExtra("sendDataMe")+" ==== "+sendDataMeFromFused.toString());

            try {
                if (m.send()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           // temTripDistance = 0.0;
                            sendDataMeFromFused.clear();
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new SendMail().execute();
                        }
                    });

                }
            } catch (Exception e) {
                Log.e("MailApp", "Could not send email", e);
            }

            return null;
        }

    }

    @Override
    public void onClick(View v) {


        int i = v.getId();
        if (i == R.id.btn_another_ride) {/*Intent intent11 = new Intent(this, NavigationDrawerDriver.class);
                    intent11.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent11.setAction("");
                    startActivity(intent11);
                    finish();*/


            int selectedId = radiogroup.getCheckedRadioButtonId();
            /*RadioButton radioButton = (RadioButton) findViewById(selectedId);*/

            String paymentMode = null;
            if(radioCash.isChecked()){
                paymentMode = "Cash";
            }else if(radioPaytm.isChecked()){
                paymentMode = "Paytm";
            }

            transactionSuccess(paymentMode);


        }
    }


    private void transactionSuccess(String paymentMode) {
        try {

            JSONObject jsonObject1 = new JSONObject();

            jsonObject1.put("method", AppConstantDriver.METHOD.USERBILLPAYMENTPROCESS_FROM_DRIVER_PROCESS);
            jsonObject1.put("tripId", trip_id);
            jsonObject1.put("userId", user_id);
            jsonObject1.put("driverId", AppPreferencesDriver.getDriverId(instance));
            jsonObject1.put("TxStatus", "");
            jsonObject1.put("TxId", "");
            jsonObject1.put("TxRefNo", "");
            jsonObject1.put("pgTxnNo", "");
            jsonObject1.put("TxMsg", "Cash Payment Successful");
            jsonObject1.put("amount", Totalpayment);
            jsonObject1.put("discount_fare", discountFare);
            jsonObject1.put("discount", AppPreferencesDriver.getPromocode(RecieptActivityDriver.this));
            jsonObject1.put("promo_id", AppPreferencesDriver.getPromoid(RecieptActivityDriver.this));
            jsonObject1.put("transactionId", "");
            jsonObject1.put("paymentMode", paymentMode);
            jsonObject1.put("TxGateway", "");
            jsonObject1.put("maskedCardNumber", "");
            jsonObject1.put("cardType", "");
            jsonObject1.put("email", "");
            jsonObject1.put("mobileNo", "");
            jsonObject1.put("txnDateTime", "");
            jsonObject1.put("bill_by", "D");


            if (Function.isConnectingToInternet(instance)) {
                JSONParser jsonParser = new JSONParser(instance);
                jsonParser.parseVollyJSONObject(AppConstantDriver.URL.ONDEMAND_DRIVER_DRIVERPAYMENT, 1, jsonObject1, "", new VolleyCallBack() {
                    @Override
                    public void success(String response) {

                        AppPreferencesDriver.setTripId(RecieptActivityDriver.this, "");
                        AppPreferencesDriver.setTripstatusForDriver(RecieptActivityDriver.this, AppConstantDriver.END_TRIP);
                        deleteDataBase();
                        Intent intent1 = new Intent(instance, NavigationDrawerDriver.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent1.setAction("");
                        startActivity(intent1);
                        overridePendingTransition(R.anim.left_to_right,
                                R.anim.right_to_left);
                        finish();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                                deleteDataBase();
                                Intent intent111 = new Intent(instance, NavigationDrawerDriver.class);
                                intent111.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent111.setAction("");
                                startActivity(intent111);
                                overridePendingTransition(R.anim.left_to_right,
                                        R.anim.right_to_left);
                                finish();
                            } else {
                                deleteDataBase();
                                Intent intent11 = new Intent(instance, NavigationDrawerDriver.class);
                                intent11.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent11.setAction("");
                                startActivity(intent11);
                                overridePendingTransition(R.anim.left_to_right,
                                        R.anim.right_to_left);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            errorDialog(instance, e.getMessage());
                        }


                    }
                }, new ServerErrorCallBack() {
                    @Override
                    public void error(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("vollymsg");
                            errorDialog(instance, msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } else {
                new AlertDialog.Builder(RecieptActivityDriver.this).setCancelable(true).setMessage("Please check internet connection!")
                        .setTitle("Connection error!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
                //type-cash / citrus,price,trip id, driver id,user id
/*

                String sms = "bike4everything billPay-" + jsonObject1.getString("paymentMode") + "," +
                        jsonObject1.getString("amount") + "," + jsonObject1.getString("tripId") + "," +
                        jsonObject1.getString("driverId");

                Logger.log("SMSSMS", sms);
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("09229224424", null, sms, null, null);

                AppPreferencesDriver.setTripId(RecieptActivityDriver.this, "");
                AppPreferencesDriver.setTripstatusForDriver(RecieptActivityDriver.this, AppConstantDriver.END_TRIP);

                deleteDataBase();
                Intent intent11 = new Intent(instance, NavigationDrawerDriver.class);
                intent11.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent11.setAction("");
                startActivity(intent11);
                overridePendingTransition(R.anim.left_to_right,
                        R.anim.right_to_left);
                finish();
*/

                //  Logger.log("SMSMSMS", sms);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void billGenerate() {
        try {

            JSONObject jsonObject1 = new JSONObject();

            jsonObject1.put("method", AppConstantDriver.METHOD.USERBILLPAYMENTPROCESS_FROM_DRIVER);
            jsonObject1.put("tripId", trip_id);
            //jsonObject1.put("userId", AppPreferencesDriver.getUserId(instance));
            jsonObject1.put("driverId", AppPreferencesDriver.getDriverId(instance));
            jsonObject1.put("TxStatus", "");
            jsonObject1.put("TxId", "");
            jsonObject1.put("TxRefNo", "");
            jsonObject1.put("pgTxnNo", "");
            jsonObject1.put("TxMsg", "");
            jsonObject1.put("amount", Totalpayment);
            jsonObject1.put("discount_fare", discountFare);
            jsonObject1.put("discount", AppPreferencesDriver.getPromocode(RecieptActivityDriver.this));
            jsonObject1.put("promo_id", AppPreferencesDriver.getPromoid(RecieptActivityDriver.this));
            jsonObject1.put("transactionId", "");
            jsonObject1.put("paymentMode", "");
            jsonObject1.put("TxGateway", "");
            jsonObject1.put("maskedCardNumber", "");
            jsonObject1.put("cardType", "");
            jsonObject1.put("email", "");
            jsonObject1.put("mobileNo", "");
            jsonObject1.put("txnDateTime", "");
            jsonObject1.put("bill_by", "D");


            JSONParser jsonParser = new JSONParser(instance);
            jsonParser.parseVollyJSONObject(AppConstantDriver.URL.ONDEMAND_DRIVER_DRIVERPAYMENT, 1, jsonObject1, "Bill Generateing...", new VolleyCallBack() {
                @Override
                public void success(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray1 = jsonObject.getJSONArray("result");
                            JSONObject jsonObject2 = jsonArray1.getJSONObject(0);
                            billId = jsonObject2.getString("bill_id");
                            user_id = jsonObject2.getString("user_id");
                            trip_id = jsonObject2.getString("trip_id");
                            // sendMailInvoice(instance);
                        } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {

                        } else {
                            billGenerate();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new ServerErrorCallBack() {
                @Override
                public void error(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg = jsonObject.getString("vollymsg");
                        errorDialog(instance, msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void successDialog(Context context, String msg, final int type) {

        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Payment")
                .setContentText(msg)
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        if (type == SweetAlertDialog.SUCCESS_TYPE) {
                            deleteDataBase();
                            Intent intent11 = new Intent(instance, NavigationDrawerDriver.class);
                            intent11.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent11.setAction("");
                            startActivity(intent11);
                            overridePendingTransition(R.anim.left_to_right,
                                    R.anim.right_to_left);
                            finish();
                        }

                    }
                })
                .show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppPreferencesDriver.setTripstatusForDriver(instance, "0");
    }

    @Override
    public void onBackPressed() {
      /*  super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right,
                R.anim.right_to_left);*/
    }

    public void errorDialog(Context context, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(RecieptActivityDriver.this);
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
    private static GpsDistanceTimeUpdater getGpsDistanceTimeUpdater(final Context context) {

        GpsDistanceTimeUpdater  gpsDistanceTimeUpdater = new GpsDistanceTimeUpdater() {
            public void updateDistanceTime(double distance, long elapsedTime, long waitTime, Location lastGPSLocation, Location lastFusedLocation, double totalHaversineDistance, boolean fromGPS) {

            }

            public void drawOldPath() {

            }

            public void addPathToMap(PolylineOptions polylineOptions) {

            }
        };

        return gpsDistanceTimeUpdater;
    }


    public void deleteDataBase(){
        Prefs.with(RecieptActivityDriver.this).save(SPLabels.TOTAL_DISTANCE, "0.0");
        Database.getInstance(RecieptActivityDriver.this).updateTotalDistance(0.0);
        Database.getInstance(RecieptActivityDriver.this).deleteWaitTimeData();
        Database.getInstance(RecieptActivityDriver.this).deleteDriverCurrentLocation();
        Database.getInstance(RecieptActivityDriver.this).deleteAllCurrentPathItems();
        Database.getInstance(RecieptActivityDriver.this).deleteDriverLocation();

    }

}
