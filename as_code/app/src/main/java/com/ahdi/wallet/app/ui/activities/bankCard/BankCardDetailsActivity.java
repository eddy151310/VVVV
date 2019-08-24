package com.ahdi.wallet.app.ui.activities.bankCard;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.sdk.BankCardSdk;
import com.ahdi.wallet.app.callback.BankCardSdkCallBack;
import com.ahdi.wallet.app.response.BindCardDetailsRsp;
import com.ahdi.wallet.app.schemas.CardBindSchema;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.imagedown.ImageDownUtil;
import com.ahdi.lib.utils.widgets.dialog.CommonDialog;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.app.main.AppMain;

import org.json.JSONObject;

/**
 * @lixue 卡详情页面
 */

public class BankCardDetailsActivity extends AppBaseActivity {

    private ImageView img_card_icon;
    private TextView tv_card_bank;
    private TextView tv_card_type;
    private TextView tv_card_number;
    private TextView tv_daily_limit;
    private TextView tv_per_fee;
    private Button btn_set_limit;

    private String bid;
    private CardBindSchema cardBindSchema;
    private View ll_details;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.activity_bank_card_detail);
        initTitle();
        initView();
        initData(false);
    }

    private void initTitle() {
        initCommonTitle(getString(R.string.BankCardDetails_A0));
    }

    private void initView() {

        ll_details = findViewById(R.id.ll_details);

        img_card_icon = findViewById(R.id.img_card_icon);
        tv_card_bank = findViewById(R.id.tv_card_bank);
        tv_card_type = findViewById(R.id.tv_card_type);
        tv_card_number = findViewById(R.id.tv_card_number);
        tv_daily_limit = findViewById(R.id.tv_daily_limit);
        tv_per_fee = findViewById(R.id.tv_per_fee);
        btn_set_limit = findViewById(R.id.btn_set_limit);
        btn_set_limit.setOnClickListener(this);
        Button btn_delete = findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(this);

        bid = getIntent().getStringExtra(Constants.LOCAL_BIND_ID_KEY);
    }


    private void initData(boolean isShowToast) {
        AppMain.getInstance().bindCardDetails(this, bid, new BindCardDetailListener(showLoading(), isShowToast));
    }

    private class BindCardDetailListener implements BankCardSdkCallBack {
        private LoadingDialog loadingDialog;
        private boolean isShowToast;

        BindCardDetailListener(LoadingDialog loadingDialog, boolean isShowToast) {
            this.loadingDialog = loadingDialog;
            this.isShowToast = isShowToast;
        }

        @Override
        public void onResult(String code, String errorMsg, JSONObject jsonObject) {
            closeLoading(loadingDialog);
            BindCardDetailsRsp resp = BindCardDetailsRsp.decodeJson(BindCardDetailsRsp.class, jsonObject);
            if (resp != null && TextUtils.equals(code, BankCardSdk.LOCAL_PAY_SUCCESS)) {
                ll_details.setVisibility(View.VISIBLE);
                if (isShowToast) {
                    showToast(getString(R.string.Toast_D1));
                }
                cardBindSchema = resp.getBindSchema();
                ImageDownUtil.downBankIconImage(BankCardDetailsActivity.this, cardBindSchema.icon, img_card_icon);
                if (cardBindSchema.isEditLimit) {
                    btn_set_limit.setVisibility(View.VISIBLE);
                } else {
                    btn_set_limit.setVisibility(View.GONE);
                }
                tv_card_bank.setText(cardBindSchema.bank);
                tv_card_number.setText(cardBindSchema.card);
                tv_card_type.setText(cardBindSchema.type);
                if (!TextUtils.isEmpty(cardBindSchema.limitDesc)) {
                    tv_daily_limit.setVisibility(View.VISIBLE);
                    tv_daily_limit.setText(cardBindSchema.limitDesc);
                } else {
                    tv_daily_limit.setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(cardBindSchema.feeDesc)) {
                    tv_per_fee.setVisibility(View.VISIBLE);
                    tv_per_fee.setText(cardBindSchema.feeDesc);
                } else {
                    tv_per_fee.setVisibility(View.GONE);
                }
            } else {
                showToast(errorMsg);
                if (!isShowToast) {
                    finish();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (!isCanClick()) {
            return;
        }
        if (v.getId() == R.id.btn_set_limit) {//修改限额
            AppMain.getInstance().setBindLimit(this, bid, new SetBindLimitListener(showLoading()));

        } else if (v.getId() == R.id.btn_delete) {//解绑卡
            showDeleteBankDialog();
        }
    }

    /**
     * 删除卡
     */
    private void showDeleteBankDialog() {
        if (cardBindSchema == null) {
            return;
        }
        String message = String.format(getString(R.string.DialogMsg_W0), cardBindSchema.bank, cardBindSchema.card);
        new CommonDialog.
                Builder(BankCardDetailsActivity.this)
                .setCancelable(false)
                .setMessage(message)
                .setMessageCenter(true)
                .setNegativeButton(getString(R.string.DialogButton_H0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        unBindCard();
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(getString(R.string.DialogButton_A0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void unBindCard() {
        AppMain.getInstance().unBindCard(this, cardBindSchema.bid, Constants.LOCAL_FROM_DELETE_BANK_CARD, new UnBinderCardsCallBack());
    }

    private class UnBinderCardsCallBack implements BankCardSdkCallBack {

        @Override
        public void onResult(String code, String errorMsg, JSONObject jsonObject) {
            if (TextUtils.equals(code, BankCardSdk.LOCAL_PAY_SUCCESS)) {
                showToast(getString(R.string.Toast_N0));
                setResult(RESULT_OK);
                finish();
            } else if (TextUtils.equals(code, BankCardSdk.LOCAL_PAY_SYSTEM_EXCEPTION)
                    || TextUtils.equals(code, BankCardSdk.LOCAL_PAY_NETWORK_EXCEPTION)) {
                showToast(errorMsg);
            }
        }
    }

    private class SetBindLimitListener implements BankCardSdkCallBack {

        private LoadingDialog loadingDialog;

        public SetBindLimitListener(LoadingDialog loadingDialog) {
            this.loadingDialog = loadingDialog;
        }

        @Override
        public void onResult(String code, String errorMsg, JSONObject jsonObject) {
            closeLoading(loadingDialog);
            if (code.equals(BankCardSdk.LOCAL_PAY_SUCCESS)) {
                initData(true);
            } else {
                showToast(errorMsg);
            }
        }
    }
}
