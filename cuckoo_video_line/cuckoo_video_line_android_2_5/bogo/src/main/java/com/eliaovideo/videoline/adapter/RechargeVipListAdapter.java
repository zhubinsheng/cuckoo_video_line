package com.eliaovideo.videoline.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;


import com.eliaovideo.videoline.R;
import com.eliaovideo.videoline.modle.RechargeVipBean;
import com.eliaovideo.videoline.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class RechargeVipListAdapter extends BaseQuickAdapter<RechargeVipBean.VipRuleBean, BaseViewHolder> {


    public RechargeVipListAdapter(@Nullable List<RechargeVipBean.VipRuleBean> data) {
        super(R.layout.rechange_vip_list_item, data);
    }


    @Override
    protected void convert(final BaseViewHolder helper, final RechargeVipBean.VipRuleBean item) {

        helper.setText(R.id.vip_list_type, item.getName());
        helper.setText(R.id.vip_list_type_now_price, "¥" + item.getMoney() + "/" + item.getDay_count() + "天");
        helper.setText(R.id.vip_list_type_day_money, item.getDay_money());

        helper.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClickListener(helper.getPosition(), item);
            }
        });

        Utils.loadHttpImg(Utils.getCompleteImgUrl(item.getIcon()), (ImageView) helper.getView(R.id.vip_list_icon_iv));
    }

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener listener) {
        itemClickListener = listener;
    }

    public interface ItemClickListener {
        void onItemClickListener(int position, RechargeVipBean.VipRuleBean vipRuleBean);
    }

}
