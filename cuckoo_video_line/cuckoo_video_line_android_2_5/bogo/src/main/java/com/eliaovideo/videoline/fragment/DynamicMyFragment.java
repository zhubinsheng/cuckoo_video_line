package com.eliaovideo.videoline.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.eliaovideo.videoline.R;
import com.eliaovideo.videoline.adapter.DynamicAdapter;
import com.eliaovideo.videoline.api.Api;
import com.eliaovideo.videoline.base.BaseListFragment;
import com.eliaovideo.videoline.event.RefreshMessageEvent;
import com.eliaovideo.videoline.json.JsonRequestBase;
import com.eliaovideo.videoline.json.JsonRequestDoGetDynamicList;
import com.eliaovideo.videoline.manage.SaveData;
import com.eliaovideo.videoline.modle.DynamicListModel;
import com.eliaovideo.videoline.ui.DynamicDetailActivity;
import com.eliaovideo.videoline.ui.common.Common;
import com.eliaovideo.videoline.utils.DialogHelp;
import com.eliaovideo.videoline.utils.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.callback.StringCallback;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 动态 关注
 */
public class DynamicMyFragment extends BaseListFragment<DynamicListModel> implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {

    public static final String TO_USER_ID = "TO_USER_ID";
    private String toUserId;

    public static DynamicMyFragment getInstance(String toUserId) {

        DynamicMyFragment dynamicMyFragment = new DynamicMyFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TO_USER_ID, toUserId);
        dynamicMyFragment.setArguments(bundle);
        return dynamicMyFragment;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        toUserId = getArguments().getString(TO_USER_ID);
    }

    @Override
    protected void initSet(View view) {

    }

    @Override
    protected void initDisplayData(View view) {

    }

    @Override
    protected boolean isRegEvent() {
        return true;
    }

    @Override
    protected BaseQuickAdapter getBaseQuickAdapter() {
        return new DynamicAdapter(getContext(), dataList);
    }

    @Override
    protected void requestGetData(boolean isCache) {
        Api.doRequestGetMyDynamicList(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), toUserId, page, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {
                JsonRequestDoGetDynamicList data = (JsonRequestDoGetDynamicList) JsonRequestBase.getJsonObj(s, JsonRequestDoGetDynamicList.class);
                if (StringUtils.toInt(data.getCode()) == 1) {
                    onLoadDataResult(data.getList());
                } else {
                    mSwRefresh.setRefreshing(false);
                    ToastUtils.showLong(data.getMsg());
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                mSwRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(getContext(), DynamicDetailActivity.class);
        intent.putExtra(DynamicDetailActivity.DYNAMIC_DATA, dataList.get(position));
        startActivity(intent);
    }

    @Override
    public void onItemChildClick(final BaseQuickAdapter adapter, View view, final int position) {
        if (view.getId() == R.id.item_iv_like_count) {
            Api.doRequestDynamicLike(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), dataList.get(position).getId(), new StringCallback() {

                @Override
                public void onSuccess(String s, Call call, Response response) {

                    JsonRequestBase data = JsonRequestBase.getJsonObj(s, JsonRequestBase.class);
                    if (StringUtils.toInt(data.getCode()) == 1) {
                        if (StringUtils.toInt(dataList.get(position).getIs_like()) == 1) {
                            dataList.get(position).setIs_like("0");
                            dataList.get(position).decLikeCount(1);
                        } else {
                            dataList.get(position).setIs_like("1");
                            dataList.get(position).plusLikeCount(1);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        } else if (view.getId() == R.id.item_del) {

            DialogHelp.getConfirmDialog(getContext(), "确定要删除动态？", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    clickDelDynamic(position);
                }
            }).show();
        } else if (view.getId() == R.id.item_tv_chat) {
            Common.startPrivatePage(getContext(), dataList.get(position).getUserInfo().getId());
        } else if (view.getId() == R.id.item_iv_avatar) {
            Common.jumpUserPage(getContext(), dataList.get(position).getUserInfo().getId());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshMessageEvent messageEvent) {
        if (messageEvent.getMessage().equals("refresh_dynamic_list")) {
            page = 1;
            requestGetData(false);
        }
    }

    private void clickDelDynamic(final int position) {

        showLoadingDialog("正在操作...");
        Api.doRequestDelDynamic(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), dataList.get(position).getId(), new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {

                hideLoadingDialog();
                JsonRequestBase data = JsonRequestBase.getJsonObj(s, JsonRequestBase.class);
                if (StringUtils.toInt(data.getCode()) == 1) {

                    dataList.remove(position);
                    baseQuickAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                hideLoadingDialog();
            }
        });
    }
}