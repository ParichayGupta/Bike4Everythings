package com.b4edriver.GCM;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;

import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.b4edriver.DriverApp.DialogActivityDriver;
import com.b4edriver.DriverApp.ItemDetailsActivity;
import com.b4edriver.DriverApp.NavigationDrawerDriver;
import com.b4edriver.DriverApp.NotificationActivityDriver;
import com.b4edriver.DriverApp.RecieptActivityDriver;
import com.b4edriver.DriverApp.TripAcceptActivityDriver;
import com.b4edriver.DriverApp.TripDetailActivityDriver;
import com.b4edriver.Model.TripDriver;
import com.b4edriver.R;
import com.b4edriver.b4edrivers.AppPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MAX on 11-Oct-16.
 */
public class MyNotification {
    Context context;
    private NotificationManager mNotificationManager;
    //private static MyNotification ourInstance = new MyNotification(context);

    public static MyNotification getInstance(Context context) {
        MyNotification ourInstance = new MyNotification(context);
        return ourInstance;
    }

    private MyNotification(Context context) {
        this.context = context;
    }



    public void sendNotification(String msg) {
        mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, NotificationActivityDriver.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Bike4Everything")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentText(msg);


        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(Config.NOTIFICATION_ID, mBuilder.build());
    }

    public void ItemDetailsUpdatedNotify(String itemDetailsUpdatedByUser) {
        mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        AppPreferencesDriver.setItemDetails(context, itemDetailsUpdatedByUser);
        if(TripAcceptActivityDriver.instance!= null) {
            TripAcceptActivityDriver.updateItemDetailsUI(itemDetailsUpdatedByUser);
        }
        Intent intent = new Intent(context, ItemDetailsActivity.class);
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("ItemDetailsUpdated");
        intent.putExtra("data", itemDetailsUpdatedByUser);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Bike4Everything")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.item_details)))
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentText(context.getString(R.string.item_details));


        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(ConfigDriver.NOTIFICATION_ID, mBuilder.build());
        context.startActivity(intent);
    }
    // [END receive_message]

    public void cancelTripByUserNotify(String msg) {
        mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, DialogActivityDriver.class);
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("cancelTripByUser");
        intent.putExtra("textmsg", msg);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Bike4Everything")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(ConfigDriver.NOTIFICATION_ID, mBuilder.build());
    }



    public void newTripNotify(TripDriver trips) {


        mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, TripDetailActivityDriver.class);
        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("tripDetails", trips);
        intent.setAction(trips.getService_id());
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Bike4Everything")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("You have get a new trip"))
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentText("You have get a new trip.");

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(ConfigDriver.NEW_TRIP_ID, mBuilder.build());

            context.startActivity(intent);

    }

    public void newOtherTripNotify(TripDriver trips) {


        mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context,  TripDetailActivityDriver.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
        intent.putExtra("tripDetails", trips);
        intent.setAction("otherService");

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Bike4Everything")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("You have get a new trip"))
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentText("You have get a new trip.");

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(ConfigDriver.NEW_TRIP_ID, mBuilder.build());
        context.startActivity(intent);
    }

    public void userPayNotify(String msg) {

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(msg);



            mNotificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);


            Intent intent = new Intent(context, DialogActivityDriver.class);
            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction("userPay");
            intent.putExtra("bill_id",jsonObject.getString("bill_id"));
            intent.putExtra("user_id",jsonObject.getString("user_id"));
            intent.putExtra("trip_id",jsonObject.getString("trip_id"));
            intent.putExtra("amount",jsonObject.getString("amount"));
            intent.putExtra("paymentMode",jsonObject.getString("paymentMode"));
            intent.putExtra("txtMsg",jsonObject.getString("message"));

            String showMsg = jsonObject.getString("message");

            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    context).setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Bike4Everything")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(showMsg))
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentText(showMsg);
            mBuilder.setContentIntent(contentIntent);

            mNotificationManager.notify(ConfigDriver.NOTIFICATION_ID, mBuilder.build());

            if(RecieptActivityDriver.instance != null){
                context.startActivity(intent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void gcmNotificationErrorNotify(String msg) {



            mNotificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);


            Intent intent = new Intent(context, DialogActivityDriver.class);
            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction("gpserror");
            intent.putExtra("msg",msg);

            String showMsg = msg;

            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    context).setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Bike4Everything")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(showMsg))
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentText(showMsg);
            mBuilder.setContentIntent(contentIntent);

            mNotificationManager.notify(ConfigDriver.NOTIFICATION_ID, mBuilder.build());

            if(NavigationDrawerDriver.instance != null){
                if(DialogActivityDriver.instance == null){
                    context.startActivity(intent);
                }

            }

    }
    public void loginSessionExpireNotify(String msg) {


            mNotificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

        AppPreferencesDriver.clearDriverPreferences(context);

            Intent intent = new Intent(context, DialogActivityDriver.class);
            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction("loginSessionExpire");
            intent.putExtra("msg",msg);

            String showMsg = msg;

            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    context).setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Bike4Everything")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(showMsg))
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentText(showMsg);
            mBuilder.setContentIntent(contentIntent);

            mNotificationManager.notify(ConfigDriver.NOTIFICATION_ID, mBuilder.build());

            if(NavigationDrawerDriver.instance != null){
                if(DialogActivityDriver.instance == null){
                    context.startActivity(intent);
                }

            }

    }

    public void tripLogdNotify(String msg) {


            mNotificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);


            Intent intent = new Intent(context, NavigationDrawerDriver.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction("tripLogdNotify");
            intent.putExtra("msg",msg);

            String showMsg = msg;

            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    context).setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Bike4Everything")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(showMsg))
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentText(showMsg);
            mBuilder.setContentIntent(contentIntent);

            mNotificationManager.notify(ConfigDriver.NOTIFICATION_ID, mBuilder.build());

               // context.startActivity(intent);

    }
    public void tripendNotify(String msg) {


            mNotificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);


            Intent intent = new Intent(context, NavigationDrawerDriver.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction("tripendNotify");
            intent.putExtra("msg",msg);

            String showMsg = msg;

            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    context).setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Bike4Everything")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(showMsg))
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentText(showMsg);
            mBuilder.setContentIntent(contentIntent);

            mNotificationManager.notify(ConfigDriver.NOTIFICATION_ID, mBuilder.build());

               // context.startActivity(intent);


    }

    public void driverApprovalNotify(String msg) {
        mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        AppPreferencesDriver.setDriverapproval(context, "yes");
        Intent intent;
        intent = new Intent(context, NavigationDrawerDriver.class);
        intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("");


        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("setTicker")
                .setContentTitle("Bike4Everything")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Congratulations, Your biker application with B4E is aprove you can now login to start B4E journey"))
                .addAction(R.drawable.logoutb,"LOGIN",contentIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(contentIntent);
        //


        mNotificationManager.notify(ConfigDriver.NOTIFICATION_ID, mBuilder.build());
    }
    public void driverUnApprovalNotify(String msg) {
        mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        AppPreferencesDriver.setDriverapproval(context, "yes");

        Intent intent;
        intent = new Intent(context, null);
        intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("");

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("setTicker")
                .setContentTitle("Bike4Everything")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(contentIntent);
        //


        mNotificationManager.notify(ConfigDriver.NOTIFICATION_ID, mBuilder.build());
    }

    public void newMessageNotify(String msg) {
        mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        JSONObject jsonObject;
        try {
            jsonObject  = new JSONObject(msg);

            Intent intent = new Intent(context,DialogActivityDriver.class);
            intent.putExtra("data",msg);
            intent.setAction("");
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    intent , PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    context).setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Bike4Everything")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(jsonObject.getString("msg")))
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentText(jsonObject.getString("msg"));
            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(ConfigDriver.NOTIFICATION_ID, mBuilder.build());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void beforeAllotBikeRentNotify(String msg) {
        mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);


            Intent intent = new Intent(context,DialogActivityDriver.class);
            intent.putExtra("data",msg);
            intent.setAction("beforeAllotBikeRentNotify");
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    intent , PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    context).setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Bike 4 Everything")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentText(msg);
            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(ConfigDriver.NOTIFICATION_ID, mBuilder.build());

    }
}
