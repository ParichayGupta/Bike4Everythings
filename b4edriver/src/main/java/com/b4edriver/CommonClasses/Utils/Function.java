package com.b4edriver.CommonClasses.Utils;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.b4elibrary.Logger;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by MAX on 11/9/2015.
 */
public class Function {

    public static EditText EditTextPointer;
    public static String errorMessage;


    public void test(){}
    
    public static boolean isEmailValid(EditText tv) {
        //add your own logic
        if (TextUtils.isEmpty(tv.getText())) {
            EditTextPointer = tv;
            errorMessage = "This field can't be empty.!";
            return false;
        } else {
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(tv.getText()).matches()) {
                return true;
            } else {
                EditTextPointer = tv;
                errorMessage = "Invalid Email Id";
                return false;
            }
        }
    }

    public static boolean validatePass(String pass1, String pass2){

        if(pass1.length() < 1 || pass2.length() < 1 ) {
            errorMessage = "This field can't be empty.!";
            return false;
        }

        if (pass1 != null && pass2 != null) {

            if (pass1.equals(pass2)) {


                pass1 = pass2;
                boolean hasUppercase = !pass1.equals(pass1.toLowerCase());
                boolean hasLowercase = !pass1.equals(pass1.toUpperCase());
                boolean hasNumber = pass1.matches(".*\\d.*");
                boolean noSpecialChar = pass1.matches("[a-zA-Z0-9 ]*");

                if (pass1.length() < 6) {

                    errorMessage = ("Password is too short. Needs to have 6 characters");
                    return false;
                } else if (!hasNumber) {

                    errorMessage = ("Password needs a number ");
                    return false;
                } else if(noSpecialChar){

                    errorMessage = ("Password needs a special character i.e. !,@,#, etc.");
                    return false;
                }else{
                    return true;
                }

                /*if (!hasUppercase) {
                    logger.info(pass1 + " <-- needs uppercase");
                    retVal.append("Password needs an upper case <br>");
                }

                if (!hasLowercase) {
                    logger.info(pass1 + " <-- needs lowercase");
                    retVal.append("Password needs a lowercase <br>");
                }*/


            }else{

                errorMessage = ("Passwords don't match");
                return false;
            }
        }else{
            //logger.info("Passwords = null");
            errorMessage = ("Passwords Null ");
            return false;
        }

/*
        if(retVal.length() == 0){
            //logger.info("Password validates");
            errorMessage = ("Success");
        }*/

    }

    public static boolean confirmPassword(String pass1, String pass2){
        if(pass2.length() < 1 ) {
            errorMessage = "This field can't be empty.!";
            return false;
        }

        if (pass1.equals(pass2)) {
            return true;
        }else{
            errorMessage = ("Passwords don't match");
            return false;
        }
    }

    public static boolean passwordValidation(String pass1){

        if(pass1.length() < 1) {
            errorMessage = "This field can't be empty.!";
            return false;
        }


        boolean hasNumber = pass1.matches(".*\\d.*");
        boolean noSpecialChar = pass1.matches("[a-zA-Z0-9 ]*");

        if (pass1.length() < 6) {

            errorMessage = ("Password is too short. Needs to have 6 characters");
            return false;
        } else if (!hasNumber) {

            errorMessage = ("Password needs a number");
            return false;
        } else if(noSpecialChar){

            errorMessage = ("Password needs a special character i.e. !,@,#, etc.");
            return false;
        }else{
            return true;
        }
    }

    public static final boolean isValidPhoneNumber(EditText tv) {

        if (tv.getText() == null || TextUtils.isEmpty(tv.getText())) {
            errorMessage = "This field can't be empty.!";
            return false;
        } else {

            Pattern pattern;
            Matcher matcher;
            final String MOBILE_PATTERN = "^[7-9][0-9]{9}$";
            pattern = Pattern.compile(MOBILE_PATTERN);
            matcher = pattern.matcher(tv.getText().toString());
            boolean isMatch = matcher.matches();
            if (isMatch) {
                return true;
            } else {
                EditTextPointer = tv;
                errorMessage = "Invalid Mobile No.";
                return false;
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

    }
    public static String generateUniqueId()
    {

        String uniqueid = "";
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZ";
        int string_length = 4;

        for (int i=0; i<string_length; i++) {
            int rnum = (int) Math.floor(Math.random() * chars.length());
            uniqueid += chars.substring(rnum,rnum+1);
        }
        return uniqueid;
    }

   




    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
        }
        return false;
    }

    public static boolean isConnectingToInternet(Activity context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
        }
        return false;
    }

    public static boolean isOnline(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        return ni != null && ni.isConnected();
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

    public static String getDirectionsUrl(LatLng origin,LatLng dest){

        String key = AppConstantDriver.GOOGLE_API_KEY; //"key=AIzaSyCEMOnYHopPu9cO0NVSSpEqH7WnhfzEYRM";

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

    public static String getCurrentDateTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(c.getTime());
        return strDate;
    }
    public static String getDateTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        String strDate = sdf.format(c.getTime());
        return strDate;
    }

    public static String getCurrentDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(c.getTime());
        return strDate;
    }

    



   
    /*public static String getAddressFromLatlng(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append(" ");
                }
                sb.append(address.getLocality()).append(" ");
                sb.append(address.getPostalCode()).append(" ");
                sb.append(address.getCountryName());
                result = sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Logger.log("Addresss::",e.getMessage());
        }
        return result;
    }*/

    public static String getAddressFromLatlng(double latitude, double longitude) {

        String result = null;
        try {
            String key = AppConstantDriver.GOOGLE_API_KEY;  //AIzaSyCEMOnYHopPu9cO0NVSSpEqH7WnhfzEYRM
            String sensor = "sensor=false";
            String output = "json";
            String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+latitude+","+longitude+"&"+key+"&"+sensor;
            Logger.log("address", url);
            result = url;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.log("Addresss::",e.getMessage());
        }
        return result;
    }

    public static String encodeImg1Tobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }
    public static String encodebase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded.trim();
    }

    public static boolean hasPermissionInManifest(Context context, String permissionName) {
        final String packageName = context.getPackageName();
        try {
            final PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermisisons = packageInfo.requestedPermissions;
            if (declaredPermisisons != null && declaredPermisisons.length > 0) {
                for (String p : declaredPermisisons) {
                    if (p.equals(permissionName)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return false;
    }

    public static String getDateFormate(String datetime){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null ;
        try {
            date = formatter.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
            String setDate = sdf.format(date.getTime());
            return (setDate);
        }catch (Exception e){
            return ("");
        }

    }

    public static String getDateFormatUtils(Context context, String datetime){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null ;
        try {
            date = formatter.parse(datetime);
        } catch (ParseException e) {
            date = Calendar.getInstance().getTime();
        } catch (NullPointerException e) {
            date = Calendar.getInstance().getTime();
        }

        return DateUtils.formatDateTime(context,date.getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);

    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (android.content.pm.Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static boolean isForeground(Context context) {//String PackageName
        // Get the Activity Manager
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // Get a list of running tasks, we are only interested in the last one,
        // the top most so we give a 1 as parameter so we only get the topmost.
        List<ActivityManager.RunningTaskInfo> task = manager.getRunningTasks(1);
        // Get the info we need for comparison.
        ComponentName componentInfo;
        try {
            componentInfo = task.get(0).topActivity;
        } catch (Exception e) {
            componentInfo = null;
        }
        // Check if it matches our package name.
        if (componentInfo != null && componentInfo.getPackageName().equals("com.bike4everything"))
            return true;
        // If not then our app is not on the foreground.
        return false;
    }

    public static void callPermisstion(Activity activity, int request){

        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.CALL_PHONE)) {

            } else {


                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.CALL_PHONE},
                        request);

            }
        }
    }

    public static void requestLocationPermission(Activity activity){

        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {

                Toast.makeText(activity,"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(activity,"GPS permission not allows.",Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    public static boolean checkBoundaries(LatLng tempCenter){

        LatLng INDORE_SWBOUND = new LatLng(22.6076326,75.7411194);
        LatLng INDORE_NEBOUND = new LatLng(22.8485735,75.9653950);
        LatLngBounds INDORE_BOUNDARY = new LatLngBounds(INDORE_SWBOUND, INDORE_NEBOUND);

        LatLng UJJAIN_SWBOUND = new LatLng(22.670947,75.793648);
        LatLng UJJAIN_NEBOUND = new LatLng(23.208435,75.819054);
        LatLngBounds UJJAIN_BOUNDARY = new LatLngBounds(UJJAIN_SWBOUND, UJJAIN_NEBOUND);
// || UJJAIN_BOUNDARY.contains(tempCenter)

        return INDORE_BOUNDARY.contains(tempCenter);
    }

    public static LatLngBounds getIndoreBoundary(){

        LatLng INDORE_SWBOUND = new LatLng(22.6076326,75.7411194);
        LatLng INDORE_NEBOUND = new LatLng(22.8485735,75.9653950);
        LatLngBounds INDORE_BOUNDARY = new LatLngBounds(INDORE_SWBOUND, INDORE_NEBOUND);

        return INDORE_BOUNDARY;
    }
    public static LatLngBounds getUjjainBoundary(){

        LatLng UJJAIN_SWBOUND = new LatLng(22.670947,75.793648);
        LatLng UJJAIN_NEBOUND = new LatLng(23.208435,75.819054);
        LatLngBounds UJJAIN_BOUNDARY = new LatLngBounds(UJJAIN_SWBOUND, UJJAIN_NEBOUND);
        return UJJAIN_BOUNDARY;
    }





    public static void errorDialog(Context context,String msg) {

        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(msg)
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();

                    }
                })
                .show();

    }
    public static boolean CheckGpsEnableOrNot(Context context) {
        boolean gpsStatus = false;
        try {
            LocationManager locationManager = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);
            gpsStatus = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
            gpsStatus = false;
        }
        return gpsStatus;
    }


    public static String generateCheckSum(String value){
        byte[]   bytesEncoded = org.apache.commons.codec.binary.Base64.encodeBase64((value).getBytes());
        String auth =  new String(bytesEncoded );
        System.out.println("ecncoded value is " +auth);
        return auth;
    }

    public static boolean checkPermission(Context context, String permission) {
        int result = ContextCompat.checkSelfPermission(context, permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Activity activity, String permission, int PERMISSION_REQUEST_CODE) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {

            //Toast.makeText(this,"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(activity, new String[]{permission}, PERMISSION_REQUEST_CODE);
        }
    }

    public static String getDateDiffrence(String strDate,String endDate) throws ParseException {
        SimpleDateFormat simpleDateFormat;
        Date date_current, date_diff;
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date_current = simpleDateFormat.parse(strDate);
        date_diff = simpleDateFormat.parse(endDate);

        long diff = date_diff.getTime() - date_current.getTime();
        int int_hours = 0;

        long int_timer = TimeUnit.HOURS.toMillis(int_hours);
        long long_hours = int_timer - diff;

        long diffSeconds2 = long_hours / 1000 % 60;
        long diffMinutes2 = long_hours / (60 * 1000) % 60;
        long diffHours2 = long_hours / (60 * 60 * 1000) % 24;

        String str_testing = String.format("%02d",diffHours2)
                + ":" + String.format("%02d",diffMinutes2)
                + ":" + String.format("%02d",diffSeconds2);
        return str_testing;
    }
    public static String getTotalDateDiffrence(String totalDate,String currentDate) throws ParseException {
        SimpleDateFormat simpleDateFormat;
        Date date_current, date_diff;
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date_current = simpleDateFormat.parse(totalDate);
        date_diff = simpleDateFormat.parse(currentDate);

        long diff = date_current.getTime() + date_diff.getTime();
        Date d = simpleDateFormat.parse(totalDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String[] number1 = currentDate.split(" ");
        String[] number = number1[1].split(":");
        cal.add(Calendar.HOUR, Integer.parseInt(number[0]));
        cal.add(Calendar.MINUTE, Integer.parseInt(number[1]));
        cal.add(Calendar.SECOND, Integer.parseInt(number[2]));
        String newTime = simpleDateFormat.format(cal.getTime());
      //  String str_testing = simpleDateFormat.format(date.getTime());
        Logger.log("WorkTimer 33", newTime);
        return newTime;
    }

    public static NetworkInfo getNetworkInfo(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public static boolean isConnectionFast(Context context){
        try {
            NetworkInfo networkInfo = getNetworkInfo(context);
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (networkInfo.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                        return false; // ~ 50-100 kbps
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                        return false; // ~ 14-64 kbps
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        return false; // ~ 50-100 kbps
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                        return false; // ~ 100 kbps
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        return true; // ~ 400-1000 kbps
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        return true; // ~ 600-1400 kbps
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                        return true; // ~ 2-14 Mbps
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                        return true; // ~ 700-1700 kbps
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                        return true; // ~ 1-23 Mbps
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                        return true; // ~ 400-7000 kbps
			/*
			 * Above API level 7, make sure to set android:targetSdkVersion
			 * to appropriate level to use these
			 */
                    case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                        return false; // ~25 kbps
                    case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                        return true; // ~ 1-2 Mbps
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                        return true; // ~ 5 Mbps
                    case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                        return true; // ~ 10-20 Mbps
                    case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                        return true; // ~ 10+ Mbps
                    // Unknown
                    case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    default:
                        return false;
                }
            } else {
                return false;
            }
        }catch (NullPointerException e){
            return false;
        }
    }
}
