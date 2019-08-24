package com.ahdi.wallet.module.QRCode.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahdi.lib.utils.base.BaseActivity;
import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.AmountUtil;
import com.ahdi.lib.utils.widgets.keyboard.KeyboardUtil;
import com.ahdi.wallet.R;

/**
 * statusBar 和 主题效果 导致冲突, 修改为继承base
 * 收款码设置金额弹层
 */
public class ReceiveSetAmountActivity extends BaseActivity {

    private EditText et_receive_amount;
    private TextView btn_confirm; // 金额确认按钮
    private String textResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 设置自定义主题从底部弹出, 否则, 在清单文件设置dialog主题之后, 界面打开之后再屏幕居中显示.
        setTheme(R.style.PayHubTheme);
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.dialog_activity_receive_set_amount);
        initTitleView();
        initView();
    }

    private void initView() {
        et_receive_amount = findViewById(R.id.et_receive_amount);
        btn_confirm = findViewById(R.id.btn_setAmount_confirm);
        btn_confirm.setOnClickListener(this);

        ((TextView) findViewById(R.id.tv_rp)).setText(ConfigCountry.KEY_CURRENCY_SYMBOL);

        updateView("");

        initKeyBoardView();
    }

    public void initTitleView() {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.ReceiveQRInputPrice_A0));
        ImageView imageViewBack = findViewById(R.id.btn_back);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCanClick()) {
                    return;
                }
                finish();
            }
        });

    }

    public void initKeyBoardView() {
        new KeyboardUtil(this, et_receive_amount, 1, new KeyboardUtil.CallBack() {
            @Override
            public void onCallback(String result) {
                int amountLength = result.replace(ConfigCountry.AMOUNT_FORMAT_SYMBOL, "").length();
                if (amountLength <= ConfigCountry.LIMIT_AMOUNT_LENGTH) {
                    textResult = result.toString();
                    et_receive_amount.setText(textResult);
                } else {
                    et_receive_amount.setText(textResult);
                }
                updateView(textResult);
            }

            @Override
            public void onPwdInputCallback(StringBuffer buffer, String pwdstr) {

            }

            @Override
            public void onPwdStrCallback(StringBuffer stringBuffer) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!isCanClick()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.btn_setAmount_confirm) {
            String amount = et_receive_amount.getText().toString();
            if (!TextUtils.isEmpty(amount) && AmountUtil.checkAmount(amount)) {
                Intent intent = new Intent();
                intent.putExtra(Constants.LOCAL_AMOUNT_KEY, amount);
                setResult(ReceiveActivity.RESULT_CLOSE_SET_AMOUNT, intent);
                finish();
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        onBottom_out_Activity();
    }

    //用于控制 btn 、 edittext右侧删除按钮 显隐
    public void updateView(String editString) {
        if (TextUtils.isEmpty(editString)) {
            btn_confirm.setEnabled(false);
        } else {
            btn_confirm.setEnabled(true);
        }
    }

}