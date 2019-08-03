package com.eliaovideo.videoline.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.eliaovideo.videoline.R;
import com.eliaovideo.videoline.modle.UserModel;
import com.eliaovideo.videoline.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class CuckooGuardianOrderListAdapter extends BaseQuickAdapter<UserModel,BaseViewHolder> {
    public CuckooGuardianOrderListAdapter(@Nullable List<UserModel> data) {
        super(R.layout.item_guardian_order,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserModel item) {
        Utils.loadHttpImg(item.getAvatar(), (ImageView) helper.getView(R.id.iv_avatar));
    }
}
