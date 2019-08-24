package com.ahdi.lib.utils.takephoto.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.ahdi.lib.utils.R;

/**
 * @author zhaohe@iapppay.com
 * @date 2018/5/23.
 */

public class IDViewFinder extends View {

    public IDViewFinder(Context context) {
        this(context, null);
    }

    public IDViewFinder(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IDViewFinder(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private Rect rect = null;
    private Paint paint = null;
    private int finderColor = -1;
    private int roundColor = -1;
    private void initView() {
        paint = new Paint();
        finderColor = getResources().getColor(R.color.CC_00000000);
        roundColor = getResources().getColor(R.color.CC_801A1B24);
    }

    /**
     * 设置透明区域
     * @param rect
     */
    public void setRect(Rect rect) {
        this.rect = rect;
        invalidate();
    }

    /**
     * 设置背景色
     * @param color
     */
    public void setRoundColor(int color){
        this.roundColor = color;
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (rect == null){
            return;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        paint.setColor(roundColor);
        canvas.drawRect(0, 0, width, rect.top, paint);
        canvas.drawRect(0,rect.top, rect.left, rect.bottom ,paint);
        canvas.drawRect(rect.right, rect.top, height, rect.bottom ,paint);
        canvas.drawRect(0, rect.bottom, width, height,paint);
        paint.setColor(finderColor);
        canvas.drawRect(rect ,paint);
    }
}
