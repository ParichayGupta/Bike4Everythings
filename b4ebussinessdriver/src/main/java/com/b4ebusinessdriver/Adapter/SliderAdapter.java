package com.b4ebusinessdriver.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.b4ebusinessdriver.Model.Items;
import com.b4ebusinessdriver.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

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
        return view == object;

    }


    public Object instantiateItem(ViewGroup container, final int position) {

        View view = this.inflater.inflate(R.layout.slider_adapter, container, false);


        Items data;
        data = pagerItems.get(position);

        ImageView my_profile_page_gallary_adapter_image = view.findViewById(R.id.my_profile_page_gallary_adapter_image);
        Glide.with(activity.getApplicationContext())
                .load(data.getUrl())
                .apply(new RequestOptions().diskCacheStrategy(ALL))
                .into(my_profile_page_gallary_adapter_image);
        container.addView(view);

        return view;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

}
