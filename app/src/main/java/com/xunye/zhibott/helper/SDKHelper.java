package com.xunye.zhibott.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.PowerManager;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SDKHelper {

    private static PowerManager.WakeLock wakeLock = null;

    /**
     * 各种锁的类型对CPU 、屏幕、键盘的影响：
     * PARTIAL_WAKE_LOCK:        保持CPU 运转，屏幕和键盘灯有可能是关闭的。
     * SCREEN_DIM_WAKE_LOCK：    保持CPU 运转，允许保持屏幕显示但有可能是灰的，允许关闭键盘灯
     * SCREEN_BRIGHT_WAKE_LOCK： 保持CPU 运转，允许保持屏幕高亮显示，允许关闭键盘灯
     * FULL_WAKE_LOCK：          保持CPU 运转，保持屏幕高亮显示，键盘灯也保持亮度
     *
     * ACQUIRE_CAUSES_WAKEUP：   强制使屏幕亮起，这种锁主要针对一些必须通知用户的操作.
     * ON_AFTER_RELEASE：        当锁被释放时，保持屏幕亮起一段时间
     *
     * 声明权限：
     * <uses-permission android:name="android.permission.WAKE_LOCK"/>
     * <uses-permission android:name="android.permission.DEVICE_POWER"/>
     */
    /** 获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行 */
    public static void acquireWakeLock(Context context) {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, SDKHelper.class.getClass().getCanonicalName());
            if (null != wakeLock) {
                wakeLock.acquire();
            }
        }
    }

    /** 获取电源锁，保持屏幕高亮显示，允许关闭键盘灯 */
    public static void acquireScreenBrightLock(Context context) {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, SDKHelper.class.getClass().getCanonicalName());
            if (null != wakeLock) {
                wakeLock.acquire();
            }
        }
    }

    /** 释放设备电源锁 */
    public static void releaseWakeLock() {
        if (null != wakeLock && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    /** 设置屏幕相关参数 */
    public static void acquireKeepScreenOn(Activity context) {
        context.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                , WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    /** 清除屏幕不锁定参数 */
    public static void releaseKeepScreenOn(Activity context) {
        context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static String saveBitmap(String filePath, Bitmap bitmap) {
        if (bitmap == null) return "";
        //String imageName = Calendar.getInstance().getTimeInMillis() + ".jpeg";
        File f = new File(filePath);
        try {
            if (!f.exists()) {
                String parentFile = filePath.substring(0, filePath.lastIndexOf("/"));
                File parent = new File(parentFile);
                if(!parent.exists()) parent.mkdirs();
                f.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //try {
            //File fdir = new File("/sdcard/");
            //if (!fdir.exists()) fdir.mkdirs();
            //f = new File(fdir, imageName);
            //if (!f.exists()) f.createNewFile();
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if(out!=null) out.flush();
                if(out!=null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return f.getAbsolutePath();
    }
}
