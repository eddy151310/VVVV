package com.ahdi.wallet.app.main;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.ahdi.wallet.app.sdk.AccountSdk;
import com.ahdi.wallet.app.sdk.BankAccountSdk;
import com.ahdi.wallet.app.sdk.BankCardSdk;
import com.ahdi.wallet.app.sdk.IDVerifySdk;
import com.ahdi.wallet.app.sdk.MsgSdk;
import com.ahdi.wallet.app.sdk.OtherSdk;
import com.ahdi.wallet.module.payment.topup.TopUpSdk;
import com.ahdi.wallet.app.sdk.UserSdk;
import com.ahdi.wallet.app.sdk.VoucherSdk;
import com.ahdi.wallet.app.bean.LoginBean;
import com.ahdi.wallet.app.bean.RegisterBean;
import com.ahdi.wallet.app.bean.UserInfoGuideSetBean;
import com.ahdi.wallet.app.callback.AccountSdkCallBack;
import com.ahdi.wallet.app.callback.BankAccountSdkCallBack;
import com.ahdi.wallet.app.callback.BankCardSdkCallBack;
import com.ahdi.wallet.app.callback.IDVerifyCallBack;
import com.ahdi.wallet.app.callback.MsgSdkCallBack;
import com.ahdi.wallet.app.callback.OtherSdkCallBack;
import com.ahdi.wallet.module.payment.topup.callback.TopUpCallBack;
import com.ahdi.wallet.app.callback.UserSdkCallBack;
import com.ahdi.wallet.app.callback.VoucherSdkCallBack;
import com.ahdi.wallet.app.response.ConfigRsp;
import com.ahdi.wallet.app.schemas.AvatarSchema;
import com.ahdi.wallet.module.payment.transfer.TransferSdk;
import com.ahdi.wallet.module.payment.withdraw.WithDrawSdk;
import com.ahdi.wallet.module.payment.transfer.bean.TABean;
import com.ahdi.wallet.module.payment.transfer.callback.TransferResultCallBack;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.app.callback.AppConfigCallBack;
import com.ahdi.wallet.module.payment.withdraw.callback.WithDrawResultCallBack;
import com.ahdi.wallet.util.AppConfigHelper;

import org.json.JSONObject;

/**
 * Date: 2017/10/24 下午2:36
 * Author: kay lau
 * Description:
 */
public class AppMain {

    private static final String TAG = AppGlobalUtil.class.getSimpleName();
    private static AppMain instance;
    private static final int MSG_BIND = 0;
    private int bindCount = 3;
    private long bindTime = 5 * 1000;
    private TerminalBindHandler handler;

    private AppMain() {
    }

    public synchronized static AppMain getInstance() {
        if (instance == null) {
            instance = new AppMain();
        }
        return instance;
    }

    class TerminalBindHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_BIND) {
                onTerminalBind();
            }
        }
    }

    /**
     * 终端设备和推送通知服务绑定
     * 绑定时候header中IID参数为必须
     */
    public void onTerminalBind() {
        Context context = GlobalApplication.getApplication().getApplicationContext();
        String sid = GlobalApplication.getApplication().getSID();
        UserSdk.iidBind(context, sid,
                new UserSdkCallBack() {
                    @Override
                    public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                        if (UserSdk.LOCAL_PAY_SUCCESS.equals(code)) {
                            LogUtil.e(TAG, jsonObject == null ? TAG + "<--iid tid bind response--> jsonObject = null" : TAG + "<--iid tid bind response--> " + jsonObject.toString());
                        } else {
                            LogUtil.e(TAG, "code : " + code + ", errorMsg" + errorMsg);
                            // 继续绑定 3次 5秒
                            if (bindCount > 0) {
                                bindCount--;
                                if (handler == null) {
                                    handler = new TerminalBindHandler();
                                }
                                handler.sendEmptyMessageDelayed(MSG_BIND, bindTime);
                            }
                        }
                    }
                });
    }

    /***
     * 获取app配置信息
     * @param context
     * @param callBack
     */
    public void appConfig(Context context, AppConfigCallBack callBack) {

        OtherSdk.getConfig(context, GlobalApplication.getApplication().getSID(), AppConfigHelper.getInstance().getCfgVersion(), new OtherSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                if (TextUtils.equals(code, OtherSdk.LOCAL_SUCCESS)) {
                    ConfigRsp response = ConfigRsp.decodeJson(ConfigRsp.class, jsonObject);
                    if (response != null && response.getAppCfgSchema() != null) {
                        AppConfigHelper.getInstance().updateClientCfg(response.getAppCfgSchema());
                    }
                    callBack.onResult(code, null);
                } else {
                    callBack.onResult(code, errorMsg);
                }
            }
        });
    }

    /**
     * 登录
     *
     * @param activity
     * @param bean
     */
    public void goToLogin(final Activity activity, LoginBean bean, UserSdkCallBack callBack) {
        UserSdk.login(activity, bean, callBack);
    }

    /**
     * 查询账号是否已经注册
     *
     * @param activity
     * @param loginName
     * @param type      0查询是否注册  1查询是否存在
     * @param callBack
     */
    public void queryRegister(Activity activity, String loginName, int type, UserSdkCallBack callBack) {
        UserSdk.queryRegister(activity, loginName, type, callBack);
    }

    /**
     * 注册
     *
     * @param activity
     * @param bean
     * @param callBack
     */
    public void register(Activity activity, RegisterBean bean, UserSdkCallBack callBack) {
        UserSdk.register(activity, bean, callBack);
    }

    /**
     * 查询绑定银行账户信息
     *
     * @param activity
     * @param callBack
     */
    public void queryBinderAccount(Activity activity, BankAccountSdkCallBack callBack) {
        BankAccountSdk.queryBindAccountInfo(activity, GlobalApplication.getApplication().getSID(), callBack);
    }

    /**
     * 绑卡列表
     *
     * @param activity
     * @param callBack
     */
    public void queryBinderCard(Activity activity, BankCardSdkCallBack callBack) {
        BankCardSdk.queryBindCardInfo(activity, GlobalApplication.getApplication().getSID(), callBack);
    }

    /**
     * 查询绑卡详情
     *
     * @param activity
     * @param bid
     * @param callBack
     */
    public void bindCardDetails(Activity activity, String bid, BankCardSdkCallBack callBack) {
        BankCardSdkMain.getInstance().getBankCardDetails(activity, GlobalApplication.getApplication().getSID(), bid, callBack);
    }

    /**
     * 查询提现账户信息(可提现金额、提现账户列表等)
     *
     * @param activity
     * @param callBack
     */
    public void queryWDBindAccountInfo(Activity activity, WithDrawResultCallBack callBack) {
        WithDrawSdk.queryWDBankCardInfo(activity, GlobalApplication.getApplication().getSID(), callBack);
    }

    /**
     * 查询充值额度（充值专用）
     *
     * @param activity
     * @param callBack
     */
    public void onQueryRechargeQuota(Activity activity, TopUpCallBack callBack) {
        TopUpSdk.queryRechargeQuota(activity, GlobalApplication.getApplication().getSID(), callBack);
    }


    /**
     * 充值结果查询
     *
     * @param activity
     * @param ot
     * @param callBack
     */
    public void onQueryRechargeResult(Activity activity, String ot, TopUpCallBack callBack) {
        TopUpSdk.rechargeResultQuery(activity, ot, GlobalApplication.getApplication().getSID(), callBack);
    }

    /**
     * 充值类型查询
     *
     * @param activity
     * @param callBack
     */
    public void onRechrTypeQuery(Activity activity, TopUpCallBack callBack) {
        TopUpSdk.rechrTypeQuery(activity, GlobalApplication.getApplication().getSID(), callBack);
    }

    /**
     * 提现
     *
     * @param activity
     * @param callBack
     */
    public void onWithdraw(Activity activity, String target, int want, String amount, String remark, WithDrawResultCallBack callBack) {
        WithDrawSdk.createWD(activity, GlobalApplication.getApplication().getSID(), target, want, amount, remark, callBack);
    }

    /**
     * 查询提现进度
     *
     * @param activity
     * @param ID
     * @param callBack
     */
    public void queryWDProgress(Activity activity, String ID, WithDrawResultCallBack callBack) {
        WithDrawSdk.queryWDProgress(activity, GlobalApplication.getApplication().getSID(), ID, callBack);
    }

    /**
     * 查询余额
     *
     * @param activity
     * @param sid
     * @param callBack
     */
    public void queryBalance(Activity activity, String sid, AccountSdkCallBack callBack) {
        AccountSdk.queryBalance(activity, sid, callBack);
    }

    /**
     * 查询转账目标
     *
     * @param activity
     * @param lName
     * @param sid
     * @param callback
     */
    public void onQueryTransferTarget(Activity activity, String lName, String sid, TransferResultCallBack callback) {
        TransferSdk.queryTransferTarget(activity, lName, sid, callback);
    }

    /**
     * 查询二维码扫描转账目标
     *
     * @param activity
     * @param param
     * @param callback
     */
    public void onQRTransferTarget(Activity activity, String param, TransferResultCallBack callback) {
        TransferSdk.onQRTransferTarget(activity, param, callback);
    }

    /**
     * 转账
     *
     * @param activity
     * @param bean
     * @param sid
     * @param callback
     */
    public void onTransfer(Activity activity, TABean bean, String sid, TransferResultCallBack callback) {
        TransferSdk.createTransfer(activity, bean, sid, callback);
    }

    /**
     * 查询转账进度
     */
    public void queryTransferProgress(Activity activity, String ID, TransferResultCallBack callback) {
        TransferSdk.queryTransferProgress(activity, ID, GlobalApplication.getApplication().getSID(), callback);
    }


    /**
     * 查询最近转账联系人
     *
     * @param activity
     * @param callBack
     */
    public void onQueryRecentTransContact(Activity activity, TransferResultCallBack callBack) {
        TransferSdk.queryRecentTransContact(activity, GlobalApplication.getApplication().getSID(), callBack);
    }

    /**
     * 用户消息概要
     *
     * @param activity
     * @param callBack
     */
    public void onProfile(Activity activity, AccountSdkCallBack callBack) {
        AccountSdk.profile(activity, GlobalApplication.getApplication().getSID(), callBack);
    }

    /**
     * 设置支付密码
     *
     * @param activity
     * @param payPwd
     * @param noPwdLimit
     * @param callBack
     */
    public void setPayPwd(Activity activity, String payPwd, long noPwdLimit, AccountSdkCallBack callBack) {
        AccountSdk.setPayPWD(activity, GlobalApplication.getApplication().getSID(), payPwd, noPwdLimit, callBack);
    }

    /**
     * 重置支付密码
     *
     * @param activity
     * @param payPwd
     * @param token
     * @param noPwdLimit
     * @param callBack
     */
    public void resetPayPwd(Activity activity, String payPwd, String token, long noPwdLimit, AccountSdkCallBack callBack) {
        AccountSdk.resetPayPWD(activity, GlobalApplication.getApplication().getSID(), payPwd, token, noPwdLimit, callBack);
    }


    /**
     * 解绑银行账户
     *
     * @param activity
     * @param ST
     * @param callBack
     */
    public void unBindAccount(Activity activity, String ST, BankAccountSdkCallBack callBack) {
        BankAccountSdk.unBindAccount(activity, GlobalApplication.getApplication().getSID(), ST, callBack);
    }

    /**
     * 修改卡限额
     *
     * @param activity
     * @param bid
     * @param callBack
     */
    public void setBindLimit(Activity activity, String bid, BankCardSdkCallBack callBack) {
        BankCardSdk.setBindLimit(activity, GlobalApplication.getApplication().getSID(), bid, callBack);
    }

    /**
     * 绑卡
     * 流程：
     * 先验证支付密码-->验证支付密码成功之后-->选择卡类型-->如果是BCA绑卡-->调起BCA绑卡界面-->如果是manbank则吊起H5界面
     *
     * @param activity
     * @param callBack
     */
    public void bindCard(Activity activity, BankCardSdkCallBack callBack) {
        BankCardSdk.bindCard(activity, GlobalApplication.getApplication().getSID(), callBack);
    }

    /**
     * 解绑银行卡
     *
     * @param activity
     * @param BID
     * @param callBack
     */
    public void unBindCard(Activity activity, String BID, int from, BankCardSdkCallBack callBack) {
        BankCardSdk.unBindCard(activity, GlobalApplication.getApplication().getSID(), BID, from, callBack);
    }

    /**
     * 忘记支付密码: 下发短信验证码
     *
     * @param activity
     * @param phoneNumber
     * @param callBack
     */
    public void sendVCodeForPayPwd(Activity activity, String phoneNumber, UserSdkCallBack callBack) {
        UserSdk.sendVCodeForPayPwd(activity, phoneNumber, GlobalApplication.getApplication().getSID(), callBack);
    }

    /**
     * 忘记支付密码: 验证短信验证码
     *
     * @param activity
     * @param phoneNumber
     * @param verifyCode
     * @param callBack
     */
    public void checkVCodeForPayPwd(Activity activity, String phoneNumber, String verifyCode, UserSdkCallBack callBack) {
        UserSdk.checkVCodeForPayPwd(activity, phoneNumber, verifyCode, GlobalApplication.getApplication().getSID(), callBack);
    }

    /**
     * 忘记登录密码: 下发短信验证码
     *
     * @param activity
     * @param phoneNumber
     * @param callBack
     */
    public void sendVCodeForLoginPwd(Activity activity, String phoneNumber, UserSdkCallBack callBack) {
        UserSdk.sendVCodeForLoginPwd(activity, phoneNumber, GlobalApplication.getApplication().getSID(), callBack);
    }

    /**
     * 忘记登录密码: 验证短信验证码
     *
     * @param activity
     * @param phoneNumber
     * @param verifyCode
     * @param callBack
     */
    public void checkVCodeForLoginPwd(Activity activity, String phoneNumber, String verifyCode, UserSdkCallBack callBack) {
        UserSdk.checkVCodeForLoginPwd(activity, phoneNumber, verifyCode, GlobalApplication.getApplication().getSID(), callBack);
    }

    /**
     * 注册: 下发短信验证码
     *
     * @param activity
     * @param phoneNumber
     * @param callBack
     */
    public void sendVCodeForRegister(Activity activity, String phoneNumber, UserSdkCallBack callBack) {
        UserSdk.sendVCodeForRegister(activity, phoneNumber, callBack);
    }

    /**
     * 登录: 下发短信验证码
     *
     * @param activity
     * @param phoneNumber
     * @param callBack
     */
    public void sendVCodeForLogin(Activity activity, String phoneNumber, UserSdkCallBack callBack) {
        UserSdk.sendVCodeForLogin(activity, phoneNumber, callBack);
    }

    /**
     * 注册: 验证短信验证码
     *
     * @param activity
     * @param phoneNumber
     * @param verifyCode
     * @param callBack
     */
    public void sendVCodeForRegister(Activity activity, String phoneNumber, String verifyCode, UserSdkCallBack callBack) {
        UserSdk.checkVCodeForRegister(activity, phoneNumber, verifyCode, callBack);
    }

    /**
     * 添加银行账户流程: 校验支付密码-->申请添加银行账户
     *
     * @param activity
     * @param pwdType
     * @param callBack
     */
    public void addBankAccount(Activity activity, int pwdType, AccountSdkCallBack callBack) {
        AccountSdk.addBankAccount(activity, GlobalApplication.getApplication().getSID(), pwdType, callBack);
    }


    /**
     * 身份验证结果查询
     *
     * @param activity
     * @param callBack
     */
    public void auditQR(Activity activity, boolean showSuccessEnding, IDVerifyCallBack callBack) {
        IDVerifySdk.auditQR(activity, GlobalApplication.getApplication().getSID(), showSuccessEnding, callBack);
    }

    /**
     * 账单列表
     *
     * @param activity
     * @param last
     * @param type
     * @param month
     * @param callBack
     */
    public AsyncTask<Void, Void, Void> getTransList(Activity activity, String last, int type, long month, AccountSdkCallBack callBack) {
        return AccountSdk.getTransList(activity, GlobalApplication.getApplication().getSID(), last, type, month, callBack);
    }

    /**
     * 登出
     *
     * @param context
     * @param sid
     * @param callBack
     */
    public void onLogout(Context context, String sid, UserSdkCallBack callBack) {
        UserSdk.logout(context, sid, callBack);
    }

    /**
     * 设置用户头像
     *
     * @param context
     * @param avatarSchema 用户头像信息
     * @param callBack
     */
    public void setUserIcon(Context context, AvatarSchema avatarSchema, UserSdkCallBack callBack) {
        UserSdk.setUserIcon(context, avatarSchema, GlobalApplication.getApplication().getSID(), callBack);
    }

    /**
     * 更新用户信息
     *
     * @param context
     * @param content
     * @param type
     * @param callBack
     */
    public void updateUserInfo(Context context, String content, int type, UserSdkCallBack callBack) {
        UserSdk.updateUserInfo(context, content, type, GlobalApplication.getApplication().getSID(), callBack);
    }

    /**
     * 检查登录密码
     *
     * @param context
     * @param pwd
     * @param callBack
     */
    public void checkLoginPwd(Context context, String pwd, UserSdkCallBack callBack) {
        UserSdk.checkLoginPwd(context, pwd, GlobalApplication.getApplication().getSID(), callBack);
    }

    /**
     * 重置登录密码
     *
     * @param type     0:修改密码 ,1: 重置密码,2:新建密码
     * @param context
     * @param pwd
     * @param token
     * @param callBack
     */
    public void resetLoginPwd(int type, Context context, String pwd, String token, UserSdkCallBack callBack) {
        UserSdk.resetLoginPwd(type, context, pwd, token, GlobalApplication.getApplication().getSID(), callBack);
    }


    /**
     * 获取用户协议
     *
     * @param context
     * @param action
     * @param callBack
     */
    public void getUserAgreement(Context context, String action, OtherSdkCallBack callBack) {
        OtherSdk.getUserAgreement(context, action, callBack);
    }

    /**
     * 用户信息引导设置
     *
     * @param context
     * @param bean
     * @param callBack
     */
    public void userInfoGuideSet(Context context, UserInfoGuideSetBean bean, UserSdkCallBack callBack) {
        UserSdk.userInfoGuideSet(context, GlobalApplication.getApplication().getSID(), bean, callBack);
    }

    /**
     * 获取banner信息
     *
     * @param context
     * @param param
     * @param callBack
     */
    public void getBannerInfo(Context context, String param, OtherSdkCallBack callBack) {
        OtherSdk.getBannerInfo(context, param, callBack);
    }

    /**
     * 获取bank va url
     *
     * @param context
     * @param type
     * @param callBack
     */
    public void getBankVaUrl(Context context, String type, OtherSdkCallBack callBack) {
        OtherSdk.getBankVaUrl(context, type, GlobalApplication.getApplication().getSID(), callBack);
    }

    public void getMsgHomeUrl(Context context, MsgSdkCallBack callBack) {
        MsgSdk.getMsgHomeUrl(context, GlobalApplication.getApplication().getSID(), callBack);
    }

    /**
     * 获取优惠券的列表
     *
     * @param context
     * @param id
     * @param voucherType
     * @param callBack
     */
    public AsyncTask getVoucherList(Context context, String id, int voucherType, VoucherSdkCallBack callBack) {
        return VoucherSdk.voucherList(context, GlobalApplication.getApplication().getSID(), id, voucherType, callBack);
    }

    /**
     * 获取优惠券的列表
     *
     * @param context
     * @param id
     * @param callBack
     */
    public void getVoucherDetails(Context context, String id, VoucherSdkCallBack callBack) {
        VoucherSdk.voucherDetail(context, GlobalApplication.getApplication().getSID(), id, callBack);
    }
}
