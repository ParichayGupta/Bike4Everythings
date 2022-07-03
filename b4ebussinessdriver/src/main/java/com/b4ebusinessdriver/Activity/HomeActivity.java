package com.b4ebusinessdriver.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.b4ebusinessdriver.Adapter.OrderListAdapter;
import com.b4ebusinessdriver.Database.Database;
import com.b4ebusinessdriver.DistanceCalculate.DriverLocationUpdateService;
import com.b4ebusinessdriver.DistanceCalculate.GpsDistanceCalculator;
import com.b4ebusinessdriver.DistanceCalculate.GpsDistanceTimeUpdater;
import com.b4ebusinessdriver.Model.DropAddress;
import com.b4ebusinessdriver.Model.LatLngs;
import com.b4ebusinessdriver.Model.OrderDetails;
import com.b4ebusinessdriver.Model.ProductImage;
import com.b4ebusinessdriver.R;
import com.b4ebusinessdriver.Reciver.ConnectivityReceiver;
import com.b4ebusinessdriver.Services.LocationService;
import com.b4ebusinessdriver.Utils.AppConstant;
import com.b4ebusinessdriver.Utils.AppPreferences;
import com.b4ebusinessdriver.Utils.DirectionsJSONParser;
import com.b4ebusinessdriver.Utils.Function;
import com.b4ebusinessdriver.Utils.Helper;
import com.b4ebusinessdriver.Utils.ProgressDialog;
import com.b4ebusinessdriver.Utils.Utils;
import com.b4edriver.CommonClasses.Utils.AppConstantDriver;
import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.b4edriver.CommonClasses.Utils.CheckDriverStatus;
import com.b4edriver.DriverApp.HomeMapActivityDriver;
import com.b4elibrary.Logger;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.b4ebusinessdriver.Services.SyncDBService.syncDatabase;
import static com.b4ebusinessdriver.Utils.Function.bitmapDescriptorFromVector;


public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        OrderListAdapter.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener, LocationService.PermisstionCallback {

    private final static int MSG_UPDATE_TIME = 0;
    public static HomeActivity instance = null;
    public Marker markerCurrent;
    public TextView txtName, pickupMobile, pickupAddress, txtAmount;
    public static boolean isStart = true;
    static GoogleMap map;
    private static boolean isMarkerRotating;
    CardView llCustomerDetails;
    CardView fabCallAdmin, fabStart;
    LinearLayout details, expandDrop;
    ImageButton expandImg;
    // FloatingActionsMenu orderListMenu;
    DropAddress activeOrderDetail;
    FloatingActionButton activeActionButton;
    FloatingActionButton fabMyLocation;
    List<FloatingActionButton> actionButtonList;
    List<DropAddress> orderDetailList;
    GpsDistanceCalculator gpsDistanceCalculator;
    LatLng pickupLatLng;
    List<ProductImage> productImage;
    OrderListAdapter orderListAdapter;
    View.OnClickListener expandDetails = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            details.setVisibility(details.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            expandImg.setBackgroundResource(details.getVisibility() == View.VISIBLE ? R.drawable.ic_keyboard_arrow_up_black_24dp :
                    R.drawable.ic_keyboard_arrow_down_black_24dp);

        }
    };
    View.OnClickListener startListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            fabStart.setEnabled(false);
            new StartTrips().execute();
        }
    };
    List<String> waypoints = new ArrayList<>();
    private RecyclerView recyclerView;
    private int mSelectedStyleId = R.string.style_label_default;
    private int mStyleIds[] = {
            R.string.style_label_normal,
            R.string.style_label_satellite,
            R.string.style_label_retro,
            R.string.style_label_night,
            R.string.style_label_grayscale,
            R.string.style_label_no_pois_no_transit,
            R.string.style_label_default,
    };

    private String TAG = "HomeActivity";

    View.OnClickListener setMyLocation = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (new Double(AppPreferences.getCurLat(HomeActivity.this)).doubleValue() != 0.0) {
                zoomIn(new LatLng(new Double(AppPreferences.getCurLat(HomeActivity.this)).doubleValue()
                        , new Double(AppPreferences.getCurLong(HomeActivity.this)).doubleValue()));
            }
        }
    };

    View.OnClickListener callAdminListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

          /*  LatLng latLng = latlng[new Random().nextInt(latlng.length)];

             String getneworder = "{\"shipping_street\":\"indore\",\"total_amt\":\"130\",\"up_cust_longitude\":\"0\",\"shipLong\":"+latLng.longitude+",\"shipLat\":"+latLng.latitude+",\"up_cust_latitude\":\"0\",\"cust_name\":\"manish\",\"cust_contact\":\"8269262610\",\"customer_id\":\"709\",\"order_id\":\"735\",\"shipping_locality\":\"Bicholi Mardana, Indore, Madhya Pradesh, India\"}";//"" + remoteMessage.getData().get(Config.GETNEWORDER);
             OrderDetail orderDetail = new Gson().fromJson(getneworder, OrderDetail.class);
            Intent intent = new Intent("getneworder");
             intent.putExtra("orderDetail", orderDetail);
            LocalBroadcastManager.getInstance(HomeActivity.this).sendBroadcast(intent);*/

            String number = ("tel:9755299999");
            Intent mIntent = new Intent(Intent.ACTION_CALL);
            mIntent.setData(Uri.parse(number));
            if (ContextCompat.checkSelfPermission(HomeActivity.this,
                    Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(HomeActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        0);

            } else {
                //You already have permission
                try {
                    startActivity(mIntent);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }

        }
    };




    @Override
    public void locationSettingsResult(PendingResult<LocationSettingsResult> result) {

        Log.e(TAG, " locationSettingsResult");
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result
                        .getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location
                        // requests here.
                        // setUpMap();

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {
                            // Show the dialog by calling
                            // startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(HomeActivity.this, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }

                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // mHandler.postDelayed(goToNextScreen, DELAY_MILLIS);

                        break;

                }
            }
        });

    }



    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e(TAG, " inside locationReceiver");

            int resultCode = intent.getIntExtra(Helper.INTENT_EXTRA_RESULT_CODE, RESULT_CANCELED);

            if (resultCode == RESULT_OK) {
                // Toast.makeText(HomeActivity.this,"new marker",Toast.LENGTH_SHORT).show();

                Location userLocation = intent.getParcelableExtra(Helper.INTENT_USER_LAT_LNG);

                /*txtName.setText("Accuracy:"+userLocation.getAccuracy()
                        +"\nSpeed:"+userLocation.getSpeed());*/

                Location userPreLocation = intent.getParcelableExtra(Helper.INTENT_USER_PRE_LAT_LNG);
                if (userPreLocation == null)
                    return;
                Log.e(TAG, " locationReceiver " + userLocation.getLatitude());


                if (markerCurrent != null) {
                    LatLng latLng = getLatLng(userLocation);
                    LatLng prelatLng = new LatLng(markerCurrent.getPosition().latitude, markerCurrent.getPosition().longitude);


                    double heading = SphericalUtil.computeHeading(prelatLng, latLng);

                    rotateMarker(markerCurrent, (float) heading, latLng);
                }

                // updateUserMarkerLocation(latLng);
            }
        }
    };
    private BroadcastReceiver mCancelReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent

            final String orderDetail = intent.getStringExtra("orderDetail");
            // OrderDetail orderDetail = new Gson().fromJson(getneworder, OrderDetail.class);
            Logger.log("orderDetail::", "\n" + orderDetail.toString());

            new AlertDialog.Builder(HomeActivity.this)
                    .setCancelable(false)
                    .setTitle("Cancel")
                    .setMessage("Order id #" + orderDetail + " is cancelled ")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            cancelOrder(orderDetail);
                        }
                    }).show();


        }
    };

    private BroadcastReceiver checkcondition = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent

            final float distanceDiff = intent.getFloatExtra("distanceDiff", 0f);
            final int timediffer = intent.getIntExtra("timediffer", 0);
            final int tA = intent.getIntExtra("tA", 0);
            final float Accuracy = intent.getFloatExtra("Accuracy", 0f);
            final int countCheck = intent.getIntExtra("countCheck", 0);
            final float speed = intent.getFloatExtra("speed", 0);
            boolean cond = tA > 0 && tA >= distanceDiff;
            /*txtName.setText("distanceDiff: "+distanceDiff
                    +"\ntimedifferInSec: "+timediffer
                    +"\ntime * 33: "+tA
                    +"\nAccuracy: "+Accuracy
                    +"\ncondition: "+cond
                    +"\ncountCheck: "+countCheck
                    +"\nspeed: "+speed
            );*/

        }
    };

    public void rotateMarker(final Marker marker, final float toRotation, final LatLng toPosition) {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = marker.getRotation();
        final long duration = 1555;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                isMarkerRotating = true;

                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * toRotation + (1 - t) * startRotation;

                float bearing = -rot > 180 ? rot / 2 : rot;

                marker.setRotation(bearing);


                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    animateMarker(marker, toPosition);
                }
            }
        });

    }

    public void animateMarker(final Marker marker, final LatLng toPosition) {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = map.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 5000;

        final LinearInterpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * toPosition.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));


                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {

                }


            }
        });
    }





    @SuppressLint("RestrictedApi")
    private void startOrderDetailActivity(DropAddress orderDetail) {
        Intent intent = new Intent(HomeActivity.this, OrderDetailActivity.class);
        intent.putExtra("orderDetail", orderDetail);
        intent.putParcelableArrayListExtra("productImage", (ArrayList<? extends Parcelable>) productImage);
        intent.putExtra("lat", pickupLatLng.latitude);
        intent.putExtra("lng", pickupLatLng.longitude);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityOptions options =
                    ActivityOptions.makeCustomAnimation(HomeActivity.this, R.anim.slide_down, R.anim.slide_down);
            startActivityForResult(intent, 1, options.toBundle());
        } else {
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.e(TAG, " onCreate");
        instance = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AppPreferencesDriver.setDriverType(HomeActivity.this, AppConstantDriver.DRIVER_B2B);
        Logger.log("delivery_id home", AppPreferences.getDeliveryId(HomeActivity.this) + "");
        details = findViewById(R.id.details);
        expandDrop = findViewById(R.id.expandDrop);
        expandImg = findViewById(R.id.expandImg);
        fabCallAdmin = findViewById(R.id.fabCallAdmin);
        fabStart = findViewById(R.id.fabStart);
        llCustomerDetails = findViewById(R.id.llCustomerDetails);
        txtName = findViewById(R.id.txtName);
        txtAmount = findViewById(R.id.txtAmount);
        pickupMobile = findViewById(R.id.pickupMobile);
        pickupAddress = findViewById(R.id.pickupAddress);
        // orderListMenu = findViewById(R.id.multiple_actions);
        recyclerView = findViewById(R.id.recycler_view);
        fabMyLocation = findViewById(R.id.fabMyLocation);

        final NavigationView navigationView = findViewById(R.id.nav_view);
        expandImg.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);

        expandImg.setOnClickListener(expandDetails);
        expandDrop.setOnClickListener(expandDetails);

        actionButtonList = new ArrayList<>();
        orderDetailList = new ArrayList<>();

        fabCallAdmin.setOnClickListener(callAdminListener);
        fabStart.setOnClickListener(startListener);
        fabMyLocation.setOnClickListener(setMyLocation);


        fabMyLocation.setEnabled(false);

        checkLocationPermission();
        // new MyService(this);
        syncDatabase(HomeActivity.this, new String[]{AppConstant.GET_RATECARD});

        orderListAdapter = new OrderListAdapter(HomeActivity.this, orderDetailList);
        orderListAdapter.setListener(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(orderListAdapter);


        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        testNotification();


        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        View hView = navigationView.getHeaderView(0);
        TextView textViewUsername = hView.findViewById(R.id.textViewUserName);
        TextView textViewMobile = hView.findViewById(R.id.textViewMobileNo);
        final ImageView imageView = hView.findViewById(R.id.imageViewProfile);

        hView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            }
        });


        if (!AppPreferences.getUsername(HomeActivity.this).equalsIgnoreCase(""))
            textViewUsername.setText(AppPreferences.getUsername(HomeActivity.this));
        else
            textViewUsername.setText("NA");

        if (!AppPreferences.getMobileNo(HomeActivity.this).equalsIgnoreCase(""))
            textViewMobile.setText(AppPreferences.getMobileNo(HomeActivity.this));
        else
            textViewMobile.setText("NA");

        if (!AppPreferences.getProfilePic(HomeActivity.this).equalsIgnoreCase("")) {
            Glide.with(HomeActivity.this)
                    .asBitmap()
                    .load(AppPreferences.getProfilePic(HomeActivity.this))
                    .apply(new RequestOptions().centerCrop())
                    .into(new BitmapImageViewTarget(imageView) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    imageView.setImageDrawable(circularBitmapDrawable);
                }
            });
        }


        IntentFilter intentFilter = new IntentFilter(Helper.ACTION_NAME_SPACE);
        LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver, intentFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mCancelReceiver,
                new IntentFilter("cancelTrip"));
        LocalBroadcastManager.getInstance(this).registerReceiver(checkcondition,
                new IntentFilter("checkcondition"));



        if (!Function.isServiceRunning(HomeActivity.this, DriverLocationUpdateService.class.getName())) {
            Logger.log(this.TAG, "onReceive startDriverService called");
            startService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
        }

        //if (isServiceRunning(LocationService.class.getName()))
            setUpMap();


        gpsDistanceCalculator =  GpsDistanceCalculator.getInstance(HomeActivity.this, getGpsDistanceTimeUpdater(instance), GpsDistanceCalculator.getTotalDistanceFromSP(instance), GpsDistanceCalculator.getLastLocationTimeFromSP(instance), GpsDistanceCalculator.getTotalHaversineDistanceFromSP(instance));


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

    private void setUpMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(HomeActivity.this);
    }


    private void updateUi(DropAddress orderDetail, FloatingActionButton childAt) {

        activeOrderDetail = orderDetail;
        activeActionButton = childAt;

        Animation slide_down = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.slide_down);


        llCustomerDetails.setVisibility(View.VISIBLE);
        //  llCustomerDetails.startAnimation(slide_down);
        //txtName.setText("Total order : "+orderDetailList.size());

        if (isStart) {
            fabStart.setVisibility(View.GONE);
        } else {
            fabStart.setVisibility(View.VISIBLE);
        }


    }

    private void testNotification() {

    }


    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
        }
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 10);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, " onStart");

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            exitAlert();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    private String secondsToString(int improperSeconds) {

        //Seconds must be fewer than are in a day

        Time secConverter = new Time();

        secConverter.hour = 0;
        secConverter.minute = 0;
        secConverter.second = 0;

        secConverter.second = improperSeconds;
        secConverter.normalize(true);

        String hours = String.valueOf(secConverter.hour);
        String minutes = String.valueOf(secConverter.minute);
        String seconds = String.valueOf(secConverter.second);

        if (seconds.length() < 2) {
            seconds = "0" + seconds;
        }
        if (minutes.length() < 2) {
            minutes = "0" + minutes;
        }
        if (hours.length() < 2) {
            hours = "0" + hours;
        }

        String timeString = hours + ":" + minutes + ":" + seconds;
        return timeString;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private void showStylesDialog() {
        // mStyleIds stores each style's resource ID, and we extract the names here, rather
        // than using an XML array resource which AlertDialog.Builder.setItems() can also
        // accept. We do this since using an array resource would mean we would not have
        // constant values we can switch/case on, when choosing which style to apply.
        List<String> styleNames = new ArrayList<>();
        for (int style : mStyleIds) {
            styleNames.add(getString(style));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.style_choose));
        builder.setItems(styleNames.toArray(new CharSequence[styleNames.size()]),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSelectedStyleId = mStyleIds[which];
                        String msg = getString(R.string.style_set_to, getString(mSelectedStyleId));

                        Logger.log(TAG, msg);
                        setSelectedStyle();
                    }
                });
        builder.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityOptions options =
                        ActivityOptions.makeCustomAnimation(HomeActivity.this, R.anim.slide_down, R.anim.slide_down);
                startActivity(intent, options.toBundle());
            } else {
                startActivity(intent);
            }
            finish();
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        } else if (id == R.id.nav_deliveries) {
            startActivity(new Intent(HomeActivity.this, DeliveriesActivity.class));
        } else if (id == R.id.nav_logout) {
            logoutAlert();
        } else if (id == R.id.nav_mapstype) {
            showStylesDialog();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {

        AppPreferences.setUserId(HomeActivity.this, "");
        AppPreferences.setMobileNo(HomeActivity.this, "");
        AppPreferences.setProfilePic(HomeActivity.this, "");
        AppPreferences.setUsername(HomeActivity.this, "");
        AppPreferencesDriver.setDriverId(HomeActivity.this, 0);

        Intent intent = new Intent(HomeActivity.this, SigninActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onMapReady(final GoogleMap maps) {
        map = maps;

        zoomIn(new LatLng(new Double(AppPreferences.getCurLat(HomeActivity.this)).doubleValue(), new Double(AppPreferences.getCurLong(HomeActivity.this)).doubleValue()));
        Logger.log(TAG, "onMapReady");

        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Logger.log("checkSelfPermission", false+"");
                    return;
                }
                map.setMyLocationEnabled(true);
                updateUserMapLocation();

                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                fabMyLocation.setEnabled(true);

                setSelectedStyle();
                //route();

                new PendingTrip().execute();
                CheckDriverStatus.getInstance().checkStatus(HomeActivity.this);

                /*List<LatLngs> latLngsList = databaseHandler.getAllDistance();

                PolylineOptions lineOptions = new PolylineOptions();
                for(int i=0; i<latLngsList.size(); i++){
                    LatLngs latLngs = latLngsList.get(i);
                    if(latLngs.getCurrentLat() == 0.0){

                    }else {
                        lineOptions.add(new LatLng(latLngs.getCurrentLat(), latLngs.getCurrentLng()));
                        Logger.log("latLngsList", latLngs.getCurrentLat() + "," + latLngs.getCurrentLng());
                    }
                }
                lineOptions.width(8);
                lineOptions.color(Color.BLUE);
                map.addPolyline(lineOptions);
*/

            }
        });

    }


    /*private void setCurrentLocationMarker() {

        if(currentLatLng == null)
            return;

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 13));
        Logger.log("onMapReady", currentLatLng.latitude + "::" + currentLatLng.longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        //markerOptions.icon(bitmapDescriptorFromVector(HomeActivity.this,R.drawable.ic_directions_bike_black_24dp));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bike));
        markerOptions.anchor(0.5f,0.5f);
        markerCurrent = map.addMarker(markerOptions);

    }*/

    private void updateUserMapLocation() {

        LatLng latLng = new LatLng(new Double(AppPreferences.getCurLat(HomeActivity.this)).doubleValue(), new Double(AppPreferences.getCurLong(HomeActivity.this)).doubleValue());
        zoomIn(latLng);
        initializeLocationMarker(latLng);
    }

    private void initializeLocationMarker(LatLng latLngMarker) {
        MarkerOptions options = new MarkerOptions()
                .flat(true)
                .position(latLngMarker)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.navigation))
                .anchor(0.5f,0.5f);
        markerCurrent = map.addMarker(options);
    }

    private void zoomIn(LatLng latLngZoom) {

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngZoom, 18));
    }
    private void setSelectedStyle() {
        MapStyleOptions style;
        switch (mSelectedStyleId) {
            case R.string.style_label_normal:
                // Sets the retro style via raw resource JSON.
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.string.style_label_satellite:
                // Sets the retro style via raw resource JSON.
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.string.style_label_retro:
                // Sets the retro style via raw resource JSON.
                style = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_retro);
                map.setMapStyle(style);
                break;
            case R.string.style_label_night:
                // Sets the night style via raw resource JSON.
                style = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_night);
                map.setMapStyle(style);
                break;
            case R.string.style_label_grayscale:
                // Sets the grayscale style via raw resource JSON.
                style = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_grayscale);
                map.setMapStyle(style);
                break;
            case R.string.style_label_no_pois_no_transit:
                // Sets the no POIs or transit style via JSON string.
                style = new MapStyleOptions("[" +
                        "  {" +
                        "    \"featureType\":\"poi.business\"," +
                        "    \"elementType\":\"all\"," +
                        "    \"stylers\":[" +
                        "      {" +
                        "        \"visibility\":\"off\"" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  {" +
                        "    \"featureType\":\"transit\"," +
                        "    \"elementType\":\"all\"," +
                        "    \"stylers\":[" +
                        "      {" +
                        "        \"visibility\":\"off\"" +
                        "      }" +
                        "    ]" +
                        "  }" +
                        "]");
                map.setMapStyle(style);
                break;
            case R.string.style_label_default:
                // Removes previously set style, by setting it to null.
                style = null;
                map.setMapStyle(style);
                break;
            default:
                return;
        }
    }

    private void exitAlert() {

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(HomeActivity.this);
        } else {
            builder = new AlertDialog.Builder(HomeActivity.this);
        }
        builder.setTitle("Bike4Everything Business")
                .setMessage("Do you want to exit")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(R.mipmap.ic_launcher)
                .show();
    }

    private void logoutAlert() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(HomeActivity.this);
        } else {
            builder = new AlertDialog.Builder(HomeActivity.this);
        }
        builder.setTitle("B4E Business Driver")
                .setMessage("Do you want to logout")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        logout();

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(R.mipmap.ic_launcher)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();

        if (isStart) {
            fabStart.setVisibility(View.GONE);
        } else {
            fabStart.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mCancelReceiver);

    }

    @Override
    public void clickItem(DropAddress orderDetail) {
        startOrderDetailActivity(orderDetail);
    }

    @Override
    public void callCustomer(String number) {

        Intent mIntent = new Intent(Intent.ACTION_CALL);
        mIntent.setData(Uri.parse(number));
        if (ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    0);

        } else {
            //You already have permission
            try {
                startActivity(mIntent);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isShowingNetworkDialog()) {
            dismissNetworkDialog();
        }
        if (!isConnected) {
            showNetworkDialog(HomeActivity.this);
        }
    }

    private void setupTripUi(final OrderDetails orderDetail) {

        Logger.log("orderDetail::", "\n" + new Gson().toJson(orderDetail));

        getSupportActionBar().hide();


      //  orderListMenu.setVisibility(View.VISIBLE);

        for (int i = 0; i < actionButtonList.size(); i++) {
        //    orderListMenu.removeButton(actionButtonList.get(i));
        }
        actionButtonList.clear();

            /* if (activeOrderDetail == null) {
                updateUi(orderDetail.getDropAddressList().get(0),newOrder);
            }*/


        AppPreferences.setDeliveryId(HomeActivity.this, orderDetail.getDeliveryId());
        AppPreferencesDriver.setTripId(HomeActivity.this, String.valueOf(orderDetail.getDeliveryId()));
        orderDetailList = orderDetail.getDropAddressList();


        pickupLatLng = new LatLng(orderDetail.getPickLat(), orderDetail.getPickLng());
        LatLng dropLatLng = new LatLng(orderDetail.getPickLat(), orderDetail.getPickLng());


        txtName.setText("" + orderDetail.getPickName());
        pickupMobile.setText("" + orderDetail.getPickMobile());
        pickupAddress.setText("" + orderDetail.getPickAddress());
        txtAmount.setText("₹ "+orderDetail.getEstTotalAmount());
        for (int i = 0; i < orderDetail.getDropAddressList().size(); i++) {

            final FloatingActionButton newOrder = new FloatingActionButton(getBaseContext());
            newOrder.setId(i);
            newOrder.setVisibility(View.GONE);
            newOrder.setIcon(R.drawable.ic_pin_drop_black_24dp);
            newOrder.setColorNormal(Color.WHITE);
            newOrder.setColorPressed(Color.GREEN);
            newOrder.setTitle(orderDetail.getDropAddressList().get(i).getDropName());
            final int finalI = i;
            newOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  //  orderListMenu.collapseImmediately();
                    updateUi(orderDetail.getDropAddressList().get(finalI), newOrder);
                    // actionB.setVisibility(actionB.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                    startOrderDetailActivity(orderDetail.getDropAddressList().get(finalI));
                }
            });
            //orderListMenu.addButton(newOrder);
            if (i != orderDetail.getDropAddressList().size() - 1) {
                waypoints.add(orderDetail.getDropAddressList().get(finalI).getDropLat() + "," + orderDetail.getDropAddressList().get(finalI).getDropLng());
            }
            dropLatLng = new LatLng(orderDetail.getDropAddressList().get(finalI).getDropLat(), orderDetail.getDropAddressList().get(finalI).getDropLng());
            actionButtonList.add(newOrder);
            if (i == 0) {
                updateUi(orderDetail.getDropAddressList().get(i), newOrder);
            }

            MarkerOptions options2 = new MarkerOptions();
            options2.position(new LatLng(orderDetail.getDropAddressList().get(finalI).getDropLat(), orderDetail.getDropAddressList().get(finalI).getDropLng()));
            options2.icon(BitmapDescriptorFactory.fromBitmap(createStoreMarker(i + 1)));
            map.addMarker(options2);
        }

        // Pickup marker
        MarkerOptions options = new MarkerOptions();
        options.position(pickupLatLng);
        options.icon(bitmapDescriptorFromVector(HomeActivity.this, R.drawable.ic_person_pin_circle_black_24dp));
        map.addMarker(options);


        String url = Function.getDirectionsUrlWaypont(pickupLatLng, dropLatLng, waypoints);
        // Logger.log("routes", url);
        RoutesDownloadTask downloadTask = new RoutesDownloadTask(instance);
        downloadTask.execute(url);

        // orderListAdapter.notifyDataSetChanged();

        orderListAdapter = new OrderListAdapter(HomeActivity.this, orderDetailList);
        orderListAdapter.setListener(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(orderListAdapter);

       // Logger.log("orderLisildCount", orderListMenu.getChildCount() + "");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                complete();
            }else if(requestCode == 1000){

                setUpMap();
            }
        }
    }

    public void complete() {
      //  orderListMenu.removeButton(activeActionButton);
        boolean remo = orderDetailList.remove(activeOrderDetail);
        boolean remo1 = actionButtonList.remove(activeActionButton);
        orderListAdapter.notifyDataSetChanged();

        Logger.log("isremove", remo + "\n" + remo1 + "\n" + orderDetailList.toString() + "\n" + activeOrderDetail.toString() + "\n" );

        if (orderDetailList.isEmpty()) {
          //  orderListMenu.setVisibility(View.GONE);
            llCustomerDetails.setVisibility(View.GONE);
            activeOrderDetail = null;
            map.clear();
            getSupportActionBar().show();

            //new FinishTrip().execute(AppConstant.METHOD.END_ORDER, "finish");
            AppPreferences.setTotalDistance(HomeActivity.this, Database.getInstance(instance).getTotalDistance()+"");
            gpsDistanceCalculator.stop();
            startActivity(new Intent(HomeActivity.this, EndTripActivity.class));
        } else {
            AppPreferences.setStartTime(HomeActivity.this, Calendar.getInstance().getTimeInMillis());

            if (!orderDetailList.isEmpty()) {
                updateUi(orderDetailList.get(0), actionButtonList.get(0));

            } else {
               // orderListMenu.setVisibility(View.GONE);
                llCustomerDetails.setVisibility(View.GONE);
                map.clear();

                getSupportActionBar().show();
            }
        }
    }



    public void cancelOrder(String orderId) {
        /*for(int i=0; i<orderListMenu.getChildCount(); i++){
            try {
                FloatingActionButton floatingActionButton = (FloatingActionButton) orderListMenu.getChildAt(i);
                if (floatingActionButton.getId() == Integer.parseInt(orderId)) {
                    orderListMenu.removeButton(floatingActionButton);
                    boolean remo1 = actionButtonList.remove(floatingActionButton);
                }
            }catch (ClassCastException e){}
        }

        for(int i=0; i<orderDetailList.size(); i++){
            if(orderDetailList.get(i).getOrderId().equalsIgnoreCase(orderId)){
                orderDetailList.remove(i);

                orderListAdapter.notifyDataSetChanged();

                Logger.log("isremove", "\n"+"\n"+orderDetailList.toString()+"\n"+ activeOrderDetail.toString()+"\n"+orderListMenu.getChildCount());

            }
        }*/

       // orderListMenu.removeAllViews();
        orderDetailList = new ArrayList<>();
        orderListAdapter.notifyDataSetChanged();

        if (orderDetailList.isEmpty()) {
          //  orderListMenu.setVisibility(View.GONE);
            llCustomerDetails.setVisibility(View.GONE);
            map.clear();

            getSupportActionBar().show();
        } else {
            if (!orderDetailList.isEmpty()) {
                updateUi(orderDetailList.get(0), actionButtonList.get(0));

            } else {
               // orderListMenu.setVisibility(View.GONE);
                llCustomerDetails.setVisibility(View.GONE);
                map.clear();

                getSupportActionBar().show();
            }
        }
    }

    public int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    private Bitmap createStoreMarker(int number) {
        View markerLayout = getLayoutInflater().inflate(R.layout.custom_marker, null);

        ImageView markerImage = markerLayout.findViewById(R.id.marker_image);
        TextView markerRating = markerLayout.findViewById(R.id.marker_text);
        markerRating.setText(number + "");

        markerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        markerLayout.layout(0, 0, markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight());

        final Bitmap bitmap = Bitmap.createBitmap(markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerLayout.draw(canvas);
        return bitmap;
    }

    private class PendingTrip extends AsyncTask<String, Void, String> {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        public PendingTrip() {
            try {
                ProgressDialog.getInstance(HomeActivity.this).show();
            }catch (Exception e){}
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject data = new JSONObject();
                data.put("method", AppConstant.GET_ALLTRIP);
                data.put("driver_id", AppPreferences.getUserId(HomeActivity.this));
                data.put("fcm_token_id", FirebaseInstanceId.getInstance().getToken());

                Log.e("Response_Response", AppConstant.B4E_DRIVER_BUSINESS_ALL_DELIVERY + "\n" + data.toString());
                //json.put("notification", dataJson);
                RequestBody body = RequestBody.create(JSON, data.toString());
                Request request = new Request.Builder()
                        .url(AppConstant.B4E_DRIVER_BUSINESS_ALL_DELIVERY)
                        .post(body)
                        .build();
                okhttp3.Response response = client.newCall(request).execute();
                result = response.body().string();


            } catch (JSONException e) {
                e.printStackTrace();
                FirebaseCrash.report(e);
            } catch (IOException e) {
                e.printStackTrace();
                FirebaseCrash.report(e);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String finalResponse) {
            super.onPostExecute(finalResponse);
            Log.e("Response_Response", AppConstant.B4E_DRIVER_BUSINESS_ALL_DELIVERY + "\n" + finalResponse);
            String s = finalResponse;
            try {
                JSONObject jsonObject = new JSONObject(s);

                if (jsonObject.getString("status").equalsIgnoreCase("200")) {

//                    isStart = jsonObject.getBoolean("isStart");

                    JSONArray array = jsonObject.getJSONArray("result");
                    for (int i = 0; i < array.length(); i++) {
                        OrderDetails orderDetail = new Gson().fromJson(array.getJSONObject(i).toString(), OrderDetails.class);
                        isStart = orderDetail.isStart();
                        productImage = orderDetail.getProductImage();
                        if(isStart) {
                            gpsDistanceCalculator.saveState();
                            gpsDistanceCalculator.start();
                            markerCurrent.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.navigation));
                        }else{
                            gpsDistanceCalculator.stop();
                            markerCurrent.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bike));
                        }
                            AppPreferences.setOntrip(HomeActivity.this, isStart);

                        setupTripUi(orderDetail);

                        databaseHandler.addOrder(orderDetail);

                        /*Intent intent = new Intent("getneworder");
                        intent.putExtra("orderDetail", orderDetail);
                        LocalBroadcastManager.getInstance(HomeActivity.this).sendBroadcast(intent);*/
                    }

                } else {
                    AppPreferences.setDeliveryId(HomeActivity.this, 0);
                    AppPreferencesDriver.setTripId(HomeActivity.this,"");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                ProgressDialog.getInstance(HomeActivity.this).dismiss();
            }catch (Exception e){}

        }
    }

    private class StartTrips extends AsyncTask<String, Void, String> {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        public StartTrips() {
            //ProgressDialog.getInstance(HomeActivity.this).show();
            Log.e("Response_Response", "Start");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                Log.e("Response_Response", "Start 111");
                OkHttpClient client = new OkHttpClient();
                JSONObject data = new JSONObject();
                data.put("method", AppConstant.START_DELIVERY);
                data.put("drop_id", orderDetailList.get(0).getDropId());
                data.put("driver_id", AppPreferences.getUserId(HomeActivity.this));
                data.put("delivery_id", AppPreferences.getDeliveryId(HomeActivity.this));

                Log.e("Response_Response", data.toString());
                //json.put("notification", dataJson);
                RequestBody body = RequestBody.create(JSON, data.toString());
                Request request = new Request.Builder()
                        .url(AppConstant.B4E_DRIVER_BUSINESS_MANAGE_DELIVERY)
                        .post(body)
                        .build();
                okhttp3.Response response = client.newCall(request).execute();
                result = response.body().string();


            } catch (JSONException e) {
                e.printStackTrace();
                FirebaseCrash.report(e);
            } catch (IOException e) {
                e.printStackTrace();
                FirebaseCrash.report(e);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String finalResponse) {
            super.onPostExecute(finalResponse);
            Log.e("Response_Response", finalResponse);
            String s = finalResponse;
            try {
                JSONObject jsonObject = new JSONObject(s);

                if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                    if (!orderDetailList.isEmpty()) {
                        AppPreferences.setOntrip(HomeActivity.this, true);

                        /*LatLngs latLngs = new LatLngs();
                        latLngs.setCurrentLat(mCurrentLocation.getLatitude());
                        latLngs.setCurrentLng(mCurrentLocation.getLongitude());
                        latLngs.setStartLat(mCurrentLocation.getLatitude());
                        latLngs.setStartLng(mCurrentLocation.getLongitude());
                        new DatabaseHandler(HomeActivity.this).addLatLng(latLngs);*/
                        updateUi(orderDetailList.get(0), actionButtonList.get(0));
                        startOrderDetailActivity(orderDetailList.get(0));
                        markerCurrent.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.navigation));
                    }
                    isStart = true;
                    fabStart.setVisibility(View.GONE);
                    gpsDistanceCalculator.start();
                } else if (jsonObject.getString("status").equals("400")) {
                    showAlertDialog(HomeActivity.this, "Error!",
                            jsonObject.getJSONArray("result").getJSONObject(0).getString("msg")
                            , "OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AppPreferences.setDeliveryId(HomeActivity.this, 0);
                                    AppPreferencesDriver.setTripId(HomeActivity.this,"");
                                    isStart = false;
                                  //  orderListMenu.removeButton(activeActionButton);
                                    orderDetailList.remove(activeOrderDetail);
                                    actionButtonList.remove(activeActionButton);
                                    orderListAdapter.notifyDataSetChanged();

                                    fabStart.setVisibility(View.GONE);
                                   // orderListMenu.setVisibility(View.GONE);
                                    llCustomerDetails.setVisibility(View.GONE);
                                    activeOrderDetail = null;
                                    map.clear();

                                    getSupportActionBar().show();
                                   // ProgressDialog.getInstance(HomeActivity.this).dismiss();
                                }
                            });
                }

            } catch (JSONException e) {
                e.printStackTrace();
                FirebaseCrash.report(e);
            }
           //ProgressDialog.getInstance(HomeActivity.this).dismiss();
            fabStart.setEnabled(true);
        }
    }

    public class RoutesDownloadTask extends AsyncTask<String, Void, String> {

        Context context;
        String distanceTime;

        public RoutesDownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Logger.log("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            parserTask.execute(result);
        }

        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.connect();

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
                Logger.log("routes", "111");

                try {

                    jObject = new JSONObject(jsonData[0]);
                    DirectionsJSONParser parser = new DirectionsJSONParser();

                    // Starts parsing data
                    routes = parser.parse(jObject);
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (OutOfMemoryError e) {
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
                String duration = "1";
                String durationValue = "1";
                String distanceValue = "0";

                try {
                    if (result.size() < 1) {
                        //Toast.makeText(ParserTask.this, "No Points", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NullPointerException e) {
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
                                distanceValue = point.get("distance");
                                continue;
                            } else if (j == 1) { // Get duration from the list
                                duration = point.get("duration");
                                durationValue = point.get("value");
                                Logger.log("duration ss", duration + "::" + durationValue);
                                continue;
                            } else if (j == 2) { // Get duration from the list

                                continue;
                            }

                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);
                            //Logger.log("routes",position.toString());
                            points.add(position);
                            LatLng mapPoint =
                                    new LatLng(lat, lng);
                            builder.include(mapPoint);

                        }
                        lineOptions.addAll(points);

                        lineOptions.width(8);

                        lineOptions.color(getRandomColor());
                        lineOptions.geodesic(true);

                        // if(polylineFinal.isGeodesic())
                        //  polylineFinal.remove();
                        map.addPolyline(lineOptions);
                        //mMap.setPadding(0, measuredHeight/2, 0, 0);

                        map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50));

                        // mMap.setPadding(0, cardView.getHeight(), 0, 0);
                    }
                }


                long dur = Long.parseLong(durationValue) * 1000;
                int secondsLeft = (int) Math.round((dur / (double) 1000));
                //deliveryTime = (secondsToString(secondsLeft));

                AppPreferences.setStartTime(HomeActivity.this, Calendar.getInstance().getTimeInMillis());

                Logger.log("duration total", dur + "");
                // downTimer(dur, 1000);
            }
        }

    }


    /*>>>>>>>>>>>>>>>*/

    public static void showMove(Location from, Location to){
//        Toast.makeText(instance, hasMoved(from,to)+"",Toast.LENGTH_LONG).show();
    }

    private static boolean hasMoved(Location from, Location to) {
        if (to == null) {
            return false;
        }

        if (from == null) {
            return true;
        }

        // if new location is older than the current one, the device hasn't moved.
        if (to.getTime() < from.getTime()) {
            return false;
        }

        // Get the distance between the two points.
        float distance = from.distanceTo(to);

        // Get the total accuracy radius for both locations.
        float totalAccuracy = from.getAccuracy() + to.getAccuracy();

        // If the distance is greater than the combined accuracy of the two
        // points then they can't overlap and hence the user has moved.
        return distance >= totalAccuracy;
    }

}


