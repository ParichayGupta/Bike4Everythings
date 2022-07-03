package com.b4ebusinessdriver.Utils;

import android.support.v4.view.ViewPager;

public interface PageIndicator extends ViewPager.OnPageChangeListener {
    int EDIT_MODE_COUNT = 5;
    int EDIT_MODE_PAGE = 2;
    String EDIT_MODE_TITLE = "Page %d";

    int INVALID_POINTER = -1;

    void notifyDataSetChanged();

    void setCurrentItem(int i);

    void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener);

    void setViewPager(ViewPager viewPager);

    void setViewPager(ViewPager viewPager, int i);


}