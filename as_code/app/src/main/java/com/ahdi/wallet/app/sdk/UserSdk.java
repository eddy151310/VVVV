package com.ahdi.wallet.app.sdk;

import android.content.Context;
import android.text.TextUtils;

import com.ahdi.wallet.app.bean.LoginBean;
import com.ahdi.wallet.app.bean.RegisterBean;
import com.ahdi.wallet.app.bean.UserInfoGuideSetBean;
import com.ahdi.wallet.app.callback.UserSdkCallBack;
import com.ahdi.wallet.app.main.UserSdkMain;
import com.ahdi.wallet.app.schemas.AvatarSchema;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;

/**
 * Date: 2018/1/4 上午11:51
 * Author: kay lau
 * Description:
 */
public class UserSdk {

    public static final String TAG = UserSdk.class.getSimpleName();

    public static final String LOCAL_PAY_SUCCESS = Constants.RET_CODE_SUCCESS;   // 支付完成

    public static final String LOCAL_PAY_PARAM_ERROR = "-3";                     // 参数错误

    public static final String LOCAL_PAY_USER_CANCEL = "-4";                     // 用户取消, 关闭收银台

    public static final String LOCAL_PAY_NETWORK_EXCEPTION = Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION;               // 网络异常

    public static final String LOCAL_PAY_SYSTEM_EXCEPTION = "-6";                // 系统响应解析异常

    public static final String LOCAL_PAY_QUERY_CANCEL = "-7";                    // 查询结果后取消 继续查询

    /**
     * 昵称
     **/
    public static final int TYPE_NICKNAME = 1;
    /**
     * 性别
     **/
    public static final int TYPE_GENDER = 2;
    /**
     * 生日
     **/
    public static final int TYPE_BIRTHDAY = 3;
    /**
     * 邮箱
     **/
    public static final int TYPE_EMAIL = 4;

    private static boolean checkContextAndCallback(Context context, UserSdkCallBack callBack) {
        if (context == null) {
            LogUtil.d(TAG, "context can't be empty");
            return true;
        }

        if (callBack == null) {
            LogUtil.d(TAG, "callback can't be empty");
            return true;
        }
        return false;
    }

    private static boolean checkSid(String sid, UserSdkCallBack callBack) {
        if (TextUtils.isEmpty(sid)) {
            LogUtil.d(TAG, "sid can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "sid can't be empty", null);
            return false;
        }
        return true;
    }

    /**
     * 查询是否注册
     *
     * @param context
     * @param loginName
     * @param type      0查询是否注册  1查询是否存在
     * @param callBack
     */
    public static void queryRegister(Context context, String loginName, int type, UserSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (TextUtils.isEmpty(loginName)) {
            LogUtil.d(TAG, "loginName cannot be empty");
            return;
        }
        UserSdkMain.getInstance().callBack = callBack;
        UserSdkMain.getInstance().queryRegister(context, loginName, type);
    }

    /**
     * 注册
     *
     * @param context
     * @param bean
     * @param callBack
     */
    public static void register(Context context, RegisterBean bean, UserSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (bean == null) {
            LogUtil.d(TAG, "RegisterBean cannot be empty");
            return;
        }
        UserSdkMain.getInstance().callBack = callBack;
        UserSdkMain.getInstance().register(context, bean);
    }

    /**
     * 登录
     *
     * @param context
     * @param bean
     * @param callBack
     */
    public static void login(Context context, LoginBean bean, UserSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (bean == null) {
            LogUtil.d(TAG, "LoginBean cannot be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "LoginBean can't be empty", null);
            return;
        }
        UserSdkMain.getInstance().login(context, bean, callBack);
    }

    public static void iidBind(Context context, String sid, UserSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!checkSid(sid, callBack)) {
            return;
        }
        UserSdkMain.getInstance().iidBind(context, sid, callBack);
    }

    /**
     * 注册: 下发短信验证码
     *
     * @param context
     * @param phoneNum
     * @param callBack
     */
    public static void sendVCodeForRegister(Context context, String phoneNum, UserSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }

        if (TextUtils.isEmpty(phoneNum)) {
            LogUtil.d(TAG, "phoneNum can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "phoneNum can't be empty", null);
            return;
        }

        UserSdkMain.getInstance().sendVCodeForRegister(context, phoneNum, callBack);

    }

    /**
     * 登录: 下发短信验证码
     *
     * @param context
     * @param phoneNum
     * @param callBack
     */
    public static void sendVCodeForLogin(Context context, String phoneNum, UserSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }

        if (TextUtils.isEmpty(phoneNum)) {
            LogUtil.d(TAG, "phoneNum can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "phoneNum can't be empty", null);
            return;
        }

        UserSdkMain.getInstance().sendVCodeForLogin(context, phoneNum, callBack);

    }

    /**
     * 注册: 验证短信验证码
     *
     * @param context
     * @param phoneNum
     * @param verifyCode
     * @param callBack
     */
    public static void checkVCodeForRegister(Context context, String phoneNum, String verifyCode, UserSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (TextUtils.isEmpty(phoneNum)) {
            LogUtil.d(TAG, "phoneNum can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "phoneNum can't be empty", null);
            return;
        }
        if (TextUtils.isEmpty(verifyCode)) {
            LogUtil.d(TAG, "verifyCode can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "verifyCode can't be empty", null);
            return;
        }
        UserSdkMain.getInstance().checkVCodeForRegister(context, phoneNum, verifyCode, callBack);
    }

    /**
     * 忘记登录密码: 下发短信验证码
     *
     * @param context
     * @param phoneNum
     * @param sid      登录状态下发需要sid
     * @param callBack
     */
    public static void sendVCodeForLoginPwd(Context context, String phoneNum, String sid, UserSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }

        if (TextUtils.isEmpty(phoneNum)) {
            LogUtil.d(TAG, "phoneNum can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "phoneNum can't be empty", null);
            return;
        }

        UserSdkMain.getInstance().sendVCodeForLoginPwd(context, phoneNum, sid, callBack);
    }

    /**
     * 忘记登录密码: 验证短信验证码
     *
     * @param context
     * @param phoneNum
     * @param verifyCode
     * @param callBack
     */
    public static void checkVCodeForLoginPwd(Context context, String phoneNum, String verifyCode, String sid, UserSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (TextUtils.isEmpty(phoneNum)) {
            LogUtil.d(TAG, "phoneNum can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "phoneNum can't be empty", null);
            return;
        }
        if (TextUtils.isEmpty(verifyCode)) {
            LogUtil.d(TAG, "verifyCode can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "verifyCode can't be empty", null);
            return;
        }
        UserSdkMain.getInstance().checkVCodeForLoginPwd(context, phoneNum, verifyCode, sid, callBack);
    }

    /**
     * 登出
     *
     * @param context
     * @param sid
     * @param callBack
     */
    public static void logout(Context context, String sid, UserSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }

        if (TextUtils.isEmpty(sid)) {
            LogUtil.d(TAG, "sid can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "sid can't be empty", null);
            return;
        }
        UserSdkMain.getInstance().logout(context, sid, callBack);
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
    public static void updateUserInfo(Context context, String content, int type, String sid, UserSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (TextUtils.isEmpty(sid)) {
            LogUtil.d(TAG, "sid can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "sid can't be empty", null);
            return;
        }
        if (TextUtils.isEmpty(content) && type == 0) {
            LogUtil.d(TAG, "content and type can't be both empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "content and type can't be both empty", null);
            return;
        }
        UserSdkMain.getInstance().updateUserInfo(context, content, type, sid, callBack);
    }

    /**
     * 用户头像设置
     *
     * @param context
     * @param avatarSchema
     * @param sid
     * @param callBack
     */
    public static void setUserIcon(Context context, AvatarSchema avatarSchema, String sid, UserSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (TextUtils.isEmpty(sid)) {
            LogUtil.d(TAG, "sid can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "sid can't be empty", null);
            return;
        }
        if (avatarSchema == null) {
            LogUtil.d(TAG, "avatarSchema can't be both empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "avatarSchema can't be both empty", null);
            return;
        }
        UserSdkMain.getInstance().setUserIcon(context, avatarSchema, sid, callBack);
    }

    /**
     * 获取图片上传地址
     *
     * @param context
     * @param sid
     * @param callBack
     */
    public static void getPhotoUpUrl(Context context, String sid, UserSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (TextUtils.isEmpty(sid)) {
            LogUtil.d(TAG, "sid can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "sid can't be empty", null);
            return;
        }
        UserSdkMain.getInstance().getPhotoUpUrl(context, sid, callBack);
    }

    /**
     * 上传头像图片
     *
     * @param context
     * @param upUrl
     * @param callBack
     */
    public static void upUserPhoto(Context context, String upUrl, byte[] imgByte, UserSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (TextUtils.isEmpty(upUrl)) {
            LogUtil.d(TAG, "upUrl can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "upUrl can't be empty", null);
            return;
        }
        if (imgByte == null || imgByte.length == 0) {
            LogUtil.d(TAG, "image can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "image can't be empty", null);
            return;
        }
        UserSdkMain.getInstance().upUserPhoto(context, upUrl, imgByte, callBack);
    }

    /**
     * 检查登录密码
     *
     * @param context
     * @param pwd
     * @param sid
     * @param callBack
     */
    public static void checkLoginPwd(Context context, String pwd, String sid, UserSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (TextUtils.isEmpty(sid)) {
            LogUtil.d(TAG, "sid can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "sid can't be empty", null);
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            LogUtil.d(TAG, "pwd can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "pwd can't be empty", null);
            return;
        }
        UserSdkMain.getInstance().checkLoginPwd(context, pwd, sid, callBack);
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
    public static void resetLoginPwd(int type, Context context, String pwd, String token, String sid, UserSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (TextUtils.isEmpty(pwd)){
            LogUtil.d(TAG, "pwd can't be both empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "pwd can't be both empty", null);
            return;
        }
        if (type != 2){
            if (TextUtils.isEmpty(token)) {
                LogUtil.d(TAG, "token can't be both empty");
                callBack.onResult(LOCAL_PAY_PARAM_ERROR, "token can't be both empty", null);
                return;
            }
        }
        UserSdkMain.getInstance().resetLoginPwd(type, context, pwd, token, sid, callBack);
    }

    /**
     * 用户信息引导设置
     *
     * @param context
     * @param sid
     * @param bean
     * @param callBack
     */
    public static void userInfoGuideSet(Context context, String sid, UserInfoGuideSetBean bean, UserSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (bean == null) {
            LogUtil.d(TAG, "UserInfoGuideSetBean can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "UserInfoGuideSetBean can't be empty", null);
            return;
        }
        UserSdkMain.getInstance().userInfoGuideSet(context, sid, bean, callBack);
    }

    /**
     * 忘记支付密码: 下发短信验证码
     *
     * @param context
     * @param phoneNum
     * @param sid
     * @param callBack
     */
    public static void sendVCodeForPayPwd(Context context, String phoneNum, String sid, UserSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }

        if (TextUtils.isEmpty(phoneNum)) {
            LogUtil.d(TAG, "phoneNum can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "phoneNum can't be empty", null);
            return;
        }

        UserSdkMain.getInstance().sendVCodeForPayPwd(context, phoneNum, sid, callBack);
    }

    /**
     * 忘记支付密码: 验证短信验证码
     *
     * @param context
     * @param phoneNum
     * @param verifyCode
     * @param callBack
     */
    public static void checkVCodeForPayPwd(Context context, String phoneNum, String verifyCode, String sid, UserSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (TextUtils.isEmpty(phoneNum)) {
            LogUtil.d(TAG, "phoneNum can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "phoneNum can't be empty", null);
            return;
        }
        if (TextUtils.isEmpty(verifyCode)) {
            LogUtil.d(TAG, "verifyCode can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "verifyCode can't be empty", null);
            return;
        }
        UserSdkMain.getInstance().checkVCodeForPayPwd(context, phoneNum, verifyCode, sid, callBack);
    }
}