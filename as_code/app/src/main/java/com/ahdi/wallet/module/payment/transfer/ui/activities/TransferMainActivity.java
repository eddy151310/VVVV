package com.ahdi.wallet.module.payment.transfer.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ahdi.wallet.module.payment.transfer.TransferSdk;
import com.ahdi.wallet.module.payment.transfer.callback.TransferResultCallBack;
import com.ahdi.wallet.module.payment.transfer.response.QueryRecentTransContactResp;
import com.ahdi.wallet.module.payment.transfer.response.QueryTargetResp;
import com.ahdi.wallet.module.payment.transfer.schemas.TAContactSchema;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.wallet.module.payment.transfer.ui.adapter.TransferContactAdapter;
import com.ahdi.lib.utils.listener.OnItemClickListener;
import com.ahdi.wallet.app.ui.adapters.listener.PhoneTextWatcher;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author zhaohe
 */
public class TransferMainActivity extends AppBaseActivity {

    private static final String TAG = TransferMainActivity.class.getSimpleName();

    private String areaCode = ConfigCountry.KEY_AREA_CODE;

    private EditText edt_phone;
    private Button btn_next;

    private ArrayList<TAContactSchema> mList;
    private View ll_recent_trans;
    private RecyclerView list_recode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.activity_transfer);
        ActivityManager.getInstance().addCommonActivity(this);

        initCommonTitle(getString(R.string.TransferHome_A0));

        initView();

        // 查询最近转账联系人
        onQueryContact();
    }

    private void initView() {
        TextView tv_phone_area_code = findViewById(R.id.tv_62);
        tv_phone_area_code.setText(ConfigCountry.KEY_ADD_AREA_CODE);

        edt_phone = findViewById(R.id.edt_account_phone);
        PhoneTextWatcher phoneTextWatcher = new PhoneTextWatcher(edt_phone, areaCode) {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //父类根据输入内容处理输入框的长度
                super.onTextChanged(s, start, before, count);
                updateUI(edt_phone.getText().toString());
            }
        };
        edt_phone.addTextChangedListener(phoneTextWatcher);

        btn_next = findViewById(R.id.btn_next_transfer_to_account);
        btn_next.setEnabled(false);
        btn_next.setOnClickListener(this);

        ll_recent_trans = findViewById(R.id.ll_recent_trans);
        ll_recent_trans.setVisibility(View.GONE);

        list_recode = findViewById(R.id.list_recode);
        list_recode.setNestedScrollingEnabled(false); // NestedScrollview嵌套RecyclerView 解决滑动卡顿.
    }

    /**
     * 更新UI
     *
     * @param account
     */
    private void updateUI(String account) {
        if (!TextUtils.isEmpty(account)) {
            int limit = TextUtils.equals(areaCode, ConfigCountry.KEY_AREA_CODE)
                    ? ConfigCountry.PHONE_LIMIT_MIN_LENGTH_8 : ConfigCountry.PHONE_LIMIT_MIN_LENGTH_7;
            if (account.length() >= limit) {
                btn_next.setEnabled(true);
            } else {
                btn_next.setEnabled(false);
            }
        } else {
            btn_next.setEnabled(false);
        }
    }

    private void CheckTransferTarget(String lName) {
        LoadingDialog loadingDialog = showLoading();
        AppMain.getInstance().onQueryTransferTarget(this, lName, GlobalApplication.getApplication().getSID(), new TransferResultCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject, String ID) {
                closeLoading(loadingDialog);
                QueryTargetResp resp = QueryTargetResp.decodeJson(QueryTargetResp.class, jsonObject);
                if (TextUtils.equals(code, TransferSdk.LOCAL_PAY_SUCCESS)) {
                    if (resp != null) {
                        openTransPayConfirm(resp);

                    } else {
                        showToast(errorMsg);
                    }
                } else {
                    showToast(errorMsg);
                }
            }
        });
    }

    private void openTransPayConfirm(QueryTargetResp resp) {
        Intent intent = new Intent(this, TransPayConfirmActivity.class);
        intent.putExtra(Constants.LOCAL_SNAME_KEY, resp.getsName());
        intent.putExtra(Constants.LOCAL_NNAME_KEY, resp.getnName());
        intent.putExtra(Constants.LOCAL_UT_KEY, resp.getUt());
        intent.putExtra(Constants.LOCAL_AVATAR_KEY, resp.getAvatar());
        startActivity(intent);
    }

    /**
     * 查询最近转账联系人
     */
    private void onQueryContact() {
        AppMain.getInstance().onQueryRecentTransContact(this, new TransferResultCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject, String ID) {
                QueryRecentTransContactResp resp = QueryRecentTransContactResp.decodeJson(QueryRecentTransContactResp.class, jsonObject);
                if (TextUtils.equals(code, TransferSdk.LOCAL_PAY_SUCCESS) && resp != null) {
                    TAContactSchema[] contactSchemas = resp.getTaContactSchema();
                    if (mList != null) {
                        mList.clear();
                    } else {
                        mList = new ArrayList<>();
                    }
                    if (contactSchemas != null && contactSchemas.length > 0) {
                        mList.addAll(Arrays.asList(contactSchemas));
                        ll_recent_trans.setVisibility(View.VISIBLE);
                        initRecords();
                    }
                } else {
                    LogUtil.e(TAG, errorMsg);
                }
            }
        });
    }

    public void initRecords() {
        TransferContactAdapter adapter = new TransferContactAdapter(this, mList);
        list_recode.setLayoutManager(new LinearLayoutManager(this));
        list_recode.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isCanClick()) {
                    CheckTransferTarget(mList.get(position).lName);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (!isCanClick()) {
            return;
        }
        if (view.getId() == R.id.btn_next_transfer_to_account) {
            closeSoftInput();
            String lName = edt_phone.getText().toString();
            checkPhoneNum(lName);
        }
    }


    private void checkPhoneNum(String phoneNum) {
        if (!TextUtils.equals(areaCode, ConfigCountry.KEY_AREA_CODE)) {
            CheckTransferTarget(areaCode + phoneNum);
            return;
        }
        if (!TextUtils.isEmpty(phoneNum)
                && (phoneNum.startsWith(ConfigCountry.PHONE_PREFIX_8)
                || phoneNum.startsWith(ConfigCountry.PHONE_PREFIX_08))) {
            CheckTransferTarget(areaCode + phoneNum);
        } else {
            showToast(getString(R.string.Toast_E0));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityManager.getInstance().removeCommonActivity(this);
    }
}