package com.hvantage.b4eemp.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.b4erental.Model.User;
import com.b4erental.RentalActivity;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hvantage.b4eemp.Database.Database;
import com.hvantage.b4eemp.R;
import com.hvantage.b4eemp.fcm.MyFirebaseInstanceIDService;
import com.hvantage.b4eemp.fragment.AlotBookingFragment;
import com.hvantage.b4eemp.fragment.CompletedFragment;
import com.hvantage.b4eemp.fragment.RunningFragment;
import com.hvantage.b4eemp.tracking.DevicesActivity;
import com.hvantage.b4eemp.utils.AppConstants;
import com.hvantage.b4eemp.utils.AppPreferance;
import com.hvantage.b4eemp.utils.TimeService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.b4elibrary.AppConstants.KEY_USER;

public class BookingActivity extends AppCompatActivity implements View.OnClickListener {
    private PendingIntent pendingIntent;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    FloatingActionButton addNewBooking;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private final String BROADCAST_ACTION = "com.example.VIEW_ACTION";

    // Receive the action from the notification item when its clicked
    // This receiver can be used to receive intents from other applications as well not just our Notification
    BroadcastReceiver notifyServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //intent, from the arguments will contain the parameters from the Notification used to trigger our IntentFilter
            startActivityIfNeeded(new Intent(getApplicationContext(), BookingActivity.class), 1);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);


        //Database.getInstance(BookingActivity.this).deleteAllIssuesData();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);

        addNewBooking = findViewById(R.id.addNewBooking);
        addNewBooking.setOnClickListener(this);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        new UpdateFcmId().execute();

        /*Intent alarmIntent = new Intent(BookingActivity.this, AlarmReceiver.class);
        alarmIntent.putExtra("text", "AlotBooking");
        pendingIntent = PendingIntent.getBroadcast(BookingActivity.this, 0, alarmIntent, 0);

        startAt10(23232);*/
        Intent intent = new Intent(BookingActivity.this, TimeService.class);
        startService(intent);
        registerReceiver();

        Database.getInstance(BookingActivity.this).deleteAllBooking();

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout) {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Fragment fragment = mSectionsPagerAdapter.getItem(position);
                if (position == 0) {

                } else if (position == 1) {
                    if (fragment instanceof RunningFragment) {
                        //((RunningFragment) fragment).onRefresh();
                    }
                } else if (position == 2) {

                }
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_booking, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            AppPreferance.setUser(BookingActivity.this, "", "", "", "", "", "");
            startActivity(new Intent(BookingActivity.this, LoginPage.class));
            finish();
            return true;
        } else if (id == R.id.action_tracker) {
            Intent intent = new Intent(BookingActivity.this, DevicesActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addNewBooking) {
            Intent intent = new Intent(BookingActivity.this, RentalActivity.class);
            User user = AppPreferance.getUserIdName(BookingActivity.this);
            intent.putExtra(KEY_USER, user);
            intent.putExtra("title", "Create New Booking");
            startActivity(intent);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return AlotBookingFragment.newInstance("", "");
            } else if (position == 1) {
                return RunningFragment.newInstance("", "");
            } else {
                return CompletedFragment.newInstance("", "");
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    public void start() {
        AlarmManager manager = (AlarmManager) BookingActivity.this.getSystemService(Context.ALARM_SERVICE);
        int interval = 8000;

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Toast.makeText(BookingActivity.this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }



    public void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(notifyServiceReceiver, intentFilter);
    }

    private void unregisterReceiver() {
        try {
            this.unregisterReceiver(notifyServiceReceiver);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        unregisterReceiver();
        super.onPause();
    }

    protected void onResume() {
        registerReceiver();
        super.onResume();
    }


    private class UpdateFcmId extends AsyncTask<String, String, String> {

        private JSONObject jsonObject;

        public UpdateFcmId() {


            try {
                jsonObject = new JSONObject();
                jsonObject.put("method", AppConstants.RENT_EMPLOYEE_FCM);
                jsonObject.put("user_id", AppPreferance.getUserid(BookingActivity.this));
                jsonObject.put("fcm_id", FirebaseInstanceId.getInstance().getToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.e("Request_Response", AppConstants.REGISTER_LOG_API + "\n" + jsonObject.toString());

            try {
                OkHttpClient client = new OkHttpClient();
                final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(AppConstants.REGISTER_LOG_API)
                        .post(body)
                        .build();
                okhttp3.Response response = client.newCall(request).execute();
                //String result = response.body().string();

                return "";

            } catch (IOException e) {
                e.printStackTrace();

            }
            return "";
        }


    }

}
