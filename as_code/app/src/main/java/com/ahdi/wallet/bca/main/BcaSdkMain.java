package com.ahdi.wallet.bca.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ahdi.wallet.R;
import com.ahdi.wallet.bca.callback.BcaBankCardCallBack;
import com.ahdi.wallet.bca.callback.BcaOTPCallBack;
import com.ahdi.wallet.bca.listener.BcaAsyncNotifyListener;
import com.ahdi.wallet.bca.listener.BcaOrderListener;
import com.ahdi.wallet.bca.listener.BcaSyncNotifyListener;
import com.ahdi.wallet.bca.listener.BcaOTPListener;
import com.ahdi.wallet.bca.requset.BcaOTPReq;
import com.ahdi.wallet.bca.requset.BcaResultNotifyReq;
import com.ahdi.wallet.bca.requset.BcaOrderReq;
import com.ahdi.wallet.app.schemas.PayBindSchema;
import com.ahdi.wallet.bca.ui.activities.BCAXCOActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.widgets.ToastUtil;

import org.json.JSONObject;

/**
 * @author xiaoniu
 * bca
 */
public class BcaSdkMain {

    private static final String TAG = BcaSdkMain.class.getSimpleName();

    private static BcaSdkMain instance;

    public BcaBankCardCallBack bankCardCallBack;
    public BcaOTPCallBack bcaOTPCallBack;
    private String sid;
    /**
     * 默认的错误原因
     */
    public String default_error = "";

    private BcaSdkMain() {
    }

    public static BcaSdkMain getInstance() {
        if (instance == null) {
            synchronized (BcaSdkMain.class) {
                if (instance == null) {
                    instance = new BcaSdkMain();
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
     * 统一对外回调
     */
    public void onBankCardResultSuccess() {
        if (bankCardCallBack != null) {
            bankCardCallBack.onSuccess();
        }
        onDestroy();
    }

    /**
     * 统一对外回调
     *
     * @param errMsg
     */
    public void onBankCardResultFail(String errMsg) {
        if (bankCardCallBack != null) {
            bankCardCallBack.onFail(errMsg);
        }
        onDestroy();
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
        if (bcaOTPCallBack != null) {
            bcaOTPCallBack.onResult(code, errorMsg, jsonObject);
        }
        onDestroy();
    }

    /**
     * 清理
     */
    public void onDestroy() {
        bankCardCallBack = null;
        instance = null;
    }

    /**
     * 添加bca银行卡或者修改限额
     *
     * @param context
     * @param sid
     * @param bid
     * @param bcaTpe  1-绑卡;2-修改卡限额
     */
    public void bcaSDKPage(Context context, String sid, String bid, PayBindSchema payBindSchema, int bcaTpe) {
        this.sid = sid;
        try {
            Intent intent = new Intent(context, BCAXCOActivity.class);
            intent.putExtra(Constants.LOCAL_KEY_BCA_PAGE_TYPE, bcaTpe);
            intent.putExtra(Constants.LOCAL_KEY_BANK_CARD_ID, bid);
            intent.putExtra(Constants.LOCAL_KEY_PAY_BC_SCHEMA, payBindSchema);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d(TAG, e.getMessage());

            String errMsg = context.getString(R.string.Toast_C1);
            ToastUtil.showToastShort(context.getApplicationContext(), errMsg);
            onBankCardResultFail(errMsg);
        }
    }

    /**
     * BCA绑卡结果通知/修改限额结果通知
     *
     * @param activity
     * @param init
     * @param method
     * @param data
     */
    public void bcaResultSyncNotify(Activity activity, String init, String method, String data, String bid) {
        BcaResultNotifyReq request = new BcaResultNotifyReq(sid, init, method, data, bid);
        HttpReqBca.getInstance().onBCAResultNotify(request, new BcaSyncNotifyListener(activity, init, method, data, bid));
    }

    /**
     * 修改限额结果通知接口(异步返回给服务端)
     *
     * @param init
     * @param method
     * @param data
     * @param bid
     */
    public void bcaResultAsyncNotify(String init, String method, String data, String bid) {
        BcaResultNotifyReq request = new BcaResultNotifyReq(sid, init, method, data, bid);
        HttpReqBca.getInstance().onBCAResultNotify(request, new BcaAsyncNotifyListener());
    }

    /**
     * 下单BCA OTP
     * BCA银行卡支付, OTP 下发短信验证码
     *
     * @param sid
     * @param msisdnId
     * @param payEx
     * @param payInfo
     * @param callBack
     */
    public void bcaOTPSendVcode(Context context, String sid, String msisdnId, String payEx, String payInfo, BcaOTPCallBack callBack) {
        initDefaultError(context);
        BcaOTPReq req = new BcaOTPReq(sid, msisdnId, payEx, payInfo);
        HttpReqBca.getInstance().onBcaOTP(req, new BcaOTPListener(callBack));
    }

    /**
     * otp支付下单
     *
     * @param sid
     * @param payEx
     * @param payInfo
     * @param channelID
     * @param callBack
     */
    public void otpOrder(Context context, String sid, String payEx, String payInfo, int channelID, BcaOTPCallBack callBack) {
        initDefaultError(context);
        BcaOrderReq req = new BcaOrderReq(sid, payEx, payInfo, channelID);
        HttpReqBca.getInstance().onOTPOrder(req, new BcaOrderListener(callBack));
    }
}