package com.ahdi.wallet.app.ui.activities.other;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.LanguageUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.wallet.R;

/**
 * 选择app内语言
 * @author zhaohe
 */
public class LanguageActivity extends AppBaseActivity {
    private static final String TAG = "LanguageActivity";

    private int from_activity  = Constants.LOCAL_LANGUAGE_FROM_LOGIN;

    private RelativeLayout rl_language_en, rl_language_in;
    private ImageView iv_en, iv_in;
    private Button btn_save;

    /**之前设置的语言*/
    private String lastLanguage;
    /**当前选中的语言*/
    private String selectLanguage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.activity_language);
        initCommonTitle(getString(R.string.SettingLanguage_A0));
        initView();
        initData();
    }

    public void initView() {
        //保存按钮
        btn_save = findViewById(R.id.btn_next);
        btn_save.setOnClickListener(this);
        btn_save.setVisibility(View.VISIBLE);
        btn_save.setText(R.string.SettingLanguage_D0);
        btn_save.setTextColor(getResources().getColor(R.color.CC_BEBEC0));
        btn_save.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.text_size_17));
        btn_save.setBackgroundResource(R.color.CC_00000000);
        RelativeLayout.LayoutParams layoutParams;
        layoutParams = (RelativeLayout.LayoutParams) btn_save.getLayoutParams();
        layoutParams.setMargins(0, 0, getResources().getDimensionPixelSize(R.dimen.dp_10), 0);
        btn_save.setLayoutParams(layoutParams);
        btn_save.setEnabled(false);

        rl_language_en = findViewById(R.id.rl_language_en);
        iv_en = findViewById(R.id.iv_en);
        rl_language_in = findViewById(R.id.rl_language_in);
        iv_in = findViewById(R.id.iv_in);
        rl_language_en.setOnClickListener(this);
        rl_language_in.setOnClickListener(this);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null){
            from_activity = intent.getIntExtra(Constants.LOCAL_SETTING_LANGUAGE_FROM_ACTIVITY, Constants.LOCAL_LANGUAGE_FROM_LOGIN);
        }
        String lastLanguage = LanguageUtil.getLanguage(LanguageActivity.this);
        refreshRightIcon(lastLanguage);
    }

    private void refreshRightIcon(String language) {
        selectLanguage = language;
        if (Constants.LOCAL_LAN_EN.equals(language)) {
            iv_en.setVisibility(View.VISIBLE);
            iv_in.setVisibility(View.INVISIBLE);
        } else if (Constants.LOCAL_LAN_ID.equals(language)) {
            iv_en.setVisibility(View.INVISIBLE);
            iv_in.setVisibility(View.VISIBLE);
        }else {
            iv_en.setVisibility(View.INVISIBLE);
            iv_in.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_next) {
            save();
        }else if (id == R.id.rl_language_en){
           selectEN();
        }else if (id == R.id.rl_language_in){
            selectIN();
        }
    }

    private void save() {
        if (!isCanClick()) {
            return;
        }
        LogUtil.d(TAG, "点击保存按钮 当前设置语言为：" + selectLanguage);
        //如果选中的语言不为空并且与之前的语言不同，正常处理。否则之间关闭
        if (!TextUtils.isEmpty(selectLanguage) && !TextUtils.equals(lastLanguage, selectLanguage)){

            LanguageUtil.setLanguage(LanguageActivity.this, selectLanguage);
            if (from_activity == Constants.LOCAL_LANGUAGE_FROM_LOGIN){
                setResult(RESULT_OK,null);
            }else if (from_activity == Constants.LOCAL_LANGUAGE_FROM_SETTING){
                LanguageUtil.switchLanguageState(LanguageActivity.this);
            }
        }
        AppGlobalUtil.getInstance().initContext(this);
        ToastUtil.showToastShort(getApplication(),AppGlobalUtil.getInstance().getContext().getString(R.string.Toast_C0));
        finish();
    }

    private void selectEN() {
        if(TextUtils.equals(selectLanguage, Constants.LOCAL_LAN_EN)){
            return;
        }
        refreshRightIcon(Constants.LOCAL_LAN_EN);
        btn_save.setEnabled(true);
        btn_save.setTextColor(getResources().getColor(R.color.CC_D63031));
        LogUtil.d(TAG, "选择英文");
    }

    private void selectIN() {
        if(TextUtils.equals(selectLanguage, Constants.LOCAL_LAN_ID)){
            return;
        }
        refreshRightIcon(Constants.LOCAL_LAN_ID);
        btn_save.setEnabled(true);
        btn_save.setTextColor(getResources().getColor(R.color.CC_D63031));
        LogUtil.d(TAG, "选择印尼语");
    }

}