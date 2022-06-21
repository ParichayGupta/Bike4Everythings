package com.bike4everythingbussiness.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bike4everythingbussiness.Adapter.DropAddressAdapter;
import com.bike4everythingbussiness.Model.FareCard;
import com.bike4everythingbussiness.Model.OrderDetails;
import com.bike4everythingbussiness.R;
import com.bike4everythingbussiness.Utils.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeliveryDetailsActivity extends BaseActivity implements DropAddressAdapter.OnItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    OrderDetails orderDetails;
    DropAddressAdapter dropAddressAdapter;
    @BindView(R.id.pickup_address)
    TextView pickupAddress;
    @BindView(R.id.dropRecyclerview)
    RecyclerView dropRecyclerview;
    @BindView(R.id.orderId)
    TextView orderId;
    @BindView(R.id.reorder)
    Button reorder;
    @BindView(R.id.distance)
    TextView distance;
    @BindView(R.id.totalamount1)
    TextView totalamount1;
    @BindView(R.id.gst)
    TextView gst;
    @BindView(R.id.totalamount)
    TextView totalamount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        orderDetails = getIntent().getParcelableExtra("orderDetails");

        dropAddressAdapter = new DropAddressAdapter(DeliveryDetailsActivity.this, orderDetails.getDropAddressList());
        dropAddressAdapter.hideIcon();
        dropAddressAdapter.setListener(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        dropRecyclerview.setLayoutManager(mLayoutManager);
        dropRecyclerview.setItemAnimator(new DefaultItemAnimator());
        dropRecyclerview.setAdapter(dropAddressAdapter);
        dropRecyclerview.setNestedScrollingEnabled(true);

        orderId.setText("# " + orderDetails.getDeliveryId());
        pickupAddress.setText(orderDetails.getPickName() + "|" + orderDetails.getPickMobile() + "\n" + orderDetails.getPickAddress());

        FareCard fareCard = databaseHandler.getFareCard();

        Logger.log("orderDetails", orderDetails.toString());

        gst.setText(""+fareCard.getGst()+"%");
        totalamount.setText("Rs."+ orderDetails.getAmount());
        totalamount1.setText("Rs."+ orderDetails.getAmount());
        distance.setText(""+ orderDetails.getDistance()+" KM");

    }

    @Override
    public void dropAddressRemove(int itemCount) {

    }

    @OnClick({R.id.pickup_address, R.id.dropRecyclerview})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.pickup_address:
                break;
            case R.id.dropRecyclerview:
                break;
        }
    }

    @OnClick(R.id.reorder)
    public void onViewClicked() {
        Intent intent = new Intent();
        intent.putExtra("orderDetails", orderDetails);
        setResult(RESULT_OK, intent);
        finish();
    }
}
