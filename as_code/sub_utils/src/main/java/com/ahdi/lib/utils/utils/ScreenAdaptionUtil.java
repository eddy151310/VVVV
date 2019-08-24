package com.ahdi.lib.utils.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import com.ahdi.lib.utils.config.ConfigHelper;

/**
 * @author xiaoniu
 * @date 2018/8/28.
 * 今日头条适配方案
 * https://mp.weixin.qq.com/s/d9QCoBP6kV9VSWvVldVVwA
 *
 * px = density * dp;
 * density = dpi / 160;
 * px = dp * (dpi / 160);
 */

public class ScreenAdaptionUtil {
    private static final String TAG = "ScreenAdaptionUtil";

    private static float sNoncompatDensity;
    private static float sNoncompatScaledDensity;

    public static void setCustomDensity(@NonNull Activity activity, @NonNull final Application application) {
//        if (!ConfigHelper.DEBUG){
//            return;
//        }
        DisplayMetrics appDisplayMetrics = application.getResources().getDisplayMetrics();
        if (sNoncompatDensity == 0) {
            //sNoncompatDensity 是 px与dp的比例关系,计算公式：px = sNoncompatDensity * dp;
            sNoncompatDensity = appDisplayMetrics.density;
            sNoncompatScaledDensity = appDisplayMetrics.scaledDensity;
            LogUtil.d(TAG, "原始Density = " + sNoncompatDensity);
            // 防止系统切换后不起作用
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        sNoncompatScaledDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });
        }
        //设计图宽度为375dp,由于px = sNoncompatDensity * dp;所以sNoncompatDensity = px/dp
        float targetDensity = appDisplayMetrics.widthPixels / 375.0f;
        LogUtil.d(TAG, "目标Density = " + targetDensity);
        // 防止字体变小
        float targetScaleDensity = targetDensity * (sNoncompatScaledDensity / sNoncompatDensity);
        //density(px与dp的比例关系) = dpi / 160;所以dpi = density*160;
        int targetDensityDpi = (int) (160 * targetDensity);
        //给application设置px与dp的比例关系
        appDisplayMetrics.density = targetDensity;
        appDisplayMetrics.scaledDensity = targetScaleDensity;
        //给application设置dpi
        appDisplayMetrics.densityDpi = targetDensityDpi;

        final DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        //给activity设置px与dp的比例关系
        activityDisplayMetrics.density = targetDensity;
        activityDisplayMetrics.scaledDensity = targetScaleDensity;
        //给activity设置dpi
        activityDisplayMetrics.densityDpi = targetDensityDpi;
    }
}
