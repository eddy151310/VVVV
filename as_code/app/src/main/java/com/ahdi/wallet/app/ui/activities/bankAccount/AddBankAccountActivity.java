package com.ahdi.wallet.app.ui.activities.bankAccount;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.sdk.AccountSdk;
import com.ahdi.wallet.app.sdk.BankAccountSdk;
import com.ahdi.wallet.app.bean.BindAccountBean;
import com.ahdi.wallet.app.callback.BankAccountSdkCallBack;
import com.ahdi.wallet.app.main.AccountSdkMain;
import com.ahdi.wallet.app.response.BindAccountRsp;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.cryptor.aes.AesKeyCryptor;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;

import org.json.JSONObject;

public class AddBankAccountActivity extends AppBaseActivity {

    private TextView tv_select_bank;
    private EditText edt_bank_account_no, edt_bank_account_name, edt_bank_account_phone;
    private Button btn_add_bank_account_confirm;
    private String token;
    private String sid;
    private String bankCode;
    public static final int REQUEST_CODE_SELECT_BANK_ACCOUNT = 0;
    public static final int RESULT_CODE_SELECT_BANK_ACCOUNT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.activity_add_bank_account);
        getWindow().setBackgroundDrawableResource(R.color.CC_F1F2F6); //代码设置bg防止键盘弹起时键盘区域漏出主题的黑色
        initCommonTitle(getString(R.string.BankAccountAdd_A0));
        initView();
        initDate();
    }

    public void initView() {
        tv_select_bank = findViewById(R.id.tv_select_bank);
        RelativeLayout re_select_bank = findViewById(R.id.re_select_bank);
        edt_bank_account_no = findViewById(R.id.edt_bank_account_no);
        edt_bank_account_name = findViewById(R.id.edt_bank_account_name);
        edt_bank_account_phone = findViewById(R.id.edt_bank_account_phone);
        btn_add_bank_account_confirm = findViewById(R.id.btn_add_bank_account_confirm);
        re_select_bank.setOnClickListener(this);
        btn_add_bank_account_confirm.setOnClickListener(this);

        edt_bank_account_no.addTextChangedListener(new CommomTextWatcher());
        edt_bank_account_name.addTextChangedListener(new CommomTextWatcher());
        edt_bank_account_name.setKeyListener(new NumberKeyListener() {
            @NonNull
            @Override
            protected char[] getAcceptedChars() {
                return Constants.COMMON_ACCEPT_CHAR.toCharArray();
            }

            @Override
            public int getInputType() {
                return InputType.TYPE_CLASS_TEXT;
            }
        });
        edt_bank_account_phone.addTextChangedListener(new CommomTextWatcher());
    }

    public void initDate() {
        Intent intent = getIntent();
        if (intent != null) {
            token = intent.getStringExtra(Constants.LOCAL_TOKEN_KEY);
            sid = intent.getStringExtra(Constants.LOCAL_KEY_SID);
        }
    }

    @Override
    public void onClick(View view) {
        if (!isCanClick()) {
            return;
        }
        int i = view.getId();
        if (i == R.id.re_select_bank) {
            Intent intent = new Intent(this, SelectBankActivity.class);
            intent.putExtra(Constants.LOCAL_KEY_SID, sid);
            startActivityForResult(intent, REQUEST_CODE_SELECT_BANK_ACCOUNT);

        } else if (i == R.id.btn_add_bank_account_confirm) {
            closeSoftInput();
            checkInfo();
        }
    }

    public void checkInfo() {
        Editable accountNum = edt_bank_account_no.getText();
        if (!TextUtils.isEmpty(accountNum)) {
            bankAccount();
        }
    }

    public void checkBtn() {
        String accountNum = edt_bank_account_no.getText().toString();
        String phone = edt_bank_account_phone.getText().toString();
        if (!TextUtils.isEmpty(accountNum) && accountNum.length() >= 6
                && !TextUtils.isEmpty(edt_bank_account_name.getText().toString())
                && !TextUtils.isEmpty(tv_select_bank.getText().toString())
                && !TextUtils.isEmpty(phone) && phone.length() >= 7) {
            btn_add_bank_account_confirm.setEnabled(true);
        } else {
            btn_add_bank_account_confirm.setEnabled(false);
        }
    }

    private class CommomTextWatcher implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            checkBtn();
        }
    }

    /**
     * 申请添加银行账户
     */
    public void bankAccount() {
        String name = edt_bank_account_name.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ToastUtil.showToastShort(AddBankAccountActivity.this, getString(R.string.Toast_U0));
            return;
        }
        LoadingDialog loadingDialog = showLoading();
        String no = edt_bank_account_no.getText().toString();
        String phone = edt_bank_account_phone.getText().toString();
        String bankAccountNo = AesKeyCryptor.encodePayPwd(no);
        BindAccountBean bindAccountBean = new BindAccountBean();
        bindAccountBean.setBankAccountNo(bankAccountNo);
        bindAccountBean.setName(name);
        bindAccountBean.setPhone(phone);
        bindAccountBean.setBanckCode(bankCode);
        bindAccountBean.setToken(token);

        BankAccountSdk.bindAccount(this, sid, bindAccountBean,
                new BankAccountSdkCallBack() {
                    @Override
                    public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                        closeLoading(loadingDialog);
                        BindAccountRsp response = BindAccountRsp.decodeJson(BindAccountRsp.class, jsonObject);
                        if (response != null && TextUtils.equals(code, BankAccountSdk.LOCAL_PAY_SUCCESS)) {
                            Intent intent = new Intent(AddBankAccountActivity.this, EndingBindAccountActivity.class);
                            intent.putExtra(Constants.LOCAL_NAME_KEY, response.getBankName());
                            intent.putExtra(Constants.LOCAL_BANK_CODE_KEY, response.getBankAccount());
                            startActivity(intent);
                            finish();
                        }else if (TextUtils.equals(code, Constants.RET_CODE_A010)) {
                            showErrorDialog(errorMsg);
                        }else{
                            showToast(errorMsg);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_BANK_ACCOUNT) {
            if (resultCode == RESULT_CODE_SELECT_BANK_ACCOUNT) {
                if (data != null) {
                    tv_select_bank.setHint("");
                    tv_select_bank.setText(data.getStringExtra(Constants.LOCAL_NAME_KEY));
                    bankCode = data.getStringExtra(Constants.LOCAL_BANK_CODE_KEY);
                    checkBtn();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AccountSdkMain.getInstance().addBankAccountCallback(AccountSdk.LOCAL_PAY_USER_CANCEL, null, null);
    }
}
