package com.ahdi.wallet.app.ui.base;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.ahdi.lib.utils.utils.ToolUtils;


/**
 * Date: 2019/1/4 下午2:22
 * Author: kay lau
 * Description:
 */
public class BaseClickableSpan extends ClickableSpan {

    private Context context;
    private boolean isUnderlineText;
    private int textColor;
    private OnClickableSpaListener listener;

    public void setUnderlineText(boolean underlineText) {
        isUnderlineText = underlineText;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public BaseClickableSpan(Context context, OnClickableSpaListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void onClick(View widget) {
        if (!ToolUtils.isCanClick()) {
            return;
        }
        if (context == null || listener == null) {
            return;
        }
        listener.onClick(widget);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        if (context != null) {
            // 设置字体颜色
            ds.setColor(textColor);
        }
        // 设置可点击文字的下划线
        ds.setUnderlineText(isUnderlineText);
        // 去掉点击的阴影效果
        ds.clearShadowLayer();
    }

    public interface OnClickableSpaListener {
        void onClick(View widget);
    }
}
