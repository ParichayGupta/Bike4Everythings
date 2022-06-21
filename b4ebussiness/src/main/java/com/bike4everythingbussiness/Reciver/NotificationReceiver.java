package com.bike4everythingbussiness.Reciver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bike4everythingbussiness.Activity.SigninActivity;
import com.bike4everythingbussiness.Utils.AppConstant;


/**
 * Created by manishsingh on 11/01/18.
 */

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();
         if (AppConstant.ACTION_LOGIN.equals(action)) {

            cancelNotification(context, 1);
            Intent intent1 = new Intent(context, SigninActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
    }

    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }
}
