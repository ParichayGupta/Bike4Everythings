package com.b4ebusinessdriver.DistanceCalculate;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MapUtils {
    public static double distance(LatLng start, LatLng end) {
        try {
            Location location1 = new Location("locationA");
            location1.setLatitude(start.latitude);
            location1.setLongitude(start.longitude);
            Location location2 = new Location("locationA");
            location2.setLatitude(end.latitude);
            location2.setLongitude(end.longitude);
            return (double) location1.distanceTo(location2);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0d;
        }
    }

    public static double distance(Location start, Location end) {
        try {
            return (double) start.distanceTo(end);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0d;
        }
    }

    public static double speed(Location start, Location end) {
        if (start == null || end == null) {
            return 0.0d;
        }
        long secondsDiff = Math.abs((end.getTime() - start.getTime()) / 1000);
        double displacement = distance(start, end);
        if (secondsDiff > 0) {
            return displacement / ((double) secondsDiff);
        }
        return 0.0d;
    }

    public static String makeDirectionsURL(LatLng source, LatLng destination) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");
        urlString.append(Double.toString(source.latitude));
        urlString.append(",");
        urlString.append(Double.toString(source.longitude));
        urlString.append("&destination=");
        urlString.append(Double.toString(destination.latitude));
        urlString.append(",");
        urlString.append(Double.toString(destination.longitude));
        urlString.append("&sensor=false&mode=driving&alternatives=false");
        return urlString.toString();
    }

    public static String makeDistanceMatrixURL(LatLng source, LatLng destination) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/distancematrix/json");
        urlString.append("?origins=");
        urlString.append(Double.toString(source.latitude));
        urlString.append(",");
        urlString.append(Double.toString(source.longitude));
        urlString.append("&destinations=");
        urlString.append(Double.toString(destination.latitude));
        urlString.append(",");
        urlString.append(Double.toString(destination.longitude));
        urlString.append("&language=EN&sensor=false&alternatives=false");
        return urlString.toString();
    }

    public static List<LatLng> decodeDirectionsPolyline(String encoded) {
        List<LatLng> poly = new ArrayList();
        int index = 0;
        int len = encoded.length();
        int lat = 0;
        int lng = 0;
        while (index < len) {
            int index2;
            int shift = 0;
            int result = 0;
            while (true) {
                index2 = index + 1;
                int bInt = encoded.charAt(index) - 63;
                result |= (bInt & 31) << shift;
                shift += 5;
                if (bInt < 32) {
                    break;
                }
                index = index2;
            }
            lat += (result & 1) == 0 ? result >> 1 : (result >> 1) ^ -1;
            shift = 0;
            result = 0;
            index = index2;
            while (true) {
                index2 = index + 1;
               int bInt = encoded.charAt(index) - 63;
                result |= (bInt & 31) << shift;
                shift += 5;
                if (bInt < 32) {
                    break;
                }
                index = index2;
            }
            lng += (result & 1) == 0 ? result >> 1 : (result >> 1) ^ -1;
            poly.add(new LatLng(((double) lat) / 100000.0d, ((double) lng) / 100000.0d));
            index = index2;
        }
        return poly;
    }

    public static List<LatLng> getLatLngListFromPath(String result) {
        try {
            return decodeDirectionsPolyline(new JSONObject(result).getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline").getString("points"));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList();
        }
    }

    public static String getGAPIAddress(Context context, LatLng latLng, boolean toLocality) {
        String fullAddress = "Unnamed";
        try {
            String language = "";
            language = context.getResources().getConfiguration().locale.toString();
            if (language.equalsIgnoreCase("hi") || language.equalsIgnoreCase("hi_in")) {
                language = "hi";
            } else {
                language = "en";
            }
            JSONObject jSONObject = null;//= new JSONObject(new String(((TypedByteArray) RestClient.getGoogleApiServices().geocode(latLng.latitude + "," + latLng.longitude, language, Boolean.valueOf(false)).getBody()).getBytes()));
            if (!jSONObject.getString("status").equalsIgnoreCase("OK")) {
                return fullAddress;
            }
            JSONObject zero = jSONObject.getJSONArray("results").getJSONObject(0);
            String streetNumber = "";
            String route = "";
            String subLocality2 = "";
            String subLocality1 = "";
            String locality = "";
            String administrativeArea2 = "";
            String administrativeArea1 = "";
            String country = "";
            String postalCode = "";
            if (!zero.has("address_components")) {
                return zero.getString("formatted_address");
            }
            try {
                int i;
                ArrayList<String> selectedAddressComponentsArr = new ArrayList();
                JSONArray addressComponents = zero.getJSONArray("address_components");
                for (i = 0; i < addressComponents.length(); i++) {
                    JSONObject iObj = addressComponents.getJSONObject(i);
                    JSONArray jArr = iObj.getJSONArray("types");
                    ArrayList<String> addressTypes = new ArrayList();
                    for (int j = 0; j < jArr.length(); j++) {
                        addressTypes.add(jArr.getString(j));
                    }
                    if ("".equalsIgnoreCase(streetNumber) && addressTypes.contains("street_number")) {
                        streetNumber = iObj.getString("long_name");
                        if (!("".equalsIgnoreCase(streetNumber) || selectedAddressComponentsArr.toString().contains(streetNumber))) {
                            selectedAddressComponentsArr.add(streetNumber);
                        }
                    }
                    if ("".equalsIgnoreCase(route) && addressTypes.contains("route")) {
                        route = iObj.getString("long_name");
                        if (!("".equalsIgnoreCase(route) || selectedAddressComponentsArr.toString().contains(route))) {
                            selectedAddressComponentsArr.add(route);
                        }
                    }
                    if ("".equalsIgnoreCase(subLocality2) && addressTypes.contains("sublocality_level_2")) {
                        subLocality2 = iObj.getString("long_name");
                        if (!("".equalsIgnoreCase(subLocality2) || selectedAddressComponentsArr.toString().contains(subLocality2))) {
                            selectedAddressComponentsArr.add(subLocality2);
                        }
                    }
                    if ("".equalsIgnoreCase(subLocality1) && addressTypes.contains("sublocality_level_1")) {
                        subLocality1 = iObj.getString("long_name");
                        if (!("".equalsIgnoreCase(subLocality1) || selectedAddressComponentsArr.toString().contains(subLocality1))) {
                            selectedAddressComponentsArr.add(subLocality1);
                        }
                    }
                    if ("".equalsIgnoreCase(locality) && addressTypes.contains("locality")) {
                        locality = iObj.getString("long_name");
                        if (!("".equalsIgnoreCase(locality) || selectedAddressComponentsArr.toString().contains(locality))) {
                            selectedAddressComponentsArr.add(locality);
                        }
                    }
                    if ("".equalsIgnoreCase(administrativeArea2) && addressTypes.contains("administrative_area_level_2")) {
                        administrativeArea2 = iObj.getString("long_name");
                        if (!("".equalsIgnoreCase(administrativeArea2) || selectedAddressComponentsArr.toString().contains(administrativeArea2))) {
                            selectedAddressComponentsArr.add(administrativeArea2);
                        }
                    }
                    if ("".equalsIgnoreCase(administrativeArea1) && addressTypes.contains("administrative_area_level_1")) {
                        administrativeArea1 = iObj.getString("long_name");
                        if (!("".equalsIgnoreCase(administrativeArea1) || selectedAddressComponentsArr.toString().contains(administrativeArea1))) {
                            selectedAddressComponentsArr.add(administrativeArea1);
                        }
                    }
                    if (!toLocality) {
                        if ("".equalsIgnoreCase(country) && addressTypes.contains("country")) {
                            country = iObj.getString("long_name");
                            if (!("".equalsIgnoreCase(country) || selectedAddressComponentsArr.toString().contains(country))) {
                                selectedAddressComponentsArr.add(country);
                            }
                        }
                        if ("".equalsIgnoreCase(postalCode) && addressTypes.contains("postal_code")) {
                            postalCode = iObj.getString("long_name");
                            if (!("".equalsIgnoreCase(postalCode) || selectedAddressComponentsArr.toString().contains(postalCode))) {
                                selectedAddressComponentsArr.add(postalCode);
                            }
                        }
                    }
                }
                fullAddress = "";
                if (selectedAddressComponentsArr.size() <= 0) {
                    return zero.getString("formatted_address");
                }
                for (i = 0; i < selectedAddressComponentsArr.size(); i++) {
                    if (i < selectedAddressComponentsArr.size() - 1) {
                        fullAddress = fullAddress + selectedAddressComponentsArr.get(i) + ", ";
                    } else {
                        fullAddress = fullAddress + selectedAddressComponentsArr.get(i);
                    }
                }
                return fullAddress;
            } catch (Exception e) {
                e.printStackTrace();
                return zero.getString("formatted_address");
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            return fullAddress;
        }
    }
}
