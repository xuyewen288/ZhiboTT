package com.xunye.zhibott.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.iermu.opensdk.ErmuOpenSDK;
import com.iermu.opensdk.OSLog;
import com.iermu.opensdk.api.ApiOkClient;
import com.iermu.opensdk.api.response.CamMetaResponse;
import com.iermu.opensdk.api.response.RegisterDevResponse;
import com.iermu.opensdk.setup.ISetupDevModule;
import com.iermu.opensdk.setup.OnApiClientInterceptor;
import com.iermu.opensdk.setup.OnSetupDevListener;
import com.iermu.opensdk.setup.conn.SetupStatus;
import com.iermu.opensdk.setup.model.CamDev;
import com.iermu.opensdk.setup.model.CamDevConf;
import com.iermu.opensdk.setup.model.ScanStatus;
import com.xunye.zhibott.MyApplication;
import com.xunye.zhibott.R;
import com.xunye.zhibott.helper.HttpUtil;
import com.xunye.zhibott.helper.SDKHelper;
import com.xyw.util.helper.LogUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 扫描设备
 *
 * Created by wcy on 16/2/29.
 */
public class ScanDevFragment extends Fragment implements View.OnClickListener,AdapterView.
        OnItemClickListener, OnApiClientInterceptor {

    final ISetupDevModule devModule = ErmuOpenSDK.newInstance().getSetupDevModule();
    static CamDevConf camdevConf;
    ListView devListView;
    Button startBtn;
    TextView logContent;
    MyAdapter myAdapter;
    int selectedPosition = 0;

    public static Fragment actionInstance(Activity activity, CamDevConf conf) {
        camdevConf = conf;
        return new ScanDevFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_devlist, null);
        devListView = (ListView) view.findViewById(R.id.devListView);
        logContent = (TextView) view.findViewById(R.id.content_log);
        startBtn = (Button) view.findViewById(R.id.startBtn);
        startBtn.setOnClickListener(this);
        devListView.setOnItemClickListener(this);
        myAdapter = new MyAdapter();
        devListView.setAdapter(myAdapter);
        devModule.scanCam(camdevConf);
        devModule.addSetupDevListener(new OnSetupDevListener() {

            @Override
            public void onScanWiFi(ScanStatus scanStatus) {
                super.onScanWiFi(scanStatus);
                Log.e("xyw","onScanWiFi---"+scanStatus);
            }

            @Override
            public void onScanAuthDev(ScanStatus status) {
                Log.e("xyw","onScanAuthDev---"+status);
                super.onScanAuthDev(status);
                if(getActivity()==null || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && getActivity().isDestroyed())) return;
                switch (status){
                case SUCCESS:
                    List<CamDev> list = ErmuOpenSDK.newInstance().getSetupDevModule().getScanCamDev();
                    myAdapter.notifyDataSetChanged(list);
                    break;
                case AUTH_EXPIRED:
                    logContent("授权码已过期!");
                    break;
                case AUTH_CREATE_FAIL:
                    logContent("创建授权码失败!");
                    break;
                }
            }
            @Override
            public void onScanDev(ScanStatus status) {
                super.onScanDev(status);
                Log.e("xyw","onScanDev---"+status);
                List<CamDev> list = ErmuOpenSDK.newInstance().getSetupDevModule().getScanCamDev();
                myAdapter.notifyDataSetChanged(list);
            }
            @Override
            public void onSetupStatus(SetupStatus status) {
                super.onSetupStatus(status);
                Log.e("xyw","onSetupStatus---"+status);
                switch (status){
                case SETUP_ENV_SMART_TIMEOUT:
                    break;
                case SETUP_ENV_SMART_WIFI_NOMATCH:
                    break;
                case REGISTER_ING:
                    break;
                case REGISTER_NOTPERMISSION:
                    startBtn.setEnabled(true);
                    logContent("没有该设备权限!");
                    break;
                case REGISTER_FAIL:
                    startBtn.setEnabled(true);
                    logContent("注册设备失败!");
                    break;
                case REGISTER_SUCCESS:
                case REGISTED:
                    break;
                case CONNECT_DEV_FAIL:
                case CONF_CONNECTDEV_FAIL:
                case CONNECT_WIFI_FAIL:
                    startBtn.setEnabled(true);
                    logContent("配置设备失败!");
                    break;
                case AUTH_DEV_FAIL:
                    startBtn.setEnabled(true);
                    logContent("授权设备失败!");
                    break;
                case AUTH_DEV_EXPIRED:
                    startBtn.setEnabled(true);
                    logContent("授权设备过期!");
                    break;
                case SETUP_FAIL:
                    startBtn.setEnabled(true);
                    logContent("安装设备失败!");
                    break;
                case SETUP_SUCCESS:
                    startBtn.setEnabled(true);
                    logContent("安装设备成功!");
                    break;
                }
            }
            @Override
            public void onUpdateProgress(int progress) {
                super.onUpdateProgress(progress);
                //Log.i("", "安装进度: "+String.valueOf(progress));
                logProgressContent(progress);
            }
        });
        SDKHelper.acquireWakeLock(getActivity());
        return view;
    }

    @Override
    public void onClick(View view) {
        if(selectedPosition>myAdapter.getCount() || myAdapter.getCount()<=0) return;

        CamDev item = (CamDev) myAdapter.getItem(selectedPosition);
        devModule.addApiClientInterceptor(this);
        devModule.connectCam(item);
        startBtn.setEnabled(false);
        logContent("");
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        myAdapter.setSelected(i);
        selectedPosition = i;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(devModule!=null) devModule.quitScanCam();
        SDKHelper.releaseWakeLock();
    }

    /**
     * 注册设备接口
     * @param camDev x
     * @return x
     */
    @Override
    public RegisterDevResponse apiRegisterDevice(CamDev camDev) {
        RegisterDevResponse response;
        String devID    = camDev.getDevID();
        int connectType = camDev.getServerConnectType();
        Log.e("xyw","apiRegisterDevice=="+devID+"---"+connectType);
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("method", "register");
            params.put("deviceid", devID);
            params.put("device_type", 1);
            params.put("connect_type", connectType);
            params.put("desc", "我的摄像机");
            params.put("access_token", ErmuOpenSDK.newInstance().getAccessToken());
            ApiOkClient okClient = new ApiOkClient(ErmuOpenSDK.newInstance().getEndpoint());
            ApiOkClient.Method method = ApiOkClient.Method.POST;
            String relativeUrl  = "/v2/pcs/device";
            String str = okClient.execute(method, relativeUrl, params);
            response = RegisterDevResponse.parseResponse(str);



            OkHttpUtils.post().url(MyApplication.serverLiveUrl+"/device/register")
                    .addParams("username",MyApplication.username)
                    .addParams("deviceid",devID)
                    .addParams("desc","我的摄像机")
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {

                }

                @Override
                public void onResponse(String response, int id) {
                    LogUtil.e("register result=="+response);
                }
            });
        } catch (Exception e) {
            OSLog.e("registerDevice", e);
            response = RegisterDevResponse.parseResponseError(e);
        }
        return response;

    }

    /**
     * 获取设备状态信息接口
     * @param camDev x
     * @return x
     */
    @Override
    public CamMetaResponse apiCamMeta(CamDev camDev) {
        CamMetaResponse response;
        String devID    = camDev.getDevID();
        Log.e("xyw","apiCamMeta=="+devID);
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("method", "meta");
            params.put("deviceid", devID);
            params.put("access_token", ErmuOpenSDK.newInstance().getAccessToken());
            ApiOkClient okClient = new ApiOkClient(ErmuOpenSDK.newInstance().getEndpoint());
            ApiOkClient.Method method = ApiOkClient.Method.GET;
            String relativeUrl = "/v2/pcs/device";
            String str = okClient.execute(method, relativeUrl, params);
            response = CamMetaResponse.parseResponse(str);
        } catch (Exception e) {
            OSLog.e("apiCamMeta", e);
            response = CamMetaResponse.parseResponseError(e);
        }
        return response;
    }
    private void logContent(String str) {
        if(logContent != null) logContent.setText(str);
    }
    private void logProgressContent(int progress) {
        String text = logContent.getText().toString();
        text = text.replaceAll("安装进度:[\\s]?[0-9]{1,3};[\\s]?", "");
        if(logContent != null) logContent.setText("安装进度: "+progress+"; "+text);
    }
    class MyAdapter extends BaseAdapter {
        List<CamDev> list = new ArrayList<CamDev>();
        private int selected;
        @Override
        public int getCount() {
            return list.size();
        }
        @Override
        public Object getItem(int i) {
            return list.get(i);
        }
        @Override
        public long getItemId(int i) {
            return i;
        }
        @SuppressLint("ViewHolder")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = View.inflate(getActivity(), R.layout.layout_devlist_item, null);
            TextView devidTv = (TextView) view.findViewById(R.id.devIDTv);
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);

            CamDev item = (CamDev) getItem(i);
            devidTv.setText(item.getDevID());
            checkBox.setChecked(selected==i);
            return view;
        }

        private void notifyDataSetChanged(List<CamDev> data) {
            if(data == null) {
                data = new ArrayList<CamDev>();
            }
            this.list = data;
            super.notifyDataSetChanged();
        }
        private void setSelected(int selected) {
            this.selected = selected;
            super.notifyDataSetChanged();
        }
    }
}
