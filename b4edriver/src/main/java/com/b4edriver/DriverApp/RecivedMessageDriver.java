package com.b4edriver.DriverApp;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.b4edriver.GCM.ConfigDriver;
import com.b4edriver.R;

public class RecivedMessageDriver extends Activity {
    //aa
    TextView message, header;
    Button btn_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recived_activity_message_driver);

        message = (TextView) findViewById(R.id.message);
        header = (TextView) findViewById(R.id.text);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(ok);

        header.setText("");
        message.setText(getIntent().getStringExtra("message"));


        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(ConfigDriver.TRIP_RECIVE_MSG_ID);

    }

    View.OnClickListener ok = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            finish();
        }
    };


}
