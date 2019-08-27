package com.ahdi.wallet.cashier.listener.pay;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.ahdi.wallet.R;
import com.ahdi.wallet.cashier.PayCashierSdk;
import com.ahdi.wallet.cashier.callback.PaymentSdkCallBack;
import com.ahdi.wallet.cashier.main.PayCashierMain;
import com.ahdi.wallet.cashier.response.pay.PayResultQueryResponse;
import com.ahdi.wallet.app.utils.ConstantsPayment;
import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.widgets.dialog.CommonDialog;

import org.json.JSONObject;

/**
 * Date: 2018/1/5 下午5:09
 * Author: kay lau
 * Description:
 */
public class PayResultQueryListener implements HttpReqTaskListener {

    private static final String TAG = PayResultQueryListener.class.getSimpleName();

    private static final int MSG_CONTINUE = 0;
    private static final int queryIntervalTime = 5 * 1000;  // 查询间隔时间
    private volatile int queryCount = 2;                    // 失败之后再查询的次数
    private Context context;
    private String OT;
    private String TT;
    private PaymentSdkCallBack callBack;
    private Handler handler;


    public PayResultQueryListener(Context context, String OT, String TT, PaymentSdkCallBack callBack) {
        this.context = context;
        this.OT = OT;
        this.TT = TT;
        this.callBack = callBack;
    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        PayResultQueryResponse resp = PayResultQueryResponse.decodeJson(PayResultQueryResponse.class, json);
        if (resp != null && TextUtils.equals(resp.getmHeader().retCode, PayCashierSdk.LOCAL_PAY_SUCCESS)) {
            if (TextUtils.equals(resp.payResult, ConstantsPayment.PAY_OK)) {
                onCallback(resp.getmHeader().retCode, resp.getmHeader().retMsg, json);

            } else if (TextUtils.equals(resp.payResult, ConstantsPayment.PAY_FAIL)
                    || TextUtils.equals(resp.payResult, ConstantsPayment.PLAT_FAIL)) {
                onCallback(resp.reason, resp.err, json);

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

    private void onCallback(String retCode, String errMsg, JSONObject json) {
        if (callBack != null) {
            callBack.onResult(retCode, errMsg, json);
        } else {
            LogUtil.e(TAG, "PayResultQueryListener CallBack is null");
        }
    }

    private void onContinueQuery(String errMsg) {
        if (queryCount > 0) {
            queryCount--;
            if (handler == null) {
                handler = new QueryHandler();
            }
            handler.sendEmptyMessageDelayed(MSG_CONTINUE, queryIntervalTime);
        } else {
            onDestroyHandler();
            showDialog(errMsg);
        }
    }

    private void loopQueryResult(String OT, String TT) {
        PayCashierMain.getInstance().payResultQuery(context, OT, TT, callBack);
    }

    private class QueryHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_CONTINUE) {
                loopQueryResult(OT, TT);
            }
        }
    }

    private void showDialog(String msg) {
        new CommonDialog
                .Builder(context)
                .setMessage(msg)
                .setMessageCenter(true)
                .setCancelable(false)
                .setNegativeButton(context.getResources().getString(R.string.DialogButton_A0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        onCallback(PayCashierSdk.LOCAL_PAY_QUERY_CANCEL, "", null);
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(context.getResources().getString(R.string.DialogButton_C0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        queryCount = 2;
                        loopQueryResult(OT, TT);
                        dialog.dismiss();
                    }
                }).show();
    }

    private void onDestroyHandler() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }
}
