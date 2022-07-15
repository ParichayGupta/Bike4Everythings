package com.hvantage.b4eemp.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.b4elibrary.Logger;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hvantage.b4eemp.R;
import com.hvantage.b4eemp.activity.BookingActivity;
import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = "MyFirebaseMsgService";

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Logger.log(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getNotification() != null) {
            Logger.log(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getData().size() > 0) {
            Logger.log(TAG, "Message data payload: " + remoteMessage.getData());

            /*>>>>>>>>>>>>>>>>>>*/
            //remoteMessage.getData().get(Config.MESSAGE);
            String message = "" + remoteMessage.getData().get(Config.MESSAGE);
            String NEWRENTBOOK = "" + remoteMessage.getData().get(Config.NEWRENTBOOK);
            String UPDATERENTBOOK = "" + remoteMessage.getData().get(Config.UPDATERENTBOOK);
            String EMP_15MINTODROPOFF = "" + remoteMessage.getData().get(Config.EMP_15MINTODROPOFF);
            String EMP_DROPOFF = "" + remoteMessage.getData().get(Config.EMP_DROPOFF);
            if (!message.equalsIgnoreCase("null")) {

            }else if(!NEWRENTBOOK.equalsIgnoreCase("null")){
                newBooking(NEWRENTBOOK);
            }else if (!UPDATERENTBOOK.equalsIgnoreCase("null")) {

                updatebooking(UPDATERENTBOOK);
            }else if (!EMP_15MINTODROPOFF.equalsIgnoreCase("null")) {

                updatebooking(EMP_15MINTODROPOFF);
            }else if (!EMP_DROPOFF.equalsIgnoreCase("null")) {

                updatebooking(EMP_DROPOFF);
            }



        }
    }

    public void updatebooking(String msg) {
        NotificationManager mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, BookingActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("B4E")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentText(msg);


        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(1, mBuilder.build());
    }
    public void newBooking(String json) {
        NotificationManager mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, BookingActivity.class);
        intent.setAction("NEWRENTBOOK");
        intent.putExtra("data",json);

        JSONObject object = null;
        try {
            object = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String title = object.optString("title");
        String message = object.optString("message");

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentText(message);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setSmallIcon(R.drawable.b4e_bike1);
            mBuilder.setColor(getResources().getColor(R.color.green));
        } else {
            mBuilder.setSmallIcon(R.drawable.b4e_bike1);
        }


        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(0, mBuilder.build());

        Intent intent1 = new Intent("NEWRENTBOOK");
        intent1.putExtra("data",json);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

}
