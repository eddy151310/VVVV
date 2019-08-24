package com.ahdi.lib.utils.base;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Date: 2018/6/27 下午2:23
 * Author: kay lau
 * Description:
 */
public abstract class BaseTextViewWatcher implements TextWatcher {


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public abstract void afterTextChanged(Editable s);
}
