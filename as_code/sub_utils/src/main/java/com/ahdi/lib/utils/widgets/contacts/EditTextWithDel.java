package com.ahdi.lib.utils.widgets.contacts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.ahdi.lib.utils.R;


/**
 * @description: 自定义带的EditText
 */
@SuppressLint("AppCompatCustomView")
public class EditTextWithDel extends EditText {
    private final static String TAG = "EditTextWithDel";
    private Drawable imgLeft,imgAble;
    private Context mContext;

    public EditTextWithDel(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public EditTextWithDel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    public EditTextWithDel(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        imgLeft = mContext.getResources().getDrawable(
                R.mipmap.select_bank_account_search);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setCompoundDrawablesWithIntrinsicBounds(imgLeft, null, null, null);
            }
        });
        setCompoundDrawablesWithIntrinsicBounds(imgLeft, null, null, null);
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
