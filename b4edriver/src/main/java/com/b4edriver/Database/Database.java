package com.b4edriver.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.support.v4.util.Pair;

import com.b4edriver.DistanceCalculate.CurrentPathItem;
import com.b4edriver.DistanceCalculate.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by manishsingh on 10/03/18.
 */

public class Database {

    public static final String OFF = "off";
    public static final String ON = "on";
    private static final String DATABASE_NAME = "bikedistance_database";
    private static final int DATABASE_VERSION = 5;
    private static final String EMAIL = "email";
    private static final String DRIVER_CURRENT_LATITUDE = "driver_current_latitude";
    private static final String DRIVER_CURRENT_LOCATION_ACCURACY = "driver_current_location_accuracy";
    private static final String DRIVER_CURRENT_LOCATION_BEARING = "driver_current_location_bearing";
    private static final String DRIVER_CURRENT_LOCATION_TIME = "driver_current_location_time";
    private static final String DRIVER_CURRENT_LONGITUDE = "driver_current_longitude";
    private static final String TOTAL_DISTANCE = "total_distance";
    private static final String DRIVER_SAVE_LOCATION = "driver_save_location";
    private static final String DRIVER_SAVE_SPEED = "driver_save_speed";
    private static final String DRIVER_SAVE_TIME = "driver_save_time";
    private static final String DRIVER_SAVE_ISRUNNING = "driver_save_isrunning";
    private static final String WAIT_TIME = "wait_time";
    private static final String PARENT_ID = "parent_id";
    private static final String SLAT = "slat";
    private static final String SLNG = "slng";
    private static final String DLAT = "dlat";
    private static final String DLNG = "dlng";
    private static final String SECTION_INCOMPLETE = "section_incomplete";
    private static final String GOOGLE_PATH = "google_path";
    private static final String ACKNOWLEDGED = "acknowledged";
    private static final String METERING_STATE = "metering_state";

    private static final String TABLE_EMAIL_SUGGESTIONS = "table_email_suggestions";
    private static final String TABLE_DRIVER_CURRENT_LOCATION = "table_driver_current_location";
    private static final String TABLE_DRIVER_LOCATION_LIST = "table_driver_location_list";
    private static final String TABLE_TOTAL_DISTANCE = "table_total_distance";
    private static final String TABLE_WAIT_TIME = "table_wait_time";
    private static final String TABLE_CURRENT_PATH = "table_current_path";
    private static final String TABLE_METERING_STATE = "table_metering_state";

    private static Database dbInstance;
    private SQLiteDatabase database;
    private DbHelper dbHelper;

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

    private static void createAllTables(SQLiteDatabase database) {
        database.execSQL(" CREATE TABLE IF NOT EXISTS "+TABLE_EMAIL_SUGGESTIONS+" (email TEXT NOT NULL);");
        database.execSQL(" CREATE TABLE IF NOT EXISTS "+TABLE_TOTAL_DISTANCE+" ("+TOTAL_DISTANCE+" REAL);");
        database.execSQL(" CREATE TABLE IF NOT EXISTS "+TABLE_DRIVER_CURRENT_LOCATION+" ("+DRIVER_CURRENT_LATITUDE+" TEXT, "+DRIVER_CURRENT_LONGITUDE+" TEXT, "+DRIVER_CURRENT_LOCATION_ACCURACY+" TEXT, "+DRIVER_CURRENT_LOCATION_TIME+" TEXT, "+DRIVER_CURRENT_LOCATION_BEARING+" TEXT);");
        database.execSQL(" CREATE TABLE IF NOT EXISTS "+TABLE_DRIVER_LOCATION_LIST+" ("+DRIVER_SAVE_LOCATION+" TEXT, "+DRIVER_SAVE_SPEED+" TEXT, "+DRIVER_SAVE_ISRUNNING+" TEXT, "+DRIVER_SAVE_TIME+" TEXT);");
        database.execSQL(" CREATE TABLE IF NOT EXISTS "+TABLE_WAIT_TIME+" ("+WAIT_TIME+" TEXT);");
        database.execSQL(" CREATE TABLE IF NOT EXISTS table_current_path (id INTEGER PRIMARY KEY AUTOINCREMENT, parent_id INTEGER, slat REAL, slng REAL, dlat REAL, dlng REAL, section_incomplete INTEGER, google_path INTEGER, acknowledged INTEGER);");
        database.execSQL(" CREATE TABLE IF NOT EXISTS table_metering_state (metering_state TEXT);");
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

    private Database(Context context) {
        this.dbHelper = new DbHelper(context);
        database = this.dbHelper.getWritableDatabase();
        createAllTables(this.database);
    }

    public void close() {
        this.database.close();
        this.dbHelper.close();
        System.gc();
    }


    public void updateDriverCurrentLocation(Context context, Location location) {
        ContentValues contentValues;
        try {
            deleteDriverCurrentLocation();
            contentValues = new ContentValues();
            contentValues.put(DRIVER_CURRENT_LATITUDE, "" + location.getLatitude());
            contentValues.put(DRIVER_CURRENT_LONGITUDE, "" + location.getLongitude());
            contentValues.put(DRIVER_CURRENT_LOCATION_ACCURACY, "" + location.getAccuracy());
            contentValues.put(DRIVER_CURRENT_LOCATION_TIME, "" + location.getTime());
            contentValues.put(DRIVER_CURRENT_LOCATION_BEARING, "" + location.getBearing());
            Log.m615e("insert successful", "= rowId =" + this.database.insert(TABLE_DRIVER_CURRENT_LOCATION, null, contentValues));
        } catch (Exception e) {
            try {
                e.printStackTrace();
                Log.m615e("e", "=" + e);
                try {
                    dbInstance = null;
                    getInstance(context);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                alterTableDriverCurrentLocation();
                deleteDriverCurrentLocation();
                contentValues = new ContentValues();
                contentValues.put(DRIVER_CURRENT_LATITUDE, "" + location.getLatitude());
                contentValues.put(DRIVER_CURRENT_LONGITUDE, "" + location.getLongitude());
                contentValues.put(DRIVER_CURRENT_LOCATION_ACCURACY, "" + location.getAccuracy());
                contentValues.put(DRIVER_CURRENT_LOCATION_TIME, "" + location.getTime());
                contentValues.put(DRIVER_CURRENT_LOCATION_BEARING, "" + location.getBearing());
                this.database.insert(TABLE_DRIVER_CURRENT_LOCATION, null, contentValues);
            } catch (Exception e12) {
                e12.printStackTrace();
            }
        }
    }

    public Location getDriverCurrentLocation(Context context) {
        Cursor cursor;
        Location location = new Location("gps");
        int in0;
        int in1;
        int in2;
        int in3;
        int in4;
        try {
            cursor = this.database.query(TABLE_DRIVER_CURRENT_LOCATION, new String[]{DRIVER_CURRENT_LATITUDE, DRIVER_CURRENT_LONGITUDE, DRIVER_CURRENT_LOCATION_ACCURACY, DRIVER_CURRENT_LOCATION_TIME, DRIVER_CURRENT_LOCATION_BEARING}, null, null, null, null, null);
            in0 = cursor.getColumnIndex(DRIVER_CURRENT_LATITUDE);
            in1 = cursor.getColumnIndex(DRIVER_CURRENT_LONGITUDE);
            in2 = cursor.getColumnIndex(DRIVER_CURRENT_LOCATION_ACCURACY);
            in3 = cursor.getColumnIndex(DRIVER_CURRENT_LOCATION_TIME);
            in4 = cursor.getColumnIndex(DRIVER_CURRENT_LOCATION_BEARING);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                location.setLatitude(Double.parseDouble(cursor.getString(in0)));
                location.setLongitude(Double.parseDouble(cursor.getString(in1)));
                location.setAccuracy(Float.parseFloat(cursor.getString(in2)));
                location.setTime(Long.parseLong(cursor.getString(in3)));
                location.setBearing(Float.parseFloat(cursor.getString(in4)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                dbInstance = null;
                getInstance(context);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            alterTableDriverCurrentLocation();
            cursor = this.database.query(TABLE_DRIVER_CURRENT_LOCATION, new String[]{DRIVER_CURRENT_LATITUDE, DRIVER_CURRENT_LONGITUDE, DRIVER_CURRENT_LOCATION_ACCURACY, DRIVER_CURRENT_LOCATION_TIME, DRIVER_CURRENT_LOCATION_BEARING}, null, null, null, null, null);
            in0 = cursor.getColumnIndex(DRIVER_CURRENT_LATITUDE);
            in1 = cursor.getColumnIndex(DRIVER_CURRENT_LONGITUDE);
            in2 = cursor.getColumnIndex(DRIVER_CURRENT_LOCATION_ACCURACY);
            in3 = cursor.getColumnIndex(DRIVER_CURRENT_LOCATION_TIME);
            in4 = cursor.getColumnIndex(DRIVER_CURRENT_LOCATION_BEARING);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                location.setLatitude(Double.parseDouble(cursor.getString(in0)));
                location.setLongitude(Double.parseDouble(cursor.getString(in1)));
                location.setAccuracy(Float.parseFloat(cursor.getString(in2)));
                location.setTime(Long.parseLong(cursor.getString(in3)));
                location.setBearing(Float.parseFloat(cursor.getString(in4)));
            }
        }
        return location;
    }

    public void alterTableDriverCurrentLocation() {
        try {
            this.database.execSQL("DROP TABLE "+TABLE_DRIVER_CURRENT_LOCATION);
            this.database.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_DRIVER_CURRENT_LOCATION
                    +" ("+DRIVER_CURRENT_LATITUDE+" TEXT, "
                    +DRIVER_CURRENT_LONGITUDE+" TEXT, "
                    +DRIVER_CURRENT_LOCATION_ACCURACY +" TEXT, "
                    +DRIVER_CURRENT_LOCATION_TIME +" TEXT, "
                    +DRIVER_CURRENT_LOCATION_BEARING+" TEXT);");
            Log.m615e("drop query", "done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int deleteDriverCurrentLocation() {
        try {
            return this.database.delete(TABLE_DRIVER_CURRENT_LOCATION, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public double getTotalDistance() {
        double totaldistance = 0.0d;
        try {
            Cursor cursor = this.database.query(TABLE_TOTAL_DISTANCE, new String[]{TOTAL_DISTANCE}, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                totaldistance = cursor.getDouble(cursor.getColumnIndex(TOTAL_DISTANCE));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totaldistance;
    }

    public void updateTotalDistance(double totalDistance) {
        try {
            deleteTotalDistance();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TOTAL_DISTANCE, totalDistance);
            this.database.insert(TABLE_TOTAL_DISTANCE, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int deleteTotalDistance() {
        try {
            return this.database.delete(TABLE_TOTAL_DISTANCE, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }



    public void updateDriverLocation(String latlng, String speed, String isrunning, String time) {
        try {

            ContentValues contentValues = new ContentValues();
            contentValues.put(DRIVER_SAVE_LOCATION, latlng);
            contentValues.put(DRIVER_SAVE_SPEED, speed);
            contentValues.put(DRIVER_SAVE_ISRUNNING, isrunning);
            contentValues.put(DRIVER_SAVE_TIME, time);
            this.database.insert(TABLE_DRIVER_LOCATION_LIST, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getDriverLocation() {
        List<String> driverlocation = new ArrayList<>();
        try {
            Cursor cursor = this.database.query(TABLE_DRIVER_LOCATION_LIST, new String[]{DRIVER_SAVE_LOCATION, DRIVER_SAVE_SPEED, DRIVER_SAVE_ISRUNNING, DRIVER_SAVE_TIME}, null, null, null, null, null);

            if (cursor.getCount() <= 0) {
                return driverlocation;
            }

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                driverlocation.add(cursor.getString(cursor.getColumnIndex(DRIVER_SAVE_LOCATION))+"="
                        +cursor.getString(cursor.getColumnIndex(DRIVER_SAVE_SPEED))+"="
                        +cursor.getString(cursor.getColumnIndex(DRIVER_SAVE_ISRUNNING))+"="
                        +cursor.getString(cursor.getColumnIndex(DRIVER_SAVE_TIME))
                );
                cursor.moveToNext();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return driverlocation;
    }


    public int deleteDriverLocation() {
        try {
            return this.database.delete(TABLE_DRIVER_LOCATION_LIST, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getWaitTimeFromDB() {
        try {
            Cursor cursor = this.database.query(TABLE_WAIT_TIME, new String[]{WAIT_TIME}, null, null, null, null, null);
            if (cursor.getCount() <= 0) {
                return "";
            }
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex(WAIT_TIME));
        } catch (Exception e) {
            return "";
        }
    }

    public int updateWaitTime(String time) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(WAIT_TIME, time);
            int rowsAffected = this.database.update(TABLE_WAIT_TIME, contentValues, null, null);
            if (rowsAffected != 0) {
                return rowsAffected;
            }
            this.database.insert(TABLE_WAIT_TIME, null, contentValues);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void deleteWaitTimeData() {
        try {
            this.database.delete(TABLE_WAIT_TIME, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public long insertCurrentPathItem(long parentId, double slat, double slng, double dlat, double dlng, int sectionIncomplete, int googlePath) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(PARENT_ID, Long.valueOf(parentId));
            contentValues.put(SLAT, slat);
            contentValues.put(SLNG, slng);
            contentValues.put(DLAT, dlat);
            contentValues.put(DLNG, dlng);
            contentValues.put(SECTION_INCOMPLETE, Integer.valueOf(sectionIncomplete));
            contentValues.put(GOOGLE_PATH, Integer.valueOf(googlePath));
            contentValues.put(ACKNOWLEDGED, Integer.valueOf(0));
            return this.database.insert(TABLE_CURRENT_PATH, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int updateCurrentPathItemSectionIncomplete(long rowId, int sectionIncomplete) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(SECTION_INCOMPLETE, Integer.valueOf(sectionIncomplete));
            return this.database.update(TABLE_CURRENT_PATH, contentValues, "id=" + rowId, null);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int updateCurrentPathItemSectionIncompleteAndGooglePath(long rowId, int sectionIncomplete, int googlePath) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(SECTION_INCOMPLETE, Integer.valueOf(sectionIncomplete));
            contentValues.put(GOOGLE_PATH, Integer.valueOf(googlePath));
            return this.database.update(TABLE_CURRENT_PATH, contentValues, "id=" + rowId, null);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int updateCurrentPathItemAcknowledged(long rowId, int acknowledged) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ACKNOWLEDGED, Integer.valueOf(acknowledged));
            return this.database.update(TABLE_CURRENT_PATH, contentValues, "id=" + rowId, null);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int updateCurrentPathItemAcknowledgedForArray(ArrayList<Long> rowId, int acknowledged) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ACKNOWLEDGED, Integer.valueOf(acknowledged));
            int rowsAffected = this.database.update(TABLE_CURRENT_PATH, contentValues, "id in(" + rowId.toString().substring(1, rowId.toString().length() - 1) + ")", null);
            Log.m615e("rowsAffected", "=" + rowsAffected);
            return rowsAffected;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public ArrayList<CurrentPathItem> getCurrentPathItemsSaved() {
        ArrayList<CurrentPathItem> currentPathItems = new ArrayList();
        try {
            currentPathItems.addAll(getCurrentPathItemsFromCursor(this.database.query(TABLE_CURRENT_PATH, new String[]{"id", PARENT_ID, SLAT, SLNG, DLAT, DLNG, SECTION_INCOMPLETE, GOOGLE_PATH, ACKNOWLEDGED}, null, null, null, null, null)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentPathItems;
    }

    public ArrayList<CurrentPathItem> getCurrentPathItemsToUpload() {
        ArrayList<CurrentPathItem> currentPathItems = new ArrayList();
        try {
            currentPathItems.addAll(getCurrentPathItemsFromCursor(this.database.query(TABLE_CURRENT_PATH, new String[]{"id", PARENT_ID, SLAT, SLNG, DLAT, DLNG, SECTION_INCOMPLETE, GOOGLE_PATH, ACKNOWLEDGED}, "acknowledged=0", null, null, null, null)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentPathItems;
    }

    public Pair<Double, CurrentPathItem> getCurrentPathItemsAllComplete() {
        ArrayList<CurrentPathItem> currentPathItems = new ArrayList();
        try {
            currentPathItems.addAll(getCurrentPathItemsFromCursor(this.database.query(TABLE_CURRENT_PATH, new String[]{"id", PARENT_ID, SLAT, SLNG, DLAT, DLNG, SECTION_INCOMPLETE, GOOGLE_PATH, ACKNOWLEDGED}, "section_incomplete=0", null, null, null, null)));
            if (currentPathItems.size() <= 0) {
                return null;
            }
            double totalDistance = 0.0d;
            CurrentPathItem lastCPI = currentPathItems.get(currentPathItems.size() - 1);
            Iterator it = currentPathItems.iterator();
            while (it.hasNext()) {
                totalDistance += ((CurrentPathItem) it.next()).distance();
            }
            return new Pair(Double.valueOf(totalDistance), lastCPI);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<CurrentPathItem> getCurrentPathItemsFromCursor(Cursor cursor) {
        ArrayList<CurrentPathItem> currentPathItems = new ArrayList();
        try {
            if (cursor.getCount() > 0) {
                int in0 = cursor.getColumnIndex("id");
                int in1 = cursor.getColumnIndex(PARENT_ID);
                int in2 = cursor.getColumnIndex(SLAT);
                int in3 = cursor.getColumnIndex(SLNG);
                int in4 = cursor.getColumnIndex(DLAT);
                int in5 = cursor.getColumnIndex(DLNG);
                int in6 = cursor.getColumnIndex(SECTION_INCOMPLETE);
                int in7 = cursor.getColumnIndex(GOOGLE_PATH);
                int in8 = cursor.getColumnIndex(ACKNOWLEDGED);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    if (cursor.getInt(in6) != 0) {
                        break;
                    }
                    CurrentPathItem currentPathItem = new CurrentPathItem(cursor.getLong(in0), cursor.getLong(in1), cursor.getDouble(in2), cursor.getDouble(in3), cursor.getDouble(in4), cursor.getDouble(in5), cursor.getInt(in6), cursor.getInt(in7), cursor.getInt(in8));
                    if (-1 == currentPathItem.parentId) {
                        currentPathItems.add(currentPathItem);
                    }
                    if (1 == currentPathItem.googlePath) {
                        long parentId = currentPathItem.id;
                        int currentCursorPosition = cursor.getPosition();
                        cursor.moveToPosition(currentCursorPosition);
                        while (!cursor.isAfterLast()) {
                            try {
                                if (cursor.getInt(in6) != 0) {
                                    break;
                                }
                                CurrentPathItem currentPathItemChild = new CurrentPathItem(cursor.getLong(in0), cursor.getLong(in1), cursor.getDouble(in2), cursor.getDouble(in3), cursor.getDouble(in4), cursor.getDouble(in5), cursor.getInt(in6), cursor.getInt(in7), cursor.getInt(in8));
                                if (parentId == currentPathItemChild.parentId) {
                                    currentPathItems.add(currentPathItemChild);
                                }
                                try {
                                    cursor.moveToNext();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                        }
                        cursor.moveToPosition(currentCursorPosition);
                    }
                    cursor.moveToNext();
                }
            }
        } catch (Exception e22) {
            e22.printStackTrace();
        }
        return currentPathItems;
    }

    public void deleteAllCurrentPathItems() {
        try {
            this.database.delete(TABLE_CURRENT_PATH, null, null);
            this.database.execSQL("DROP TABLE table_current_path");
            createAllTables(this.database);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getMetringState() {
        Cursor cursor = this.database.query(TABLE_METERING_STATE, new String[]{METERING_STATE}, null, null, null, null, null);
        if (cursor.getCount() <= 0) {
            return OFF;
        }
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(METERING_STATE));
    }

    public int updateMetringState(String choice) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(METERING_STATE, choice);
            int rowsAffected = this.database.update(TABLE_METERING_STATE, contentValues, null, null);
            if (rowsAffected != 0) {
                return rowsAffected;
            }
            this.database.insert(TABLE_METERING_STATE, null, contentValues);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

}
