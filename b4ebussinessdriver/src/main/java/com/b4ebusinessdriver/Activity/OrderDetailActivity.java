package com.b4ebusinessdriver.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.b4ebusinessdriver.Adapter.UploadedImageAdapter;
import com.b4ebusinessdriver.Model.DropAddress;
import com.b4ebusinessdriver.Model.ImageUploadModel;
import com.b4ebusinessdriver.Model.ProductImage;
import com.b4ebusinessdriver.R;
import com.b4ebusinessdriver.Services.LocationService;
import com.b4ebusinessdriver.Utils.AppConstant;
import com.b4ebusinessdriver.Utils.AppPreferences;
import com.b4ebusinessdriver.Utils.DirectionsJSONParser;
import com.b4ebusinessdriver.Utils.Function;
import com.b4ebusinessdriver.Utils.Helper;
import com.b4elibrary.Logger;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.crash.FirebaseCrash;
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

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.b4ebusinessdriver.Activity.HomeActivity.isStart;
import static com.b4ebusinessdriver.Utils.Function.bitmapDescriptorFromVector;


public class OrderDetailActivity extends BaseActivity
        implements  OnMapReadyCallback , LocationService.PermisstionCallback{

    private final static int MSG_UPDATE_TIME = 0;
    public static OrderDetailActivity instance = null;
    Marker markerCurrent;
    GoogleMap map;
    TextView txtName, txtMobile, txtAddress, txtAmount;
    CardView llCustomerDetails;
    FloatingActionButton fabNavigate;
    CardView fabCallCustomer, fabCallAdmin, fabComplete, fabStart;
    FloatingActionButton fabMyLocation;
    LinearLayout details;
    ImageButton expandImg;
    String deliveryTime, extraDeliveryTime, reachDeliveryTime;
    DropAddress orderDetail;
    ArrayList<ProductImage> productImage;
    private LocationService mLocationService;
    private boolean isServiceBound;
    private final Handler mUIUpdateHandler = new UIUpdateHandler(this);

    RecyclerView recycler_view;
    private UploadedImageAdapter adapter;
    ArrayList<ImageUploadModel> imageList;

    View.OnClickListener setMyLocation = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           LatLng latLngZoom = new LatLng(mLocationService.getUserLocation().getLatitude(), mLocationService.getUserLocation().getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngZoom, 18));
        }
    };

    private static class UIUpdateHandler extends Handler {

        private final static int UPDATE_RATE_MS = 5000;
        private final WeakReference<OrderDetailActivity> activity;

        UIUpdateHandler(OrderDetailActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message message) {
            if (MSG_UPDATE_TIME == message.what) {
                activity.get().updateData();
                sendEmptyMessageDelayed(MSG_UPDATE_TIME, UPDATE_RATE_MS);
            }
        }
    }
    private void updateStopWalkUI() {
        if(mUIUpdateHandler.hasMessages(MSG_UPDATE_TIME)) {
            mUIUpdateHandler.removeMessages(MSG_UPDATE_TIME);
        }
    }
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            Log.e(TAG, "Service bound");

            isServiceBound = true;
            LocationService.LocalBinder localBinder = (LocationService.LocalBinder) binder;
            mLocationService = localBinder.getService(OrderDetailActivity.this);

            if (mLocationService.isUserWalking()) {
                updateStartWalkUI();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isServiceBound = false;
        }
    };
    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e(TAG, " inside locationReceiver");

            int resultCode = intent.getIntExtra(Helper.INTENT_EXTRA_RESULT_CODE, RESULT_CANCELED);

            if (resultCode == RESULT_OK) {
                // Toast.makeText(HomeActivity.this,"new marker",Toast.LENGTH_SHORT).show();

                Location userLocation = intent.getParcelableExtra(Helper.INTENT_USER_LAT_LNG);
                Location userPreLocation = intent.getParcelableExtra(Helper.INTENT_USER_PRE_LAT_LNG);
                if(userPreLocation == null)
                    return;

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

    private void startLocationService() {
        Intent intent = new Intent(this, LocationService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    View.OnClickListener navigation = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

                LatLng destination;
            destination = new LatLng(orderDetail.getDropLat(), orderDetail.getDropLng());

            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("google.navigation:q=" + destination.latitude
                            + "," + destination.longitude + ""));

            startActivity(intent);

        }
    };
    View.OnClickListener startOrder = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            new AlertDialog.Builder(OrderDetailActivity.this)
                    .setTitle("Business")
                    .setCancelable(false)
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();


                            fabStart.setEnabled(false);
                            new StartTrip().execute(AppConstant.START_DELIVERY);
                            //new StartTrip().execute(AppConstant.METHOD.START_ORDER,"start");
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();

        }
    };
    View.OnClickListener completeOrder = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            final Dialog dialog = new Dialog(OrderDetailActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.amount_dialog);

            final EditText amount = (EditText) dialog.findViewById(R.id.amount);
            final EditText note = (EditText) dialog.findViewById(R.id.note);
            TextView receivedamount = (TextView) dialog.findViewById(R.id.receivedamount);
            final TextView error = (TextView) dialog.findViewById(R.id.error);
            TextView submit = (TextView) dialog.findViewById(R.id.submit);

            receivedamount.setText(orderDetail.getDropAmount());

            amount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    error.setVisibility(View.GONE);
                    error.setText("");
                }
            });

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String receiveamount = orderDetail.getDropAmount();
                    String currentAmount = amount.getText().toString();
                    if(receiveamount.equalsIgnoreCase(currentAmount)){
                        dialog.dismiss();
                        /*int secondsLeft = (int) Math.round((timeInMilliseconds / (double) 1000));
                        extraDeliveryTime = (secondsToString(secondsLeft));
                        int reachsecondsLeft = (int) Math.round(((int) (SystemClock.uptimeMillis() - startTime) / (double) 1000));
                        reachDeliveryTime = (secondsToString(reachsecondsLeft));*/
                        fabComplete.setEnabled(false);
                        new CompleteTask( currentAmount, note.getText().toString()).execute();
                    }else if(TextUtils.isEmpty(currentAmount)){
                        error.setVisibility(View.VISIBLE);
                        error.setText("Please enter received amount");
                    }else  {
                        error.setVisibility(View.VISIBLE);
                        error.setText("Recevied amount and your entered amount not match");
                    }

                }
            });

            dialog.show();




        }
    };
    View.OnClickListener expandDetails = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            details.setVisibility(details.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            expandImg.setBackgroundResource(details.getVisibility() == View.VISIBLE ? R.drawable.ic_keyboard_arrow_up_black_24dp :
                    R.drawable.ic_keyboard_arrow_down_black_24dp);

        }
    };
    View.OnClickListener callCustomerListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String number = ("tel:" + txtMobile.getText().toString());
            Intent mIntent = new Intent(Intent.ACTION_CALL);
            mIntent.setData(Uri.parse(number));
            if (ContextCompat.checkSelfPermission(OrderDetailActivity.this,
                    Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(OrderDetailActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        0);

            } else {
                //You already have permission
                try {
                    startActivity(mIntent);
                } catch(SecurityException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    View.OnClickListener callAdminListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String number = ("tel:9294777999");
            Intent mIntent = new Intent(Intent.ACTION_CALL);
            mIntent.setData(Uri.parse(number));
            if (ContextCompat.checkSelfPermission(OrderDetailActivity.this,
                    Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(OrderDetailActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        0);

            } else {
                //You already have permission
                try {
                    startActivity(mIntent);
                } catch(SecurityException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private String TAG = "OrderDetailActivity";
    private LatLng currentLatLng, pickupLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderdetail);


        instance = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("On Trip");


        details = findViewById(R.id.details);
        expandImg = findViewById(R.id.expandImg);
        fabCallCustomer = findViewById(R.id.fabCallCustomer);
        fabCallAdmin = findViewById(R.id.fabCallAdmin);
        fabComplete = findViewById(R.id.fabComplete);
        fabStart = findViewById(R.id.fabStart);
        fabNavigate = findViewById(R.id.fabNavigate);
        llCustomerDetails = findViewById(R.id.llCustomerDetails);
        txtName = findViewById(R.id.txtName);
        txtMobile = findViewById(R.id.txtMobile);
        txtAddress = findViewById(R.id.txtAddress);
        txtAmount = findViewById(R.id.txtAmount);
        fabMyLocation = findViewById(R.id.fabMyLocation);

        expandImg.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);

        fabComplete.setOnClickListener(completeOrder);
        fabStart.setOnClickListener(startOrder);
        fabNavigate.setOnClickListener(navigation);
        expandImg.setOnClickListener(expandDetails);
        txtName.setOnClickListener(expandDetails);

        fabCallCustomer.setOnClickListener(callCustomerListener);
        fabCallAdmin.setOnClickListener(callAdminListener);
        fabMyLocation.setOnClickListener(setMyLocation);

        pickupLatLng = new LatLng(getIntent().getDoubleExtra("lat",0.0), getIntent().getDoubleExtra("lng",0.0));

        if(isStart){
            fabCallCustomer.setCardBackgroundColor(getResources().getColor(R.color.color3));
        }else{
            fabCallCustomer.setCardBackgroundColor(getResources().getColor(R.color.color2));
        }

        IntentFilter intentFilter = new IntentFilter(Helper.ACTION_NAME_SPACE);
        LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver, intentFilter);

        startLocationService();
        if(isServiceRunning(LocationService.class.getName()))
            setUpMap();


        fabStart.setVisibility(View.GONE);


    }

    private void setImageAdapter(ArrayList<ImageUploadModel> imageList) {
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(OrderDetailActivity.this);
        recycler_view.setLayoutManager(manager);
        adapter = new UploadedImageAdapter(OrderDetailActivity.this, imageList);
        recycler_view.setAdapter(adapter);
    }

    private void setUpMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(OrderDetailActivity.this);
    }

    private void updateUi(DropAddress orderDetail) {
        Animation slide_down = AnimationUtils.loadAnimation(OrderDetailActivity.this, R.anim.slide_down);
       // Logger.log("orderDetails 333", orderDetail.toString() +"\n"+isStart);
        fabCallCustomer.setVisibility(View.VISIBLE);
        if(isStart){
            fabComplete.setVisibility(View.VISIBLE);
           // fabStart.setVisibility(View.GONE);
        }else{
            fabComplete.setVisibility(View.GONE);
           // fabStart.setVisibility(View.VISIBLE);
        }

        fabNavigate.setVisibility(View.VISIBLE);
        llCustomerDetails.setVisibility(View.VISIBLE);
        llCustomerDetails.startAnimation(slide_down);
        txtName.setText(orderDetail.getDropName());
        txtMobile.setText(orderDetail.getDropMobile());
        txtAddress.setText("" + orderDetail.getDropAddress());
        txtAmount.setText("" + orderDetail.getDropAmount());

        imageList = new ArrayList<ImageUploadModel>();

            for(int i=0; i<productImage.size(); i++){

                ImageUploadModel uploadModel = new ImageUploadModel(productImage.get(i).getImage(), productImage.get(i).getDimension(),
                        productImage.get(i).getRemark());
                imageList.add(uploadModel);

            }

        setImageAdapter(imageList);



        LatLng destination;
        destination = new LatLng(orderDetail.getDropLat(), orderDetail.getDropLng());
        String url = Function.getDirectionsUrl(pickupLatLng, destination);
        // Logger.log("routes", url);
        RoutesDownloadTask downloadTask = new RoutesDownloadTask(instance);
        downloadTask.execute(url);


    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public void onBackPressed() {
       super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    //    getMenuInflater().inflate(R.menu.home, menu);
       // MenuItem timerItem = menu.findItem(R.id.action_timer);
       // timerItem.setVisible(false);

        return true;
    }


    private void initializeWalkService() {
        mLocationService.startUserWalk();
        mLocationService.startBroadcasting();
        //mLocationService.startForeground();
    }

    private void stopWalkService() {
        mLocationService.stopUserWalk();
        mLocationService.stopBroadcasting();
        //mLocationService.stopNotification();
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
        if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap maps) {
        this.map = maps;
        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                orderDetail = getIntent().getParcelableExtra("orderDetail");
                productImage = getIntent().getParcelableArrayListExtra("productImage");

                Logger.log("orderDetails", orderDetail.toString());

                if (orderDetail != null) {
                    updateUi(orderDetail);
                }

                setCurrentLocationMarker();
            }
        });


        //route();

    }

    private void setCurrentLocationMarker() {
        LatLng indoreCenter = new LatLng(new Double(AppPreferences.getCurLat(OrderDetailActivity.this)).doubleValue(),
                new Double(AppPreferences.getCurLong(OrderDetailActivity.this)).doubleValue());

        currentLatLng = indoreCenter;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(indoreCenter, 13));
        Logger.log("onMapReady", indoreCenter.latitude + "::" + indoreCenter.longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(indoreCenter).anchor(0.5f,0.5f).flat(true);

        if(isStart) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.navigation));
        }else{
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bike));
        }
        markerCurrent = map.addMarker(markerOptions);
    }



    @Override
    protected void onPause() {
        super.onPause();

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver);
        updateStopWalkUI();
        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
        }

    }

    @Override
    public void locationSettingsResult(PendingResult<LocationSettingsResult> result) {

    }

    public  void rotateMarker(final Marker marker, final float toRotation, final LatLng toPosition) {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = marker.getRotation();
        final long duration = 1555;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {

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

    public  void animateMarker(final Marker marker, final LatLng toPosition) {

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

    private class CompleteTask extends AsyncTask<String, Void, String> {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        String currentAmount = "0";
        String note = "";

        public CompleteTask(String currentAmount, String s){
            this.currentAmount = currentAmount;
            this.note = s;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            int secondStartsLeft = (int) Math.round((AppPreferences.getStartTime(OrderDetailActivity.this) / (double) 1000));
            String startTime = (secondsToString(secondStartsLeft));
            int secondEndtsLeft = (int) Math.round((Calendar.getInstance().getTimeInMillis() / (double) 1000));
            String endTime = (secondsToString(secondEndtsLeft));
            int secondsDiffLeft = (int) Math.round(((Calendar.getInstance().getTimeInMillis() - AppPreferences.getStartTime(OrderDetailActivity.this)) / (double) 1000));
            String timeDiffrence = (secondsToString(secondsDiffLeft));


            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject data = new JSONObject();
                data.put("method",AppConstant.COMPLETE_DELIVERY);
                data.put("drop_id", orderDetail.getDropId());
                data.put("dropAmount", currentAmount);
                data.put("additional_note", note);
                data.put("driver_id", AppPreferences.getUserId(OrderDetailActivity.this));
                data.put("delivery_id", AppPreferences.getDeliveryId(OrderDetailActivity.this));
                data.put("startTime1", AppPreferences.getStartTime(OrderDetailActivity.this));
                data.put("startTime", startTime);
                data.put("endTime", endTime);
                data.put("timeDiffrence", timeDiffrence);
                data.put("deliveryTime", deliveryTime);

                Log.e("Response_Response", AppConstant.B4E_DRIVER_BUSINESS_MANAGE_DELIVERY +"\n"+data.toString());
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



                    new AlertDialog.Builder(OrderDetailActivity.this)
                            .setTitle("Business")
                            .setCancelable(false)
                            .setMessage(jsonObject.getJSONArray("result").getJSONObject(0).getString("msg"))
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    Animation slide_up = AnimationUtils.loadAnimation(OrderDetailActivity.this, R.anim.slide_up);
                                    llCustomerDetails.startAnimation(slide_up);
                                    fabCallCustomer.setVisibility(View.GONE);
                                    fabComplete.setVisibility(View.GONE);
                                    fabNavigate.setVisibility(View.GONE);
                                    llCustomerDetails.setVisibility(View.GONE);




                                   // downTimer.cancel();

                                    deliveryTime = "";
                                    reachDeliveryTime = "";
                                    extraDeliveryTime = "";


                                    Intent intent = new Intent();
                                    intent.putExtra("orderDetail", orderDetail);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            })
                            .show();
                } else if (jsonObject.getString("status").equals("400")) {
                    new AlertDialog.Builder(OrderDetailActivity.this)
                            .setTitle("Business")
                            .setMessage(jsonObject.getJSONArray("result").getJSONObject(0).getString("msg"))
                            .setPositiveButton("Send Again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    new CompleteTask(currentAmount, note).execute();
                                }
                            }).show();




                }

            } catch (JSONException e) {
                e.printStackTrace();
                FirebaseCrash.report(e);
            }
            fabComplete.setEnabled(true);

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
                                continue;
                            } else if (j == 1) { // Get duration from the list
                                duration = point.get("duration");
                                durationValue = point.get("value");
                                Logger.log("duration ss", duration+"::"+ durationValue);
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
                        lineOptions.color(Color.BLUE);
                        lineOptions.geodesic(true);

                        // if(polylineFinal.isGeodesic())
                        //  polylineFinal.remove();
                        map.addPolyline(lineOptions);
                        //mMap.setPadding(0, measuredHeight/2, 0, 0);

                        map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50));

                        // mMap.setPadding(0, cardView.getHeight(), 0, 0);
                    }
                }

                // Start marker
                MarkerOptions options = new MarkerOptions();
                options.position(pickupLatLng);
                options.icon(bitmapDescriptorFromVector(OrderDetailActivity.this,R.drawable.ic_person_pin_circle_black_24dp));
                map.addMarker(options);

                //        // End marker
                MarkerOptions options2 = new MarkerOptions();
                options2.position(points.get(points.size() - 1));
                options2.icon(bitmapDescriptorFromVector(OrderDetailActivity.this,R.drawable.ic_pin_drop_black_24dp));
                map.addMarker(options2);


                long dur = Long.parseLong(durationValue) * 1000;
                int secondsLeft = (int) Math.round((dur / (double) 1000));
                deliveryTime = (secondsToString(secondsLeft));


            }
        }

    }

    private class StartTrip extends AsyncTask<String, Void, String> {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

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
                data.put("method", params[0]);
                data.put("drop_id", orderDetail.getDropId());
                data.put("driver_id", AppPreferences.getUserId(OrderDetailActivity.this));
                data.put("delivery_id", AppPreferences.getDeliveryId(OrderDetailActivity.this));

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
                    AppPreferences.setOntrip(OrderDetailActivity.this, true);
                    if (isServiceBound && !mLocationService.isUserWalking()) {
                        initializeWalkService();
                        updateStartWalkUI();
                    }
                    fabComplete.setVisibility(View.VISIBLE);
                    fabStart.setVisibility(View.GONE);
                    fabCallCustomer.setCardBackgroundColor(getResources().getColor(R.color.color3));
                    isStart = true;
                    markerCurrent.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.navigation));

                    AppPreferences.setStartTime(OrderDetailActivity.this, Calendar.getInstance().getTimeInMillis());
                } else if (jsonObject.getString("status").equals("400")) {

                }

            } catch (JSONException e) {
                e.printStackTrace();
                FirebaseCrash.report(e);
            }
            fabStart.setEnabled(true);
        }
    }

    private void updateStartWalkUI() {
        mUIUpdateHandler.sendEmptyMessage(MSG_UPDATE_TIME);

    }
    private void updateData() {
        if (isServiceBound) {

            //txtAddress.setText(getString(R.string.daily_dist_data, Helper.meterToMileConverter(mLocationService.distanceCovered())) + " :: " + Helper.calculatePace(mLocationService.elapsedTime(), mLocationService.distanceCovered()));

        }
    }


}


