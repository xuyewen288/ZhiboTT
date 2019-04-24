package com.xunye.zhibott.helper;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class InputHelper {

    /**
     * 隐藏软键盘
     * @param ctx
     * @param edit
     */
    public static void hideSoftInput(Activity ctx, View edit){
        if(edit != null) {
            edit.clearFocus();
            InputMethodManager mInputManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            mInputManager.hideSoftInputFromWindow(edit.getWindowToken(), 0);
        }
    }

    public static void hideSoftInput(final Activity ctx) {
        ctx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                InputMethodManager mInputKeyBoard = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (ctx.getCurrentFocus() != null) {
                    mInputKeyBoard.hideSoftInputFromWindow(ctx.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    ctx.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                }
            }
        });
    }

    /**
     * 弹出软键盘
     * @param ctx
     * @param edit
     */
    public static void showSoftInput(Activity ctx, EditText edit){
        if(edit != null) {
            edit.setFocusable(true);
            edit.requestFocus();
            InputMethodManager mInputManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            mInputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}