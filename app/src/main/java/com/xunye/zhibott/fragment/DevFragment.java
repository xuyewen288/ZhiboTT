package com.xunye.zhibott.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.iermu.opensdk.ErmuOpenSDK;
import com.iermu.opensdk.api.ApiOkClient;
import com.iermu.opensdk.api.response.CamMetaResponse;
import com.iermu.opensdk.api.response.LiveMediaResponse;
import com.iermu.opensdk.api.response.RegisterDevResponse;
import com.iermu.opensdk.setup.OnApiClientInterceptor;
import com.iermu.opensdk.setup.OnSetupDevListener;
import com.iermu.opensdk.setup.conn.SetupStatus;
import com.iermu.opensdk.setup.model.CamDev;
import com.iermu.opensdk.setup.model.ScanStatus;
import com.xunye.zhibott.MyApplication;
import com.xunye.zhibott.R;
import com.xunye.zhibott.acitvity.ModeChooseActivity;
import com.xunye.zhibott.acitvity.PlayActivity;
import com.xunye.zhibott.acitvity.ViewActivity;
import com.xunye.zhibott.api.ServerApi;
import com.xunye.zhibott.helper.MessageEvent;
import com.xunye.zhibott.helper.ViewHolder;
import com.xyw.util.helper.HttpUtil;
import com.xyw.util.helper.LogUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DevFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DevFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ExecutorService threadPool;


    OnSetupDevListener onSetupDevListener;
    OnApiClientInterceptor onApiClientInterceptor;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ListView mListView;
    MyAdapter adapter;

    private OnFragmentInteractionListener mListener;

    public DevFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DevFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DevFragment newInstance(String param1, String param2) {
        DevFragment fragment = new DevFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dev, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        threadPool=((ViewActivity)getActivity()).getThreadPool();
        view.findViewById(R.id.tv_add_dev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ModeChooseActivity.class));
            }
        });
        mSwipeRefreshLayout=view.findViewById(R.id.SwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.e("xyw","mSwipeRefreshLayout onRefresh");
                apiDeviceList();
            }
        });
        mListView=view.findViewById(R.id.dev_listview);
        adapter=new MyAdapter();
        mListView.setAdapter(adapter);
        apiDeviceList();

//        new Thread(new Runnable() {
////            @Override
////            public void run() {
////                String res= ServerApi.apiDeviceList(ErmuOpenSDK.newInstance().getAccessToken());
////                Log.e("xyw","device list res="+res);
////
////                String res2= ServerApi.apiDevicePlaylist(ErmuOpenSDK.newInstance().getAccessToken());
////                Log.e("xyw","device list res2="+res2);
////
////                String res3= ServerApi.apiDeviceVod(ErmuOpenSDK.newInstance().getAccessToken());
////                Log.e("xyw","device list res3="+res3);
////            }
////        }).start();

    }

    private void initErmu(){
        ErmuOpenSDK.newInstance().getSetupDevModule().scanWiFi();
        onSetupDevListener=new OnSetupDevListener() {
            @Override
            public void onScanWiFi(ScanStatus scanStatus) {
                super.onScanWiFi(scanStatus);
            }

            @Override
            public void onScanQRCode(ScanStatus scanStatus) {
                super.onScanQRCode(scanStatus);
            }

            @Override
            public void onScanDev(ScanStatus scanStatus) {
                super.onScanDev(scanStatus);
            }

            @Override
            public void onScanAuthDev(ScanStatus scanStatus) {
                super.onScanAuthDev(scanStatus);
            }

            @Override
            public void onSetupStatus(SetupStatus setupStatus) {
                super.onSetupStatus(setupStatus);
            }

            @Override
            public void onUpdateProgress(int i) {
                super.onUpdateProgress(i);
            }
        };
        onApiClientInterceptor=new OnApiClientInterceptor() {
            @Override
            public RegisterDevResponse apiRegisterDevice(CamDev camDev) {
                return null;
            }

            @Override
            public CamMetaResponse apiCamMeta(CamDev camDev) {
                return null;
            }
        };
        ErmuOpenSDK.newInstance().getSetupDevModule().addSetupDevListener(onSetupDevListener);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void apiDeviceList(){
//        threadPool.execute(new Runnable() {
//            @Override
//            public void run() {
//                String res= ServerApi.apiDeviceList(ErmuOpenSDK.newInstance().getAccessToken());
//                Log.e("xyw","device list res="+res);
//                try {
//                    JSONObject jsonObject=new JSONObject(res);
//                    adapter.setNumber(jsonObject.getInt("count"));
//                    JSONArray jsonArray=jsonObject.getJSONArray("list");
//                    Log.e("xyw","jsonArray="+jsonArray.toString());
//                    adapter.setJsonArray(jsonArray);
//                    EventBus.getDefault().post(new MessageEvent("updateDevice"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });


        OkHttpUtils.post().url(MyApplication.serverLiveUrl+"/device/person/list")
                .addParams("username",MyApplication.username).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.e(response);
                JSONArray jsonArray= null;
                try {
                    jsonArray = new JSONArray(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("xyw","jsonArray="+jsonArray.toString());
                adapter.setNumber(jsonArray.length());
                adapter.setJsonArray(jsonArray);

                adapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
//                EventBus.getDefault().post(new MessageEvent("updateDevice"));
            }
        });
    }

    @Override
    public void onEvent(MessageEvent messageEvent) {
        super.onEvent(messageEvent);
        Log.e("xyw","onevent");
        if(messageEvent.getWhat()==1) {
            adapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
        }else if(messageEvent.getWhat()==2){
            mSwipeRefreshLayout.setRefreshing(true);
            apiDeviceList();
        }
    }

    class MyAdapter extends BaseAdapter{

        private int number=0;
        private JSONArray jsonArray;

        public MyAdapter() {

        }

        public void setNumber(int number){
            this.number=number;
        }
        public void setJsonArray(JSONArray jsonArray){
            this.jsonArray=jsonArray;
        }

        @Override
        public int getCount() {
            return number;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(null==convertView){
                convertView=LayoutInflater.from(DevFragment.this.getContext()).inflate(R.layout.item_list_layout,parent,false);
            }
            final ImageView iv_thumbnail= ViewHolder.get(convertView,R.id.iv_thumbnail);
            final TextView tv_description=ViewHolder.get(convertView,R.id.tv_description);
//            LogUtil.e("jsonArray==>"+jsonArray.toString());
            try {
                final JSONObject jsonObject=jsonArray.getJSONObject(position);
                LogUtil.e("jsonObject==>"+jsonObject.toString());
//                tv_description.setText(jsonObject.getString("descinfo"));
                OkHttpUtils.post().url(MyApplication.serverLiveUrl+"/device/info")
                        .addParams("deviceid",jsonObject.getString("deviceid")).build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject resjson=new JSONObject(response);
                            LogUtil.e("response==>"+response);
                            tv_description.setText(resjson.getString("description"));
                            Glide.with(DevFragment.this).load(resjson.getString("thumbnail"))
                                    .placeholder(R.mipmap.loading2)
                                    .transition(DrawableTransitionOptions.withCrossFade(300).crossFade())
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(iv_thumbnail)
                            ;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //获取设备授权码
            /*
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    String codeRes= "111";
                    try {
                        codeRes = ServerApi.apiGrantCode(ErmuOpenSDK.newInstance().getAccessToken(),
                                jsonArray.getJSONObject(position).getString("deviceid"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("xyw","codeRes===>"+codeRes);
                }
            });
            */

            iv_thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    threadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            LiveMediaResponse response= null;
                            try {
//                                response = ServerApi.apiLivePlay(jsonArray.getJSONObject(position).getString("deviceid"), ErmuOpenSDK.newInstance().getAccessToken(),"","");
                                Map<String, Object> params = new HashMap<String, Object>();
                                String deviceid=jsonArray.getJSONObject(position).getString("deviceid");
                                params.put("deviceid", deviceid);
                                params.put("username", MyApplication.username);
//                                params.put("access_token", accessToken);
                                ApiOkClient okClient = new ApiOkClient(MyApplication.serverLiveUrl);
                                ApiOkClient.Method method = ApiOkClient.Method.POST;
                                String relativeUrl  = "/device/liveplay";
                                String res = okClient.execute(method, relativeUrl, params);
                                response = LiveMediaResponse.parseResponse(deviceid, res);

                                Intent intent=new Intent(getActivity(),PlayActivity.class);
                                intent.putExtra("url",response.getLiveMedia().getPlayUrl());
                                intent.putExtra("deviceid",deviceid);
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
            return convertView;
        }
    }
}
