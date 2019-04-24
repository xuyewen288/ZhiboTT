package com.xunye.zhibott.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.iermu.opensdk.ErmuOpenSDK;
import com.iermu.opensdk.setup.OnSetupDevListener;
import com.iermu.opensdk.setup.conn.SetupStatus;
import com.iermu.opensdk.setup.model.CamDev;
import com.iermu.opensdk.setup.model.CamDevConf;
import com.iermu.opensdk.setup.model.ScanStatus;
import com.xunye.zhibott.R;
import com.xunye.zhibott.helper.QRImageHelper;
import com.xunye.zhibott.helper.SDKHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 生成二维码页面.
 */
public class ScanQRCodeFragment extends Fragment implements View.OnClickListener {

    static CamDevConf camdevConf;
    ImageView qrImage;
    TextView logContent;
    Bitmap qrBitmap;

    public static Fragment actionInstance(Activity activity, CamDevConf conf) {
        camdevConf = conf;
        return new ScanQRCodeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qrcode, container, false);
        qrImage = (ImageView) view.findViewById(R.id.qrimage);
        logContent = (TextView) view.findViewById(R.id.content_log);
        view.findViewById(R.id.Create_QRImage).setOnClickListener(this);
        SDKHelper.acquireScreenBrightLock(getActivity());
        ErmuOpenSDK.newInstance().getSetupDevModule().addSetupDevListener(mSetupDevListener);
        ErmuOpenSDK.newInstance().getSetupDevModule().scanQRCode(camdevConf);
        return view;
    }

    //二维码内容格式：
    //authcode\nSsid\nPasswd\nUserName\nWep
    //以\n为分割，总计5项
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.Create_QRImage:
            logContent("");
            ErmuOpenSDK.newInstance().getSetupDevModule().scanQRCode(camdevConf);
            break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(qrBitmap!=null && !qrBitmap.isRecycled()) qrBitmap.recycle();
        ErmuOpenSDK.newInstance().getSetupDevModule().removeSetupDevListener(mSetupDevListener);
        SDKHelper.releaseWakeLock();
    }
    private void logContent(String str) {
        if(logContent != null) logContent.setText(str);
    }
    private void logProgressContent(int progress) {
        String text = logContent.getText().toString();
        text = text.replaceAll("安装进度:[\\s]?[0-9]{1,3};[\\s]?", "");
        if(logContent != null) logContent.setText("安装进度: "+progress+"; "+text);
    }
    @SuppressLint("SdCardPath")
    private String getQRCodePath() {
        Log.i("xyw",""+Environment.getExternalStorageDirectory().getPath());
        Log.i("xyw",""+Environment.getDataDirectory().getPath());
        File fdir = new File(Environment.getExternalStorageDirectory().getPath()+"/sdcard/爱耳目摄像机/qrcode");
        if (!fdir.exists()) fdir.mkdirs();
        File f = new File(fdir, "qr_" + System.currentTimeMillis() + ".jpg");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return f.getPath();
    }

    private void ThreadCreateQRBitmap(final String qrContent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(getActivity()==null || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && getActivity().isDestroyed())) return;
                String qrPath         = getQRCodePath();
                DisplayMetrics metric = new DisplayMetrics();
                WindowManager wm      = getActivity().getWindowManager();
                wm.getDefaultDisplay().getMetrics(metric);
                int width = metric.widthPixels;
                boolean b = QRImageHelper.createQRImage(qrContent, null, qrPath, width, width);
                if(mHandler!=null && b) {
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("qr_path", qrPath);
                    msg.what = 0;
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                    return;
                }
                if (mHandler != null) mHandler.sendEmptyMessage(-1);
            }
        }).start();
    }
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(getActivity()==null || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && getActivity().isDestroyed())) return;
            Bundle bundle = msg.getData();
            switch (msg.what){
            case -1:
                logContent("生成二维码失败!");
                break;
            case 0://创建二维码成功
                String path = bundle.getString("qr_path");
                qrBitmap = BitmapFactory.decodeFile(path);
                qrImage.setImageBitmap(qrBitmap);
                break;
            }
        }
    };
    OnSetupDevListener mSetupDevListener = new OnSetupDevListener() {
        @Override
        public void onScanQRCode(ScanStatus status) {
            super.onScanQRCode(status);
            if(getActivity()==null || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && getActivity().isDestroyed())) return;
            switch (status) {
            case AUTH_CREATE_FAIL:
                logContent("创建二维码失败! code="+status.getErrorCode());
                break;
            case AUTH_EXPIRED:
                logContent("二维码已过期! code="+status.getErrorCode());
                break;
            case SUCCESS://创建二维码成功
                String content = status.getQRContent();
//                logContent("二维码status.getQRContent()="+status.getQRContent());
                logContent("创建二维码成功 请将二维码对准摄像头");
                ThreadCreateQRBitmap(content);
                break;
            }
        }
        @Override
        public void onScanAuthDev(ScanStatus status) {
            super.onScanAuthDev(status);
            switch (status) {
            case AUTH_CREATE_FAIL:
                logContent("创建二维码失败! code="+status.getErrorCode());
                break;
            case AUTH_EXPIRED:
                logContent("二维码已过期! code="+status.getErrorCode());
                break;
            case SUCCESS://创建二维码成功
                List<CamDev> list = ErmuOpenSDK.newInstance().getSetupDevModule().getScanCamDev();
                if(list.size()>0) {
                    CamDev item = list.get(0);//目前仅开放单个设备配置.
                    ErmuOpenSDK.newInstance().getSetupDevModule().connectCam(item);
                }
                break;
            }
        }
        @Override
        public void onSetupStatus(SetupStatus status) {
            super.onSetupStatus(status);
            switch (status){
                case SETUP_ENV_SMART_TIMEOUT:
                    break;
                case SETUP_ENV_SMART_WIFI_NOMATCH:
                    break;
                case REGISTER_ING:
                    break;
                case REGISTER_NOTPERMISSION:
                    logContent("没有该设备权限!");
                    break;
                case REGISTER_FAIL:
                    logContent("注册设备失败!");
                    break;
                case REGISTER_SUCCESS:
                case REGISTED:
                    break;
                case CONNECT_DEV_FAIL:
                case CONF_CONNECTDEV_FAIL:
                case CONNECT_WIFI_FAIL:
                    logContent("配置设备失败!");
                    break;
                case AUTH_DEV_FAIL:
                    logContent("授权设备失败!");
                    break;
                case AUTH_DEV_EXPIRED:
                    logContent("授权设备过期!");
                    break;
                case SETUP_FAIL:
                    logContent("安装设备失败!");
                    break;
                case SETUP_SUCCESS:
                    logContent("安装设备成功!");
                    break;
            }
        }
        @Override
        public void onUpdateProgress(int progress) {
            super.onUpdateProgress(progress);
            //Log.i("", "安装进度: "+String.valueOf(progress));
            logProgressContent(progress);
        }
    };
}
