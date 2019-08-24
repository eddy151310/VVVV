package com.ahdi.wallet.module.payment.transfer.listener;


import android.text.TextUtils;

import com.ahdi.wallet.cashier.PayCashierSdk;
import com.ahdi.wallet.cashier.callback.PaymentSdkCallBack;
import com.ahdi.wallet.module.payment.transfer.TransferSdk;
import com.ahdi.wallet.module.payment.withdraw.WithDrawSdk;
import com.ahdi.wallet.module.payment.transfer.main.TransferMain;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.wallet.module.payment.withdraw.main.WithDrawMain;

import org.json.JSONObject;

/**
 * @author admin
 * @date Created by xiaoniu on 2018/1/9.
 * @email zhao_zhaohe@163.com
 */

public class PayListener implements PaymentSdkCallBack {

    private String id;
    private int from;

    public PayListener(String id, int from) {
        this.id = id;
        this.from = from;
    }

    @Override
    public void onResult(String code, String errorMsg, JSONObject jsonObject) {
        if (from == Constants.LOCAL_FROM_PAY) {
            onTransferCallback(code, errorMsg, jsonObject);
        } else if (from == Constants.LOCAL_FROM_WITHDRAW) {
            onWithDrawMainCallback(code, errorMsg, jsonObject);
        }
    }

    private void onWithDrawMainCallback(String code, String errorMsg, JSONObject jsonObject) {
        if (TextUtils.equals(code, PayCashierSdk.LOCAL_PAY_SUCCESS)) {
            code = WithDrawSdk.LOCAL_PAY_SUCCESS;

        } else if (TextUtils.equals(code, PayCashierSdk.LOCAL_PAY_PARAM_ERROR)) {
            code = WithDrawSdk.LOCAL_PAY_PARAM_ERROR;

        } else if (TextUtils.equals(code, PayCashierSdk.LOCAL_PAY_USER_CANCEL)) {
            code = WithDrawSdk.LOCAL_PAY_USER_CANCEL;

        } else if (TextUtils.equals(code, PayCashierSdk.LOCAL_PAY_NETWORK_EXCEPTION)) {
            code = WithDrawSdk.LOCAL_PAY_NETWORK_EXCEPTION;

        } else if (TextUtils.equals(code, PayCashierSdk.LOCAL_PAY_SYSTEM_EXCEPTION)) {
            code = WithDrawSdk.LOCAL_PAY_SYSTEM_EXCEPTION;

        } else if (TextUtils.equals(code, PayCashierSdk.LOCAL_PAY_QUERY_CANCEL)) {
            code = WithDrawSdk.LOCAL_PAY_QUERY_CANCEL;

        }
        WithDrawMain.getInstance().onResultBack(code, errorMsg, jsonObject, id);
    }

    private void onTransferCallback(String code, String errorMsg, JSONObject jsonObject) {
        if (TextUtils.equals(code, PayCashierSdk.LOCAL_PAY_SUCCESS)) {
            code = TransferSdk.LOCAL_PAY_SUCCESS;

        } else if (TextUtils.equals(code, PayCashierSdk.LOCAL_PAY_PARAM_ERROR)) {
            code = TransferSdk.LOCAL_PAY_PARAM_ERROR;

        } else if (TextUtils.equals(code, PayCashierSdk.LOCAL_PAY_USER_CANCEL)) {
            code = TransferSdk.LOCAL_PAY_USER_CANCEL;

        } else if (TextUtils.equals(code, PayCashierSdk.LOCAL_PAY_NETWORK_EXCEPTION)) {
            code = TransferSdk.LOCAL_PAY_NETWORK_EXCEPTION;

        } else if (TextUtils.equals(code, PayCashierSdk.LOCAL_PAY_SYSTEM_EXCEPTION)) {
            code = TransferSdk.LOCAL_PAY_SYSTEM_EXCEPTION;

        } else if (TextUtils.equals(code, PayCashierSdk.LOCAL_PAY_QUERY_CANCEL)) {
            code = TransferSdk.LOCAL_PAY_QUERY_CANCEL;

        }
        TransferMain.getInstance().onResultBack(code, errorMsg, jsonObject, id);
    }
}
