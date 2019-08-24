package com.ahdi.wallet.tts;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import com.ahdi.lib.utils.utils.LogUtil;

import java.util.Locale;

/**
 * Created by Administrator on 2017/11/7.
 */

public class TTSPlayer {

    private static final String TAG = TTSPlayer.class.getSimpleName();

    private TextToSpeech mTextToSpeech  ;
    private static Context mContext  ;
    private TTSPlayerListener mTTSPlayerListener;
    private static TTSPlayer mTTSPlayer = null;

    //播报语言
    private static final Locale mLocale  = Locale.ENGLISH ;//new Locale("in", "ID")
    //播报速度
    private static final float speechRate  = 1.0f;


    public static TTSPlayer getTTSPlayer(Context context) {
        if( null == mTTSPlayer ){
            mTTSPlayer = new TTSPlayer();
            mContext = context;
        }
        return mTTSPlayer;
    }

    /** 初始化 TTS  */
    public void initTTS(TTSPlayerListener listener ){
        this.mTTSPlayerListener = listener;
        mTextToSpeech = new TextToSpeech(mContext, new TTSListener() );
    }

    private class TTSListener implements TextToSpeech.OnInitListener {

        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                mTextToSpeech.setSpeechRate(speechRate); //设置速度
                int localeResult = mTextToSpeech.setLanguage(mLocale); //设置语言
                if (localeResult != TextToSpeech.LANG_AVAILABLE && localeResult != TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                    mTTSPlayerListener.onCallBackInitError(localeResult);
                    LogUtil.d(TAG, " -----  TTS 初始化 成功  -----  但不支持当前语言  ----- ");
                }else{
                    mTTSPlayerListener.onCallBackInitSuccess();
                    LogUtil.d(TAG, " -----  TTS 初始化 成功  -----");
                }
            }else if(status == TextToSpeech.ERROR){
                LogUtil.d(TAG, "-----  TTS 初始化 失败  -----status :" + status);
                mTTSPlayerListener.onCallBackInitError(status);
            }
        }
    }


    //play
    public void play(final String ttsString){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(mTextToSpeech.isSpeaking()){
                    mTextToSpeech.speak(ttsString, TextToSpeech.QUEUE_ADD, null);
                }else{
                    mTextToSpeech.speak(ttsString, TextToSpeech.QUEUE_FLUSH, null);
                }
                LogUtil.d(TAG,"TTS播放语音：" + ttsString);
            }
        }).start();
    }

    public TextToSpeech getmTextToSpeech() {
        return mTextToSpeech;
    }

    //destroy
    public void onDestroy(){
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
            mTextToSpeech = null;
        }
    }

    //listener
    public interface TTSPlayerListener {
        void onCallBackInitSuccess();
        void onCallBackInitError(int status);
    }
}
