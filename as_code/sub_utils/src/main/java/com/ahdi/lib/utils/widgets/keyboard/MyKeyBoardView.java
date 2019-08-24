package com.ahdi.lib.utils.widgets.keyboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

import com.ahdi.lib.utils.R;

import java.util.List;

public class MyKeyBoardView extends KeyboardView {
    private Context mContext;
    private Keyboard mKeyBoard;
    public int type;

    public MyKeyBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public MyKeyBoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
    }

    /**
     * 重新画一些按键
     */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mKeyBoard = this.getKeyboard();
        List<Key> keys = null;
        if (mKeyBoard != null) {
            keys = mKeyBoard.getKeys();
        }

        if (keys != null) {
            for (Key key : keys) {
              if (key.codes[0] == -12) {
                    drawKeyBackground(R.drawable.keyboard_item_bg_null, canvas, key);
                }else if(key.codes[0] == -6){
                    //drawKeyBackground(R.drawable.keyboard_item_bg_empty, canvas, key);
                  //不执行重画
                }
            }
        }
    }

    private void drawKeyBackground(int drawableId, Canvas canvas, Key key) {
        Drawable npd = mContext.getResources().getDrawable(drawableId);
        int[] drawableState = key.getCurrentDrawableState();
        if (key.codes[0] != 0) {
            npd.setState(drawableState);
        }
        npd.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
        npd.draw(canvas);

    }

}
