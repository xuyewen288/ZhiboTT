package com.xunye.zhibott.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.ShowMessageFromWX;
import com.tencent.mm.opensdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xunye.zhibott.R;
import com.xunye.zhibott.acitvity.PayActivity;
import com.xunye.zhibott.helper.LogUtil;
import com.xyw.util.wxapi.WXHelp;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	
	private Button gotoBtn, regBtn, launchBtn, checkBtn, payBtn, favButton;
	
	// IWXAPI �ǵ�����app��΢��ͨ�ŵ�openapi�ӿ�
    private IWXAPI api;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry);
        
        // ͨ��WXAPIFactory��������ȡIWXAPI��ʵ��
    	api = WXAPIFactory.createWXAPI(this, "wxe26cf976f3973f93", false);

    	regBtn = (Button) findViewById(R.id.reg_btn);
    	regBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// ����appע�ᵽ΢��
			    api.registerApp("wxe26cf976f3973f93");
			}
		});
    	
        gotoBtn = (Button) findViewById(R.id.goto_send_btn);
        gotoBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//		        startActivity(new Intent(WXEntryActivity.this, SendToWXActivity.class));
//		        finish();
			}
		});
        
        launchBtn = (Button) findViewById(R.id.launch_wx_btn);
        launchBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(WXEntryActivity.this, "launch result = " + api.openWXApp(), Toast.LENGTH_LONG).show();
			}
		});
        
        checkBtn = (Button) findViewById(R.id.check_timeline_supported_btn);
        checkBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int wxSdkVersion = api.getWXAppSupportAPI();
				if (wxSdkVersion >= TIMELINE_SUPPORTED_VERSION) {
					Toast.makeText(WXEntryActivity.this, "wxSdkVersion = " + Integer.toHexString(wxSdkVersion) + "\ntimeline supported", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(WXEntryActivity.this, "wxSdkVersion = " + Integer.toHexString(wxSdkVersion) + "\ntimeline not supported", Toast.LENGTH_LONG).show();
				}
			}
		});
        
        payBtn = (Button) findViewById(R.id.goto_pay_btn);
        payBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(WXEntryActivity.this, PayActivity.class));
		        finish();
			}
		});
        
        favButton = (Button) findViewById(R.id.goto_fav_btn);
        favButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				startActivity(new Intent(WXEntryActivity.this, AddFavoriteToWXActivity.class));
//				finish();
			}
		});
        
        // debug
       
        // debug end
        
        api.handleIntent(getIntent(), this);
		LogUtil.e("finish");
		finish();
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	// ΢�ŷ������󵽵�����Ӧ��ʱ����ص����÷���
	@Override
	public void onReq(BaseReq req) {
		Toast.makeText(this, "openid = " + req.openId, Toast.LENGTH_SHORT).show();
		
		switch (req.getType()) {
		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
			goToGetMsg();		
			break;
		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
			goToShowMsg((ShowMessageFromWX.Req) req);
			break;
		case ConstantsAPI.COMMAND_LAUNCH_BY_WX:
//			Toast.makeText(this, R.string.launch_from_wx, Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}

	// ������Ӧ�÷��͵�΢�ŵ�����������Ӧ�������ص����÷���
	@Override
	public void onResp(BaseResp resp) {
		Toast.makeText(this, "openid = " + resp.openId, Toast.LENGTH_SHORT).show();
		
		if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
			Toast.makeText(this, "code = " + ((SendAuth.Resp) resp).code, Toast.LENGTH_SHORT).show();
			LogUtil.i("code==>"+((SendAuth.Resp) resp).code);
		}
		
		int result = 0;
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			result = R.string.errcode_success;
			OkHttpUtils.get()
					.url("https://api.weixin.qq.com/sns/oauth2/access_token?appid=wxe26cf976f3973f93&secret=97210d330e80e19f792bd441648a2424&grant_type=authorization_code&code="+((SendAuth.Resp) resp).code)
					.build().execute(new StringCallback() {
				@Override
				public void onError(Call call, Exception e, int id) {

				}

				@Override
				public void onResponse(String response, int id) {
					LogUtil.i("response==>"+response);
					try {
						JSONObject jsonObject=new JSONObject(response);
						String access_token=jsonObject.getString("access_token");
						String openid=jsonObject.getString("openid");
						OkHttpUtils.get().url("https://api.weixin.qq.com/sns/userinfo?access_token="+access_token+"&openid="+openid).build().execute(new StringCallback() {
							@Override
							public void onError(Call call, Exception e, int id) {

							}

							@Override
							public void onResponse(String response, int id) {
								LogUtil.i("response 2==>"+response);
								try {
								JSONObject jsonObject=new JSONObject(response);
								String openid=jsonObject.getString("openid");
								String nickname=jsonObject.getString("nickname");
								String headimgurl=jsonObject.getString("headimgurl");
								String unionid=jsonObject.getString("unionid");
								WXHelp.getInstance(WXEntryActivity.this).success(openid,nickname,headimgurl,unionid);

								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
//			result = R.string.errcode_cancel;
			WXHelp.getInstance(this).userCancle();
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
//			result = R.string.errcode_deny;
			WXHelp.getInstance(this).authDenied();
			break;
		default:
//			result = R.string.errcode_unknown;
			WXHelp.getInstance(this).unknown();
			break;
		}
		
		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
	}
	
	private void goToGetMsg() {
//		Intent intent = new Intent(this, GetFromWXActivity.class);
//		intent.putExtras(getIntent());
//		startActivity(intent);
//		finish();
	}
	
	private void goToShowMsg(ShowMessageFromWX.Req showReq) {
		WXMediaMessage wxMsg = showReq.message;		
		WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;
		
		StringBuffer msg = new StringBuffer(); // ��֯һ������ʾ����Ϣ����
		msg.append("description: ");
		msg.append(wxMsg.description);
		msg.append("\n");
		msg.append("extInfo: ");
		msg.append(obj.extInfo);
		msg.append("\n");
		msg.append("filePath: ");
		msg.append(obj.filePath);
		
//		Intent intent = new Intent(this, ShowFromWXActivity.class);
//		intent.putExtra(Constants.ShowMsgActivity.STitle, wxMsg.title);
//		intent.putExtra(Constants.ShowMsgActivity.SMessage, msg.toString());
//		intent.putExtra(Constants.ShowMsgActivity.BAThumbData, wxMsg.thumbData);
//		startActivity(intent);
//		finish();
	}
}