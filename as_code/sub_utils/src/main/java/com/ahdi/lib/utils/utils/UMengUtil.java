package com.ahdi.lib.utils.utils;

import android.content.Context;
import android.text.TextUtils;

import com.ahdi.lib.utils.BuildConfig;
import com.ahdi.lib.utils.config.ConfigHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

/**
 * @author xiaoniu
 * @date 2018/9/10.
 *
 * 友盟统计工具类
 */

public class UMengUtil {

    private static final String TAG = "UMengUtil";

    /**
     * 初始化友盟
     * @param context
     */
    public static void initUMeng(Context context){
        if (context == null){
            return;
        }
        LogUtil.d(TAG, "初始化友盟");
        if (BuildConfig.DEBUG){
            UMConfigure.setLogEnabled(true);
        }
        UMConfigure.init(context, ConfigHelper.UMENG_APPKEY, ConfigHelper.UMENG_CHANNEL, UMConfigure.DEVICE_TYPE_PHONE, null);
        MobclickAgent.setScenarioType(context, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.openActivityDurationTrack(false);
        // 友盟Appkey为明文发送，没有校验，刷量的门槛低。appkey验证方式，Appkey保护策略（secret），可以解决Appkey泄露导致的数据滥刷问题。
//        MobclickAgent.setSecret(this, "XXXX");
    }

    /**
     * 自己捕获的异常上报
     */
    public static void reportError(Context context, Throwable throwable){
        if (context == null || throwable == null){
            return;
        }
        LogUtil.d(TAG, "自己捕获的异常使用友盟上报");
        MobclickAgent.reportError(context, throwable);
    }


    /**
     * activity onResume
     * @param context
     */
    public static void onResume(Context context) {
        if (context == null){
            return;
        }
        MobclickAgent.onResume(context);
    }

    /**
     * 进入页面
     * @param pageName
     */
    public static void onPageStart(String pageName){
        if (TextUtils.isEmpty(pageName)){
            return;
        }
        LogUtil.d(TAG, "进入页面：" + pageName);
        MobclickAgent.onPageStart(pageName);
    }
    /**
     * 离开页面
     * @param pageName
     */
    public static void onPageEnd(String pageName){
        if (TextUtils.isEmpty(pageName)){
            return;
        }
        LogUtil.d(TAG, "离开页面：" + pageName);
        MobclickAgent.onPageEnd(pageName);
    }

    /**
     * activity onPause
     * @param context
     */
    public static  void onPause(Context context) {
        if (context == null){
            return;
        }
        MobclickAgent.onPause(context);
    }

    /**
     * 调用kill或者exit之类的方法杀死进程，请务必在此之前调用onKillProcess(Context context)方法，用来保存统计数据。
     * @param context
     */
    public static void onKillProcess(Context context){
        if (context == null){
            return;
        }
        LogUtil.d(TAG, "调用MobclickAgent#onKillProcess用来保存统计数据");
        MobclickAgent.onKillProcess(context);
    }

}
