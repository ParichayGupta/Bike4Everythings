package com.bike4everythingbussiness.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bike4everythingbussiness.R;
import com.bike4everythingbussiness.Services.RequestToServer;
import com.bike4everythingbussiness.Utils.AppConstant;
import com.bike4everythingbussiness.Utils.AppPreferance;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SupportActivity extends BaseActivity {

    @BindView(R.id.messageTab)
    TextView messageTab;
    @BindView(R.id.callTab)
    TextView callTab;
    @BindView(R.id.callView)
    CardView callView;
    @BindView(R.id.subjectTxt)
    EditText subjectTxt;
    @BindView(R.id.messageTxt)
    EditText messageTxt;
    @BindView(R.id.sendMessage)
    Button sendMessage;
    @BindView(R.id.messageView)
    CardView messageView;
    @BindView(R.id.mainView)
    LinearLayout mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        hideCallView(false);
        showMessageView(true);

    }

    @OnClick(R.id.messageTab)
    public void onMessageTabClicked() {
        hideCallView(true);
        showMessageView(true);
    }

    @OnClick(R.id.callTab)
    public void onCallTabClicked() {
        hideMessageView(true);
        showCallView(true);
    }

    @OnClick(R.id.sendMessage)
    public void onSendMessageClicked() {
        String subject = subjectTxt.getText().toString();
        String message = messageTxt.getText().toString();
        if(TextUtils.isEmpty(subject)){
            subjectTxt.setAnimation(shake);
        }else if(TextUtils.isEmpty(message)){
            messageTxt.setAnimation(shake);
        }else{

            JSONObject object = new JSONObject();
            try {
                object.put("method", AppConstant.BUSINESS_ENQUIRY);
                object.put("business_id", AppPreferance.getUserid(SupportActivity.this));
                object.put("subject", subject);
                object.put("message", message);

                sendMessageTask(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessageTask(JSONObject object) {
        RequestToServer.getInstance().send(SupportActivity.this,object, AppConstant.B4E_BUSINESS_ENQUIRY, new RequestToServer.CallBack() {
            @Override
            public void success(String json) {
                try {
                    JSONObject object1 = new JSONObject(json);
                    new AlertDialog.Builder(SupportActivity.this)
                            .setTitle("Support")
                            .setMessage(object1.getJSONArray("result").getJSONObject(0).getString("msg"))
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    subjectTxt.setText("");
                                    messageTxt.setText("");
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    public void hideMessageView(boolean animate) {

        messageView.animate().setStartDelay(0);
        messageView.animate().setDuration(animate ? 500 : 0);
        messageView.animate().translationX(-messageView.getWidth());
        messageView.animate().start();
    }

    public void showMessageView(boolean animate) {
        messageView.animate().setStartDelay(0);
        messageView.animate().setDuration(animate ? 500 : 0);
        messageView.animate().translationX(0);
        messageView.animate().start();
    }

    public void hideCallView(boolean animate) {
        callView.animate().setStartDelay(0);
        callView.animate().setDuration(animate ? 500 : 0);
        callView.animate().translationX(callView.getWidth());
        callView.animate().start();
    }

    public void showCallView(boolean animate) {
        callView.animate().setStartDelay(0);
        callView.animate().setDuration(animate ? 500 : 0);
        callView.animate().translationX(0);
        callView.animate().start();
    }
}
