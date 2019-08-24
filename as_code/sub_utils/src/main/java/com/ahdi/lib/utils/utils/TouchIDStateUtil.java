package com.ahdi.lib.utils.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Date: 2018/7/19 下午3:24
 * Author: kay lau
 * Description:
 */
public class TouchIDStateUtil {

    private static final String key_unlock_login = "_t_unlock";
    private static final String key_unlock_login_avatar = "_t_unlock_Avatar";
    private static final String key_pay = "_t_pay";
    private static final String key_open_touch_pay_Ver = "_t_pay_touch_Ver";
    private static final String key_open_touch_pay_Key = "_t_pay_touch_Key";
    private static final String key_guide_unlock = "_t_guide_unlock";
    private static final String key_guide_pay = "_t_guide_pay";

    //-------------------------------对外提供 start-------------------------------------

    /**
     * 获取指纹解锁登录的头像url
     *
     * @param context
     * @param lname
     * @return
     */
    public static String getUnlockLoginAvatar(Context context, String lname) {
        return getString(context, lname + key_unlock_login_avatar, lname);
    }

    /**
     * 保存指纹解锁登录的头像url
     *
     * @param context
     * @param lname
     * @param url
     */
    public static void setTouchIDUnlockAvatar(Context context, String lname, String url) {
        putString(context, lname + key_unlock_login_avatar, lname, url);
    }

    /**
     * 保存指纹解锁的状态
     *
     * @param context
     * @param lname
     * @param data
     */
    public static void setTouchIDUnlockState(Context context, String lname, String data) {
        putString(context, lname + key_unlock_login, lname, data);
    }

    /**
     * 是否开启指纹解锁登录
     *
     * @param context
     * @param lname
     * @return
     */
    public static boolean isStartTouchIDUnlock(Context context, String lname) {
        return !TextUtils.isEmpty(getTouchIDUnlockState(context, lname));
    }

    public static String getTouchIDUnlockState(Context context, String lname) {
        return getString(context, lname + key_unlock_login, lname).trim();
    }

    /**
     * 清理lname对应的指纹登录所有数据
     *
     * @param context
     */
    public static void clearTouchIDUnlockData(Context context, String lname) {
        if (context == null) {
            return;
        }
        clearSharedPreferences(context, lname + key_unlock_login);
    }

    /**
     * 是否引导开启指纹解锁
     *
     * @param context
     * @param lname
     * @return true: 跳过, 下次不再提示引导  false: 继续引导
     */
    public static boolean isSkipGuideUnlock(Context context, String lname) {
        return getBoolean(context, lname + key_guide_unlock, lname);
    }

    public static void setGuideUnlockState(Context context, String lname, boolean isGuideOpen) {
        if (isGuideOpen) {
            putBoolean(context, lname + key_guide_unlock, lname, isGuideOpen);
        } else {
            clearSharedPreferences(context, lname);
        }
    }

    /**
     * 清理lname对应的指纹支付所有数据
     *
     * @param context
     */
    public static void clearTouchIDPayData(Context context, String lname) {
        if (context == null) {
            return;
        }
        clearSharedPreferences(context, lname + key_pay);
    }

    /**
     * 保存指纹支付登录的状态
     *
     * @param context
     * @param lname
     * @param data    rsa私钥
     */
    public static void setTouchIDPaymentState(Context context, String lname, String data) {
        putString(context, lname + key_pay, lname, data);

    }

    /**
     * 是否开启指纹支付
     *
     * @param context
     * @param lname
     * @return true: 跳过, 下次不再提示引导  false: 继续引导
     */
    public static boolean isStartTouchIDPayment(Context context, String lname) {
        return !TextUtils.isEmpty(getTouchIDPayRsaPrivateKey(context, lname));
    }

    public static String getTouchIDPayRsaPrivateKey(Context context, String lname) {
        return getString(context, lname + key_pay, lname).trim();
    }

    /**
     * 是否需要引导开启指纹支付
     *
     * @param context
     * @param lname
     * @return
     */
    public static boolean isSkipGuidePay(Context context, String lname) {
        return getBoolean(context, lname + key_pay, lname + key_guide_pay);
    }

    public static void setGuidePayState(Context context, String lname, boolean isGuideOpen) {
        if (isGuideOpen) {
            putBoolean(context, lname + key_pay, lname + key_guide_pay, isGuideOpen);
        } else {
            clearSharedPreferences(context, lname);
        }
    }

    /**
     * 指纹支付开启成功, 保存后台返回的凭证版本号
     *
     * @param context
     * @param lname
     * @param data
     */
    public static void saveTouchIDPayVer(Context context, String lname, String data) {
        putString(context, lname + key_pay, lname + key_open_touch_pay_Ver, data);

    }

    public static String getTouchIDPayVer(Context context, String lname) {
        return getString(context, lname + key_pay, lname + key_open_touch_pay_Ver);
    }

    /**
     * 指纹支付开启成功, 保存后台返回的凭证
     *
     * @param context
     * @param lname
     * @param data
     */
    public static void saveTouchIDPayKey(Context context, String lname, String data) {
        putString(context, lname + key_pay, lname + key_open_touch_pay_Key, data);

    }

    public static String getTouchIDPayKey(Context context, String lname) {
        return getString(context, lname + key_pay, lname + key_open_touch_pay_Key);
    }


    //-----------------------------对外提供 end---------------------------------------

    private static void clearSharedPreferences(Context context, String spkey) {
        SharedPreferences preferences = context.getSharedPreferences(spkey, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.clear().commit();
    }

    private static String getString(Context context, String spkey, String dataKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spkey, Activity.MODE_PRIVATE);
        return sharedPreferences.getString(dataKey, "");
    }

    private static void putString(Context context, String spkey, String dataKey, String str) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spkey, Activity.MODE_PRIVATE);
        sharedPreferences.edit().putString(dataKey, str).commit();
    }

    private static void putBoolean(Context context, String spkey, String dataKey, boolean bool) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spkey, Activity.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(dataKey, bool).commit();
    }

    private static boolean getBoolean(Context context, String spkey, String dataKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spkey, Activity.MODE_PRIVATE);
        return sharedPreferences.getBoolean(dataKey, false);
    }
}
