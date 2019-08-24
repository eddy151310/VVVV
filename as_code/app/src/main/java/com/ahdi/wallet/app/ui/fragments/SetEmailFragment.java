package com.ahdi.wallet.app.ui.fragments;

import android.text.InputFilter;
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
import com.ahdi.lib.utils.bean.UserData;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.lib.utils.base.BaseEditTextWatcher;
import com.ahdi.lib.utils.base.BaseFragment;

import org.json.JSONObject;

/**
 * @author zhaohe@iapppay.com
 * @date 2018/6/28.
 *
 * 设置邮箱
 *
 *	邮箱输入框：
	已设置邮箱则默认展示用户已设置邮箱；当光标在输入框中，展示出快速删除按钮，可直接删除全部昵称内容；
	未设置邮箱则默认展示提示‘Please your email’。当光标在输入框中，提示文案消失，展示出快速删除按钮，可直接删除全部昵称内容；
	邮箱限制在6-32个字符，超过后不可输入；
	只允许输入英文字母、数字、下划线_、英文句号.、@、以及中划线-组成。
 */

public class SetEmailFragment extends BaseFragment implements View.OnClickListener{

    private int EMAIL_MAX_LENGTH = 32;

    private static final String TAG = "SetEmailFragment";
    /**
     * 输入框的文字是否修改了
     */
    private boolean isChange = false;

    private EditText et_email;
    private Button btn_confirm;
    private String email = "";
    @Override
    protected View initRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_set_email, container, false);
    }

    @Override
    protected void initView(View view) {
        et_email = view.findViewById(R.id.et_email);
        btn_confirm = view.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        et_email.setFilters(new InputFilter[]{new InputFilter.LengthFilter(EMAIL_MAX_LENGTH)});
        et_email.addTextChangedListener(new BaseEditTextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isChange && (start != before || !TextUtils.equals(s.toString(), email))){
                    isChange = true;
                }
                if (!TextUtils.isEmpty(s.toString()) && s.toString().length() > 6){
                    btn_confirm.setEnabled(isChange);
                }else {
                    btn_confirm.setEnabled(false);
                }
            }
        });
        et_email.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    et_email.setCursorVisible(true);
                }
                return false;
            }
        });
    }

    @Override
    protected void initData(View view) {
        UserData userData = ProfileUserUtil.getInstance().getUserData();
        if (userData != null){
            email = userData.getEmail();
            if(!TextUtils.isEmpty(email)){
                et_email.setText(email);
                if (email.length() <= EMAIL_MAX_LENGTH){
                    et_email.setSelection(email.length());
                }
            }
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
                String inputEmail = et_email.getText().toString();
                if (inputEmail.matches(Constants.EMAIL_REGEX)){
                    updateEmail(inputEmail);
                }else {
                    showErrorDialog(getString(R.string.DialogMsg_G0));
                }
                break;
            default:
                break;
        }
    }

    /**
     * 修改用户昵称
     */
    public void updateEmail(String inputEmail) {
        LoadingDialog dialog = showLoading();
        LogUtil.d(TAG, "修改之后的用户email:" + inputEmail);
        AppMain.getInstance().updateUserInfo(getActivity(), inputEmail,UserSdk.TYPE_EMAIL, new UserSdkCallBack() {
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
        }else if (!TextUtils.isEmpty(userSchema.email)){
            userData.setEmail(userSchema.email);
        }
    }
}
