package com.b4edriver.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.b4edriver.DriverApp.SelectServicesActivityDriver;
import com.b4edriver.Model.BusinessTypeDriver;
import com.b4edriver.R;

import java.util.ArrayList;

/**
 * Created by MAX on 09-Jul-16.
 */
public class SelectBusnTypeAdapterDriver extends RecyclerView.Adapter<SelectBusnTypeAdapterDriver.MyViewHolder> {

    private ArrayList<BusinessTypeDriver> businessTypeList;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name;
        public CheckBox checkBox;
        public ImageView image;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            image = (ImageView) view.findViewById(R.id.image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }


    public SelectBusnTypeAdapterDriver(Context context, ArrayList<BusinessTypeDriver> businessTypeList) {
        this.businessTypeList = businessTypeList;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return 0;
            default:
                return 0;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;

        switch (viewType) {
            case 0: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.selectbusinesstype_listrow_driver, parent, false);
                return new MyViewHolder(view);
            }

        }
        return null;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final BusinessTypeDriver businessType = businessTypeList.get(position);
        holder.name.setText(businessType.getName());
        holder.checkBox.setChecked(businessType.isSelected());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SelectServicesActivityDriver.businessTypeList.get(position).setSelected(isChecked);
            }
        });
        holder.image.setImageResource(businessType.getImage());
    }

    @Override
    public int getItemCount() {
        return businessTypeList.size();
    }
}