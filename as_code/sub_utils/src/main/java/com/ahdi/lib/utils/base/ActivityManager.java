package com.ahdi.lib.utils.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ahdi.lib.utils.R;
import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理activity
 *
 * @author Administrator
 */
public class ActivityManager {

    private static final String TAG = ActivityManager.class.getSimpleName();

    private List<Activity> listActivity = new ArrayList<>();
    private List<Activity> listActivityMain = new ArrayList<>();
    private static ActivityManager instance;

    private static final String mainActivityPackageName = "com.ahdi.wallet.app.ui.activities.AppMainActivity";
    private static final String loginActivityPackageName = "com.ahdi.wallet.app.ui.activities.login.LoginActivity";
    private static final String loginActivityPackageName2 = "com.ahdi.wallet.app.ui.aaaa.login.LoginActivity2";
    private static final String forgetPwdActivityPackageName = "com.ahdi.wallet.app.ui.activities.loginPwd.ForgetPwdActivity";
    /**
     * 忘记支付密码
     */
    public static final String forgetPayPwdActivityPackageName = "com.ahdi.wallet.app.ui.activities.payPwd.ForgetPayPwdVerifyActivity";

    public static ActivityManager getInstance() {
        if (instance == null) {
            instance = new ActivityManager();
        }
        return instance;
    }

    /**
     * 添加activity
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        if (listActivity == null) {
            listActivity = new ArrayList<>();
        }

        if (listActivityMain == null) {
            listActivityMain = new ArrayList<>();
        }
        if (activity.getClass().getName().equals(mainActivityPackageName)) {
            listActivityMain.add(activity);
        } else {
            listActivity.add(activity);
        }
    }

    /**
     * 删除activity
     *
     * @param activity
     */
    public void removeActivity(Activity activity) {
        if (activity.getClass().getName().equals(mainActivityPackageName)) {
            if (listActivityMain != null && listActivityMain.size() > 0) {
                listActivityMain.remove(activity);
            }
            return;
        }
        if (listActivity == null || listActivity.size() == 0) {
            return;
        }
        listActivity.remove(activity);
    }

    /**
     * 退出所有已添加的activity
     */
    public void finishAllActivity() {
        if (listActivity != null) {
            for (final Activity activity : listActivity) {
                if (activity != null) {
                    activity.finish();
                }
            }
            listActivity.clear();
            listActivity = null;
        }
        if (listActivityMain != null) {
            for (Activity activity : listActivityMain) {
                if (activity != null) {
                    activity.finish();
                }
            }
            listActivityMain.clear();
            listActivityMain = null;
        }
        instance = null;
    }

    /**
     * 判断主activity是否被打开
     */
    public boolean hasMainActivity() {

        if (listActivityMain == null || listActivityMain.size() == 0) {
            return false;
        }
        for (Activity activity : listActivityMain) {
            if (activity != null && !activity.isFinishing() && activity.getClass().getName().equals(mainActivityPackageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 回到主activity
     */
    public void finishToMainActivity() {

        if (listActivity != null) {
            for (final Activity activity : listActivity) {
                if (activity != null && !activity.isFinishing() && !activity.getClass().getName().equals(mainActivityPackageName)) {
                    activity.finish();
                }
            }
        }
    }

    /**
     * 打开主界面
     *
     * @param activity
     */
    public void openMainActivity(Activity activity) {
        if (activity == null) {
            return;
        }
        if (hasMainActivity()) {
            finishToMainActivity();
        } else {
            try {
                activity.startActivity(new Intent(activity, Class.forName(mainActivityPackageName)));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 打开忘记密码界面
     *
     * @param context
     * @param loginName
     */
    public void openForgetPwdActivity(Context context, String loginName) {
        if (context == null) {
            return;
        }
        try {
            Class<?> mainClass = Class.forName(forgetPwdActivityPackageName);
            Intent intent = new Intent(context, mainClass);
            if (!TextUtils.isEmpty(loginName) && loginName.startsWith(ConfigCountry.KEY_AREA_CODE)) {
                loginName = loginName.substring(ConfigCountry.KEY_AREA_CODE.length());
            }
            intent.putExtra(Constants.LOCAL_PAYMENT_PHONE, loginName);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.in_from_right, 0);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            LogUtil.e(TAG, "ForgetPwdActivity ClassNotFoundException: " + e.toString());
        }
    }

    /**
     * 打开登录界面
     *
     * @param context
     */
    public void openLoginActivity(Context context) {
        if (context == null) {
            return;
        }
        try {
            Class<?> mainClass = Class.forName(loginActivityPackageName2);
            Intent intent = new Intent(context, mainClass);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            LogUtil.e(TAG, "LoginActivity ClassNotFoundException: " + e.toString());
        }
    }

    private List<Activity> businessListActivity = new ArrayList<>();

    /**
     * 添加业务流程(充值转账)打开的activity, 用于一次性全部关闭.
     *
     * @param activity
     */
    public void addCommonActivity(Activity activity) {
        if (businessListActivity == null) {
            businessListActivity = new ArrayList<>();
        }
        businessListActivity.add(activity);
    }

    public void removeCommonActivity(Activity activity) {
        if (activity == null || businessListActivity == null || businessListActivity.size() == 0) {
            return;
        }
        businessListActivity.remove(activity);
    }

    /**
     * 全部关闭业务流程(充值转账)打开的activity, 在ending界面显示调用此方法.
     * <p>
     * 原因: 如果设备打开横竖屏自动切换, 点击done关闭多个activity时, 会引起横竖屏切换动画.
     */
    public void finishCommonActivity() {
        if (businessListActivity != null) {
            for (final Activity activity : businessListActivity) {
                if (activity != null) {
                    activity.finish();
                }
            }
            businessListActivity.clear();
            businessListActivity = null;
        }
    }

    private List<Activity> setPwdListActivities = new ArrayList<>();

    /**
     * 重置密码、找回密码流程保存的activity
     */
    public void addSetPwdActivity(Activity activity) {
        if (setPwdListActivities == null) {
            setPwdListActivities = new ArrayList<>();
        }
        setPwdListActivities.add(activity);
    }

    public void removeSetPwdActivity(Activity activity) {
        if (activity == null || setPwdListActivities == null || setPwdListActivities.size() == 0) {
            return;
        }
        setPwdListActivities.remove(activity);
    }

    /**
     * 退出设置密码流程已添加的activity
     */
    public void finishSetPswActivity() {
        if (setPwdListActivities != null) {
            for (final Activity activity : setPwdListActivities) {
                if (activity != null) {
                    activity.finish();
                    activity.overridePendingTransition(0, 0);
                }
            }
            clearSetPswActivity();
        }
    }

    /**
     * 清理设置密码流程已添加的activity
     */
    public void clearSetPswActivity() {
        setPwdListActivities.clear();
        setPwdListActivities = null;
    }

    private List<Activity> bindBankCardList = new ArrayList<>();

    public void addBindBankCardActivity(Activity activity) {
        if (bindBankCardList == null) {
            bindBankCardList = new ArrayList<>();
        }
        bindBankCardList.add(activity);
    }

    public void removeBindBankCardActivity(Activity activity) {
        if (activity == null || bindBankCardList == null || bindBankCardList.size() == 0) {
            return;
        }
        bindBankCardList.remove(activity);
    }

    /**
     * 关闭 绑定银行卡 流程添加的 activity
     */
    public void finishBindBankCardActivity() {
        if (bindBankCardList != null) {
            for (final Activity activity : bindBankCardList) {
                if (activity != null) {
                    activity.finish();
                }
            }
            bindBankCardList.clear();
            bindBankCardList = null;
        }
    }

    private List<Activity> payHubActivity = new ArrayList<>();

    public void addPayHubActivity(Activity activity) {
        if (payHubActivity == null) {
            payHubActivity = new ArrayList<>();
        }
        payHubActivity.add(activity);
    }

    public void removePayHubActivity(Activity activity) {
        if (activity == null || payHubActivity == null || payHubActivity.size() == 0) {
            return;
        }
        payHubActivity.remove(activity);
    }

    /**
     * 关闭收银台首页--付款确认界面
     */
    public void finishPayHubActivity() {
        if (payHubActivity != null) {
            for (final Activity activity : payHubActivity) {
                if (activity != null) {
                    activity.finish();
                }
            }
            payHubActivity.clear();
            payHubActivity = null;
        }
    }

}
