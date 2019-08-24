package com.ahdi.wallet.module.QRCode.main;

import android.os.AsyncTask;

import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.wallet.module.QRCode.requset.CollectQRCodeReq;
import com.ahdi.wallet.module.QRCode.requset.PayCodeInitReq;
import com.ahdi.wallet.module.QRCode.requset.PayCodePINConfirmReq;
import com.ahdi.wallet.module.QRCode.requset.PayCodeUseQueryReq;
import com.ahdi.wallet.module.QRCode.util.ConstantsQRCode;
import com.ahdi.wallet.network.HttpReqAsyncTask;

public class HttpReqQRCode {

    private static HttpReqQRCode instance;

    private HttpReqQRCode() {

    }

    public synchronized static HttpReqQRCode getInstance() {
        if (instance == null) {
            instance = new HttpReqQRCode();
        }
        return instance;
    }

    /**
     * 支付授权码初始化
     *
     * @param request
     * @param listener
     */
    public void onInitPayCode(PayCodeInitReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsQRCode.MODULE_PAY_CODE_INIT, request, listener)).execute();
    }

    /**
     * 支付授权码使用查询
     *
     * @param request
     * @param listener
     */
    public void onPayAuthCodeUseQuery(PayCodeUseQueryReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsQRCode.MODULE_PAY_AUTH_CODE_QR, request, listener)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 支付授权码确认/取消
     *
     * @param request
     * @param listener
     */
    public void onPayAuthCodeConfirm(PayCodePINConfirmReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsQRCode.MODULE_PAY_AUTH_CODE_CONFIRM, request, listener)).execute();
    }


    /**
     * 收款二维码
     *
     * @param request
     * @param listener
     */
    public void onCollectQR(CollectQRCodeReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsQRCode.MODULE_COLLECT_QR, request, listener)).execute();
    }

}
