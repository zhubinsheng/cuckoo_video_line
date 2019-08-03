package com.eliaovideo.videoline.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.eliaovideo.videoline.R;
import com.eliaovideo.videoline.base.BaseActivity;
import com.eliaovideo.videoline.utils.Utils;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;
import com.xiao.nicevideoplayer.TxVideoPlayerController;

import java.lang.reflect.Field;

public class CuckooDynamicVideoPlayerActivity extends AppCompatActivity {

    public static final String VIDEO_URL = "VIDEO_URL";
    public static final String COVER_URL = "COVER_URL";

    private NiceVideoPlayer videoPlayer;
    private String videoUrl;
    private String coverUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_dynamic_video_player);
        initView();
        initData();
    }

    protected void initView() {

        videoUrl = getIntent().getStringExtra(VIDEO_URL);
        coverUrl = getIntent().getStringExtra(COVER_URL);

        videoPlayer = findViewById(R.id.video_player);

        videoPlayer.setPlayerType(NiceVideoPlayer.TYPE_IJK); // or NiceVideoPlayer.TYPE_NATIVE
        videoPlayer.setUp(videoUrl, null);

        TxVideoPlayerController controller = new TxVideoPlayerController(this);
        controller.setTitle("");
        Utils.loadHttpImg(this, coverUrl, controller.imageView(), 0);

        //隐藏掉分享按钮，如果需要可以添加操作
        try {
            Field sharedFiled = controller.getClass().getDeclaredField("mShare");
            sharedFiled.setAccessible(true);
            TextView sharedTv = (TextView) sharedFiled.get(controller);
            sharedTv.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        videoPlayer.setController(controller);

    }

    private void initData() {

    }

    @Override
    protected void onStop() {
        super.onStop();
        // 在onStop时释放掉播放器
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }

    @Override
    public void onBackPressed() {
        // 在全屏或者小窗口时按返回键要先退出全屏或小窗口，
        // 所以在Activity中onBackPress要交给NiceVideoPlayer先处理。
        if (NiceVideoPlayerManager.instance().onBackPressd()) return;
        super.onBackPressed();
    }



}
