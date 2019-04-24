package com.xunye.zhibott.fragment;

import android.net.Uri;

import com.xunye.zhibott.helper.PreferenceUtil;

public interface OnFragmentInteractionListener {
    void onFragmentInteraction(Uri uri);
    PreferenceUtil getPreferenceUtil();
}
