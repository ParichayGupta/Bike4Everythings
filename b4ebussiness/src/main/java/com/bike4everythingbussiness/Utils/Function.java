package com.bike4everythingbussiness.Utils;

import android.util.Log;

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
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

    public static String getDirectionsUrlWaypont(LatLng origin, LatLng dest, List<String> waypoints){

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
}
