package com.eliaovideo.videoline.utils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.eliaovideo.videoline.MyApplication;
import com.eliaovideo.videoline.api.Api;
import com.eliaovideo.videoline.json.JsonRequestBase;
import com.eliaovideo.videoline.manage.RequestConfig;
import com.eliaovideo.videoline.manage.SaveData;
import com.eliaovideo.videoline.ui.common.LoginUtils;
import com.lzy.okgo.callback.StringCallback;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by 魏鹏 on 2018/4/17.
 * email:1403102936@qq.com
 * 山东布谷鸟网络科技有限公司著
 */

public class UserOnlineHeartUtils implements BGTimedTaskManage.BGTimeTaskRunnable {

    private static UserOnlineHeartUtils instance;
    private BGTimedTaskManage heartRunnable;
    private int hradNumber = 0;//心跳次数

    private String userId;
    private String userToken;

    private boolean isInit = false;

    public static UserOnlineHeartUtils getInstance() {

        if (instance == null) {
            instance = new UserOnlineHeartUtils();

        }
        return instance;
    }


    /**
     * 初始化心跳
     */
    private void initHeartBeat() {

        heartRunnable = new BGTimedTaskManage();
        if (RequestConfig.getConfigObj().getRequestIntervalIsOnLine() != 0) {
            heartRunnable.setTime(RequestConfig.getConfigObj().getRequestIntervalIsOnLine() * 1000);

            heartRunnable.startRunnable(this, true);

            isInit = true;
        }
    }

    public void startHeartTime() {
        if (!isInit) {
            initHeartBeat();
        } else {
            if (heartRunnable != null) {
                heartRunnable.startRunnable(true);
            }
        }
        if (heartRunnable != null) {
            heartRunnable.setPause(false);
        }
    }

    public void stopHeartTime() {

        if (heartRunnable != null) {

            heartRunnable.setPause(true);
        }
    }


    /**
     * 请求心跳
     */
    private void requestHeartBeat() {

        if (!SaveData.getInstance().isIsLogin()) {
            return;
        }

        userId = SaveData.getInstance().getId();
        userToken = SaveData.getInstance().getToken();

        Api.doMonitorIsOnLine(
                userId,
                userToken,
                new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JsonRequestBase data = JsonRequestBase.getJsonObj(s, JsonRequestBase.class);
                        if (data.getCode() == 10002) {
                            ToastUtils.showLong(data.getMsg());
                            LoginUtils.doLoginOut(MyApplication.getInstances());
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        ToastUtils.showLong("与服务器断开连接,请重新登录!");
                    }
                }
        );
    }

    @Override
    public void onRunTask() {

        hradNumber++;
        LogUtils.i("心跳---------->" + hradNumber);
        //请求心跳操作
        requestHeartBeat();
    }

    public BGTimedTaskManage getHeartRunnable() {
        return heartRunnable;
    }
}
