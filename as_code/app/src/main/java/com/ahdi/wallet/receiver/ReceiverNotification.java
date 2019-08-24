package com.ahdi.wallet.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.app.ui.activities.voucher.VoucherListActivity;

/**
 * Date: 2017/12/19 下午6:30
 * Author: kay lau
 * Description:
 */
public class ReceiverNotification extends BroadcastReceiver {

    private static final String TAG = ReceiverNotification.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent in) {

        String notify_type = in.getStringExtra(Constants.NOTIFY_TYPE_KEY);
        if (TextUtils.equals(notify_type, Constants.NOTIFY_TYPE_SIX)){//优惠券
            openVoucherPage(context);
        }else{
            // 所有推送通知都跳转 SplashActivity, SplashActivity内部判断是否打开登录页或者首页
            openLaunchPage(context);
        }
        NotificationManager notificationManager = (NotificationManager) AppGlobalUtil.getInstance().getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }

    /**
     * 打开启动页面
     */
    private void openLaunchPage(Context context) {
        if (GlobalApplication.getApplication().isForeground()) {
            LogUtil.e(TAG, " ---点击消息栏准备打开app启动页面，app已经在前台了就不再处理--");
            return;
        }
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launchIntent);
        }
        LogUtil.d(TAG, " ---点击消息栏打开app启动页面--");
    }

    /**
     * 打开优惠券页面
     *
     * @param context
     */
    private void openVoucherPage(Context context) {
        if (TextUtils.isEmpty(GlobalApplication.getApplication().getSID()) || ProfileUserUtil.getInstance().getUserData() == null){
            openLaunchPage(context);
        }else {
            try{
                Intent intent = new Intent(context, VoucherListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                LogUtil.d(TAG, " ---点击消息栏打开Voucher页面--");
            }catch (Exception ex){
                ex.printStackTrace();
                LogUtil.d(TAG, "消息打开优惠券列表页面失败：" + ex.getMessage());
            }

        }
    }
}
