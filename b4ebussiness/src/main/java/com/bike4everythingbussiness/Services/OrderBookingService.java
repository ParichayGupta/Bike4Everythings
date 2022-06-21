package com.bike4everythingbussiness.Services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.bike4everythingbussiness.DatabaseHandler;
import com.bike4everythingbussiness.Model.OrderDetails;
import com.bike4everythingbussiness.Utils.AppConstant;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

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
public class OrderBookingService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_NEWORDER = "com.bike4everythingbussiness.Services.action.NEWORDER";
    private static final String ACTION_BAZ = "com.bike4everythingbussiness.Services.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_NEWORDER = "com.bike4everythingbussiness.Services.extra.NEWORDER";
    private static final String EXTRA_ORDERID = "com.bike4everythingbussiness.Services.extra.ORDERID";
    private static final String EXTRA_PARAM2 = "com.bike4everythingbussiness.Services.extra.PARAM2";

    public OrderBookingService() {
        super("OrderBookingService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startNewOrder(Context context, OrderDetails orderDetails) {
        Intent intent = new Intent(context, OrderBookingService.class);
        intent.setAction(ACTION_NEWORDER);
        intent.putExtra(EXTRA_NEWORDER, orderDetails);
       // intent.putExtra(EXTRA_ORDERID, orderid);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, OrderBookingService.class);
        intent.setAction(ACTION_BAZ);
        //intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_NEWORDER.equals(action)) {
                final OrderDetails orderDetails = intent.getParcelableExtra(EXTRA_NEWORDER);
              //  final int orderid = intent.getIntExtra(EXTRA_ORDERID, 0);
                handleActionFoo(orderDetails);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM2);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(OrderDetails orderDetails) {
        // TODO: Handle action Foo
        new OrderNow(orderDetails).execute();
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    class OrderNow extends AsyncTask<Void,Void,String>{
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OrderDetails orderDetails;


        public OrderNow(OrderDetails orderDetails) {
            this.orderDetails = orderDetails;
        }

        @Override
        protected String doInBackground(Void... voids) {

            String result = "";
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("method", AppConstant.BUSINESS_CREATE_DELIVERY);
                    jsonObject.put("business_id", orderDetails.getUserId());
                    jsonObject.put("delivery_id", orderDetails.getDeliveryId());
                    jsonObject.put("schedule",orderDetails.getSchedule());

                    jsonObject.put("pickName",orderDetails.getPickName());
                    jsonObject.put("pickMobile",orderDetails.getPickMobile());
                    jsonObject.put("pickAddress",orderDetails.getPickAddress());
                    jsonObject.put("pickAddressName",orderDetails.getPickAddressName());
                    jsonObject.put("pickLat",orderDetails.getPickLat());
                    jsonObject.put("pickLng",orderDetails.getPickLng());


                    JSONArray dropArray = new JSONArray();
                    for(int i=0; i<orderDetails.getDropAddressList().size(); i++) {
                        JSONObject dropObject = new JSONObject();
                        dropObject.put("dropName", orderDetails.getDropAddressList().get(i).getDropName());
                        dropObject.put("dropMobile", orderDetails.getDropAddressList().get(i).getDropMobile());
                        dropObject.put("dropAddress", orderDetails.getDropAddressList().get(i).getDropAddress());
                        dropObject.put("dropAmount", orderDetails.getDropAddressList().get(i).getDropAmount());
                        dropObject.put("dropLat", orderDetails.getDropAddressList().get(i).getDropLat());
                        dropObject.put("dropLng", orderDetails.getDropAddressList().get(i).getDropLng());
                        dropArray.put(dropObject);
                    }

                    jsonObject.put("dropAddressList",dropArray);
                    jsonObject.put("note",orderDetails.getNote());
                    jsonObject.put("returnRequired",orderDetails.isReturnrequired());
                    jsonObject.put("fcm_token_id", FirebaseInstanceId.getInstance().getToken());
                    Log.e("Request_Response", jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(AppConstant.B4E_BUSINESS_CREATE_DELIVERY)
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
            if(!result.equalsIgnoreCase("")){
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.getString("status").equalsIgnoreCase("200")){
                        new DatabaseHandler(OrderBookingService.this).updateOrderSelect(Integer.parseInt(orderDetails.getDeliveryId()),
                                jsonObject.getString("delivery_id"), true);
                    }else{

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            stopSelf();
        }
    }
}
