package com.ahdi.lib.utils.utils;

import android.app.NotificationManager;
import android.content.Context;

import com.ahdi.lib.utils.config.Constants;


public class CleanConfigUtil {


    /**
     * 登出（主动、被动）需要清理相关缓存
     */
    public static void cleanAllConfig() {
        cleanVoucher();
        cleanNotification();
        cleanProfileUserManager();
        LanguageUtil.cleanRecreateState(AppGlobalUtil.getInstance().getContext());
    }

    /**
     * 清理DC
     */
    public static void cleanVoucher() {
        AppGlobalUtil.getInstance().removeKey(AppGlobalUtil.getInstance().getContext(), Constants.SP_KEY_SID);
    }


    /**
     * 清理 notification
     */
    public static void cleanNotification(){
        NotificationManager notificationManager = (NotificationManager) AppGlobalUtil.getInstance().getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }


    /**
     * 清理 ProfileUserUtil
     */
    public static void cleanProfileUserManager(){
        ProfileUserUtil.getInstance().cleanAccountUserData();
    }

    public static void cleanLoginInfo() {
        AppGlobalUtil.getInstance().removeKey(AppGlobalUtil.getInstance().getContext(), Constants.SP_KEY_SID);
        AppGlobalUtil.getInstance().removeKey(AppGlobalUtil.getInstance().getContext(), Constants.SP_KEY_LOGIN_NAME);
        AppGlobalUtil.getInstance().removeKey(AppGlobalUtil.getInstance().getContext(), Constants.SP_KEY_USER_ID);
    }


}
