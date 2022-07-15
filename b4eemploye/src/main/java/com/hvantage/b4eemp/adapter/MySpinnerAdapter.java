package com.hvantage.b4eemp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.hvantage.b4eemp.Database.DataModel;
import com.hvantage.b4eemp.R;

import java.util.List;

public class MySpinnerAdapter extends ArrayAdapter<DataModel> {
    List<DataModel> objects;
    public MySpinnerAdapter(@NonNull Context context, @NonNull List<DataModel> objects) {
        super(context, R.layout.spinner, objects);
        this.objects = objects;
    }
    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.spinner, null);
        DataModel rowItem = objects.get(position);


        TextView txtTitle = v.findViewById(R.id.name);
        txtTitle.setText(rowItem.getName());

        return v;
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.spinner, null);
        DataModel rowItem = objects.get(position);


        TextView txtTitle = v.findViewById(R.id.name);
        txtTitle.setText(rowItem.getName());

        return v;
    }
}
