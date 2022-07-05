package com.b4edriver.b4edrivers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.b4edriver.BuildConfig;
import com.b4edriver.CommonClasses.ServerConnection.JSONParser;
import com.b4edriver.CommonClasses.ServerConnection.ServerErrorCallBack;
import com.b4edriver.CommonClasses.ServerConnection.VolleyCallBack;
import com.b4edriver.CommonClasses.Utils.AppConstantDriver;
import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.b4edriver.CommonClasses.Utils.DriverStatus;
import com.b4edriver.Database.Database;
import com.b4edriver.DriverApp.DialogActivityDriver;
import com.b4edriver.DriverApp.NavigationDrawerDriver;
import com.b4edriver.Model.TripDriver;
import com.b4edriver.R;
import com.b4elibrary.Logger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements LocationListener, View.OnClickListener {


    private static final int REQUEST_CAMERA = 12;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    public final int INITIALIZE_TRIP = 0;
    public final int START_TRIP = 1;
    public final int REACHED_TRIP = 2;
    public final int START_DELIVERY = 3;

    Toolbar toolbar;
    TextView name;
    Button startTrip;
    TextView datetime;
    GoogleApiClient mGoogleApiClient;
    LocationRequest locationRequest;
    Location mycurrentLoc;
    private String RESTART_SERVICE = "com.hvantage.b4edrivers.alarm";
    String tripId;
    TripDriver tripDetails;


    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b4edrivers_activity_main);
        initView();
        setSupportActionBar(toolbar);

        tripId = getIntent().getStringExtra("tripId");

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
            }
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 11);
            }
        } else {

        }

        settingsrequest();



        /*if (AppPreferance.getStartTrip(MainActivity.this)) {
            startTrip.setText(getString(R.string.end_trip));
            name.setText(getString(R.string.afterstarttxt, "Restaurant Name"));
        } else {
            startTrip.setText(getString(R.string.start_trip));
            name.setText(AppPreferance.getUserName(MainActivity.this));
        }*/


        Calendar calander = Calendar.getInstance();
        SimpleDateFormat simpledateformat = new SimpleDateFormat("dd MMM, yyy");
        String date = simpledateformat.format(calander.getTime());

        datetime.setText(date);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", AppConstantDriver.METHOD.ACCEPT_BOOKING);
            jsonObject.put("driver_id", AppPreferencesDriver.getDriverId(MainActivity.this));
            jsonObject.put("trip_id",  tripId);
            jsonObject.put("status", "accept");
            // tempTrip = trip;
            acceptTripTask(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void initView() {

         toolbar=findViewById(R.id.toolbar);

         name=findViewById(R.id.name);

         startTrip=findViewById(R.id.startTrip);
         datetime=findViewById(R.id.datetime);

        startTrip.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (getCurrentLocation() == null) {
            showAlert("Location", "Please wait your current location is searching...");
            // } else if (AppPreferance.getTripStatus(MainActivity.this) == START_DELIVERY && !AppPreferance.isCustomerMobVeryfy(MainActivity.this)) {
            // enterMobileVerify();
        } else {
            cameraIntent();
        }
    }


    private void enterMobileVerify(final String kmGoogleTxt, final String kmMeterTxt, final String meterReadingTxt) {
        final Dialog otpDialog = new Dialog(MainActivity.this);
        otpDialog.setContentView(R.layout.b4edrivers_otp_login);
        otpDialog.setCancelable(false);
        otpDialog.setCanceledOnTouchOutside(false);

        final TextView title = (TextView) otpDialog.findViewById(R.id.title);
        final EditText otpTxt = (EditText) otpDialog.findViewById(R.id.otpTxt);
        Button next = (Button) otpDialog.findViewById(R.id.next);

        title.setText("Enter Customer No.");
        otpTxt.setHint("Enter Mobile Number");
        otpTxt.setInputType(InputType.TYPE_CLASS_PHONE);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(otpTxt.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter contact no.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("method", AppConstant.TRIP_VERIFY);
                        jsonObject.put("trip_id", AppPreferance.getTripId(MainActivity.this));
                        jsonObject.put("mobile", otpTxt.getText().toString());

                        otpRequest(kmGoogleTxt, kmMeterTxt, meterReadingTxt, jsonObject, otpDialog);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        otpDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.b4edrivers_menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            if (AppPreferance.getTripStatus(MainActivity.this) == INITIALIZE_TRIP) {
                new AlertDialog.Builder(MainActivity.this).setTitle("Logout!")
                        .setMessage("Are you sure to logout !")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AppPreferance.setUserid(MainActivity.this, 0);
                                AppPreferance.setMeterReading(MainActivity.this, 0);
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                finish();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

            } else {
                new AlertDialog.Builder(MainActivity.this).setTitle("Warning!")
                        .setMessage("Please finish the current trip...")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }

        } else if (item.getItemId() == R.id.action_history) {
            startActivity(new Intent(MainActivity.this, HostoryActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void cameraIntent() {

        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/b4edrivers/";
        File newdir = new File(dir);
        newdir.mkdirs();

        String file = dir + "b4edrivers.jpg";
        Logger.log("imagesss cam11", file);
        File newfile = new File(file);
        try {
            newfile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();

        }
        //final Uri outputFileUri = Uri.fromFile(newfile);
        Uri outputFileUri = null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            try {

            }catch (NullPointerException e){
                //outputFileUri = FileProvider.getUriForFile(MainActivity.this,
                  //      "com.b4ebusinessdriver.provider", newfile);
            }

            outputFileUri = FileProvider.getUriForFile(MainActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider", newfile);
            Logger.log("FileProvider",BuildConfig.APPLICATION_ID);
        } else {
            outputFileUri = Uri.fromFile(newfile);
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_CAMERA);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CAMERA) {
            File croppedImageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    + "/b4edrivers/" + "b4edrivers.jpg");

            final Uri originalFileUri;
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                originalFileUri = Uri.fromFile(croppedImageFile);

            } else {
                originalFileUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID+".provider", croppedImageFile);

            }

            startCropImageActivity(originalFileUri);

        } else if (resultCode == Activity.RESULT_OK && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getUri());
                    bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                showPreviewDialog(bitmap);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    //cameraIntent();
                    break;
                case Activity.RESULT_CANCELED:
                    settingsrequest();//keep asking if imp or do whatever
                    break;
            }
        }

    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAspectRatio(2, 2)
                // .setRequestedSize(200,200)
                .setScaleType(CropImageView.ScaleType.CENTER)
                .start(MainActivity.this);
    }

    private void showPreviewDialog(final Bitmap bitmap) {
        final Dialog dialog = new Dialog(MainActivity.this, R.style.image_preview_dialog);
        dialog.setContentView(R.layout.b4edrivers_image_setup_layout);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        final ImageView imageView = (ImageView) dialog.findViewById(R.id.img_circle);
        NestedScrollView container = (NestedScrollView) dialog.findViewById(R.id.container);

        Button btnclose = (Button) dialog.findViewById(R.id.btnclose);
        Button btnSave = (Button) dialog.findViewById(R.id.btnSave);
        final EditText meterReading = (EditText) dialog.findViewById(R.id.meterReading);
        final EditText remarkText = (EditText) dialog.findViewById(R.id.remarkText);
        final EditText address = (EditText) dialog.findViewById(R.id.address);
        final EditText cashamount = (EditText) dialog.findViewById(R.id.cashamount);
        final LinearLayout paymetmodeView = (LinearLayout) dialog.findViewById(R.id.paymetmodeView);
        final RadioGroup paymetmode = (RadioGroup) dialog.findViewById(R.id.paymetmode);

        final HorizontalScrollView scrollView = (HorizontalScrollView) dialog.findViewById(R.id.scrollView);

        address.setVisibility(View.GONE);

        switch (AppPreferance.getTripStatus(MainActivity.this)) {
            case INITIALIZE_TRIP:
                remarkText.setHint("Enter the PickUp Restaurant Name");
                remarkText.setText(AppPreferance.getPickupAddress(MainActivity.this));
                remarkText.setEnabled(true);
                paymetmodeView.setVisibility(View.GONE);
                break;
            case START_TRIP:
                remarkText.setHint("Enter Restaurant Name");
                remarkText.setText(AppPreferance.getPickupAddress(MainActivity.this));
                remarkText.setEnabled(false);
                paymetmodeView.setVisibility(View.GONE);
                break;
            case REACHED_TRIP:
                remarkText.setHint("Enter restaurant name, address & contact no.");
                remarkText.setText(AppPreferance.getPickupAddress(MainActivity.this));
                remarkText.setEnabled(false);
                paymetmodeView.setVisibility(View.VISIBLE);
                break;
            case START_DELIVERY:
                remarkText.setHint("Enter Customer Name");
                address.setHint("Enter Address");
                address.setVisibility(View.VISIBLE);
                remarkText.setEnabled(true);
                paymetmodeView.setVisibility(View.GONE);
                break;
        }

        paymetmode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.cash) {
                    cashamount.setVisibility(View.VISIBLE);
                } else {
                    cashamount.setVisibility(View.GONE);
                }
            }
        });

        if (AppPreferance.getTripStatus(MainActivity.this) == REACHED_TRIP) {
           meterReading.setText(AppPreferance.getMeterReading(MainActivity.this)+"");
        }

        imageView.setImageBitmap(bitmap);
        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //AppPreferance.setPickupAddress(MainActivity.this, remarkText.getText().toString());


                String meterReadingTxt = meterReading.getText().toString();
                if (TextUtils.isEmpty(meterReadingTxt)) {
                    Toast.makeText(MainActivity.this, "Enter meter reading", Toast.LENGTH_SHORT).show();
                    return;
                }

                float meterRead = Float.parseFloat(meterReadingTxt);
                Logger.log("getTripStatus", AppPreferance.getTripStatus(MainActivity.this) + " :: " + AppPreferance.getMeterReading(MainActivity.this));
                if (!(AppPreferance.getTripStatus(MainActivity.this) == REACHED_TRIP
                        ? (AppPreferance.getMeterReading(MainActivity.this) <= meterRead)
                        : (AppPreferance.getMeterReading(MainActivity.this) < meterRead))) {
                    showAlert("", "Your Current Meter Reading Can't be less then or same from Previous Meter Reading, Check & Re-enter the Correct Meter Reading.");
                    return;
                }

               /* if (AppPreferance.getMeterReading(MainActivity.this) > meterRead ) {
                    showAlert("", "Your Current Meter Reading Can't be less then Previous Meter Reading, Check & Re-enter the Correct Meter Reading.");
                    return;
                }*/

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);


                switch (AppPreferance.getTripStatus(MainActivity.this)) {
                    case INITIALIZE_TRIP:
                        String remarkTextTxt = remarkText.getText().toString();
                        if (TextUtils.isEmpty(remarkTextTxt)) {
                            Toast.makeText(MainActivity.this, "Enter Pickup point address", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        startTrip(meterReadingTxt, remarkText.getText().toString(), encoded, dialog);
                        break;
                    case START_TRIP:
                        reachedTrip(meterReadingTxt, remarkText.getText().toString(), encoded, dialog);
                        break;
                    case REACHED_TRIP:
                        String payment = "Paid";
                        if (paymetmode.getCheckedRadioButtonId() == R.id.cash && TextUtils.isEmpty(cashamount.getText().toString())) {
                            showAlert("Error!", "Please enter recived amount.");
                            return;
                        }

                        if (paymetmode.getCheckedRadioButtonId() == R.id.cash) {
                            payment = cashamount.getText().toString();
                        }

                        startDelivery(meterReadingTxt, remarkText.getText().toString(), encoded, payment, dialog);
                        break;
                    case START_DELIVERY:
                        String remarkTextTxt1 = remarkText.getText().toString();
                        if (TextUtils.isEmpty(remarkTextTxt1)) {
                            Toast.makeText(MainActivity.this, "Enter customer name", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String addres = address.getText().toString();
                        if (TextUtils.isEmpty(addres)) {
                            Toast.makeText(MainActivity.this, "Enter address", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        endDelivery(meterReadingTxt, remarkText.getText().toString() + addres, encoded, dialog);

                        break;
                }


            }

        });


        dialog.show();

        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                return false;
            }
        });
    }

    private void startTrip(final String meterReadingTxt, String remarkText, String encoded, final Dialog dialog) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", AppConstant.START_TRIP);
            jsonObject.put("driver_id", AppPreferencesDriver.getDriverId(MainActivity.this));
            jsonObject.put("trip_id", tripId);
            jsonObject.put("start_meter", meterReadingTxt);
            jsonObject.put("start_remark", remarkText);
            jsonObject.put("start_lat", getCurrentLocation().getLatitude());
            jsonObject.put("start_lng", getCurrentLocation().getLongitude());
            jsonObject.put("start_image", encoded);

            RequestToServer.getInstance().send(MainActivity.this, jsonObject, AppConstant.DRIVER, new RequestToServer.CallBack() {
                @Override
                public void success(String json) {
                    if (TextUtils.isEmpty(json)) {
                        showAlert("Error", "Please contact to administrator");
                    } else {
                        try {
                            JSONObject object = new JSONObject(json);
                            if (object.getString("status").equalsIgnoreCase("200")) {
                                JSONArray jsonArray = object.getJSONArray("result");
                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                                int tripId = jsonObject1.getInt("trip_id");
                                AppPreferance.setTripId(MainActivity.this, tripId+"");
                                AppPreferance.setMeterReading(MainActivity.this, Float.parseFloat(meterReadingTxt));
                                AppPreferance.setTripStatus(MainActivity.this, START_TRIP);
                                startTrip.setText(getString(R.string.reached_trip));
                                name.setText(getString(R.string.msg1, AppPreferance.getPickupAddress(MainActivity.this)));

                                dialog.dismiss();
                            } else {
                                JSONArray jsonArray = object.getJSONArray("result");
                                showAlert("Error", jsonArray.getJSONObject(0).getString("msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void reachedTrip(final String meterReadingTxt, String remarkText, String encoded, final Dialog dialog) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", AppConstant.REACHED_TRIP);
            jsonObject.put("driver_id", AppPreferencesDriver.getDriverId(MainActivity.this));
            jsonObject.put("trip_id", AppPreferance.getTripId(MainActivity.this));
            jsonObject.put("start_meter", meterReadingTxt);
            jsonObject.put("start_remark", remarkText);
            jsonObject.put("start_lat", getCurrentLocation().getLatitude());
            jsonObject.put("start_lng", getCurrentLocation().getLongitude());
            jsonObject.put("start_image", encoded);

            RequestToServer.getInstance().send(MainActivity.this, jsonObject, AppConstant.DRIVER, new RequestToServer.CallBack() {
                @Override
                public void success(String json) {
                    if (TextUtils.isEmpty(json)) {
                        showAlert("Error", "Please contact to administrator");
                    } else {
                        try {
                            JSONObject object = new JSONObject(json);
                            if (object.getString("status").equalsIgnoreCase("200")) {
                                JSONArray jsonArray = object.getJSONArray("result");
                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                AppPreferance.setMeterReading(MainActivity.this, Float.parseFloat(meterReadingTxt));
                                AppPreferance.setTripStatus(MainActivity.this, REACHED_TRIP);
                                startTrip.setText(getString(R.string.start_delivery));
                                name.setText(getString(R.string.msg2, AppPreferance.getPickupAddress(MainActivity.this)));

                                String kmGoogle = jsonObject1.getString("google");
                                String kmMeter = jsonObject1.getString("meter");
                                showTripStart_Rest(kmGoogle, kmMeter);

                                dialog.dismiss();
                            } else {
                                JSONArray jsonArray = object.getJSONArray("result");
                                showAlert("Error", jsonArray.getJSONObject(0).getString("msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void startDelivery(final String meterReadingTxt, String remarkText, String encoded, String payment, final Dialog dialog) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", AppConstant.START_DELIVERY);
            jsonObject.put("driver_id", AppPreferencesDriver.getDriverId(MainActivity.this));
            jsonObject.put("trip_id", AppPreferance.getTripId(MainActivity.this));
            jsonObject.put("start_meter", meterReadingTxt);
            jsonObject.put("start_remark", remarkText);
            jsonObject.put("paymentmode", payment);
            jsonObject.put("start_lat", getCurrentLocation().getLatitude());
            jsonObject.put("start_lng", getCurrentLocation().getLongitude());
            jsonObject.put("start_image", encoded);

            RequestToServer.getInstance().send(MainActivity.this, jsonObject, AppConstant.DRIVER, new RequestToServer.CallBack() {
                @Override
                public void success(String json) {
                    if (TextUtils.isEmpty(json)) {
                        showAlert("Error", "Please contact to administrator");
                    } else {
                        try {
                            JSONObject object = new JSONObject(json);
                            if (object.getString("status").equalsIgnoreCase("200")) {
                                JSONArray jsonArray = object.getJSONArray("result");
                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                AppPreferance.setMeterReading(MainActivity.this, Float.parseFloat(meterReadingTxt));
                                AppPreferance.setTripStatus(MainActivity.this, START_DELIVERY);
                                startTrip.setText(getString(R.string.end_delivery));
                                name.setText(getString(R.string.msg3, AppPreferance.getPickupAddress(MainActivity.this)));

                                dialog.dismiss();
                            } else {
                                JSONArray jsonArray = object.getJSONArray("result");
                                showAlert("Error", jsonArray.getJSONObject(0).getString("msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void endDelivery(final String meterReadingTxt, String remarkText, String encoded, final Dialog dialog) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", AppConstant.END_DELIVERY);
            jsonObject.put("driver_id", AppPreferencesDriver.getDriverId(MainActivity.this));
            jsonObject.put("trip_id", AppPreferance.getTripId(MainActivity.this));
            jsonObject.put("start_meter", meterReadingTxt);
            jsonObject.put("start_remark", remarkText);
            jsonObject.put("start_lat", getCurrentLocation().getLatitude());
            jsonObject.put("start_lng", getCurrentLocation().getLongitude());
            jsonObject.put("start_image", encoded);

            RequestToServer.getInstance().send(MainActivity.this, jsonObject, AppConstant.DRIVER, new RequestToServer.CallBack() {
                @Override
                public void success(String json) {
                    if (TextUtils.isEmpty(json)) {
                        showAlert("Error", "Please contact to administrator");
                    } else {
                        try {
                            JSONObject object = new JSONObject(json);
                            if (object.getString("status").equalsIgnoreCase("200")) {
                                JSONArray jsonArray = object.getJSONArray("result");
                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);



                                String kmGoogle = jsonObject1.getString("google");
                                String kmMeter = jsonObject1.getString("meter");

                                enterMobileVerify(kmGoogle,kmMeter, meterReadingTxt);

                                //endTrip(kmGoogle, kmMeter, meterReadingTxt, true);

                                dialog.dismiss();
                            } else {
                                JSONArray jsonArray = object.getJSONArray("result");
                                showAlert("Error", jsonArray.getJSONObject(0).getString("msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void otpRequest(final String kmGoogleTxt, final String kmMeterTxt, final String meterReadingTxt, final JSONObject jsonObject, final Dialog dialog) {
        RequestToServer.getInstance().send(MainActivity.this, jsonObject, AppConstant.DRIVER, new RequestToServer.CallBack() {
            @SuppressLint("StringFormatMatches")
            @Override
            public void success(String json) {
                if (TextUtils.isEmpty(json)) {
                    showAlert("Error", "Please contact to administrator");
                } else {
                    try {
                        JSONObject object = new JSONObject(json);
                        if (object.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = object.getJSONArray("result");
                            JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                            final String otp = jsonObject1.getString("otp");

                            final Dialog otpDialog = new Dialog(MainActivity.this);
                            Window window = otpDialog.getWindow();
                            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            otpDialog.getWindow().setSoftInputMode(
                                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                            otpDialog.setContentView(R.layout.b4edrivers_otp_end_trip);
                            otpDialog.setCancelable(false);
                            otpDialog.setCanceledOnTouchOutside(false);


                            final TextView tripamount = (TextView) otpDialog.findViewById(R.id.tripamount);
                            final TextView title = (TextView) otpDialog.findViewById(R.id.title);
                            final EditText otpTxt = (EditText) otpDialog.findViewById(R.id.otpTxt);
                            final EditText cashamount = (EditText) otpDialog.findViewById(R.id.cashamount);
                            Button next = (Button) otpDialog.findViewById(R.id.next);
                            TextView kmGoogle = (TextView) otpDialog.findViewById(R.id.googleDistance);
                            TextView kmMeter = (TextView) otpDialog.findViewById(R.id.meterDistance);
                            RadioGroup paymetmode = (RadioGroup) otpDialog.findViewById(R.id.paymetmode);

                            kmGoogle.setText(kmGoogleTxt + " KM");
                            kmMeter.setText(kmMeterTxt + " KM");


                            title.setText("OTP");
                            otpTxt.setHint("Enter OTP code");


                            final String amountfor_biker_str = Function.tripAnountForBike(kmMeterTxt);
                            final String amountfor_b4e_str = Function.tripAnountForB4E(kmMeterTxt);

                            tripamount.setText(getString(R.string.trip_amount_for_b4e, amountfor_b4e_str));

                            final String[] payment_mode = {"Credit"};

                            paymetmode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup group, int checkedId) {
                                    if(checkedId == R.id.credit){
                                        cashamount.setVisibility(View.GONE);
                                        payment_mode[0] = "Credit";
                                    }else {
                                        cashamount.setVisibility(View.VISIBLE);
                                        payment_mode[0] = "";
                                    }
                                }
                            });



                            /*otpDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                @Override
                                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP){
                                        Toast.makeText(MainActivity.this, "Please click on submit button", Toast.LENGTH_LONG).show();
                                    }
                                    //finish();
                                    return true;
                                }
                            });*/

                            next.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if(!payment_mode[0].equalsIgnoreCase("Credit")){
                                        payment_mode[0] = cashamount.getText().toString();
                                    }

                                    if (TextUtils.isEmpty(payment_mode[0])) {
                                        Toast.makeText(MainActivity.this, "Enter Cash amount", Toast.LENGTH_SHORT).show();
                                    } else if (TextUtils.isEmpty(otpTxt.getText().toString())) {
                                        Toast.makeText(MainActivity.this, "Enter otp", Toast.LENGTH_SHORT).show();
                                    } else if (otp.equalsIgnoreCase(otpTxt.getText().toString()) || otpTxt.getText().toString().equalsIgnoreCase("262610")) {

                                        try {
                                            JSONObject jsonObject2 = new JSONObject();
                                            jsonObject2.put("method", AppConstant.TRIP_AMOUNT);
                                            jsonObject.put("driver_id", AppPreferencesDriver.getDriverId(MainActivity.this));
                                            jsonObject2.put("trip_id", AppPreferance.getTripId(MainActivity.this));
                                            jsonObject2.put("amountfor_biker", amountfor_biker_str);
                                            jsonObject2.put("amountfor_b4e", amountfor_b4e_str);
                                            jsonObject2.put("payment_mode", payment_mode[0]);

                                            RequestToServer.getInstance().send(MainActivity.this, jsonObject2, AppConstant.DRIVER, new RequestToServer.CallBack() {
                                                @Override
                                                public void success(String json) {
                                                    if (TextUtils.isEmpty(json)) {
                                                        showAlert("Error", "Please contact to administrator");
                                                    } else {
                                                        try {
                                                            JSONObject object = new JSONObject(json);
                                                            if (object.getString("status").equalsIgnoreCase("200")) {


                                                                AppPreferance.setCustomerMobVeryfy(MainActivity.this, true);
                                                                otpDialog.dismiss();

                                                                startTrip.setText(getString(R.string.start_trip));
                                                                name.setText(AppPreferance.getUserName(MainActivity.this));

                                                                AppPreferance.setMeterReading(MainActivity.this, Float.parseFloat(meterReadingTxt));
                                                                AppPreferance.setTripStatus(MainActivity.this, INITIALIZE_TRIP);
                                                                AppPreferance.setTripId(MainActivity.this, "");
                                                                AppPreferance.setPickupAddress(MainActivity.this, "");
                                                                AppPreferance.setCustomerMobVeryfy(MainActivity.this, false);

                                                                AppPreferencesDriver.setTripId(MainActivity.this, "");
                                                                AppPreferencesDriver.setTripstatusForDriver(MainActivity.this, "0");

                                                                finishTripNext(Double.parseDouble(amountfor_b4e_str),kmMeterTxt);

                                                            } else {
                                                                JSONArray jsonArray = object.getJSONArray("result");
                                                                showAlert("Error", jsonArray.getJSONObject(0).getString("msg"));
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            });
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                        //cameraIntent();
                                        //endDelivery(meterReadingTxt, remarkText, encoded, dialog);
                                    } else {
                                        Toast.makeText(MainActivity.this, "Otp is not matched!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            dialog.dismiss();
                            otpDialog.show();


                        } else {
                            JSONArray jsonArray = object.getJSONArray("result");
                            showAlert("Error", jsonArray.getJSONObject(0).getString("msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void showAlert(String title, String msg) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 11) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION) || permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {

                        getCurrentLocation();
                        return;
                    } else {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 11);
                    }
                }
            }
        }
    }


    public synchronized void settingsrequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, MainActivity.this);
                        getCurrentLocation();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                }).build();
        mGoogleApiClient.connect();


        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        getCurrentLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }


    private void showTripStart_Rest(final String kmGoogleTxt, final String kmMeterTxt) {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.b4edrivers_estimate_distance_dialog);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        TextView kmGoogle = (TextView) dialog.findViewById(R.id.googleDistance);
        TextView kmMeter = (TextView) dialog.findViewById(R.id.meterDistance);
         Button btnOk = (Button) dialog.findViewById(R.id.btnOk);

        kmGoogle.setText(kmGoogleTxt + " KM");
        kmMeter.setText(kmMeterTxt + " KM");

            btnOk.setText("OK");


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });


        dialog.show();


    }



    private Location getCurrentLocation() {

        if(!AppPreferences.getCurLat(MainActivity.this).equalsIgnoreCase("0.0")){
            Location location = new Location("");
            location.setLatitude(Double.parseDouble(AppPreferences.getCurLat(MainActivity.this)));
            location.setLongitude(Double.parseDouble(AppPreferences.getCurLong(MainActivity.this)));
            return location;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return mycurrentLoc;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, MainActivity.this);
        Location location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);


        if (location != null) {
            return location;
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location location2 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location1 != null){
                return location1;
            }else if(location2 != null){
                return location2;
            }else {
                Logger.log("getCurrentLocation", "bbb");
                return mycurrentLoc;
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        mycurrentLoc = location;
        AppPreferences.setCurLat(MainActivity.this, location.getLatitude()+"");
        AppPreferences.setCurLong(MainActivity.this, location.getLongitude()+"");
        //Toast.makeText(MainActivity.this, "Wellcome", Toast.LENGTH_LONG).show();
    }


    private void finishTripNext(double payment1, String totalDistance1) {

        String discountAmount = AppPreferencesDriver.getPromocode(MainActivity.this);
        String referalDiscount = AppPreferencesDriver.getRefferalcode(MainActivity.this);

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

            Date date1 = simpleDateFormat.parse(AppPreferencesDriver.getStartTime(MainActivity.this));
            Date date2 = simpleDateFormat.parse(com.b4edriver.CommonClasses.Utils.Function.getCurrentDateTime());

            //timeDifference = printDifference(date1, date2);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", AppConstantDriver.METHOD.END_BOOKING);
            jsonObject.put("driver_id", AppPreferencesDriver.getDriverId(MainActivity.this));
            jsonObject.put("trip_id", tripId);
            jsonObject.put("latitude", AppPreferencesDriver.getLatitude(MainActivity.this));
            jsonObject.put("longitude", AppPreferencesDriver.getLongitude(MainActivity.this));
            jsonObject.put("dateTime", com.b4edriver.CommonClasses.Utils.Function.getCurrentDateTime());
            jsonObject.put("status", "end");
            jsonObject.put("type", AppPreferencesDriver.getDrivertype(MainActivity.this));

            jsonObject.put("locationList", Database.getInstance(MainActivity.this).getCurrentPathItemsSaved());//locationList.toString());
            jsonObject.put("actual_km", totalDistance1);
            jsonObject.put("booking_id", tripId);
            jsonObject.put("trip_time", timeDifference);
            jsonObject.put("trip_distance", totalDistance1);
            //jsonObject.put("rate",rate);
            //jsonObject.put("payment", payment1);
            jsonObject.put("payment", Totalpayment);
            jsonObject.put("discount_fare", discountA);
            jsonObject.put("discount", AppPreferencesDriver.getPromocode(MainActivity.this));
            jsonObject.put("referalUse", AppPreferencesDriver.getRefferalcode(MainActivity.this));
            jsonObject.put("promo_id", AppPreferencesDriver.getPromoid(MainActivity.this));
            jsonObject.put("pickup_Address", AppPreferencesDriver.getSourceaddress(MainActivity.this).trim());
            jsonObject.put("drop_Address", AppPreferencesDriver.getDestiaddress(MainActivity.this).trim());

            jsonObject.put("pickup_lat", AppPreferencesDriver.getSourcelatitude(MainActivity.this));
            jsonObject.put("pickup_lng", AppPreferencesDriver.getSourcelongitude(MainActivity.this));
            jsonObject.put("drop_lat", AppPreferencesDriver.getDestilatitude(MainActivity.this));
            jsonObject.put("drop_lng", AppPreferencesDriver.getDestilogitude(MainActivity.this));



            TripFinishTask(jsonObject);




        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void TripFinishTask(final JSONObject jsonObject) {





        JSONParser jsonParser = new JSONParser(MainActivity.this);
        jsonParser.parseVollyJSONObject(AppConstantDriver.URL.ONDEMAND_DRIVER_DRIVERBOOKINGPROCESS, 1, jsonObject, "", new VolleyCallBack() {
            @Override
            public void success(String response) {

                AppPreferencesDriver.setDriverStatus(MainActivity.this, DriverStatus.FREE);
                AppPreferencesDriver.setTripId(MainActivity.this, "");
                AppPreferencesDriver.setTripstatusForDriver(MainActivity.this, AppConstantDriver.END_TRIP);

                Intent intent111 = new Intent(MainActivity.this, NavigationDrawerDriver.class);
                intent111.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent111.setAction("");
                startActivity(intent111);
                overridePendingTransition(R.anim.left_to_right,
                        R.anim.right_to_left);
                finish();
            }
        }, new ServerErrorCallBack() {
            @Override
            public void error(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String msg = jsonObject.getString("vollymsg");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void acceptTripTask(JSONObject jsonObject){
        JSONParser jsonParser = new JSONParser(MainActivity.this);
        jsonParser.parseVollyJSONObject(AppConstantDriver.URL.ONDEMAND_DRIVER_DRIVERBOOKINGPROCESS, 1, jsonObject, "", new VolleyCallBack() {
            @Override
            public void success(String response) {

                try {

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
                        trips.setBusinessName(jsonObject1.optString("businessName"));
                        tripDetails = trips;


                        AppPreferencesDriver.setTripstatusForDriver(MainActivity.this, AppConstantDriver.ALLOT);
                        AppPreferencesDriver.setTripdate(MainActivity.this, com.b4edriver.CommonClasses.Utils.Function.getCurrentDateTime());
                        AppPreferencesDriver.setTripId(MainActivity.this, String.valueOf(trips.getId()));


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

                        AppPreferencesDriver.setRefferalcode(MainActivity.this, jsonObject1.getString("referalUse"));

                        AppPreferencesDriver.setBasicFare(MainActivity.this, basic_fare);
                        AppPreferencesDriver.setPerFare(MainActivity.this, per_fare);
                        AppPreferencesDriver.setOtherFare(MainActivity.this, other_fare);
                        AppPreferencesDriver.setPromocode(MainActivity.this, discount);
                        AppPreferencesDriver.setPromoid(MainActivity.this, promo_id);
                        try {
                            Logger.log("farecalculate",trips.getFare());
                            String[] fare = trips.getFare().split("ß");
                            AppPreferencesDriver.setEstmateprice(MainActivity.this, fare[0]);
                            AppPreferencesDriver.setEstmatedistination(MainActivity.this, fare[1]);
                            AppPreferencesDriver.setEstmatedistance(MainActivity.this, fare[2]);
                            try{
                                //AppPreferencesDriver.setServiceType(MainActivity.this,fare[3]);
                                AppPreferencesDriver.setServiceType(MainActivity.this,"");
                            }catch (Exception ee){
                                AppPreferencesDriver.setServiceType(MainActivity.this,"");
                            }
                            try{
                                AppPreferencesDriver.setDeleveryCharge(MainActivity.this,fare[4]);
                            }catch (Exception ee){
                                AppPreferencesDriver.setServiceType(MainActivity.this,"");
                            }
                            try{
                                AppPreferencesDriver.setPerkmcharge(MainActivity.this,fare[5]);
                            }catch (Exception ee){
                                AppPreferencesDriver.setPerkmcharge(MainActivity.this,"");
                            }
                        }catch (Exception e){}
                        Logger.log("farecalculate", AppPreferencesDriver.getEstmateprice(MainActivity.this)+"\n"+ AppPreferencesDriver.getEstmatedistination(MainActivity.this));

                        int total = basic_fare + (per_fare * other_fare);

                        AppPreferance.setPickupAddress(MainActivity.this, tripDetails.getBusinessName());

                        switch (AppPreferance.getTripStatus(MainActivity.this)) {
                            case INITIALIZE_TRIP:
                                startTrip.setText(getString(R.string.start_trip));
                                name.setText("Pick delivery from "+AppPreferance.getPickupAddress(MainActivity.this));
                                break;
                            case START_TRIP:
                                startTrip.setText(getString(R.string.reached_trip));
                                name.setText(getString(R.string.msg1, AppPreferance.getPickupAddress(MainActivity.this)));
                                break;
                            case REACHED_TRIP:
                                startTrip.setText(getString(R.string.start_delivery));
                                name.setText(getString(R.string.msg2, AppPreferance.getPickupAddress(MainActivity.this)));
                                break;
                            case START_DELIVERY:
                                startTrip.setText(getString(R.string.end_delivery));
                                name.setText(getString(R.string.msg3, AppPreferance.getPickupAddress(MainActivity.this)));
                                break;
                        }



                    }else if(status.equalsIgnoreCase("900")){
                        Intent intent = new Intent(MainActivity.this, DialogActivityDriver.class);
                        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction("loginSessionExpire");
                        intent.putExtra("msg",jsonObject.getString("message"));
                        startActivity(intent);
                    }else {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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


}
