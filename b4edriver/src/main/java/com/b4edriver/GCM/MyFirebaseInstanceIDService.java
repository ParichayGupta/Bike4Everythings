package com.b4edriver.GCM;

import android.util.Log;

import com.b4edriver.CommonClasses.ServerConnection.JSONParser;
import com.b4edriver.CommonClasses.ServerConnection.ServerErrorCallBack;
import com.b4edriver.CommonClasses.ServerConnection.Util;
import com.b4edriver.CommonClasses.ServerConnection.VolleyCallBack;
import com.b4edriver.CommonClasses.Utils.AppConstantDriver;
import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.b4edriver.b4edrivers.AppPreferences;
import com.b4elibrary.Logger;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MAX on 20-Sep-16.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Logger.log(TAG, "Refreshed token: " + refreshedToken);

      /*  try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.

        if(AppPreferencesDriver.getDriverId(MyFirebaseInstanceIDService.this)!=0){
            updateGcmId(token);
        }
    }

    private void updateGcmId(String token)  {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("method", AppConstantDriver.METHOD.GETUSERRESETLOGINDETAILS);
            jsonObject.put("user_id", AppPreferencesDriver.getDriverId(MyFirebaseInstanceIDService.this));
            jsonObject.put("device_id",token);

            JSONParser jsonParser = new JSONParser(MyFirebaseInstanceIDService.this);
            jsonParser.parseVollyJSONObject(AppConstantDriver.URL.ONDEMAND_USER_USERPROFILE, 1, jsonObject, "", new VolleyCallBack() {

                @Override
                public void success(String response) {
                    if (response != null) {
                        try {
                            JSONObject jsonObject1 = new JSONObject(response);
                            if (jsonObject1.getString("status").equalsIgnoreCase("200")) {
                                Logger.log("Message", jsonObject1.getString("message"));
                            } else {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new ServerErrorCallBack() {
                @Override
                public void error(String response) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }
}
