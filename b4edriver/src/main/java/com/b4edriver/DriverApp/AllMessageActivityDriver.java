package com.b4edriver.DriverApp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.b4edriver.Adapter.AllMessageAdapterDriver;
import com.b4edriver.Database.DBAdapter_Driver;
import com.b4edriver.Model.AllMessageDriver;
import com.b4edriver.R;

import java.util.ArrayList;
import java.util.List;

public class AllMessageActivityDriver extends AppCompatActivity {

    ListView msg_listview;
    AllMessageAdapterDriver allMessageAdapter;
    List<AllMessageDriver> allMessageList;
    DBAdapter_Driver dbAdapter_driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_message_driver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow);

        initView();

        allMessageList = new ArrayList<AllMessageDriver>();
        dbAdapter_driver = new DBAdapter_Driver(getApplicationContext());

        allMessageList = dbAdapter_driver.getAllMessage();


        allMessageAdapter = new AllMessageAdapterDriver(getApplicationContext(),allMessageList);
        msg_listview.setAdapter(allMessageAdapter);

    }

    private void initView() {
        msg_listview = (ListView) findViewById(R.id.msg_listview);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.left_to_right,
                        R.anim.right_to_left);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbAdapter_driver.deleteAllMsg();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.left_to_right,
                R.anim.right_to_left);
    }
}
