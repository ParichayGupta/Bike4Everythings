package com.b4ebusinessdriver.Reciver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.b4ebusinessdriver.Activity.AlertActivity;
import com.b4ebusinessdriver.Activity.HomeActivity;
import com.b4ebusinessdriver.Activity.SigninActivity;
import com.b4ebusinessdriver.Activity.SplashActivity;
import com.b4ebusinessdriver.Utils.AppConstant;
import com.b4ebusinessdriver.Utils.AppPreferences;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by manishsingh on 11/01/18.
 */

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();
        if (AppConstant.ACTION_ACCEPT.equals(action)) {

            cancelNotification(context, 1);


            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("method", AppConstant.ACCEPT_DELIVERY);
                jsonObject.put("driver_id", AppPreferences.getUserId(context));
                jsonObject.put("delivery_id", intent.getStringExtra("delivery_id"));
                jsonObject.put("fcm_token_id", FirebaseInstanceId.getInstance().getToken());
                new AcceptTask(context,jsonObject).execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }



        } else  if (AppConstant.ACTION_REJECT.equals(action)) {

            cancelNotification(context, 1);
        } else  if (AppConstant.ACTION_LOGIN.equals(action)) {

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


    class AcceptTask extends AsyncTask<Void, Void, String> {

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject;
        Context context;

        public AcceptTask(Context context,JSONObject jsonObject) {
            this.context = context;
            this.jsonObject = jsonObject;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            try {
                OkHttpClient client = new OkHttpClient();

                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(AppConstant.B4E_DRIVER_BUSINESS_MANAGE_DELIVERY)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                result = response.body().string();
                Log.e("Request_Response", jsonObject.toString() + "\n" + result.toString());

                return result;

            } catch (IOException e) {
                e.printStackTrace();

            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            if (!s.equalsIgnoreCase("")) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    JSONArray array = jsonObject.getJSONArray("result");
                    JSONObject object = array.getJSONObject(0);
                    if (jsonObject.getString("status").equalsIgnoreCase("200")) {


                        Intent intent1 = new Intent(context, HomeActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent1);

                    } else {
                        String msg = object.getString("msg");
                      //  showAlertDialog(SigninActivity.this,"Under Review",msg, "Ok");
                        Intent intent = new Intent(context, AlertActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("message", msg);
                        context.startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
