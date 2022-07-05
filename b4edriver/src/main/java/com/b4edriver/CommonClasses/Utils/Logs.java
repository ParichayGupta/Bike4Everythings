package com.b4edriver.CommonClasses.Utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.b4edriver.Database.DBAdapter_Driver;
import com.b4elibrary.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;

/**
 * Created by System7 on 20/07/2016.
 */
public class Logs {


    static Context mContext;

    public static void setLog(Context context, String tag, String msg) {
        Logger.log(tag, msg);
        mContext = context;
        new DBAdapter_Driver(context).insertLogs(tag, msg);
    }

    /*public static void sendMailAfterFinish(Context mContext, String location, double distance, double freedistance, String startLoc, String endLoc) {

        String cid = ContentIdGenerator.getContentId();

        String map = "<a href=\'https://maps.googleapis.com/maps/api/staticmap?" +
                "size=800x800" +
                "&markers=color:blue|label:S|" + startLoc +
                "&markers=color:green|label:D|" + endLoc +
                "&path=color:0xff0000ff|" + location.replace("[", "").replace(", ", "|").replace(" ", ",").replace("]", "") +
                "&key=AIzaSyA5Ro0Lx9hWfjNWYPqlxQV0KHhkmS_WbeQ\'> Show Route On Map </a>";

        String body = "<html><head>"
                + "<title>Trip Details</title>"
                + "</head>\n"
                + "<body>"//<img src="smiley.gif" alt="Smiley face" height="42" width="42">
                + "<div><img src=\"cid:"
                + cid
                + "\"alt=\"Bike 4 Everything\" height=\"50\" width=\"100\"></div>"
                + "<div><b>Trip ID: " + AppPreferencesDriver.getTripId(mContext) + "</b></div>"
                + "<div><b>Driver Id: " + AppPreferencesDriver.getDriverId(mContext) + "</b></div>"
                + "<div><b>Driver Name: " + new DBAdapter_Driver(mContext).getUser().getName() + "</b></div>"
                + "<div><b>Free Distance: " + roundTwoDecimals(freedistance / 1000) + " km" + "</b></div>"
                + "<div><b>Total Trip Distance: " + roundTwoDecimals(distance / 1000) + " km" + "</b></div>"
                + "<div>" + map + " </div></body></html>";

        *//*String body = "===============================Trip Details========================================\n"
                +"=> Trip ID:   " + AppPreferencesDriver.getTripId(mContext)                        +"\n"
                +"=> Driver Id: " + AppPreferencesDriver.getDriverId(mContext)                              +"\n"
                +"=> Driver Name: " + new DBAdapter_Driver(mContext).getUser().getName()            +"\n"
                +"=> trip Distance Testing: " + roundTwoDecimals(testDistance)+" km"                                  +"\n"
                +"=> trip Distance on end trip: " + roundTwoDecimals(distance/1000)+" km"                                  +"\n"
                +"=> Free ride Distance from start: " + roundTwoDecimals(freedistance/1000)+" km"                          +"\n"
                +"=> This trip show on map : " + map                                            +"\n"
                +"====================================================================================";
*//*
        Intent intent = new Intent(mContext, MyEmail.class);

        intent.setAction("end");
        intent.putExtra("subject", "B4E Before End Trip");
        intent.putExtra("body", body);
        intent.putExtra("senderid", AppConstantDriver.EMAILID_SENDER);
        intent.putExtra("recipients", AppConstantDriver.EMAILID_ALL);
        intent.putExtra("fileName", getTripLogFile());
        intent.putExtra("cid", cid);

        mContext.startService(intent);

    }*/

    /*public static void sendMailBeforeStart(Context mContext) {
        DistanceTravelled distanceTravelled = new DBAdapter_Driver(mContext).getDistance();
        double tripdistance = 0.0;
        double freedistance = 0.0;
        if (distanceTravelled != null) {
            tripdistance = distanceTravelled.getDistanceTrip();
            freedistance = distanceTravelled.getDistanceFree();
        }
        Intent intent = new Intent(mContext, MyEmail.class);
        intent.setAction("start");
        intent.putExtra("subject", "B4E Before Start Trip");
        intent.putExtra("body",
                "\n===============================Trip Details========================================\n"
                        + "=> Trip ID:   " + AppPreferencesDriver.getTripId(mContext) + "\n"
                        + "=> Driver Id: " + AppPreferencesDriver.getDriverId(mContext) + "\n"
                        + "=> Driver Name: " + new DBAdapter_Driver(mContext).getUser().getName() + "\n"
                        + "=> trip Distance from start: " + tripdistance / 1000 + " km" + "\n"
                        + "=> Free ride Distance from start: " + freedistance / 1000 + " km" + "\n"
                        + "====================================================================================");
        intent.putExtra("senderid", AppConstantDriver.EMAILID_SENDER);
        intent.putExtra("recipients", AppConstantDriver.EMAILID_ALL);
        mContext.startService(intent);

    }*/


    public static String getTripLogFile() {
        String strFile;

        strFile = Environment.getExternalStorageDirectory() + "/B4ELogs";

        // if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        File cacheDir = new File(strFile);
        // else
        //     cacheDir = mContext.getApplicationContext().getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();

        //********************************************************
//        String[] files = cacheDir.list();
//        for (int i=0; i<files.length; i++) {
//            Log.e("file name",files[i]);
//        }

        //walkdir(cacheDir);

        //**********************************************************

        Log.e("dir", cacheDir.toString());
        DBAdapter_Driver dbAdapter_driver = new DBAdapter_Driver(mContext);
        String str = null;
        try {
            str = dbAdapter_driver.getLogs();
            dbAdapter_driver.deleteLogs();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        FileOutputStream fos;
        try {
//            Date date1 = new Date();
//            String a = strFile + "/log"+ ".txt";
//            Log.e("fileName", a);
//            File myFile = new File(a);
//            try {
//                myFile.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            fos = new FileOutputStream(myFile);
//            OutputStreamWriter myOutWriter = new OutputStreamWriter(fos);
//            myOutWriter.append(Html.fromHtml(str != null ? str : "I am testing logs"));
//            myOutWriter.close();
//            fos.flush();
//            fos.close();
//            try {
//                dbAdapter_driver.deleteLogs();
//            } catch (NullPointerException e) {
//                e.printStackTrace();
//            }

            //deleteFile(myFile.toString(),mContext);
            return str.toString();
        } catch (Exception e) {
            Log.e("Exception ", "Creating logs " + e.toString());
            e.printStackTrace();
            return "";
        }
    }

    public static double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    public static void deleteFile(String fileName, Context context) {
        context.deleteFile(fileName);

    }
}
