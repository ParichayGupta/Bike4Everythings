package com.hvantage.b4eemp.Wedgets;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewSwitcher;


import com.hvantage.b4eemp.R;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomDateTimePicker implements View.OnClickListener {
    private Calendar calendar_date = null;
    private Calendar min_calendar_date = null;
    private ViewSwitcher viewSwitcher;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private AppCompatButton btnDate, btnTime;
    Context context;

    private ICustomDateTimeListener iCustomDateTimeListener = null;

    private Dialog dialog;

    private boolean is24HourView = true, isAutoDismiss = true;

    private int selectedHour, selectedMinute;
    private int TIME_PICKER_INTERVAL = 30;

    public CustomDateTimePicker(Context context,
                                ICustomDateTimeListener customDateTimeListener) {
        iCustomDateTimeListener = customDateTimeListener;
        this.context = context;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.date_time_picker);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                resetData();
            }
        });
    }



    private void setView() {
        btnDate = dialog.findViewById(R.id.btn_date);
        btnTime = dialog.findViewById(R.id.btn_time);
        dialog.findViewById(R.id.btn_set).setOnClickListener(this);
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(this);
        viewSwitcher = dialog.findViewById(R.id.view_switcher);
        datePicker = dialog.findViewById(R.id.date_picker);
        timePicker = dialog.findViewById(R.id.time_picker);

        btnDate.setOnClickListener(this);
        btnTime.setOnClickListener(this);

        timePicker.setIs24HourView(is24HourView);
        //timePicker.setCurrentHour(selectedHour);
        //timePicker.setCurrentMinute(selectedMinute);

        setTimePickerInterval(timePicker);

        if (((selectedMinute % TIME_PICKER_INTERVAL) != 0)) {
            int minuteFloor = (selectedMinute + TIME_PICKER_INTERVAL) - (selectedMinute % TIME_PICKER_INTERVAL);
            selectedMinute = minuteFloor + (selectedMinute == (minuteFloor + 1) ? TIME_PICKER_INTERVAL : 0);
            if (selectedMinute >= 60) {
                selectedMinute = selectedMinute % 60;
                selectedHour++;
            }

            timePicker.setCurrentHour(selectedHour);
            timePicker.setCurrentMinute(selectedMinute / TIME_PICKER_INTERVAL);
        }

        datePicker.setMinDate(min_calendar_date.getTimeInMillis());
        datePicker.updateDate(calendar_date.get(Calendar.YEAR),
                calendar_date.get(Calendar.MONTH),
                calendar_date.get(Calendar.DATE));

        btnDate.performClick();
    }

    private void setTimePickerInterval(TimePicker timePicker) {
        try {

            NumberPicker minutePicker = timePicker.findViewById(Resources.getSystem().getIdentifier(
                    "minute", "id", "android"));
            minutePicker.setMinValue(0);
            minutePicker.setMaxValue((60 / TIME_PICKER_INTERVAL) - 1);
            List<String> displayedValues = new ArrayList<String>();
            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                displayedValues.add(String.format("%02d", i));
            }
            minutePicker.setDisplayedValues(displayedValues.toArray(new String[0]));
        } catch (Exception e) {
            Log.e("datetime", "Exception: " + e);
        }
    }

    public CustomDateTimePicker setTimePickerInterval(int time_picker_interval) {
            TIME_PICKER_INTERVAL = time_picker_interval;
        return this;
    }

    public void showDialog() {
        if (!dialog.isShowing()) {
            if (calendar_date == null)
                calendar_date = Calendar.getInstance();

            selectedHour = calendar_date.get(Calendar.HOUR_OF_DAY);
            selectedMinute = calendar_date.get(Calendar.MINUTE);

            dialog.show();

            setView();
        }
    }

    public CustomDateTimePicker setAutoDismiss(boolean isAutoDismiss) {
        this.isAutoDismiss = isAutoDismiss;
        return this;
    }

    public CustomDateTimePicker dismissDialog() {
        if (!dialog.isShowing())
            dialog.dismiss();

        return this;
    }

    public CustomDateTimePicker setDate(Calendar calendar) {
        if (calendar != null)
            calendar_date = calendar;
        return this;
    }


    public CustomDateTimePicker setMinDate(Calendar calendar) {
        if(calendar != null)
            min_calendar_date = calendar;
        return this;
    }



    public CustomDateTimePicker setDate(Date date) {
        if (date != null) {
            calendar_date = Calendar.getInstance();
            calendar_date.setTime(date);
        }
        return this;
    }

    public CustomDateTimePicker setDate(int year, int month, int day) {
        if (month < 12 && month >= 0 && day < 32 && day >= 0 && year > 100
                && year < 3000) {
            calendar_date = Calendar.getInstance();
            calendar_date.set(year, month, day);
        }
        return this;
    }

    public CustomDateTimePicker setTimeIn24HourFormat(int hourIn24Format, int minute) {
        if (hourIn24Format < 24 && hourIn24Format >= 0 && minute >= 0
                && minute < 60) {
            if (calendar_date == null)
                calendar_date = Calendar.getInstance();

            calendar_date.set(calendar_date.get(Calendar.YEAR),
                    calendar_date.get(Calendar.MONTH),
                    calendar_date.get(Calendar.DAY_OF_MONTH), hourIn24Format,
                    minute);

            is24HourView = true;
        }
        return this;
    }

    public CustomDateTimePicker setTimeIn12HourFormat(int hourIn12Format, int minute,
                                                      boolean isAM) {
        if (hourIn12Format < 13 && hourIn12Format > 0 && minute >= 0
                && minute < 60) {
            if (hourIn12Format == 12)
                hourIn12Format = 0;

            int hourIn24Format = hourIn12Format;

            if (!isAM)
                hourIn24Format += 12;

            if (calendar_date == null)
                calendar_date = Calendar.getInstance();

            calendar_date.set(calendar_date.get(Calendar.YEAR),
                    calendar_date.get(Calendar.MONTH),
                    calendar_date.get(Calendar.DAY_OF_MONTH), hourIn24Format,
                    minute);

            is24HourView = false;
        }
        return this;
    }

    public CustomDateTimePicker set24HourFormat(boolean is24HourFormat) {
        is24HourView = is24HourFormat;
        return this;
    }

    public interface ICustomDateTimeListener {
        void onSet(Dialog dialog, Calendar calendarSelected,
                   Date dateSelected, int year, String monthFullName,
                   String monthShortName, int monthNumber, int date,
                   String weekDayFullName, String weekDayShortName, int hour24,
                   int hour12, int min, int sec, String AM_PM);

        void onCancel();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_date) {
            btnTime.setBackgroundColor(context.getResources().getColor(R.color.black_shad));
            btnDate.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));

            if (viewSwitcher.getCurrentView() != datePicker) {
                viewSwitcher.showPrevious();
            }
        } else if (i == R.id.btn_time) {
            btnTime.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            btnDate.setBackgroundColor(context.getResources().getColor(R.color.black_shad));
            if (viewSwitcher.getCurrentView() == datePicker) {
                viewSwitcher.showNext();
            }
        } else if (i == R.id.btn_set) {
            if (iCustomDateTimeListener != null) {
                int month = datePicker.getMonth();
                int year = datePicker.getYear();
                int day = datePicker.getDayOfMonth();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    selectedHour = timePicker.getHour();
                    selectedMinute = timePicker.getMinute();
                } else {
                    selectedHour = timePicker.getCurrentHour();
                    selectedMinute = timePicker.getCurrentMinute() * TIME_PICKER_INTERVAL;
                }

                calendar_date.set(year, month, day, selectedHour,
                        selectedMinute);

                Calendar c_datetime = Calendar.getInstance();

                if (calendar_date.getTimeInMillis() >= c_datetime.getTimeInMillis()) {

                    iCustomDateTimeListener.onSet(dialog, calendar_date,
                            calendar_date.getTime(), calendar_date
                                    .get(Calendar.YEAR),
                            getMonthFullName(calendar_date.get(Calendar.MONTH)),
                            getMonthShortName(calendar_date.get(Calendar.MONTH)),
                            calendar_date.get(Calendar.MONTH), calendar_date
                                    .get(Calendar.DAY_OF_MONTH),
                            getWeekDayFullName(calendar_date
                                    .get(Calendar.DAY_OF_WEEK)),
                            getWeekDayShortName(calendar_date
                                    .get(Calendar.DAY_OF_WEEK)), calendar_date
                                    .get(Calendar.HOUR_OF_DAY),
                            getHourIn12Format(calendar_date
                                    .get(Calendar.HOUR_OF_DAY)), calendar_date
                                    .get(Calendar.MINUTE), calendar_date
                                    .get(Calendar.SECOND), getAMPM(calendar_date));
                    if (dialog.isShowing() && isAutoDismiss)
                        dialog.dismiss();
                } else {
                    //it's before current'
                    Toast.makeText(context, "Invalid Time", Toast.LENGTH_SHORT).show();

                }
            }
        } else if (i == R.id.btn_cancel) {
            if (iCustomDateTimeListener != null)
                iCustomDateTimeListener.onCancel();
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

    /**
     * @param date       date in String
     * @param fromFormat format of your <b>date</b> eg: if your date is 2011-07-07
     *                   09:09:09 then your format will be <b>yyyy-MM-dd hh:mm:ss</b>
     * @param toFormat   format to which you want to convert your <b>date</b> eg: if
     *                   required format is 31 July 2011 then the toFormat should be
     *                   <b>d MMMM yyyy</b>
     * @return formatted date
     */
    public static String convertDate(String date, String fromFormat,
                                     String toFormat) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fromFormat, Locale.getDefault());
            Date d = simpleDateFormat.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);

            simpleDateFormat = new SimpleDateFormat(toFormat, Locale.getDefault());
            simpleDateFormat.setCalendar(calendar);
            date = simpleDateFormat.format(calendar.getTime());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return date;
    }

    private String getMonthFullName(int monthNumber) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, monthNumber);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
        simpleDateFormat.setCalendar(calendar);

        return simpleDateFormat.format(calendar.getTime());
    }

    private String getMonthShortName(int monthNumber) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, monthNumber);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM", Locale.getDefault());
        simpleDateFormat.setCalendar(calendar);

        return simpleDateFormat.format(calendar.getTime());
    }

    private String getWeekDayFullName(int weekDayNumber) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, weekDayNumber);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        simpleDateFormat.setCalendar(calendar);

        return simpleDateFormat.format(calendar.getTime());
    }

    private String getWeekDayShortName(int weekDayNumber) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, weekDayNumber);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE", Locale.getDefault());
        simpleDateFormat.setCalendar(calendar);

        return simpleDateFormat.format(calendar.getTime());
    }

    private int getHourIn12Format(int hour24) {
        int hourIn12Format = 0;

        if (hour24 == 0)
            hourIn12Format = 12;
        else if (hour24 <= 12)
            hourIn12Format = hour24;
        else
            hourIn12Format = hour24 - 12;

        return hourIn12Format;
    }

    private String getAMPM(Calendar calendar) {
        return (calendar.get(Calendar.AM_PM) == (Calendar.AM)) ? "AM"
                : "PM";
    }

    private CustomDateTimePicker resetData() {
        calendar_date = null;
        is24HourView = true;
        return this;
    }

    public static String pad(int integerToPad) {
        if (integerToPad >= 10 || integerToPad < 0)
            return String.valueOf(integerToPad);
        else
            return "0" + integerToPad;
    }
}