package com.bike4everythingbussiness.Reciver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.bike4everythingbussiness.Activity.MainActivity;
import com.bike4everythingbussiness.Activity.SplashActivity;
import com.bike4everythingbussiness.DatabaseHandler;
import com.bike4everythingbussiness.Model.OrderDetails;
import com.bike4everythingbussiness.R;
import com.bike4everythingbussiness.Utils.AppConstant;
import com.bike4everythingbussiness.Utils.ConnectivityReceiver;
import com.bike4everythingbussiness.Utils.NotificationHelper;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.bike4everythingbussiness.Utils.ConnectivityReceiver.isConnected;

/**
 * Created by manishsingh on 16/02/18.
 */


public class ScheduleBookingReceiver extends BroadcastReceiver  {

    public static int ALARM_TYPE_RTC = 100;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context =context;
        if(isConnected()){
            cancelNotification(context, ALARM_TYPE_RTC);
            OrderDetails orderDetails = intent.getParcelableExtra("orderDetails");
            new OrderNow(orderDetails).execute();
            NotificationHelper.cancelAlarm(context);
        }else {

            if(intent.getAction().equalsIgnoreCase(AppConstant.ACTION_CANCEL)){
                cancelNotification(context, ALARM_TYPE_RTC);
                NotificationHelper.cancelAlarm(context);
            }else if(intent.getAction().equalsIgnoreCase(AppConstant.ACTION_SNOOZE)){
                cancelNotification(context, ALARM_TYPE_RTC);
            }else if(intent.getAction().equalsIgnoreCase(AppConstant.ACTION_SETTING)){
                cancelNotification(context, ALARM_TYPE_RTC);
                Intent setting = new Intent("android.settings.WIFI_SETTINGS");
                context.startActivity(setting);
            }else {
                Intent intentToRepeat = new Intent(context, SplashActivity.class);
                intentToRepeat.setAction("scheduleBooking");
                intentToRepeat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentToRepeat.putExtra("orderDetails", intent.getParcelableExtra("orderDetails"));

                //Pending intent to handle launch of Activity in intent above
                PendingIntent pendingIntent =
                        PendingIntent.getActivity(context, ALARM_TYPE_RTC, intentToRepeat, PendingIntent.FLAG_UPDATE_CURRENT);

                //Build notification
                Notification repeatedNotification = buildLocalNotification(context, pendingIntent).build();

                //Send local notification
                getNotificationManager(context).notify(ALARM_TYPE_RTC, repeatedNotification);
            }
        }
    }

    public static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }


   /* public NotificationCompat.Builder buildLocalNotification(Context context, PendingIntent pendingIntent) {
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.bikelogo)
                        .setSound(Notification.DEFAULT_SOUND)
                        .setContentTitle("Reminder you B2B schedule booking")
                        .setContentText("Please connect your internet connection.")
                        .setAutoCancel(true);

        return builder;
    }*/

    public NotificationCompat.Builder buildLocalNotification(Context context, PendingIntent pendingIntent) {

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("Please connect you internet connection");
        bigText.setBigContentTitle("B2B Schedule booking");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.bikelogo)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.bikelogo))
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setContentTitle("B2B Schedule booking")
                .setContentIntent(pendingIntent)
                .setContentText("Please connect you internet connection")
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_VIBRATE)
                .setSound(defaultSoundUri)
                .setStyle(bigText)
                .setOngoing(true)
                .setFullScreenIntent(pendingIntent,true)
                .setPriority(NotificationManager.IMPORTANCE_HIGH);


        Intent cancelIntent = new Intent();
        cancelIntent.setAction(AppConstant.ACTION_CANCEL);
        PendingIntent pendingIntentYes2 = PendingIntent.getBroadcast(context, 12345, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_dialog_close_light, "Booking cancel", pendingIntentYes2);


        Intent snoozeIntent = new Intent();
        snoozeIntent.setAction(AppConstant.ACTION_SNOOZE);
        PendingIntent pendingIntentYes = PendingIntent.getBroadcast(context, 12345, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(android.R.drawable.ic_lock_idle_alarm, "Snooze", pendingIntentYes);


        Intent settingIntent = new Intent();
        settingIntent.setAction(AppConstant.ACTION_SETTING);
        PendingIntent pendingsettingIntent = PendingIntent.getBroadcast(context, 12345, settingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_settings_applications_black_24dp, "Setting", pendingsettingIntent);



       return builder;



    }
    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

    public class OrderNow extends AsyncTask<Void,Void,String> {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OrderDetails orderDetails;


        public OrderNow(OrderDetails orderDetails) {
            this.orderDetails = orderDetails;
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
                    jsonObject.put("schedule",orderDetails.getSchedule());

                    jsonObject.put("pickName",orderDetails.getPickName());
                    jsonObject.put("pickMobile",orderDetails.getPickMobile());
                    jsonObject.put("pickAddress",orderDetails.getPickAddress());
                    jsonObject.put("pickAddressName",orderDetails.getPickAddressName());
                    jsonObject.put("pickLat",orderDetails.getPickLat());
                    jsonObject.put("pickLng",orderDetails.getPickLng());


                    JSONArray dropArray = new JSONArray();
                    for(int i=0; i<orderDetails.getDropAddressList().size(); i++) {
                        JSONObject dropObject = new JSONObject();
                        dropObject.put("dropName", orderDetails.getDropAddressList().get(i).getDropName());
                        dropObject.put("dropMobile", orderDetails.getDropAddressList().get(i).getDropMobile());
                        dropObject.put("dropAddress", orderDetails.getDropAddressList().get(i).getDropAddress());
                        dropObject.put("dropAmount", orderDetails.getDropAddressList().get(i).getDropAmount());
                        dropObject.put("dropLat", orderDetails.getDropAddressList().get(i).getDropLat());
                        dropObject.put("dropLng", orderDetails.getDropAddressList().get(i).getDropLng());
                        dropArray.put(dropObject);
                    }

                    jsonObject.put("dropAddressList",dropArray);
                    jsonObject.put("note",orderDetails.getNote());
                    jsonObject.put("returnRequired",orderDetails.isReturnrequired());
                    jsonObject.put("fcm_token_id", FirebaseInstanceId.getInstance().getToken());
                    Log.e("Request_Response", jsonObject.toString());
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
            if(!result.equalsIgnoreCase("")){
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.getString("status").equalsIgnoreCase("200")){
                        new DatabaseHandler(context).updateAllDropStatus(false);
                        new DatabaseHandler(context).addOrder(orderDetails);
                        new DatabaseHandler(context).updateOrderSelect(Integer.parseInt(orderDetails.getDeliveryId()),
                                jsonObject.getString("delivery_id"), true);


                    }else if(jsonObject.getString("status").equalsIgnoreCase("400")){

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
