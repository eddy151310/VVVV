package com.ahdi.wallet.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.ahdi.lib.utils.utils.CleanConfigUtil;
import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.ToolUtils;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.bean.UserData;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;
import com.ahdi.wallet.tts.TTSPlayer;
import com.ahdi.wallet.tts.TTSUtil;
import com.ahdi.lib.utils.base.ActivityManager;

import org.json.JSONObject;

/**
 * Date: 2017/10/17 下午3:02
 * Author: kay lau
 * Description:
 */
public class ReceiverPushMsg extends BroadcastReceiver {

    private static final String TAG = ReceiverPushMsg.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Bundle data = intent.getBundleExtra("todo");// TODO: 2019/7/9  
            if (data == null) {
                LogUtil.e(TAG, " ---push --- content --- null ---");
                return;
            }
            LogUtil.d(TAG, "-----------------APP业务层 收到push---start-------------" );
            //1.1 当前没登录账号，不提示
            String voucher = "";
            UserData userData = ProfileUserUtil.getInstance().getUserData();
            if (userData != null) {
                voucher = userData.getVoucher();
            }
            if (TextUtils.isEmpty(voucher)) {
                LogUtil.e(TAG, " ---push --- voucher ---null ---");
                return;
            }
            //1.2 若不是当前登录账号的推送,不提示
            long userID = 0;
            String voice = "";
            try {
                String transparent = data.getString("transparent");
                JSONObject json = new JSONObject(transparent);
                userID = json.optLong("userId");
                voice = json.optString("voice");
                LogUtil.e(TAG, " ---push --- tts ---: " + voice);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(TAG, " ---push --- transparent ---JSONException: " + e.toString());
            }
            UserData accountUserData = ProfileUserUtil.getInstance().getUserData();
            if (accountUserData != null) {
                String cacheUID = accountUserData.getUID();
                LogUtil.e(TAG, " ---push --- pushUID = " + userID + "-----本地存储的UID=" + cacheUID);
                if (TextUtils.isEmpty(cacheUID) || !TextUtils.equals(cacheUID, String.valueOf(userID))) {
                    return;
                }
            } else {
                return;
            }

            //2.1若是当前登录账号的推送，进行声音和震动提示
            String title = data.getString("title");
            String body = data.getString("body");

            String notify_type = data.getString(Constants.NOTIFY_TYPE_KEY);
            LogUtil.e(TAG, " ---push---notify_type = " + notify_type);

            if (TextUtils.equals(notify_type, Constants.NOTIFY_TYPE_FIVE)) {
                doLogOutPush(notify_type, title, body);
            }else if(TextUtils.equals(notify_type, Constants.NOTIFY_TYPE_SIX)){
                doVoucherPush(notify_type, title, body);
            }else {
                doPayPush(context, notify_type, title, body, voice);
            }
            LogUtil.d(TAG, "-----------------APP业务层 收到push---end-------------" );
        }
    }

    private void doVoucherPush(String notify_type, String title, String body) {
        sendNotification(notify_type, title, body);
    }

    /**
     * 处理支付消息的逻辑
     */
    private void doPayPush(Context context, String notify_type, String title, String body, String voice) {
        sendNotification(notify_type, title, body);
//        if (!TextUtils.isEmpty(voice)) {
//            playTTS(context, voice);
//        }
    }

    /**
     * 处理被登出消息的逻辑
     * 3.1 被登出的原设备应用前台运行中： 收到push（不展示notification）、弹窗提示、点击确定、返回登录界面
     * 3.2 被登出的原设备应用在后台： 收到push、点击push或点app图标、进入登录界面
     */
    private void doLogOutPush(String notify_type, String title, String body) {
        CleanConfigUtil.cleanVoucher();
        CleanConfigUtil.cleanProfileUserManager();

        if (GlobalApplication.getApplication().isForeground()) {
            sendOutBroadcast(body);
            LogUtil.e(TAG, " ---push --- out ---app在前台");
        } else {
            ActivityManager.getInstance().finishAllActivity();
            sendNotification(notify_type, title, body);
            LogUtil.e(TAG, " ---push --- out ---app在后台");
        }
    }

    private void sendNotification(String notify_type, String title, String message) {
        int id = (int) System.currentTimeMillis();

        Context context = AppGlobalUtil.getInstance().getContext();
        Intent intent = new Intent(context, ReceiverNotification.class);
        intent.putExtra(Constants.NOTIFY_TYPE_KEY, notify_type);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                id, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL)
                .setSmallIcon(R.mipmap.notification_small_icon)
                .setColor(context.getResources().getColor(R.color.CC_D63031))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.app_icon))
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = notificationBuilder.build();

        if (notificationManager != null) {
            notificationManager.notify(id, notification);
        }
    }


    private void playTTS(final Context mContext, final String tts) {
        try {
            if (TTSUtil.ismServiceConnectionUsable(TTSPlayer.getTTSPlayer(mContext).getmTextToSpeech())) {
                TTSPlayer.getTTSPlayer(mContext).play(tts);
            } else {
                LogUtil.e(TAG, "*********PUSH 中重新初始化TTS***** ");
                TTSUtil.checkTTS(mContext);
                TTSPlayer.getTTSPlayer(mContext).initTTS(new TTSPlayer.TTSPlayerListener() {
                    @Override
                    public void onCallBackInitSuccess() {
                        TTSPlayer.getTTSPlayer(mContext).play(tts);
                        LogUtil.e(TAG, "*********PUSH 中初始化TTS成功***** ");
                    }

                    @Override
                    public void onCallBackInitError(int status) {
                        LogUtil.e(TAG, "*********PUSH 中初始化TTS失败***** status = " + status);
                    }
                });
            }

        } catch (Exception e) {
            LogUtil.d(TAG, "TTS播报错误：" + e.toString());
        }
    }

    private void sendOutBroadcast(String msg) {
        ToolUtils.sendBroadcastBackLogin(msg);
    }
}