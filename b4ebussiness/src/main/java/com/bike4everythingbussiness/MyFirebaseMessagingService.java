package com.bike4everythingbussiness;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.bike4everythingbussiness.Activity.DeliveriesActivity;
import com.bike4everythingbussiness.Activity.SigninActivity;
import com.bike4everythingbussiness.Activity.SplashActivity;
import com.bike4everythingbussiness.Services.SyncDBService;
import com.bike4everythingbussiness.Utils.AppConstant;
import com.bike4everythingbussiness.Utils.Config;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Peter on 17-Jul-17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FBMessagingService";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("Notification Data : ", ">>"+remoteMessage.getData().toString());

        if(remoteMessage.getData().size() > 0){

           String defaultMessage =  ""+remoteMessage.getData().get(Config.MESSAGE);
           String USER_APPROVAL =  ""+remoteMessage.getData().get(Config.USER_APPROVAL);
           String BUSINESS_UPDATE_STATUS =  ""+remoteMessage.getData().get(Config.BUSINESS_UPDATE_STATUS);

           // {user_approval={"message":"Admin now approval. You Can Login Using Your Login Credentials."}}
            if(!defaultMessage.equalsIgnoreCase("null")){

                sendNotification(defaultMessage);
            }else if(!USER_APPROVAL.equalsIgnoreCase("null")){

                approvalNotification(USER_APPROVAL);
            }else if(!BUSINESS_UPDATE_STATUS.equalsIgnoreCase("null")){

                //{"delivery_id":"1", "delivery_status":"Complete"}

                try {
                    JSONObject jsonObject = new JSONObject(BUSINESS_UPDATE_STATUS);
                    String deliveryStatus = jsonObject.getString("status");
                    int orderStatus;
                    /*if (deliveryStatus.equalsIgnoreCase("Pending")) {
                        orderStatus = (Config.ORDER_PENDING);
                    } else */if (deliveryStatus.equalsIgnoreCase("Complete")) {
                        orderStatus = (Config.ORDER_COMPLETED);
                    } else {
                        orderStatus = (Config.ORDER_ONGOING);
                    }
                    new DatabaseHandler(MyFirebaseMessagingService.this)
                            .updateOrderStatus(jsonObject.getString("delivery_id"),
                                    orderStatus,
                                    deliveryStatus);

                    UpdateStatusNotification("Your order is "+ deliveryStatus);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }



            }


    }



    private void sendNotification(String message)
    {
        try {
            Intent intent= new Intent(getApplicationContext(), SplashActivity.class);
             intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

           // Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.tone);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Bike4Everything B2B")
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notificationBuilder.build());
        }
        catch (Exception e)
        {
            Log.e("Notification Ex", e.getMessage());
        }
    }
    private void UpdateStatusNotification(String message)
    {
        try {
            Intent intent= new Intent(getApplicationContext(), DeliveriesActivity.class);
             intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

           // Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.tone);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.bikelogo)
                    .setContentTitle("Bike4Everything B2B")
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.bikelogo))
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notificationBuilder.build());
        }
        catch (Exception e)
        {
            Log.e("Notification Ex", e.getMessage());
        }
    }

    public void approvalNotification(String msg) {

        Intent intent = new Intent(MyFirebaseMessagingService.this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(msg);
        bigText.setBigContentTitle("Bike4Everything Bussiness");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.bikelogo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.bikelogo))
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentTitle("Bike4Everything Bussiness")
                .setContentIntent(notificationPendingIntent)
                .setContentText(msg)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_VIBRATE)
                .setSound(defaultSoundUri)
                .setStyle(bigText)
                .setPriority(NotificationManager.IMPORTANCE_HIGH);


        if(msg.contains("Dis")){

        }else{
            builder.setFullScreenIntent(notificationPendingIntent,true);
            Intent login = new Intent();
            login.setAction(AppConstant.ACTION_LOGIN);
            PendingIntent pendingIntentYes = PendingIntent.getBroadcast(this, 12345, login, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.ic_exit_to_app_black_24dp, AppConstant.ACTION_LOGIN, pendingIntentYes);
        }


        Notification notification = builder.build();


        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(1, notification);


    }
}
