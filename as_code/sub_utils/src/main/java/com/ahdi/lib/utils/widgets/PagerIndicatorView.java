package com.ahdi.lib.utils.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ahdi.lib.utils.R;


/**
 * @author xiaoniu
 * @date 2018/7/30.
 */

public class PagerIndicatorView extends RelativeLayout {

    /**圆点的个数*/
    private int dotNum = 4;
    /**默认的icon*/
    private int default_icon_id;
    /**选中的icon*/
    private int select_icon_id;
    private int icon_right_margin = 20;
    /**圆点之间的距离*/
    private int mDistance;

    private LinearLayout indicatorLayout;
    private ImageView selected_indicator;

    public PagerIndicatorView(Context context) {
        this(context, null);
    }

    public PagerIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PagerIndicatorView);
        if (typedArray != null){
            dotNum = typedArray.getInteger(R.styleable.PagerIndicatorView_itemCount, 4);
            default_icon_id = typedArray.getResourceId(R.styleable.PagerIndicatorView_default_icon, -1);
            select_icon_id = typedArray.getResourceId(R.styleable.PagerIndicatorView_selected_icon, -1);
            icon_right_margin = (int) typedArray.getDimension(R.styleable.PagerIndicatorView_icon_right_margin,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 20, getResources().getDisplayMetrics()));
        }
        initView();
    }

    private void initView() {
        /**添加圆点父布局*/
        indicatorLayout = new LinearLayout(getContext());
        addView(indicatorLayout);
        /**添加选中的圆点*/
        selected_indicator = new ImageView(getContext());
        selected_indicator.setImageResource(select_icon_id);
        selected_indicator.setBackgroundDrawable(null);
        addView(selected_indicator);
        /**计算两点之间的距离*/
        post(new Runnable() {
            @Override
            public void run() {
                if (dotNum > 1){
                    getIndicatorDistance();
                }
            }
        });
        /**开始向点的父布局添加点*/
        ImageView defaultDot = null;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, icon_right_margin, 0);
        for (int i = 0; i < dotNum; i++) {
            defaultDot = new ImageView(getContext());
            defaultDot.setImageResource(default_icon_id);
            indicatorLayout.addView(defaultDot, layoutParams);
        }
    }

    /**
     * 获取两个点之间的距离
     */
    private void getIndicatorDistance(){
        try {
            mDistance = indicatorLayout.getChildAt(1).getLeft() - indicatorLayout.getChildAt(0).getLeft();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void onPageScrolled(int position, float positionOffset) {
        //页面滚动时小白点移动的距离，并通过setLayoutParams(params)不断更新其位置
        if (mDistance <= 0){
            getIndicatorDistance();
        }
        float leftMargin = mDistance * (position + positionOffset);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) selected_indicator.getLayoutParams();
        params.leftMargin = (int) leftMargin;
        selected_indicator.setLayoutParams(params);
    }

    public void onPageSelected(int position) {
        //页面跳转时，设置小圆点的margin
//        if (mDistance <= 0){
//            getIndicatorDistance();
//        }
//        float leftMargin = mDistance * position;
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) selected_indicator.getLayoutParams();
//        params.leftMargin = (int) leftMargin;
//        selected_indicator.setLayoutParams(params);
    }

}
