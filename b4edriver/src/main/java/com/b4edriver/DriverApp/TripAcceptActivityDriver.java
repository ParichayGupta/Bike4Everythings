package com.b4edriver.DriverApp;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.b4edriver.CommonClasses.ServerConnection.Util;
import com.b4edriver.CommonClasses.ServerConnection.VolleyCallBack;
import com.b4edriver.CommonClasses.Classes.FusedLocationServiceDriver;
import com.b4edriver.CommonClasses.Services.WorkTimerService;
import com.b4edriver.CommonClasses.Utils.DirectionsJSONParser;
import com.b4edriver.CommonClasses.Utils.Function;
import com.b4edriver.Database.DBAdapter_Driver;
import com.b4edriver.GCM.ConfigDriver;
import com.b4edriver.Model.AllMessageDriver;
import com.b4edriver.Model.NotificationDriver;
import com.b4edriver.Model.TripDriver;
import com.b4edriver.CommonClasses.Utils.AppConstantDriver;
import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.b4edriver.CommonClasses.Utils.DialogManagerDriver;
import com.b4edriver.CommonClasses.ServerConnection.JSONParser;
import com.b4edriver.CommonClasses.ServerConnection.ServerErrorCallBack;
import com.b4edriver.R;
import com.b4elibrary.Logger;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class TripAcceptActivityDriver extends AppCompatActivity implements LocationListener, OnMapReadyCallback {
    public static TripAcceptActivityDriver instance = null;
    //aa
    DialogManagerDriver dialogManager;
    TripDriver trip;
    RelativeLayout userDetailLL;
    ImageView call_img, collapseBtn;
    TextView name, number, address;
    FloatingActionsMenu floatingActionsMenu;
    FusedLocationServiceDriver gps;
    double lat, lon;
    boolean isCollapse = true;
    View view;
    static FloatingActionButton fab_itemdetail;
    LocationManager locationManager;
    DBAdapter_Driver dbAdapter_driver;
    Handler messageHandler = new Handler();
    AlertDialog alertDialog;
    FloatingActionButton fabAllmessage;
    AlertDialog.Builder builder;
    View dialogView;
    AlertDialog alertDialogReciveMessage;
    View.OnClickListener message = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            floatingActionsMenu.collapseImmediately();


            SendMessageDialog(getString(R.string.send_message));


        }
    };
    View.OnClickListener allMessage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            floatingActionsMenu.collapseImmediately();
            Intent intent = new Intent(instance, AllMessageActivityDriver.class);
            startActivity(intent);
            overridePendingTransition(R.anim.left_to_right,
                    R.anim.right_to_left);
        }
    };
    View.OnClickListener cancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            floatingActionsMenu.collapseImmediately();

            final String[] message = {getString(R.string.address_does_not_exist)};
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TripAcceptActivityDriver.this);
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.trip_accept_cancel_driver, null);
            final EditText otherText = (EditText) dialogView.findViewById(R.id.otherText);
            dialogBuilder.setView(dialogView);
            View header = inflater.inflate(R.layout.dialog_heading_driver, null);
            TextView textView = (TextView) header.findViewById(R.id.text);
            ImageView icon = (ImageView) header.findViewById(R.id.icon);
            icon.setImageResource(R.mipmap.ic_launcher);
            textView.setText(R.string.cancel_trip);
            dialogBuilder.setCustomTitle(header);
            //dialogBuilder.setTitle(R.string.cancel_trip);
            //dialogBuilder.setIcon(R.drawable.ic_action_cancel);
            dialogBuilder.setCancelable(false);

            final AlertDialog alertDialog = dialogBuilder.create();

            final RadioGroup mRadioGroup = (RadioGroup) dialogView.findViewById(R.id.radiogroup);
            Button confirm = (Button) dialogView.findViewById(R.id.btn_confirm);
            Button cancel = (Button) dialogView.findViewById(R.id.btn_cancel);

            mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                    if (radioButton.getId() == R.id.radiobutton3) {
                        otherText.setVisibility(View.VISIBLE);
                        message[0] = otherText.getText().toString();
                    } else {
                        otherText.setVisibility(View.GONE);
                        message[0] = radioButton.getText().toString();
                    }
                }
            });

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Function.isOnline(TripAcceptActivityDriver.this)) {

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("method", AppConstantDriver.METHOD.CANCELBOOKING_DRIVER);
                            jsonObject.put("driver_id", AppPreferencesDriver.getDriverId(instance));
                            jsonObject.put("trip_id", trip.getId());
                            jsonObject.put("message", message[0]);
                            jsonObject.put("type", AppPreferencesDriver.getDrivertype(instance));

                            BookingCancelTask(jsonObject);

                        } catch (JSONException e) {
                            dialogManager.stopProcessDialog();
                        }

                        //new TripRejectTask(trip.getId(), message[0]).execute();
                    } else {
                        //dialogManager.showAlertDialog(TripAcceptActivityDriver.this, getString(R.string.error_connection_problem), getString(R.string.error_check_internet_connection), null);
                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_check_internet_connection), Snackbar.LENGTH_LONG).show();
                    }
                    alertDialog.dismiss();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.show();

            /*dialogManager.showProcessDialog(instance,"");
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("method", AppConstantDriver.CANCELBOOKING_DRIVER);
                jsonObject.put("driver_id", AppPreferencesDriver.getUserId(instance));
                jsonObject.put("trip_id", trip.getId());
                jsonObject.put("type", AppPreferencesDriver.getDrivertype(instance));

                new BookingCancelTask(jsonObject).execute();

            }catch (JSONException e){
                dialogManager.stopProcessDialog();
            }*/

        }
    };
    View.OnClickListener arrived = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            floatingActionsMenu.collapseImmediately();
            floatingActionsMenu.setClickable(false);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("method", AppConstantDriver.METHOD.ARRIVE_BOOKING);
                jsonObject.put("driver_id", AppPreferencesDriver.getDriverId(TripAcceptActivityDriver.this));
                jsonObject.put("trip_id", trip.getId());
                jsonObject.put("latitude", gps.getLocation().getLatitude());
                jsonObject.put("longitude", gps.getLocation().getLongitude());
                jsonObject.put("dateTime", Function.getCurrentDateTime());
                jsonObject.put("status", "arrived");
                jsonObject.put("type", AppPreferencesDriver.getDrivertype(TripAcceptActivityDriver.this));

                if (Function.isConnectingToInternet(instance)) {
                    TripArrivedTask(jsonObject);
                } else {

                    dbAdapter_driver.insertOfflineTrip(jsonObject.toString());
                    AppPreferencesDriver.setTripstatusForDriver(TripAcceptActivityDriver.this, AppConstantDriver.ARRIVAL);
                    AppPreferencesDriver.setArrivedTime(TripAcceptActivityDriver.this, Function.getCurrentDateTime());
                    Intent intent = new Intent(TripAcceptActivityDriver.this, TaxiWaitingActivityDriver.class);
                    intent.putExtra("tripDetails", trip);
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_to_right,
                            R.anim.right_to_left);
                    finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };
    View.OnClickListener itemdetail = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(instance, ItemDetailsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction("ItemDetailsUpdated");
            intent.putExtra("data", AppPreferencesDriver.getItemDetails(TripAcceptActivityDriver.this));
            startActivity(intent);
            overridePendingTransition(R.anim.left_to_right,
                    R.anim.right_to_left);
        }
    };
    View.OnClickListener call = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + trip.getPickCno()));
            try {
                startActivity(in);
                overridePendingTransition(R.anim.left_to_right,
                        R.anim.right_to_left);
            } catch (android.content.ActivityNotFoundException ex) {
                Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_LONG)
                        .show();
            }
        }
    };
    View.OnClickListener userCall = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + trip.getUserNumber()));
            try {
                startActivity(in);
                overridePendingTransition(R.anim.left_to_right,
                        R.anim.right_to_left);
            } catch (android.content.ActivityNotFoundException ex) {
                Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_LONG)
                        .show();
            }
        }
    };
    Runnable messageAcceptTask = new Runnable() {
        @Override
        public void run() {


            Logger.log("messageAcceptTask", "messageAcceptTask");
            if (!AppPreferencesDriver.getReciveMessage(instance).equalsIgnoreCase("")) {
                try {
                    JSONObject jsonObject = new JSONObject(AppPreferencesDriver.getReciveMessage(instance));
                    ReciveMessageDialog("New Message", jsonObject.getString("msg"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AppPreferencesDriver.setReciveMessage(instance, "");

            }
            if (!Function.isAppIsInBackground(instance)) {
                String ns = Context.NOTIFICATION_SERVICE;
                NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);

                nMgr.cancel(ConfigDriver.NOTIFICATION_ID);
            }


            messageHandler.postDelayed(messageAcceptTask, 1000);
        }
    };
    private GoogleMap map;
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
                String totalDuration = Function.getTotalDateDiffrence(AppPreferencesDriver.getTotalDriverDuration(context), AppPreferencesDriver.getCurrentDriverDuration(context));
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
        setContentView(R.layout.activity_trip_accept_driver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        instance = this;

        registerReceiver(broadcastReceiver, new IntentFilter(WorkTimerService.str_receiver));

        gps = new FusedLocationServiceDriver(this);

        AppPreferencesDriver.setActivity(TripAcceptActivityDriver.this, getClass().getName());

        LocalBroadcastManager.getInstance(this).registerReceiver(tripReceiver,
                new IntentFilter("cancelTripAuto"));
        ////GetTripServices.shouldContinue = false;

        builder = new AlertDialog.Builder(instance);
        builder.setCancelable(false);

        LayoutInflater inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.all_popup_driver, (ViewGroup) findViewById(R.id.layout_root));

        builder.setView(dialogView);
        alertDialogReciveMessage = builder.create();
        dbAdapter_driver = new DBAdapter_Driver(instance);
        Function.callPermisstion(instance, 1);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", AppConstantDriver.METHOD.ACCEPT_BOOKING);
            jsonObject.put("driver_id", AppPreferencesDriver.getDriverId(instance));
            jsonObject.put("trip_id", getIntent().getStringExtra("tripId"));
            jsonObject.put("status", "accept");
            // tempTrip = trip;
            acceptTripTask(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                0, 0, this);

        dialogManager = new DialogManagerDriver();


        messageHandler.postDelayed(messageAcceptTask, 500);

        name = (TextView) findViewById(R.id.name);
        number = (TextView) findViewById(R.id.number);
        address = (TextView) findViewById(R.id.address);
        call_img = (ImageView) findViewById(R.id.img_call);
        userDetailLL = (RelativeLayout) findViewById(R.id.userDetailLL);
        collapseBtn = (ImageButton) findViewById(R.id.collapseBtn);

        call_img.setOnClickListener(call);


        collapseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCollapse) {
                    isCollapse = false;
                    collapseBtn.setImageResource(R.drawable.pluss);
                    userDetailLL.setVisibility(View.GONE);
                } else {
                    isCollapse = true;
                    collapseBtn.setImageResource(R.drawable.minuss);
                    userDetailLL.setVisibility(View.VISIBLE);
                }
            }
        });

        /* FloatingActionsMenu */

        floatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        FloatingActionButton fabArrived = (FloatingActionButton) findViewById(R.id.fab_arrived);
        Button btn_arrived = (Button) findViewById(R.id.btn_arrived);
        fab_itemdetail = (FloatingActionButton) findViewById(R.id.fab_itemdetail);
        FloatingActionButton fabCall = (FloatingActionButton) findViewById(R.id.fab_call);
        FloatingActionButton fabMessage = (FloatingActionButton) findViewById(R.id.fab_message);
        fabAllmessage = (FloatingActionButton) findViewById(R.id.fab_allmessage);
        FloatingActionButton fabCancel = (FloatingActionButton) findViewById(R.id.fab_cancel);

        fabArrived.setOnClickListener(arrived);
        btn_arrived.setOnClickListener(arrived);
        fab_itemdetail.setOnClickListener(itemdetail);
        fabCall.setOnClickListener(userCall);
        fabMessage.setOnClickListener(message);
        fabAllmessage.setOnClickListener(allMessage);
        fabCancel.setOnClickListener(cancel);

        fab_itemdetail.setVisibility(View.GONE);

        //((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        gps.getLocation();
        lat = gps.getLocation().getLatitude();
        lon = gps.getLocation().getLongitude();

        if (lat == 0.0 && lon == 0.0) {
            // showSettingsAlert();
        }

        View mapView = mapFragment.getView();

        View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);

        locationButton.performClick();

        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();

        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);


    }

    public static void updateItemDetailsUI(final String data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (data.equalsIgnoreCase("")) {
                    fab_itemdetail.setVisibility(View.GONE);
                } else {

                    fab_itemdetail.setVisibility(View.VISIBLE);
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(data);

                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String serviceId = jsonObject.getString("order_no");

                        if (serviceId.equalsIgnoreCase("1")) {
                            fab_itemdetail.setIcon(R.drawable.food);
                        } else if (serviceId.equalsIgnoreCase("2")) {
                            fab_itemdetail.setIcon(R.drawable.laundry);
                        } else if (serviceId.equalsIgnoreCase("3")) {
                            fab_itemdetail.setIcon(R.drawable.logistic);
                        } else if (serviceId.equalsIgnoreCase("4")) {
                            fab_itemdetail.setIcon(R.drawable.taxi);
                        } else if (serviceId.equalsIgnoreCase("5")) {
                            fab_itemdetail.setIcon(R.drawable.health_beauty);
                        } else if (serviceId.equalsIgnoreCase("6")) {
                            fab_itemdetail.setIcon(R.drawable.travel);
                        } else if (serviceId.equalsIgnoreCase("7")) {
                            fab_itemdetail.setIcon(R.drawable.homeservice);
                        } else if (serviceId.equalsIgnoreCase("8")) {
                            fab_itemdetail.setIcon(R.drawable.electritian);
                        } else if (serviceId.equalsIgnoreCase("9")) {
                            fab_itemdetail.setIcon(R.drawable.carpainter);
                        } else if (serviceId.equalsIgnoreCase("10")) {
                            fab_itemdetail.setIcon(R.drawable.shirt);
                        } else if (serviceId.equalsIgnoreCase("11")) {
                            fab_itemdetail.setIcon(R.drawable.grocery);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
        // map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
        // map.moveCamera(CameraUpdateFactory.newLatLng(loc));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume() {
        super.onResume();

       /* if(!AppPreferencesDriver.getActivityresumeopen(instance).equalsIgnoreCase("")) {
            Intent intent = new Intent(this, RecivedMessageDriver.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("message", AppPreferencesDriver.getActivityresumeopen(instance));
            startActivity(intent);
            AppPreferencesDriver.setActivityresumeopen(instance,"");
        }*/
    }

    @Override
    public void onBackPressed() {

    }

    private void SendMessageDialog(String message) {

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
        final RadioButton radio1 = (RadioButton) dialogView.findViewById(R.id.radio1);
        RadioButton radio2 = (RadioButton) dialogView.findViewById(R.id.radio2);
        RadioButton radio3 = (RadioButton) dialogView.findViewById(R.id.radio3);
        RadioButton radio4 = (RadioButton) dialogView.findViewById(R.id.radio4);


        radio1.setText(getString(R.string.five_min_late_traffic));
        radio2.setText(getString(R.string.cant_find_address));
        radio3.setText(getString(R.string.ok));
        radio4.setText(getString(R.string.thanksyou));

        radiogroup.setVisibility(View.VISIBLE);
        edittext.setVisibility(View.GONE);

        et_title.setGravity(Gravity.CENTER);
        et_title.setTypeface(Typeface.SERIF, Typeface.BOLD);
        et_title.setTextSize(20);

        et_title.setText(message);

        btn_submit.setText(getString(R.string.send));


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

                int selectedId = radiogroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                radio[0] = (RadioButton) dialogView.findViewById(selectedId);
                String value = radio[0].getText().toString();
                //  Toast.makeText(instance,value,Toast.LENGTH_LONG).show();
                final JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("method", AppConstantDriver.METHOD.SENDMESSAGE);
                    jsonObject.put("uid", AppPreferencesDriver.getDriverId(instance));
                    jsonObject.put("tripId", AppPreferencesDriver.getTripId(instance));
                    jsonObject.put("message", value);
                    jsonObject.put("activity", "accept");


                    if (Function.isConnectingToInternet(instance)) {
                        SendMessage(jsonObject);

                    } else {
                        String phoneNumber = trip.getPickCno();
                        String smsBody = value;

                        SmsManager smsManager = SmsManager.getDefault();

                        smsManager.sendTextMessage(phoneNumber, null, smsBody, null, null);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                alertDialog.dismiss();


            }
        });

        alertDialog.show();
    }

    public void SendMessage(final JSONObject jsonObject) {
        JSONParser jsonParser = new JSONParser(instance);
        jsonParser.parseVollyJSONObject(AppConstantDriver.URL.ONDEMAND_USER_DRIVERMESSAGE, 1, jsonObject, "", new VolleyCallBack() {
            @Override
            public void success(String response) {

            }
        }, new ServerErrorCallBack() {
            @Override
            public void error(String response) {
                try {
                    JSONObject jsonObject1 = new JSONObject(response);
                    String msg = jsonObject1.getString("vollymsg");
                    resendDialog(instance, msg, jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void resendDialog(Context context, String msg, final JSONObject jsonObject) {

        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(msg)
                .setConfirmText("Re Send")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        SendMessage(jsonObject);
                    }
                })
                .show();

    }

    private void ReciveMessageDialog(String message, String msg) {

        List<AllMessageDriver> messageList = new ArrayList<AllMessageDriver>();
        DBAdapter_Driver dbAdapter_user = new DBAdapter_Driver(instance);
        messageList = dbAdapter_user.getAllMessage();

        if (messageList.isEmpty()) {
            fabAllmessage.setVisibility(View.GONE);
        } else {
            fabAllmessage.setVisibility(View.VISIBLE);
        }


        ImageView header = (ImageView) dialogView.findViewById(R.id.header_tv);
        TextView et_title = (TextView) dialogView.findViewById(R.id.et_title);
        TextView countTxt = (TextView) dialogView.findViewById(R.id.countTxt);
        final EditText edittext = (EditText) dialogView.findViewById(R.id.edittext);
        Button btn_submit = (Button) dialogView.findViewById(R.id.btn_submit);

        if (messageList.size() > 1) {
            countTxt.setVisibility(View.VISIBLE);
            Logger.log("LISTSIZE", messageList.size() + ">>>>");
            countTxt.setText(messageList.size() + "");
        } else {
            countTxt.setVisibility(View.GONE);
        }

        edittext.setVisibility(View.GONE);

        et_title.setGravity(Gravity.CENTER);
        et_title.setTypeface(Typeface.SERIF, Typeface.BOLD);
        et_title.setTextSize(20);

        et_title.setText(message + "\n\n" + msg);

        btn_submit.setText(getString(R.string.ok));


        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogReciveMessage.cancel();
            }
        });
        final List<AllMessageDriver> finalMessageList = messageList;
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (finalMessageList.isEmpty() || finalMessageList.size() < 2) {

                } else {

                    Intent intent = new Intent(instance, AllMessageActivityDriver.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_to_right,
                            R.anim.right_to_left);
                }

                alertDialogReciveMessage.cancel();


            }
        });


        if (TripAcceptActivityDriver.instance != null) {
            alertDialogReciveMessage.show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void CancelTripDialog(String msg) {

        AppPreferencesDriver.setTripId(TripAcceptActivityDriver.this, "");

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbAdapter_driver.deleteAllMsg();
        messageHandler.removeCallbacks(messageAcceptTask);
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            map.setMyLocationEnabled(true);
        }



    }

    public void TripArrivedTask(JSONObject jsonObject){
        JSONParser jsonParser = new JSONParser(instance);
        jsonParser.parseVollyJSONObject(AppConstantDriver.URL.ONDEMAND_DRIVER_DRIVERBOOKINGPROCESS, 1, jsonObject, "", new VolleyCallBack() {
            @Override
            public void success(String response) {
                try {
                   if (response != null) {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("200")) {
                            AppPreferencesDriver.setTripstatusForDriver(TripAcceptActivityDriver.this, AppConstantDriver.ARRIVAL);
                            AppPreferencesDriver.setArrivedTime(TripAcceptActivityDriver.this, Function.getCurrentDateTime());
                            Intent intent = new Intent(TripAcceptActivityDriver.this, TaxiWaitingActivityDriver.class);
                            intent.putExtra("tripDetails", trip);
                            startActivity(intent);
                            overridePendingTransition(R.anim.left_to_right,
                                    R.anim.right_to_left);
                            finish();
                        }else if (object.getString("status").equalsIgnoreCase("300")) {
                            final Dialog dialog = new Dialog(TripAcceptActivityDriver.this);
                            // Include dialog.xml file
                            dialog.setContentView(R.layout.aleart_dialog);
                            // Set dialog title
                            dialog.setTitle("Aleart!");

                            dialog.show();

                            Button declineButton = (Button) dialog.findViewById(R.id.declineButton);
                            // if decline button is clicked, close the custom dialog
                            declineButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();

                                }
                            });
                            Button callButton = (Button) dialog.findViewById(R.id.callButton);
                            // if decline button is clicked, close the custom dialog
                            callButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + trip.getPickCno()));
                                    try {
                                        startActivity(in);
                                        overridePendingTransition(R.anim.left_to_right,
                                                R.anim.right_to_left);
                                    } catch (android.content.ActivityNotFoundException ex) {
                                        Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            });
                        }

                        if (object.getString("status").equalsIgnoreCase("400")) {
                            floatingActionsMenu.setClickable(true);
                            errorDialog(instance,getString(R.string.server_not_response));

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new ServerErrorCallBack() {
            @Override
            public void error(String response) {

            }
        });
    }



    public class RoutesDownloadTask extends AsyncTask<String, Void, String> {

        Context context;
        String distanceTime;

        public RoutesDownloadTask(Context context) {
            this.context = context;
        }

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Logger.log("Background Task", e.toString());
            }
            return data;
        }


        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);


        }

        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();

                br.close();

            } catch (Exception e) {
                //Logger.log("Exception while downloading url", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }

        public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

            // Parsing the data in non-ui thread
            @Override
            protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

                JSONObject jObject;
                List<List<HashMap<String, String>>> routes = null;


                try {
                    jObject = new JSONObject(jsonData[0]);
                    DirectionsJSONParser parser = new DirectionsJSONParser();

                    // Starts parsing data
                    routes = parser.parse(jObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return routes;
            }

            // Executes in UI thread, after the parsing process
            @Override
            protected void onPostExecute(List<List<HashMap<String, String>>> result) {


                PolylineOptions lineOptions = null;
                ArrayList<LatLng> points = null;
                String distance = "";
                String duration = "";

                try{


                if (result.size() < 1) {
                    //Toast.makeText(ParserTask.this, "No Points", Toast.LENGTH_SHORT).show();
                    return;
                }
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    if (i == 1) {
                        lineOptions = new PolylineOptions();
                        points = new ArrayList<LatLng>();
                        // Fetching i-th route
                        List<HashMap<String, String>> path = result.get(i);
                        // Fetching all the points in i-th route
                        for (int j = 0; j < path.size(); j++) {
                            HashMap<String, String> point = path.get(j);

                            if (j == 0) {    // Get distance from the list
                                distance = point.get("distance");
                                continue;
                            } else if (j == 1) { // Get duration from the list
                                duration = point.get("duration");
                                continue;
                            } else if (j == 2) { // Get duration from the list

                                continue;
                            }else if(j == 3){
                                continue;
                            }

                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);

                            points.add(position);
//                        Logger.log("distancess", distance);
                            //    String dis = distance.replace("km", "").replace("m", "").replace(" ", "").replace(",", "");
                            //   double dist = new Double(dis);

                            LatLng mapPoint =
                                    new LatLng(lat, lng);
                            builder.include(mapPoint);
                            //if (dist > 40) {
                            //  map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 7f));
                            // } else {
                            // map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 12f));
                            // }
                        }
                        lineOptions.addAll(points);
                        lineOptions.width(5);
                        lineOptions.color(Color.BLUE);

                    map.addPolyline(lineOptions);
                    map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 10));
                    }
                }

                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }

    }

    public void BookingCancelTask(JSONObject jsonObject){
        JSONParser jsonParser = new JSONParser(instance);
        jsonParser.parseVollyJSONObject(AppConstantDriver.URL.ONDEMAND_DRIVER_DRIVERBOOKINGPROCESS, 1, jsonObject, "", new VolleyCallBack() {
            @Override
            public void success(String response) {
                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String s = jsonObject.getString("status");
                        if (s.equalsIgnoreCase("200")) {

                            NotificationDriver notification = new NotificationDriver();
                            notification.setHeader("TripDriver Cancel");
                            notification.setDescription("TripDriver cancel by own");
                            notification.setTime(Function.getCurrentDateTime());

                            AppPreferencesDriver.setTripId(instance, "");
                            AppPreferencesDriver.setItemDetails(instance, "");
                            Intent intent11 = new Intent(instance, NavigationDrawerDriver.class);
                            intent11.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent11.setAction("");
                            startActivity(intent11);
                            overridePendingTransition(R.anim.left_to_right,
                                    R.anim.right_to_left);
                            DBAdapter_Driver dbAdapterDriver = new DBAdapter_Driver(instance);
                            dbAdapterDriver.deleteTripById(Integer.parseInt(String.valueOf(trip.getId())));
                            dbAdapterDriver.insertNotification(notification);
                            finish();

                        }else {
                            errorDialog(instance,getString(R.string.server_not_response));
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
                    errorDialog(instance,msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void acceptTripTask(JSONObject jsonObject){
        JSONParser jsonParser = new JSONParser(instance);
        jsonParser.parseVollyJSONObject(AppConstantDriver.URL.ONDEMAND_DRIVER_DRIVERBOOKINGPROCESS, 1, jsonObject, "", new VolleyCallBack() {
            @Override
            public void success(String response) {

                try {
                    /*try {
                        AppPreferencesDriver.setPendingTripId(TripAcceptActivityDriver.this, "");
                    }catch (NullPointerException e){}
                    if(!AlarmServicesDriver.pendingTrip.isEmpty()) {
                        AlarmServicesDriver.pendingTrip.remove(AppPreferencesDriver.getPendingTrip(TripAcceptActivityDriver.this));
                    }
                    AppPreferencesDriver.setPendingTrip(TripAcceptActivityDriver.this, null);*/
                    Logger.log("AcceptResponse", response);
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("200")) {

                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                        TripDriver trips = new TripDriver();
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
                        trip = trips;


                        AppPreferencesDriver.setTripstatusForDriver(instance, AppConstantDriver.ALLOT);
                        AppPreferencesDriver.setTripdate(instance, Function.getCurrentDateTime());
                        AppPreferencesDriver.setTripId(instance, String.valueOf(trips.getId()));

                        updateUi(trips);

                        int basic_fare = 0, per_fare = 0, other_fare = 0;
                        String discount = "0", promo_id = "0";
                        if (!jsonObject1.getString("basic_fare").equalsIgnoreCase("")) {
                            basic_fare = Integer.parseInt(jsonObject1.getString("basic_fare"));
                        }
                        if (!jsonObject1.getString("per_fare").equalsIgnoreCase("")) {
                            per_fare = Integer.parseInt(jsonObject1.getString("per_fare"));
                        }
                        if (!jsonObject1.getString("other_fare").equalsIgnoreCase("")) {
                            other_fare = Integer.parseInt(jsonObject1.getString("other_fare"));
                        }

                        if (!jsonObject1.getString("discount").equalsIgnoreCase("")) {
                            discount = jsonObject1.getString("discount");
                        }
                        if (!jsonObject1.getString("promo_id").equalsIgnoreCase("")) {
                            promo_id = jsonObject1.getString("promo_id");
                        }

                        AppPreferencesDriver.setRefferalcode(instance, jsonObject1.getString("referalUse"));

                        AppPreferencesDriver.setBasicFare(instance, basic_fare);
                        AppPreferencesDriver.setPerFare(instance, per_fare);
                        AppPreferencesDriver.setOtherFare(instance, other_fare);
                        AppPreferencesDriver.setPromocode(instance, discount);
                        AppPreferencesDriver.setPromoid(instance, promo_id);
                        try {
                            Logger.log("farecalculate",trips.getFare());
                                    String[] fare = trips.getFare().split("ß");
                            AppPreferencesDriver.setEstmateprice(instance, fare[0]);
                            AppPreferencesDriver.setEstmatedistination(instance, fare[1]);
                            AppPreferencesDriver.setEstmatedistance(instance, fare[2]);
                            try{
                                //AppPreferencesDriver.setServiceType(instance,fare[3]);
                                AppPreferencesDriver.setServiceType(instance,"");
                            }catch (Exception ee){
                                AppPreferencesDriver.setServiceType(instance,"");
                            }
                            try{
                                AppPreferencesDriver.setDeleveryCharge(instance,fare[4]);
                            }catch (Exception ee){
                                AppPreferencesDriver.setServiceType(instance,"");
                            }
                            try{
                                AppPreferencesDriver.setPerkmcharge(instance,fare[5]);
                            }catch (Exception ee){
                                AppPreferencesDriver.setPerkmcharge(instance,"");
                            }
                        }catch (Exception e){}
                        Logger.log("farecalculate", AppPreferencesDriver.getEstmateprice(instance)+"\n"+ AppPreferencesDriver.getEstmatedistination(instance));

                        int total = basic_fare + (per_fare * other_fare);



                    }else if(status.equalsIgnoreCase("900")){
                        Intent intent = new Intent(instance, DialogActivityDriver.class);
                        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction("loginSessionExpire");
                        intent.putExtra("msg",jsonObject.getString("message"));
                        startActivity(intent);
                    }else {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(instance);
                        builder.setCancelable(false);

                        LayoutInflater inflater = getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.all_popup_driver, (ViewGroup) findViewById(R.id.layout_root));

                        builder.setView(dialogView);

                        ImageView header = (ImageView) dialogView.findViewById(R.id.header_tv);
                        TextView et_title = (TextView) dialogView.findViewById(R.id.et_title);
                        final EditText edittext = (EditText) dialogView.findViewById(R.id.edittext);
                        Button btn_submit = (Button) dialogView.findViewById(R.id.btn_submit);

                        et_title.setGravity(Gravity.CENTER);
                        et_title.setTypeface(null, Typeface.BOLD);
                        et_title.setTextSize(20);

                        et_title.setText("This trip is already assigned to some other driver!");
                        edittext.setVisibility(View.GONE);
                        btn_submit.setText("OK");


                        final AlertDialog alertDialog = builder.create();


                        header.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.cancel();
                                finish();
                            }
                        });

                        btn_submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                alertDialog.cancel();
                                finish();
                            }
                        });


                        alertDialog.show();
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

    public void updateUi(TripDriver trip){

        final LatLng sourceLocation = new LatLng(trip.getSourceLatitude(), trip.getSourcelogitude());
        final LatLng destinationLocation = new LatLng(trip.getDestinationLatitude(), trip.getDestinationLogitude());
        // map.moveCamera(CameraUpdateFactory.newLatLng(sourceLocation));
        //map.animateCamera(CameraUpdateFactory.zoomTo(11));



        name.setText(trip.getPickCname());
        number.setText(trip.getPickCno());
        address.setText(trip.getSourceAddress());

        dbAdapter_driver.insertTempTrip(trip);
        if (String.valueOf(trip.getDestinationLatitude()).equalsIgnoreCase("") || String.valueOf(trip.getDestinationLogitude()).equalsIgnoreCase("")) {
            trip.setDestinationLatitude(gps.getLocation().getLatitude());
            trip.setDestinationLogitude(gps.getLocation().getLongitude());
        }
        LatLng currentLocation = new LatLng(lat, lon);
        /*final LatLng sourceLocation = new LatLng(trip.getSourceLatitude(), trip.getSourcelogitude());
        final LatLng destinationLocation = new LatLng(trip.getDestinationLatitude(), trip.getDestinationLogitude());*/

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String url = Function.getDirectionsUrl(sourceLocation, destinationLocation);
                RoutesDownloadTask downloadTask = new RoutesDownloadTask(TripAcceptActivityDriver.this);
                downloadTask.execute(url);
            }
        });

        map.addMarker(new MarkerOptions()
                .position(sourceLocation)
                .title("")
                .snippet(trip.getSourceAddress()).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_three)));
        map.addMarker(new MarkerOptions()
                .position(destinationLocation)
                .title("")
                .snippet(trip.getDestinationAddress()).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_four)));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sourceLocation, 15));
    }
    public void errorDialog(Context context,String msg) {

        AlertDialog.Builder  builder = new AlertDialog.Builder(TripAcceptActivityDriver.this);
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
}
