package com.bike4everythingbussiness.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bike4everythingbussiness.Adapter.WalletAdapter;
import com.bike4everythingbussiness.Model.Wallet;
import com.bike4everythingbussiness.R;
import com.bike4everythingbussiness.Utils.AppConstant;
import com.bike4everythingbussiness.Utils.AppPreferance;
import com.bike4everythingbussiness.Utils.Logger;
import com.bike4everythingbussiness.Utils.PaytmEnvironment;
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
import okhttp3.Response;

public class WalletActivity extends BaseActivity implements WalletAdapter.OnItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    WalletAdapter walletAdapter;
    PaytmEnvironment paytmEnvironment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        paytmEnvironment = PaytmEnvironment.LIVE;

        try {
            walletAdapter = new WalletAdapter(WalletActivity.this, new GetWallet().execute().get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        walletAdapter.setListener(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(WalletActivity.this);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setAdapter(walletAdapter);
        recyclerview.setNestedScrollingEnabled(true);



    }

    @Override
    public void onClick(Wallet wallet) {
            Intent intent = new Intent(WalletActivity.this, AddWalletActivity.class);
            intent.putExtra("title", "Add B4E Wallet");
            intent.putExtra("amount",wallet.getWallet());
            intent.putExtra("ssotoken",wallet.getSsoToken());
            intent.putExtra("number","");
            startActivityForResult(intent, 3);
    }

    @Override
    public void onClickPaytm(Wallet wallet) {
        if(wallet.getSsoToken().equalsIgnoreCase("0")){
            Intent intent = new Intent(WalletActivity.this, PaytmWalletActivity.class);
            startActivityForResult(intent, 1);
        }else{

            //new ValidateToken().execute(wallet.getSsoToken());
            Intent intent = new Intent(WalletActivity.this, AddWalletActivity.class);
            intent.putExtra("title", "Add Paytm Wallet");
            intent.putExtra("amount",wallet.getWallet());
            intent.putExtra("ssotoken",wallet.getSsoToken());
            intent.putExtra("number",wallet.getMobileNo());
            startActivityForResult(intent, 2);
        }
    }


    private class GetWallet extends AsyncTask<Void, Void, List<Wallet>>{

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected List<Wallet> doInBackground(Void... voids) {
            List<Wallet> walletList = new ArrayList<>();
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject data = new JSONObject();
                data.put("method", AppConstant.BUSINESS_WALLET);
                data.put("business_id", AppPreferance.getUserid(WalletActivity.this));

                Log.e("Response_Response", AppConstant.B4E_BUSINESS_WALLET + "\n" + data.toString());
                //json.put("notification", dataJson);
                RequestBody body = RequestBody.create(JSON, data.toString());
                Request request = new Request.Builder()
                        .url(AppConstant.B4E_BUSINESS_WALLET)
                        .post(body)
                        .build();
                okhttp3.Response response = client.newCall(request).execute();
                String result = response.body().string();

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.getString("status").equalsIgnoreCase("200")) {


                        JSONArray array = jsonObject.getJSONArray("result");
                        for (int i = 0; i < array.length(); i++) {
                            Wallet wallet = new Gson().fromJson(array.getJSONObject(i).toString(), Wallet.class);
                            walletList.add(wallet);

                        }

                    } else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            }

            return walletList;
        }
    }



    private class GetPaytmBalance extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject data = new JSONObject();
                data.put("TOKEN",params[0]);
                data.put("MID", paytmEnvironment.getMID());

                Log.e("Response_Response", paytmEnvironment.getWalletUrl()+"oltp/HANDLER_INTERNAL/checkBalance?JsonData="+data.toString());

                Request request = new Request.Builder()
                        .url(paytmEnvironment.getWalletUrl()+"oltp/HANDLER_INTERNAL/checkBalance?JsonData="+data.toString())
                        .get()
                        .build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();

                try {
                    JSONObject object = new JSONObject(result);
                    Logger.log("PAYTMWALLET", object.toString());
                    String paytmBal = object.getString("WALLETBALANCE");

                    new AddWalletToServer().execute(params[0], params[1], params[2], paytmBal);
                   return paytmBal;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(String paytmBal) {
            super.onPostExecute(paytmBal);
           // walletAdapter.updateWallet(TextUtils.isEmpty(paytmBal) ? "0": paytmBal, "Paytm");

        }
    }

    private class AddWalletToServer extends AsyncTask<String, Void, Void>{

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... params) {
            List<Wallet> walletList = new ArrayList<>();
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject data = new JSONObject();
                data.put("method", AppConstant.ADD_WALLET_ACCOUNT);
                data.put("business_id", AppPreferance.getUserid(WalletActivity.this));
                data.put("mobile_no", params[2]);
                data.put("state", params[1]);
                data.put("access_token", params[0]);
                data.put("walletBalance", params[3]);

                Log.e("Response_Response", AppConstant.B4E_BUSINESS_WALLET + "\n" + data.toString());
                //json.put("notification", dataJson);
                RequestBody body = RequestBody.create(JSON, data.toString());
                Request request = new Request.Builder()
                        .url(AppConstant.B4E_BUSINESS_WALLET)
                        .post(body)
                        .build();
                okhttp3.Response response = client.newCall(request).execute();
                String result = response.body().string();



            } catch (JSONException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void wallets) {
            super.onPostExecute(wallets);

            Intent intent = getIntent();
            startActivity(intent);
            finish();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case 1:
                    new GetPaytmBalance().execute(data.getStringExtra("token"),data.getStringExtra("state")
                            ,data.getStringExtra("mobile"));

                    break;
                case 2:
                    Intent intent1 = getIntent();
                    startActivity(intent1);
                    finish();
                    break;
                case 3:
                    Intent intent2 = getIntent();
                    startActivity(intent2);
                    finish();
                    break;
            }
        }
    }
}
