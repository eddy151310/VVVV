package com.ahdi.wallet.app.ui.activities.bankAccount;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.sdk.AccountSdk;
import com.ahdi.wallet.app.main.AccountSdkMain;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;

public class EndingBindAccountActivity extends AppBaseActivity implements View.OnClickListener {

    private TextView tv_bank_account_tip;
    private Button btn_confirm;
    private String bankName;
    private String bankCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.activity_bind_account_result);
        initTitle();
        initView();
        initDate();
    }


    void initTitle(){
        initTitleTextView(getString(R.string.BankAccountEnding_A0));
        initTitleBack().setVisibility(View.INVISIBLE);
    }

    public void initView() {
        tv_bank_account_tip = findViewById(R.id.tv_bank_account_info);
        btn_confirm = findViewById(R.id.btn_bank_account_confirm);
        btn_confirm.setOnClickListener(this);
    }

    public void initDate() {
        Intent intent = getIntent();
        bankName = intent.getStringExtra(Constants.LOCAL_NAME_KEY);
        bankCode = intent.getStringExtra(Constants.LOCAL_BANK_CODE_KEY);
        tv_bank_account_tip.setText(Html.fromHtml(String.format(getResources().getString(R.string.BankAccountEnding_B0), bankName + "(" + bankCode + ")")));
    }

    @Override
    public void onClick(View view) {
        if (!isCanClick()) {
            return;
        }
        if (view.getId() == R.id.btn_bank_account_confirm) {
            AccountSdkMain.getInstance().addBankAccountCallback(AccountSdk.LOCAL_PAY_SUCCESS, "", null);
            finish();
        }
    }
}
