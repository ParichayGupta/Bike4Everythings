package com.b4edriver.CommonClasses.BroadCastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.b4elibrary.Logger;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySMSBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
           // Logger.log("respo ", status.toString());
            switch(status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // Get SMS message contents
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    // Extract one-time code from the message and complete verification
                    // by sending the code back to your server.

                    String code = parseCode(message);
                    if(!TextUtils.isEmpty(code)){
                       sendOtp(context, code);
                    }
                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...
                    Logger.log("respo", "timeout");
                    break;
            }
        }
    }

    public String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{6}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        if(TextUtils.isEmpty(code)){
            Pattern p1 = Pattern.compile("\\b\\d{5}\\b");
            Matcher m1 = p1.matcher(message);
            while (m1.find()) {
                code = m1.group(0);
            }
            return code;
        }
        return code;
    }
    private void sendOtp(Context context, String otp) {
        Intent intent = new Intent("OTPActivity");
        intent.putExtra("code", otp);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
