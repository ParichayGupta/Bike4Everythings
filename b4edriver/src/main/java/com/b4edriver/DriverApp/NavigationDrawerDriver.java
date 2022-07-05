package com.b4edriver.DriverApp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.b4edriver.AppController;
import com.b4edriver.CommonClasses.ServerConnection.JSONParser;
import com.b4edriver.CommonClasses.ServerConnection.ServerErrorCallBack;
import com.b4edriver.CommonClasses.ServerConnection.VolleyCallBack;
import com.b4edriver.CommonClasses.Services.AlarmServicesDriver;
import com.b4edriver.CommonClasses.Services.FusedLocationService;
import com.b4edriver.CommonClasses.Services.Mail;
import com.b4edriver.CommonClasses.Services.WorkTimerService;
import com.b4edriver.CommonClasses.Utils.AppConstantDriver;
import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.b4edriver.CommonClasses.Utils.DialogManagerDriver;
import com.b4edriver.CommonClasses.Utils.DriverStatus;
import com.b4edriver.CommonClasses.Utils.Function;
import com.b4edriver.Database.DBAdapter_Driver;
import com.b4edriver.DistanceCalculate.DriverLocationUpdateService;
import com.b4edriver.DistanceCalculate.GpsDistanceTimeUpdater;
import com.b4edriver.Model.UserDriver;
import com.b4edriver.R;
import com.b4edriver.b4edrivers.HostoryActivity;
import com.b4elibrary.Logger;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.b4edriver.CommonClasses.Utils.Logs.getTripLogFile;

public class NavigationDrawerDriver extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static NavigationDrawerDriver instance = null;
    static NavigationView navigationView;
    protected ActivityDetectionBroadcastReceiver mBroadcastReceiver;
    int indecateStatus = 400;
    String indecateMsg = "Gps signal weak!";
    //aa
    UserDriver user;
    DBAdapter_Driver dbUser;
    ImageView driverimage;
    TextView driverName, driverEmail;
    DialogManagerDriver dialogManager;
    Fragment fragment;
    String data;
    MenuItem ItemViewList;
    MenuItem ItemViewindecate;
    MenuItem ItemViewActivty;
    SweetAlertDialog gpsAlertDialog, netAlertDialog;
    BroadcastReceiver Gpsindecator = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getStringExtra("status").equalsIgnoreCase("200")) {
                    indecateStatus = 200;
                    indecateMsg = intent.getStringExtra("msg");
                    if (ItemViewindecate != null) {
                        ItemViewindecate.setIcon(R.drawable.greendot);
                    }
                    navigationView.getMenu().findItem(R.id.nav_offline).setVisible(false);
                    if (gpsAlertDialog.isShowing()) {
                        gpsAlertDialog.dismissWithAnimation();
                        gpsAlertDialog = new SweetAlertDialog(instance, SweetAlertDialog.WARNING_TYPE);
                    }
                } else if (intent.getStringExtra("status").equalsIgnoreCase("300")) {
                    indecateStatus = 300;
                    indecateMsg = intent.getStringExtra("msg");
                    if (ItemViewindecate != null) {
                        ItemViewindecate.setIcon(R.drawable.reddot);
                    }
                    navigationView.getMenu().findItem(R.id.nav_offline).setVisible(true);

                    try {

                        gpsAlertDialog.setTitleText("Oops...");
                        gpsAlertDialog.setCancelable(false);
                        gpsAlertDialog.setContentText(indecateMsg);
                        gpsAlertDialog.setConfirmText("Ok");
                        gpsAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                gpsAlertDialog = new SweetAlertDialog(instance, SweetAlertDialog.WARNING_TYPE);
                                if (FusedLocationService.mGoogleApiClient == null) {
                                    //new FusedLocationService(NavigationDrawerDriver.this);
                                    FusedLocationService locationService = FusedLocationService.getInstance(NavigationDrawerDriver.this);
                                    locationService.googleClientReConnect();
                                } else {
                                    if (!FusedLocationService.mGoogleApiClient.isConnected() || FusedLocationService.mGoogleApiClient.isConnecting()) {
                                        FusedLocationService.mGoogleApiClient.connect();
                                    }
                                }
                            }
                        });


                        gpsAlertDialog.show();


                    } catch (RuntimeException e) {
                    }

                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
    };
    BroadcastReceiver Netindecator = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getStringExtra("status").equalsIgnoreCase("200")) {
                    if (netAlertDialog.isShowing()) {
                        netAlertDialog.dismissWithAnimation();
                        netAlertDialog = new SweetAlertDialog(instance, SweetAlertDialog.WARNING_TYPE);
                    }
                } else if (intent.getStringExtra("status").equalsIgnoreCase("300")) {


                    try {

                        // gpsAlertDialog = new SweetAlertDialog(instance, SweetAlertDialog.WARNING_TYPE)
                        netAlertDialog.setTitleText("Alert");
                        netAlertDialog.setCancelable(false);
                        netAlertDialog.setContentText(intent.getStringExtra("msg"));
                        netAlertDialog.setConfirmText("Ok");
                        netAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                try {
                                    netAlertDialog.dismissWithAnimation();
                                } catch (NullPointerException e) {
                                }
                                netAlertDialog = new SweetAlertDialog(instance, SweetAlertDialog.WARNING_TYPE);
                            }
                        });

                        netAlertDialog.show();


                    } catch (RuntimeException e) {
                    }

                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
    };


    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            String batteryStatus = String.valueOf(level);
            AppPreferencesDriver.setBatteryStatus(instance, batteryStatus);
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

    private static GpsDistanceTimeUpdater getGpsDistanceTimeUpdater(final Context context) {

        GpsDistanceTimeUpdater gpsDistanceTimeUpdater = new GpsDistanceTimeUpdater() {
            public void updateDistanceTime(double distance, long elapsedTime, long waitTime, Location lastGPSLocation, Location lastFusedLocation, double totalHaversineDistance, boolean fromGPS) {

            }

            public void drawOldPath() {

            }

            public void addPathToMap(PolylineOptions polylineOptions) {

            }
        };

        return gpsDistanceTimeUpdater;
    }

/*
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver,new IntentFilter(WorkTimerService.str_receiver));
    }
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppPreferencesDriver.getDriverType(NavigationDrawerDriver.this) == AppConstantDriver.DRIVER_B2B) {
            Class<?> homeClass = null;
            try {
                homeClass = Class.forName("com.b4ebusinessdriver.Activity.HomeActivity");
                Intent intent11 = new Intent(this, homeClass);
                intent11.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent11.setAction("");
                startActivity(intent11);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }



        setContentView(R.layout.activity_navigation_drawer_driver);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        instance = this;

        LocalBroadcastManager.getInstance(NavigationDrawerDriver.this).registerReceiver(broadcastReceiver, new IntentFilter(WorkTimerService.str_receiver));

        AppPreferencesDriver.setDriverStatus(instance, DriverStatus.FREE);

        // GpsDistanceCalculator.getInstance(instance, getGpsDistanceTimeUpdater(instance), GpsDistanceCalculator.getTotalDistanceFromSP(instance), GpsDistanceCalculator.getLastLocationTimeFromSP(instance), GpsDistanceCalculator.getTotalHaversineDistanceFromSP(instance));

        //AppPreferencesDriver.setDriverId(NavigationDrawerDriver.this, 26);

        if (FusedLocationService.mGoogleApiClient == null) {
            //new FusedLocationService(instance);
            FusedLocationService locationService = FusedLocationService.getInstance(NavigationDrawerDriver.this);
            locationService.googleClientReConnect();
        } else {
            if (!FusedLocationService.mGoogleApiClient.isConnected() || FusedLocationService.mGoogleApiClient.isConnecting()) {
                FusedLocationService.mGoogleApiClient.connect();
            }
        }


        if (ContextCompat.checkSelfPermission(instance, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(instance, Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(instance, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Toast.makeText(instance, "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();

            } else {
                //  Toast.makeText(instance, "GPS permission not allows.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(instance, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        } else {
            // Toast.makeText(instance, "GPS permission ", Toast.LENGTH_LONG).show();
        }

        /*if (ContextCompat.checkSelfPermission(instance, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(instance, Manifest.permission.SEND_SMS)) {

                // Toast.makeText(instance, "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();

            } else {
                //  Toast.makeText(instance, "GPS permission not allows.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 2);
            }
        } else {
            // Toast.makeText(instance, "GPS permission ", Toast.LENGTH_LONG).show();
        }*/


        if (!Function.isServiceRunning(NavigationDrawerDriver.this, WorkTimerService.class.getName())) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date_time = simpleDateFormat.format(calendar.getTime());
            AppPreferencesDriver.setTimerData(NavigationDrawerDriver.this, date_time);
            AppPreferencesDriver.setDriverLoginTime(NavigationDrawerDriver.this, date_time);
            startService(new Intent(NavigationDrawerDriver.this, WorkTimerService.class));
        }

        if (!Function.isServiceRunning(NavigationDrawerDriver.this, FusedLocationService.class.getName())) {

            startService(new Intent(NavigationDrawerDriver.this, FusedLocationService.class));
        }
        if (!Function.isServiceRunning(NavigationDrawerDriver.this, DriverLocationUpdateService.class.getName())) {

            startService(new Intent(NavigationDrawerDriver.this, DriverLocationUpdateService.class));
        }

        if (FusedLocationService.mGoogleApiClient == null) {
            //new FusedLocationService(NavigationDrawerDriver.this);
            FusedLocationService locationService = FusedLocationService.getInstance(NavigationDrawerDriver.this);
            locationService.googleClientReConnect();
        } else {
            if (!FusedLocationService.mGoogleApiClient.isConnected() || FusedLocationService.mGoogleApiClient.isConnecting()) {
                FusedLocationService.mGoogleApiClient.connect();
            }
        }


        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        AppPreferencesDriver.setActivity(instance, getClass().getName());

        LocalBroadcastManager.getInstance(this).registerReceiver(Gpsindecator,
                new IntentFilter("Gpsindecator"));
        LocalBroadcastManager.getInstance(this).registerReceiver(Netindecator,
                new IntentFilter("Netindecator"));
        mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(AppConstantDriver.BROADCAST_ACTION));

        gpsAlertDialog = new SweetAlertDialog(instance, SweetAlertDialog.WARNING_TYPE);
        netAlertDialog = new SweetAlertDialog(instance, SweetAlertDialog.WARNING_TYPE);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
//        navigationView.getMenu().findItem(R.id.nav_activity).setChecked(true);


        navigationView.getMenu().findItem(R.id.nav_logout).setTitle(getString(R.string.nav_become_user));


        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_navigation_drawer_driver);

        driverimage = headerLayout.findViewById(R.id.driverimage);
        driverName = headerLayout.findViewById(R.id.driver_tv);
        driverEmail = headerLayout.findViewById(R.id.header_email_tv);


        //  startService(new Intent(instance, AllTripServices.class));


        dialogManager = new DialogManagerDriver();

        dbUser = new DBAdapter_Driver(instance);

        user = dbUser.getUser();

        if (user != null) {
            driverName.setText(user.getName());
            // email.setText(user.getEmailId());
            try {
                final String url = user.getUserImage();
                Logger.log("ImageUrl", url);
                ImageLoader imageLoader = AppController.getInstance(NavigationDrawerDriver.this).getImageLoader();
                imageLoader.get(url, ImageLoader.getImageListener(driverimage,
                        android.R.drawable
                                .ic_dialog_alert, R.drawable.user));

            } catch (NullPointerException e) {

            }
        }

        //  driverEmail.setText(user.getEmailId());
//        Picasso.with(this)
//                .load(user.getUserImage())
//                .error(R.drawable.ic_action_user)
//                .resize(200, 200)
//                .into(driverimage);
//        data = getIntent().getStringExtra("notificationData");
//        openHomeActivity(data);

        openHomeActivity(data);

        AppPreferencesDriver.setPendingTripId(instance, "");
        if (!AlarmServicesDriver.pendingTrip.isEmpty()) {
            AlarmServicesDriver.pendingTrip.remove(0);
        }
        AppPreferencesDriver.setPendingTrip(instance, null);

        Logger.log("trip_id ripId>>", AppPreferencesDriver.getTripId(NavigationDrawerDriver.this) + ">> Tripstatus:" +
                AppPreferencesDriver.getTripstatusForDriver(NavigationDrawerDriver.this) + ">> PendingTripId::" +
                AppPreferencesDriver.getPendingTripId(NavigationDrawerDriver.this));


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            // super.onBackPressed();

            AlertDialog.Builder builder = new AlertDialog.Builder(NavigationDrawerDriver.this);
            builder.setTitle(getString(R.string.exit));
            builder.setMessage(getString(R.string.exit_message));
            builder.setCancelable(false);
            builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();

                }
            });

            builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer_driver, menu);
        ItemViewList = menu.findItem(R.id.action_list).setVisible(false);
        ItemViewindecate = menu.findItem(R.id.action_indecate);
        ItemViewActivty = menu.findItem(R.id.action_activity);

       /* drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, getResources().getColor(R.color.white));
        menu.findItem(R.id.action_call).setIcon(drawable);*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_list) {

            /*fragment = new MainScreen();
            if (fragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).commit();

                ItemViewList.setVisible(false);
                ItemViewMap.setVisible(true);


            } else {
                // error in creating fragment
                Log.e("MainScreen", "Error in creating fragment");
            }*/

            return true;
        }/* else if (id == R.id.action_map) {
            fragment = new HomeMapActivityDriver();
            if (fragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).commit();

                ItemViewList.setVisible(true);


            } else {
                // error in creating fragment
                Log.e("MainScreen", "Error in creating fragment");
            }
        } */ else if (id == R.id.action_indecate) {
            if (indecateStatus == 200) {
                try {
                    new SweetAlertDialog(instance, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText(indecateMsg)
                            .setContentText("")
                            .setConfirmText("Ok")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                } catch (RuntimeException e) {
                }
            } else {
                try {
                    new SweetAlertDialog(instance, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(indecateMsg)
                            .setConfirmText("Ok")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    if (FusedLocationService.mGoogleApiClient == null) {
                                        //new FusedLocationService(NavigationDrawerDriver.this);
                                        FusedLocationService locationService = FusedLocationService.getInstance(NavigationDrawerDriver.this);
                                        locationService.googleClientReConnect();
                                    } else {
                                        if (!FusedLocationService.mGoogleApiClient.isConnected() || FusedLocationService.mGoogleApiClient.isConnecting()) {
                                            FusedLocationService.mGoogleApiClient.connect();
                                        }
                                    }
                                }
                            })
                            .show();
                } catch (RuntimeException e) {
                }
            }


        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        navigationView.getMenu().findItem(R.id.nav_activity).setChecked(true);

        if (id == R.id.nav_activity) {
            data = null;
            //sendMailAfterFinish(instance);
            //openHomeActivity(data);
            // FirebaseMessaging.getInstance().subscribeToTopic("news");
            // [END subscribe_topics]

        } else if (id == R.id.nav_trip_history) {
            Intent intent = new Intent(instance, MyTripRideActivityDriver.class);
            startActivity(intent);
            overridePendingTransition(R.anim.left_to_right,
                    R.anim.right_to_left);
        } else if (item.getItemId() == R.id.action_history) {
            startActivity(new Intent(instance, HostoryActivity.class));
        } else if (id == R.id.nav_my_profile) {
            Intent intent = new Intent(instance, ProfileActivityDriver.class);
            startActivity(intent);
            overridePendingTransition(R.anim.left_to_right,
                    R.anim.right_to_left);
        } else if (id == R.id.nav_offline) {
            SendLocationDialog("Your GPS or Internet connection is problem plz click on send button for send your current location to server");
        } else if (id == R.id.nav_notification) {
            Intent intent = new Intent(instance, NotificationActivityDriver.class);
            startActivity(intent);
            overridePendingTransition(R.anim.left_to_right,
                    R.anim.right_to_left);
        } else if (id == R.id.nav_logintime) {
            Intent intent = new Intent(instance, LoginTimeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.left_to_right,
                    R.anim.right_to_left);
        } else if (id == R.id.nav_helpdesk) {

            new SendMail().execute(AppPreferencesDriver.getDriverId(NavigationDrawerDriver.this) + "", "", "", "Triplog send ", getTripLogFile());
        } else if (id == R.id.nav_logout) {

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date_time = simpleDateFormat.format(calendar.getTime());

            String[] totalDurationTime = new String[0];

            try {
                String totalDuration = Function.getTotalDateDiffrence(AppPreferencesDriver.getTotalDriverDuration(instance),
                        AppPreferencesDriver.getCurrentDriverDuration(instance));
                totalDurationTime = totalDuration.split(" ");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = new JSONObject();


            if (Function.isConnectingToInternet(instance)) {
                try {
                    jsonObject.put("method", AppConstantDriver.METHOD.SWITCH_ROLE);
                    jsonObject.put("user_id", AppPreferencesDriver.getDriverId(instance));
                    jsonObject.put("login_time", AppPreferencesDriver.getDriverLoginTime(instance));
                    jsonObject.put("logout_time", date_time);
                    jsonObject.put("login_duration", totalDurationTime[1]);
                    jsonObject.put("switch_to", "user");

                    SwitchToUD(jsonObject);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //new SwitchToUD(jsonObject).execute();
            } else {
                dialogManager.showAlertDialog(instance, "Bike4Everything", getResources().getString(R.string.error_check_internet_connection), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
            }


        }
        navigationView.getMenu().findItem(R.id.nav_activity).setChecked(true);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openHomeActivity(String data1) {

        if (data1 != null) {

            try {
                JSONObject object = new JSONObject(data1);
                String trip_id = object.getString("trip_id");
                String message = object.getString("canceltaxirequest");

                AlertDialog.Builder builder = new AlertDialog.Builder(NavigationDrawerDriver.this);
                //builder.setTitle(getString(R.string.cancel_trip));
                //builder.setIcon(R.drawable.ic_launcher);
                LayoutInflater inflater = getLayoutInflater();
                View header = inflater.inflate(R.layout.dialog_heading_driver, null);
                TextView textView = header.findViewById(R.id.text);
                ImageView icon = header.findViewById(R.id.icon);
                icon.setImageResource(R.mipmap.ic_launcher);
                textView.setText(R.string.cancel_trip);
                builder.setCustomTitle(header);
                builder.setCancelable(false);
                builder.setMessage("TripDriver id : " + trip_id + "\nMessage : " + message);

                builder.setPositiveButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                builder.show();

            } catch (JSONException e) {
                e.printStackTrace();
            }

            data = null;
        }

        //fragment = new MainScreen();
        fragment = new HomeMapActivityDriver();
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

        } else {
            // error in creating fragment
            Log.e("MainScreen", "Error in creating fragment");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mBatInfoReceiver);
        LocalBroadcastManager.getInstance(NavigationDrawerDriver.this).unregisterReceiver(broadcastReceiver);
        // AppPreferencesDriver.setShowDialog(instance, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    }

    private void SendLocationDialog(String message) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(instance);
        builder.setCancelable(false);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.all_popup_driver, (ViewGroup) findViewById(R.id.layout_root));

        builder.setView(dialogView);

        ImageView header = dialogView.findViewById(R.id.header_tv);
        TextView et_title = dialogView.findViewById(R.id.et_title);
        final EditText edittext = dialogView.findViewById(R.id.edittext);
        Button btn_submit = dialogView.findViewById(R.id.btn_submit);
        final RadioGroup radiogroup = dialogView.findViewById(R.id.radiogroup);


        radiogroup.setVisibility(View.GONE);
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

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /*[{"method":"trip_log","driverId":75,"tripId":"","latitude":"22.72653","longitude":"75.8797983",
                        "date":"2016-07-23 17:00:42",
                        "device_id":"APA91bErwE1eYjCD0fdEkZXwBUZeiRPvkPSmsfTZR-Mcg_dH5y8iOHWRGHt3kkhrJk6zbBzM7hB8kv-GSJkLAD2kJmmlKvD9Kl3qYNIZw1FU4H9SiZNZC3N9GRJEuk3QCuXhngaMFs92",
                        "type":"1","batterystatus":"76","speed":"","gps":"true","activity":"active","status":"0"}]*/


                String phoneNumber = "09229224424";
                String smsBody = "bike4everything " + "trip_log," + AppPreferencesDriver.getDriverId(instance) + "," + AppPreferencesDriver.getTripId(instance) + "," + FusedLocationService.getLocation().getLatitude() + "," + FusedLocationService.getLocation().getLongitude() + "";

                SmsManager smsManager = SmsManager.getDefault();

                smsManager.sendTextMessage(phoneNumber, null, smsBody, null, null);

                alertDialog.dismiss();


            }
        });

        alertDialog.show();
    }

    public void SwitchToUD(final JSONObject jsonObject) {

        JSONParser jsonParser = new JSONParser(instance);
        jsonParser.parseVollyJSONObject(AppConstantDriver.URL.ONDEMAND_DRIVER_DRIVERPROFILE, 1, jsonObject, "Please wait a few second...", new VolleyCallBack() {
            @Override
            public void success(String response) {
                if (response != null) {
                    AppPreferencesDriver.setWaitApi(instance, false);
                    AppPreferencesDriver.setDriverId(instance, 0);

                    JSONObject jsonObject = null;
                    try {

                        jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (status.equalsIgnoreCase("200")) {

                            AppPreferencesDriver.setTotalDriverDuration(NavigationDrawerDriver.this, "0000-00-00 00:00:00");
                            AppPreferencesDriver.setCurrentDriverDuration(NavigationDrawerDriver.this, "0000-00-00 00:00:00");
                            AppPreferencesDriver.setTimerData(NavigationDrawerDriver.this, "0000-00-00 00:00:00");

                            Intent intent_service = new Intent(getApplicationContext(), WorkTimerService.class);
                            stopService(intent_service);


                            stopService(new Intent(NavigationDrawerDriver.this, FusedLocationService.class));

                            Intent launchIntent = null;
                            try {
                                launchIntent = new Intent(instance, Class.forName("com.b4euser.UserApp.homeScreen.HomeScreen"));
                                launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                launchIntent.setAction("");
                                if (launchIntent != null) {
                                    startActivity(launchIntent);
                                }
                                finish();
                                overridePendingTransition(R.anim.left_to_right,
                                        R.anim.right_to_left);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                                Toast.makeText(NavigationDrawerDriver.this, "B4E User App is not install in your mobile.", Toast.LENGTH_LONG).show();
                            }

                   /* Intent intent = new Intent(instance, NavigationDrawerDriver.class);
                    intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("userType","user");
                    startActivity(intent);*/


                        } else if (status.equalsIgnoreCase("900")) {
                            Intent intent = new Intent(instance, DialogActivityDriver.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction("loginSessionExpire");
                            intent.putExtra("msg", jsonObject.getString("message"));
                            startActivity(intent);
                        } else {
                            dialogManager.showAlertDialog(instance, "Alert", "UnSuccess", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                        }

                        dialogManager.stopProcessDialog();

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

        new SweetAlertDialog(instance, SweetAlertDialog.ERROR_TYPE)
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

    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<DetectedActivity> updatedActivities =
                    intent.getParcelableArrayListExtra(AppConstantDriver.ACTIVITY_EXTRA);
            DetectedActivity myActivity = null;
            int confidence = 80;
            for (DetectedActivity activity : updatedActivities) {
            /*if (activity.getType() == DetectedActivity.RUNNING && activity.getType() == DetectedActivity.WALKING)
                continue;*/

                if (activity.getConfidence() > confidence)
                    myActivity = activity;
            }

            if (myActivity != null) {
               /* Toast.makeText(NavigationDrawerDriver.this, AppConstantDriver.getActivityString(
                        context,
                        myActivity.getType()) + " " + myActivity.getConfidence() + "%", Toast.LENGTH_LONG).show();*/
                if (ItemViewActivty != null) {
                   /* ItemViewActivty.setTitle(AppConstantDriver.getActivityString(
                            context,
                            myActivity.getType()));*/
                }
            }
        }
    }

    private class SendMail extends AsyncTask<String, Integer, Void> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(NavigationDrawerDriver.this, "Please wait", "Sending mail", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }

        protected Void doInBackground(String... params) {
            Mail m = new Mail("hvantageproject1@gmail.com", "hvantage@123");

            String[] toArr = {"hvantageproject1@gmail.com"};
            m.setTo(toArr);
            m.setFrom("hvantageproject1@gmail.com");
            m.setSubject("TRIP LOGS : Request Mail by " + params[0]);
            m.setBody(params[4]);


            try {
                if (m.send()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NavigationDrawerDriver.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NavigationDrawerDriver.this, "Email was not sent.", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            } catch (Exception e) {
                Log.e("MailApp", "Could not send email", e);
            }
            return null;
        }

    }

}