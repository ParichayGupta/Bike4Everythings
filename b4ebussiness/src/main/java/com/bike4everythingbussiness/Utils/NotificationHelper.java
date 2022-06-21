package com.bike4everythingbussiness.Utils;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.bike4everythingbussiness.Activity.MainActivity;
import com.bike4everythingbussiness.Model.OrderDetails;
import com.bike4everythingbussiness.Reciver.ScheduleBookingReceiver;

import java.security.PublicKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by manishsingh on 16/02/18.
 */

public class NotificationHelper{

    public static int ALARM_TYPE_RTC = 100;
    private static AlarmManager alarmManagerRTC;
    private static PendingIntent alarmIntentRTC;



    public static void scheduleBooking(Context context, OrderDetails orderDetails){

        alarmManagerRTC = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ScheduleBookingReceiver.class);
        intent.setAction("scheduleBooking");
        intent.putExtra("orderDetails", orderDetails);
        alarmIntentRTC = PendingIntent.getBroadcast(context, 0, intent, 0);
        Calendar calendar = Calendar.getInstance();



        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
        try {
            Date mDate = sdf.parse(orderDetails.getSchedule());
            long timeInMilliseconds = mDate.getTime();
            System.out.println("TIMEDATE :: " + timeInMilliseconds);

            calendar.setTimeInMillis(timeInMilliseconds);

            Date newDate  = new Date(timeInMilliseconds);

            System.out.println("TIMEDATE: "+newDate.toString());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        alarmManagerRTC.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * 1, alarmIntentRTC);

    }

    public static void cancelAlarm(Context context) {
        if (alarmManagerRTC!= null) {
            alarmManagerRTC.cancel(alarmIntentRTC);
            Logger.log("Canceled","Canceled Alarm!");
        }
    }




}
