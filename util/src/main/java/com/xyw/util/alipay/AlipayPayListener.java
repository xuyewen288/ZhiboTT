package com.xyw.util.alipay;

import java.util.Map;

public interface AlipayPayListener {
    void onResponse(Map<String, String> result);
}
