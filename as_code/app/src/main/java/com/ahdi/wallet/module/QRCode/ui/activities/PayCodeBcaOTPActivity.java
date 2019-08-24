package com.ahdi.wallet.module.QRCode.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
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
import com.ahdi.wallet.module.QRCode.bean.PayCodePINConfirmBean;
import com.ahdi.wallet.bca.callback.BcaOTPCallBack;
import com.ahdi.wallet.bca.response.BcaOTPRsp;
import com.ahdi.wallet.module.QRCode.QRCodeSdk;
import com.ahdi.wallet.module.QRCode.callback.QRCodeSdkCallBack;
import com.ahdi.wallet.app.schemas.PayOrderSchema;
import com.ahdi.wallet.app.schemas.VerifyCodeSchema;
import com.ahdi.lib.utils.base.BaseActivity;
import com.ahdi.lib.utils.bean.PhoneBean;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.CountdownUtil;
import com.ahdi.lib.utils.utils.DeviceUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.ToolUtils;
import com.ahdi.lib.utils.widgets.DeleteEditText;
import com.ahdi.lib.utils.widgets.PhoneListPopUpWindow;
import com.ahdi.lib.utils.widgets.dialog.CommonDialog;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.lib.utils.base.BaseEditTextWatcher;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * statusBar 和 主题效果 导致冲突, 修改为继承base
 *
 * @author xiaoniu
 * <p>
 * Pay码支付 bcaOTP
 */
public class PayCodeBcaOTPActivity extends BaseActivity {

    private static final String TAG = PayCodeBcaOTPActivity.class.getSimpleName();


    public static final String BCA_OTP_PAY_SUCCESS = "success";
    private static final String BCA_OTP_PAY_CANCEL = "cancel";
    private static final String BCA_OTP_PAY_TIME_OUT = "time_out";

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
    //键盘是否显示
    private boolean softInputShow = false;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 设置自定义主题从底部弹出, 否则, 在清单文件设置dialog主题之后, 界面打开之后再屏幕居中显示.
        setTheme(R.style.PayHubTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paycode_bca_otp);
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
        ot = intent.getStringExtra(Constants.LOCAL_OT_KEY);
        tt = intent.getStringExtra(Constants.LOCAL_TT_KEY);
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
            }
            phoneList = new ArrayList<>();
            PhoneBean phoneBean;
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
            LogUtil.e(TAG, "解析payParam出错：" + e.getMessage());
            finish();
        }
    }


    private void initView() {
        //title
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.PayQROtp_A0));
        ImageView btn_back = findViewById(R.id.btn_back);
        btn_back.setImageResource(R.drawable.selector_btn_title_back);
        btn_back.setOnClickListener(this);

        scrollView = findViewById(R.id.scrollView);

        //显示手机号码的区域
        ll_phone_area = findViewById(R.id.ll_phone_area);
        tv_phone_number = findViewById(R.id.tv_phone_number);
        findViewById(R.id.ll_select_phone).setOnClickListener(this);
        iv_pointer = findViewById(R.id.iv_pointer);

        //验证码区域
        codeEdit = findViewById(R.id.et_verify_code);
        tv_send_code = findViewById(R.id.tv_send_code);
        tv_send_code.setOnClickListener(this);
        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);

        //btn_submit
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

        } else if (id == R.id.btn_back) {
            closeSoftInput();
            onBackPressed();
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
        BcaSdk.bcaOTPSendVcode(mContext, GlobalApplication.getApplication().getSID(),
                phoneList.get(currentIndex).getMsisdnId(), payEx, payInfo, new BcaOTPCallBack() {
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
                tv_send_code.setText(getString(R.string.PayQROtp_F0, count));
            }

            @Override
            public void onComplete() {
                tv_send_code.setText(getString(R.string.PayQROtp_E0));
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
     * otp下单
     */
    private void orderPay() {
        LoadingDialog loadingDialog = showLoading();
        String payInfo = getPayInfo(codeEdit.getText().toString().trim());
        BcaSdk.bcaOTPOrder(mContext, GlobalApplication.getApplication().getSID(), payEx, payInfo, channelID, new BcaOTPCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);
                if (TextUtils.equals(code, Constants.RET_CODE_SUCCESS)) {
                    onClosed(BCA_OTP_PAY_SUCCESS);
                } else if (TextUtils.equals(code, Constants.RET_CODE_6013)) {
                    showPayFailDialog(errorMsg);
                } else if (TextUtils.equals(code, Constants.RET_CODE_A010)) {
                    showNormalDialog(errorMsg);
                } else {
                    showToast(errorMsg);
                }
            }
        });
    }

    private void showPayFailDialog(String msg) {
        new CommonDialog
                .Builder(mContext)
                .setMessage(msg)
                .setMessageCenter(true)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.DialogButton_B0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onClosed(BCA_OTP_PAY_TIME_OUT);
                        finish();
                        onCloseLeftToRightActivity();
                        dialog.dismiss();
                    }
                }).show();
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
            onClosed(BCA_OTP_PAY_CANCEL);
            payCancel();
            finish();
//            onCloseLeftToRightActivity();
            onBottom_out_Activity();
        }
    }

    /**
     * 取消支付的上报
     */
    private void payCancel() {
        PayCodePINConfirmBean bean = new PayCodePINConfirmBean();
        bean.setOT(ot);
        bean.setTT(tt);
        bean.setPayEx(payEx);
        bean.setCancel(1);
        QRCodeSdk.payCodePINConfirm(mContext, bean, Constants.PAY_AUTH_TYPE_PWD,
                GlobalApplication.getApplication().getSID(), new QRCodeSdkCallBack() {
                    @Override
                    public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                    }
                });
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

    /**
     * 返回到payCode页面
     *
     * @param payResult
     */
    private void onClosed(String payResult) {
        Intent intent = new Intent();
        intent.putExtra(Constants.LOCAL_RESULT_KEY, payResult);
        if (!TextUtils.isEmpty(tt)) {
            intent.putExtra(Constants.LOCAL_TT_KEY, tt);
        }
        if (!TextUtils.isEmpty(ot)) {
            intent.putExtra(Constants.LOCAL_OT_KEY, ot);
        }
        setResult(PayQRCodeActivity.RESULT_CLOSE_BCA_OTP, intent);
        finish();
        onCloseLeftToRightActivity();
    }

    private void showNormalDialog(String msg) {
        new CommonDialog
                .Builder(mContext)
                .setMessage(msg)
                .setMessageCenter(true)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.DialogButton_B0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
