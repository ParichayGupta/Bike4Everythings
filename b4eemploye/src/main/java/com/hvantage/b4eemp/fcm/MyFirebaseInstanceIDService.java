package com.hvantage.b4eemp.fcm;

import android.os.AsyncTask;
import android.util.Log;

import com.b4elibrary.Logger;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.hvantage.b4eemp.utils.AppConstants;
import com.hvantage.b4eemp.utils.AppPreferance;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by MAX on 20-Sep-16.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";


    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Logger.log(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("method", AppConstants.RENT_EMPLOYEE_FCM);
            jsonObject.put("user_id", AppPreferance.getUserid(MyFirebaseInstanceIDService.this));
            jsonObject.put("fcm_id", token);

            new UpdateFcmId(jsonObject).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private class UpdateFcmId extends AsyncTask<String, String, String> {

        private JSONObject jsonObject;

        public UpdateFcmId(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.e("Request_Response", AppConstants.REGISTER_LOG_API + "\n" + jsonObject.toString());

            try {
                OkHttpClient client = new OkHttpClient();
                final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(AppConstants.REGISTER_LOG_API)
                        .post(body)
                        .build();
                okhttp3.Response response = client.newCall(request).execute();
                //String result = response.body().string();

                return "";

            } catch (IOException e) {
                e.printStackTrace();

            }
            return "";
        }


    }


}
