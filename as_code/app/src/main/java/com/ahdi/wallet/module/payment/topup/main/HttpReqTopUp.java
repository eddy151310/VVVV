package com.ahdi.wallet.module.payment.topup.main;

import android.os.AsyncTask;

import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.wallet.module.payment.topup.requset.RechrQuotaQueryReq;
import com.ahdi.wallet.module.payment.topup.requset.RechrResultQueryReq;
import com.ahdi.wallet.module.payment.topup.requset.RechrTypeQueryReq;
import com.ahdi.wallet.module.payment.topup.util.ConstantsTopUp;
import com.ahdi.wallet.network.HttpReqAsyncTask;

public class HttpReqTopUp {

    private static HttpReqTopUp instance;

    private HttpReqTopUp() {

    }

    public synchronized static HttpReqTopUp getInstance() {
        if (instance == null) {
            instance = new HttpReqTopUp();
        }
        return instance;
    }

    /**
     * 充值类型查询
     *
     * @param req
     * @param listener
     */
    public void onRechrTypeQuery(RechrTypeQueryReq req, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsTopUp.MODULE_RECHR_TYPE, req, listener)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 查询充值额度(充值专用)
     *
     * @param request
     * @param listener
     */
    public void onQueryRechrQuota(RechrQuotaQueryReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsTopUp.MODULE_QUERY_RECHARGE_QUOTA, request, listener)).execute();
    }

    /**
     * 充值结果查询
     *
     * @param request
     * @param listener
     */
    public void onRechrResultQuery(RechrResultQueryReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsTopUp.MODULE_RECHARGE_RESULT_QUERY, request, listener)).execute();
    }

}
