package com.xyw.util.wxapi;

public interface WXLoginListener {
    void success(String openid, String nickname, String headimgurl, String unionid);

    void userCancle();

    void authDenied();

    void unknown();
}
