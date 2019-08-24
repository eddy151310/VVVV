package com.ahdi.wallet.app.ui.fragments;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ahdi.wallet.app.sdk.UserSdk;
import com.ahdi.wallet.app.callback.UserSdkCallBack;
import com.ahdi.wallet.app.response.UpdateUserInfoRsp;
import com.ahdi.wallet.app.schemas.UserSchema;
import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.lib.utils.base.BaseFragment;
import com.ahdi.lib.utils.bean.UserData;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.lib.utils.base.BaseEditTextWatcher;

import org.json.JSONObject;

/**
 * @author zhaohe@iapppay.com
 * @date 2018/6/28.
 *
 * 设置nickName
 *
 * 	昵称输入框：
	默认展示用户已设置昵称（包含默认昵称UID）；
	当光标在输入框中，展示出快速删除按钮，可直接删除全部昵称内容；
	昵称限制在1-32个字符，超过后不可输入；
	确认按钮：默认置灰；当昵称输入框输入内容后，按钮高亮；点击进行昵称提交：
 */

public class SetNickNameFragment extends BaseFragment implements View.OnClickListener{

    private static final String TAG = "SetNickNameFragment";
    /**
     * 输入框的文字是否修改了
     */
    private boolean isChange = false;

    private EditText et_name;
    private Button btn_confirm;
    private String nname = "";
    @Override
    protected View initRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_set_nick_name, container, false);
    }

    @Override
    protected void initView(View view) {
        et_name = view.findViewById(R.id.et_name);
        btn_confirm = view.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        et_name.addTextChangedListener(new BaseEditTextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isChange && (start != before || !TextUtils.equals(s.toString(), nname))){
                    isChange = true;
                }
                if (!TextUtils.isEmpty(s.toString()) && isChange){
                    btn_confirm.setEnabled(isChange);
                }else {
                    btn_confirm.setEnabled(false);
                }
            }
        });
        et_name.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    et_name.setCursorVisible(true);
                }
                return false;
            }
        });
    }

    @Override
    protected void initData(View view) {
        UserData userData = ProfileUserUtil.getInstance().getUserData();
        if (userData != null){
            nname = userData.getNName();
            if(TextUtils.isEmpty(userData.getNName())){
                nname = userData.getUID();
            }
            et_name.setText(nname);
            et_name.setSelection(nname.length());
        }
    }

    @Override
    public void onClick(View v) {
        if (!isCanClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_confirm:
                closeSoftInput();
                String nname = et_name.getText().toString().trim();
                if (TextUtils.isEmpty(nname)){
                    et_name.setText("");
                    ToastUtil.showToastShort(getActivity(), getString(R.string.Toast_T0));
                    return;
                }
                updateUserNName(nname);
                break;
            default:
                break;
        }
    }

    /**
     * 修改用户昵称
     */
    public void updateUserNName(String nname) {
        LoadingDialog dialog = showLoading();
        LogUtil.d(TAG, "修改之后的用户昵称" + nname);
        AppMain.getInstance().updateUserInfo(getActivity(), nname,UserSdk.TYPE_NICKNAME, new UserSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(dialog);
                if (TextUtils.equals(code, UserSdk.LOCAL_PAY_SUCCESS)){
                    UpdateUserInfoRsp response = UpdateUserInfoRsp.decodeJson(UpdateUserInfoRsp.class, jsonObject);
                    if (response != null && response.getUserSchema() != null){
                        updateUserData(response.getUserSchema());
                    }
                    if (mActivity != null){
                        mActivity.finish();
                    }
                }else{
                    if (TextUtils.equals(code, Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION) ||
                            TextUtils.equals(code, Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION)){
                        ToastUtil.showToastShort(mActivity, errorMsg);
                    }else {
                        showErrorDialog(errorMsg);
                    }
                }
            }
        });
    }


    /**
     * 更新用户信息
     * @param userSchema
     */
    private void updateUserData(UserSchema userSchema) {
        if (userSchema == null){
            return;
        }
        UserData userData = ProfileUserUtil.getInstance().getUserData();
        if (userData == null){
            GlobalApplication.getApplication().updateUserSchema(userSchema);
        }else if (!TextUtils.isEmpty(userSchema.NName)){
            userData.setNName(userSchema.NName);
        }
    }

}
