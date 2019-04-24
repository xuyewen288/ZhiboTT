package com.xunye.zhibott.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PreferenceUtil {

    private static final String PREFERENCE_NAME = "config";
    public static final String KEY_UID          = "_uid";
    public static final String KEY_ACCESSTOKEN  = "_accesstoken";
    public static final String KEY_REFRESHTOKEN = "_refreshtoken";
    public static final String KEY_USERTOKEN_LYY = "_usertoken_lyy";
    public static final String KEY_USERCONFIG_LYY= "_userconfig_lyy";
    private SharedPreferences mPreferences;

    public PreferenceUtil(Context ctx) {
        mPreferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void setAccount(String uid, String accessToken, String refreshToken, String userTokenLYY, String userConfigLYY) {
        setPreference(KEY_UID, uid);
        setPreference(KEY_ACCESSTOKEN, accessToken);
        setPreference(KEY_REFRESHTOKEN, refreshToken);
        setPreference(KEY_USERTOKEN_LYY, userTokenLYY);
        setPreference(KEY_USERCONFIG_LYY, userConfigLYY);
    }

    public Map<String, String> getAccount() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(KEY_UID, getString(KEY_UID));
        map.put(KEY_ACCESSTOKEN, getString(KEY_ACCESSTOKEN));
        map.put(KEY_REFRESHTOKEN, getString(KEY_REFRESHTOKEN));
        map.put(KEY_USERTOKEN_LYY, getString(KEY_USERTOKEN_LYY));
        map.put(KEY_USERCONFIG_LYY, getString(KEY_USERCONFIG_LYY));
        return map;
    }

    public void logoutAccount() {
        setPreference(KEY_UID, "");
        setPreference(KEY_ACCESSTOKEN, "");
        setPreference(KEY_REFRESHTOKEN, "");
        setPreference(KEY_USERTOKEN_LYY, "");
        setPreference(KEY_USERCONFIG_LYY, "");
    }

    public boolean isLogin() {
        return !TextUtils.isEmpty(getString(KEY_UID)) && !TextUtils.isEmpty(getString(KEY_ACCESSTOKEN));
    }

    private boolean getBoolean(String key) {
        return mPreferences.getBoolean(key, false);
    }

    public String getString(String key) {
        return mPreferences.getString(key, "");
    }

    public int getInt(String key) {
        return mPreferences.getInt(key, 0);
    }

    public void setPreference(String key, Object value) {
        if (TextUtils.isEmpty(key))
            return;
        SharedPreferences.Editor editor = mPreferences.edit();
        if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Set) {
            editor.putStringSet(key, (Set) value);
        }
        editor.commit();
    }
}