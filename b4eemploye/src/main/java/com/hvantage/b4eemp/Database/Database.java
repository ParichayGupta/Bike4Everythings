package com.hvantage.b4eemp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.hvantage.b4eemp.model.BookingData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by manishsingh on 10/03/18.
 */

public class Database {

    private static final String DATABASE_NAME = "b4eemp_db";
    private static final int DATABASE_VERSION = 1;


    private static final String BOOKING_TABLE = "booking_table";

    private static final String _ID = "_id";
    private static final String IMAGE = "image";
    private static final String NAME = "name";
    private static final String MOBILE = "mobile";
    private static final String PICKUP_DATE = "pickup_date";
    private static final String PICKUP_TIME = "pickup_time";
    private static final String DROPOFF_DATE = "dropoff_date";
    private static final String DROPOFF_TIME = "dropoff_time";
    private static final String ADDEDON = "addedon";
    private static final String BOOKING_STATUS = "booking_status";


    private static Database dbInstance;
    private SQLiteDatabase database;
    private DbHelper dbHelper;

    private Database(Context context) {
        this.dbHelper = new DbHelper(context);
        database = this.dbHelper.getWritableDatabase();
        createAllTables(this.database);
    }


    private static void createAllTables(SQLiteDatabase database) {
        database.execSQL(" CREATE TABLE IF NOT EXISTS " + BOOKING_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                +_ID+" TEXT NOT NULL" +", "
                +NAME+" TEXT DEFAULT ''"+", "
                +MOBILE+" TEXT DEFAULT ''"+", "
                +IMAGE+" TEXT DEFAULT ''"+", "
                +PICKUP_DATE+" TEXT DEFAULT '', "
                +PICKUP_TIME+" TEXT DEFAULT '', "
                +DROPOFF_DATE+" TEXT DEFAULT '', "
                +DROPOFF_TIME+" TEXT DEFAULT ''"+", "
                +BOOKING_STATUS+" INTEGER DEFAULT 0"+", "
                +ADDEDON+" TEXT DEFAULT '');");
    }

    public static Database getInstance(Context context) {
        if (dbInstance == null) {
            dbInstance = new Database(context);
        } else if (!dbInstance.database.isOpen()) {
            dbInstance = null;
            dbInstance = new Database(context);
        }
        return dbInstance;
    }

    public void close() {
        this.database.close();
        this.dbHelper.close();
        System.gc();
    }

    public boolean addBookingData(BookingData bookingData) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(_ID, bookingData.getId());
        contentValues.put(NAME, bookingData.getName());
        contentValues.put(MOBILE, bookingData.getMobile());
        contentValues.put(IMAGE, bookingData.getAltCustomerImage());
        contentValues.put(PICKUP_DATE, bookingData.getPickupDate());
        contentValues.put(PICKUP_TIME, bookingData.getPickupTime());
        contentValues.put(DROPOFF_DATE, bookingData.getDropoffDate());
        contentValues.put(DROPOFF_TIME, bookingData.getDropoffTime());
        contentValues.put(ADDEDON, bookingData.getAddedon());
        contentValues.put(BOOKING_STATUS, bookingData.getBookingStatus());


        this.database.insert(BOOKING_TABLE, null, contentValues);
        return true;
    }
    public boolean updateBookingata(BookingData bookingData) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(_ID, bookingData.getId());
        contentValues.put(NAME, bookingData.getName());
        contentValues.put(MOBILE, bookingData.getMobile());
        contentValues.put(IMAGE, bookingData.getAltCustomerImage());
        contentValues.put(PICKUP_DATE, bookingData.getPickupDate());
        contentValues.put(PICKUP_TIME, bookingData.getPickupTime());
        contentValues.put(DROPOFF_DATE, bookingData.getDropoffDate());
        contentValues.put(DROPOFF_TIME, bookingData.getDropoffTime());
        contentValues.put(ADDEDON, bookingData.getAddedon());
        contentValues.put(BOOKING_STATUS, bookingData.getBookingStatus());

        this.database.update(BOOKING_TABLE, contentValues, _ID + " =? ", new String[]{bookingData.getId()});
        return true;
    }

    public List<BookingData> getAllBookingData(){
        List<BookingData> bookingDataList = new ArrayList<>();
        String[] columns = new String[]{_ID,IMAGE,PICKUP_DATE,PICKUP_TIME,NAME,MOBILE,
                DROPOFF_DATE,DROPOFF_TIME,ADDEDON, BOOKING_STATUS};

        //Cursor cursor = this.database.query(BOOKING_TABLE, columns, null, null, null, null, String.valueOf(new Random().nextInt(10000)));
        Cursor cursor = this.database.rawQuery("SELECT * FROM "+BOOKING_TABLE+" ORDER BY RANDOM() LIMIT 1", null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                BookingData bookingData = new BookingData();
                bookingData.setId(cursor.getString(cursor.getColumnIndex(_ID)));
                bookingData.setName(cursor.getString(cursor.getColumnIndex(NAME)));
                bookingData.setMobile(cursor.getString(cursor.getColumnIndex(MOBILE)));
                bookingData.setAltCustomerImage(cursor.getString(cursor.getColumnIndex(IMAGE)));
                bookingData.setPickupDate(cursor.getString(cursor.getColumnIndex(PICKUP_DATE)));
                bookingData.setPickupTime(cursor.getString(cursor.getColumnIndex(PICKUP_TIME)));
                bookingData.setDropoffDate(cursor.getString(cursor.getColumnIndex(DROPOFF_DATE)));
                bookingData.setDropoffTime(cursor.getString(cursor.getColumnIndex(DROPOFF_TIME)));
                bookingData.setAddedon(cursor.getString(cursor.getColumnIndex(ADDEDON)));
                bookingData.setBookingStatus(cursor.getInt(cursor.getColumnIndex(BOOKING_STATUS)));

                bookingDataList.add(bookingData);

                cursor.moveToNext();
            }

            return bookingDataList;

        }else {
            return bookingDataList;
        }
    }

    public boolean isBookingIDExits(String  id){
        String[] columns = new String[]{_ID};

        Cursor cursor = this.database.query(BOOKING_TABLE, columns, _ID + " =? ", new String[]{String.valueOf(id)}, null, null, null);
        //Cursor cursor = this.database.rawQuery("SELECT * FROM 'issued_data_table' where booking_status = '1'",null);

        return cursor.getCount() > 0;
    }

    public void deleteBookingData(String id) {
        this.database.delete(BOOKING_TABLE, _ID + "=?", new String[]{id});
    }
    public void deleteAllBooking() {
        this.database.delete(BOOKING_TABLE, null, null);
    }

    private static class DbHelper extends SQLiteOpenHelper {
        public DbHelper(Context context) {
            super(context, Database.DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase database) {
            Database.createAllTables(database);
        }

        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            onCreate(database);
        }
    }


}
