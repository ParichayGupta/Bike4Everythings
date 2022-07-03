package com.b4ebusinessdriver.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.b4ebusinessdriver.Model.DropAddress;
import com.b4ebusinessdriver.R;

import java.util.List;

/**
 * Created by manishsingh on 13/12/17.
 */

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.MyViewHolder> {
    private List<DropAddress> addressList;
    Context mContext;
    OnClickListener onClickListener;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        ImageButton call;
        View itemView;

        public MyViewHolder(View view) {
            super(view);
            itemView = view;
            txtName = view.findViewById(R.id.txtName);
            call = view.findViewById(R.id.call);
        }
    }

    public void setListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }


    public OrderListAdapter(Context context, List<DropAddress> addressList) {
        mContext = context;
        this.addressList = addressList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final DropAddress orderDetail = addressList.get(position);
        holder.txtName.setText(orderDetail.getDropName());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.clickItem(orderDetail);

            }
        });

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.callCustomer("tel:" +orderDetail.getDropMobile());

            }
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public interface OnClickListener{
        void clickItem(DropAddress orderDetail);
        void callCustomer(String number);
    }
}
