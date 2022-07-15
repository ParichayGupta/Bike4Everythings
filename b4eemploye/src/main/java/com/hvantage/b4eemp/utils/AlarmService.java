package com.hvantage.b4eemp.utils;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;

import com.b4elibrary.Logger;
import com.google.gson.Gson;
import com.hvantage.b4eemp.Database.Database;
import com.hvantage.b4eemp.R;
import com.hvantage.b4eemp.activity.BookingActivity;
import com.hvantage.b4eemp.dialog.AllotBookingDialog;
import com.hvantage.b4eemp.model.BookingData;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AlarmService extends Service {
    private Handler mHandler = new Handler();
    SimpleDateFormat dateFormatInteger = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler.removeCallbacks(r);
        mHandler.post(r);
        return super.onStartCommand(intent, flags, startId);
    }


    // Build your notification widget
    void notifyMe(Bitmap bitmap, BookingData bookingData, String remaningtime, String messages) {
        int notifyId = Integer.parseInt(bookingData.getId());
        // Specify the intent to be triggered when the Notification is clicked on
        String from = "#"+bookingData.getId()+" "+bookingData.getName();
        String msg = messages;
        Intent resultIntent = new Intent(this, AllotBookingDialog.class);
        //resultIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        resultIntent.putExtra(AllotBookingDialog.Params.class.getCanonicalName(), new AllotBookingDialog.Params(AllotBookingDialog.Params.Mode.DIALOG));
        resultIntent.putExtra("bookingData", bookingData);
        resultIntent.putExtra("msg", msg);
        // here you can put in extra intents, values you want to be received by the Receiving activity, when a Notification is clicked
        resultIntent.putExtra("RQS", 1);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        notifyId,
                        resultIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );
        try {
            resultPendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setLargeIcon(bitmap)
                        .setContentTitle(from)
                        .setAutoCancel(true)
                        .setTicker(msg) //Ticker texts flash in the message bar for a couple of seconds while the notification is still fresh
                        .setLights(Color.argb(255,255,100,0), 500, 5000)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setPriority(2)
                        .setContentText(msg);
        mBuilder.setContentIntent(resultPendingIntent);


        // Get Notification manager service
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // mNotificationId and ID used to represent Notificaitons from our application
        mNotifyMgr.notify(notifyId, mBuilder.build());


    }

    public void openNextActivityAsDialog()
    {
        AllotBookingDialog.start(this, new AllotBookingDialog.Params(AllotBookingDialog.Params.Mode.DIALOG));
    }

    // A runnable to perform actions when the Alarm is fired
    private Runnable r = new Runnable() {
        public void run() {
            // Do stuff every time this service is called by the Alarm, every 60 seconds in this example
            new Thread(new Runnable() {
                public void run() {


                    Bitmap bitmap = null;

                    // Pass in a bitmap to represent the big image for the notification
                    // Not required, but it's a cool addition
                    //notifyMe(getString(R.string.app_name), "You have 5 new updates", 5, bitmap);

                    List<BookingData> bookingDataArrayList = Database.getInstance(AlarmService.this).getAllBookingData();
                    if(bookingDataArrayList != null) {
                        for (BookingData bookingData : bookingDataArrayList) {
                            try {
                                // Get Image from Internet
                                InputStream in = new java.net.URL(bookingData.getAltCustomerImage()).openStream();
                                bitmap = BitmapFactory.decodeStream(in);
                            }
                            catch (Exception e0) {
                                // Get a default image from resources
                                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                            }
                            Logger.log("AlarmTesting", new Gson().toJson(bookingData));
                            try {
                                String msgPrefix = "";
                                final Date startDate = dateFormatInteger.parse(bookingData.getPickupDate() +" "+ bookingData.getPickupTime());
                                final Date endDate = dateFormatInteger.parse(bookingData.getDropoffDate() +" "+ bookingData.getDropoffTime());
                                Calendar calendar = Calendar.getInstance();

                                boolean isNotify = false;

                                long msDiff = endDate.getTime() - calendar.getTime().getTime();

                                if(String.valueOf(msDiff).contains("-")){
                                    msgPrefix = "Your Rent Period has ended at %1$s";
                                    msDiff = calendar.getTime().getTime() - endDate.getTime();
                                    isNotify = true;
                                }else {
                                    msgPrefix = "Your Rent Period is about to end %1$s";
                                    isNotify = false;
                                }
                                long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);
                                long hourssDiff = TimeUnit.MILLISECONDS.toHours(msDiff);
                                long minutesDiff = TimeUnit.MILLISECONDS.toMinutes(msDiff);


                                CharSequence remaningtime =
                                        DateUtils.getRelativeTimeSpanString(endDate.getTime(),
                                                calendar.getTimeInMillis(),
                                                DateUtils.MINUTE_IN_MILLIS,
                                                DateUtils.FORMAT_ABBREV_RELATIVE);

                                Logger.log("AlarmTesting ", "formatDateRange:"+ DateUtils.formatDateRange(AlarmService.this, calendar.getTime().getTime(), endDate.getTime(), DateUtils.FORMAT_SHOW_TIME));


                                String messages = String.format(msgPrefix, remaningtime);
                                Logger.log("AlarmTesting ", messages+" >>>>");

                                if(minutesDiff < 15 || isNotify){
                                    Logger.log("AlarmTesting ", "in:");
                                    notifyMe( bitmap, bookingData, remaningtime+"", messages);
                                }else {
                                    Logger.log("AlarmTesting ", "out:");
                                }


                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }else {
                        Logger.log("AlarmTesting droptime", "bookingData null");
                    }

                }
            }).start();
        }
    };
}

