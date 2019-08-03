package com.eliaovideo.videoline.fragment;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eliaovideo.videoline.R;
import com.eliaovideo.videoline.adapter.recycler.RecyclerRecommendAdapter;
import com.eliaovideo.videoline.api.Api;
import com.eliaovideo.videoline.base.BaseFragment;
import com.eliaovideo.videoline.base.BaseListFragment;
import com.eliaovideo.videoline.inter.JsonCallback;
import com.eliaovideo.videoline.json.JsonRequestBase;
import com.eliaovideo.videoline.json.JsonRequestsPeople;
import com.eliaovideo.videoline.json.jsonmodle.TargetUserData;
import com.eliaovideo.videoline.ui.common.Common;
import com.chad.library.adapter.base.BaseQuickAdapter;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 同城
 */
public class SameCityFragment extends BaseListFragment<TargetUserData> {
    @Override
    protected void initDate(View view) {

    }

    @Override
    public void fetchData() {

        //加载数据源
        requestGetData(false);
    }

    @Override
    protected void initSet(View view) {

    }

    @Override
    protected void initDisplayData(View view) {

    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManage() {
        return new GridLayoutManager(getContext(), 2);
    }

    @Override
    protected BaseQuickAdapter getBaseQuickAdapter() {
        return new RecyclerRecommendAdapter(getContext(), dataList);
    }

    @Override
    protected void requestGetData(boolean isCache) {
        Api.getNewPeoplePageList(
                uId,
                uToken,
                page,
                new JsonCallback() {
                    @Override
                    public Context getContextToJson() {
                        return getContext();
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {

                        JsonRequestsPeople requestObj = (JsonRequestsPeople) JsonRequestBase.getJsonObj(s, JsonRequestsPeople.class);
                        if (requestObj.getCode() == 1) {
                            onLoadDataResult(requestObj.getData());
                        } else {
                            onLoadDataError();
                            showToastMsg(getContext(), requestObj.getMsg());
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        onLoadDataError();
                    }
                }
        );
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        Common.jumpUserPage(getContext(), dataList.get(position).getId());
    }
}
