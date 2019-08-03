package com.eliaovideo.videoline.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.eliaovideo.videoline.MyApplication;
import com.eliaovideo.videoline.R;
import com.eliaovideo.videoline.modle.UserModel;
import com.eliaovideo.videoline.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class CuckooSearchListAdapter extends BaseQuickAdapter<UserModel,BaseViewHolder>{
    public CuckooSearchListAdapter(@Nullable List<UserModel> data) {
        super(R.layout.item_search_user,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserModel item) {

        helper.setText(R.id.item_tv_name,item.getUser_nickname());
        helper.setText(R.id.item_tv_city,item.getAddress());
        Utils.loadHttpImg(MyApplication.getInstances(),item.getAvatar(), (ImageView) helper.getView(R.id.item_iv_avatar),0);

    }
}
