package com.xunye.zhibott.acitvity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.iermu.opensdk.ErmuOpenSDK;
import com.iermu.opensdk.api.response.LiveMediaResponse;
import com.xunye.zhibott.R;
import com.xunye.zhibott.api.ServerApi;

import tv.danmaku.ijk.media.widget.VideoView;

public class PlayActivity extends AppCompatActivity {

    VideoView mVideoView;
    LiveMediaResponse response;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        url=getIntent().getStringExtra("url");
        mVideoView=findViewById(R.id.videoView);

        findViewById(R.id.bt_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("xyw","bt play");
                mVideoView.playLyyRTMPVideo(url);
            }
        });
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                response= ServerApi.apiLivePlay("137898420843", ErmuOpenSDK.newInstance().getAccessToken(),"","");
//                Log.e("xyw","getLiveMedia isPowerOn="+response.getLiveMedia().isPowerOn());
//                Log.e("xyw","getLiveMedia getPlayUrl="+response.getLiveMedia().getPlayUrl());
//                Log.e("xyw","getLiveMedia isRtmpLive="+response.getLiveMedia().isRtmpLive());
//                Log.e("xyw","getLiveMedia isConnected="+response.getLiveMedia().isConnected());
//                Log.e("xyw","getLiveMedia isConnected="+response.getLiveMedia().isLiveOn());
//                Log.e("xyw","getLiveMedia isConnected="+response.getLiveMedia().isOffline());
//                Log.e("xyw","getLiveMedia isConnected="+response.getLiveMedia().isOffLive());
////                mVideoView.playLyyRTMPVideo(response.getLiveMedia().getPlayUrl());
//            }
//        }).start();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        url=intent.getStringExtra("url");


    }
}
