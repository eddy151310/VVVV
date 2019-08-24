package com.ahdi.wallet.app.ui.activities.balance;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ahdi.wallet.app.sdk.AccountSdk;
import com.ahdi.wallet.app.sdk.IDVerifySdk;
import com.ahdi.wallet.app.callback.AccountSdkCallBack;
import com.ahdi.wallet.app.callback.IDVerifyCallBack;
import com.ahdi.wallet.app.response.QueryBalanceRsp;
import com.ahdi.wallet.app.ui.activities.payPwd.PayPwdGuideSetActivity;
import com.ahdi.wallet.module.payment.topup.ui.activities.TopUpActivity;
import com.ahdi.wallet.module.payment.withdraw.WithDrawSdk;
import com.ahdi.wallet.module.payment.withdraw.callback.WithDrawResultCallBack;
import com.ahdi.wallet.module.payment.withdraw.response.WDQueryBankCardInfoResp;
import com.ahdi.wallet.module.payment.withdraw.schemas.WDBankAccountSchema;
import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.utils.AmountUtil;
import com.ahdi.lib.utils.utils.ToolUtils;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.widgets.CheckSafety;
import com.ahdi.wallet.module.payment.withdraw.ui.activities.WithdrawActivity;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * 余额界面:
 * -------充值
 *
 * @author admin
 */
public class BalanceActivity extends AppBaseActivity implements OnRefreshListener {

    private TextView tv_cash_balance;
    private RelativeLayout rl_withdraw, rl_top_up;
    private TextView tv_withdraw, tv_top_up;
    private SwipeRefreshLayout refresh_layout;
    private static final int REQUEST_CODE_SET_PAY_PWD = 0;
    public static final int RESULT_CODE_CHECK_SAFETY = 1;
    private boolean query = true;
    private boolean isAgainQuery = true;
    private LinearLayout ll_text_tips;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.activity_balance);
        initTitle();
        initView();
        refreshBalance();
    }

    private void initTitle() {
        initCommonTitle(getString(R.string.BalanceHome_A0));
        initTitleBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void checkSafety() {
        String status = ProfileUserUtil.getInstance().getAccountStatus();
        if (!TextUtils.equals(status, Constants.STATUE_UNSAFE_KEY)) {
            tv_withdraw.setTextColor(ToolUtils.getColor(this, R.color.CC_1A1B24));
            rl_withdraw.setEnabled(true);

            tv_top_up.setTextColor(ToolUtils.getColor(this, R.color.CC_1A1B24));
            rl_top_up.setEnabled(true);

            ll_text_tips.setVisibility(View.GONE);

        } else { // 没有设置支付密码 不可用
            tv_withdraw.setTextColor(ToolUtils.getColor(this, R.color.CC_5F5F67));
            rl_withdraw.setEnabled(false);

            tv_top_up.setTextColor(ToolUtils.getColor(this, R.color.CC_5F5F67));
            rl_top_up.setEnabled(false);

            ll_text_tips.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        refresh_layout = findViewById(R.id.refresh_layout);
        refresh_layout.setColorSchemeColors(getResources().getColor(R.color.CC_C31617));
        refresh_layout.setOnRefreshListener(this);

        tv_cash_balance = findViewById(R.id.tv_cash_balance);

        TextView tv_cash_unit = findViewById(R.id.balance_cash_unit);
        tv_cash_unit.setText(ConfigCountry.KEY_CURRENCY_SYMBOL);

        // 充值
        rl_top_up = findViewById(R.id.rl_top_up);
        rl_top_up.setOnClickListener(this);
        tv_top_up = findViewById(R.id.tv_top_up);

        // 提现
        rl_withdraw = findViewById(R.id.rl_withdraw);
        // 提现入口默认隐藏, 查询余额接口响应之后, 再根据"WithdrawSW"是否显示.
        rl_withdraw.setVisibility(View.GONE);
        rl_withdraw.setOnClickListener(this);
        tv_withdraw = findViewById(R.id.tv_withdraw);

        ll_text_tips = findViewById(R.id.ll_text_tips);
        ll_text_tips.setOnClickListener(this);
        TextView tv_tip = findViewById(R.id.tv_tip);
        tv_tip.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tv_tip.getPaint().setAntiAlias(true);// 抗锯齿

        checkSafety();
    }

    private void refreshBalance() {
        String balance = ProfileUserUtil.getInstance().getAccountBalance();
        if (!TextUtils.isEmpty(balance)) {
            tv_cash_balance.setText(AmountUtil.formatAmount(balance));
        }
    }

    private void onQueryBalance(LoadingDialog dialog) {

        AppMain.getInstance().queryBalance(this, GlobalApplication.getApplication().getSID(),
                new AccountSdkCallBack() {
                    @Override
                    public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                        closeLoading(dialog);
                        isAgainQuery = true;
                        refresh_layout.setRefreshing(false);
                        if (TextUtils.equals(code, AccountSdk.LOCAL_PAY_SUCCESS)) {
                            QueryBalanceRsp resp = QueryBalanceRsp.decodeJson(QueryBalanceRsp.class, jsonObject);
                            if (resp != null && resp.getAccountSchema() != null) {
                                GlobalApplication.getApplication().updateAccountSchema(resp.getAccountSchema());
                                refreshBalance();
                                refreshWithdraw(resp.isWithdraw);
                            }
                        } else {
                            showToast(errorMsg);
                        }
                    }
                });
    }

    private void refreshWithdraw(boolean isWithdraw) {
        // 提现入口默认隐藏, 查询余额接口响应之后, 再根据"WithdrawSW"是否显示.
        if (isWithdraw) {
            rl_withdraw.setVisibility(View.VISIBLE);
        } else {
            rl_withdraw.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityManager.getInstance().finishCommonActivity();
        if (query && isAgainQuery && isLogin()) {
            isAgainQuery = false;
            LoadingDialog dialog = showLoading();
            onQueryBalance(dialog);
        }
    }

    @Override
    public void onClick(View v) {
        if (!isCanClick()) {
            return;
        }
        if (refresh_layout.isRefreshing()) {
            return;
        }
        switch (v.getId()) {
            case R.id.rl_top_up:
                openTopUp();
                break;
            case R.id.rl_withdraw:
                // 打开提现界面
                openWithdraw();
                break;
            case R.id.ll_text_tips:
                // 打开引导设置支付密码
                openPayPwdGuide();
                break;
            default:
                break;
        }
    }

    /**
     * 打开充值界面
     */
    private void openTopUp() {
        if (CheckSafety.checkSafe(this, PayPwdGuideSetActivity.class)) {
            Intent topUp = new Intent(this, TopUpActivity.class);
            startActivity(topUp);
        }
    }

    /**
     * 提现: 查询绑定银行账户信息
     */
    public void queryWDBindAccountInfo() {
        LoadingDialog dialog = showLoading();
        AppMain.getInstance().queryWDBindAccountInfo(this, new WithDrawResultCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject, String ID) {
                closeLoading(dialog);
                onBindCardCallback(code, errorMsg, jsonObject);
            }
        });
    }

    /**
     * 提现账户信息接口回调
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
                startWithdraw(resp, bindSchemas);
            } else {
                onAddBankAccount(); //如果无提现账户  引导添加
            }
        } else {
            showToast(errorMsg);
        }
    }

    /**
     * 打开提现界面
     *
     * @param resp
     * @param bindSchemas
     */
    private void startWithdraw(WDQueryBankCardInfoResp resp, WDBankAccountSchema[] bindSchemas) {
        List<WDBankAccountSchema> bindSchemaList = Arrays.asList(bindSchemas);
        Intent withDraw = new Intent(this, WithdrawActivity.class);
        withDraw.putExtra(Constants.LOCAL_BANK_KEY, (Serializable) bindSchemaList);
        withDraw.putExtra(Constants.LOCAL_FEE_KEY, resp.getFeeExp());
        startActivity(withDraw);
    }

    /**
     * 添加银行账户
     */
    private void onAddBankAccount() {
        AppMain.getInstance().addBankAccount(this, AccountSdk.PWD_CHECK_TTYPE_BINDBANKACCOUNT,
                new AccountSdkCallBack() {
                    @Override
                    public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                        if (TextUtils.equals(code, AccountSdk.LOCAL_PAY_SUCCESS)) {
                            query = false;
                            queryWDBindAccountInfo();
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
                    queryWDBindAccountInfo();

                } else if (!TextUtils.equals(code, IDVerifySdk.LOCAL_USER_CANCEL) && !TextUtils.isEmpty(errorMsg)) {
                    showToast(errorMsg);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SET_PAY_PWD) {
            if (resultCode == RESULT_CODE_CHECK_SAFETY) {
                checkSafety();
            }
        }
    }

    @Override
    public void onRefresh() {
        onQueryBalance(null);
    }


    @Override
    public void onBackPressed() {
        ActivityManager.getInstance().finishToMainActivity();
        finish();
    }

    private void openPayPwdGuide() {
        Intent intent = new Intent(this, PayPwdGuideSetActivity.class);
        intent.putExtra(Constants.LOCAL_FROM_KEY, Constants.LOCAL_PAYMENT_FROM_GUIDE_SET);
        startActivityForResult(intent, REQUEST_CODE_SET_PAY_PWD);
    }

    private void openWithdraw() {
        // 检查支付密码, 没设置的话, 引导设置支付密码
        if (!CheckSafety.checkSafe(this, PayPwdGuideSetActivity.class)) {
            return;
        }
        // 实名认证开关已开启并且需要实名认证
        if (ProfileUserUtil.getInstance().isRnaSw()
                && ProfileUserUtil.getInstance().isNeedRNA()) {
            checkIDStatus();
        } else {
            queryWDBindAccountInfo();
        }
    }
}
