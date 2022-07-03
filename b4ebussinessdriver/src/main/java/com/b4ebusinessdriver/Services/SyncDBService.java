package com.b4ebusinessdriver.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.b4ebusinessdriver.Database.DatabaseHandler;
import com.b4ebusinessdriver.Model.FareCard;
import com.b4ebusinessdriver.Utils.AppConstant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SyncDBService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.b4ebusinessdriver.Services.action.FOO";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.b4ebusinessdriver.Services.extra.PARAM1";

    public SyncDBService() {
        super("SyncDBService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void syncDatabase(Context context, String[] param1) {
        for(int i=0; i<param1.length; i++) {
            Intent intent = new Intent(context, SyncDBService.class);
            intent.setAction(ACTION_FOO);
            intent.putExtra(EXTRA_PARAM1, param1[i]);
            context.startService(intent);
            Log.e("Request_Response", "1111");
        }
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                handleActionFoo(param1);
                Log.e("Request_Response", "2222");
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1) {
        // TODO: Handle action Foo
       // throw new UnsupportedOperationException("Not yet implemented");
        new SaveData(param1).execute();
    }

    class SaveData extends AsyncTask<String,Void,String> {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String method = "";
        public SaveData(String method) {
            this.method = method;
        }

        @Override
        protected String doInBackground(String ... params) {

            String result = "";
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("method", method);
                    Log.e("Request_Response", jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(AppConstant.B4E_BUSINESS_ALL_DELIVERY)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                result = response.body().string();
                Log.e("Request_Response", jsonObject.toString() + "\n" + result.toString());


            } catch (IOException e) {
                e.printStackTrace();

            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("Request_Response", AppConstant.B4E_BUSINESS_ALL_DELIVERY +"\n"+result);
            if(!result.equalsIgnoreCase("")) {
                if (method.equalsIgnoreCase(AppConstant.GET_RATECARD)) {

                    try {
                        JSONObject  jsonObject = new JSONObject(result);

                        if(jsonObject.getString("status").equalsIgnoreCase("200")){
                            FareCard fareCard = new Gson().fromJson(jsonObject.toString(), FareCard.class);
                            new DatabaseHandler(SyncDBService.this).addFareCard(fareCard);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }else  {
                if (method.equalsIgnoreCase(AppConstant.GET_RATECARD)) {
                    FareCard fareCard = new FareCard();
                    fareCard.setLimitKm("5");
                    fareCard.setBaseFare("30");
                    fareCard.setPerKmFare("8");
                    fareCard.setGst("18");
                    fareCard.setReturnFare("10");

                    JSONArray array = null;
                    try {
                        array = new JSONArray("[{\"drop_point\":\"4\", \"fare\":\"10\"},{\"drop_point\":\"7\", \"fare\":\"7\"},{\"drop_point\":\"10\", \"fare\":\"5\"},{\"drop_point\":\"above\", \"fare\":\"3\"}]");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Type listType = new TypeToken<ArrayList<FareCard.Delivery>>(){}.getType();
                    List<FareCard.Delivery> deliveryList = new Gson().fromJson(array.toString(), listType);
                    fareCard.setDeliveries(deliveryList);
                    new DatabaseHandler(SyncDBService.this).addFareCard(fareCard);
                }
            }
            stopSelf();

        }
    }

}
