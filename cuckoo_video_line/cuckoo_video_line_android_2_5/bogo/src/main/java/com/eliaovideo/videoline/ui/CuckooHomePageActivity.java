package com.eliaovideo.videoline.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.eliaovideo.videoline.R;
import com.eliaovideo.videoline.adapter.CuckooGuardianOrderListAdapter;
import com.eliaovideo.videoline.adapter.FragAdapter;
import com.eliaovideo.videoline.adapter.recycler.RecycleUserHomeGiftAdapter;
import com.eliaovideo.videoline.api.Api;
import com.eliaovideo.videoline.base.BaseActivity;
import com.eliaovideo.videoline.fragment.CuckooHomePageImageFragment;
import com.eliaovideo.videoline.fragment.CuckooHomePageUserInfoFragment;
import com.eliaovideo.videoline.fragment.CuckooHomePageVideoFragment;
import com.eliaovideo.videoline.fragment.DynamicMyFragment;
import com.eliaovideo.videoline.fragment.NewPeopleFragment;
import com.eliaovideo.videoline.helper.SelectResHelper;
import com.eliaovideo.videoline.inter.JsonCallback;
import com.eliaovideo.videoline.json.JsonDoGetSelectContact;
import com.eliaovideo.videoline.json.JsonRequest;
import com.eliaovideo.videoline.json.JsonRequestBase;
import com.eliaovideo.videoline.json.JsonRequestTarget;
import com.eliaovideo.videoline.json.jsonmodle.TargetUserData;
import com.eliaovideo.videoline.manage.RequestConfig;
import com.eliaovideo.videoline.manage.SaveData;
import com.eliaovideo.videoline.modle.ConfigModel;
import com.eliaovideo.videoline.modle.UserModel;
import com.eliaovideo.videoline.ui.common.Common;
import com.eliaovideo.videoline.utils.DialogHelp;
import com.eliaovideo.videoline.utils.GlideImageLoader;
import com.eliaovideo.videoline.utils.StringUtils;
import com.eliaovideo.videoline.utils.Utils;
import com.eliaovideo.videoline.utils.im.IMHelp;
import com.eliaovideo.videoline.widget.BGLevelTextView;
import com.lzy.okgo.callback.StringCallback;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIViewPager;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.sns.TIMFriendResult;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class CuckooHomePageActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    @BindView(R.id.view_pager)
    QMUIViewPager viewPager;

    @BindView(R.id.tv_voice_money)
    TextView tv_voice_money;

    @BindView(R.id.tv_user_id)
    TextView tv_user_id;

    @BindView(R.id.rv_guardian_order)
    RecyclerView rv_guardian_order;

    @BindView(R.id.rl_guardian)
    RelativeLayout rl_guardian;

    @BindView(R.id.ll_select_contact)
    LinearLayout ll_select_contact;

    @BindView(R.id.list_bar_gift)
    RelativeLayout list_bar_gift;

    private FragAdapter mInfoTabFragAdapter;
    private String targetUserId;//目标用户id
    private TargetUserData targetUserData;//当前目标用户信息

    //轮播图
    private ArrayList<String> rollPath = new ArrayList<>();
    private List fragmentList = new ArrayList();

    //数据
    private TextView maxLevelText;//当前最高等级
    private TextView userNickname; //当前player名称
    private TextView userTimeText; //通话x小时
    private TextView userGoodText;//好评
    private TextView userLocationText;//当前player位置
    private TextView userIsonLineText;//当前player是否在线
    private TextView fansNumber;//粉丝数量
    private TextView listBarGiftText;//礼物数量
    private TextView listBarVideoText;//小视频数量
    private TextView listBarPhotoText;//私照数量
    private TextView tvVideoMoney;
    private BGLevelTextView tv_level;
    private ImageView userIsonLine;//是否在线图标
    private ImageView iv_auth_status;//是否认证
    private Banner homePageWallpaper;//轮播组件
    private TextView userLoveme;//关注这个player

    private Dialog menuDialog;
    private TextView infoTv;
    private TextView videoTv, dynamicTv;
    private LinearLayout chatLl;
    private TextView myPrivateImg;

    private int[] actionMenuIds;


    private RecyclerView listBarGiftList;//礼物列表
    private RecycleUserHomeGiftAdapter recycleUserHomeGiftAdapter;

    private List<UserModel> guardianOrderList = new ArrayList<>();
    private CuckooGuardianOrderListAdapter cuckooGuardianOrderListAdapter;

    //收到的礼物列表
    private ArrayList<TargetUserData.GiftBean> giftList = new ArrayList<>();

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_cuckoo_home_page;
    }

    @Override
    protected void initView() {

        QMUIStatusBarHelper.translucent(this); // 沉浸式状态栏

        userIsonLine = findViewById(R.id.userinfo_bar_isonLine);
        iv_auth_status = findViewById(R.id.iv_auth_status);
        userNickname = findViewById(R.id.userinfo_bar_userid);
        userTimeText = findViewById(R.id.userinfo_bar_time_text);
        userGoodText = findViewById(R.id.userinfo_bar_good_text);
        userLocationText = findViewById(R.id.userinfo_bar_location_text);
        userIsonLineText = findViewById(R.id.userinfo_bar_isonLine_text);
        fansNumber = findViewById(R.id.fans_number);
        //listBarGiftList = findViewById(R.id.list_bar_gift_list);
        //listBarVideoList = findViewById(R.id.list_bar_video_list);
        //listBarPhotoList = findViewById(R.id.list_bar_photo_list);
        listBarGiftText = findViewById(R.id.list_bar_gift_text);
        listBarVideoText = findViewById(R.id.list_bar_video_text);
        listBarPhotoText = findViewById(R.id.list_bar_photo_text);
        userLoveme = findViewById(R.id.userinfo_bar_loveme);
        //homePageWallpaper = findViewById(R.id.home_page_wallpaper);
        tv_level = findViewById(R.id.tv_level);
        maxLevelText = findViewById(R.id.userinfo_bar_max_number);
        tvVideoMoney = findViewById(R.id.tv_video_money);
        homePageWallpaper = findViewById(R.id.home_page_wallpaper);
        infoTv = findViewById(R.id.tv_btn_info);
        videoTv = findViewById(R.id.tv_btn_video);
        chatLl = findViewById(R.id.chat_ll);
        dynamicTv = findViewById(R.id.tv_btn_dynamic);
        myPrivateImg = findViewById(R.id.tv_btn_img);
        listBarGiftList = findViewById(R.id.list_bar_gift_list);


        //设置收到的礼物列表
        LinearLayoutManager listGiftLayoutManage = new LinearLayoutManager(this);
        listGiftLayoutManage.setOrientation(LinearLayoutManager.HORIZONTAL);
        listBarGiftList.setLayoutManager(listGiftLayoutManage);

        recycleUserHomeGiftAdapter = new RecycleUserHomeGiftAdapter(this, giftList);
        listBarGiftList.setAdapter(recycleUserHomeGiftAdapter);

        LinearLayoutManager guardianOrderManager = new LinearLayoutManager(getNowContext());
        guardianOrderManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_guardian_order.setLayoutManager(guardianOrderManager);
        cuckooGuardianOrderListAdapter = new CuckooGuardianOrderListAdapter(guardianOrderList);
        rv_guardian_order.setAdapter(cuckooGuardianOrderListAdapter);

        if (ConfigModel.getInitData().getOpen_select_contact() == 1) {
            ll_select_contact.setVisibility(View.VISIBLE);
        } else {
            ll_select_contact.setVisibility(View.GONE);
        }

        selectItem(15, true, R.color.admin_color, 13, false, R.color.black, 13, false, R.color.black, 13, false, R.color.black);
    }

    @Override
    protected void initSet() {

        //轮播参数设置
        //设置图片加载器
        homePageWallpaper.setImageLoader(new GlideImageLoader());
        //设置图片集合
        homePageWallpaper.setImages(rollPath);
        //banner设置方法全部调用完毕时最后调用
        homePageWallpaper.start();

        fragmentList.add(CuckooHomePageUserInfoFragment.getInstance(targetUserId));
        fragmentList.add(CuckooHomePageVideoFragment.getInstance(targetUserId));
        fragmentList.add(CuckooHomePageImageFragment.getInstance(targetUserId));
        fragmentList.add(DynamicMyFragment.getInstance(targetUserId));

        viewPager.setOffscreenPageLimit(1);
        mInfoTabFragAdapter = new FragAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(mInfoTabFragAdapter);
        viewPager.setOnPageChangeListener(this);
    }

    @Override
    protected void initData() {

        targetUserId = getIntent().getStringExtra("str");
        tv_user_id.setText("ID: " + targetUserId);
        requestTargetUserData();
    }

    @Override
    protected void initPlayerDisplayData() {

    }

    @OnClick({R.id.tv_select_wx, R.id.tv_select_qq, R.id.tv_select_phone, R.id.list_bar_gift_text, R.id.rl_guardian, R.id.rl_voice_call, R.id.tv_btn_info, R.id.tv_btn_video, R.id.contribution_btn, R.id.float_back, R.id.float_meun, R.id.ll_chat, R.id.ll_gift, R.id.rl_call, R.id.userinfo_bar_loveme, R.id.tv_btn_img, R.id.tv_btn_dynamic})
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_select_wx:
                clickSelectContact("wx");
                break;
            case R.id.tv_select_qq:
                clickSelectContact("qq");
                break;
            case R.id.tv_select_phone:
                clickSelectContact("phone");
                break;
            case R.id.userinfo_bar_loveme:
                loveThisPlayer();
                break;
            case R.id.tv_btn_info:
                selectItem(15, true, R.color.admin_color, 13, false, R.color.black, 13, false, R.color.black, 13, false, R.color.black);
                viewPager.setCurrentItem(0);
                break;
            case R.id.tv_btn_video:
                selectItem(13, false, R.color.black, 15, true, R.color.admin_color, 13, false, R.color.black, 13, false, R.color.black);
                viewPager.setCurrentItem(1);
                break;
            case R.id.tv_btn_img:
                selectItem(13, false, R.color.black, 13, false, R.color.black, 15, true, R.color.admin_color, 13, false, R.color.black);
                viewPager.setCurrentItem(2);
                break;

            case R.id.tv_btn_dynamic:
                selectItem(13, false, R.color.black, 13, false, R.color.black, 13, false, R.color.black, 15, true, R.color.admin_color);
                viewPager.setCurrentItem(3);
                break;
            //贡献榜按钮
            case R.id.contribution_btn:
                showContribution();
                break;
            case R.id.float_back:
                finish();
                break;
            //举报
            case R.id.float_meun:
                if (targetUserData == null) {
                    return;
                }
                showFloatMeun();
                break;

            //修改资料
            case R.id.edit_mine:
                Intent intent = new Intent(this, EditActivity.class);
                startActivity(intent);
                menuDialog.dismiss();
                break;

            //加入黑名单操作
            case R.id.join_black_list:
                clickBlack();
                menuDialog.dismiss();
                break;
            //举报该用户操作
            case R.id.report_this_user:
                clickReport();
                menuDialog.dismiss();
                break;
            //取消操作
            case R.id.dialog_dis:
                menuDialog.dismiss();
                break;
            case R.id.ll_chat:
                showChatPage(false);
                break;
            case R.id.ll_gift:
                showChatPage(true);
                break;
            //视频通话
            case R.id.rl_call:
                callThisPlayer();
                break;
            case R.id.rl_voice_call:
                callVoice();
                break;
            case R.id.rl_guardian:
                openGuardianPage();
                break;

            case R.id.list_bar_gift_text:
                Intent intentGift = new Intent(this, CuckooGiftCabinetGiftListActivity.class);
                intentGift.putExtra(CuckooGiftCabinetGiftListActivity.TO_USER_ID, targetUserId);
                startActivity(intentGift);
                break;

            default:
        }
    }

    //查看联系方式
    private void clickSelectContact(final String type) {
        Api.doSelectContact(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), type, targetUserId, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {
                JsonDoGetSelectContact data = (JsonDoGetSelectContact) JsonDoGetSelectContact.getJsonObj(s, JsonDoGetSelectContact.class);
                if (data.getCode() == 1) {
                    DialogHelp.getMessageDialog(getNowContext(), data.getNumber()).show();
                } else {
                    DialogHelp.getConfirmDialog(getNowContext(), "是否花费" + data.getPriceFormat() + "查看", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            clickBuyContact( type);
                        }
                    }).show();
                }
            }
        });
    }

    //购买联系方式
    private void clickBuyContact(String type) {
        showLoadingDialog("正在购买...");
        Api.doBuyContact(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), type, targetUserId, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {
                hideLoadingDialog();
                JsonDoGetSelectContact data = (JsonDoGetSelectContact) JsonDoGetSelectContact.getJsonObj(s, JsonDoGetSelectContact.class);
                if (data.getCode() == 1) {
                    DialogHelp.getMessageDialog(getNowContext(), data.getNumber()).show();
                } else {
                    Common.showRechargeDialog(getNowContext(), "余额不足，前去充值");
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                hideLoadingDialog();
            }
        });
    }

    //守护页面
    private void openGuardianPage() {
        WebViewActivity.openH5Activity(CuckooHomePageActivity.this, true, "守护", ConfigModel.getInitData().getApp_h5().getGuardian_list() + "?hostid=" + targetUserId);
    }

    private void selectItem(int infoTvSize, boolean infoTvStyle, int infoColor, int videoTvSize, boolean videoTvStyle, int videoColor, int myPrivateImgSize, boolean myPrivateImgStyle, int myPrivateImgColor, int myDynamicImgSize, boolean myDynamicImgStyle, int myDynamicImgColor) {
        infoTv.setTextSize(infoTvSize);
        TextPaint tp1 = infoTv.getPaint();
        tp1.setFakeBoldText(infoTvStyle);
        infoTv.setTextColor(getResources().getColor(infoColor));

        videoTv.setTextSize(videoTvSize);
        TextPaint tpv1 = videoTv.getPaint();
        tpv1.setFakeBoldText(videoTvStyle);
        videoTv.setTextColor(getResources().getColor(videoColor));

        myPrivateImg.setTextSize(myPrivateImgSize);
        TextPaint tpvImg = myPrivateImg.getPaint();
        tpvImg.setFakeBoldText(myPrivateImgStyle);
        myPrivateImg.setTextColor(getResources().getColor(myPrivateImgColor));


        dynamicTv.setTextSize(myDynamicImgSize);
        TextPaint tpvDy = dynamicTv.getPaint();
        tpvDy.setFakeBoldText(myDynamicImgStyle);
        dynamicTv.setTextColor(getResources().getColor(myDynamicImgColor));
    }


    //关注这个player
    private void loveThisPlayer() {
        Api.doLoveTheUser(
                targetUserId,
                uId,
                uToken,
                new JsonCallback() {
                    @Override
                    public Context getContextToJson() {
                        return getNowContext();
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        super.onSuccess(s, call, response);
                        JsonRequest requestObj = JsonRequest.getJsonObj(s);
                        if (requestObj.getCode() == 1) {
                            concealView(userLoveme);//隐藏关注按钮
                            showToastMsg(getString(R.string.action_success));
                        } else {
                            LogUtils.i("关注当前player:" + requestObj.getMsg());
                        }
                    }
                }
        );
    }

    //给这个player打电话
    private void callThisPlayer() {
        showToastMsg(getString(R.string.now_call));
        Common.callVideo(this, targetUserId, 0);
    }

    //拨打音频电话
    private void callVoice() {
        showToastMsg(getString(R.string.now_call));
        Common.callVideo(this, targetUserId, 1);
    }

    //显示聊天页面
    private void showChatPage(boolean isShowGift) {
        Common.startPrivatePage(this, targetUserId);
    }

    //显示菜单
    private void showFloatMeun() {

        //是自己的个人主页
        if (SaveData.getInstance().getId().equals(targetUserId)) {
            actionMenuIds = new int[]{R.id.edit_mine, R.id.join_black_list, R.id.report_this_user, R.id.dialog_dis};
            menuDialog = showButtomDialog(R.layout.dialog_float_meun, actionMenuIds, 20);
            menuDialog.findViewById(R.id.join_black_list).setVisibility(View.GONE);
            menuDialog.findViewById(R.id.report_this_user).setVisibility(View.GONE);
        } else {
            actionMenuIds = new int[]{R.id.join_black_list, R.id.report_this_user, R.id.dialog_dis};
            menuDialog = showButtomDialog(R.layout.dialog_float_meun_no_edit, actionMenuIds, 20);
        }


        TextView tv = menuDialog.findViewById(R.id.join_black_list);

        if (targetUserData.getIs_black() == 1) {
            tv.setText(R.string.relieve_black);
        }
        menuDialog.show();

    }


    //显示贡献榜
    private void showContribution() {
        Intent intent = new Intent(this, UserContribuionRankActivity.class);
        intent.putExtra(UserContribuionRankActivity.TO_USER_ID, targetUserId);
        startActivity(intent);
    }

    //举报
    private void clickReport() {
        Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra(ReportActivity.REPORT_USER_ID, targetUserId);
        startActivity(intent);
    }

    /**
     * 初始化显示页面数据
     */
    private void initDisplayData() {

        //是自己主页
        if (StringUtils.toInt(targetUserData.getIs_visible_bottom_btn()) == 0) {
            chatLl.setVisibility(View.GONE);
        } else {
            chatLl.setVisibility(View.VISIBLE);
        }

        if(targetUserData.getSex() == 2){
            list_bar_gift.setVisibility(View.VISIBLE);
        }

        //初始化显示页面数据
        tv_level.setLevelInfo(targetUserData.getSex(), targetUserData.getLevel());
        //设置是否显示关注
        userLoveme.setVisibility(StringUtils.toInt(targetUserData.getAttention()) == 1 ? View.INVISIBLE : View.VISIBLE);

        userNickname.setText(targetUserData.getUser_nickname());
        userTimeText.setText(getString(R.string.call) + targetUserData.getCall());
        userGoodText.setText(getString(R.string.praise) + targetUserData.getEvaluation());
        userLocationText.setText(targetUserData.getAddress());

        if (StringUtils.toInt(targetUserData.getUser_status()) == 1) {
            tvVideoMoney.setText("视频聊 " + targetUserData.getVideo_deduction() + RequestConfig.getConfigObj().getCurrency() + getString(R.string.minute));
            tv_voice_money.setText("语音聊 " + targetUserData.getVoice_deduction() + RequestConfig.getConfigObj().getCurrency() + getString(R.string.minute));
        }

        iv_auth_status.setImageResource(SelectResHelper.getAttestationResForSex(targetUserData.getSex(), StringUtils.toInt(targetUserData.getUser_status())));

        fansNumber.setText(targetUserData.getAttention_fans() + getString(R.string.fans));

        //是否在线
        userIsonLineText.setText(StringUtils.toInt(targetUserData.getIs_online()) == 1 ? getString(R.string.on_line) : getString(R.string.off_line));

        userIsonLine.setBackgroundResource(SelectResHelper.getOnLineRes(StringUtils.toInt(targetUserData.getIs_online())));

        //礼物--视频--私照--统计个数
        //listBarVideoText.setText("小视频("+objToString(targetUserData.getVideo_count())+")");
        //listBarPhotoText.setText("私照("+objToString(targetUserData.getPictures_count())+")");

//        if (targetUserId.equals(SaveData.getInstance().getId())){
//            //查询自己
//            userLoveme.setVisibility(View.GONE);
//            listBarGiftText.setText("送出的礼物("+objToString(targetUserData.getGift_count())+")");
//        }else{
//            listBarGiftText.setText("收到的礼物("+objToString(targetUserData.getGift_count())+")");
//        }

        if (targetUserData.getImg() != null) {
            for (TargetUserData.ImgBean img : targetUserData.getImg()) {
                rollPath.add(Utils.getCompleteImgUrl(img.getImg()));
            }
            if (targetUserData.getImg().size() == 0) {
                rollPath.add(Utils.getCompleteImgUrl(targetUserData.getAvatar()));
            }
        }

        listBarGiftText.setText(String.format(Locale.CHINA, "收到的礼物(%d)", targetUserData.getGift_count()));

        if (targetUserData.getImg() != null) {
            for (TargetUserData.ImgBean img : targetUserData.getImg()) {
                rollPath.add(Utils.getCompleteImgUrl(img.getImg()));
            }
            if (targetUserData.getImg().size() == 0) {
                rollPath.add(Utils.getCompleteImgUrl(targetUserData.getAvatar()));
            }
        }

        //填充收到的礼物列表
        if (targetUserData.getGift() != null) {
            giftList.addAll(targetUserData.getGift());
            recycleUserHomeGiftAdapter.notifyDataSetChanged();
        }

        //填充收到的礼物列表
//        if(targetUserData.getGift() != null){
//            giftList.addAll(targetUserData.getGift());
//            recycleUserHomeGiftAdapter.notifyDataSetChanged();
//        }
//
//        //填充发布的视频列表
//        if(targetUserData.getVideo() != null){
//            videoList.addAll(targetUserData.getVideo());
//            recycleUserHomePhotoAdapter.notifyDataSetChanged();
//        }
//        //填充发布的私照列表
//        if(targetUserData.getPictures() != null){
//            photoList.addAll(targetUserData.getPictures());
//            recycleUserHomeVideoAdapter.notifyDataSetChanged();
//        }

        homePageWallpaper.setImages(rollPath);
        homePageWallpaper.start();

        if (targetUserData.getSex() == 2) {
            videoTv.setVisibility(View.VISIBLE);
            dynamicTv.setVisibility(View.VISIBLE);
            myPrivateImg.setVisibility(View.VISIBLE);
            rl_guardian.setVisibility(View.VISIBLE);
        }

        guardianOrderList.clear();
        guardianOrderList.addAll(targetUserData.getGuardian_user_list());
        cuckooGuardianOrderListAdapter.notifyDataSetChanged();

    }

    /**
     * 获取目标用户基础信息
     */
    private void requestTargetUserData() {

        Api.getUserHomePageInfo(
                targetUserId,
                uId,
                uToken,
                new JsonCallback() {
                    @Override
                    public Context getContextToJson() {
                        return getNowContext();
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JsonRequestTarget jsonTargetUser = JsonRequestTarget.getJsonObj(s);
                        if (jsonTargetUser.getCode() == 1) {
                            targetUserData = jsonTargetUser.getData();
                            initDisplayData();
                        } else {
                            //请求失败
                            showToastMsg(jsonTargetUser.getMsg());
                        }
                    }
                }
        );
    }


    //拉黑
    private void clickBlack() {

        showLoadingDialog(getString(R.string.loading_action));
        Api.doRequestBlackUser(uId, uToken, targetUserId, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {

                hideLoadingDialog();
                JsonRequestBase jsonObj = JsonRequestBase.getJsonObj(s, JsonRequestBase.class);
                if (jsonObj.getCode() == 1) {
                    showToastMsg(getResources().getString(R.string.action_success));
                    if (targetUserData.getIs_black() == 1) {
                        targetUserData.setIs_black(0);
                        IMHelp.deleteBlackUser(targetUserId, new TIMValueCallBack<List<TIMFriendResult>>() {
                            @Override
                            public void onError(int i, String s) {
                                LogUtils.i("解除拉黑用户失败:" + s);
                            }

                            @Override
                            public void onSuccess(List<TIMFriendResult> timFriendResults) {
                                LogUtils.i("解除拉黑用户成功");
                            }
                        });
                    } else {
                        targetUserData.setIs_black(1);
                        IMHelp.addBlackUser(targetUserId, new TIMValueCallBack<List<TIMFriendResult>>() {
                            @Override
                            public void onError(int i, String s) {
                                LogUtils.i("拉黑用户失败:" + s);
                            }

                            @Override
                            public void onSuccess(List<TIMFriendResult> timFriendResults) {
                                LogUtils.i("拉黑用户成功");
                            }
                        });
                    }
                } else {
                    showToastMsg(jsonObj.getMsg());
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                hideLoadingDialog();
            }
        });
    }

    /**
     * viewpager滑动监听
     *
     * @param position
     * @param positionOffset
     * @param positionOffsetPixels
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                selectItem(15, true, R.color.admin_color, 13, false, R.color.black, 13, false, R.color.black, 13, false, R.color.black);
                viewPager.setCurrentItem(0);
                break;
            case 1:
                selectItem(13, false, R.color.black, 15, true, R.color.admin_color, 13, false, R.color.black, 13, false, R.color.black);
                viewPager.setCurrentItem(1);
                break;
            case 2:
                selectItem(13, false, R.color.black, 13, false, R.color.black, 15, true, R.color.admin_color, 13, false, R.color.black);
                viewPager.setCurrentItem(2);
                break;

            case 3:
                selectItem(13, false, R.color.black, 13, false, R.color.black, 13, false, R.color.black, 15, true, R.color.admin_color);
                viewPager.setCurrentItem(3);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
