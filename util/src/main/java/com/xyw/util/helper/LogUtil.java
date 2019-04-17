package com.xyw.util.helper;

import android.util.Log;

public class LogUtil {
    private static final String TAG="xyw";
    public static void i(String string){
        Log.i(TAG,string);
    }

    public static void e(String string){
        Log.e(TAG,string);
    }
}
