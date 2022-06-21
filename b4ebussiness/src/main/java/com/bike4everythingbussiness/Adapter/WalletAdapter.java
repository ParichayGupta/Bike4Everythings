package com.bike4everythingbussiness.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bike4everythingbussiness.Model.Wallet;
import com.bike4everythingbussiness.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by manishsingh on 02/01/18.
 */

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.MyViewHolder> {

    Context context;
    List<Wallet> walletList;
    OnItemClickListener onItemClickListener;



    public WalletAdapter(Context context, List<Wallet> walletList) {
        this.context = context;
        this.walletList = walletList;
    }

    public void setListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wallet_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Wallet wallet = walletList.get(position);

        holder.name.setText(wallet.getName());
        holder.amount.setText("Rs."+wallet.getWallet());
        if(wallet.getName().contains("Paytm")){
            holder.icon.setImageResource(R.drawable.paytm_wallet);
        }else{
            holder.icon.setImageResource(R.drawable.b4e_wallet);
        }

       // {"status":"200","result":[{"name":"PAYTM","wallet":"0",
         //"sso_token":"0","mobile_no":"0"},{"name":"B4E Business Wallet","wallet":"2","sso_token":"0","mobile_no":"9755299999"}]}

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wallet.getName().contains("Paytm")){
                    onItemClickListener.onClickPaytm(wallet);
                }else{
                    onItemClickListener.onClick(wallet);
                }

            }
        });

        return;


    }

    @Override
    public int getItemCount() {
        return walletList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.icon)
        ImageView icon;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.amount)
        TextView amount;
        View view;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            view =itemView;
        }
    }

    public void updateWallet(String amount, String where){


        for(int i=0; i<walletList.size(); i++){
            if(where.contains("Paytm")){
                walletList.get(i).setWallet(amount);
                walletList.get(i).setName("Paytm Wallet");
            }else {
                walletList.get(i).setWallet(amount);
            }
        }

        notifyDataSetChanged();

    }

    public interface OnItemClickListener {
        void onClick(Wallet wallet);
        void onClickPaytm(Wallet wallet);
    }


}
