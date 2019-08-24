package com.ahdi.wallet.cashier.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.cryptor.aes.AesKeyCryptor;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.widgets.dialog.CommonDialog;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.lib.utils.widgets.keyboard.KeyboardUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Date: 2019/2/19 下午2:23
 * Author: kay lau
 * Description: 支付密码确认下单界面
 */
public class PayVerifyPINActivity extends PayBaseActivity {

    public static final String TAG = PayVerifyPINActivity.class.getSimpleName();

    private EditText editText;
    private int from;
    private KeyboardUtil keyboardUtil;
    private ImageView[] imageViews;
    private int height;
    private int mPosition;
    private String errMsg;
    private String iv;
    private LoadingDialog loadingDialog;
    private String rechrAmount;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        initIntentData(getIntent());
        mContext = this;
        setContentView(R.layout.dialog_activity_verify_pin);
        initTitleView();
        initView();

        ActivityManager.getInstance().addPayHubActivity(this);
    }

    private void initIntentData(Intent intent) {
        if (intent == null) {
            return;
        }
        from = intent.getIntExtra(Constants.LOCAL_FROM_KEY, -1);
        errMsg = intent.getStringExtra(Constants.MSG_KEY);
        iv = intent.getStringExtra("ivKey");
        height = intent.getIntExtra(Constants.LOCAL_HEIGHT_KEY, 0);
        mPosition = intent.getIntExtra(Constants.LOCAL_POSITION_KEY, 0);
        rechrAmount = intent.getStringExtra("rechrAmountKey");
    }

    public void initTitleView() {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.SDKCashierEnterPIN_A0));
        ImageView backView = findViewById(R.id.btn_back);
        backView.setImageResource(R.drawable.selector_btn_title_back);
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
        initRootViewHeight();

        // 初始化错误提示文案
        RelativeLayout rl_error_tips = findViewById(R.id.rl_error_tips);
        if (!TextUtils.isEmpty(errMsg)) {
            TextView tv_error_tips = findViewById(R.id.tv_error_tips);
            rl_error_tips.setVisibility(View.VISIBLE);
            tv_error_tips.setText(errMsg);
        } else {
            rl_error_tips.setVisibility(View.INVISIBLE);
        }

        View pwd_layout = findViewById(R.id.pwd_layout);
        editText = pwd_layout.findViewById(R.id.et);
        imageViews = new ImageView[6];
        imageViews[0] = pwd_layout.findViewById(R.id.item_code_iv1);
        imageViews[1] = pwd_layout.findViewById(R.id.item_code_iv2);
        imageViews[2] = pwd_layout.findViewById(R.id.item_code_iv3);
        imageViews[3] = pwd_layout.findViewById(R.id.item_code_iv4);
        imageViews[4] = pwd_layout.findViewById(R.id.item_code_iv5);
        imageViews[5] = pwd_layout.findViewById(R.id.item_code_iv6);

        intKeyBoardView();
    }

    private void initRootViewHeight() {
        RelativeLayout bg_view = findViewById(R.id.bg_view);
        ViewGroup.LayoutParams layoutParams = bg_view.getLayoutParams();
        layoutParams.height = height;
        bg_view.setLayoutParams(layoutParams);
        bg_view.setBackgroundResource(R.drawable.bg_dialog_payhub_title);
    }

    private void intKeyBoardView() {
        keyboardUtil = new KeyboardUtil(this, editText, 2, new PaymentKeyboardCallback());
        keyboardUtil.showKeyboard();
    }

    private class PaymentKeyboardCallback implements KeyboardUtil.CallBack {

        @Override
        public void onCallback(String result) {
            // 输入支付密码
            // 支付下单
            onOrder(AesKeyCryptor.encodePayPwd(result));
        }

        @Override
        public void onPwdInputCallback(StringBuffer buffer, String pwdStr) {
            for (int i = 0; i < buffer.length(); i++) {
                ImageView imageView = imageViews[i];
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(R.drawable.password_black_point);
            }
        }

        @Override
        public void onPwdStrCallback(StringBuffer stringBuffer) {
            imageViews[stringBuffer.length()].setVisibility(View.INVISIBLE);
        }
    }

    private void onClearKeyboard() {
        if (keyboardUtil != null) {
            keyboardUtil.onDeleteAll();
        }
    }

    private void onOrder(String result) {
        if (loadingDialog == null) {
            loadingDialog = showLoading();
        }
        if (from == Constants.LOCAL_FROM_PAY || from == Constants.LOCAL_FROM_WITHDRAW) {
            onPayOrder(result);

        } else if (from == Constants.LOCAL_FROM_TOP_UP) {
            onTopupOrder(result);
        }
    }

    private void onPayOrder(String result) {
        PayOrderBean payOrderBean = buildOrderParams(result);
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
                onClearKeyboard();
                PayResultQueryResponse resp = PayResultQueryResponse.decodeJson(PayResultQueryResponse.class, jsonObject);
                if (TextUtils.equals(code, PayCashierSdk.LOCAL_PAY_SUCCESS)
                        && resp != null && TextUtils.equals(resp.payResult, ConstantsPayment.PAY_OK)) {
                    // 查询支付结果成功: 支付上报, callback给业务层, 再查询相关业务结果
                    PayCashierMain.getInstance().onResultBack(mContext, code, errorMsg, jsonObject, ConstantsPayment.PAY_OK);

                } else if (TextUtils.equals(code, PayCashierSdk.LOCAL_PAY_OTP_VERIFY)) {
                    // 下单返回需要bca otp, 关闭当前页面
                    openPayHubBCAOtp(PayCashierMain.getInstance().sid, jsonObject);
                    finish();

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
     * @param result
     */
    private void onTopupOrder(String result) {
        PayOrderBean orderBean = buildOrderParams(result);
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
                onClearKeyboard();
                RechrPayResultRsp resp = RechrPayResultRsp.decodeJson(RechrPayResultRsp.class, jsonObject);
                if (TextUtils.equals(code, RechrCashierSdk.LOCAL_PAY_SUCCESS) && resp != null
                        && TextUtils.equals(resp.getRechargePayResult(), ConstantsPayment.PAY_OK)) {
                    // 查询充值支付结果成功: 支付上报, callback给业务层, 再查询相关业务结果
                    RechrCashierMain.getInstance().onResultBack(code, errorMsg, jsonObject, OT, ConstantsPayment.PAY_OK, orderID);

                } else if (TextUtils.equals(code, RechrCashierSdk.LOCAL_PAY_OTP_VERIFY)) {
                    // 下单返回需要bca otp, 关闭当前页面
                    openPayHubBCAOtp(RechrCashierMain.getInstance().sid, jsonObject);
                    finish();

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
    private PayOrderBean buildOrderParams(String result) {
        PayOrderBean payOrderBean = new PayOrderBean();
        payOrderBean.setTerminalInfoSchema(new TerminalInfoSchema());
        payOrderBean.setPayAuthSchema(new PayAuthSchema(Constants.PAY_AUTH_TYPE_PWD, result));
        payOrderBean.setIv(iv);
        payOrderBean.setRechrAmount(rechrAmount);

        TransSchema transSchema = CashierPricing.getInstance(mContext).getTransSchema();
        if (transSchema != null) {
            payOrderBean.setTT(transSchema.TT);
        }

        ArrayList<PayTypeSchema> payTypesSchemaList = CashierPricing.getInstance(mContext).getPayTypesSchemaList();
        if (payTypesSchemaList != null && payTypesSchemaList.size() > 0) {
            PayTypeSchema typeSchema = payTypesSchemaList.get(mPosition);
            payOrderBean.setPayEx(typeSchema.PayEx);
            payOrderBean.setPayType(typeSchema.ID);
            payOrderBean.setPayInfo(typeSchema.getPayInfo());
        }

        ArrayList<MarketPayTypeSchema> marketPayTypeSchemaList = CashierPricing.getInstance(mContext).getMarketPayTypeSchemaList();
        Map<Integer, String> hashMap = new HashMap<>();
        for (MarketPayTypeSchema marketPayTypeSchema : marketPayTypeSchemaList) {
            hashMap.put(marketPayTypeSchema.Id, marketPayTypeSchema.PayEx);
        }
        payOrderBean.setMPay(hashMap);

        return payOrderBean;
    }

    private void showDialog(String msg, int from) {
        new CommonDialog
                .Builder(mContext)
                .setMessage(msg)
                .setMessageCenter(true)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.DialogButton_B0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (from == Constants.LOCAL_FROM_WITHDRAW) {
                            PayCashierMain.getInstance().onResultBack(PayCashierSdk.LOCAL_PAY_USER_CANCEL, "", null);
                            finish();
                        }
                        dialog.dismiss();
                    }
                }).show();
    }

    private void checkErrorResult(String code, String errorMsg) {
        if (code.equals(Constants.RET_CODE_A203)/*支付密码错误*/
                || code.equals(Constants.RET_CODE_A206)/*密码被锁定*/
                || code.equals(Constants.RET_CODE_A010) /*账户被锁定*/) {
            showNormalDialog(errorMsg);

        } else if (code.equals(Constants.RET_CODE_5012)) {
            //支付方式请求支付时支付失败pay order error
            if (from == Constants.LOCAL_FROM_WITHDRAW) {
                showDialog(errorMsg, from);
            } else {
                // 关闭当前页面, 回到收银台首页, 再打开选择支付方式列表界面
                Intent intent = new Intent();
                intent.putExtra(Constants.MSG_KEY, errorMsg);
                setResult(PayHubActivity.RESULT_CODE_CLOSE_VERIFY_PIN_5012, intent);
                finish();
                onRight_in_Activity();
            }
        } else if (TextUtils.equals(code, Constants.RET_CODE_A110)) {
            // TODO: 2019/5/23 已删除绑卡支付，卡支付失败
            LogUtil.e(TAG, code + " = 已删除绑卡支付，卡支付失败");
        } else {
            showToast(errorMsg);
        }
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    private void onBack() {
        finish();
        onCloseLeftToRightActivity();
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
        intent.putExtra(Constants.LOCAL_SID_KEY, sid);
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
}
