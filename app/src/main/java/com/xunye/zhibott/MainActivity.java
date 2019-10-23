package com.xunye.zhibott;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.iermu.opensdk.ErmuOpenSDK;
import com.iermu.opensdk.OSLog;
import com.iermu.opensdk.api.ApiOkClient;
import com.iermu.opensdk.api.model.ConnectType;
import com.iermu.opensdk.api.response.CamMetaResponse;
import com.iermu.opensdk.api.response.RegisterDevResponse;
import com.iermu.opensdk.setup.ISetupDevModule;
import com.iermu.opensdk.setup.OnApiClientInterceptor;
import com.iermu.opensdk.setup.OnSetupDevListener;
import com.iermu.opensdk.setup.conn.SetupStatus;
import com.iermu.opensdk.setup.model.CamDev;
import com.iermu.opensdk.setup.model.CamDevConf;
import com.iermu.opensdk.setup.model.ScanStatus;
import com.tencent.rtmp.TXLiveBase;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.xunye.zhibott.acitvity.CameraActivity;
import com.xunye.zhibott.acitvity.CmsSetupActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tv.danmaku.ijk.media.widget.TopvdnApi;

public class MainActivity extends AppCompatActivity implements OnApiClientInterceptor {


    TXLivePushConfig mLivePushConfig;

    TXLivePusher mLivePusher;

    EditText wifiName;
    EditText wifipassword;

    ISetupDevModule devModule;
    // Used to load the 'native-lib' library on application startup.
//    static {
//        System.loadLibrary("native-lib");
//    }
//    public native String stringFromJNI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        String sdkver = TXLiveBase.getSDKVersionStr();
        Log.d("liteavsdk", "liteav sdk version is : " + sdkver);
        TXLiveBase.setConsoleEnabled(true);
        TXLiveBase.setLogLevel(TXLiveConstants.LOG_LEVEL_DEBUG);


        findViewById(R.id.bt_txzb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTXZBSDK();
            }
        });

        findViewById(R.id.bt_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mLivePusher)
                    mLivePusher.switchCamera();
            }
        });

        findViewById(R.id.bt_scan_wifi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanWifi();
            }
        });

        wifiName=findViewById(R.id.et_wifi_name);
        wifipassword=findViewById(R.id.et_wifi_password);

        findViewById(R.id.bt_scan_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiManager wifiMgr = (WifiManager) MainActivity.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                int wifiState = wifiMgr.getWifiState();
                WifiInfo info = wifiMgr.getConnectionInfo();
                String wifiId = info != null ? info.getSSID() : null;
                wifiId=wifiId.substring(1,wifiId.length()-1);
                Log.e("xyw","wifiid==>"+wifiId);
                List<ScanResult> list = wifiMgr.getScanResults();
                Log.i("xyw", "list.size()==>"+list.size());
                String capabilities="";
                String SSID="";
                for (ScanResult scResult : list) {
                    Log.i("xyw","scResult=" + scResult.SSID);
                    if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(wifiId)) {
                        capabilities = scResult.capabilities;
                        SSID=scResult.SSID;
                        Log.i("xyw","capabilities=" + capabilities);

                        if (!TextUtils.isEmpty(capabilities)) {

                            if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                                Log.i("xyw", "wpa");

                            } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                                Log.i("xyw", "wep");
                            } else {
                                Log.i("xyw", "no");
                            }
                        }
                    }
                }
                Log.i("xyw", "SSID==>"+SSID);
//                CamDevConf conf = CamDevConf.buildConf(wifiName.getText().toString());
                CamDevConf conf = CamDevConf.buildConf(SSID);
                conf.setWiFiCapabilities(capabilities);
                conf.setWiFiAccount(wifiId);
//                conf.setWiFiPwd(wifipassword.getText().toString());
                conf.setWiFiPwd("Xu***888");
//                scanCamera(conf);
                startActivity(new Intent(MainActivity.this,CmsSetupActivity.class));
            }
        });

        initAiErMu();
    }

    private void initAiErMu(){
        ErmuOpenSDK.newInstance()
                .init(getApplication())
                .configLogLevel(ErmuOpenSDK.LogLevel.FULL)
                .configEndpoint("https://api.iermu.com")
                .configBaiduToken(BAIDU_TOKEN, BAIDU_UID)
                .configToken(IERMU_TOKEN,IERMU_UID);

        getAccessToken();

    }

    private void addListener(){
        devModule=ErmuOpenSDK.newInstance().getSetupDevModule();
        devModule.addSetupDevListener(new OnSetupDevListener() {

            @Override
            public void onScanWiFi(ScanStatus scanStatus) {
                super.onScanWiFi(scanStatus);

                WifiManager wifiMgr = (WifiManager) MainActivity.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                int wifiState = wifiMgr.getWifiState();
                WifiInfo info = wifiMgr.getConnectionInfo();
                String wifiId = info != null ? info.getSSID() : null;
                wifiId=wifiId.substring(1,wifiId.length()-1);
                List<ScanResult> results = ErmuOpenSDK.newInstance().getSetupDevModule().getScanWiFi();
                Log.e("xyw","onscanwifi=="+results.size());
                Log.e("xyw","wifiId=="+wifiId);
                Iterator<ScanResult> it=results.iterator();
                while (it.hasNext()){
                    ScanResult result=it.next();
                    if(result.SSID.equals(wifiId)){
                        CamDevConf conf = CamDevConf.buildConf(result.SSID);

                        conf.setWiFiCapabilities(result.capabilities);
//                        conf.setWiFiAccount(result.SSID);
//                conf.setWiFiPwd(wifipassword.getText().toString());
                        conf.setWiFiPwd("00000009");

                        Log.e("xyw","-------result.SSID------"+result.SSID);
                        Log.e("xyw","-------result.capabilities-----"+result.capabilities);
//                        scanCamera(conf);
                    }
                }
            }

            @Override
            public void onScanQRCode(ScanStatus scanStatus) {
                super.onScanQRCode(scanStatus);
                Log.e("xyw","onScanQRCode==>"+scanStatus);

            }

            @Override
            public void onScanDev(ScanStatus scanStatus) {
                super.onScanDev(scanStatus);
                Log.e("xyw","onScanDev==>"+scanStatus);
            }

            @Override
            public void onScanAuthDev(ScanStatus scanStatus) {
                super.onScanAuthDev(scanStatus);
                Log.e("xyw","onScanAuthDev==>"+scanStatus);
            }

            @Override
            public void onSetupStatus(SetupStatus setupStatus) {
                super.onSetupStatus(setupStatus);
                Log.e("xyw","onSetupStatus==>"+setupStatus);
                if(setupStatus==SetupStatus.SETUP_SUCCESS){
                    Toast.makeText(MainActivity.this,"连接成功",Toast.LENGTH_SHORT).show();
//                    getZBurl(devModule.getScanCamDev().get(0));
                }
            }

            @Override
            public void onUpdateProgress(int i) {
                super.onUpdateProgress(i);
            }
        });

        devModule.addApiClientInterceptor(new OnApiClientInterceptor() {
            @Override
            public RegisterDevResponse apiRegisterDevice(CamDev camDev) {

                Log.e("xyw","RegisterDevResponse camDev.getDevID()==>"+camDev.getDevID());
                return null;
            }

            @Override
            public CamMetaResponse apiCamMeta(CamDev camDev) {
                Log.e("xyw","CamMetaResponse camDev.getDevID()==>"+camDev.getDevID());
                return null;
            }
        });
        devModule.scanWiFi();
    }

    private void initTXZBSDK() {
        mLivePusher = new TXLivePusher(this);
        mLivePushConfig = new TXLivePushConfig();
        mLivePusher.setConfig(mLivePushConfig);
        String rtmpUrl = "rtmp://30507.livepush.myqcloud.com/live/30507_bfc5843578?bizid=30507&txSecret=372570cea7c2303fb120a4ff949a2343&txTime=5B841FFF";
        mLivePusher.startPusher(rtmpUrl);

        TXCloudVideoView mCaptureView = (TXCloudVideoView) findViewById(R.id.video_view);
        mLivePusher.startCameraPreview(mCaptureView);

    }
    public static final String APPKEY_DEMO = "yHa0LoYzL4ePcNxTveY6";
    private static final String BAIDU_UID = "";
    private static final String BAIDU_TOKEN = "";
    private static final String IERMU_UID = "";
    private static final String IERMU_TOKEN = "";


    private void scanWifi(){
    }

    private void getAccessToken(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String token=null;
                try {
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("grant_type", "client_credentials");
                    params.put("client_id", "E0xEFPQx1XdMEgTMdsu7");
                    params.put("client_secret", "3LGvZMPG621aYECrnZgOxCJhrDw889kOtAOoE0xD");
//                    params.put("scope", "basic");
                    ApiOkClient okClient = new ApiOkClient(ErmuOpenSDK.newInstance().getEndpoint());
                    ApiOkClient.Method method = ApiOkClient.Method.POST;
                    String relativeUrl  = "/oauth2/token";
                    String res = okClient.execute(method, relativeUrl, params);
                    Log.e("xyw", "res====>"+res);
                    JSONObject jsonObject=new JSONObject(res);
                    token=jsonObject.getString("access_token");
                    String uid2=jsonObject.getString("uid");
                    Log.e("xyw", "token====>"+token);
                    Log.e("xyw", "uid2====>"+uid2);
                    ErmuOpenSDK.newInstance()
                            .init(getApplication()).configToken(token,uid2);
                    addListener();

                } catch (Exception e) {
                    OSLog.e("registerDevice", e);
                }

                    Map<String, Object> params2 = new HashMap<String, Object>();
                    params2.put("method", "info");
                    params2.put("access_token", token);
                    params2.put("connect", 1);
                    ApiOkClient okClient2 = new ApiOkClient(ErmuOpenSDK.newInstance().getEndpoint());
                    ApiOkClient.Method method2 = ApiOkClient.Method.POST;
                    String relativeUrl2  = "/v2/passport/user";
                String res2 = null;
                try {
                    res2 = okClient2.execute(method2, relativeUrl2, params2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("xyw", "res2====>"+res2);
                JSONObject jsonObject1= null;
                JSONArray jsonArray=null;
                try {
                    jsonObject1 = new JSONObject(res2);
                    jsonArray=jsonObject1.getJSONArray("connect");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                    for (int i=0;i<jsonArray.length();i++){
                        try {
                            Log.e("xyw", "connect====>"+jsonArray.getJSONObject(i).toString());

                        JSONObject jsonObject2=jsonArray.getJSONObject(i);
                        int connect_type=jsonObject2.getInt("connect_type");
                            Log.e("xyw", "connect_type====>"+connect_type);
                        if(connect_type==ConnectType.LINYANG){
                            String user_token=jsonObject2.getString("user_token");
                            String init=jsonObject2.getString("init");
                            Log.e("xyw", "user_token====>"+user_token);
                            Log.e("xyw", "init====>"+init);
                            TopvdnApi.start_lyy_service(user_token, init, null);

                        }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }



            }
        }).start();

    }


    private void scanCamera(CamDevConf conf) {
        devModule.addApiClientInterceptor(this);
        devModule.scanCam(conf);
    }

    public String getZBurl(CamDev camDev){
        String response="";
        String devID    = camDev.getDevID();
        int connectType = camDev.getServerConnectType();
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("method", "liveplay");
            params.put("deviceid", devID);
            params.put("shareid", "901015");
            params.put("access_token", ErmuOpenSDK.newInstance().getAccessToken());
            ApiOkClient okClient = new ApiOkClient(ErmuOpenSDK.newInstance().getEndpoint());
            ApiOkClient.Method method = ApiOkClient.Method.POST;
            String relativeUrl  = "/v2/pcs/device";
            response = okClient.execute(method, relativeUrl, params);
            Log.e("xyw","ZBURL==>"+response);
        } catch (Exception e) {
            OSLog.e("registerDevice", e);
        }
        try {
            JSONObject jsonObject=new JSONObject(response);
            String url=jsonObject.getString("url");
            if(null!=url){
                Log.e("xyw","'playurl==>"+url);
//                Toast.makeText(MainActivity.this,url,Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(MainActivity.this,CameraActivity.class);
                intent.putExtra("url",url);
                startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return response;
    }

    /**
     * 注册设备接口
     * @param camDev x
     * @return x
     */
    @Override
    public RegisterDevResponse apiRegisterDevice(CamDev camDev) {
        Log.e("xyw","apiRegisterDevice==>"+camDev.getDevID());
        RegisterDevResponse response;
        String devID    = camDev.getDevID();
        int connectType = camDev.getServerConnectType();
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("method", "register");
            params.put("deviceid", devID);
            params.put("device_type", 1);
            params.put("connect_type", connectType);
            params.put("desc", "我的摄像机");
            params.put("access_token", ErmuOpenSDK.newInstance().getAccessToken());
            ApiOkClient okClient = new ApiOkClient(ErmuOpenSDK.newInstance().getEndpoint());
            ApiOkClient.Method method = ApiOkClient.Method.POST;
            String relativeUrl  = "/v2/pcs/device";
            String str = okClient.execute(method, relativeUrl, params);
            response = RegisterDevResponse.parseResponse(str);
        } catch (Exception e) {
            OSLog.e("registerDevice", e);
            response = RegisterDevResponse.parseResponseError(e);
        }
//        getZBurl(camDev);
        return response;

    }

    /**
     * 获取设备状态信息接口
     * @param camDev x
     * @return x
     */
    @Override
    public CamMetaResponse apiCamMeta(CamDev camDev) {
        Log.e("xyw","apiCamMeta==>"+camDev.getDevID());
        CamMetaResponse response;
        String devID    = camDev.getDevID();
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("method", "meta");
            params.put("deviceid", devID);
            params.put("access_token", ErmuOpenSDK.newInstance().getAccessToken());
            ApiOkClient okClient = new ApiOkClient(ErmuOpenSDK.newInstance().getEndpoint());
            ApiOkClient.Method method = ApiOkClient.Method.GET;
            String relativeUrl = "/v2/pcs/device";
            String str = okClient.execute(method, relativeUrl, params);
            response = CamMetaResponse.parseResponse(str);
        } catch (Exception e) {
            OSLog.e("apiCamMeta", e);
            response = CamMetaResponse.parseResponseError(e);
        }
        return response;
    }
}
