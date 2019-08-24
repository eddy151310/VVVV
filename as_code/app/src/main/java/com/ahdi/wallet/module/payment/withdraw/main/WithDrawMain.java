package com.ahdi.wallet.module.payment.withdraw.main;

import android.content.Context;

import com.ahdi.wallet.R;
import com.ahdi.wallet.cashier.PayCashierSdk;
import com.ahdi.wallet.module.payment.transfer.listener.PayListener;
import com.ahdi.wallet.module.payment.withdraw.callback.WithDrawResultCallBack;
import com.ahdi.wallet.module.payment.withdraw.listener.QueryWDBankCardInfoListener;
import com.ahdi.wallet.module.payment.withdraw.listener.QueryWDProgressListener;
import com.ahdi.wallet.module.payment.withdraw.listener.WDCreateListener;
import com.ahdi.wallet.module.payment.withdraw.requset.WDCreateRequest;
import com.ahdi.wallet.module.payment.withdraw.requset.WDQueryBankCardInfoRequest;
import com.ahdi.wallet.module.payment.withdraw.requset.WDQueryProgressRequest;

import org.json.JSONObject;

/**
 * @author admin
 * 转账逻辑处理类
 */

public class WithDrawMain {

    private static WithDrawMain instance;
    /**
     * 对外的结果回调
     */
    public WithDrawResultCallBack callBack;
    /**
     * 默认的错误原因
     */
    public String default_error = "";
    private Context context;


    public static WithDrawMain getInstance() {
        if (instance == null) {
            synchronized (WithDrawMain.class) {
                if (instance == null) {
                    instance = new WithDrawMain();
                }
            }
        }
        return instance;
    }

    private WithDrawMain() {
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
     * 提现查询绑卡信息
     *
     * @param context
     * @param sid
     */
    public void queryWDBankCardInfo(Context context, String sid) {
        initDefaultError(context);
        WDQueryBankCardInfoRequest request = new WDQueryBankCardInfoRequest(sid);
        HttpReqWithdraw.getInstance().queryWDBankCardInfo(request, new QueryWDBankCardInfoListener());
    }

    /**
     * 发起提现
     *
     * @param context
     * @param sid
     */
    public void createWD(Context context, String sid, String target, int want, String amount, String remark) {
        initDefaultError(context);
        this.context = context;
        WDCreateRequest request = new WDCreateRequest(sid, target, want, amount, remark);
        HttpReqWithdraw.getInstance().createWD(request, new WDCreateListener(sid));
    }

    /**
     * 查询提现进度
     *
     * @param context
     * @param sid
     */
    public void queryWDProgress(Context context, String sid, String ID) {
        initDefaultError(context);
        WDQueryProgressRequest request = new WDQueryProgressRequest(ID, sid);
        HttpReqWithdraw.getInstance().queryWDProgress(request, new QueryWDProgressListener(ID));
    }

    /**
     * 支付
     *
     * @param id
     * @param pay
     * @param sid
     */
    public void pay(String id, String pay, String sid, int from) {
        PayCashierSdk.startPay(context, pay, sid, from, new PayListener(id, from));
    }

    /**
     * 统一对外回调
     *
     * @param code       “0”成功 其他失败
     * @param errorMsg   错误描述
     * @param jsonObject 响应成功时的json数据
     */
    public void onResultBack(String code, String errorMsg, JSONObject jsonObject, String ID) {
        if (callBack != null) {
            callBack.onResult(code, errorMsg, jsonObject, ID);
        }
        onDestroy();
    }

    /**
     * 清理
     */
    private void onDestroy() {
        callBack = null;
        instance = null;
        context = null;
    }
}
