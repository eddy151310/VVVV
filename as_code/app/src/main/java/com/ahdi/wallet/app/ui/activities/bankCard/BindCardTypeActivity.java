package com.ahdi.wallet.app.ui.activities.bankCard;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.sdk.BankCardSdk;
import com.ahdi.wallet.app.data.PaymentGlobalData;
import com.ahdi.wallet.app.main.BankCardSdkMain;
import com.ahdi.wallet.app.schemas.BindCardTypeSchema;
import com.ahdi.wallet.app.ui.adapters.BindCardTypeAdapter;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.listener.OnItemClickListener;
import com.ahdi.lib.utils.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Date: 2019/2/18 下午4:21
 * Author: kay lau
 * Description:
 */
public class BindCardTypeActivity extends AppBaseActivity {

    private static final String TAG = BindCardTypeActivity.class.getSimpleName();

    private String sid;

    private List<BindCardTypeSchema> bindCardTypeList = new ArrayList<>();
    private BindCardTypeAdapter adapter;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.activity_bind_card_type);
        initView();
        initData();
        ActivityManager.getInstance().addBindBankCardActivity(this);
    }

    private void initView() {
        initCommonTitle(getString(R.string.BindCardType_A0));
        RecyclerView recyclerview_bind_card_type = findViewById(R.id.recyclerview_bind_card_type);
        adapter = new BindCardTypeAdapter(bindCardTypeList);
        recyclerview_bind_card_type.setLayoutManager(new LinearLayoutManager(BindCardTypeActivity.this));
        recyclerview_bind_card_type.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String bank = bindCardTypeList.get(position).bank;
                BankCardSdkMain.getInstance().bindCard(BindCardTypeActivity.this, sid, bank, token, showLoading());
            }
        });
    }

    private void initData() {
        sid = getIntent().getStringExtra(Constants.LOCAL_SID_KEY);
        token = getIntent().getStringExtra(Constants.LOCAL_TOKEN_KEY);
        BindCardTypeSchema[] cardTypeSchemas = PaymentGlobalData.getInstance().getBindCardTypeSchemas();
        bindCardTypeList.clear();
        if (cardTypeSchemas != null && cardTypeSchemas.length > 0) {
            Collections.addAll(bindCardTypeList, cardTypeSchemas);
            adapter.notifyDataSetChanged();
        }
        if (cardTypeSchemas != null) {
            LogUtil.e(TAG, "bindSchemas: " + cardTypeSchemas.length);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BankCardSdk.onDestroy();
        ActivityManager.getInstance().removeBindBankCardActivity(this);
    }
}
