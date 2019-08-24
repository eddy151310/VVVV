package com.ahdi.wallet.bca.callback;

/**
 * @author xiaoniu
 * bca 绑卡、调整限额的回调
 */
public interface BcaBankCardCallBack {

    /**
     * 成功
     */
    void onSuccess();

    /**
     * 失败（包括取消）
     *
     * @param msg
     */
    void onFail(String msg);
}
