package com.b4edriver.CommonClasses.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.b4edriver.Model.TripDriver;
import com.google.gson.Gson;

/**
 * Created by MMFA-MANISH on 16/3/2016.
 */
public class AppPreferencesDriver {
    /*USER*/
    public static final String PREFERENCES = "B4EDRIVER";
    public static final String DRIVERAPPROVAL = "DRIVERAPPROVAL";
    public static final String DRIVERACCEPTED = "DRIVERACCEPTED";
    public static final String DRIVERTYPE = "DRIVERTYPE";
    public static final String TRIPDATE = "TRIPDATE";
    public static final String TRIPSTATUSFORDRIVER = "TRIPSTATUSFORDRIVER";

    public static final String BASIC_FARE = "BASIC_FARE";
    public static final String PER_FARE = "PER_FARE";
    public static final String OTHER_FARE = "OTHER_FARE";
    public static final String LOGINDATE = "LOGINDATE";
    public static final String PROMOCODE = "PROMOCODE";
    public static final String PROMOID = "PROMOID";
    public static final String ESTMATEPRICE = "ESTMATEPRICE";
    public static final String ESTMATEDISTINATION = "ESTMATEDISTINATION";
    public static final String ESTMATEDISTANCE = "ESTMATEDISTANCE";
    public static final String ISONLINE = "ISONLINE";
    public static final String PERKMCHARGE = "perkmcharge";


    /*DRIVER*/

    public static final String DRIVER_ID = "driver_id";
    public static final String TRIP_ID = "trip_id";
    public static final String ACTIVITY = "activity";
    public static final String START_TIME = "start_time";
    public static final String ARRIVED_TIME = "arrived_time";
    public static final String BOOKINGLOCATIONSPEED = "booking_location_speed";
    public static final String SOURCELATITUDE = "SOURCELATITUDE";
    public static final String SOURCELONGITUDE = "SOURCELONGITUDE";
    public static final String DESTILATITUDE = "DESTILATITUDE";
    public static final String DESTILOGITUDE = "DESTILOGITUDE";
    public static final String SOURCEADDRESS = "SOURCEADDRESS";
    public static final String DESTIADDRESS = "DESTIADDRESS";
    public static final String TRIPSTART = "TRIPSTART";

    public static final String RECIVE_MESSAGE = "RECIVE_MESSAGE";
    public static final String OFFLINE = "OFFLINE";
    public static final String ITEMDETAILS = "ITEMDETAILS";

    private static final String KEY_LAT = "latitude";
    private static final String KEY_LONG = "longitude";
    private static final String KEY_DRIVER_STATUS = "driver_current_status";
    private static final String KEY_PENDINGTRIPID = "driver_pendingid";
    private static final String KEY_PENDINGTRIP = "driver_pending";
    private static final String KEY_WAITAPI = "key_waitapi";
    private static final String KEY_BATTERYSTATUS = "key_batterystatus";
    private static final String KEY_UNIQUE_NUMBER = "unique_number";
    private static final String TIMER_DATA = "timer_data";
    private static final String DRIVER_LOGIN_TIME = "driver_login_time";
    private static final String CURRENT_DRIVER_DURATION = "current_driver_duration";
    private static final String TOTAL_DRIVER_DURATION = "total_driver_duration";
    private static final String REFERRAL = "REFERRAL";
    private static final String SERVICE_TYPE = "service_type";
    private static final String DELEVERY_CHARGE = "Delevery_charge";
    private static final String DRIVER_TYPE = "driver_type";


    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static int getDriverType(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getInt(DRIVER_TYPE, 0);
    }

    public static void setDriverType(Context context, int value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(DRIVER_TYPE, value);
        editor.commit();
    }

    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static boolean isonline(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getBoolean(ISONLINE, true);
    }

    public static void setIsonline(Context context, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(ISONLINE, value);
        editor.commit();
    }
    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static String getTotalDriverDuration(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(TOTAL_DRIVER_DURATION, "0000-00-00 00:00:00");
    }

    public static void setTotalDriverDuration(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TOTAL_DRIVER_DURATION, value);
        editor.commit();
    }
    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static String getCurrentDriverDuration(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(CURRENT_DRIVER_DURATION, "0000-00-00 00:00:00");
    }

    public static void setCurrentDriverDuration(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CURRENT_DRIVER_DURATION, value);
        editor.commit();
    }
    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static String getDriverLoginTime(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(DRIVER_LOGIN_TIME, "0000-00-00 00:00:00");
    }

    public static void setDriverLoginTime(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(DRIVER_LOGIN_TIME, value);
        editor.commit();
    }
    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static String getTimerData(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(TIMER_DATA, "");
    }

    public static void setTimerData(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TIMER_DATA, value);
        editor.commit();
    }
    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static String getUniqueNumber(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(KEY_UNIQUE_NUMBER, "9755299999");
    }

    public static void setUniqueNumber(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_UNIQUE_NUMBER, value);
        editor.commit();
    }
    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static String getBatteryStatus(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(KEY_BATTERYSTATUS, "0");
    }

    public static void setBatteryStatus(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_BATTERYSTATUS, value);
        editor.commit();
    }
    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static boolean getWaitApi(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getBoolean(KEY_WAITAPI, false);
    }

    public static void setWaitApi(Context context, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_WAITAPI, value);
        editor.commit();
    }
    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static TripDriver getPendingTrip(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pereference.getString(KEY_PENDINGTRIP, "");
        return gson.fromJson(json, TripDriver.class);
    }

    public static void setPendingTrip(Context context, TripDriver value) {
        try {

        Gson gson = new Gson();
        String json = gson.toJson(value);
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_PENDINGTRIP, json);
        editor.commit();

        }catch (NullPointerException e){}
    }
    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static String getPendingTripId(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(KEY_PENDINGTRIPID, "");
    }

    public static void setPendingTripId(Context context, String value) {
        try {
            SharedPreferences preferences = context.getSharedPreferences(
                    PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_PENDINGTRIPID, value);
            editor.commit();
        }catch (NullPointerException e){}
    }

    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static String getOffline(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(OFFLINE, "no");
    }

    public static void setOffline(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(OFFLINE, value);
        editor.commit();
    }
    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static String getTripStart(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(TRIPSTART, "no");
    }

    public static void setTripStart(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TRIPSTART, value);
        editor.commit();
    }
    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static String getReciveMessage(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(RECIVE_MESSAGE, "");
    }

    public static void setReciveMessage(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(RECIVE_MESSAGE, value);
        editor.commit();
    }


     /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static String getDriverapproval(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(DRIVERAPPROVAL, "no");
    }

    public static void setDriverapproval(Context context, String token) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(DRIVERAPPROVAL, token);
        editor.commit();
    }


    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static String getDrivertype(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(DRIVERTYPE, "");
    }

    public static void setDrivertype(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(DRIVERTYPE, value);
        editor.commit();
    }

    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static String getPromoid(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(PROMOID, "0");
    }

    public static void setPromoid(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PROMOID, value);
        editor.commit();
    }

    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static String getEstmateprice(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(ESTMATEPRICE, "0.0-0.0");
    }

    public static void setEstmateprice(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ESTMATEPRICE, value);
        editor.commit();
    }

    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static String getEstmatedistance(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(ESTMATEDISTANCE, "0.0-0.0");
    }

    public static void setEstmatedistance(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ESTMATEDISTANCE, value);
        editor.commit();
    }

    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static String getEstmatedistination(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(ESTMATEDISTINATION, "0.0-0.0");
    }

    public static void setEstmatedistination(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ESTMATEDISTINATION, value);
        editor.commit();
    }
    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static String getPromocode(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(PROMOCODE, "0");
    }

    public static void setPromocode(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PROMOCODE, value);
        editor.commit();
    }
    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/


    public static String getPerkmcharge(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(PERKMCHARGE, "");
    }

    public static void setPerkmcharge(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PERKMCHARGE, value);
        editor.commit();

    }
    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static Integer getBasicFare(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getInt(BASIC_FARE, 0);
    }

    public static void setBasicFare(Context context, int value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(BASIC_FARE, value);
        editor.commit();

    }

    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static Integer getPerFare(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getInt(PER_FARE, 0);
    }

    public static void setPerFare(Context context, int value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PER_FARE, value);
        editor.commit();

    }

    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static Integer getOtherFare(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getInt(OTHER_FARE, 0);
    }

    public static void setOtherFare(Context context, int value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(OTHER_FARE, value);
        editor.commit();

    }

    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static String getTripstatusForDriver(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(TRIPSTATUSFORDRIVER, "0");
    }

    public static void setTripstatusForDriver(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TRIPSTATUSFORDRIVER, value);
        editor.commit();

    }



    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static String getTripdate(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(TRIPDATE, "");
    }

    public static void setTripdate(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TRIPDATE, value);
        editor.commit();

    }




    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/


     /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static String getDriveraccepted(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(DRIVERACCEPTED, "");
    }

    public static void setDriveraccepted(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(DRIVERACCEPTED, value);
        editor.commit();
    }







    /*>>>>>>>>>>>>>>DRIVER>>>>>>>>>>>>>>>>>*/


     //////////////////////

    public static void setSourceaddress(Context context, String speed) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SOURCEADDRESS, speed);
        editor.commit();
    }

    public static String getSourceaddress(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(SOURCEADDRESS, "");
    }

    //////////////////////
    public static void setDestiaddress(Context context, String speed) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(DESTIADDRESS, speed);
        editor.commit();
    }

    public static String getDestiaddress(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(DESTIADDRESS, "");
    }

    //////////////////////
    public static void setSourcelatitude(Context context, String speed) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SOURCELATITUDE, speed);
        editor.commit();
    }

    public static String getSourcelatitude(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(SOURCELATITUDE, "0");
    }

    //////////////////////
    public static void setSourcelongitude(Context context, String speed) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SOURCELONGITUDE, speed);
        editor.commit();
    }

    public static String getSourcelongitude(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(SOURCELONGITUDE, "0");
    }

    //////////////////////
    public static void setDestilatitude(Context context, double lati) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(DESTILATITUDE, (float) lati);
        editor.commit();
    }

    public static float getDestilatitude(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getFloat(DESTILATITUDE, 0.0f);
    }

    //////////////////////
    public static void setDestilogitude(Context context, double logi) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(DESTILOGITUDE, (float) logi);
        editor.commit();
    }

    public static float getDestilogitude(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getFloat(DESTILOGITUDE, 0.0f);
    }

    //////////////////////
    public static void setSpeed(Context context, String speed) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(BOOKINGLOCATIONSPEED, speed);
        editor.commit();
    }

    public static String getSpeed(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(BOOKINGLOCATIONSPEED, "0");
    }


    ///////////////////////

    public static void setStartTime(Context context, String time) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(START_TIME, time);
        editor.commit();
    }

    public static String getStartTime(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(START_TIME, "");
    }

    ///////////////////////
    public static void setArrivedTime(Context context, String time) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(ARRIVED_TIME, time);
        editor.commit();
    }

    public static String getArrivedTime(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(ARRIVED_TIME, "");
    }



    ///////////////////////


    public static void setActivity(Context context, String className) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ACTIVITY, className);
        editor.commit();
    }

    public static String getActivity(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(ACTIVITY, "");
    }

     /*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    public static String getRefferalcode(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(REFERRAL, "0");
    }

    public static void setRefferalcode(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(REFERRAL, value);
        editor.commit();
    }

    ////////////////////////////////////////////////////////////

    public static void setTripId(Context context, String id) {
        try {
            SharedPreferences preferences = context.getSharedPreferences(
                    PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(TRIP_ID, id);
            editor.commit();
        }catch (NullPointerException e){}
    }

    public static String getTripId(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(TRIP_ID, "");
    }
    ////////////////////////////////////////////////////////////

    public static void setItemDetails(Context context, String item) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ITEMDETAILS, item);
        editor.commit();
    }

    public static String getItemDetails(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getString(ITEMDETAILS, "");
    }



    public static void setDriverId(Context context, int id) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(DRIVER_ID, id);
        editor.commit();
    }



    public static int getDriverId(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        return pereference.getInt(DRIVER_ID, 0);
    }

    /**
     * Storing the current latitude and longitude...
     *
     * @param context
     * @param latitude
     * @param longitude
     */
    public static void setLatLong(Context context, double latitude, double longitude) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Logger.log("Set LatLong", latitude + ", " + longitude);
        editor.putString(KEY_LAT, "" + latitude);
        editor.putString(KEY_LONG, "" + longitude);
        editor.commit();
    }

    public static double getLatitude(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
//        return Double.longBitsToDouble(sharedPreferences.getLong(KEY_LAT, 0.0));
        return Double.parseDouble(sharedPreferences.getString(KEY_LAT, "0.0"));
    }

    public static double getLongitude(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
//        return Double.longBitsToDouble(sharedPreferences.getLong(KEY_LONG, 0L));
        return Double.parseDouble(sharedPreferences.getString(KEY_LONG, "0.0"));
    }

    public static void setDriverStatus(Context context, String driverStatus) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_DRIVER_STATUS, driverStatus);
        editor.commit();
    }

    public static String getDriverStatus(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_DRIVER_STATUS, "");
    }

    public static void setServiceType(Context context, String driverStatus) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SERVICE_TYPE, driverStatus);
        editor.commit();
    }

    public static String getServiceType(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SERVICE_TYPE, "");
    }
    public static void setDeleveryCharge(Context context, String driverStatus) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DELEVERY_CHARGE, driverStatus);
        editor.commit();
    }

    public static String getDeleveryCharge(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(DELEVERY_CHARGE, "");
    }

    public static void clearDriverPreferences(Context context){
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().clear().commit();
    }
}
