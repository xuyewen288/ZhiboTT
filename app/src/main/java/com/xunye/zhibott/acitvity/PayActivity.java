package com.xunye.zhibott.acitvity;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.iermu.opensdk.api.ApiOkClient;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.JumpToOfflinePay;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xunye.zhibott.MyApplication;
import com.xunye.zhibott.R;
import com.xunye.zhibott.helper.Utils;
import com.xyw.util.alipay.AlipayHelp;
import com.xyw.util.alipay.AlipayPayListener;
import com.xyw.util.helper.LogUtil;
import com.xyw.util.wxapi.WXHelp;
import com.xyw.util.wxapi.WXPayListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
//import cn.smssdk.EventHandler;
//import cn.smssdk.SMSSDK;
//import cn.smssdk.gui.RegisterPage;
//import cn.smssdk.gui.util.Const;

public class PayActivity extends Activity implements WXPayListener, AlipayPayListener {

//    private IWXAPI msgApi;
    private WXHelp wxHelp;
    private MyHandler myHandler;

    private AlipayHelp alipayHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        myHandler=new MyHandler(this);
        wxHelp=WXHelp.getInstance(this, "wxe26cf976f3973f93", "97210d330e80e19f792bd441648a2424");
        wxHelp.addPayListener(this);
//        msgApi = WXAPIFactory.createWXAPI(this, "wxe26cf976f3973f93", false);
//         将该app注册到微信
//        msgApi.registerApp("wxe26cf976f3973f93");

        findViewById(R.id.bt_zfb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mobPay();
                zhifubaoPay();
            }
        });

        findViewById(R.id.bt_wx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                wxPay();
                try {
                    weixinPay();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

//        sendCode(this);
//        registerUser("86","13631505030");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wxHelp.removePayListener(this);
    }

    private void zhifubaoPay() {
        alipayHelp=new AlipayHelp();
        alipayHelp.setAlipayPayListener(this);
        alipayHelp.pay(MyApplication.serverSystemUrl+"/pay/zhifubao",PayActivity.this);

    }

    private void weixinPay() throws JSONException {
        wxHelp.pay(MyApplication.serverSystemUrl+"/pay/weixin");
    }

    @Override
    public void paySuccess() {
        LogUtil.e("支付成功");
        LogUtil.e("支付成功"+Thread.currentThread());
    }

    @Override
    public void payFail(int errCode) {
        LogUtil.e("支付失败："+errCode);
    }

    @Override
    public void onResponse(Map<String, String> result) {
        for (Map.Entry<String, String> entry : result.entrySet()) {
                        //Map.entry<String,String> 映射项（键-值对）  有几个方法：用上面的名字entry
                         //entry.getKey() ;entry.getValue(); entry.setValue();
                        //map.entrySet()  返回此映射中包含的映射关系的 Set视图。
                        LogUtil.e("key= " + entry.getKey() + " and value= "
                                        + entry.getValue());
                    }
    }

    // 提交用户信息
//    private void registerUser(String country, String phone) {
//        Random rnd = new Random();
//        int id = Math.abs(rnd.nextInt());
//        String uid = String.valueOf(id);
//        String nickName = "SmsSDK_User_" + uid;
//        String avatar = Const.AVATOR_ARR[id % Const.AVATOR_ARR.length];
//        SMSSDK.submitUserInfo(uid, nickName, avatar, country, phone);
//    }

//    public void sendCode(Context context) {
//        RegisterPage page = new RegisterPage();
//        //如果使用我们的ui，没有申请模板编号的情况下需传null
//        page.setTempCode(null);
//        Log.e("xyw","  SMSSDK.getVersion()==>"+ SMSSDK.getVersion() );
//        page.setRegisterCallback(new EventHandler() {
//            public void afterEvent(int event, int result, Object data) {
//                if (result == SMSSDK.RESULT_COMPLETE) {
//                    // 处理成功的结果
//                    HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
//                    String country = (String) phoneMap.get("country"); // 国家代码，如“86”
//                    String phone = (String) phoneMap.get("phone"); // 手机号码，如“13800138000”
//                    // TODO 利用国家代码和手机号码进行后续的操作
//                    Log.e("xyw"," country==>"+country +"--phone==>"+phone );
//                    registerUser(country,phone);
//                } else{
//                    // TODO 处理错误的结果
//                }
//            }
//        });
//        page.show(context);
//    }

    private class MyHandler extends Handler {
        private final WeakReference<PayActivity> weakReference;

        public MyHandler(PayActivity payActivity){
            weakReference=new WeakReference<>(payActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            PayActivity activity=weakReference.get();
            if(null==activity)
                return;
            switch (msg.what){
                case 0:
                    break;
                case 1:
                    break;
            }
        }
    }
}
