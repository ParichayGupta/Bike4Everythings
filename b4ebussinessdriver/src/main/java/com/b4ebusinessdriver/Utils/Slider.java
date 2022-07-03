package com.b4ebusinessdriver.Utils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


import com.b4ebusinessdriver.Adapter.SliderAdapter;
import com.b4ebusinessdriver.Model.Items;
import com.b4ebusinessdriver.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class Slider {
    static int currentPage=0;

    public static void ViewMethodAdd(Activity activity, CirclePageIndicator circlePageIndicator, final ViewPager viewPager)
    {


        List<Items> itemses = new ArrayList<>();
        for (int i=1; i<5; i++){
            Items items = new Items();
            items.setUrl("https://www.bike4everything.in/wp-content/uploads/2016/05/cropped-logo-1.png");
            //items.setUrl("http://business.bike4everything.in/assets/img/screenshot/"+i+".jpg");
            //items.setUrl("http://192.168.1.8/b2bimages/b2b"+i+".jpeg");
            itemses.add(items);
        }

        setSliderIndicatorProperties(activity,circlePageIndicator);
        final SliderAdapter adapter = new SliderAdapter(activity, itemses);
        viewPager.setAdapter(adapter);
        viewPager.invalidate();

        circlePageIndicator.setViewPager(viewPager);
        viewPager.setPageTransformer(false, new FadePageTransformer());
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                PointF downP = new PointF();
                PointF curP = new PointF();
                int act = event.getAction();
                if (act == MotionEvent.ACTION_DOWN
                        || act == MotionEvent.ACTION_MOVE
                        || act == MotionEvent.ACTION_UP) {
                    ((ViewGroup) v).requestDisallowInterceptTouchEvent(true);
                    if (downP.x == curP.x && downP.y == curP.y) {
                        return false;
                    }
                }
                return false;
            }
        });

        final Handler handler = new Handler();


        final Runnable update = new Runnable() {
            @Override
            public void run() {
                if (currentPage == adapter.getCount() - 1) {
                    currentPage = 0;
                }else {
                    currentPage++;
                }

                viewPager.setCurrentItem(currentPage, true);
            }
        };


        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 3000, 5000);    }

     private static void setSliderIndicatorProperties(final Activity activity, CirclePageIndicator circlePageIndicator) {
        float density = activity.getResources().getDisplayMetrics().density;
        circlePageIndicator.setBackgroundColor(0);
        circlePageIndicator.setRadius(5.0f * density);
        circlePageIndicator.setPageColor(-3355444);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            circlePageIndicator.setFillColor(activity.getColor(R.color.white));
        } else {
            circlePageIndicator.setFillColor(activity.getResources().getColor(R.color.white));
        }
        circlePageIndicator.setStrokeColor(Color.BLUE);
        circlePageIndicator.setStrokeWidth(2500 * density);
    }

}
