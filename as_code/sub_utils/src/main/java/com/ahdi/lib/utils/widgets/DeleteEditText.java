package com.ahdi.lib.utils.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import com.ahdi.lib.utils.R;


/**
 * @author xiaoniu
 * @date 2018/7/26.
 *
 * 统一输入框
 */
@SuppressLint("AppCompatCustomView")
public class DeleteEditText extends EditText {

    public DeleteEditText(Context context) {
        super(context, null);
        initView(context, null, 0);
    }

    public DeleteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public DeleteEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }


    private Drawable deleteDrawable = null;

    private void initView(Context context, AttributeSet attrs,int defStyleAttr) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DeleteEditText,defStyleAttr,0);
        int deleteID = typedArray.getResourceId(R.styleable.DeleteEditText_deleteIcon, R.mipmap.common_clear_edt);
        deleteDrawable = getResources().getDrawable(deleteID);
        typedArray.recycle();
        addTextChangedListener(new MyTextWatcher());
    }

    /**
     * 设置删除按钮的资源
     * @param deleteRes
     */
    public void setDeleteDrawable(int deleteRes){
        deleteDrawable = getResources().getDrawable(deleteRes);
        Drawable[] compoundDrawables = getCompoundDrawables();
        if (compoundDrawables != null && compoundDrawables[2] != null){
            showDeleteDrawable();
        }
        invalidate();
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused){
            showDeleteDrawable();
        }else {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // 原理：当手指抬起的位置在删除图标的区域，即视为点击了删除图标 = 清空搜索框内容
        switch (event.getAction()) {
            // 判断动作 = 手指抬起时
            case MotionEvent.ACTION_UP:
                Drawable drawable =  deleteDrawable;

                if (drawable != null && event.getX() <= (getWidth() - getPaddingRight())
                        && event.getX() >= (getWidth() - getPaddingRight() - drawable.getBounds().width())) {

                    // 判断条件说明
                    // event.getX() ：抬起时的位置坐标
                    // getWidth()：控件的宽度
                    // getPaddingRight():删除图标图标右边缘至EditText控件右边缘的距离
                    // 即：getWidth() - getPaddingRight() = 删除图标的右边缘坐标 = X1
                    // getWidth() - getPaddingRight() - drawable.getBounds().width() = 删除图标左边缘的坐标 = X2
                    // 所以X1与X2之间的区域 = 删除图标的区域
                    // 当手指抬起的位置在删除图标的区域（X2=<event.getX() <=X1），即视为点击了删除图标 = 清空搜索框内容
                    setText("");
                    return true;//由于图片是在输入框内的，相当于点击输入框，所以点击叉号会调起软键盘。返回true即可屏蔽软键盘的弹起
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    class MyTextWatcher implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            showDeleteDrawable();
        }
    }

    private void showDeleteDrawable(){
        if (getText().toString().length() > 0 && hasFocus()){
            setCompoundDrawablesWithIntrinsicBounds(null, null, deleteDrawable, null);
        }else {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }
}
