package com.ahdi.wallet.cashier.main;

import android.content.Context;
import android.text.TextUtils;

import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.lib.utils.utils.TerminalIdUtil;
import com.ahdi.lib.utils.utils.TouchIDStateUtil;
import com.ahdi.wallet.R;
import com.ahdi.wallet.cashier.bean.PayOrderBean;
import com.ahdi.wallet.app.bean.QueryRechargeListBean;
import com.ahdi.wallet.cashier.bean.ReportDataBean;
import com.ahdi.wallet.app.main.OtherSdkMain;
import com.ahdi.wallet.app.utils.ConstantsPayment;
import com.ahdi.wallet.cashier.callback.RechrCallBack;
import com.ahdi.wallet.cashier.listener.recharge.RechrListQueryListener;
import com.ahdi.wallet.cashier.listener.recharge.RechrOrderListener;
import com.ahdi.wallet.cashier.listener.recharge.RechrPayQueryListener;
import com.ahdi.wallet.cashier.requset.rechr.RechrListQueryReq;
import com.ahdi.wallet.cashier.requset.rechr.RechrOrderReq;
import com.ahdi.wallet.cashier.requset.rechr.RechrPayResultQueryReq;
import com.ahdi.wallet.module.payment.topup.callback.TopUpCallBack;
import com.ahdi.wallet.app.schemas.TerminalInfoSchema;

import org.json.JSONObject;

/**
 * Date: 2019/6/24 下午3:28
 * Author: kay lau
 * Description:
 */
public class RechrCashierMain {

    /**
     * 重新执行充值流程
     */
    public interface OnReTopUpCallback {

        void onCallback(String retCode, String errMsg, JSONObject json);
    }

    private static RechrCashierMain instance;

    /**
     * 对外的结果回调
     */
    public TopUpCallBack callBack;
    /**
     * 默认的错误原因
     */
    public String default_error = "";
    public String sid;

    private RechrPayQueryListener rechargePayQueryListener = null;

    private RechrCashierMain() {
    }

    public static RechrCashierMain getInstance() {
        if (instance == null) {
            synchronized (PayCashierMain.class) {
                if (instance == null) {
                    instance = new RechrCashierMain();
                }
            }
        }
        return instance;
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
     * 充值流程: 查询充值方式--->充值下单--->充值结果查询
     *
     * @param context
     * @param rechr
     * @param sid
     */
    public void recharge(Context context, String rechr, String sid) {
        initDefaultError(context);
        // 查询充值方式--->充值下单--->充值结果查询
        onQueryRechrMode(context, rechr, sid);
    }

    /**
     * 充值批价(查询充值方式)
     *
     * @param context
     * @param rechr
     * @param sid
     */
    private void onQueryRechrMode(Context context, String rechr, String sid) {
        QueryRechargeListBean bean = new QueryRechargeListBean();
        bean.setRechr(rechr);
        bean.setTerminalInfoSchema(new TerminalInfoSchema());
        // 查询充值方式
        queryRechargeMode(context, bean, sid, false, null);
    }


    /**
     * 重新充值批价(查询充值方式)
     *
     * @param context
     * @param rechr
     * @param isAgain  是否重新批价
     * @param callBack
     */
    public void onReQueryRechrMode(Context context, String rechr, boolean isAgain, OnReTopUpCallback callBack) {
        QueryRechargeListBean bean = new QueryRechargeListBean();
        bean.setRechr(rechr);
        bean.setTerminalInfoSchema(new TerminalInfoSchema());
        // 查询充值方式
        queryRechargeMode(context, bean, sid, isAgain, callBack);
    }

    /**
     * 查询充值方式
     *
     * @param context
     * @param bean
     * @param sid
     * @param isAgain  是否重新批价: true为重新批价 默认为false
     * @param callBack
     */
    private void queryRechargeMode(Context context, QueryRechargeListBean bean, String sid, boolean isAgain, OnReTopUpCallback callBack) {
        initDefaultError(context);
        this.sid = sid;
        TerminalInfoSchema schema = bean.getTerminalInfoSchema();
        String rechr = bean.getRechr();
        String lName = AppGlobalUtil.getInstance().getLName(context);
        String touchVer = TouchIDStateUtil.getTouchIDPayVer(context, lName);
        RechrListQueryReq request = new RechrListQueryReq(rechr, schema, touchVer, sid);
        RechrListQueryListener listener = new RechrListQueryListener(context, bean.getRechr(), isAgain, callBack);
        HttpReqCashier.getInstance().onQueryRechargeList(request, listener);
    }

    /**
     * 充值下单
     *
     * @param context
     * @param orderBean
     * @param callBack
     */
    public void rechargeOrder(Context context, PayOrderBean orderBean, RechrCallBack callBack) {
        initDefaultError(context);
        String lName = AppGlobalUtil.getInstance().getLName(context);
        String touchVer = TouchIDStateUtil.getTouchIDPayVer(context, lName);
        RechrOrderReq request = new RechrOrderReq(orderBean, touchVer, sid);
        HttpReqCashier.getInstance().onRechargeOrder(request, new RechrOrderListener(context, orderBean.getIv(), callBack));
    }

    /**
     * 内部：充值支付结果查询
     *
     * @param context
     * @param ot
     * @param orderID
     * @param callBack
     */
    public void rechargePayResultQuery(Context context, String ot, String orderID, RechrCallBack callBack) {
        if (rechargePayQueryListener == null) {
            rechargePayQueryListener = new RechrPayQueryListener(context, ot, orderID, callBack);
        }
        HttpReqCashier.getInstance().onRechargePayResultQuery(new RechrPayResultQueryReq(ot, sid), rechargePayQueryListener);
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
     * 统一对外回调, 支付上报
     *
     * @param code       “0”成功 其他失败
     * @param errorMsg   错误描述
     * @param jsonObject 响应成功时的json数据
     * @param payResult  支付上报的PayResult
     */
    public void onResultBack(String code, String errorMsg, JSONObject jsonObject, String OT, String payResult, String orderID) {
        if (TextUtils.isEmpty(errorMsg)) {
            errorMsg = jsonObject == null ? "" : jsonObject.optString(Constants.MSG_KEY);
        }
        if (!TextUtils.isEmpty(payResult)) {
            payReport(payResult, orderID);
        }
        onResultBack(code, errorMsg, jsonObject, OT);
    }

    /**
     * 支付上报
     *
     * @param payResult
     * @param orderID
     */
    private void payReport(String payResult, String orderID) {
        ReportDataBean reportData = new ReportDataBean();
        reportData.setUID(ProfileUserUtil.getInstance().getUserID());
        reportData.setTID(TerminalIdUtil.getTerminalID());
        reportData.setOrderID(orderID);
        reportData.setPayResult(payResult);
        OtherSdkMain.getInstance().payReport(ConstantsPayment.REPORT_TYPE_EXIT, reportData, sid);
    }

    /**
     * 清理
     */
    public void onDestroy() {
        callBack = null;
        instance = null;
    }
}
