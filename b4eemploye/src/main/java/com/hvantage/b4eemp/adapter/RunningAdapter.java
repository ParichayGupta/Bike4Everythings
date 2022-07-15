package com.hvantage.b4eemp.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.TimeUtils;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.hvantage.b4eemp.R;
import com.hvantage.b4eemp.model.BookingData;
import com.hvantage.b4eemp.utils.Functions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


import static android.text.format.DateUtils.*;

public class RunningAdapter extends RecyclerView.Adapter<RunningAdapter.ViewHolder> {


    private List<BookingData> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context context;
    SimpleDateFormat dateFormatInteger = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    public RunningAdapter(Context context, List<BookingData> data) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = data;
    }

    public void setData(List<BookingData> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.running_list_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        final BookingData bookingData = mData.get(i);



        viewHolder.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onUpdate(bookingData);
            }
        });
        viewHolder.btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onComplete(bookingData);
            }
        });
        viewHolder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onDetails(bookingData);
            }
        });
        viewHolder.track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onTrack(bookingData);
            }
        });

        viewHolder.id.setText(Html.fromHtml(Functions.getHeaderBlackText("Booking-id"," #" +bookingData.getId())), TextView.BufferType.SPANNABLE);

        final SimpleDateFormat format = new SimpleDateFormat("dd MMM, yyyy hh:mm a");


        viewHolder.name.setText(bookingData.getAltName());
        viewHolder.mobile.setText(bookingData.getAltMobile());
        viewHolder.bikenumber.setText(bookingData.getBikeNumber());
        viewHolder.servicename.setText(bookingData.getServiceName());

        try {
            Date endDate = dateFormatInteger.parse(bookingData.getDropoffDate() +" "+ bookingData.getDropoffTime());
            Calendar calendar = Calendar.getInstance();

            long msDiff = endDate.getTime() - calendar.getTime().getTime();

            long minutesDiff = TimeUnit.MILLISECONDS.toMinutes(msDiff);

            if(String.valueOf(msDiff).contains("-")){
                viewHolder.daytime.setText("Expired");
                viewHolder.daytime.setBackgroundColor(context.getResources().getColor(R.color.red_tomato));
            }else if(minutesDiff < 15){
                viewHolder.daytime.setText("Expire "+ DateUtils.getRelativeTimeSpanString(endDate.getTime(),
                        calendar.getTimeInMillis(),
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE));
                viewHolder.daytime.setBackgroundColor(context.getResources().getColor(R.color.green));
            }else {
                viewHolder.daytime.setText("");
                viewHolder.daytime.setBackgroundColor(context.getResources().getColor(R.color.white));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }



        Glide.with(context)
                .load(bookingData.getAltCustomerImage())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .apply(new RequestOptions().placeholder(R.drawable.ic_perm_identity_black_24dp))
                .apply(RequestOptions.circleCropTransform())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        viewHolder.progressBarCImg.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        viewHolder.progressBarCImg.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(viewHolder.customerImage);

        Glide.with(context)
                .load(bookingData.getIdProofImage())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .apply(new RequestOptions().placeholder(R.drawable.ic_id_proof))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        viewHolder.progressBarIDProof.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        viewHolder.progressBarIDProof.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(viewHolder.idProof);


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView id;
        TextView daytime;
        TextView name;
        TextView mobile;
        TextView bikenumber;
        TextView servicename;
        ImageView customerImage;
        ProgressBar progressBarCImg;
        ImageView idProof;
        ProgressBar progressBarIDProof;
        AppCompatButton btnDetails;
        AppCompatButton btnUpdate;
        AppCompatButton btnComplete;
        ImageView track;
        LinearLayout form;

        ViewHolder(View view) {
            super(view);

             id=view.findViewById(R.id.id);

             daytime=view.findViewById(R.id.daytime);

             name=view.findViewById(R.id.name);

             mobile=view.findViewById(R.id.mobile);

             bikenumber=view.findViewById(R.id.bikenumber);

             servicename=view.findViewById(R.id.servicename);

             customerImage=view.findViewById(R.id.customerImage);

             progressBarCImg=view.findViewById(R.id.progressBarCImg);

             idProof=view.findViewById(R.id.idProof);

             progressBarIDProof=view.findViewById(R.id.progressBarIDProof);

             btnDetails=view.findViewById(R.id.btnDetails);

             btnUpdate=view.findViewById(R.id.btnUpdate);

             btnComplete=view.findViewById(R.id.btnComplete);

             track=view.findViewById(R.id.track);

             form=view.findViewById(R.id.form);
        }

    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onComplete(BookingData bookingData);
        void onUpdate(BookingData bookingData);

        void onDetails(BookingData bookingData);
        void onTrack(BookingData bookingData);
    }
}
