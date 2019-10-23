package com.xunye.zhibott.acitvity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mob.MobSDK;
import com.xunye.zhibott.MyApplication;
import com.xunye.zhibott.R;
import com.xunye.zhibott.helper.LogUtil;
import com.xunye.zhibott.helper.PreferenceUtil;
import com.xunye.zhibott.helper.RSAEncrypt;
import com.xyw.util.wxapi.WXHelp;
import com.xyw.util.wxapi.WXLoginListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;

public class LoginActivity extends AppCompatActivity implements WXLoginListener {

    @BindView(R.id.et_username)
    EditText mEtUsername;

    @BindView(R.id.et_password)
    EditText mEtPassword;

    @BindView(R.id.et_phone)
    EditText mEtPhone;

    @BindView(R.id.et_verifycode)
    EditText mEtVerifyCode;

    @BindView(R.id.bt_verifyget)
    Button mBtVerifyget;

    @BindView(R.id.bt_login2)
    Button mBtLogin;

    @BindView(R.id.bt_login)
    Button mBtWXLogin;

    @BindView(R.id.progressLayout)
    LinearLayout progressLayout;

    EventHandler eventHandler;
    Handler mHandler;

    PreferenceUtil preferenceUtil;

    int delay=30;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initEventHandler();

//        msgApi = WXAPIFactory.createWXAPI(this, "wxe26cf976f3973f93", false);
        // 将该app注册到微信
//        msgApi.registerApp("wxe26cf976f3973f93");
//        weixinLogin();
        preferenceUtil=new PreferenceUtil(this);
        //测试数据
        mEtUsername.setText("xyw");
        mEtPassword.setText("xyw");
        mEtPhone.setText("13631505030");
    }

    private void initEventHandler() {
        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==1){
                    Toast.makeText(LoginActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                }else if(msg.what==2){
                    delay--;
                    mBtVerifyget.setText(delay+"秒");
                    if(delay>0){
                        if(mBtVerifyget.isEnabled()) {
                            mBtVerifyget.setEnabled(false);
                        }
                        sendEmptyMessageDelayed(2,1000);
                    }else {
                        removeMessages(2);
                        mBtVerifyget.setEnabled(true);
                        mBtVerifyget.setText(R.string.verifyget);
                        delay=30;
                    }


                }
            }
        };
        eventHandler=new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                super.afterEvent(event,result,data);
                // afterEvent会在子线程被调用，因此如果后续有UI相关操作，需要将数据发送到UI线程
                if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        // TODO 处理成功得到验证码的结果
                        // 请注意，此时只是完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达
                        mHandler.obtainMessage(2).sendToTarget();
                    } else {
                        // TODO 处理错误的结果
                        ((Throwable) data).printStackTrace();
                    }
                } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    if (result == SMSSDK.RESULT_COMPLETE) {
//                        mHandler.obtainMessage(1,"验证通过").sendToTarget();
                        // TODO 处理验证码验证通过的结果
                        login();
                    } else {
                        // TODO 处理错误的结果
//                        ((Throwable) data).printStackTrace();
                        mHandler.obtainMessage(1,"验证码错误").sendToTarget();;
                    }
                }
            }
        };
        // 注册一个事件回调，用于处理SMSSDK接口请求的结果
        SMSSDK.registerEventHandler(eventHandler);
    }

    private void login() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", mEtUsername.getText().toString());

        try {
//            params.put("password", URLEncoder.encode(RSAEncrypt.encrypt(mEtPassword.getText().toString(),RSAEncrypt.PublicKey),"UTF-8"));
            params.put("password", RSAEncrypt.encrypt(mEtPassword.getText().toString(),RSAEncrypt.PublicKey));
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.put("phone", mEtPhone.getText().toString());
        try {
            params.put("watchtime", preferenceUtil.getRSAString("watchtime"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.i("登陆"+params.toString());
//        String url=MyApplication.serverSystemUrl+"/login/usershop";
        String url=MyApplication.serverSystemUrl+"/login/person";
        OkHttpUtils.post().url(url).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.i("登陆错误："+e.toString()+"--id=="+id);
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    LogUtil.i("登陆结果："+response+"--id=="+id);
                    JSONObject jsonObject=new JSONObject(response);
                    int status=jsonObject.getInt("status");
                    if(status==200){
                        JSONObject jsonObject1=new JSONObject(jsonObject.getString("result"));
                        MyApplication.username=mEtUsername.getText().toString();
                        MyApplication.watchtime=jsonObject1.getLong("watchtime");
                        startActivity(new Intent(LoginActivity.this,ViewActivity.class));
                        mHandler.obtainMessage(1,"登陆成功").sendToTarget();
                        finish();
                    }else if(status==401){
                        mHandler.obtainMessage(1,"密码错误").sendToTarget();;
                    }else if(status==402){
                        mHandler.obtainMessage(1,"绑定手机号不正确").sendToTarget();;
                    }else if(status==403){
                        mHandler.obtainMessage(1,"用户名错误").sendToTarget();;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void weixinLogin() {
        findViewById(R.id.bt_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Platform wechat= ShareSDK.getPlatform(Wechat.NAME);
                wechat.setPlatformActionListener(new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                        Log.e("xyw","  onComplete==>"+platform.getDb().getUserId() );
                        Log.e("xyw","  onComplete==>"+platform.getDb().getUserIcon() );
                        Log.e("xyw","  onComplete==>"+platform.getDb().getUserName() );
                        Log.e("xyw","  onComplete==>"+platform.getDb().getPlatformNname() );
                        Log.e("xyw","  onComplete==>"+platform.getName() );
                        MobSDK.setUser(platform.getDb().getUserId(),platform.getDb().getUserName(),platform.getDb().getPlatformNname(),null);
//                        String unionid = (String) hashMap.get("unionid");
//                        Log.e("xyw","  unionid==>"+unionid );
                        startActivity(new Intent(LoginActivity.this,ViewActivity.class));
                        finish();
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {
                        Log.e("xyw","  onError==>"+platform.getId() );
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                        Log.e("xyw","  onCancel==>"+platform.getId() );
                    }
                });
//              wechat.removeAccount(true); //移除授权状态和本地缓存，下次授权会重新授权
//              wechat.SSOSetting(false); //SSO授权，传false默认是客户端授权，没有客户端授权或者不支持客户端授权会跳web授权
                if(wechat.isClientValid()){
                    //判断是否存在授权凭条的客户端，true是有客户端，false是无
                }
                Log.e("xyw","  wechat.isAuthValid()==>"+wechat.isAuthValid() );
                if(wechat.isAuthValid()){
                    //判断是否已经存在授权状态，可以根据自己的登录逻辑设置
                    Toast.makeText(LoginActivity.this, "已经授权过了", Toast.LENGTH_SHORT).show();
                    Log.e("xyw","  getName==>"+wechat.getName() );
                    Log.e("xyw","  getPlatformNname==>"+wechat.getDb().getPlatformNname() );
                    startActivity(new Intent(LoginActivity.this,ViewActivity.class));
                    finish();
                    return;
                }
                wechat.authorize();
            }
        });
    }

    @OnClick({R.id.bt_login2,R.id.bt_verifyget,R.id.bt_login})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.bt_verifyget:
                // 请求验证码，其中country表示国家代码，如“86”；phone表示手机号码，如“13800138000”
                SMSSDK.getVerificationCode("86", mEtPhone.getText().toString());
                mHandler.obtainMessage(1,"请求验证码").sendToTarget();;
                break;
            case R.id.bt_login2:
                //mHandler.obtainMessage(1,"登陆").sendToTarget();;
                // 提交验证码，其中的code表示验证码，如“1357”
//                SMSSDK.submitVerificationCode("86", mEtPhone.getText().toString(), mEtVerifyCode.getText().toString());
                if (((MyApplication)getApplication()).future.isDone()){
                    try {
                        if (((MyApplication)getApplication()).future.get().equals("success")){
                            login();
//                            SMSSDK.submitVerificationCode("86", mEtPhone.getText().toString(), mEtVerifyCode.getText().toString());
                        }else {
                            //初始化服务器不成功的时候，先连接服务器获取token，必须要初始化爱耳目SDK
//                            mHandler.obtainMessage(1,"服务器连接错误").sendToTarget();
                            ((MyApplication)getApplication()).connectServer();//重新连接服务器
                            try {
                                String res= ((MyApplication)getApplication()).future.get(3, TimeUnit.SECONDS);//设置3秒超时
                                if(res.equals("success")){
                                    login();
//                                    SMSSDK.submitVerificationCode("86", mEtPhone.getText().toString(), mEtVerifyCode.getText().toString());
                                }else {
                                    mHandler.obtainMessage(1,"服务器连接错误").sendToTarget();
                                }
                            } catch (TimeoutException e) {
                                mHandler.obtainMessage(1,"服务器连接错误").sendToTarget();
                                e.printStackTrace();
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }else {
                    mHandler.obtainMessage(1,"正在连接服务器").sendToTarget();;
                }


                break;
            case R.id.bt_login:
                progressLayout.setVisibility(View.VISIBLE);
                wxLogin();
//                weixinLogin();
                break;
        }

    }

    private void wxLogin() {
        WXHelp wxHelp= WXHelp.getInstance(this,"wxe26cf976f3973f93","97210d330e80e19f792bd441648a2424");
//        WXHelp WXHelp=WXHelp.getInstance(this);
        wxHelp.addLoginListener(this);
        wxHelp.login();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        SMSSDK.unregisterEventHandler(eventHandler);
        if(null!=WXHelp.getInstance(this))
            WXHelp.getInstance(this).removeLoginListener(this);
    }

    @Override
    public void success(String openid, String nickname, String headimgurl, String unionid) {
        LogUtil.e("openid="+openid+"\n nickname="+nickname+"\n headimgurl="+headimgurl+"\n unionid="+unionid);
        preferenceUtil.setPreference("openid",openid);
        preferenceUtil.setPreference("nickname",nickname);
        preferenceUtil.setPreference("headimgurl",headimgurl);
        preferenceUtil.setPreference("unionid",unionid);
        startActivity(new Intent(LoginActivity.this,ViewActivity.class));
        finish();
    }

    @Override
    public void userCancle() {
        progressLayout.setVisibility(View.GONE);
    }

    @Override
    public void authDenied() {
        progressLayout.setVisibility(View.GONE);
    }

    @Override
    public void unknown() {
        progressLayout.setVisibility(View.GONE);
    }
}
