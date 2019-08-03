package com.eliaovideo.videoline.fragment;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eliaovideo.chat.fragment.ConversationFragment;
import com.eliaovideo.chat.model.Conversation;
import com.eliaovideo.videoline.R;
import com.eliaovideo.videoline.adapter.recycler.RecyclerMsgAdapter;
import com.eliaovideo.videoline.api.Api;
import com.eliaovideo.videoline.base.BaseFragment;
import com.eliaovideo.videoline.event.BaseEvent;
import com.eliaovideo.videoline.event.EImOnNewMessages;
import com.eliaovideo.videoline.inter.AdapterOnItemClick;
import com.eliaovideo.videoline.inter.JsonCallback;
import com.eliaovideo.videoline.json.JsonGetMsgPage;
import com.eliaovideo.videoline.json.JsonRequestBase;
import com.eliaovideo.videoline.json.JsonRequestsImage;
import com.eliaovideo.videoline.json.jsonmodle.ImageData;
import com.eliaovideo.videoline.manage.RequestConfig;
import com.eliaovideo.videoline.manage.SaveData;
import com.eliaovideo.videoline.modle.ConfigModel;
import com.eliaovideo.videoline.msg.ui.AboutFansActivity;
import com.eliaovideo.videoline.ui.CuckooSubscribeActivity;
import com.eliaovideo.videoline.ui.PrivateChatActivity;
import com.eliaovideo.videoline.ui.SystemMessageActivity;
import com.eliaovideo.videoline.ui.WebViewActivity;
import com.eliaovideo.videoline.ui.common.Common;
import com.eliaovideo.videoline.utils.GlideImageLoader;
import com.eliaovideo.videoline.utils.IMUtils;
import com.lzy.okgo.callback.StringCallback;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.ext.message.TIMConversationExt;
import com.tencent.imsdk.ext.message.TIMManagerExt;
import com.tencent.open.utils.ThreadManager;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 消息
 * Created by weipeng on 2017/12/28 0028.
 */
public class MsgFragment extends BaseFragment implements AdapterOnItemClick {
    //功能
    private QMUITopBar msgQMUITopBar;//头部
    private Banner banner;//轮播
    private RecyclerView mRecyclerView;//RecyclerView
    //自定义RecyclerView-adapter适配器
    private RecyclerMsgAdapter mRecyclerMsgAdapter;
    //RecyclerView的grid视图
    private GridLayoutManager mLayoutManager;

    private ArrayList<String> rollImg = new ArrayList<>();
    private List<String> listString;
    private List<Integer> numberString;
    private List<Integer> drawableList;

    private List<ImageData> bannerList = new ArrayList<>();
    private ConversationFragment conversationFragment;

    //////////////////////////////////////////初始化操作//////////////////////////////////////////////
    @Override
    protected View getBaseView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.view_msg_page, container, false);
    }

    @Override
    protected void initView(View view) {
        msgQMUITopBar = view.findViewById(R.id.msg_page_topBar);
        banner = view.findViewById(R.id.mRollPagerView);
        mRecyclerView = view.findViewById(R.id.mRecyclerView);

        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                ImageData imageData = bannerList.get(position);
                if (imageData.getUrl() != null && imageData.getTitle() != null) {
                    WebViewActivity.openH5Activity(getContext(), true, imageData.getTitle(), imageData.getUrl());
                }
            }
        });
        banner.setVisibility(View.GONE);
    }

    @Override
    protected void initDate(View view) {
        doList();
        //requestRollView();
    }

    @Override
    protected void initSet(View view) {
        refreshRollViEW();

        //设置列表
        mRecyclerMsgAdapter = new RecyclerMsgAdapter(listString, numberString, drawableList);
        mLayoutManager = new GridLayoutManager(getContext(), 1);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mRecyclerMsgAdapter);

        //设置topBar
        msgQMUITopBar.setFitsSystemWindows(true);
        msgQMUITopBar.setTitle(getString(R.string.message));
        //设置监听
        mRecyclerMsgAdapter.setOnItemClickListener(this);

        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (conversationFragment == null) {
            conversationFragment = new ConversationFragment();
        }
        ft.replace(R.id.fragment, conversationFragment);
        ft.commit();

        //getUnReadMsg();
    }

    @Override
    protected void initDisplayData(View view) {

    }

    ////////////////////////////////////////////监听方法处理//////////////////////////////////////////
    @Override
    public void onClick(View v) {

    }


    private void getUnReadMsg() {

        if (numberString != null && numberString.size() > 0 && mRecyclerMsgAdapter != null) {
            ThreadManager.executeOnSubThread(new Runnable() {
                @Override
                public void run() {

                    numberString.set(1, IMUtils.getIMUnReadMessageCount());
                    ThreadManager.getMainHandler().post(new Runnable() {
                        @Override
                        public void run() {

                            mRecyclerMsgAdapter.notifyDataSetChanged();
                        }
                    });
                }

            });
        }

    }

    ////////////////////////////////////////////本地工具方法//////////////////////////////////////////

    /**
     * 获取轮播
     */
    private void requestRollView() {
        Api.getRollImage(
                uId,
                uToken,
                "2",
                new JsonCallback() {
                    @Override
                    public Context getContextToJson() {
                        return getContext();
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        super.onSuccess(s, call, response);
                        JsonRequestsImage requestObj = JsonRequestsImage.getJsonObj(s);
                        if (requestObj.getCode() == 1) {
                            bannerList = requestObj.getData();
                            rollImg.clear();
                            for (ImageData img : bannerList) {
                                rollImg.add(img.getImage());
                            }
                            refreshRollViEW();//刷新设置轮播图
                        } else {
                            showToastMsg(getContext(), requestObj.getMsg());
                        }
                    }
                }
        );
    }

    /**
     * 刷新轮播
     */
    private void refreshRollViEW() {
        //设置轮播
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(rollImg);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
    }

    /**
     * 加载静态资源
     */
    public void doList() {
        listString = new ArrayList<>();
        listString.add(getString(R.string.system_message));
        listString.add(getString(R.string.subscribe));

        numberString = new ArrayList<>();
        numberString.add(0);
        numberString.add(0);

        drawableList = new ArrayList<>();
        drawableList.add(R.drawable.icon_system_notification);
        drawableList.add(R.mipmap.booking);
    }

    @Override
    public void onItemClick(View view, ViewName viewName, int position) {
        //showToastMsg(getContext(),"点击了"+position);
        switch (position) {
            case 0:
                //系统消息
                //goActivity(getContext(), SystemMessageActivity.class);
                WebViewActivity.openH5Activity(getContext(), true, "系统消息", RequestConfig.getConfigObj().getSystemMessage());
                break;

            case 1:
                goActivity(getContext(), CuckooSubscribeActivity.class);
//                showToastMsg(getContext(),"subs");
                break;
        }
    }


    private void getSystemUnReadMsgCount() {
        Api.getMsgPageInfo(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {
                JsonGetMsgPage unReadSystemMsg = (JsonGetMsgPage) JsonRequestBase.getJsonObj(s, JsonGetMsgPage.class);
                if (unReadSystemMsg.getCode() == 1) {
                    if (unReadSystemMsg.getSum() >= 0) {
                        numberString.set(0, unReadSystemMsg.getSum());
                        mRecyclerMsgAdapter.notifyDataSetChanged();
                    }

                    if (unReadSystemMsg.getUn_handle_subscribe_num() >= 0) {
                        numberString.set(1, unReadSystemMsg.getUn_handle_subscribe_num());
                        mRecyclerMsgAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMsgEventThread(EImOnNewMessages var1) {
        //getUnReadMsg();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //getUnReadMsg();
    }

    @Override
    public void onResume() {
        super.onResume();
        //getUnReadMsg();
        getSystemUnReadMsgCount();
    }

    @Override
    protected boolean isRegEvent() {
        return true;
    }
}
