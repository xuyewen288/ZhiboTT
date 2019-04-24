package com.xunye.zhibott.acitvity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.xunye.zhibott.R;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.widget.VideoView;

public class CameraActivity extends AppCompatActivity implements View.OnTouchListener, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnAudioListener, IMediaPlayer.OnRecordingListener {

    VideoView videoView;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        url=getIntent().getStringExtra("url");

        videoView   = (VideoView)findViewById(R.id.videoView);

        //调整播放器显示宽高比
        DisplayMetrics metric = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(metric);
        int screenWidth  = metric.widthPixels;
        int screenHeight = metric.heightPixels;
        int videoHeight  = (screenHeight > screenWidth) ? screenWidth*3/4 : screenHeight*3/4;
        int videoWidth   = videoHeight * 16 / 9;
//        int margin       = (screenHeight > screenWidth) ? (videoWidth - screenWidth)/2 : (videoWidth - screenHeight)/2;

        RelativeLayout.LayoutParams videoParams = (RelativeLayout.LayoutParams) videoView.getLayoutParams();
        videoParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        videoParams.height= videoHeight;
        videoView.setLayoutParams(videoParams);
        videoView.setTranslationX(0);

        videoView.setOnTouchListener(this);
        videoView.setOnPreparedListener(this);
        videoView.setOnErrorListener(this);
        videoView.setOnAudioListener(this);
        videoView.setOnRecordingListener(this);

        videoView.playVideo(url);
//        videoView.playLyyRTMPVideo();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId() == R.id.videoView) {
            //播放器自带缩放功能
            v.onTouchEvent(event);
            return true;
        }
        return false;
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {

    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onAudioConnected(IMediaPlayer iMediaPlayer) {

    }

    @Override
    public void onAudioVolume(double v) {

    }

    @Override
    public void onAudioError(IMediaPlayer iMediaPlayer, int i, int i1) {

    }

    @Override
    public void onAudioClosed(IMediaPlayer iMediaPlayer) {

    }

    @Override
    public void onRecordStart(IMediaPlayer iMediaPlayer) {

    }

    @Override
    public void onRecordStopped(IMediaPlayer iMediaPlayer) {

    }

    @Override
    public void onRecordSizeChanged(IMediaPlayer iMediaPlayer, long l, long l1) {

    }

    @Override
    public void onRecordError(IMediaPlayer iMediaPlayer, int i, int i1) {

    }
}
