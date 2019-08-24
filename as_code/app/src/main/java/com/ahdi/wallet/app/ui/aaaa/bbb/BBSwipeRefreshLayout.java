package com.ahdi.wallet.app.ui.aaaa.bbb;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class BBSwipeRefreshLayout extends SwipeRefreshLayout {

    private OnPreInterceptTouchEventDelegate mOnPreInterceptTouchEventDelegate;


    public BBSwipeRefreshLayout(Context context) {
        super(context);
    }

    public BBSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override


    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean disallowIntercept = false;
        if (mOnPreInterceptTouchEventDelegate != null)

            disallowIntercept = mOnPreInterceptTouchEventDelegate.shouldDisallowInterceptTouchEvent(ev);

        if (disallowIntercept) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setOnPreInterceptTouchEventDelegate(OnPreInterceptTouchEventDelegate listener) {
        mOnPreInterceptTouchEventDelegate = listener;
    }

    public interface OnPreInterceptTouchEventDelegate {
        boolean shouldDisallowInterceptTouchEvent(MotionEvent ev);

    }
}
