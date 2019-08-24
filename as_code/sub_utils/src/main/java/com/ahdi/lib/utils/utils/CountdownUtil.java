package com.ahdi.lib.utils.utils;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;

public class CountdownUtil {
    private final int HANDLE_WHAT_COUNTDOWN_ING = 10001;
    private final int HANDLE_WHAT_COUNTDOWN = 10002;

    private CountDownCallback callBack;
    private CountDownTimer timer;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case HANDLE_WHAT_COUNTDOWN_ING:
                    if (callBack != null) {
                        callBack.onCountDowning(msg.arg1 / 1000);
                    }
                    break;
                case HANDLE_WHAT_COUNTDOWN:
                    if (callBack != null) {
                        callBack.onComplete();
                    }
                    break;
                default:
                    break;
            }
        }
    };


    public void startCount(int mCount, CountDownCallback CallBack) {
        this.callBack = CallBack;
        timer = new CountDownTimer(mCount * 1000, 1000) {

            @Override
            public void onTick(long arg0) {
                Message msg = new Message();
                msg.arg1 = (int) arg0;
                msg.what = HANDLE_WHAT_COUNTDOWN_ING;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFinish() {
                mHandler.sendEmptyMessage(HANDLE_WHAT_COUNTDOWN);
            }
        };
        timer.start();
    }

    public void destroyTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * 倒计时
     */
    public interface CountDownCallback {

        /**
         * 正在倒计时
         *
         * @param count
         */
        void onCountDowning(int count);

        /**
         * 倒计时完成
         */
        void onComplete();
    }
}
