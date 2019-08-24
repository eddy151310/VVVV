package com.ahdi.lib.utils.widgets.keyboard;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.ahdi.lib.utils.R;
import com.ahdi.lib.utils.utils.AmountUtil;

import java.lang.reflect.Method;

public class KeyboardUtil {
    private Activity act;
    private MyKeyBoardView keyboardView;
    private Keyboard k1;

    private EditText ed;
    private boolean inputEnable = true;
    private String medt;
    private int mtype;
    private int count = 6;
    private StringBuffer stringBuffer = new StringBuffer();
    private String inputContent;
    private CallBack callBack;
    private boolean isShow;


    public KeyboardUtil(final Activity act, final EditText edit, final int type, final CallBack callBack) {
        this.act = act;
        this.ed = edit;
        this.mtype = type;
        this.callBack = callBack;
        k1 = new Keyboard(act, R.xml.symbols_pwd);
        keyboardView = act.findViewById(R.id.keyboard_view);
        keyboardView.setKeyboard(k1);
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(listener);
        onHiddenSystemKeyboard(edit);
        exchangeEd(edit);
    }

    /**
     * 切换键盘对应的  EditText
     *
     * @param editText
     */
    public void exchangeEd(EditText editText) {
        if (editText == null) {
            return;
        }
        this.ed = editText;
        ed.setSingleLine(true);
        ed.addTextChangedListener(textWatcher);
    }

    public EditText getEditText() {
        return ed;
    }

    private void onHiddenSystemKeyboard(EditText edit) {
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            edit.setInputType(InputType.TYPE_NULL);
        } else {
            this.act.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus = cls.getMethod(
                        "setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(false);
                setShowSoftInputOnFocus.invoke(edit, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onPress(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            ed.setText(AmountUtil.unFormatString(ed.getText().toString()));// 去掉格式化
            Editable editable = ed.getText();
            int start = ed.getSelectionStart();
            if (primaryCode == 48) {
                if (mtype == 1) {
                    if (ed.length() == 0) {
                        return;
                    } else {
                        ed.getText().append("0");
                    }
                } else {
                    ed.getText().append("0");
                }
            } else if (primaryCode == -6) {
                if (mtype == 1) {
                    if (!TextUtils.isEmpty(ed.getText().toString())) {
                        ed.setText(ed.getText().toString()
                                .substring(0, ed.length() - 1));
                    }
                } else {
                    if (count == 0) {
                        count = 6;
                    }
                    if (stringBuffer.length() > 0) {
                        //删除相应位置的字符
                        stringBuffer.delete((count - 1), count);
                        count--;
                        inputContent = stringBuffer.toString();
                        if (callBack != null) {
                            callBack.onPwdStrCallback(stringBuffer);
                        }
                    }
                }
            } else if (primaryCode == -12) {

            } else {
                editable.insert(start, Character.toString((char) primaryCode));
            }
            if (!inputEnable && !TextUtils.isEmpty(ed.getText().toString())) {
                ed.setText(ed.getText().toString()
                        .substring(0, ed.length() - 1));
            }
            if (mtype == 1) {
                if (!TextUtils.isEmpty(ed.getText().toString())) {
                    if (callBack != null) {
                        callBack.onCallback(AmountUtil.formatAmount(ed.getText().toString()));
                    }
                } else {
                    callBack.onCallback("");
                }
            }
            inputEnable = true;
        }
    };

    private TextWatcher textWatcher = new TextWatcher() {
        boolean isPreNull = true;//输入框文字变化之前是否为空
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            isPreNull = TextUtils.isEmpty(ed.getText().toString());
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            medt = ed.getText().toString();
            if (medt.length() == 16) {
                inputEnable = false;
            }
            ed.setSelection(ed.length());
            if (mtype == 2) {
                //如果字符不为""时才进行操作
                if (!editable.toString().equals("")) {
                    if (stringBuffer.length() > 5) {
                        ed.setText("");
                        return;
                    }
                    //将文字添加到StringBuffer中
                    stringBuffer.append(editable);
                    ed.setText("");//添加后将EditText置空
                    count = stringBuffer.length();
                    inputContent = stringBuffer.toString();
                    if (stringBuffer.length() == 6) {
                        //文字长度位4  则调用完成输入的监听
                        if (callBack != null) {
                            callBack.onCallback(inputContent);
                        }
                        ed.setEnabled(false);
                    }
                    if (callBack != null) {
                        callBack.onPwdInputCallback(stringBuffer, inputContent);
                    }
                }
            }else if (mtype == 1 && TextUtils.isEmpty(medt) && !isPreNull){
                //editText改为统一的DeleteEditText，点击叉号时需要回调
                callBack.onCallback("");
            }
        }
    };

    public interface CallBack {
        void onCallback(String result);

        void onPwdInputCallback(StringBuffer buffer, String pwdStr);

        void onPwdStrCallback(StringBuffer stringBuffer);
    }

    public void showKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            keyboardView.setVisibility(View.VISIBLE);
            isShow = true;
        }
    }

    public void hideKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.VISIBLE) {
            keyboardView.setVisibility(View.INVISIBLE);
            isShow = false;
        }
    }

    public boolean isShow() {
        return isShow;
    }

    public void onDeleteAll() {
        int length = stringBuffer.length();
        if (count == 0) {
            count = length;
        }
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                stringBuffer.delete((count - 1), count);
                count--;
                inputContent = stringBuffer.toString();
                if (callBack != null) {
                    callBack.onPwdStrCallback(stringBuffer);
                }
            }
        }
    }

}
