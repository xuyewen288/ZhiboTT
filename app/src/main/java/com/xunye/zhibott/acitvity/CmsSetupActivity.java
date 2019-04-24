package com.xunye.zhibott.acitvity;

import android.annotation.SuppressLint;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.cms.iermu.cms.WifiType;
import com.iermu.opensdk.ErmuOpenSDK;
import com.iermu.opensdk.setup.OnSetupDevListener;
import com.iermu.opensdk.setup.model.CamDevConf;
import com.iermu.opensdk.setup.model.ScanStatus;
import com.iermu.opensdk.setup.scan.WifiNetworkManager;
import com.xunye.zhibott.R;
import com.xunye.zhibott.fragment.ScanDevFragment;
import com.xunye.zhibott.fragment.ScanQRCodeFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加摄像机模块
 *
 * Created by wcy on 16/2/29.
 */
public class CmsSetupActivity extends FragmentActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher {

    private MyAdapter   ssidAdapter;
    private EditText    accountEdt;
    private EditText    pwdEdt;
    private int selectedItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Spinner ssidEdt = (Spinner) findViewById(R.id.ssidEdt);
        accountEdt = (EditText) findViewById(R.id.accountEdt);
        pwdEdt     = (EditText) findViewById(R.id.pwdEdt);
        findViewById(R.id.apOrSmartBtn).setOnClickListener(this);
        findViewById(R.id.qrBtn).setOnClickListener(this);
        findViewById(R.id.qr2Btn).setOnClickListener(this);
        //findViewById(R.id.qr2Btn).setVisibility(View.GONE);
        accountEdt.addTextChangedListener(this);
        pwdEdt.addTextChangedListener(this);

        String SSID = WifiNetworkManager.getInstance(this).getSSIDSub();
        ssidAdapter = new MyAdapter(SSID);
        ssidEdt.setAdapter(ssidAdapter);
        ssidEdt.setOnItemSelectedListener(this);
        ErmuOpenSDK.newInstance().getSetupDevModule().scanWiFi();
        ErmuOpenSDK.newInstance().getSetupDevModule().addSetupDevListener(new OnSetupDevListener() {
            @Override
            public void onScanWiFi(ScanStatus status) {
                List<ScanResult> results = ErmuOpenSDK.newInstance().getSetupDevModule().getScanWiFi();
                ssidAdapter.notifyDataChange(results);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ssidAdapter.getCount()<=1) {
            String SSID = WifiNetworkManager.getInstance(this).getSSIDSub();
            ssidAdapter.notifyFirstChange(SSID);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedItem = i;
        changeEnableView(i);
        enableConfBtn();
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    @Override
    public void onClick(View view) {
        enableConfBtn();
        String SSID     = ssidAdapter.getItem(selectedItem);
        String capabilities= ssidAdapter.getCapabilities(selectedItem);
        String pwd      = pwdEdt.getText().toString().trim();
        String account  = accountEdt.getText().toString().trim();
        CamDevConf conf = CamDevConf.buildConf(SSID);
        conf.setWiFiCapabilities(capabilities);
        conf.setWiFiAccount(account);
        conf.setWiFiPwd(pwd);
        //conf.setDhcpIP(ipText);
        //conf.setDhcpGateway(gatewayTtext);
        //conf.setDhcpNetmask(netmaskText);
        if(view.getId()==R.id.apOrSmartBtn) {
            startScanDev(conf);
        } else if(view.getId()==R.id.qrBtn) {
            startQrDev(conf);
        } else if(view.getId()==R.id.qr2Btn) {
//            Fragment fragment = QRCodeFragment.actionInstance(this, conf);
//            FragmentManager manager = getSupportFragmentManager();
//            FragmentTransaction transaction = manager.beginTransaction();
//            transaction.replace(R.id.framelayout, fragment).commit();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ErmuOpenSDK.newInstance().getSetupDevModule().quitScanWiFi();
        ErmuOpenSDK.newInstance().getSetupDevModule().quitSetupDev();
    }

    private void changeEnableView(int i) {
        String capabilities = ssidAdapter.getCapabilities(selectedItem);
        int wifiType = CamDevConf.getWiFiType(capabilities);
        if (wifiType == WifiType.OPEN) {
            accountEdt.setVisibility(View.GONE);
            pwdEdt.setVisibility(View.GONE);
        } else if(wifiType == WifiType.EAP) {
            accountEdt.setVisibility(View.VISIBLE);
            pwdEdt.setVisibility(View.VISIBLE);
        } else {
            accountEdt.setVisibility(View.GONE);
            pwdEdt.setVisibility(View.VISIBLE);
        }
    }
    //Wifi为null || (密码为null && 加密类型 ) || ((用户名为null || 密码为null ) && 802.1x )
    //-> (密码小于8位 && 加密类型) || (密码小于8位 && 802.1x)
    private void enableConfBtn() {
        String SSID     = ssidAdapter.getItem(selectedItem);
        String capabilities = ssidAdapter.getCapabilities(selectedItem);
        int wifiType    = CamDevConf.getWiFiType(capabilities);
        String pwd      = pwdEdt.getText().toString();
        String account  = accountEdt.getText().toString();
        boolean emptyWifi = TextUtils.isEmpty(SSID);
        boolean emptyPwd = TextUtils.isEmpty(pwd);
        boolean emptyAccount = TextUtils.isEmpty(account);

        boolean disable = emptyWifi
                || (emptyPwd && (wifiType!= WifiType.OPEN))
                || ((emptyAccount || emptyPwd) && (wifiType== WifiType.EAP))
                || (pwd.length()<1 && (wifiType!= WifiType.OPEN));
        findViewById(R.id.apOrSmartBtn).setEnabled(!disable);
        findViewById(R.id.qrBtn).setEnabled(!disable);
        findViewById(R.id.qr2Btn).setEnabled(!disable);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        enableConfBtn();
    }
    @Override
    public void afterTextChanged(Editable editable) {}

    //AP|Smart扫描设备
    private void startScanDev(final CamDevConf conf) {
        Fragment fragment = ScanDevFragment.actionInstance(CmsSetupActivity.this, conf);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.framelayout, fragment).commit();
    }

    //二维码配置
    private void startQrDev(CamDevConf conf) {
        if(conf==null) return;
        Fragment fragment = ScanQRCodeFragment.actionInstance(this, conf);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.framelayout, fragment).commit();
    }

    class MyAdapter extends BaseAdapter {
        List<String> results = new ArrayList<String>();
        List<ScanResult> scanList = new ArrayList<ScanResult>();
        private MyAdapter(String SSID) {
            if(!TextUtils.isEmpty(SSID))results.add(SSID);
        }
        @Override
        public View getDropDownView(int i, View view, ViewGroup viewGroup) {
            View view1 = View.inflate(CmsSetupActivity.this, R.layout.layout_scanwifi_item, null);
            TextView ssidTv = (TextView) view1.findViewById(R.id.ssidTv);
            if(WifiNetworkManager.is5GHz(getFrequency(i))) {
                view1.setEnabled(false);
                view1.setClickable(false);
            }
            ssidTv.setText(getItem(i));
            return view1;
        }
        @Override
        public int getCount() {
            return results.size();
        }
        @Override
        public String getItem(int i) {
            return results.get(i);
        }
        @Override
        public long getItemId(int i) {
            return i;
        }
        @Override
        public boolean isEnabled(int i) {
            return !WifiNetworkManager.is5GHz(getFrequency(i));
        }
        @SuppressLint("ViewHolder")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1 = View.inflate(CmsSetupActivity.this, R.layout.layout_scanwifi_item, null);
            TextView ssidTv = (TextView) view1.findViewById(R.id.ssidTv);
            if(WifiNetworkManager.is5GHz(getFrequency(i))) {
                view1.setEnabled(false);
                view1.setClickable(false);
            }
            ssidTv.setText(getItem(i));
            return view1;
        }
        void notifyFirstChange(String SSID) {
            if(TextUtils.isEmpty(SSID)) return;
            results.clear();
            results.add(SSID);
            notifyDataSetChanged();
        }
        void notifyDataChange(List<ScanResult> data) {
            if(data == null) return;
            this.scanList = data;
            if(data.size() > 0) {
                results.clear();
                for(int i=-0; i<scanList.size(); i++) {
                    ScanResult item = scanList.get(i);
                    results.add(item.SSID);
                }
            }
            notifyDataSetChanged();
        }
        private int getFrequency(int selectedItem) {
            ScanResult item = __getItemScan(selectedItem);
            return (item==null) ? WifiNetworkManager.getInstance(CmsSetupActivity.this).getFrequency() : item.frequency;
        }
        private String getCapabilities(int selectedItem) {
            ScanResult item = __getItemScan(selectedItem);
            return (item==null) ? WifiNetworkManager.getInstance(CmsSetupActivity.this).getCapabilities() : item.capabilities;
        }
        private ScanResult __getItemScan(int i) { return (i>=scanList.size()) ? null : scanList.get(i); }
    }
}
