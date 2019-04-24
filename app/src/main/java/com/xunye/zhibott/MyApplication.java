package com.xunye.zhibott;

import android.content.Context;
import android.util.Log;

import com.iermu.opensdk.ErmuOpenSDK;
import com.iermu.opensdk.api.ApiOkClient;
import com.mob.MobApplication;
import com.xunye.zhibott.helper.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import tv.danmaku.ijk.media.widget.TopvdnApi;

public class MyApplication extends MobApplication {
    public static Context sContext;
    public static String username;
//    public static String serverLiveUrl="http://192.168.1.100:8082/live";
    public static String serverLiveUrl="http://192.168.2.242:8082/live";
//    public static String serverLiveUrl="http://www.itrontest.top:8082/live";
//    public static String serverSystemUrl="http://192.168.1.100:8082/system";
    public static String serverSystemUrl="http://192.168.2.242:8082/system";
//    public static String serverSystemUrl="http://www.itrontest.top:8082/system";
    private ExecutorService executorService= Executors.newSingleThreadExecutor();
    public Future<String> future;
    private Callable<String> callable;
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i("应用启动");
        callable=new Callable<String>(){

            @Override
            public String call() throws Exception {
                LogUtil.i("开始连接服务器");
                ApiOkClient okClient2 = new ApiOkClient(MyApplication.serverLiveUrl);
                ApiOkClient.Method method2 = ApiOkClient.Method.GET;
                String relativeUrl2  = "/oauth2/token";
                String res2 = null;
                try {
                    res2 = okClient2.execute(method2, relativeUrl2, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    return "error";
                }
                Log.i("xyw","token==>"+res2);
                try {
                    JSONObject jsonObject=new JSONObject(res2);
                    String access_token=jsonObject.getString("access_token");
                    String uid=jsonObject.getString("uid");
                    String init=jsonObject.getString("init");
                    String user_token=jsonObject.getString("user_token");

                    ErmuOpenSDK.newInstance()
                            .init(MyApplication.this)
                            .configEndpoint("https://api.iermu.com")
                            .configToken(access_token,uid);
                    TopvdnApi.start_lyy_service(user_token, init, null);
                    return "success";
                } catch (Exception e) {
                    return "error";
                }

            }
        };
        future=executorService.submit(callable);
    }

    public void connectServer(){
        future=executorService.submit(callable);
    }
}
