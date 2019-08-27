package com.ahdi.wallet.cashier.listener.recharge;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ahdi.wallet.R;
import com.ahdi.wallet.cashier.RechrCashierSdk;
import com.ahdi.wallet.cashier.main.RechrCashierMain;
import com.ahdi.wallet.app.main.CashierPricing;
import com.ahdi.wallet.cashier.response.rechr.RechrListQueryRsp;
import com.ahdi.wallet.cashier.ui.activities.PayHubActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;

/**
 * Date: 2018/1/5 下午5:09
 * Author: kay lau
 * Description:
 */
public class RechrListQueryListener implements HttpReqTaskListener {

    private static final String TAG = RechrListQueryListener.class.getSimpleName();

    private Context context;
    private String rechrEdtAmount;
    private boolean isAgain;
    private RechrCashierMain.OnReTopUpCallback callBack;

    public RechrListQueryListener(Context context, String rechr, boolean isAgain, RechrCashierMain.OnReTopUpCallback callBack) {
        this.context = context;
        this.rechrEdtAmount = rechr;
        this.isAgain = isAgain;
        this.callBack = callBack;
    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        RechrListQueryRsp resp = RechrListQueryRsp.decodeJson(RechrListQueryRsp.class, json);
        if (resp != null) {
            if (TextUtils.equals(RechrCashierSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().retCode)) {
                CashierPricing.getInstance(context).notifyQueryChargeListResponse(resp);
                // 打开收银台界面
                if (!isAgain) {
                    if (resp.getPayTypeSchemas() != null) {
                        showEnterPwdActivity(resp);
                    } else {
                        LogUtil.e(TAG, "resp.getPayTypeSchemas() = null");
                    }
                } else {
                    // 重新批价 刷新充值收银台数据
                    if (callBack != null) {
                        callBack.onCallback(resp.getmHeader().retCode, resp.getmHeader().retMsg, json);
                    }
                }
            } else {
                RechrCashierMain.getInstance().onResultBack(resp.getmHeader().retCode, resp.getmHeader().retMsg, json, "");
                if (isAgain) {
                    ((Activity) context).finish();
                }
            }
        } else {
            RechrCashierMain.getInstance().onResultBack(RechrCashierSdk.LOCAL_PAY_SYSTEM_EXCEPTION, resp.getmHeader().retMsg, json, "");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? TAG + "onError(JSONObject e)" : json.toString());
        RechrCashierMain.getInstance().onResultBack(RechrCashierSdk.LOCAL_PAY_NETWORK_EXCEPTION, "", json, "");
        if (isAgain) {
            ((Activity) context).finish();
        }
    }

    private void showEnterPwdActivity(RechrListQueryRsp resp) {
        if (context != null) {
            Intent intent = new Intent(context, PayHubActivity.class);
            intent.putExtra(Constants.LOCAL_FROM_KEY, Constants.LOCAL_FROM_TOP_UP);
            if (resp != null) {
                intent.putExtra("TouchFlag", resp.TouchFlag);
                intent.putExtra(Constants.LOCAL_SUBJECT_KEY, resp.subject);
                intent.putExtra("rechrEdtAmountKey", rechrEdtAmount);
                intent.putExtra("rechrAmountKey", resp.amount);
            }
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.bottom_in, 0);
        } else {
            LogUtil.e(TAG, "获取充值列表成功之后, 打开收银台界面 context 为空");
        }
    }
}
