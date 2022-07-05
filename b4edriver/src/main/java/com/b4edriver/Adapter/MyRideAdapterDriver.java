package com.b4edriver.Adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b4edriver.DriverApp.TaxiWaitingActivityDriver;
import com.b4edriver.DriverApp.TripAcceptActivityDriver;
import com.b4edriver.DriverApp.TripStartedActivityDriver;
import com.b4edriver.Model.TripDriver;
import com.b4edriver.CommonClasses.Utils.AppConstantDriver;
import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.b4edriver.CommonClasses.Utils.Function;
import com.b4edriver.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by MMFA-MANISH on 11/7/2015.
 */
public class MyRideAdapterDriver extends BaseAdapter {

    private LayoutInflater inflater;
    Context context;
    List<TripDriver> modelList = new ArrayList<TripDriver>();

    public MyRideAdapterDriver(Context context, List<TripDriver> modelList){
        this.context = context;
        this.modelList = modelList;
    }

    @Override
    public int getCount() {
        return modelList.size();
    }

    @Override
    public Object getItem(int position) {
        return modelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final TripDriver model = modelList.get(position);
        if (inflater == null)
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.myride_list_driver, null);


        TextView tripid_tv = (TextView) convertView.findViewById(R.id.trip_id);
        TextView source_address_tv = (TextView) convertView.findViewById(R.id.source_address_tv);
        TextView destination_address_tv = (TextView) convertView.findViewById(R.id.destination_address_tv);
        TextView pickup_datetime_tv = (TextView) convertView.findViewById(R.id.pickup_datetime_tv);
        TextView fare_tv = (TextView) convertView.findViewById(R.id.fare_tv);
        TextView status_tv = (TextView) convertView.findViewById(R.id.status_tv);

       // Logger.log("responsefffff",model.getFare()+"::::dfgfdgdf");

        if(AppConstantDriver.END_TRIP.equalsIgnoreCase(model.getTripStatus())
                || AppConstantDriver.CANCEL.equalsIgnoreCase(model.getTripStatus())){

            status_tv.setBackgroundResource(R.drawable.btn_red_normal_driver);
        }else if(AppConstantDriver.COMPLETE.equalsIgnoreCase(model.getTripStatus())){

            status_tv.setBackgroundResource(R.drawable.btn_green_normal);
        }else{
            status_tv.setBackgroundResource(R.drawable.btn_white_bg_driver);
        }

        tripid_tv.setText(String.valueOf(model.getId()));
        source_address_tv.setText(model.getSourceAddress());
        destination_address_tv.setText(model.getDestinationAddress());

        String date = Function.getDateFormate(model.getDate());

        pickup_datetime_tv.setText(date);


        if(model.getFare().equalsIgnoreCase("")){
            fare_tv.setText("N/A");
        }else{
            fare_tv.setText("Rs.\n"+model.getFare()+"/-");
        }

        status_tv.setText("Status - "+model.getMonth());

        status_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppPreferencesDriver.getActivity(context).equalsIgnoreCase(TripAcceptActivityDriver.class.getName()) ||
                        AppPreferencesDriver.getActivity(context).equalsIgnoreCase(TaxiWaitingActivityDriver.class.getName()) ||
                        AppPreferencesDriver.getActivity(context).equalsIgnoreCase(TripStartedActivityDriver.class.getName())) {
                    Class<?> lastActivity;
                    try {
                        lastActivity = Class.forName(AppPreferencesDriver.getActivity(context));


                        Intent intent = new Intent(context, lastActivity);

                            intent.putExtra("tripDetails", model);

                        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();

                    }
                }else{
                  /*  if(AppConstantDriver.END_TRIP.equalsIgnoreCase(model.getTripStatus())
                            || AppConstantDriver.COMPLETE.equalsIgnoreCase(model.getTripStatus())
                            || AppConstantDriver.CANCEL.equalsIgnoreCase(model.getTripStatus())){

                        status_tv.setBackgroundResource(R.drawable.btn_red_normal_driver);
                    }else{
                        status_tv.setBackgroundResource(R.drawable.btn_white_bg_driver);
                    }*/

                    if(AppConstantDriver.ALLOT.equalsIgnoreCase(model.getTripStatus())){
                        Intent intent = new Intent(context, TripAcceptActivityDriver.class);

                        intent.putExtra("tripDetails", model);

                        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }else if(AppConstantDriver.ARRIVAL.equalsIgnoreCase(model.getTripStatus())){
                        Intent intent = new Intent(context, TaxiWaitingActivityDriver.class);

                        intent.putExtra("tripDetails", model);

                        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }else if(AppConstantDriver.START_TRIP.equalsIgnoreCase(model.getTripStatus())){
                        Intent intent = new Intent(context, TripStartedActivityDriver.class);

                        intent.putExtra("tripDetails", model);

                        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }


            }
        });

        return convertView;
    }
}
