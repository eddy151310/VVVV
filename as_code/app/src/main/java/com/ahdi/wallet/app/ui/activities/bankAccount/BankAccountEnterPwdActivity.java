package com.ahdi.wallet.app.ui.activities.bankAccount;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.BankAccountSdkMain;
import com.ahdi.lib.utils.cryptor.aes.AesKeyCryptor;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.widgets.keyboard.KeyboardUtil;


public class BankAccountEnterPwdActivity extends AppBaseActivity {

    private EditText editText;
    private ImageView[] imageViews;
    private String sid;
    private String bid;
    private KeyboardUtil keyboardUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        initIntentData();
        setContentView(R.layout.activity_check_pay_pwd_bankcard);
        initCommonTitle(getString(R.string.BankAccountDeleteCheckPWD_A0));
        initView();
    }

    private void initIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            sid = intent.getStringExtra(Constants.LOCAL_KEY_SID);
            bid = intent.getStringExtra(Constants.LOCAL_BID_KEY);
        }
    }

    private void initView() {
        ((TextView)findViewById(R.id.tv_tip_one)).setText(R.string.BankAccountDeleteCheckPWD_B0);
        editText = findViewById(R.id.et);
        imageViews = new ImageView[6];
        imageViews[0] = findViewById(R.id.item_code_iv1);
        imageViews[1] = findViewById(R.id.item_code_iv2);
        imageViews[2] = findViewById(R.id.item_code_iv3);
        imageViews[3] = findViewById(R.id.item_code_iv4);
        imageViews[4] = findViewById(R.id.item_code_iv5);
        imageViews[5] = findViewById(R.id.item_code_iv6);
        intKeyBoardView();
    }

    public void intKeyBoardView() {
        keyboardUtil = new KeyboardUtil(this, editText, 2, new KeyboardCallback());
        keyboardUtil.showKeyboard();
    }

    public void getUnbind(String payPwd) {
        BankAccountSdkMain.getInstance().unBindCard(this, sid, bid, payPwd, showLoading());
        onClearKeyboard();
    }

    private class KeyboardCallback implements KeyboardUtil.CallBack {

        @Override
        public void onCallback(String result) {
            if (!TextUtils.isEmpty(bid)) {
                getUnbind(AesKeyCryptor.encodePayPwd(result));
            }
        }

        @Override
        public void onPwdInputCallback(StringBuffer buffer, String pwdStr) {
            for (int i = 0; i < buffer.length(); i++) {
                ImageView imageView = imageViews[i];
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(R.drawable.password_black_point);

            }
        }

        @Override
        public void onPwdStrCallback(StringBuffer stringBuffer) {
            imageViews[stringBuffer.length()].setVisibility(View.INVISIBLE);
        }
    }

    private void onClearKeyboard() {
        if (keyboardUtil != null) {
            keyboardUtil.onDeleteAll();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
