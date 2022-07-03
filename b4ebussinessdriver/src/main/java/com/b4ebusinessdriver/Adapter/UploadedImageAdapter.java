package com.b4ebusinessdriver.Adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.b4ebusinessdriver.Activity.HomeActivity;
import com.b4ebusinessdriver.Model.ImageUploadModel;
import com.b4ebusinessdriver.R;
import com.b4ebusinessdriver.Utils.AppPreferences;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.ArrayList;


public class UploadedImageAdapter extends RecyclerView.Adapter<UploadedImageAdapter.ViewHolder> {
    Context context;
    ArrayList<ImageUploadModel> modalList;
    int selected_position = 0;
    //    private ImageUploadModel modal;
    private ProgressDialog progressDialog;
    private Dialog dialog1;
    private String tempDimen = "", tempRemark = "";
    private String update_id = "";


    public UploadedImageAdapter(Context context, ArrayList<ImageUploadModel> modalList) {
        this.context = context;
        this.modalList = modalList;
    }

    @Override
    public UploadedImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.uploaded_image_layout, parent, false);
        UploadedImageAdapter.ViewHolder viewHolder = new UploadedImageAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final UploadedImageAdapter.ViewHolder holder, final int position) {
        final ImageUploadModel modal = modalList.get(position);
        Log.e("UploadedImageAdapter", " ImageModel >> " + modal);
        holder.tvRemark.setText(modal.getRemark());
        holder.tvDimen.setText(modal.getDimension());

       /* byte[] decodedString = Base64.decode(modal.getImage_url(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.image.setImageBitmap(decodedByte);*/

            Glide.with(context)
                    .asBitmap()
                    .load(modal.getImage_url())
                    .apply(new RequestOptions().centerCrop())
                    .apply(new RequestOptions().error(R.drawable.loadingicon))
                    .into(new BitmapImageViewTarget(holder.image) {
                @Override
                protected void setResource(Bitmap resource) {

                    holder.image.setImageBitmap(resource);
                }
            });


    }



    @Override
    public int getItemCount() {
        return modalList.size();
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRemark, tvDimen, tvDateTime;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            tvRemark = (TextView) itemView.findViewById(R.id.tvRemark);
            tvDimen = (TextView) itemView.findViewById(R.id.tvDimen);
            tvDateTime = (TextView) itemView.findViewById(R.id.tvDateTime);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }



}
