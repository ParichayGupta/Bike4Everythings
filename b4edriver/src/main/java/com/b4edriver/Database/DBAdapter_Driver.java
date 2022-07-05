package com.b4edriver.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.b4edriver.CommonClasses.ServerConnection.Util;
import com.b4edriver.CommonClasses.Services.FusedLocationService;
import com.b4edriver.Model.AllMessageDriver;
import com.b4edriver.Model.DistanceTravelled;
import com.b4edriver.Model.NotificationDriver;
import com.b4edriver.Model.TripDriver;
import com.b4edriver.Model.UserDriver;
import com.b4elibrary.Logger;

import java.util.ArrayList;
import java.util.List;



public class DBAdapter_Driver extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    private static final String TAG = "DBAdapter_Driver";
    private static final String DATABASE_NAME = "taxidriver.db";

    public static final String KEY_ID = "id";

    /* TABLE NAME*/
    public static final String TBL_TRIP = "tbl_trip";
    public static final String TBL_TEMP_TRIP = "tbl_temp_trip";
    public static final String TBL_TRIP_DELETED = "tbl_trip_deleted";
    public static final String TBL_USER = "tbl_user";
    public static final String TBL_LOCATION = "tbl_location";
    public static final String TBL_NOTIFICATION = "tbl_notification";
    public static final String TBL_TRIP_HISTORY = "tbl_trip_history";
    public static final String TBL_SNOOZER = "tbl_snoozer";
    public static final String TBL_OFFLINE = "tbl_offline";
    public static final String TBL_ALLMSG = "TBL_ALLMSG";
    public static final String TBL_DISTANCE = "TBL_DISTANCE";
    private static final String TABLE_DISTANCE = "travelled_distance";
    private static final String TABLE_LOGS = "logs";

    /* KEY NAME*/
    public static final String ID = "id";
    public static final String KEY_DRIVER_ID = "driver_id";
    public static final String KEY_TRIP_ID = "trip_id";
    public static final String KEY_DISTANCE = "distance";
    public static final String KEY_AGREEMENT = "agreement";
    public static final String KEY_FARE = "fare";
    public static final String KEY_DATE = "date";
    public static final String KEY_ADDED_ON = "added_on";
    public static final String KEY_NAME = "name";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_MOBILE = "mobile";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_USERIMAGE = "userimage";
    public static final String KEY_EMAILID = "emailid";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_CUSTOMER_NAME = "customerName";
    public static final String KEY_CUSTOMER_IMAGE = "customerImage";
    public static final String KEY_SOURCEADDRESS = "sourceAddress";
    public static final String KEY_DESTINATIONADDRESS = "destinationAddress";
    public static final String KEY_WHERETOCOMETRIP = "whereToComeTrip";
    public static final String KEY_CURRENTLATITUDE = "currentLatitude";
    public static final String KEY_CURRENTLOGITUDE = "currentlogitude";
    public static final String KEY_SOURCELATITUDE = "sourceLatitude";
    public static final String KEY_SOURCELOGITUDE = "sourcelogitude";
    public static final String KEY_DESTINATIONLATITUDE = "destinationLatitude";
    public static final String KEY_DESTINATIONLOGITUDE = "destinationLogitude";
    public static final String KEY_TRIPTYPE = "triptype";
    public static final String KEY_TRAVELTIME = "travel_time";
    public static final String KEY_CORPORATETYPE = "corporate_type";
    public static final String KEY_NOTIFY_HEADING = "notify_heading";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_HISTORY_MONTH = "history_month";
    public static final String KEY_CUSTOMER_RATING = "KEY_CUSTOMER_RATING";
    private static final String KEY_METHOD = "method";

    private static final String KEY_DISTANCE_TOTAL = "total_distance";
    private static final String KEY_DISTANCE_FREE = "free_distance";
    private static final String KEY_DISTANCE_TO_CUSTOMER = "to_customer_distance";
    private static final String KEY_DISTANCE_TRIP = "trip_distance";
    private static final String KEY_DRIVER_STATUS = "driver_status";
    private static final String KEY_BOOKING_ID = "booking_id";


    private static final String KEY_LOG_TAG = "log_tag";
    private static final String KEY_LOG = "log";

    public static final String KEY_DURATION = "key_duration";


    private static final String KEY_CURRENT_LAT = "c_lat";
    private static final String KEY_CURRENT_LNG = "c_lng";
    public static final String KEY_STATUS = "status";
    private static final String KEY_DRIVER_TYPE = "driver_type";
    private static final String KEY_LOCATION_LIST = "location_list";
    private static final String KEY_TIME_DIFFERENCE = "time_difference";
    public static final String KEY_SNOOZE_TIME = "KEY_SNOOZE_TIME";

    /*Account Statment*/

    private static final String CREATE_TBL_TRIP_DELETED = "create table "
            + TBL_TRIP_DELETED + " (" + ID
            + " integer primary key autoincrement, "
            + KEY_TRIP_ID + " integer null, "
            + KEY_ADDED_ON + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ");";

 private static final String CREATE_TBL_DISTANCE = "create table "
            + TBL_DISTANCE + " (" + ID
            + " integer primary key autoincrement, "
            + KEY_TRIP_ID + " text null, "
            + KEY_DISTANCE + " text null, "
            + KEY_ADDED_ON + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ");";

    private static final String CREATE_TBL_ALL_MSG = "create table "
            + TBL_ALLMSG + " (" + ID
            + " integer primary key autoincrement, "
            + KEY_DESCRIPTION + " text null, "
            + KEY_ADDED_ON + " text null"
            + ");";


    private static final String CREATE_TBL_LOCATION = "create table "
            + TBL_LOCATION + " (" + ID
            + " integer primary key autoincrement, "
            + KEY_TRIP_ID + " text null, "
            + KEY_CURRENTLATITUDE + " text null, "
            + KEY_CURRENTLOGITUDE + " text null, "
            + KEY_ADDED_ON + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ");";

    private static final String CREATE_TBL_USER = "create table "
            + TBL_USER + " (" + ID
            + " integer primary key autoincrement, "
            + KEY_USER_ID + " text null, "
            + KEY_NAME + " text null, "
            + KEY_EMAIL + " text null, "
            + KEY_MOBILE + " text null, "
            + KEY_IMAGE + " text null, "
            + KEY_ADDED_ON + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ");";

    private static final String CREATE_TBL_TRIP = "create table "
            + TBL_TRIP + " (" + ID
            + " integer primary key, "
            + KEY_TRIP_ID + " integer null, "
            + KEY_DISTANCE + " text null, "
            + KEY_AGREEMENT + " text null, "
            + KEY_FARE + " text null, "
            + KEY_DATE + " text null,"
            + KEY_CUSTOMER_NAME + " text null,"
            + KEY_PHONE + " text null,"
            + KEY_SOURCEADDRESS + " text null,"
            + KEY_DESTINATIONADDRESS + " text null,"
            + KEY_WHERETOCOMETRIP + " text null,"
            + KEY_SOURCELATITUDE + " text null,"
            + KEY_SOURCELOGITUDE + " text null,"
            + KEY_DESTINATIONLATITUDE + " text null,"
            + KEY_DESTINATIONLOGITUDE + " text null,"
            + KEY_TRIPTYPE + " text null,"
            + KEY_TRAVELTIME + " text null,"
            + KEY_CORPORATETYPE + " text null,"
            + KEY_ADDED_ON + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ");";

    private static final String CREATE_TBL_TEMP_TRIP = "create table "
            + TBL_TEMP_TRIP + " (" + ID
            + " integer primary key, "
            + KEY_TRIP_ID + " integer null, "
            + KEY_DISTANCE + " text null, "
            + KEY_DATE + " DATETIME null,"
            + KEY_CUSTOMER_NAME + " text null,"
            + KEY_PHONE + " text null,"
            + KEY_SOURCEADDRESS + " text null,"
            + KEY_SOURCELATITUDE + " text null,"
            + KEY_SOURCELOGITUDE + " text null,"
            + KEY_DESTINATIONLATITUDE + " text null,"
            + KEY_DESTINATIONLOGITUDE + " text null,"
            + KEY_DESTINATIONADDRESS + " text null,"
            + KEY_FARE + " text null,"
            + KEY_ADDED_ON + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ");";


    private static final String CREATE_TBL_OFFINE = "create table "
            + TBL_OFFLINE + " (" + ID
            + " integer primary key, "
            + KEY_METHOD + " text null,"

            + KEY_ADDED_ON + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ");";


    private static final String CREATE_TBL_SNOOZER = "create table "
            + TBL_SNOOZER + " (" + ID
            + " integer primary key, "
            + KEY_TRIP_ID + " integer null, "
            + KEY_DISTANCE + " text null, "
            + KEY_DATE + " DATETIME null, "
            + KEY_CUSTOMER_NAME + " text null, "
            + KEY_PHONE + " text null, "
            + KEY_SOURCEADDRESS + " text null, "
            + KEY_SOURCELATITUDE + " text null, "
            + KEY_SOURCELOGITUDE + " text null, "
            + KEY_DESTINATIONLATITUDE + " text null, "
            + KEY_DESTINATIONLOGITUDE + " text null, "
            + KEY_DESTINATIONADDRESS + " text null, "
            + KEY_FARE + " text null, "
            + KEY_ADDED_ON + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ");";

    private static final String CREATE_TBL_TRIP_HISTORY = "create table "
            + TBL_TRIP_HISTORY + " (" + ID
            + " integer primary key, "
            + KEY_TRIP_ID + " integer null, "
            + KEY_DATE + " DATETIME null,"
            + KEY_TRIPTYPE + " text null,"
            + KEY_HISTORY_MONTH + " text null,"
            + KEY_CUSTOMER_NAME + " text null,"
            + KEY_CUSTOMER_RATING + " text null,"
            + KEY_CUSTOMER_IMAGE + " text null,"
            + KEY_SOURCEADDRESS + " text null,"
            + KEY_DESTINATIONADDRESS + " text null,"
            + KEY_SOURCELATITUDE + " text null,"
            + KEY_SOURCELOGITUDE + " text null,"
            + KEY_DESTINATIONLATITUDE + " text null,"
            + KEY_DESTINATIONLOGITUDE + " text null,"
            + KEY_ADDED_ON + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ");";

    private static final String CREATE_TBL_NOTIFICATION = "create table "
            + TBL_NOTIFICATION + " (" + ID
            + " integer primary key autoincrement, "
            + KEY_TRIP_ID + " integer null, "
            + KEY_NOTIFY_HEADING + " text null, "
            + KEY_DESCRIPTION + " text null, "
            + KEY_ADDED_ON + " text null"
            + ");";

    private final String CREATE_DISTANCE_QUERY = "CREATE TABLE IF NOT EXISTS " + TABLE_DISTANCE + " ( " +
            KEY_ID + " INTEGER PRIMARY KEY, " +
            KEY_DRIVER_STATUS + " TEXT, " +
            KEY_BOOKING_ID + " TEXT, " +
            KEY_DISTANCE_TOTAL + " REAL, " +
            KEY_DISTANCE_FREE + " REAL, " +
            KEY_DISTANCE_TO_CUSTOMER + " REAL, " +
            KEY_DISTANCE_TRIP + " REAL )";

    private final String CREATE_LOGS_QUERY = "CREATE TABLE IF NOT EXISTS " + TABLE_LOGS + " ( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_LOG_TAG + " TEXT, " +
            KEY_LOG + " TEXT)";

    public DBAdapter_Driver(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        Logger.log(TAG, "onCreate");
        db.execSQL(CREATE_TBL_USER);
        db.execSQL(CREATE_TBL_TRIP);
        db.execSQL(CREATE_TBL_TEMP_TRIP);
        db.execSQL(CREATE_TBL_SNOOZER);
        db.execSQL(CREATE_TBL_LOCATION);
        db.execSQL(CREATE_TBL_NOTIFICATION);
        db.execSQL(CREATE_TBL_TRIP_HISTORY);
        db.execSQL(CREATE_TBL_TRIP_DELETED);
        db.execSQL(CREATE_TBL_OFFINE);
        db.execSQL(CREATE_TBL_ALL_MSG);
        db.execSQL(CREATE_TBL_DISTANCE);
        db.execSQL(CREATE_DISTANCE_QUERY);
        db.execSQL(CREATE_LOGS_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        String query0 = "DROP TABLE IF EXISTS " + TBL_USER;
        String query1 = "DROP TABLE IF EXISTS " + TBL_TRIP;
        String query2 = "DROP TABLE IF EXISTS " + TBL_LOCATION;
        String query3 = "DROP TABLE IF EXISTS " + TBL_NOTIFICATION;
        String query4 = "DROP TABLE IF EXISTS " + TBL_TRIP_HISTORY;
        String query5 = "DROP TABLE IF EXISTS " + TBL_TEMP_TRIP;
        String query6 = "DROP TABLE IF EXISTS " + TBL_TRIP_DELETED;
        String query7 = "DROP TABLE IF EXISTS " + TBL_SNOOZER;
        String query8 = "DROP TABLE IF EXISTS " + TBL_OFFLINE;
        String query9 = "DROP TABLE IF EXISTS " + TBL_ALLMSG;
        String query10 = "DROP TABLE IF EXISTS " + TBL_DISTANCE;
        String query11 = "DROP TABLE IF EXISTS " + TABLE_DISTANCE;
        String query12 = "DROP TABLE IF EXISTS " + TABLE_LOGS;

        db.execSQL(query0);
        db.execSQL(query1);
        db.execSQL(query2);
        db.execSQL(query3);
        db.execSQL(query4);
        db.execSQL(query5);
        db.execSQL(query6);
        db.execSQL(query7);
        db.execSQL(query8);
        db.execSQL(query9);
        db.execSQL(query10);
        db.execSQL(query11);
        db.execSQL(query12);

        onCreate(db);
    }

    // Insert Data
    // TODO Auto-generated method stub

    public long insertDriver(UserDriver user) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_USER_ID, user.getId());
        initialValues.put(KEY_NAME, user.getName());
        initialValues.put(KEY_EMAIL, user.getEmailId());
        initialValues.put(KEY_MOBILE, user.getPhone());
        initialValues.put(KEY_IMAGE, user.getUserImage());

        long id = db.insert(TBL_USER, null, initialValues);

        Logger.log("UpdateUser In", ":::"+id);
        db.close();
        return id;
    }
    public long updateDriver(UserDriver user) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, user.getName());
        initialValues.put(KEY_IMAGE, user.getUserImage());

        long id = db.update(TBL_USER, initialValues, KEY_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        Logger.log("UpdateUser up", ":::"+id);
        db.close();
        return id;
    }

    public UserDriver getUser() {

        UserDriver user = new UserDriver();
        try {
            String query = "select * from " + TBL_USER + ";";
            System.out.println("query : " + query);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {
                return user;
            } else if (cursor.getCount() == 0) {
                return user;
            }
            if (cursor.moveToFirst()) {

                do {
                    System.out.println("UpdateUser : " + Long.valueOf(cursor.getInt(cursor.getColumnIndex(ID))));
                  //  user.setId(Long.valueOf(cursor.getInt(cursor.getColumnIndex(ID))));
                    user.setId(Long.valueOf(cursor.getString(cursor
                            .getColumnIndex(KEY_USER_ID))));
                    user.setName(cursor.getString(cursor
                            .getColumnIndex(KEY_NAME)));
                    user.setEmailId(cursor.getString(cursor
                            .getColumnIndex(KEY_EMAIL)));
                    user.setPhone(cursor.getString(cursor
                            .getColumnIndex(KEY_MOBILE)));
                    user.setUserImage(cursor.getString(cursor
                            .getColumnIndex(KEY_IMAGE)));
                    user.setAddedOn(cursor.getString(cursor
                            .getColumnIndex(KEY_ADDED_ON)));

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;

    }
    public long insertNotification(NotificationDriver notification) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TRIP_ID, notification.getId());
        initialValues.put(KEY_NOTIFY_HEADING, notification.getHeader());
        initialValues.put(KEY_DESCRIPTION, notification.getDescription());
        initialValues.put(KEY_ADDED_ON,notification.getTime());

        long id = db.insert(TBL_NOTIFICATION, null, initialValues);

        db.close();
    return id;
}

    public long insertDistance(String tripId, String distance) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TRIP_ID, tripId);
        initialValues.put(KEY_DISTANCE, distance);

        long id = db.insert(TBL_DISTANCE, null, initialValues);

        db.close();
    return id;
}

    public long insertAllMessage(AllMessageDriver allMessage) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_DESCRIPTION, allMessage.getText());
        initialValues.put(KEY_ADDED_ON, allMessage.getDate());

        long id = db.insert(TBL_ALLMSG, null, initialValues);

        db.close();
        return id;
    }

    public long insertLocation(UserDriver user) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TRIP_ID, user.getId());
        initialValues.put(KEY_CURRENTLATITUDE, user.getCurrentLatitude());
        initialValues.put(KEY_CURRENTLOGITUDE, user.getCurrentLongitude());

        long id = db.insert(TBL_LOCATION, null, initialValues);

        db.close();
        return id;
    }


    public long insertTempTrip(TripDriver trip) {
        long id = 0;
        SQLiteDatabase db = this.getWritableDatabase();

        Long trips = getTempTripId(Integer.parseInt(String.valueOf(trip.getId())));


        if(Integer.parseInt(String.valueOf(trips)) == Integer.parseInt(String.valueOf(trip.getId()))){

        } else {

            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_TRIP_ID, trip.getId());
            initialValues.put(KEY_DISTANCE, trip.getDistance());
            initialValues.put(KEY_SOURCEADDRESS, trip.getSourceAddress());
            initialValues.put(KEY_SOURCELATITUDE, trip.getSourceLatitude());
            initialValues.put(KEY_SOURCELOGITUDE, trip.getSourcelogitude());
            initialValues.put(KEY_DESTINATIONLATITUDE, trip.getDestinationLatitude());
            initialValues.put(KEY_DESTINATIONLOGITUDE, trip.getDestinationLogitude());
            initialValues.put(KEY_DESTINATIONADDRESS, trip.getDestinationAddress());
            initialValues.put(KEY_FARE, trip.getFare());
            initialValues.put(KEY_CUSTOMER_NAME, trip.getPickCname());
            initialValues.put(KEY_PHONE, trip.getPickCno());
            initialValues.put(KEY_DATE, trip.getDate());

            id = db.insert(TBL_TEMP_TRIP, null, initialValues);


        }
        db.close();
        return id;
    }



    public long insertOfflineTrip(String trip) {
        long id = 0;
        SQLiteDatabase db = this.getWritableDatabase();

            ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_METHOD, trip);

            id = db.insert(TBL_OFFLINE, null, initialValues);

        db.close();
        return id;
    }








    public ArrayList<UserDriver> getLocation() {

        ArrayList<UserDriver> userList = new ArrayList<UserDriver>();
        try {
            String query = "select * from " + TBL_LOCATION + ";";
            System.out.println("query : " + query);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {
                return userList;
            } else if (cursor.getCount() == 0) {
                return userList;
            }
            if (cursor.moveToFirst()) {

                do {
                    UserDriver user = new UserDriver();
                    user.setId(cursor.getLong(cursor.getColumnIndex(KEY_TRIP_ID)));
                    user.setCurrentLatitude(cursor.getDouble(cursor
                            .getColumnIndex(KEY_CURRENTLATITUDE)));
                    user.setCurrentLongitude(cursor.getDouble(cursor
                            .getColumnIndex(KEY_CURRENTLOGITUDE)));

                    userList.add(user);
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;

    }



    public List<NotificationDriver> getAllNotification() {

        List<NotificationDriver> notificationList = new ArrayList<NotificationDriver>();
        try {
            String query = "select * from " + TBL_NOTIFICATION + " ORDER BY "+ID+" DESC;";
            System.out.println("query : " + query);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {
                return notificationList;
            } else if (cursor.getCount() == 0) {
                return notificationList;
            }
            if (cursor.moveToFirst()) {

                do {
                    NotificationDriver notification = new NotificationDriver();
                    notification.setHeader(cursor.getString(cursor
                            .getColumnIndex(KEY_NOTIFY_HEADING)));
                    notification.setDescription(cursor.getString(cursor
                            .getColumnIndex(KEY_DESCRIPTION)));
                    notification.setTime(cursor.getString(cursor.getColumnIndex(KEY_ADDED_ON)));



                    notificationList.add(notification);
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notificationList;

    }



    public ArrayList<String> getOfflineTrip() {

        ArrayList<String> tripList = new ArrayList<String>();
        try {
            String query = "select * from " + TBL_OFFLINE + " ;";
            System.out.println("query : " + query);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {
                return tripList;
            } else if (cursor.getCount() == 0) {
                return tripList;
            }
            if (cursor.moveToFirst()) {

                do {
                    tripList.add(cursor.getString(cursor.getColumnIndex(KEY_METHOD)));

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tripList;

    }



    public Long getTempTripId(int id) {

        long tripId = 0l;
        try {
            String query = "select * from " + TBL_TEMP_TRIP +" where "+KEY_TRIP_ID+" = "+id+";";
            System.out.println("query : " + query);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {
                return tripId;
            } else if (cursor.getCount() == 0) {
                return tripId;
            }
            if (cursor.moveToFirst()) {

                do {

                    tripId = (cursor.getLong(cursor.getColumnIndex(KEY_TRIP_ID)));
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tripId;

    }

    public List<AllMessageDriver> getAllMessage() {

        List<AllMessageDriver> messageList = new ArrayList<AllMessageDriver>();
        try {
            String query = "select * from " + TBL_ALLMSG +";";
            System.out.println("query : " + query);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {
                return messageList;
            } else if (cursor.getCount() == 0) {
                return messageList;
            }
            if (cursor.moveToFirst()) {

                do {
                    AllMessageDriver message = new AllMessageDriver();
                    message.setText(cursor.getString(cursor
                            .getColumnIndex(KEY_DESCRIPTION)));
                    message.setDate(cursor.getString(cursor.getColumnIndex(KEY_ADDED_ON)));



                    messageList.add(message);
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messageList;
    }

    public boolean setDistance(DistanceTravelled distanceTravelled) {
        DistanceTravelled oldDistanceObj = getDistance();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, distanceTravelled.getId());
        values.put(KEY_DRIVER_STATUS, distanceTravelled.getDriverStatus());
        values.put(KEY_BOOKING_ID, distanceTravelled.getBookingID());
        values.put(KEY_DISTANCE_TOTAL, distanceTravelled.getDistanceTotal());
        values.put(KEY_DISTANCE_FREE, distanceTravelled.getDistanceFree());
        values.put(KEY_DISTANCE_TO_CUSTOMER, distanceTravelled.getDistanceToCustomer());
        values.put(KEY_DISTANCE_TRIP, distanceTravelled.getDistanceTrip());



        if (oldDistanceObj != null) {
            // Update distance in database if distance is already present with certain status....
            int rows = db.update(TABLE_DISTANCE, values, KEY_ID + " = ?",
                    new String[]{String.valueOf(distanceTravelled.getId())});
            //sendDataMeFromFused.add("Database action update: " + rows +" >>> dist :  "+ oldDistanceObj.getDistanceTrip());
            db.close();
            return true;
        } else {
            // Now inserting new driver details in the database....
            if (db.insert(TABLE_DISTANCE, null, values) != -1) {
                db.close();
                //sendDataMeFromFused.add("Database action insert: " );
                return true;
            }else{
              //  sendDataMeFromFused.add("Database action error: " );
            }
            db.close();
            return false;
        }
    }

    public DistanceTravelled getDistance() {
        String selectQuery = "SELECT * FROM " + TABLE_DISTANCE + " WHERE " + KEY_ID + " = " + 1 + ";";
        DistanceTravelled distanceTravelled = new DistanceTravelled();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor == null) {
            db.close();
            return null;
        } else if (cursor.getCount() == 0) {
            Log.e("DATABASE", "Distance cursor size : " + cursor.getCount() + "");

            if (!cursor.isClosed()) {
                cursor.close();
            }
            db.close();
            return null;
        }
        //sendDataMeFromFused.add("Database action datasize: "+cursor.getCount() );
        while (cursor.moveToNext()) {
            distanceTravelled.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            distanceTravelled.setBookingID(cursor.getString(cursor.getColumnIndex(KEY_BOOKING_ID)));
            distanceTravelled.setDistanceTotal(cursor.getDouble(cursor.getColumnIndex(KEY_DISTANCE_TOTAL)));
            distanceTravelled.setDistanceFree(cursor.getDouble(cursor.getColumnIndex(KEY_DISTANCE_FREE)));
            distanceTravelled.setDistanceToCustomer(cursor.getDouble(cursor.getColumnIndex(KEY_DISTANCE_TO_CUSTOMER)));
            distanceTravelled.setDistanceTrip(cursor.getDouble(cursor.getColumnIndex(KEY_DISTANCE_TRIP)));

          //  sendDataMeFromFused.add("Database action dataDis: "+cursor.getDouble(cursor.getColumnIndex(KEY_DISTANCE_TRIP)));
        }

       /* if (cursor.moveToFirst()) {
            distanceTravelled.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            distanceTravelled.setBookingID(cursor.getString(cursor.getColumnIndex(KEY_BOOKING_ID)));
            distanceTravelled.setDistanceTotal(cursor.getDouble(cursor.getColumnIndex(KEY_DISTANCE_TOTAL)));
            distanceTravelled.setDistanceFree(cursor.getDouble(cursor.getColumnIndex(KEY_DISTANCE_FREE)));
            distanceTravelled.setDistanceToCustomer(cursor.getDouble(cursor.getColumnIndex(KEY_DISTANCE_TO_CUSTOMER)));
            distanceTravelled.setDistanceTrip(cursor.getDouble(cursor.getColumnIndex(KEY_DISTANCE_TRIP)));
        }*/

        if (!cursor.isClosed()) {
            cursor.close();
        }
        db.close();
        return distanceTravelled;
    }

    public DistanceTravelled getDistance(String status) {
        String selectQuery = "SELECT * FROM " + TABLE_DISTANCE + " WHERE " + KEY_ID + " = " + 1 + " and "+ KEY_DRIVER_STATUS + " = '"+ status+"';";
        DistanceTravelled distanceTravelled = new DistanceTravelled();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor == null) {
            db.close();
            return null;
        } else if (cursor.getCount() == 0) {
            Log.e("DATABASE", "Distance cursor size : " + cursor.getCount() + "");
            if (!cursor.isClosed()) {
                cursor.close();
            }
            db.close();
            return null;
        }

        if (cursor.moveToFirst()) {
            distanceTravelled.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            distanceTravelled.setBookingID(cursor.getString(cursor.getColumnIndex(KEY_BOOKING_ID)));
            distanceTravelled.setDistanceTotal(cursor.getDouble(cursor.getColumnIndex(KEY_DISTANCE_TOTAL)));
            distanceTravelled.setDistanceFree(cursor.getDouble(cursor.getColumnIndex(KEY_DISTANCE_FREE)));
            distanceTravelled.setDistanceToCustomer(cursor.getDouble(cursor.getColumnIndex(KEY_DISTANCE_TO_CUSTOMER)));
            distanceTravelled.setDistanceTrip(cursor.getDouble(cursor.getColumnIndex(KEY_DISTANCE_TRIP)));
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }
        db.close();
        return distanceTravelled;
    }

    public boolean insertLogs(String tag, String log) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LOG_TAG, tag);
        values.put(KEY_LOG, log);

        // Now inserting new driver details in the database....
        if (db.insert(TABLE_LOGS, null, values) != -1) {
            db.close();
            return true;
        }
        db.close();
        return false;
    }


    public String getLogs() {
        String selectQuery = "SELECT * FROM " + TABLE_LOGS + " ;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor == null) {
            db.close();
            return null;
        } else if (cursor.getCount() == 0) {
            if (!cursor.isClosed()) {
                cursor.close();
            }
            db.close();
            return null;
        }
        Log.e("DATABASE", "Logs cursor size : " + cursor.getCount() + "");

        StringBuilder sb = new StringBuilder();
        sb.append("<h4>B4E logs... <h4>");
        sb.append("\n");
        sb.append("\n");
        sb.append("<table border=\"1\" bordercolor=\"yellow\" bgcolor=\"blue\">");

        while (cursor.moveToNext()) {

            sb.append("<tr>")
                    .append("<td><strong>")
                    .append(cursor.getString(cursor.getColumnIndex(KEY_ID)) + "\t" + cursor.getString(cursor.getColumnIndex(KEY_LOG_TAG)))
                    .append("</strong></td>");
            sb.append("</tr>");
            sb.append(cursor.getString(cursor.getColumnIndex(KEY_LOG)));
            sb.append("<br/>");
            sb.append("<br/>");

        }
        sb.append("</table>");

        if (!cursor.isClosed()) {
            cursor.close();
        }
        db.close();
        return sb.toString();
    }


    public int deleteLogs() {
        //String deleteQuery = "DELETE FROM " + TABLE_BOOKING_EVENT + " WHERE " + KEY_REQUEST_ID + " = " + bookingEvent.getRequestID() + " AND " + KEY_BOOKING_ID + " = '" + bookingEvent.getBookingID() + "' ;";
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = 0;
        try {
            //db.execSQL(deleteQuery);
            rows = db.delete(TABLE_LOGS, null, null);
        } catch (Exception e) {
        }
        db.close();
        return rows;
    }





    public boolean deleteLocation() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TBL_LOCATION, null, null) > 0;
    }

    public boolean deleteTrip(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TBL_TRIP, KEY_TRIP_ID + " = " + id, null) > 0;
    }
    public boolean deleteTempTrip(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TBL_TEMP_TRIP, null, null) > 0;
    }
    public boolean deleteTripById(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TBL_TEMP_TRIP, KEY_TRIP_ID + " = " + id, null) > 0;
    }

    public boolean deleteDeletedTrip() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TBL_TRIP_DELETED, null, null) > 0;
    }

    public boolean deleteSnoozeTrip(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TBL_SNOOZER, KEY_TRIP_ID + " = " + id, null) > 0;
    }

    public boolean deleteOfflineTrip() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TBL_OFFLINE, null, null) > 0;
    }
    public boolean deleteAllMsg(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TBL_ALLMSG, null, null) > 0;
    }
    public boolean deleteDistance(){
        FusedLocationService.totalDistance = 0;
        FusedLocationService.freeDistance = 0;
        FusedLocationService.toCustomerDistance = 0;
        FusedLocationService.tripDistance = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_DISTANCE, null, null) > 0;
    }
    public boolean deleteUser(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TBL_USER, null, null) > 0;
    }

}