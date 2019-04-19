package com.xyw.util.alipay;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.alipay.sdk.app.PayTask;
import com.xyw.util.helper.LogUtil;
import com.xyw.util.helper.OkHttpUtils;

import java.util.Map;

public class AlipayHelp {
    private static AlipayHelp Instance;
    private Handler mHandler;
    private AlipayPayListener alipayPayListener;
    public AlipayHelp(){
        mHandler = new Handler(Looper.getMainLooper());
    }

    public AlipayPayListener getAlipayPayListener() {
        return alipayPayListener;
    }

    public void setAlipayPayListener(AlipayPayListener alipayPayListener) {
        this.alipayPayListener = alipayPayListener;
    }

    public void pay(String url, final Activity activity){
        OkHttpUtils.get(url, new OkHttpUtils.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                final String orderInfo = response;   // 订单信息
                LogUtil.e("支付宝订单信息："+response);
                Runnable payRunnable = new Runnable() {

                    @Override
                    public void run() {
                        PayTask alipay = new PayTask(activity);
                        final Map<String,String> result = alipay.payV2(orderInfo,true);
                        LogUtil.e("支付宝支付结果："+result);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                alipayPayListener.onResponse(result);
                            }
                        });
//                        Message msg = new Message();
//                        msg.what = SDK_PAY_FLAG;
//                        msg.obj = result;
//                        mHandler.sendMessage(msg);
                    }
                };
                // 必须异步调用
                Thread payThread = new Thread(payRunnable);
                payThread.start();
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }
}
