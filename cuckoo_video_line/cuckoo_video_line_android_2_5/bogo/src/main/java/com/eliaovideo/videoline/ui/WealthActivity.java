package com.eliaovideo.videoline.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.eliaovideo.videoline.R;
import com.eliaovideo.videoline.api.Api;
import com.eliaovideo.videoline.base.BaseActivity;
import com.eliaovideo.videoline.event.EWxPayResultCodeComplete;
import com.eliaovideo.videoline.json.JsonRequestDoGetWealthPage;
import com.eliaovideo.videoline.manage.RequestConfig;
import com.eliaovideo.videoline.modle.ConfigModel;
import com.eliaovideo.videoline.paypal.PayPalHandle;
import com.eliaovideo.videoline.utils.BGViewUtil;
import com.lzy.okgo.callback.StringCallback;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author 魏鹏
 * email 1403102936@qq.com
 * 山东布谷鸟网络科技有限公司著
 * @dw 财富
 */

public class WealthActivity extends BaseActivity {

    private QMUIGroupListView groupListView;
    private TextView mTvCoin;
    private QMUITopBar topBar;

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_wealth;
    }

    @Override
    protected void initView() {
        QMUIStatusBarHelper.translucent(this); // 沉浸式状态栏

        groupListView = findViewById(R.id.groupListView);
        topBar = findViewById(R.id.qmui_top_bar);
        mTvCoin = findViewById(R.id.tv_coin);
        initTopBar();
        initItemView();
    }

    private void initTopBar() {
        topBar.setBackgroundColor(getResources().getColor(R.color.admin_color));
        topBar.removeAllLeftViews();
        topBar.addLeftImageButton(R.drawable.icon_back_white, R.id.all_backbtn).setOnClickListener(this);
        Button rightBtn = topBar.addRightTextButton(getString(R.string.detail), R.id.right_btn);
        TextView title = topBar.setTitle(getString(R.string.wealth));
        title.setTextColor(getResources().getColor(R.color.white));
        title.setPadding(0, BGViewUtil.dp2px(40), 0, 0);
        rightBtn.setOnClickListener(this);
        rightBtn.setTextColor(getResources().getColor(R.color.white));


        ((TextView) findViewById(R.id.tv_total_coin_name)).setText(RequestConfig.getConfigObj().getCurrency());
    }

    private void initItemView() {

        QMUICommonListItemView itemRecharge = groupListView.createItemView(getString(R.string.recharge) + RequestConfig.getConfigObj().getCurrency());
        itemRecharge.setId(R.id.wealth_recharge);
        QMUICommonListItemView itemProfit = groupListView.createItemView(getString(R.string.income_cash));
        itemProfit.setId(R.id.wealth_cash);
        QMUICommonListItemView itemRadio = groupListView.createItemView(getString(R.string.income_ratio));
        itemRadio.setId(R.id.wealth_split);

        itemRecharge.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemProfit.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemRadio.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        QMUIGroupListView.newSection(this)
                .setTitle(RequestConfig.getConfigObj().getCurrency() + getString(R.string.wealth_tips1))
                .addItemView(itemRecharge, this)
                //.addItemView(itemProfit, this)
                .addTo(groupListView);
    }

    @Override
    protected void initSet() {

        // QMUIStatusBarHelper.translucent(this,getResources().getColor(R.color.admin_color)); // 沉浸式状态栏
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        requestGetWealthPageInfo();
    }

    @Override
    protected void initPlayerDisplayData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.right_btn:
                WebViewActivity.openH5Activity(this, true, getString(R.string.detail), RequestConfig.getConfigObj().getMyDetailUrl());
                break;
            case R.id.wealth_cash:
//                Intent intent = new Intent(this, UserIncomeActivity.class);
//                startActivity(intent);
                WebViewActivity.openH5Activity(this, true, "收益", ConfigModel.getInitData().getApp_h5().getUser_withdrawal());
                break;
            case R.id.all_backbtn:
                finish();
                break;
            case R.id.wealth_recharge:
                RechargeActivity.startRechargeActivity(this);
                break;

            default:
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWxPayCommon(EWxPayResultCodeComplete var1) {
        ToastUtils.showLong(var1.content);
        requestGetWealthPageInfo();
    }

    //获取页面数据
    private void requestGetWealthPageInfo() {

        Api.doRequestGetWealthPageInfo(uId, uToken, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {

                JsonRequestDoGetWealthPage jsonObj =
                        (JsonRequestDoGetWealthPage) JsonRequestDoGetWealthPage.getJsonObj(s, JsonRequestDoGetWealthPage.class);
                if (jsonObj.getCode() == 1) {
                    mTvCoin.setText(jsonObj.getCoin());
                }

            }
        });
    }

    public static void startWealthActivity(Context context) {
        Intent intent = new Intent(context, WealthActivity.class);
        context.startActivity(intent);
    }
}
