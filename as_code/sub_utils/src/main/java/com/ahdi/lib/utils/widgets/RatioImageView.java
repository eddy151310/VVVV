package com.ahdi.lib.utils.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ahdi.lib.utils.R;

/**
 * @author zhaohe@iapppay.com
 * @date 2018/7/12.
 *
 * 具有宽高比例的imageView
 */

public class RatioImageView extends ImageView {

    public RatioImageView(Context context) {
        this(context, null);
    }

    public RatioImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatioImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }


    /**高宽比 hwRatio = height/width*/
    private float hwRatio = 1;
    private void initView(Context context, AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView);
        if (typedArray != null){
            int width = typedArray.getInt(R.styleable.RatioImageView_design_width, 1);
            int height = typedArray.getInt(R.styleable.RatioImageView_design_height, 1);
            hwRatio = height*1.0f / width;
        }
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY ){
            int heightMeasure = (int)(MeasureSpec.getSize(widthMeasureSpec) * hwRatio);
            heightMeasureSpec =  MeasureSpec.makeMeasureSpec(heightMeasure, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
