package com.b4ebusinessdriver.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.b4ebusinessdriver.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AlertActivity extends Activity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.message)
    TextView message;
    @BindView(R.id.no_btn)
    TextView noBtn;
    @BindView(R.id.yes_btn)
    TextView yesBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_alert);
        ButterKnife.bind(this);

        message.setText(getIntent().getStringExtra("message"));
        noBtn.setVisibility(View.GONE);
        yesBtn.setText("OK");
    }

    @OnClick(R.id.no_btn)
    public void onNoBtnClicked() {
        finish();
    }

    @OnClick(R.id.yes_btn)
    public void onYesBtnClicked() {
        finish();
    }
}
