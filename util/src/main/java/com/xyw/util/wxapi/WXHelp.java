package com.xyw.util.wxapi;

import android.content.Context;
import android.text.TextUtils;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xyw.util.helper.LogUtil;
import com.xyw.util.helper.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WXHelp {
    private static String APPID = "";
    private static String APPSECRET = "";

    private static WXHelp instance;
    private IWXAPI iwxapi;

    private final static ArrayList<WXLoginListener> wxLoginListeners = new ArrayList<>();
    private final static ArrayList<WXPayListener> wxPayListeners = new ArrayList<>();

    private void Init(Context context, String APPID, String APPSECRET) {
        this.APPID=APPID;
        this.APPSECRET=APPSECRET;
        iwxapi = WXAPIFactory.createWXAPI(context.getApplicationContext(), APPID, false);
        iwxapi.registerApp(APPID);
    }

    private WXHelp(Context context, String APPID, String APPSECRET) {
        Init(context,APPID,APPSECRET);
    }

    public synchronized static WXHelp getInstance(Context context, String APPID, String APPSECRET) {
        if (instance == null) {
            instance = new WXHelp(context,APPID,APPSECRET);
        }
        return instance;
    }

    public synchronized static WXHelp getInstance(Context context) throws NullPointerException {
        if (instance == null ) {
            if(TextUtils.isEmpty(APPID) || TextUtils.isEmpty(APPSECRET)){
                throw new NullPointerException("APPID or APPSECRET null");
            }
            instance = new WXHelp(context,APPID,APPSECRET);
        }
        return instance;
    }

    public void addLoginListener(WXLoginListener listener) {
        if (!wxLoginListeners.contains(listener)) {
            wxLoginListeners.add(listener);
        }
    }

    public void removeLoginListener(WXLoginListener listener) {
        if (wxLoginListeners.contains(listener)) {
            wxLoginListeners.remove(listener);
        }
    }

    public void addPayListener(WXPayListener listener) {
        if (!wxPayListeners.contains(listener)) {
            wxPayListeners.add(listener);
        }
    }

    public void removePayListener(WXPayListener listener) {
        if (wxPayListeners.contains(listener)) {
            wxPayListeners.remove(listener);
        }
    }

    public void success(String openid, String nickname, String headimgurl, String unionid) {
        for (WXLoginListener listener : wxLoginListeners) {
            listener.success(openid,nickname,headimgurl,unionid);
        }
    }

    public void userCancle() {
        for (WXLoginListener listener : wxLoginListeners) {
            listener.userCancle();
        }
    }

    public void authDenied() {
        for (WXLoginListener listener : wxLoginListeners) {
            listener.authDenied();
        }
    }

    public void unknown() {
        for (WXLoginListener listener : wxLoginListeners) {
            listener.unknown();
        }
    }

    public void paySuccess(){
        for (WXPayListener listener : wxPayListeners) {
            listener.paySuccess();
        }
    }

    public void payFail(int errCode){
        for (WXPayListener listener : wxPayListeners) {
            listener.payFail(errCode);
        }
    }

    public String getAPPID() {
        return APPID;
    }

    public void setAPPID(String APPID) {
        this.APPID = APPID;
    }

    public String getAPPSECRET() {
        return APPSECRET;
    }

    public void setAPPSECRET(String APPSECRET) {
        this.APPSECRET = APPSECRET;
    }

    public IWXAPI getIwxapi() {
        return iwxapi;
    }

    public void setIwxapi(IWXAPI iwxapi) {
        this.iwxapi = iwxapi;
    }

    public void login() {
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        iwxapi.sendReq(req);
    }

    public int getWXAppSupportAPI(){
        return iwxapi.getWXAppSupportAPI();
    }

    /**
     * 统一下单接口  url是自己服务器的接口，返回的参数，一定要固定 一定要固定 一定要固定
     * @param url
     */
    public void pay(String url){
        OkHttpUtils.get(url, new OkHttpUtils.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                JSONObject json= null;
                try {
                    json=new JSONObject(response);
                    LogUtil.i("微信订单==>"+json.toString());
                    PayReq req = new PayReq();
                    //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
                    req.appId			= json.getString("appid");
                    req.partnerId		= json.getString("partnerid");
                    req.prepayId		= json.getString("prepayid");
                    req.nonceStr		= json.getString("noncestr");
                    req.timeStamp		= json.getString("timestamp");
                    req.packageValue	= json.getString("package");
                    req.sign			= json.getString("sign");
//                    req.openId=json.getString("appid");
//                    Toast.makeText(PayActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
                    // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                    boolean b=iwxapi.sendReq(req);
//                    boolean b2=msgApi.sendReq(new JumpToOfflinePay.Req()); //离线扫码
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

}
