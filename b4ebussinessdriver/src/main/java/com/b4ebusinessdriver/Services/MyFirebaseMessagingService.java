package com.b4ebusinessdriver.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.b4ebusinessdriver.Activity.NewOrderActivity;
import com.b4ebusinessdriver.Activity.SplashActivity;
import com.b4ebusinessdriver.R;
import com.b4ebusinessdriver.Utils.AppConstant;
import com.b4ebusinessdriver.Utils.AppPreferences;
import com.b4ebusinessdriver.Utils.Config;
import com.b4elibrary.Logger;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Peter on 17-Jul-17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FBMessagingService";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("Notification Data : ", ">>" + remoteMessage.getData().toString());

        if (remoteMessage.getData().size() > 0) {
            Logger.log(TAG, "Message data payload: BTBD" + remoteMessage.getData());
            String defaultMessage = "" + remoteMessage.getData().get(Config.MESSAGE);
            String DRIVER_APPROVAL = "" + remoteMessage.getData().get(Config.DRIVER_APPROVAL);
            String NEWTRIP = "" + remoteMessage.getData().get(Config.NEWTRIP);

            // {user_approval={"message":"Admin now approval. You Can Login Using Your Login Credentials."}}
            if (!defaultMessage.equalsIgnoreCase("null")) {

                sendNotification(defaultMessage);
            } else if (!DRIVER_APPROVAL.equalsIgnoreCase("null")) {
                AppPreferences.setUserId(MyFirebaseMessagingService.this, "");
                AppPreferences.setUsername(MyFirebaseMessagingService.this, "");
                approvalNotification(DRIVER_APPROVAL);
            } else if (!NEWTRIP.equalsIgnoreCase("null")) {
                //Intent intent = new Intent("getNewTrip");
                //LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

                try {
                    JSONObject jsonObject = new JSONObject(NEWTRIP);
                    JSONArray array = jsonObject.getJSONArray("AllServices");
                    JSONObject object = array.getJSONObject(0);
                    String pickAddress = object.getString("pickAddress");
                    String title = object.getString("title");
                    String buiness_name = object.getString("buiness_name");
                    String pickup_name = object.getString("pickup_name");
                    String delivery_id = object.getString("delivery_id");
                    String amount = object.optString("amount");
                    String mobile = object.optString("pickup_contact");

                    Intent intent = new Intent(MyFirebaseMessagingService.this, NewOrderActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("pickAddress", pickAddress);
                    intent.putExtra("title", title);
                    intent.putExtra("buiness_name", buiness_name);
                    intent.putExtra("pickup_name", pickup_name);
                    intent.putExtra("delivery_id", delivery_id);
                    intent.putExtra("amount", amount);
                    intent.putExtra("number", mobile);
                    startActivity(intent);

                    newTripNotification(delivery_id, title, buiness_name, pickup_name, pickAddress);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                //startService(new Intent(MyFirebaseMessagingService.this, com.b4edriver.GCM.MyFirebaseMessagingService.class));
            }


        }


    }


    private void sendNotification(String message) {
        try {
            Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            // Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.tone);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Bike4Everything Business Driver")
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                    .setSound(defaultSoundUri)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notificationBuilder.build());
        } catch (Exception e) {
            Log.e("Notification Ex", e.getMessage());
        }
    }

    private void newTripNotification(String delivery_id, String title, String buiness_name, String pickup_name, String pickAddress) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        //Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.car_alarm);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(buiness_name + "\n" + pickup_name + "\nAddress: " + pickAddress);
        bigText.setBigContentTitle(title);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.bikelogo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.bikelogo))
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentTitle(title)
                .setContentIntent(notificationPendingIntent)
                .setContentText(buiness_name + " : \n" + pickup_name)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_VIBRATE)
                .setSound(defaultSoundUri)
                .setStyle(bigText)
                .setOngoing(true)
                .setAutoCancel(true)
                .setFullScreenIntent(notificationPendingIntent, true)
                .setPriority(NotificationManager.IMPORTANCE_HIGH);


     /*   Intent reject = new Intent();
        reject.setAction(AppConstant.ACTION_REJECT);
        PendingIntent pendingIntentYes2 = PendingIntent.getBroadcast(this, 12345, reject, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_close_black_24dp, AppConstant.ACTION_REJECT, pendingIntentYes2);


        Intent accept = new Intent();
        accept.setAction(AppConstant.ACTION_ACCEPT);
        accept.putExtra("delivery_id",delivery_id);
        PendingIntent pendingIntentYes = PendingIntent.getBroadcast(this, 12345, accept, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_check_black_24dp, AppConstant.ACTION_ACCEPT, pendingIntentYes);

*/

        final Notification notification = builder.build();


        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(1, notification);

    }

    public void approvalNotification(String msg) {


        Intent intent = new Intent(MyFirebaseMessagingService.this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

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
                .setAutoCancel(true)
                .setStyle(bigText)
                .setPriority(NotificationManager.IMPORTANCE_HIGH);


        if (msg.contains("Dis")) {

        } else {
            builder.setFullScreenIntent(notificationPendingIntent, true);
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
