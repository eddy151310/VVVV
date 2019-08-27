package com.ahdi.wallet.app.ui.activities.bankCard;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.sdk.BankCardSdk;
import com.ahdi.wallet.app.callback.BankCardSdkCallBack;
import com.ahdi.wallet.app.data.PaymentGlobalData;
import com.ahdi.wallet.app.main.BankCardSdkMain;
import com.ahdi.wallet.app.response.SelectCardTypeRsp;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.cryptor.aes.AesKeyCryptor;
import com.ahdi.lib.utils.widgets.dialog.CommonDialog;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.lib.utils.widgets.keyboard.KeyboardUtil;

import org.json.JSONObject;

public class BankCardEnterPwdActivity extends AppBaseActivity {

    private EditText editText;
    private ImageView[] imageViews;
    private String sid;
    private String bid;
    private KeyboardUtil keyboardUtil;
    private int from;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        initIntentData();
        setContentView(R.layout.activity_check_pay_pwd_bankcard);
        initView();
    }

    private void initIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            sid = intent.getStringExtra(Constants.LOCAL_KEY_SID);
            bid = intent.getStringExtra(Constants.LOCAL_BID_KEY);
            from = intent.getIntExtra(Constants.LOCAL_FROM_KEY, 0);
        }
    }

    private void initView() {
        initCommonTitle("");
        TextView tv_title = initTitleTextView("");
        if (from == Constants.LOCAL_FROM_DELETE_BANK_CARD) {
            tv_title.setText(getString(R.string.BankCardDeleteCheckPWD_A0));
        } else {
            tv_title.setText(getString(R.string.BankCardAddCheckPWD_A0));
        }

        View view = findViewById(R.id.check_pay_pwd_layout);
        TextView tv_tips = view.findViewById(R.id.tv_tip_one);
        tv_tips.setText(getString(R.string.BankCardDeleteCheckPWD_B0));
        View pwd_layout = view.findViewById(R.id.pwd_layout);
        editText = pwd_layout.findViewById(R.id.et);
        imageViews = new ImageView[6];
        imageViews[0] = pwd_layout.findViewById(R.id.item_code_iv1);
        imageViews[1] = pwd_layout.findViewById(R.id.item_code_iv2);
        imageViews[2] = pwd_layout.findViewById(R.id.item_code_iv3);
        imageViews[3] = pwd_layout.findViewById(R.id.item_code_iv4);
        imageViews[4] = pwd_layout.findViewById(R.id.item_code_iv5);
        imageViews[5] = pwd_layout.findViewById(R.id.item_code_iv6);
        intKeyBoardView();
    }

    public void intKeyBoardView() {
        keyboardUtil = new KeyboardUtil(this, editText, 2, new KeyboardCallback());
        keyboardUtil.showKeyboard();
    }


    private class KeyboardCallback implements KeyboardUtil.CallBack {

        @Override
        public void onCallback(String result) {
            String payPwd = AesKeyCryptor.encodePayPwd(result);
            if (!TextUtils.isEmpty(bid)) {
                getUnbind(payPwd);
            } else {
                //验证完支付密码要去选择卡类型，选择卡类型接口会返回密码是否校验成功
                BankCardSdkMain.getInstance().selectCardType(BankCardEnterPwdActivity.this, sid, payPwd, new SelectBindCardTypeListener(showLoading()));
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

    public void getUnbind(String payPwd) {
        BankCardSdkMain.getInstance().unBindCard(this, sid, bid, payPwd, showLoading());
        onClearKeyboard();
    }

    private void onClearKeyboard() {
        if (keyboardUtil != null) {
            keyboardUtil.onDeleteAll();
        }
    }

    public void showDialog(String msg) {
        new CommonDialog
                .Builder(this)
                .setMessage(msg)
                .setMessageCenter(true)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.DialogButton_D0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onClearKeyboard();
                    }
                }).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BankCardSdkMain.getInstance().onDestroy();
    }

    private class SelectBindCardTypeListener implements BankCardSdkCallBack {
        LoadingDialog loadingDialog;

        public SelectBindCardTypeListener(LoadingDialog loadingDialog) {
            this.loadingDialog = loadingDialog;
        }

        @Override
        public void onResult(String code, String errorMsg, JSONObject jsonObject) {
            closeLoading(loadingDialog);
            SelectCardTypeRsp resp = SelectCardTypeRsp.decodeJson(SelectCardTypeRsp.class, jsonObject);
            if (resp != null && TextUtils.equals(code, BankCardSdk.LOCAL_PAY_SUCCESS)) {
                Intent intent = new Intent(BankCardEnterPwdActivity.this, BindCardTypeActivity.class);
                intent.putExtra(Constants.LOCAL_KEY_SID, sid);
                intent.putExtra(Constants.LOCAL_TOKEN_KEY, resp.getToken());
                PaymentGlobalData.getInstance().setBindCardTypeSchemas(resp.getCardTypeSchema());
                startActivity(intent);
                finish();
            } else {
                showDialog(errorMsg);
            }
        }
    }
}
