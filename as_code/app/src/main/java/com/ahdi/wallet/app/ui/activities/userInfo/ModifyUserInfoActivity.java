package com.ahdi.wallet.app.ui.activities.userInfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.ui.fragments.SetEmailFragment;
import com.ahdi.wallet.app.ui.fragments.SetNickNameFragment;

/**
 * 修改用户信息
 * @author zhaohe
 */
public class ModifyUserInfoActivity extends AppBaseActivity{
    private static final String TAG = "ModifyUserInfoActivity";
    /**
     * 修改的哪一种用户信息
     */
    private int type = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.activity_modify_user_info);
        initIntent();
        isStatisticActivity = false;
        showFragment();
    }

    private void initIntent() {
        Intent intent = getIntent();
        if (intent != null){
            type = intent.getIntExtra(Constants.LOCAL_TYPE_KEY, 0);
        }
    }

    private void showFragment() {
        LogUtil.d(TAG, "type = " + type);
        if (type == Constants.TYPE_MODIFY_NICK_NAME){
            initCommonTitle(getString(R.string.NickName_A0));
            getFragmentManager().beginTransaction().replace(R.id.fl_container, new SetNickNameFragment()).commit();
        }else if (type == Constants.TYPE_MODIFY_EMAIL){
            initCommonTitle(getString(R.string.SetEmail_A0));
            getFragmentManager().beginTransaction().replace(R.id.fl_container, new SetEmailFragment()).commit();
        }else {
            finish();
        }
    }

}
