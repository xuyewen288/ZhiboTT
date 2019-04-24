package com.xunye.zhibott.api;

import com.iermu.opensdk.ErmuOpenSDK;
import com.iermu.opensdk.OSLog;
import com.iermu.opensdk.api.ApiOkClient;
import com.iermu.opensdk.api.response.AuthCodeResponse;
import com.iermu.opensdk.api.response.LiveMediaResponse;
import com.iermu.opensdk.api.response.TokenResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * wcy
 * Created by wcy on 2018/5/21.
 */

public class ServerApi {
    private static final String APPKEY = "lTlpBDk0eviYJ7MyC3OG";//MainActivity.APPKEY_DEMO;
    public static final String APPKEY_DEMO = "yHa0LoYzL4ePcNxTveY6";


    public static String getToken(String code, String clientId, String redirectUri) {
        String grantType = "authorization_code";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("grant_type", grantType);
        params.put("code", code);
        params.put("client_id", clientId);
        params.put("redirect_uri", redirectUri);
        ApiOkClient okClient = new ApiOkClient(ErmuOpenSDK.newInstance().getEndpoint());
        ApiOkClient.Method method = ApiOkClient.Method.GET;
        String relativeUrl = "/oauth2/token";
        String str = "";
        try {
            str = okClient.execute(method, relativeUrl, params);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static TokenResponse mobileLogin(String mobile, String password) {
        TokenResponse response;
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("grant_type", "password");
            params.put("client_id", APPKEY);
            params.put("mobile", mobile);
            params.put("password", password);
            ApiOkClient okClient = new ApiOkClient(ErmuOpenSDK.newInstance().getEndpoint());
            ApiOkClient.Method method = ApiOkClient.Method.GET;
            String relativeUrl  = "/oauth2/token";
            String str = okClient.execute(method, relativeUrl, params);
            response = TokenResponse.parseResponse(str);
        } catch (Exception e) {
            OSLog.e("mobileLogin", e);
            response = TokenResponse.parseResponseError(e);
        }
        return response;
    }

    public static TokenResponse companyLogin(String company, String username, String password) {
        TokenResponse response;
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("grant_type", "password");
            params.put("client_id", APPKEY);
            params.put("username", username);
            params.put("password", password);
            params.put("domain", company);
            ApiOkClient okClient = new ApiOkClient(ErmuOpenSDK.newInstance().getEndpoint());
            ApiOkClient.Method method = ApiOkClient.Method.GET;
            String relativeUrl  = "/oauth2/token";
            String str = okClient.execute(method, relativeUrl, params);
            response = TokenResponse.parseResponse(str);
        } catch (Exception e) {
            OSLog.e("companyLogin", e);
            response = TokenResponse.parseResponseError(e);
        }
        return response;
    }

    public static AuthCodeResponse getAuthCode(String accessToken) {
        AuthCodeResponse response;
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("method", "authcode");
            params.put("operation", "register");
            params.put("access_token", accessToken);
            ApiOkClient okClient = new ApiOkClient(ErmuOpenSDK.newInstance().getEndpoint());
            ApiOkClient.Method method = ApiOkClient.Method.GET;
            String relativeUrl  = "/v2/pcs/device";
            String str = okClient.execute(method, relativeUrl, params);
            response = AuthCodeResponse.parseResponse(str);
        } catch (Exception e) {
            OSLog.e("getAuthCode", e);
            response = AuthCodeResponse.parseResponseError(e);
        }
        return response;
    }

    /**
     * 获取直播信息
     *
     * @param deviceId    设备ID
     * @param accessToken AccessToken
     * @param shareId     公开分享ID
     * @param uk          用户ID
     */
    public static LiveMediaResponse apiLivePlay(String deviceId, String accessToken, String shareId, String uk) {
        LiveMediaResponse response;
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("method", "liveplay");
            params.put("deviceid", deviceId);
            params.put("shareid", shareId);
            params.put("uk", uk);
            params.put("access_token", accessToken);
            ApiOkClient okClient = new ApiOkClient(ErmuOpenSDK.newInstance().getEndpoint());
            ApiOkClient.Method method = ApiOkClient.Method.POST;
            String relativeUrl  = "/v2/pcs/device";
            String res = okClient.execute(method, relativeUrl, params);
            response = LiveMediaResponse.parseResponse(deviceId, res);
        } catch (Exception e) {
            OSLog.e("registerDevice", e);
            response = LiveMediaResponse.parseResponseError(e);
        }
        return response;
    }

    /**
     * 获取设备列表信息
     *
     * @param accessToken AccessToken
     */
    public static String apiDeviceList( String accessToken) {
        String res="";
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("method", "list");
            params.put("access_token", accessToken);
            ApiOkClient okClient = new ApiOkClient(ErmuOpenSDK.newInstance().getEndpoint());
            ApiOkClient.Method method = ApiOkClient.Method.POST;
            String relativeUrl  = "/v2/pcs/device";
            res = okClient.execute(method, relativeUrl, params);
        } catch (Exception e) {
            OSLog.e("registerDevice", e);
        }
        return res;
    }

    /**
     * 获取设备录像信息
     *
     * @param accessToken AccessToken
     */
    public static String apiDeviceVod( String accessToken) {
        String res="";
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("method", "vod");
            params.put("deviceid", "137898420843");
            params.put("st", "1538856000");
            params.put("et", "1538859600");
            params.put("access_token", accessToken);
            ApiOkClient okClient = new ApiOkClient(ErmuOpenSDK.newInstance().getEndpoint());
            ApiOkClient.Method method = ApiOkClient.Method.POST;
            String relativeUrl  = "/v2/pcs/device";
            res = okClient.execute(method, relativeUrl, params);
        } catch (Exception e) {
            OSLog.e("registerDevice", e);
        }
        return res;
    }

    /**
     * 获取设备路线列表信息
     *
     * @param accessToken AccessToken
     */
    public static String apiDevicePlaylist( String accessToken) {
        String res="";
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("method", "playlist");
            params.put("deviceid", "137898420843");
            params.put("st", "1538856000");
            params.put("et", "1538859600");
            params.put("access_token", accessToken);
            ApiOkClient okClient = new ApiOkClient(ErmuOpenSDK.newInstance().getEndpoint());
            ApiOkClient.Method method = ApiOkClient.Method.POST;
            String relativeUrl  = "/v2/pcs/device";
            res = okClient.execute(method, relativeUrl, params);
        } catch (Exception e) {
            OSLog.e("registerDevice", e);
        }
        return res;
    }

    /**
     * 获取设备授权码
     * @return
     */
    public static String apiGrantCode(String accessToken,String deviceid){
        String res="";
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("method", "grantcode");
            params.put("deviceid", deviceid);
            params.put("grant_type", 1);
            params.put("access_token", accessToken);
            ApiOkClient okClient = new ApiOkClient(ErmuOpenSDK.newInstance().getEndpoint());
            ApiOkClient.Method method = ApiOkClient.Method.POST;
            String relativeUrl  = "/v2/pcs/device";
            res = okClient.execute(method, relativeUrl, params);
        } catch (Exception e) {
            OSLog.e("apiGrantCode", e);
        }
        return res;
    }
}
