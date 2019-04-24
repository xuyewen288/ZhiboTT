package com.xunye.zhibott.helper;

import android.content.Context;

import java.io.InputStream;

/**
 * Created by wcy on 2016/12/21.
 */
public class JSONReaderFileHelper {

    public static String readerAssetsJsonFile(Context context, String fileName) {
        String resultString = "";
        try {
            InputStream inputStream = context.getResources().getAssets().open(fileName);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            resultString = new String(bytes, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultString;
    }

}
