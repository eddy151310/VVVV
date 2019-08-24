package com.ahdi.lib.utils.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author zhaohe@iapppay.com
 * @date 2018/7/12.
 *
 * 圆角ImageView
 */

public class SquareView extends View {

    public SquareView(Context context) {
        this(context, null);
    }

    public SquareView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SquareView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY ){
            heightMeasureSpec = widthMeasureSpec;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
