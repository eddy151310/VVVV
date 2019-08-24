package com.ahdi.lib.utils.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.ahdi.lib.utils.R;

import static android.os.Build.VERSION_CODES.M;

public class StatusBarUtil {

    /**
     *
     *
        系统版本	是否支持设置状态栏颜色	是否允许设置状态栏黑色图标
        4.4	        否	                    否
        5.0	        是	                    否
        6.0+	    是	                    是
     */

    /**状态栏颜色*/
    private static final int STATUS_BAR_COLOR = R.color.CC_FFFFFF ;
    /**需要手动设置true或者false.状态栏颜色是否为亮色 ( 如果状态栏颜色为亮色需要给状态栏文字设置深色 ) */
    private static final boolean STATUS_BAR_COLOR_LIGHT = true;

    /**
     * 设置状态栏颜色
     * void setContentView(int layoutResID) {
     *     super.setContentView(layoutResID);
     *     StatusBarUtil.setStatusBar(this);
     * }
     *
     * 一定要在setContentView之后调用
     * @param activity
     */
    public static void setStatusBar(Activity activity) {
        setStatusBar(activity, STATUS_BAR_COLOR, STATUS_BAR_COLOR_LIGHT);
    }

    /**
     *
     * @param activity
     * @param statusBarColor 状态栏颜色
     * @param light 状态栏颜色是否为亮色
     */
    public static void setStatusBar(Activity activity, int statusBarColor, boolean light){
        if (light && Build.VERSION.SDK_INT >= M){
            setStateBarAndTextColor(activity, statusBarColor, light);
        }else if(!light){
            setStateBarDark(activity, statusBarColor, light);
        }
    }

    /**
     * 设置深色状态栏
     * @param activity
     * @param colorResId
     */
    @TargetApi(19)
    private static void setStateBarDark(Activity activity, int colorResId, boolean light) {
        //底部导航栏
        //window.setNavigationBarColor(activity.getResources().getColor(colorResId));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < M ) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(colorResId));
        }else if (Build.VERSION.SDK_INT >= M) {
            setStateBarAndTextColor(activity, colorResId, light);
        }
    }

    /**
     * 设置状态栏和文字颜色
     * @param activity
     * @param statusBarColor 状态栏的颜色
     * @param mStatusBarLight  状态栏是否为亮色
     */
    @TargetApi(23)
    private static void setStateBarAndTextColor(Activity activity, int statusBarColor, boolean mStatusBarLight) {
        //设置状态栏颜色
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(activity.getResources().getColor(statusBarColor));

        //设置状态栏文字颜色
        View decor = activity.getWindow().getDecorView();
        if (mStatusBarLight) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        //防止布局整体上移
        fitsSystemWindowsTrue(activity);
    }

    /**
     * 防止设置状态栏文字颜色的flag导致布局整体上移与状态栏重叠一部分
     * @param activity
     */
    private static void fitsSystemWindowsTrue(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View content = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            if (content != null) {
                content.setFitsSystemWindows(true);
            }
        }
    }
}