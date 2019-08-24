package com.ahdi.lib.utils.network;

import org.json.JSONObject;

/**
 * 调用接口时的回调
 */
public interface HttpReqTaskListener {
    /**
     * 调用接口前执行的操作
     */
    void onPreExecute();

    void onPostExecute(JSONObject json);

    void onError(JSONObject json);

}
