package com.b4ebusinessdriver.Utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.b4ebusinessdriver.R;
import com.b4elibrary.Logger;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by manishsingh on 06/01/18.
 */

public class Function {


    public static String dateTimeFormat(String date_s){
        // String date_s = " 2011-01-18 00:00:00.0";
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        Date date = null;
        try {
            date = dt.parse(date_s);
        } catch (ParseException e) {
            e.printStackTrace();
            return date_s;
        }
        SimpleDateFormat dt1 = new SimpleDateFormat("dd MMM yyyy hh:mm a");
        System.out.println(dt1.format(date));
        return dt1.format(date);
    }

    public static String getDirectionsUrl(LatLng origin, LatLng dest){

        String key = "key=AIzaSyDJhqncRtd0xZjv4dzx57q46dpV2CE0Bhw";

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";


        // String mode = "mode=\"DRIVING\"";

        String alternatives = "alternatives=true";

        String traffic_model = "traffic_model=pessimistic";

        // Building the parameters to the web service
        String parameters = key+"&"+str_origin+"&"+str_dest+"&"+sensor+"&"+alternatives;

        // Output format
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
        Logger.log("routeUrl", url);
        return url;
    }
    public static String getDirectionsUrlWaypont(LatLng origin, LatLng dest, List<String> waypoints){

        String key = "key=AIzaSyDJhqncRtd0xZjv4dzx57q46dpV2CE0Bhw";

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";


        // String mode = "mode=\"DRIVING\"";

        String alternatives = "alternatives=false";

        String traffic_model = "traffic_model=pessimistic";

        String waypoint="";

        for(int i=0; i<waypoints.size(); i++){
            if(i==0){
                waypoint = "via:"+waypoints.get(i);
            }else {
                waypoint = waypoint + "|via:" + waypoints.get(i);
            }
        }
        // Building the parameters to the web service
        String parameters = key+"&"+str_origin+"&"+str_dest+"&"+sensor+"&"+alternatives+"&waypoints="+waypoint;

        // Output format
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
        Logger.log("routeUrl", url);
        return url;
    }

    public static BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static boolean isServiceRunning(Context context, String serviceClassName){
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
                return true;
            }
        }
        return false;
    }
    public static int getBatteryLevel(Context context)
    {
        Intent intent  = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int    level   = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int    scale   = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        int    percent = (level*100)/scale;
        return percent;
    }

    public static boolean isGPSEnabled(Context context)
    {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE );
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context ctx) {
        ActivityManager manager = (ActivityManager)ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
