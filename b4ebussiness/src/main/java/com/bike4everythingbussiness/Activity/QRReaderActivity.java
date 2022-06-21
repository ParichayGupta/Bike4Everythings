package com.bike4everythingbussiness.Activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bike4everythingbussiness.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QRReaderActivity extends AppCompatActivity {


    private static final String cameraPerm = Manifest.permission.CAMERA;
    @BindView(R.id.code_info)
    TextView codeInfo;
    @BindView(R.id.camera_view)
    SurfaceView cameraView;
    @BindView(R.id.payll)
    RelativeLayout payll;
    String userQRCode = "";
  //  private QREader qrEader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrreader);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("SCAN");

        payll.setVisibility(View.GONE);

        setupQREader();


    }

    void restartActivity() {
        startActivity(new Intent(QRReaderActivity.this, QRReaderActivity.class));
        finish();
    }

    void setupQREader() {
        // Init QREader
        // ------------
   /*     qrEader = new QREader.Builder(this, cameraView, new QRDataListener() {
            @Override
            public void onDetected(final String data) {
                Logger.log("QREader", "Value : " + data);
                codeInfo.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(data)) {
                            qrEader.stop();
                            Logger.log("REQUEST_RESPONSE code", data);
                            userQRCode = data;
                            codeInfo.setText(userQRCode);
                            payll.setVisibility(View.VISIBLE);
                        }

                    }
                });
            }
        }).facing(QREader.BACK_CAM)
                .enableAutofocus(true)
                .height(cameraView.getHeight())
                .width(cameraView.getWidth())
                .build();

        qrEader.initAndStart(cameraView);*/

    }

    @Override
    protected void onPause() {
        super.onPause();

        //qrEader.releaseAndCleanup();
    }

    @Override
    protected void onResume() {
        super.onResume();

      //  qrEader.initAndStart(cameraView);
    }



}
