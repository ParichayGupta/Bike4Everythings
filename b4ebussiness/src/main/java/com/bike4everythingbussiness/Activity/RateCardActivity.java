package com.bike4everythingbussiness.Activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bike4everythingbussiness.Model.FareCard;
import com.bike4everythingbussiness.MyApplication;
import com.bike4everythingbussiness.R;
import com.bike4everythingbussiness.Utils.ConnectivityReceiver;
import com.bike4everythingbussiness.Utils.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RateCardActivity extends BaseActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    @BindView(R.id.forkms)
    TextView forkms;
    @BindView(R.id.basefareExpand)
    ImageButton basefareExpand;
    @BindView(R.id.timeslot)
    TextView timeslot;
    @BindView(R.id.basefare)
    TextView basefare;
    @BindView(R.id.basefareDetails)
    TableLayout basefareDetails;
    @BindView(R.id.afterkms)
    TextView afterkms;
    @BindView(R.id.perkmExpand)
    ImageButton perkmExpand;
    @BindView(R.id.rangekmfare)
    TextView rangekmfare;
    @BindView(R.id.perkmfare)
    TextView perkmfare;
    @BindView(R.id.perkmDetails)
    TableLayout perkmDetails;
    @BindView(R.id.additionaltxt)
    TextView additionaltxt;
    @BindView(R.id.perdeliveryExpand)
    ImageButton perdeliveryExpand;
    @BindView(R.id.perdeliveryDetails)
    TableLayout perdeliveryDetails;
    @BindView(R.id.basefareExpand1)
    RelativeLayout basefareExpand1;
    @BindView(R.id.perkmExpand1)
    RelativeLayout perkmExpand1;
    @BindView(R.id.perdeliveryExpand1)
    RelativeLayout perdeliveryExpand1;
    @BindView(R.id.returnfare)
    TextView returnfare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_card);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FareCard fareCard = databaseHandler.getFareCard();
        Logger.log("fareCard", fareCard.toString());

        forkms.setText("for " + fareCard.getLimitKm() + " km");
        basefare.setText(fareCard.getBaseFare());
        afterkms.setText("after " + fareCard.getLimitKm() + " kms");
        rangekmfare.setText(fareCard.getLimitKm() + " and above");
        perkmfare.setText(fareCard.getPerKmFare());
        returnfare.setText("Rs."+fareCard.getReturnFare()+" per/delivery");

        try {
            List<FareCard.Delivery> deliveryrangeList = fareCard.getDeliveries();

            for (int i = 0; i < deliveryrangeList.size(); i++) {
                FareCard.Delivery object = deliveryrangeList.get(i);
                Logger.log("FareCard", deliveryrangeList.get(i).toString());
                TableRow tableRow = new TableRow(RateCardActivity.this);
                tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                TextView deliveriesrange = new TextView(RateCardActivity.this);
                deliveriesrange.setWidth(0);
                deliveriesrange.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                deliveriesrange.setBackgroundResource(R.drawable.edit_box_blue_border);
                deliveriesrange.setTextColor(getResources().getColor(R.color.black));
                deliveriesrange.setGravity(Gravity.CENTER);
                TextView deliveryfare = new TextView(RateCardActivity.this);
                deliveryfare.setWidth(0);
                deliveryfare.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                deliveryfare.setBackgroundResource(R.drawable.edit_box_blue_border);
                deliveryfare.setTextColor(getResources().getColor(R.color.black));
                deliveryfare.setGravity(Gravity.CENTER);

                String kms = object.getDropPoint();
                String fare = object.getFare();
                if (i == 0) {
                    deliveriesrange.setText("2 - " + kms);
                    deliveryfare.setText(fare);
                } else {
                    deliveriesrange.setText((1 + Integer.parseInt(deliveryrangeList.get(i - 1).getDropPoint())) + " - " + kms);
                    deliveryfare.setText(fare);
                }

                tableRow.addView(deliveriesrange);
                tableRow.addView(deliveryfare);
                perdeliveryDetails.addView(tableRow);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.basefareExpand, R.id.basefareExpand1})
    public void onBasefareExpandClicked() {
        if (basefareDetails.getVisibility() == View.GONE) {
            basefareDetails.setVisibility(View.VISIBLE);
            basefareExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
        } else {
            basefareDetails.setVisibility(View.GONE);
            basefareExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_right_black_24dp);
        }
    }

    @OnClick({R.id.perkmExpand, R.id.perkmExpand1})
    public void onPerkmExpandClicked() {
        if (perkmDetails.getVisibility() == View.GONE) {
            perkmDetails.setVisibility(View.VISIBLE);
            perkmExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
        } else {
            perkmDetails.setVisibility(View.GONE);
            perkmExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_right_black_24dp);
        }
    }

    @OnClick({R.id.perdeliveryExpand, R.id.perdeliveryExpand1})
    public void onPerdeliveryExpandClicked() {
        if (perdeliveryDetails.getVisibility() == View.GONE) {
            perdeliveryDetails.setVisibility(View.VISIBLE);
            perdeliveryExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
        } else {
            perdeliveryDetails.setVisibility(View.GONE);
            perdeliveryExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_right_black_24dp);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isShowingNetworkDialog()) {
            dismissNetworkDialog();
        }
        if (!isConnected) {
            showNetworkDialog(RateCardActivity.this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

}
