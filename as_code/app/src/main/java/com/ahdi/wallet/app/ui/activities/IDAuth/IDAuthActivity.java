package com.ahdi.wallet.app.ui.activities.IDAuth;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.sdk.IDVerifySdk;
import com.ahdi.wallet.app.main.IDVerifySdkMain;
import com.ahdi.wallet.app.ui.fragments.KTPAuthFragment;
import com.ahdi.wallet.app.ui.fragments.KitasAuthFragment;
import com.ahdi.wallet.app.ui.fragments.PassPortAuthFragment;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.widgets.dialog.ListSelectDialog;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author zhaohe@iapppay.com
 * @date 2018/5/18.
 * 实名认证页面
 */

public class IDAuthActivity extends AppBaseActivity implements View.OnClickListener {

    private static final String TAG = "IDAuthActivity";

    /**KTP，印尼身份证*/
    private static final String TYPE_KTP = "KTP";
    /** Passport，护照*/
    private static final String TYPE_PASSPORT = "Passport";
    /**KITAS，印尼临时居留证*/
    private static final String TYPE_KITAS = "KITAS";


    /**上次选中的类型类型*/
    private String lastType = "";
    private String[] types = null;

    private TextView tv_auth_type;

    private FragmentManager fragmentManager = null;

    private int defaultSelect = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        initIntent();
        setContentView(R.layout.activity_id_auth);
        getWindow().setBackgroundDrawableResource(R.color.CC_F1F2F6); //代码设置bg防止键盘弹起时键盘区域漏出主题的黑色
        initView();
        isStatisticActivity = false;
    }

    private void initIntent() {
        Intent intent = getIntent();
        if (intent != null){
            types = intent.getStringArrayExtra(Constants.LOCAL_TYPES_KEY);
        }
        if (types == null || types.length == 0){
            finish();
        }
    }

    private void initView(){
        initCommonTitle(getString(R.string.Identity_Auth_A0));
//        findViewById(R.id.rl_id_auth_type).setOnClickListener(this);
        tv_auth_type = findViewById(R.id.tv_auth_type);
        changeAuthType(types[0]);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.rl_id_auth_type){
            if (types != null && types.length > 1){
                selectVerifyType();
            }
        }
    }

    /**
     *  选择认证类型
     */
    private void selectVerifyType() {
        ArrayList<String> list = new ArrayList<>(3);
        list.addAll(Arrays.asList(types));

        new ListSelectDialog(this, R.style.ActionSheetDialogStyle, list, defaultSelect)
                .setOnGenderDialogListener(new ListSelectDialog.OnListSelectListener() {
                    @Override
                    public void onCallBack(int selectedIndex, String item) {
                        LogUtil.d(TAG, "index = " + selectedIndex + " , item = " + item);
                        defaultSelect = selectedIndex;
                        changeAuthType(item);
                    }
                }).show();
    }

    /**
     * 切换界面
     * @param selectTypeName
     */
    private void changeAuthType(String selectTypeName){
        if (TextUtils.isEmpty(selectTypeName)){
            finish();
        }
        if (TextUtils.equals(lastType, selectTypeName)){
            return;
        }
        tv_auth_type.setText(selectTypeName);
        if (fragmentManager == null){
            fragmentManager = getFragmentManager();
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (TextUtils.equals(selectTypeName.toLowerCase(), TYPE_KTP.toLowerCase())){
            transaction.replace(R.id.fl_container, new KTPAuthFragment());
        }else if (TextUtils.equals(selectTypeName.toLowerCase(), TYPE_PASSPORT.toLowerCase())){
            transaction.replace(R.id.fl_container, new PassPortAuthFragment());
        }else if (TextUtils.equals(selectTypeName.toLowerCase(), TYPE_KITAS.toLowerCase())){
            transaction.replace(R.id.fl_container, new KitasAuthFragment());
        }
        transaction.commit();
        lastType = selectTypeName;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onClose();
    }

    /**
     * 关闭界面
     */
    private void onClose() {
        IDVerifySdkMain.getInstance().onResultBack(IDVerifySdk.LOCAL_USER_CANCEL, "", null);
        finish();
    }
}
