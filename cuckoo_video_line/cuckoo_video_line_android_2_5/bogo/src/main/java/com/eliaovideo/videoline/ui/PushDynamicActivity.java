package com.eliaovideo.videoline.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.eliaovideo.videoline.LiveConstant;
import com.eliaovideo.videoline.R;
import com.eliaovideo.videoline.adapter.FullyGridLayoutManager;
import com.eliaovideo.videoline.adapter.GridImageAdapter;
import com.eliaovideo.videoline.api.Api;
import com.eliaovideo.videoline.audiorecord.AudioPlaybackManager;
import com.eliaovideo.videoline.audiorecord.AudioRecordJumpUtil;
import com.eliaovideo.videoline.audiorecord.entity.AudioEntity;
import com.eliaovideo.videoline.audiorecord.util.PaoPaoTips;
import com.eliaovideo.videoline.audiorecord.view.CommonSoundItemView;
import com.eliaovideo.videoline.base.BaseActivity;
import com.eliaovideo.videoline.event.EventBusConfig;
import com.eliaovideo.videoline.event.RefreshMessageEvent;
import com.eliaovideo.videoline.event.VoiceRecordEvent;
import com.eliaovideo.videoline.helper.ImageUtil;
import com.eliaovideo.videoline.json.JsonDoRequestGetOssInfo;
import com.eliaovideo.videoline.json.JsonRequestBase;
import com.eliaovideo.videoline.manage.SaveData;
import com.eliaovideo.videoline.utils.StringUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.lzy.okgo.callback.StringCallback;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class PushDynamicActivity extends BaseActivity {

    @BindView(R.id.btn_voice_record)
    Button mBtnVoiceRecord;

    @BindView(R.id.btn_video_record)
    Button mBtnVideoRecord;

    @BindView(R.id.et_input)
    EditText mEtInput;

    @BindView(R.id.tv_mark)
    TextView mark;

    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    @BindView(R.id.pp_sound_item_view)
    CommonSoundItemView soundItemView;

    private GridImageAdapter adapter;

    private boolean hasVoiceFile = false;
    private String voiceFilePath = "";
    private List<LocalMedia> selectList = new ArrayList<>();
    private int maxSelectNum = 9;

    private int selectType = PictureMimeType.ofImage();

    /**
     * 1 视频 0图片
     */
    private int fileType = 0;


    private String uploadVideoUrl = "";
    private String uploadImgUrl = "";
    private String uploadVideoThmbUrl = "";
    private String uploadAudoUrl = "";
    private List<String> uploadImgUrlList = new ArrayList<>();
    private String content;

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_push_dynamic;
    }

    @Override
    protected void initView() {
        QMUIStatusBarHelper.translucent(this); // 沉浸式状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        adapter = new GridImageAdapter(this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(maxSelectNum);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initSet() {
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initPlayerDisplayData() {

    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {

            PictureSelector.create(PushDynamicActivity.this)
                    .openGallery(selectType)
                    .maxSelectNum(maxSelectNum)
                    .previewVideo(true)
                    .recordVideoSecond(60)
                    .forResult(PictureConfig.CHOOSE_REQUEST);
        }
    };

    @OnClick({R.id.rl_input, R.id.btn_voice_record, R.id.tv_push, R.id.tv_cancel, R.id.btn_video_record})
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rl_input:
                KeyboardUtils.showSoftInput(mEtInput);
                break;
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_push:
                clickPushDynamic();
                break;
            case R.id.btn_voice_record:
                clickRecrodVoice();
                break;
            case R.id.btn_video_record:
                clickSelectVideo();
                break;

//            case R.id.iv_add_img:
//
//
//                PictureSelector.create(this)
//                        .openGallery(PictureMimeType.ofImage())
//                        .maxSelectNum(9)
//                        .forResult(PictureConfig.CHOOSE_REQUEST);
//                break;

            default:
                break;
        }
    }

    private void clickRecrodVoice() {
        if (hasVoiceFile) {
            File tempFile = new File(voiceFilePath);
            if (tempFile.exists()) {
                tempFile.delete();
            }
            hasVoiceFile = false;
            soundItemView.setVisibility(View.GONE);
            mBtnVoiceRecord.setText("录制音频");
            return;
        }

        AudioRecordJumpUtil.startRecordAudio(PushDynamicActivity.this);
    }

    private void clickSelectVideo() {
        String trim = mBtnVideoRecord.getText().toString().trim();
        if ("上传视频".equals(trim)) {
            mBtnVideoRecord.setText("上传图片");
            mark.setText("添加视频不超过1个，文字备注不超过300字");
            selectType = PictureMimeType.ofVideo();
            selectList.clear();

            maxSelectNum = 1;

        } else {
            mBtnVideoRecord.setText("上传视频");
            mark.setText("添加图片不超过9张，文字备注不超过300字");
            selectType = PictureMimeType.ofImage();
            selectList.clear();

            maxSelectNum = 9;
        }

        adapter.setSelectMax(maxSelectNum);
        adapter.setList(selectList);
        adapter.notifyDataSetChanged();
    }

    private void clickPushDynamic() {
        content = mEtInput.getText().toString();
        if (TextUtils.isEmpty(content)) {
            ToastUtils.showLong("内容不能为空！");
            return;
        }

        //上传视频 图片到七牛云
        getUploadOssSign();

    }

    private void toPush() {
        showLoadingDialog("正在发布...");
        Api.doRequestPushDynamic(SaveData.getInstance().getId(), SaveData.getInstance().getToken(),
                content,
                hasVoiceFile ? 1 : 0,
                uploadImgUrlList, uploadVideoUrl, uploadAudoUrl, fileType, uploadVideoThmbUrl, new StringCallback() {

                    @Override
                    public void onSuccess(String s, Call call, Response response) {

                        hideLoadingDialog();
                        JsonRequestBase data = JsonRequestBase.getJsonObj(s, JsonRequestBase.class);
                        if (StringUtils.toInt(data.getCode()) == 1) {
                            ToastUtils.showLong("发布成功！");
                            EventBus.getDefault().post(new RefreshMessageEvent("refresh_dynamic_list"));
                            finish();
                        } else {
                            ToastUtils.showLong(data.getMsg());
                        }

                    }
                });
    }


    private void getUploadOssSign() {
        showLoadingDialog(getString(R.string.loading_upload_info));

        Api.doRequestGetOSSInfo(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {


                JsonDoRequestGetOssInfo data = (JsonDoRequestGetOssInfo) JsonRequestBase.getJsonObj(s, JsonDoRequestGetOssInfo.class);
                if (StringUtils.toInt(data.getCode()) == 1) {
                    //上传音频 如果有
                    if (hasVoiceFile) {
                        uploadVoiceFile(data, new File(voiceFilePath));
                    } else {
                        //上传视频 图片
                        uploadImgAndVideo(data);
                    }

                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                hideLoadingDialog();
            }
        });
    }

    private void uploadImgAndVideo(JsonDoRequestGetOssInfo data) {

        uploadImgUrlList.clear();

        if (selectType == PictureMimeType.ofImage()) {
            fileType = 0;

            for (int i = 0; i < selectList.size(); i++) {
                uploadIdCardImg(data, new File(selectList.get(i).getPath()), fileType);
            }

            //如果没选择图片
            if (selectList.size() == 0) {
                //发布
                toPush();
            }

        } else {
            fileType = 1;

            //如果没选择视频
            if (selectList.size() == 0) {
                //发布
                toPush();
                return;
            }

            //获取封面
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            media.setDataSource(selectList.get(0).getPath());// videoPath 本地视频的路径
            Bitmap bitmap = media.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            File saveFile = ImageUtil.getSaveFile(bitmap, String.valueOf(System.currentTimeMillis()));

            uploadIdCardImg(data, new File(saveFile.getPath()), fileType);

        }
    }

    //上传音频
    private void uploadVoiceFile(final JsonDoRequestGetOssInfo data, File file) {

        //设置上传后文件的key
        final String upkey = LiveConstant.AUDIO_DIR + System.currentTimeMillis() + "_" + file.getName();

        String token = data.getToken();
        UploadManager uploadManager = new UploadManager();
        uploadManager.put(file, upkey, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject response) {

                        uploadAudoUrl = data.getDomain() + "/" + upkey;

                        //上传视频 图片
                        uploadImgAndVideo(data);


                    }

                }, null);
    }

    //上传视频
    private void uploadVideo(final JsonDoRequestGetOssInfo data, File file) {

        //设置上传后文件的key
        final String upkey = LiveConstant.VIDEO_DIR + System.currentTimeMillis() + "_" + file.getName();

        String token = data.getToken();
        UploadManager uploadManager = new UploadManager();
        uploadManager.put(file, upkey, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject response) {
                        uploadVideoUrl = data.getDomain() + "/" + upkey;

                        //发布
                        toPush();
                    }


                }, null);
    }

    //上传图片或封面
    private void uploadIdCardImg(final JsonDoRequestGetOssInfo data, File file, final int fileType) {

        //设置上传后文件的key
        final String upkey = LiveConstant.VIDEO_COVER_IMG_DIR + System.currentTimeMillis() + "_" + file.getName();

        String token = data.getToken();
        UploadManager uploadManager = new UploadManager();
        uploadManager.put(file, upkey, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject response) {
                        if (fileType == 0) {
                            uploadImgUrl = data.getDomain() + "/" + upkey;
                            uploadImgUrlList.add(uploadImgUrl);

                            if (uploadImgUrlList.size() == selectList.size()) {
                                //发布
                                toPush();
                            }

                        } else {
                            uploadVideoThmbUrl = data.getDomain() + "/" + upkey;

                            //上传视频
                            uploadVideo(data, new File(selectList.get(0).getPath()));


                        }

                    }

                }, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(VoiceRecordEvent mainThreadEvent) {
        if (mainThreadEvent.getWhat() == EventBusConfig.SOUND_FEED_RECORD_FINISH) {
            Object soundPath = mainThreadEvent.getObj();
            if (soundPath != null && soundPath instanceof String) {
                String path = (String) soundPath;
                voiceFilePath = path;
                AudioEntity entity = new AudioEntity();
                entity.setUrl(path);
                int duration = AudioPlaybackManager.getDuration(path);
                if (duration <= 0) {
                    //PPLog.d(TAG, "duration <= 0");
                    PaoPaoTips.showDefault(this, "无权限");

                    File tempFile = new File(path);
                    if (tempFile.exists()) {
                        tempFile.delete();
                        return;
                    }
                } else {
                    entity.setDuration(duration / 1000);
                    mBtnVoiceRecord.setText("删除音频");
                    soundItemView.setSoundData(entity);
                    soundItemView.setVisibility(View.VISIBLE);
                    hasVoiceFile = true;
                    //PPLog.d(TAG, "soundPath:" + path);
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                    for (LocalMedia media : selectList) {
                        Log.i("图片-----》", media.getPath());
                    }
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }
}
