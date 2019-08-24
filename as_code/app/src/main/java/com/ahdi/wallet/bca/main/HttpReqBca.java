package com.ahdi.wallet.bca.main;

import android.os.AsyncTask;

import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.wallet.bca.requset.BcaOTPReq;
import com.ahdi.wallet.bca.requset.BcaOrderReq;
import com.ahdi.wallet.bca.requset.BcaResultNotifyReq;
import com.ahdi.wallet.bca.util.ConstantsBca;
import com.ahdi.wallet.network.HttpReqAsyncTask;

public class HttpReqBca {

    private static HttpReqBca instance;

    private HttpReqBca() {

    }

    public synchronized static HttpReqBca getInstance() {
        if (instance == null) {
            instance = new HttpReqBca();
        }
        return instance;
    }


    /**
     * BCA绑卡和修改限额结果通知
     *
     * @param request
     * @param listener
     */
    public void onBCAResultNotify(BcaResultNotifyReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsBca.MODULE_BCA_RESULT_NOTIFY, request, listener)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * bca OTP
     *
     * @param request
     * @param listener
     */
    public void onBcaOTP(BcaOTPReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsBca.MODULE_BCA_OTP, request, listener)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * OTP 下单
     *
     * @param request
     * @param listener
     */
    public void onOTPOrder(BcaOrderReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsBca.MODULE_BCA_OTP_ORDER, request, listener)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}
