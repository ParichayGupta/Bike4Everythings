package com.b4edriver.b4edrivers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;


import com.b4edriver.R;
import com.b4elibrary.Logger;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeatilsActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView tripId;
    ImageView part1Img1;
    ImageView part1Img2;
    TextView part1Googlekm;
    TextView part1Meterkm;
    ImageView part2Img1;
    ImageView part2Img2;
    TextView part2Googlekm;
    TextView part2Meterkm;
    TextView tripamount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b4edrivers_activity_deatils);
        initView();
        setSupportActionBar(toolbar);

        DriverDatum datum = getIntent().getParcelableExtra("details");

        Logger.log("datum", datum.toString());

        Glide.with(DeatilsActivity.this)
                .asBitmap()
                .load(datum.getStartImage())
                .into(part1Img1);
        Glide.with(DeatilsActivity.this)
                .asBitmap()
                .load(datum.getReachImage())
                .into(part1Img2);
        Glide.with(DeatilsActivity.this)
                .asBitmap()
                .load(datum.getDeliveryStartImage())
                .into(part2Img1);
        Glide.with(DeatilsActivity.this)
                .asBitmap()
                .load(datum.getEndImage())
                .into(part2Img2);

        tripId.setText("# " + datum.getId());

        part1Googlekm.setText(datum.getSRKmGoogle() + " KM");
        part1Meterkm.setText(datum.getSRKmMeter() + " KM");

        part2Googlekm.setText(datum.getDEKmGoogle() + " KM");
        part2Meterkm.setText(datum.getDEKmMeter() + " KM");

        String amountfor_biker_str_aditional = Function.tripAnountFrom_S_R(datum.getSRKmMeter());

        String amountfor_biker_str = Function.tripAnountForBike(datum.getDEKmMeter());

        float total = Float.parseFloat(amountfor_biker_str) + Float.parseFloat(amountfor_biker_str_aditional);

        tripamount.setText(getString(R.string.trip_amount_for_bike, amountfor_biker_str) + "+"+ amountfor_biker_str_aditional
        +" = " +total);

    }

    private void initView() {

         toolbar=findViewById(R.id.toolbar);

         tripId=findViewById(R.id.tripId);

         part1Img1=findViewById(R.id.part1_img1);

         part1Img2=findViewById(R.id.part1_img2);

         part1Googlekm=findViewById(R.id.part1_googlekm);

         part1Meterkm=findViewById(R.id.part1_meterkm);

         part2Img1=findViewById(R.id.part2_img1);

         part2Img2=findViewById(R.id.part2_img2);

         part2Googlekm=findViewById(R.id.part2_googlekm);

         part2Meterkm=findViewById(R.id.part2_meterkm);

         tripamount=findViewById(R.id.tripamount);
    }

}
