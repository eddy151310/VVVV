package com.ahdi.wallet.bca.callback;

import org.json.JSONObject;

/**
 * @author xiaoniu
 * bca 绑卡、调整限额的回调
 */
public interface BcaOTPCallBack {
    /**
     * otp回调
     *
     * @param code       “0”成功 其他失败
     * @param errorMsg   错误描述
     * @param jsonObject 响应成功时的json数据
     */
    void onResult(String code, String errorMsg, JSONObject jsonObject);
}
