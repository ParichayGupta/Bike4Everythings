package com.b4edriver.CommonClasses.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.b4edriver.CommonClasses.ServerConnection.JSONParser;
import com.b4edriver.CommonClasses.ServerConnection.ServerErrorCallBack;
import com.b4edriver.CommonClasses.ServerConnection.VolleyCallBack;
import com.b4edriver.CommonClasses.Services.AlarmServicesDriver;
import com.b4edriver.DriverApp.RecieptActivityDriver;
import com.b4edriver.DriverApp.TaxiWaitingActivityDriver;
import com.b4edriver.DriverApp.TripAcceptActivityDriver;
import com.b4edriver.DriverApp.TripStartedActivityDriver;
import com.b4edriver.Model.TripDriver;
import com.b4elibrary.Logger;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CheckDriverStatus {
    private static final CheckDriverStatus ourInstance = new CheckDriverStatus();

    private CheckDriverStatus() {
    }

    public static CheckDriverStatus getInstance() {
        return ourInstance;
    }

    public void checkStatus(final Context context) {


        try {
            JSONObject jsonObjec = new JSONObject();
            jsonObjec.put("method", AppConstantDriver.METHOD.CHECK_DRIVER_STATUS);
            jsonObjec.put("driver_id", AppPreferencesDriver.getDriverId(context));

            JSONParser jsonParser = new JSONParser(context);
            jsonParser.parseVollyJSONObject(AppConstantDriver.URL.ONDEMAND_DRIVER_DRIVERCHECKDRIVERSTATUS, 1, jsonObjec, "Checking driver status...", new VolleyCallBack() {
                @Override
                public void success(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Logger.log("checkDriverStatus11", jsonObject.toString());
                        final JSONArray jsonArray = jsonObject.getJSONArray("result");
                        Logger.log("checkDriverStatus22", jsonArray.toString());
                        final JSONObject object = jsonArray.getJSONObject(0);
 /* {"result":[{"current_statusId":"1","current_status":"Driver Accept",
                                    "message":"You already have an unfinished running trip. Please complete the old trip.",
                                    "trip_id":"9","status":"400"}]}*/
                        Logger.log("checkDriverStatus333", object.toString());
                        if (object.getString("status").equalsIgnoreCase("400")) {
                            Logger.log("checkDriverStatus>>>", object.getString("status"));
                            final String statusId = object.getString("current_statusId");
                            final String tripId = object.getString("trip_id");

                            // new SendMail(tripId,statusId).execute();

                            new AlertDialog.Builder(context)
                                    .setTitle("Bike 4 Everything")
                                    .setMessage(object.getString("message"))
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            try {

                                                JSONObject jsonObject1 = object.getJSONObject("trip_data");

                                                AppPreferencesDriver.setTripstatusForDriver(context, statusId);
                                                AppPreferencesDriver.setTripId(context, jsonObject1.getString("id"));

                                                TripDriver trips = new TripDriver();
                                                trips.setId(Long.parseLong(jsonObject1.getString("id")));
                                                trips.setSourceAddress(jsonObject1.getString("pickup_address"));
                                                trips.setDestinationAddress(jsonObject1.getString("drop_address"));
                                                trips.setSourceLatitude(new Double(jsonObject1.getString("pickup_lat")));
                                                trips.setSourcelogitude(new Double(jsonObject1.getString("pickup_lng")));
                                                if (jsonObject1.getString("drop_lat").equalsIgnoreCase("")) {
                                                    trips.setDestinationLatitude(new Double(0.0));
                                                } else {
                                                    trips.setDestinationLatitude(new Double(jsonObject1.getString("drop_lat")));
                                                }

                                                if (jsonObject1.getString("drop_lng").equalsIgnoreCase("")) {
                                                    trips.setDestinationLogitude(new Double(0.0));
                                                } else {
                                                    trips.setDestinationLogitude(new Double(jsonObject1.getString("drop_lng")));
                                                }
                                                //trips.setDestinationLatitude(new Double(jsonObject1.getString("drop_lat")));
                                                // trips.setDestinationLogitude(new Double(jsonObject1.getString("drop_lng")));
                                                trips.setDistance(jsonObject1.getString("est_km"));

                                                trips.setUserName(jsonObject1.getString("user_name"));
                                                trips.setUserNumber(jsonObject1.getString("user_number"));
                                                trips.setPickCno(jsonObject1.getString("mobile"));
                                                trips.setPickCname(jsonObject1.getString("name"));
                                                trips.setDropCname(jsonObject1.getString("drop_name"));
                                                trips.setDropCno(jsonObject1.getString("drop_mobile"));
                                                trips.setService_id(jsonObject1.getString("service_id"));
                                                trips.setDate(jsonObject1.getString("added_on"));
                                                trips.setFare(jsonObject1.getString("est_fare"));
                                                trips.setBusinessName(jsonObject1.optString("businessName"));

                                                try {
                                                    Logger.log("farecalculate", trips.getFare());
                                                    String[] fare = trips.getFare().split("ß");
                                                    AppPreferencesDriver.setEstmateprice(context, fare[0]);
                                                    AppPreferencesDriver.setEstmatedistination(context, fare[1]);
                                                    AppPreferencesDriver.setEstmatedistance(context, fare[2]);
                                                    try {
                                                        //AppPreferencesDriver.setServiceType(context, fare[3]);
                                                        AppPreferencesDriver.setServiceType(context, "");
                                                    } catch (Exception ee) {
                                                        AppPreferencesDriver.setServiceType(context, "");
                                                    }
                                                    try {
                                                        AppPreferencesDriver.setDeleveryCharge(context, fare[4]);
                                                    } catch (Exception ee) {
                                                        AppPreferencesDriver.setServiceType(context, "");
                                                    }
                                                } catch (Exception e) {

                                                }
                                                Logger.log("farecalculate", AppPreferencesDriver.getEstmateprice(context) + "\n" + AppPreferencesDriver.getEstmatedistination(context));


                                                Intent intent;
                                                if (statusId.equalsIgnoreCase("1")) {
                                                    intent = new Intent(context, TripAcceptActivityDriver.class);
                                                } else if (statusId.equalsIgnoreCase("2")) {
                                                    intent = new Intent(context, TaxiWaitingActivityDriver.class);
                                                } else if (statusId.equalsIgnoreCase("4")) {
                                                    intent = new Intent(context, TripStartedActivityDriver.class);
                                                } else {
                                                    intent = new Intent(context, RecieptActivityDriver.class);
                                                }
                                                if (AppPreferencesDriver.getServiceType(context).equalsIgnoreCase("saprateOtherServices")) {
                                                    intent = new Intent(context, com.b4edriver.b4edrivers.MainActivity.class);
                                                }

                                                intent.putExtra("tripDetails", trips);
                                                intent.putExtra("tripId", tripId);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                context.startActivity(intent);
                                                ((AppCompatActivity) context).finish();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    })
                                    .show();


                        } else {
                            AppPreferencesDriver.setTripId(context, "");
                            AppPreferencesDriver.setPendingTripId(context, "");
                            if (!AlarmServicesDriver.pendingTrip.isEmpty()) {
                                AlarmServicesDriver.pendingTrip.remove(0);
                            }
                            AppPreferencesDriver.setPendingTrip(context, null);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new ServerErrorCallBack() {
                @Override
                public void error(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg = jsonObject.getString("vollymsg");
                        showDialog(context, msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showDialog(Context context, String msg) {

        new AlertDialog.Builder(context)
                .setTitle("Oops...")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }

}
