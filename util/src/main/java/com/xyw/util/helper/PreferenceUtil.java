package com.xyw.util.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PreferenceUtil {

    private static final String PREFERENCE_NAME = "config";
    private SharedPreferences mPreferences;

    public PreferenceUtil(Context ctx) {
        mPreferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
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