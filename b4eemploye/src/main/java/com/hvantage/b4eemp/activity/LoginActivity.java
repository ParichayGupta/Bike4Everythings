package com.hvantage.b4eemp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.b4elibrary.Logger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hvantage.b4eemp.R;
import com.hvantage.b4eemp.utils.AppPreferance;
import com.hvantage.b4eemp.utils.CustomProgressBar;
import com.hvantage.b4eemp.utils.SliderView;

import java.util.concurrent.TimeUnit;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


    EditText mobile;

    Button next;
    LinearLayout mobileView;
    EditText otp;
    Button otpverify;
    LinearLayout otpView;
    Button resend;
    ViewPager viewPager;
    TabLayout tabLayout;

    private FirebaseAuth mAuth;
    private String TAG = "LoginActivity_";

    CustomProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppPreferance.isUserLogedin(LoginActivity.this)) {
            gotoMainScreen();
        }
        setContentView(R.layout.activity_login);
        initView();

        getSupportActionBar().hide();

        SliderView.getInstance().slideRequest(LoginActivity.this, viewPager, tabLayout);


        progressBar = new CustomProgressBar();

        mAuth = FirebaseAuth.getInstance();
        updateView(STATE_INITIALIZED);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                // mVerificationInProgress = false;
                Logger.log(TAG, "onVerificationCompleted:" + credential.toString());
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Logger.log(TAG, "onVerificationFailed:" + e.getMessage());
                if (progressBar.getDialog().isShowing()) {
                    progressBar.getDialog().dismiss();
                }
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Error!")
                        .setMessage(e.getMessage())
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {

                Logger.log(TAG, "onCodeSent:" + verificationId + " \ntoken:" + token);
                if (progressBar.getDialog().isShowing()) {
                    progressBar.getDialog().dismiss();
                }
                mVerificationId = verificationId;
                mResendToken = token;
                updateView(STATE_CODE_SENT);

            }
        };

    }

    private void initView() {

         mobile=findViewById(R.id.mobile);

         next=findViewById(R.id.next);

         mobileView=findViewById(R.id.mobileView);

         otp=findViewById(R.id.otp);

         otpverify=findViewById(R.id.otpverify);

         otpView=findViewById(R.id.otpView);

         resend=findViewById(R.id.resend);

         viewPager=findViewById(R.id.viewPager);

         tabLayout=findViewById(R.id.tab_layout);

         next.setOnClickListener(this);
        otpverify.setOnClickListener(this);
        resend.setOnClickListener(this);


    }

    private void updateView(int code) {
        switch (code) {
            case STATE_INITIALIZED:
                mobileView.setVisibility(View.VISIBLE);
                otpView.setVisibility(View.GONE);
                break;
            case STATE_CODE_SENT:
                mobileView.setVisibility(View.GONE);
                otpView.setVisibility(View.VISIBLE);
                break;
        }

    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.next) {
            String _mobile = mobile.getText().toString();
            if (TextUtils.isEmpty(_mobile)) {
                Toast.makeText(this, "Please enter your mobile number.", Toast.LENGTH_SHORT).show();
            } else {
                progressBar.show(LoginActivity.this);
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91" + _mobile,
                        60,
                        TimeUnit.SECONDS,
                        this,
                        mCallbacks);
            }
        } else if (i == R.id.otpverify) {
            String _otp = otp.getText().toString();
            if (TextUtils.isEmpty(_otp)) {
                Toast.makeText(this, "Please enter OTP.", Toast.LENGTH_SHORT).show();
            } else {
                progressBar.show(LoginActivity.this);
                verifyPhoneNumberWithCode(_otp);
            }
        } else if (i == R.id.resend) {
            progressBar.show(LoginActivity.this);
            String _mobile_ = mobile.getText().toString();
            resendVerificationCode(_mobile_, mResendToken);
        }
    }

    private void verifyPhoneNumberWithCode(String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                token);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (progressBar.getDialog().isShowing()) {
                            progressBar.getDialog().dismiss();
                        }
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Logger.log(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();

                            AppPreferance.setUser(LoginActivity.this, user.getUid());
                            gotoMainScreen();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                                otp.setError("Invalid code.");
                            }

                        }
                    }
                });
    }

    private void gotoMainScreen() {
        Intent intent = new Intent(LoginActivity.this, BookingActivity.class);
        startActivity(intent);
        finish();
    }

}
