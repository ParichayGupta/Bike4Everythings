package com.hvantage.b4eemp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.b4elibrary.Logger;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.hvantage.b4eemp.R;
import com.hvantage.b4eemp.model.BookingData;
import com.hvantage.b4eemp.utils.AlarmService;
import com.hvantage.b4eemp.utils.Functions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    private List<BookingData> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context context;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    SimpleDateFormat dateFormatOut = new SimpleDateFormat("dd MMM yyyy HH:mm");

    public BookingAdapter(Context context, List<BookingData> data) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = data;
    }

    public void setData(List<BookingData> mData){
        this.mData = mData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.booking_list_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        final BookingData bookingData = mData.get(i);

        viewHolder.issueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onIssue(bookingData);
            }
        });
        viewHolder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onCancel(bookingData);
            }
        });
        viewHolder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onEdit(bookingData);
            }
        });

        viewHolder.id.setText(Html.fromHtml(Functions.getHeaderBlackText("Booking-Id : ", "# "+bookingData.getId())) , TextView.BufferType.SPANNABLE);

        viewHolder.name.setText(bookingData.getName());
        viewHolder.mobile.setText(bookingData.getMobile());
        viewHolder.bikename.setText(bookingData.getBikeName());
        viewHolder.servicename.setText(bookingData.getServiceName());
        viewHolder.pickaddress.setText(bookingData.getPickupAddress());
        viewHolder.dropaddress.setText(bookingData.getDropAddress());
        viewHolder.paymentMode.setText(bookingData.getPaymentType());
        viewHolder.payment.setText("₹ "+bookingData.getPayment());

        Logger.log("myCalendar", new Gson().toJson(bookingData));

        try {
            Date startDate = dateFormat.parse(bookingData.getPickupDate() +" "+ bookingData.getPickupTime());
            Date endDate = dateFormat.parse(bookingData.getDropoffDate() +" "+ bookingData.getDropoffTime());

            long msDiff = endDate.getTime() - startDate.getTime();
            long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);
            long hourssDiff = TimeUnit.MILLISECONDS.toHours(msDiff);
            long minutesDiff = TimeUnit.MILLISECONDS.toMinutes(msDiff);

            Calendar calendar = Calendar.getInstance();


            if(String.valueOf(msDiff).contains("-")){
                viewHolder.date.setText("Expired");
                viewHolder.date.setBackgroundColor(context.getResources().getColor(R.color.red_tomato));
            }else if(minutesDiff < 15){
                viewHolder.date.setText("Expire "+ DateUtils.getRelativeTimeSpanString(endDate.getTime(),
                        calendar.getTimeInMillis(),
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE));
                viewHolder.date.setBackgroundColor(context.getResources().getColor(R.color.green));
            }else {
                viewHolder.date.setText(bookingData.getAddedon());
                viewHolder.date.setBackgroundColor(context.getResources().getColor(R.color.white));
            }

            viewHolder.daterange.setText( DateUtils.formatDateRange(context, startDate.getTime(), endDate.getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME));

            viewHolder.duration.setText(bookingData.getDuration());


        } catch (ParseException e) {
            e.printStackTrace();
        }


        Glide.with(context)
                .load(bookingData.getCustomerImage())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .apply(new RequestOptions().placeholder(R.drawable.ic_perm_identity_black_24dp))
                .apply(RequestOptions.circleCropTransform())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        viewHolder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        viewHolder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(viewHolder.customerimage);



    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView id;
        TextView date;
        TextView name;
        TextView mobile;
        Button issueBtn;
        Button editBtn;
        Button btnCancel;
        TextView dropaddress;
        TextView pickaddress;
        TextView bikename;
        TextView servicename;
        TextView daterange;
        TextView duration;
        TextView paymentMode;
        TextView payment;
        ImageView customerimage;
        ProgressBar progressBar;
        LinearLayout form;

        ViewHolder(View view) {
            super(view);
            id = view.findViewById(R.id.id);

             date=view.findViewById(R.id.date);

             name=view.findViewById(R.id.name);

             mobile=view.findViewById(R.id.mobile);

             issueBtn=view.findViewById(R.id.issueBtn);

             editBtn=view.findViewById(R.id.editBtn);

             btnCancel=view.findViewById(R.id.btnCancel);

             dropaddress=view.findViewById(R.id.dropaddress);

             pickaddress=view.findViewById(R.id.pickaddress);

             bikename=view.findViewById(R.id.bikename);

             servicename=view.findViewById(R.id.servicename);

             daterange=view.findViewById(R.id.daterange);

             duration =view.findViewById(R.id.duration);

             paymentMode=view.findViewById(R.id.paymentMode);

             payment=view.findViewById(R.id.payment);

             customerimage=view.findViewById(R.id.customerimage);

             progressBar=view.findViewById(R.id.progressBar);

            form=view.findViewById(R.id.form);
        }

    }
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onIssue(BookingData bookingData);
        void onCancel(BookingData bookingData);
        void onEdit(BookingData bookingData);
    }
}
