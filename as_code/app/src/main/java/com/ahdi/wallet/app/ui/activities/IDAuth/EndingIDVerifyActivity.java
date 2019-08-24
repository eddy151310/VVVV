package com.ahdi.wallet.app.ui.activities.IDAuth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.sdk.IDVerifySdk;
import com.ahdi.wallet.app.main.IDVerifySdkMain;
import com.ahdi.wallet.app.schemas.PaySdkTipsSchema;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;

/**
 * @author zhaohe@iapppay.com
 * @date 2018/5/18.
 * 身份证信息提交成功页面
 */

public class EndingIDVerifyActivity extends AppBaseActivity implements View.OnClickListener{
    private static final String TAG = "EndingIDVerifyActivity";

    public static final String PAGE_TYPE = "PAGE_TYPE";
    /**
     * 1：审核通过2：审核未通过3：审核中
     */
    private int pageType = -1;
    private PaySdkTipsSchema prompt = null;
    private Button btnSure;
    private TextView tv_result,tv_result_tip;
    private ImageView iv_result;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.activity_ending_id_verify);
        initIntent();
        initTitle();
        initView();
        initData();
    }

    private void initIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            pageType = intent.getIntExtra(PAGE_TYPE, -1);
            prompt = (PaySdkTipsSchema) intent.getSerializableExtra(Constants.LOCAL_PROMPT_KEY);
        }
    }

    void initTitle(){
        initTitleTextView(getString(R.string.IdEnding_A0));
        initTitleBack().setVisibility(View.INVISIBLE);
    }

    private void initView() {
        iv_result = findViewById(R.id.iv_result);
        tv_result = findViewById(R.id.tv_result);
        tv_result_tip = findViewById(R.id.tv_result_tip);
        btnSure = findViewById(R.id.btn_sure);
        btnSure.setOnClickListener(this);
    }


    private void initData() {
        if (prompt != null) {
            tv_result.setText(prompt.title);
            tv_result_tip.setText(prompt.body);
        }
    }

    @Override
    public void onClick(View v) {
        if (!isCanClick()){
            return;
        }
        int id = v.getId();
        if (id == R.id.btn_sure){
            IDVerifySdkMain.getInstance().onResultBack(IDVerifySdk.LOCAL_WAIT,"",null);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        //屏蔽返回键
    }
}
