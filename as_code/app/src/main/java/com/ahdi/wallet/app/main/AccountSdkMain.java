package com.ahdi.wallet.app.main;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.HttpReqApp;
import com.ahdi.wallet.app.request.CheckPayPwdReq;
import com.ahdi.wallet.app.request.ProfileReq;
import com.ahdi.wallet.app.request.QueryBalanceReq;
import com.ahdi.wallet.app.request.TransListReq;
import com.ahdi.wallet.app.sdk.AccountSdk;
import com.ahdi.wallet.app.callback.AccountSdkCallBack;
import com.ahdi.wallet.app.listener.account.CheckPayPwdListener;
import com.ahdi.wallet.app.listener.account.OpenTouchIDPayListener;
import com.ahdi.wallet.app.listener.account.ProfileListener;
import com.ahdi.wallet.app.listener.account.QueryBalanceListener;
import com.ahdi.wallet.app.listener.account.ResetPayPwdListener;
import com.ahdi.wallet.app.listener.account.SetPayPwdListener;
import com.ahdi.wallet.app.listener.account.TransListListener;
import com.ahdi.wallet.app.request.CloseTouchIDPayReq;
import com.ahdi.wallet.app.request.OpenTouchIDPayReq;
import com.ahdi.wallet.app.request.ResetPayPwdReq;
import com.ahdi.wallet.app.request.SetPayPwdReq;
import com.ahdi.wallet.app.ui.activities.bankAccount.AddBankAccountActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONObject;

/**
 * Date: 2018/1/31 下午2:30
 * Author: kay lau
 * Description:
 */
public class AccountSdkMain {

    private static AccountSdkMain instance;

    public String default_error = "";

    public AccountSdkCallBack callBack;
    private AccountSdkCallBack bankAccountCallBack;
    public AccountSdkCallBack checkPayPwdCallback;

    private AccountSdkMain() {
    }

    public static AccountSdkMain getInstance() {
        if (instance == null) {
            synchronized (AccountSdkMain.class) {
                if (instance == null) {
                    instance = new AccountSdkMain();
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
        bankAccountCallBack = null;
        checkPayPwdCallback = null;
        instance = null;
    }

    /**
     * 查询余额
     * [Account---------AccountSchema(账户信息)]
     *
     * @param context
     * @param sid
     */
    public void queryBalance(Context context, String sid) {
        initDefaultError(context);
        HttpReqApp.getInstance().onQueryBalance(new QueryBalanceReq(sid), new QueryBalanceListener());
    }

    /**
     * 设置支付密码
     *
     * @param context
     * @param PayPwd
     * @param NoPwdLimit
     */
    public void setPayPWD(Context context, String sid, String PayPwd, long NoPwdLimit) {
        initDefaultError(context);
        SetPayPwdReq request = new SetPayPwdReq(sid, PayPwd, NoPwdLimit);
        HttpReqApp.getInstance().onSetPayPWD(request, new SetPayPwdListener());
    }

    /**
     * 修改密码
     *
     * @param context
     * @param PayPwd
     * @param token
     * @param NoPwdLimit
     */
    public void resetPayPWD(Context context, String sid, String PayPwd, String token, long NoPwdLimit) {
        initDefaultError(context);
        ResetPayPwdReq request = new ResetPayPwdReq(sid, PayPwd, token, NoPwdLimit);
        HttpReqApp.getInstance().onResetPayPWD(request, new ResetPayPwdListener());
    }

    public void checkPayPwd(Context context, String sid, String payPwd, int TType, AccountSdkCallBack AccountSdkCallBack) {
        initDefaultError(context);
        CheckPayPwdReq request = new CheckPayPwdReq(sid, payPwd, TType);
        HttpReqApp.getInstance().onCheckPayPwd(request, new CheckPayPwdListener(AccountSdkCallBack));
    }

    /**
     * 用户概要信息
     * [UserSchema------用户账号信息]
     * [AccountSchema---用户账户信息]
     * [CardNum---------用户银行卡数量]
     * [HasMsg----------用户新消息：0无；1有]
     *
     * @param context
     * @param sid
     * @param callBack
     */
    public void profile(Context context, String sid, AccountSdkCallBack callBack) {
        initDefaultError(context);
        ProfileReq request = new ProfileReq(sid);
        HttpReqApp.getInstance().onAccountProfile(request, new ProfileListener(callBack));
    }

    public void addBankAccount(Context context, int pwdType, String sid, AccountSdkCallBack callBack) {
        bankAccountCallBack = callBack;
        // 校验支付密码
        AccountSdk.checkPayPwd(context, sid, pwdType, new AccountSdkCallBack() {
            @Override
            public void onResult(String code, String token, JSONObject jsonObject) {
                if (code.equals(AccountSdk.LOCAL_PAY_SUCCESS)) {
                    //校验支付密码成功, 打开绑定银行账户界面
                    Intent intent = new Intent(context, AddBankAccountActivity.class);
                    intent.putExtra(Constants.LOCAL_TOKEN_KEY, token);
                    intent.putExtra(Constants.LOCAL_KEY_SID, sid);
                    context.startActivity(intent);
                }
            }
        });
    }

    public void addBankAccountCallback(String code, String errorMsg, JSONObject jsonObject) {
        if (bankAccountCallBack != null) {
            bankAccountCallBack.onResult(code, errorMsg, jsonObject);
            bankAccountCallBack = null;
            onDestroy();
        }
    }

    public void checkPayPwdCallback(String code, String errorMsg, JSONObject jsonObject) {
        if (checkPayPwdCallback != null) {
            checkPayPwdCallback.onResult(code, errorMsg, jsonObject);
            checkPayPwdCallback = null;
        }
    }

    /**
     * 账单列表
     *
     * @param context
     * @param sid
     * @param last
     * @param type
     * @param month
     * @param callBack
     */
    public AsyncTask<Void, Void, Void> getTransList(Context context, String sid, String last, int type, long month, AccountSdkCallBack callBack) {
        initDefaultError(context);
        TransListReq request = new TransListReq(sid, last, type, month);
        return HttpReqApp.getInstance().getTransList(request, new TransListListener(callBack));
    }

    /**
     * 开启指纹支付
     *
     * @param context
     * @param publicKey
     * @param payPwdCipher
     * @param sign
     * @param sid
     * @param callBack
     */
    public void openTouchIDPay(Context context, String publicKey, String payPwdCipher, String sign, String sid, AccountSdkCallBack callBack) {
        initDefaultError(context);
        OpenTouchIDPayReq req = new OpenTouchIDPayReq(payPwdCipher, publicKey, sign, sid);
        HttpReqApp.getInstance().onOpenTouchIDPay(req, new OpenTouchIDPayListener(callBack));
    }

    /**
     * 关闭指纹支付
     *
     * @param sid
     */
    public void closeTouchIDPay(String sid) {
        HttpReqApp.getInstance().onCloseTouchIDPay(new CloseTouchIDPayReq(sid),
                new HttpReqTaskListener() {
                    @Override
                    public void onPostExecute(JSONObject json) {
                        if (json == null) {
                            return;
                        }
                        LogUtil.e("closeTouchIDPay", json.toString());
                    }

                    @Override
                    public void onError(JSONObject json) {
                        if (json == null) {
                            return;
                        }
                        LogUtil.e("closeTouchIDPayError", json.toString());
                    }
                });
    }
}