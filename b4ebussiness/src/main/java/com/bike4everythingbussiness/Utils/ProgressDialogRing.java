package com.bike4everythingbussiness.Utils;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bike4everythingbussiness.R;


public class ProgressDialogRing {

     static ProgressDialogRing instance;
    MyDialog myDialog;

    private class MyDialog extends Dialog{
        private ImageView imageView;
        public MyDialog(@NonNull Context context, int themeResId) {
            super(context, R.style.CustomProgressDialog);

            WindowManager.LayoutParams wlmp = getWindow().getAttributes();
            wlmp.gravity = Gravity.CENTER_HORIZONTAL;
            getWindow().setAttributes(wlmp);
            setTitle("Please wait");
            setCancelable(false);
            setOnCancelListener(null);
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(120,120);
            imageView = new ImageView(context);
            imageView.setImageResource(themeResId);
            layout.addView(imageView, params);
            addContentView(layout, params);
        }

        @Override
        public void show() {
                try {
                    super.show();
                    RotateAnimation anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
                    anim.setInterpolator(new LinearInterpolator());
                    anim.setRepeatCount(Animation.INFINITE);
                    anim.setDuration(2200);
                    imageView.setAnimation(anim);
                    imageView.startAnimation(anim);
                }catch (WindowManager.BadTokenException w){}


        }

        @Override
        public void dismiss() {
            super.dismiss();
        }
    }

    private ProgressDialogRing(Context context) {
        this.myDialog = new MyDialog(context,R.drawable.loadingicon);

    }


    public static ProgressDialogRing getInstance(Context context) {
        if (instance == null) {
            instance = new ProgressDialogRing(context);
        }
        return instance;
    }

    public void show(){
        this.myDialog.show();
    }

    public void hide(){
        this.myDialog.dismiss();
    }

    public boolean isShowing(){
       return this.myDialog.isShowing();
    }

}
