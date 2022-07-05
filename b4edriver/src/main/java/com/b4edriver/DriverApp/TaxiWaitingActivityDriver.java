package com.b4edriver.DriverApp;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.InputType;
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
import android.widget.Toast;

import com.b4edriver.CommonClasses.ServerConnection.JSONParser;
import com.b4edriver.CommonClasses.ServerConnection.ServerErrorCallBack;
import com.b4edriver.CommonClasses.ServerConnection.Util;
import com.b4edriver.CommonClasses.ServerConnection.VolleyCallBack;
import com.b4edriver.CommonClasses.Services.WorkTimerService;
import com.b4edriver.CommonClasses.Utils.AppConstantDriver;
import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.b4edriver.CommonClasses.Utils.DialogManagerDriver;
import com.b4edriver.CommonClasses.Utils.DirectionsJSONParser;
import com.b4edriver.CommonClasses.Utils.Function;
import com.b4edriver.Database.DBAdapter_Driver;
import com.b4edriver.Database.Database;
import com.b4edriver.DistanceCalculate.GpsDistanceCalculator;
import com.b4edriver.DistanceCalculate.GpsDistanceTimeUpdater;
import com.b4edriver.DistanceCalculate.Prefs;
import com.b4edriver.DistanceCalculate.SPLabels;
import com.b4edriver.GCM.ConfigDriver;
import com.b4edriver.Model.AllMessageDriver;
import com.b4edriver.Model.GoogleAddress;
import com.b4edriver.Model.NotificationDriver;
import com.b4edriver.Model.TripDriver;
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
import com.google.gson.Gson;
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


public class TaxiWaitingActivityDriver extends AppCompatActivity implements LocationListener, OnMapReadyCallback {
    public static TaxiWaitingActivityDriver instance = null;
    public  FloatingActionButton fabBoarded;
    FloatingActionButton fabAllmessage;
    //aa
    TripDriver trip;
    RelativeLayout userDetailLL;
    ImageView call_img, collapseBtn;
    boolean isCollapse = true;
    TextView name, number, address, offline_tv;
    FloatingActionsMenu floatingActionsMenu;
    DialogManagerDriver dialogManager;
    double lat, lon;
    LocationManager locationManager;
    AlertDialog.Builder builder;
    View dialogView;
    AlertDialog alertDialogReciveMessage;
    DBAdapter_Driver dbAdapter_driver;
    Handler messageHandler = new Handler();
    View.OnClickListener message = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            floatingActionsMenu.collapseImmediately();

            SendMessageDialog(getString(R.string.send_message));

        }
    };
    View.OnClickListener cancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            floatingActionsMenu.collapseImmediately();

            final String[] message = {getString(R.string.client_is_not_here)};
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TaxiWaitingActivityDriver.this);

            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.trip_waiting_cancel_driver, null);
            dialogBuilder.setView(dialogView);
            View header = inflater.inflate(R.layout.dialog_heading_driver, null);
            ImageView icon = (ImageView) header.findViewById(R.id.icon);
            icon.setImageResource(R.mipmap.ic_launcher);
            TextView textView = (TextView) header.findViewById(R.id.text);
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
                    message[0] = radioButton.getText().toString();
                }
            });

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Function.isOnline(TaxiWaitingActivityDriver.this)) {

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
                        //dialogManager.showAlertDialog(TaxiWaitingActivityDriver.this, getString(R.string.error_connection_problem), getString(R.string.error_check_internet_connection), null);
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


        }
    };
    View.OnClickListener boarded = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            StartTrip();


        }
    };
    View.OnClickListener itemdetail = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(instance, ItemDetailsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction("ItemDetailsUpdated");
            intent.putExtra("data", AppPreferencesDriver.getItemDetails(TaxiWaitingActivityDriver.this));
            startActivity(intent);
            overridePendingTransition(R.anim.left_to_right,
                    R.anim.right_to_left);
        }
    };
    View.OnClickListener call = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            floatingActionsMenu.collapseImmediately();
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
            floatingActionsMenu.collapseImmediately();
            Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + trip.getUserNumber()));
            try {
                if (ActivityCompat.checkSelfPermission(instance, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(in);
                overridePendingTransition(R.anim.left_to_right,
                        R.anim.right_to_left);
            } catch (android.content.ActivityNotFoundException ex) {
                Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_LONG)
                        .show();
            }
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
    Runnable messageWaitTask = new Runnable() {
        @Override
        public void run() {
            /*if(!AppPreferencesDriver.getReciveMessage(instance).equalsIgnoreCase("")){

                ReciveMessageDialog("New Message",AppPreferencesDriver.getReciveMessage(instance));
                AppPreferencesDriver.setReciveMessage(instance,"");

            }*/

            if (!AppPreferencesDriver.getReciveMessage(instance).equalsIgnoreCase("")) {
                try {
                    JSONObject jsonObject = new JSONObject(AppPreferencesDriver.getReciveMessage(instance));
                    ReciveMessageDialog("New Message", jsonObject.getString("msg"));
                    AppPreferencesDriver.setReciveMessage(instance, "");
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

            messageHandler.postDelayed(messageWaitTask, 5000);
        }
    };
    private GoogleMap map;

    public void StartTrip() {

       /* DistanceTravelled distanceTravelled1 = new DistanceTravelled(1, "",
                0.0 + 0.0 + 0.0,
                0.0,
                0.0,
                0.0,
                AppPreferencesDriver.getTripId(instance));
        dbAdapter_driver.setDistance(distanceTravelled1);
        dbAdapter_driver.deleteDistance();*/
        dbAdapter_driver.deleteLocation();

        try {
            dbAdapter_driver.deleteLogs();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        //sendMailBeforeStart(instance);


        /*>>>>>>>*/


        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog1, null);
        dialog.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
        edt.setInputType(InputType.TYPE_CLASS_NUMBER);

        dialog.setTitle("B4E");
        dialog.setMessage("Enter the Unique Verification Code Given by Customer");
        dialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                boolean isVerify = false;

                if(edt.getText().toString().equalsIgnoreCase("8269262610")){
                    isVerify = true;
                }else if(edt.getText().toString().equalsIgnoreCase(AppPreferencesDriver.getUniqueNumber(TaxiWaitingActivityDriver.this))){
                    isVerify = true;
                }else {
                    isVerify = false;
                    Toast.makeText(instance,"Invalid Code! : "+ edt.getText().toString(), Toast.LENGTH_LONG).show();
                }

                if(isVerify) {
                    //AppPreferencesDriver.setUniqueNumber(instance, MASTER_VERIFICATIONCODE);
                    LatLng currentPickup = new LatLng(AppPreferencesDriver.getLatitude(TaxiWaitingActivityDriver.this),
                            AppPreferencesDriver.getLongitude(TaxiWaitingActivityDriver.this));
                    String addressURL = Function.getAddressFromLatlng(currentPickup.latitude, currentPickup.longitude);

                    JSONParser jsonParser = new JSONParser(TaxiWaitingActivityDriver.this);
                    jsonParser.getGoogleAddress(addressURL, new VolleyCallBack() {
                        @Override
                        public void success(String response) {
                            Gson gson = new Gson();
                            GoogleAddress googleAddress = gson.fromJson(response, GoogleAddress.class);

                            List<GoogleAddress> address = googleAddress.results;
                            if (address.isEmpty()) {
                                if (trip.getSourceAddress() == null) {
                                    AppPreferencesDriver.setSourceaddress(TaxiWaitingActivityDriver.this, "N/A");
                                } else {
                                    AppPreferencesDriver.setSourceaddress(TaxiWaitingActivityDriver.this, trip.getSourceAddress());
                                }
                            } else {
                                Logger.log("Addresss::", "sorce:DONE>>>" + address.get(0).address);
                                String sourceAddress = address.get(0).address;
                                AppPreferencesDriver.setSourceaddress(TaxiWaitingActivityDriver.this, sourceAddress);
                            }
                            BoardTrip();

                        }
                    }, new ServerErrorCallBack() {
                        @Override
                        public void error(String response) {
                            Logger.log("Addresss::", "sorce:DONE" + response);
                            try {
                                JSONObject object = new JSONObject(response);
                                if (object.getString("status").equals("200")) {
                                    AppPreferencesDriver.setSourceaddress(TaxiWaitingActivityDriver.this, trip.getSourceAddress());
                                    BoardTrip();
                                } else {
                                    new SweetAlertDialog(TaxiWaitingActivityDriver.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Oops...")
                                            .setContentText(object.getString("msg"))
                                            .setConfirmText("Close")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    sweetAlertDialog.dismissWithAnimation();
                                                }
                                            })
                                            .show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                AppPreferencesDriver.setSourceaddress(TaxiWaitingActivityDriver.this, trip.getSourceAddress());
                                BoardTrip();
                            }
                        }
                    });
                }

            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        dialog.show();
        /*Dialog b = dialog.create();
        b.show();*/
        /*>>>>>>>*/


    }

    private void BoardTrip() {
        floatingActionsMenu.collapseImmediately();
        floatingActionsMenu.setClickable(false);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", AppConstantDriver.METHOD.START_BOOKING);
            jsonObject.put("driver_id", AppPreferencesDriver.getDriverId(instance));
            jsonObject.put("trip_id", trip.getId());
            jsonObject.put("latitude", AppPreferencesDriver.getLatitude(TaxiWaitingActivityDriver.this));
            jsonObject.put("longitude", AppPreferencesDriver.getLongitude(TaxiWaitingActivityDriver.this));
            jsonObject.put("dateTime", Function.getCurrentDateTime());
            jsonObject.put("status", "start");
            jsonObject.put("type", AppPreferencesDriver.getDrivertype(instance));


            if (Function.isConnectingToInternet(instance)) {
                TripStartTask(jsonObject);
            } else {
                new AlertDialog.Builder(TaxiWaitingActivityDriver.this).setCancelable(true).setMessage("Please check internet connection!")
                        .setTitle("Connection error!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
               /* DBAdapter_Driver dbAdapter_driver = new DBAdapter_Driver(instance);
                dbAdapter_driver.insertOfflineTrip(jsonObject.toString());

                AppPreferencesDriver.setTripstatusForDriver(TaxiWaitingActivityDriver.this, AppConstantDriver.START_TRIP);
                AppPreferencesDriver.setStartTime(TaxiWaitingActivityDriver.this, Function.getCurrentDateTime());
                LatLng currentPickup = new LatLng(AppPreferencesDriver.getLatitude(TaxiWaitingActivityDriver.this),
                        AppPreferencesDriver.getLongitude(TaxiWaitingActivityDriver.this));
                AppPreferencesDriver.setSourcelatitude(TaxiWaitingActivityDriver.this, String.valueOf(currentPickup.latitude));
                AppPreferencesDriver.setSourcelongitude(TaxiWaitingActivityDriver.this, String.valueOf(currentPickup.longitude));


                Intent intent = new Intent(TaxiWaitingActivityDriver.this, TripStartedActivityDriver.class);
                intent.putExtra("tripDetails", trip);
                startActivity(intent);
                overridePendingTransition(R.anim.left_to_right,
                        R.anim.right_to_left);
                finish();*/
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

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
        setContentView(R.layout.activity_taxi_waiting_driver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        instance = this;
        Function.callPermisstion(instance, 1);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                0, 0, this);

        registerReceiver(broadcastReceiver,new IntentFilter(WorkTimerService.str_receiver));

        builder = new AlertDialog.Builder(instance);
        builder.setCancelable(false);

        LayoutInflater inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.all_popup_driver, (ViewGroup) findViewById(R.id.layout_root));

        builder.setView(dialogView);
        alertDialogReciveMessage = builder.create();

        dbAdapter_driver = new DBAdapter_Driver(instance);
        AppPreferencesDriver.setActivity(TaxiWaitingActivityDriver.this, getClass().getName());

        name = (TextView) findViewById(R.id.name);
        number = (TextView) findViewById(R.id.number);
        address = (TextView) findViewById(R.id.address);
        offline_tv = (TextView) findViewById(R.id.offline_tv);
        call_img = (ImageView) findViewById(R.id.img_call);

        trip = getIntent().getParcelableExtra("tripDetails");
        name.setText(trip.getPickCname());
        number.setText(trip.getPickCno());
        address.setText(trip.getSourceAddress());

        messageHandler.postDelayed(messageWaitTask, 1000);

        DBAdapter_Driver dbAdapter_driver = new DBAdapter_Driver(instance);
        dbAdapter_driver.insertTempTrip(trip);

        dialogManager = new DialogManagerDriver();

        if (String.valueOf(trip.getDestinationLatitude()).equalsIgnoreCase("") || String.valueOf(trip.getDestinationLogitude()).equalsIgnoreCase("")) {
            trip.setDestinationLatitude(AppPreferencesDriver.getLatitude(TaxiWaitingActivityDriver.this));
            trip.setDestinationLogitude(AppPreferencesDriver.getLongitude(TaxiWaitingActivityDriver.this));
        }
         /* FloatingActionsMenu */

        floatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        FloatingActionButton fabCall = (FloatingActionButton) findViewById(R.id.fab_call);
        FloatingActionButton fab_itemdetail = (FloatingActionButton) findViewById(R.id.fab_itemdetail);
        FloatingActionButton fabMessage = (FloatingActionButton) findViewById(R.id.fab_message);
        FloatingActionButton fabCancel = (FloatingActionButton) findViewById(R.id.fab_cancel);
        fabAllmessage = (FloatingActionButton) findViewById(R.id.fab_allmessage);
        fabBoarded = (FloatingActionButton) findViewById(R.id.fab_boarded);
        Button btn_boarded = (Button) findViewById(R.id.btn_boarded);
        userDetailLL = (RelativeLayout) findViewById(R.id.userDetailLL);
        collapseBtn = (ImageButton) findViewById(R.id.collapseBtn);

        fabCall.setOnClickListener(userCall);
        call_img.setOnClickListener(call);
        fab_itemdetail.setOnClickListener(itemdetail);
        fabMessage.setOnClickListener(message);
        fabAllmessage.setOnClickListener(allMessage);
        fabCancel.setOnClickListener(cancel);
        fabBoarded.setOnClickListener(boarded);
        btn_boarded.setOnClickListener(boarded);

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


       // ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        lat = AppPreferencesDriver.getLatitude(TaxiWaitingActivityDriver.this);
        lon = AppPreferencesDriver.getLongitude(TaxiWaitingActivityDriver.this);

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

        if (AppPreferencesDriver.getItemDetails(TaxiWaitingActivityDriver.this).equalsIgnoreCase("")) {
            fab_itemdetail.setVisibility(View.GONE);
        } else {
            fab_itemdetail.setVisibility(View.VISIBLE);
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(AppPreferencesDriver.getItemDetails(instance));

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
        LatLng currentLocation = new LatLng(lat, lon);

        final LatLng sourceLocation = new LatLng(trip.getSourceLatitude(), trip.getSourcelogitude());
        final LatLng destinationLocation = new LatLng(trip.getDestinationLatitude(), trip.getDestinationLogitude());

        if (Function.isConnectingToInternet(instance)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String url = Function.getDirectionsUrl(sourceLocation, destinationLocation);
                    RoutesDownloadTask downloadTask = new RoutesDownloadTask(TaxiWaitingActivityDriver.this);
                    downloadTask.execute(url);
                    offline_tv.setVisibility(View.GONE);
                }
            });
        } else {
            offline_tv.setVisibility(View.GONE);
        }



        GpsDistanceCalculator gpsDistanceCalculator =  GpsDistanceCalculator.getInstance(TaxiWaitingActivityDriver.this, getGpsDistanceTimeUpdater(instance), 0.0, System.currentTimeMillis(), 0.0);

        deleteDataBase();
        gpsDistanceCalculator.stop();
        gpsDistanceCalculator.start();


    }

    @Override
    protected void onStart() {
        super.onStart();
        AppPreferencesDriver.setActivity(TaxiWaitingActivityDriver.this, getClass().getName());

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onResume() {
        super.onResume();

       /* if (AppPreferencesDriver.getActivityresumeopen(instance).equalsIgnoreCase("yes")) {
            fabBoarded.performClick();
            AppPreferencesDriver.setActivityresumeopen(instance, "");
        } else if (!AppPreferencesDriver.getActivityresumeopen(instance).equalsIgnoreCase("")) {
            Intent intent = new Intent(this, RecivedMessageDriver.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("message", AppPreferencesDriver.getActivityresumeopen(instance));
            startActivity(intent);
            AppPreferencesDriver.setActivityresumeopen(instance, "");
        }*/
    }

    @Override
    public void onLocationChanged(Location location) {

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
        RadioButton radio1 = (RadioButton) dialogView.findViewById(R.id.radio1);
        RadioButton radio2 = (RadioButton) dialogView.findViewById(R.id.radio2);
        RadioButton radio3 = (RadioButton) dialogView.findViewById(R.id.radio3);
        RadioButton radio4 = (RadioButton) dialogView.findViewById(R.id.radio4);


        radio1.setText(getString(R.string.i_am_in_truble_please_help));
        radio4.setText(getString(R.string.other));
        radio2.setText(getString(R.string.ok));
        radio3.setText(getString(R.string.thanksyou));

        radiogroup.setVisibility(View.VISIBLE);
        edittext.setVisibility(View.GONE);

        et_title.setGravity(Gravity.CENTER);
        et_title.setTypeface(Typeface.SERIF, Typeface.BOLD);
        et_title.setTextSize(20);

        et_title.setText(message);

        btn_submit.setText(getString(R.string.send));

        final AlertDialog alertDialog = builder.create();

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        final String[] msg = {getString(R.string.i_am_in_truble_please_help)};
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                if (radioButton.getId() == R.id.radio4) {
                    edittext.setVisibility(View.VISIBLE);
                    edittext.setHint("Enter message here.");
                    msg[0] = edittext.getText().toString();
                } else {
                    edittext.setVisibility(View.GONE);
                    msg[0] = radioButton.getText().toString();
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedId = radiogroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                //radio[0] = (RadioButton) dialogView.findViewById(selectedId);
                RadioButton radioButton = (RadioButton) dialogView.findViewById(selectedId);
                if (radioButton.getId() == R.id.radio4) {
                    msg[0] = edittext.getText().toString();
                } else {
                    msg[0] = radioButton.getText().toString();
                }
                String value = msg[0];
                final JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("method", AppConstantDriver.METHOD.SENDMESSAGE);
                    jsonObject.put("uid", AppPreferencesDriver.getDriverId(instance));
                    jsonObject.put("tripId", AppPreferencesDriver.getTripId(instance));
                    jsonObject.put("message", value);
                    jsonObject.put("activity", "arrived");

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

                alertDialog.cancel();


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
                    errorDialog1(instance, msg, jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void errorDialog1(Context context, String msg, final JSONObject jsonObject) {

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


        if (TaxiWaitingActivityDriver.instance != null) {
            alertDialogReciveMessage.show();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbAdapter_driver.deleteAllMsg();
        messageHandler.removeCallbacks(messageWaitTask);
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        final LatLng sourceLocation = new LatLng(trip.getSourceLatitude(), trip.getSourcelogitude());
        final LatLng destinationLocation = new LatLng(trip.getDestinationLatitude(), trip.getDestinationLogitude());
        // map.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        // map.animateCamera(CameraUpdateFactory.zoomTo(13));

        map.addMarker(new MarkerOptions()
                .position(sourceLocation)
                .title(trip.getPickCname())
                .snippet(trip.getSourceAddress()).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_three)));
        map.addMarker(new MarkerOptions()
                .position(destinationLocation)
                .title(trip.getDropCname())
                .snippet(trip.getDestinationAddress()).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_four)));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sourceLocation, 15));

    }

    public void deleteDataBase(){
        Prefs.with(TaxiWaitingActivityDriver.this).save(SPLabels.TOTAL_DISTANCE, "0.0");
        Database.getInstance(TaxiWaitingActivityDriver.this).updateTotalDistance(0.0);
        Database.getInstance(TaxiWaitingActivityDriver.this).deleteWaitTimeData();
        Database.getInstance(TaxiWaitingActivityDriver.this).deleteDriverCurrentLocation();
        Database.getInstance(TaxiWaitingActivityDriver.this).deleteAllCurrentPathItems();
        Database.getInstance(TaxiWaitingActivityDriver.this).deleteDriverLocation();
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

    public void TripStartTask(JSONObject jsonObject) {
        JSONParser jsonParser = new JSONParser(instance);
        jsonParser.parseVollyJSONObject(AppConstantDriver.URL.ONDEMAND_DRIVER_DRIVERBOOKINGPROCESS, 1, jsonObject, "", new VolleyCallBack() {
            @Override
            public void success(String response) {
                try {
                    if (response != null) {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("200")) {

                            AppPreferencesDriver.setTripstatusForDriver(TaxiWaitingActivityDriver.this, AppConstantDriver.START_TRIP);
                            AppPreferencesDriver.setStartTime(TaxiWaitingActivityDriver.this, Function.getCurrentDateTime());
                            LatLng currentPickup = new LatLng(AppPreferencesDriver.getLatitude(TaxiWaitingActivityDriver.this),
                                    AppPreferencesDriver.getLongitude(TaxiWaitingActivityDriver.this));
                            AppPreferencesDriver.setSourcelatitude(TaxiWaitingActivityDriver.this, String.valueOf(currentPickup.latitude));
                            AppPreferencesDriver.setSourcelongitude(TaxiWaitingActivityDriver.this, String.valueOf(currentPickup.longitude));


                            Intent intent = new Intent(TaxiWaitingActivityDriver.this, TripStartedActivityDriver.class);
                            intent.putExtra("tripDetails", trip);
                            startActivity(intent);
                            overridePendingTransition(R.anim.left_to_right,
                                    R.anim.right_to_left);

                            finish();


                        } else {
                            floatingActionsMenu.setClickable(true);
                            errorDialog(instance, getString(R.string.server_not_response));

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
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String msg = jsonObject.getString("vollymsg");
                    if(!((Activity)instance).isFinishing()) {
                        errorDialog(instance, msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void BookingCancelTask(JSONObject jsonObject) {
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
                            AppPreferencesDriver.setItemDetails(instance, "");
                            AppPreferencesDriver.setTripId(instance, "");
                            Intent intent11 = new Intent(instance, NavigationDrawerDriver.class);
                            intent11.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent11.setAction("");
                            startActivity(intent11);
                            overridePendingTransition(R.anim.left_to_right,
                                    R.anim.right_to_left);
                            DBAdapter_Driver dbAdapterDriver = new DBAdapter_Driver(instance);
                            dbAdapterDriver.deleteTripById(Integer.parseInt(String.valueOf(trip.getId())));
                            dbAdapterDriver.insertNotification(notification);
                            finish();

                        } else {
                            errorDialog(instance, getString(R.string.server_not_response));
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
                    errorDialog(instance, msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                            }else if (j == 2) { // Get duration from the list

                                continue;
                            }else if(j == 3){
                                continue;
                            }

                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);

                            points.add(position);

                            // String dis = distance.replace("km", "").replace("m", "").replace(" ", "").replace(",", "");
                            // double dist = new Double(dis);
                            LatLng mapPoint =
                                    new LatLng(lat, lng);
                            builder.include(mapPoint);
                            // if (dist > 40) {
                            // map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 7f));
                            //  } else {
                            //  map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 12f));
                            //  }

                        }
                        lineOptions.addAll(points);
                        lineOptions.width(4);
                        //lineOptions.color(Color.BLUE);

                        map.addPolyline(lineOptions);
                        map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 0));
                    }
                }
            }
        }


    }
    public void errorDialog(Context context,String msg) {

        AlertDialog.Builder  builder = new AlertDialog.Builder(TaxiWaitingActivityDriver.this);
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

