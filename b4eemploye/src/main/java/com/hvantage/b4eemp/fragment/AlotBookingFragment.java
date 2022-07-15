package com.hvantage.b4eemp.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.b4elibrary.Logger;
import com.b4erental.Model.RentTripData;
import com.b4erental.Model.User;
import com.b4erental.RentalActivity;
import com.b4erental.Utils.DialogManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hvantage.b4eemp.Database.Database;
import com.hvantage.b4eemp.R;
import com.hvantage.b4eemp.activity.IssueActivity;
import com.hvantage.b4eemp.adapter.BookingAdapter;
import com.hvantage.b4eemp.model.BookingData;
import com.hvantage.b4eemp.model.BookingModel;
import com.hvantage.b4eemp.utils.AppConstants;
import com.hvantage.b4eemp.utils.AppPreferance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.b4elibrary.AppConstants.KEY_RENTTRIPDATA;
import static com.b4elibrary.AppConstants.KEY_USER;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link AlotBookingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlotBookingFragment extends Fragment implements BookingAdapter.ItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    RecyclerView recyclerView;
    SwipeRefreshLayout pullToRefresh;
    ImageView norecordfound;

    BookingAdapter bookingAdapter;




    public AlotBookingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlotBookingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlotBookingFragment newInstance(String param1, String param2) {
        AlotBookingFragment fragment = new AlotBookingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_new_booking, container, false);
        initView(view);


        pullToRefresh.setOnRefreshListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        bookingAdapter = new BookingAdapter(getActivity(), new ArrayList<BookingData>());
        bookingAdapter.setClickListener(this);
        recyclerView.setAdapter(bookingAdapter);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                newBookingReciver, new IntentFilter("NEWRENTBOOK"));

        return view;
    }

    private void initView(View view) {

         recyclerView=view.findViewById(R.id.recyclerView);

         pullToRefresh=view.findViewById(R.id.pullToRefresh);

         norecordfound=view.findViewById(R.id.norecordfound);
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onIssue(final BookingData bookingData) {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());

        builderSingle.setTitle("Is the Person Booked has come to Pick?");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Yes");
        arrayAdapter.add("No");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), IssueActivity.class);
                intent.putExtra("bookingData", bookingData);
                intent.putExtra("isOwner", !strName.equalsIgnoreCase("No"));
                startActivity(intent);
            }
        });
        builderSingle.show();


    }

    @Override
    public void onCancel(final BookingData bookingData) {
        AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
        alert.setTitle("Cancel Booking");
        alert.setMessage("Are you sure you want to cancel booking?");
        alert.setButton(Dialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("method", AppConstants.UPDATERENTBOKINGABORT);
                    jsonObject.put("trip_id", bookingData.getId());
                    jsonObject.put("booking_send_by", "EMP");
                    new CancelBooking(jsonObject).execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        alert.setButton(Dialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();

    }

    @Override
    public void onEdit(BookingData bookingData) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", AppConstants.MYRENT_ALLBYTRIP);
            jsonObject.put("trip_id", bookingData.getId());
            new getBookingDataById(jsonObject).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", AppConstants.GETALLRENTCURRENTBOOKING);
            jsonObject.put("location_id", AppPreferance.getLocationId(getActivity().getApplicationContext()));
            new getAlotedData(jsonObject).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pullToRefresh.setRefreshing(false);
    }



    private class getAlotedData extends AsyncTask<String, String, String> {

        private JSONObject jsonObject;

        public getAlotedData(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.e("Request_Response", AppConstants.RENT_TRIP_BOOK_API + "\n" + jsonObject.toString());

            try {
                OkHttpClient client = new OkHttpClient();
                final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(AppConstants.RENT_TRIP_BOOK_API)
                        .post(body)
                        .build();
                okhttp3.Response response = client.newCall(request).execute();
                String result = response.body().string();
                Log.e("Request_Response", result);

                return result;

            } catch (IOException e) {
                e.printStackTrace();

            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(TextUtils.isEmpty(s)){
                return;
            }
            try {

                BookingModel bookingModel = new Gson().fromJson(s, BookingModel.class);
                ArrayList<BookingData> bookingData = new ArrayList<>();
                if(bookingModel.getStatus().equals("200")){
                    bookingData = bookingModel.getResult();
                }

                bookingAdapter.setData(bookingData);

                for(BookingData data : bookingData){
                    if(Database.getInstance(getActivity()).isBookingIDExits(data.getId())){
                        Database.getInstance(getActivity()).updateBookingata(data);
                    }else {
                        Database.getInstance(getActivity()).addBookingData(data);
                    }
                }


                if(bookingData.isEmpty()){
                    norecordfound.setVisibility(View.VISIBLE);
                    setAlphaAnimation(norecordfound);
                }else {
                    norecordfound.setVisibility(View.GONE);
                }
            }catch (NullPointerException e){}catch (Exception e){}

        }
    }
    private class getBookingDataById extends AsyncTask<String, String, String> {
        DialogManager myDialogManager;
        private JSONObject jsonObject;

        public getBookingDataById(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
            myDialogManager = new DialogManager();
            myDialogManager.showProcessDialog(getActivity(), "Featching booking data", false);
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.e("Request_Response", AppConstants.RENT_BOOKINGS + "\n" + jsonObject.toString());

            try {
                OkHttpClient client = new OkHttpClient();
                final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(AppConstants.RENT_BOOKINGS)
                        .post(body)
                        .build();
                okhttp3.Response response = client.newCall(request).execute();
                String result = response.body().string();
                Log.e("Request_Response", result);

                return result;

            } catch (IOException e) {
                e.printStackTrace();

            }
            return "";
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if(TextUtils.isEmpty(response)){
                myDialogManager.stopProcessDialog();
                return;
            }
            RentTripData rentTripData = null;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if (status.equalsIgnoreCase("200")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                    rentTripData = new Gson().fromJson(jsonObject1.toString(), RentTripData.class);
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
            if(rentTripData != null){
                User user = AppPreferance.getUserIdName(getActivity());
                Intent intent = new Intent(getActivity(), RentalActivity.class);
                intent.putExtra(KEY_RENTTRIPDATA, rentTripData);
                intent.putExtra(KEY_USER,user);
                intent.putExtra("title", "Edit Booking");
                startActivity(intent);
            }
            myDialogManager.stopProcessDialog();

        }
    }
    private class CancelBooking extends AsyncTask<String, String, String> {

            private JSONObject jsonObject;

            public CancelBooking(JSONObject jsonObject) {
                this.jsonObject = jsonObject;
            }

            @Override
            protected String doInBackground(String... strings) {
                Log.e("Request_Response", AppConstants.RENT_TRIP_BOOK_UPDATE + "\n" + jsonObject.toString());

                try {
                    OkHttpClient client = new OkHttpClient();
                    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                    Request request = new Request.Builder()
                            .url(AppConstants.RENT_TRIP_BOOK_UPDATE)
                            .post(body)
                            .build();
                    okhttp3.Response response = client.newCall(request).execute();
                    String result = response.body().string();
                    Log.e("Request_Response", result);

                    return result;

                } catch (IOException e) {
                    e.printStackTrace();

                }
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    Database.getInstance(getActivity()).deleteBookingData(jsonObject.getString("trip_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObject jsonObject = new Gson().fromJson(s, JsonObject.class);
                AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
                alert.setCancelable(false);
                alert.setCanceledOnTouchOutside(false);
                alert.setTitle(jsonObject.get("message").getAsString());
                alert.setMessage(jsonObject.get("result").getAsJsonObject().get("msg").getAsString());
                alert.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onRefresh();
                    }
                });
                alert.show();
            }
        }

    public  void setAlphaAnimation(View v) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(v, "alpha", 0f, 1f);
        fadeIn.setDuration(1500);

        final AnimatorSet mAnimationSet = new AnimatorSet();

        mAnimationSet.play(fadeIn);
        mAnimationSet.start();
    }

    private BroadcastReceiver newBookingReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String data = intent.getStringExtra("data");
            onRefresh();
        }
    };


}
