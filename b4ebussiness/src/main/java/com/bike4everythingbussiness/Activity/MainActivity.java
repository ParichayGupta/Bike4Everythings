package com.bike4everythingbussiness.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bike4everythingbussiness.Adapter.DropAddressAdapter;
import com.bike4everythingbussiness.Adapter.PickupAddressAdapter;
import com.bike4everythingbussiness.Adapter.UploadedImageAdapter;
import com.bike4everythingbussiness.Adapter.UploadedImageAdapter.OnClickListener;
import com.bike4everythingbussiness.BuildConfig;
import com.bike4everythingbussiness.DatabaseHandler;
import com.bike4everythingbussiness.Model.DropAddress;
import com.bike4everythingbussiness.Model.FareCard;
import com.bike4everythingbussiness.Model.ImageUploadModel;
import com.bike4everythingbussiness.Model.OrderDetails;
import com.bike4everythingbussiness.Model.PickAddress;
import com.bike4everythingbussiness.Model.User;
import com.bike4everythingbussiness.Model.Wallet;
import com.bike4everythingbussiness.MyApplication;
import com.bike4everythingbussiness.R;
import com.bike4everythingbussiness.Services.RequestToServer;
import com.bike4everythingbussiness.Utils.AppConstant;
import com.bike4everythingbussiness.Utils.AppPreferance;
import com.bike4everythingbussiness.Utils.Config;
import com.bike4everythingbussiness.Utils.ConnectivityReceiver;
import com.bike4everythingbussiness.Utils.DirectionsJSONParser;
import com.bike4everythingbussiness.Utils.Function;
import com.bike4everythingbussiness.Utils.Logger;
import com.blikoon.qrcodescanner.QrCodeActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.bike4everythingbussiness.Services.SyncDBService.syncDatabase;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, PickupAddressAdapter.OnItemSelectLIstener,
        ConnectivityReceiver.ConnectivityReceiverListener, DropAddressAdapter.OnItemClickListener, OnClickListener {
    private static final int PICKUP_ADDRESS_REQUESTCODE = 1;
    private static final int DROP_ADDRESS_REQUESTCODE = 2;
    public static final int DROP_ADDRESS_ADAPTER_REQUESTCODE = 3;
    public static final int RE_ORDER_REQUEST = 4;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 5;

    private static final int QRCODE_READER = 6;

    private static final int REQUEST_STORAGE = 11;
    private static final int REQUEST_CAMERA = 12;
    private static final int REQUEST_IMAGE_CAPTURE = REQUEST_STORAGE + 1;
    private static final int REQUEST_LOAD_IMAGE = REQUEST_IMAGE_CAPTURE + 1;
    private static final int PIC_CROP = REQUEST_LOAD_IMAGE + 1;


    @BindView(R.id.schedule_delivery)
    SwitchCompat scheduleDelivery;
    @BindView(R.id.pickup_address)
    TextView pickupAddress;
    @BindView(R.id.picupRecyclerview)
    RecyclerView pickupRecyclerview;
    @BindView(R.id.dropRecyclerview)
    RecyclerView dropRecyclerview;
    @BindView(R.id.booking)
    FloatingActionButton booking;
    @BindView(R.id.scheduleTime)
    TextView scheduleTime;
    @BindView(R.id.addanotherstop)
    Button addanotherstop;
    @BindView(R.id.dropMobile)
    EditText dropmobile;
    @BindView(R.id.dropsearchMobile)
    ImageButton dropsearchMobile;
    @BindView(R.id.dropshowMobileNo)
    LinearLayout dropshowMobileNo;
    @BindView(R.id.dropName)
    EditText dropname;
    @BindView(R.id.dropAddress)
    EditText dropaddress;
    @BindView(R.id.dropAmount)
    EditText dropamount;
    @BindView(R.id.dropaddBtn)
    Button dropaddBtn;
    @BindView(R.id.dropcancelBtn)
    TextView dropcancelBtn;
    @BindView(R.id.dropshowOtherDetails)
    LinearLayout dropshowOtherDetails;


    DropAddressAdapter dropAddressAdapter;
    PickupAddressAdapter pickupAddressAdapter;
    @BindView(R.id.dropMainView)
    LinearLayout dropMainView;
    @BindView(R.id.addpickupaddress)
    Button addpickupaddress;
    @BindView(R.id.pickupaddressList)
    LinearLayout pickupaddressList;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.stopCount)
    TextView stopCount;
    @BindView(R.id.notes)
    EditText notes;
    @BindView(R.id.returnRequired)
    SwitchCompat returnRequired;

    double dropLat, dropLng;
    @BindView(R.id.estfare_view)
    TextView estfareView;
    @BindView(R.id.estfare_progressbar)
    ProgressBar estfareProgressbar;
    List<String> waypoints;

    ArrayList<ImageUploadModel> imageList;
    @BindView(R.id.takepicture)
    Button takepicture;
    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;
    private UploadedImageAdapter adapter;

    JsonArray jsonArrayImages = new JsonArray();
    private JsonObject jsonObjectImages;
    private String tempDimen = "", tempRemark = "";
    private ImageView profileImage;
    private TextView profileName;
    private TextView profileMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        syncDatabase(MainActivity.this, new String[]{ AppConstant.GET_RATECARD, AppConstant.GET_ALL_PICKUP_ADDRESS});

        LocalBroadcastManager.getInstance(this).registerReceiver(profileUpdate, new IntentFilter("profileUpdate"));

        new GetAppSettings().execute();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });

         profileImage = headerView.findViewById(R.id.icon);
        profileName = headerView.findViewById(R.id.name);
        profileMobile = headerView.findViewById(R.id.mobile);
        String image = currentUser.getProfile_image();
        Logger.log("ProfileImage", image +"   ?>>>");
        Glide.with(MainActivity.this).load(image.contains("http") ? image : Uri.parse(image)).asBitmap().centerCrop().into(new BitmapImageViewTarget(profileImage) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                profileImage.setImageDrawable(circularBitmapDrawable);
            }
        });


        profileName.setText(currentUser.getName());
        profileMobile.setText(currentUser.getPhone_number());

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }*/


        try {
            pickupAddressAdapter = new PickupAddressAdapter(MainActivity.this, new GetPickupFrom().execute().get());
            pickupAddressAdapter.setListener(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getApplicationContext());
        pickupRecyclerview.setLayoutManager(mLayoutManager1);
        pickupRecyclerview.setItemAnimator(new DefaultItemAnimator());
        pickupRecyclerview.setAdapter(pickupAddressAdapter);
        pickupRecyclerview.setNestedScrollingEnabled(true);


        dropMainView.setVisibility(View.VISIBLE);
        addanotherstop.setVisibility(View.GONE);
        dropcancelBtn.setVisibility(View.GONE);
        stopCount.setText("");


        try {
            dropAddressAdapter = new DropAddressAdapter(MainActivity.this, new GetDeliverTo().execute().get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        dropAddressAdapter.setListener(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        dropRecyclerview.setLayoutManager(mLayoutManager);
        dropRecyclerview.setItemAnimator(new DefaultItemAnimator());
        dropRecyclerview.setAdapter(dropAddressAdapter);
        dropRecyclerview.setNestedScrollingEnabled(true);

        scheduleDelivery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    scheduleTime.setVisibility(View.VISIBLE);
                    openDatePicker();
                } else {
                    scheduleTime.setVisibility(View.GONE);
                    scheduleTime.setText("");
                }
            }
        });

        returnRequired.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });


        imageList = new ArrayList<ImageUploadModel>();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
            }
        }

        setImageAdapter();


    }

    private void setImageAdapter() {
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(MainActivity.this);
        recycler_view.setLayoutManager(manager);
        adapter = new UploadedImageAdapter(MainActivity.this, imageList, this);
        recycler_view.setAdapter(adapter);
    }


    private void openDatePicker() {
        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        Logger.log("TIMEDATE", dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        openTimePicker(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }

                }, mYear, mMonth, mDay);
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    scheduleDelivery.setChecked(false);
                    scheduleTime.setVisibility(View.GONE);
                    scheduleTime.setText("");
                }
            }
        });

        datePickerDialog.show();
    }

    private void openTimePicker(final String date) {
        final Calendar c = Calendar.getInstance();

        int mHour_ = c.get(Calendar.HOUR_OF_DAY);
        int mMinute_ = c.get(Calendar.MINUTE);

        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        String AMPM = "";
        if (mHour > 12) {
            mHour = mHour - 12;
            AMPM = "PM";
        } else if (mHour == 12) {
            mHour = mHour;
            AMPM = "PM";
        } else if (mHour < 12) {
            if (mHour != 0) {
                mHour = mHour;
                AMPM = "AM";
            } else {
                mHour = 12;
                AMPM = "AM";
            }
        }

        c.add(Calendar.MINUTE, 15);
        int mHour_15 = c.get(Calendar.HOUR_OF_DAY);
        int mMinute_15 = c.get(Calendar.MINUTE);
        String AMPM_15 = "";
        if (mHour_15 > 12) {
            mHour_15 = mHour_15 - 12;
            AMPM_15 = "PM";
        } else if (mHour_15 == 12) {
            mHour_15 = mHour_15;
            AMPM_15 = "PM";
        } else if (mHour_15 < 12) {
            if (mHour_15 != 0) {
                mHour_15 = mHour_15;
                AMPM_15 = "AM";
            } else {
                mHour_15 = 12;
                AMPM_15 = "AM";
            }
        }

        // System.out.println("formattedDate "+formattedDate);

        final TextView fromtime, totime, am_pm;
        Button no_btn, yes_btn;

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.timepicker_dialog);

        fromtime = dialog.findViewById(R.id.fromtime);
        totime = dialog.findViewById(R.id.totime);
        am_pm = dialog.findViewById(R.id.am_pm);
        no_btn = dialog.findViewById(R.id.no_btn);
        yes_btn = dialog.findViewById(R.id.yes_btn);

        String sHour = "00";
        if (mHour < 10) {
            sHour = "0" + mHour;
        } else {
            sHour = String.valueOf(mHour);
        }

        String sMinute = "00";
        if (mMinute < 10) {
            sMinute = "0" + mMinute;
        } else {
            sMinute = String.valueOf(mMinute);
        }

        String sHour_15 = "00";
        if (mHour_15 < 10) {
            sHour_15 = "0" + mHour_15;
        } else {
            sHour_15 = String.valueOf(mHour_15);
        }

        String sMinute_15 = "00";
        if (mMinute_15 < 10) {
            sMinute_15 = "0" + mMinute_15;
        } else {
            sMinute_15 = String.valueOf(mMinute_15);
        }

        fromtime.setText(sHour + ":" + sMinute);
        totime.setText(sHour_15 + ":" + sMinute_15);


        am_pm.setText(AMPM_15);

        TimePicker simpleTimePicker = (TimePicker) dialog.findViewById(R.id.timePicker); // initiate a time picker


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            simpleTimePicker.setHour(mHour_);
            simpleTimePicker.setMinute(mMinute_);
        } else {
            simpleTimePicker.setCurrentHour(mHour_); // before api level 23
            simpleTimePicker.setCurrentMinute(mMinute_);
        }
        simpleTimePicker.setIs24HourView(false);

        simpleTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {


                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                int mHourTp = calendar.get(Calendar.HOUR_OF_DAY);
                int mMinuteTp = calendar.get(Calendar.MINUTE);
                Logger.log("timechange", hourOfDay + "::" + minute + "\n"
                        + mHourTp + "::" + mMinuteTp);

                if (mHourTp > 12) {
                    mHourTp = mHourTp - 12;
                } else if (mHourTp == 12) {
                    mHourTp = mHourTp;
                } else if (mHourTp < 12) {
                    if (mHourTp != 0) {
                        mHourTp = mHourTp;
                    } else {
                        mHourTp = 12;
                    }
                }

                calendar.add(Calendar.MINUTE, 15);
                int mHourTp_15 = calendar.get(Calendar.HOUR_OF_DAY);
                int mMinuteTp_15 = calendar.get(Calendar.MINUTE);
                String AMPMTP_15 = "";
                if (mHourTp_15 > 12) {
                    mHourTp_15 = mHourTp_15 - 12;
                    AMPMTP_15 = "PM";
                } else if (mHourTp_15 == 12) {
                    mHourTp_15 = mHourTp_15;
                    AMPMTP_15 = "PM";
                } else if (mHourTp_15 < 12) {
                    if (mHourTp_15 != 0) {
                        mHourTp_15 = mHourTp_15;
                        AMPMTP_15 = "AM";
                    } else {
                        mHourTp_15 = 12;
                        AMPMTP_15 = "AM";
                    }
                }


                String sHourTp = "00";
                if (mHourTp < 10) {
                    sHourTp = "0" + mHourTp;
                } else {
                    sHourTp = String.valueOf(mHourTp);
                }

                String sMinuteTp = "00";
                if (mMinuteTp < 10) {
                    sMinuteTp = "0" + mMinuteTp;
                } else {
                    sMinuteTp = String.valueOf(mMinuteTp);
                }

                String sHourTp_15 = "00";
                if (mHourTp_15 < 10) {
                    sHourTp_15 = "0" + mHourTp_15;
                } else {
                    sHourTp_15 = String.valueOf(mHourTp_15);
                }

                String sMinuteTp_15 = "00";
                if (mMinuteTp_15 < 10) {
                    sMinuteTp_15 = "0" + mMinuteTp_15;
                } else {
                    sMinuteTp_15 = String.valueOf(mMinuteTp_15);
                }

                fromtime.setText(sHourTp + ":" + sMinuteTp);
                totime.setText(sHourTp_15 + ":" + sMinuteTp_15);


                am_pm.setText(AMPMTP_15);


            }
        });

        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //+ " - " + totime.getText().toString()
                scheduleTime.setText(dateFormat(date) + ", " + fromtime.getText().toString() + " " + am_pm.getText().toString());
                dialog.dismiss();
            }
        });
        no_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scheduleDelivery.setChecked(false);
                scheduleTime.setVisibility(View.GONE);
                scheduleTime.setText("");
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_newdelevery) {
            // Handle the camera action
        } else if (id == R.id.nav_deliveries) {
            Intent intent = new Intent(MainActivity.this, DeliveriesActivity.class);
            startActivityForResult(intent, RE_ORDER_REQUEST);
        } else if (id == R.id.nav_wallet) {
            syncDatabase(MainActivity.this, new String[]{AppConstant.GET_ALLTRIP});
            startActivity(new Intent(MainActivity.this, WalletActivity.class));
        } else if (id == R.id.nav_notification) {

        } else if (id == R.id.nav_ratecard) {
            startActivity(new Intent(MainActivity.this, RateCardActivity.class));
        } else if (id == R.id.nav_tc) {

        } else if (id == R.id.nav_support) {
            startActivity(new Intent(MainActivity.this, SupportActivity.class));
        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_logout) {

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("LOG OUT")
                    .setMessage("Are you sure to log out?")
                    .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            AppPreferance.setUserid(MainActivity.this, 0);

                            startActivity(new Intent(MainActivity.this, SigninActivity.class));
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isShowingNetworkDialog()) {
            dismissNetworkDialog();
        }
        if (!isConnected) {
            showNetworkDialog(MainActivity.this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void pickupAddressSelect(String name) {
        pickupAddress.setText(name);
        pickupAddress.setVisibility(View.VISIBLE);
        pickupaddressList.setVisibility(View.GONE);
    }

    @Override
    public void pickupAddressDelete(int id) {
        new DeletePickupAddress(id).execute();
    }

    @OnClick(R.id.pickup_address)
    public void onPickupAddressClicked() {
        pickupAddress.setVisibility(View.GONE);
        pickupaddressList.setVisibility(View.VISIBLE);
        resetEstFare();
    }

    @OnClick(R.id.booking)
    public void onBookingClicked() {
        calculateEstimateAmount(true);

    }

    @OnClick(R.id.addanotherstop)
    public void onAddanotherstopClicked() {
        addanotherstop.setVisibility(View.GONE);
        dropshowOtherDetails.setVisibility(View.GONE);
        dropMainView.setVisibility(View.VISIBLE);

        dropcancelBtn.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.dropsearchMobile)
    public void onDropsearchMobileClicked() {
        if (TextUtils.isEmpty(dropmobile.getText())) {
            dropmobile.startAnimation(shake);
        } else {
            DropAddress dropAddres = databaseHandler.getDropAddress(dropmobile.getText().toString());
            if (dropAddres != null) {
                dropname.setText(dropAddres.getDropName());
                dropaddress.setText(dropAddres.getDropAddress());
                dropLat = dropAddres.getDropLat();
                dropLng = dropAddres.getDropLng();
            }
            dropshowOtherDetails.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.dropaddBtn)
    public void onDropaddBtnClicked() {

        if (TextUtils.isEmpty(dropmobile.getText())) {
            dropmobile.startAnimation(shake);
        } else if (TextUtils.isEmpty(dropname.getText())) {
            dropname.startAnimation(shake);
        } else if (TextUtils.isEmpty(dropaddress.getText())) {
            dropaddress.startAnimation(shake);
        } else if (TextUtils.isEmpty(dropamount.getText())) {
            dropamount.startAnimation(shake);
        } else {

            DropAddress dropAddress = new DropAddress();
            dropAddress.setId(String.valueOf(AppPreferance.getUserid(MainActivity.this)));
            dropAddress.setDropName(dropname.getText().toString());
            dropAddress.setDropMobile(dropmobile.getText().toString());
            dropAddress.setDropAddress(dropaddress.getText().toString());
            dropAddress.setDropAmount(dropamount.getText().toString());
            dropAddress.setDropLat(dropLat);
            dropAddress.setDropLng(dropLng);
            dropAddress.setSelect(true);


            dropMainView.setVisibility(View.GONE);
            addanotherstop.setVisibility(View.VISIBLE);

            dropAddressAdapter.setData(dropAddress);

            DropAddress dropAddres = databaseHandler.getDropAddress(dropmobile.getText().toString());
            if (dropAddres == null) {
                databaseHandler.addDropAddress(dropAddress);
            } else {
                databaseHandler.updateDropAddress(dropAddress);
            }

            dropmobile.setText("");
            dropname.setText("");
            dropaddress.setText("");
            dropamount.setText("");
            stopCount.setText(dropAddressAdapter.getItemCount() + " Stop");
            resetEstFare();
        }
    }

    @OnClick(R.id.dropcancelBtn)
    public void onDropcancelBtnClicked() {
        dropshowOtherDetails.setVisibility(View.GONE);
        dropMainView.setVisibility(View.GONE);
        addanotherstop.setVisibility(View.VISIBLE);
    }

    @Override
    public void dropAddressRemove(int itemCount) {
        if (itemCount == 1) {
            addanotherstop.setVisibility(View.GONE);
            dropshowOtherDetails.setVisibility(View.GONE);
            dropMainView.setVisibility(View.VISIBLE);
        } else {
        }
        int count = databaseHandler.getDropSelectedCount(true);
        if (count == 0) {
            stopCount.setText("");
        } else {
            stopCount.setText(count + " Stop");
        }
        resetEstFare();
        dropAddressAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.addpickupaddress, R.id.dropAddress, R.id.estfare_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dropAddress:
                dropaddress.setEnabled(false);
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(MainActivity.this), DROP_ADDRESS_REQUESTCODE);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.addpickupaddress:
                Intent intent = new Intent(MainActivity.this, PickupAddressActivity.class);
                startActivityForResult(intent, PICKUP_ADDRESS_REQUESTCODE);
                break;
            case R.id.estfare_view:

               calculateEstimateAmount(false);

                break;
        }

    }

    private void calculateEstimateAmount(boolean isBooking) {
        PickAddress pickAddress = databaseHandler.getSelectedPickupAddress(true);
        List<DropAddress> dropAddressList = databaseHandler.getDropSelectedAddress(true);

        if (pickAddress == null) {
            addpickupaddress.startAnimation(shake);
            addpickupaddress.requestFocus();
        } else if (dropAddressList == null) {
            dropshowMobileNo.startAnimation(shake);
            dropmobile.requestFocus();
        } else {
            estfareView.setVisibility(View.GONE);
            estfareProgressbar.setVisibility(View.VISIBLE);
            LatLng pickupLatLng = new LatLng(pickAddress.getPickLat(), pickAddress.getPickLng());
            LatLng dropLatLng = null;
            waypoints = new ArrayList<>();
            for (int i = 0; i < dropAddressList.size(); i++) {
                if (i == dropAddressList.size() - 1) {
                    dropLatLng = new LatLng(dropAddressList.get(i).getDropLat(), dropAddressList.get(i).getDropLng());
                } else {
                    waypoints.add(dropAddressList.get(i).getDropLat() + "," + dropAddressList.get(i).getDropLng());
                }

            }


            String url = Function.getDirectionsUrlWaypont(pickupLatLng, dropLatLng, waypoints);
            // Logger.log("routes", url);
            RoutesDownloadTask downloadTask = new RoutesDownloadTask(MainActivity.this, isBooking);
            downloadTask.execute(url);


        }
    }

    @OnClick(R.id.returnRequired)
    public void onViewClicked() {
        resetEstFare();
        Toast.makeText(MainActivity.this, "Turn it on if you want the driver to return to the pick up location after completing the deliveries.", Toast.LENGTH_LONG).show();
    }

    private void resetEstFare() {
        estfareProgressbar.setVisibility(View.GONE);
        estfareView.setVisibility(View.VISIBLE);
        estfareView.setClickable(true);
        estfareView.setText("View");
        estfareView.setTextColor(getResources().getColor(R.color.white));
        estfareView.setBackgroundResource(R.drawable.signin_btn);
    }

    @OnClick(R.id.takepicture)
    public void onTakepictureClicked() {
        selectImage();
    }

    private void selectImage() {
        final CharSequence[] items = {"QR Code","Take Photo", "Choose from gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Select Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
               // boolean result = UtilClass.checkPermission(MainActivity.this);
                if (items[item].equals("QR Code")) {
                   // if (result)
                        qrReader();
                } else   if (items[item].equals("Take Photo")) {
                   // if (result)
                        cameraIntent();
                } else if (items[item].equals("Choose from gallery")) {
                  //  if (result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        startActivityForResult(createPickIntent(), REQUEST_LOAD_IMAGE);
    }

    @Nullable
    private Intent createPickIntent() {
        Intent picImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (picImageIntent.resolveActivity(getPackageManager()) != null) {
            return picImageIntent;
        } else {
            return null;
        }
    }

    private void qrReader(){
        Intent intent = new Intent(MainActivity.this, QrCodeActivity.class);
        startActivityForResult(intent, QRCODE_READER);
    }

    private void cameraIntent() {

        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/b4ebisness/";
        File newdir = new File(dir);
        newdir.mkdirs();

        String file = dir + "activityimg.jpg";
        Logger.log("imagesss cam11", file);
        File newfile = new File(file);
        try {
            newfile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();

        }
        //final Uri outputFileUri = Uri.fromFile(newfile);
        final Uri outputFileUri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            outputFileUri = FileProvider.getUriForFile(MainActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider", newfile);
        } else {
            outputFileUri = Uri.fromFile(newfile);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_CAMERA);

    }


    private void showPreviewDialog(final Bitmap bitmap1, String heightTxt, String widthTxt, String lengthTxt, String remarkTxt, final int position, final boolean isUpdate) {
       final Dialog dialog = new Dialog(MainActivity.this, R.style.image_preview_dialog);
        dialog.setContentView(R.layout.image_setup_layout);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        final ImageView imageView = (ImageView) dialog.findViewById(R.id.img_circle);
        NestedScrollView container = (NestedScrollView) dialog.findViewById(R.id.container);

        ImageView imgBack = (ImageView) dialog.findViewById(R.id.imgBack);
        Button btnSave = (Button) dialog.findViewById(R.id.btnSave);
        final EditText height = (EditText) dialog.findViewById(R.id.dimensionTextheight);
        final EditText width = (EditText) dialog.findViewById(R.id.dimensionTextwidth);
        final EditText length = (EditText) dialog.findViewById(R.id.dimensionTextLength);
        final EditText remarkText = (EditText) dialog.findViewById(R.id.remarkText);

        //String croppedfilePath = Environment.getExternalStorageDirectory() + "/activityimage.jpg";
        final Bitmap[] bitmap = {bitmap1};
        if(bitmap[0] == null){
            Glide.with(MainActivity.this).load(remarkTxt).asBitmap().centerCrop().override(800,600).into(new BitmapImageViewTarget(imageView) {
                @Override
                protected void setResource(Bitmap resource) {
                    imageView.setImageBitmap(resource);
                    bitmap[0] = resource;
                }
            });
        }else{

            imageView.setImageBitmap(bitmap[0]);
            remarkText.setText(remarkTxt);
        }
        height.setText(heightTxt);
        width.setText(widthTxt);
        length.setText(lengthTxt);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (height.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(MainActivity.this, "Enter height", Toast.LENGTH_SHORT).show();
                else if (width.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(MainActivity.this, "Enter width", Toast.LENGTH_SHORT).show();
                else if  (length.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(MainActivity.this, "Enter width", Toast.LENGTH_SHORT).show();
                else if (remarkText.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(MainActivity.this, "Enter remark", Toast.LENGTH_SHORT).show();
                else {
                    tempDimen = height.getText().toString() + "x" + width.getText().toString() + "x" + length.getText().toString();
                    //Log.e(TAG, "tempDimen: " + tempDimen);
                    tempRemark = remarkText.getText().toString();
                    ImageUploadModel model = new ImageUploadModel(bitmap[0], tempDimen, tempRemark);
                    if(isUpdate){
                        imageList.set(position,model);
                        adapter.notifyItemChanged(position);
                        new ImageTask().execute(bitmap[0]);
                    }else {
                        imageList.add(model);
                        adapter.notifyDataSetChanged();
                        new ImageTask().execute(bitmap[0]);
                    }
                    if(adapter.getItemCount() >= 3){
                        takepicture.setVisibility(View.GONE);
                    }else {
                        takepicture.setVisibility(View.VISIBLE);
                    }
                    dialog.dismiss();
                }
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
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

    @Override
    public void editItem(Bitmap image, String dimension, String remark, int position) {
        String[] dimen = dimension.split("x");
        showPreviewDialog(image, dimen[0], dimen[1], dimen[2],remark,position, true);
    }

    @Override
    public void deleteItem(int position) {
        imageList.remove(position);
        adapter.notifyItemRemoved(position);
    }

    class ImageTask extends AsyncTask<Bitmap, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Bitmap... bitmaps) {
            Bitmap bitmapImage = bitmaps[0];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String Encoded_userimage = Base64.encodeToString(byteArray, Base64.DEFAULT);
            //Logger.log(TAG, "Picture Image :-" + Encoded_userimage);
            jsonObjectImages = new JsonObject();
            try {
                jsonObjectImages.addProperty("image", Encoded_userimage);
                jsonObjectImages.addProperty("remark", tempRemark);
                jsonObjectImages.addProperty("dimension", tempDimen);
                jsonArrayImages.add(jsonObjectImages);

                publishProgress();
            } catch (Exception e) {
                publishProgress();
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                publishProgress();
                e.printStackTrace();
            }
            return null;
        }

        private void publishProgress() {
            //hideProgressDialog();
        }
    }



    private class GetDeliverTo extends AsyncTask<Void, Void, List<DropAddress>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected List<DropAddress> doInBackground(Void... voids) {
            List<DropAddress> dropAddressList = databaseHandler.getDropSelectedAddress(true);

            if(dropAddressList == null){
                return new ArrayList<DropAddress>();
            }

            return dropAddressList;
        }

        @Override
        protected void onPostExecute(List<DropAddress> dropAddressList) {
            super.onPostExecute(dropAddressList);

            if (dropAddressList == null) {
                dropMainView.setVisibility(View.VISIBLE);
                addanotherstop.setVisibility(View.GONE);
                dropcancelBtn.setVisibility(View.GONE);
                stopCount.setText("");
                //return new ArrayList<DropAddress>();
            } else {
                dropMainView.setVisibility(View.GONE);
                addanotherstop.setVisibility(View.VISIBLE);
                stopCount.setText(dropAddressList.size() + " Stop");
            }
        }
    }

    private class GetAppSettings extends AsyncTask<String, Void, String> {

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        public GetAppSettings(){
        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject data = new JSONObject();
                data.put("method", AppConstant.APP_SETTINGS);

                Log.e("Response_Response", AppConstant.B4E_BUSINESS_WALLET + "\n" + data.toString());
                //json.put("notification", dataJson);
                RequestBody body = RequestBody.create(JSON, data.toString());
                Request request = new Request.Builder()
                        .url(AppConstant.B4E_BUSINESS_WALLET)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                result = response.body().string();
                Log.e("Response_Response", result);
            } catch (JSONException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(TextUtils.isEmpty(result)){

            }else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.optString("status").equals("200")){
                        JSONArray jsonArray = jsonObject.optJSONArray("result");
                        JSONObject object = jsonArray.optJSONObject(0);
                        MyApplication.setBookingRequiredAmount(Integer.parseInt(object.optString("minimum_wallet_fare")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    private class DeletePickupAddress extends AsyncTask<String, Void, Void> {

            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            int pickupId = 0;

            public DeletePickupAddress(int pickupId) {
                this.pickupId = pickupId;
            }

            @Override
            protected Void doInBackground(String... params) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject data = new JSONObject();
                    data.put("method", AppConstant.REMOVE_PICKUP_ADDRESS);
                    data.put("business_id", AppPreferance.getUserid(MainActivity.this));
                    data.put("pickup_id", pickupId);

                    Log.e("Response_Response", AppConstant.B4E_BUSINESS_ALL_DELIVERY + "\n" + data.toString());
                    //json.put("notification", dataJson);
                    RequestBody body = RequestBody.create(JSON, data.toString());
                    Request request = new Request.Builder()
                            .url(AppConstant.B4E_BUSINESS_ALL_DELIVERY)
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String result = response.body().string();


                } catch (JSONException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();

                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                databaseHandler.deletePickupAddress(pickupId);
                pickupAddressAdapter.delete(pickupId);

            }
        }

    private class GetPickupFrom extends AsyncTask<Void, Void, List<PickAddress>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected List<PickAddress> doInBackground(Void... voids) {
            List<PickAddress> pickupAddressList = databaseHandler.getAllPickupAddress();

            if(pickupAddressList == null){
                return new ArrayList<PickAddress>();
            }
            return pickupAddressList;
        }

        @Override
        protected void onPostExecute(List<PickAddress> pickupAddressList) {
            super.onPostExecute(pickupAddressList);

            if (pickupAddressList.isEmpty()) {
                pickupAddress.setVisibility(View.GONE);
                pickupaddressList.setVisibility(View.VISIBLE);
            } else {

                pickupAddress.setText(pickupAddressList.get(0).getPickName() + " | " + pickupAddressList.get(0).getPickMobile() + "\n" + pickupAddressList.get(0).getPickAddressName());
                pickupAddress.setVisibility(View.VISIBLE);
                pickupaddressList.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted

            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == PICKUP_ADDRESS_REQUESTCODE) {
            if (resultCode == RESULT_OK) {
                PickAddress pickAddress = intent.getParcelableExtra("address");
                pickupAddressAdapter.setData(pickAddress);


                pickupAddress.setText(pickAddress.getPickName() + " | " + pickAddress.getPickMobile() + "\n" + pickAddress.getPickAddressName());
                pickupAddress.setVisibility(View.VISIBLE);
                pickupaddressList.setVisibility(View.GONE);

                databaseHandler.addPickupAddress(pickAddress);

            }
        } else if (requestCode == DROP_ADDRESS_REQUESTCODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(intent, this);

                String placename = String.format("%s", place.getName());
                String addresses = String.format("%s", place.getAddress());
                String phone = String.format("%s", place.getPhoneNumber());
                dropLat = place.getLatLng().latitude;
                dropLng = place.getLatLng().longitude;
                dropaddress.setText(TextUtils.isEmpty(addresses) ? placename : addresses);
            }
            dropaddress.setEnabled(true);
        } else if (requestCode == DROP_ADDRESS_ADAPTER_REQUESTCODE && resultCode == RESULT_OK) {
            dropAddressAdapter.onActivityResult(requestCode, resultCode, intent);
        } else if (requestCode == RE_ORDER_REQUEST && resultCode == RESULT_OK) {
            OrderDetails orderDetails = intent.getParcelableExtra("orderDetails");

            PickAddress pickAddress = new PickAddress();
            pickAddress.setId(AppPreferance.getUserid(MainActivity.this));
            pickAddress.setPickAddress(orderDetails.getPickAddress());
            pickAddress.setPickAddressName(orderDetails.getPickAddressName());
            pickAddress.setPickName(orderDetails.getPickName());
            pickAddress.setPickMobile(orderDetails.getPickMobile());
            pickAddress.setPickLat(orderDetails.getPickLat());
            pickAddress.setPickLng(orderDetails.getPickLng());
            pickAddress.setSelect(true);

            pickupAddressAdapter.setData(pickAddress);

            pickupAddress.setText(pickAddress.getPickName() + " | " + pickAddress.getPickMobile() + "\n" + pickAddress.getPickAddressName());
            pickupAddress.setVisibility(View.VISIBLE);
            pickupaddressList.setVisibility(View.GONE);

            // databaseHandler.addPickupAddress(pickAddress);

            String schedule = orderDetails.getSchedule();
            if (TextUtils.isEmpty(schedule)) {
                scheduleDelivery.setVisibility(View.GONE);
            } else {
                scheduleDelivery.setVisibility(View.VISIBLE);
                scheduleDelivery.setText(schedule);
            }

            List<DropAddress> dropAddressList = orderDetails.getDropAddressList();

            dropAddressAdapter.clear();

            databaseHandler.updateAllDropStatus(false);

            for (int i = 0; i < dropAddressList.size(); i++) {
                DropAddress dropAddress = dropAddressList.get(i);
                dropAddress.setSelect(true);


                dropMainView.setVisibility(View.GONE);
                addanotherstop.setVisibility(View.VISIBLE);

                dropAddressAdapter.setData(dropAddress);

                DropAddress dropAddres = databaseHandler.getDropAddress(dropmobile.getText().toString());
                if (dropAddres == null) {
                    databaseHandler.addDropAddress(dropAddress);
                } else {
                    databaseHandler.updateDropAddress(dropAddress);
                }

                dropmobile.setText("");
                dropname.setText("");
                dropaddress.setText("");
                dropamount.setText("");
                stopCount.setText(dropAddressAdapter.getItemCount() + " Stop");
            }

            notes.setText(orderDetails.getNote());
            returnRequired.setChecked(orderDetails.isReturnrequired());

            resetEstFare();

        }else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_LOAD_IMAGE && intent != null) {

            Uri selectedImage = null;
            selectedImage = intent.getData();
            //originalImageUri = data.getData();
            try {
                performCrop(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            File croppedImageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    + "/b4ebisness/" + "activityimg.jpg");

            final Uri originalFileUri;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {

                originalFileUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", croppedImageFile);
            } else {

                originalFileUri = Uri.fromFile(croppedImageFile);
            }


            try {
                performCrop(originalFileUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        //} else if (requestCode == PIC_CROP) {
        } else if (resultCode == Activity.RESULT_OK && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(intent);
            if (resultCode == RESULT_OK) {
                Bitmap selectedImage = null;
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getUri());
                    selectedImage = Bitmap.createScaledBitmap(selectedImage, 200, 200, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                showPreviewDialog(selectedImage, "", "", "","",0, false);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }

        }else if(resultCode == RESULT_OK && requestCode == QRCODE_READER){
            String result = intent.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");


            Logger.log("QRCODE_READER", result);
            try {
                JSONObject object = new JSONObject(result);

            String height = object.getString("height");
            String width = object.getString("width");
            String length = object.getString("length");
            String remarkText = "";
            String image = object.getString("image");



            showPreviewDialog(null, height, width, length,image,0, false);

               /* try
                {
                    String helloWorldInHex = HexStringConverter.getHexStringConverterInstance().stringToHex("HELLO WORLD");
                    System.out.println("'HELLO WORLD' in HEX : " + helloWorldInHex);
                    System.out.println("Reconvert to String : " + HexStringConverter.getHexStringConverterInstance().hexToString(helloWorldInHex));
                }
                catch (UnsupportedEncodingException ex)
                {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }*/

           // Bitmap bitmap = (Bitmap) intent.getParcelableExtra("bitmap");

           // tempDimen = height + "x" + width + "x" + length;
            //Log.e(TAG, "tempDimen: " + tempDimen);
            //tempRemark = remarkText;
            //ImageUploadModel model = new ImageUploadModel(bitmap, tempDimen, tempRemark);
            //imageList.add(model);
           // adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void startCropImageActivity(Uri imageUri) {

    }

    private void performCrop(Uri imageUri) throws IOException {

        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAspectRatio(2, 2)
                // .setRequestedSize(200,200)
                .setScaleType(CropImageView.ScaleType.CENTER)
                .start(MainActivity.this);

    }

    public class RoutesDownloadTask extends AsyncTask<String, Void, String> {

        Context context;
        String distanceTime;
        boolean isBooking;

        public RoutesDownloadTask(Context context, boolean isBooking) {
            this.context = context;
            this.isBooking = isBooking;
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

            ParserTask parserTask = new ParserTask(isBooking);

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

            boolean isBooking;
            public ParserTask(boolean isBooking) {
                this.isBooking = isBooking;
            }

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
                String distance = "0";
                String duration = "1";
                String durationValue = "1";
                String distanceValue = "0";

                try {
                    if (result.size() < 1) {
                        //Toast.makeText(MainActivity.this, "No Points", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    //Toast.makeText(MainActivity.this, "No Points error", Toast.LENGTH_SHORT).show();
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
                                distanceValue = point.get("value");
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

                    }
                }

                FareCard fareCard = databaseHandler.getFareCard();

                if(TextUtils.isEmpty(fareCard.getLimitKm())){
                    estfareView.setClickable(false);
                    estfareView.setText("---");
                    estfareView.setTextColor(getResources().getColor(R.color.red));
                    estfareView.setBackgroundResource(R.drawable.signup_btn);
                    estfareView.setVisibility(View.VISIBLE);
                    estfareProgressbar.setVisibility(View.GONE);
                    return;
                }

                int limitKm = Integer.parseInt(fareCard.getLimitKm());
                int perKm = Integer.parseInt(fareCard.getPerKmFare());
                int baseFare = Integer.parseInt(fareCard.getBaseFare());
                int returnFare = Integer.parseInt(fareCard.getReturnFare());
                int gstCharge = Integer.parseInt(fareCard.getGst());


                List<FareCard.Delivery> dropPointList = fareCard.getDeliveries();


                double aa = Double.valueOf(distanceValue);

                double bb = aa / 1000;

                double cc = 0;

                if (bb <= limitKm) {
                    int tptalDroppoints = waypoints.size();
                    int dropPoint = 0;
                    int dropFare = 0;
                    if (tptalDroppoints != 0) {
                        for (int i = 0; i < dropPointList.size(); i++) {
                            try {
                                dropFare = Integer.parseInt(dropPointList.get(i).getFare());
                                dropPoint = Integer.parseInt(dropPointList.get(i).getDropPoint());

                            } catch (NumberFormatException e) {
                                cc = baseFare + (tptalDroppoints * dropFare);
                                break;
                            }
                            if (tptalDroppoints <= dropPoint) {
                                cc = baseFare + (tptalDroppoints * dropFare);
                                break;
                            } else {
                                cc = baseFare + (tptalDroppoints * dropFare);
                            }
                        }
                    } else {
                        cc = baseFare;
                    }
                    Logger.log("ESTFAREACTU True", "tptalDroppoints: " + tptalDroppoints + "\n"
                            + "(tptalDroppoints <= dropPoint && tptalDroppoints != 0): " + (tptalDroppoints <= dropPoint && tptalDroppoints != 0) + "\n"
                    );

                } else {
                    double dd = bb - limitKm;
                    int tptalDroppoints = waypoints.size();
                    int dropPoint = 0;
                    int dropFare = 0;
                    if (tptalDroppoints != 0) {
                        for (int i = 0; i < dropPointList.size(); i++) {
                            try {
                                dropFare = Integer.parseInt(dropPointList.get(i).getFare());
                                dropPoint = Integer.parseInt(dropPointList.get(i).getDropPoint());

                            } catch (NumberFormatException e) {
                                cc = (perKm * dd) + baseFare + (tptalDroppoints * dropFare);
                                break;
                            }
                            if (tptalDroppoints <= dropPoint) {
                                cc = (perKm * dd) + baseFare + (tptalDroppoints * dropFare);
                                Logger.log("ESTFAREACTU False", "\n"
                                        + "(perKm * dd) + baseFare + (tptalDroppoints * dropFare): " + (perKm * dd) + baseFare + (tptalDroppoints * dropFare) + "\n"

                                        + "cc: " + cc + "\n"
                                        + "perKm: " + perKm + "\n"
                                        + "dd: " + dd + "\n"
                                        + "tptalDroppoints: " + tptalDroppoints + "\n"
                                        + "dropPoint: " + dropPoint + "\n"
                                        + "dropFare: " + dropFare + "\n"
                                );
                                break;
                            } else {
                                cc = (perKm * dd) + baseFare;
                            }
                        }
                    } else {
                        cc = (perKm * dd) + baseFare;
                    }

                    Logger.log("ESTFAREACTU False", "tptalDroppoints: " + tptalDroppoints + "\n"
                            + "(tptalDroppoints <= dropPoint): " + (tptalDroppoints <= dropPoint) + "\n"
                            + "bb: " + bb + "-" + "limitKm: " + limitKm + "\n"
                            + "=dd: " + dd + "\n"
                            + "dropFare: " + dropFare + "\n"
                    );
                }


                double estfare = cc + (returnRequired.isChecked() ? returnFare : 0);

                double gst = (estfare * gstCharge / 100);

                estfare = estfare + gst;
                DecimalFormat df = new DecimalFormat("#");
                String estfareFinal = df.format(estfare);
                Logger.log("ESTFAREACTU", "limitKm" + limitKm + "\n"
                        + "perKm: " + perKm + "\n"
                        + "baseFare: " + baseFare + "\n"
                        + "returnFare: " + returnFare + "\n"
                        + "gstCharge: " + gstCharge + "\n"
                        + "dropPointList: " + dropPointList.size() + "\n"
                        + "aa meter: " + aa + "\n"
                        + "bb km: " + bb + "\n"
                        + "cc fare: " + cc + "\n"
                        + "gst fare: " + gst + "\n"
                        + "estfare fare: " + estfare + "\n"
                        + "estfareFinal fare: " + estfareFinal + "\n"
                        + "bb <= limitKm: " + (bb <= limitKm) + "\n"
                );
              /*  String latlng = "";
                for(int i=0; i<points.size(); i++){
                    latlng = latlng +"\n"+ points.get(i).latitude+","+points.get(i).longitude;
                }
                int maxLogSize = 1000;
                for(int i = 0; i <= latlng.length() / maxLogSize; i++) {
                    int start = i * maxLogSize;
                    int end = (i+1) * maxLogSize;
                    end = end > latlng.length() ? latlng.length() : end;
                    Log.v("ESTFAREACTU", latlng.substring(start, end));
                }*/

                estfareView.setClickable(false);
                estfareView.setText("Rs " + estfareFinal + "/-");
                estfareView.setTextColor(getResources().getColor(R.color.black));
                estfareView.setBackgroundResource(R.drawable.signup_btn);
                estfareView.setVisibility(View.VISIBLE);
                estfareProgressbar.setVisibility(View.GONE);

                if(isBooking){
                   checkWalletBalance(estfareFinal);
                }

            }
        }

    }

    private void checkWalletBalance(String estfareFinal) {

        PickAddress pickAddress = databaseHandler.getSelectedPickupAddress(true);
        List<DropAddress> dropAddressList = databaseHandler.getDropSelectedAddress(true);

        OrderDetails orderDetails = new OrderDetails();

        int orderId = new Random().nextInt(1000);
        orderDetails.setDeliveryId(String.valueOf(orderId));
        orderDetails.setOrderStatus(Config.ORDER_PENDING);
        orderDetails.setDeliveryStatus("Pending");
        orderDetails.setUserId(String.valueOf(AppPreferance.getUserid(MainActivity.this)));
        orderDetails.setSchedule(scheduleDelivery.isChecked() ? scheduleTime.getText().toString() : "");
        orderDetails.setPickName(pickAddress.getPickName());
        orderDetails.setPickMobile(pickAddress.getPickMobile());
        orderDetails.setPickAddress(pickAddress.getPickAddress());
        orderDetails.setPickAddressName(pickAddress.getPickAddressName());
        orderDetails.setPickLat(pickAddress.getPickLat());
        orderDetails.setPickLng(pickAddress.getPickLng());

        orderDetails.setDropAddressList(dropAddressList);

        orderDetails.setNote(notes.getText().toString());
        orderDetails.setReturnrequired(returnRequired.isChecked());

        orderDetails.setUpdateOnServer(false);
        orderDetails.setAmount(estfareFinal);
        //startNewOrder(MainActivity.this, orderDetails);
        new GetWallet(orderDetails).execute();

    }


    private void orderNow(final OrderDetails orderDetails){
        final String result = "";

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("method", AppConstant.BUSINESS_CREATE_DELIVERY);
                jsonObject.put("business_id", orderDetails.getUserId());
                jsonObject.put("delivery_id", orderDetails.getDeliveryId());
                jsonObject.put("schedule", orderDetails.getSchedule());

                jsonObject.put("pickName", orderDetails.getPickName());
                jsonObject.put("pickMobile", orderDetails.getPickMobile());
                jsonObject.put("pickAddress", orderDetails.getPickAddress());
                jsonObject.put("pickAddressName", orderDetails.getPickAddressName());
                jsonObject.put("pickLat", orderDetails.getPickLat());
                jsonObject.put("pickLng", orderDetails.getPickLng());
                jsonObject.put("estCalculate", orderDetails.getAmount());



                JSONArray dropArray = new JSONArray();
                for (int i = 0; i < orderDetails.getDropAddressList().size(); i++) {
                    JSONObject dropObject = new JSONObject();
                    dropObject.put("dropName", orderDetails.getDropAddressList().get(i).getDropName());
                    dropObject.put("dropMobile", orderDetails.getDropAddressList().get(i).getDropMobile());
                    dropObject.put("dropAddress", orderDetails.getDropAddressList().get(i).getDropAddress());
                    dropObject.put("dropAmount", orderDetails.getDropAddressList().get(i).getDropAmount());
                    dropObject.put("dropLat", orderDetails.getDropAddressList().get(i).getDropLat());
                    dropObject.put("dropLng", orderDetails.getDropAddressList().get(i).getDropLng());
                    dropArray.put(dropObject);
                }

                jsonObject.put("dropAddressList", dropArray);
                jsonObject.put("note", orderDetails.getNote());
                jsonObject.put("returnRequired", orderDetails.isReturnrequired());
                jsonObject.put("fcm_token_id", FirebaseInstanceId.getInstance().getToken());

                jsonObject.put("productImage", adapter.getData());

                RequestToServer.getInstance().send(MainActivity.this, jsonObject, AppConstant.B4E_BUSINESS_CREATE_DELIVERY, new RequestToServer.CallBack() {
                    @Override
                    public void success(String json) {
                        if (!json.equalsIgnoreCase("")) {
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                                    databaseHandler.updateAllDropStatus(false);
                                    orderDetails.setAddedOn("");
                                    databaseHandler.addOrder(orderDetails);
                                    new DatabaseHandler(MainActivity.this).updateOrderSelect(Integer.parseInt(orderDetails.getDeliveryId()),
                                            jsonObject.getString("delivery_id"), true);

                                    showAlertDialog(MainActivity.this, "Success", "Thankyou for Order!\nYour order is under proccessing...", "OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            scheduleDelivery.setChecked(false);
                                            returnRequired.setChecked(false);
                                            scheduleTime.setText("");
                                            notes.setText("");
                                            stopCount.setText("");
                                            dropAddressAdapter.clear();
                                            databaseHandler.updateAllDropStatus(false);
                                            resetEstFare();
                                            dismissAlertDialog();
                                        }
                                    });
                                } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("Warring!")
                                            .setMessage("Some thing went wrong ....")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                    //startActivity(new Intent(MainActivity.this, WalletActivity.class));
                                                }
                                            })
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            });
                                    builder.show();
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

    /*public class OrderNow extends AsyncTask<Void, Void, String> {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OrderDetails orderDetails;


        public OrderNow(OrderDetails orderDetails) {
            this.orderDetails = orderDetails;
            if (!((AppCompatActivity) MainActivity.this).isFinishing()) {
                ProgressDialogRing.getInstance(MainActivity.this).show();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {

            String result = "";
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("method", AppConstant.BUSINESS_CREATE_DELIVERY);
                    jsonObject.put("business_id", orderDetails.getUserId());
                    jsonObject.put("delivery_id", orderDetails.getDeliveryId());
                    jsonObject.put("schedule", orderDetails.getSchedule());

                    jsonObject.put("pickName", orderDetails.getPickName());
                    jsonObject.put("pickMobile", orderDetails.getPickMobile());
                    jsonObject.put("pickAddress", orderDetails.getPickAddress());
                    jsonObject.put("pickAddressName", orderDetails.getPickAddressName());
                    jsonObject.put("pickLat", orderDetails.getPickLat());
                    jsonObject.put("pickLng", orderDetails.getPickLng());

                        jsonObject.put("productImage", adapter.getData());


                    JSONArray dropArray = new JSONArray();
                    for (int i = 0; i < orderDetails.getDropAddressList().size(); i++) {
                        JSONObject dropObject = new JSONObject();
                        dropObject.put("dropName", orderDetails.getDropAddressList().get(i).getDropName());
                        dropObject.put("dropMobile", orderDetails.getDropAddressList().get(i).getDropMobile());
                        dropObject.put("dropAddress", orderDetails.getDropAddressList().get(i).getDropAddress());
                        dropObject.put("dropAmount", orderDetails.getDropAddressList().get(i).getDropAmount());
                        dropObject.put("dropLat", orderDetails.getDropAddressList().get(i).getDropLat());
                        dropObject.put("dropLng", orderDetails.getDropAddressList().get(i).getDropLng());
                        dropArray.put(dropObject);
                    }

                    jsonObject.put("dropAddressList", dropArray);
                    jsonObject.put("note", orderDetails.getNote());
                    jsonObject.put("returnRequired", orderDetails.isReturnrequired());
                    jsonObject.put("fcm_token_id", FirebaseInstanceId.getInstance().getToken());
                    Log.e("Request_Response", jsonObject.toString() + "  url : " + AppConstant.B4E_BUSINESS_CREATE_DELIVERY);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(AppConstant.B4E_BUSINESS_CREATE_DELIVERY)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                result = response.body().string();
                Log.e("Request_Response", jsonObject.toString() + "\n" + result.toString());


            } catch (IOException e) {
                e.printStackTrace();

            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!result.equalsIgnoreCase("")) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                        databaseHandler.updateAllDropStatus(false);
                        databaseHandler.addOrder(orderDetails);
                        new DatabaseHandler(MainActivity.this).updateOrderSelect(Integer.parseInt(orderDetails.getDeliveryId()),
                                jsonObject.getString("delivery_id"), true);

                        showAlertDialog(MainActivity.this, "Success", "Thankyou for Order!\nYour order is under proccessing...", "OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                scheduleDelivery.setChecked(false);
                                returnRequired.setChecked(false);
                                scheduleTime.setText("");
                                notes.setText("");
                                stopCount.setText("");
                                dropAddressAdapter.clear();
                                databaseHandler.updateAllDropStatus(false);
                                resetEstFare();
                                dismissAlertDialog();
                            }
                        });
                    } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Warring!")
                                .setMessage("Some thing went wrong ....")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        //startActivity(new Intent(MainActivity.this, WalletActivity.class));
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                        builder.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            ProgressDialogRing.getInstance(MainActivity.this).hide();
        }
    }*/

    private class GetWallet extends AsyncTask<Void, Void, List<Wallet>> {

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        OrderDetails orderDetails;

        public GetWallet(OrderDetails orderDetails) {
            this.orderDetails = orderDetails;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected List<Wallet> doInBackground(Void... voids) {
            List<Wallet> walletList = new ArrayList<>();
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject data = new JSONObject();
                data.put("method", AppConstant.BUSINESS_WALLET);
                data.put("business_id", AppPreferance.getUserid(MainActivity.this));

                Log.e("Response_Response", AppConstant.B4E_BUSINESS_WALLET + "\n" + data.toString());
                //json.put("notification", dataJson);
                RequestBody body = RequestBody.create(JSON, data.toString());
                Request request = new Request.Builder()
                        .url(AppConstant.B4E_BUSINESS_WALLET)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.getString("status").equalsIgnoreCase("200")) {


                        JSONArray array = jsonObject.getJSONArray("result");
                        for (int i = 0; i < array.length(); i++) {
                            Wallet wallet = new Gson().fromJson(array.getJSONObject(i).toString(), Wallet.class);
                            walletList.add(wallet);

                        }

                    } else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            }

            return walletList;
        }

        @Override
        protected void onPostExecute(List<Wallet> wallets) {
            super.onPostExecute(wallets);
            if (wallets.isEmpty()) {

            } else {
                double amount = 0;
                for (int i = 0; i < wallets.size(); i++) {
                    amount += new Double(wallets.get(i).getWallet()).doubleValue();
                }

                if (amount >= MyApplication.getBookingRequiredAmount()) {
                    orderNow(orderDetails);


                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Warring!")
                            .setMessage(
                                    "Your current balance is Rs. " + amount + ", to do the booking your minimum wallet balance should be Rs. " + MyApplication.getBookingRequiredAmount() + ". Kindly add Money.")
                            .setPositiveButton("Wallet", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    startActivity(new Intent(MainActivity.this, WalletActivity.class));
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    builder.show();
                }
            }
        }
    }
    private BroadcastReceiver profileUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            User updatetUser = databaseHandler.getContact(currentUser.getId());

            String image = updatetUser.getProfile_image();
            Glide.with(getApplicationContext()).load(image.contains("http") ? image : Uri.parse(image)).asBitmap().centerCrop().into(new BitmapImageViewTarget(profileImage) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    profileImage.setImageDrawable(circularBitmapDrawable);
                }
            });


            profileName.setText(updatetUser.getName());
            profileMobile.setText(updatetUser.getPhone_number());
        }
    };


}
