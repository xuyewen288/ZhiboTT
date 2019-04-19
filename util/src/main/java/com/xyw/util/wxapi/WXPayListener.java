package com.xyw.util.wxapi;

public interface WXPayListener {
        void paySuccess();
        void payFail(int errCode);
}
