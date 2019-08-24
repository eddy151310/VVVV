package com.ahdi.wallet.app.ui.aaaa.ddd;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * Developer：chunsoft on 2016/10/31 16:04
 * Email：chun_soft@qq.com
 * Content：viewPager和RecycleView相互冲突，将父View传到ViewPager里面
 * 使用父类方法requestDisallowInterceptTouchEvent(true)
 * 用来子View告诉父容器不要拦截我们的事件的
 */

public class DDDViewPager extends ViewPager {

    float x, y;
    private ViewGroup parent;

    public DDDViewPager(Context context) {
        super(context);
    }

    public DDDViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setNestedpParent(ViewGroup parent) {
        this.parent = parent;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                return super.onInterceptHoverEvent(event);
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(x - event.getX()) > Math.abs(y - event.getY()))
                    return true;
                else return false;
            case MotionEvent.ACTION_UP:
                return super.onInterceptHoverEvent(event);
        }
        return super.onInterceptHoverEvent(event);
    }
}
