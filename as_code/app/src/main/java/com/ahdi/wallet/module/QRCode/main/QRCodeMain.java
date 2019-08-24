package com.ahdi.wallet.module.QRCode.main;

import android.content.Context;
import android.text.TextUtils;

import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.TouchIDStateUtil;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.listener.account.CollectQRListener;
import com.ahdi.wallet.module.QRCode.requset.CollectQRCodeReq;
import com.ahdi.wallet.module.QRCode.bean.PayCodePINConfirmBean;
import com.ahdi.wallet.module.QRCode.bean.PayCodeInitBean;
import com.ahdi.wallet.module.QRCode.requset.PayCodeInitReq;
import com.ahdi.wallet.module.QRCode.requset.PayCodePINConfirmReq;
import com.ahdi.wallet.module.QRCode.listener.PayCodeInitListener;
import com.ahdi.wallet.module.QRCode.listener.PayCodeUseQueryListener;
import com.ahdi.wallet.module.QRCode.listener.PayCodePINConfirmListener;
import com.ahdi.wallet.module.QRCode.callback.QRCodeSdkCallBack;
import com.ahdi.wallet.module.QRCode.requset.PayCodeUseQueryReq;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Date: 2019/6/24 下午5:29
 * Author: kay lau
 * Description:
 */
public class QRCodeMain {

    private static QRCodeMain instance;

    public String default_error = "";

    public QRCodeSdkCallBack callBack;


    private QRCodeMain() {
    }

    public static QRCodeMain getInstance() {
        if (instance == null) {
            synchronized (QRCodeMain.class) {
                if (instance == null) {
                    instance = new QRCodeMain();
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
     * 清理
     */
    public void onDestroy() {
        callBack = null;
        instance = null;
    }

    /**
     * 2.4.2.8	支付授权码初始化
     *
     * @param context
     * @param bean
     * @param sid
     * @param callBack
     */
    public void initPayCode(Context context, PayCodeInitBean bean, String sid, QRCodeSdkCallBack callBack) {
        initDefaultError(context);
        HttpReqQRCode.getInstance().onInitPayCode(new PayCodeInitReq(bean, sid), new PayCodeInitListener(callBack));
    }

    /**
     * 支付授权码是否被使用
     * 如果用户已经使用授权码做了支付，则返回当前授权码的支付订单[TT]
     *
     * @param context
     * @param sid
     * @param callBack
     */
    public void payAuthCodeUseQuery(Context context, long clientId, ArrayList<String> timesLot, String sid, QRCodeSdkCallBack callBack) {
        initDefaultError(context);
        String lName = AppGlobalUtil.getInstance().getLName(context);
        String touchVer = TouchIDStateUtil.getTouchIDPayVer(context, lName);
        PayCodeUseQueryReq request = new PayCodeUseQueryReq(clientId, timesLot, touchVer, sid);
        HttpReqQRCode.getInstance().onPayAuthCodeUseQuery(request, new PayCodeUseQueryListener(callBack));
    }

    /**
     * 支付授权码确认/取消
     *
     * @param context
     * @param bean
     * @param sid
     * @param callBack
     */
    public void payAuthCodeConfirm(Context context, PayCodePINConfirmBean bean, String sid, String payAuthType, QRCodeSdkCallBack callBack) {
        initDefaultError(context);
        String lName = AppGlobalUtil.getInstance().getLName(context);
        String touchVer = TouchIDStateUtil.getTouchIDPayVer(context, lName);
        PayCodePINConfirmReq request = new PayCodePINConfirmReq(bean, sid, touchVer, payAuthType);
        HttpReqQRCode.getInstance().onPayAuthCodeConfirm(request, new PayCodePINConfirmListener(callBack));
    }

    /**
     * 收款二维码 [收款地址 URI]
     *
     * @param context
     * @param amount
     * @param sid
     */
    public void collectQR(Context context, String amount, String sid) {
        initDefaultError(context);
        HttpReqQRCode.getInstance().onCollectQR(new CollectQRCodeReq(amount, sid), new CollectQRListener());
    }
}
