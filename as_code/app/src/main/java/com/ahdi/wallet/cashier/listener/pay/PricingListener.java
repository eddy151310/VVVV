package com.ahdi.wallet.cashier.listener.pay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ahdi.wallet.R;
import com.ahdi.wallet.cashier.PayCashierSdk;
import com.ahdi.wallet.app.main.CashierPricing;
import com.ahdi.wallet.cashier.main.PayCashierMain;
import com.ahdi.wallet.cashier.response.pay.PricingResponse;
import com.ahdi.wallet.cashier.schemas.TransSchema;
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
public class PricingListener implements HttpReqTaskListener {

    private static final String TAG = PricingListener.class.getSimpleName();

    private Context context;
    private int from;

    public PricingListener(Context context, int from) {
        this.context = context;
        this.from = from;
    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        PricingResponse resp = PricingResponse.decodeJson(PricingResponse.class, json);
        if (resp != null) {
            if (TextUtils.equals(PayCashierSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().retCode)) {
                CashierPricing.getInstance(context).notifyPricingResponse(resp);
                openPayHubActivity(resp);
            } else {
                PayCashierMain.getInstance().onResultBack(resp.getmHeader().retCode, resp.getmHeader().retMsg, json);
            }
        } else {
            PayCashierMain.getInstance().onResultBack(PayCashierSdk.LOCAL_PAY_SYSTEM_EXCEPTION, PayCashierMain.getInstance().default_error, null);
            LogUtil.d(TAG, TAG + "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? TAG + "onError(JSONObject e)" : json.toString());
        PayCashierMain.getInstance().onResultBack(PayCashierSdk.LOCAL_PAY_NETWORK_EXCEPTION, "", json);
    }

    /**
     * 打开收银台
     *
     * @param resp
     */
    private void openPayHubActivity(PricingResponse resp) {
        if (context != null && resp != null) {
            Intent intent = new Intent(context, PayHubActivity.class);
            TransSchema transSchema = resp.getTransSchema();
            if (transSchema != null) {
                if (from == Constants.LOCAL_FROM_WITHDRAW) {
                    intent.putExtra("withdrawTransPrice", transSchema.TransPrice);
                    intent.putExtra(Constants.LOCAL_FEE_KEY, transSchema.Fee);
                }
                intent.putExtra(Constants.LOCAL_PRICE_KEY, transSchema.Price);
                intent.putExtra(Constants.LOCAL_GNAME_KEY, transSchema.GName);
            }
            intent.putExtra("TouchFlag", resp.touchFlag);
            intent.putExtra(Constants.LOCAL_FROM_KEY, from);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.bottom_in, 0);
        } else {
            LogUtil.e(TAG, "批价成功之后, 打开收银台界面 context 为空 || resp = null");
        }
    }
}
