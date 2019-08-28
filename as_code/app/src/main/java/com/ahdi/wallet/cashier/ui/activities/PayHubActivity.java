package com.ahdi.wallet.cashier.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ahdi.wallet.R;
import com.ahdi.wallet.cashier.PayCashierSdk;
import com.ahdi.wallet.cashier.RechrCashierSdk;
import com.ahdi.wallet.cashier.main.RechrCashierMain;
import com.ahdi.wallet.cashier.bean.PayOrderBean;
import com.ahdi.wallet.cashier.callback.PaymentSdkCallBack;
import com.ahdi.wallet.cashier.callback.RechrCallBack;
import com.ahdi.wallet.app.main.CashierPricing;
import com.ahdi.wallet.cashier.main.PayCashierMain;
import com.ahdi.wallet.cashier.response.pay.PayOrderResponse;
import com.ahdi.wallet.cashier.response.pay.PayResultQueryResponse;
import com.ahdi.wallet.cashier.response.rechr.RechrOrderRsp;
import com.ahdi.wallet.cashier.response.rechr.RechrPayResultRsp;
import com.ahdi.wallet.cashier.schemas.MarketPayTypeSchema;
import com.ahdi.wallet.cashier.schemas.PayAuthSchema;
import com.ahdi.wallet.cashier.schemas.PayTypeSchema;
import com.ahdi.wallet.app.schemas.TerminalInfoSchema;
import com.ahdi.wallet.cashier.schemas.TransSchema;
import com.ahdi.wallet.app.utils.ConstantsPayment;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.cryptor.rsa.RSAHelper;
import com.ahdi.lib.utils.fingerprint.FingerprintSDK;
import com.ahdi.lib.utils.fingerprint.callback.FingerprintPaymentCallback;
import com.ahdi.lib.utils.utils.AmountUtil;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.DeviceUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.StringUtil;
import com.ahdi.lib.utils.utils.TouchIDStateUtil;
import com.ahdi.lib.utils.widgets.dialog.CommonDialog;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 1、PayHubActivity 不采用SetAmountActivity 界面主题
 * 依然使用PayHubTheme 主题
 * 原因： 选择支付方式界面 --- 返回   ---  收银台会闪一下
 * 2、 类似收银台这样的半弹层不使用statusBar
 * <p>
 * 此界面的背景色不是半透明, 是由于后台显示loading的原因
 */
public class PayHubActivity extends PayBaseActivity {

    public static final String TAG = PayHubActivity.class.getSimpleName();

    public static final int REQUEST_CODE_OPEN_PAY_MODE = 1;
    public static final int REQUEST_CODE_OPEN_VERIFY_PIN = 2;
    public static final int RESULT_CODE_CLOSE_VERIFY_PIN_5012 = 3;

    public static final String PAY_TYPE_IS_NULL = "pay_type_is_null";

    private int from;
    private String payTypeTotalAmount;
    private String GName;
    private String price;
    private String withdrawFee;
    private String serviceFee;
    private int mPosition = 0;
    private String msg;
    private int screenHeight;
    private boolean isInitPayView;

    private LinearLayout ll_transfer, ll_withdraw, ll_topup;
    private RelativeLayout rl_transfer_type, rl_transfer_price, rl_transfer_bank_fee, rl_transfer_voucher,
            rl_withdraw_type, rl_withdraw_amount, rl_withdraw_fee,
            rl_topup_price, rl_topup_bank_fee;

    private TextView tv_transfer_symbol, tv_transfer_total, tv_transfer_type, tv_transfer_price,
            tv_transfer_bank_fee, tv_transfer_voucher, tv_pay_method,
            tv_withdraw_symbol, tv_withdraw_total, tv_withdraw_type, tv_withdraw_amount, tv_withdraw_fee,
            tv_topup_symbol, tv_topup_total, tv_topup_type, tv_topup_price, tv_topup_bank_fee, tv_topup_method;

    private String TT; // 初次批价返回的TT, 用于重新批价
    private int touchFlag;
    private String iv;
    private String marketMoney;
    private String symbol = ConfigCountry.KEY_CURRENCY_SYMBOL;
    private String withdrawTransPrice;
    private View rl_pay_mode;
    private LoadingDialog loadingDialog;
    private Context mContext;
    private String rechrAmount;             // 充值金额
    private String rechrEdtAmount;          // 客户端输入的充值金额, 用于绑卡成功后的重新查询充值方式
    private String rechrSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        initIntentData(getIntent());

        mContext = this;

        setContentView(R.layout.dialog_activity_pay_hub);

        initView();

        initData();

        ActivityManager.getInstance().addPayHubActivity(this);
    }

    private void initIntentData(Intent intent) {
        if (intent == null) {
            return;
        }
        from = intent.getIntExtra(Constants.LOCAL_FROM_KEY, 0);
        touchFlag = intent.getIntExtra("TouchFlag", -100);

        if (from == Constants.LOCAL_FROM_TOP_UP) {
            rechrSubject = intent.getStringExtra(Constants.LOCAL_SUBJECT_KEY);
            rechrEdtAmount = intent.getStringExtra("rechrEdtAmountKey");
            rechrAmount = intent.getStringExtra("rechrAmountKey");

        } else {
            GName = intent.getStringExtra(Constants.LOCAL_GNAME_KEY);
            price = intent.getStringExtra(Constants.LOCAL_PRICE_KEY);
        }

        if (from == Constants.LOCAL_FROM_WITHDRAW) {
            withdrawFee = intent.getStringExtra(Constants.LOCAL_FEE_KEY);
            withdrawTransPrice = intent.getStringExtra("withdrawTransPrice");
        }

        // 优惠券展示数据
        ArrayList<MarketPayTypeSchema> marketPayTypeSchemaList = CashierPricing.getInstance(mContext).getMarketPayTypeSchemaList();
        if (marketPayTypeSchemaList != null && marketPayTypeSchemaList.size() > 0) {
            MarketPayTypeSchema marketPayTypeSchema = marketPayTypeSchemaList.get(0);
            marketMoney = marketPayTypeSchema.Money;
        }

        ArrayList<PayTypeSchema> payTypesSchemaList = CashierPricing.getInstance(mContext).getPayTypesSchemaList();
        if (payTypesSchemaList == null || payTypesSchemaList.size() <= 0) {
            return;
        }
        PayTypeSchema payTypeSchema = payTypesSchemaList.get(0);
        if (payTypeSchema == null) {
            return;
        }
        if (from == Constants.LOCAL_FROM_PAY || from == Constants.LOCAL_FROM_TOP_UP) {
            serviceFee = payTypeSchema.Fee;
            payTypeTotalAmount = payTypeSchema.TotalMoney;
        }
    }

    public void initTitleView() {
        TextView tv_title = findViewById(R.id.tv_title);
        if (from == Constants.LOCAL_FROM_WITHDRAW) {
            tv_title.setText(getString(R.string.SDKWithdrawCashier_A0));

        } else if (from == Constants.LOCAL_FROM_PAY) {
            tv_title.setText(getString(R.string.SDKPayCashier_A0));

        } else if (from == Constants.LOCAL_FROM_TOP_UP) {
            tv_title.setText(getString(R.string.SDKRechrCashier_A0));
        }

        View backView = findViewById(R.id.btn_back);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCanClick()) {
                    return;
                }
                onBack();
            }
        });
    }

    private void initView() {
        // 自定义设置屏幕高度
        initRootViewHeight();

        initTitleView();

        ll_transfer = findViewById(R.id.ll_transfer);
        ll_withdraw = findViewById(R.id.ll_withdraw);
        ll_topup = findViewById(R.id.ll_topup);

        if (from == Constants.LOCAL_FROM_WITHDRAW) {
            ll_transfer.setVisibility(View.GONE);
            ll_topup.setVisibility(View.GONE);
            ll_withdraw.setVisibility(View.VISIBLE);
            // 提现
            initWithdrawView();

        } else if (from == Constants.LOCAL_FROM_PAY) {
            ll_topup.setVisibility(View.GONE);
            ll_withdraw.setVisibility(View.GONE);
            ll_transfer.setVisibility(View.VISIBLE);
            // 转账
            initTransferView();

        } else if (from == Constants.LOCAL_FROM_TOP_UP) {
            ll_transfer.setVisibility(View.GONE);
            ll_withdraw.setVisibility(View.GONE);
            ll_topup.setVisibility(View.VISIBLE);
            // 充值
            initTopupView();
        }
        // 初始化底部提交按钮
        initSubmitBtn();
    }

    private void initTransferView() {

        // 金额展示: 货币符号+金额
        tv_transfer_symbol = findViewById(R.id.tv_transfer_symbol);
        tv_transfer_total = findViewById(R.id.tv_transfer_total);

        // Type
        rl_transfer_type = findViewById(R.id.rl_transfer_type);
        tv_transfer_type = findViewById(R.id.tv_transfer_type);

        // Price
        rl_transfer_price = findViewById(R.id.rl_transfer_price);
        tv_transfer_price = findViewById(R.id.tv_transfer_price);

        // Bank Fee
        rl_transfer_bank_fee = findViewById(R.id.rl_transfer_bank_fee);
        tv_transfer_bank_fee = findViewById(R.id.tv_transfer_bank_fee);

        // Voucher
        rl_transfer_voucher = findViewById(R.id.rl_transfer_voucher);
        tv_transfer_voucher = findViewById(R.id.tv_transfer_voucher);

        // 选择支付方式
        rl_pay_mode = findViewById(R.id.rl_pay_mode);
        rl_pay_mode.setOnClickListener(this);
        tv_pay_method = findViewById(R.id.tv_pay_method);
    }

    private void initWithdrawView() {

        tv_withdraw_symbol = findViewById(R.id.tv_withdraw_symbol);
        tv_withdraw_total = findViewById(R.id.tv_withdraw_total);

        // Type
        rl_withdraw_type = findViewById(R.id.rl_withdraw_type);
        tv_withdraw_type = findViewById(R.id.tv_withdraw_type);

        // Withdraw amount
        rl_withdraw_amount = findViewById(R.id.rl_withdraw_amount);
        tv_withdraw_amount = findViewById(R.id.tv_withdraw_amount);

        // Withdraw withdrawFee
        rl_withdraw_fee = findViewById(R.id.rl_withdraw_fee);
        tv_withdraw_fee = findViewById(R.id.tv_withdraw_fee);
    }

    private void initTopupView() {
        tv_topup_symbol = findViewById(R.id.tv_topup_symbol);
        tv_topup_total = findViewById(R.id.tv_topup_total);

        tv_topup_type = findViewById(R.id.tv_topup_type);

        rl_topup_price = findViewById(R.id.rl_topup_price);
        tv_topup_price = findViewById(R.id.tv_topup_price);

        rl_topup_bank_fee = findViewById(R.id.rl_topup_bank_fee);
        tv_topup_bank_fee = findViewById(R.id.tv_topup_bank_fee);

        findViewById(R.id.rl_topup_mode).setOnClickListener(this);
        tv_topup_method = findViewById(R.id.tv_topup_method);
    }

    private void initSubmitBtn() {
        Button btn_submit = findViewById(R.id.btn_submit);
        if (from == Constants.LOCAL_FROM_WITHDRAW) {
            btn_submit.setText(getString(R.string.SDKWithdrawCashier_E0));

        } else if (from == Constants.LOCAL_FROM_PAY) {
            btn_submit.setText(getString(R.string.SDKPayCashier_F0));

        } else if (from == Constants.LOCAL_FROM_TOP_UP) {
            btn_submit.setText(getString(R.string.SDKRechrCashier_F0));
        }
        btn_submit.setOnClickListener(this);
    }

    private void initData() {
        TransSchema schema = CashierPricing.getInstance(mContext).getTransSchema();
        if (schema != null) {
            TT = schema.TT;
        }
        mPosition = 0;
        getRootViewHeight();
        // 更新转账或者提现, 展示的订单信息
        updateOrderInfo();
    }

    private void updateOrderInfo() {
        if (from == Constants.LOCAL_FROM_WITHDRAW) {
            showWithdrawInfo();

        } else if (from == Constants.LOCAL_FROM_PAY) {
            showTransferInfo();

        } else if (from == Constants.LOCAL_FROM_TOP_UP) {
            showTopupInfo();
        }
    }

    private void initRootViewHeight() {
        int height = DeviceUtil.getScreenHeight(this);
        RelativeLayout bg_view = findViewById(R.id.content);
        ViewGroup.LayoutParams layoutParams = bg_view.getLayoutParams();
        layoutParams.height = ((int) (height * Constants.LOCAL_DIALOG_HEIGHT_SCALE));
        bg_view.setLayoutParams(layoutParams);
        bg_view.setBackgroundResource(R.drawable.bg_dialog_payhub_title);
    }

    private void getRootViewHeight() {
        RelativeLayout rootView = findViewById(R.id.content);
        ViewTreeObserver viewTreeObserver = rootView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                removeViewTreeObserver(viewTreeObserver, this);
                screenHeight = rootView.getHeight();
                if (!isInitPayView) {
                    isInitPayView = true;
                    initPayView();
                }
            }
        });
    }

    private void showTransferInfo() {
        ll_transfer.setVisibility(View.VISIBLE);
        ll_withdraw.setVisibility(View.GONE);
        ll_topup.setVisibility(View.GONE);

        tv_transfer_symbol.setText(symbol);
        tv_transfer_total.setText(AmountUtil.formatAmount(payTypeTotalAmount));

        // 有商品名称则展示 否则不展示
        if (!TextUtils.isEmpty(GName)) {
            rl_transfer_type.setVisibility(View.VISIBLE);
            tv_transfer_type.setVisibility(View.VISIBLE);
            tv_transfer_type.setText(GName);
        } else {
            rl_transfer_type.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(serviceFee) && TextUtils.isEmpty(marketMoney)) {
            // 无费率 无优惠券, 隐藏交易金额 费率 优惠券
            rl_transfer_price.setVisibility(View.GONE);
            rl_transfer_bank_fee.setVisibility(View.GONE);
            rl_transfer_voucher.setVisibility(View.GONE);

        } else {
            // 有商品价格则展示 否则不展示
            if (!TextUtils.isEmpty(price)) {
                rl_transfer_price.setVisibility(View.VISIBLE);
                tv_transfer_price.setVisibility(View.VISIBLE);
                tv_transfer_price.setText(AmountUtil.getFormatAmount(symbol, price));
            } else {
                rl_transfer_price.setVisibility(View.GONE);
            }
            // 有费率则展示 否则不展示
            if (!TextUtils.isEmpty(serviceFee)) {
                rl_transfer_bank_fee.setVisibility(View.VISIBLE);
                tv_transfer_bank_fee.setVisibility(View.VISIBLE);
                tv_transfer_bank_fee.setText(AmountUtil.getFormatAmount(symbol, serviceFee));
            } else {
                rl_transfer_bank_fee.setVisibility(View.GONE);
            }
            // 有优惠券则展示 否则不展示
            if (!TextUtils.isEmpty(marketMoney)) {
                rl_transfer_voucher.setVisibility(View.VISIBLE);
                tv_transfer_voucher.setVisibility(View.VISIBLE);
                String amt = AmountUtil.getFormatAmount(symbol, marketMoney, Constants.KEY_MINUS);
                tv_transfer_voucher.setText(amt);
            } else {
                rl_transfer_voucher.setVisibility(View.GONE);
            }
        }
    }

    private void showWithdrawInfo() {
        ll_topup.setVisibility(View.GONE);
        ll_transfer.setVisibility(View.GONE);
        ll_withdraw.setVisibility(View.VISIBLE);

        tv_withdraw_symbol.setText(symbol);
        // 提现时, 交易总金额
        tv_withdraw_total.setText(AmountUtil.formatAmount(price));

        if (!TextUtils.isEmpty(GName)) {
            rl_withdraw_type.setVisibility(View.VISIBLE);
            tv_withdraw_type.setVisibility(View.VISIBLE);
            tv_withdraw_type.setText(GName);
        } else {
            rl_withdraw_type.setVisibility(View.GONE);

        }
        if (TextUtils.isEmpty(withdrawFee)) {
            // 无费率时, 隐藏提现金额 费率
            rl_withdraw_amount.setVisibility(View.GONE);
            rl_withdraw_fee.setVisibility(View.GONE);
        } else {
            rl_withdraw_amount.setVisibility(View.VISIBLE);
            rl_withdraw_fee.setVisibility(View.VISIBLE);
            // 提现时, 到账金额
            tv_withdraw_amount.setText(AmountUtil.getFormatAmount(symbol, withdrawTransPrice));
            tv_withdraw_fee.setText(AmountUtil.getFormatAmount(symbol, withdrawFee));
        }
    }

    private void showTopupInfo() {
        ll_transfer.setVisibility(View.GONE);
        ll_withdraw.setVisibility(View.GONE);
        ll_topup.setVisibility(View.VISIBLE);

        tv_topup_symbol.setText(symbol);
        tv_topup_total.setText(AmountUtil.formatAmount(payTypeTotalAmount));

        tv_topup_type.setText(rechrSubject);
        if (TextUtils.isEmpty(serviceFee)) {
            rl_topup_bank_fee.setVisibility(View.GONE);
            rl_topup_price.setVisibility(View.GONE);
        } else {
            rl_topup_bank_fee.setVisibility(View.VISIBLE);
            rl_topup_price.setVisibility(View.VISIBLE);
            tv_topup_price.setText(AmountUtil.getFormatAmount(symbol, rechrAmount));
            tv_topup_bank_fee.setText(AmountUtil.getFormatAmount(symbol, serviceFee));
        }
    }

    private void initPayView() {
        if (from == Constants.LOCAL_FROM_PAY || from == Constants.LOCAL_FROM_TOP_UP) {
            ArrayList<MarketPayTypeSchema> marketPayTypeSchemaList = CashierPricing.getInstance(mContext).getMarketPayTypeSchemaList();
            ArrayList<PayTypeSchema> payTypesSchemaList = CashierPricing.getInstance(mContext).getPayTypesSchemaList();

            if (marketPayTypeSchemaList == null || marketPayTypeSchemaList.size() <= 0) {
                checkPayTypeList(payTypesSchemaList);

            } else {
                MarketPayTypeSchema marketPayTypeSchema = marketPayTypeSchemaList.get(0);
                if (marketPayTypeSchema != null && TextUtils.equals(marketPayTypeSchema.TotalMoney, "0")) {
                    // 停留在收银台 并且 隐藏 支付方式 条目
                    rl_pay_mode.setVisibility(View.GONE);
                    tv_transfer_symbol.setText(symbol);
                    tv_transfer_total.setText(AmountUtil.formatAmount(marketPayTypeSchema.TotalMoney));

                } else {
                    checkPayTypeList(payTypesSchemaList);
                }
            }
        }
    }

    private void checkPayTypeList(ArrayList<PayTypeSchema> payTypesSchemaList) {
        if (payTypesSchemaList != null && payTypesSchemaList.size() > 0) {
            PayTypeSchema payTypeSchema = payTypesSchemaList.get(0);
            // 判断第一个block是否为空, 如果第一个block不为空, 那么后面的支付方式也都是不可用的.
            if (payTypeSchema != null && TextUtils.isEmpty(payTypeSchema.Block)) {
                String payMethodName = payTypeSchema.Name;
                updatePayMethodText(payMethodName);
            } else {
                LogUtil.e(TAG, "payTypesSchemaList size <= 0");
                // 批价: 无可用支付方式
                showSelectPayMode(PAY_TYPE_IS_NULL);
            }
        } else {
            LogUtil.e(TAG, "payTypesSchemaList size <= 0");
            showSelectPayMode(PAY_TYPE_IS_NULL);
        }
    }

    private void updatePayMethodText(String payMethodName) {
        if (from == Constants.LOCAL_FROM_TOP_UP) {
            tv_topup_method.setText(payMethodName);

        } else if (from == Constants.LOCAL_FROM_PAY) {
            tv_pay_method.setText(payMethodName);
        }
    }

    /**
     * 支付下单: 5012
     */
    private void showSelectPayMode() {
        showSelectPayMode("");
    }

    /**
     * 无可用支付方式, 打开支付方式列表界面, 引导绑卡操作
     *
     * @param type
     */
    private void showSelectPayMode(String type) {
        if (from == Constants.LOCAL_FROM_PAY || from == Constants.LOCAL_FROM_TOP_UP) {
            Intent intent = new Intent(mContext, PayModeSelectActivity.class);
            if (TextUtils.isEmpty(type)) {
                intent.putExtra(Constants.LOCAL_ID_KEY, 0);
            } else {
                intent.putExtra(Constants.LOCAL_ID_KEY, mPosition);
            }
            if (!TextUtils.isEmpty(msg)) {
                intent.putExtra(Constants.MSG_KEY, msg);
            }
            intent.putExtra(Constants.LOCAL_HEIGHT_KEY, screenHeight);
            intent.putExtra(Constants.LOCAL_TT_KEY, TT);
            intent.putExtra(Constants.LOCAL_FROM_KEY, from);
            intent.putExtra("rechrEdtAmountKey", rechrAmount);

            startActivityForResult(intent, REQUEST_CODE_OPEN_PAY_MODE);
            if (type.equals(PAY_TYPE_IS_NULL)) {
                onBottom_in_Activity();
            } else {
                onRight_in_Activity();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (!isCanClick()) {
            return;
        }
        int id = view.getId();
        if (id == R.id.rl_pay_mode || id == R.id.rl_topup_mode) {
            // 打开选择支付方式列表
            openSelectPayMode();

        } else if (id == R.id.btn_submit) {
            onSubmit();
        }
    }

    private void openPwdPIN(String errMsg) {
        Intent intent = new Intent(mContext, PayVerifyPINActivity.class);
        intent.putExtra(Constants.LOCAL_HEIGHT_KEY, screenHeight);
        intent.putExtra(Constants.LOCAL_POSITION_KEY, mPosition);
        intent.putExtra(Constants.LOCAL_FROM_KEY, from);
        intent.putExtra(Constants.MSG_KEY, errMsg);
        intent.putExtra("ivKey", iv);
        intent.putExtra("rechrAmountKey", rechrAmount);
        startActivityForResult(intent, REQUEST_CODE_OPEN_VERIFY_PIN);
        onRight_in_Activity();
    }

    /**
     * 手动点击打开支付方式列表界面
     */
    private void openSelectPayMode() {
        Intent intent = new Intent(mContext, PayModeSelectActivity.class);
        intent.putExtra(Constants.LOCAL_ID_KEY, mPosition);
        if (!TextUtils.isEmpty(msg)) {
            intent.putExtra(Constants.MSG_KEY, msg);
        }
        intent.putExtra(Constants.LOCAL_HEIGHT_KEY, screenHeight);
        intent.putExtra(Constants.LOCAL_TT_KEY, TT);
        intent.putExtra(Constants.LOCAL_FROM_KEY, from);
        intent.putExtra("rechrEdtAmountKey", rechrEdtAmount);

        startActivityForResult(intent, REQUEST_CODE_OPEN_PAY_MODE);
        onRight_in_Activity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_PAY_MODE
                && resultCode == PayModeSelectActivity.RESULT_CODE_CLOSE_PAY_MODE) {
            // 选择支付方式列表界面, 点击返回按钮或者选中支付方式时, 收银台首页更新支付方式
            refreshUIData(data);

        } else if (requestCode == REQUEST_CODE_OPEN_VERIFY_PIN
                && resultCode == RESULT_CODE_CLOSE_VERIFY_PIN_5012 && data != null) {
            // PIN界面 支付下单返回5012时, 打开选择支付方式列表界面, 展示相关错误信息
            onOrder5012(data);
        }
    }

    /**
     * 支付下单时, 错误码5012的处理
     *
     * @param data
     */
    private void onOrder5012(Intent data) {
        PayTypeSchema schema = getPayTypeSchema(mPosition);
        if (schema != null) {
            schema.Block = getString(R.string.SDKPayTypeList_C0); // 支付下单时  5012 本地赋值 不可用的描述
            CashierPricing.getInstance(mContext).getPayTypesSchemaList().add(schema);
        }
        CashierPricing.getInstance(mContext).getPayTypesSchemaList().remove(mPosition);
        CashierPricing.getInstance(mContext).initPayTypesList();

        if (from == Constants.LOCAL_FROM_PAY) {
            tv_pay_method.setText(getPayTypeName(0));

        } else if (from == Constants.LOCAL_FROM_TOP_UP) {
            tv_topup_method.setText(getPayTypeName(0));
        }
        if (data != null) {
            msg = data.getStringExtra(Constants.MSG_KEY);
        }
        // 打开选择支付方式列表界面
        showSelectPayMode();
        msg = "";
    }

    private PayTypeSchema getPayTypeSchema(int position) {
        if (position < 0) {
            return null;
        }
        ArrayList<PayTypeSchema> list = CashierPricing.getInstance(mContext).getPayTypesSchemaList();
        if (list.size() > 0) {
            return list.get(position);
        }
        return null;
    }

    private String getPayTypeName(int position) {
        String payTypeName = "";
        PayTypeSchema payTypeSchema = getPayTypeSchema(position);
        if (payTypeSchema != null) {
            payTypeName = payTypeSchema.Name;
        }
        return payTypeName;
    }

    public String getTotalAmount(int position) {
        String totalMoney = "";
        PayTypeSchema payTypeSchema = getPayTypeSchema(position);
        if (payTypeSchema != null) {
            totalMoney = payTypeSchema.TotalMoney;
        }
        return totalMoney;
    }

    public String getFee(int position) {
        String fee = "";
        PayTypeSchema payTypeSchema = getPayTypeSchema(position);
        if (payTypeSchema != null) {
            fee = payTypeSchema.Fee;
        }
        return fee;
    }

    /**
     * 由于不同支付方式的费率不同, 所以更新支付方式界面返回到收银台时, 需要更新总金额和费率, 以及对应的typeName
     *
     * @param data
     */
    private void refreshUIData(Intent data) {
        if (data != null) {
            mPosition = data.getIntExtra(Constants.LOCAL_POSITION_KEY, 0);
            int tf = data.getIntExtra("TouchFlag", -101);
            if (tf == Constants.TOUCH_FLAG_NOT_OPEN
                    || tf == Constants.TOUCH_FLAG_NOT_CHANGE
                    || tf == Constants.TOUCH_FLAG_CHANGE) {
                touchFlag = tf;
            }
            payTypeTotalAmount = getTotalAmount(mPosition);
            serviceFee = getFee(mPosition);

            updateOrderInfo();

            String payTypeName = getPayTypeName(mPosition);
            if (from == Constants.LOCAL_FROM_PAY) {
                tv_pay_method.setText(payTypeName);

            } else if (from == Constants.LOCAL_FROM_TOP_UP) {
                tv_topup_method.setText(payTypeName);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
            default:
                return false;
        }
    }

    /**
     * 主动退出收银台
     */
    private void onBack() {
        if (from == Constants.LOCAL_FROM_PAY || from == Constants.LOCAL_FROM_WITHDRAW) {
            PayCashierMain.getInstance().onResultBack(mContext, PayCashierSdk.LOCAL_PAY_USER_CANCEL,
                    PayCashierSdk.USER_CANCEL, null, ConstantsPayment.PayResult_OTHER);

        } else if (from == Constants.LOCAL_FROM_TOP_UP) {
            RechrCashierMain.getInstance().onResultBack(RechrCashierSdk.LOCAL_PAY_USER_CANCEL,
                    RechrCashierSdk.USER_CANCEL, null, "", ConstantsPayment.PayResult_OTHER, "");
        }
        onClose();
    }

    /**
     * 关闭收银台页面
     */
    private void onClose() {
        finish();
        onBottom_out_Activity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getInstance().removePayHubActivity(this);
    }

    private void onSubmit() {
        String lName = AppGlobalUtil.getInstance().getLName(mContext);
        // 支付凭证版本号没变化
        if (touchFlag == Constants.TOUCH_FLAG_NOT_CHANGE
                // 设备支持指纹
                && FingerprintSDK.isSupport(mContext)
                // 设备已开启指纹支付
                && TouchIDStateUtil.isStartTouchIDPayment(mContext, lName)
                // 设备存在至少一个指纹
                && FingerprintSDK.isHasFingerprints(mContext)) {
            // 新增指纹时, 验证通过并且支付下单成功, 才会更新指纹sdk的iv, 否则为init_iv_error
            String iv = FingerprintSDK.getIV(mContext);
            if (TextUtils.equals(iv, FingerprintSDK.INIT_IV_ERROR)) {
                LogUtil.e(TAG, "----------iv 不匹配------------本地重新开启指纹支付-------");
                // 本地重新开启指纹支付, 不调用指纹支付开启的服务端接口. 同新增指纹流程
                onStartTouchIDPay(lName, FingerprintSDK.FINGERPRINT_PAY_RE_START);
            } else {
                onStartTouchIDPay(lName, FingerprintSDK.FINGERPRINT_PAY_VERIFY);
            }
        } else {
            // 打开验证密码界面
            openPwdPIN("");
        }
    }

    private void onStartTouchIDPay(String lName, int verifyType) {

        FingerprintSDK.startAuthenticatePayment(this, lName, verifyType,
                new FingerprintPaymentCallback() {
                    @Override
                    public void onSuccess(String iv, String publicKey, String privateKey) {
                        PayHubActivity.this.iv = iv;
                        LogUtil.e(TAG, "---收银台指纹验证成功---iv: " + iv);
                        try {
                            String touchKeyCipher = TouchIDStateUtil.getTouchIDPayKey(mContext, lName);
                            String touchKey = RSAHelper.decryptByPrivateKey(touchKeyCipher, privateKey);
                            // 支付下单
                            onOrder(touchKey, privateKey);
                        } catch (Exception e) {
                            openPwdPIN("");
                            LogUtil.e(TAG, "touchKey解密失败: " + e.toString());
                        }
                    }

                    @Override
                    public void onFailed(int code, String errMsg) {
                        LogUtil.e(TAG, "指纹验证errCode: " + code);
                        if (code == FingerprintSDK.CODE_0) {
                            // 连续验证失败超过3次, 切换到支付密码验证
                            openPwdPIN(getString(R.string.SDKCashierEnterPIN_B0));

                        } else if (code == FingerprintSDK.CODE_1) {
                            // 点击取消按钮，页面弹窗提示用户是否要退出"Are you sure to quit?"
                            showDialogQuit();

                        } else if (code == FingerprintSDK.CODE_5) {
                            // 系统锁定, 失败5次
                            openPwdPIN(getString(R.string.SDKCashierEnterPIN_B0));

                        } else if (code == FingerprintSDK.CODE_7) {
                            // 点击PIN, 主动打开验证密码界面
                            openPwdPIN("");

                        } else if (code == FingerprintSDK.CODE_8) {
                            // 新增指纹, 有变化, 重新验证指纹, 验证成功输入支付密码, 支付下单成功后, 更新指纹支付token
                            FingerprintSDK.cancelFingerprintRecognition();
                            onStartTouchIDPay(lName, FingerprintSDK.FINGERPRINT_PAY_RE_START);

                        } else if (code == FingerprintSDK.CODE_10) {
                            // 前两次验证失败，点击取消直接关闭验证收银台
                            onBack();
                        }
                    }
                });
    }

    private void showDialogQuit() {
        new CommonDialog.
                Builder(mContext)
                .setCancelable(false)
                .setMessage(getString(R.string.DialogMsg_H0))
                .setMessageCenter(true)
                .setNegativeButton(getString(R.string.DialogButton_E0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onBack();
                    }
                })
                .setPositiveButton(getString(R.string.DialogButton_J0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openPwdPIN("");
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void onOrder(String touchKey, String privateKey) {
        if (loadingDialog == null) {
            loadingDialog = showLoading();
        }
        if (from == Constants.LOCAL_FROM_PAY || from == Constants.LOCAL_FROM_WITHDRAW) {
            onPayOrder(touchKey, privateKey);

        } else if (from == Constants.LOCAL_FROM_TOP_UP) {
            onTopupOrder(touchKey, privateKey);
        }
    }

    /**
     * 支付下单
     *
     * @param touchKey
     * @param privateKey
     */
    private void onPayOrder(String touchKey, String privateKey) {
        PayOrderBean payOrderBean = buildOrderParams(touchKey, privateKey);
        if (payOrderBean == null) {
            closeLoading(loadingDialog);
            loadingDialog = null;
            return;
        }

        PayCashierMain.getInstance().payOrder(mContext, payOrderBean, new PaymentSdkCallBack() {

            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);
                loadingDialog = null;
                PayResultQueryResponse resp = PayResultQueryResponse.decodeJson(PayResultQueryResponse.class, jsonObject);
                if (TextUtils.equals(code, PayCashierSdk.LOCAL_PAY_SUCCESS)
                        && resp != null && TextUtils.equals(resp.payResult, ConstantsPayment.PAY_OK)) {
                    // 查询支付结果成功: 支付上报, callback给业务层, 再查询相关业务结果
                    PayCashierMain.getInstance().onResultBack(mContext, code, errorMsg, jsonObject, ConstantsPayment.PAY_OK);

                } else if (TextUtils.equals(code, PayCashierSdk.LOCAL_PAY_OTP_VERIFY)) {
                    // 下单返回需要bca otp
                    openPayHubBCAOtp(PayCashierMain.getInstance().sid, jsonObject);

                } else if (TextUtils.equals(code, PayCashierSdk.LOCAL_PAY_QUERY_CANCEL)) {
                    // 支付上报 查询支付结果, 用户点击取消按钮
                    PayCashierMain.getInstance().onResultBack(mContext, code, "", null, ConstantsPayment.PayResult_OTHER);

                } else {
                    checkErrorResult(code, errorMsg);
                }
            }
        });
    }

    /**
     * 充值下单
     *
     * @param touchKey
     * @param privateKey
     */
    private void onTopupOrder(String touchKey, String privateKey) {
        PayOrderBean orderBean = buildOrderParams(touchKey, privateKey);
        if (orderBean == null) {
            closeLoading(loadingDialog);
            loadingDialog = null;
            return;
        }
        RechrCashierMain.getInstance().rechargeOrder(mContext, orderBean, new RechrCallBack() {

            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject, String OT, String orderID) {
                closeLoading(loadingDialog);
                loadingDialog = null;
                RechrPayResultRsp resp = RechrPayResultRsp.decodeJson(RechrPayResultRsp.class, jsonObject);
                if (TextUtils.equals(code, RechrCashierSdk.LOCAL_PAY_SUCCESS) && resp != null
                        && TextUtils.equals(resp.getRechargePayResult(), ConstantsPayment.PAY_OK)) {
                    // 查询充值支付结果成功: 支付上报, callback给业务层, 再查询相关业务结果
                    RechrCashierMain.getInstance().onResultBack(code, errorMsg, jsonObject, OT, ConstantsPayment.PAY_OK, orderID);

                } else if (TextUtils.equals(code, RechrCashierSdk.LOCAL_PAY_OTP_VERIFY)) {
                    // 下单返回需要bca otp, 关闭当前页面
                    openPayHubBCAOtp(RechrCashierMain.getInstance().sid, jsonObject);

                } else if (TextUtils.equals(code, PayCashierSdk.LOCAL_PAY_QUERY_CANCEL)) {
                    // 充值上报 查询充值支付结果, 用户点击取消按钮
                    RechrCashierMain.getInstance().onResultBack(code, errorMsg, jsonObject, OT, ConstantsPayment.PayResult_OTHER, orderID);

                } else {
                    checkErrorResult(code, errorMsg);
                }
            }
        });
    }

    @Nullable
    private PayOrderBean buildOrderParams(String touchKey, String privateKey) {
        PayOrderBean payOrderBean = new PayOrderBean();
        payOrderBean.setTerminalInfoSchema(new TerminalInfoSchema());
        payOrderBean.setRechrAmount(rechrAmount);

        String transID = "";
        String payTypePayEx = "";       // 普通支付方式的payEx
        String mPayTypePayEx = "";      // 营销支付方式的payEx
        String payEx = "";
        TransSchema transSchema = CashierPricing.getInstance(mContext).getTransSchema();
        if (transSchema != null) {
            payOrderBean.setTT(transSchema.TT);
            transID = transSchema.ID;
        }
        ArrayList<PayTypeSchema> payTypesSchemaList = CashierPricing.getInstance(mContext).getPayTypesSchemaList();
        if (payTypesSchemaList != null && payTypesSchemaList.size() > 0) {
            PayTypeSchema typeSchema = payTypesSchemaList.get(mPosition);
            payTypePayEx = typeSchema.PayEx;
            payOrderBean.setPayEx(payTypePayEx);
            payOrderBean.setPayType(typeSchema.ID);
            payOrderBean.setPayInfo(typeSchema.getPayInfo());
        }
        ArrayList<MarketPayTypeSchema> marketPayTypeSchemaList = CashierPricing.getInstance(mContext).getMarketPayTypeSchemaList();
        if (marketPayTypeSchemaList != null && marketPayTypeSchemaList.size() > 0) {
            Map<Integer, String> hashMap = new HashMap<>();
            for (MarketPayTypeSchema marketPayTypeSchema : marketPayTypeSchemaList) {
                hashMap.put(marketPayTypeSchema.Id, marketPayTypeSchema.PayEx);
            }
            payOrderBean.setMPay(hashMap);
            mPayTypePayEx = marketPayTypeSchemaList.get(0).PayEx;
        }
        if (TextUtils.isEmpty(payTypePayEx)) {
            payEx = mPayTypePayEx;
        }
        if (TextUtils.isEmpty(mPayTypePayEx)) {
            payEx = payTypePayEx;
        }
        if (!TextUtils.isEmpty(payTypePayEx) && !TextUtils.isEmpty(mPayTypePayEx)) {
            payEx = payTypePayEx + "&" + mPayTypePayEx;
            payEx = StringUtil.sortNumberString(payEx, "&");
        }
        try {
            String signData = "";
            if (from == Constants.LOCAL_FROM_PAY || from == Constants.LOCAL_FROM_WITHDRAW) {
                signData = getSignData(touchKey, transID, payEx);

            } else if (from == Constants.LOCAL_FROM_TOP_UP) {
                signData = getSignData(touchKey, rechrAmount, payEx);
            }
            // payAuthData签名数据, 至少包含一个payEx(普通支付方式和营销支付方式),
            // 多个的话, 用 & 连接, 升序排列.
            String payAuthData = RSAHelper.signWithSHA(signData, privateKey);
            payOrderBean.setPayAuthSchema(new PayAuthSchema(Constants.PAY_AUTH_TYPE_TK, payAuthData));
        } catch (Exception e) {
            LogUtil.e(TAG, "payAuthData签名失败: " + e.toString());
        }

        return payOrderBean;
    }

    /**
     * 拼接签名数据:
     * 支付时 transID + "&" + payEx + "&" + touchKey
     * 充值时 rechrAmount + "&" + payEx + "&" + touchKey
     *
     * @param touchKey
     * @param data
     * @param payEx
     * @return
     */
    @NonNull
    private String getSignData(String touchKey, String data, String payEx) {
        String signData;
        if (TextUtils.isEmpty(payEx)) {
            signData = data + "&" + touchKey;
        } else {
            signData = data + "&" + payEx + "&" + touchKey;
        }
        return signData;
    }

    /**
     * 打开BCA otp界面
     *
     * @param sid
     * @param jsonObject
     */
    private void openPayHubBCAOtp(String sid, JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        Intent intent = new Intent(mContext, PayhubBcaOTPActivity.class);
        intent.putExtra(Constants.SP_KEY_SID, sid);
        intent.putExtra(Constants.LOCAL_FROM_KEY, from);

        if (from == Constants.LOCAL_FROM_PAY) {
            PayOrderResponse resp = PayOrderResponse.decodeJson(PayOrderResponse.class, jsonObject);
            if (resp != null) {
                intent.putExtra(Constants.LOCAL_TT_KEY, resp.TT);
                intent.putExtra(Constants.LOCAL_PAY_ORDER_KEY, resp.getPayOrder());
            }
        } else if (from == Constants.LOCAL_FROM_TOP_UP) {
            RechrOrderRsp resp = RechrOrderRsp.decodeJson(RechrOrderRsp.class, jsonObject);
            if (resp != null) {
                intent.putExtra(Constants.LOCAL_OT_KEY, resp.OT);
                intent.putExtra("orderIDKey", resp.orderID);
                intent.putExtra(Constants.LOCAL_PAY_ORDER_KEY, resp.getPayOrder());
            }
        }
        startActivity(intent);
        onRight_in_Activity();
    }

    private void checkErrorResult(String code, String errorMsg) {
        if (code.equals(Constants.RET_CODE_A203)        /*支付密码错误*/
                || code.equals(Constants.RET_CODE_A206) /*密码被锁定*/
                || code.equals(Constants.RET_CODE_A010) /*账户被锁定*/) {
            showNormalDialog(errorMsg);

        } else if (code.equals(Constants.RET_CODE_5012)) {
            // 支付方式请求支付时支付失败pay order error
            if (from == Constants.LOCAL_FROM_PAY
                    || from == Constants.LOCAL_FROM_TOP_UP) {
                // 打开选择支付方式列表界面
                Intent intent = new Intent();
                intent.putExtra(Constants.MSG_KEY, errorMsg);
                onOrder5012(intent);
            }
        } else if (TextUtils.equals(code, Constants.RET_CODE_A110)) {
            // TODO: 2019/5/23 已删除绑卡支付，卡支付失败
            LogUtil.e(TAG, code + " = 已删除绑卡支付，卡支付失败");
        } else {
            showToast(errorMsg);
        }
    }
}

