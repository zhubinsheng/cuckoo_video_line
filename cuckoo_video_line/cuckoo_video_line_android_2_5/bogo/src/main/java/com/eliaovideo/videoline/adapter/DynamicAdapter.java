package com.eliaovideo.videoline.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eliaovideo.videoline.R;
import com.eliaovideo.videoline.audiorecord.entity.AudioEntity;
import com.eliaovideo.videoline.audiorecord.view.CommonSoundItemView;
import com.eliaovideo.videoline.manage.SaveData;
import com.eliaovideo.videoline.modle.DynamicListModel;
import com.eliaovideo.videoline.modle.UserModel;
import com.eliaovideo.videoline.ui.DynamicImagePreviewActivity;
import com.eliaovideo.videoline.utils.StringUtils;
import com.eliaovideo.videoline.utils.UIHelp;
import com.eliaovideo.videoline.utils.Utils;
import com.eliaovideo.videoline.widget.BGLevelTextView;
import com.eliaovideo.videoline.widget.GlideImageEngine;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.maning.imagebrowserlibrary.ImageEngine;
import com.maning.imagebrowserlibrary.MNImageBrowser;
import com.maning.imagebrowserlibrary.listeners.OnPageChangeListener;
import com.maning.imagebrowserlibrary.model.ImageBrowserConfig;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.xiao.nicevideoplayer.TxVideoPlayerController;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DynamicAdapter extends BaseQuickAdapter<DynamicListModel, BaseViewHolder> {

    private Context mContext;


    public DynamicAdapter(Context context, @Nullable List<DynamicListModel> data) {
        super(R.layout.item_dynamic, data);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, final DynamicListModel item) {
        UserModel userInfo = item.getUserInfo();

        helper.setText(R.id.item_tv_content, item.getMsg_content());
        if (userInfo != null) {
            helper.setText(R.id.item_tv_name, userInfo.getUser_nickname());
        }
        helper.setText(R.id.item_tv_time, item.getPublish_time());
        //回复
        helper.setText(R.id.item_tv_common_count, item.getComment_count());
        //点赞
        helper.setText(R.id.item_tv_like_count, item.getLike_count());

        RecyclerView rv = helper.getView(R.id.rv_photo_list);
        //视频动态
        if (!TextUtils.isEmpty(item.getVideo_url())) {
            Utils.loadHttpImg(item.getCover_url(), (ImageView) helper.getView(R.id.iv_video_thumb));
            helper.setVisible(R.id.rl_video_player, true);
            helper.getView(R.id.rl_video_player).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UIHelp.showDynamicVideoPlayer(mContext, item.getVideo_url(), item.getCover_url());
                }
            });
            rv.setVisibility(View.GONE);
        } else {
            helper.getView(R.id.rl_video_player).setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
            rv.setLayoutManager(new GridLayoutManager(mContext, 3));
            DynamicImgAdapter dynamicImgAdapter = new DynamicImgAdapter(mContext, item);
            rv.setAdapter(dynamicImgAdapter);
        }

        if (StringUtils.toInt(item.getIs_audio()) == 1) {
            helper.setGone(R.id.pp_sound_item_view, true);
        } else {
            helper.setGone(R.id.pp_sound_item_view, false);
        }

        CommonSoundItemView commonSoundItemView = helper.getView(R.id.pp_sound_item_view);
        AudioEntity audioEntity = new AudioEntity();
        audioEntity.setUrl(item.getAudio_file());
        commonSoundItemView.setSoundData(audioEntity);
        if (userInfo != null) {
            Utils.loadHttpIconImg(mContext, userInfo.getAvatar(), (ImageView) helper.getView(R.id.item_iv_avatar), 0);
        }

        //点赞
        helper.addOnClickListener(R.id.item_iv_like_count);

        if (StringUtils.toInt(item.getIs_like()) == 1) {
            helper.setBackgroundRes(R.id.item_iv_like_count, R.mipmap.ic_dynamic_thumbs_up_s);
        } else {
            helper.setBackgroundRes(R.id.item_iv_like_count, R.mipmap.ic_dynamic_thumbs_up_n);
        }

        if (StringUtils.toInt(item.getUid()) == StringUtils.toInt(SaveData.getInstance().getId())) {
            helper.setGone(R.id.item_del, true);
        } else {
            helper.setGone(R.id.item_del, false);
        }

        if (item.getUserInfo() != null) {

            ((BGLevelTextView) helper.getView(R.id.tv_level)).setLevelInfo(item.getUserInfo().getSex(), item.getUserInfo().getLevel());
        }
        helper.addOnClickListener(R.id.item_tv_chat);
        helper.addOnClickListener(R.id.item_del);
        helper.addOnClickListener(R.id.item_iv_avatar);
    }


    private OnImgClickListener onImgClickListener;

    public void setOnImgClickListener(OnImgClickListener listener) {
        onImgClickListener = listener;
    }

    public interface OnImgClickListener {
        void onItemClickListener(String imgUrl, String pid);
    }
}
