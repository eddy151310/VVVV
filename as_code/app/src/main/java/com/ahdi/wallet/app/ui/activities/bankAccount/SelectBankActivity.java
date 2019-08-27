package com.ahdi.wallet.app.ui.activities.bankAccount;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.sdk.BankAccountSdk;
import com.ahdi.wallet.app.callback.BankAccountSdkCallBack;
import com.ahdi.wallet.app.response.BankAccountListRsp;
import com.ahdi.wallet.app.schemas.BankSchema;
import com.ahdi.lib.utils.widgets.SideBarIndex;
import com.ahdi.wallet.app.ui.adapters.SortBankAdapter;
import com.ahdi.wallet.app.ui.widgets.BankListProvider;
import com.ahdi.wallet.app.ui.widgets.PinyinComparator;
import com.ahdi.wallet.app.utils.ConstantsPayment;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class SelectBankActivity extends AppBaseActivity {

    private ListView sortListView;
    private SortBankAdapter adapter;
    private EditText mEtSearchName;
    private ImageView iv_clear_edt;
    private List<BankSchema> bankList = new ArrayList<>();
    private List<BankSchema> addBankList = new ArrayList<>();
    private SideBarIndex sideBar;
    private IntentFilter intentFilter;
    private GetBankListBroadcast getBankListBroadcast;
    private long time = 0;
    private String sid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        initIntentData(getIntent());
        setContentView(R.layout.activity_select_bank);
        intentFilter = new IntentFilter();
        intentFilter.addAction(ConstantsPayment.ACTION_GET_BANK_LIST);
        getBankListBroadcast = new GetBankListBroadcast();
        initCommonTitle(getString(R.string.BankAccountSelectBank_A0));
        initView();
        initDate();
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(getBankListBroadcast, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(getBankListBroadcast);
    }

    private void initIntentData(Intent intent) {
        if (intent != null) {
            sid = intent.getStringExtra(Constants.LOCAL_KEY_SID);
        }
    }

    public void initView() {
        sortListView = findViewById(R.id.lv_bank);
        mEtSearchName = findViewById(R.id.edt_search);
        iv_clear_edt = findViewById(R.id.iv_clear_edt);
        sideBar = findViewById(R.id.sidrbar);
        TextView dialog = findViewById(R.id.dialog);
        sideBar.setTextView(dialog);
        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBarIndex.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                if (bankList.size() != 0) {
                    int position = adapter.getPositionForSection(s.charAt(0));
                    if (position != -1) {
                        sortListView.setSelection(position);
                    }
                }
            }
        });
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (!isCanClick()) {
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra(Constants.LOCAL_NAME_KEY, ((BankSchema) adapter.getItem(position)).getName());
                intent.putExtra(Constants.LOCAL_BANK_CODE_KEY, ((BankSchema) adapter.getItem(position)).getCode());
                setResult(AddBankAccountActivity.RESULT_CODE_SELECT_BANK_ACCOUNT, intent);
                finish();
            }
        });
        //根据输入框输入值的改变来过滤搜索
        mEtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterSearchData(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        iv_clear_edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCanClick()) {
                    return;
                }
                mEtSearchName.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(mEtSearchName.getWindowToken(), 0);
                }
                mEtSearchName.clearFocus();
                filterSearchData("");
            }
        });

    }

    public void initDate() {
        LoadingDialog loadingDialog = showLoading();
        time = AppGlobalUtil.getInstance().getTimestamp();
        if (time > 0) {
            bankList = BankListProvider.getProvider(SelectBankActivity.this).getBankInfos();
            filledData(bankList, loadingDialog);
            refreshDate();
        } else {
            BankAccountSdk.queryBankAccountList(this, sid, time, new BankAccountSdkCallBack() {
                @Override
                public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                    closeLoading(loadingDialog);
                    BankAccountListRsp response = BankAccountListRsp.decodeJson(BankAccountListRsp.class, jsonObject);
                    if (response != null && TextUtils.equals(code, BankAccountSdk.LOCAL_PAY_SUCCESS)) {
                        BankSchema[] bankSchemas = response.getBank();
                        long timestamp = response.getTimestamp();
                        if (timestamp > 0) {
                            AppGlobalUtil.getInstance().setTimestamp(timestamp);
                        }
                        if (bankSchemas != null && bankSchemas.length > 0) {
                            BankListProvider.getProvider(SelectBankActivity.this).clearAll();
                            bankList = Arrays.asList(bankSchemas);
                            filledData(bankList, showLoading());
                            for (BankSchema bankSchema : bankSchemas) {
                                BankListProvider.getProvider(SelectBankActivity.this).add(bankSchema);
                            }
                        } else {
                            mEtSearchName.setEnabled(false);
                            sideBar.setEnabled(false);
                        }
                    } else {
                        showToast(errorMsg);
                    }
                }
            });
        }
    }

    public void refreshDate() {
        LoadingDialog loadingDialog = showLoading();
        BankAccountSdk.queryBankAccountList(this, sid, time, new BankAccountSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);
                BankAccountListRsp response = BankAccountListRsp.decodeJson(BankAccountListRsp.class, jsonObject);
                if (response != null && TextUtils.equals(code, BankAccountSdk.LOCAL_PAY_SUCCESS)) {
                    BankSchema[] bankSchemas = response.getBank();
                    if (bankSchemas != null && bankSchemas.length > 0) {
                        addBankList.addAll(Arrays.asList(bankSchemas));
                    }
                } else {
                    LogUtil.e("selectBank", errorMsg);
                }
            }
        });
    }

    class GetBankListBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            for (int i = 0; i < addBankList.size(); i++) {
                BankListProvider.getProvider(SelectBankActivity.this).add(addBankList.get(i));
            }
        }
    }

    public void filledData(List<BankSchema> list, LoadingDialog loadingDialog) {
        List<BankSchema> mlist = new ArrayList<BankSchema>();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                BankSchema sort;
                sort = list.get(i);
                String sortString = sort.getName().substring(0, 1).toUpperCase();
                if (sortString.matches("[A-Z]")) {
                    sort.setSortLetters(sortString.toUpperCase());
                } else {
                    sort.setSortLetters("#");
                }
                mlist.add(sort);
            }
        } else {
            mEtSearchName.setEnabled(false);
            sideBar.setEnabled(false);
        }
        Collections.sort(mlist, new PinyinComparator());
        adapter = new SortBankAdapter(this, mlist);
        sortListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        closeLoading(loadingDialog);
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterSearchData(String filterStr) {
        List<BankSchema> mSortList = new ArrayList<>();
        if (TextUtils.isEmpty(filterStr)) {
            mSortList = bankList;
            iv_clear_edt.setVisibility(View.GONE);
        } else {
            iv_clear_edt.setVisibility(View.VISIBLE);
            mSortList.clear();
            for (BankSchema sortModel : bankList) {
                String name = sortModel.getName().toLowerCase();
                if (name.indexOf(filterStr.toLowerCase()) != -1) {
                    mSortList.add(sortModel);
                }
            }
        }
        // 根据a-z进行排序
        Collections.sort(mSortList, new PinyinComparator());
        if (adapter != null) {
            adapter.updateListView(mSortList);
        }
    }
}
