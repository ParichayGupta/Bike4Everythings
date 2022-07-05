package com.b4edriver.CommonClasses.Wedgets;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

public class VehicleEditText extends EditText implements TextWatcher {

    private boolean isDelete;


    public VehicleEditText(Context context){
        super(context);
        init();

    }

    public VehicleEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VehicleEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }




    private void init() {
        setFilters(new InputFilter[] {new InputFilter.LengthFilter(13)});
        setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        setInputType(InputType.TYPE_CLASS_TEXT);
        addTextChangedListener(this);
        setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    isDelete = true;
                }else {
                    isDelete = false;
                }

                return false;
            }
        });
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String val = s.toString();
        String a = "";
        String b = "";
        String c = "";
        String d = "";

        if (isDelete) {
            if(val != null && s.toString().length() > 0){
                //val = val.replace(" ", "");
                if (val.length() < 2){
                    setInputType(InputType.TYPE_CLASS_TEXT);
                }else if (val.length() < 5){
                    setInputType(InputType.TYPE_CLASS_NUMBER);
                }else if (val.length() < 8){
                    setInputType(InputType.TYPE_CLASS_TEXT);
                }else if (val.length() <= 10){
                    setInputType(InputType.TYPE_CLASS_NUMBER);
                }
            }

            return;
        }



        if (val != null && val.length() > 0) {
            val = val.replace(" ", "");
            if (val.length() >= 2) {
                a = val.substring(0, 2);
            } else if (val.length() < 2) {
                a = val.substring(0, val.length());
            }

            if (val.length() >= 4) {
                b = val.substring(2, 4);
                //c = val.substring(4, 6);
            } else if (val.length() > 2 && val.length() < 4) {
                b = val.substring(2, val.length());
            }

            if (val.length() >= 6) {
                c = val.substring(4, 6);
                //c = val.substring(4, val.length());
            } else if (val.length() > 4 && val.length() < 6) {
                c = val.substring(4, val.length());
            }

            if (val.length() >= 10) {
                d = val.substring(6, 10);
                //c = val.substring(4, val.length());
            } else if (val.length() > 6 && val.length() < 10) {
                d = val.substring(6, val.length());
            }


            StringBuffer stringBuffer = new StringBuffer();
            if (a != null && a.length() > 0) {
                stringBuffer.append(a);
                if (a.length() == 2) {
                    stringBuffer.append(" ");
                    if (val.length() == 2)
                        setInputType(InputType.TYPE_CLASS_NUMBER);
                }
            }
            if (b != null && b.length() > 0) {
                stringBuffer.append(b);
                if (b.length() == 2) {
                    stringBuffer.append(" ");
                    if (val.length() == 4)
                    setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }
            if (c != null && c.length() > 0) {
                stringBuffer.append(c);
                if (c.length() == 2) {
                    stringBuffer.append(" ");
                    if (val.length() == 6)
                    setInputType(InputType.TYPE_CLASS_NUMBER);
                }
            }
            if (d != null && d.length() > 0) {
                stringBuffer.append(d);
            }
            removeTextChangedListener(this);
            setText(stringBuffer.toString());
            setSelection(getText().toString().length());
            addTextChangedListener(this);
        } else {
            removeTextChangedListener(this);
            setText("");
            addTextChangedListener(this);
        }
    }

    private void changeView(String val, String a, String b, String c, String d){

    }
}
