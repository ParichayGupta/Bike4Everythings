package com.b4edriver.DriverApp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.b4edriver.Adapter.NotificationAdapterDriver;
import com.b4edriver.Database.DBAdapter_Driver;
import com.b4edriver.Model.NotificationDriver;
import com.b4edriver.R;

import java.util.ArrayList;
import java.util.List;


public class NotificationActivityDriver extends AppCompatActivity {
    //aa
    NotificationAdapterDriver notificationAdapter;
    List<NotificationDriver> modelList;
    ListView notify_listview;
    DBAdapter_Driver db;
    TextView header_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_driver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notify_listview = (ListView) findViewById(R.id.notify_listview);
        header_tv = (TextView) findViewById(R.id.header_tv);
        modelList = new ArrayList<NotificationDriver>();

        db = new DBAdapter_Driver(NotificationActivityDriver.this);
        modelList = db.getAllNotification();

        notificationAdapter = new NotificationAdapterDriver(NotificationActivityDriver.this, modelList);
        notify_listview.setAdapter(notificationAdapter);

        if(modelList.isEmpty()){
            final String message = getString(R.string.sorry_no_data_found);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    header_tv.setVisibility(View.VISIBLE);
                    header_tv.setText(message);
                }
            });

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.left_to_right,
                R.anim.right_to_left);
    }
}
