package com.b4edriver.GCM;

public interface ConfigDriver {

	// Google Project Number


	String GOOGLE_PROJECT_ID = "972821667542";
	String MESSAGE = "message";
	String DRIVER_APPROVAL = "approval";
	String NEW_TRIP = "newTrip";
	String CANCELTRIPBYUSER = "cancelByUser";
	String CANCELTRIPBYDRIVER = "cancelByDriver";
	String RECIVE_MESSAGE = "userMessage";
	String CANCELTRIPAUTO = "cancelTripAuto";
	String USERPAY = "userPay";
	String OTHER_SERVICE_PAY = "other_service_pay";
	String TRIPACCEPTCANCEL = "tripAcceptCancel";
	String TRIPCANCELBYADMIN = "tripCancelByAdmin";
	String ITEMDETAILSUPDATEDBYUSER = "orderDetails";
	String NOTIFYDRIVERTOENDTRIP = "notifyDrivertoEndTrip";
	String TRIPLOGNOTIFY = "triplogNotify";
	String gcmNotificationError = "gcmNotificationError";
	String loginSessionExpire = "loginSessionExpire";

	int OUTOFZONE_ID = 10;
	int NEW_TRIP_ID = 11;
	int TRIP_RECIVE_MSG_ID = 12;
	int NOTIFICATION_ID = 1;


}
