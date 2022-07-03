package com.b4ebusinessdriver.Widgets;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.b4ebusinessdriver.Activity.SigninActivity;
import com.b4ebusinessdriver.Activity.SignupActivity;
import com.b4ebusinessdriver.Activity.SplashActivity;
import com.b4ebusinessdriver.R;

/**
 * Created by manishsingh on 17/03/18.
 */

public class SignInView extends FrameLayout {

    private View v,rootv;
    private Button signup_btn,signin_btn;
    public static int myHeight=0;

    public SignInView(@NonNull Context context) {
        super(context);
        initView();
    }

    public SignInView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SignInView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }
    private void initView() {
        v= LayoutInflater.from(getContext()).inflate(R.layout.sigin_view, null);

        rootv=v.findViewById(R.id.logingscreen);
        signup_btn= (Button)v.findViewById(R.id.signup_btn);
        signin_btn= (Button)v.findViewById(R.id.signin_btn);
        signin_btn.setOnClickListener(signIn);
        signup_btn.setOnClickListener(signUp);
        post(new Runnable(){
            @Override
            public void run() {
                myHeight=getHeight();
            }
        });

        addView(v);
    }

    public void hide(final boolean animate){
        rootv.post(new Runnable(){
            @Override
            public void run() {

                animate().setStartDelay(0);
                animate().setDuration(animate?500:0);
                animate().translationY(rootv.getHeight());
                animate().start();

            }
        });
    }

    public void show(){

        animate().setStartDelay(500);
        animate().setDuration(1000);
        animate().translationY(0);
        animate().start();

    }

    View.OnClickListener signUp = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getContext().startActivity(new Intent(getContext(), SignupActivity.class));
        }
    };
    View.OnClickListener signIn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getContext().startActivity(new Intent(getContext(), SigninActivity.class));
        }
    };
}
