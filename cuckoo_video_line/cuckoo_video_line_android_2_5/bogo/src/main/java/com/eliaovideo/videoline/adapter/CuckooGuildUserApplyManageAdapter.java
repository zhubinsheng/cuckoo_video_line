package com.eliaovideo.videoline.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.eliaovideo.videoline.R;
import com.eliaovideo.videoline.modle.GuildUserModel;
import com.eliaovideo.videoline.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class CuckooGuildUserApplyManageAdapter extends BaseQuickAdapter<GuildUserModel, BaseViewHolder> {
    public CuckooGuildUserApplyManageAdapter(@Nullable List<GuildUserModel> data) {
        super(R.layout.item_guild_user_apply_list, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GuildUserModel item) {
        helper.setText(R.id.item_tv_name, item.getUser_nickname());
        Utils.loadHttpImg(item.getAvatar(), (ImageView) helper.getView(R.id.item_iv_avatar));
    }
}
