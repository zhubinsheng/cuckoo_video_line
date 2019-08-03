package com.eliaovideo.videoline.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eliaovideo.videoline.R;
import com.eliaovideo.videoline.modle.RechargeVipBean;

import java.util.ArrayList;
import java.util.List;

public class PayWayListAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<RechargeVipBean.PayListBean> paywayList = new ArrayList<>();
    private int selectPosi = 0;

    public PayWayListAdapter(Context context, List<RechargeVipBean.PayListBean> list) {
        this.context = context;
        paywayList.addAll(list);
    }


    public void setSelectPosi(int position) {
        selectPosi = position;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new PayWayListAdapter.PayWayViewHolder(View.inflate(context, R.layout.pay_way_list_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        PayWayListAdapter.PayWayViewHolder viewHolder = (PayWayViewHolder) holder;

        final RechargeVipBean.PayListBean payListBean = paywayList.get(position);

        viewHolder.payWay.setText(payListBean.getPay_name());

        if (selectPosi == position) {
            viewHolder.payWay.setTextColor(Color.parseColor("#FF4081"));
            viewHolder.line.setVisibility(View.GONE);
        } else {
            viewHolder.payWay.setTextColor(Color.parseColor("#c5c5c5"));
            viewHolder.line.setVisibility(View.GONE);
        }

        viewHolder.paywayLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClickListener(position,payListBean);
            }
        });

    }


    @Override
    public int getItemCount() {
        return paywayList.size();
    }



    public class PayWayViewHolder extends RecyclerView.ViewHolder {


        private final TextView payWay;
        private final View line;
        private final LinearLayout paywayLl;

        public PayWayViewHolder(View itemView) {
            super(itemView);

            payWay = itemView.findViewById(R.id.pay_btn);
            line = itemView.findViewById(R.id.pay_line);
            paywayLl = itemView.findViewById(R.id.pay_way_ll);
        }
    }

    private ItemClickListener itemClickListener;

    public void setPayWayItemClickListener(ItemClickListener listener) {
        itemClickListener = listener;
    }

    public interface ItemClickListener {
        void onItemClickListener(int position, RechargeVipBean.PayListBean paywayList);
    }

}
