package com.bike4everythingbussiness.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bike4everythingbussiness.R;
import com.bike4everythingbussiness.Utils.AppConstant;
import com.bike4everythingbussiness.Utils.AppPreferance;
import com.bike4everythingbussiness.Utils.Logger;
import com.bike4everythingbussiness.Utils.PaytmEnvironment;
import com.paytm.pgsdk.PaytmMerchant;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddWalletActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.number)
    TextView number;
    @BindView(R.id.balance)
    TextView balance;
    @BindView(R.id.btnAmount1)
    Button btnAmount1;
    @BindView(R.id.btnAmount2)
    Button btnAmount2;
    @BindView(R.id.btnAmount3)
    Button btnAmount3;
    @BindView(R.id.addBalance)
    EditText addBalance;
    @BindView(R.id.btnAdd)
    Button btnAdd;
    @BindView(R.id.addMoneyView)
    LinearLayout addMoneyView;
    @BindView(R.id.numberView)
    LinearLayout numberView;
    String ssotoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wallet);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
         ssotoken = getIntent().getStringExtra("ssotoken");
        String paytmNumber = getIntent().getStringExtra("number");
        if (paytmNumber.equals("")) {
            numberView.setVisibility(View.GONE);
        } else {
            numberView.setVisibility(View.VISIBLE);
            number.setText(paytmNumber);
        }

        balance.setText("Rs. "+getIntent().getStringExtra("amount"));

        btnAdd.setText(getIntent().getStringExtra("title"));

    }

    @OnClick(R.id.btnAmount1)
    public void onBtnAmount1Clicked() {
        addBalance.setText("2000");
    }

    @OnClick(R.id.btnAmount2)
    public void onBtnAmount2Clicked() {
        addBalance.setText("5000");
    }

    @OnClick(R.id.btnAmount3)
    public void onBtnAmount3Clicked() {
        addBalance.setText("10000");
    }

    @OnClick(R.id.btnAdd)
    public void onBtnAddClicked() {
        String amount = addBalance.getText().toString();
        if(TextUtils.isEmpty(amount)){
            addBalance.startAnimation(shake);
        }else{

            if(getIntent().getStringExtra("title").contains("Paytm")) {
                addMoneyView.setVisibility(View.GONE);
                webview.setVisibility(View.VISIBLE);

                String ORDER_ID = "ORD" + AppPreferance.getUserid(AddWalletActivity.this) + new Random().nextInt(100000);
                String CUST_ID = String.valueOf(AppPreferance.getUserid(AddWalletActivity.this) + new Random().nextInt(1000));
                String TXN_AMOUNT = amount;
                String SSO_TOKEN = ssotoken;

                String url = AppConstant.PAYTM_ADD_MONEY + "?ORDER_ID=" + ORDER_ID + "&CUST_ID=" +
                        CUST_ID + "&TXN_AMOUNT=" + TXN_AMOUNT + "&SSO_TOKEN=" + SSO_TOKEN;
                Logger.log("addmoney", url);
                webview.loadUrl(url);

                WebSettings webSettings = webview.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webview.addJavascriptInterface(new WebAppInterface(this), "AndroidFunction");
            }else {
                onStartTransaction(amount);

            }
        }
    }

    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void showToast(String toast) {
            //Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction("amount");
            intent.putExtra("amount", toast);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(getIntent().getStringExtra("title").contains("Paytm")) {
            getMenuInflater().inflate(R.menu.addwallet, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_remove:
                        new RemoveWallet().execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private class RemoveWallet extends AsyncTask<String, Void, Void> {

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject data = new JSONObject();
                data.put("method", AppConstant.REMOVE_BUSINESS_PAYTM_WALLET);
                data.put("business_id", AppPreferance.getUserid(AddWalletActivity.this));

                Log.e("Response_Response", AppConstant.B4E_BUSINESS_WALLET + "\n" + data.toString());
                //json.put("notification", dataJson);
                RequestBody body = RequestBody.create(JSON, data.toString());
                Request request = new Request.Builder()
                        .url(AppConstant.B4E_BUSINESS_WALLET)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();

                try {
                    JSONObject object = new JSONObject(result);
                    Intent intent = new Intent();
                    intent.setAction("remove");
                    intent.putExtra("remove", "success");
                    setResult(RESULT_OK, intent);

                    finish();
                    return null;
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
    }


    public void onStartTransaction(String payment) {

        PaytmEnvironment paytmEnvironment = PaytmEnvironment.LIVE;

        PaytmPGService Service = PaytmPGService.getProductionService();
        Map<String, String> paramMap = new HashMap<String, String>();

        // these are mandatory parameters
        paramMap.put("ORDER_ID", "ORD"+ AppPreferance.getUserid(AddWalletActivity.this)+ new Random().nextInt(100000));
        paramMap.put("MID", paytmEnvironment.getMID());
        paramMap.put("CUST_ID", String.valueOf(AppPreferance.getUserid(AddWalletActivity.this)+ new Random().nextInt(1000)));
        paramMap.put("CHANNEL_ID", paytmEnvironment.getChannelId());
        paramMap.put("INDUSTRY_TYPE_ID", paytmEnvironment.getIndustryTypeId());
        paramMap.put("WEBSITE", paytmEnvironment.getWebsite());
        paramMap.put("TXN_AMOUNT", payment);
        paramMap.put("THEME", paytmEnvironment.getMerchantKey());
        //paramMap.put("EMAIL", user.getEmailId());
        //paramMap.put("MOBILE_NO", user.getMobile());
        PaytmOrder Order = new PaytmOrder(paramMap);

        Logger.log("REQUES", paramMap.toString());
        PaytmMerchant Merchant = new PaytmMerchant(
                "http://bike4everything.in/administrator/user_api/paytm/generateChecksum.php",
                "http://bike4everything.in/administrator/user_api/paytm/verifyChecksum.php");

        Service.initialize(Order, Merchant, null);
        Service.enableLog(AddWalletActivity.this);


        Service.startPaymentTransaction(this, true, true,
                new PaytmPaymentTransactionCallback() {
                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        Logger.log("PGSDK", "Payment someUIErrorOccurred " + inErrorMessage);
                    }

                    @Override
                    public void onTransactionSuccess(Bundle inResponse) {
                        paytmTransaction(inResponse);
                        Logger.log("PGSDK", "Payment Transaction is successful " + inResponse);
                        Toast.makeText(getApplicationContext(), "Payment Transaction is successful ", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onTransactionFailure(String inErrorMessage,
                                                     Bundle inResponse) {
                        Logger.log("PGSDK", "Payment Transaction Failed " + inErrorMessage);
                        Toast.makeText(getBaseContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void networkNotAvailable() {
                        Toast.makeText(getApplicationContext(), "Network not avalable", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        Logger.log("PGSDK", "Payment clientAuthenticationFailed " + inErrorMessage);
                        Toast.makeText(getApplicationContext(), inErrorMessage, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                        Logger.log("PGSDK", "Payment onErrorLoadingWebPage " + inErrorMessage +"\nurl"+inFailingUrl);
                        Toast.makeText(getApplicationContext(), inErrorMessage, Toast.LENGTH_LONG).show();
                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        // TODO Auto-generated method stub
                        Logger.log("LOG", "Payment  back" );
                        Toast.makeText(getApplicationContext(), "Transaction cancel", Toast.LENGTH_LONG).show();
                    }

                });
    }


    private void paytmTransaction(Bundle inResponse) {

        final String STATUS = inResponse.getString("STATUS");
        final String BANKNAME = inResponse.getString("BANKNAME");
        final String ORDERID = inResponse.getString("ORDERID");
        final String TXNAMOUNT = inResponse.getString("TXNAMOUNT");
        final String TXNDATE = inResponse.getString("TXNDATE");
        final String MID = inResponse.getString("MID");
        final String TXNID = inResponse.getString("TXNID");
        final String RESPCODE = inResponse.getString("RESPCODE");
        final String PAYMENTMODE = inResponse.getString("PAYMENTMODE");
        final String BANKTXNID = inResponse.getString("BANKTXNID");
        final String CURRENCY = inResponse.getString("CURRENCY");
        final String GATEWAYNAME = inResponse.getString("GATEWAYNAME");
        final String RESPMSG = "Paytm Transaction Successful";
    /*    final String email = user.getEmailId();
        final String mobileNo = user.getMobile();
*/
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("method", AppConstant.BUSINESS_WALLET_ADD);
            jsonObject1.put("business_id", AppPreferance.getUserid(AddWalletActivity.this));
            jsonObject1.put("ORDERID", ORDERID);
            jsonObject1.put("MID", MID);
            jsonObject1.put("TXNID", TXNID);
            jsonObject1.put("TXNAMOUNT", TXNAMOUNT);
            jsonObject1.put("PAYMENTMODE", PAYMENTMODE);
            jsonObject1.put("CURRENCY", CURRENCY);
            jsonObject1.put("TXNDATE", TXNDATE);
            jsonObject1.put("STATUS", STATUS);
            jsonObject1.put("RESPCODE", RESPCODE);
            jsonObject1.put("RESPMSG", RESPMSG);
            jsonObject1.put("GATEWAYNAME", GATEWAYNAME);
            jsonObject1.put("BANKTXNID", BANKTXNID);
            jsonObject1.put("BANKNAME", BANKNAME);
            jsonObject1.put("CHECKSUMHASH", "");
        }catch (JSONException e){}

       new TransactionInB4EWallet(jsonObject1).execute();
    }

    private class TransactionInB4EWallet extends AsyncTask<Void,Void,Void>{
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject;
        public TransactionInB4EWallet(JSONObject jsonObject){
            this.jsonObject = jsonObject;
        }
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                OkHttpClient client = new OkHttpClient();

                Log.e("Response_Response", AppConstant.B4E_BUSINESS_WALLET_TRANSACTION + "\n" + jsonObject.toString());
                //json.put("notification", dataJson);
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(AppConstant.B4E_BUSINESS_WALLET_TRANSACTION)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                Log.e("Response_Response", result);

            } catch (IOException e) {
                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Intent intent = new Intent();
            intent.setAction("addwallet");
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
