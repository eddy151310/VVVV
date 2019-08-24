package com.ahdi.wallet.module.payment.topup.main;

import android.content.Context;
import android.text.TextUtils;

import com.ahdi.lib.utils.config.Constants;
import com.ahdi.wallet.R;
import com.ahdi.wallet.cashier.main.PayCashierMain;
import com.ahdi.wallet.module.payment.topup.callback.TopUpCallBack;
import com.ahdi.wallet.module.payment.topup.listener.RechrQuotaQueryListener;
import com.ahdi.wallet.module.payment.topup.listener.RechrResultQueryListener;
import com.ahdi.wallet.module.payment.topup.listener.RechrTypeQueryListener;
import com.ahdi.wallet.module.payment.topup.requset.RechrQuotaQueryReq;
import com.ahdi.wallet.module.payment.topup.requset.RechrResultQueryReq;
import com.ahdi.wallet.module.payment.topup.requset.RechrTypeQueryReq;

import org.json.JSONObject;

/**
 * Date: 2018/4/25 上午11:11
 * Author: kay lau
 * Description:
 */
public class TopUpMain {

    private static TopUpMain instance;

    /**
     * 对外的结果回调
     */
    public TopUpCallBack callBack;
    /**
     * 默认的错误原因
     */
    public String default_error = "";
    public String sid;


    private TopUpMain() {
    }

    public static TopUpMain getInstance() {
        if (instance == null) {
            synchronized (PayCashierMain.class) {
                if (instance == null) {
                    instance = new TopUpMain();
                }
            }
        }
        return instance;
    }

    /**
     * 充值类型查询
     *
     * @param context
     * @param sid
     */
    public void rechrTypeQuery(Context context, String sid) {
        initDefaultError(context);
        HttpReqTopUp.getInstance().onRechrTypeQuery(new RechrTypeQueryReq(sid), new RechrTypeQueryListener());
    }

    /**
     * 查询充值额度---充值
     *
     * @param context
     * @param sid
     */
    public void queryRechargeQuota(Context context, String sid) {
        initDefaultError(context);
        HttpReqTopUp.getInstance().onQueryRechrQuota(new RechrQuotaQueryReq(sid), new RechrQuotaQueryListener());
    }


    /**
     * 对外提供：充值结果查询
     *
     * @param context
     * @param ot
     * @param sid
     */
    public void rechargeResultQuery(Context context, String ot, String sid) {
        initDefaultError(context);
        HttpReqTopUp.getInstance().onRechrResultQuery(new RechrResultQueryReq(ot, sid), new RechrResultQueryListener());
    }

    /**
     * 初始化默认的错误原因
     *
     * @param context
     */
    private void initDefaultError(Context context) {
        if (context != null) {
            default_error = context.getString(R.string.LocalError_C0);
        }
    }

    /**
     * 统一对外回调
     *
     * @param code       “0”成功 其他失败
     * @param errorMsg   错误描述
     * @param jsonObject 响应成功时的json数据
     * @param OT
     */
    public void onResultBack(String code, String errorMsg, JSONObject jsonObject, String OT) {
        if (TextUtils.isEmpty(errorMsg)) {
            errorMsg = jsonObject == null ? "" : jsonObject.optString(Constants.MSG_KEY);
        }
        if (callBack != null) {
            callBack.onResult(code, errorMsg, jsonObject, OT);
            onDestroy();
        }
    }

    /**
     * 清理
     */
    public void onDestroy() {
        callBack = null;
        instance = null;
    }
}
