package com.xunye.zhibott.acitvity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.google.zxing.activity.CaptureActivity;
import com.xunye.zhibott.MyApplication;
import com.xunye.zhibott.R;
import com.xunye.zhibott.helper.QRImageHelper2;
import com.xyw.util.alipay.AlipayHelp;
import com.xyw.util.alipay.AlipayPayListener;
import com.xyw.util.helper.LogUtil;
import com.xyw.util.helper.OkHttpUtils;
import com.xyw.util.wxapi.WXHelp;
import com.xyw.util.wxapi.WXPayListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PayActivity extends Activity implements WXPayListener, AlipayPayListener {

//    private IWXAPI msgApi;
    private WXHelp wxHelp;
    private MyHandler myHandler;

    private AlipayHelp alipayHelp;

    ImageView iv_saoma;

    //打开扫描界面请求码
    private int REQUEST_CODE = 0x01;
    //扫描成功返回码
    private int RESULT_OK = 0xA1;

    private TextView tv_res;

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
        iv_saoma= findViewById(R.id.iv_qrcode);
        findViewById(R.id.bt_shoukuanma).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpUtils.get(MyApplication.serverSystemUrl + "/pay/weixin/saoma", new OkHttpUtils.ResultCallback<String>() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e("saoma=="+response);
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            String code_url=jsonObject.getString("code_url");
                            Bitmap bitmap=QRImageHelper2.createBitmap(code_url,400,400);
                            iv_saoma.setImageBitmap(bitmap);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
            }
        });

        findViewById(R.id.bt_wxsaoma).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PayActivity.this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        tv_res=findViewById(R.id.tv_res);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描结果回调
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN);
            //将扫描出的信息显示出来
            tv_res.setText(scanResult);
            saoMaShouQian(scanResult);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wxHelp.removePayListener(this);
    }

    private void saoMaShouQian(String scanResult){
        List<OkHttpUtils.Param> params=new ArrayList<>();
        params.add(new OkHttpUtils.Param("auth_code",scanResult));
        OkHttpUtils.post(MyApplication.serverSystemUrl + "/pay/weixin/erweima", new OkHttpUtils.ResultCallback<String>() {
            @Override
            public void onSuccess(String  response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String result_code=jsonObject.getString("result_code");
                    String return_code=jsonObject.getString("return_code");
                    if(result_code.equals("SUCCESS") && return_code.equals("SUCCESS"))
                        Toast.makeText(PayActivity.this,"扫码成功",Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Exception e) {

            }
        },params);
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
