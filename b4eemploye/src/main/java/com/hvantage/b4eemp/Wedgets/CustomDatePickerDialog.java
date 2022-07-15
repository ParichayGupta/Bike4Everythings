package com.hvantage.b4eemp.Wedgets;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CustomDatePickerDialog extends DatePickerDialog{

    DatePicker mDatePicker;
    OnDateSetListener mDateSetListener;

    @SuppressLint("NewApi")
    public CustomDatePickerDialog(Context context, OnDateSetListener callBack,
                                  int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);

        mDateSetListener = callBack;
        /*mDatePicker = new DatePickerDialog(context,theme,callBack, year, monthOfYear, dayOfMonth);

        mDatePicker.getDatePicker().init(2013, 7, 16, this);

        updateTitle(year, monthOfYear);*/

    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                if (mDateSetListener != null) {
                    mDateSetListener.onDateSet(mDatePicker, mDatePicker.getYear(),mDatePicker.getMonth(), mDatePicker.getDayOfMonth());

                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            Field timePickerField = classForid.getField("datePicker");
            mDatePicker = findViewById(timePickerField.getInt(null));
            mDatePicker.setMinDate(Calendar.getInstance().getTimeInMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* public void onDateChanged(DatePicker view, int year,
                              int month, int day) {
        updateTitle(year, month);
    }
    private void updateTitle(int year, int month) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
//       mCalendar.set(Calendar.DAY_OF_MONTH, day);
        mDatePicker.setTitle(getFormat().format(mCalendar.getTime()));

    }

    public DatePickerDialog getPicker(){

        return this.mDatePicker;
    }

    public SimpleDateFormat getFormat(){
        return new SimpleDateFormat("MM/dd/yyyy");
    };*/
}