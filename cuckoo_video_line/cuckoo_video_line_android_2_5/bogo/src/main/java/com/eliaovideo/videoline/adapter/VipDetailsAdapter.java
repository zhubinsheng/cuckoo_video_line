package com.eliaovideo.videoline.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.eliaovideo.videoline.R;
import com.eliaovideo.videoline.modle.VipDetailsModel;
import com.eliaovideo.videoline.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class VipDetailsAdapter extends BaseQuickAdapter<VipDetailsModel, BaseViewHolder> {
    public VipDetailsAdapter(@Nullable List<VipDetailsModel> data) {
        super(R.layout.item_vip_details, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VipDetailsModel item) {
        Utils.loadHttpImg(item.getImg(), (ImageView) helper.getView(R.id.item_iv_img));
        helper.setText(R.id.item_tv_center, item.getCenter());
    }
}
