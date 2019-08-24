package com.ahdi.wallet.app.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ahdi.wallet.app.HttpReqApp;
import com.ahdi.wallet.app.callback.BankCardSdkCallBack;
import com.ahdi.wallet.app.listener.account.ApplyBindCardListener;
import com.ahdi.wallet.app.listener.account.BindCardDetailsListener;
import com.ahdi.wallet.app.listener.account.IsCanBindCardListener;
import com.ahdi.wallet.app.listener.account.QueryBindCardInfoListener;
import com.ahdi.wallet.app.listener.account.SelectCardTypeListener;
import com.ahdi.wallet.app.listener.account.SetBindCardLimitListener;
import com.ahdi.wallet.app.listener.account.UnBindCardListener;
import com.ahdi.wallet.app.request.ApplyBindCardReq;
import com.ahdi.wallet.app.request.BindCardDetailsReq;
import com.ahdi.wallet.app.request.IsCanBindReq;
import com.ahdi.wallet.app.request.QueryBindCardReq;
import com.ahdi.wallet.app.request.SelectCardTypeReq;
import com.ahdi.wallet.app.request.SetBindLimitReq;
import com.ahdi.wallet.app.request.UnBindCardReq;
import com.ahdi.wallet.app.ui.activities.bankCard.BankCardEnterPwdActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;

import org.json.JSONObject;


public class BankCardSdkMain {

    private final static String TAG = BankCardSdkMain.class.getSimpleName();
    private static BankCardSdkMain instance;

    public String default_error = "";

    public BankCardSdkCallBack callBack;

    private Context context;

    private BankCardSdkMain() {
    }

    public static BankCardSdkMain getInstance() {
        if (instance == null) {
            synchronized (BankCardSdkMain.class) {
                if (instance == null) {
                    instance = new BankCardSdkMain();
                }
            }
        }
        return instance;
    }

    /**
     * 查询绑卡信息
     * [Bind---------BCBindSchema(用户绑卡信息)]
     *
     * @param context
     * @param sid
     * @param callBack
     */
    public void queryBindCardInfo(Context context, String sid, BankCardSdkCallBack callBack) {
        initDefaultError(context);
        HttpReqApp.getInstance().onQueryBindCardInfo(new QueryBindCardReq(sid), new QueryBindCardInfoListener(callBack));
    }

    /**
     * 申请绑卡
     *
     * @param context
     */
    public void bindCard(Activity context, String sid, String bank, String token, LoadingDialog loadingDialog) {
        initDefaultError(context);
        HttpReqApp.getInstance().onBindCard(new ApplyBindCardReq(sid, bank,token), new ApplyBindCardListener(context, sid, loadingDialog));
    }

    /**
     * 选择绑卡类型
     * @param context
     * @param sid
     * @param payPwd
     * @param callBack
     */
    public void selectCardType(Activity context,String sid,String payPwd,BankCardSdkCallBack callBack){
        initDefaultError(context);
        HttpReqApp.getInstance().onSelectCardType(new SelectCardTypeReq(sid,payPwd),new SelectCardTypeListener(callBack));
    }


    public void bindCardCheckPayPwd(Activity context, String sid) {
        initDefaultError(context);
        Intent intent = new Intent(context, BankCardEnterPwdActivity.class);
        intent.putExtra(Constants.LOCAL_SID_KEY, sid);
        context.startActivity(intent);
    }

    /**
     * 绑卡详情查询
     * @param context
     * @param sid
     * @param bid
     * @param callBack
     */
    public void getBankCardDetails(Activity context,String sid,String bid,BankCardSdkCallBack callBack){
        initDefaultError(context);
        HttpReqApp.getInstance().onBindCardDetails(new BindCardDetailsReq(sid,bid),new BindCardDetailsListener(callBack));
    }

    /**
     * 解除绑卡
     *
     * @param context
     * @param BID
     * @param pwd
     */
    public void unBindCard(Activity context, String sid, String BID, String pwd, LoadingDialog loadingDialog) {
        initDefaultError(context);
        HttpReqApp.getInstance().unBindCard(new UnBindCardReq(sid, BID, pwd), new UnBindCardListener(context, loadingDialog));
    }

    public void unBindCard(Context context, String sid, String bid, int from) {
        initDefaultError(context);
        if (!TextUtils.isEmpty(bid)) {
            Intent intent = new Intent(context, BankCardEnterPwdActivity.class);
            intent.putExtra(Constants.LOCAL_BID_KEY, bid);
            intent.putExtra(Constants.LOCAL_SID_KEY, sid);
            intent.putExtra(Constants.LOCAL_FROM_KEY, from);
            context.startActivity(intent);
        } else {
            LogUtil.e(TAG, "bid is empty");
        }
    }

    /**
     * 修改所绑卡的限额
     * @param context
     * @param sid
     * @param bid
     */
    public void setBindLimit(Context context,String sid,String bid){
        initDefaultError(context);
        HttpReqApp.getInstance().setBindLimit(new SetBindLimitReq(sid, bid), new SetBindCardLimitListener(context,sid,bid));
    }

    /**
     * 是否可以绑卡
     * @param context
     * @param sid
     */
    public void isCanBind(Context context,String sid,BankCardSdkCallBack cardSdkCallBack){
        initDefaultError(context);
        HttpReqApp.getInstance().isCanBind(new IsCanBindReq(sid), new IsCanBindCardListener(cardSdkCallBack));
    }

    /**
     * 初始化默认的错误原因
     *
     * @param context
     */
    private void initDefaultError(Context context) {
        if (context != null) {
            this.context = context.getApplicationContext();
            default_error = context.getString(com.ahdi.lib.utils.R.string.LocalError_C0);
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
        } else {
            ToastUtil.showToastLong(context, errorMsg);
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
