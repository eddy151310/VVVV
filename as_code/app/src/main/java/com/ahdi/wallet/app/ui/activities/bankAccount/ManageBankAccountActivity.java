package com.ahdi.wallet.app.ui.activities.bankAccount;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.ahdi.wallet.app.sdk.BankAccountSdk;
import com.ahdi.wallet.app.callback.BankAccountSdkCallBack;
import com.ahdi.wallet.app.schemas.BankAccountSchema;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.widgets.dialog.CommonDialog;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.wallet.app.ui.adapters.ManagerBankAccountAdapter;
import com.ahdi.lib.utils.listener.OnItemClickListener;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * 管理银行账户表页
 */
public class ManageBankAccountActivity extends AppBaseActivity {

    private List<BankAccountSchema> bindSchemaList = null;
    private RecyclerView bank_account_list;
    private ManagerBankAccountAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.activity_manager_bank_accounts);
        initView();
        initData();
    }

    private void initView() {
        initCommonTitle(getString(R.string.ManagerBankAccount_A0));
        bank_account_list = findViewById(R.id.bank_account_list);
    }

    private void initData() {
        Intent intent = getIntent();
        bindSchemaList = (List<BankAccountSchema>) intent.getSerializableExtra(Constants.LOCAL_BANK_KEY);
        adapter = new ManagerBankAccountAdapter(this, bindSchemaList);
        bank_account_list.setLayoutManager(new LinearLayoutManager(this));
        bank_account_list.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showDeleteBankDialog(position);
            }
        });
    }

    /**
     * 删除卡
     *
     * @param position
     */
    private void showDeleteBankDialog(int position) {
        BankAccountSchema bankAccountSchema = bindSchemaList.get(position);
        if (bankAccountSchema == null) {
            return;
        }
        String message = getString(R.string.ManagerBankAccount_C0, bankAccountSchema.bank + " " + bankAccountSchema.getAccountNo());
        new CommonDialog.
                Builder(this)
                .setCancelable(false)
                .setMessage(message)
                .setMessageCenter(true)
                .setNegativeButton(getString(R.string.DialogButton_A0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(getString(R.string.DialogButton_B0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        unBindCard(position);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void unBindCard(int position) {
        AppMain.getInstance().unBindAccount(this, bindSchemaList.get(position).getSt(), new UnBinderAccountCallBack(position));
    }

    private class UnBinderAccountCallBack implements BankAccountSdkCallBack {
        private int mPosition;

        public UnBinderAccountCallBack(int mPosition) {
            this.mPosition = mPosition;
        }

        @Override
        public void onResult(String code, String errorMsg, JSONObject jsonObject) {

            if (TextUtils.equals(code, BankAccountSdk.LOCAL_PAY_SUCCESS)) {
                showToast(getString(R.string.Toast_N0));
                bindSchemaList.remove(mPosition);
                adapter.notifyDataSetChanged();
                if (bindSchemaList.size() == 0) {
                    setResult(RESULT_OK);
                    finish();
                }
            } else {
                showToast(errorMsg);
            }
        }
    }

    public void backToFinish() {
        Intent intent = new Intent();
        intent.putExtra(Constants.LOCAL_BANK_KEY, (Serializable) bindSchemaList);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        backToFinish();
    }
}
