package com.b4edriver.DriverApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.b4edriver.CommonClasses.ServerConnection.JSONParser;
import com.b4edriver.CommonClasses.ServerConnection.ServerErrorCallBack;
import com.b4edriver.CommonClasses.ServerConnection.VolleyCallBack;
import com.b4edriver.CommonClasses.Utils.AppConstantDriver;
import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.b4edriver.CommonClasses.Utils.DialogManagerDriver;
import com.b4edriver.CommonClasses.Utils.Function;
import com.b4edriver.GCM.ConfigDriver;
import com.b4edriver.R;
import com.b4elibrary.Logger;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DialogActivityDriver extends Activity {

    public static DialogActivityDriver instance = null;
    String bill_id, user_id, trip_id, amount, paymentMode;
    DialogManagerDriver dialogManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_driver);

        setFinishOnTouchOutside(false);

        instance = this;

        dialogManager = new DialogManagerDriver();

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
        nMgr.cancel(ConfigDriver.NOTIFICATION_ID);

        if (!AppPreferencesDriver.getReciveMessage(DialogActivityDriver.this).equalsIgnoreCase("")) {
            Logger.log("messageTask", "messageTask");
            if (!Function.isAppIsInBackground(DialogActivityDriver.this)) {
                nMgr.cancel(ConfigDriver.NOTIFICATION_ID);
            }
            AppPreferencesDriver.setReciveMessage(DialogActivityDriver.this, "");
        }
        TextView textView = (TextView) findViewById(R.id.text);
        Button button = (Button) findViewById(R.id.btn_submit);
        ImageView imageView = (ImageView) findViewById(R.id.close);


        String data, txtMsg = "";
        if (getIntent().getAction().equalsIgnoreCase("userPay")) {

            setFinishOnTouchOutside(true);

            bill_id = getIntent().getStringExtra("bill_id");
            user_id = getIntent().getStringExtra("user_id");
            trip_id = getIntent().getStringExtra("trip_id");
            amount = getIntent().getStringExtra("amount");
            paymentMode = getIntent().getStringExtra("paymentMode");


            txtMsg = getIntent().getStringExtra("txtMsg");

            textView.setText(txtMsg);

            imageView.setVisibility(View.GONE);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    //transactionConfirm();
                }
            });


        } else if (getIntent().getAction().equalsIgnoreCase("gpsOrNet")) {


            txtMsg = getIntent().getStringExtra("msg");

            textView.setText(txtMsg);

            imageView.setVisibility(View.GONE);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });


        } else if (getIntent().getAction().equalsIgnoreCase("alarm")) {

            setFinishOnTouchOutside(true);

            txtMsg = getIntent().getStringExtra("msg");

            textView.setText(txtMsg);

            imageView.setVisibility(View.GONE);

            button.setText("Setting");

            AppPreferencesDriver.clearDriverPreferences(instance);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });


        } else if (getIntent().getAction().equalsIgnoreCase("tripAcceptByOtherDriver")) {


            txtMsg = getIntent().getStringExtra("msg");

            textView.setText(txtMsg);

            imageView.setVisibility(View.GONE);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });


        } else if (getIntent().getAction().equalsIgnoreCase("gpserror")) {


            txtMsg = getIntent().getStringExtra("msg");

            textView.setText(txtMsg);

            imageView.setVisibility(View.GONE);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSettingsAlert();
                    //finish();
                }
            });


        } else if (getIntent().getAction().equalsIgnoreCase("ItemDetailsUpdated")) {
            //{orderDetails=[{"order_no":"bsfcs1345","desc_one":"tea","desc_three":"","desc_two":"coffee"}]}
            try {
                JSONArray jsonArray = new JSONArray(getIntent().getStringExtra("data"));
                JSONObject jsonObject = jsonArray.getJSONObject(0);


                txtMsg = "Order No : " + jsonObject.getString("order_no")
                        + "\n-------------------------------------------"
                        + "\nITEMS\n"
                        + "\n1) " + jsonObject.getString("desc_one")
                        + "\n2) " + jsonObject.getString("desc_two")
                        + "\n3) " + jsonObject.getString("desc_three");

            } catch (JSONException e) {
                e.printStackTrace();
            }


            textView.setText(txtMsg);
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextSize(20);

            imageView.setVisibility(View.GONE);


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });


        } else if (getIntent().getAction().equalsIgnoreCase("cancelTripByUser")) {

            try {

                txtMsg = getIntent().getStringExtra("textmsg");
            } catch (NullPointerException e) {
                txtMsg = getString(R.string.canceled_trip_by_user);
            }

            textView.setText(txtMsg);

            imageView.setVisibility(View.GONE);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });


        }else if (getIntent().getAction().equalsIgnoreCase("beforeAllotBikeRentNotify")) {

            try {

                txtMsg = getIntent().getStringExtra("data");
            } catch (NullPointerException e) {
                txtMsg = "";
            }

            textView.setText(txtMsg);

            imageView.setVisibility(View.GONE);
            button.setText("OK");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });


        }else if (getIntent().getAction().equalsIgnoreCase("gps_error")) {

            final MediaPlayer mp  = MediaPlayer.create(this, R.raw.car_alarm);
            playSound(mp);
            long pattern[] = { 100, 200, 300, 400, 500 };
            final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(pattern, 0);


            txtMsg = "Please disable and again enable you GPS location";

            textView.setText(txtMsg);

            imageView.setVisibility(View.GONE);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mp.stop();
                    vibrator.cancel();
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    finish();
                }
            });


        } else {
            finish();

        }


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

    private void transactionConfirm() {
        try {

            JSONObject jsonObject1 = new JSONObject();

            jsonObject1.put("method", AppConstantDriver.METHOD.USERBILLPAYMENTPROCESS_FROM_DRIVER_CONFORM);
            jsonObject1.put("tripId", trip_id);
            jsonObject1.put("billId", bill_id);
            jsonObject1.put("userId", user_id);
            jsonObject1.put("driverId", AppPreferencesDriver.getDriverId(DialogActivityDriver.this));
            jsonObject1.put("amount", amount);
            jsonObject1.put("paymentMode", paymentMode);
            jsonObject1.put("bill_by", "D");

            JSONParser jsonParser = new JSONParser(DialogActivityDriver.this);
            jsonParser.parseVollyJSONObject(AppConstantDriver.URL.ONDEMAND_DRIVER_DRIVERPAYMENT, 1, jsonObject1, "", new VolleyCallBack() {
                @Override
                public void success(String response) {

                    Intent intent11 = new Intent(DialogActivityDriver.this, NavigationDrawerDriver.class);
                    intent11.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent11.setAction("");
                    startActivity(intent11);
                    if (RecieptActivityDriver.instance != null) {
                        RecieptActivityDriver.instance.finish();
                    }
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
                        showDialog(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

    }

    public void showDialog(String msg) {

        new SweetAlertDialog(DialogActivityDriver.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(msg)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        finish();

                    }
                })
                .show();

    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DialogActivityDriver.this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("Do you want to go to gps settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                overridePendingTransition(R.anim.left_to_right,
                        R.anim.right_to_left);
                finish();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}
