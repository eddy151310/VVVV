package com.ahdi.lib.utils.utils;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import com.ahdi.lib.utils.R;
import com.ahdi.lib.utils.config.ConfigHelper;
import com.ahdi.lib.utils.config.Constants;

public class ToolUtils {

    private static long lastClickTime;

    /**
     * 防止多次点击
     *
     * @return
     */
    public static boolean isCanClick() {

        try {
            long time = System.currentTimeMillis();
            long offSetTime = time - lastClickTime;
            if (Math.abs(offSetTime) > 500) {
                lastClickTime = time;
                return true;
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    private static long tLastClickTime;

    /**
     * 防止多次点击
     *
     * @param t 可点击的间隔时间
     * @return
     */
    public static boolean isCanClick(long t) {

        try {
            long time = System.currentTimeMillis();
            long offSetTime = time - tLastClickTime;
            if (Math.abs(offSetTime) > t) {
                tLastClickTime = time;
                return true;
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 发送广播返回到登录页 (发送广播设置包名, 避免其他应用收到类似广播---如 push sdk 之前出现此问题)
     *
     * @param msg
     */
    public static void sendBroadcastBackLogin(String msg) {
        AppGlobalUtil appUtil = AppGlobalUtil.getInstance();
        appUtil.sendBroadcast(appUtil.getContext(), Constants.MSG_KEY,
                msg, Constants.ACTION_ACCOUNT_OUT);
    }

    public static int getColor(Context context, int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.getColor(context, colorId);

        } else {
            return context.getResources().getColor(colorId);
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int parseInt(String intStr) {
        int parse = 0;
        try {
            parse = Integer.parseInt(intStr);
        } catch (NumberFormatException e) {
            LogUtil.e("tag", e.toString());
        }
        return parse;
    }

}