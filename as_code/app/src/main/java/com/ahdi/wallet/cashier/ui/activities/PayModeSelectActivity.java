package com.ahdi.wallet.cashier.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.sdk.BankCardSdk;
import com.ahdi.wallet.cashier.PayCashierSdk;
import com.ahdi.wallet.cashier.RechrCashierSdk;
import com.ahdi.wallet.cashier.main.RechrCashierMain;
import com.ahdi.wallet.app.callback.BankCardSdkCallBack;
import com.ahdi.wallet.cashier.callback.PaymentSdkCallBack;
import com.ahdi.wallet.app.main.CashierPricing;
import com.ahdi.wallet.cashier.main.PayCashierMain;
import com.ahdi.wallet.cashier.response.pay.PricingResponse;
import com.ahdi.wallet.cashier.response.rechr.RechrListQueryRsp;
import com.ahdi.wallet.cashier.schemas.PayTypeSchema;
import com.ahdi.wallet.cashier.ui.adapter.CashierPayModeSelectAdapter;
import com.ahdi.wallet.app.utils.ConstantsPayment;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.listener.OnItemClickListener;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;

import org.json.JSONObject;

import java.util.List;

/**
 * 选择支付/充值方式列表界面
 */
public class PayModeSelectActivity extends PayBaseActivity {

    private static final String TAG = PayModeSelectActivity.class.getSimpleName();

    //title back  +  close
    private static final int RES_ID_BACK_CLOSE = R.drawable.selector_btn_title_close;
    private static final int RES_ID_BACK_BACK = R.drawable.selector_btn_title_back;

    public static final int TITLE_LEFT_ICON_CLOSE = 0;
    public static final int TITLE_LEFT_ICON_BACK = 1;

    public static final int RESULT_CODE_CLOSE_PAY_MODE = 3;

    private ImageView imageViewBack;
    private RecyclerView enableListView, blockListView;
    private TextView tv_card_balance_not_enough;
    private View ll_text_tips;

    private int mPosition;
    private String TT;
    private String errorMsg;
    private int close; // 0: 左上角显示关闭 X  1: 左上角显示返回 <
    private int height;
    private int from;
    private int touchFlag = -100;
    private Context mContext;
    private String rechrEdtAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        initIntentData(getIntent());

        setContentView(R.layout.dialog_activity_select_pay_mode);
        mContext = this;
        initTitleView();
        initView();
    }

    public void initTitleView() {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.SDKPayTypeList_A0));
        imageViewBack = findViewById(R.id.btn_back);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
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
        enableListView = findViewById(R.id.payType_list);
        blockListView = findViewById(R.id.payType_list_block);
        findViewById(R.id.layout_add_card_view).setOnClickListener(this);
        tv_card_balance_not_enough = findViewById(R.id.tv_card_balance_not_enough);
        ll_text_tips = findViewById(R.id.ll_text_tips);

        initErrorTips();

        initListData();
    }

    private void initIntentData(Intent intent) {
        if (intent == null) {
            return;
        }
        mPosition = intent.getIntExtra(Constants.LOCAL_ID_KEY, -1);
        TT = intent.getStringExtra(Constants.LOCAL_TT_KEY);
        errorMsg = intent.getStringExtra(Constants.MSG_KEY);
        height = intent.getIntExtra(Constants.LOCAL_HEIGHT_KEY, 0);
        from = intent.getIntExtra(Constants.LOCAL_FROM_KEY, -1);
        rechrEdtAmount = intent.getStringExtra("rechrEdtAmountKey");
    }

    /**
     * 初始化支付方式列表: 包括可用和不可用
     */
    public void initListData() {
        List<PayTypeSchema> enablePayTypes = CashierPricing.getInstance(mContext).getEnablePayTypes();
        if (enablePayTypes != null && enablePayTypes.size() > 0) {
            close = TITLE_LEFT_ICON_BACK;
        } else {
            close = TITLE_LEFT_ICON_CLOSE;
        }
        // 首先初始化可用支付方式列表, 根据列表的size是否大于0, 设置title的左边icon X 或者 <
        initTitleLeftBtn();

        // 解决ScrollView嵌套RecyclerView滑动卡顿问题
        enableListView.setLayoutManager(new LinearLayoutManager(mContext) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        CashierPayModeSelectAdapter enableAdapter = new CashierPayModeSelectAdapter(mContext, enablePayTypes, mPosition);
        enableListView.setAdapter(enableAdapter);
        enableAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mPosition = position;
                onBack();
            }
        });


        List<PayTypeSchema> blockPayTypes = CashierPricing.getInstance(mContext).getUnablePayTypes();
        // 解决ScrollView嵌套RecyclerView滑动卡顿问题
        blockListView.setLayoutManager(new LinearLayoutManager(mContext) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        CashierPayModeSelectAdapter blockAdapter = new CashierPayModeSelectAdapter(mContext, blockPayTypes, mPosition);
        blockListView.setAdapter(blockAdapter);
        blockAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mPosition = position;
                onBack();
            }
        });
    }

    /**
     * 初始化title左上角按钮图片
     */
    private void initTitleLeftBtn() {
        if (close == TITLE_LEFT_ICON_CLOSE) {
            imageViewBack.setImageResource(RES_ID_BACK_CLOSE);
        } else {
            imageViewBack.setImageResource(RES_ID_BACK_BACK);
        }
    }

    /**
     * 初始化错误提示文案
     */
    private void initErrorTips() {
        if (!TextUtils.isEmpty(errorMsg)) {
            tv_card_balance_not_enough.setText(errorMsg);
            ll_text_tips.setVisibility(View.VISIBLE);
        } else {
            ll_text_tips.setVisibility(View.GONE);
        }
    }

    private void initRootViewHeight() {
        RelativeLayout bg_view = findViewById(R.id.bg_view);
        ViewGroup.LayoutParams layoutParams = bg_view.getLayoutParams();
        layoutParams.height = height;
        bg_view.setLayoutParams(layoutParams);
        bg_view.setBackgroundResource(R.drawable.bg_dialog_payhub_title);
    }

    @Override
    public void onClick(View view) {
        if (!isCanClick()) {
            return;
        }
        if (view.getId() == R.id.layout_add_card_view) {
            // 绑卡流程
            onBindBankCard();
        }
    }

    /**
     * 绑定银行卡
     */
    private void onBindBankCard() {
        BankCardSdk.bindCard(this, getSid(), new BankCardSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                if (TextUtils.equals(code, BankCardSdk.LOCAL_PAY_SUCCESS)) {
                    LogUtil.e(TAG, "------绑定银行卡成功, 重新批价/重新查询支付方式------");
                    if (from == Constants.LOCAL_FROM_PAY) {
                        // 重新批价
                        rePricing();

                    } else if (from == Constants.LOCAL_FROM_TOP_UP) {
                        // 重新查询支付方式
                        reQueryRechrType();
                    }
                } else {
                    LogUtil.e(TAG, "code: " + code);
                }
            }
        });
    }

    private String getSid() {
        String sid = "";
        if (from == Constants.LOCAL_FROM_PAY) {
            sid = PayCashierMain.getInstance().sid;

        } else if (from == Constants.LOCAL_FROM_TOP_UP) {
            sid = RechrCashierMain.getInstance().sid;
        }
        return sid;
    }

    /**
     * 绑卡成功之后 重新批价
     */
    private void rePricing() {
        if (TextUtils.isEmpty(TT)) {
            LogUtil.e(TAG, "rePricing TT = null");
            return;
        }
        LoadingDialog loadingDialog = showLoading();
        PayCashierMain.getInstance().restartPay(mContext, TT, new PaymentSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);
                if (TextUtils.equals(PayCashierSdk.LOCAL_PAY_SUCCESS, code)) {
                    PricingResponse resp = PricingResponse.decodeJson(PricingResponse.class, jsonObject);
                    // 刷新支付方式列表数据
                    CashierPricing.getInstance(mContext).notifyPricingResponse(resp);
                    mPosition = 0;
                    // 更新支付方式列表和标题栏左边按钮
                    initListData();
                    if (resp != null) {
                        touchFlag = resp.touchFlag;
                    }
                } else {
                    PayCashierMain.getInstance().onResultBack(mContext, code, errorMsg, jsonObject, ConstantsPayment.PayResult_OTHER);
                    onExitPayHub(errorMsg);
                }
            }
        });
    }

    /**
     * 重新查询支付方式
     */
    private void reQueryRechrType() {
        if (TextUtils.isEmpty(rechrEdtAmount)) {
            LogUtil.e(TAG, "rechrEdtAmount is null");
            return;
        }
        LoadingDialog loadingDialog = showLoading();
        RechrCashierMain.getInstance().onReQueryRechrMode(mContext, rechrEdtAmount, true, new RechrCashierMain.OnReTopUpCallback() {

            @Override
            public void onCallback(String retCode, String errorMsg, JSONObject json) {
                closeLoading(loadingDialog);
                RechrListQueryRsp resp = RechrListQueryRsp.decodeJson(RechrListQueryRsp.class, json);
                if (TextUtils.equals(retCode, RechrCashierSdk.LOCAL_PAY_SUCCESS)) {
                    CashierPricing.getInstance(mContext).notifyQueryChargeListResponse(resp);
                    initListData();
                    mPosition = 0;
                    if (resp != null) {
                        touchFlag = resp.TouchFlag;
                    }
                } else {
                    RechrCashierMain.getInstance().onResultBack(retCode, errorMsg, null, "", ConstantsPayment.PayResult_OTHER, "");
                    onExitPayHub(errorMsg);
                }
            }
        });
    }

    private void onBack() {
        Intent intent = new Intent();
        if (close == TITLE_LEFT_ICON_BACK) {
            intent.putExtra(Constants.LOCAL_POSITION_KEY, mPosition);
            intent.putExtra("TouchFlag", touchFlag);
            setResult(RESULT_CODE_CLOSE_PAY_MODE, intent);
            finish();
            onCloseLeftToRightActivity();

        } else if (close == TITLE_LEFT_ICON_CLOSE) {
            if (from == Constants.LOCAL_FROM_PAY) {
                PayCashierMain.getInstance().onResultBack(mContext, PayCashierSdk.LOCAL_PAY_USER_CANCEL,
                        PayCashierSdk.USER_CANCEL, null, ConstantsPayment.PayResult_OTHER);

            } else if (from == Constants.LOCAL_FROM_TOP_UP) {
                RechrCashierMain.getInstance().onResultBack(RechrCashierSdk.LOCAL_PAY_USER_CANCEL,
                        RechrCashierSdk.USER_CANCEL, null, "", ConstantsPayment.PayResult_OTHER, "");
            }
            ActivityManager.getInstance().finishPayHubActivity();
            finish();
            onBottom_out_Activity();
        }
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    private void onExitPayHub(String errorMsg) {
        ActivityManager.getInstance().finishPayHubActivity();
        LogUtil.e(TAG, "errorMsg: " + errorMsg);
        finish();
        onBottom_out_Activity();
    }

}
