package com.ahdi.wallet.module.payment.transfer.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.NumberKeyListener;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ahdi.wallet.app.sdk.IDVerifySdk;
import com.ahdi.wallet.app.callback.IDVerifyCallBack;
import com.ahdi.wallet.module.payment.transfer.TransferSdk;
import com.ahdi.wallet.module.payment.transfer.bean.TABean;
import com.ahdi.wallet.module.payment.transfer.callback.TransferResultCallBack;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.imagedown.ImageDownUtil;
import com.ahdi.lib.utils.utils.AmountUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.lib.utils.utils.ToolUtils;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.lib.utils.base.BaseEditTextWatcher;

import org.json.JSONObject;

public class TransPayConfirmActivity extends AppBaseActivity {

    public static final String TAG = TransPayConfirmActivity.class.getSimpleName();

    private Context mContext;
    private EditText edt_setNum;
    private EditText edt_remark;
    private String nickName;
    private String loginName;
    private String ut;
    private String avatar; // 用户头像
    private String amount;
    private ImageView iv_logo;
    private TextView tv_name;
    private TextView tv_account;
    private View btn_confirm;
    private ScrollView scrollview_top;
    /**
     * 确定按钮在屏幕中的位置
     */
    private int confirmBtnWindowHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        initIntentData(getIntent());

        setContentView(R.layout.activity_transfer_confirm);
        getWindow().setBackgroundDrawableResource(R.color.CC_F1F2F6); //代码设置bg 防止键盘弹起 闪黑屏
        ActivityManager.getInstance().addCommonActivity(this);

        mContext = this;
        initCommonTitle(getString(R.string.TransferConfirm_A0));
        initView();

        initData();
        initSoftKeyboard();
    }

    private void initData() {
        tv_account.setText(loginName);
        ImageDownUtil.downOtherUserPhotoImage(this, avatar, iv_logo);

        if (!TextUtils.isEmpty(nickName)) {
            tv_name.setText(nickName);
        } else {
            tv_name.setVisibility(View.GONE);
        }
        updateView(amount);
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_dollar)).setText(ConfigCountry.KEY_CURRENCY_SYMBOL);
        btn_confirm = findViewById(R.id.btn_scan_collect_confirm);
        btn_confirm.setOnClickListener(this);
        scrollview_top = findViewById(R.id.scrollview_top);
        btn_confirm.post(new Runnable() {
            @Override
            public void run() {
                int[] locations = new int[2];
                btn_confirm.getLocationInWindow(locations);
                confirmBtnWindowHeight = locations[1] + btn_confirm.getHeight() + ToolUtils.dip2px(mContext, 10);
            }
        });
        tv_name = findViewById(R.id.tv_nick_name);
        tv_account = findViewById(R.id.tv_account);
        edt_setNum = findViewById(R.id.et_receive_amount);
        edt_remark = findViewById(R.id.edt_add_remark);
        iv_logo = findViewById(R.id.iv_logo);
        edt_remark.setKeyListener(new NumberKeyListener() {
            @NonNull
            @Override
            protected char[] getAcceptedChars() {
                return Constants.COMMON_ACCEPT_CHAR.toCharArray();
            }

            @Override
            public int getInputType() {
                return InputType.TYPE_CLASS_TEXT;
            }
        });
        initEditAmount();
    }

    private void initEditAmount() {
        if (!TextUtils.isEmpty(amount)) {
            if (AmountUtil.checkAmount(amount)) {
                edt_setNum.setText(AmountUtil.formatAmount(amount));
                edt_setNum.setFocusable(false);
                edt_setNum.setEnabled(false);
            } else {
                amount = "";
                LogUtil.d(TAG, "金额返回有问题：" + amount);
            }
        }

        int len = ConfigCountry.LIMIT_AMOUNT_LENGTH / 3 + ConfigCountry.LIMIT_AMOUNT_LENGTH;
        edt_setNum.setFilters(new InputFilter[]{new InputFilter.LengthFilter(len)});
        edt_setNum.addTextChangedListener(new BaseEditTextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = edt_setNum.getText().toString();
                String amount = AmountUtil.unFormatString(text);
                if (!TextUtils.isEmpty(amount)) {
                    btn_confirm.setEnabled(true);
                } else {
                    btn_confirm.setEnabled(false);
                }
                edt_setNum.setSelection(text.length());
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                int len = s.toString().length();
                if (len == 1 && text.equals("0")) {
                    s.clear();
                }
            }
        });
    }

    private void initIntentData(Intent intent) {
        if (intent != null) {
            nickName = intent.getStringExtra(Constants.LOCAL_NNAME_KEY);
            loginName = intent.getStringExtra(Constants.LOCAL_SNAME_KEY);
            amount = intent.getStringExtra(Constants.LOCAL_AMOUNT_KEY);
            ut = intent.getStringExtra(Constants.LOCAL_UT_KEY);
            avatar = intent.getStringExtra(Constants.LOCAL_AVATAR_KEY);
        }
    }

    @Override
    public void onClick(View view) {
        if (!isCanClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.btn_scan_collect_confirm:
                closeSoftInput();
                amount = edt_setNum.getText().toString();
                if (!AmountUtil.checkAmount(amount)) {
                    showToast(getString(R.string.Toast_G0));
                    return;
                }
                // 实名认证开关已开启并且需要实名认证
                if (ProfileUserUtil.getInstance().isRnaSw()
                        && ProfileUserUtil.getInstance().isNeedRNA()) {
                    checkIDStatus();
                } else {
                    transfer();
                }
                break;
            default:
                break;
        }
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
                    transfer();
                } else if (!TextUtils.equals(code, IDVerifySdk.LOCAL_USER_CANCEL) && !TextUtils.isEmpty(errorMsg)) {
                    ToastUtil.showToastShort(mContext, errorMsg);
                }
            }
        });
    }

    private void transfer() {
        // 转账
        String rmark = edt_remark.getText().toString().trim();
        toTransfer(AmountUtil.unFormatString(amount), ut, rmark);
    }

    /**
     * @param amount 转账金额
     * @param target uid
     * @param rmark  备注信息
     */
    private void toTransfer(String amount, String target, String rmark) {
        TABean taBean = new TABean(AmountUtil.unFormatString(amount), target, rmark);
        LoadingDialog loadingDialog = showLoading();
        AppMain.getInstance().onTransfer(this, taBean, GlobalApplication.getApplication().getSID(), new TransferResultCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject, String ID) {
                closeLoading(loadingDialog);
                if (TransferSdk.LOCAL_PAY_SUCCESS.equals(code) && !TextUtils.isEmpty(ID)) {
                    showResultActivity(ID, "");

                } else if (TextUtils.equals(code, TransferSdk.LOCAL_PAY_QUERY_CANCEL)) {
                    showResultActivity("", getString(R.string.TransferEnding_M0));

                } else if (TransferSdk.LOCAL_PAY_USER_CANCEL.equals(code)) {
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
     * 跳到展示结果页
     */
    private void showResultActivity(String ID, String errorMsg) {
        Intent intent = new Intent(this, EndingTransferActivity.class);
        intent.putExtra(Constants.LOCAL_ID_KEY, ID);
        intent.putExtra(Constants.MSG_KEY, errorMsg);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityManager.getInstance().removeCommonActivity(this);
    }

    //用于控制 btn 、 edittext右侧删除按钮 显隐
    public void updateView(String editString) {
        if (TextUtils.isEmpty(editString)) {
            btn_confirm.setEnabled(false);
        } else {
            btn_confirm.setEnabled(true);
        }
    }

    private void initSoftKeyboard() {
        ViewTreeObserver viewTreeObserver = edt_setNum.getViewTreeObserver();
        if (viewTreeObserver != null) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    removeViewTreeObserver(viewTreeObserver, this);
                    Rect r = new Rect();
                    edt_setNum.getWindowVisibleDisplayFrame(r);
                    int screenHeight = edt_setNum.getRootView().getHeight();
                    int heightDifference = screenHeight - (r.bottom);

                    if (heightDifference > 200) {
                        LogUtil.d(TAG, "confirmBtnWindowHeight =  " + confirmBtnWindowHeight + ",r.bottom = " + r.bottom);
                        if (confirmBtnWindowHeight > r.bottom) {
                            scrollview_top.scrollTo(0, confirmBtnWindowHeight - r.bottom);
                        }
                        LogUtil.d(TAG, "heightDifference = " + heightDifference + ", confirmBtnWindowHeight =  " + confirmBtnWindowHeight + ",getScrollY =  " + scrollview_top.getScrollY());
                    } else {
                        scrollview_top.scrollTo(0, 0);
                    }
                }
            });
        }
    }
}
