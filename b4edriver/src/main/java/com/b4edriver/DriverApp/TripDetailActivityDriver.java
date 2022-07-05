package com.b4edriver.DriverApp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.b4edriver.CommonClasses.Classes.FusedLocationServiceDriver;
import com.b4edriver.CommonClasses.Services.WorkTimerService;
import com.b4edriver.CommonClasses.Utils.Function;
import com.b4edriver.Database.DBAdapter_Driver;
import com.b4edriver.GCM.ConfigDriver;
import com.b4edriver.Model.TripDriver;
import com.b4edriver.CommonClasses.Utils.AppConstantDriver;
import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.b4edriver.CommonClasses.Utils.DialogManagerDriver;
import com.b4edriver.CommonClasses.ServerConnection.JSONParser;
import com.b4edriver.CommonClasses.ServerConnection.ServerErrorCallBack;
import com.b4edriver.CommonClasses.ServerConnection.VolleyCallBack;
import com.b4edriver.R;
import com.b4edriver.CommonClasses.Services.AlarmServicesDriver;
import com.b4edriver.b4edrivers.MainActivity;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class TripDetailActivityDriver extends AppCompatActivity {
    public static TripDetailActivityDriver instance = null;
    public TripDriver trip;
    //aa
    Button btn_accept, btn_discard;//, btn_snooze;
    MediaPlayer mp = null;
    DialogManagerDriver dialogManager;
    FusedLocationServiceDriver gps;
    CardView container1;
    Vibrator vibrator;
    LinearLayout container2;
    View.OnClickListener accepct = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            try {
                Intent snoozeintent = new Intent(instance, TripDetailActivityDriver.class);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                PendingIntent pendingIntent = PendingIntent.getActivity(instance, Integer.parseInt(String.valueOf(trip.getId())), snoozeintent, PendingIntent.FLAG_CANCEL_CURRENT);

                alarmManager.cancel(pendingIntent);
            }catch (NullPointerException e){}
            {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("method", AppConstantDriver.METHOD.ACCEPT_THE_TRIP);
                    jsonObject.put("driver_id", AppPreferencesDriver.getDriverId(TripDetailActivityDriver.this));
                    jsonObject.put("trip_id", trip.getId());
                    jsonObject.put("driver_latitude", gps.getLocation().getLatitude());
                    jsonObject.put("driver_longitude", gps.getLocation().getLongitude());
                    jsonObject.put("status", "accept");
                    jsonObject.put("type", AppPreferencesDriver.getDrivertype(TripDetailActivityDriver.this));
                    // tempTrip = trip;
                    acceptTripTask(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                AppPreferencesDriver.setDriveraccepted(instance, "yes");
            }

        }
    };
    View.OnClickListener discard = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent snoozeintent = new Intent(instance, TripDetailActivityDriver.class);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getActivity(instance, Integer.parseInt(String.valueOf(trip.getId())), snoozeintent, PendingIntent.FLAG_CANCEL_CURRENT);

            alarmManager.cancel(pendingIntent);
            // if(trip.getTravelTime().equalsIgnoreCase("")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RejectDialog("Please select the reasion why the trip was rejected");
                }
            });

            // }else{
            //   RejectDialogForAdmin("Please select the reasion why the trip was rejected");
            // }

        }
    };

    private BroadcastReceiver tripReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            CancelTripDialog(intent.getStringExtra("msg"));
        }
    };
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String totalDuration = Function.getTotalDateDiffrence(AppPreferencesDriver.getTotalDriverDuration(context),AppPreferencesDriver.getCurrentDriverDuration(context));
                String[] totaltime = totalDuration.split(" ");
                getSupportActionBar().setSubtitle(String.valueOf(totaltime[1]));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details_driver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        instance = TripDetailActivityDriver.this;



        LocalBroadcastManager.getInstance(this).registerReceiver(tripReceiver,
                new IntentFilter("cancelTripAuto"));

        registerReceiver(broadcastReceiver,new IntentFilter(WorkTimerService.str_receiver));


        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
        nMgr.cancel(ConfigDriver.NEW_TRIP_ID);

        dialogManager = new DialogManagerDriver();

        mp = MediaPlayer.create(getBaseContext(), R.raw.alarm);
        playSound(this);
        startVibrate();

        container1 = (CardView) findViewById(R.id.container1);
        container2 = (LinearLayout) findViewById(R.id.container2);

        TextView trip_id = (TextView) findViewById(R.id.trip_id);
        TextView date_tv = (TextView) findViewById(R.id.date_tv);
        TextView source = (TextView) findViewById(R.id.source);
        TextView destination = (TextView) findViewById(R.id.destination);
        TextView fare_tv = (TextView) findViewById(R.id.fare_tv);
        TextView pickCname = (TextView) findViewById(R.id.pickCname);
        TextView pickCno = (TextView) findViewById(R.id.pickCno);
        TextView pick_address = (TextView) findViewById(R.id.pick_address);

        TextView dropCname = (TextView) findViewById(R.id.dropCname);
        TextView dropCno = (TextView) findViewById(R.id.dropCno);
        TextView drop_address = (TextView) findViewById(R.id.drop_address);

        btn_accept = (Button) findViewById(R.id.btn_accept);
        btn_discard = (Button) findViewById(R.id.btn_discard);

        btn_accept.setOnClickListener(accepct);
        btn_discard.setOnClickListener(discard);

        gps = new FusedLocationServiceDriver(this);

        trip = getIntent().getParcelableExtra("tripDetails");
        senRingingStatus();
        getSupportActionBar().setTitle(trip.getMonth());

        if(getIntent().getAction().equalsIgnoreCase("taxiService")) {
            container1.setVisibility(View.VISIBLE);
            container2.setVisibility(View.GONE);

        }else{
            container1.setVisibility(View.GONE);
            //container1.setVisibility(View.VISIBLE);
            container2.setVisibility(View.VISIBLE);

            pickCname.setText(trip.getPickCname());
            pickCno.setText(trip.getPickCno());
            pick_address.setText(trip.getSourceAddress());
            dropCname.setText(trip.getDropCname());
            dropCno.setText(trip.getDropCno());
            drop_address.setText(trip.getDestinationAddress()
            );

        }

        AppPreferencesDriver.setPendingTripId(instance,String.valueOf(trip.getId()));
        trip_id.setText(String.valueOf(trip.getId()));
        source.setText(trip.getSourceAddress());
        destination.setText(trip.getDestinationAddress());
        //fare_tv.setText(getString(R.string.currency) + trip.getFare() +" "+ getString(R.string.per_km));


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = formatter.parse(trip.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
            String setDate = sdf.format(date.getTime());
            date_tv.setText(setDate);
        } catch (Exception e) {

        }

        try {
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(ConfigDriver.NEW_TRIP_ID);
        } catch (NullPointerException e) {
            Toast.makeText(instance, "notify not clear", Toast.LENGTH_LONG).show();
        }




    }

    private void senRingingStatus() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", AppConstantDriver.METHOD.drivergotNotification);
            jsonObject.put("tripId", trip.getId());
            jsonObject.put("driverId", AppPreferencesDriver.getDriverId(TripDetailActivityDriver.this));
            jsonObject.put("ringingStatus", 1);

            JSONParser jsonParser = new JSONParser(instance);
            jsonParser.parseVollyObject(AppConstantDriver.URL.onDeand_Driver_drivertificationManage, 1, jsonObject, new VolleyCallBack() {
                @Override
                public void success(String response) {

                    try {
                        JSONObject jsonObject1 = new JSONObject(response);
                        if(jsonObject1.getString("status").equalsIgnoreCase("200")){

                        }else if(jsonObject1.getString("status").equalsIgnoreCase("900")){
                            Intent intent = new Intent(instance, DialogActivityDriver.class);
                            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction("loginSessionExpire");
                            intent.putExtra("msg",jsonObject1.getString("message"));
                            startActivity(intent);
                        }else{
                            //senRingingStatus();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new ServerErrorCallBack() {
                @Override
                public void error(String response) {
                    //senRingingStatus();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    private void playSound(final Context context) {


        Thread background = new Thread(new Runnable() {
            public void run() {
                try {
                    mp.setLooping(true);
                    mp.start();

                } catch (Throwable t) {
                    Log.i("Animation", "Thread  exception " + t);
                }
            }
        });
        background.start();
    }


    private void RejectDialog(String message) {

        try {

            final AlertDialog.Builder builder = new AlertDialog.Builder(instance);
            builder.setCancelable(false);

            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.all_popup_driver, (ViewGroup) findViewById(R.id.layout_root));

            builder.setView(dialogView);

            ImageView header = (ImageView) dialogView.findViewById(R.id.header_tv);
            TextView et_title = (TextView) dialogView.findViewById(R.id.et_title);
            final EditText edittext = (EditText) dialogView.findViewById(R.id.edittext);
            Button btn_submit = (Button) dialogView.findViewById(R.id.btn_submit);
            final RadioGroup radiogroup = (RadioGroup) dialogView.findViewById(R.id.radiogroup);
            final RadioButton[] radio = new RadioButton[1]; // = (RadioButton) dialogView.findViewById(R.id.radio1);
            RadioButton radio1 = (RadioButton) dialogView.findViewById(R.id.radio1);
            RadioButton radio2 = (RadioButton) dialogView.findViewById(R.id.radio2);
            RadioButton radio3 = (RadioButton) dialogView.findViewById(R.id.radio3);
            RadioButton radio4 = (RadioButton) dialogView.findViewById(R.id.radio4);
            radio4.setVisibility(View.GONE);

            radio1.setText("I am occupied this moment");
            radio2.setText("My work day is ending");
            radio3.setText("Other");


            radiogroup.setVisibility(View.VISIBLE);
            edittext.setVisibility(View.GONE);

            et_title.setGravity(Gravity.CENTER);
            et_title.setTypeface(Typeface.SERIF, Typeface.BOLD);
            et_title.setTextSize(20);

            et_title.setText(message);


            btn_submit.setText("SUBMIT");


            final AlertDialog alertDialog;
            alertDialog = builder.create();


            header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.cancel();
                }
            });

            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.cancel();
                    int selectedId = radiogroup.getCheckedRadioButtonId();

                    // find the radiobutton by returned id
                    radio[0] = (RadioButton) dialogView.findViewById(selectedId);

                    String value = radio[0].getText().toString();



                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("method", AppConstantDriver.METHOD.TRIP_REJECT);
                        jsonObject.put("driver_id", AppPreferencesDriver.getDriverId(instance));
                        jsonObject.put("trip_id", trip.getId());
                        jsonObject.put("message", value);
                        jsonObject.put("type", AppPreferencesDriver.getDrivertype(instance));

                        RejectTrip(jsonObject);

                    } catch (JSONException e) {
                        dialogManager.stopProcessDialog();
                    }

                    AppPreferencesDriver.setPendingTripId(TripDetailActivityDriver.this,"");
                    AppPreferencesDriver.setPendingTrip(TripDetailActivityDriver.this, null);
                    if(!AlarmServicesDriver.pendingTrip.isEmpty()) {
                        AlarmServicesDriver.pendingTrip.remove(AppPreferencesDriver.getPendingTrip(TripDetailActivityDriver.this));
                    }


                    Intent intent = new Intent(instance, TripDetailActivityDriver.class);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    PendingIntent pendingIntent = PendingIntent.getActivity(instance, Integer.parseInt(String.valueOf(trip.getId())), intent, PendingIntent.FLAG_CANCEL_CURRENT);

                    alarmManager.cancel(pendingIntent);
                    DBAdapter_Driver db = new DBAdapter_Driver(instance);

                    db.deleteSnoozeTrip(Integer.parseInt(String.valueOf(trip.getId())));

                    Intent intent11 = new Intent(instance, NavigationDrawerDriver.class);
                    intent11.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent11.setAction("");
                    startActivity(intent11);
                    overridePendingTransition(R.anim.left_to_right,
                            R.anim.right_to_left);
                    finish();

                    if(dialogManager!=null){
                        dialogManager.stopProcessDialog();
                    }

                }
            });



            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(alertDialog != null)
                        alertDialog.show();
                }
            });




        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.stop();
        stopVibrate();
        instance = null;
        unregisterReceiver(broadcastReceiver);

    }

    @Override
    public void onBackPressed() {

    }

    private void CancelTripDialog(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(instance);
        builder.setCancelable(false);


        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.all_popup_driver, (ViewGroup) findViewById(R.id.layout_root));

        builder.setView(dialogView);


        ImageView header = (ImageView) dialogView.findViewById(R.id.header_tv);
        TextView et_title = (TextView) dialogView.findViewById(R.id.et_title);
        final EditText edittext = (EditText) dialogView.findViewById(R.id.edittext);
        Button btn_submit = (Button) dialogView.findViewById(R.id.btn_submit);

        edittext.setVisibility(View.GONE);

        et_title.setGravity(Gravity.CENTER);
        et_title.setTypeface(Typeface.SERIF, Typeface.BOLD);
        et_title.setTextSize(20);

        et_title.setText(msg);

        btn_submit.setText(getString(R.string.ok));

        final AlertDialog alertDialogReciveMessage = builder.create();

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogReciveMessage.cancel();
                finish();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialogReciveMessage.cancel();
                finish();

            }
        });


        try {
            alertDialogReciveMessage.show();
        } catch (Exception e) {
        }

    }

    public void acceptTripTask(JSONObject jsonObject){
        JSONParser jsonParser = new JSONParser(instance);
        jsonParser.parseVollyJSONObject(AppConstantDriver.URL.ONDEMAND_DRIVER_DRIVERBOOKINGPROCESS, 1, jsonObject, "", new VolleyCallBack() {
            @Override
            public void success(String response) {

                try {
                    if(!AlarmServicesDriver.pendingTrip.isEmpty()) {
                        AlarmServicesDriver.pendingTrip.clear();
                    }
                    try {
                        AppPreferencesDriver.setPendingTripId(TripDetailActivityDriver.this, "");
                    }catch (NullPointerException e){}
                    try {
                        AppPreferencesDriver.setPendingTrip(TripDetailActivityDriver.this, null);
                    }catch (NullPointerException e){}


                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("200")) {

                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                        String tripId = jsonObject1.getString("trip_id");
                        String unique_number = jsonObject1.getString("unique_number");

                        try {
                            AppPreferencesDriver.setUniqueNumber(instance, unique_number);
                        }catch (Exception e){}
                        String ns = Context.NOTIFICATION_SERVICE;
                        NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
                        nMgr.cancel(ConfigDriver.NEW_TRIP_ID);

                        AppPreferencesDriver.setTripstatusForDriver(instance, AppConstantDriver.ALLOT);
                        AppPreferencesDriver.setTripdate(instance, Function.getCurrentDateTime());
                        AppPreferencesDriver.setTripId(instance, tripId);

                        /*>>>>>>>>>>>>>>>>*/
                        JSONObject jsonObject3 = new JSONObject();
                        try {
                            jsonObject3.put("method", AppConstantDriver.METHOD.SENDNOTIFICATIONTOALLOTHERDRIVER);
                            jsonObject3.put("driver_id", AppPreferencesDriver.getDriverId(instance));
                            jsonObject3.put("trip_id", tripId);

                            JSONParser jsonParser = new JSONParser(instance);
                            jsonParser.parseVollyObject(AppConstantDriver.URL.ONDEMAND_DRIVER_DRIVERBOOKINGPROCESS, 1, jsonObject3, new VolleyCallBack() {
                                @Override
                                public void success(String response) {

                                }
                            }, new ServerErrorCallBack() {
                                @Override
                                public void error(String response) {

                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        /*>>>>>>>>>>>>>>>>*/

                        /*Intent intent;
                        if(container1.getVisibility() == View.VISIBLE){
                            intent = new Intent(TripDetailActivityDriver.this, TripAcceptActivityDriver.class);
                        }else {
                            intent = new Intent(TripDetailActivityDriver.this, MainActivity.class);
                        }*/
                        Intent intent = new Intent(TripDetailActivityDriver.this, TripAcceptActivityDriver.class);

                        intent.putExtra("tripDetails", trip);
                        intent.putExtra("tripId", tripId);
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_to_right,
                                R.anim.right_to_left);
                        finish();

                        /*trips = new TripDriver();
                        trips.setId(Long.parseLong(jsonObject1.getString("trip_id")));
                        trips.setSourceAddress(jsonObject1.getString("pickup_address"));
                        trips.setDestinationAddress(jsonObject1.getString("drop_address"));
                        trips.setSourceLatitude(new Double(jsonObject1.getString("pickup_lat")));
                        trips.setSourcelogitude(new Double(jsonObject1.getString("pickup_lng")));
                        if(jsonObject1.getString("drop_lat").equalsIgnoreCase("")){
                            trips.setDestinationLatitude(new Double(0.0));
                        }else{
                            trips.setDestinationLatitude(new Double(jsonObject1.getString("drop_lat")));
                        }

                        if(jsonObject1.getString("drop_lng").equalsIgnoreCase("")){
                            trips.setDestinationLogitude(new Double(0.0));
                        }else{
                            trips.setDestinationLogitude(new Double(jsonObject1.getString("drop_lng")));
                        }
                        //trips.setDestinationLatitude(new Double(jsonObject1.getString("drop_lat")));
                        // trips.setDestinationLogitude(new Double(jsonObject1.getString("drop_lng")));
                        trips.setDistance(jsonObject1.getString("est_km"));

                        trips.setUserName(jsonObject1.getString("user_name"));
                        trips.setUserNumber(jsonObject1.getString("user_number"));
                        trips.setPickCno(jsonObject1.getString("mobile"));
                        trips.setPickCname(jsonObject1.getString("name"));
                        trips.setDropCname(jsonObject1.getString("drop_name"));
                        trips.setDropCno(jsonObject1.getString("drop_mobile"));
                        trips.setService_id(jsonObject1.getString("service_id"));
                        trips.setDate(jsonObject1.getString("added_on"));
                        trips.setFare(jsonObject1.getString("est_fare"));

                        int basic_fare = 0, per_fare = 0, other_fare = 0;
                        if (!jsonObject1.getString("basic_fare").equalsIgnoreCase("")) {
                            basic_fare = Integer.parseInt(jsonObject1.getString("basic_fare"));
                        }
                        if (!jsonObject1.getString("per_fare").equalsIgnoreCase("")) {
                            per_fare = Integer.parseInt(jsonObject1.getString("per_fare"));
                        }
                        if (!jsonObject1.getString("other_fare").equalsIgnoreCase("")) {
                            other_fare = Integer.parseInt(jsonObject1.getString("other_fare"));
                        }


                        AppPreferencesDriver.setBasicFare(instance, basic_fare);
                        AppPreferencesDriver.setPerFare(instance, per_fare);
                        AppPreferencesDriver.setOtherFare(instance, other_fare);

                        int total = basic_fare + (per_fare * other_fare);

                        String ns = Context.NOTIFICATION_SERVICE;

                        DBAdapter_Driver db = new DBAdapter_Driver(instance);

                        db.deleteSnoozeTrip(Integer.parseInt(String.valueOf(trips.getId())));
                        Intent snoozeintent = new Intent(instance, TripDetailActivityDriver.class);
                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        PendingIntent pendingIntent = PendingIntent.getActivity(instance, Integer.parseInt(String.valueOf(trips.getId())), snoozeintent, PendingIntent.FLAG_CANCEL_CURRENT);

                        alarmManager.cancel(pendingIntent);

                        NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
                        nMgr.cancel(ConfigDriver.NEW_TRIP_ID);
                        AppPreferencesDriver.setTripstatusForDriver(instance, AppConstantDriver.ALLOT);
                        AppPreferencesDriver.setTripdate(instance, Function.getCurrentDateTime());
                        AppPreferencesDriver.setTripId(instance, String.valueOf(trips.getId()));


                        JSONObject jsonObject3 = new JSONObject();
                        try {
                            jsonObject3.put("method", AppConstantDriver.METHOD.SENDNOTIFICATIONTOALLOTHERDRIVER);
                            jsonObject3.put("driver_id", AppPreferencesDriver.getDriverId(instance));
                            jsonObject3.put("trip_id", trip.getId());

                            JSONParser jsonParser = new JSONParser(instance);
                            jsonParser.parseVollyJSONObject(AppConstantDriver.URL.ONDEMAND_DRIVER_DRIVERBOOKINGPROCESS, 1, jsonObject3, "", new VolleyCallBack() {
                                @Override
                                public void success(String response) {
                                    Intent intent = new Intent(TripDetailActivityDriver.this, TripAcceptActivityDriver.class);
                                    intent.putExtra("tripDetails", trips);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.left_to_right,
                                            R.anim.right_to_left);
                                    finish();
                                }
                            }, new ServerErrorCallBack() {
                                @Override
                                public void error(String response) {

                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/

                    }else if(status.equalsIgnoreCase("900")){
                        Intent intent = new Intent(instance, DialogActivityDriver.class);
                        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction("loginSessionExpire");
                        intent.putExtra("msg",jsonObject.getString("message"));
                        startActivity(intent);
                    }else {



                        tripAlreadyAccept(TripDetailActivityDriver.this, "This trip is already assigned to some other driver!");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new ServerErrorCallBack() {
            @Override
            public void error(String response) {

            }
        });
    }

    public void RejectTrip(JSONObject jsonObject){
        JSONParser jsonParser = new JSONParser(instance);
        jsonParser.parseVollyJSONObject(AppConstantDriver.URL.onDeand_Driver_drivertificationManage, 1, jsonObject, "Wait...", new VolleyCallBack() {
            @Override
            public void success(String response) {

                try {
                    if(!((AppCompatActivity)TripDetailActivityDriver.this).isFinishing()){

                    JSONObject jsonObject = new JSONObject(response);
                    String status =  jsonObject.getString("status");


                    if (status.equalsIgnoreCase("200")) {
                        AppPreferencesDriver.setPendingTripId(TripDetailActivityDriver.this,"");
                        if(!AlarmServicesDriver.pendingTrip.isEmpty()) {
                            AlarmServicesDriver.pendingTrip.remove(0);
                        }
                        AppPreferencesDriver.setPendingTrip(TripDetailActivityDriver.this, null);
                        Intent intent = new Intent(instance, TripDetailActivityDriver.class);
                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        PendingIntent pendingIntent = PendingIntent.getActivity(instance, Integer.parseInt(jsonObject.getString("trip_id")), intent, PendingIntent.FLAG_CANCEL_CURRENT);

                        alarmManager.cancel(pendingIntent);
                        DBAdapter_Driver db = new DBAdapter_Driver(instance);

                        db.deleteSnoozeTrip(Integer.parseInt(jsonObject.getString("trip_id")));

                        Intent intent11 = new Intent(instance, NavigationDrawerDriver.class);
                        intent11.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent11.setAction("");
                        startActivity(intent11);
                        overridePendingTransition(R.anim.left_to_right,
                                R.anim.right_to_left);
                        finish();
                    }else if(status.equalsIgnoreCase("900")){
                        Intent intent = new Intent(instance, DialogActivityDriver.class);
                        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction("loginSessionExpire");
                        intent.putExtra("msg",jsonObject.getString("message"));
                        startActivity(intent);
                    } else {
                        errorDialog(instance, "Please try again later");
                        Snackbar.make(findViewById(android.R.id.content), "Please try again later", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                    }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new ServerErrorCallBack() {
            @Override
            public void error(String response) {
                try {
                    if(!((Activity)instance).isFinishing()) {
                        JSONObject jsonObject1 = new JSONObject(response);
                        String msg = jsonObject1.getString("vollymsg");
                        errorDialog(instance, msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void errorDialog(Context context, String msg) {

        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
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
    public void tripAlreadyAccept(Context context, String msg) {

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(msg)
                .setConfirmText("Ok")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        finish();
                    }
                });
        sweetAlertDialog.setCancelable(false);

        if(!((AppCompatActivity)TripDetailActivityDriver.this).isFinishing()){
            sweetAlertDialog.show();
        }


    }

    public void startVibrate() {
        long pattern[] = { 0, 100, 200, 300, 400 };
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, 0);
    }

    public void stopVibrate() {
        vibrator.cancel();
    }
}
