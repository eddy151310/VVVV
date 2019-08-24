package com.ahdi.wallet.app.callback;

import org.json.JSONObject;


public interface BankCardSdkCallBack {

    /**
     * @param code       “0”成功 其他失败
     * @param errorMsg   错误描述
     * @param jsonObject 响应成功时的json数据
     */
    void onResult(String code, String errorMsg, JSONObject jsonObject);
}
