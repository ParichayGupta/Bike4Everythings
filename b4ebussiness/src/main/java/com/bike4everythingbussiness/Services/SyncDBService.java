package com.bike4everythingbussiness.Services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bike4everythingbussiness.DatabaseHandler;
import com.bike4everythingbussiness.Model.DropAddress;
import com.bike4everythingbussiness.Model.FareCard;
import com.bike4everythingbussiness.Model.OrderDetails;
import com.bike4everythingbussiness.Model.PickAddress;
import com.bike4everythingbussiness.Utils.AppConstant;
import com.bike4everythingbussiness.Utils.AppPreferance;
import com.bike4everythingbussiness.Utils.Config;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
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
    private static final String ACTION_FOO = "com.bike4everythingbussiness.Services.action.FOO";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.bike4everythingbussiness.Services.extra.PARAM1";

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
                    jsonObject.put("business_id", AppPreferance.getUserid(SyncDBService.this));

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
                if (method.equalsIgnoreCase(AppConstant.GET_ALLTRIP)) {

                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if(jsonObject.getString("status").equalsIgnoreCase("200")){
                            JSONArray array = jsonObject.getJSONArray("result");
                            new DatabaseHandler(SyncDBService.this).deleteAllOrder();
                            for(int i=0; i<array.length(); i++){
                                JSONObject object = array.getJSONObject(i);
                                OrderDetails orderDetails = new Gson().fromJson(object.toString(), OrderDetails.class);
                                if (!orderDetails.getSchedule().equalsIgnoreCase("")) {
                                    orderDetails.setOrderStatus(Config.ORDER_PENDING);
                                } else if (orderDetails.getDeliveryStatus().equalsIgnoreCase("Complete") ||
                                        orderDetails.getDeliveryStatus().equalsIgnoreCase("Cancelled")) {
                                    orderDetails.setOrderStatus(Config.ORDER_COMPLETED);
                                } else {
                                    orderDetails.setOrderStatus(Config.ORDER_ONGOING);
                                }
                                if(new DatabaseHandler(SyncDBService.this).isExitsOrderId(orderDetails.getDeliveryId())){
                                    new DatabaseHandler(SyncDBService.this).updateOrderStatus(orderDetails.getDeliveryId(), orderDetails.getOrderStatus(),orderDetails.getDeliveryStatus());
                                }else {

                                    new DatabaseHandler(SyncDBService.this).addOrder(orderDetails);
                                }
                            }


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent("onUpdate");
                    // You can also include some extra data.
                    intent.putExtra("message", "update");
                    LocalBroadcastManager.getInstance(SyncDBService.this).sendBroadcast(intent);

                }else  if (method.equalsIgnoreCase(AppConstant.GET_RATECARD)) {

                    try {
                        JSONObject  jsonObject = new JSONObject(result);

                        if(jsonObject.getString("status").equalsIgnoreCase("200")){
                            FareCard fareCard = new Gson().fromJson(jsonObject.toString(), FareCard.class);
                            //Logger.log("fareCard", Arrays.toString(fareCard.getDeliveries().toArray()));
                            if(fareCard.getDeliveries() != null){
                                new DatabaseHandler(SyncDBService.this).addFareCard(fareCard);
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if (method.equalsIgnoreCase(AppConstant.GET_ALL_PICKUP_ADDRESS)) {
                    new DatabaseHandler(SyncDBService.this).deleteAllPickupAddress();
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        if(jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray array = jsonObject.getJSONArray("result");
                            for(int i=0; i<array.length(); i++){
                                JSONObject object = array.getJSONObject(i);
                                PickAddress pickAddress = new Gson().fromJson(object.toString(), PickAddress.class);
                                if(i==0) {
                                    pickAddress.setSelect(true);
                                }
                                new DatabaseHandler(SyncDBService.this).addPickupAddress(pickAddress);
                            }


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (method.equalsIgnoreCase("aaaa")) {
                    DropAddress dropAddress = new DropAddress();
                    new DatabaseHandler(SyncDBService.this).addDropAddress(dropAddress);
                }


            }else  {
                /*if (method.equalsIgnoreCase(AppConstant.GET_RATECARD)) {
                    FareCard fareCard = new FareCard();
                    fareCard.setLimitKm("5");
                    fareCard.setBaseFare("30");
                    fareCard.setPerKmFare("8");
                    fareCard.setGst("18");
                    fareCard.setReturnFare("10");
                    try {
                        fareCard.setDeliveries((List<FareCard.Delivery>) new JSONArray("[{\"drop_point\":\"4\", \"fare\":\"10\"},{\"drop_point\":\"7\", \"fare\":\"7\"},{\"drop_point\":\"10\", \"fare\":\"5\"},{\"drop_point\":\"above\", \"fare\":\"3\"}]"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new DatabaseHandler(SyncDBService.this).addFareCard(fareCard);

                }*/
            }
            stopSelf();

        }
    }

}
