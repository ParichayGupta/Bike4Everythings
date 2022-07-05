package com.b4edriver.DriverApp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.b4edriver.BuildConfig;
import com.b4edriver.CommonClasses.Classes.AnimateMarkerDriver;
import com.b4edriver.CommonClasses.ServerConnection.JSONParser;
import com.b4edriver.CommonClasses.ServerConnection.ServerErrorCallBack;
import com.b4edriver.CommonClasses.ServerConnection.VolleyCallBack;
import com.b4edriver.CommonClasses.Services.AlarmServicesDriver;
import com.b4edriver.CommonClasses.Services.Mail;
import com.b4edriver.CommonClasses.Utils.AppConstantDriver;
import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.b4edriver.CommonClasses.Utils.CheckDriverStatus;
import com.b4edriver.Database.DBAdapter_Driver;
import com.b4edriver.Model.TripDriver;
import com.b4edriver.R;
import com.b4elibrary.Logger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

public class HomeMapActivityDriver extends Fragment implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static TextView distance;
    public static Location mCurrentLocation, mPreviousLocation;
    static View rootView;
    public Marker marker;
    public Marker driverMarker;
    GoogleMap mMap;
    Handler mHandler;
    DBAdapter_Driver db_driver;
    ArrayList<Long> tripIds;
    ArrayList<Long> tripIdsDeleted;
    Location loc;   //Will hold lastknown location
    Location wptLoc = new Location("");    // Waypoint location
    private Hashtable<String, TripDriver> markers;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;

    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.content_home_map_driver, container, false);
        } catch (InflateException e) {

        }


        db_driver = new DBAdapter_Driver(getActivity());

        mHandler = new Handler();

        MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        markers = new Hashtable<String, TripDriver>();


        tripIdsDeleted = new ArrayList<Long>();


        distance = (TextView) rootView.findViewById(R.id.distance);


        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setSmallestDisplacement(1);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();

        }



      /*  ll = ((LinearLayout) rootView.getParent());
//        ll.setBackgroundColor(Color.parseColor("#ffb5d6e1"));
        int childcount = ll.getChildCount();
        for (int i = 0; i < childcount; i++) {
            View v = ll.getChildAt(i);
            if (v instanceof TextView) ((TextView) v).setTextColor(Color.parseColor("##ffb5ffff"));
            if (v instanceof ImageView) {
                ImageView img = (ImageView) v;
                img.setImageResource(R.drawable.arrow);
                Matrix matrix = new Matrix();
                img.setScaleType(ImageView.ScaleType.MATRIX);
                matrix.postRotate(arrow_rotation, img.getWidth() / 2, img.getHeight() / 2);
                img.setImageMatrix(matrix);
            }
        }*/

        CheckDriverStatus.getInstance().checkStatus(getActivity());
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);


        final LatLng latLng1 = new LatLng(22.696654, 75.88789);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 15));


        // mHandler.postDelayed(LoadTripRunnable,500);


    }

    @Override
    public void onLocationChanged(Location location) {
        Logger.log("Rlocationcheck>>>>", location.toString());


        if (mMap != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (driverMarker != null) {
                AnimateMarkerDriver.getInstance().animateMarker(getContext(), mMap, driverMarker, latLng, false, location.getBearing());
                // animateMarker(TripStartedActivityDriver.marker, latLng, false);
            } else {
                driverMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bikemarker3)));
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mCurrentLocation = fusedLocationProviderApi.getLastLocation(mGoogleApiClient);


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

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


}
