package com.b4edriver.DriverApp;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.b4edriver.R;

public class ThankyouActivityDriver extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thankyou_driver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Handler handler = new Handler();
        handler.postDelayed(runnable,3000);


    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {

            finish();
            overridePendingTransition(R.anim.left_to_right,
                    R.anim.right_to_left);
        }
    };

}
