package com.ahdi.lib.utils.utils;

import android.content.Context;
import android.text.TextUtils;

import com.ahdi.lib.utils.config.Constants;

/**
 * @author xiaoniu
 * @date 2018/10/9.
 *
 * 语言工具类
 * 切换语言，保存语言
 */
public class LanguageUtil {

    private static final String TAG = "LanguageUtil";

    /**
     * 获取当前设置的语言
     * @param context
     * @return
     */
    public static String getLanguage(Context context) {
        String language = AppGlobalUtil.getInstance().getString(context, Constants.LOCAL_LAN_KEY);
        LogUtil.d(TAG, "=======当前设定语言：=============：" + language);
        if (TextUtils.isEmpty(language)) {
            setLanguage(context, Constants.LOCAL_LAN_DEFAULT);
            language = Constants.LOCAL_LAN_DEFAULT;
            LogUtil.d(TAG, "=======设置默认语言=============" + language);
        }
        return language;
    }

    public static void setLanguage(Context context, String language) {
        AppGlobalUtil.getInstance().putString(context, Constants.LOCAL_LAN_KEY, language);
    }

    /**
     * 清理标记的重启activity
     * @param context
     */
    public static void cleanRecreateState(Context context) {
        AppGlobalUtil.getInstance().putString(context, Constants.LOCAL_RECREATE_APP_MAIN_ACTIVITY, "");
        AppGlobalUtil.getInstance().putString(context, Constants.LOCAL_RECREATE_SETTING_ACTIVITY, "");
    }

    /**
     * 标记需要重启的activity
     * @param context
     */
    public static void switchLanguageState(Context context) {
        AppGlobalUtil.getInstance().putString(context, Constants.LOCAL_RECREATE_APP_MAIN_ACTIVITY, Constants.LOCAL_RECREATE_APP_MAIN_ACTIVITY);
        AppGlobalUtil.getInstance().putString(context, Constants.LOCAL_RECREATE_SETTING_ACTIVITY, Constants.LOCAL_RECREATE_SETTING_ACTIVITY);
    }

}
