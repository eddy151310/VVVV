package com.ahdi.lib.utils.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.ahdi.lib.utils.R;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.CleanConfigUtil;
import com.ahdi.lib.utils.utils.LBSInstance;
import com.ahdi.lib.utils.utils.LanguageUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.PermissionsActivity;
import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.lib.utils.utils.ScreenAdaptionUtil;
import com.ahdi.lib.utils.utils.ToolUtils;
import com.ahdi.lib.utils.utils.UMengUtil;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.lib.utils.widgets.dialog.CommonDialog;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;

/**
 * Date: 2017/10/10 下午5:20
 * Author: kay lau
 * Description:
 */
public class BaseActivity extends PermissionsActivity implements View.OnClickListener {

    private static final String TAG = BaseActivity.class.getSimpleName();

    private BroadcastReceiver broadcastReceiver = new DialogBroadRec();
    private CommonDialog dialog = null;

    /**
     * 是否需要统计activity页面，默认需要。如果activity有多个fragment只需要统计fragment
     **/
    public boolean isStatisticActivity = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        ScreenAdaptionUtil.setCustomDensity(this, getApplication());
        ActivityManager.getInstance().addActivity(this);
    }

    /**
     * 内存系统回收或者深度清理进程之后, 判断是否重建activity
     *
     * @param savedInstanceState
     * @return true: 系统尝试重建Activity时,  不在继续创建, 关闭之后返回到启动页面
     */
    protected boolean isMemoryRecover(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getBoolean("isMemoryRecover")) {
            LogUtil.e(TAG, "isMemoryRecover======> " + getClass().getName());
            ActivityManager.getInstance().openLoginActivity(BaseActivity.this);// 解决 手机调整字体大小后  无法再运行
            finish();
            return true;
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isMemoryRecover", true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(Constants.ACTION_ACCOUNT_OUT));
        if (isStatisticActivity) {
            UMengUtil.onPageStart(getClass().getSimpleName().intern());
        }
        UMengUtil.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isStatisticActivity) {
            UMengUtil.onPageEnd(getClass().getSimpleName().intern());
        }
        UMengUtil.onPause(this);
    }

    //APP 右出动画
    protected void onCloseLeftToRightActivity() {
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    //payhub  由下至上  进入 ,  由上至下 退出
    protected void onBottom_in_Activity() {
        overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
    }

    //payhub 由上至下 退出
    protected void onBottom_out_Activity() {
        overridePendingTransition(0, R.anim.bottom_out);
    }

    //payhub --> 选择支付方式     右进入 动画
    protected void onRight_in_Activity() {
        overridePendingTransition(R.anim.in_from_right, 0);
    }


    protected void closeLoading(LoadingDialog dialog) {
        LoadingDialog.dismissDialog(dialog);
    }

    protected LoadingDialog showLoading() {
        return LoadingDialog.showDialogLoading(this, getString(R.string.DialogTitle_C0));
    }

    protected LoadingDialog showWhitePointLoading() {
        return LoadingDialog.showWhitePointLoading(this);
    }

    protected LoadingDialog showLoading(String loading) {
        return LoadingDialog.showDialogLoading(this, loading);
    }

    public void showToast(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            ToastUtil.showToastShort(getApplicationContext(), msg);
        }
    }

    public void showTimeToast(String msg, long time) {
        if (!TextUtils.isEmpty(msg)) {
            ToastUtil.showTimeToast(getApplicationContext(), msg, time);
        }
    }

    public void closeSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        //Public.Language 为 对应的资源格式后缀，比如"zh"
        super.attachBaseContext(GlobalContextWrapper.wrap(newBase, LanguageUtil.getLanguage(newBase)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ToastUtil.onDestroy();
        broadcastReceiver = null;
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        ActivityManager.getInstance().removeActivity(this);
    }


    /**
     * 显示被踢出的dialog
     *
     * @param msg
     */
    public void showOutDialog(String msg) {
        LogUtil.d(TAG, msg);
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        CleanConfigUtil.cleanAllConfig();
        dialog = new CommonDialog
                .Builder(this)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.DialogButton_B0), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityManager.getInstance().openLoginActivity(BaseActivity.this);
                        dialog.dismiss();
                        finish();
                    }
                }).show();
    }

    @Override
    public void onClick(View view) {

    }

    private class DialogBroadRec extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            if (TextUtils.equals(intent.getAction(), Constants.ACTION_ACCOUNT_OUT)) {
                showOutDialog(intent.getStringExtra(Constants.MSG_KEY));
                onShowLogoutDialog();
            }
        }
    }

    protected boolean isCanClick() {
        return ToolUtils.isCanClick();
    }

    public boolean isLogin() {
        return !TextUtils.isEmpty(ProfileUserUtil.getInstance().getSID());
    }

    /**
     * 被踢出时activity弹起对话框
     * 在此方法activity可以处理停止扫描二维码等操作
     */
    protected void onShowLogoutDialog() {

    }

    @Override
    public void finish() {
        super.finish();
        closeSoftInput();
    }

    protected void removeViewTreeObserver(ViewTreeObserver observer, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (observer.isAlive()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                observer.removeOnGlobalLayoutListener(listener);
            } else {
                observer.removeGlobalOnLayoutListener(listener);
            }
        }
    }

    protected void openWebCommonActivity(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Intent intent = new Intent(this, WebCommonActivity.class);
        intent.putExtra(Constants.LOCAL_WEB_VIEW_URL_KEY, url);
        startActivity(intent);
    }

}
