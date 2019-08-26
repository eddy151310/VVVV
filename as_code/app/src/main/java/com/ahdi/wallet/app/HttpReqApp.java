package com.ahdi.wallet.app;

import android.os.AsyncTask;

import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.wallet.app.request.ApplyBindCardReq;
import com.ahdi.wallet.app.request.AuditQRReq;
import com.ahdi.wallet.app.request.BankAccountListReq;
import com.ahdi.wallet.app.request.BannerInfoReq;
import com.ahdi.wallet.app.request.BindAccountReq;
import com.ahdi.wallet.app.request.BindCardDetailsReq;
import com.ahdi.wallet.app.request.CheckLoginPwdReq;
import com.ahdi.wallet.app.request.CheckPayPwdReq;
import com.ahdi.wallet.app.request.CloseTouchIDPayReq;
import com.ahdi.wallet.app.request.GetBankVAReq;
import com.ahdi.wallet.app.request.GetPhotoUpUrlReq;
import com.ahdi.wallet.app.request.IIDBind_LogoutReq;
import com.ahdi.wallet.app.request.IsCanBindReq;
import com.ahdi.wallet.app.request.LoginReq;
import com.ahdi.wallet.app.request.MsgListReq;
import com.ahdi.wallet.app.request.OpenTouchIDPayReq;
import com.ahdi.wallet.app.request.PhoneAreaCodeReq;
import com.ahdi.wallet.app.request.ProfileReq;
import com.ahdi.wallet.app.request.QueryBalanceReq;
import com.ahdi.wallet.app.request.QueryBankAccountReq;
import com.ahdi.wallet.app.request.QueryBindCardReq;
import com.ahdi.wallet.app.request.QueryRegisterReq;
import com.ahdi.wallet.app.request.RNASubmitAllReq;
import com.ahdi.wallet.app.request.RegisterReq;
import com.ahdi.wallet.app.request.ResetLoginPwdReq;
import com.ahdi.wallet.app.request.ResetPayPwdReq;
import com.ahdi.wallet.app.request.RnaTypeReq;
import com.ahdi.wallet.app.request.ScanCheckReq;
import com.ahdi.wallet.app.request.SelectCardTypeReq;
import com.ahdi.wallet.app.request.SetBindLimitReq;
import com.ahdi.wallet.app.request.SetPayPwdReq;
import com.ahdi.wallet.app.request.SetUserIconReq;
import com.ahdi.wallet.app.request.TransListReq;
import com.ahdi.wallet.app.request.UnBindAccountReq;
import com.ahdi.wallet.app.request.UnBindCardReq;
import com.ahdi.wallet.app.request.UpUserPhotoReq;
import com.ahdi.wallet.app.request.UpdateUserInfoReq;
import com.ahdi.wallet.app.request.UserAgreementReq;
import com.ahdi.wallet.app.request.UserInfoGuideSetReq;
import com.ahdi.wallet.app.request.VerifyCodeReq;
import com.ahdi.wallet.app.request.VoucherDetailReq;
import com.ahdi.wallet.app.request.VoucherListReq;
import com.ahdi.wallet.app.request.aaa.LoginSMSReq;
import com.ahdi.wallet.app.request.aaa.SmsCodeReq;
import com.ahdi.wallet.app.utils.ConstantsPayment;
import com.ahdi.wallet.cashier.requset.PayReportReq;
import com.ahdi.wallet.network.HttpReqAsyncTask;
import com.ahdi.wallet.network.framwork.Request;

/**
 * Date: 2017/10/14 下午3:24
 * Author: kay lau
 * Description:
 */
public class HttpReqApp {

    private static HttpReqApp instance;

    private HttpReqApp() {

    }

    public synchronized static HttpReqApp getInstance() {
        if (instance == null) {
            instance = new HttpReqApp();
        }
        return instance;
    }

    /**
     * 获取 验证码
     *
     * @param request
     * @param listener
     */
    public void onSMSCode(SmsCodeReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_SMS_CODE, request, listener)).execute();
    }

    /**
     * Login: 登录（SMS）
     * @param request
     * @param listener
     */
    public void onLoginSMS(LoginSMSReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_LOGIN_SMS, request, listener)).execute();
    }





    /**
     * 查询是否注册
     *
     * @param request
     * @param listener
     */
    public void onQueryRegister(QueryRegisterReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_QUERY_REGISTER, request, listener)).execute();
    }

    /**
     * 注册
     *
     * @param request
     * @param listener
     */
    public void onRegister(RegisterReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_REGISTER, request, listener)).execute();
    }

    /**
     * 登录
     *
     * @param request
     * @param listener
     */
    public void onLogin(LoginReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_LOGIN, request, listener)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 绑定IID
     *
     * @param request
     * @param listener
     */
    public void onIIDBind(IIDBind_LogoutReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_IID_BIND, request, listener)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 退出
     *
     * @param request
     * @param listener
     */
    public void onLogout(IIDBind_LogoutReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_LOGOUT, request, listener)).execute();
    }

    /**
     * 查询余额
     *
     * @param request
     * @param listener
     */
    public void onQueryBalance(QueryBalanceReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_QUERY_BALANCE, request, listener)).execute();
    }

    /**
     * 设置支付密码信息
     *
     * @param request
     * @param listener
     */
    public void onSetPayPWD(SetPayPwdReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_SET_PAY_PWD, request, listener)).execute();
    }

    /**
     * 修改支付密码
     *
     * @param request
     * @param listener
     */
    public void onResetPayPWD(ResetPayPwdReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_RESET_PAY_PWD, request, listener)).execute();
    }

    /**
     * 验证支付密码
     *
     * @param request
     * @param listener
     */
    public void onCheckPayPwd(CheckPayPwdReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_PAY_PWD_CHECK, request, listener)).execute();
    }

    /**
     * 查询绑定银行账户信息
     *
     * @param request
     * @param listener
     */
    public void onQueryBindAccountInfo(QueryBankAccountReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_QUERY_BIND_ACCOUNT_INFO, request, listener)).execute();
    }

    /**
     * 查询绑卡信息
     *
     * @param request
     * @param listener
     */
    public void onQueryBindCardInfo(QueryBindCardReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_QUERY_BIND_CARD_INFO, request, listener)).execute();
    }

    /**
     * 选择所要绑卡的类型
     *
     * @param request
     * @param listener
     */
    public void onSelectCardType(SelectCardTypeReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_SELECT_BIND_CARD_TYPE, request, listener)).execute();
    }

    /**
     * 查询绑卡详情
     *
     * @param request
     * @param listener
     */
    public void onBindCardDetails(BindCardDetailsReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_BIND_CARD_DETAILS, request, listener)).execute();
    }

    /**
     * 绑定银行账户
     *
     * @param request
     * @param listener
     */
    public void onBindAccount(BindAccountReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_BIND_ACCOUNT, request, listener)).execute();
    }

    /**
     * 解除绑定银行账户
     *
     * @param request
     * @param listener
     */
    public void unBindAccount(UnBindAccountReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_UNBIND_ACCOUNT, request, listener)).execute();
    }

    /**
     * 获取银行列表
     *
     * @param request
     * @param listener
     */
    public void queryBankAccountList(BankAccountListReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_QUERY_BANK_ACCOUNT_LIST, request, listener)).execute();
    }

    /**
     * 扫码检测
     *
     * @param request
     * @param listener
     */
    public void onScanCheck(ScanCheckReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_SCAN_CHECK, request, listener)).execute();
    }

    /**
     * 申请绑卡
     *
     * @param request
     * @param listener
     */
    public void onBindCard(ApplyBindCardReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_BIND_CARD, request, listener)).execute();
    }

    /**
     * 解除绑卡
     *
     * @param request
     * @param listener
     */
    public void unBindCard(UnBindCardReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_UNBIND_CARD, request, listener)).execute();
    }

    /**
     * 修改绑卡限额
     *
     * @param request
     * @param listener
     */
    public void setBindLimit(SetBindLimitReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_SETLIMIT_CARD, request, listener)).execute();
    }

    /**
     * 检查是否可以绑卡
     *
     * @param request
     * @param listener
     */
    public void isCanBind(IsCanBindReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_ISCANBIND_CARD, request, listener)).execute();
    }

    /**
     * 消息列表
     *
     * @param request
     * @param listener
     */
    public void onMsgList(MsgListReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_MSG_HOME_URL, request, listener)).execute();
    }

    /**
     * 用户概要信息
     *
     * @param request
     * @param listener
     */
    public void onAccountProfile(ProfileReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_ACCOUNT_PROFILE, request, listener)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 注册: 下发短信验证码
     *
     * @param request
     * @param listener
     */
    public void onSendVCodeForRegister(VerifyCodeReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_SVC_REGISTER, request, listener)).execute();
    }

    /**
     * 注册: 验证短信验证码
     *
     * @param request
     * @param listener
     */
    public void onCheckVCodeForRegister(VerifyCodeReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_VVC_REGISTER, request, listener)).execute();
    }

    /**
     * 忘记登录密码: 下发短信验证码
     *
     * @param request
     * @param listener
     */
    public void onSendVCodeForLoginPwd(VerifyCodeReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_PWD_SVC, request, listener)).execute();
    }

    /**
     * 忘记登录密码: 验证短信验证码
     *
     * @param request
     * @param listener
     */
    public void onCheckVCodeForLoginPwd(VerifyCodeReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_PWD_VVC, request, listener)).execute();
    }

    /**
     * 忘记支付密码: 下发短信验证码
     *
     * @param request
     * @param listener
     */
    public void onSendVCodeForPayPwd(VerifyCodeReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_PAY_PWD_SVC, request, listener)).execute();
    }

    /**
     * 忘记支付密码: 验证短信验证码
     *
     * @param request
     * @param listener
     */
    public void onCheckVCodeForPayPwd(VerifyCodeReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_PAY_PWD_VVC, request, listener)).execute();
    }

    /**
     * 身份证信息确认接口
     *
     * @param request
     * @param listener
     */
    public void onRnaSubmitAll(RNASubmitAllReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_RNA_SUBMIT_ALL, request, listener)).execute();
    }

    /**
     * 实名认证类型
     *
     * @param request
     * @param listener
     */
    public void onRnaType(RnaTypeReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_RNA_TYPES, request, listener)).execute();
    }

    /**
     * 实名认证审核结果查询
     *
     * @param request
     * @param listener
     */
    public void onAuditQR(AuditQRReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_AUDIT_QR, request, listener)).execute();
    }

    /**
     * 账单列表
     *
     * @param request
     * @param listener
     */
    public AsyncTask<Void, Void, Void> getTransList(TransListReq request, HttpReqTaskListener listener) {
        AsyncTask<Void, Void, Void> execute = new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_BILL_LIST, request, listener)).execute();
        return execute;
    }

    /**
     * 获取头像上传地址
     *
     * @param request
     * @param listener
     */
    public void getPhotoUpUrl(GetPhotoUpUrlReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_UP_URL, request, listener)).execute();
    }

    /**
     * 用户信息修改
     *
     * @param request
     * @param listener
     */
    public void updateUserInfo(UpdateUserInfoReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_UP, request, listener)).execute();
    }

    /**
     * 用户头像设置
     *
     * @param request
     * @param listener
     */
    public void setUserIcon(SetUserIconReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_SET_ICON, request, listener)).execute();
    }

    /**
     * 上传用户头像
     *
     * @param request
     * @param listener
     */
    public void upUserPhoto(UpUserPhotoReq request, HttpReqTaskListener listener) {
        HttpReqAsyncTask.Param param = new HttpReqAsyncTask.Param(request.getUpURL(), request, listener);
        param.isUpPhoto = true;
        param.imgByte = request.getImgByte();
        new HttpReqAsyncTask(param).execute();
    }

    /**
     * 检测登录密码
     *
     * @param request
     * @param listener
     */
    public void checkLoginPwd(CheckLoginPwdReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_PWD_CHECK, request, listener)).execute();
    }

    /**
     * 重置登录密码
     *
     * @param type     0:修改密码, 1:重置密码, 2:新建密码.
     * @param request
     * @param listener
     */
    public void resetLoginPwd(int type, ResetLoginPwdReq request, HttpReqTaskListener listener) {
        switch (type) {
            case Constants.LOCAL_INTERFACE_MODIFY_LOGIN_PWD:
                new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_PWD_MODIFY, request, listener)).execute();
                break;
            case Constants.LOCAL_INTERFACE_RESET_LOGIN_PWD:
                new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_PWD_SET, request, listener)).execute();
                break;
            case Constants.LOCAL_INTERFACE_CREATE_LOGIN_PWD:
                new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_PWD_CREATE, request, listener)).execute();
                break;
        }
    }

    /**
     * 获取配置信息
     *
     * @param request
     * @param listener
     */
    public void getConfig(Request request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_CLIENT_CFG, request, listener)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 用户协议内容加载
     *
     * @param request
     * @param listener
     */
    public void onGetUserAgreement(UserAgreementReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_USER_AGREEMENT, request, listener)).execute();
    }

    /**
     * 用户信息引导注册
     *
     * @param request
     * @param listener
     */
    public void userInfoGuideSet(UserInfoGuideSetReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_USER_INFO_GUIDE_SET, request, listener)).execute();
    }

    /**
     * 首页banner
     *
     * @param request
     * @param listener
     */
    public void bannerInfo(BannerInfoReq request, HttpReqTaskListener listener) {
        HttpReqAsyncTask.Param param = new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_BANNER_INFO, request, listener);
        param.isGetMethod = true;
        new HttpReqAsyncTask(param).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * getBankVaUrl
     *
     * @param request
     * @param listener
     */
    public void getBankVaUrl(GetBankVAReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_BANK_VA, request, listener)).execute();
    }

    /**
     * getBankUrl
     *
     * @param request
     * @param listener
     */
    public void getBankUrl(GetBankVAReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_BANK_VA, request, listener)).execute();
    }

    /**
     * 电话区号
     *
     * @param request
     * @param listener
     */
    public void getPhoneAreaCode(PhoneAreaCodeReq request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_PHONE_AREA_CODE, request, listener)).execute();
    }


    /**
     * 开启指纹支付
     *
     * @param req
     * @param listener
     */
    public void onOpenTouchIDPay(OpenTouchIDPayReq req, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_TouchID_PAY_OPEN, req, listener)).execute();
    }

    /**
     * 关闭指纹支付
     *
     * @param req
     * @param listener
     */
    public void onCloseTouchIDPay(CloseTouchIDPayReq req, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_TouchID_PAY_CLOSE, req, listener)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 优惠券列表
     *
     * @param req
     * @param listener
     */
    public AsyncTask onVoucherList(VoucherListReq req, HttpReqTaskListener listener) {
        return new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_VOUCHER_LIST, req, listener)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 优惠券详情
     *
     * @param req
     * @param listener
     */
    public void onVoucherDetail(VoucherDetailReq req, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_VOUCHER_DETAIL, req, listener)).execute();
    }

    /**
     * 支付上报
     *
     * @param req
     * @param listener
     */
    public void onPayReport(PayReportReq req, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsPayment.MODULE_PAY_REPORT, req, listener)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
