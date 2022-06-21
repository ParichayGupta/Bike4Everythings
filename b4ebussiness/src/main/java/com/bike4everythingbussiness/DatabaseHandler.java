package com.bike4everythingbussiness;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bike4everythingbussiness.Model.DropAddress;
import com.bike4everythingbussiness.Model.FareCard;
import com.bike4everythingbussiness.Model.OrderDetails;
import com.bike4everythingbussiness.Model.PickAddress;
import com.bike4everythingbussiness.Model.User;
import com.bike4everythingbussiness.Utils.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by manishsingh on 04/01/18.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "b2bbussiness.db";

    // Contacts table name
    private static final String TABLE_CONTACTS = "contacts";
    private static final String TABLE_PICKUP_ADDRESS = "table_pickup_address";
    private static final String TABLE_DROP_ADDRESS = "table_drop_address";
    private static final String TABLE_ORDER = "table_order";
    private static final String TABLE_FARECARD = "table_farecard";


    // Comman Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_USERID = "user_id";
    private static final String KEY_SELECT = "key_select";

    // Contacts Table Columns names
    private static final String KEY_NAME = "name";
    private static final String KEY_PH_NO = "phone_number";
    private static final String KEY_IMAGE = "key_image";
    private static final String KEY_EMAIL = "key_email";
    private static final String KEY_PASSWORD = "key_password";

    // Pickup address Table Columns names
    private static final String KEY_PICKUP_NAME = "key_pickup_name";
    private static final String KEY_PICKUP_MOBILE = "key_pickup_mobile";
    private static final String KEY_PICKUP_ADDRESS = "key_pickup_address";
    private static final String KEY_PICKUP_ADDRESS_NAME = "key_pickup_address_name";
    private static final String KEY_PICKUP_LAT = "key_pickup_lat";
    private static final String KEY_PICKUP_LNG = "key_pickup_lng";

    // Drop address Table Columns names
    private static final String KEY_DROP_NAME = "key_drop_name";
    private static final String KEY_DROP_MOBILE = "key_drop_mobile";
    private static final String KEY_DROP_ADDRESS = "key_drop_address";
    private static final String KEY_DROP_LAT = "key_drop_lat";
    private static final String KEY_DROP_LNG = "key_drop_lng";
    private static final String KEY_DROP_CASH = "key_drop_cash";

    // Order Table Columns names
    private static final String KEY_ORDERID= "key_orderid";
    private static final String KEY_ORDER_STATUS= "key_order_status";
    private static final String KEY_DELIVERY_STATUS= "key_delivery_status";
    private static final String KEY_DROP_ADDRESS_LIST= "key_drop_address_list";
    private static final String KEY_SCHEDULE = "key_schedule";
    private static final String KEY_NOTES = "key_notes";
    private static final String KEY_AMOUNT = "key_amount";
    private static final String KEY_DISTANCE = "key_distance";
    private static final String KEY_RETURN_REQURIED = "key_return_requried";
    private static final String KEY_ADDED_ON = "key_added_on";
    private static final String KEY_DRIVERNAME = "key_drivername";

    // Fare Card Table Columns names
    private static final String KEY_LIMIT_KM = "key_limit_km";
    private static final String KEY_BASE_FARE = "key_base_fare";
    private static final String KEY_PER_KM_FARE = "key_per_km_fare";
    private static final String KEY_GST = "key_gst";
    private static final String KEY_DELIVERIES = "key_deliveries";
    private static final String KEY_RETURN_FARE = "key_return_fare";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_USERID + " INTEGER,"
                + KEY_NAME + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_EMAIL + " TEXT,"
                + KEY_PASSWORD + " TEXT,"
                + KEY_PH_NO + " TEXT" + ")";

        String CREATE_PICKUP_ADDRESS_TABLE = "CREATE TABLE " + TABLE_PICKUP_ADDRESS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_USERID + " INTEGER,"
                + KEY_PICKUP_NAME + " TEXT,"
                + KEY_PICKUP_MOBILE + " TEXT,"
                + KEY_PICKUP_ADDRESS + " TEXT,"
                + KEY_PICKUP_ADDRESS_NAME + " TEXT,"
                + KEY_PICKUP_LAT + " REAL,"
                + KEY_PICKUP_LNG + " REAL,"
                + KEY_SELECT + " INTEGER" + ")";

        String CREATE_DROP_ADDRESS_TABLE = "CREATE TABLE " + TABLE_DROP_ADDRESS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_USERID + " TEXT,"
                + KEY_DROP_NAME + " TEXT,"
                + KEY_DROP_MOBILE + " TEXT,"
                + KEY_DROP_ADDRESS + " TEXT,"
                + KEY_DROP_LAT + " REAL,"
                + KEY_DROP_LNG + " REAL,"
                + KEY_DROP_CASH + " TEXT,"
                + KEY_SELECT + " INTEGER" + ")";

        String CREATE_ORDER_TABLE = "CREATE TABLE " + TABLE_ORDER + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_ORDERID + " TEXT,"
                + KEY_ORDER_STATUS + " INTEGER,"
                + KEY_DELIVERY_STATUS + " TEXT,"
                + KEY_USERID + " TEXT,"
                + KEY_PICKUP_NAME + " TEXT,"
                + KEY_PICKUP_MOBILE + " TEXT,"
                + KEY_PICKUP_ADDRESS + " TEXT,"
                + KEY_PICKUP_ADDRESS_NAME + " TEXT,"
                + KEY_PICKUP_LAT + " REAL,"
                + KEY_PICKUP_LNG + " REAL,"
                + KEY_DROP_ADDRESS_LIST + " TEXT,"
                + KEY_SCHEDULE + " TEXT,"
                + KEY_NOTES + " TEXT,"
                + KEY_AMOUNT + " TEXT,"
                + KEY_DISTANCE + " TEXT,"
                + KEY_RETURN_REQURIED + " TEXT,"
                + KEY_DRIVERNAME + " TEXT,"
                + KEY_SELECT + " INTEGER,"
                + KEY_ADDED_ON + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";


        String CREATE_FARE_CARD = "CREATE TABLE " + TABLE_FARECARD + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_LIMIT_KM + " TEXT,"
                + KEY_BASE_FARE + " TEXT,"
                + KEY_PER_KM_FARE + " TEXT,"
                + KEY_GST + " TEXT,"
                + KEY_DELIVERIES + " TEXT,"
                + KEY_RETURN_FARE + " TEXT,"
                + KEY_ADDED_ON + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";

        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_PICKUP_ADDRESS_TABLE);
        db.execSQL(CREATE_DROP_ADDRESS_TABLE);
        db.execSQL(CREATE_ORDER_TABLE);
        db.execSQL(CREATE_FARE_CARD);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PICKUP_ADDRESS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DROP_ADDRESS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FARECARD);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    public void addContact(User contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERID, contact.getId());
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PH_NO, contact.getPhone_number());
        values.put(KEY_IMAGE, contact.getProfile_image());
        values.put(KEY_EMAIL, contact.getEmail());
        values.put(KEY_PASSWORD, contact.getPassword());

        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
    }

    // Adding new Pickup Address
    public void addPickupAddress(PickAddress pickAddress) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERID, pickAddress.getId());
        values.put(KEY_PICKUP_NAME, pickAddress.getPickName());
        values.put(KEY_PICKUP_MOBILE, pickAddress.getPickMobile());
        values.put(KEY_PICKUP_ADDRESS, pickAddress.getPickAddress());
        values.put(KEY_PICKUP_ADDRESS_NAME, pickAddress.getPickAddressName());
        values.put(KEY_PICKUP_LAT, pickAddress.getPickLat());
        values.put(KEY_PICKUP_LNG, pickAddress.getPickLng());
        values.put(KEY_SELECT, pickAddress.isSelect()? 1 : 0);

        // Inserting Row
        db.insert(TABLE_PICKUP_ADDRESS, null, values);
        db.close(); // Closing database connection
    }

    // Adding new Drop Address
    public void addDropAddress(DropAddress dropAddress) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERID, dropAddress.getId());
        values.put(KEY_DROP_NAME, dropAddress.getDropName());
        values.put(KEY_DROP_MOBILE, dropAddress.getDropMobile());
        values.put(KEY_DROP_ADDRESS, dropAddress.getDropAddress());
        values.put(KEY_DROP_LAT, dropAddress.getDropLat());
        values.put(KEY_DROP_LNG, dropAddress.getDropLng());
        values.put(KEY_DROP_CASH, dropAddress.getDropAmount());
        values.put(KEY_SELECT, dropAddress.isSelect()? 1 : 0);

        // Inserting Row
        db.insert(TABLE_DROP_ADDRESS, null, values);
        db.close(); // Closing database connection
    }

    // Adding new Drop Address
    public void addOrder(OrderDetails orderDetails) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ORDERID, orderDetails.getDeliveryId());
        values.put(KEY_ORDER_STATUS, orderDetails.getOrderStatus());
        values.put(KEY_DELIVERY_STATUS, orderDetails.getDeliveryStatus());
        values.put(KEY_USERID, orderDetails.getUserId());
        values.put(KEY_PICKUP_NAME, orderDetails.getPickName());
        values.put(KEY_PICKUP_MOBILE, orderDetails.getPickMobile());
        values.put(KEY_PICKUP_ADDRESS, orderDetails.getPickAddress());
        values.put(KEY_PICKUP_ADDRESS_NAME, orderDetails.getPickAddressName());
        values.put(KEY_PICKUP_LAT, orderDetails.getPickLat());
        values.put(KEY_PICKUP_LNG, orderDetails.getPickLng());
        values.put(KEY_DROP_ADDRESS_LIST, new Gson().toJson(orderDetails.getDropAddressList()));
        values.put(KEY_SCHEDULE, orderDetails.getSchedule());
        values.put(KEY_NOTES, orderDetails.getNote());
        values.put(KEY_AMOUNT, orderDetails.getAmount());
        values.put(KEY_DISTANCE, orderDetails.getDistance());
        values.put(KEY_ADDED_ON, orderDetails.getAddedOn());
        values.put(KEY_DRIVERNAME, orderDetails.getDriverName());
        values.put(KEY_RETURN_REQURIED, orderDetails.isReturnrequired()? 1 : 0);
        values.put(KEY_SELECT, orderDetails.isUpdateOnServer()? 1 : 0);

        // Inserting Row
        db.insert(TABLE_ORDER, null, values);
        db.close(); // Closing database connection
    }

    // Adding Fare Card
    public void addFareCard(FareCard fareCard) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LIMIT_KM, fareCard.getLimitKm());
        values.put(KEY_BASE_FARE, fareCard.getBaseFare());
        values.put(KEY_PER_KM_FARE, fareCard.getPerKmFare());
        values.put(KEY_GST, fareCard.getGst());
        values.put(KEY_DELIVERIES, Arrays.toString(fareCard.getDeliveries().toArray()));
        values.put(KEY_RETURN_FARE, fareCard.getReturnFare());

        if( getFareCardCount() == 0){
            db.insert(TABLE_FARECARD, null, values);
        }else{
            db.update(TABLE_FARECARD, values, null,null);
        }


        db.close(); // Closing database connection
    }

    // Getting single contact
    public User getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        User contact = new User();
        Cursor cursor = db.query(TABLE_CONTACTS, new String[] {
                KEY_USERID,
                        KEY_NAME, KEY_PH_NO,KEY_IMAGE, KEY_EMAIL, KEY_PASSWORD }, KEY_USERID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        if (cursor.moveToFirst()) {
            do {

                contact.setId(cursor.getInt(cursor.getColumnIndex(KEY_USERID)));
                contact.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                contact.setPhone_number(cursor.getString(cursor.getColumnIndex(KEY_PH_NO)));
                contact.setProfile_image(cursor.getString(cursor.getColumnIndex(KEY_IMAGE)));
                contact.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
                contact.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));

            } while (cursor.moveToNext());
        }
        db.close();
        return contact;
    }

    // Getting Fare Card
    public FareCard getFareCard() {
        SQLiteDatabase db = this.getReadableDatabase();
        FareCard fareCard = new FareCard();
        Cursor cursor = db.query(TABLE_FARECARD, null, null,
                null, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        if (cursor.moveToFirst()) {
            do {

                fareCard.setLimitKm(cursor.getString(cursor.getColumnIndex(KEY_LIMIT_KM)));
                fareCard.setBaseFare(cursor.getString(cursor.getColumnIndex(KEY_BASE_FARE)));
                fareCard.setPerKmFare(cursor.getString(cursor.getColumnIndex(KEY_PER_KM_FARE)));
                fareCard.setGst(cursor.getString(cursor.getColumnIndex(KEY_GST)));

                JSONArray array = null;
                try {
                    array = new JSONArray(cursor.getString(cursor.getColumnIndex(KEY_DELIVERIES)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Type listType = new TypeToken<ArrayList<FareCard.Delivery>>(){}.getType();
                List<FareCard.Delivery> deliveryList = new Gson().fromJson(array.toString(), listType);

                fareCard.setDeliveries(deliveryList);


                fareCard.setReturnFare(cursor.getString(cursor.getColumnIndex(KEY_RETURN_FARE)));

            } while (cursor.moveToNext());
        }
        db.close();
        return fareCard;
    }

    // Getting All Contacts
    public List<User> getAllContacts() {
        List<User> contactList = new ArrayList<User>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User contact = new User();
                contact.setId(cursor.getInt(cursor.getColumnIndex(KEY_USERID)));
                contact.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                contact.setPhone_number(cursor.getString(cursor.getColumnIndex(KEY_PH_NO)));
                contact.setProfile_image(cursor.getString(cursor.getColumnIndex(KEY_IMAGE)));
                contact.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
                contact.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        db.close();
        // return contact list
        return contactList;
    }
    // Getting Pickup Id
    public boolean getPickupId(int id) {

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PICKUP_ADDRESS +" WHERE " + KEY_USERID + " = "+ id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        boolean isIdExits = false;

        if(cursor.isFirst()){
            isIdExits = true;
        }

        db.close();
        // return contact list
        return isIdExits;
    }
    // Getting All Pickup Address
    public List<PickAddress> getAllPickupAddress() {
        List<PickAddress> pickAddresses = new ArrayList<PickAddress>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PICKUP_ADDRESS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PickAddress pickAddress = new PickAddress();
                pickAddress.setId(cursor.getInt(cursor.getColumnIndex(KEY_USERID)));
                pickAddress.setPickName(cursor.getString(cursor.getColumnIndex(KEY_PICKUP_NAME)));
                pickAddress.setPickMobile(cursor.getString(cursor.getColumnIndex(KEY_PICKUP_MOBILE)));
                pickAddress.setPickAddress(cursor.getString(cursor.getColumnIndex(KEY_PICKUP_ADDRESS)));
                pickAddress.setPickAddressName(cursor.getString(cursor.getColumnIndex(KEY_PICKUP_ADDRESS_NAME)));
                pickAddress.setPickLat(cursor.getDouble(cursor.getColumnIndex(KEY_PICKUP_LAT)));
                pickAddress.setPickLng(cursor.getDouble(cursor.getColumnIndex(KEY_PICKUP_LNG)));
                pickAddress.setSelect(cursor.getInt(cursor.getColumnIndex(KEY_SELECT)) == 1 ? true : false);

                pickAddresses.add(pickAddress);
            } while (cursor.moveToNext());
        }
        db.close();
        // return contact list
        return pickAddresses;
    }
    // Getting All Pickup Address
    public PickAddress getSelectedPickupAddress(boolean isSelect) {
        PickAddress pickAddress = null;
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PICKUP_ADDRESS +" WHERE "+ KEY_SELECT +" = "+(isSelect?1:0)+";";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                 pickAddress = new PickAddress();
                pickAddress.setId(cursor.getInt(cursor.getColumnIndex(KEY_USERID)));
                pickAddress.setPickName(cursor.getString(cursor.getColumnIndex(KEY_PICKUP_NAME)));
                pickAddress.setPickMobile(cursor.getString(cursor.getColumnIndex(KEY_PICKUP_MOBILE)));
                pickAddress.setPickAddress(cursor.getString(cursor.getColumnIndex(KEY_PICKUP_ADDRESS)));
                pickAddress.setPickAddressName(cursor.getString(cursor.getColumnIndex(KEY_PICKUP_ADDRESS_NAME)));
                pickAddress.setPickLat(cursor.getDouble(cursor.getColumnIndex(KEY_PICKUP_LAT)));
                pickAddress.setPickLng(cursor.getDouble(cursor.getColumnIndex(KEY_PICKUP_LNG)));
                pickAddress.setSelect(cursor.getInt(cursor.getColumnIndex(KEY_SELECT)) == 1 ? true : false);

            } while (cursor.moveToNext());
        }
        db.close();
        // return contact list
        return pickAddress;
    }

    // Getting  Drop Address
    public DropAddress getDropAddress(String number) {
        DropAddress dropAddress = null;
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_DROP_ADDRESS +" WHERE "+ KEY_DROP_MOBILE +" = '"+number+"';";
        Logger.log("selectQuery", selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                dropAddress = new DropAddress();
                dropAddress.setId(cursor.getString(cursor.getColumnIndex(KEY_USERID)));
                dropAddress.setDropName(cursor.getString(cursor.getColumnIndex(KEY_DROP_NAME)));
                dropAddress.setDropMobile(cursor.getString(cursor.getColumnIndex(KEY_DROP_MOBILE)));
                dropAddress.setDropAddress(cursor.getString(cursor.getColumnIndex(KEY_DROP_ADDRESS)));
                dropAddress.setDropLat(cursor.getDouble(cursor.getColumnIndex(KEY_DROP_LAT)));
                dropAddress.setDropLng(cursor.getDouble(cursor.getColumnIndex(KEY_DROP_LNG)));
                dropAddress.setDropAmount(cursor.getString(cursor.getColumnIndex(KEY_DROP_CASH)));
                dropAddress.setSelect(cursor.getInt(cursor.getColumnIndex(KEY_SELECT)) == 1 ? true : false);


            } while (cursor.moveToNext());
        }
        db.close();
        // return contact list
        return dropAddress;
    }

    // Getting  Drop Address
    public List<DropAddress> getDropSelectedAddress(boolean isSelect) {
        List<DropAddress> dropAddressList = null;
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_DROP_ADDRESS +" WHERE "+ KEY_SELECT +" = "+(isSelect?1:0)+";";
        Logger.log("selectQuery", selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            dropAddressList = new ArrayList<>();
            do {
                DropAddress dropAddress = new DropAddress();
                dropAddress.setId(cursor.getString(cursor.getColumnIndex(KEY_USERID)));
                dropAddress.setDropName(cursor.getString(cursor.getColumnIndex(KEY_DROP_NAME)));
                dropAddress.setDropMobile(cursor.getString(cursor.getColumnIndex(KEY_DROP_MOBILE)));
                dropAddress.setDropAddress(cursor.getString(cursor.getColumnIndex(KEY_DROP_ADDRESS)));
                dropAddress.setDropLat(cursor.getDouble(cursor.getColumnIndex(KEY_DROP_LAT)));
                dropAddress.setDropLng(cursor.getDouble(cursor.getColumnIndex(KEY_DROP_LNG)));
                dropAddress.setDropAmount(cursor.getString(cursor.getColumnIndex(KEY_DROP_CASH)));
                dropAddress.setSelect(cursor.getInt(cursor.getColumnIndex(KEY_SELECT)) == 1 ? true : false);

                dropAddressList.add(dropAddress);
            } while (cursor.moveToNext());
        }
        db.close();
        // return contact list
        return dropAddressList;
    }



    // Getting  All Orders
    public List<OrderDetails> getOrdersByStatus(int status) {
        List<OrderDetails> orderDetailsList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_ORDER +" WHERE "+ KEY_ORDER_STATUS +" = "+status+";";
        Logger.log("selectQuery", selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {

            do {
                OrderDetails orderDetails = new OrderDetails();
                orderDetails.setDeliveryId(cursor.getString(cursor.getColumnIndex(KEY_ORDERID)));
                orderDetails.setOrderStatus(cursor.getInt(cursor.getColumnIndex(KEY_ORDER_STATUS)));
                orderDetails.setDeliveryStatus(cursor.getString(cursor.getColumnIndex(KEY_DELIVERY_STATUS)));
                orderDetails.setUserId(cursor.getString(cursor.getColumnIndex(KEY_USERID)));
                orderDetails.setPickName(cursor.getString(cursor.getColumnIndex(KEY_PICKUP_NAME)));
                orderDetails.setPickMobile(cursor.getString(cursor.getColumnIndex(KEY_PICKUP_MOBILE)));
                orderDetails.setPickAddress(cursor.getString(cursor.getColumnIndex(KEY_PICKUP_ADDRESS)));
                orderDetails.setPickAddressName(cursor.getString(cursor.getColumnIndex(KEY_PICKUP_ADDRESS_NAME)));
                orderDetails.setPickLat(cursor.getDouble(cursor.getColumnIndex(KEY_PICKUP_LAT)));
                orderDetails.setPickLng(cursor.getDouble(cursor.getColumnIndex(KEY_PICKUP_LNG)));
                Type type = new TypeToken<ArrayList<DropAddress>>() {}.getType();

                ArrayList<DropAddress>  dropAddressArrayList = new Gson().fromJson(cursor.getString(cursor.getColumnIndex(KEY_DROP_ADDRESS_LIST)), type);
                orderDetails.setDropAddressList(dropAddressArrayList);
                orderDetails.setSchedule(cursor.getString(cursor.getColumnIndex(KEY_SCHEDULE)));
                orderDetails.setAddedOn(cursor.getString(cursor.getColumnIndex(KEY_ADDED_ON)));
                orderDetails.setDriverName(cursor.getString(cursor.getColumnIndex(KEY_DRIVERNAME)));
                orderDetails.setNote(cursor.getString(cursor.getColumnIndex(KEY_NOTES)));
                orderDetails.setAmount(cursor.getString(cursor.getColumnIndex(KEY_AMOUNT)));
                orderDetails.setDistance(cursor.getString(cursor.getColumnIndex(KEY_DISTANCE)));
                orderDetails.setReturnrequired(cursor.getInt(cursor.getColumnIndex(KEY_RETURN_REQURIED)) == 1 ? true : false);
                orderDetails.setUpdateOnServer(cursor.getInt(cursor.getColumnIndex(KEY_SELECT)) == 1 ? true : false);

                orderDetailsList.add(orderDetails);
            } while (cursor.moveToNext());
        }
        db.close();
        // return contact list
        return orderDetailsList;
    }

    // Getting  All Orders Ids
    public boolean isExitsOrderId(String id) {
       boolean isExits;
        // Select All Query
        String selectQuery = "SELECT "+KEY_ORDERID+" FROM " + TABLE_ORDER +" WHERE "+ KEY_ORDERID +" = '"+id+"';";
        Logger.log("selectQuery", selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {

            isExits = true;
        }else {

            isExits = false;
        }
        db.close();
        // return contact list
        return isExits;
    }


    // Updating single contact
    public int updateContact(User contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PH_NO, contact.getPhone_number());
        values.put(KEY_IMAGE, contact.getProfile_image());
        values.put(KEY_EMAIL, contact.getEmail());
        values.put(KEY_PASSWORD, contact.getPassword());

        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_USERID + " = ?",
                new String[] { String.valueOf(contact.getId()) });
    }

    // Adding new Drop Address
    public int updateDropAddress(DropAddress dropAddress) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERID, dropAddress.getId());
        values.put(KEY_DROP_NAME, dropAddress.getDropName());
        values.put(KEY_DROP_MOBILE, dropAddress.getDropMobile());
        values.put(KEY_DROP_ADDRESS, dropAddress.getDropAddress());
        values.put(KEY_DROP_LAT, dropAddress.getDropLat());
        values.put(KEY_DROP_LNG, dropAddress.getDropLng());
        values.put(KEY_DROP_CASH, dropAddress.getDropAmount());
        values.put(KEY_SELECT, dropAddress.isSelect()? 1 : 0);

        // updating row
        return db.update(TABLE_DROP_ADDRESS, values, KEY_DROP_MOBILE + " = ?",
                new String[] { dropAddress.getDropMobile() });
    }

    // Adding new Drop Address
    public int updatePickupSelectStatus(String id, boolean isSelect) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values1 = new ContentValues();
        values1.put(KEY_SELECT, 0);
        db.update(TABLE_PICKUP_ADDRESS, values1, null,
                null);

        ContentValues values = new ContentValues();
        values.put(KEY_SELECT, isSelect ? 1 :0);

        // updating row
        return db.update(TABLE_PICKUP_ADDRESS, values, KEY_USERID + " = ?",
                new String[] { id });
    }
    // Adding new Drop Address
    public int updateDropSelectStatus(String number, boolean isSelect) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SELECT, isSelect ? 1 :0);

        // updating row
        return db.update(TABLE_DROP_ADDRESS, values, KEY_DROP_MOBILE + " = ?",
                new String[] { number });
    }
    // Adding new Drop Address
    public int updateAllDropStatus(boolean isSelect) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SELECT, isSelect ? 1 :0);

        // updating row
        return db.update(TABLE_DROP_ADDRESS, values, null,null);
    }

    // Adding new Drop Address
    public int updateOrderSelect(int orderid,String delivery_id, boolean isSelect) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SELECT, isSelect ? 1 :0);
        values.put(KEY_ORDERID, delivery_id);

        // updating row
        return db.update(TABLE_ORDER, values, KEY_ORDERID + " = ?",
                new String[] {String.valueOf(orderid)});
    }
    // Adding new Drop Address
    public int updateOrderStatus(String deleveryId, int status, String deleveryStatus) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ORDER_STATUS, status);
        values.put(KEY_DELIVERY_STATUS, deleveryStatus);

        // updating row
        return db.update(TABLE_ORDER, values, KEY_ORDERID + " = ?",
                new String[] {deleveryId});
    }



    // Deleting single contact
    public void deleteContact(User contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_USERID + " = ?",
                new String[] {String.valueOf(contact.getId())});
        db.close();
    }
    // Deleting single Pickup Address
    public void deletePickupAddress(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PICKUP_ADDRESS, KEY_USERID + " = ?",
                new String[] {String.valueOf(id)});
        db.close();
    }
    // Deleting single Pickup Address
    public void deleteAllPickupAddress() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PICKUP_ADDRESS, null, null);
        db.close();
    }
    // Deleting single Pickup Address
    public void deleteAllOrder() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ORDER, null, null);
        db.close();
    }
    // Deleting single Drop Address
    public void deleteDropAddress(String number) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DROP_ADDRESS, KEY_DROP_MOBILE + " = ?",
                new String[] {String.valueOf(number)});
        db.close();
    }


    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
    // Getting contacts Count
    public int getFareCardCount() {
        String countQuery = "SELECT  * FROM " + TABLE_FARECARD;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }
    // Getting contacts Count
    public int getDropSelectedCount(boolean isSelect) {
        String countQuery = "SELECT  * FROM " + TABLE_DROP_ADDRESS +" WHERE "+ KEY_SELECT +" = "+ (isSelect?1:0);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
       // cursor.close();

        // return count
        return cursor.getCount();
    }

}