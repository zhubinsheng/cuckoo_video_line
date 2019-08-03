package com.eliaovideo.videoline.utils;

import android.content.Context;
import android.content.Intent;

import com.eliaovideo.videoline.manage.RequestConfig;
import com.eliaovideo.videoline.modle.ConfigModel;
import com.eliaovideo.videoline.modle.UserChatData;
import com.eliaovideo.videoline.ui.CuckooChangeUserRatioActivity;
import com.eliaovideo.videoline.ui.CuckooDynamicVideoPlayerActivity;
import com.eliaovideo.videoline.ui.CuckooGuildApplyListActivity;
import com.eliaovideo.videoline.ui.CuckooGuildCreateActivity;
import com.eliaovideo.videoline.ui.CuckooGuildListActivity;
import com.eliaovideo.videoline.ui.CuckooGuildManageActivity;
import com.eliaovideo.videoline.ui.CuckooGuildUserManageActivity;
import com.eliaovideo.videoline.ui.CuckooSelectIncomeLogActivity;
import com.eliaovideo.videoline.ui.CuckooVoiceCallActivity;
import com.eliaovideo.videoline.ui.PlayerCallActivity;
import com.eliaovideo.videoline.ui.VideoLineActivity;
import com.eliaovideo.videoline.ui.WebViewActivity;

public class UIHelp {

    /**
     * @param callType       视频或音频
     * @param isNeedCharging 是否是需要扣费
     * @param resolvingPower 视频的分辨率
     * @param userChatData   用户信息
     * @param videoDeduction 价格
     * @param freeTime       免费时长（s）
     * @dw 跳转视频通话或者音频通话页面
     */
    public static void startVideoLineActivity(Context context, int callType, String resolvingPower, int isNeedCharging, String videoDeduction, int freeTime, UserChatData userChatData) {

        Intent intent;

        if (callType == 0) {
            intent = new Intent(context, VideoLineActivity.class);
        } else {
            intent = new Intent(context, CuckooVoiceCallActivity.class);
        }
        intent.putExtra("obj", userChatData);
        intent.putExtra("video_px", resolvingPower);
        intent.putExtra(VideoLineActivity.IS_NEED_CHARGE, isNeedCharging == 1);
        intent.putExtra(VideoLineActivity.IS_BE_CALL, true);
        intent.putExtra(VideoLineActivity.VIDEO_DEDUCTION, videoDeduction);
        intent.putExtra(VideoLineActivity.CALL_TYPE, callType);
        intent.putExtra(VideoLineActivity.FREE_TIME, freeTime);
        context.startActivity(intent);
    }

    public static void showGuildCreateActivity(Context context) {
        Intent intent = new Intent(context, CuckooGuildCreateActivity.class);
        context.startActivity(intent);
    }

    public static void showGuildList(Context context) {
        Intent intent = new Intent(context, CuckooGuildListActivity.class);
        context.startActivity(intent);
    }

    public static void showGuildManageActivity(Context context) {
        Intent intent = new Intent(context, CuckooGuildManageActivity.class);
        context.startActivity(intent);
    }

    public static void showGuildUserManage(Context context, String guildId) {
        Intent intent = new Intent(context, CuckooGuildUserManageActivity.class);
        intent.putExtra(CuckooGuildUserManageActivity.GUILD_ID, guildId);
        context.startActivity(intent);
    }

    public static void showGuildApplyUserManage(Context context, String guildId) {
        Intent intent = new Intent(context, CuckooGuildApplyListActivity.class);
        intent.putExtra(CuckooGuildUserManageActivity.GUILD_ID, guildId);
        context.startActivity(intent);
    }

    public static void showChangeUserRatioPage(Context context, int id) {
        Intent intent = new Intent(context, CuckooChangeUserRatioActivity.class);
        intent.putExtra(CuckooChangeUserRatioActivity.USER_ID, id);
        context.startActivity(intent);
    }

    public static void showSelectIncomeLog(Context context) {
        Intent intent = new Intent(context, CuckooSelectIncomeLogActivity.class);
        context.startActivity(intent);
    }

    public static void showIncomePage(Context context) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("title", "收益");
        intent.putExtra("url", ConfigModel.getInitData().getApp_h5().getUser_withdrawal());
        intent.putExtra("is_token", true);
        intent.putExtra(WebViewActivity.IS_INCOME, true);
        context.startActivity(intent);
    }

    public static void showDynamicVideoPlayer(Context context,String videoUrl,String coverUrl){
        Intent intent = new Intent(context, CuckooDynamicVideoPlayerActivity.class);
        intent.putExtra(CuckooDynamicVideoPlayerActivity.VIDEO_URL,videoUrl);
        intent.putExtra(CuckooDynamicVideoPlayerActivity.COVER_URL,coverUrl);
        context.startActivity(intent);
    }
}
