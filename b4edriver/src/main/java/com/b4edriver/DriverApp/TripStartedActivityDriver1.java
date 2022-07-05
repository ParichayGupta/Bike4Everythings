package com.b4edriver.DriverApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.b4edriver.CommonClasses.Classes.GMapV2DirectionDriver;
import com.b4edriver.CommonClasses.ServerConnection.JSONParser;
import com.b4edriver.CommonClasses.ServerConnection.ServerErrorCallBack;
import com.b4edriver.CommonClasses.ServerConnection.Util;
import com.b4edriver.CommonClasses.ServerConnection.VolleyCallBack;
import com.b4edriver.CommonClasses.Services.CreateRoutesServices;
import com.b4edriver.CommonClasses.Services.FusedLocationService;
import com.b4edriver.CommonClasses.Services.WorkTimerService;
import com.b4edriver.CommonClasses.Utils.AppConstantDriver;
import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.b4edriver.CommonClasses.Utils.DialogManagerDriver;
import com.b4edriver.CommonClasses.Utils.DirectionsJSONParser;
import com.b4edriver.CommonClasses.Utils.DriverStatus;
import com.b4edriver.CommonClasses.Utils.Function;
import com.b4edriver.Database.DBAdapter_Driver;
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
import org.w3c.dom.Document;

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



public class TripStartedActivityDriver1 extends AppCompatActivity implements LocationListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    public static TextView already_paid_tv;
    public static TripStartedActivityDriver1 mInstance;
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
    View.OnClickListener navigation = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            boolean isdesti = (String.valueOf(trip.getDestinationLatitude()).equalsIgnoreCase(""));
            if (isdesti || trip.getDestinationLatitude() == 0.0) {
                Snackbar.make(v, "Destination Address is not define by user", Snackbar.LENGTH_LONG).show();
            } else if (Function.isConnectingToInternet(TripStartedActivityDriver1.this)) {
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

                new SweetAlertDialog(TripStartedActivityDriver1.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
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

        startService(new Intent(mInstance, FusedLocationService.class));

        registerReceiver(broadcastReceiver, new IntentFilter(WorkTimerService.str_receiver));


        if (FusedLocationService.mGoogleApiClient == null) {
            //new FusedLocationService(TripStartedActivityDriver1.this);
            FusedLocationService locationService =  FusedLocationService.getInstance(TripStartedActivityDriver1.this);
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

        AppPreferencesDriver.setDestilatitude(mInstance, FusedLocationService.getLocation().getLatitude());
        AppPreferencesDriver.setDestilogitude(mInstance, FusedLocationService.getLocation().getLongitude());

        LatLng sourceLocation = new LatLng(trip.getSourceLatitude(), trip.getSourcelogitude());

        LatLng destinationLocation = new LatLng(trip.getDestinationLatitude(), trip.getDestinationLogitude());
        dest = destinationLocation;
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

                        DistanceTravelled distanceTravelled = new DistanceTravelled(1, "",
                                0.0 + 0.0 + 0.0,
                                0.0,
                                0.0,
                                0.0,
                                AppPreferencesDriver.getTripId(mInstance));
                        boolean setDis = dbAdapterDriver.setDistance(distanceTravelled);


                        try {
                            boolean isDeleted = dbAdapterDriver.deleteTripById(Integer.parseInt(AppPreferencesDriver.getTripId(mInstance)));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                        dbAdapterDriver.deleteLocation();


                        boolean delDis = dbAdapter_driver.deleteDistance();
                        Logger.log("dbAdapter_driver", dbAdapter_driver.toString() + "delDis" + delDis + "setDis" + setDis);


                        AppPreferencesDriver.setTripStart(mInstance, "no");
                        AppPreferencesDriver.setTripId(mInstance, "");
                        AppPreferencesDriver.setTripstatusForDriver(mInstance, AppConstantDriver.END_TRIP);
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

                        String addressURL = Function.getAddressFromLatlng(Double.valueOf(AppPreferencesDriver.getDestilatitude(getApplicationContext())), Double.valueOf(AppPreferencesDriver.getDestilogitude(getApplicationContext())));

                        JSONParser jsonParser = new JSONParser(TripStartedActivityDriver1.this);
                        jsonParser.getGoogleAddress(addressURL, new VolleyCallBack() {
                            @Override
                            public void success(String response) {
                                Gson gson = new Gson();
                                GoogleAddress googleAddress = gson.fromJson(response, GoogleAddress.class);

                                List<GoogleAddress> address = googleAddress.results;
                                if (address.isEmpty()) {
                                    if (trip.getDestinationAddress() == null) {
                                        AppPreferencesDriver.setDestiaddress(TripStartedActivityDriver1.this, "N/A");
                                    } else {
                                        AppPreferencesDriver.setDestiaddress(TripStartedActivityDriver1.this, trip.getDestinationAddress());
                                    }
                                } else {
                                    Logger.log("Addresss::", "sorce:DONE>>>" + address.get(0).address);
                                    String destinationAddress = address.get(0).address;
                                    AppPreferencesDriver.setDestiaddress(TripStartedActivityDriver1.this, destinationAddress);
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
                                            AppPreferencesDriver.setDestiaddress(TripStartedActivityDriver1.this, "N/A");
                                        } else {
                                            AppPreferencesDriver.setDestiaddress(TripStartedActivityDriver1.this, trip.getDestinationAddress());
                                        }
                                        finishTrip();
                                    } else {
                                        new SweetAlertDialog(TripStartedActivityDriver1.this, SweetAlertDialog.WARNING_TYPE)
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

        if (!((AppCompatActivity) TripStartedActivityDriver1.this).isFinishing()) {
            alertDialog.show();
        }
    }

    private void finishTrip() {
        btn_trip_end.setClickable(false);


        Logger.log("Addresss::", "sorce:" + AppPreferencesDriver.getSourceaddress(TripStartedActivityDriver1.this)
                + "\nDest:" + AppPreferencesDriver.getDestiaddress(TripStartedActivityDriver1.this));

        ArrayList<UserDriver> locationListsss = dbAdapter_driver.getLocation();


        String startloc = FusedLocationService.getLocation().getLatitude() + "," + FusedLocationService.getLocation().getLongitude();
        String endloc = FusedLocationService.getLocation().getLatitude() + "," + FusedLocationService.getLocation().getLongitude();

        locationList = new ArrayList<String>();

        for (int i = 0; i < locationListsss.size(); i++) {

            startloc = locationListsss.get(0).getCurrentLatitude().toString() + "," + locationListsss.get(0).getCurrentLongitude().toString();
            endloc = locationListsss.get(locationListsss.size() - 1).getCurrentLatitude().toString() + "," + locationListsss.get(locationListsss.size() - 1).getCurrentLongitude().toString();

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

        // ArrayList<String> lists = dbAdapter_driver.getDistance();

//        DistanceTravelled distanceTravelled = dbAdapter_driver.getDistance(DriverStatus.ON_TRIP);
        DistanceTravelled distanceTravelled = dbAdapter_driver.getDistance();

        double dis = 0.0;
        double freedistance = 0.0;
        if (distanceTravelled != null) {
            dis = distanceTravelled.getDistanceTrip();
            freedistance = distanceTravelled.getDistanceFree();
            //sendMailAfterFinish(mInstance, locationList.toString(), distanceTravelled.getDistanceTrip(),freedistance,startloc, endloc);
        } else {
            // sendMailAfterFinish(mInstance, locationList.toString(), dis,freedistance,startloc, endloc);
        }

        totalDistance1 = dis / 1000;//(double) Math.round(dis * 100) / 100;
        totalDistance1 = roundTwoDecimals(totalDistance1);
        Logger.log("totalDistance1 ", totalDistance1 + " ::: " + " ::: " + dis);


        int basic = AppPreferencesDriver.getBasicFare(TripStartedActivityDriver1.this);
        int perFare = AppPreferencesDriver.getPerFare(TripStartedActivityDriver1.this);


        double payment1 = basic + (perFare * totalDistance1);
        //double payment = (double) Math.round(payment1 * 100) / 100; //10 * dista;
        payment1 = roundTwoDecimals(payment1);

        Logger.log("Distance p", payment1 + " :: " + payment1 + " :: " + payment1);

        /* with in 1 km for estemate location*/

//        String aq = "71.0-115.0|M|22.7228189-75.7795529|M|12.1-16.5|M|saprateOtherServices|M|[{\\\"km\\\":\\\"5\\\",\\\"price\\\":\\\"45\\\"},{\\\"km\\\":\\\"5\\\",\\\"price\\\":\\\"45\\\"},{\\\"km\\\":\\\"5\\\",\\\"price\\\":\\\"45\\\"}]";
//
//        try {
//            Logger.log("farecalculate",aq);
//            String[] fare = aq.replace("|M|","ß").split("ß");
//            Logger.log("farecalculate",fare[0]);
//            AppPreferencesDriver.setEstmateprice(TripStartedActivityDriver.this, fare[0]);
//            AppPreferencesDriver.setEstmatedistination(TripStartedActivityDriver.this, fare[1]);
//            AppPreferencesDriver.setEstmatedistance(TripStartedActivityDriver.this, fare[2]);
//            try{
//                AppPreferencesDriver.setServiceType(TripStartedActivityDriver.this,fare[3]);
//            }catch (Exception ee){
//                AppPreferencesDriver.setServiceType(TripStartedActivityDriver.this,"");
//            }
//            try{
//                AppPreferencesDriver.setDeleveryCharge(TripStartedActivityDriver.this,fare[4]);
//            }catch (Exception ee){
//                AppPreferencesDriver.setServiceType(TripStartedActivityDriver.this,"");
//            }
//        }catch (Exception e){}
//        Logger.log("farecalculate", AppPreferencesDriver.getEstmateprice(TripStartedActivityDriver.this)+"\n"+ AppPreferencesDriver.getEstmatedistination(TripStartedActivityDriver.this));

        float[] results = new float[1];
        try {
            String[] estLoc = AppPreferencesDriver.getEstmatedistination(TripStartedActivityDriver1.this).split("-");
            Location.distanceBetween(Double.valueOf(estLoc[0]).doubleValue(), Double.valueOf(estLoc[1]).doubleValue(),
                    Double.valueOf(AppPreferencesDriver.getLatitude(TripStartedActivityDriver1.this)).doubleValue(),
                    Double.valueOf(AppPreferencesDriver.getLongitude(TripStartedActivityDriver1.this)).doubleValue(), results);
        } catch (Exception e) {
            e.printStackTrace();
        }
        float distanceInMeters = results[0];
        boolean isWithin200m = distanceInMeters <= 200;
        Logger.log("distance isWithin200m", "" + isWithin200m);

        try {
            String minmaxValue = AppPreferencesDriver.getEstmateprice(TripStartedActivityDriver1.this);

            String[] minMax = minmaxValue.split("-");

            double min_fare = Double.valueOf(minMax[0]).doubleValue();
            double max_fare = Double.valueOf(minMax[1]).doubleValue();
            if (AppPreferencesDriver.getServiceType(TripStartedActivityDriver1.this).equalsIgnoreCase("saprateOtherServices")) {
                Logger.log("farecalculate select", max_fare + "");
                if (isWithin200m) {
                    Logger.log("farecalculate isWithi", max_fare + "");

                    payment1 = max_fare;

                } else {
                    JSONArray array = new JSONArray(AppPreferencesDriver.getDeleveryCharge(TripStartedActivityDriver1.this));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        double a = new Double(jsonObject.getString("km")).doubleValue();
                        if (totalDistance1 <= a) {
                            Logger.log("farecalculate isWithOut", new Double(jsonObject.getString("price")).doubleValue() + "::" + totalDistance1 + "::" + a);
                            payment1 = new Double(jsonObject.getString("price")).doubleValue();

                            break;
                        } else if (i == array.length() - 1) {
                            Logger.log("farecalculate 1", new Double(jsonObject.getString("price")).doubleValue() + "+((" +
                                    totalDistance1 + "-" + a + ")*10" +
                                    new Double(jsonObject.getString("price")).doubleValue() + ((totalDistance1 - a) * 10));
                            payment1 = new Double(jsonObject.getString("price")).doubleValue() + ((totalDistance1 - a) * 10);

                            break;
                        }

                    }
                }


                finishTripNext(payment1);

            }
            else if (totalDistance1 == 0.0 && AppPreferencesDriver.getLatitude(TripStartedActivityDriver1.this) == 0.0) {
                payment1 = min_fare;
                try {
                    totalDistance1 = new Double(AppPreferencesDriver.getEstmatedistance(TripStartedActivityDriver1.this).split("-")[0]).doubleValue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finishTripNext(payment1);
            }
            else if (totalDistance1 == 0.0 && AppPreferencesDriver.getLatitude(TripStartedActivityDriver1.this) != 0.0) {

                if (isWithin200m) {
                    payment1 = min_fare;
                    try {
                        totalDistance1 = new Double(AppPreferencesDriver.getEstmatedistance(TripStartedActivityDriver1.this).split("-")[0]).doubleValue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    finishTripNext(payment1);
                }
                else {
                    //api
                    LatLng source = new LatLng(trip.getSourceLatitude(), trip.getSourcelogitude());
                    LatLng destination = new LatLng(AppPreferencesDriver.getLatitude(TripStartedActivityDriver1.this),
                            AppPreferencesDriver.getLongitude(TripStartedActivityDriver1.this));
                    String url = Function.getDirectionsUrl(source, destination);
                    RoutesDownloadTask downloadTask = new RoutesDownloadTask(TripStartedActivityDriver1.this, payment1);
                    downloadTask.execute(url);
                }

            } else if (totalDistance1 != 0.0 && AppPreferencesDriver.getLatitude(TripStartedActivityDriver1.this) == 0.0) {
                if (payment1 < min_fare && min_fare != 0.0) {
                    payment1 = min_fare;
                    try {
                        totalDistance1 = new Double(AppPreferencesDriver.getEstmatedistance(TripStartedActivityDriver1.this).split("-")[0]).doubleValue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    finishTripNext(payment1);
                } else {
                    finishTripNext(payment1);
                    // actual
                }

//
            } else {
                finishTripNext(payment1);
                //acctule
            }

            /*
            else if(totalDistance1 == 0.0){
                payment1 = min_fare;
                try {
                    totalDistance1 = new Double(AppPreferencesDriver.getEstmatedistance(TripStartedActivityDriver.this).split("-")[0]).doubleValue();
                }catch (Exception e){e.printStackTrace();}

            }else if(payment1 < min_fare && min_fare != 0.0 && isWithin200m){
                payment1 = min_fare;
                try {
                    totalDistance1 = new Double(AppPreferencesDriver.getEstmatedistance(TripStartedActivityDriver.this).split("-")[0]).doubleValue();
                }catch (Exception e){e.printStackTrace();}

            }else if(payment1 > max_fare && max_fare != 0.0 && isWithin200m){
                payment1 = max_fare;
                try{
                    totalDistance1 = new Double(AppPreferencesDriver.getEstmatedistance(TripStartedActivityDriver.this).split("-")[1]).doubleValue();
                }catch (Exception e){e.printStackTrace();}
            }else{
                //acctule
            }
            * */


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void finishTripNext(double payment1) {

        String discountAmount = AppPreferencesDriver.getPromocode(TripStartedActivityDriver1.this);
        String referalDiscount = AppPreferencesDriver.getRefferalcode(TripStartedActivityDriver1.this);

        double discountA = 0.0;

        if (!discountAmount.equalsIgnoreCase("0")) {
            discountA = new Double(payment1) * Integer.parseInt(discountAmount) / 100;
        } else {
            discountA = new Double(payment1) * Integer.parseInt(referalDiscount) / 100;
        }

        double discountB = new Double(payment1) - discountA;

        DecimalFormat twoDForm = new DecimalFormat("#.##");
        double discountFare = Double.valueOf(twoDForm.format(discountB));


        String Totalpayment = String.valueOf(discountFare);


        String timeDifference = "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {

            Date date1 = simpleDateFormat.parse(AppPreferencesDriver.getStartTime(TripStartedActivityDriver1.this));
            Date date2 = simpleDateFormat.parse(Function.getCurrentDateTime());

            timeDifference = printDifference(date1, date2);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", AppConstantDriver.METHOD.END_BOOKING);
            jsonObject.put("driver_id", AppPreferencesDriver.getDriverId(TripStartedActivityDriver1.this));
            jsonObject.put("trip_id", trip.getId());
            jsonObject.put("latitude", FusedLocationService.getLocation().getLatitude());
            jsonObject.put("longitude", FusedLocationService.getLocation().getLongitude());
            jsonObject.put("dateTime", Function.getCurrentDateTime());
            jsonObject.put("status", "end");
            jsonObject.put("type", AppPreferencesDriver.getDrivertype(TripStartedActivityDriver1.this));

            jsonObject.put("locationList", locationList.toString());
            jsonObject.put("actual_km", totalDistance1);
            jsonObject.put("booking_id", trip.getId());
            jsonObject.put("trip_time", timeDifference);
            jsonObject.put("trip_distance", totalDistance1);
            //jsonObject.put("rate",rate);
            //jsonObject.put("payment", payment1);
            jsonObject.put("payment", Totalpayment);
            jsonObject.put("discount_fare", discountFare);
            jsonObject.put("discount", AppPreferencesDriver.getPromocode(TripStartedActivityDriver1.this));
            jsonObject.put("referalUse", AppPreferencesDriver.getRefferalcode(TripStartedActivityDriver1.this));
            jsonObject.put("promo_id", AppPreferencesDriver.getPromoid(TripStartedActivityDriver1.this));
            jsonObject.put("pickup_Address", AppPreferencesDriver.getSourceaddress(TripStartedActivityDriver1.this).trim());
            jsonObject.put("drop_Address", AppPreferencesDriver.getDestiaddress(TripStartedActivityDriver1.this).trim());

            jsonObject.put("pickup_lat", AppPreferencesDriver.getSourcelatitude(TripStartedActivityDriver1.this));
            jsonObject.put("pickup_lng", AppPreferencesDriver.getSourcelongitude(TripStartedActivityDriver1.this));
            jsonObject.put("drop_lat", AppPreferencesDriver.getDestilatitude(TripStartedActivityDriver1.this));
            jsonObject.put("drop_lng", AppPreferencesDriver.getDestilogitude(TripStartedActivityDriver1.this));


            if (Function.isConnectingToInternet(TripStartedActivityDriver1.this)) {
                TripFinishTask(jsonObject);
            } else {
                String sms = "bike4everything tripEnd-" + FusedLocationService.getLocation().getLatitude() + "," + FusedLocationService.getLocation().getLongitude() + "," + Totalpayment + "," + trip.getId() + "," + AppPreferencesDriver.getDriverId(TripStartedActivityDriver1.this) + "," + jsonObject.getString("trip_distance");

                Logger.log("SMSSMS", sms);
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("09229224424", null, sms, null, null);

                dbAdapter_driver.insertOfflineTrip(jsonObject.toString());
                DBAdapter_Driver dbAdapterDriver = new DBAdapter_Driver(mInstance);
                AppPreferencesDriver.setDriverStatus(mInstance, DriverStatus.FREE);
                DistanceTravelled distanceTravelled1 = new DistanceTravelled(1, "",
                        0.0 + 0.0 + 0.0,
                        0.0,
                        0.0,
                        0.0,
                        AppPreferencesDriver.getTripId(mInstance).equalsIgnoreCase("") ? "0" : AppPreferencesDriver.getTripId(mInstance));
                dbAdapterDriver.setDistance(distanceTravelled1);

                boolean isDeleted = dbAdapter_driver.deleteTripById(Integer.parseInt(AppPreferencesDriver.getTripId(TripStartedActivityDriver1.this)));
                //Logger.log("isDeleted", isDeleted + "   >>>>>>>>>>>");
                dbAdapter_driver.deleteLocation();
                dbAdapter_driver.deleteDistance();

                AppPreferencesDriver.setTripStart(TripStartedActivityDriver1.this, "no");
                AppPreferencesDriver.setTripId(TripStartedActivityDriver1.this, "");
                AppPreferencesDriver.setTripstatusForDriver(TripStartedActivityDriver1.this, AppConstantDriver.END_TRIP);
                //finish();

                try {
                    Intent intent1 = new Intent(TripStartedActivityDriver1.this, RecieptActivityDriver.class);
                    intent1.putExtra("driver_name", trip.getDropCname());
                    intent1.putExtra("pickup_address", jsonObject.getString("pickup_Address"));
                    intent1.putExtra("drop_address", jsonObject.getString("drop_Address"));
                    intent1.putExtra("trip_distance", jsonObject.getString("trip_distance"));
                    intent1.putExtra("payment", jsonObject.getString("payment"));
                    intent1.putExtra("discountAmount", jsonObject.getString("discount"));
                    intent1.putExtra("trip_id", jsonObject.getString("trip_id"));
                    intent1.putExtra("datetime", AppPreferencesDriver.getTripdate(TripStartedActivityDriver1.this));
                    intent1.putExtra("totalDistance1", String.valueOf(totalDistance1));
                    intent1.putExtra("msg", tripEndMsg);
                    startActivity(intent1);
                    finish();
                    overridePendingTransition(R.anim.left_to_right,
                            R.anim.right_to_left);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        btn_trip_end.setClickable(true);
    }

    public void errorDialog(Context context, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(TripStartedActivityDriver1.this);
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

    private class LongOperationTrun extends AsyncTask<String, Void, String> {

        private String getDirection() {
            try {
                GMapV2DirectionDriver md = new GMapV2DirectionDriver();

                Document doc = md.getDocument(myLatLng, dest,
                        GMapV2DirectionDriver.MODE_WALKING);

                ArrayList<String> turnText = md.getTurnText(doc);

                String text = turnText.get(1).toString();


                //isDirectionDrawn = true;

                return text;
            } catch (Exception e) {
                /// Logger.log("direction...", e.getMessage());
                ///possible error:
                ///java.lang.IllegalStateException: Error using newLatLngBounds(LatLngBounds, int): Map size can't be 0. Most likely, layout has not yet occured for the map view.  Either wait until layout has occurred or use newLatLngBounds(LatLngBounds, int, int, int) which allows you to specify the map's dimensions.
                return null;
            }

        }


        @Override
        protected String doInBackground(String... params) {
            String turnText = null;
            try {
                turnText = getDirection();
            } catch (Exception e) {
                Thread.interrupted();
            }
            return turnText;
        }

        @Override
        protected void onPostExecute(String result) {

            LayoutInflater li = getLayoutInflater();
            //Getting the View object as defined in the customtoast.xml file
            View layout = li.inflate(R.layout.customtoast_driver,
                    (ViewGroup) findViewById(R.id.custom_toast_layout));

            TextView textView = (TextView) layout.findViewById(R.id.custom_toast_message);

            textView.setText(result);

//            Logger.log("direction...", result);

            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setView(layout);//setting the view of custom toast layout
            toast.show();

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
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
                    LatLng mapPoint =
                            new LatLng(points.get(i).latitude, points.get(i).longitude);
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
                    LatLng mapPoint =
                            new LatLng(points.get(i).latitude, points.get(i).longitude);
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
        double payment1;

        DialogManagerDriver dialogManagerDriver;

        public RoutesDownloadTask(Context context, double payment) {
            this.context = context;
            this.payment1 = payment;
            dialogManagerDriver = new DialogManagerDriver();
            dialogManagerDriver.showProcessDialog(TripStartedActivityDriver1.this, "", true);
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
                            }

                        }
                    }

                }

                try {
                    String minDist = "0.0";
                    String maxDist = "0.0";
                    List<String> distList = new ArrayList<String>();
                    if (distance.size() == 1) {
                        minDist = distance.get(0).replace("km", "").replace("m", "").replace(" ", "");
                        maxDist = distance.get(0).replace("km", "").replace("m", "").replace(" ", "");

                    }
                    else if (distance.size() > 1) {

                        for (int i = 0; i < distance.size(); i++) {
                            distList.add(distance.get(i).replace("km", "").replace("m", "").replace(" ", ""));
                        }

                        Logger.log("farecalculate distList", distList.toString());
                        Collections.sort(distList);
                        Logger.log("farecalculate distLis", distList.toString());
                        for (int i = 0; i < distList.size(); i++) {

                            if (i == 0) {
                                minDist = distList.get(i);
                            } else {
                                maxDist = distList.get(i);
                            }
                        }


                    }

                    Logger.log("farecalculate distance", distance.toString());
                    double minDist1 = Double.parseDouble(minDist);
                    double maxDist1 = Double.parseDouble(maxDist);

                    double min_fare = 0;
                    double max_fare = 0;

                    min_fare = AppPreferencesDriver.getBasicFare(TripStartedActivityDriver1.this)
                            + (AppPreferencesDriver.getPerFare(TripStartedActivityDriver1.this) * minDist1)
                            + AppPreferencesDriver.getOtherFare(TripStartedActivityDriver1.this);
                    max_fare = AppPreferencesDriver.getBasicFare(TripStartedActivityDriver1.this)
                            + (AppPreferencesDriver.getPerFare(TripStartedActivityDriver1.this) * maxDist1)
                            + AppPreferencesDriver.getOtherFare(TripStartedActivityDriver1.this);


                    payment1 = min_fare;
                    try {
                        totalDistance1 = minDist1;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialogManagerDriver.stopProcessDialog();

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                finishTripNext(payment1);


            }
        }

    }
}
