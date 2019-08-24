package com.ahdi.wallet.app.ui.base;

import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.ConfigCountry;

/**
 * Date: 2018/5/28 下午8:28
 * Author: kay lau
 * Description:
 */
public abstract class EndingBaseActivity extends AppBaseActivity implements View.OnClickListener {

    protected static final int TYPE_0 = 0; // ending页面展示, 仅校验是否为空
    protected static final int TYPE_1 = 1; // ending页面展示, 校验是否为空 是否为0
    protected static final int TYPE_2 = 2; // ending页面展示, 校验是否为空 是否为0, 金额前边加"-", 用于展示优惠金额

    protected String errMsg;
    protected String amt;             // 实际支付金额
    protected String payResult;       // 支付结果
    protected String merName;         // 商户名称
    protected String payTypeName;     // 支付方式
    protected String price;           // 商品原价
    protected String tipFee;          // 小费
    protected String serviceFee;      // 平台手续费
    protected String voucher;         // 营销抵扣金额
    protected String symbol = ConfigCountry.KEY_CURRENCY_SYMBOL;

    protected ImageView iv_ending_state;

    protected TextView tv_ending_amount, tv_ending_state, tv_ending_pay_type, tv_ending_mer_name,
            tv_ending_price, tv_ending_tips_fee, tv_ending_service_fee, tv_ending_voucher;

    protected View ending_result, ending_exception;

    protected LinearLayout ll_mer_name, ll_pay_type, ll_bottom_info, ll_price, ll_tips, ll_service_fee, ll_voucher;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }

}
