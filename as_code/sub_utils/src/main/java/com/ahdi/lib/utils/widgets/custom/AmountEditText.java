package com.ahdi.lib.utils.widgets.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.ahdi.lib.utils.widgets.DeleteEditText;

public class AmountEditText extends DeleteEditText {

    private static final String separator = ".";
    private String oldText = "";

    public AmountEditText(Context context) {
        super(context, null);
        init();
    }

    public AmountEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AmountEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setLongClickable(false);
        setTextIsSelectable(false);
        setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        String text = getText().toString();
        text = text.replace(separator, "");
        if (!oldText.equals(text)) {
            oldText = text;
            @SuppressLint("DrawAllocation") StringBuilder builder = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                builder.append(text.charAt(i));
            }
            String formatText = formatAmount(builder.toString());
            setText(formatText);
            setSelection(formatText.length());
        }
        super.onDraw(canvas);
    }


    public String formatAmount(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        str = new StringBuilder(str).reverse().toString(); // 先将字符串颠倒顺序
        String str2 = "";
        for (int i = 0; i < str.length(); i++) {
            if (i * 3 + 3 > str.length()) {
                str2 += str.substring(i * 3, str.length());
                break;
            }
            str2 += str.substring(i * 3, i * 3 + 3) + separator;
        }
        if (str2.endsWith(separator)) {
            str2 = str2.substring(0, str2.length() - 1);
        }
        return new StringBuilder(str2).reverse().toString();
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        setSelection(this.length());
    }
}