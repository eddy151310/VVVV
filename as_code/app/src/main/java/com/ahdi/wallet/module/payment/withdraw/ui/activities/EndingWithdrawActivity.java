package com.ahdi.wallet.module.payment.withdraw.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahdi.wallet.module.payment.withdraw.WithDrawSdk;
import com.ahdi.wallet.module.payment.transfer.schemas.TipsSchema;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.AmountUtil;
import com.ahdi.lib.utils.utils.ToolUtils;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.wallet.app.ui.adapters.EndingAdapter;
import com.ahdi.wallet.app.ui.bean.EndingItemBean;
import com.ahdi.wallet.module.payment.withdraw.callback.WithDrawResultCallBack;
import com.ahdi.wallet.module.payment.withdraw.response.WDQueryProgressResp;

import org.json.JSONObject;

import java.util.ArrayList;

public class EndingWithdrawActivity extends AppBaseActivity {

    public static final String CARD_NUM = "CARD_NUM";
    private static final int QUERY_INTERVAL_TIME = 5 * 1000; // 查询间隔时间
    private static final int CONTINUE_QUERY = 0;
    private volatile int queryCount = 2; //失败之后继续查询的次数

    private String ID = "";
    private String errMsg = "";
    private ImageView iv_pay_result;
    private TextView tv_pay_state, tv_pay_tips, tv_pay_tips_except;
    private View root_view_query_result, root_view_query_exception;
    private Handler handler;
    private LoadingDialog loadingDialog;
    private RecyclerView ending_recycler_view;
    private Button btn_cancel_except;
    private Button btn_try_again_except;

    private class QueryHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == CONTINUE_QUERY) {
                onStartQueryPayResult();
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        initIntentData(getIntent());
        setContentView(R.layout.ending_activity_withdraw);
        loadingDialog = showLoading();
        initCommonTitle(getString(R.string.WithdrawEnding_A0));
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 关闭转账流程打开的activity, 原因: 如果设备打开横竖屏自动切换, 点击done关闭多个activity时, 会引起横竖屏切换动画.
        ActivityManager.getInstance().finishCommonActivity();
        // 关闭收银台相关界面, 原因: 收银台查询支付结果之后跳转此界面时, 右入动画无效.
        ActivityManager.getInstance().finishPayHubActivity();
    }

    protected void initIntentData(Intent intent) {
        if (intent == null) {
            return;
        }
        ID = intent.getStringExtra(Constants.LOCAL_ID_KEY);
        errMsg = intent.getStringExtra(Constants.MSG_KEY);
    }

    private void initView() {

        findViewById(R.id.btn_back).setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_next).setVisibility(View.INVISIBLE);

        // 查询结果界面
        root_view_query_result = findViewById(R.id.root_view_query_result);
        iv_pay_result = findViewById(R.id.icon_pay_result);
        tv_pay_state = findViewById(R.id.tv_pay_state_title);
        tv_pay_tips = findViewById(R.id.tv_pay_tips_body);
        ending_recycler_view = findViewById(R.id.ending_recycler_view);

        // 查询异常界面
        root_view_query_exception = findViewById(R.id.root_view_query_exception);
        tv_pay_tips_except = findViewById(R.id.tv_pay_tips_except);
        btn_cancel_except = findViewById(R.id.btn_cancel_except);
        btn_try_again_except = findViewById(R.id.btn_try_again_except);

    }

    private void initData() {
        // 隐藏所有布局
        onHiddenAllView();
        initTitleTextView(getString(R.string.WithdrawEnding_A0));
        initTitleBack().setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        btn_cancel_except.setOnClickListener(this);
        btn_try_again_except.setOnClickListener(this);
        if (!TextUtils.isEmpty(errMsg)) {
            onShowExceptionView();
            tv_pay_tips_except.setText(errMsg);
            closeLoading(loadingDialog);
            btn_cancel_except.setText(getString(R.string.WithdrawEnding_G0));
            btn_try_again_except.setVisibility(View.GONE);
        } else {
            btn_try_again_except.setVisibility(View.VISIBLE);
            btn_try_again_except.setText(getString(R.string.WithdrawEnding_H0));
            btn_cancel_except.setText(getString(R.string.WithdrawEnding_I0));
            startQueryResult();
        }
    }

    /**
     * 隐藏所有布局
     */
    private void onHiddenAllView() {
        root_view_query_result.setVisibility(View.INVISIBLE);
        root_view_query_exception.setVisibility(View.INVISIBLE);
    }

    /**
     * 显示异常结果根布局
     */
    private void onShowExceptionView() {
        root_view_query_result.setVisibility(View.GONE);
        root_view_query_exception.setVisibility(View.VISIBLE);
    }

    private void startQueryResult() {
        if (handler == null) {
            handler = new QueryHandler();
        }
        Message message = handler.obtainMessage(CONTINUE_QUERY);
        handler.sendMessageDelayed(message, 200);
    }

    /**
     * 开始查询提现结果
     */
    private void onStartQueryPayResult() {
        AppMain.getInstance().queryWDProgress(this, ID, new WithDrawResultCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject, String ID) {
                WDQueryProgressResp rsp = WDQueryProgressResp.decodeJson(WDQueryProgressResp.class, jsonObject);
                //查询提现结果 状态处理
                handlerQueryResult(code, rsp);
            }
        });
    }

    /**
     * 处理提现结果:
     * 成功,失败, 第三次查询返回支付中:              显示状态以及条目信息
     * 网络异常, 服务端异常(retCode !0), 其他异常:   展示try again 界面, 展示对应错误信息(本地定义)
     *
     * @param code
     * @param rsp
     */
    private void handlerQueryResult(String code, WDQueryProgressResp rsp) {
        if (rsp != null) {
            if (WithDrawSdk.LOCAL_PAY_SUCCESS.equals(code)) {
                if (TextUtils.equals(rsp.getResult(), Constants.APP_QUERY_PAY_RESULT_SUCCESS)) {
                    //支付成功处理
                    closeLoading(loadingDialog);
                    handlerSuccessResult(rsp);
                } else if (TextUtils.equals(rsp.getResult(), Constants.APP_QUERY_PAY_RESULT_FAIL)) {
                    //支付失败处理
                    closeLoading(loadingDialog);
                    handlerFailResult(rsp);
                } else if (TextUtils.equals(rsp.getResult(), Constants.APP_QUERY_PAY_RESULT_PAYING)) {
                    //支付中处理
                    closeLoading(loadingDialog);
                    handlerPayingResult(rsp);

                } else {
                    //继续查询
                    onContinueQuery(code, rsp);
                }
            } else {
                onContinueQuery(code, rsp);
            }
        } else {
            onContinueQuery(code, null);
        }

    }

    /**
     * 结果失败处理
     *
     * @param rsp
     */
    private void handlerFailResult(WDQueryProgressResp rsp) {
        onSetItemInfo(rsp, R.mipmap.common_ending_fail, true);
    }

    /**
     * 结果提现中处理
     *
     * @param rsp
     */
    private void handlerPayingResult(WDQueryProgressResp rsp) {
        onSetItemInfo(rsp, R.mipmap.common_ending_wait, true);
    }

    /**
     * 结果成功处理
     *
     * @param rsp
     */
    private void handlerSuccessResult(WDQueryProgressResp rsp) {
        onSetItemInfo(rsp, R.mipmap.common_ending_success, false);
    }

    /**
     * 继续查询
     *
     * @param code
     * @param rsp
     */
    private void onContinueQuery(String code, WDQueryProgressResp rsp) {
        root_view_query_exception.setVisibility(View.GONE);
        if (queryCount > 0) {
            queryCount--;
            if (handler == null) {
                handler = new QueryHandler();
            }
            Message msg = handler.obtainMessage(CONTINUE_QUERY);
            handler.sendMessageDelayed(msg, QUERY_INTERVAL_TIME);
        } else {
            closeLoading(loadingDialog);
            onDestroyHandler();
            onShowExceptionView();
            if (rsp != null && TextUtils.equals(code, WithDrawSdk.LOCAL_PAY_SUCCESS)
                    && (TextUtils.equals(rsp.getResult(), Constants.APP_QUERY_PAY_RESULT_PAYING)
                    || TextUtils.equals(rsp.getResult(), Constants.APP_QUERY_PAY_RESULT_WAIT))) {
                // 处理支付中
                handlerPayingResult(rsp);

            } else if (TextUtils.equals(code, WithDrawSdk.LOCAL_PAY_NETWORK_EXCEPTION)) {
                tv_pay_tips_except.setText(getString(R.string.WithdrawEnding_J0));

            } else {
                tv_pay_tips_except.setText(getString(R.string.WithdrawEnding_K0));
            }
        }
    }

    /**
     * 设置结果状态信息
     *
     * @param rsp
     */
    private void setErrInfo(WDQueryProgressResp rsp, boolean showTip) {
        TipsSchema tipsSchemas = rsp.getTipsSchemas();
        if (showTip && tipsSchemas != null && !TextUtils.isEmpty(tipsSchemas.body)) {
            tv_pay_tips.setText(tipsSchemas.body);
            tv_pay_tips.setVisibility(View.VISIBLE);
        } else {
            tv_pay_tips.setVisibility(View.GONE);
        }
    }

    /**
     * 设置转账状态标题
     *
     * @param rsp
     */
    private void setPayStateInfo(WDQueryProgressResp rsp) {
        TipsSchema tipsSchemas = rsp.getTipsSchemas();
        if (tipsSchemas != null && !TextUtils.isEmpty(tipsSchemas.title)) {
            tv_pay_state.setText(tipsSchemas.title);
            tv_pay_state.setVisibility(View.VISIBLE);
        } else {
            tv_pay_state.setVisibility(View.GONE);
        }
    }

    /**
     * 设置条目信息
     *
     * @param rsp
     * @param resId
     * @param showTip 是否显示tip(V1.0.3开始成功状态 不再显示tip)
     */
    private void onSetItemInfo(WDQueryProgressResp rsp, int resId, boolean showTip) {
        onShowResultView();
        setResultImg(resId);
        if (rsp != null) {
            setPayStateInfo(rsp);
            setErrInfo(rsp, showTip);
            addItem(rsp);
        }
    }

    /**
     * 显示正常结果根布局(支付成功, 失败, 支付中)
     */
    private void onShowResultView() {
        root_view_query_exception.setVisibility(View.GONE);
        root_view_query_result.setVisibility(View.VISIBLE);
    }

    /**
     * 设置查询结果状态图片
     *
     * @param resId
     */
    private void setResultImg(int resId) {
        iv_pay_result.setBackgroundColor(ToolUtils.getColor(this, R.color.CC_00000000));
        iv_pay_result.setImageResource(resId);
    }

    /**
     * 添加展示条目
     *
     * @param rsp
     */
    private void addItem(WDQueryProgressResp rsp) {
        ArrayList<EndingItemBean> list = new ArrayList<>();
        if (!TextUtils.isEmpty(rsp.bankAccount)) {
            list.add(new EndingItemBean(getString(R.string.WithdrawEnding_B0), rsp.bankAccount));
        }
        if (!TextUtils.isEmpty(rsp.wdName)) {
            list.add(new EndingItemBean(getString(R.string.WithdrawEnding_C0), rsp.wdName));
        }
        list.add(new EndingItemBean(true));

        if (!TextUtils.isEmpty(rsp.amount)) {
            list.add(new EndingItemBean(getString(R.string.WithdrawEnding_D0), AmountUtil.getFormatAmount(ConfigCountry.KEY_CURRENCY_SYMBOL, rsp.amount)));
        }
        if (!TextUtils.isEmpty(rsp.chargeFee)) {
            list.add(new EndingItemBean(getString(R.string.WithdrawEnding_E0), AmountUtil.getFormatAmount(ConfigCountry.KEY_CURRENCY_SYMBOL, rsp.chargeFee)));
        }
        if (!TextUtils.isEmpty(rsp.payAmount)) {
            list.add(new EndingItemBean(getString(R.string.WithdrawEnding_F0), AmountUtil.getFormatAmount(ConfigCountry.KEY_CURRENCY_SYMBOL, rsp.payAmount)));
        }
        EndingAdapter endingAdapter = new EndingAdapter(this, list);
        ending_recycler_view.setLayoutManager(new LinearLayoutManager(this));
        ending_recycler_view.setAdapter(endingAdapter);
    }

    private void onDestroyHandler() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (!isCanClick()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.btn_confirm
                || id == R.id.btn_cancel_except) {
            onDestroyHandler();
            finish();

        } else if (id == R.id.btn_try_again_except) {
            // 重新开始查询
            queryCount = 2;
            loadingDialog = showLoading();
            onStartQueryPayResult();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyHandler();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }
}
