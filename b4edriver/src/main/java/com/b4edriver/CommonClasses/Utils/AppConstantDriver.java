package com.b4edriver.CommonClasses.Utils;

import android.content.Context;
import android.content.res.Resources;

import com.b4edriver.R;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by MAX on 18-Mar-16.
 */
public class AppConstantDriver {

    public static final int DRIVER_B2C = 1;
    public static final int DRIVER_B2B = 2;

    public static final String ALLOT = "1";
    public static final String ARRIVAL = "2";
    public static final String START_TRIP = "4";
    public static final String END_TRIP = "5";
    public static final String COMPLETE = "99";
    public static final String CANCEL = "400";
    public static final String GOOGLE_API_KEY = "key=AIzaSyA7Bf-jC-8m0jWBDESWu1qqvtN7nCBiOps";

    public static final String EMAILID_SENDER = "bike4everything@gmail.com";
    public static final String EMAILID_ME = "mmfinfotech366@gmail.com";
    public static final String EMAILID_ALL = "mmfinfotech366@gmail.com,neelesh2999@gmail.com,mmfinfotech253@gmail.com";

    //private static String USER_APPURL_NEW = "http://35.166.139.95/project_work/B4E/administrator/user_api/Ondemand_User/Ondemand_Driver/";

    //private static String USER_APPURL_NEW_LIVE = "https://www.bike4everything.in/administrator/user_api/Ondemand_User/Ondemand_Driver/";
    private static String USER_APPURL_NEW_LIVE = "https://www.bike4everything.in";
    private static String USER_APPURL_NEW_DEMO = "http://192.168.29.250/B4E_Development";
    private static String USER_APPURL_LIVE_DEV = "http://34.217.209.240/B4E_Development";

    private static String DOMAIL = USER_APPURL_NEW_LIVE;

    private static String USER_APPURL_NEW = DOMAIL+ "/administrator/user_api/Ondemand_User/Ondemand_Driver/";
    private static String USER_APPURL_NEW_USER = DOMAIL+ "/administrator/user_api/Ondemand_User";


    public interface URL {

        String ONDEMAND_DRIVER_DRIVERREGISTRATION = USER_APPURL_NEW + "onDemand_Driver_driverRegistration.php";
        String ONDEMAND_USER_DRIVERTRIPLOGUPDATE = USER_APPURL_NEW + "onDemand_Driver_driverTripLogUpdate.php";
        String ONDEMAND_DRIVER_DRIVERBOOKINGPROCESS = USER_APPURL_NEW + "onDemand_Driver_driverBookingProcess.php";
        String ONDEMAND_USER_DRIVERMESSAGE = USER_APPURL_NEW + "onDemand_Driver_driverMessage.php";
        String ONDEMAND_DRIVER_DRIVERLOGIN = USER_APPURL_NEW + "onDemand_Driver_driverLogin.php";
        String ONDEMAND_DRIVER_DRIVERLOGOUT = USER_APPURL_NEW + "onDemand_Driver_driverLogout.php";
        String ONDEMAND_DRIVER_DRIVERRIDEDETAILS = USER_APPURL_NEW + "onDemand_Driver_driverRideDetails.php";
        String ONDEMAND_DRIVER_DRIVERPAYMENT = USER_APPURL_NEW + "onDemand_Driver_driverPayment.php";
        String ONDEMAND_DRIVER_DRIVERCHECKDRIVERSTATUS = USER_APPURL_NEW + "onDemand_Driver_driverCheckDriverStatus.php";
        String ONDEMAND_DRIVER_DRIVERPROFILE = USER_APPURL_NEW + "onDemand_Driver_driverProfile.php";
        String OFFLINEURL = USER_APPURL_NEW + "onDemand_Driver_driverOfflineBookingData.php";
        String ONDEMAND_DRIVER_DRIVERLOGS = USER_APPURL_NEW + "onDemand_Driver_driverLogs.php";
        String onDeand_Driver_drivertificationManage = USER_APPURL_NEW + "onDemand_Driver_driverNotificationManage.php";
        // String CHECKUSERDRIVERSTATUS = "http://35.166.139.95/project_work/B4E/administrator/user_api/checkUserDriverStatus.php";
        String CHECKUSERDRIVERSTATUS = DOMAIL+"/administrator/user_api/checkUserDriverStatus.php";


        String PAYTM_CHECKSUM_NEW = DOMAIL+"/administrator/user_api/paytm/generateChecksum_new.php";

        String ONDEMAND_USER_USERPROFILE = USER_APPURL_NEW_USER + "onDemand_User_userProfile.php";
    }

    public interface METHOD {

        /*ONDEMAND_DRIVER_DRIVERREGISTRATION*/
        String PERSONAL_DETAILS = "add_driver_details_from_app_part_one";
        String VEHICLE_DETAILS = "add_driver_details_from_app_part_two";
        String SERVICE_DETAILS = "add_driver_details_from_app_part_three";
        String SERVICESLIST = "servicesList";

        /*ONDEMAND_USER_DRIVERTRIPLOGUPDATE*/
        String TRIP_LOG = "trip_log";

        /*ONDEMAND_DRIVER_DRIVERBOOKINGPROCESS*/

        String ACCEPT_THE_TRIP = "accept_the_trip";
        String ACCEPT_BOOKING = "accept_booking";
        String ARRIVE_BOOKING = "arrive_booking";
        String START_BOOKING = "start_booking";
        String END_BOOKING = "end_booking";
        String CANCELBOOKING_DRIVER = "cancel_bookingByDriver";
        String SENDNOTIFICATIONTOALLOTHERDRIVER = "sendNotificationToAllOtherDriver";
        String TRIP_REJECT = "driverDiscardNotification";
        String TRIPDETAILSFORUSER = "tripDetailsForUser";
        String UPDATEDEVICEIDDRIVER = "updateDeviceidDriver";

        /*ONDEMAND_USER_DRIVERMESSAGE*/
        String SENDMESSAGE = "arriveMessageByDriver";

        /*ONDEMAND_DRIVER_DRIVERLOGIN*/
        String SIGNIN = "newDriverLogin";

        /*ONDEMAND_DRIVER_DRIVERLOGOUT*/
        String LOGOUT = "logout";

        /*ONDEMAND_DRIVER_DRIVERRIDEDETAILS*/
        String MYRIDE_DRIVER = "myRides_AllByDriver";

        /*ONDEMAND_DRIVER_DRIVERPAYMENT*/
        String USERBILLPAYMENTPROCESS_FROM_DRIVER_PROCESS = "userBillPaymentProcess_From_Driver_Process";
        String USERBILLPAYMENTPROCESS_FROM_DRIVER = "userBillPaymentProcess_From_Driver";
        String USERBILLPAYMENTPROCESS_FROM_DRIVER_CONFORM = "userBillPaymentProcess_From_Driver_Conform";

        /*ONDEMAND_DRIVER_DRIVERCHECKDRIVERSTATUS*/
        String CHECK_DRIVER_STATUS = "checkDriverStatus";

        /*ONDEMAND_DRIVER_DRIVERPROFILE*/
        String UPDATEPROFILE = "updateProfile";
        String SWITCH_ROLE = "switch_loginTo_UD";

        /*onDeand_Driver_drivertificationManage*/
        String drivergotNotification = "drivergotNotification";

        /*CHECKUSERDRIVERSTATUS*/
        String CHECKUSERDRIVERSTATUS = "checkUserDriverStatus";

        /*ONDEMAND_DRIVER_DRIVERLOGS*/
        String DRIVERLOGSDETAILS = "driverLogsDetails";

        String BIKE_RENT_BOOKING = "bikeRentBooking";

        String GETUSERRESETLOGINDETAILS = "getUserResetLoginDetails";
    }

    private static final String PACKAGE_NAME = "com.google.android.gms.location.activityrecognition";
    public static final String ACTIVITY_EXTRA = PACKAGE_NAME + ".ACTIVITY_EXTRA";
    public static final String BROADCAST_ACTION = PACKAGE_NAME + ".BROADCAST_ACTION";

    public static final long DETECTION_INTERVAL_IN_MILLISECONDS = 0;

    public static final int[] MONITORED_ACTIVITIES = {
            DetectedActivity.STILL,
            DetectedActivity.ON_FOOT,
            DetectedActivity.WALKING,
            DetectedActivity.RUNNING,
            DetectedActivity.ON_BICYCLE,
            DetectedActivity.IN_VEHICLE,
            DetectedActivity.TILTING,
            DetectedActivity.UNKNOWN
    };

    public static String getActivityString(Context context, int detectedActivityType) {
        Resources resources = context.getResources();
        switch(detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            default:
                return resources.getString(R.string.unidentifiable_activity, detectedActivityType);
        }
    }


}
