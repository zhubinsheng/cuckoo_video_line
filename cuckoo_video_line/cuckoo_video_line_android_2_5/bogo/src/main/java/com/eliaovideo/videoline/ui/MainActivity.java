package com.eliaovideo.videoline.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.eliaovideo.videoline.R;
import com.eliaovideo.videoline.adapter.FragAdapter;
import com.eliaovideo.videoline.api.Api;
import com.eliaovideo.videoline.base.BaseActivity;
import com.eliaovideo.videoline.dialog.AppUpdateDialog;
import com.eliaovideo.videoline.dialog.DoCallVideoWaitDialog;
import com.eliaovideo.videoline.dialog.InviteCodeDialog;
import com.eliaovideo.videoline.event.CuckooOnLoginTimSuccessEvent;
import com.eliaovideo.videoline.event.CuckooPushVideoCallDialogEvent;
import com.eliaovideo.videoline.event.EImOnNewMessages;
import com.eliaovideo.videoline.event.EImVideoCallMessages;
import com.eliaovideo.videoline.event.LocalEvent;
import com.eliaovideo.videoline.fragment.DynamicFragment;
import com.eliaovideo.videoline.fragment.MsgFragment;
import com.eliaovideo.videoline.fragment.IndexPageFragment;
import com.eliaovideo.videoline.fragment.UserPage2Fragment;
import com.eliaovideo.videoline.fragment.UserPageFragment;
import com.eliaovideo.videoline.fragment.VideoPlayFragment;
import com.eliaovideo.videoline.fragment.VideoPushFragment;
import com.eliaovideo.videoline.fragment.VideoSmallFragment;
import com.eliaovideo.videoline.json.JsonDoRequestGetUserAuthStatus;
import com.eliaovideo.videoline.json.JsonRequestBase;
import com.eliaovideo.videoline.manage.AppManager;
import com.eliaovideo.videoline.MyApplication;
import com.eliaovideo.videoline.manage.RequestConfig;
import com.eliaovideo.videoline.manage.SaveData;
import com.eliaovideo.videoline.modle.ConfigModel;
import com.eliaovideo.videoline.modle.custommsg.CustomMsg;
import com.eliaovideo.videoline.modle.custommsg.CustomMsgVideoCall;
import com.eliaovideo.videoline.modle.custommsg.TIMMsgModel;
import com.eliaovideo.videoline.ui.common.Common;
import com.eliaovideo.videoline.utils.BGEventManage;
import com.eliaovideo.videoline.utils.CuckooSharedPreUtil;
import com.eliaovideo.videoline.utils.DialogHelp;
import com.eliaovideo.videoline.utils.IMUtils;
import com.eliaovideo.videoline.utils.SharedPreferencesUtils;
import com.eliaovideo.videoline.utils.StringUtils;
import com.eliaovideo.videoline.utils.UserOnlineHeartUtils;
import com.eliaovideo.videoline.widget.NoScrollViewPager;
import com.lzy.okgo.callback.StringCallback;
import com.maning.imagebrowserlibrary.utils.StatusBarUtil;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMManager;
import com.tencent.open.utils.ThreadManager;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.tillusory.sdk.TiSDK;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 主页
 */

public class MainActivity extends BaseActivity implements PermissionUtils.OnPermissionListener {

    @BindView(R.id.rl_bottom)
    RelativeLayout rl_bottom;
    //功能
    private NoScrollViewPager mainViewPage;
    private QMUITabSegment mainTabSegment;

    //数据
    private List<Fragment> fragmentList;

    private static final int REQUEST_PERMISSION = 0;
    private AppUpdateDialog appUpdateDialog;
    private TextView unReadMsg;
    private String pushData;

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {

        //QMUIStatusBarHelper.translucent(this,getResources().getColor(R.color.transparent));

        mainViewPage = findViewById(R.id.main_view_page);
        mainTabSegment = findViewById(R.id.main_tab_segment);

        //未读消息View
        int itemWidth = ScreenUtils.getScreenWidth() / 4;
        int padding = ConvertUtils.dp2px(1) * 2;
        unReadMsg = new TextView(this);
        unReadMsg.setVisibility(View.GONE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ConvertUtils.dp2px(15), ConvertUtils.dp2px(15));
        params.setMargins(itemWidth * 2 + itemWidth / 4 * 3, 0, 0, 0);
        unReadMsg.setPadding(padding, padding, padding, padding);
        unReadMsg.setGravity(Gravity.CENTER);
        unReadMsg.setTextSize(ConvertUtils.dp2px(3));
        unReadMsg.setBackgroundResource(R.drawable.bg_un_read_msg);
        unReadMsg.setTextColor(getResources().getColor(R.color.white));
        unReadMsg.setLayoutParams(params);
        rl_bottom.addView(unReadMsg);

        addTabAndViewPage();

        StatusBarUtil.setColor(this, getResources().getColor(R.color.white), 0);
        StatusBarUtil.setLightMode(this);

    }


    @Override
    protected void initData() {

        //检查邀请码
        InviteCodeDialog.checkInviteCode(this);

        if (ConfigModel.getInitData().getIs_force_upgrade() != 1) {

            showUpdateDialog();
        }
        //系统公告消息
        showSystemMsg();

        //初始化心跳
        UserOnlineHeartUtils.getInstance().startHeartTime();

        if (!TextUtils.isEmpty(ConfigModel.getInitData().getBogokj_beauty_sdk_key())) {
            //初始化布谷科技美颜SDK
            TiSDK.init(ConfigModel.getInitData().getBogokj_beauty_sdk_key(), this);
        }
    }

    @Override
    protected void initPlayerDisplayData() {

    }


    @Override
    protected void initSet() {

//        设置viewPage的缓存页数
//        mainViewPage.setOffscreenPageLimit(0);
        //设置adapter
        mainViewPage.setAdapter(new FragAdapter(getSupportFragmentManager(), fragmentList));
        //设置字体大小
        mainTabSegment.setTabTextSize(ConvertUtils.dp2px(12));
        //设置 Tab 选中状态下的颜色
        mainTabSegment.setDefaultSelectedColor(getResources().getColor(R.color.admin_color));
        //关联viewPage
        mainTabSegment.setupWithViewPager(mainViewPage, false);
        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        PermissionUtils.requestPermissions(this, REQUEST_PERMISSION, permission, this);


        boolean isOpenNotification = NotificationManagerCompat.from(this).areNotificationsEnabled();
        if (!isOpenNotification) {
            new QMUIDialog.MessageDialogBuilder(this)
                    .setTitle("提示")
                    .setMessage("为了更好的软件体验请打开手机中的通知权限！")
                    .addAction(0, "确定", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            Intent localIntent = new Intent();
                            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if (Build.VERSION.SDK_INT >= 9) {
                                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                localIntent.setData(Uri.fromParts("package", MainActivity.this.getPackageName(), null));
                            } else if (Build.VERSION.SDK_INT <= 8) {
                                localIntent.setAction(Intent.ACTION_VIEW);

                                localIntent.setClassName("com.android.settings",
                                        "com.android.settings.InstalledAppDetails");

                                localIntent.putExtra("com.android.settings.ApplicationPkgName",
                                        MainActivity.this.getPackageName());
                            }
                            startActivity(localIntent);
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }


    //初始化ViewPage和tab
    private void addTabAndViewPage() {

        //添加关联的fragment
        fragmentList = new ArrayList<>();
        fragmentList.add(new IndexPageFragment());

        if (StringUtils.toInt(ConfigModel.getInitData().getOpen_video_chat()) == 1) {
            if (SaveData.getInstance().getUserInfo().getSex() == 1) {
                fragmentList.add(new VideoPlayFragment());
            } else {
                fragmentList.add(new VideoPushFragment());
            }
        }

        fragmentList.add(new VideoSmallFragment());
        fragmentList.add(new DynamicFragment());
        fragmentList.add(new MsgFragment());
        fragmentList.add(new UserPageFragment());
        //添加tab
        mainTabSegment.addTab(
                new QMUITabSegment.Tab(
                        getResources().getDrawable(R.drawable.main_screen_drawable_ranking_unselected),
                        getResources().getDrawable(R.drawable.main_screen_drawable_ranking_selected),
                        getString(R.string.index),
                        false,
                        true));

        if (StringUtils.toInt(ConfigModel.getInitData().getOpen_video_chat()) == 1) {
            mainTabSegment.addTab(
                    new QMUITabSegment.Tab(
                            getResources().getDrawable(R.drawable.main_screen_drawable_ranking_unselected),
                            getResources().getDrawable(R.drawable.main_screen_drawable_ranking_selected),
                            null,
                            false,
                            true));
        }

        mainTabSegment.addTab(
                new QMUITabSegment.Tab(
                        getResources().getDrawable(R.drawable.main_screen_drawable_video_unselected),
                        getResources().getDrawable(R.drawable.main_screen_drawable_video_selected),
                        getString(R.string.video),
                        false,
                        true));

        mainTabSegment.addTab(
                new QMUITabSegment.Tab(
                        getResources().getDrawable(R.drawable.main_screen_drawable_dynamic_unselected),
                        getResources().getDrawable(R.drawable.main_screen_drawable_dynamic_selected),
                        getString(R.string.dynamic),
                        false,
                        true));


        mainTabSegment.addTab(
                new QMUITabSegment.Tab(
                        getResources().getDrawable(R.drawable.main_screen_drawable_message_unselected),
                        getResources().getDrawable(R.drawable.main_screen_drawable_message_selected),
                        getString(R.string.message),
                        false,
                        true));

        mainTabSegment.addTab(
                new QMUITabSegment.Tab(
                        getResources().getDrawable(R.drawable.main_screen_drawable_mine_unselected),
                        getResources().getDrawable(R.drawable.main_screen_drawable_mine_selected),
                        getString(R.string.me),
                        false,
                        true));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocalEvent(LocalEvent var1) {

//        if(MyApplication.getInstances().getLocation().get("city") != null){
//            Api.doRefreshCity(uId,MyApplication.getInstances().getLocation().get("city"),null);
//        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMsgEventThread(EImOnNewMessages var1) {
        initUnReadMessage();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginTimSuccess(CuckooOnLoginTimSuccessEvent var1) {
        initUnReadMessage();
    }


    //初始化未读消息
    private void initUnReadMessage() {

        int count = IMUtils.getIMUnReadMessageCount();
        if (count > 0) {
            unReadMsg.setVisibility(View.VISIBLE);
            unReadMsg.setText(String.valueOf(count));
        } else {
            unReadMsg.setVisibility(View.GONE);
        }
    }

    //系统消息
    private void showSystemMsg() {
        int isShowSystemMsg = CuckooSharedPreUtil.getInt(this, "IS_SHOW_SYSTEM_MSG");

        if (!TextUtils.isEmpty(RequestConfig.getConfigObj().getMainSystemMessage()) && isShowSystemMsg == 0) {

            new QMUIDialog.MessageDialogBuilder(this)
                    .setTitle(getString(R.string.system_notice))
                    .setMessage(RequestConfig.getConfigObj().getMainSystemMessage())
                    .addAction(0, "不再提示", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            CuckooSharedPreUtil.put(MainActivity.this, "IS_SHOW_SYSTEM_MSG", 1);
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }


    private void showUpdateDialog() {
        if (appUpdateDialog != null) {
            appUpdateDialog.dismiss();
        }

        appUpdateDialog = AppUpdateDialog.checkUpdate(this);
    }


    @Override
    public void onPermissionGranted() {

    }

    @Override
    public void onPermissionDenied(String[] deniedPermissions) {
        ToastUtils.showLong(R.string.not_have_permission);
    }

    @Override
    public void onBackPressed() {
        //动态视频
        if (NiceVideoPlayerManager.instance().onBackPressd()) return;

        Intent intent = new Intent();
        // 为Intent设置Action、Category属性
        intent.setAction(Intent.ACTION_MAIN);// "android.intent.action.MAIN"
        intent.addCategory(Intent.CATEGORY_HOME); //"android.intent.category.HOME"
        startActivity(intent);


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ConfigModel.getInitData().getIs_force_upgrade() == 1) {
            showUpdateDialog();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //初始化未读消息
        initUnReadMessage();

        pushData = getIntent().getStringExtra("push_data");
        if (pushData != null && !TextUtils.isEmpty(pushData)) {
            getIntent().putExtra("push_data", "");
            Intent intent = new Intent(this, CuckooVideoCallListActivity.class);
            startActivity(intent);
        }

        //后台收到消息
        if (MyApplication.getInstances().getPushVideoCallMessage() != null) {

            CustomMsg customMsg = MyApplication.getInstances().getPushVideoCallMessage().getCustomMsg();
            Intent intent = new Intent(this, CuckooVideoCallWaitActivity.class);
            intent.putExtra(CuckooVideoCallWaitActivity.CALL_TYPE, ((CustomMsgVideoCall) customMsg).getCall_type());
            intent.putExtra(CuckooVideoCallWaitActivity.CALL_USER_INFO, customMsg.getSender());
            intent.putExtra(CuckooVideoCallWaitActivity.CHANNEL_ID, ((CustomMsgVideoCall) customMsg).getChannel());
            intent.putExtra(CuckooVideoCallWaitActivity.IS_USE_FREE, ((CustomMsgVideoCall) customMsg).getIs_free());
            startActivity(intent);
            MyApplication.getInstances().setPushVideoCallMessage(null);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        UserOnlineHeartUtils.getInstance().stopHeartTime();
    }


}
