package com.b4edriver.DriverApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.b4edriver.CommonClasses.ServerConnection.JSONParser;
import com.b4edriver.CommonClasses.ServerConnection.ServerErrorCallBack;
import com.b4edriver.CommonClasses.ServerConnection.VolleyCallBack;
import com.b4edriver.CommonClasses.Services.CreateRoutesServices;
import com.b4edriver.CommonClasses.Services.FusedLocationService;
import com.b4edriver.CommonClasses.Services.WorkTimerService;
import com.b4edriver.CommonClasses.Utils.AppConstantDriver;
import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.b4edriver.CommonClasses.Utils.DirectionsJSONParser;
import com.b4edriver.CommonClasses.Utils.DriverStatus;
import com.b4edriver.CommonClasses.Utils.Function;
import com.b4edriver.Database.DBAdapter_Driver;
import com.b4edriver.Database.Database;
import com.b4edriver.Model.DistanceTravelled;
import com.b4edriver.Model.GoogleAddress;
import com.b4edriver.Model.TripDriver;
import com.b4edriver.Model.UserDriver;
import com.b4edriver.R;
import com.b4elibrary.Logger;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



import static com.b4edriver.CommonClasses.Services.FusedLocationService.isRunning;
import static com.b4edriver.CommonClasses.Services.FusedLocationService.mPreviousLocation;
import static com.b4edriver.CommonClasses.Services.FusedLocationService.sendDataMeFromFused;


public class TripStartedActivityDriver extends AppCompatActivity implements LocationListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    public static TextView already_paid_tv;
    public static TripStartedActivityDriver mInstance;
    public static MenuItem menuItem;
    public static GoogleMap map;
    public static Marker marker;
    public static double testDistance = 0.0;
    public double lat, lon;
    public ArrayList<LatLng> points = null;
    public Location mCurrentLocation;
    public LinearLayout directionLL;
    public ImageView directionIcon;
    public TextView directionAddress;
    public TextView direction;
    public double totalDistance1 = 0.0;
    public FloatingActionButton btn_trip_end;
    ArrayList<String> locationList;
    FloatingActionButton btn_nav;
    TripDriver trip;
    String distances = "";
    TextView distanceTxt;
    CardView already_paid_text;
    LatLng lastLocation;
    LatLng dest;
    RelativeLayout userDetailLL;
    ImageView call_img, collapseBtn;
    boolean isCollapse = true;
    TextView name, number, address;
    PolylineOptions lineOptionsSave = new PolylineOptions();
    //public static Location destiLoc = new Location("");
    LatLng myLatLng;
    DBAdapter_Driver dbAdapter_driver;
    CardView directionCl;
    float arrow_rotation = 0;
    double distanceBW2Points = 0.0;
    WebView webView;
    Location checkLoaction;
    List<String> sendDataMe = new ArrayList<>();
    int locationPopCount = 0;
    int locationResetCount = 0;

    View.OnClickListener navigation = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            boolean isdesti = (String.valueOf(trip.getDestinationLatitude()).equalsIgnoreCase(""));
            if (isdesti || trip.getDestinationLatitude() == 0.0) {
                Snackbar.make(v, "Destination Address is not define by user", Snackbar.LENGTH_LONG).show();
            } else if (Function.isConnectingToInternet(TripStartedActivityDriver.this)) {
                /*isNavi = true;
                getSupportActionBar().hide();
                directionLL.setVisibility(View.VISIBLE);
                map.setMyLocationEnabled(false);
                if (marker == null) {
                    marker = map.addMarker(new MarkerOptions()
                            .position(markerLocation)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.nav)));
                }
                drawRoute(lat, lon, isNavi);
                new LongOperationTrun().execute("");*/

                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:" +
                        "q=" + trip.getDestinationLatitude() + "," + trip.getDestinationLogitude()));//+ "&daddr=" + trip.getDestinationLatitude() + "," + trip.getDestinationLogitude()));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);

                overridePendingTransition(R.anim.left_to_right,
                        R.anim.right_to_left);

            } else {
                new AlertDialog.Builder(TripStartedActivityDriver.this).setCancelable(true).setMessage("Please check internet connection!")
                        .setTitle("Connection error!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        }
    };
    View.OnClickListener call = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (trip.getService_id().equalsIgnoreCase("4")) {
                Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + trip.getUserNumber()));
                try {
                    startActivity(in);
                    overridePendingTransition(R.anim.left_to_right,
                            R.anim.right_to_left);
                } catch (android.content.ActivityNotFoundException ex) {
                    Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_LONG)
                            .show();
                }
            } else {

                new SweetAlertDialog(TripStartedActivityDriver.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("Call To Customer")
                        .setCustomImage(R.drawable.callb)
                        .setContentText("")
                        .setCancelText("Delivery")
                        .setConfirmText("Customer")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
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
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + trip.getDropCno()));
                                try {
                                    startActivity(in);
                                    overridePendingTransition(R.anim.left_to_right,
                                            R.anim.right_to_left);
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_LONG)
                                            .show();
                                }
                            }
                        })
                        .show();
            }

        }
    };

    private AddressResultReceiver mResultReceiver;
    private AddressResultReceiverForDriver mResultReceiverForDriver;
    private String tripEndMsg = "";
    View.OnClickListener tripEnd = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            StopTrip();

        }
    };
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
    private BroadcastReceiver notifydrivertoendtrip = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            tripEndMsg = intent.getStringExtra("msg");
            btn_trip_end.performClick();
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

    public static double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    public static void updateNavMarker(Location mPrevLocation, Location mCurrentLocation) {

        final LatLng location = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        Logger.log("LOCATIONSEN", location.toString());

        Location firstLoc = new Location("A");
        Location secondLoc = new Location("B");

        firstLoc.setLatitude(mPrevLocation.getLatitude());
        firstLoc.setLongitude(mPrevLocation.getLongitude());

        secondLoc.setLatitude(mCurrentLocation.getLatitude());
        secondLoc.setLongitude(mCurrentLocation.getLongitude());

        float bearing = 0;
        float heading = 0;

        bearing = firstLoc.bearingTo(secondLoc);    // -180 to 180
        heading = firstLoc.getBearing();         // 0 to 360

        float arrow_rotation = (360 + ((bearing + 360) % 360) - heading) % 360;


        if (marker == null) {

            try {
                marker = map.addMarker(new MarkerOptions()
                        .position(location)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.nav)));
                map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(location, 18, 10f, arrow_rotation)));
            } catch (NullPointerException e) {
            }
        } else {
            //AnimateMarkerDriver.getInstance().animateMarker(mInstance,map,marker,location,location.getBearing());

            marker.setRotation(arrow_rotation);

            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            Projection proj = map.getProjection();
            Point startPoint = proj.toScreenLocation(marker.getPosition());
            final LatLng startLatLng = proj.fromScreenLocation(startPoint);
            final long duration = 500;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        long elapsed = SystemClock.uptimeMillis() - start;
                        float t = interpolator.getInterpolation((float) elapsed
                                / duration);
                        double lng = t * location.longitude + (1 - t)
                                * startLatLng.longitude;
                        double lat = t * location.latitude + (1 - t)
                                * startLatLng.latitude;
                        marker.setPosition(new LatLng(lat, lng));

                        if (t < 1.0) {
                            // Post again 16ms later.
                            handler.postDelayed(this, 16);
                        } else {
                            if (false) {
                                marker.setVisible(false);
                            } else {
                                marker.setVisible(true);
                            }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_started_driver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mInstance = this;

        /*if (ContextCompat.checkSelfPermission(mInstance, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(mInstance, Manifest.permission.SEND_SMS)) {

                //  Toast.makeText(instance, "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();

            } else {
                //  Toast.makeText(instance, "GPS permission not allows.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(mInstance, new String[]{Manifest.permission.SEND_SMS}, 110);
            }
        } else {

        }*/

        LocalBroadcastManager.getInstance(this).registerReceiver(notifydrivertoendtrip,
                new IntentFilter("notifydrivertoendtrip"));
        mResultReceiver = new AddressResultReceiver(new Handler());
        mResultReceiverForDriver = new AddressResultReceiverForDriver(new Handler());
        AppPreferencesDriver.setActivity(mInstance, getClass().getName());
        AppPreferencesDriver.setTripStart(mInstance, "yes");
        AppPreferencesDriver.setItemDetails(mInstance, "");
        AppPreferencesDriver.setDriverStatus(mInstance, DriverStatus.ON_TRIP);



        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(WorkTimerService.str_receiver));


        if (FusedLocationService.mGoogleApiClient == null) {
            //new FusedLocationService(TripStartedActivityDriver.this);
            FusedLocationService locationService =  FusedLocationService.getInstance(TripStartedActivityDriver.this);
            locationService.googleClientReConnect();
        } else {
            if (!FusedLocationService.mGoogleApiClient.isConnected() || FusedLocationService.mGoogleApiClient.isConnecting()) {
                FusedLocationService.mGoogleApiClient.connect();
            }
        }


        distanceTxt = (TextView) findViewById(R.id.text);
        already_paid_tv = (TextView) findViewById(R.id.already_paid_tv);

        already_paid_text = (CardView) findViewById(R.id.cardView);
        // offline_tv = (TextView) findViewById(R.id.offline_tv);

        btn_trip_end = (FloatingActionButton) findViewById(R.id.btn_trip_end);
        btn_trip_end.setOnClickListener(tripEnd);

        btn_nav = (FloatingActionButton) findViewById(R.id.btn_nav);
        btn_nav.setOnClickListener(navigation);

        directionLL = (LinearLayout) findViewById(R.id.directionLL);
        directionAddress = (TextView) findViewById(R.id.directionAddress);
        direction = (TextView) findViewById(R.id.direction);
        directionIcon = (ImageView) findViewById(R.id.directionIcon);
        directionCl = (CardView) findViewById(R.id.directionCl);


        userDetailLL = (RelativeLayout) findViewById(R.id.userDetailLL);
        collapseBtn = (ImageButton) findViewById(R.id.collapseBtn);
        name = (TextView) findViewById(R.id.name);
        number = (TextView) findViewById(R.id.number);
        address = (TextView) findViewById(R.id.address);
        call_img = (ImageView) findViewById(R.id.img_call);

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

        webView = (WebView) findViewById(R.id.webView);

        call_img.setOnClickListener(call);

        dbAdapter_driver = new DBAdapter_Driver(mInstance);

        ImageButton myLocation = (ImageButton) findViewById(R.id.btn_myloc);

        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Logger.log("LocationServiceDriver", "" + FusedLocationService.getLocation().getLatitude());
                map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(FusedLocationService.getLocation().getLatitude(), FusedLocationService.getLocation().getLongitude())));
                map.animateCamera(CameraUpdateFactory.zoomTo(18));
            }
        });


        // fusedLocationService = new FusedLocationServiceDriver(mInstance);


        trip = getIntent().getParcelableExtra("tripDetails");

        Logger.log("Addresss::", "sorce:DONE");

        if (trip.getService_id().equalsIgnoreCase("4")) {
            name.setText(trip.getPickCname());
            number.setText(trip.getPickCno());
        } else {
            name.setText(trip.getDropCname());
            number.setText(trip.getDropCno());
        }

        /*if(trip.getSourceLatitude() == 0 || trip.getSourceLatitude() == 0.0) {
            trip.setSourceLatitude(Database.getInstance(mInstance).getDriverCurrentLocation(mInstance).getLatitude());
            trip.setSourcelogitude(Database.getInstance(mInstance).getDriverCurrentLocation(mInstance).getLongitude());
            AppPreferencesDriver.setSourcelatitude(mInstance, String.valueOf(trip.getSourceLatitude()));
            AppPreferencesDriver.setSourcelongitude(mInstance, String.valueOf(trip.getSourcelogitude()));
        }*/

        if (trip.getDestinationAddress().equalsIgnoreCase("")) {
            address.setText("N/A");
        } else {
            address.setText(trip.getDestinationAddress());
        }

        if (String.valueOf(trip.getDestinationLatitude()).equalsIgnoreCase("") || String.valueOf(trip.getDestinationLogitude()).equalsIgnoreCase("")) {
            trip.setDestinationLatitude(trip.getSourceLatitude());
            trip.setDestinationLogitude(trip.getSourcelogitude());
        }

        distanceTxt.setVisibility(View.GONE);
        //distanceTxt.setText(trip.getDistance());
        DBAdapter_Driver dbAdapter_driver = new DBAdapter_Driver(mInstance);
        dbAdapter_driver.insertTempTrip(trip);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //gps.getLocation();
        lat = FusedLocationService.getLocation().getLatitude();
        lon = FusedLocationService.getLocation().getLongitude();

        if (Function.isConnectingToInternet(mInstance)) {
            //if(trip.getSourceLatitude()!= 0.0 && trip.getDestinationLogitude()!=0.0)
            //drawRoute(lat, lon, isNavi);
            //new LongOperationTrun().execute("");
            if (trip.getSourceLatitude() != 0.0 && trip.getDestinationLogitude() != 0.0) {
                Intent intent = new Intent(mInstance, CreateRoutesServices.class);
                intent.putExtra(CreateRoutesServices.RECEIVER, mResultReceiver);
                intent.putExtra("pick_lat", trip.getSourceLatitude());
                intent.putExtra("pick_lng", trip.getSourcelogitude());
                intent.putExtra("drop_lat", trip.getDestinationLatitude());
                intent.putExtra("drop_lng", trip.getDestinationLogitude());
                startService(intent);
            }
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setSmallestDisplacement(1);

        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        mGoogleApiClient = new GoogleApiClient.Builder(mInstance)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();


        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                if(checkLoaction == null){
                    checkLoaction = mPreviousLocation;
                }else {

                    //sendDataMe.add("timer gps : "+ mPreviousLocation.getLatitude() +"=="+ checkLoaction.getLatitude() +" : isRunning="+isRunning);

                    if ( mPreviousLocation.getLatitude() == checkLoaction.getLatitude()
                           && isRunning ) {
                      //  displayPromptForEnablingGPS(TripStartedActivityDriver.this);
                    } else {
                        checkLoaction = mPreviousLocation;
                    }
                }




            }
        },500, 60000);


    }



    public  void displayPromptForEnablingGPS(final Context activity)
    {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                locationPopCount += 1;
                final MediaPlayer mp  = MediaPlayer.create(activity, R.raw.car_alarm);
                playSound(mp);
                long pattern[] = { 0, 100, 200, 300, 400 };
                final Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(pattern, 0);

                final AlertDialog.Builder builder =  new AlertDialog.Builder(activity);
                final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                final String message = "Please disable and again enable you GPS location";
                builder.setCancelable(false);
                builder.setMessage(message)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int id) {
                                        locationResetCount += 1;
                                        mp.stop();
                                        vibrator.cancel();
                                        activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                        d.dismiss();
                                    }
                                });
                        /*.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int id) {
                                        mp.stop();
                                        vibrator.cancel();
                                        d.cancel();
                                    }
                                });*/
                builder.create().show();
            }
        });

    }
    private void playSound(final MediaPlayer mp) {


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

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub


    }

    public String printDifference(Date startDate, Date endDate) {

        //1 minute = 60 seconds
        //1 hour = 60 x 60 = 3600
        //1 day = 3600 x 24 = 86400


        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds);

        String diffre = elapsedHours + " h " + elapsedMinutes + " m " + elapsedSeconds + " s";

        return diffre;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        sendDataMeFromFused.add("startTrip :::: " );

        LatLng sourceLocation = new LatLng(trip.getSourceLatitude(), trip.getSourcelogitude());

        LatLng destinationLocation = new LatLng(trip.getDestinationLatitude(), trip.getDestinationLogitude());
        dest = destinationLocation;

        AppPreferencesDriver.setDestilatitude(mInstance, trip.getDestinationLatitude());
        AppPreferencesDriver.setDestilogitude(mInstance, trip.getDestinationLogitude());

        LatLng markerLocation2 = new LatLng(FusedLocationService.getLocation().getLatitude(), FusedLocationService.getLocation().getLongitude());
      /*  if (simpleLocation.getLatitude() == 0.0) {
            map.moveCamera(CameraUpdateFactory.newLatLng(sourceLocation));
            markerLocation2 = sourceLocation;

        } else {*/
        map.moveCamera(CameraUpdateFactory.newLatLng(markerLocation2));
        //}

        Logger.log("LOCATIONSEN", markerLocation2.toString());
        // map.animateCamera(CameraUpdateFactory.zoomTo(15));

        //{"status":"200"}


        map.addMarker(new MarkerOptions()
                .position(sourceLocation)
                .title("Source")
                .snippet(trip.getSourceAddress()).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_three)));
        map.addMarker(new MarkerOptions()
                .position(destinationLocation)
                .title("Destination")
                .snippet(trip.getDestinationAddress()).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_four)));


        if (marker == null) {
            //LatLng loccc = new LatLng(simpleLocation.getLatitude(), simpleLocation.getLongitude());
            marker = map.addMarker(new MarkerOptions()
                    .position(markerLocation2)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.nav)));
            map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(markerLocation2, 18, 10f, arrow_rotation)));

        } else {
            //AnimateMarkerDriver.getInstance().animateMarker(mInstance,map,marker,location,location.getBearing());
            //animateMarker(marker, lastLocation, false, arrow_rotation);
        }


        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                mCurrentLocation = location;
            }
        });


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        //mCurrentLocation = fusedLocationProviderApi.getLastLocation(mGoogleApiClient);

        //TODO: if current location is not null we have to put it into shared preference

//        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
//
//        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
//            //Execute location service call if user has explicitly granted ACCESS_FINE_LOCATION..
//        }
        fusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mGoogleApiClient.connect();
    }

    public boolean checkInRadius(double cLat, double cLng, Circle circle, int radius1) {
        float[] radius = new float[radius1];


        Location.distanceBetween(cLat, cLng, circle.getCenter().latitude, circle.getCenter().longitude, radius);

        return radius[0] <= circle.getRadius();
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker, float arrow_rotation) {

        marker.setRotation(arrow_rotation);

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = map.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed
                            / duration);
                    double lng = t * toPosition.longitude + (1 - t)
                            * startLatLng.longitude;
                    double lat = t * toPosition.latitude + (1 - t)
                            * startLatLng.latitude;
                    marker.setPosition(new LatLng(lat, lng));

                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        if (hideMarker) {
                            marker.setVisible(false);
                        } else {
                            marker.setVisible(true);
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient.connect();
     /*   Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "TripStarted Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.bike4everythingdriver.DriverApp.Activity/http/host/path")
        );
        AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);*/
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
       /* Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "TripStarted Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.bike4everythingdriver.DriverApp.Activity/http/host/path")
        );
        AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);*/
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // make the device update its location

        // ...
    }

    public void TripFinishTask(final JSONObject jsonObject) {





        JSONParser jsonParser = new JSONParser(mInstance);
        jsonParser.parseVollyJSONObject(AppConstantDriver.URL.ONDEMAND_DRIVER_DRIVERBOOKINGPROCESS, 1, jsonObject, "", new VolleyCallBack() {
            @Override
            public void success(String response) {
                try {
                    if (response != null) {

                        testDistance = 0.0;
                        distanceBW2Points = 0.0;

                        //{"message":"Success","status":"200","result":[{"msg":"Your trip updated","respp":"Cash","payCode":"0"}]}
                        JSONObject object = new JSONObject(response);
                        String paycode = "0";
                        String respp = "";
                        if (object.getString("status").equalsIgnoreCase("200")) {
                            paycode = object.getJSONArray("result").getJSONObject(0).getString("payCode");
                            respp = object.getJSONArray("result").getJSONObject(0).getString("respp");
                        }

                        //  if (object.getString("status").equalsIgnoreCase("200")) {
                        AppPreferencesDriver.setDriverStatus(mInstance, DriverStatus.FREE);
                        DBAdapter_Driver dbAdapterDriver = new DBAdapter_Driver(mInstance);

                       /* DistanceTravelled distanceTravelled = new DistanceTravelled(1, "",
                                0.0 + 0.0 + 0.0,
                                0.0,
                                0.0,
                                0.0,
                                AppPreferencesDriver.getTripId(mInstance));
                        boolean setDis = dbAdapterDriver.setDistance(distanceTravelled);*/


                        try {
                            boolean isDeleted = dbAdapterDriver.deleteTripById(Integer.parseInt(AppPreferencesDriver.getTripId(mInstance)));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                        dbAdapterDriver.deleteLocation();


                        boolean delDis = dbAdapter_driver.deleteDistance();
                        Logger.log("dbAdapter_driver", dbAdapter_driver.toString() + "delDis" + delDis );
                       // sendDataMeFromFused.add("deleteDistance : "+ delDis );

                       // sendDataMe.add("Database: "+ delDis);

                        AppPreferencesDriver.setTripStart(mInstance, "no");
                        /*AppPreferencesDriver.setTripId(mInstance, "");
                        AppPreferencesDriver.setTripstatusForDriver(mInstance, AppConstantDriver.END_TRIP);*/
                        //finish();

                        try {
                            Intent intent1 = new Intent(mInstance, RecieptActivityDriver.class);
                            intent1.putExtra("driver_name", trip.getDropCname());
                            intent1.putExtra("pickup_address", jsonObject.getString("pickup_Address"));
                            intent1.putExtra("drop_address", jsonObject.getString("drop_Address"));
                            intent1.putExtra("trip_distance", jsonObject.getString("trip_distance"));
                            intent1.putExtra("payment", jsonObject.getString("payment"));
                            intent1.putExtra("trip_id", jsonObject.getString("trip_id"));
                            intent1.putExtra("datetime", AppPreferencesDriver.getTripdate(mInstance));
                            intent1.putExtra("totalDistance1", String.valueOf(totalDistance1));
                            intent1.putExtra("msg", tripEndMsg);
                            intent1.putExtra("paycode", paycode);
                            intent1.putExtra("respp", respp);
                            intent1.putExtra("sendDataMe", sendDataMe.toString());
                            startActivity(intent1);
                            finish();
                            overridePendingTransition(R.anim.left_to_right,
                                    R.anim.right_to_left);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                        /*}else {
                            btn_trip_end.setClickable(true);
                            Function.errorDialog(mInstance,getString(R.string.server_not_response));

                        }*/
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
                    errorDialog(mInstance, msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.starttrip_menu, menu);
        menuItem = menu.findItem(R.id.action_km);

        DistanceTravelled distanceTravelled = dbAdapter_driver.getDistance();
        if (distanceTravelled != null) {
            menuItem.setTitle(distanceTravelled.getDistanceTrip() + " KM");
        } else {
            menuItem.setTitle("0 KM");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_km) {
        }
        return super.onOptionsItemSelected(item);
    }

    public void StopTrip() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mInstance);
        alertDialog.setTitle("Bike 4 Everything");
        alertDialog.setMessage("Are you sure ?");
        alertDialog.setIcon(R.drawable.ic_launcher);

        //alertDialog.setIcon(R.drawable.);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                        AppPreferencesDriver.setDestilatitude(mInstance, FusedLocationService.getLocation().getLatitude());
                        AppPreferencesDriver.setDestilogitude(mInstance, FusedLocationService.getLocation().getLongitude());

                        String addressURL = Function.getAddressFromLatlng(Double.valueOf(AppPreferencesDriver.getDestilatitude(getApplicationContext())), Double.valueOf(AppPreferencesDriver.getDestilogitude(getApplicationContext())));

                        JSONParser jsonParser = new JSONParser(TripStartedActivityDriver.this);
                        jsonParser.getGoogleAddress(addressURL, new VolleyCallBack() {
                            @Override
                            public void success(String response) {
                                Gson gson = new Gson();
                                GoogleAddress googleAddress = gson.fromJson(response, GoogleAddress.class);

                                List<GoogleAddress> address = googleAddress.results;
                                if (address.isEmpty()) {
                                    if (trip.getDestinationAddress() == null) {
                                        AppPreferencesDriver.setDestiaddress(TripStartedActivityDriver.this, "N/A");
                                    } else {
                                        AppPreferencesDriver.setDestiaddress(TripStartedActivityDriver.this, trip.getDestinationAddress());
                                    }
                                } else {
                                    Logger.log("Addresss::", "sorce:DONE>>>" + address.get(0).address);
                                    String destinationAddress = address.get(0).address;
                                    AppPreferencesDriver.setDestiaddress(TripStartedActivityDriver.this, destinationAddress);
                                }
                                finishTrip();

                            }
                        }, new ServerErrorCallBack() {
                            @Override
                            public void error(String response) {
                                Logger.log("Addresss::", "sorce:DONE" + response);
                                try {
                                    JSONObject object = new JSONObject(response);
                                    if (object.getString("status").equals("200")) {
                                        if (trip.getDestinationAddress() == null) {
                                            AppPreferencesDriver.setDestiaddress(TripStartedActivityDriver.this, "N/A");
                                        } else {
                                            AppPreferencesDriver.setDestiaddress(TripStartedActivityDriver.this, trip.getDestinationAddress());
                                        }
                                        finishTrip();
                                    } else {
                                        new SweetAlertDialog(TripStartedActivityDriver.this, SweetAlertDialog.WARNING_TYPE)
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
                                }
                            }
                        });

                    }

                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        if (!((AppCompatActivity) TripStartedActivityDriver.this).isFinishing()) {
            alertDialog.show();
        }
    }

    private void finishTrip() {
        btn_trip_end.setClickable(false);

        sendDataMeFromFused.add("finishTrip :::: " );
        ArrayList<UserDriver> locationListsss = dbAdapter_driver.getLocation();


        locationList = new ArrayList<String>();

        for (int i = 0; i < locationListsss.size(); i++) {


            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("lat", locationListsss.get(i).getCurrentLatitude());
                jsonObject.put("lng", locationListsss.get(i).getCurrentLongitude());

                locationList.add(locationListsss.get(i).getCurrentLatitude() + " " +
                        locationListsss.get(i).getCurrentLongitude());


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        DistanceTravelled distanceTravelled = dbAdapter_driver.getDistance();

      //  double dis = 0.0;
      /*  double freedistance = 0.0;
        if (distanceTravelled != null) {
            dis = distanceTravelled.getDistanceTrip();
            freedistance = distanceTravelled.getDistanceFree();

        }*/

        double disss = Database.getInstance(TripStartedActivityDriver.this).getTotalDistance();

        if(disss == 0.0 || disss == 0){
            if (distanceTravelled != null) {
                disss = distanceTravelled.getDistanceTrip();
                sendDataMeFromFused.add("Distance by OLD db : " + disss);
            }
        }

        totalDistance1 = disss / 1000;//(double) Math.round(dis * 100) / 100;

        sendDataMe.add("Distance by device : "+ totalDistance1);
        sendDataMeFromFused.add("Distance by device : "+ totalDistance1);

        if(totalDistance1 == 0.0){
            LatLng source = new LatLng(trip.getSourceLatitude(), trip.getSourcelogitude());
            LatLng destination = new LatLng(AppPreferencesDriver.getLatitude(TripStartedActivityDriver.this),
                    AppPreferencesDriver.getLongitude(TripStartedActivityDriver.this));
            String url = Function.getDirectionsUrl(source, destination);
            RoutesDownloadTask downloadTask = new RoutesDownloadTask(TripStartedActivityDriver.this);
            downloadTask.execute(url);
        }else {
            fareCalculate(totalDistance1);
        }

    }

    private void finishTripNext(double payment1) {

        String discountAmount = AppPreferencesDriver.getPromocode(TripStartedActivityDriver.this);
        String referalDiscount = AppPreferencesDriver.getRefferalcode(TripStartedActivityDriver.this);

        double discountA = 0.0;

        if (!discountAmount.equalsIgnoreCase("0")) {
            discountA = new Double(payment1) * Integer.parseInt(discountAmount) / 100;
        } else {
            discountA = new Double(payment1) * Integer.parseInt(referalDiscount) / 100;
        }

        double discountB = new Double(payment1) - discountA;

        DecimalFormat twoDForm = new DecimalFormat("#.#");
        double discountFare = Double.valueOf(twoDForm.format(discountB));


        String Totalpayment = String.valueOf(discountFare);


        String timeDifference = "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {

            Date date1 = simpleDateFormat.parse(AppPreferencesDriver.getStartTime(TripStartedActivityDriver.this));
            Date date2 = simpleDateFormat.parse(Function.getCurrentDateTime());

            timeDifference = printDifference(date1, date2);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", AppConstantDriver.METHOD.END_BOOKING);
            jsonObject.put("driver_id", AppPreferencesDriver.getDriverId(TripStartedActivityDriver.this));
            jsonObject.put("trip_id", trip.getId());
            jsonObject.put("latitude", AppPreferencesDriver.getLatitude(TripStartedActivityDriver.this));
            jsonObject.put("longitude", AppPreferencesDriver.getLongitude(TripStartedActivityDriver.this));
            jsonObject.put("dateTime", Function.getCurrentDateTime());
            jsonObject.put("status", "end");
            jsonObject.put("type", AppPreferencesDriver.getDrivertype(TripStartedActivityDriver.this));

            jsonObject.put("locationList", Database.getInstance(TripStartedActivityDriver.this).getCurrentPathItemsSaved());//locationList.toString());
            jsonObject.put("actual_km", totalDistance1);
            jsonObject.put("booking_id", trip.getId());
            jsonObject.put("trip_time", timeDifference);
            jsonObject.put("trip_distance", totalDistance1);
            //jsonObject.put("rate",rate);
            //jsonObject.put("payment", payment1);
            jsonObject.put("payment", Totalpayment);
            jsonObject.put("discount_fare", discountA);
            jsonObject.put("discount", AppPreferencesDriver.getPromocode(TripStartedActivityDriver.this));
            jsonObject.put("referalUse", AppPreferencesDriver.getRefferalcode(TripStartedActivityDriver.this));
            jsonObject.put("promo_id", AppPreferencesDriver.getPromoid(TripStartedActivityDriver.this));
            jsonObject.put("pickup_Address", AppPreferencesDriver.getSourceaddress(TripStartedActivityDriver.this).trim());
            jsonObject.put("drop_Address", AppPreferencesDriver.getDestiaddress(TripStartedActivityDriver.this).trim());

            jsonObject.put("pickup_lat", AppPreferencesDriver.getSourcelatitude(TripStartedActivityDriver.this));
            jsonObject.put("pickup_lng", AppPreferencesDriver.getSourcelongitude(TripStartedActivityDriver.this));
            jsonObject.put("drop_lat", AppPreferencesDriver.getDestilatitude(TripStartedActivityDriver.this));
            jsonObject.put("drop_lng", AppPreferencesDriver.getDestilogitude(TripStartedActivityDriver.this));

            sendDataMe.add("fareCalculate : jsonObject "+" :: "+jsonObject);

           /* sendDataMe.add("fareCalculate : jsonObject "+" :: "+jsonObject);
            sendDataMe.add("locationResetCount "+" :: "+locationResetCount);
            sendDataMe.add("locationPopCount "+" :: "+locationPopCount);
            sendDataMeFromFused.add("trip_time "+" :: "+timeDifference);
            sendDataMeFromFused.add("locationList "+" :: "+locationList.toString());
            sendDataMeFromFused.add("pickup_add "+" :: "+AppPreferencesDriver.getSourcelatitude(TripStartedActivityDriver.this)+","+AppPreferencesDriver.getSourcelongitude(TripStartedActivityDriver.this));
            sendDataMeFromFused.add("drop_add "+" :: "+AppPreferencesDriver.getDestilatitude(TripStartedActivityDriver.this)+","+AppPreferencesDriver.getDestilogitude(TripStartedActivityDriver.this));
            */


            if (Function.isConnectingToInternet(TripStartedActivityDriver.this)) {
                TripFinishTask(jsonObject);
            } else {
                new AlertDialog.Builder(TripStartedActivityDriver.this).setCancelable(true).setMessage("Please check internet connection!")
                        .setTitle("Connection error!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
                /*String sms = "bike4everything tripEnd-" + FusedLocationService.getLocation().getLatitude() + "," + FusedLocationService.getLocation().getLongitude() + "," + Totalpayment + "," + trip.getId() + "," + AppPreferencesDriver.getDriverId(TripStartedActivityDriver.this) + "," + jsonObject.getString("trip_distance");

                Logger.log("SMSSMS", sms);
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("09229224424", null, sms, null, null);

                dbAdapter_driver.insertOfflineTrip(jsonObject.toString());
                DBAdapter_Driver dbAdapterDriver = new DBAdapter_Driver(mInstance);
                AppPreferencesDriver.setDriverStatus(mInstance, DriverStatus.FREE);
                *//*DistanceTravelled distanceTravelled1 = new DistanceTravelled(1, "",
                        0.0 + 0.0 + 0.0,
                        0.0,
                        0.0,
                        0.0,
                        AppPreferencesDriver.getTripId(mInstance).equalsIgnoreCase("") ? "0" : AppPreferencesDriver.getTripId(mInstance));
                dbAdapterDriver.setDistance(distanceTravelled1);*//*

                boolean isDeleted = dbAdapter_driver.deleteTripById(Integer.parseInt(AppPreferencesDriver.getTripId(TripStartedActivityDriver.this)));
                //Logger.log("isDeleted", isDeleted + "   >>>>>>>>>>>");
                dbAdapter_driver.deleteLocation();
                dbAdapter_driver.deleteDistance();

                AppPreferencesDriver.setTripStart(TripStartedActivityDriver.this, "no");
                //finish();

                try {
                    Intent intent1 = new Intent(TripStartedActivityDriver.this, RecieptActivityDriver.class);
                    intent1.putExtra("driver_name", trip.getDropCname());
                    intent1.putExtra("pickup_address", jsonObject.getString("pickup_Address"));
                    intent1.putExtra("drop_address", jsonObject.getString("drop_Address"));
                    intent1.putExtra("trip_distance", jsonObject.getString("trip_distance"));
                    intent1.putExtra("payment", jsonObject.getString("payment"));
                    intent1.putExtra("discountAmount", jsonObject.getString("discount"));
                    intent1.putExtra("trip_id", jsonObject.getString("trip_id"));
                    intent1.putExtra("datetime", AppPreferencesDriver.getTripdate(TripStartedActivityDriver.this));
                    intent1.putExtra("totalDistance1", String.valueOf(totalDistance1));
                    intent1.putExtra("msg", tripEndMsg);
                    startActivity(intent1);
                    finish();
                    overridePendingTransition(R.anim.left_to_right,
                            R.anim.right_to_left);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        btn_trip_end.setClickable(true);
    }


    public void errorDialog(Context context, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(TripStartedActivityDriver.this);
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


    private class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == CreateRoutesServices.resultCode) {
                ArrayList<LatLng> points = resultData.getParcelableArrayList("points");

                PolylineOptions lineOptions = new PolylineOptions();
                lineOptions.addAll(points);
                lineOptions.width(11);
                lineOptions.color(Color.BLUE);

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                Logger.log("POINTS:", points.get(0).toString());
                for (int i = 0; i < points.size(); i++) {
                    com.google.android.gms.maps.model.LatLng mapPoint =
                            new com.google.android.gms.maps.model.LatLng(points.get(i).latitude, points.get(i).longitude);
                    builder.include(mapPoint);
                }

                map.addPolyline(lineOptions);
                lineOptionsSave = lineOptions;

                stopService(new Intent(mInstance, CreateRoutesServices.class));
            }


        }

    }

    private class AddressResultReceiverForDriver extends ResultReceiver {
        public AddressResultReceiverForDriver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == CreateRoutesServices.resultCode) {
                ArrayList<LatLng> points = resultData.getParcelableArrayList("points");

                PolylineOptions lineOptions = new PolylineOptions();
                lineOptions.addAll(points);
                lineOptions.width(9);
                lineOptions.color(Color.YELLOW);

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                Logger.log("POINTS:", points.get(0).toString());
                for (int i = 0; i < points.size(); i++) {
                    com.google.android.gms.maps.model.LatLng mapPoint =
                            new com.google.android.gms.maps.model.LatLng(points.get(i).latitude, points.get(i).longitude);
                    builder.include(mapPoint);
                }

                //map.clear();
                //map.addPolyline(lineOptionsSave);
                map.addPolyline(lineOptions);

                if (lastLocation != null) {
                    Location firstLoc = new Location("A");
                    Location secondLoc = new Location("B");

                    firstLoc.setLatitude(lastLocation.latitude);
                    firstLoc.setLongitude(lastLocation.longitude);

                    secondLoc.setLatitude(FusedLocationService.getLocation().getLatitude());
                    secondLoc.setLongitude(FusedLocationService.getLocation().getLongitude());

                    float bearing = 0;
                    float heading = 0;


                    bearing = firstLoc.bearingTo(secondLoc);    // -180 to 180
                    heading = firstLoc.getBearing();         // 0 to 360
                    // *** Code to calculate where the arrow should point ***
                    arrow_rotation = (360 + ((bearing + 360) % 360) - heading) % 360;
                }

                if (marker == null) {

                    marker = map.addMarker(new MarkerOptions()
                            .position(lastLocation)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.nav)));
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(lastLocation, 18, 10f, arrow_rotation)));

                } else {
                    //AnimateMarkerDriver.getInstance().animateMarker(mInstance,map,marker,location,location.getBearing());
                    animateMarker(marker, lastLocation, false, arrow_rotation);
                }
                // map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50));
                stopService(new Intent(mInstance, CreateRoutesServices.class));
            }


        }

    }
    /*>>>>>>>>>>>>>>>>>>>>>>>>>>*/

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
                // Logger.log("routes", data);
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

            RoutesDownloadTask.ParserTask parserTask = new RoutesDownloadTask.ParserTask();

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
                Logger.log("routes", "111");

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
                List<String> distance = new ArrayList<>();
                String duration = "";
                String end_address = "";
                List<String> latLng = new ArrayList<>();
                if (result.size() < 1) {
                    //Toast.makeText(ParserTask.this, "No Points", Toast.LENGTH_SHORT).show();
                    return;
                }
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                // Traversing through all the routes

                for (int i = 0; i < result.size(); i++) {

                    if (i == 1 || i == 4 || i == 7) {

                        List<HashMap<String, String>> path = result.get(i);

                        for (int j = 0; j < path.size(); j++) {
                            HashMap<String, String> point = path.get(j);

                            if (j == 0) {    // Get distance from the list
                                distance.add(point.get("distance"));
                                // continue;
                            } else if (j == 1) { // Get duration from the list
                                duration = point.get("duration");
                                // continue;
                            } else if (j == 2) { // Get duration from the list
                                latLng.add(point.get("end_location"));
                                // continue;
                            }else if (j == 3) { // Get duration from the list
                                end_address = point.get("end_address");
                                // continue;
                            }

                        }
                    }

                }

                AppPreferencesDriver.setDestiaddress(TripStartedActivityDriver.this, end_address);
                AppPreferencesDriver.setDestilatitude(TripStartedActivityDriver.this, Double.parseDouble(latLng.get(0).split("-")[0]));
                AppPreferencesDriver.setDestilogitude(TripStartedActivityDriver.this, Double.parseDouble(latLng.get(0).split("-")[1]));


                sendDataMe.add("direction api distance: "+ distance.toString());

                String minDist = "0.0";
                List<String> distList = new ArrayList<String>();
                if (distance.size() == 1) {
                    if(distance.get(0).contains("km")){
                        minDist = distance.get(0).replace("km", "").replace("m", "").replace(" ", "");
                        // maxDist = distance.get(0).replace("km", "").replace("m", "").replace(" ", "");
                        sendDataMe.add("direction api : 1 true "+ minDist);
                    }else{
                        double minDistDoub = Double.parseDouble(distance.get(0).replace("km", "").replace("m", "").replace(" ", ""));

                        minDist = String.valueOf(minDistDoub/1000);
                        sendDataMe.add("direction api : 1 false "+ minDist);
                    }


                } else if (distance.size() > 1) {

                    for (int i = 0; i < distance.size(); i++) {

                        if(distance.get(0).contains("km")){
                            distList.add(distance.get(i).replace("km", "").replace("m", "").replace(" ", ""));
                        }else{
                            double minDistDoub = Double.parseDouble(distance.get(0).replace("km", "").replace("m", "").replace(" ", ""));
                            distList.add(String.valueOf(minDistDoub/1000));

                        }


                    }
                    sendDataMe.add("direction api :2 distList "+ distList.toString());
                    Logger.log("farecalculate distList", distList.toString());
                    Collections.sort(distList);
                    Logger.log("farecalculate distLis", distList.toString());
                    for (int i = 0; i < distList.size(); i++) {

                        if (i == 0) {
                            minDist = distList.get(i);
                        } else {
                            //maxDist = distList.get(i);
                        }
                    }
                    sendDataMe.add("direction api :2 minDist "+ minDist.toString());


                }

                sendDataMe.add("direction api : minDis"+ minDist.toString());
                Logger.log("farecalculate distance", distance.toString());
                fareCalculate(Double.parseDouble(minDist));



            }
        }

    }

    private void fareCalculate( double distance) {
        try {

           // String maxDist = "0.0";

            double minDist1 = distance;
          //  double maxDist1 = Double.parseDouble(maxDist);
            sendDataMe.add("fareCalculate : distance "+" :: "+minDist1);
            double min_fare = 0;
           // double max_fare = 0;

            if (trip.getService_id().equalsIgnoreCase("4") && AppPreferencesDriver.getPerkmcharge(TripStartedActivityDriver.this).equalsIgnoreCase("1")) {
                min_fare = AppPreferencesDriver.getBasicFare(TripStartedActivityDriver.this)
                        + (AppPreferencesDriver.getPerFare(TripStartedActivityDriver.this) * minDist1)
                        + AppPreferencesDriver.getOtherFare(TripStartedActivityDriver.this);
              /*  max_fare = AppPreferencesDriver.getBasicFare(TripStartedActivityDriver.this)
                        + (AppPreferencesDriver.getPerFare(TripStartedActivityDriver.this) * maxDist1)
                        + AppPreferencesDriver.getOtherFare(TripStartedActivityDriver.this);*/
                sendDataMe.add("fareCalculate : service id 4 && getPerkmcharge 1"+ min_fare);

            } else {
                JSONArray array = new JSONArray(AppPreferencesDriver.getDeleveryCharge(TripStartedActivityDriver.this));
               // JSONArray array = new JSONArray(deliveryCharges);
                sendDataMe.add("fareCalculate : service id "+trip.getService_id()+" : getPerkmcharge "+AppPreferencesDriver.getPerkmcharge(TripStartedActivityDriver.this)+""+
                        array.toString());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    double a = new Double(jsonObject.getString("km")).doubleValue();
                    if (minDist1 <= a) {
                        min_fare = new Double(jsonObject.getString("price")).doubleValue();
                    }
                    if (minDist1 <= a) {
                       // Logger.log("farecalculate ", new Double(jsonObject.getString("price")).doubleValue() + "::" + maxDist1 + "::" + a);
                        min_fare = new Double(jsonObject.getString("price")).doubleValue();
                        sendDataMe.add("fareCalculate : minDist1 <= a"+ min_fare);
                        break;
                    } else if (i == array.length() - 1) {
                        /*Logger.log("farecalculate 1", new Double(jsonObject.getString("price")).doubleValue() + "+((" +
                                maxDist1 + "-" + a + ")*10" +
                                new Double(jsonObject.getString("price")).doubleValue() + ((maxDist1 - a) * 10));*/
                        if (trip.getService_id().equalsIgnoreCase("4")) {
                            min_fare = ((minDist1) * AppPreferencesDriver.getPerFare(TripStartedActivityDriver.this)) + AppPreferencesDriver.getBasicFare(TripStartedActivityDriver.this);
                          //  max_fare = ((maxDist1) * AppPreferencesDriver.getPerFare(TripStartedActivityDriver.this)) + AppPreferencesDriver.getBasicFare(TripStartedActivityDriver.this);
                            sendDataMe.add("fareCalculate : taxi  getService_id"+ trip.getService_id()+" :: "+min_fare);
                        } else {
                            min_fare = new Double(jsonObject.getString("price")).doubleValue() + ((minDist1 - a) * 10);
                          //  max_fare = new Double(jsonObject.getString("price")).doubleValue() + ((maxDist1 - a) * 10);
                            sendDataMe.add("fareCalculate : other  getService_id"+ trip.getService_id()+" :: "+min_fare);
                        }
                    }

                }

            }

            totalDistance1 = minDist1;
            finishTripNext(min_fare);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }
    }


}
