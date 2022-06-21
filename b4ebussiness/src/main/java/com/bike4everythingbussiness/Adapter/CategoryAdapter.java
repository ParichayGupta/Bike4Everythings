package com.bike4everythingbussiness.Adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bike4everythingbussiness.Model.Items;
import com.bike4everythingbussiness.R;

import java.util.List;

/**
 * Created by manishsingh on 23/11/17.
 */

public class CategoryAdapter extends ArrayAdapter<Items> {

    public List<Items> mList;
    private Context mContext;
    LayoutInflater inflater;
    int resource = 0;

    public CategoryAdapter(@NonNull Context context, int resource, @NonNull List<Items> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mList = objects;
        this.resource = resource;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }



    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
        View row = inflater.inflate(R.layout.spinerlist, parent, false);

        final Items product = mList.get(position);
        TextView label        = (TextView)row.findViewById(R.id.lable);
       label.setText(product.getName());


        if(position == 0){
            label.setTextColor(mContext.getResources().getColor(R.color.green1));
        }

        return row;
    }


}