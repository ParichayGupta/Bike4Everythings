package com.b4edriver.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b4edriver.Model.AllMessageDriver;
import com.b4edriver.CommonClasses.ServerConnection.Util;
import com.b4edriver.R;
import com.b4elibrary.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by MMFA-MANISH on 11/7/2015.
 */
public class AllMessageAdapterDriver extends BaseAdapter {

    private LayoutInflater inflater;
    Context context;
    List<AllMessageDriver> modelList = new ArrayList<AllMessageDriver>();

    public AllMessageAdapterDriver(Context context, List<AllMessageDriver> modelList){
        this.context = context;
        this.modelList = modelList;
    }

    @Override
    public int getCount() {
        return modelList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AllMessageDriver model = modelList.get(position);
        if (inflater == null)
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.notification_list_driver, null);


        TextView header = (TextView) convertView.findViewById(R.id.heading);
        TextView description = (TextView) convertView.findViewById(R.id.description);
        TextView date_tv = (TextView) convertView.findViewById(R.id.date_tv);

        date_tv.setText(model.getDate());
        header.setText("Message");
        description.setText(model.getText());

        Logger.log("Messageaaaaa", model.getText()+">>>>>>>>>>>>");

        return convertView;
    }
}
