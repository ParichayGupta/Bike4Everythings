package com.hvantage.b4eemp.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.b4elibrary.Logger;
import com.b4erental.Model.RentTripData;
import com.b4erental.Model.User;
import com.b4erental.RentalActivity;
import com.b4erental.Utils.DialogManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hvantage.b4eemp.Database.Database;
import com.hvantage.b4eemp.R;
import com.hvantage.b4eemp.Wedgets.CustomDateTimePicker;
import com.hvantage.b4eemp.adapter.RunningAdapter;
import com.hvantage.b4eemp.model.BookingData;
import com.hvantage.b4eemp.model.BookingModel;
import com.hvantage.b4eemp.tracking.MainActivity;
import com.hvantage.b4eemp.utils.AppConstants;
import com.hvantage.b4eemp.utils.AppPreferance;
import com.hvantage.b4eemp.utils.Functions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.b4elibrary.AppConstants.KEY_RENTTRIPDATA;
import static com.b4elibrary.AppConstants.KEY_USER;


public class RunningFragment extends Fragment implements RunningAdapter.ItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    RecyclerView recyclerView;
    SwipeRefreshLayout pullToRefresh;
    ImageView norecordfound;

    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    RunningAdapter runningAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RunningFragment() {
        // Required empty public constructor
    }


    public static RunningFragment newInstance(String param1, String param2) {
        RunningFragment fragment = new RunningFragment();
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
        View view = inflater.inflate(R.layout.fragment_running, container, false);
        initView(view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        runningAdapter = new RunningAdapter(getActivity(), new ArrayList<BookingData>());
        runningAdapter.setClickListener(this);
        recyclerView.setAdapter(runningAdapter);

        pullToRefresh.setOnRefreshListener(this);

        return view;
    }
    private void initView(View view) {

        recyclerView=view.findViewById(R.id.recyclerView1);

        pullToRefresh=view.findViewById(R.id.pullToRefresh1);

        norecordfound=view.findViewById(R.id.norecordfound1);
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
    public void onComplete(final BookingData bookingData) {
        LayoutInflater li = LayoutInflater.from(getActivity());
        final View view = li.inflate(R.layout.remark, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Complete process");

        final EditText remark = view.findViewById(R.id.remark);

        final Button submitBtn = view.findViewById(R.id.submitBtn);

        alertDialog.setView(view);
        alertDialog.show();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String _remark = remark.getText().toString();
                if (TextUtils.isEmpty(_remark)) {
                    remark.setError("This field is required");
                } else {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("method", AppConstants.COMPLETERENTBOOKING);
                        jsonObject.put("id", bookingData.getId());
                        jsonObject.put("remark", _remark);
                        jsonObject.put("booking_status", AppConstants.COMPLETED);
                        new CompleteRentBooking(jsonObject).execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    alertDialog.dismiss();
                }
            }
        });

    }

    @Override
    public void onUpdate(final BookingData bookingData) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", AppConstants.MYRENT_ALLBYTRIP);
            jsonObject.put("trip_id", bookingData.getId());
            new getBookingDataById(jsonObject).execute();
        } catch (JSONException e) {
            e.printStackTrace();
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

    @Override
    public void onDetails(BookingData bookingData) {
        LayoutInflater li = LayoutInflater.from(getActivity());
        final View view = li.inflate(R.layout.booking_list_row_show, null);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Details");

        TextView id = view.findViewById(R.id.id);
        TextView date = view.findViewById(R.id.date);
        TextView name = view.findViewById(R.id.name);
        TextView mobile = view.findViewById(R.id.mobile);
        TextView bikename = view.findViewById(R.id.bikename);
        TextView servicename = view.findViewById(R.id.servicename);
        TextView daterange=view.findViewById(R.id.daterange);
        TextView duration =view.findViewById(R.id.duration);
        TextView paymentMode =view.findViewById(R.id.paymentMode);
        TextView payment =view.findViewById(R.id.payment);

        ImageView customerimage = view.findViewById(R.id.customerimage);
        final ProgressBar progressBar = view.findViewById(R.id.progressBar);

        alertDialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.setView(view);
        alertDialog.show();

        id.setText(Html.fromHtml(Functions.getHeaderBlackText("Booking-Id : ", "# " + bookingData.getId())), TextView.BufferType.SPANNABLE);
        date.setText(bookingData.getIssuesDate());
        name.setText(bookingData.getName());
        mobile.setText(bookingData.getMobile());
        bikename.setText(bookingData.getBikeName());
        servicename.setText(bookingData.getServiceName());

        paymentMode.setText(bookingData.getPaymentType());
        payment.setText("₹ "+bookingData.getPayment());


        try {
            Date startDate = dateFormat.parse(bookingData.getPickupDate() +" "+ bookingData.getPickupTime());
            Date endDate = dateFormat.parse(bookingData.getDropoffDate() +" "+ bookingData.getDropoffTime());

            long msDiff = endDate.getTime() - startDate.getTime();
            long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);
            long hourssDiff = TimeUnit.MILLISECONDS.toHours(msDiff);


            daterange.setText( DateUtils.formatDateRange(getActivity(), startDate.getTime(), endDate.getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME));
            duration.setText(bookingData.getDuration());


        } catch (ParseException e) {
            e.printStackTrace();
        }


        Glide.with(getActivity())
                .load(bookingData.getCustomerImage())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .apply(new RequestOptions().placeholder(R.drawable.ic_perm_identity_black_24dp))
                .apply(RequestOptions.circleCropTransform())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(customerimage);

    }

    @Override
    public void onTrack(BookingData bookingData) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("data", bookingData);
        startActivity(intent);
    }


    @Override
    public void onRefresh() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", AppConstants.GETALLRENTRUNNINGBOOKING);
            jsonObject.put("location_id", AppPreferance.getLocationId(getActivity()));
            new getRunningData(jsonObject).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pullToRefresh.setRefreshing(false);
    }

    public void setAlphaAnimation(View v) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(v, "alpha", 0f, 1f);
        fadeIn.setDuration(1500);

        final AnimatorSet mAnimationSet = new AnimatorSet();

        mAnimationSet.play(fadeIn);
        mAnimationSet.start();
    }

    private class getRunningData extends AsyncTask<String, String, String> {

        private JSONObject jsonObject;

        public getRunningData(JSONObject jsonObject) {
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
            if (!TextUtils.isEmpty(s)) {
                BookingModel bookingModel = new Gson().fromJson(s, BookingModel.class);
                ArrayList<BookingData> bookingData = new ArrayList<>();

                if (bookingModel.getStatus().equals("200")) {
                    bookingData = bookingModel.getResult();
                }
                runningAdapter.setData(bookingData);

                for (BookingData data : bookingData) {
                    if (Database.getInstance(getActivity()).isBookingIDExits(data.getId())) {
                        Database.getInstance(getActivity()).updateBookingata(data);
                    } else {
                        Database.getInstance(getActivity()).addBookingData(data);
                    }
                }
                try {
                    if (bookingData.isEmpty()) {
                        norecordfound.setVisibility(View.VISIBLE);
                        setAlphaAnimation(norecordfound);
                    } else {
                        norecordfound.setVisibility(View.GONE);
                    }
                } catch (NullPointerException e) {
                } catch (Exception e) {
                }
            }
        }
    }

    private class CompleteRentBooking extends AsyncTask<String, String, String> {

        private JSONObject jsonObject;

        public CompleteRentBooking(JSONObject jsonObject) {
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
            BookingModel bookingModel = new Gson().fromJson(s, BookingModel.class);
            if (bookingModel.getStatus().equals("200")) {
                Database.getInstance(getActivity()).deleteBookingData(bookingModel.getResult().get(0).getId());
                onRefresh();
//                BookingData bookingData = bookingModel.getResult().get(0);
//                Database.getInstance(getActivity()).updateBookingStatus(bookingData.getId(), AppConstants.COMPLETED);
//                onRefresh();
            }

        }
    }


    private class UpdateBooking extends AsyncTask<String, String, String> {

        private JSONObject jsonObject;

        public UpdateBooking(JSONObject jsonObject) {
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
            JsonObject jsonObject = new Gson().fromJson(s, JsonObject.class);
            AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
            alert.setTitle(jsonObject.get("message").getAsString());
            alert.setMessage(jsonObject.get("result").getAsJsonObject().get("msg").getAsString());
            alert.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.show();
        }
    }

}
