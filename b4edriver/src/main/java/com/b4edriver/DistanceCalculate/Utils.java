package com.b4edriver.DistanceCalculate;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.analytics.FirebaseAnalytics.Param;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class Utils {
    private static DecimalFormat decimalFormat;
    private static DecimalFormat decimalFormatMoney;
    private static DecimalFormat decimalFormatNoDecimal;


    public static int compareDouble(double d1, double d2) {
        if (d1 == d2) {
            return 0;
        }
        if (d1 - d2 > 1.0E-7d) {
            return 1;
        }
        if (d1 - d2 < 1.0E-7d) {
            return -1;
        }
        return 0;
    }

    public static void expandListForFixedHeight(ListView list) {
        try {
            if (list.getCount() > 0) {
                View listItem = list.getAdapter().getView(0, null, list);
                listItem.measure(0, 0);
                int totalHeight = listItem.getMeasuredHeight() * list.getCount();
                LayoutParams params = list.getLayoutParams();
                params.height = (list.getDividerHeight() * (list.getCount() - 1)) + totalHeight;
                list.setLayoutParams(params);
                list.requestLayout();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void expandListForVariableHeight(ListView list) {
        try {
            if (list.getCount() > 0) {
                ListAdapter listAdap = list.getAdapter();
                int totalHeight = 0;
                for (int i = 0; i < listAdap.getCount(); i++) {
                    View listItem = listAdap.getView(i, null, list);
                    listItem.measure(0, 0);
                    totalHeight += listItem.getMeasuredHeight();
                }
                LayoutParams params = list.getLayoutParams();
                params.height = (list.getDividerHeight() * (list.getCount() - 1)) + totalHeight;
                list.setLayoutParams(params);
                list.requestLayout();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static boolean mockLocationEnabled(Location location) {
        try {

            boolean isMockLocation = false;
            if (location != null) {
                Bundle extras = location.getExtras();
                isMockLocation = !(extras == null || !extras.getBoolean(FusedLocationProviderApi.KEY_MOCK_LOCATION, false));
            }
            return isMockLocation;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean isBatteryCharging(Context context) {
        try {
            Intent batteryStatus = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
            float batteryPct = (((float) batteryStatus.getIntExtra(Param.LEVEL, -1)) / ((float) batteryStatus.getIntExtra("scale", -1))) * 100.0f;
            int status = batteryStatus.getIntExtra("status", -1);
            return status == 2 || status == 5 || batteryPct > BitmapDescriptorFactory.HUE_YELLOW;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static String getChronoTimeFromMillis(long elapsedTime) {
        long timeR = elapsedTime;
        int hR = (int) (timeR / 3600000);
        int mR = ((int) (timeR - ((long) (hR * 3600000)))) / 60000;
        int sR = ((int) ((timeR - ((long) (hR * 3600000))) - ((long) (60000 * mR)))) / 1000;
        String hhR = hR < 10 ? "0" + hR : hR + "";
        return hhR + ":" + (mR < 10 ? "0" + mR : mR + "") + ":" + (sR < 10 ? "0" + sR : sR + "");
    }

    public static void openCallIntent(Activity activity, String phoneNumber) {
        Intent callIntent = new Intent("android.intent.action.VIEW");
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        activity.startActivity(callIntent);
    }

    public static void makeCallIntent(Activity activity, String phoneNumber) {
        try {
            Intent callIntent = new Intent("android.intent.action.CALL");
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            activity.startActivity(callIntent);
        } catch (Exception e) {
            openCallIntent(activity, phoneNumber);
        }
    }

    public static String hidePhoneNoString(String phoneNo) {
        String returnPhoneNo = "";
        if (phoneNo.length() <= 0) {
            return returnPhoneNo;
        }
        int charLength = phoneNo.length();
        int stars = charLength < 4 ? 0 : charLength - 4;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < stars; i++) {
            stringBuilder.append("*");
        }
        return stringBuilder.toString() + phoneNo.substring(stars, phoneNo.length());
    }

    public static String retrievePhoneNumberTenChars(String phoneNo) {
        phoneNo = phoneNo.replace(" ", "").replace("(", "").replace("/", "").replace(")", "").replace("N", "").replace(",", "").replace("*", "").replace(";", "").replace("#", "").replace("-", "").replace(".", "");
        if (phoneNo.length() >= 10) {
            return phoneNo.substring(phoneNo.length() - 10, phoneNo.length());
        }
        return phoneNo;
    }

    public static boolean validPhoneNumber(String phoneNo) {
        boolean z = false;
        try {
            if (!(TextUtils.isEmpty(phoneNo) || phoneNo.length() < 10 || phoneNo.charAt(0) == '0' || phoneNo.charAt(0) == '1' || phoneNo.contains("+"))) {
                z = isPhoneValid(phoneNo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return z;
    }

    public static boolean isPhoneValid(CharSequence phone) {
        return Patterns.PHONE.matcher(phone).matches();
    }

    public static boolean isEmailValid(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String file : children) {
                if (!deleteDir(new File(dir, file))) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static boolean checkIfOnlyDigits(String strTocheck) {
        return strTocheck.matches("[0-9+]+");
    }

    public static DecimalFormat getDecimalFormatForMoney() {
        if (decimalFormatMoney == null) {
            decimalFormatMoney = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.ENGLISH));
        }
        return decimalFormatMoney;
    }

    public static DecimalFormat getDecimalFormat() {
        if (decimalFormat == null) {
            decimalFormat = new DecimalFormat("#.##");
        }
        return decimalFormat;
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }




    public static boolean isServiceRunning(Context context, Class serviceClass) {
        for (RunningServiceInfo service : ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String getDeviceName() {
        return (Build.MANUFACTURER + Build.MODEL).toString();
    }

    public static int getAppVersion(Context context) {
        int i = 0;
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return i;
        }
    }

    public static File getStorageDirectory(Context context) {
        if (Environment.getExternalStorageState().equals("mounted")) {
            return context.getExternalFilesDir(null);
        }
        return context.getFilesDir();
    }

    public static void saveImage(Bitmap bitmap, String saveToFile, Context context) {
        File mImagefile = new File(getStorageDirectory(context), saveToFile);
        if (mImagefile.exists()) {
            mImagefile.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(mImagefile);
            bitmap.compress(CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void enableReceiver(Context context, Class classT, boolean enable) {
        try {
            ComponentName receiver = new ComponentName(context, classT);
            PackageManager pm = context.getPackageManager();
            if (enable) {
                pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            } else {
                pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






    public static void deleteGpsData(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("all", true);
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.sendExtraCommand("gps", "delete_aiding_data", null);
        locationManager.sendExtraCommand("gps", "force_xtra_injection", bundle);
        locationManager.sendExtraCommand("gps", "force_time_injection", bundle);
    }

    public static void openNavigationIntent(Context context, LatLng latLng) {
        try {
            Intent mapIntent = new Intent("android.intent.action.VIEW", Uri.parse("google.navigation:q=" + latLng.latitude + "," + latLng.longitude));
            mapIntent.setPackage("com.google.android.apps.maps");
            context.startActivity(mapIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setDrawableColor(View view, String color, int defaultColor) {
        int intColor = defaultColor;
        if (color != null) {
            try {
                if (color.length() == 7 || color.length() == 9) {
                    intColor = Color.parseColor(color);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        Drawable background = view.getBackground();
        if (background instanceof ShapeDrawable) {
            ((ShapeDrawable) background).getPaint().setColor(intColor);
        } else if (background instanceof GradientDrawable) {
            ((GradientDrawable) background).setColor(intColor);
        } else if (background instanceof ColorDrawable) {
            ((ColorDrawable) background).setColor(intColor);
        }
    }

    public static void setTextColor(View view, String color, int defaultColor) {
        int intColor = defaultColor;
        if (color != null) {
            try {
                if (color.length() == 7 || color.length() == 9) {
                    intColor = Color.parseColor(color);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(intColor);
        }
    }

    public static Bitmap setBitmapColor(Bitmap sourceBitmap, String color, int defaultColor) {
        int intColor = defaultColor;
        if (color != null && (color.length() == 7 || color.length() == 9)) {
            intColor = Color.parseColor(color);
        }
        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth() - 1, sourceBitmap.getHeight() - 1);
        Paint p = new Paint();
        p.setColorFilter(new LightingColorFilter(intColor, 1));
        new Canvas(resultBitmap).drawBitmap(resultBitmap, 0.0f, 0.0f, p);
        return resultBitmap;
    }

    public static byte[] gzipCompress(String string) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
        GZIPOutputStream gos = new GZIPOutputStream(os);
        gos.write(string.getBytes());
        gos.close();
        byte[] compressed = os.toByteArray();
        os.close();
        return compressed;
    }

    public static String gzipDecompress(byte[] compressed) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(is, 32);
        StringBuilder string = new StringBuilder();
        byte[] data = new byte[32];
        while (true) {
            int bytesRead = gis.read(data);
            if (bytesRead != -1) {
                string.append(new String(data, 0, bytesRead));
            } else {
                gis.close();
                is.close();
                return string.toString();
            }
        }
    }


    /*public static void clearApplicationData(Context context) {
        if (System.currentTimeMillis() - Prefs.with(context).getLong(SPLabels.CLEAR_APP_CACHE_TIME, 0) > 604800000) {
            Prefs.with(context).save(SPLabels.CLEAR_APP_CACHE_TIME, System.currentTimeMillis());
            File appDir = new File(context.getCacheDir().getParent());
            if (appDir.exists()) {
                for (String s : appDir.list()) {
                    if (!s.equals("lib")) {
                        deleteAppData(new File(appDir, s));
                        Log.m616i("TAG", "File /data/data/APP_PACKAGE/" + s + " DELETED");
                    }
                }
            }
        }
    }*/

    public static boolean deleteAppData(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String file : children) {
                if (!deleteDir(new File(dir, file))) {
                    return false;
                }
            }
        }
        return dir.delete();
    }


    public static File compressToFile(Context context, Bitmap src, CompressFormat format, int quality, int index) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(format, quality, os);
        File f = new File(context.getExternalCacheDir(), "temp" + index + ".jpg");
        try {
            f.createNewFile();
            byte[] bitmapdata = os.toByteArray();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / ((float) width);
        float scaleHeight = ((float) newHeight) / ((float) height);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }


    public static DecimalFormat getDecimalFormatNoDecimal() {
        if (decimalFormatNoDecimal == null) {
            decimalFormatNoDecimal = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.ENGLISH));
        }
        return decimalFormatNoDecimal;
    }


    public static int dpToPx(Context context, float dp) {
        int temp = (int) dp;
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }



    public static String getActivityName(Context context) {
        String mPackageName = "";
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (VERSION.SDK_INT > 20) {
            return mActivityManager.getRunningAppProcesses().get(0).processName;
        }
        return mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
    }

    public static CharSequence trimHTML(CharSequence s) {
        if (s.length() == 0) {
            return "";
        }
        int start = 0;
        int end = s.length();
        while (start < end && Character.isWhitespace(s.charAt(start))) {
            start++;
        }
        while (end > start && Character.isWhitespace(s.charAt(end - 1))) {
            end--;
        }
        return s.subSequence(start, end);
    }

    public static Spanned fromHtml(String html) {
        return Html.fromHtml(html);
    }

    public static String getKilometers(double kilometer, Context context) {
        return getDecimalFormatNoDecimal().format(kilometer) + " " + (kilometer > 1.0d ? "kms" : "km");
    }



}
