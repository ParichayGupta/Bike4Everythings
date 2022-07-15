package com.hvantage.b4eemp.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
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

import java.text.SimpleDateFormat;
import java.util.List;


public class CompletedAdapter extends RecyclerView.Adapter<CompletedAdapter.ViewHolder> {


    Context context;
    private List<BookingData> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public CompletedAdapter(Context context, List<BookingData> data) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = data;
    }

    public void setData(List<BookingData> mData) {
        this.mData = mData;
        notifyDataSetChanged();
        //String[] strings = new String[mData.size()];
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

        viewHolder.btnComplete.setVisibility(View.GONE);
        viewHolder.btnUpdate.setVisibility(View.GONE);

        viewHolder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onDetails(bookingData);
            }
        });

        viewHolder.id.setText("# " + bookingData.getId());

        final SimpleDateFormat format = new SimpleDateFormat("dd MMM, yyyy hh:mm a");

        viewHolder.name.setText(bookingData.getAltName());
        viewHolder.mobile.setText(bookingData.getAltMobile());
        viewHolder.bikenumber.setText(bookingData.getBikeNumber());
        viewHolder.servicename.setText(bookingData.getServiceName());
        viewHolder.daytime.setText("COMPLETED");
        viewHolder.daytime.setBackgroundColor(context.getResources().getColor(R.color.green));


        Glide.with(context)
                .load(bookingData.getAltCustomerImage())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .apply(new RequestOptions().placeholder(R.drawable.ic_perm_identity_black_24dp))
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

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onDetails(BookingData bookingData);
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
        LinearLayout form;

        ViewHolder(View view) {
            super(view);
            id = view.findViewById(R.id.id);

            daytime = view.findViewById(R.id.daytime);

            name = view.findViewById(R.id.name);

            mobile = view.findViewById(R.id.mobile);

            bikenumber = view.findViewById(R.id.bikenumber);

            servicename = view.findViewById(R.id.servicename);

            customerImage = view.findViewById(R.id.customerImage);

            progressBarCImg = view.findViewById(R.id.progressBarCImg);

            idProof = view.findViewById(R.id.idProof);

            progressBarIDProof = view.findViewById(R.id.progressBarIDProof);

            btnDetails = view.findViewById(R.id.btnDetails);

            btnUpdate = view.findViewById(R.id.btnUpdate);

            btnComplete = view.findViewById(R.id.btnComplete);

            form = view.findViewById(R.id.form);
        }

    }
}
