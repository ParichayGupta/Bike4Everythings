package com.bike4everythingbussiness.Adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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

import com.bike4everythingbussiness.Model.ImageUploadModel;
import com.bike4everythingbussiness.R;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class UploadedImageAdapter extends RecyclerView.Adapter<UploadedImageAdapter.ViewHolder> {
    Context context;
    ArrayList<ImageUploadModel> modalList;
    int selected_position = 0;
    //    private ImageUploadModel modal;
    private ProgressDialog progressDialog;
    private Dialog dialog1;
    private String tempDimen = "", tempRemark = "";
    private String update_id = "";

    OnClickListener onClickListener;

    public UploadedImageAdapter(Context context, ArrayList<ImageUploadModel> modalList, OnClickListener onClickListener) {
        this.context = context;
        this.modalList = modalList;
        this.onClickListener = onClickListener;
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

        if (modal.getImage() != null)
            holder.image.setImageBitmap(modal.getImage());

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_position = position;
                deleteDialog(position);
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.editItem(modal.getImage(), modal.getDimension(),modal.getRemark(),position);
                    /*Intent intent = new Intent(context, EditPhotoActivity.class);
                    intent.putExtra("image_url", modal.getImage_url());
                    intent.putExtra("dimen", modal.getDimension());
                    intent.putExtra("remark", modal.getRemark());
                    intent.putExtra("update_id", modal.getUpdate_id());
                    context.startActivity(intent);*/
            }
        });


    }

    private void deleteDialog(final int position) {
        AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setMessage("Do you want to delete");
        builder.setTitle("Alert Delete");
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selected_position = 0;
            }
        });
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              //  new DeleteTask().execute(update_id);
                onClickListener.deleteItem(position);
            }
        });
        builder.show();

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
        ImageView edit, delete;
        LinearLayout ll_edit;

        public ViewHolder(View itemView) {
            super(itemView);
            tvRemark = (TextView) itemView.findViewById(R.id.tvRemark);
            tvDimen = (TextView) itemView.findViewById(R.id.tvDimen);
            tvDateTime = (TextView) itemView.findViewById(R.id.tvDateTime);
            image = (ImageView) itemView.findViewById(R.id.image);
            edit = (ImageView) itemView.findViewById(R.id.edit);
            delete = (ImageView) itemView.findViewById(R.id.delete);
            ll_edit = (LinearLayout) itemView.findViewById(R.id.ll_edit);
        }
    }

    public interface OnClickListener{
        void editItem(Bitmap image, String dimension, String remark, int position);
        void deleteItem(int position);
    }

    public JSONArray getData(){
        JSONArray array = new JSONArray();
        for(int i=0; i<modalList.size(); i++){
            JSONObject object = new JSONObject();
            Bitmap bitmapImage = modalList.get(i).getImage();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String image = Base64.encodeToString(byteArray, Base64.DEFAULT);
            try {
                object.put("dimension",modalList.get(i).getDimension());
                object.put("remark",modalList.get(i).getRemark());
                object.put("image",image);
                array.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array;
    }


}
