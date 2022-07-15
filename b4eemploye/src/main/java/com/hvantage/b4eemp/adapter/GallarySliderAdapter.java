package com.hvantage.b4eemp.adapter;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.hvantage.b4eemp.R;
import com.hvantage.b4eemp.model.Items;
import java.util.List;

public class GallarySliderAdapter extends PagerAdapter {
    List<Items> itemsList;


    public GallarySliderAdapter(List<Items> itemsList) {
        this.itemsList = itemsList;
    }

    public void swapDataSet(List<Items> items) {
        this.itemsList = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return itemsList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = LayoutInflater.from(container.getContext()).inflate(R.layout.gallary_slider_row, container, false);

        Items items = itemsList.get(position);

        ImageView imageView = itemView.findViewById(R.id.imageView);
        final ProgressBar progressbar = itemView.findViewById(R.id.progressbar);

        Glide.with(container.getContext())
                .asBitmap()
                .load(TextUtils.isEmpty(items.getImage()) ? items.getDrawableImage() : items.getImage())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        progressbar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);




        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


}