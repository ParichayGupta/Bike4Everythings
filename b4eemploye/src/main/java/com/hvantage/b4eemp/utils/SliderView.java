package com.hvantage.b4eemp.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.hvantage.b4eemp.R;
import com.hvantage.b4eemp.adapter.GallarySliderAdapter;
import com.hvantage.b4eemp.model.Items;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SliderView {
    private static final SliderView ourInstance = new SliderView();
    GallarySliderAdapter sliderAdapter;
    Runnable timeCounter;
    Handler handler = new Handler();
    Context context;
    ViewPager viewPager;
    TabLayout tabLayout;
    Integer[] images = {R.drawable.b4e_bike1, R.drawable.b4e_bike2};
    private FixedSpeedScroller mScroller = null;
    private int currentIndex = 0;
    private int conditionValue = 1;
    private SliderView() {
    }

    public static SliderView getInstance() {
        return ourInstance;
    }

    public void slideRequest(Context context, final ViewPager viewPager, TabLayout tabLayout) {
        this.context = context;
        this.viewPager = viewPager;
        this.tabLayout = tabLayout;

        List<Items> items = new ArrayList<>();
        for (int i = 0; i < images.length; i++) {
            Items items1 = new Items();
            items1.setDrawableImage(images[i]);
            items.add(items1);
        }

        slide(items);
    }

    public void slide(final List<Items> items) {
        // List<Items> items = new ArrayList<>();

        sliderAdapter = new GallarySliderAdapter(items);

        try {
            // Class<?> viewpager = viewPager.getClass();
            Field scroller = viewPager.getClass().getDeclaredField("mScroller");
            scroller.setAccessible(true);
            mScroller = new FixedSpeedScroller(context,
                    new DecelerateInterpolator(), true);
            scroller.set(viewPager, mScroller);
        } catch (Exception ignored) {
        }

        viewPager.setOffscreenPageLimit(10);
        viewPager.setAdapter(sliderAdapter);
        viewPager.setPageTransformer(true, new PageTransformer(viewPager.getPaddingLeft()));
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, r.getDisplayMetrics());
        viewPager.setPageMargin((int) (-1 * px));
        tabLayout.setupWithViewPager(viewPager, true);

        timeCounter = new Runnable() {

            @Override
            public void run() {
               /* if ((currentIndex + 1) > sliderAdapter.getCount()) {
                    currentIndex = 0;
                } else {
                    currentIndex++;
                }*/

                if (currentIndex == sliderAdapter.getCount() - 1) {
                    conditionValue = -1;
                    currentIndex += conditionValue;
                    viewPager.setCurrentItem(currentIndex, false);
                } else if (currentIndex == 0) {
                    conditionValue = 1;
                    currentIndex += conditionValue;
                    viewPager.setCurrentItem(currentIndex, true);
                }else {
                    currentIndex += conditionValue;
                    viewPager.setCurrentItem(currentIndex, true);
                }



                handler.postDelayed(timeCounter, 6 * 1000);


            }
        };
        handler.postAtTime(timeCounter, 3 * 1000);

    }


    private class FixedSpeedScroller extends Scroller {

        private int mDuration = 1000;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        public void setScrollDuration(int duration) {
            mDuration = duration;
        }
    }


}
