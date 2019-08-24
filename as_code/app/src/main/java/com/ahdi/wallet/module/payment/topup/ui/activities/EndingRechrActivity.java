package com.ahdi.wallet.module.payment.topup.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahdi.wallet.module.payment.topup.TopUpSdk;
import com.ahdi.wallet.module.payment.topup.callback.TopUpCallBack;
import com.ahdi.wallet.module.payment.topup.response.RechrResultQueryRsp;
import com.ahdi.wallet.app.schemas.PaySdkTipsSchema;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.AmountUtil;
import com.ahdi.lib.utils.utils.ToolUtils;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.wallet.app.ui.adapters.EndingAdapter;
import com.ahdi.wallet.app.ui.base.EndingBaseActivity;
import com.ahdi.wallet.app.ui.bean.EndingItemBean;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EndingRechrActivity extends EndingBaseActivity {

    private static final int QUERY_INTERVAL_TIME = 5 * 1000;    // 查询间隔时间
    private static final int MSG_CONTINUE_QUERY = 0;
    private volatile int queryCount = 2;                        // 失败之后继续查询的次数

    private View ll_result, ll_exception, ll_btn_double;
    private ImageView iv_topup_result;
    private TextView tv_topup_state, tv_topup_tips, tv_topup_exception_result;
    private RecyclerView rv_topup_info_list;
    private Button btn_single;

    protected String errMsg;
    protected String OT;
    protected LoadingDialog loadingDialog;

    protected Handler handler;

    private class QueryHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_CONTINUE_QUERY) {
                onStartQueryPayResult();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        initIntentData(getIntent());
        setContentView(R.layout.ending_activity_topup);
        initCommonTitle(getString(R.string.RechrEnding_A0));
        initView();
        initData();
        loadingDialog = showLoading();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 关闭充值流程打开的activity, 原因: 如果设备打开横竖屏自动切换, 点击done关闭多个activity时, 会引起横竖屏切换动画.
        ActivityManager.getInstance().finishCommonActivity();
        // 关闭收银台相关界面, 原因: 收银台查询支付结果之后跳转此界面时, 右入动画无效.
        ActivityManager.getInstance().finishPayHubActivity();
    }

    private void initIntentData(Intent intent) {
        if (intent == null) {
            return;
        }
        OT = intent.getStringExtra(Constants.LOCAL_OT_KEY);
        errMsg = intent.getStringExtra(Constants.MSG_KEY);
    }

    private void initView() {
        // 结果页面
        ll_result = findViewById(R.id.ll_result);
        iv_topup_result = findViewById(R.id.iv_topup_result);
        tv_topup_state = findViewById(R.id.tv_topup_state);
        tv_topup_tips = findViewById(R.id.tv_topup_tips);

        //展示信息列表
        rv_topup_info_list = findViewById(R.id.rv_topup_info_list);
        rv_topup_info_list.setLayoutManager(new LinearLayoutManager(this));

        // 异常结果页面
        ll_exception = findViewById(R.id.ll_exception);
        tv_topup_exception_result = findViewById(R.id.tv_topup_exception_result);
        ll_btn_double = findViewById(R.id.ll_btn_double);

        // 初始化 btn 点击事件
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        findViewById(R.id.btn_exception_cancel).setOnClickListener(this);
        findViewById(R.id.btn_exception_try_again).setOnClickListener(this);
        btn_single = findViewById(R.id.btn_exception_single);
        btn_single.setOnClickListener(this);
    }

    private void initData() {
        // 隐藏所有布局
        ll_result.setVisibility(View.GONE);
        ll_exception.setVisibility(View.GONE);
        // 隐藏标题返回按钮
        initTitleBack().setVisibility(View.INVISIBLE);

        if (!TextUtils.isEmpty(errMsg)) {
            // sdk支付结果查询查不到结果时, 显示单选按钮
            ll_exception.setVisibility(View.VISIBLE);
            ll_btn_double.setVisibility(View.GONE);
            btn_single.setVisibility(View.VISIBLE);
            tv_topup_exception_result.setText(getString(R.string.RechrEnding_I0));
            closeLoading(loadingDialog);

        } else {
            ll_exception.setVisibility(View.GONE);
            ll_btn_double.setVisibility(View.VISIBLE);
            btn_single.setVisibility(View.GONE);
        }
        startQueryResult();
    }

    protected void startQueryResult() {
        if (handler == null) {
            handler = new QueryHandler();
        }
        Message message = handler.obtainMessage(MSG_CONTINUE_QUERY);
        handler.sendMessageDelayed(message, 200);
    }

    /**
     * 开始查询充值结果
     */
    protected void onStartQueryPayResult() {
        AppMain.getInstance().onQueryRechargeResult(this, OT, new TopUpCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject, String ID) {
                RechrResultQueryRsp rsp = RechrResultQueryRsp.decodeJson(RechrResultQueryRsp.class, jsonObject);
                //查询充值结果 状态处理
                handlerQueryResult(code, rsp);
            }
        });
    }

    /**
     * 处理充值结果:
     * 成功,失败, 第三次查询返回支付中:              显示状态以及条目信息
     * 网络异常, 服务端异常(retCode !0), 其他异常:   展示try again 界面, 展示对应错误信息(本地定义)
     *
     * @param code
     * @param rsp
     */
    private void handlerQueryResult(String code, RechrResultQueryRsp rsp) {
        if (rsp != null) {
            if (TextUtils.equals(TopUpSdk.LOCAL_PAY_SUCCESS, code)) {
                if (TextUtils.equals(rsp.rechargeResult, Constants.APP_QUERY_PAY_RESULT_SUCCESS)) {
                    //支付成功处理
                    closeLoading(loadingDialog);
                    handlerSuccessResult(rsp);

                } else if (TextUtils.equals(rsp.rechargeResult, Constants.APP_QUERY_PAY_RESULT_FAIL)) {
                    //支付失败处理
                    closeLoading(loadingDialog);
                    handlerFailResult(rsp);

                } else if (TextUtils.equals(rsp.rechargeResult, Constants.APP_QUERY_PAY_RESULT_PAYING)) {
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
    private void handlerFailResult(RechrResultQueryRsp rsp) {
        rv_topup_info_list.setVisibility(View.GONE);
        onSetItemInfo(rsp, R.mipmap.common_ending_fail);
    }

    /**
     * 结果充值中处理
     *
     * @param rsp
     */
    private void handlerPayingResult(RechrResultQueryRsp rsp) {
        onSetItemInfo(rsp, R.mipmap.common_ending_wait);
    }

    /**
     * 结果成功处理
     *
     * @param rsp
     */
    private void handlerSuccessResult(RechrResultQueryRsp rsp) {
        onSetItemInfo(rsp, R.mipmap.common_ending_success);
    }

    /**
     * 继续查询
     *
     * @param code
     * @param rsp
     */
    private void onContinueQuery(String code, RechrResultQueryRsp rsp) {
        ll_exception.setVisibility(View.GONE);
        if (queryCount > 0) {
            queryCount--;
            if (handler == null) {
                handler = new QueryHandler();
            }
            Message msg = handler.obtainMessage(MSG_CONTINUE_QUERY);
            handler.sendMessageDelayed(msg, QUERY_INTERVAL_TIME);
        } else {
            closeLoading(loadingDialog);
            onDestroyHandler();
            onShowExceptionView();
            if (rsp != null && TextUtils.equals(code, TopUpSdk.LOCAL_PAY_SUCCESS)
                    && (TextUtils.equals(rsp.rechargeResult, Constants.APP_QUERY_PAY_RESULT_PAYING)
                    || TextUtils.equals(rsp.rechargeResult, Constants.APP_QUERY_PAY_RESULT_WAIT))) {
                // 处理支付中
                handlerPayingResult(rsp);

            } else if (TextUtils.equals(code, TopUpSdk.LOCAL_PAY_NETWORK_EXCEPTION)) {
                tv_topup_exception_result.setText(getString(R.string.RechrEnding_G0));

            } else {
                tv_topup_exception_result.setText(getString(R.string.RechrEnding_H0));
            }
        }
    }

    /**
     * 显示异常结果根布局
     */
    protected void onShowExceptionView() {
        ll_result.setVisibility(View.GONE);
        ll_exception.setVisibility(View.VISIBLE);
    }

    /**
     * 设置条目信息
     *
     * @param rsp
     * @param resId
     */
    private void onSetItemInfo(RechrResultQueryRsp rsp, int resId) {
        ll_result.setVisibility(View.VISIBLE);
        iv_topup_result.setBackgroundColor(ToolUtils.getColor(this, R.color.CC_00000000));
        iv_topup_result.setImageResource(resId);
        if (rsp != null) {
            setPayStateInfo(rsp);
            addItem(rsp);
        }
    }

    /**
     * 设置充值状态标题
     *
     * @param rsp
     */
    private void setPayStateInfo(RechrResultQueryRsp rsp) {
        PaySdkTipsSchema tipsSchemas = rsp.getTipsSchemas();
        if (tipsSchemas == null) {
            tv_topup_state.setVisibility(View.GONE);
            tv_topup_tips.setVisibility(View.GONE);
            return;
        }
        if (!TextUtils.isEmpty(tipsSchemas.title)) {
            tv_topup_state.setText(tipsSchemas.title);
            tv_topup_state.setVisibility(View.VISIBLE);
        } else {
            tv_topup_state.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(tipsSchemas.body)) {
            tv_topup_tips.setText(tipsSchemas.body);
            tv_topup_tips.setVisibility(View.VISIBLE);
        } else {
            tv_topup_tips.setVisibility(View.GONE);
        }
    }

    /**
     * 添加展示条目
     *
     * @param rsp
     */
    private void addItem(RechrResultQueryRsp rsp) {
        List<EndingItemBean> list = new ArrayList<>();

        if (!TextUtils.isEmpty(rsp.payType)) {
            list.add(new EndingItemBean(getString(R.string.RechrEnding_B0), rsp.payType));
        }
        //添加分割线
        list.add(new EndingItemBean(true));

        if (!TextUtils.isEmpty(rsp.amount)) {
            list.add(new EndingItemBean(getString(R.string.RechrEnding_C0), AmountUtil.getFormatAmount(ConfigCountry.KEY_CURRENCY_SYMBOL, rsp.amount)));
        }
        if (!TextUtils.isEmpty(rsp.serviceFee)) {
            list.add(new EndingItemBean(getString(R.string.RechrEnding_J0), AmountUtil.getFormatAmount(ConfigCountry.KEY_CURRENCY_SYMBOL, rsp.serviceFee)));
        }
        if (!TextUtils.isEmpty(rsp.totalAmount)) {
            list.add(new EndingItemBean(getString(R.string.RechrEnding_K0), AmountUtil.getFormatAmount(ConfigCountry.KEY_CURRENCY_SYMBOL, rsp.totalAmount)));
        }
        rv_topup_info_list.setAdapter(new EndingAdapter(this, list));
    }

    @Override
    public void onClick(View v) {
        if (!isCanClick()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.btn_confirm
                || id == R.id.btn_exception_single
                || id == R.id.btn_exception_cancel) {
            onDestroyHandler();
            finish();

        } else if (id == R.id.btn_exception_try_again) {
            // 重新开始查询
            queryCount = 2;
            loadingDialog = showLoading();
            onStartQueryPayResult();
        }
    }

    private void onDestroyHandler() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }
}
