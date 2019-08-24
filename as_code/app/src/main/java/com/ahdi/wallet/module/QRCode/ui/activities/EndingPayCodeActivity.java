package com.ahdi.wallet.module.QRCode.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ahdi.wallet.cashier.PayCashierSdk;
import com.ahdi.wallet.cashier.bean.ReportDataBean;
import com.ahdi.wallet.app.main.OtherSdkMain;
import com.ahdi.wallet.cashier.response.pay.PayResultQueryResponse;
import com.ahdi.wallet.app.utils.ConstantsPayment;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.AmountUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.lib.utils.utils.TerminalIdUtil;
import com.ahdi.lib.utils.utils.ToolUtils;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.ui.base.EndingBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * pay码被扫, 支付结果页
 */
public class EndingPayCodeActivity extends EndingBaseActivity {

    private static final String TAG = EndingPayCodeActivity.class.getSimpleName();

    private String transID;         // 用于支付上报.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        initIntentData(getIntent());
        setContentView(R.layout.ending_activity_pay_code);
        initTitle();
        initView();
        initData();
    }


    private void initIntentData(Intent intent) {
        if (intent == null) {
            return;
        }
        payResult = intent.getStringExtra(Constants.LOCAL_RESULT_KEY);
        errMsg = intent.getStringExtra(Constants.MSG_KEY);
        transID = intent.getStringExtra("transID");
        // 支付上报
        payReport();

        String data = intent.getStringExtra(Constants.DATA_KEY);
        if (TextUtils.isEmpty(data)) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(data);
            PayResultQueryResponse response = PayResultQueryResponse.decodeJson(PayResultQueryResponse.class, jsonObject);
            errMsg = response.err;
            amt = response.amt;
            this.payResult = response.payResult;
            merName = response.mer;
            payTypeName = response.payTypeName;
            price = response.OrderAmt;
            serviceFee = response.ServiceFee;
            voucher = response.MktAmt;

        } catch (JSONException e) {
            e.printStackTrace();
            LogUtil.e(TAG, "-------EndingPayCode 解析支付结果数据异常-------");
        }
    }

    private void payReport() {
        // 支付上报: 支付失败时 不需要上报.
        if (TextUtils.equals(payResult, ConstantsPayment.PAY_OK)) {
            // 支付成功.
            onPayReport(ConstantsPayment.PAY_OK);

        } else if (TextUtils.equals(payResult, ConstantsPayment.PLAT_FAIL)) {
            // 平台失败.
            onPayReport(ConstantsPayment.PLAT_FAIL);

        } else if (TextUtils.equals(payResult, PayCashierSdk.LOCAL_PAY_QUERY_CANCEL)) {
            // 点击按钮取消查询时打开ending, 包含查询支付结果PAY_ING, 网络异常, 网络超时.
            onPayReport(ConstantsPayment.PayResult_OTHER);
        }
    }

    private void initTitle() {
        initTitleTextView(getString(R.string.PayQREnding_A0));
        initTitleBack().setVisibility(View.INVISIBLE);
        initTitleNext().setVisibility(View.INVISIBLE);
    }

    private void initView() {
        ending_result = findViewById(R.id.ll_ending_result);
        ending_result.setVisibility(View.VISIBLE);

        // 顶部icon 金额 状态
        iv_ending_state = findViewById(R.id.iv_ending_state);
        tv_ending_amount = findViewById(R.id.tv_ending_amount);
        tv_ending_state = findViewById(R.id.tv_ending_state);

        // 中间显示: 商户名称 支付方式
        ll_mer_name = findViewById(R.id.ll_mer_name);
        tv_ending_mer_name = findViewById(R.id.tv_ending_mer_name);
        ll_pay_type = findViewById(R.id.ll_pay_type);
        tv_ending_pay_type = findViewById(R.id.tv_ending_pay_type);

        // 底部展示: 应付原价 消费Tips 支付方式服务费 营销抵扣金额(优惠券)
        ll_bottom_info = findViewById(R.id.ll_bottom_info);

        ll_price = findViewById(R.id.ll_price);
        tv_ending_price = findViewById(R.id.tv_ending_price);

        ll_service_fee = findViewById(R.id.ll_service_fee);
        tv_ending_service_fee = findViewById(R.id.tv_ending_service_fee);

        ll_voucher = findViewById(R.id.ll_voucher);
        tv_ending_voucher = findViewById(R.id.tv_ending_voucher);

        // 初始完成按钮点击事件
        findViewById(R.id.btn_ending_confirm).setOnClickListener(this);

        // 查询异常界面初始化
        initExceptionView();
    }

    private void initExceptionView() {
        ending_exception = findViewById(R.id.ll_ending_exception);
        ending_exception.setVisibility(View.GONE);

        findViewById(R.id.btn_exception).setOnClickListener(this);
    }

    private void initData() {
        // 根据查询的支付结果, 对应初始化数据: Icon 支付状态
        if (TextUtils.equals(payResult, ConstantsPayment.PAY_OK)) {
            setResultImg(iv_ending_state, R.mipmap.common_ending_success);
            String data = null;
            if (!TextUtils.isEmpty(amt)) {
                data = AmountUtil.getFormatAmount(symbol, amt);
            }
            initAmount(data);
            tv_ending_state.setText(getString(R.string.PayQREnding_B0));
            initShowItem();

        } else if (TextUtils.equals(payResult, PayCashierSdk.LOCAL_PAY_QUERY_CANCEL)) {
            ending_result.setVisibility(View.GONE);
            ending_exception.setVisibility(View.VISIBLE);

        } else if (!TextUtils.isEmpty(errMsg)) {
            // 支付结果失败  errMsg = response.err;
            setResultImg(iv_ending_state, R.mipmap.common_ending_fail);
            initAmount(getString(R.string.ScanQREnding_C0));
            tv_ending_state.setText(errMsg);
            // 只显示商户名称, 隐藏其他条目信息
            ll_pay_type.setVisibility(View.GONE);
            ll_bottom_info.setVisibility(View.GONE);
            // 商户名称
            setItemData(merName, ll_mer_name, tv_ending_mer_name, TYPE_0);
        }
    }

    /**
     * 初始化数据: 顶部金额 支付成功时显示金额, 支付失败时显示 Payment Failed
     *
     * @param data
     */
    private void initAmount(String data) {
        if (!TextUtils.isEmpty(data)) {
            tv_ending_amount.setVisibility(View.VISIBLE);
            tv_ending_amount.setText(data);

        } else {
            tv_ending_amount.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化数据: 底部条目, 判断有对应的值则展示 否则隐藏.
     */
    private void initShowItem() {
        // 商户名称
        setItemData(merName, ll_mer_name, tv_ending_mer_name, TYPE_0);
        // 支付方式
        setItemData(payTypeName, ll_pay_type, tv_ending_pay_type, TYPE_0);

        if (TextUtils.isEmpty(serviceFee) && TextUtils.isEmpty(voucher)) {
            // 无服务费无优惠 只显示商户名称和支付方式, 其他全部隐藏
            ll_bottom_info.setVisibility(View.GONE);

        } else {
            ll_bottom_info.setVisibility(View.VISIBLE);
            // 应付原价
            setItemData(price, ll_price, tv_ending_price, TYPE_1);
            // 支付方式服务费
            setItemData(serviceFee, ll_service_fee, tv_ending_service_fee, TYPE_1);
            // 营销抵扣金额
            setItemData(voucher, ll_voucher, tv_ending_voucher, TYPE_2);
        }
    }

    /**
     * 显示或隐藏展示的条目.
     *
     * @param data
     * @param view
     * @param textView
     */
    private void setItemData(String data, LinearLayout view, TextView textView, int type) {

        if (TextUtils.isEmpty(data)) {
            view.setVisibility(View.GONE);
            return;
        }

        view.setVisibility(View.VISIBLE);

        if (type == TYPE_0) {
            textView.setText(data);

        } else if (type == TYPE_1) {
            textView.setText(AmountUtil.getFormatAmount(symbol, data));

        } else if (type == TYPE_2) {
            textView.setText(AmountUtil.getFormatAmount(symbol, data, Constants.KEY_MINUS));
        }
    }

    /**
     * 根据查询到结果状态 设置对应图片(成功或者失败).
     * 注意: 支付中 网络异常等状态的图片设置, 在异常界面单独设置的icon.
     *
     * @param imageView
     * @param resId
     */
    protected void setResultImg(ImageView imageView, int resId) {
        imageView.setBackgroundColor(ToolUtils.getColor(this, R.color.CC_00000000));
        imageView.setImageResource(resId);
    }

    @Override
    public void onClick(View v) {
        if (!isCanClick()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.btn_ending_confirm
                || id == R.id.btn_exception) {
            ActivityManager.getInstance().openMainActivity(this);
        }
    }

    /**
     * 支付上报
     *
     * @param payResult
     */
    private void onPayReport(String payResult) {
        ReportDataBean reportData = new ReportDataBean();
        reportData.setUID(ProfileUserUtil.getInstance().getUserID());
        reportData.setTID(TerminalIdUtil.getTerminalID());
        reportData.setTransID(transID);
        reportData.setPayResult(payResult);
        OtherSdkMain.getInstance().payReport(ConstantsPayment.REPORT_TYPE_EXIT, reportData, GlobalApplication.getApplication().getSID());
    }

}
