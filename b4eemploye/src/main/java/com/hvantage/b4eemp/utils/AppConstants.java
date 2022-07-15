package com.hvantage.b4eemp.utils;

public class AppConstants {
    public static int ALOTED = 0;
    public static int RUNNING = 1;
    public static int COMPLETED = 2;

    private static String LIVE = "https://www.bike4everything.in";
    private static String DEMO = "http://192.168.29.250/B4E_Development";

    private static String  DONAIM = LIVE+"/administrator/user_api/Ondemand_RENTAPP/";

    public static String  BASEURL = DONAIM;

    public static final String TRACKING_SERVER = "http://bike4everything.in:8082";
    //public static final String TRACKING_SERVER = "http://192.168.29.200:8082";

    public static final String GOOGLE_API_KEY = "AIzaSyA7Bf-jC-8m0jWBDESWu1qqvtN7nCBiOps";
    /*Pages*/
    public static final String  RENT_TRIP_BOOK_API = BASEURL +"Rent_Trip_Book_Api.php";
    public static final String  REGISTER_LOG_API = BASEURL +"Register_Log_Api.php";
    public static final String  RENT_TRIP_BOOK_UPDATE = BASEURL +"Rent_Trip_Book_Update.php";
    public static final String  RENT_FREE_BIKES = BASEURL +"Rent_Free_Bikes.php";
    public static final String RENT_BOOKINGS = BASEURL + "Rent_bookings.php";


    /*Method*/
    public static final String  GETALLRENTCURRENTBOOKING = "getAllRentCurrentBooking";
    public static final String  GETALLRENTRUNNINGBOOKING = "getAllRentRunningBooking";
    public static final String  GETALLRENTCOMPLETEBOOKING = "getAllRentCompleteBooking";
    public static final String  ALLOTRENTBOOKING = "AllotRentBooking";
    public static final String  COMPLETERENTBOOKING = "CompleteRentBooking";
    public static final String  GETFREEBIKES = "getFreeBikes";
    public static final String  SENDOTP = "sendOTP";
    public static final String  RENT_LOGIN_EMPLOYEE = "Rent_Login_Employee";
    public static final String  RENT_EMPLOYEE_FCM = "Rent_Employee_FCM";
    public static final String  UPDATERENTBOKINGABORT = "updateRentBokingAbort";
    public static final String  UPDATERENTBOKINGDROP = "updateRentBokingDrop";
    public static final String  UPDATERENTBOKINGCOMPLETE = "updateRentBokingComplete";
    public static final String  FREE_BIKES = "Free_Bikes";
    public static final String  MYRENT_ALLBYTRIP = "myRent_AllByTrip";


}
