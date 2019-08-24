package com.ahdi.wallet.app.callback;

import com.ahdi.wallet.network.framwork.Response;

/**
 * @author admin
 */
public interface VoucherSdkCallBack {

    /**
     * 响应结果
     * @param code       “0”成功 其他失败
     * @param errorMsg   错误描述
     * @param response   响应数据
     */
    void onResult(String code, String errorMsg, Response response);
}
