package com.ahdi.lib.utils.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

/**
 * @author zhaohe@iapppay.com
 * @date 2018/7/12.
 *
 * 圆角ImageView
 */

public class RectangleImageView extends ImageView {

    public RectangleImageView(Context context) {
        this(context, null);
    }

    public RectangleImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RectangleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private Paint mPaint;
    /**
     * 圆角半径
     */
    private int currRound;

    private void initViews() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
       currRound =  dp2px(5);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY ){
            int heightMeasure = (int)(MeasureSpec.getSize(widthMeasureSpec) * 236.0f/ 335);
            heightMeasureSpec =  MeasureSpec.makeMeasureSpec(heightMeasure, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable mDrawable = getDrawable();
        Matrix mDrawMatrix = getImageMatrix();
        if (mDrawable == null) {
            return; // couldn't resolve the URI
        }

        if (mDrawable.getIntrinsicWidth() == 0 || mDrawable.getIntrinsicHeight() == 0) {
            return;     // nothing to draw (empty bounds)
        }

        if (mDrawMatrix == null && getPaddingTop() == 0 && getPaddingLeft() == 0) {
            mDrawable.draw(canvas);
        } else {
            final int saveCount = canvas.getSaveCount();
            canvas.save();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (getCropToPadding()) {
                    final int scrollX = getScrollX();
                    final int scrollY = getScrollY();
                    canvas.clipRect(scrollX + getPaddingLeft(), scrollY + getPaddingTop(),
                            scrollX + getRight() - getLeft() - getPaddingRight(),
                            scrollY + getBottom() - getTop() - getPaddingBottom());
                }
            }
            canvas.translate(getPaddingLeft(), getPaddingTop());

            Bitmap bitmap = drawable2Bitmap(mDrawable);
            mPaint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            canvas.drawRoundRect(new RectF(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom()),
                    currRound, currRound, mPaint);
            canvas.restoreToCount(saveCount);
        }
    }

    /**
     * drawable转换成bitmap
     */
    private Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        //根据传递的scaletype获取matrix对象，设置给bitmap
        Matrix matrix = getImageMatrix();
        if (matrix != null) {
            canvas.concat(matrix);
        }
        drawable.draw(canvas);
        return bitmap;
    }

    private int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

}
