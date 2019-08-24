package com.ahdi.wallet.cashier.main;

import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.wallet.cashier.requset.pay.PayOrderRequest;
import com.ahdi.wallet.cashier.requset.pay.PayResultQueryRequest;
import com.ahdi.wallet.cashier.requset.pay.PricingRequest;
import com.ahdi.wallet.cashier.requset.pay.RePricingRequest;
import com.ahdi.wallet.cashier.requset.rechr.RechrListQueryReq;
import com.ahdi.wallet.cashier.requset.rechr.RechrOrderReq;
import com.ahdi.wallet.cashier.requset.rechr.RechrPayResultQueryReq;
import com.ahdi.wallet.cashier.util.ConstantsCashier;
import com.ahdi.wallet.network.HttpReqAsyncTask;

public class HttpReqCashier {

    private static HttpReqCashier instance;

    private HttpReqCashier() {

    }

    public synchronized static HttpReqCashier getInstance() {
        if (instance == null) {
            instance = new HttpReqCashier();
        }
        return instance;
    }


    /**
     * 批价
     *
     * @param request
     * @param listener
     */
    public void onPricing(PricingRequest request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsCashier.MODULE_PAY_PRICING, request, listener)).execute();
    }

    /**
     * 重新批价
     *
     * @param request
     * @param listener
     */
    public void onRePricing(RePricingRequest request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsCashier.MODULE_REPAY_CODE, request, listener)).execute();
    }

    /**
     * 支付下单
     *
     * @param request
     * @param listener
     */
    public void onPayOrder(PayOrderRequest request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsCashier.MODULE_PAY_ORDER, request, listener)).execute();
    }

    /**
     * 支付结果查询
     *
     * @param request
     * @param listener
     */
    public void onPayResultQuery(PayResultQueryRequest request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsCashier.MODULE_PAY_RESULT_QUERY, request, listener)).execute();
    }

    /**
     * 查询充值方式
     *
     * @param request
     * @param listener
     */
    public void onQueryRechargeList(RechrListQueryReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsCashier.MODULE_QUERY_RECHARGE_LIST, request, listener)).execute();
    }

    /**
     * 充值下单
     *
     * @param request
     * @param listener
     */
    public void onRechargeOrder(RechrOrderReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsCashier.MODULE_RECHARGE_ORDER, request, listener)).execute();
    }


    /**
     * 充值支付结果查询
     *
     * @param request
     * @param listener
     */
    public void onRechargePayResultQuery(RechrPayResultQueryReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsCashier.MODULE_QUERY_RECHARGE_PAY_RESULT, request, listener)).execute();
    }

}
