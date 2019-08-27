package com.ahdi.wallet.cashier.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ahdi.wallet.R;
import com.ahdi.wallet.bca.BcaSdk;
import com.ahdi.wallet.cashier.PayCashierSdk;
import com.ahdi.wallet.bca.callback.BcaOTPCallBack;
import com.ahdi.wallet.cashier.callback.PaymentSdkCallBack;
import com.ahdi.wallet.cashier.callback.RechrCallBack;
import com.ahdi.wallet.cashier.main.PayCashierMain;
import com.ahdi.wallet.cashier.main.RechrCashierMain;
import com.ahdi.wallet.bca.response.BcaOTPRsp;
import com.ahdi.wallet.cashier.response.pay.PayResultQueryResponse;
import com.ahdi.wallet.cashier.response.rechr.RechrPayResultRsp;
import com.ahdi.wallet.app.schemas.PayOrderSchema;
import com.ahdi.wallet.app.schemas.VerifyCodeSchema;
import com.ahdi.wallet.app.utils.ConstantsPayment;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.base.BaseEditTextWatcher;
import com.ahdi.lib.utils.bean.PhoneBean;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.CountdownUtil;
import com.ahdi.lib.utils.utils.DeviceUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.ToolUtils;
import com.ahdi.lib.utils.widgets.DeleteEditText;
import com.ahdi.lib.utils.widgets.PhoneListPopUpWindow;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author xiaoniu
 * <p>
 * 收银台支付 bcaOTP
 */
public class PayhubBcaOTPActivity extends PayBaseActivity {

    private static final String TAG = PayhubBcaOTPActivity.class.getSimpleName();

    private ScrollView scrollView;
    private LinearLayout ll_phone_area;
    private TextView tv_phone_number;
    private ImageView iv_pointer;
    private DeleteEditText codeEdit;
    private TextView tv_send_code, btn_submit;
    private PhoneListPopUpWindow popUpWindow;

    private ViewTreeObserver.OnGlobalLayoutListener listener;
    private CountdownUtil countdownUtil;
    private ArrayList<PhoneBean> phoneList;
    //选中的手机号码的索引
    private int currentIndex = 0;

    //下发验证码或者支付下单时需要的参数
    private String payEx;
    private int channelID;
    private String ot;
    private String tt;
    private boolean softInputShow = false;//键盘是否显示
    private String sid;
    private String orderID;
    private Context mContext;
    private int from;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payhub_bca_otp);
        ActivityManager.getInstance().addPayHubActivity(this);
        mContext = this;
        initIntent(getIntent());
        initView();
        initData();
    }

    private void initIntent(Intent intent) {
        if (intent == null) {
            finish();
            return;
        }
        //获取传进来的参数
        from = intent.getIntExtra(Constants.LOCAL_FROM_KEY, -100);
        ot = intent.getStringExtra(Constants.LOCAL_OT_KEY);
        tt = intent.getStringExtra(Constants.LOCAL_TT_KEY);
        sid = intent.getStringExtra(Constants.LOCAL_KEY_SID);
        orderID = intent.getStringExtra("orderIDKey");
        PayOrderSchema payOrderSchema = (PayOrderSchema) intent.getSerializableExtra(Constants.LOCAL_PAY_ORDER_KEY);
        if (payOrderSchema == null) {
            finish();
            return;
        }
        //处理payOrderSchema
        channelID = payOrderSchema.ChannelID;
        String payParam = payOrderSchema.PayParam;
        LogUtil.d(TAG, "payParam: " + payParam);
        //解析payOrderSchema.payParam
        try {
            JSONObject payParamJson = new JSONObject(payParam);
            payEx = payParamJson.optString("payEx");
            //获取手机号列表
            JSONArray mobileList = payParamJson.optJSONArray("mobileList");
            if (mobileList.length() < 1) {
                finish();
                return;
            }
            phoneList = new ArrayList<>();
            PhoneBean phoneBean = null;
            for (int i = 0; i < mobileList.length(); i++) {
                JSONObject jsonObject = mobileList.getJSONObject(i);
                phoneBean = new PhoneBean(jsonObject.optString("msisdnId"), jsonObject.optString("msisdn"));
                phoneList.add(phoneBean);
            }
            if (phoneList.size() < 1) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d(TAG, "解析payParam出错：" + e.getMessage());
            finish();
        }
    }

    private void initView() {
        // title
        initCommonTitle(getString(R.string.SDKCheckOtp_A0));
        initTitleBack().setImageResource(R.drawable.selector_btn_title_back);

        scrollView = findViewById(R.id.scrollView);
        // 显示手机号码的区域
        ll_phone_area = findViewById(R.id.ll_phone_area);
        tv_phone_number = findViewById(R.id.tv_phone_number);
        findViewById(R.id.ll_select_phone).setOnClickListener(this);
        iv_pointer = findViewById(R.id.iv_pointer);
        // 验证码区域
        codeEdit = findViewById(R.id.et_verify_code);
        tv_send_code = findViewById(R.id.tv_send_code);
        tv_send_code.setOnClickListener(this);
        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);

        // btn_submit
        codeEdit.addTextChangedListener(new BaseEditTextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateViewState();
            }
        });
        //手机号码下拉框
        popUpWindow = new PhoneListPopUpWindow(mContext);
        //设置内容布局的高度
        initContentViewHeight();
        //监听键盘弹起的布局变化
        initSoftKeyboard();
    }

    /**
     * 设置内容布局的高度
     */
    private void initContentViewHeight() {
        int height = DeviceUtil.getScreenHeight(this);
        ViewGroup bg_view = findViewById(R.id.main_content);
        ViewGroup.LayoutParams layoutParams = bg_view.getLayoutParams();
        layoutParams.height = ((int) (height * Constants.LOCAL_DIALOG_HEIGHT_SCALE));
        bg_view.setLayoutParams(layoutParams);
        bg_view.setBackgroundResource(R.drawable.bg_dialog_payhub_title);
    }

    /**
     * 初始化布局变化的监听器
     */
    private void initSoftKeyboard() {
        ViewTreeObserver viewTreeObserver = codeEdit.getViewTreeObserver();
        removeViewTreeObserver(viewTreeObserver, listener);
        listener = new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                codeEdit.getWindowVisibleDisplayFrame(r);
                int screenHeight = codeEdit.getRootView().getHeight();
                int heightDifference = screenHeight - (r.bottom);
                //键盘弹起时隐藏提交按钮，防止按钮一起被顶到键盘上方
                if (heightDifference > 200) {
                    softInputShow = true;
                    btn_submit.setVisibility(View.GONE);
                    scrollView.smoothScrollTo(0, heightDifference);
                } else {
                    scrollView.smoothScrollTo(0, 0);
                    btn_submit.setVisibility(View.VISIBLE);
                }
            }
        };
        viewTreeObserver.addOnGlobalLayoutListener(listener);
    }

    /**
     * 用于控制 btn的状态
     */
    public void updateViewState() {
        String code = codeEdit.getText().toString();
        if (!TextUtils.isEmpty(code) && code.length() >= Constants.LOCAL_BCA_OTP_CODE_MIN_LENGTH) {
            btn_submit.setEnabled(true);
        } else {
            btn_submit.setEnabled(false);
        }
    }

    private void initData() {
        popUpWindow.setData(phoneList);
        popUpWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tv_phone_number.setText(phoneList.get(position).getMsisdn());
                currentIndex = position;
                popupWindowDismiss();
            }
        });
        //添加默认值
        tv_phone_number.setText(phoneList.get(0).getMsisdn());
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (!ToolUtils.isCanClick()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.ll_select_phone) {
            if (popUpWindow.isShowing()) {
                popupWindowDismiss();
            } else {
                popupWindowShow();
            }
        } else if (id == R.id.tv_send_code) {
            getCode();

        } else if (id == R.id.btn_submit) {
            closeSoftInput();
            orderPay();
        }
    }

    /**
     * 显示下拉框
     */
    private void popupWindowShow() {
        LogUtil.d(TAG, "显示下拉框");
        iv_pointer.setImageResource(R.mipmap.bca_otp_pointer_up);
        ll_phone_area.setBackground(getResources().getDrawable(R.drawable.bg_bca_otp_select_phone_area));
        popUpWindow.showAtLocation(ll_phone_area);
    }

    /**
     * 隐藏下拉框
     */
    private void popupWindowDismiss() {
        LogUtil.d(TAG, "隐藏下拉框");
        iv_pointer.setImageResource(R.mipmap.bca_otp_pointer_down);
        ll_phone_area.setBackground(getResources().getDrawable(R.drawable.bg_bca_otp_phone));
        popUpWindow.dismiss();
    }

    /**
     * 获取短信验证码
     */
    private void getCode() {
        LoadingDialog loadingDialog = showLoading();
        String payInfo = getPayInfo("");
        BcaSdk.bcaOTPSendVcode(mContext, sid, phoneList.get(currentIndex).getMsisdnId(), payEx, payInfo, new BcaOTPCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);
                BcaOTPRsp rsp = BcaOTPRsp.decodeJson(BcaOTPRsp.class, jsonObject);
                if (TextUtils.equals(code, Constants.RET_CODE_SUCCESS)) {
                    detailOTP(rsp.vCodeSchema);
                } else {
                    showToast(errorMsg);
                    if (rsp != null && rsp.vCodeSchema != null) {
                        detailOTP(rsp.vCodeSchema);
                    }
                }
            }
        });
    }

    /**
     * 处理倒计时
     *
     * @param vCodeSchema
     */
    private void detailOTP(VerifyCodeSchema vCodeSchema) {

        if (vCodeSchema != null) {
            int remain = vCodeSchema.Remain;
            if (remain > 0) {
                startCount(vCodeSchema.Wait);
            } else {
                tv_send_code.setEnabled(false);
            }
        }
    }

    /**
     * 倒计时
     *
     * @param count
     */
    public void startCount(int count) {
        stopCount();
        if (countdownUtil == null) {
            countdownUtil = new CountdownUtil();
        }
        countdownUtil.startCount(count, new CountdownUtil.CountDownCallback() {

            @Override
            public void onCountDowning(int count) {
                tv_send_code.setEnabled(false);
                tv_send_code.setText(getString(R.string.SDKCheckOtp_F0, count));
            }

            @Override
            public void onComplete() {
                tv_send_code.setText(getString(R.string.SDKCheckOtp_E0));
                tv_send_code.setEnabled(true);
            }
        });
    }

    private void stopCount() {
        if (countdownUtil != null) {
            countdownUtil.destroyTimer();
        }
    }

    /**
     * otp支付下单
     */
    private void orderPay() {
        LoadingDialog loadingDialog = showLoading();
        String payInfo = getPayInfo(codeEdit.getText().toString().trim());
        BcaSdk.bcaOTPOrder(mContext, sid, payEx, payInfo, channelID, new BcaOTPCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);
                if (TextUtils.equals(code, Constants.RET_CODE_SUCCESS)) {
                    if (from == Constants.LOCAL_FROM_PAY) {
                        queryPayResult();

                    } else if (from == Constants.LOCAL_FROM_TOP_UP) {
                        queryRechrPayResult();
                    }
                } else if (TextUtils.equals(code, Constants.RET_CODE_A010)) {
                    showNormalDialog(errorMsg);

                } else {
                    showToast(errorMsg);
                }
            }
        });
    }

    /**
     * 下单之后查询支付结果
     */
    private void queryPayResult() {
        LoadingDialog loadingDialog = showLoading();
        PayCashierMain.getInstance().payResultQuery(mContext, ot, tt, new PaymentSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);

                PayResultQueryResponse resp = PayResultQueryResponse.decodeJson(PayResultQueryResponse.class, jsonObject);
                if (TextUtils.equals(code, PayCashierSdk.LOCAL_PAY_SUCCESS) && resp != null
                        && TextUtils.equals(resp.payResult, ConstantsPayment.PAY_OK)) {
                    PayCashierMain.getInstance().onResultBack(code, errorMsg, jsonObject);

                } else {
                    checkErrorResult(code, errorMsg);
                }
            }
        });
    }

    /**
     * 下单之后查询充值支付结果
     */
    private void queryRechrPayResult() {
        LoadingDialog loadingDialog = showLoading();
        RechrCashierMain.getInstance().rechargePayResultQuery(mContext, ot, orderID, new RechrCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject, String OT, String orderID) {
                closeLoading(loadingDialog);

                RechrPayResultRsp resp = RechrPayResultRsp.decodeJson(RechrPayResultRsp.class, jsonObject);
                if (TextUtils.equals(code, PayCashierSdk.LOCAL_PAY_SUCCESS) && resp != null
                        && TextUtils.equals(resp.getRechargePayResult(), ConstantsPayment.PAY_OK)) {
                    // 查询支付结果成功 callback给业务层, 再查询相关业务结果
                    RechrCashierMain.getInstance().onResultBack(code, errorMsg, jsonObject, OT, ConstantsPayment.PAY_OK, orderID);
                    ActivityManager.getInstance().finishPayHubActivity();
                    finish();
                } else {
                    checkErrorResult(code, errorMsg);
                }
            }
        });
    }

    /**
     * 处理错误码
     *
     * @param code
     * @param errorMsg
     */
    private void checkErrorResult(String code, String errorMsg) {
        if (code.equals(Constants.RET_CODE_A203)/*支付密码错误*/
                || code.equals(Constants.RET_CODE_A206)/*密码被锁定*/
                || code.equals(Constants.RET_CODE_A010) /*账户被锁定*/) {
            showNormalDialog(errorMsg);
        } else if (code.equals(Constants.RET_CODE_5012)) {
            //支付方式请求支付时支付失败pay order error
            Intent intent = new Intent();
            intent.putExtra(Constants.MSG_KEY, errorMsg);
            setResult(PayHubActivity.RESULT_CODE_CLOSE_VERIFY_PIN_5012, intent);
            finish();
            onCloseLeftToRightActivity();
        } else {
            showToast(errorMsg);
        }
    }

    /**
     * 生成支付信息
     *
     * @param code
     * @return
     */
    private String getPayInfo(String code) {

        JSONObject object = new JSONObject();
        try {
            object.put("deviceId", AppGlobalUtil.getInstance().getBCADeviceID());
            object.put("userAgent", AppGlobalUtil.getInstance().getBCAUserAgent());
            if (!TextUtils.isEmpty(code)) {
                object.put("otp", code);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    @Override
    public void onBackPressed() {
        LogUtil.d(TAG, "onBackPressed");
        if (popUpWindow != null && popUpWindow.isShowing()) {
            popupWindowDismiss();
        } else {
            finish();
            onCloseLeftToRightActivity();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //监听返回键(有软键盘的情况下想关闭下拉框，需要拦截KeyEvent.KEYCODE_BACK)
        if (softInputShow && event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            softInputShow = false;
            if (popUpWindow != null && popUpWindow.isShowing()) {
                popupWindowDismiss();
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
