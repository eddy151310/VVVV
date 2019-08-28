package com.ahdi.wallet.app.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.HttpReqApp;
import com.ahdi.wallet.app.bean.BindAccountBean;
import com.ahdi.wallet.app.callback.BankAccountSdkCallBack;
import com.ahdi.wallet.app.listener.account.BankAccountListListener;
import com.ahdi.wallet.app.listener.account.BindAccountListener;
import com.ahdi.wallet.app.listener.account.QueryBindAccountInfoListener;
import com.ahdi.wallet.app.listener.account.UnBindAccountListener;
import com.ahdi.wallet.app.request.BankAccountListReq;
import com.ahdi.wallet.app.request.BindAccountReq;
import com.ahdi.wallet.app.request.QueryBankAccountReq;
import com.ahdi.wallet.app.request.UnBindAccountReq;
import com.ahdi.wallet.app.ui.activities.bankAccount.BankAccountEnterPwdActivity;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONObject;


public class BankAccountSdkMain {

    private final static String TAG = BankAccountSdkMain.class.getSimpleName();
    private static BankAccountSdkMain instance;

    public String default_error = "";

    public BankAccountSdkCallBack callBack;

    private BankAccountSdkMain() {
    }

    public static BankAccountSdkMain getInstance() {
        if (instance == null) {
            synchronized (BankAccountSdkMain.class) {
                if (instance == null) {
                    instance = new BankAccountSdkMain();
                }
            }
        }
        return instance;
    }

    /**
     * 查询绑定银行账户信息
     * [Bind---------BCBindSchema(用户绑卡信息)]
     *
     * @param context
     * @param sid
     * @param callBack
     */
    public void queryBindCardInfo(Context context, String sid, BankAccountSdkCallBack callBack) {
        initDefaultError(context);
        HttpReqApp.getInstance().onQueryBindAccountInfo(new QueryBankAccountReq(sid), new QueryBindAccountInfoListener(callBack));
    }

    /**
     * 绑定银行账户
     *
     * @param context
     */
    public void bindAccount(Context context, String sid, BindAccountBean bean, BankAccountSdkCallBack callBack) {
        initDefaultError(context);
        String bankAccountNo = bean.getBankAccountNo();
        String name = bean.getName();
        String bankCode = bean.getBanckCode();
        String token = bean.getToken();
        String phone = bean.getPhone();
        HttpReqApp.getInstance().onBindAccount(new BindAccountReq(sid, bankAccountNo, name, phone, bankCode, token), new BindAccountListener(callBack));
    }

    /**
     * 解除绑定银行账户
     *
     * @param context
     * @param ST
     * @param pwd
     */
    public void unBindCard(Activity context, String sid, String ST, String pwd, LoadingDialog loadingDialog) {
        initDefaultError(context);
        HttpReqApp.getInstance().unBindAccount(new UnBindAccountReq(sid, ST, pwd),
                new UnBindAccountListener(context, loadingDialog));
    }

    public void unBindCard(Context context, String sid, String ST) {
        initDefaultError(context);
        if (!TextUtils.isEmpty(ST)) {
            Intent intent = new Intent(context, BankAccountEnterPwdActivity.class);
            intent.putExtra(Constants.LOCAL_BID_KEY, ST);
            intent.putExtra(Constants.SP_KEY_SID, sid);
            context.startActivity(intent);
        } else {
            LogUtil.e(TAG, "bid is empty");
        }
    }

    public void queryBankAccountList(Context context, String sid, long time) {
        initDefaultError(context);
        HttpReqApp.getInstance().queryBankAccountList(new BankAccountListReq(sid, time), new BankAccountListListener());
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
}
