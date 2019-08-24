package com.ahdi.wallet.module.payment.withdraw.callback;

import org.json.JSONObject;

/**
 * Created by Administrator on 2018/4/9.
 * 对外结果回调
 */

public interface WithDrawResultCallBack {

    /**
     * 红包sdk结果回调
     * @param code “0”成功 其他失败
     * @param errorMsg 错误描述
     * @param jsonObject 响应成功时的json数据
     */
    void onResult(String code, String errorMsg, JSONObject jsonObject,String ID);

}
