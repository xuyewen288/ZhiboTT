package com.xunye.zhibott.acitvity;

import android.Manifest;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.iermu.opensdk.ErmuOpenSDK;
import com.iermu.opensdk.OSLog;
import com.iermu.opensdk.api.ApiOkClient;
import com.iermu.opensdk.api.model.ConnectType;
import com.xunye.zhibott.MyApplication;
import com.xunye.zhibott.R;
import com.xunye.zhibott.helper.LogUtil;
import com.xunye.zhibott.helper.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import pub.devrel.easypermissions.EasyPermissions;
import tv.danmaku.ijk.media.widget.TopvdnApi;

public class WelcomeActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private MyHandler mHandler;
//    PreferenceUtil preferenceUtil;
    private TextView mTvSkip;
    int second=3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initView();
        getEasyPermission();

//        preferenceUtil=new PreferenceUtil(this);
//        String KEY_UID=preferenceUtil.getString(PreferenceUtil.KEY_UID);
//        String KEY_ACCESSTOKEN=preferenceUtil.getString(PreferenceUtil.KEY_ACCESSTOKEN);
//        Log.e("xyw", "KEY_UID====>"+KEY_UID);
//        Log.e("xyw", "KEY_ACCESSTOKEN====>"+KEY_ACCESSTOKEN);
//        ErmuOpenSDK.newInstance()
//                .init(getApplication())
//                .configLogLevel(ErmuOpenSDK.LogLevel.FULL)
//                .configEndpoint("https://api.iermu.com")
//                .configToken(KEY_ACCESSTOKEN,KEY_UID);
        //getAccessToken();
//        getLiveToken();
    }

    private void initView() {
        mTvSkip=findViewById(R.id.skip);
        mTvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mHandler)
                    mHandler.removeMessages(1);

                goToLoginActivity();
            }
        });

        mHandler=new MyHandler(this);
    }

    private void getLiveToken(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ApiOkClient okClient2 = new ApiOkClient(MyApplication.serverLiveUrl);
                ApiOkClient.Method method2 = ApiOkClient.Method.GET;
                String relativeUrl2  = "/oauth2/token";
                String res2 = null;
                try {
                    res2 = okClient2.execute(method2, relativeUrl2, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i("xyw","token==>"+res2);
                try {
                    JSONObject jsonObject=new JSONObject(res2);
                    String access_token=jsonObject.getString("access_token");
                    String uid=jsonObject.getString("uid");
                    String init=jsonObject.getString("init");
                    String user_token=jsonObject.getString("user_token");

//                    preferenceUtil.setPreference(PreferenceUtil.KEY_UID,uid);
//                    preferenceUtil.setPreference(PreferenceUtil.KEY_ACCESSTOKEN,access_token);
                    ErmuOpenSDK.newInstance()
                            .init(getApplication())
                            .configEndpoint("https://api.iermu.com")
                            .configToken(access_token,uid);

//                    preferenceUtil.setPreference(PreferenceUtil.KEY_USERTOKEN_LYY,user_token);
//                    preferenceUtil.setPreference(PreferenceUtil.KEY_USERCONFIG_LYY,init);
                    TopvdnApi.start_lyy_service(user_token, init, null);

                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }).start();
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
//                    preferenceUtil.setPreference(PreferenceUtil.KEY_UID,uid2);
//                    preferenceUtil.setPreference(PreferenceUtil.KEY_ACCESSTOKEN,token);
                    ErmuOpenSDK.newInstance()
                            .init(getApplication()).configToken(token,uid2);

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
                if(res2==null ){
                    //有时候获取为空
//                    Thread.sleep(3000);
                    if(Utils.isNetworkConnected(getApplicationContext()))
                        getAccessToken();
                    return;
                }
                JSONObject jsonObject1= null;
                JSONArray jsonArray=null;
                try {
                        jsonObject1 = new JSONObject(res2);
                        jsonArray = jsonObject1.getJSONArray("connect");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(null==jsonArray){
                    if(Utils.isNetworkConnected(getApplicationContext()))
                        getAccessToken();
                }

                for (int i=0;i<jsonArray.length();i++){
                    try {
                        Log.e("xyw", "connect====>"+jsonArray.getJSONObject(i).toString());

                        JSONObject jsonObject2=jsonArray.getJSONObject(i);
                        int connect_type=jsonObject2.getInt("connect_type");
                        Log.e("xyw", "connect_type====>"+connect_type);
                        if(connect_type== ConnectType.LINYANG){
                            String user_token=jsonObject2.getString("user_token");
                            String init=jsonObject2.getString("init");
//                            Log.e("xyw", "user_token====>"+user_token);
//                            Log.e("xyw", "init====>"+init);
//                            preferenceUtil.setPreference(PreferenceUtil.KEY_USERTOKEN_LYY,user_token);
//                            preferenceUtil.setPreference(PreferenceUtil.KEY_USERCONFIG_LYY,init);
                            TopvdnApi.start_lyy_service(user_token, init, null);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }



            }
        }).start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=mHandler)
            mHandler.removeCallbacksAndMessages(null);
        mHandler=null;
    }

    private void getEasyPermission(){
        String[] perms = {Manifest.permission.CAMERA,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE
                ,Manifest.permission.RECORD_AUDIO,Manifest.permission.RECEIVE_SMS,Manifest.permission.CALL_PHONE
                ,Manifest.permission.READ_CONTACTS};
        if (EasyPermissions.hasPermissions(this, perms)) {
            //todo something
            mHandler.sendEmptyMessage(1);//开始读秒
            LogUtil.i("testEasyPermission() : this camera permission is granted");
        } else {
            LogUtil.i("testEasyPermission() : this camera premission is denied , " +
                    "ready to request this permission");
            EasyPermissions.requestPermissions(this, "需要打开权限！！！",
                    100, perms);
        }

//自定义询问框内容部分，可试试看
//        EasyPermissions.requestPermissions(
//                new PermissionRequest.Builder(this, RC_CAMERA_AND_LOCATION, perms)
//                        .setRationale(R.string.camera_and_location_rationale)
//                        .setPositiveButtonText(R.string.rationale_ask_ok)
//                        .setNegativeButtonText(R.string.rationale_ask_cancel)
//                        .setTheme(R.style.my_fancy_style)
//                        .build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        //省略switch requestCode
        LogUtil.i("EasyPermission CallBack onPermissionsGranted() : "+perms.get(0)+
                " request granted , to do something...");
        //todo somthing

        mHandler.sendEmptyMessage(1);//开始读秒

    }

    private void goToLoginActivity() {
        Platform wechat= ShareSDK.getPlatform(Wechat.NAME);
        wechat.removeAccount(true);
        if(wechat.isAuthValid()){
            //判断是否已经存在授权状态，可以根据自己的登录逻辑设置
            Toast.makeText(WelcomeActivity.this, "已经授权过了", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(WelcomeActivity.this,ViewActivity.class));
        }else {
            startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
//                    startActivity(new Intent(WelcomeActivity.this,ViewActivity.class));
        }
        finish();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        //省略switch requestCode
        //无权限，且被选择"不再提醒"：提醒客户到APP应用设置中打开权限
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            LogUtil.e("EasyPermission CallBack onPermissionsDenied() : this " + perms.get(0) + " is denied " +
                    "and never ask again");
//            ToastUtil.showShort(this, "拒绝权限，不再弹出询问框，请前往APP应用设置中打开此权限");
            //todo nothing
        }

        //无权限，只是单纯被拒绝
        else {
            LogUtil.e("EasyPermission CallBack onPermissionsDenied() : " + perms.get(0) + "request denied");
//            ToastUtil.showShort(this, "拒绝权限，等待下次询问哦");
            //todo request permission again
        }


    }

   private static class MyHandler extends Handler{
       WeakReference<WelcomeActivity> mWeakReference;
       public MyHandler(WelcomeActivity activity)
       {
           mWeakReference=new WeakReference<WelcomeActivity>(activity);
       }
       @Override
       public void handleMessage(Message msg)
       {
           WelcomeActivity activity=mWeakReference.get();
           if(activity!=null)
           {
             if(msg.what==1){
                activity.mTvSkip.setText("跳过("+activity.second+")");
                activity.second--;
                if(activity.second==0){
                    activity.goToLoginActivity();
                }else {
                    sendEmptyMessageDelayed(1,1000);
                }
             }
           }
       }
   }
}
