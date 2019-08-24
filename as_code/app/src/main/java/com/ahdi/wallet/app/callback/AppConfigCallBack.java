package com.ahdi.wallet.app.callback;

/**
 * @author xiaoniu
 * @date 2018/8/23.
 */

public interface AppConfigCallBack {

    /**
     * APP 配置信息回调
     * @param Code
     * @param errorMsg
     */
    void onResult(String Code, String errorMsg);
}
