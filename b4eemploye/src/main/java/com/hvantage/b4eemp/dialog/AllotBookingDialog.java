package com.hvantage.b4eemp.dialog;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.b4elibrary.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hvantage.b4eemp.R;
import com.hvantage.b4eemp.Wedgets.CustomDateTimePicker;
import com.hvantage.b4eemp.fragment.RunningFragment;
import com.hvantage.b4eemp.model.BookingData;
import com.hvantage.b4eemp.utils.AppConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AllotBookingDialog extends AppCompatActivity implements View.OnClickListener {
    TextView tvMsg, close, call, timeExtend;
    BookingData bookingData;

    public static final class Params implements Serializable
    {

        private final Mode displayMode;

        public Params(Mode displayMode)
        {
            this.displayMode = displayMode;
        }

        public Mode getDisplayMode()
        {
            return displayMode;
        }

        public enum Mode
        {
            DIALOG,
            DIALOG_NO_TOOLBAR,
            NORMAL,
            NORMAL_NO_TOOLBAR,
        }
    }

    public static Intent start(Context context, Params params)
    {
        Intent starter = new Intent(context, AllotBookingDialog.class);
        starter.putExtra(Params.class.getCanonicalName(), params);
        return starter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        Params params = (Params) getIntent().getSerializableExtra(Params.class.getCanonicalName());
        switch(params.getDisplayMode())
        {
            case DIALOG:
                setTheme(R.style.AppDialogTheme);
                break;
            case DIALOG_NO_TOOLBAR:
                setTheme(R.style.AppDialogTheme_NoActionBar);
                break;
            case NORMAL:
                setTheme(R.style.AppTheme);
                break;
            case NORMAL_NO_TOOLBAR:
                setTheme(R.style.AppTheme_NoActionBar);
                break;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allot_booking_dialog);
        this.setFinishOnTouchOutside(false);
        bookingData = getIntent().getParcelableExtra("bookingData");
        initView();
        setTitle("#"+bookingData.getId() +" "+ bookingData.getName());

        tvMsg.setText(getIntent().getStringExtra("msg"));
    }

    private void initView() {
        tvMsg = findViewById(R.id.tvMsg);
        call = findViewById(R.id.call);
        close = findViewById(R.id.close);
        timeExtend = findViewById(R.id.timeExtend);

        call.setOnClickListener(this);
        close.setOnClickListener(this);
        timeExtend.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.call) {
            cancelNotification(Integer.parseInt(bookingData.getId()));
            String phone = bookingData.getMobile();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            startActivity(intent);
            finish();
        } else if (i == R.id.close) {
            finish();
        } else if (i == R.id.timeExtend) {
            extendTime();
        }
    }

    private void extendTime() {
        new CustomDateTimePicker(AllotBookingDialog.this,
                new CustomDateTimePicker.ICustomDateTimeListener() {
                    @Override
                    public void onSet(Dialog dialog, Calendar calendarSelected,
                                      Date dateSelected, int year,
                                      String monthFullName,
                                      String monthShortName,
                                      int monthNumber, int date,
                                      String weekDayFullName,
                                      String weekDayShortName, int hour24,
                                      int hour12,
                                      int min, int sec, String AM_PM) {
                        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyy HH:mm");
                        String formattedDate = df.format(dateSelected.getTime());
                        Logger.log("datetime", formattedDate);
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("method", AppConstants.UPDATERENTBOKINGDROP);
                            jsonObject.put("trip_id", bookingData.getId());
                            jsonObject.put("dropoff_date", formattedDate.split(" ")[0]);
                            jsonObject.put("dropoff_time", formattedDate.split(" ")[1]);
                            new UpdateBooking(jsonObject).execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                }).set24HourFormat(false)
                .setDate(Calendar.getInstance())
                .setMinDate(Calendar.getInstance())
                .showDialog();
    }

    private class UpdateBooking extends AsyncTask<String, String, String> {

        private JSONObject jsonObject;

        public UpdateBooking(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.e("Request_Response", AppConstants.RENT_TRIP_BOOK_UPDATE + "\n" + jsonObject.toString());

            try {
                OkHttpClient client = new OkHttpClient();
                final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(AppConstants.RENT_TRIP_BOOK_UPDATE)
                        .post(body)
                        .build();
                okhttp3.Response response = client.newCall(request).execute();
                String result = response.body().string();
                Log.e("Request_Response", result);

                return result;

            } catch (IOException e) {
                e.printStackTrace();

            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JsonObject jsonObject = new Gson().fromJson(s, JsonObject.class);
            AlertDialog alert = new AlertDialog.Builder(AllotBookingDialog.this).create();
            alert.setCanceledOnTouchOutside(false);
            alert.setCancelable(false);
            alert.setTitle(jsonObject.get("message").getAsString());
            alert.setMessage(jsonObject.get("result").getAsJsonObject().get("msg").getAsString());
            alert.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelNotification(Integer.parseInt(bookingData.getId()));
                    dialog.dismiss();
                    finish();
                }
            });
            alert.show();
        }
    }

    private void cancelNotification(int id){
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(id);
    }
}
