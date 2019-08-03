package com.eliaovideo.videoline.fragment;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eliaovideo.videoline.R;
import com.eliaovideo.videoline.adapter.recycler.RecyclerRecommendAdapter;
import com.eliaovideo.videoline.api.Api;
import com.eliaovideo.videoline.base.BaseFragment;
import com.eliaovideo.videoline.base.BaseListFragment;
import com.eliaovideo.videoline.inter.JsonCallback;
import com.eliaovideo.videoline.json.JsonRequestsImage;
import com.eliaovideo.videoline.json.JsonRequestsTarget;
import com.eliaovideo.videoline.json.jsonmodle.ImageData;
import com.eliaovideo.videoline.json.jsonmodle.TargetUserData;
import com.eliaovideo.videoline.ui.HomePageActivity;
import com.eliaovideo.videoline.ui.WebViewActivity;
import com.eliaovideo.videoline.ui.common.Common;
import com.eliaovideo.videoline.utils.GlideImageLoader;
import com.eliaovideo.videoline.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.callback.StringCallback;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;


/**
 * 在线
 */

public class OnlineFragment extends BaseListFragment<TargetUserData> {

    @Override
    protected void initDate(View view) {

    }

    @Override
    public void fetchData() {
        requestGetData(false);
    }

    @Override
    protected void initSet(View view) {

    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManage() {
        return new GridLayoutManager(getContext(),2);
    }

    @Override
    protected BaseQuickAdapter getBaseQuickAdapter() {
        return new RecyclerRecommendAdapter(getContext(),dataList);
    }

    @Override
    protected void initDisplayData(View view) {
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    protected void requestGetData(boolean isCache) {
        Api.getOnlineUserList(
                uId,
                uToken,
                page,
                new StringCallback() {

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JsonRequestsTarget requestObj = JsonRequestsTarget.getJsonObj(s);
                        if (requestObj.getCode() == 1){
                            onLoadDataResult(requestObj.getData());
                        }else{
                            onLoadDataError();
                            showToastMsg(getContext(),requestObj.getMsg());
                        }
                    }
                }
        );
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        Common.jumpUserPage(getContext(),dataList.get(position).getId());
    }
}
