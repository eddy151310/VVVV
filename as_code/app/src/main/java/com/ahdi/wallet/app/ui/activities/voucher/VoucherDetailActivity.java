package com.ahdi.wallet.app.ui.activities.voucher;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ahdi.wallet.app.callback.VoucherSdkCallBack;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.response.VoucherDetailRsp;
import com.ahdi.wallet.app.schemas.VoucherDetailSchema;
import com.ahdi.wallet.app.schemas.VoucherSchema;
import com.ahdi.wallet.app.utils.ConstantsPayment;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.imagedown.ImageDownUtil;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.wallet.app.ui.adapters.VoucherLimitAdapter;
import com.ahdi.wallet.app.ui.adapters.VoucherMoreRulesAdapter;

import java.util.List;

/**
 * 优惠券详情页面
 *
 * @author zhaohe
 */
public class VoucherDetailActivity extends AppBaseActivity {

    private ImageView iv_voucher_icon, iv_more_rules;
    private TextView tv_voucher_name, tv_voucher_amount_unit, tv_voucher_amount, tv_voucher_status,
            tv_instructions;
    private LinearLayout root_bg, ll_details_content, ll_more_rules_area, ll_instructions_area;
    private RecyclerView rv_limits, rv_more_rules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.activity_voucher_detail);
        initView();
        initData();
    }


    public void initView() {

        root_bg = findViewById(R.id.ll_root_bg);

        initCommonTitle(getString(R.string.VoucherDetail_A0));

        ll_details_content = findViewById(R.id.ll_details_content);
        ll_details_content.setVisibility(View.GONE);
        //优惠券上半部分
        iv_voucher_icon = findViewById(R.id.iv_voucher_icon);
        tv_voucher_name = findViewById(R.id.tv_voucher_name);
        tv_voucher_amount_unit = findViewById(R.id.tv_voucher_amount_unit);
        tv_voucher_amount = findViewById(R.id.tv_voucher_amount);
        tv_voucher_status = findViewById(R.id.tv_voucher_status);
        //优惠券下半部分
        ll_more_rules_area = findViewById(R.id.ll_more_rules_area);
        findViewById(R.id.ll_show_more).setOnClickListener(this);
        iv_more_rules = findViewById(R.id.iv_more_rules);
        rv_limits = findViewById(R.id.rv_limits);
        rv_limits.setLayoutManager(new LinearLayoutManager(this));
        rv_more_rules = findViewById(R.id.rv_more_rules);
        rv_more_rules.setLayoutManager(new LinearLayoutManager(this));
        //底部描述
        ll_instructions_area = findViewById(R.id.ll_instructions_area);
        tv_instructions = findViewById(R.id.tv_instructions);
    }

    private void initData() {
        if (getIntent() == null) {
            finish();
        } else {
            String voucherID = getIntent().getStringExtra(Constants.LOCAL_ID_KEY);
            getVoucherInfo(voucherID);
        }
    }

    private void getVoucherInfo(String voucherID) {
        LoadingDialog loadingDialog = showLoading();
        AppMain.getInstance().getVoucherDetails(VoucherDetailActivity.this, voucherID, new VoucherSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, Response response) {
                loadingDialog.dismiss();
                if (!TextUtils.equals(code, Constants.RET_CODE_SUCCESS)) {
                    showToast(errorMsg);
                    finish();
                    return;
                }
                if (response == null) {
                    finish();
                } else {
                    ll_details_content.setVisibility(View.VISIBLE);
                    refreshUI((VoucherDetailRsp) response);
                }
            }
        });
    }

    /**
     * 刷界面
     *
     * @param voucherDetailResponse
     */
    private void refreshUI(VoucherDetailRsp voucherDetailResponse) {
        if (voucherDetailResponse == null) {
            finish();
        }
        VoucherDetailSchema voucherDetailSchema = voucherDetailResponse.getVoucherDetailSchema();
        if (voucherDetailSchema == null) {
            return;
        }

        //显示优惠券信息
        VoucherSchema voucherSchema = voucherDetailSchema.getVoucherSchema();
        if (voucherSchema == null) {
            finish();
        }
        //根据优惠券状态修改文字颜色
        updateTextColor(voucherSchema.getStatus());
        showVoucherInfo(voucherSchema);

        //显示更多限制
        List<String> othLimits = voucherDetailSchema.getOthLimits();
        if (othLimits != null && othLimits.size() > 0) {
            rv_more_rules.setAdapter(new VoucherMoreRulesAdapter(VoucherDetailActivity.this, othLimits, voucherSchema.getStatus()));
        } else {
            ll_more_rules_area.setVisibility(View.GONE);
        }

        //显示Instructions of Voucher
        String info = voucherDetailSchema.getInfo();
        if (!TextUtils.isEmpty(info)) {
            tv_instructions.setText(info);
            root_bg.setBackgroundResource(R.color.CC_FFFFFF);
        } else {
            ll_instructions_area.setVisibility(View.GONE);
            root_bg.setBackgroundResource(R.color.CC_F1F2F6);
        }
    }

    /**
     * 根据优惠券的状态修改文字颜色
     *
     * @param status
     */
    private void updateTextColor(int status) {
        if (status != ConstantsPayment.VOUCHER_STATUS_VALID) {
            int color = getResources().getColor(R.color.CC_5F5F67);
            tv_voucher_name.setTextColor(color);
            tv_voucher_amount_unit.setTextColor(color);
            tv_voucher_amount.setTextColor(color);
            tv_instructions.setTextColor(color);
        }
    }

    /**
     * 显示优惠券信息
     *
     * @param voucherSchema
     */
    private void showVoucherInfo(VoucherSchema voucherSchema) {
        ImageDownUtil.downVoucherIcon(VoucherDetailActivity.this, voucherSchema.getIcon(), iv_voucher_icon);
        tv_voucher_name.setText(voucherSchema.getName());
        tv_voucher_amount_unit.setText(ConfigCountry.KEY_CURRENCY_SYMBOL);
        tv_voucher_amount.setText(voucherSchema.getMoney());
        if (voucherSchema.getStatus() == ConstantsPayment.VOUCHER_STATUS_VALID) {
            tv_voucher_status.setVisibility(View.GONE);
        } else if (voucherSchema.getStatus() == ConstantsPayment.VOUCHER_STATUS_EXPIRED) {
            tv_voucher_status.setVisibility(View.VISIBLE);
            tv_voucher_status.setText(R.string.VoucherDetail_D0);
        } else if (voucherSchema.getStatus() == ConstantsPayment.VOUCHER_STATUS_USED) {
            tv_voucher_status.setVisibility(View.VISIBLE);
            tv_voucher_status.setText(R.string.VoucherDetail_E0);
        }
        rv_limits.setAdapter(new VoucherLimitAdapter(VoucherDetailActivity.this, voucherSchema.getLimits(), voucherSchema.getStatus() == ConstantsPayment.VOUCHER_STATUS_VALID));
    }

    @Override
    public void onClick(View view) {
        if (!isCanClick()) {
            return;
        }
        int id = view.getId();
        if (id == R.id.ll_show_more) {
            if (rv_more_rules.getVisibility() == View.VISIBLE) {
                iv_more_rules.setBackgroundResource(R.drawable.selector_arrow_down);
                rv_more_rules.setVisibility(View.GONE);
            } else {
                iv_more_rules.setBackgroundResource(R.drawable.selector_arrow_up);
                rv_more_rules.setVisibility(View.VISIBLE);
            }
        }
    }

}