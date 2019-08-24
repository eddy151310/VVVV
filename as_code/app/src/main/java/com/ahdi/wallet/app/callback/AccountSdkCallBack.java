package com.ahdi.wallet.app.callback;

        import org.json.JSONObject;

/**
 * Date: 2018/1/5 下午5:07
 * Author: kay lau
 * Description:
 */
public interface AccountSdkCallBack {

    /**
     * @param code       “0”成功 其他失败
     * @param errorMsg   错误描述
     * @param jsonObject 响应成功时的json数据
     */
    void onResult(String code, String errorMsg, JSONObject jsonObject);
}
