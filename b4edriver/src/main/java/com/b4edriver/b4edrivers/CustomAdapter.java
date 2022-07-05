package com.b4edriver.b4edrivers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.b4edriver.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<DriverDatum> dataSet;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView datetime;
        TextView tripId, pickupAddress;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.datetime = (TextView) itemView.findViewById(R.id.datetime);
            this.tripId = (TextView) itemView.findViewById(R.id.tripId);
            this.pickupAddress = (TextView) itemView.findViewById(R.id.pickupAddress);
        }
    }
    public static class EmptyViewHolder extends RecyclerView.ViewHolder {


        TextView title;
        public EmptyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }

    public CustomAdapter(ArrayList<DriverDatum> data) {
        this.dataSet = data;
    }

    public void updateList(ArrayList<DriverDatum> list){
        dataSet = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(dataSet.size() > 0){
            return 1;
        }else {
            return 2;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        if(viewType == 1){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.b4edrivers_history_lidt_row, parent, false);

            view.setOnClickListener(HostoryActivity.myOnClickListener);

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.b4edrivers_empty, parent, false);

            EmptyViewHolder holder = new EmptyViewHolder(view);
            return holder;
        }


    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holders, final int listPosition) {

        if(holders instanceof MyViewHolder){
            MyViewHolder holder = (MyViewHolder) holders;
            DriverDatum driverDatum = dataSet.get(listPosition);

            holder.tripId.setText("# "+driverDatum.getId());
            holder.pickupAddress.setText(driverDatum.getStartRemark());
            holder.datetime.setText(dateTimeFormat(driverDatum.getAddedOn()));


        }else {
            EmptyViewHolder holder = (EmptyViewHolder) holders;
            holder.title.setText("History is not available to this date");
        }

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static String dateTimeFormat(String date_s) {
        // String date_s = " 2011-01-18 00:00:00.0";
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = dt.parse(date_s);
        } catch (ParseException e) {
            e.printStackTrace();
            return date_s;
        }
        SimpleDateFormat dt1 = new SimpleDateFormat("dd MMM yyyy");
        System.out.println(dt1.format(date));
        return dt1.format(date);
    }
}