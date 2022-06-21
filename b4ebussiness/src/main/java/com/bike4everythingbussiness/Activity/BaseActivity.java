package com.bike4everythingbussiness.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bike4everythingbussiness.DatabaseHandler;
import com.bike4everythingbussiness.Model.User;
import com.bike4everythingbussiness.R;
import com.bike4everythingbussiness.Utils.AppPreferance;
import com.bike4everythingbussiness.Utils.Logger;
import com.bike4everythingbussiness.Utils.ProgressDialogRing;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by manishsingh on 02/01/18.
 */

public class BaseActivity extends AppCompatActivity {


    private Dialog alertDialog;
    private Dialog networkDialog;
    public Animation shake;
    private long startClickTime;
    public DatabaseHandler databaseHandler;
    User currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shake = AnimationUtils.loadAnimation(BaseActivity.this, R.anim.shake);

        databaseHandler = new DatabaseHandler(BaseActivity.this);
        currentUser = databaseHandler.getContact(AppPreferance.getUserid(BaseActivity.this));

        Logger.log("currentUser", currentUser.toString());

    }

    View.OnClickListener networkRefresh = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent("android.settings.WIFI_SETTINGS");
            startActivity(intent);
        }
    };

    View.OnClickListener alertYesButton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           if(alertDialog.isShowing()){
               alertDialog.dismiss();
           }
        }
    };
    View.OnClickListener alertNoButton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           if(alertDialog.isShowing()){
               alertDialog.dismiss();
           }
        }
    };

    public void showProgressDialog(Context context){
        ProgressDialogRing.getInstance(context).show();
    }
    public void showAlertDialog(Context context, String title, String message, String yes){
        alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.setContentView(R.layout.alert_dialog);
        TextView mTitle = (TextView) alertDialog.findViewById(R.id.title);
        TextView mMessage = (TextView) alertDialog.findViewById(R.id.message);
        TextView mNo_btn = (TextView) alertDialog.findViewById(R.id.no_btn);
        TextView mYes_btn = (TextView) alertDialog.findViewById(R.id.yes_btn);
        mTitle.setText(title);
        mMessage.setText(message);
        mYes_btn.setText(yes);
        mNo_btn.setVisibility(View.GONE);
        mYes_btn.setOnClickListener(alertYesButton);
        mNo_btn.setOnClickListener(alertNoButton);
        alertDialog.show();
    }
    public void showAlertDialog(Context context, String title, String message, String yes, View.OnClickListener yesbtn){
        alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.setContentView(R.layout.alert_dialog);
        TextView mTitle = (TextView) alertDialog.findViewById(R.id.title);
        TextView mMessage = (TextView) alertDialog.findViewById(R.id.message);
        TextView mNo_btn = (TextView) alertDialog.findViewById(R.id.no_btn);
        TextView mYes_btn = (TextView) alertDialog.findViewById(R.id.yes_btn);
        mTitle.setText(title);
        mMessage.setText(message);
        mYes_btn.setText(yes);
        mNo_btn.setVisibility(View.GONE);
        mYes_btn.setOnClickListener(yesbtn);
        mNo_btn.setOnClickListener(alertNoButton);
        alertDialog.show();
    }
    public void showNetworkDialog(Context context){
        networkDialog = new Dialog(context);
        networkDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        networkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        networkDialog.setCancelable(false);
        networkDialog.setContentView(R.layout.network_dialog);
        Button networkrefresh;
        networkrefresh = (Button) networkDialog.findViewById(R.id.refresh);
        networkrefresh.setOnClickListener(networkRefresh);
        networkDialog.show();
    }

    public void dismissAlertDialog(){
        if(alertDialog == null){
            return ;
        }
        alertDialog.dismiss();
    }

    public boolean isShowingAlertDialog(){
        if(alertDialog == null){
            return false;
        }
        return alertDialog.isShowing();
    }
    public void dismissProgressDialog(){
        ProgressDialogRing.getInstance(this).hide();
    }

    public boolean isShowingProgressDialog(){
        return ProgressDialogRing.getInstance(this).isShowing();
    }
    public void dismissNetworkDialog(){
        if(networkDialog == null){
            return ;
        }
        networkDialog.dismiss();
    }

    public boolean isShowingNetworkDialog(){
        if(networkDialog == null){
            return false;
        }
        return networkDialog.isShowing();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            startClickTime = System.currentTimeMillis();

        } else if (event.getAction() == MotionEvent.ACTION_UP) {

            if (System.currentTimeMillis() - startClickTime < ViewConfiguration.getTapTimeout()) {

                // Touch was a simple tap. Do whatever.
                if (view instanceof EditText) {
                    View w = getCurrentFocus();
                    int scrcoords[] = new int[2];
                    w.getLocationOnScreen(scrcoords);
                    float x = event.getRawX() + w.getLeft() - scrcoords[0];
                    float y = event.getRawY() + w.getTop() - scrcoords[1];

                    if (event.getAction() == MotionEvent.ACTION_UP
                            && (x < w.getLeft() || x >= w.getRight()
                            || y < w.getTop() || y > w.getBottom()) ) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                    }
                }
            } else {

                // Touch was a not a simple tap.

            }

        }

        return ret;
    }

    public String dateFormat(String date_s){
       // String date_s = " 2011-01-18 00:00:00.0";
        SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = dt.parse(date_s);
        } catch (ParseException e) {
            e.printStackTrace();
            return date_s;
        }
        SimpleDateFormat dt1 = new SimpleDateFormat("dd MMM yyyy");
        System.out.println(dt1.format(date));
        return dt1.format(date);
    }


}
