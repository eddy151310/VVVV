package com.ahdi.wallet.app.callback;

import com.ahdi.wallet.app.bean.PhoneAreaCodeBean;

/**
 * @author zhaohe
 */
public interface PhoneAreaCodeCallBack {

    /**
     * 回调
     * @param code      “0”成功 其他失败
     * @param errorMsg   错误描述
     * @param bean 选择的区号
     */
    void onResult(String code, String errorMsg, PhoneAreaCodeBean bean);
}
