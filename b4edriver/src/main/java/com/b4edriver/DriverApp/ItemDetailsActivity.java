package com.b4edriver.DriverApp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.b4edriver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ItemDetailsActivity extends AppCompatActivity {

    TextView payment_mode_order;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_item_details);

        LinearLayout items = (LinearLayout) findViewById(R.id.items);
        TextView orderID = (TextView) findViewById(R.id.orderID);
        TextView payment_mode_order = (TextView) findViewById(R.id.payment_mode_order);
        TextView paymentDesc = (TextView) findViewById(R.id.paymentDesc);

        Button button = (Button) findViewById(R.id.button);

        try {
            JSONArray jsonArray = new JSONArray(getIntent().getStringExtra("data"));
            JSONObject jsonObject = jsonArray.getJSONObject(0);

            //orderDetails=[{"order_no":"1334","desc_one":"vsvsdbd","desc_three":"fmfm","desc_two":"fmfm"}]
            payment_mode_order.setText(jsonObject.getString("payment_mode_order"));
            if(jsonObject.getString("payment_mode_order").equalsIgnoreCase("paytm")){
                paymentDesc.setText(jsonObject.getString("message"));
            }else{
                paymentDesc.setText("");
            }
            orderID.setText(jsonObject.getString("order_no"));

            if(!jsonObject.getString("desc_one").equalsIgnoreCase("")){
                TextView textView = new TextView(ItemDetailsActivity.this);
                textView.setText("1) "+jsonObject.getString("desc_one"));
                textView.setTextSize(18);
                items.addView(textView);
            }
            if(!jsonObject.getString("desc_two").equalsIgnoreCase("")){
                TextView textView = new TextView(ItemDetailsActivity.this);
                textView.setText("2) "+jsonObject.getString("desc_two"));
                textView.setTextSize(18);
                items.addView(textView);
            }
            if(!jsonObject.getString("desc_three").equalsIgnoreCase("")){
                TextView textView = new TextView(ItemDetailsActivity.this);
                textView.setText("3) "+jsonObject.getString("desc_three"));
                textView.setTextSize(18);
                items.addView(textView);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                overridePendingTransition(R.anim.left_to_right,
                        R.anim.right_to_left);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right,
                R.anim.right_to_left);
    }
}
