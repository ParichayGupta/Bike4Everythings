package com.b4ebusinessdriver.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b4ebusinessdriver.Adapter.DeliveriesAdapter;
import com.b4ebusinessdriver.Database.DatabaseHandler;
import com.b4ebusinessdriver.Model.OrderDetails;
import com.b4ebusinessdriver.R;
import com.b4elibrary.Logger;

import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class PastFragment extends Fragment implements DeliveriesAdapter.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    Unbinder unbinder;

    // TODO: Rename and change types of parameters
    private int orderStatus;
    private String mParam2;

    DeliveriesAdapter deliveriesAdapter;

    public PastFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment PastFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PastFragment newInstance(int param1) {
        PastFragment fragment = new PastFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderStatus = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_past, container, false);

        unbinder = ButterKnife.bind(this, view);



        try {
            deliveriesAdapter = new DeliveriesAdapter(getActivity(), new GetData().execute(orderStatus).get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        deliveriesAdapter.setListener(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setAdapter(deliveriesAdapter);
        recyclerview.setNestedScrollingEnabled(true);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick() {

    }

    private class GetData extends AsyncTask<Integer, Void, List<OrderDetails>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected List<OrderDetails> doInBackground(Integer... params) {
            List<OrderDetails> orderDetailsList = new DatabaseHandler(getActivity()).getOrdersByStatus(params[0]);
            Logger.log("Request_Response",orderDetailsList+" :: "+ orderStatus);
            return orderDetailsList;
        }
    }




}
