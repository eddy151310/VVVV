package com.ahdi.wallet.module.payment.withdraw.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ahdi.wallet.module.payment.withdraw.WithDrawSdk;
import com.ahdi.wallet.module.payment.withdraw.schemas.WDBankAccountSchema;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.lib.utils.imagedown.ImageDownUtil;
import com.ahdi.lib.utils.utils.AmountUtil;
import com.ahdi.lib.utils.utils.ToolUtils;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.fee.Fee;
import com.ahdi.lib.utils.fee.FeeFactory;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.widgets.keyboard.KeyboardUtil;
import com.ahdi.wallet.module.payment.withdraw.callback.WithDrawResultCallBack;
import com.ahdi.wallet.module.payment.withdraw.response.WDQueryBankCardInfoResp;

import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author admin
 * @date Created by xiaoniu on 2018/1/10.
 * @email zhao_zhaohe@163.com
 * <p>
 * 提现页面
 */

public class WithdrawActivity extends AppBaseActivity {

    private static final String TAG = WithdrawActivity.class.getSimpleName();

    /**
     * 卡信息
     * icon、卡号、手机号
     */
    private TextView tv_bank_account_name, tv_bank_account_num, tv_amount_text, tv_rp;
    private TextView tv_withdraw_tip, tv_withdraw_all, tv_service_fee, tv_total_deduction;
    private RelativeLayout re_default, re_input;
    private EditText edt_withdraw_amount;
    private View input_amount_area;

    private Button btn_confirm;
    private ImageView iv_edt_delete;

    private WDBankAccountSchema wdBindCardSchema;
    private List<WDBankAccountSchema> bindSchemaList = new ArrayList<>();
    private static final int REQUEST_CODE_SELECT_BANK_ACCOUNT = 1;
    private int position = 0;
    private String chargeFee;//费率公式
    private Fee fee;

    /**
     * 自定义键盘工具类
     */
    private KeyboardUtil keyboardUtil;
    private View view_keyboard;
    private long balance;       // 余额
    private long feeValue;      // 余额对应的手续费
    private int want;           // 提现金额类型:0支付,1到账金额
    private long minValue;      // 最小支付金额
    private long total;         // 总共支付金额
    private ImageView iv_withdraw_icon_bank;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        initIntentData(getIntent());
        setContentView(R.layout.activity_withdraw);
        ActivityManager.getInstance().addCommonActivity(this);
        initView();
        initData();
    }

    private void initIntentData(Intent intent) {
        if (intent != null) {
            bindSchemaList = (List<WDBankAccountSchema>) intent.getSerializableExtra(Constants.LOCAL_BANK_KEY);
            chargeFee = intent.getStringExtra(Constants.LOCAL_FEE_KEY);
        }
    }

    private void initView() {
        initCommonTitle(getString(R.string.WithdrawConfirm_A0));
        findViewById(R.id.card_info_area).setOnClickListener(this);

        // 卡信息区域
        iv_withdraw_icon_bank = findViewById(R.id.iv_withdraw_icon_bank);
        tv_bank_account_name = findViewById(R.id.tv_bank_account_name);
        tv_bank_account_num = findViewById(R.id.tv_bank_account_num);
        findViewById(R.id.main_layout).setOnClickListener(this);        // 点击根布局控制键盘隐藏与显示

        // 输入金额布局
        input_amount_area = findViewById(R.id.input_amount_area);
        tv_amount_text = findViewById(R.id.tv_amount_text);
        tv_rp = findViewById(R.id.tv_rp);
        tv_rp.setText(ConfigCountry.KEY_CURRENCY_SYMBOL);
        edt_withdraw_amount = findViewById(R.id.edt_withdraw_amount);
        iv_edt_delete = findViewById(R.id.iv_edt_delete);

        // 提示文案区域
        re_default = findViewById(R.id.re_default);
        re_input = findViewById(R.id.re_input);

        tv_withdraw_tip = findViewById(R.id.tv_withdraw_tip);
        tv_withdraw_all = findViewById(R.id.tv_withdraw_all);
        tv_service_fee = findViewById(R.id.tv_service_fee);
        tv_total_deduction = findViewById(R.id.tv_total_deduction);

        // 提交按钮
        btn_confirm = findViewById(R.id.btn_confirm);

        // 键盘区域
        view_keyboard = findViewById(R.id.view_keyboard);
    }

    private void initData() {

        iv_edt_delete.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);

        // 设置文案下划线与点击监听
        tv_withdraw_all.setOnClickListener(this);
        tv_withdraw_all.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
        tv_withdraw_all.getPaint().setAntiAlias(true);                  // 抗锯齿

        balance = new BigDecimal(ProfileUserUtil.getInstance().getAccountBalance()).longValue();
        fee = FeeFactory.getInstance().create(chargeFee);
        if (balance > 0) {
            BigDecimal withdrawFee = fee.fee(new BigDecimal(balance), 0);
            feeValue = withdrawFee.longValue();
        }
        minValue = fee.getMinPay().longValue();
        showDefault();
        if (bindSchemaList.size() > 0) {
            refreshUI(bindSchemaList.get(0));
        }
        initKeyBoardView();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initKeyBoardView() {
        //初始化金额键盘
        keyboardUtil = new KeyboardUtil(this, edt_withdraw_amount, 1, new KeyboardUtil.CallBack() {
            @Override
            public void onCallback(String result) {
                edt_withdraw_amount.setText(result);
                if (!TextUtils.isEmpty(edt_withdraw_amount.getText())) {
                    showInput();
                    showEdtDel();
                } else {
                    showDefault();
                    hiddenEdtDel();
                }
            }

            @Override
            public void onPwdInputCallback(StringBuffer buffer, String pwdstr) {

            }

            @Override
            public void onPwdStrCallback(StringBuffer stringBuffer) {

            }
        });
        edt_withdraw_amount.setCursorVisible(true);
        edt_withdraw_amount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        onShowKeyboard();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void refreshUI(WDBankAccountSchema bindSchema) {
        this.wdBindCardSchema = bindSchema;
        ImageDownUtil.downBankIconImage(this, bindSchema.Icon, iv_withdraw_icon_bank);
        tv_bank_account_name.setText(bindSchema.Bank);
        tv_bank_account_num.setText(bindSchema.Account);
    }

    public void showDefault() {
        re_default.setVisibility(View.VISIBLE);
        if (minValue > balance || feeValue == balance) {
            tv_withdraw_tip.setText(Html.fromHtml(String.format(getString(R.string.WithdrawConfirm_H0), ConfigCountry.KEY_CURRENCY_SYMBOL, AmountUtil.formatAmount(String.valueOf(balance)))));
            tv_withdraw_all.setVisibility(View.GONE);

            // 输入金额区域置灰, 不可点击
            edt_withdraw_amount.setEnabled(false);
            input_amount_area.setBackgroundResource(R.color.CC_E6E6E9);
            int disableColor = ToolUtils.getColor(this, R.color.CC_919399);
            tv_amount_text.setTextColor(disableColor);
            tv_rp.setTextColor(disableColor);
            edt_withdraw_amount.setHintTextColor(disableColor);

        } else {
            tv_withdraw_tip.setText(getString(R.string.WithdrawConfirm_D0,
                    ConfigCountry.KEY_CURRENCY_SYMBOL,
                    AmountUtil.formatAmount(String.valueOf(balance))));
        }
        btn_confirm.setEnabled(false);
        re_input.setVisibility(View.GONE);
    }

    public void showInput() {
        re_input.setVisibility(View.VISIBLE);
        re_default.setVisibility(View.GONE);
        BigDecimal withdrawInpFee = fee.pay(new BigDecimal(AmountUtil.unFormatString(edt_withdraw_amount.getText().toString())), 0);
        total = withdrawInpFee.longValue();
        String formatAmount = AmountUtil.formatAmount(String.valueOf(fee.fee(withdrawInpFee, 0).intValue()));
        tv_service_fee.setText(getString(R.string.WithdrawConfirm_F0).concat(ConfigCountry.KEY_CURRENCY_SYMBOL.concat(formatAmount)));
        String formatTotal = AmountUtil.formatAmount(String.valueOf(total));
        if (total > balance) {
            tv_total_deduction.setText(Html.fromHtml(String.format(getString(R.string.WithdrawConfirm_I0), ConfigCountry.KEY_CURRENCY_SYMBOL, formatTotal)));
            btn_confirm.setEnabled(false);
        } else {
            tv_total_deduction.setText(getString(R.string.WithdrawConfirm_G0).concat(ConfigCountry.KEY_CURRENCY_SYMBOL.concat(formatTotal)));
            btn_confirm.setEnabled(true);
        }
    }

    /**
     * 提现
     */
    public void onWithdraw() {
        String amount = edt_withdraw_amount.getText().toString();
        if (!AmountUtil.checkAmount(amount)) {
            return;
        }
        String target = wdBindCardSchema.getToken();
        if (total == balance) {
            want = 0;
            amount = AmountUtil.formatAmount(String.valueOf(balance));
        } else {
            want = 1;
        }
        LoadingDialog loadingDialog = showLoading();
        AppMain.getInstance().onWithdraw(this, target, want, AmountUtil.unFormatString(amount), "", new WithDrawResultCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject, String ID) {
                closeLoading(loadingDialog);
                onWithdrawCallback(code, errorMsg, ID);
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
            // 发起提现
            onWithdraw();

        } else if (id == R.id.main_layout) {
            // 隐藏自定义键盘
            onHiddenKeyboard();

        } else if (id == R.id.et_receive_amount) {
            // 显示自定义键盘
            onShowKeyboard();

        } else if (id == R.id.iv_edt_delete) {
            edt_withdraw_amount.setText("");
            hiddenEdtDel();
            showDefault();

        } else if (id == R.id.card_info_area) {
            // 打开选择银行账户界面
            openSelectBankAccount();

        } else if (id == R.id.tv_withdraw_all) {
            // 提现全部
            showEdtDel();
            long value = balance - feeValue;
            edt_withdraw_amount.setText(AmountUtil.formatAmount(String.valueOf(value)));
            showInput();
        }
    }

    private void onWithdrawCallback(String code, String errorMsg, String ID) {

        if (WithDrawSdk.LOCAL_PAY_SUCCESS.equals(code) && !TextUtils.isEmpty(ID)) {
            showResultActivity(ID, "");

        } else if (TextUtils.equals(code, WithDrawSdk.LOCAL_PAY_QUERY_CANCEL)) {
            showResultActivity("", getString(R.string.WithdrawEnding_L0));

        } else if (WithDrawSdk.LOCAL_PAY_USER_CANCEL.equals(code)) {
            LogUtil.e(TAG, errorMsg);

        } else if (TextUtils.equals(code, Constants.RET_CODE_A010)) {
            showErrorDialog(errorMsg);

        } else {
            LogUtil.e(TAG, errorMsg);
            showToast(errorMsg);
        }
    }

    /**
     * 跳到展示结果页
     */
    private void showResultActivity(String ID, String errorMsg) {
        Intent intent = new Intent(this, EndingWithdrawActivity.class);
        intent.putExtra(EndingWithdrawActivity.CARD_NUM, wdBindCardSchema.Bank + wdBindCardSchema.getAccount());
        intent.putExtra(Constants.LOCAL_ID_KEY, ID);
        intent.putExtra(Constants.MSG_KEY, errorMsg);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_BANK_ACCOUNT) {
            if (resultCode == SelectWDAccountActivity.RESULT_CODE_SELECT_BANK_ACCOUNT) {
                if (data != null) {
                    position = data.getIntExtra(Constants.LOCAL_POSITION_KEY, 0);
                    refreshUI(bindSchemaList.get(position));
                }
            } else if (resultCode == SelectWDAccountActivity.RESULT_CODE_BANK_ACCOUNT_ADD_SUCCESS) {
                queryWDBindAccountInfo();
            }
        }
    }

    public void queryWDBindAccountInfo() {
        LoadingDialog loadingDialog = showLoading();
        AppMain.getInstance().queryWDBindAccountInfo(this, new WithDrawResultCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject, String ID) {
                closeLoading(loadingDialog);
                onBindCardCallback(code, errorMsg, jsonObject);
            }
        });
    }

    /**
     * 绑卡信息接口回调
     *
     * @param code
     * @param errorMsg
     * @param jsonObject
     */
    private void onBindCardCallback(String code, String errorMsg, JSONObject jsonObject) {
        WDQueryBankCardInfoResp resp = WDQueryBankCardInfoResp.decodeJson(WDQueryBankCardInfoResp.class, jsonObject);
        if (TextUtils.equals(code, WithDrawSdk.LOCAL_PAY_SUCCESS) && resp != null) {
            WDBankAccountSchema[] bindSchemas = resp.getBAccounts();
            GlobalApplication.getApplication().updateAccountBalance(resp.getBalance());
            if (bindSchemas != null && bindSchemas.length > 0) {
                bindSchemaList.clear();
                bindSchemaList = Arrays.asList(bindSchemas);
                refreshUI(bindSchemaList.get(0));
                position = 0;
            }
        } else {
            showToast(errorMsg);
            finish();
        }
    }

    private void onShowKeyboard() {
        closeSoftInput();
        keyboardUtil.showKeyboard();
        view_keyboard.setVisibility(View.VISIBLE);
    }

    private void onHiddenKeyboard() {
        keyboardUtil.hideKeyboard();
        view_keyboard.setVisibility(View.GONE);
    }

    private void openSelectBankAccount() {
        Intent intent = new Intent(this, SelectWDAccountActivity.class);
        intent.putExtra(Constants.LOCAL_BANK_KEY, (Serializable) bindSchemaList);
        intent.putExtra(Constants.LOCAL_POSITION_KEY, position);
        startActivityForResult(intent, REQUEST_CODE_SELECT_BANK_ACCOUNT);
    }

    private void hiddenEdtDel() {
        iv_edt_delete.setVisibility(View.GONE);
    }

    private void showEdtDel() {
        iv_edt_delete.setVisibility(View.VISIBLE);
    }
}
