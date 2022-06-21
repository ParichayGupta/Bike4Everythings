package com.bike4everythingbussiness.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bike4everythingbussiness.Model.PickAddress;
import com.bike4everythingbussiness.Model.Wallet;
import com.bike4everythingbussiness.MyApplication;
import com.bike4everythingbussiness.R;
import com.bike4everythingbussiness.Utils.AppConstant;
import com.bike4everythingbussiness.Utils.AppPreferance;
import com.bike4everythingbussiness.Utils.ConnectivityReceiver;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class PickupAddressActivity extends BaseActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.addressName)
    EditText addressName;
    @BindView(R.id.phonenumber)
    EditText phonenumber;
    @BindView(R.id.contactname)
    EditText contactname;
    @BindView(R.id.address)
    EditText address;
    @BindView(R.id.saveBtn)
    Button saveBtn;

    double latitude = 0.0, longitude = 0.0;

    private static final int PLACE_PICKER_REQUEST = 1000;
    private GoogleApiClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_address);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mClient.connect();
    }
    @Override
    protected void onStop() {
        mClient.disconnect();
        super.onStop();
    }

    @OnClick({R.id.address, R.id.saveBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.address:
                address.setEnabled(false);
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(PickupAddressActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.saveBtn:
                if(TextUtils.isEmpty(address.getText())){
                    address.startAnimation(shake);
                }else if(TextUtils.isEmpty(addressName.getText())){
                    addressName.startAnimation(shake);
                }else if(TextUtils.isEmpty(contactname.getText())){
                    contactname.startAnimation(shake);
                }else if(TextUtils.isEmpty(phonenumber.getText())){
                    phonenumber.startAnimation(shake);
                }else {


                    PickAddress pickAddress = new PickAddress();
                    pickAddress.setId(AppPreferance.getUserid(PickupAddressActivity.this));
                    pickAddress.setPickAddress(address.getText().toString());
                    pickAddress.setPickAddressName(addressName.getText().toString());
                    pickAddress.setPickName(contactname.getText().toString());
                    pickAddress.setPickMobile(phonenumber.getText().toString());
                    pickAddress.setPickLat(latitude);
                    pickAddress.setPickLng(longitude);
                    pickAddress.setSelect(true);

                    new AddPickupAddress(pickAddress).execute();


                }
                break;
        }
    }

    private class AddPickupAddress extends AsyncTask<Void,Void,Void>{

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        PickAddress pickAddress;
        public AddPickupAddress(PickAddress pickAddress) {
            this.pickAddress = pickAddress;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject data = new JSONObject();
                data.put("method", AppConstant.APP_PICKUP_ADDRESS);
                data.put("business_id", AppPreferance.getUserid(PickupAddressActivity.this));
                data.put("name",pickAddress.getPickName());
                data.put("contact",pickAddress.getPickMobile());
                data.put("street",pickAddress.getPickAddressName());
                data.put("locality",pickAddress.getPickAddress());
                data.put("latitude",pickAddress.getPickLat());
                data.put("longitude",pickAddress.getPickLng());

                Log.e("Response_Response", AppConstant.B4E_BUSINESS_ALL_DELIVERY + "\n" + data.toString());
                //json.put("notification", dataJson);
                RequestBody body = RequestBody.create(JSON, data.toString());
                Request request = new Request.Builder()
                        .url(AppConstant.B4E_BUSINESS_ALL_DELIVERY)
                        .post(body)
                        .build();
                okhttp3.Response response = client.newCall(request).execute();
                String result = response.body().string();

                Intent intent = new Intent();
                intent.putExtra("address", pickAddress);
                setResult(RESULT_OK, intent);
                finish();

            } catch (JSONException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            }

            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);

                String placename = String.format("%s", place.getName());
                String addresses = String.format("%s", place.getAddress());
                String phone = String.format("%s", place.getPhoneNumber());

                 latitude = place.getLatLng().latitude;
                 longitude = place.getLatLng().longitude;
                if(latitude == 0.0 || longitude == 0.0){
                    Toast.makeText(PickupAddressActivity.this, "latlng is not available in this address, please select different address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                address.setText(TextUtils.isEmpty(addresses) ? placename : addresses);
                addressName.setText(placename);
                phonenumber.setText(phone.replace(" ",""));
               // textView.setText(stBuilder.toString());
            }

            address.setEnabled(true);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isShowingNetworkDialog()) {
            dismissNetworkDialog();
        }
        if (!isConnected) {
            showNetworkDialog(PickupAddressActivity.this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }
}
