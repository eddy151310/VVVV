package com.ahdi.wallet.app.callback;

import org.json.JSONObject;

/**
 * @author zhaohe@iapppay.com
 * @date 2018/5/18.
 */

public interface IDVerifyCallBack {
    /**
     * 实名认证结果
     * @param code
     * @param errorMsg   错误描述
     * @param jsonObject 响应成功时的json数据
     */
    void onResult(String code, String errorMsg, JSONObject jsonObject);
}
