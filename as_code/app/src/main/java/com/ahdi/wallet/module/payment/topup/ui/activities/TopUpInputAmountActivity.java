package com.ahdi.wallet.module.payment.topup.ui.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.app.sdk.IDVerifySdk;
import com.ahdi.wallet.cashier.RechrCashierSdk;
import com.ahdi.wallet.module.payment.topup.TopUpSdk;
import com.ahdi.wallet.app.callback.IDVerifyCallBack;
import com.ahdi.wallet.module.payment.topup.callback.TopUpCallBack;
import com.ahdi.wallet.app.response.QueryRechrQuotaRsp;
import com.ahdi.wallet.cashier.response.rechr.RechrPayResultRsp;
import com.ahdi.wallet.app.utils.ConstantsPayment;
import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.lib.utils.utils.AmountUtil;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.ToolUtils;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.lib.utils.widgets.keyboard.KeyboardUtil;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.lib.utils.base.BaseEditTextWatcher;

import org.json.JSONObject;

import java.math.BigDecimal;

/**
 * 充值输入金额页面
 */
public class TopUpInputAmountActivity extends AppBaseActivity {

    public static final String TAG = TopUpInputAmountActivity.class.getSimpleName();

    public static final String RECHARGE_AMOUNT_KEY = "recharge_amount";

    private Button btn_confirm;
    private EditText et_amount;
    private TextView tv_amount, tv_rp, tv_hint, tv_id_verify;
    private String rechargeQuotaStr;
    private KeyboardUtil keyboardUtil;
    private View view_keyboard, ll_edt_amount;
    private String text;
    private long rechrQuotaLong;
    private String hintDefaultText;
    private String currencyRechrQuotaStr;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        initIntentData(getIntent());
        setContentView(R.layout.layout_activity_top_up_input_amount);
        initView();
        initData();
        ActivityManager.getInstance().addCommonActivity(this);
    }

    private void initIntentData(Intent intent) {
        if (intent != null) {
            rechargeQuotaStr = intent.getStringExtra(RECHARGE_AMOUNT_KEY);
            rechrQuotaLong = new BigDecimal(rechargeQuotaStr).longValue();
            rechargeQuotaStr = String.valueOf(rechrQuotaLong);
            currencyRechrQuotaStr = ConfigCountry.KEY_CURRENCY_SYMBOL.concat(AmountUtil.formatAmount(rechargeQuotaStr));
        }
    }

    private void initView() {

        initCommonTitle(getString(R.string.RechrConfirm_A0));

        // 金额输入框区域
        ll_edt_amount = findViewById(R.id.ll_edt_amount);
        tv_amount = findViewById(R.id.tv_amount);
        tv_rp = findViewById(R.id.tv_rp);
        et_amount = findViewById(R.id.et_amount);

        tv_hint = findViewById(R.id.tv_hint);
        tv_id_verify = findViewById(R.id.tv_id_verify);

        btn_confirm = findViewById(R.id.btn_confirm);

        view_keyboard = findViewById(R.id.view_keyboard);

        initKyeBoardView();
    }

    private void initData() {
        // 设置金额符号
        tv_rp.setText(ConfigCountry.KEY_CURRENCY_SYMBOL);

        et_amount.setOnClickListener(this);

        // 默认提示文案
        hintDefaultText = getString(R.string.RechrConfirm_C0, currencyRechrQuotaStr);
        tv_hint.setText(hintDefaultText);

        tv_id_verify.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
        tv_id_verify.getPaint().setAntiAlias(true);                  // 抗锯齿
        tv_id_verify.setOnClickListener(this);

        btn_confirm.setEnabled(false);
        btn_confirm.setOnClickListener(this);

        if (rechrQuotaLong <= 0) {
            et_amount.setEnabled(false);
            // 置灰输入金额区域
            ll_edt_amount.setBackgroundResource(R.color.CC_E6E6E9);
            int disableColor = ToolUtils.getColor(this, R.color.CC_919399);
            tv_amount.setTextColor(disableColor);
            tv_rp.setTextColor(disableColor);
            et_amount.setHintTextColor(disableColor);

            if (ProfileUserUtil.getInstance().isNeedRNA()) {
                tv_id_verify.setVisibility(View.VISIBLE);
            } else {
                tv_id_verify.setVisibility(View.GONE);
            }
        } else {
            tv_id_verify.setVisibility(View.GONE);
        }
        et_amount.addTextChangedListener(new BaseEditTextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                refreshUI();
            }

            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                if (TextUtils.isEmpty(et_amount.getText().toString())) {
                    et_amount.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                } else {
                    et_amount.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
            }
        });
    }

    /**
     * 输入框金额变化时改变UI
     */
    private void refreshUI() {
        // 默认提示文案颜色.
        int hintDefaultColor = ToolUtils.getColor(this, R.color.CC_5F5F67);

        String edtAmount = et_amount.getText().toString();

        if (!TextUtils.isEmpty(edtAmount)) {
            edtAmount = AmountUtil.unFormatString(edtAmount);
            long inputAmount = AmountUtil.parseLong(edtAmount);
            // 输入金额大于可充值额度
            if (inputAmount > rechrQuotaLong) {
                // 设置提示文案, 字体颜色设置红色
                String tipText = getString(R.string.RechrConfirm_D0, currencyRechrQuotaStr);
                tv_hint.setTextColor(ToolUtils.getColor(this, R.color.CC_D63031));
                // 是否显示实名认证提示文案
                if (ProfileUserUtil.getInstance().isNeedRNA()) {
                    tv_id_verify.setVisibility(View.VISIBLE);
                } else {
                    tv_id_verify.setVisibility(View.GONE);
                }
                // 提交按钮不可点击
                btn_confirm.setEnabled(false);
                tv_hint.setText(tipText);
            } else {
                // 输入金额小于可充值额度
                // 设置默认提示文案字体颜色为灰色
                tv_hint.setTextColor(hintDefaultColor);
                // 隐藏实名认证提示文案
                tv_id_verify.setVisibility(View.GONE);
                // 提交按钮可点击
                btn_confirm.setEnabled(true);
                tv_hint.setText(hintDefaultText);
            }
        } else {
            btn_confirm.setEnabled(false);
            tv_hint.setTextColor(hintDefaultColor);
            tv_hint.setText(hintDefaultText);
            tv_id_verify.setVisibility(View.GONE);
        }
    }

    public void initKyeBoardView() {
        keyboardUtil = new KeyboardUtil(this, et_amount, 1, new KeyboardUtil.CallBack() {
            @Override
            public void onCallback(String result) {
                int amountLength = 0;
                String unFormatString = AmountUtil.unFormatString(result);
                if (!TextUtils.isEmpty(unFormatString)) {
                    amountLength = unFormatString.length();
                }
                if (amountLength <= ConfigCountry.LIMIT_AMOUNT_LENGTH) {
                    text = result;
                    et_amount.setText(result);
                } else {
                    // 输入金额超过8位, 还是显示前8位.
                    et_amount.setText(text);
                }
            }

            @Override
            public void onPwdInputCallback(StringBuffer buffer, String pwdstr) {
            }

            @Override
            public void onPwdStrCallback(StringBuffer stringBuffer) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!isCanClick()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.btn_confirm) {
            onTopup();

        } else if (id == R.id.et_amount) {
            showKeyboard();

        } else if (id == R.id.tv_id_verify) {
            // 实名认证开关已开启并且需要实名认证
            if (ProfileUserUtil.getInstance().isRnaSw()
                    && ProfileUserUtil.getInstance().isNeedRNA()) {
                checkIDStatus();
            } else {
                LogUtil.e(TAG, "------已实名认证------");
            }
        }
    }

    private void showKeyboard() {
        keyboardUtil.showKeyboard();
        view_keyboard.setVisibility(View.VISIBLE);
    }

    /**
     * 充值
     */
    private void onTopup() {
        String amount = et_amount.getText().toString();
        if (!AmountUtil.checkAmount(amount)) {
            showToast(getString(R.string.Toast_G0));
            return;
        }
        amount = AmountUtil.unFormatString(amount);
        LoadingDialog loadingDialog = showLoading();
        RechrCashierSdk.recharge(this, amount, GlobalApplication.getApplication().getSID(), new TopUpCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject, String OT) {
                closeLoading(loadingDialog);
                RechrPayResultRsp response = RechrPayResultRsp.decodeJson(RechrPayResultRsp.class, jsonObject);
                if (TextUtils.equals(code, RechrCashierSdk.LOCAL_PAY_SUCCESS) && response != null
                        && TextUtils.equals(response.getRechargePayResult(), ConstantsPayment.PAY_OK)) {
                    openRechrEnding(OT, "");

                } else if (TextUtils.equals(code, RechrCashierSdk.LOCAL_PAY_QUERY_CANCEL)) {
                    openRechrEnding("", getString(R.string.RechrEnding_I0));

                } else if (TextUtils.equals(RechrCashierSdk.LOCAL_PAY_USER_CANCEL, code)) {
                    LogUtil.e(TAG, errorMsg);

                } else if (TextUtils.equals(code, Constants.RET_CODE_A010)) {
                    showErrorDialog(errorMsg);

                } else {
                    LogUtil.e(TAG, errorMsg);
                    showToast(errorMsg);
                }
            }
        });
    }

    /**
     * 查询实名认证状态
     */
    private void checkIDStatus() {
        LoadingDialog dialog = showLoading();
        AppMain.getInstance().auditQR(this, false, new IDVerifyCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(dialog);
                if (TextUtils.equals(code, IDVerifySdk.LOCAL_SUCCESS)) {
                    ProfileUserUtil.getInstance().setRNA(true);
                    tv_id_verify.setVisibility(View.GONE);
                    queryRechargeQuota();

                } else if (!TextUtils.equals(code, IDVerifySdk.LOCAL_USER_CANCEL) && !TextUtils.isEmpty(errorMsg)) {
                    showToast(errorMsg);

                } else {
                    LogUtil.e(TAG, "查询实名认证状态: " + errorMsg);
                }
            }
        });
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
                    rechargeQuotaStr = response.rechargeAmount;
                    // 实名认证成功之后刷新UI界面
                    refreshUI();

                } else {
                    showToast(errorMsg);
                }
            }
        });
    }

    /**
     * 打开结果页
     *
     * @param OT
     * @param errorMsg
     */
    private void openRechrEnding(String OT, String errorMsg) {
        Intent intent = new Intent(this, EndingRechrActivity.class);
        intent.putExtra(Constants.LOCAL_OT_KEY, OT);
        intent.putExtra(Constants.MSG_KEY, errorMsg);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityManager.getInstance().removeCommonActivity(this);
    }
}
