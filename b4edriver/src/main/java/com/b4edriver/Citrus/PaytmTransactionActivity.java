package com.b4edriver.Citrus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.b4edriver.CommonClasses.Utils.AppConstantDriver;
import com.b4edriver.Database.DBAdapter_Driver;
import com.b4edriver.Model.UserDriver;
import com.b4edriver.R;
import com.b4elibrary.Logger;
import com.google.gson.Gson;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PaytmTransactionActivity extends AppCompatActivity {
    public static int PAYTMTRANSACTIONREQUESTCODE = 1323;
    UserDriver user;
    private String TAG = "PaytmTransactionActivity";
    TextView text;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paytm_transaction);

        text = findViewById(R.id.text);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        user = new DBAdapter_Driver(PaytmTransactionActivity.this).getUser();

        PaytmEnvironment paytmEnvironment = PaytmEnvironment.DEMO;

        final PaytmPGService Service = PaytmPGService.getStagingService();

        String ORDER_ID = getOrderId();
        String CUST_ID = "Cust_ID"+String.valueOf(user.getId()+ new Random().nextInt(1000));
        String TXN_AMOUNT = getIntent().getStringExtra("txn_amount");
        String EMAIL = user.getEmailId();
        String MOBILE_NO = paytmEnvironment.isLive() ? user.getPhone() : "7777777777";

        String MID = paytmEnvironment.getMID();
        final String MercahntKey = paytmEnvironment.getMerchantKey();
        String INDUSTRY_TYPE_ID = paytmEnvironment.getIndustryTypeId();
        String CHANNLE_ID = paytmEnvironment.getChannelId();
        String WEBSITE = paytmEnvironment.getWebsite();
        String CALLBACK_URL = paytmEnvironment.getCallBackUrl();

        final HashMap<String,String> paramMap = new HashMap<String,String>();
        paramMap.put("MID" , MID);
        paramMap.put("ORDER_ID" , ORDER_ID);
        paramMap.put("CUST_ID" , CUST_ID);
        paramMap.put("INDUSTRY_TYPE_ID" , INDUSTRY_TYPE_ID);
        paramMap.put("CHANNEL_ID" , CHANNLE_ID);
        paramMap.put("TXN_AMOUNT" , TXN_AMOUNT);
        paramMap.put("WEBSITE" , WEBSITE);
        paramMap.put("EMAIL" , EMAIL);
        paramMap.put("MOBILE_NO" , MOBILE_NO);
        paramMap.put("CALLBACK_URL" , CALLBACK_URL+ORDER_ID);
        Log.e("RESPONSE",  AppConstantDriver.URL.PAYTM_CHECKSUM_NEW +"\n"+paramMap.toString());
        try{


            ////////
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstantDriver.URL.PAYTM_CHECKSUM_NEW, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("RESPONSE", response);
                    paramMap.put("CHECKSUMHASH" , response);
                    Log.e("RESPONSE CHEC", paramMap.toString());
                    PaytmOrder Order = new PaytmOrder(paramMap);

                    Service.initialize(Order, null);

                    Service.startPaymentTransaction(PaytmTransactionActivity.this, true, true, new PaytmPaymentTransactionCallback() {
                        @Override
                        public void onTransactionResponse(Bundle inResponse) {
                            String jsondata = new Gson().toJson(inResponse);
                            Logger.log(TAG, "onTransactionResponse: "+ jsondata);
                            Intent intent = new Intent();
                            intent.putExtra("jsondata", jsondata);
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                        @Override
                        public void networkNotAvailable() {
                            Logger.log(TAG, "networkNotAvailable: ");
                            text.setText("Network Not Available\nPlease check your network.");
                            back.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void clientAuthenticationFailed(String inErrorMessage) {
                            Logger.log(TAG, "clientAuthenticationFailed: "+inErrorMessage);
                            text.setText(inErrorMessage);
                            back.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void someUIErrorOccurred(String inErrorMessage) {
                            Logger.log(TAG, "someUIErrorOccurred: "+inErrorMessage);
                            text.setText(inErrorMessage);
                            back.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                            Logger.log(TAG, "onErrorLoadingWebPage: "+inErrorMessage);
                            text.setText(inErrorMessage);
                            back.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onBackPressedCancelTransaction() {
                            Logger.log(TAG, "onBackPressedCancelTransaction: ");
                            text.setText("Your Transaction is canceled.");
                            back.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                            Logger.log(TAG, "onTransactionCancel: "+inErrorMessage);
                            text.setText(inErrorMessage);
                            back.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(PaytmTransactionActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    text.setText(error.getMessage());
                    back.setVisibility(View.VISIBLE);
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    paramMap.put("MERCHANT_KEY" , MercahntKey);
                    return paramMap;
                }

            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);




        }catch(Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Logger.log(TAG, "Exception: "+e.getMessage());
        }






    }

    private String getOrderId() {
        Random r = new Random(System.currentTimeMillis());
        return "Order_ID" + (1 + r.nextInt(2)) * 10000
                + r.nextInt(10000);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
