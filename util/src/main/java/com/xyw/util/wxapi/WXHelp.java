package com.xyw.util.wxapi;

import android.content.Context;
import android.text.TextUtils;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.ArrayList;

public class WXHelp {
    private static String APPID = "";
    private static String APPSECRET = "";

    private static WXHelp instance;
    private IWXAPI iwxapi;

    private final static ArrayList<WXLoginListener> wxLoginListeners = new ArrayList<>();

    private void Init(Context context,String APPID,String APPSECRET) {
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

    public void addListener(WXLoginListener listener) {
        if (!wxLoginListeners.contains(listener)) {
            wxLoginListeners.add(listener);
        }
    }

    public void removeListener(WXLoginListener listener) {
        if (wxLoginListeners.contains(listener)) {
            wxLoginListeners.remove(listener);
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


}
