package com.ahdi.wallet.app.ui.activities.bankAccount;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ahdi.wallet.app.sdk.AccountSdk;
import com.ahdi.wallet.app.sdk.BankAccountSdk;
import com.ahdi.wallet.app.callback.AccountSdkCallBack;
import com.ahdi.wallet.app.callback.BankAccountSdkCallBack;
import com.ahdi.wallet.app.response.QueryBankAccountInfoRsp;
import com.ahdi.wallet.app.schemas.BankAccountSchema;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.widgets.CheckSafety;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.wallet.app.ui.activities.payPwd.PayPwdGuideSetActivity;
import com.ahdi.wallet.app.ui.adapters.BankAccountAdapter;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 银行账户表页
 */
public class BankAccountActivity extends AppBaseActivity {

    private static final int REQUEST_CODE_MANAGE_BANK_ACCOUNT = 3;

    private List<BankAccountSchema> bindSchemaList = new ArrayList<>();
    private RecyclerView bank_account_list;
    private BankAccountAdapter adapter;
    private View ll_add_bank_account, rl_no_account_list;
    private TextView tv_tips;
    private Button btnManageAccount;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.activity_bankaccount_cards);
        initView();
        initData();
    }

    private void initView() {
        initCommonTitle(getString(R.string.BankAccountHome_A0));
        btnManageAccount = findViewById(R.id.btn_next);
        btnManageAccount.setOnClickListener(this);
        btnManageAccount.setBackgroundResource(R.drawable.selector_btn_manager_bank_account);

        rl_no_account_list = findViewById(R.id.rl_no_account_list);

        tv_tips = findViewById(R.id.tv_tips);
        ll_add_bank_account = findViewById(R.id.ll_add_bank_account);
        ll_add_bank_account.setOnClickListener(this);

        bank_account_list = findViewById(R.id.bank_account_list);
    }

    private void initData() {
        // 默认显示无卡列表view
        showNoBankAccountList();

        adapter = new BankAccountAdapter(this, bindSchemaList);
        bank_account_list.setLayoutManager(new LinearLayoutManager(this));
        bank_account_list.setAdapter(adapter);

        // 查询绑定银行账户信息
        onQueryBindAccount();
    }

    @Override
    public void onClick(View v) {
        if (!isCanClick()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.ll_add_bank_account) {
            //添加新卡逻辑 判断是否已经设置支付密码
            if (CheckSafety.checkSafe(this, PayPwdGuideSetActivity.class)) {
                onAddBankAccount();
            }
        } else if (id == R.id.btn_next) {
            Intent intent = new Intent(this, ManageBankAccountActivity.class);
            intent.putExtra(Constants.LOCAL_BANK_KEY, (Serializable) bindSchemaList);
            startActivityForResult(intent, REQUEST_CODE_MANAGE_BANK_ACCOUNT);
        }
    }

    private void onAddBankAccount() {
        AppMain.getInstance().addBankAccount(this, AccountSdk.PWD_CHECK_TTYPE_BINDBANKACCOUNT, new AccountSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                if (TextUtils.equals(code, AccountSdk.LOCAL_PAY_SUCCESS)) {
                    onQueryBindAccount();
                }
            }
        });
    }


    private void onQueryBindAccount() {
        AppMain.getInstance().queryBinderAccount(this, new BinderAccountCallBack(showLoading()));
    }

    /**
     * 绑卡信息接口回调
     */
    private class BinderAccountCallBack implements BankAccountSdkCallBack {

        LoadingDialog loadingDialog;

        public BinderAccountCallBack(LoadingDialog dialog) {
            loadingDialog = dialog;
        }

        @Override
        public void onResult(String code, String errorMsg, JSONObject jsonObject) {
            closeLoading(loadingDialog);
            QueryBankAccountInfoRsp resp = QueryBankAccountInfoRsp.decodeJson(QueryBankAccountInfoRsp.class, jsonObject);
            if (resp != null && TextUtils.equals(code, BankAccountSdk.LOCAL_PAY_SUCCESS)) {
                BankAccountSchema[] bindSchemas = resp.getBind();
                bindSchemaList.clear();
                if (bindSchemas != null && bindSchemas.length > 0) {
                    bindSchemaList.addAll(Arrays.asList(bindSchemas));
                    if (bindSchemaList.size() >= 5) {
                        hiddenBottomAddBtn();

                    } else {
                        showBottomAddBtn();
                    }
                    showBankAccountList();
                    adapter.notifyDataSetChanged();
                } else {
                    showNoBankAccountList();
                    adapter.notifyDataSetChanged();
                }
                updateBtn();
            } else {
                showToast(errorMsg);
            }
        }
    }

    /**
     * 根据账户列表长度, 更新底部按钮和title右边按钮的隐藏与显示
     */
    public void updateBtn() {
        if (bindSchemaList.size() > 0) {
            if (bindSchemaList.size() >= 5) {
                hiddenBottomAddBtn();
            } else {
                showBottomAddBtn();
            }
            btnManageAccount.setVisibility(View.VISIBLE);
        } else {
            btnManageAccount.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MANAGE_BANK_ACCOUNT && resultCode == RESULT_OK) {
            if (data != null) {
                bindSchemaList.clear();
                List<BankAccountSchema> list = (List<BankAccountSchema>) data.getSerializableExtra(Constants.LOCAL_BANK_KEY);
                bindSchemaList.addAll(list);
            } else {
                bindSchemaList.clear();
            }
            updateBtn();
            adapter.notifyDataSetChanged();
        }
    }

    private void hiddenBottomAddBtn() {
        ll_add_bank_account.setVisibility(View.GONE);
        tv_tips.setVisibility(View.GONE);
    }

    private void showBottomAddBtn() {
        ll_add_bank_account.setVisibility(View.VISIBLE);
        tv_tips.setVisibility(View.VISIBLE);
    }

    private void showNoBankAccountList() {
        rl_no_account_list.setVisibility(View.VISIBLE);
        bank_account_list.setVisibility(View.GONE);
    }

    private void showBankAccountList() {
        rl_no_account_list.setVisibility(View.GONE);
        bank_account_list.setVisibility(View.VISIBLE);
    }
}
