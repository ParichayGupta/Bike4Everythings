package com.bike4everythingbussiness.Utils;

import android.support.v4.view.ViewPager;

public interface PageIndicator extends ViewPager.OnPageChangeListener {
    public static final int EDIT_MODE_COUNT = 5;
    public static final int EDIT_MODE_PAGE = 2;
    public static final String EDIT_MODE_TITLE = "Page %d";

    public static final int INVALID_POINTER = -1;

    void notifyDataSetChanged();

    void setCurrentItem(int i);

    void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener);

    void setViewPager(ViewPager viewPager);

    void setViewPager(ViewPager viewPager, int i);


}