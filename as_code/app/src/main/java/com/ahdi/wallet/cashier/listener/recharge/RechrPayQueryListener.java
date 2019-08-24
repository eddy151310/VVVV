package com.ahdi.wallet.cashier.listener.recharge;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.ahdi.wallet.R;
import com.ahdi.wallet.cashier.RechrCashierSdk;
import com.ahdi.wallet.cashier.main.RechrCashierMain;
import com.ahdi.wallet.cashier.callback.RechrCallBack;
import com.ahdi.wallet.cashier.response.rechr.RechrPayResultRsp;
import com.ahdi.wallet.app.utils.ConstantsPayment;
import com.ahdi.lib.utils.widgets.dialog.CommonDialog;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;

/**
 * 内部: 查询充值支付结果
 */
public class RechrPayQueryListener implements HttpReqTaskListener {

    private static final String TAG = RechrPayQueryListener.class.getSimpleName();

    private static final int MSG_CONTINUE = 0;
    private static final int queryIntervalTime = 5 * 1000;  // 查询间隔时间
    private volatile int queryCount = 2;                    // 失败之后再查询的次数
    private RechrCallBack callBack;
    private String OT;
    private Context context;
    private Handler handler;
    private String orderID;

    public RechrPayQueryListener(Context context, String OT, String orderID, RechrCallBack callBack) {
        this.context = context;
        this.OT = OT;
        this.callBack = callBack;
        this.orderID = orderID;
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        RechrPayResultRsp resp = RechrPayResultRsp.decodeJson(RechrPayResultRsp.class, json);
        if (resp != null && TextUtils.equals(resp.getmHeader().RetCode, RechrCashierSdk.LOCAL_PAY_SUCCESS)) {
            if (TextUtils.equals(resp.getRechargePayResult(), ConstantsPayment.PAY_OK)) {
                onCallBack(resp.getmHeader().RetCode, resp.getmHeader().ErrMsg, json);

            } else if (TextUtils.equals(resp.getRechargePayResult(), ConstantsPayment.PAY_FAIL)
                    || TextUtils.equals(resp.getRechargePayResult(), ConstantsPayment.PLAT_FAIL)) {
                onCallBack(resp.reason, resp.err, json);

            } else {
                onContinueQuery(context.getString(R.string.DialogMsg_E0));
            }
        } else {
            LogUtil.d(TAG, TAG + "解析之后为空");
            onContinueQuery(context.getString(R.string.DialogMsg_D0));
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? TAG + "onError(JSONObject e)" : json.toString());
        onContinueQuery(context.getString(R.string.DialogMsg_D0));
    }

    private void onCallBack(String code, String errorMsg, JSONObject jsonObject) {
        if (callBack != null) {
            callBack.onResult(code, errorMsg, jsonObject, OT, orderID);
        } else {
            LogUtil.e(TAG, "RechrPayQueryListener CallBack = null");
        }
    }

    private void onContinueQuery(String errMsg) {
        if (queryCount > 0) {
            queryCount--;
            if (handler == null) {
                handler = new RechargeHandler();
            }
            handler.sendEmptyMessageDelayed(MSG_CONTINUE, queryIntervalTime);
        } else {
            onDestroyHandler();
            showDialog(errMsg);
        }
    }

    private void loopQueryResult(String ot) {
        RechrCashierMain.getInstance().rechargePayResultQuery(context, ot, orderID, callBack);
    }

    private class RechargeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_CONTINUE) {
                loopQueryResult(OT);
            }
        }
    }

    private void showDialog(String errMsg) {
        new CommonDialog
                .Builder(context)
                .setMessage(errMsg)
                .setMessageCenter(true)
                .setCancelable(false)
                .setNegativeButton(context.getString(R.string.DialogButton_A0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onCallBack(RechrCashierSdk.LOCAL_PAY_QUERY_CANCEL, errMsg, null);
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(context.getString(R.string.DialogButton_C0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        queryCount = 2;
                        loopQueryResult(OT);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void onDestroyHandler() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }
}
