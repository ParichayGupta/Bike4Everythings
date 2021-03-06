package com.bike4everythingbussiness.Utils;

/**
 * Created by MAX on 12/3/2015.
 */

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DirectionsJSONParser {


    public static List<String> htlmStringList ;

    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
    public List<List<HashMap<String,String>>> parse(JSONObject jObject){

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        JSONObject jDistance = null;
        JSONObject jDuration = null;
        JSONObject jEndLocation = null;
        JSONObject jTurn = null;
        htlmStringList = new ArrayList<String>();

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");

                List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                List<HashMap<String, String>> shortPathList = new ArrayList<HashMap<String, String>>();

                List<HashMap<String, String>> trunList = new ArrayList<HashMap<String, String>>();
                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){

                    /** Getting distance from the json data */
                    jDistance = ((JSONObject) jLegs.get(j)).getJSONObject("distance");
                    HashMap<String, String> hmDistance = new HashMap<String, String>();
                    hmDistance.put("distance", jDistance.getString("text"));
                    hmDistance.put("value", jDistance.getString("value"));

                    /** Getting duration from the json data */
                    jDuration = ((JSONObject) jLegs.get(j)).getJSONObject("duration");
                    HashMap<String, String> hmDuration = new HashMap<String, String>();
                    hmDuration.put("duration", jDuration.getString("text"));
                    hmDuration.put("value", jDuration.getString("value"));

                    /** Getting end-location from the json data */
                    jEndLocation = ((JSONObject) jLegs.get(j)).getJSONObject("end_location");
                    HashMap<String, String> hmEndlocation = new HashMap<String, String>();
                    hmEndlocation.put("end_location", jEndLocation.getString("lat")+"-"+jEndLocation.getString("lng"));

                    /** Adding distance object to the path */
                    path.add(hmDistance);

                    /** Adding duration object to the path */
                    path.add(hmDuration);

                    /** Adding end_location object to the path */
                    path.add(hmEndlocation);

                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");



                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);



                        /** Traversing all points */
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(list.get(l).latitude) );
                            hm.put("lng", Double.toString(list.get(l).longitude) );
                            path.add(hm);
                        }

                        double startLat = (double) ((JSONObject) ((JSONObject) jSteps.get(k)).get("start_location")).get("lat");
                        double startLng = (double) ((JSONObject) ((JSONObject) jSteps.get(k)).get("start_location")).get("lng");

                        double endLat = (double) ((JSONObject) ((JSONObject) jSteps.get(k)).get("end_location")).get("lat");
                        double endLng = (double) ((JSONObject) ((JSONObject) jSteps.get(k)).get("end_location")).get("lng");


                        HashMap<String, String> startLocation = new HashMap<String, String>();
                        startLocation.put("lat", String.valueOf(startLat));
                        startLocation.put("lng", String.valueOf(startLng));

                        HashMap<String, String> endLocation = new HashMap<String, String>();
                        endLocation.put("lat", String.valueOf(endLat));
                        endLocation.put("lng", String.valueOf(endLng));

                        shortPathList.add(startLocation);
                        shortPathList.add(endLocation);

                        try {
                            String turn = (String)(((JSONObject)jSteps.get(k)).get("maneuver"));
                            HashMap<String, String> hmTurn = new HashMap<String, String>();
                            hmTurn.put("turn", turn);
                            trunList.add(hmTurn);

                        }catch (JSONException e){
                            HashMap<String, String> hmTurn = new HashMap<String, String>();
                            hmTurn.put("turn", "");
                            trunList.add(hmTurn);
                        }

                        String htmll="";
                      //  if(k==0 || k==1) {
                            htmll = (String) (((JSONObject) jSteps.get(k)).get("html_instructions"));


                       // }
                        htlmStringList.add(htmll);
                    }
                }
                routes.add(shortPathList);
                routes.add(path);
                routes.add(trunList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return routes;
    }

    /**
     * Method to decode polyline points
     * Courtesy : jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }
}