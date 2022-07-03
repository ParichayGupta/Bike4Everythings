package com.b4ebusinessdriver.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.b4ebusinessdriver.Model.OrderDetails;
import com.b4ebusinessdriver.R;
import com.b4ebusinessdriver.Utils.Config;
import com.b4ebusinessdriver.Utils.Function;

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
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.deliveries_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        try {
            final OrderDetails orderDetails = deliveriesList.get(position);



            holder.orderId.setText(orderDetails.getDeliveryId() + "");
            int ordStats = orderDetails.getOrderStatus();
            String orderStatus = ordStats == Config.ORDER_COMPLETED ? "COMPLETED" : ordStats == Config.ORDER_PENDING ? "PENDING" : ordStats == Config.ORDER_ONGOING ? "PROCESSING" : "N/A";
            holder.orderStatus.setText(orderDetails.getDeliveryStatus());
            holder.dateTime.setText(Function.dateTimeFormat(orderDetails.getAddedOn()));
            holder.driverName.setText("N/A");
            //holder.amount.setText("Rs. " + orderDetails.getDropAddressList().get(0).getDropAmount());
            holder.amount.setText("Rs. " + orderDetails.getAmount());
            holder.dropName.setText(orderDetails.getDropAddressList().get(0).getDropName());
            holder.dropAddress.setText(orderDetails.getDropAddressList().get(0).getDropAddress());
        }catch (Exception e){
            e.printStackTrace();
        }
        return;


    }

    @Override
    public int getItemCount() {
        return deliveriesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView orderId;
        TextView orderStatus;
        TextView dateTime;
        TextView amount;
        TextView driverName;
        TextView dropName;
        TextView dropAddress;

        public MyViewHolder(View itemView) {
            super(itemView);

            orderId = (TextView) itemView.findViewById(R.id.orderId);
            orderStatus = (TextView) itemView.findViewById(R.id.orderStatus);
            dateTime = (TextView) itemView.findViewById(R.id.dateTime);
            amount = (TextView) itemView.findViewById(R.id.amount);
            driverName = (TextView) itemView.findViewById(R.id.driverName);
            dropName = (TextView) itemView.findViewById(R.id.dropName);
            dropAddress = (TextView) itemView.findViewById(R.id.dropAddress);
        }
    }

    public interface OnItemClickListener {
        void onClick();
    }


}
