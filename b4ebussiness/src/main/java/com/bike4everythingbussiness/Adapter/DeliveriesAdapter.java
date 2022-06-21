package com.bike4everythingbussiness.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bike4everythingbussiness.Model.OrderDetails;
import com.bike4everythingbussiness.R;
import com.bike4everythingbussiness.Utils.Config;
import com.bike4everythingbussiness.Utils.Function;
import com.bike4everythingbussiness.Utils.Logger;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by manishsingh on 02/01/18.
 */

public class DeliveriesAdapter extends RecyclerView.Adapter<DeliveriesAdapter.MyViewHolder> {

    Context context;
    List<OrderDetails> deliveriesList;
    OnItemClickListener onItemClickListener;


    public DeliveriesAdapter(Context context, List<OrderDetails> deliveriesList) {
        this.context = context;
        this.deliveriesList = deliveriesList;
    }

    public void setListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.deliveries_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final OrderDetails orderDetails = deliveriesList.get(position);

        holder.orderId.setText(orderDetails.getDeliveryId());
        final int ordStats = orderDetails.getOrderStatus();
        String orderStatus = ordStats == Config.ORDER_COMPLETED ? orderDetails.getDeliveryStatus() : ordStats == Config.ORDER_PENDING ? orderDetails.getSchedule() : ordStats == Config.ORDER_ONGOING ? orderDetails.getDeliveryStatus() : "N/A";
        holder.orderStatus.setText(orderStatus);
        if(TextUtils.isEmpty(orderDetails.getAddedOn())){
            holder.dateTime.setText("");
        }else {
            holder.dateTime.setText(Function.dateTimeFormat(orderDetails.getAddedOn()));
        }

        holder.driverName.setText(orderDetails.getDriverName());
        if(!orderDetails.getDropAddressList().isEmpty()) {
            //holder.amount.setText("Rs. " + orderDetails.getDropAddressList().get(0).getDropAmount());
            holder.amount.setText("Rs. " + orderDetails.getAmount());
            holder.dropName.setText(orderDetails.getDropAddressList().get(0).getDropName());
            holder.dropAddress.setText(orderDetails.getDropAddressList().get(0).getDropAddress());
        }
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ordStats == Config.ORDER_COMPLETED) {
                    Logger.log("orderDetails", orderDetails.toString());
                    onItemClickListener.onClick(orderDetails);
                }
            }
        });

        return;


    }

    @Override
    public int getItemCount() {
        return deliveriesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.orderId)
        TextView orderId;
        @BindView(R.id.orderStatus)
        TextView orderStatus;
        @BindView(R.id.dateTime)
        TextView dateTime;
        @BindView(R.id.amount)
        TextView amount;
        @BindView(R.id.driverName)
        TextView driverName;
        @BindView(R.id.dropName)
        TextView dropName;
        @BindView(R.id.dropAddress)
        TextView dropAddress;
        View view;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            view = itemView;
        }
    }

    public interface OnItemClickListener {
        void onClick(OrderDetails orderDetails);
    }


}
