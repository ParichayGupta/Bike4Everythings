package com.bike4everythingbussiness.Reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by MAX on 08-Jun-16.
 */
public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiverSms";

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (Object aPdusObj : pdusObj) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    String senderAddress = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    Log.e(TAG, "Received SMS: " + message + ", Sender: " + senderAddress);

                    // if the SMS is not from our gateway, ignore the message
                    if (!senderAddress.contains("BikeFE")) {
                        return;
                    }

                    // verification code from sms
                    if(message.contains("otp")){
                        String verificationCode = getVerificationCode(message);
                        Log.e(TAG, "OTP received: " + verificationCode);
                        sendMessage(verificationCode);
                    }


                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Getting the OTP from sms message body
     * ':' is the separator of OTP from the message
     *
     * @param message
     * @return
     */
    private String getVerificationCode(String message) {
        String code = "";

        code = message.replaceAll("\\D+","");

        int index= code.lastIndexOf("4");

        code = code.substring(0,index);

        return code;
    }


    private void sendMessage(String msg) {
        Intent intent = new Intent("OTPActivity");
        intent.putExtra("code", msg);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}