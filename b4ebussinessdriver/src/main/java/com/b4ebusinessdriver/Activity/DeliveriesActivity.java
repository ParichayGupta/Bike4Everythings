package com.b4ebusinessdriver.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.b4ebusinessdriver.Adapter.DeliveriesAdapter;
import com.b4ebusinessdriver.Database.DatabaseHandler;
import com.b4ebusinessdriver.Fragment.PastFragment;
import com.b4ebusinessdriver.Model.OrderDetails;
import com.b4ebusinessdriver.R;
import com.b4ebusinessdriver.Utils.AppConstant;
import com.b4ebusinessdriver.Utils.AppPreferences;
import com.b4ebusinessdriver.Utils.ProgressDialog;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class DeliveriesActivity extends BaseActivity implements DeliveriesAdapter.OnItemClickListener {


    DeliveriesAdapter deliveriesAdapter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliveries);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            deliveriesAdapter = new DeliveriesAdapter(DeliveriesActivity.this, new GetData().execute().get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        deliveriesAdapter.setListener(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(DeliveriesActivity.this);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setAdapter(deliveriesAdapter);
        recyclerview.setNestedScrollingEnabled(true);
    }

    @Override
    public void onClick() {

    }

    private class GetData extends AsyncTask<Integer, Void, List<OrderDetails>> {

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //ProgressDialog.getInstance(DeliveriesActivity.this).show();

        }

        @Override
        protected List<OrderDetails> doInBackground(Integer... params) {
            List<OrderDetails> orderDetails = new ArrayList<>();
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject data = new JSONObject();
                data.put("method", AppConstant.GET_COMPLETE_ALLTRIP);
                data.put("driver_id", AppPreferences.getUserId(DeliveriesActivity.this));

                Log.e("Response_Response", AppConstant.B4E_DRIVER_BUSINESS_ALL_DELIVERY + "\n" + data.toString());
                //json.put("notification", dataJson);
                RequestBody body = RequestBody.create(JSON, data.toString());
                Request request = new Request.Builder()
                        .url(AppConstant.B4E_DRIVER_BUSINESS_ALL_DELIVERY)
                        .post(body)
                        .build();
                okhttp3.Response response = client.newCall(request).execute();
                String result = response.body().string();
                Log.d("Response_Response", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.getString("status").equalsIgnoreCase("200")) {

//                    isStart = jsonObject.getBoolean("isStart");

                        JSONArray array = jsonObject.getJSONArray("result");
                        for (int i = 0; i < array.length(); i++) {
                            OrderDetails orderDetail = new Gson().fromJson(array.getJSONObject(i).toString(), OrderDetails.class);
                            orderDetails.add(orderDetail);
                        }

                    } else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                FirebaseCrash.report(e);
            } catch (IOException e) {
                e.printStackTrace();
                FirebaseCrash.report(e);
            }

            //ProgressDialog.getInstance(DeliveriesActivity.this).dismiss();
            return orderDetails;
        }
    }
}
