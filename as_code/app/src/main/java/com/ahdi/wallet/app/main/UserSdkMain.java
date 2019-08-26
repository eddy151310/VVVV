package com.ahdi.wallet.app.main;

import android.content.Context;
import android.text.TextUtils;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.HttpReqApp;
import com.ahdi.wallet.app.bean.LoginBean;
import com.ahdi.wallet.app.bean.RegisterBean;
import com.ahdi.wallet.app.bean.UserInfoGuideSetBean;
import com.ahdi.wallet.app.callback.UserSdkCallBack;
import com.ahdi.wallet.app.listener.user.CheckLoginPwdListener;
import com.ahdi.wallet.app.listener.user.GetPhotoUpUrlListener;
import com.ahdi.wallet.app.listener.user.LoginListener;
import com.ahdi.wallet.app.listener.user.QueryRegisterListener;
import com.ahdi.wallet.app.listener.user.RegisterListener;
import com.ahdi.wallet.app.listener.user.ResetLoginPwdListener;
import com.ahdi.wallet.app.listener.user.TerminalBind_LogoutListener;
import com.ahdi.wallet.app.listener.user.UpUserPhotoListener;
import com.ahdi.wallet.app.listener.user.UpdateUserInfoListener;
import com.ahdi.wallet.app.listener.user.UserInfoGuideSetListener;
import com.ahdi.wallet.app.listener.user.VerifyCodeListener;
import com.ahdi.wallet.app.request.CheckLoginPwdReq;
import com.ahdi.wallet.app.request.GetPhotoUpUrlReq;
import com.ahdi.wallet.app.request.IIDBind_LogoutReq;
import com.ahdi.wallet.app.request.LoginReq;
import com.ahdi.wallet.app.request.QueryRegisterReq;
import com.ahdi.wallet.app.request.RegisterReq;
import com.ahdi.wallet.app.request.ResetLoginPwdReq;
import com.ahdi.wallet.app.request.SetUserIconReq;
import com.ahdi.wallet.app.request.UpUserPhotoReq;
import com.ahdi.wallet.app.request.UpdateUserInfoReq;
import com.ahdi.wallet.app.request.UserInfoGuideSetReq;
import com.ahdi.wallet.app.request.VerifyCodeReq;
import com.ahdi.wallet.app.request.aaa.LoginSMSReq;
import com.ahdi.wallet.app.request.aaa.SmsCodeReq;
import com.ahdi.wallet.app.schemas.AvatarSchema;
import com.ahdi.wallet.app.schemas.LoginSchema;
import com.ahdi.wallet.app.schemas.RegisterSchema;
import com.ahdi.lib.utils.config.Constants;

import org.json.JSONObject;

/**
 * Date: 2018/1/31 下午2:30
 * Author: kay lau
 * Description:
 */
public class UserSdkMain {

    private static UserSdkMain instance;

    public String default_error = "";

    public UserSdkCallBack callBack;

    private UserSdkMain() {
    }

    public static UserSdkMain getInstance() {
        if (instance == null) {
            synchronized (UserSdkMain.class) {
                if (instance == null) {
                    instance = new UserSdkMain();
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
     * 登录（sms登录）
     */
    public void loginSMS(Context context, String phoneNum, String smsCode, String smsOrderid, UserSdkCallBack callBack) {
        initDefaultError(context);
        LoginSMSReq request = new LoginSMSReq(phoneNum ,smsCode , smsOrderid);
        HttpReqApp.getInstance().onLoginSMS(request, new VerifyCodeListener(callBack));
    }

    /**
     * 获取短信验证码
     */
    public void getSMSCode(Context context, String phoneNum, UserSdkCallBack callBack) {
        initDefaultError(context);
        SmsCodeReq request = new SmsCodeReq(phoneNum);
        HttpReqApp.getInstance().onSMSCode(request, new VerifyCodeListener(callBack));
    }


    /**
     * 查询是否注册
     *
     * @param context
     * @param loginName
     * @param type
     */
    public void queryRegister(Context context, String loginName, int type) {
        initDefaultError(context);
        QueryRegisterReq request = new QueryRegisterReq(loginName, type);
        HttpReqApp.getInstance().onQueryRegister(request, new QueryRegisterListener());
    }

    /**
     * 注册
     *
     * @param context
     * @param bean
     */
    public void register(Context context, RegisterBean bean) {
        initDefaultError(context);
        RegisterSchema schema = new RegisterSchema(bean);
        RegisterReq request = new RegisterReq(schema, bean.getToken());
        HttpReqApp.getInstance().onRegister(request, new RegisterListener());
    }


    /**
     * 注册: 验证短信验证码
     */
    public void checkVCodeForRegister(Context context, String phoneNum, String verifyCode, UserSdkCallBack callBack) {
        initDefaultError(context);
        VerifyCodeReq request = new VerifyCodeReq(phoneNum, verifyCode, "");
        HttpReqApp.getInstance().onCheckVCodeForRegister(request, new VerifyCodeListener(callBack));
    }

    /**
     * 忘记登录密码：下发短信验证码
     */
    public void sendVCodeForLoginPwd(Context context, String phoneNum, String sid, UserSdkCallBack callBack) {
        initDefaultError(context);
        VerifyCodeReq request = new VerifyCodeReq(phoneNum);
        HttpReqApp.getInstance().onSendVCodeForLoginPwd(request, new VerifyCodeListener(callBack));
    }

    /**
     * 忘记登录密码: 验证短信验证码
     */
    public void checkVCodeForLoginPwd(Context context, String phoneNum, String verifyCode, String sid, UserSdkCallBack callBack) {
        initDefaultError(context);
        VerifyCodeReq request = new VerifyCodeReq(phoneNum, verifyCode, sid);
        HttpReqApp.getInstance().onCheckVCodeForLoginPwd(request, new VerifyCodeListener(callBack));
    }

    /**
     * 登录
     *
     * @param context
     * @param bean
     * @param callBack
     */
    public void login(Context context, LoginBean bean, UserSdkCallBack callBack) {
        initDefaultError(context);
        String loginType = bean.getLoginType();
        String loginName = bean.getLoginName();
        String loginPassword = bean.getLoginPassword();
        LoginSchema schema = new LoginSchema(loginType, loginName, loginPassword);
        LoginReq request = new LoginReq(schema);
        HttpReqApp.getInstance().onLogin(request, new LoginListener(callBack));
    }

    /**
     * 绑定IID
     *
     * @param context
     * @param sid
     * @param callBack
     */
    public void iidBind(Context context, String sid, UserSdkCallBack callBack) {
        initDefaultError(context);
        HttpReqApp.getInstance().onIIDBind(new IIDBind_LogoutReq(sid), new TerminalBind_LogoutListener(callBack));
    }

    /**
     * 登出
     *
     * @param context
     * @param sid
     * @param callBack
     */
    public void logout(Context context, String sid, UserSdkCallBack callBack) {
        initDefaultError(context);
        HttpReqApp.getInstance().onLogout(new IIDBind_LogoutReq(sid), new TerminalBind_LogoutListener(callBack));
    }

    /**
     * 用户信息修改
     *
     * @param context
     * @param content
     * @param type
     * @param sid
     * @param callBack
     */
    public void updateUserInfo(Context context, String content, int type, String sid, UserSdkCallBack callBack) {
        initDefaultError(context);
        UpdateUserInfoReq request = new UpdateUserInfoReq(sid, content, type);
        HttpReqApp.getInstance().updateUserInfo(request, new UpdateUserInfoListener(callBack));
    }

    /**
     * 用户头像修改
     *
     * @param context
     * @param avatarSchema
     * @param sid
     * @param callBack
     */
    public void setUserIcon(Context context, AvatarSchema avatarSchema, String sid, UserSdkCallBack callBack) {
        initDefaultError(context);
        SetUserIconReq request = new SetUserIconReq(sid, avatarSchema);
        HttpReqApp.getInstance().setUserIcon(request, new UpdateUserInfoListener(callBack));
    }

    /**
     * 获取图片上传地址
     *
     * @param context
     * @param sid
     * @param callBack
     */
    public void getPhotoUpUrl(Context context, String sid, UserSdkCallBack callBack) {
        initDefaultError(context);
        GetPhotoUpUrlReq request = new GetPhotoUpUrlReq(sid);
        HttpReqApp.getInstance().getPhotoUpUrl(request, new GetPhotoUpUrlListener(callBack));
    }

    /**
     * 上传头像图片
     *
     * @param context
     * @param upUrl
     * @param imgByte
     * @param callBack
     */
    public void upUserPhoto(Context context, String upUrl, byte[] imgByte, UserSdkCallBack callBack) {
        initDefaultError(context);
        UpUserPhotoReq request = new UpUserPhotoReq(upUrl, imgByte);
        HttpReqApp.getInstance().upUserPhoto(request, new UpUserPhotoListener(callBack));
    }

    /**
     * 检查登录密码
     *
     * @param context
     * @param pwd
     * @param sid
     * @param callBack
     */
    public void checkLoginPwd(Context context, String pwd, String sid, UserSdkCallBack callBack) {
        initDefaultError(context);
        CheckLoginPwdReq request = new CheckLoginPwdReq(sid, pwd);
        HttpReqApp.getInstance().checkLoginPwd(request, new CheckLoginPwdListener(callBack));
    }

    /**
     * 重置登录密码
     *
     * @param type 0:修改密码 ,1: 重置密码,2:新建密码
     * @param context
     * @param pwd
     * @param token
     * @param sid
     * @param callBack
     */
    public void resetLoginPwd(int type, Context context, String pwd, String token, String sid, UserSdkCallBack callBack) {
        initDefaultError(context);
        ResetLoginPwdReq request = new ResetLoginPwdReq(sid, pwd, token);
        HttpReqApp.getInstance().resetLoginPwd(type, request, new ResetLoginPwdListener(callBack));
    }

    /**
     * 用户信息引导设置
     *
     * @param context
     * @param sid
     * @param bean
     * @param callBack
     */
    public void userInfoGuideSet(Context context, String sid, UserInfoGuideSetBean bean, UserSdkCallBack callBack) {
        initDefaultError(context);
        UserInfoGuideSetReq request = new UserInfoGuideSetReq(sid, bean);
        HttpReqApp.getInstance().userInfoGuideSet(request, new UserInfoGuideSetListener(callBack));
    }

    /**
     * 忘记支付密码：下发短信验证码
     */
    public void sendVCodeForPayPwd(Context context, String phoneNum, String sid, UserSdkCallBack callBack) {
        initDefaultError(context);
        VerifyCodeReq request = new VerifyCodeReq(phoneNum);
        HttpReqApp.getInstance().onSendVCodeForPayPwd(request, new VerifyCodeListener(callBack));
    }

    /**
     * 忘记支付密码: 验证短信验证码
     */
    public void checkVCodeForPayPwd(Context context, String phoneNum, String verifyCode, String sid, UserSdkCallBack callBack) {
        initDefaultError(context);
        VerifyCodeReq request = new VerifyCodeReq(phoneNum, verifyCode, sid);
        HttpReqApp.getInstance().onCheckVCodeForPayPwd(request, new VerifyCodeListener(callBack));
    }

}
