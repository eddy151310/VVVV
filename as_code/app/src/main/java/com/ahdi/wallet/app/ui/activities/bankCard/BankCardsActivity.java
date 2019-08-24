package com.ahdi.wallet.app.ui.activities.bankCard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.ahdi.wallet.app.sdk.BankCardSdk;
import com.ahdi.wallet.app.callback.BankCardSdkCallBack;
import com.ahdi.wallet.app.response.QueryBindCardInfoRsp;
import com.ahdi.wallet.app.schemas.CardBindSchema;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.widgets.CheckSafety;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.wallet.app.ui.activities.payPwd.PayPwdGuideSetActivity;
import com.ahdi.wallet.app.ui.adapters.BankCardsAdapter;
import com.ahdi.lib.utils.listener.OnItemClickListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 银行卡列表页
 */
public class BankCardsActivity extends AppBaseActivity {

    private static final String TAG = BankCardsActivity.class.getSimpleName();

    private final static int REQUEST_CODE_PARAM_STRING = 10;

    private List<CardBindSchema> bindSchemaList = new ArrayList<>();
    private BankCardsAdapter adapter;
    private RelativeLayout rl_no_card_list;
    private RecyclerView recyclerView_list;
    private View bg_view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.activity_bank_cards);
        initView();
        initData(true);
    }

    private void initView() {
        initCommonTitle(getString(R.string.BankCardHome_A0));

        bg_view = findViewById(R.id.bg_view);

        findViewById(R.id.ll_add_card).setOnClickListener(this);

        rl_no_card_list = findViewById(R.id.rl_no_card_list);

        recyclerView_list = findViewById(R.id.bank_card_list);

        adapter = new BankCardsAdapter(this, bindSchemaList);
        recyclerView_list.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_list.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                // 到达卡详情页面
                openBankCardDetail(position);
            }
        });
    }

    private void openBankCardDetail(int position) {
        Intent intent = new Intent(this, BankCardDetailsActivity.class);
        intent.putExtra(Constants.LOCAL_BIND_ID_KEY, bindSchemaList.get(position).bid);
        startActivityForResult(intent, REQUEST_CODE_PARAM_STRING);
    }

    private void initData(boolean isFirstLoad) {
        AppMain.getInstance().queryBinderCard(this, new BinderCardsCallBack(showLoading(), isFirstLoad));
    }


    @Override
    public void onClick(View v) {
        if (!isCanClick()) {
            return;
        }
        if (v.getId() == R.id.ll_add_card) {
            toBindCard();
        }
    }

    private void toBindCard() {
        if (CheckSafety.checkSafe(this, PayPwdGuideSetActivity.class)) {
            AppMain.getInstance().bindCard(this, new BankCardSdkCallBack() {
                @Override
                public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                    if (code.equals(BankCardSdk.LOCAL_PAY_SUCCESS)) {
                        // 绑卡成功刷新卡列表
                        initData(false);
                    } else {
                        // 停留在当前页面
                        showToast(errorMsg);
                    }
                }
            });
        }
    }

    /**
     * 查询绑卡信息接口回调
     */
    private class BinderCardsCallBack implements BankCardSdkCallBack {

        LoadingDialog loadingDialog;
        /**
         * 是否为第一次加载
         */
        private boolean isFirstLoad;

        BinderCardsCallBack(LoadingDialog dialog, boolean isFirstLoad) {
            loadingDialog = dialog;
            this.isFirstLoad = isFirstLoad;
        }

        @Override
        public void onResult(String code, String errorMsg, JSONObject jsonObject) {
            closeLoading(loadingDialog);
            QueryBindCardInfoRsp resp = QueryBindCardInfoRsp.decodeJson(QueryBindCardInfoRsp.class, jsonObject);
            if (resp != null && TextUtils.equals(code, BankCardSdk.LOCAL_PAY_SUCCESS)) {
                CardBindSchema[] bindSchemas = resp.getBind();
                bindSchemaList.clear();
                if (bindSchemas != null && bindSchemas.length > 0) {
                    showCardListView();
                    Collections.addAll(bindSchemaList, bindSchemas);
                    adapter.notifyDataSetChanged();

                } else {
                    showNoCardListView();
                }

                if (bindSchemas != null) {
                    LogUtil.e(TAG, "bindSchemas: " + bindSchemas.length);
                }
            } else {
                showToast(errorMsg);
                if (isFirstLoad) {
                    showNoCardListView();
                    isFirstLoad = false;

                } else {
                    showCardListView();
                }
            }
        }
    }

    private void showCardListView() {
        rl_no_card_list.setVisibility(View.GONE);
        bg_view.setBackgroundResource(R.color.CC_F1F2F6);
        recyclerView_list.setVisibility(View.VISIBLE);
    }

    private void showNoCardListView() {
        rl_no_card_list.setVisibility(View.VISIBLE);
        bg_view.setBackgroundResource(R.color.CC_FFFFFF);
        recyclerView_list.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PARAM_STRING && resultCode == Activity.RESULT_OK) {
            // 绑卡成功刷新卡列表, 更新相关信息
            initData(false);
        }
    }
}
