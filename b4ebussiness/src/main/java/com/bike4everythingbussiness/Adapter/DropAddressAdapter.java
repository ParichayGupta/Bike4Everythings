package com.bike4everythingbussiness.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bike4everythingbussiness.Activity.MainActivity;
import com.bike4everythingbussiness.Model.DropAddress;
import com.bike4everythingbussiness.R;
import com.bike4everythingbussiness.Utils.AppPreferance;
import com.bike4everythingbussiness.Utils.Logger;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.bike4everythingbussiness.Activity.MainActivity.DROP_ADDRESS_ADAPTER_REQUESTCODE;

/**
 * Created by manishsingh on 02/01/18.
 */

public class DropAddressAdapter extends RecyclerView.Adapter<DropAddressAdapter.MyViewHolder> {

    Context context;
    List<DropAddress> dropAddressList;
    OnItemClickListener onItemClickListener;
    private double dropLat;
    private double dropLng;
    private TextView dropaddress;
    boolean iconHide;

    public DropAddressAdapter(Context context, List<DropAddress> dropAddressList) {
        this.context = context;
        this.dropAddressList = dropAddressList;
    }

    public void setListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }



    public void clear(){
        dropAddressList.clear();
        notifyDataSetChanged();
    }

    public void setData(DropAddress dropAddress){
        dropAddressList.add(dropAddress);
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.drop_address_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final DropAddress dropAddress = dropAddressList.get(position);

        if(dropAddress == null)
            return;

        if(iconHide){
            holder.deleteItem.setVisibility(View.GONE);
        }else {
            holder.deleteItem.setVisibility(View.VISIBLE);
        }

        holder.itemcount.setText( (position+1) +"");
        holder.itemname.setText(dropAddress.getDropName()+" | "+ dropAddress.getDropMobile());
        holder.paymentType.setText("COD: Rs."+ dropAddress.getDropAmount());

        holder.mobile.setText(dropAddress.getDropMobile());
        holder.name.setText(dropAddress.getDropName());
        holder.address.setText(dropAddress.getDropAddress());
        holder.amount.setText(dropAddress.getDropAmount());

        holder.address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dropaddress = holder.address;
                dropaddress.setEnabled(false);
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    ((MainActivity)context).startActivityForResult(builder.build((MainActivity)context), DROP_ADDRESS_ADAPTER_REQUESTCODE);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        holder.displayItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!iconHide) {
                    holder.displayItem.setVisibility(View.GONE);
                    holder.showMobileNo.setVisibility(View.VISIBLE);
                    holder.showOtherDetails.setVisibility(View.VISIBLE);

                    dropaddress = holder.address;
                    dropaddress.setText(dropAddress.getDropAddress());
                    dropLat = dropAddress.getDropLat();
                    dropLng = dropAddress.getDropLng();
                }
            }
        });
        holder.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(holder.mobile.getText())){
                    holder.mobile.startAnimation(((MainActivity)context).shake);
                }else if(TextUtils.isEmpty(holder.name.getText())){
                    holder.name.startAnimation(((MainActivity)context).shake);
                }else if(TextUtils.isEmpty(holder.address.getText())){
                    holder.address.startAnimation(((MainActivity)context).shake);
                }else if(TextUtils.isEmpty(holder.amount.getText())){
                    holder.amount.startAnimation(((MainActivity)context).shake);
                }else {
                    holder.displayItem.setVisibility(View.VISIBLE);
                    holder.showMobileNo.setVisibility(View.GONE);
                    holder.showOtherDetails.setVisibility(View.GONE);

                    DropAddress dropAddress = new DropAddress();
                    dropAddress.setId(String.valueOf(AppPreferance.getUserid((MainActivity)context)));
                    dropAddress.setDropName(holder.name.getText().toString());
                    dropAddress.setDropMobile(holder.mobile.getText().toString());
                    dropAddress.setDropAddress(dropaddress.getText().toString());
                    dropAddress.setDropAmount(holder.amount.getText().toString());
                    dropAddress.setDropLat(dropLat);
                    dropAddress.setDropLng(dropLng);
                    dropAddress.setSelect(true);


                    DropAddress dropAddressDb = ((MainActivity) context).databaseHandler.getDropAddress(holder.mobile.getText().toString());
                    if(dropAddressDb == null){
                        ((MainActivity) context).databaseHandler.addDropAddress(dropAddress);
                    }else {
                        ((MainActivity) context).databaseHandler.updateDropAddress(dropAddress);
                    }

                    dropAddressList.set(position, dropAddress);

                    notifyItemChanged(position);
                }



            }
        });
        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               holder.displayItem.setVisibility(View.VISIBLE);
               holder.showMobileNo.setVisibility(View.GONE);
               holder.showOtherDetails.setVisibility(View.GONE);
            }
        });
        Logger.log("POSITIONADP all", position+"");
        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)context).databaseHandler.updateDropSelectStatus(dropAddress.getDropMobile(), false);
                onItemClickListener.dropAddressRemove(getItemCount());
                removeItem(position);

            }
        });

    }

    private void removeItem(int i) {
        dropAddressList.remove(i);
        notifyItemRemoved(i);

    }

    @Override
    public int getItemCount() {
        return dropAddressList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.itemcount)
        TextView itemcount;
        @BindView(R.id.itemname)
        TextView itemname;
        @BindView(R.id.paymentType)
        TextView paymentType;
        @BindView(R.id.deleteItem)
        ImageButton deleteItem;
        @BindView(R.id.displayItem)
        RelativeLayout displayItem;
        @BindView(R.id.mobile)
        EditText mobile;
        @BindView(R.id.searchMobile)
        ImageButton searchMobile;
        @BindView(R.id.showMobileNo)
        LinearLayout showMobileNo;
        @BindView(R.id.name)
        EditText name;
        @BindView(R.id.address)
        EditText address;
        @BindView(R.id.amount)
        EditText amount;
        @BindView(R.id.AddBtn)
        Button updateBtn;
        @BindView(R.id.cancelBtn)
        TextView cancelBtn;
        @BindView(R.id.showOtherDetails)
        LinearLayout showOtherDetails;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener{
        void dropAddressRemove(int itemCount);
    }

    public void hideIcon(){
        iconHide = true;
    }

    public  void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Logger.log("MyAdapter", "onActivityResult");
        if (requestCode == DROP_ADDRESS_ADAPTER_REQUESTCODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(intent, context);

                String placename = String.format("%s", place.getName());
                String addresses = String.format("%s", place.getAddress());
                String phone = String.format("%s", place.getPhoneNumber());
                dropLat = place.getLatLng().latitude;
                dropLng = place.getLatLng().longitude;
                dropaddress.setText(TextUtils.isEmpty(addresses) ? placename : addresses);
            }
            dropaddress.setEnabled(true);
        }
    }
}
