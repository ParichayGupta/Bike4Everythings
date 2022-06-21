package com.bike4everythingbussiness.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bike4everythingbussiness.Model.Items;
import com.bike4everythingbussiness.R;
import com.bumptech.glide.Glide;

import java.util.List;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.ALL;


public class SliderAdapter extends android.support.v4.view.PagerAdapter {
    private Activity activity;
    private List<Items> pagerItems;
    LayoutInflater inflater;
    String[] textArray;


    public SliderAdapter(Activity activity, List<Items> pagerItems) {
        this.activity = activity;
        this.pagerItems = pagerItems;

        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.e("movieview", String.valueOf(pagerItems));
    }

    @Override
    public int getCount() {
        return pagerItems.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);

    }


    public Object instantiateItem(ViewGroup container, final int position) {

        View view = this.inflater.inflate(R.layout.slider_adapter, container, false);


        Items data;
        data = pagerItems.get(position);

        ImageView my_profile_page_gallary_adapter_image = (ImageView) view.findViewById(R.id.my_profile_page_gallary_adapter_image);
        Glide.with(activity.getApplicationContext())
                .load(data.getUrl())
                .diskCacheStrategy(ALL)
                .into(my_profile_page_gallary_adapter_image);
        ((ViewPager) container).addView(view);

        return view;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

}
