package com.b4edriver.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.b4edriver.Model.DriverLoginTime;
import com.b4edriver.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by MAX on 09-Jul-16.
 */
public class DriverLoginTimeAdapter extends RecyclerView.Adapter<DriverLoginTimeAdapter.MyViewHolder> {

    Context context;
    private List<DriverLoginTime> driverLoginTimes;
    private int lastCheckedPosition = -1;

    public DriverLoginTimeAdapter(Context context, List<DriverLoginTime> driverLoginTimes) {
        this.driverLoginTimes = driverLoginTimes;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.logintime_driver_list, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final DriverLoginTime driverLoginTime = driverLoginTimes.get(position);
        DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String date = driverLoginTime.getDateD();
        try {
            holder.mDate.setText(outputDateFormat.format(inputDateFormat.parse(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dur = driverLoginTime.getTotalTime();
        if (dur.equalsIgnoreCase("0.0") || dur.equalsIgnoreCase("")) {
            dur = "00:00:00";
        }

        String[] duration = dur.split(":");

        holder.mloginDuration.setText(String.format("%1sH:%2sM:%3sS", duration[0], duration[1], duration[2]));
        String login[] = driverLoginTime.getLoginIn().split(" ");
        String logout[] = driverLoginTime.getLoginOut().split(" ");

        DateFormat inputTimeFormat = new SimpleDateFormat("HH:mm:ss");
        DateFormat outputTimeFormat = new SimpleDateFormat("KK:mm a");

        if (driverLoginTime.getLoginIn().equalsIgnoreCase("") ||
                driverLoginTime.getLoginIn().contains("00:00:00")) {
            holder.mLoginTime.setText("N/A");
        } else {
            String input = login[1];
            try {
                holder.mLoginTime.setText(outputTimeFormat.format(inputTimeFormat.parse(input)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (driverLoginTime.getLoginOut().equalsIgnoreCase("") ||
                driverLoginTime.getLoginOut().contains("00:00:00")) {
            holder.mLogoutTime.setText("N/A");
        } else {
            String input = logout[1];
            try {
                holder.mLogoutTime.setText(outputTimeFormat.format(inputTimeFormat.parse(input)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public int getItemCount() {
        return driverLoginTimes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mDate, mloginDuration, mLoginTime, mLogoutTime;
        public RadioButton radio;
        public ImageView image;

        public MyViewHolder(View view) {
            super(view);
            mDate = (TextView) view.findViewById(R.id.mDate);
            mloginDuration = (TextView) view.findViewById(R.id.mloginDuration);
            mLoginTime = (TextView) view.findViewById(R.id.mLoginTime);
            mLogoutTime = (TextView) view.findViewById(R.id.mLogoutTime);

        }

    }
}