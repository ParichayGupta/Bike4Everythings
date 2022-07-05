package com.b4edriver.GCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.b4edriver.CommonClasses.ServerConnection.Util;
import com.b4edriver.CommonClasses.Services.AlarmServicesDriver;
import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.b4edriver.CommonClasses.Utils.Function;
import com.b4edriver.Database.DBAdapter_Driver;
import com.b4edriver.DriverApp.NavigationDrawerDriver;
import com.b4edriver.DriverApp.TripDetailActivityDriver;
import com.b4edriver.Model.AllMessageDriver;
import com.b4edriver.Model.NotificationDriver;
import com.b4edriver.Model.TripDriver;
import com.b4edriver.b4edrivers.AppPreferences;
import com.b4elibrary.Logger;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = "MyFirebaseMsgService";
    DBAdapter_Driver driver_db;

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        driver_db = new DBAdapter_Driver(this);

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
            if (!message.equalsIgnoreCase("null")) {
                MyNotification.getInstance(this).sendNotification(message);

            }


                String driverApproval = "" + remoteMessage.getData().get(ConfigDriver.DRIVER_APPROVAL);
                String newTrip = "" + remoteMessage.getData().get(ConfigDriver.NEW_TRIP);

                String cancelTripByUser = "" + remoteMessage.getData().get(ConfigDriver.CANCELTRIPBYUSER);
                String cancelTripByDriver = "" + remoteMessage.getData().get(ConfigDriver.CANCELTRIPBYDRIVER);
                String reciveMessage = "" + remoteMessage.getData().get(ConfigDriver.RECIVE_MESSAGE);
                String cancelTripAuto = "" + remoteMessage.getData().get(ConfigDriver.CANCELTRIPAUTO);
                String userPay = "" + remoteMessage.getData().get(ConfigDriver.USERPAY);
               // String OTHER_SERVICE_PAY = "" + remoteMessage.getData().get(ConfigDriver.OTHER_SERVICE_PAY);
                String tripacceptcancel = "" + remoteMessage.getData().get(ConfigDriver.TRIPACCEPTCANCEL);
                String tripCancelByAdmin = "" + remoteMessage.getData().get(ConfigDriver.TRIPCANCELBYADMIN);
                String ItemDetailsUpdatedByUser = "" + remoteMessage.getData().get(ConfigDriver.ITEMDETAILSUPDATEDBYUSER);
                String notifydrivertoendtrip = "" + remoteMessage.getData().get(ConfigDriver.NOTIFYDRIVERTOENDTRIP);
                String triplogNotify = "" + remoteMessage.getData().get(ConfigDriver.TRIPLOGNOTIFY);
                String gcmNotificationError = "" + remoteMessage.getData().get(ConfigDriver.gcmNotificationError);
                String loginSessionExpire = "" + remoteMessage.getData().get(ConfigDriver.loginSessionExpire);
                Logger.log("NOTIFYMSG", remoteMessage.getData().toString());
                //OTHER_SERVICE_PAY = "{\"message\":\"The Sum of Rs 1.00 Amount is Received.\"}";
                if (!message.equalsIgnoreCase("null")) {
                    MyNotification.getInstance(this).sendNotification(message);
                } else if (!gcmNotificationError.equalsIgnoreCase("null")) {
                    MyNotification.getInstance(this).gcmNotificationErrorNotify("Alert - Your location is not getting updated.");


                } else if (!loginSessionExpire.equalsIgnoreCase("null")) {
                    try {
                        JSONObject jsonObject = new JSONObject(loginSessionExpire);

                        MyNotification.getInstance(this).loginSessionExpireNotify(jsonObject.getString("msg"));
                           /* if(!Function.isAppIsInBackground(this)) {

                            }*/
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else if (!triplogNotify.equalsIgnoreCase("null")) {
                    MyNotification.getInstance(this).tripLogdNotify(triplogNotify);


                } else if (!userPay.equalsIgnoreCase("null")) {
                    MyNotification.getInstance(this).userPayNotify(userPay);


                } else if (!notifydrivertoendtrip.equalsIgnoreCase("null")) {
                    Intent intent1 = new Intent("notifydrivertoendtrip");
                    intent1.putExtra("msg", notifydrivertoendtrip);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
                    MyNotification.getInstance(this).tripendNotify(notifydrivertoendtrip);


                } else if (!ItemDetailsUpdatedByUser.equalsIgnoreCase("null")) {
                    MyNotification.getInstance(this).ItemDetailsUpdatedNotify(ItemDetailsUpdatedByUser);


                } else if (!tripCancelByAdmin.equalsIgnoreCase("null")) {
                    AppPreferencesDriver.setTripId(this, "");
                    AppPreferencesDriver.setItemDetails(this, "");
                    AppPreferencesDriver.setActivity(this, NavigationDrawerDriver.class.getName());

                    NotificationDriver notification = new NotificationDriver();
                    notification.setHeader("Trip Canceled");
                    notification.setDescription("Trip canceled by admin");
                    notification.setTime(Function.getCurrentDateTime());
                    driver_db.insertNotification(notification);

                    String msg = "Trip canceled by admin";

                    try {
                        JSONArray jsonArray = new JSONArray(cancelTripByUser);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        msg = "Trip canceled by admin.\n" + jsonObject.getString("message");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(!AlarmServicesDriver.pendingTrip.isEmpty()) {
                        AlarmServicesDriver.pendingTrip.remove(0);
                    }
                    AppPreferencesDriver.setPendingTrip(this, null);
                    //MyNotification.getInstance(this).cancelTripByUserNotify(msg);

                    Intent intent11 = new Intent(this, NavigationDrawerDriver.class);
                    intent11.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent11.setAction("");
                    startActivity(intent11);

                } else if (!tripacceptcancel.equalsIgnoreCase("null")) {
                    // tripacceptcancelNotify(tripacceptcancel);
                    String cancelMessage = "";
                    try {
                        JSONArray jsonArray = new JSONArray(tripacceptcancel);
                        if(!jsonArray.isNull(0)) {
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String trip_id = jsonObject.getString("trip_id");
                            cancelMessage = jsonObject.getString("message");

                            NotificationDriver notification = new NotificationDriver();
                            notification.setHeader("Approval");
                            notification.setDescription("Trip id:"+trip_id+" "+cancelMessage);
                            notification.setTime(Function.getCurrentDateTime());
                            driver_db.insertNotification(notification);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //MyNotification.getInstance(this).tripAcceptByOtherDriverNotify(cancelMessage);

                    if (TripDetailActivityDriver.instance != null) {
                        if(!AlarmServicesDriver.pendingTrip.isEmpty()) {
                            AlarmServicesDriver.pendingTrip.remove(0);
                        }
                        AppPreferencesDriver.setPendingTrip(this, null);
                        Intent intent11 = new Intent(this, NavigationDrawerDriver.class);
                        intent11.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
                        intent11.setAction("");
                        startActivity(intent11);

                    }


                } else if (!driverApproval.equalsIgnoreCase("null")) {


                    if (driverApproval.equalsIgnoreCase("Approve by admin")) {
                        MyNotification.getInstance(this).driverApprovalNotify(driverApproval);

                        NotificationDriver notification = new NotificationDriver();
                        notification.setHeader("Approval");
                        notification.setDescription("Congratulations, Your biker application with B4E is aprove you can now login to start B4E journey");
                        notification.setTime(Function.getCurrentDateTime());
                        driver_db.insertNotification(notification);
                    } else {
                        MyNotification.getInstance(this).driverUnApprovalNotify(driverApproval);
                        //AppPreferencesDriver.setUserId(this,0);
                        AppPreferencesDriver.setTripId(this, "");
                        AppPreferencesDriver.setItemDetails(this, "");
                        AppPreferencesDriver.setActivity(this, NavigationDrawerDriver.class.getName());


                        Class<?> homeClass = null;
                        try {
                            homeClass = Class.forName("com.b4euser.UserApp.homeScreen.HomeScreen");
                            Intent intent11 = new Intent(this, homeClass);
                            intent11.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent11.setAction("");
                            startActivity(intent11);


                            NotificationDriver notification = new NotificationDriver();
                            notification.setHeader("Dis-Approval");
                            notification.setDescription("Dis-Approve by admin" + "\n");
                            notification.setTime(Function.getCurrentDateTime());
                            driver_db.insertNotification(notification);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }


                    }


                } else if (!newTrip.equalsIgnoreCase("null") && AppPreferencesDriver.getTripId(this).equalsIgnoreCase("")) {

                    startService(new Intent(this, AlarmServicesDriver.class));
                    try {
                        JSONObject object = new JSONObject(newTrip);

                        if (newTrip.contains("otherService")) {
                            otherServices(object);

                        } else {
                            taxiServices(object);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (!cancelTripByUser.equalsIgnoreCase("null")) {

                    AppPreferencesDriver.setTripId(this, "");
                    AppPreferencesDriver.setItemDetails(this, "");
                    AppPreferencesDriver.setActivity(this, NavigationDrawerDriver.class.getName());

                    NotificationDriver notification = new NotificationDriver();
                    notification.setHeader("Trip Cancel");
                    notification.setDescription("Trip cancel by user");
                    notification.setTime(Function.getCurrentDateTime());
                    driver_db.insertNotification(notification);

                    String msg = "Trip cancel by user";

                    try {
                        JSONArray jsonArray = new JSONArray(cancelTripByUser);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        msg = "Trip canceled by user.\n" + jsonObject.getString("message");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    MyNotification.getInstance(this).cancelTripByUserNotify(msg);

                    Intent intent11 = new Intent(this, NavigationDrawerDriver.class);
                    intent11.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent11.setAction("");
                    startActivity(intent11);


                } else if (!cancelTripByDriver.equalsIgnoreCase("null")) {

                    Intent intent11 = new Intent(this, NavigationDrawerDriver.class);
                    intent11.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent11.setAction("");
                    startActivity(intent11);

                    AppPreferencesDriver.setTripId(this, "");
                    AppPreferencesDriver.setItemDetails(this, "");
                    AppPreferencesDriver.setActivity(this, NavigationDrawerDriver.class.getName());
                } else if (!reciveMessage.equalsIgnoreCase("null")) {

                    try {
                        JSONObject jsonObject = new JSONObject(reciveMessage);
                        AllMessageDriver allMessage = new AllMessageDriver();
                        allMessage.setText(jsonObject.getString("msg"));
                        allMessage.setDate(DateFormat.getTimeInstance().format(new Date()));
                        driver_db.insertAllMessage(allMessage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    AppPreferencesDriver.setReciveMessage(this, reciveMessage);
                    MyNotification.getInstance(this).newMessageNotify(reciveMessage);


                } else if (!cancelTripAuto.equalsIgnoreCase("null")) {

                    AppPreferencesDriver.setTripId(this, "");
                    AppPreferencesDriver.setItemDetails(this, "");
                    AppPreferencesDriver.setActivity(this, getClass().getName());



                   /* Intent intent11 = new Intent("cancelTripAuto");
                    intent11.putExtra("msg","TripDriver Canceled by user");

                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent11);
                   */

                    if (TripDetailActivityDriver.instance != null) {

                        if(!AlarmServicesDriver.pendingTrip.isEmpty()) {
                            AlarmServicesDriver.pendingTrip.remove(0);
                        }
                        AppPreferencesDriver.setPendingTrip(this, null);

                        //MyNotification.getInstance(this).cancelTripByUserNotify("Trip Canceled by user");


                        Intent intent11 = new Intent(this, NavigationDrawerDriver.class);
                        intent11.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK  );
                        intent11.setAction("");
                        startActivity(intent11);
                    }
                }



            /*>>>>>>>>>>>>>>>>>>*/
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }


    private void otherServices(JSONObject object) {

        /*newTrip={"otherService":[{"driver_id":"5",}]}*/

        try {
            JSONArray jsonArray = object.getJSONArray("otherService");
            JSONObject jsonObject = jsonArray.getJSONObject(0);

            String tripId = jsonObject.getString("trip_id");

            String pickCname = jsonObject.getString("pickCname");
            String pickCno = jsonObject.getString("pickCno");
            String pickup = jsonObject.getString("pickup_address");

            String dropCname = jsonObject.getString("dropCname");
            String dropCno = jsonObject.getString("dropCno");
            String Drop = jsonObject.getString("drop_address");

            String added_on = jsonObject.getString("added_on");
            String title = jsonObject.getString("title");


            String disc = "Trip id : " + tripId + "\n" +
                    "Pickup Address : " + pickup + "\n" +
                    "Drop Address : " + Drop + "";

            //   Logger.log("tripidsss", disc);

            TripDriver trip = new TripDriver();
            trip.setId(Long.parseLong(tripId));
            trip.setSourceAddress(pickup);
            trip.setDestinationAddress(Drop);
            trip.setDate(added_on);
            trip.setPickCname(pickCname);
            trip.setPickCno(pickCno);
            trip.setDropCname(dropCname);
            trip.setDropCno(dropCno);
            trip.setMonth(title);
            trip.setService_id("otherService");

            AlarmServicesDriver.pendingTrip.add(trip);

            AppPreferencesDriver.setPendingTrip(this, trip);

            //MyNotification.getInstance(this).newOtherTripNotify(trip);

            NotificationDriver notification = new NotificationDriver();
            notification.setHeader("Trip Details");
            notification.setDescription(disc);
            notification.setTime(Function.getCurrentDateTime());
            driver_db.insertNotification(notification);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void taxiServices(JSONObject object) {
        try {
            JSONArray jsonArray = object.getJSONArray("taxiService");
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String tripId = jsonObject.getString("trip_id");
            String pickup = jsonObject.getString("pickup_address");
            String Drop = jsonObject.getString("drop_address");
            String added_on = jsonObject.getString("added_on");
            String title = jsonObject.getString("title");

            String disc = "Trip id : " + tripId + "\n" +
                    "Pickup Address : " + pickup + "\n" +
                    "Drop Address : " + Drop + "";

            //   Logger.log("tripidsss", disc);

            TripDriver trip = new TripDriver();
            trip.setId(Long.parseLong(tripId));
            trip.setSourceAddress(pickup);
            trip.setDestinationAddress(Drop);
            trip.setDate(added_on);
            trip.setMonth(title);
            trip.setService_id("taxiService");


            AlarmServicesDriver.pendingTrip.add(trip);

            AppPreferencesDriver.setPendingTrip(this, trip);

            NotificationDriver notification = new NotificationDriver();
            notification.setHeader("Trip Details");
            notification.setDescription(disc);
            notification.setTime(Function.getCurrentDateTime());
            driver_db.insertNotification(notification);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
