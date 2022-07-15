package com.hvantage.b4eemp.utils;

import android.support.v4.view.ViewPager;
import android.view.View;

public class PageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;
    private float offset = -1;
    private float paddingLeft;
    private float minScale;
    private float minAlpha;

    public PageTransformer(float paddingLeft) {
        this.paddingLeft = paddingLeft;
        this.minAlpha = MIN_ALPHA;
        this.minScale = MIN_SCALE;
    }

    @Override
    public void transformPage(View page, float position) {
        if (offset == -1) {
            offset = paddingLeft / page.getMeasuredWidth();
        }
        if (position < -1) {
            page.setAlpha(0);
        } else if (position <= 1) {
            float scaleFactor = Math.max(minScale, 1 - Math.abs(position - offset));
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            float alphaFactor = Math.max(minAlpha, 1 - Math.abs(position - offset));
            page.setAlpha(alphaFactor);
        } else {
            page.setAlpha(0);
        }


    }


    /*private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0);

        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                view.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                view.setTranslationX(-horzMargin + vertMargin / 2);
            }
            //view.setTranslationX(-2 * position * view.getWidth());
            // Scale the page down (between MIN_SCALE and 1)
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            // Fade the page relative to its size.
            view.setAlpha(MIN_ALPHA +
                    (scaleFactor - MIN_SCALE) /
                            (1 - MIN_SCALE) * (1 - MIN_ALPHA));

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0);
        }
    }*/
}