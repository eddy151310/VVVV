package com.ahdi.wallet.app.ui.fragments;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahdi.wallet.app.sdk.IDVerifySdk;
import com.ahdi.wallet.app.callback.IDVerifyCallBack;
import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.lib.utils.utils.AmountUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.wallet.R;
import com.ahdi.lib.utils.imagedown.ImageDownUtil;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.wallet.app.ui.activities.balance.BalanceActivity;
import com.ahdi.wallet.app.ui.activities.bankAccount.BankAccountActivity;
import com.ahdi.wallet.app.ui.activities.bankCard.BankCardsActivity;
import com.ahdi.wallet.app.ui.activities.userInfo.ProfileActivity;
import com.ahdi.wallet.app.ui.activities.payPwd.PayPwdGuideSetActivity;
import com.ahdi.wallet.app.ui.activities.other.SettingsActivity;
import com.ahdi.lib.utils.base.BaseFragment;
import com.ahdi.lib.utils.widgets.CheckSafety;
import com.ahdi.wallet.app.ui.activities.voucher.VoucherListActivity;

import org.json.JSONObject;

/**
 * @author admin
 */
public class MeFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = MeFragment.class.getSimpleName();

    /**
     * 头像
     */
    private ImageView iv_me_user_photo;
    /**
     * 姓名和手机号码
     */
    private TextView tv_me_name, tv_me_phone_number;

    /**
     * 余额
     */
    private TextView balance_content;
    /**
     * 银行卡
     */
    private TextView bank_cards_content;
    /**
     * 银行账户
     */
    private TextView bank_account_content;
    /**
     * 身份验证
     */
    private ImageView iv_reg_state;

    private IDCallBack idVerifyCallBack = null;
    private View layout_id_verify;
    private View layout_bank_account;


    @Override
    protected View initRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.layout_fragment_me, container, false);
    }

    @Override
    protected void initView(View view) {
        //初始化title控件
        View titleView = view.findViewById(R.id.title);
        titleView.setBackgroundResource(R.color.CC_FFFFFF);
        TextView tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.Me_A0));
        view.findViewById(R.id.btn_back).setVisibility(View.INVISIBLE);

        // 个人信息
        iv_me_user_photo = view.findViewById(R.id.iv_me_user_photo);
        tv_me_name = view.findViewById(R.id.tv_me_name);
        tv_me_phone_number = view.findViewById(R.id.tv_me_phone_number);
        view.findViewById(R.id.rl_user_info).setOnClickListener(this);

        // 余额
        View layout_balance = view.findViewById(R.id.layout_balance);
        ((ImageView) layout_balance.findViewById(R.id.iv_icon)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.me_icon_balance));
        ((TextView) layout_balance.findViewById(R.id.item_title)).setText(getString(R.string.Me_B0));
        balance_content = layout_balance.findViewById(R.id.item_content);
        layout_balance.setOnClickListener(this);

        // 银行卡
        View layout_bank_cards = view.findViewById(R.id.layout_bank_cards);
        ((ImageView) layout_bank_cards.findViewById(R.id.iv_icon)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.me_icon_bankcards));
        ((TextView) layout_bank_cards.findViewById(R.id.item_title)).setText(getString(R.string.Me_C0));
        bank_cards_content = layout_bank_cards.findViewById(R.id.item_content);
//        layout_bank_cards.findViewById(R.id.me_item_line).setVisibility(View.GONE);
        layout_bank_cards.setOnClickListener(this);

        // 银行账户
        layout_bank_account = view.findViewById(R.id.layout_bank_account);
        bank_account_content = layout_bank_account.findViewById(R.id.item_content);

        // 身份验证
        layout_id_verify = view.findViewById(R.id.layout_id_verify);
        iv_reg_state = layout_id_verify.findViewById(R.id.iv_reg_state);

        //优惠券
        View layout_id_voucher = view.findViewById(R.id.layout_id_voucher);
        ((ImageView) layout_id_voucher.findViewById(R.id.iv_icon)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.me_icon_coupon));
        ((TextView) layout_id_voucher.findViewById(R.id.item_title)).setText(getString(R.string.Me_J0));
        layout_id_voucher.findViewById(R.id.me_item_line).setVisibility(View.GONE);
        layout_id_voucher.setOnClickListener(this);

        // 设置
        View layout_setting = view.findViewById(R.id.layout_setting);
        ((ImageView) layout_setting.findViewById(R.id.iv_icon)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.me_icon_setting));
        ((TextView) layout_setting.findViewById(R.id.item_title)).setText(getString(R.string.Me_F0));
        ((TextView) layout_setting.findViewById(R.id.item_content)).setText(getString(R.string.Me_G0));
        layout_setting.findViewById(R.id.me_item_line).setVisibility(View.GONE);
        layout_setting.setOnClickListener(this);
    }

    @Override
    protected void initData(View view) {
        onRefreshData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onRefreshData() {
        if (mActivity == null) {
            LogUtil.e(TAG, "mActivity is null");
            return;
        }

        refreshRNA();

        refreshWithdraw();

        balance_content.setText(AmountUtil.getFormatAmount(ConfigCountry.KEY_CURRENCY_SYMBOL, ProfileUserUtil.getInstance().getAccountBalance()));
        int cardNum = ProfileUserUtil.getInstance().getCardNum();
        String cardNumStr = "";
        if (cardNum > 0) {
            cardNumStr = mActivity.getString(R.string.ME_H0, String.valueOf(cardNum));
        }
        bank_cards_content.setText(cardNumStr);
        String accNum = "";
        if (ProfileUserUtil.getInstance().getAccNum() > 0) {
            accNum = mActivity.getString(R.string.Me_I0, String.valueOf(ProfileUserUtil.getInstance().getAccNum()));
        }
        bank_account_content.setText(accNum);
        if (ProfileUserUtil.getInstance().isRNA()) {
            iv_reg_state.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.id_auth_registered));
        } else {
            iv_reg_state.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.id_auth_unregistered));
        }
        if (ProfileUserUtil.getInstance().getUserData() != null) {
            tv_me_name.setText(ProfileUserUtil.getInstance().getUserData().getNName());
            tv_me_phone_number.setText(ProfileUserUtil.getInstance().getUserData().getsLName());
            ImageDownUtil.downMySelfPhoto(mActivity, ProfileUserUtil.getInstance().getUserData().getAvatar(), iv_me_user_photo);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onResume();
        }
    }


    @Override
    public void onClick(View v) {
        if (!isCanClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.rl_user_info:
                goToOtherActivity(ProfileActivity.class);
                break;
            case R.id.layout_balance:
                goToOtherActivity(BalanceActivity.class);
                break;
            case R.id.layout_bank_cards:
                goToOtherActivity(BankCardsActivity.class);
                break;
            case R.id.layout_bank_account: // 提现账户
                goToOtherActivity(BankAccountActivity.class);
                break;
            case R.id.layout_id_verify:
                if (ProfileUserUtil.getInstance().isRnaSw()) {
                    checkIDStatus();
                }
                break;
            case R.id.layout_id_voucher:
                goToOtherActivity(VoucherListActivity.class);
                break;
            case R.id.layout_setting:
                goToOtherActivity(SettingsActivity.class);
                break;
            default:
                break;
        }
    }

    /**
     * 跳转到其他activity
     *
     * @param cls
     */
    private void goToOtherActivity(Class<?> cls) {
        if (cls == null) {
            return;
        }
        if (cls == BankAccountActivity.class || cls == BankCardsActivity.class) {
            // 检查是否需要引导设置支付密码
            if (CheckSafety.checkSafe(mActivity, PayPwdGuideSetActivity.class)) {
                Intent intent = new Intent(mActivity, cls);
                startActivity(intent);
            }
        }else{
            Intent intent = new Intent(mActivity, cls);
            startActivity(intent);
        }

    }

    private void checkIDStatus() {
        if (idVerifyCallBack == null) {
            idVerifyCallBack = new IDCallBack();
        }
        LoadingDialog dialog = showLoading();
        idVerifyCallBack.setDialog(dialog);
        AppMain.getInstance().auditQR(mActivity, true, idVerifyCallBack);
    }

    /**
     * 实名认证结果查询
     */
    private class IDCallBack implements IDVerifyCallBack {
        private LoadingDialog dialog = null;

        public void setDialog(LoadingDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void onResult(String code, String errorMsg, JSONObject jsonObject) {
            closeLoading(dialog);
            if (TextUtils.equals(code, IDVerifySdk.LOCAL_SUCCESS)) {
                ProfileUserUtil.getInstance().setRNA(true);

            } else if (!TextUtils.equals(code, IDVerifySdk.LOCAL_USER_CANCEL) && !TextUtils.isEmpty(errorMsg)) {
                ToastUtil.showToastShort(mActivity, errorMsg);
            }
        }
    }

    /**
     * 根据用户概要接口响应, 是否显示实名认证入口
     */
    private void refreshRNA() {
        if (ProfileUserUtil.getInstance().isRnaSw()) {
            layout_id_verify.setVisibility(View.VISIBLE);
            ((ImageView) layout_id_verify.findViewById(R.id.iv_icon)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.me_icon_identity_verification));
            ((TextView) layout_id_verify.findViewById(R.id.item_title)).setText(getString(R.string.Me_E0));
            layout_id_verify.findViewById(R.id.item_content).setVisibility(View.GONE);
            layout_id_verify.findViewById(R.id.me_item_line).setVisibility(View.GONE);
            iv_reg_state.setVisibility(View.VISIBLE);
            layout_id_verify.setOnClickListener(this);
        } else {
            layout_id_verify.setVisibility(View.GONE);
        }
    }

    /**
     * 根据用户概要接口响应, 是否显示提现账户入口
     */
    private void refreshWithdraw() {
        if (ProfileUserUtil.getInstance().isWithdraw()) {
            layout_bank_account.setVisibility(View.VISIBLE);
            ((ImageView) layout_bank_account.findViewById(R.id.iv_icon)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.me_icon_bankaccount));
            ((TextView) layout_bank_account.findViewById(R.id.item_title)).setText(getString(R.string.Me_D0));
            layout_bank_account.setOnClickListener(this);
        } else {
            layout_bank_account.setVisibility(View.GONE);
        }
    }
}