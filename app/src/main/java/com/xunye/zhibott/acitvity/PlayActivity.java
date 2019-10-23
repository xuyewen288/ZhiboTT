package com.xunye.zhibott.acitvity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.xunye.zhibott.MyApplication;
import com.xunye.zhibott.R;
import com.xunye.zhibott.helper.LogUtil;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.widget.VideoView;

public class PlayActivity extends AppCompatActivity implements IMediaPlayer.OnPreparedListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnVideoSizeChangedListener {

    VideoView mVideoView;
    String url;
    long watchtime;
    CountDownTimer countDownTimer;

    @BindViews({R.id.bt_play,R.id.bt_stop,R.id.bt_photo,R.id.bt_allscreen})
    Button[] buttons;

    @BindView(R.id.videoframe)
    View videoFrame;
    @BindView(R.id.layout_controller)
    View layoutController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        ButterKnife.bind(this);
        this.watchtime= MyApplication.watchtime;
        url=getIntent().getStringExtra("url");
        mVideoView=findViewById(R.id.videoView);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnBufferingUpdateListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.setOnVideoSizeChangedListener(this);

        countDownTimer=new CountDownTimer(watchtime*1000+300,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
//                LogUtil.e("watchtime=>"+watchtime--);
            }

            @Override
            public void onFinish() {
                AlertDialog.Builder builder=new AlertDialog.Builder(PlayActivity.this)
                        .setTitle("提示").setMessage("亲，观看时间结束了，需要付款哦")
                        .setPositiveButton("付款去", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(PlayActivity.this,PayActivity.class));
                                cancel();
                            }
                        }).setNegativeButton("不看了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cancel();
                                finish();
                            }
                        });
                builder.create().show();
            }
        };
        initVideoView();
    }

    private void initVideoView(){
        //调整播放器显示宽高比
        DisplayMetrics metric = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(metric);
        int screenWidth  = metric.widthPixels;
        int screenHeight = metric.heightPixels;
        int videoHeight  = (screenHeight > screenWidth) ? screenWidth*3/4 : screenHeight*3/4;
        int videoWidth   = videoHeight * 16 / 9;
        int margin       = (screenHeight > screenWidth) ? (videoWidth - screenWidth)/2 : (videoWidth - screenHeight)/2;

        RelativeLayout.LayoutParams frameParams = (RelativeLayout.LayoutParams) videoFrame.getLayoutParams();
        frameParams.width = screenWidth + margin*2;
        frameParams.height= videoHeight;
        videoFrame.setLayoutParams(frameParams);

        RelativeLayout.LayoutParams videoParams = (RelativeLayout.LayoutParams) mVideoView.getLayoutParams();
        videoParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        videoParams.height= videoHeight;
        mVideoView.setLayoutParams(videoParams);
        mVideoView.setTranslationX(0);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        url=intent.getStringExtra("url");
    }

    @OnClick({R.id.bt_play,R.id.bt_stop,R.id.bt_photo,R.id.bt_allscreen,R.id.exit_full})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.bt_play:
                LogUtil.e("播放=>"+url);
                countDownTimer.start();
                mVideoView.playLyyRTMPVideo(url);
                break;
            case R.id.bt_stop:
                LogUtil.e("停止");
                mVideoView.stopAudio();
//                countDownTimer.cancel();
                break;
            case R.id.bt_photo:
                LogUtil.e("快照");
                Bitmap bitmap=mVideoView.takePicture();

                break;
            case R.id.bt_allscreen:
                DisplayMetrics metric = new DisplayMetrics();
                getWindow().getWindowManager().getDefaultDisplay().getMetrics(metric);

                int screenWidth  = metric.widthPixels;
                int screenHeight = metric.heightPixels;

                RelativeLayout.LayoutParams frameParams = (RelativeLayout.LayoutParams) videoFrame.getLayoutParams();
                frameParams.width = screenWidth;
                frameParams.height= screenHeight;
                videoFrame.setLayoutParams(frameParams);

                RelativeLayout.LayoutParams videoParams = (RelativeLayout.LayoutParams) mVideoView.getLayoutParams();
                videoParams.width = screenWidth;
                videoParams.height= screenHeight;
                mVideoView.setLayoutParams(videoParams);
                mVideoView.setTranslationX(90);
                mVideoView.setSystemUiVisibility(View.GONE);
                layoutController.setVisibility(View.GONE);
                break;
            case R.id.exit_full:
                initVideoView();
                layoutController.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=countDownTimer)
            countDownTimer.cancel();
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        LogUtil.e("onPrepared");
        countDownTimer.start();
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        LogUtil.e("onCompletion");
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
        LogUtil.e("onBufferingUpdate");
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        LogUtil.e("onError");
        return false;
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {

    }
}
