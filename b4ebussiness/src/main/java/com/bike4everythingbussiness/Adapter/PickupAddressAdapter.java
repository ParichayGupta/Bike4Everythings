package com.bike4everythingbussiness.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bike4everythingbussiness.DatabaseHandler;
import com.bike4everythingbussiness.Model.PickAddress;
import com.bike4everythingbussiness.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by manishsingh on 02/01/18.
 */

public class PickupAddressAdapter extends RecyclerView.Adapter<PickupAddressAdapter.MyViewHolder> {

    Context context;
    List<PickAddress> pickupAddressList;
    OnItemSelectLIstener onItemSelectLIstener;

    public PickupAddressAdapter(Context context, List<PickAddress> pickupAddressList) {
        this.context = context;
        this.pickupAddressList = pickupAddressList;
    }

    public void setListener(OnItemSelectLIstener onItemSelectLIstener){
        this.onItemSelectLIstener = onItemSelectLIstener;
    }

    public void setData(PickAddress pickAddress){

        for(int i=0; i<pickupAddressList.size(); i++){
            pickupAddressList.get(i).setSelect(false);
        }

        pickupAddressList.add(pickAddress);
        notifyDataSetChanged();
    }

    public void delete(int id){

        for(int i=0; i<pickupAddressList.size(); i++){
            if(pickupAddressList.get(i).getId() == id){
                pickupAddressList.remove(i);
            }
        }
        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pickup_address_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final PickAddress pickAddress = pickupAddressList.get(position);

            holder.itemselect.setChecked(pickAddress.isSelect());


        holder.number.setText(pickAddress.getPickName()+" | "+ pickAddress.getPickMobile());
        holder.address.setText(pickAddress.getPickAddress());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for(int i=0; i<pickupAddressList.size(); i++){
                    if(i == position){
                        pickupAddressList.get(i).setSelect(true);

                    }else {
                        pickupAddressList.get(i).setSelect(false);

                    }

                    notifyItemChanged(i);
                }
                onItemSelectLIstener.pickupAddressSelect(pickAddress.getPickName()+" | "+ pickAddress.getPickMobile()+"\n"+pickAddress.getPickAddressName());
                new DatabaseHandler(context).updatePickupSelectStatus(String.valueOf(pickAddress.getId()),true);
            }
        });

        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemSelectLIstener.pickupAddressDelete(pickAddress.getId());

            }
        });


    }

    @Override
    public int getItemCount() {
        return pickupAddressList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.itemselect)
        RadioButton itemselect;
        @BindView(R.id.number)
        TextView number;
        @BindView(R.id.address)
        TextView address;
        @BindView(R.id.deleteItem)
        ImageButton deleteItem;
        @BindView(R.id.displayItem)
        RelativeLayout displayItem;

        View view;

        public MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemSelectLIstener{
        void pickupAddressSelect(String name);
        void pickupAddressDelete(int id);
    }


}
