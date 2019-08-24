package com.ahdi.wallet.module.payment.withdraw.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ahdi.wallet.app.sdk.AccountSdk;
import com.ahdi.wallet.app.callback.AccountSdkCallBack;
import com.ahdi.wallet.app.ui.activities.payPwd.PayPwdGuideSetActivity;
import com.ahdi.wallet.module.payment.withdraw.schemas.WDBankAccountSchema;
import com.ahdi.lib.utils.utils.ToolUtils;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.wallet.module.payment.withdraw.ui.adapter.WDAccountAdapter;
import com.ahdi.lib.utils.listener.OnItemClickListener;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.widgets.CheckSafety;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SelectWDAccountActivity extends AppBaseActivity {

    private List<WDBankAccountSchema> bindSchemaList = new ArrayList<>();
    private RecyclerView bank_account_list;
    private View layout_add_card_view;
    private TextView tv_bank_account_tip;
    private int mPosition = 0;
    public static final int RESULT_CODE_SELECT_BANK_ACCOUNT = 1;
    public static final int RESULT_CODE_BANK_ACCOUNT_ADD_SUCCESS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        initIntentData(getIntent());
        setContentView(R.layout.activity_select_bank_account);
        initView();
        initData();
        initAddBankView();
    }

    private void initIntentData(Intent intent) {
        if (intent != null) {
            mPosition = intent.getIntExtra(Constants.LOCAL_POSITION_KEY, 0);
            bindSchemaList = (List<WDBankAccountSchema>) intent.getSerializableExtra(Constants.LOCAL_BANK_KEY);
        }
    }

    private void initView() {
        initCommonTitle(getString(R.string.WithdrawSelectAccount_A0));

        layout_add_card_view = findViewById(R.id.layout_add_card_view);
        layout_add_card_view.setOnClickListener(this);
        ((TextView) layout_add_card_view.findViewById(R.id.tv_add)).setText(R.string.WithdrawSelectAccount_B0);
        tv_bank_account_tip = findViewById(R.id.tv_bank_account_tip);
        bank_account_list = findViewById(R.id.bank_account_list);
    }

    private void initData() {
        WDAccountAdapter adapter = new WDAccountAdapter(this, bindSchemaList, mPosition);
        bank_account_list.setLayoutManager(new LinearLayoutManager(this));
        bank_account_list.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (ToolUtils.isCanClick()) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.LOCAL_POSITION_KEY, position);
                    setResult(RESULT_CODE_SELECT_BANK_ACCOUNT, intent);
                    finish();
                }
            }
        });

    }

    public void initAddBankView() {
        if (bindSchemaList != null) {
            if (bindSchemaList.size() > 0 && bindSchemaList.size() <= 5) {
                layout_add_card_view.setVisibility(View.VISIBLE);
                tv_bank_account_tip.setVisibility(View.VISIBLE);

            } else {
                layout_add_card_view.setVisibility(View.GONE);
                tv_bank_account_tip.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (!isCanClick()) {
            return;
        }
        if (v.getId() == R.id.layout_add_card_view) {
            //添加新卡逻辑 判断是否已经设置支付密码
            if (CheckSafety.checkSafe(this, PayPwdGuideSetActivity.class)) {
                onAddBankAccount();
            }
        }
    }

    private void onAddBankAccount() {
        AppMain.getInstance().addBankAccount(this, AccountSdk.PWD_CHECK_TTYPE_BINDBANKACCOUNT,
                new AccountSdkCallBack() {
                    @Override
                    public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                        if (TextUtils.equals(code, AccountSdk.LOCAL_PAY_SUCCESS)) {
                            setResult(RESULT_CODE_BANK_ACCOUNT_ADD_SUCCESS);
                            finish();
                        }
                    }
                });
    }
}
