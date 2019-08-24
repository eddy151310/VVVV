package com.ahdi.wallet.module.payment.transfer.callback;

import org.json.JSONObject;

/**
 * @author admin
 * @date Created by xiaoniu on 2018/1/5.
 * @email zhao_zhaohe@163.com
 *
 * 对外结果回调
 */

public interface TransferResultCallBack {

    /**
     * 红包sdk结果回调
     * @param code “0”成功 其他失败
     * @param errorMsg 错误描述
     * @param jsonObject 响应成功时的json数据
     */
    void onResult(String code, String errorMsg, JSONObject jsonObject,String ID);

}
