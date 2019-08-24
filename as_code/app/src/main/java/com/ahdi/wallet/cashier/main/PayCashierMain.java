package com.ahdi.wallet.cashier.main;

import android.content.Context;
import android.text.TextUtils;

import com.ahdi.wallet.R;
import com.ahdi.wallet.cashier.bean.PayOrderBean;
import com.ahdi.wallet.cashier.bean.PricingBean;
import com.ahdi.wallet.cashier.bean.ReportDataBean;
import com.ahdi.wallet.cashier.callback.PaymentSdkCallBack;
import com.ahdi.wallet.app.main.CashierPricing;
import com.ahdi.wallet.app.main.OtherSdkMain;
import com.ahdi.wallet.cashier.listener.pay.PayOrderListener;
import com.ahdi.wallet.module.QRCode.listener.PayCodePayResultQueryListener;
import com.ahdi.wallet.cashier.listener.pay.PayResultQueryListener;
import com.ahdi.wallet.cashier.listener.pay.PricingListener;
import com.ahdi.wallet.cashier.listener.pay.RePricingListener;
import com.ahdi.wallet.cashier.requset.pay.PayOrderRequest;
import com.ahdi.wallet.cashier.requset.pay.PayResultQueryRequest;
import com.ahdi.wallet.cashier.requset.pay.PricingRequest;
import com.ahdi.wallet.cashier.requset.pay.RePricingRequest;
import com.ahdi.wallet.app.schemas.LoginSchema;
import com.ahdi.wallet.app.schemas.TerminalInfoSchema;
import com.ahdi.wallet.cashier.schemas.TransSchema;
import com.ahdi.wallet.app.utils.ConstantsPayment;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.lib.utils.utils.TerminalIdUtil;
import com.ahdi.lib.utils.utils.TouchIDStateUtil;

import org.json.JSONObject;

/**
 * Date: 2018/1/4 下午3:16
 * Author: kay lau
 * Description:
 */

public class PayCashierMain {

    private static PayCashierMain instance;

    /**
     * 对外的结果回调
     */
    public PaymentSdkCallBack callBack;
    /**
     * 默认的错误原因
     */
    public String default_error = "";
    public String sid;
    private PayResultQueryListener payResultQueryListener;
    private PayCodePayResultQueryListener payQRResultQueryListener;

    private PayCashierMain() {
    }

    public static PayCashierMain getInstance() {
        if (instance == null) {
            synchronized (PayCashierMain.class) {
                if (instance == null) {
                    instance = new PayCashierMain();
                }
            }
        }
        return instance;
    }

    /**
     * 支付流程: 批价--->支付下单--->支付结果查询
     *
     * @param context
     * @param params
     * @param sid
     */
    public void startPay(Context context, String params, String sid, int from) {
        initDefaultError(context);
        this.sid = sid;
        // 批价--->支付下单--->支付结果查询
        PricingBean pricingBean = new PricingBean();
        pricingBean.setCPOrder(params);
        pricingBean.setTerminalInfoSchema(new TerminalInfoSchema());
        String lName = AppGlobalUtil.getInstance().getLName(context);
        String touchVer = TouchIDStateUtil.getTouchIDPayVer(context, lName);
        // 批价
        pricing(context, pricingBean, from, touchVer);
    }

    /**
     * 批价无可用支付方式进入选择支付方式界面, 绑卡成功重新发起支付.
     *
     * @param context
     * @param TT
     * @param callBack
     */
    public void restartPay(Context context, String TT, PaymentSdkCallBack callBack) {
        initDefaultError(context);
        String lName = AppGlobalUtil.getInstance().getLName(context);
        String touchVer = TouchIDStateUtil.getTouchIDPayVer(context, lName);
        repricing(context, TT, touchVer, callBack);
    }

    /**
     * 批价
     *
     * @param context
     * @param bean
     * @param touchVer
     */
    private void pricing(Context context, PricingBean bean, int from, String touchVer) {
        initDefaultError(context);
        String cfgVersion = bean.getCfgVersion();
        String cpOrder = bean.getCPOrder();
        LoginSchema loginSchema = bean.getLoginSchema();
        TerminalInfoSchema terminalInfoSchema = bean.getTerminalInfoSchema();
        PricingRequest request = new PricingRequest(cpOrder, cfgVersion, terminalInfoSchema, loginSchema, touchVer, sid);
        HttpReqCashier.getInstance().onPricing(request, new PricingListener(context, from));
    }

    /**
     * 重新批价
     *
     * @param context
     * @param TT
     * @param touchVer
     * @param callBack
     */
    private void repricing(Context context, String TT, String touchVer, PaymentSdkCallBack callBack) {
        initDefaultError(context);
        RePricingRequest request = new RePricingRequest(TT, touchVer, sid);
        HttpReqCashier.getInstance().onRePricing(request, new RePricingListener(callBack));
    }

    /**
     * 支付下单
     *
     * @param context
     * @param bean
     * @param callBack
     */
    public void payOrder(Context context, PayOrderBean bean, PaymentSdkCallBack callBack) {
        initDefaultError(context);
        String touchVer = TouchIDStateUtil.getTouchIDPayVer(context, AppGlobalUtil.getInstance().getLName(context));
        PayOrderRequest request = new PayOrderRequest(bean, touchVer, sid);
        HttpReqCashier.getInstance().onPayOrder(request, new PayOrderListener(context, bean.getIv(), callBack));
    }

    /**
     * 对外提供：支付二维码使用后, 查询支付结果
     *
     * @param context
     * @param tt
     * @param ot
     * @param sid
     */
    public void payQRResultQuery(Context context, String tt, String ot, String sid) {
        initDefaultError(context);
        if (payQRResultQueryListener == null) {
            payQRResultQueryListener = new PayCodePayResultQueryListener();
        }
        HttpReqCashier.getInstance().onPayResultQuery(new PayResultQueryRequest(ot, tt, sid), payQRResultQueryListener);
    }


    /**
     * 支付系统内部: 支付结果查询
     *
     * @param context
     * @param ot
     * @param tt
     * @param callBack
     */
    public void payResultQuery(Context context, String ot, String tt, PaymentSdkCallBack callBack) {
        initDefaultError(context);
        if (payResultQueryListener == null) {
            payResultQueryListener = new PayResultQueryListener(context, ot, tt, callBack);
        }
        HttpReqCashier.getInstance().onPayResultQuery(new PayResultQueryRequest(ot, tt, sid), payResultQueryListener);
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
     */
    public void onResultBack(String code, String errorMsg, JSONObject jsonObject) {
        if (TextUtils.isEmpty(errorMsg)) {
            errorMsg = jsonObject == null ? "" : jsonObject.optString(Constants.MSG_KEY);
        }
        if (callBack != null) {
            callBack.onResult(code, errorMsg, jsonObject);
            onDestroy();
        }
    }

    /**
     * 统一对外回调, 支付上报
     *
     * @param context
     * @param code       “0”成功 其他失败
     * @param errorMsg   错误描述
     * @param jsonObject 响应成功时的json数据
     * @param payResult  支付上报的PayResult
     */
    public void onResultBack(Context context, String code, String errorMsg, JSONObject jsonObject, String payResult) {
        if (TextUtils.isEmpty(errorMsg)) {
            errorMsg = jsonObject == null ? "" : jsonObject.optString(Constants.MSG_KEY);
        }
        if (!TextUtils.isEmpty(payResult)) {
            payReport(context, payResult);
        }
        onResultBack(code, errorMsg, jsonObject);
    }

    /**
     * 支付上报
     *
     * @param context
     * @param payResult
     */
    private void payReport(Context context, String payResult) {
        ReportDataBean reportData = new ReportDataBean();
        reportData.setUID(ProfileUserUtil.getInstance().getUserID());
        reportData.setTID(TerminalIdUtil.getTerminalID());
        TransSchema transSchema = CashierPricing.getInstance(context).getTransSchema();
        if (transSchema != null) {
            reportData.setTransID(transSchema.ID);
        }
        reportData.setPayResult(payResult);
        OtherSdkMain.getInstance().payReport(ConstantsPayment.REPORT_TYPE_EXIT, reportData, sid);
    }

    /**
     * 清理
     */
    public void onDestroy() {
        CashierPricing.getInstance(null).destroy();
        callBack = null;
        instance = null;
    }

}