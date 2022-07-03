package com.b4ebusinessdriver.Services;


import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


/**
 * Created by Peter on 17-Jul-17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseIdService";


    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);
       // sendLocation(refreshedToken);

    }

 /*   private void sendLocation(final String refreshToken) {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        new AsyncTask<String, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                String result = "";
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject data = new JSONObject();
                   // data.put("method", AppConstant.UPDATE_GEOLOCATION_FCM);
                   // data.put("staff_id", AppPreferences.getUserId(getApplicationContext()));
                   // data.put("fcm_token_id", refreshToken);

                    Log.e("Request sendLocation", data.toString());
                    //json.put("notification", dataJson);
                    RequestBody body = RequestBody.create(JSON, data.toString());
                    Request request = new Request.Builder()
                            .url(AppConstant.STAFF_SIGNIN_PROCESS)
                            .post(body)
                            .build();
                    okhttp3.Response response = client.newCall(request).execute();
                    result = response.body().string();


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String finalResponse) {
                super.onPostExecute(finalResponse);
                Log.e("Response sendLocation", finalResponse);

            }
        }.execute("", "");
    }*/


}
