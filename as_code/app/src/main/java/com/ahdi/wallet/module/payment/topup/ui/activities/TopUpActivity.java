package com.ahdi.wallet.module.payment.topup.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.ahdi.wallet.module.payment.topup.TopUpSdk;
import com.ahdi.wallet.module.payment.topup.callback.TopUpCallBack;
import com.ahdi.wallet.app.response.QueryRechrQuotaRsp;
import com.ahdi.wallet.module.payment.topup.response.RechrTypeQueryRsp;
import com.ahdi.wallet.module.payment.topup.schemas.RechrTypeGroupSchema;
import com.ahdi.wallet.module.payment.topup.schemas.RechrTypeSchema;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.lib.utils.listener.OnItemClickListener;
import com.ahdi.wallet.module.payment.topup.ui.adapter.RechrTypeAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 选择充值类型页面
 */
public class TopUpActivity extends AppBaseActivity {

    private List<RechrTypeSchema> rechrTypeList = new ArrayList<>();
    private RecyclerView rv_rechr_type;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.activity_topup);
        initRechrTypeListData();
        initCommonTitle(getString(R.string.RechrMode_A0));
        ActivityManager.getInstance().addCommonActivity(this);
        initView();
    }

    private void initView() {
        rv_rechr_type = findViewById(R.id.rv_rechr_type);
    }

    private void initRechrTypeListData() {
        LoadingDialog loadingDialog = showLoading();
        // 获取充值类型
        AppMain.getInstance().onRechrTypeQuery(this, new TopUpCallBack() {

            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject, String OT) {
                closeLoading(loadingDialog);
                RechrTypeQueryRsp rsp = RechrTypeQueryRsp.decodeJson(RechrTypeQueryRsp.class, jsonObject);
                if (TextUtils.equals(code, TopUpSdk.LOCAL_PAY_SUCCESS) && rsp != null) {
                    RechrTypeGroupSchema[] rechrTypeGroupSchemas = rsp.getRechrTypeGroupSchemas();
                    List<RechrTypeGroupSchema> rechrTypeGroupList = new ArrayList<>(Arrays.asList(rechrTypeGroupSchemas));
                    initRechrTypeListData(rechrTypeGroupList);
                    refreshRechrTypeRecycleView();

                } else {
                    showToast(errorMsg);
                }
            }
        });
    }

    private void refreshRechrTypeRecycleView() {
        rv_rechr_type.setLayoutManager(new LinearLayoutManager(this));
        RechrTypeAdapter adapter = new RechrTypeAdapter(this, rechrTypeList);
        rv_rechr_type.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                RechrTypeSchema rechrTypeSchema = rechrTypeList.get(position);
                if (TextUtils.equals(rechrTypeSchema.Invoke, "native")) {
                    // 本地余额充值入口
                    queryRechargeQuota();

                } else if (TextUtils.equals(rechrTypeSchema.Invoke, "web")) {
                    openWebCommonActivity(rechrTypeSchema.url);
                }
            }
        });
    }

    /**
     * 遍历充值类型组信息
     *
     * @param rechrTypeGroupList
     */
    private void initRechrTypeListData(List<RechrTypeGroupSchema> rechrTypeGroupList) {
        if (rechrTypeGroupList != null && rechrTypeGroupList.size() > 0) {
            for (int i = 0; i < rechrTypeGroupList.size(); i++) {
                RechrTypeGroupSchema rechrTypeGroupSchema = rechrTypeGroupList.get(i);
                String rechrTypeGroupName = rechrTypeGroupSchema.Name;
                RechrTypeSchema[] rechrTypeSchemas = rechrTypeGroupSchema.getRechrTypeSchemas();
                if (rechrTypeSchemas != null) {
                    List<RechrTypeSchema> rechrTypeSchemaList = Arrays.asList(rechrTypeSchemas);
                    setRechrTypeGroupName(rechrTypeGroupName, rechrTypeSchemaList);
                }
            }
        }
    }

    /**
     * 给每一组的第一个schema 添加组名, 用于item展示或隐藏充值类型组的名字
     *
     * @param rechrTypeGroupName
     * @param rechrTypeSchemaList
     */
    private void setRechrTypeGroupName(String rechrTypeGroupName, List<RechrTypeSchema> rechrTypeSchemaList) {
        if (rechrTypeSchemaList != null && rechrTypeSchemaList.size() > 0) {
            for (int j = 0; j < rechrTypeSchemaList.size(); j++) {
                RechrTypeSchema rechrTypeSchema = rechrTypeSchemaList.get(j);
                if (j == 0) {
                    // 设置item组名称
                    rechrTypeSchema.setRechrTypeGroupName(rechrTypeGroupName);
                }
                if (j == rechrTypeSchemaList.size() - 1) {
                    // 设置item隐藏底线
                    rechrTypeSchema.setHiddenBottomLine(true);
                }
                rechrTypeList.add(rechrTypeSchema);
            }
        }
    }

    /**
     * 查询充值额度
     */
    private void queryRechargeQuota() {
        LoadingDialog loadingDialog = showLoading();
        AppMain.getInstance().onQueryRechargeQuota(this, new TopUpCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject, String OT) {
                closeLoading(loadingDialog);
                QueryRechrQuotaRsp response = QueryRechrQuotaRsp.decodeJson(QueryRechrQuotaRsp.class, jsonObject);
                if (response != null && TextUtils.equals(code, TopUpSdk.LOCAL_PAY_SUCCESS)) {
                    Intent intent = new Intent(TopUpActivity.this, TopUpInputAmountActivity.class);
                    intent.putExtra(TopUpInputAmountActivity.RECHARGE_AMOUNT_KEY, response.rechargeAmount);
                    startActivity(intent);

                } else {
                    showToast(errorMsg);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityManager.getInstance().removeCommonActivity(this);
    }
}
