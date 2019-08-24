package com.ahdi.wallet.app.ui.aaaa.bbb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * 重写带有滑动监听的 ScrollView
 */
public class BBScrollView extends ScrollView {
    private ScrollViewListener scrollViewListener = null;
    private OnScrollViewTouchDown onScrollViewTouchDown = null;
    private int handlerWhatId = 65984;
    private int timeInterval = 20;
    private int lastY = 0;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == handlerWhatId) {
                if (lastY == getScrollY()) {
                    if (scrollViewListener != null) {
                        scrollViewListener.onScrollStop(true);
                    }
                } else {
                    if (scrollViewListener != null) {
                        scrollViewListener.onScrollStop(false);
                    }
                    handler.sendMessageDelayed(handler.obtainMessage(handlerWhatId, this), timeInterval);
                    lastY = getScrollY();
                }
            }
        }
    };

    public BBScrollView(Context context) {
        super(context);
    }

    public BBScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BBScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 滚动监听
     */
    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    /**
     * 自定义 ScrollView 滑动监听
     */
    public interface ScrollViewListener {
        /**
         * 滑动监听
         *
         * @param scrollView ScrollView控件
         * @param x          x轴坐标
         * @param y          y轴坐标
         * @param oldx       上一个x轴坐标
         * @param oldy       上一个y轴坐标
         */
        void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy);

        /**
         * 是否滑动停止
         *
         * @param isScrollStop true:滑动停止;false:未滑动停止
         */
        void onScrollStop(boolean isScrollStop);
    }

    /**
     * 按下监听
     */
    public void setOnScrollViewTouchDown(OnScrollViewTouchDown onScrollViewTouchDown) {
        this.onScrollViewTouchDown = onScrollViewTouchDown;
    }

    /**
     * 自定义 ScrollView 触摸监听
     */
    public interface OnScrollViewTouchDown {
        /**
         * 按下监听
         *
         * @param isTouchDown 是否按下
         */
        void onTouchDown(boolean isTouchDown);
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }

    private float xDistance, yDistance, xLast, yLast;

    //dispatchTouchEvent 用于事件拦截
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();

                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;

                if (xDistance > yDistance) {
                    return false;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }


}

