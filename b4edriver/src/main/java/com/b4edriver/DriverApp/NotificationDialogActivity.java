package com.b4edriver.DriverApp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.b4edriver.R;

public class NotificationDialogActivity extends Activity implements View.OnClickListener {

   TextView source_tv, desti_tv;
    Button btn_accept, btn_reject;
    NotificationDialogActivity instance = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_dialog_driver);

        instance = this;

        init();
        listener();


    }

    private void init() {
        source_tv = (TextView) findViewById(R.id.source_tv);
        desti_tv = (TextView) findViewById(R.id.desti_tv);
        btn_accept = (Button) findViewById(R.id.btn_accept);
        btn_reject = (Button) findViewById(R.id.btn_reject);
    }

    private void listener() {
        btn_accept.setOnClickListener(this);
        btn_reject.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_accept) {
        } else if (i == R.id.btn_reject) {
            finish();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.left_to_right,
                R.anim.right_to_left);
    }
}
