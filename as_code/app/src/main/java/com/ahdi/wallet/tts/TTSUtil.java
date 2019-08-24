package com.ahdi.wallet.tts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;

import com.ahdi.lib.utils.utils.LogUtil;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2017/11/8.
 */

public class TTSUtil {

    private static final String TAG = "TTSUtil";
    private static final int REQUEST_CHECK_TTS = 10010;

    //没有语音包需要安装一个
    public static void toInstallaTTS(Activity mActivity) {
        Intent installIntent = new Intent();
        installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
        mActivity.startActivity(installIntent);

    }

    //检查是否安装了TTS语音包
    public static void checkTTS(Context context) {
        if (context == null){
            LogUtil.e(TAG, "检查是否安装了TTS语音包时 context == null");
            return;
        }
        Intent installIntent = new Intent();
        installIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(installIntent);
    }

    /**
     * TTS初始化之后有时会无法播放语音。
     * 从打印日志看failed: not bound to TTS engine
     * 找到源代码打印处
     * if (mServiceConnection == null) {
     *      Log.w(TAG, method + " failed: not bound to TTS engine");
     *   return errorResult;
     *  }
     *  通过反射判断mServiceConnection是否为空
     * @param tts
     * @return true 可用
     */
    public static boolean ismServiceConnectionUsable(TextToSpeech tts) {

        boolean isBindConnection = true;
        if (tts == null){
            return false;
        }
        Field[] fields = tts.getClass().getDeclaredFields();
        for (int j = 0; j < fields.length; j++) {
            fields[j].setAccessible(true);
            if (TextUtils.equals("mServiceConnection",fields[j].getName()) && TextUtils.equals("android.speech.tts.TextToSpeech$Connection",fields[j].getType().getName())) {
                try {
                    if(fields[j].get(tts) == null){
                        isBindConnection = false;
                        LogUtil.e(TAG, "*******反射判断 TTS -> mServiceConnection == null*******");
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return isBindConnection;
    }
}
