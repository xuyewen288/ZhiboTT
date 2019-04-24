package com.xunye.zhibott.wxapi;


import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xunye.zhibott.R;
import com.xunye.zhibott.helper.LogUtil;
import com.xyw.util.wxapi.WXHelp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	private IWXAPI api;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_result);
		api = WXAPIFactory.createWXAPI(this, "wxe26cf976f3973f93");

		api.handleIntent(getIntent(), this);
		api.registerApp("wxe26cf976f3973f93");
		finish();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
		boolean b=api.registerApp("wxe26cf976f3973f93");
		LogUtil.i("WXPayEntryActivity onNewIntent=="+b);
	}

	@Override
	public void onReq(BaseReq baseReq) {
		LogUtil.i("openid="+baseReq.openId);
		LogUtil.i("arg="+baseReq.checkArgs());
		LogUtil.i("type="+baseReq.getType());
		//...
	}

	@Override
	public void onResp(BaseResp baseResp) {
		LogUtil.i("openid="+baseResp.errCode);
		if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			//...
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle("提示");
//			builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(baseResp.errCode)));
//			builder.show();
			if(baseResp.errCode==0){
				WXHelp.getInstance(this).paySuccess();
			}else {
				WXHelp.getInstance(this).payFail(baseResp.errCode);
			}
		}
	}
}