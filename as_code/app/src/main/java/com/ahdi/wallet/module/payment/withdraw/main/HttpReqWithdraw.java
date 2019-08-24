package com.ahdi.wallet.module.payment.withdraw.main;

import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.wallet.module.payment.withdraw.requset.WDCreateRequest;
import com.ahdi.wallet.module.payment.withdraw.requset.WDQueryBankCardInfoRequest;
import com.ahdi.wallet.module.payment.withdraw.requset.WDQueryProgressRequest;
import com.ahdi.wallet.module.payment.withdraw.util.ConstantsWithdraw;
import com.ahdi.wallet.network.HttpReqAsyncTask;

public class HttpReqWithdraw {

    private static HttpReqWithdraw instance;

    private HttpReqWithdraw() {

    }

    public synchronized static HttpReqWithdraw getInstance() {
        if (instance == null) {
            instance = new HttpReqWithdraw();
        }
        return instance;
    }


    /**
     * 查询银行账户
     *
     * @param request
     * @param listener
     */
    public void queryWDBankCardInfo(WDQueryBankCardInfoRequest request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsWithdraw.MODULE_WDQUERY_BIND_CARD_INFO, request, listener)).execute();
    }

    /**
     * 发起提现
     *
     * @param request
     * @param listener
     */
    public void createWD(WDCreateRequest request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsWithdraw.MODULE_WDQUERY_CREATE, request, listener)).execute();
    }

    /**
     * 查询提现进度
     *
     * @param request
     * @param listener
     */
    public void queryWDProgress(WDQueryProgressRequest request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsWithdraw.MODULE_WDQUERY_PROGRESS, request, listener)).execute();
    }

}
