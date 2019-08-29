package com.ahdi.wallet.app.ui.aaaa;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.bean.UserData;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.imagedown.ImageDownUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.lib.utils.takephoto.TakePhotoMain;
import com.ahdi.lib.utils.takephoto.callback.TakePhotoCallBack;
import com.ahdi.lib.utils.takephoto.util.ImageUtil;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.DateUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.lib.utils.utils.StringUtil;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.lib.utils.widgets.datepicker.DatePickerView;
import com.ahdi.lib.utils.widgets.dialog.ListSelectDialog;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.HttpReqApp;
import com.ahdi.wallet.app.callback.UserSdkCallBack;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.wallet.app.request.aaa.UserCenterReq;
import com.ahdi.wallet.app.response.GetPhotoUpUrlRsp;
import com.ahdi.wallet.app.response.UpUserPhotoRsp;
import com.ahdi.wallet.app.response.UpdateUserInfoRsp;
import com.ahdi.wallet.app.response.aaa.UserCenterRsp;
import com.ahdi.wallet.app.schemas.AvatarSchema;
import com.ahdi.wallet.app.schemas.UserSchema;
import com.ahdi.wallet.app.sdk.UserSdk;
import com.ahdi.wallet.app.ui.activities.other.SettingsActivity;
import com.ahdi.wallet.app.ui.activities.userInfo.ModifyUserInfoActivity;
import com.ahdi.wallet.app.ui.widgets.SelectPhotoView;
import com.ahdi.wallet.databinding.AaaActivityUserCenterBinding;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 用户信息页面
 *
 * @author ibb
 */
public class UserCenterActivity extends AppBaseActivity {

    private static final String TAG = "ProfileActivity";

    private LoadingDialog loadingDialog = null;
    /**
     * 日期选择
     */
    private DatePickerView datePickerView;
    private DateResultHandler resultHandler = null;
    private String startDate = "1900-01-01 00:00";
    private String selectDate = "1990-06-15 00:00";
    private SelectPhotoView selectPhotoView = null;
    /**
     * 性别
     */
    private int gender;
    private int defaultSelect = 0;

    private Button btnNext  ;

    AaaActivityUserCenterBinding dataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        dataBinding =  DataBindingUtil.setContentView(this, R.layout.aaa_activity_user_center);
        initCommonTitle(getString(R.string.Profile_A0));
        initView();
        getUserCenterInfo();
    }

    public void initView() {
        btnNext = initTitleNext();
        btnNext.setVisibility(View.VISIBLE);
        btnNext.setOnClickListener(this);
    }

    private void initData() {

        UserData userData = ProfileUserUtil.getInstance().getUserData();
        if (userData == null) {
            return;
        }
//        if (!TextUtils.isEmpty(userData.getAvatar())) {
//            ImageDownUtil.downMySelfPhoto(UserCenterActivity.this, userData.getAvatar(), iv_user_photo);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onClick(View view) {
        if (!isCanClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.btn_next:
                intentActivity(SettingsActivity.class);
                break;
            case R.id.rl_user_photo_area:
                selectPhotoFrom();
                break;
            case R.id.rl_user_name_area:
                modifyUserInfo(Constants.TYPE_MODIFY_NICK_NAME);
                break;
            case R.id.ll_user_gender_area:
                selectGender();
                break;
            case R.id.ll_user_email_area:
                modifyUserInfo(Constants.TYPE_MODIFY_EMAIL);
                break;
            case R.id.ll_user_birth_area:
                setBirthday();
                break;
            default:
                break;
        }
    }

    /**
     * 选择图片
     */
    private void selectPhotoFrom() {
        selectPhotoView = new SelectPhotoView(UserCenterActivity.this);
        selectPhotoView.show();
        selectPhotoView.setListener(new SelectPhotoView.SelectPhotoListener() {
            @Override
            public void onSelectType(int type) {
                if (type == SelectPhotoView.TYPE_CAMERA) {
                    getPhoto(TakePhotoMain.TYPE_USER_PHOTO_CAMERA);
                } else if (type == SelectPhotoView.TYPE_ALBUM) {
                    getPhoto(TakePhotoMain.TYPE_USER_PHOTO_ALBUM);
                } else if (type == SelectPhotoView.TYPE_CANCEL) {
                    LogUtil.d(TAG, "弹窗取消");
                }
            }
        });
    }

    /**
     * 获取图片
     *
     * @param type
     */
    private void getPhoto(int type) {
        TakePhotoMain.getInstance().takePhoto(UserCenterActivity.this, type, new TakePhotoCallBack() {

            @Override
            public void onResult(String Code, int type, String reason, Bitmap resultBitmap, String photoPath) {
                if (TextUtils.equals(Code, TakePhotoMain.RESULT_SUCCESS)) {
                    if (resultBitmap != null) {
                        getPhotoUpUrl(resultBitmap);
                    }
                } else if (TextUtils.equals(Code, TakePhotoMain.RESULT_FAIL)) {
                    LogUtil.d(TAG, reason);
                } else if (TextUtils.equals(Code, TakePhotoMain.RESULT_CANCEL)) {
                    LogUtil.d(TAG, "取消");
                }
            }
        });
    }

    /**
     * 获取头像上传地址
     */
    private void getPhotoUpUrl(Bitmap resultBitmap) {
        loadingDialog = showLoading();
        UserSdk.getPhotoUpUrl(UserCenterActivity.this, AppGlobalUtil.getInstance().getSID(), new UserSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                if (TextUtils.equals(code, UserSdk.LOCAL_PAY_SUCCESS)) {
                    GetPhotoUpUrlRsp response = GetPhotoUpUrlRsp.decodeJson(GetPhotoUpUrlRsp.class, jsonObject);
                    if (response != null) {
                        upPhoto(response.getUrl(), response.getType(), ImageUtil.bitmapToByte(resultBitmap));
                    } else {
                        closeLoading(loadingDialog);
                    }
                } else {
                    closeLoading(loadingDialog);
                    if (TextUtils.equals(code, Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION)) {
                        ToastUtil.showToastShort(UserCenterActivity.this, errorMsg);
                    } else {
                        showErrorDialog(errorMsg);
                    }
                }
            }
        });
    }

    /**
     * 上传图片
     *
     * @param upUrl
     * @param type
     */
    private void upPhoto(String upUrl, int type, byte[] imgByte) {

        UserSdk.upUserPhoto(UserCenterActivity.this, upUrl, imgByte, new UserSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                if (TextUtils.equals(code, UserSdk.LOCAL_PAY_SUCCESS)) {
                    UpUserPhotoRsp response = UpUserPhotoRsp.decodeJson(UpUserPhotoRsp.class, jsonObject);
                    if (response != null) {
                        setUserIcon(response.getRelativePath(), type);
                    } else {
                        closeLoading(loadingDialog);
                    }
                } else {
                    closeLoading(loadingDialog);
                    if (TextUtils.equals(code, Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION)) {
                        ToastUtil.showToastShort(UserCenterActivity.this, errorMsg);
                    } else {
                        showErrorDialog(errorMsg);
                    }
                }
            }
        });
    }

    /**
     * 更新用户信息
     *
     * @param relativePath
     * @param type
     */
    private void setUserIcon(String relativePath, int type) {

        AvatarSchema schema = new AvatarSchema(relativePath, type);
        AppMain.getInstance().setUserIcon(UserCenterActivity.this, schema, new UserSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);
                if (TextUtils.equals(code, UserSdk.LOCAL_PAY_SUCCESS)) {
                    UpdateUserInfoRsp response = UpdateUserInfoRsp.decodeJson(UpdateUserInfoRsp.class, jsonObject);
                    if (response != null && response.getUserSchema() != null) {
                        updateUserData(response.getUserSchema());
                    }
                } else {
                    if (TextUtils.equals(code, Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION) || TextUtils.equals(code, Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION)) {
                        ToastUtil.showToastShort(UserCenterActivity.this, errorMsg);
                    } else {
                        showErrorDialog(errorMsg);
                    }
                }
            }
        });
    }

    /**
     * 更新用户信息
     *
     * @param userSchema
     */
    private void updateUserData(UserSchema userSchema) {
        if (userSchema == null) {
            return;
        }
        UserData userData = ProfileUserUtil.getInstance().getUserData();
        if (userData == null) {
            GlobalApplication.getApplication().updateUserSchema(userSchema);
        } else {
            if (!TextUtils.isEmpty(userSchema.Avatar)) {
                userData.setAvatar(userSchema.Avatar);
            }
            userData.setBirthday(userSchema.birthday);
            if (userSchema.gender > 0) {
                userData.setGender(userSchema.gender);
            }
        }
        initData();
    }

    /**
     * 设置生日
     */
    private void setBirthday() {
        if (resultHandler == null) {
            resultHandler = new DateResultHandler();
        }
        datePickerView = new DatePickerView(this, resultHandler, startDate, DateUtil.formatTimeYMDHM(System.currentTimeMillis(), null));
        datePickerView.showDay(true);
        UserData userData = ProfileUserUtil.getInstance().getUserData();
        if (userData != null) {
            String birthday = userData.getBirthday();
            if (!TextUtils.isEmpty(birthday)) {
                selectDate = DateUtil.ddMMyyyy2yyyyMMddHHmm(birthday);
            }
        }
        datePickerView.show(selectDate);
    }

    private class DateResultHandler implements DatePickerView.ResultHandler {
        @Override
        public void handle(long time) {
            LogUtil.d(TAG, DateUtil.formatTimeDMYNoHMS(time));
            updateUserInfo(DateUtil.formatTimeDMYNoHMS(time), UserSdk.TYPE_BIRTHDAY);
        }
    }

    /**
     * 设置性别
     */
    private void selectGender() {
        String selectedGender = "";
        if (gender == Constants.LOCAL_GENDER_FEMALE) {
            selectedGender = getString(R.string.Profile_F0);
        } else if (gender == Constants.LOCAL_GENDER_MALE) {
            selectedGender = getString(R.string.Profile_G0);
        }
        ArrayList<String> list = StringUtil.getGenderList(this);
        new ListSelectDialog(this, R.style.ActionSheetDialogStyle, list, defaultSelect)
                .setOnGenderDialogListener(new ListSelectDialog.OnListSelectListener() {
                    @Override
                    public void onCallBack(int selectedIndex, String item) {
                        LogUtil.e(TAG, "selectedIndex: " + selectedIndex);
                        defaultSelect = selectedIndex;
                        updateUserInfo(selectedIndex == 0 ? "2" : "1", UserSdk.TYPE_GENDER);
                    }
                })
                .selectItem(selectedGender)
                .show();
    }

    /**
     * 跳转到其他页面去修改用户信息
     *
     * @param type 要修改的内容的种类  Constants.TYPE_MODIFY_NICK_NAME ,Constants.TYPE_MODIFY_EMAIL
     */
    private void modifyUserInfo(int type) {
        Intent intent = new Intent(UserCenterActivity.this, ModifyUserInfoActivity.class);
        intent.putExtra(Constants.LOCAL_TYPE_KEY, type);
        startActivity(intent);
    }

    /**
     * 修改用户信息
     *
     * @param content
     * @param type
     */
    private void updateUserInfo(String content, int type) {
        loadingDialog = showLoading();

        AppMain.getInstance().updateUserInfo(UserCenterActivity.this, content, type, new UserSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);
                if (TextUtils.equals(code, UserSdk.LOCAL_PAY_SUCCESS)) {
                    UpdateUserInfoRsp response = UpdateUserInfoRsp.decodeJson(UpdateUserInfoRsp.class, jsonObject);
                    if (response != null && response.getUserSchema() != null) {
                        updateUserData(response.getUserSchema());
                    }
                } else {
                    if (TextUtils.equals(code, Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION)) {
                        ToastUtil.showToastShort(UserCenterActivity.this, errorMsg);
                    } else {
                        showErrorDialog(errorMsg);
                    }
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
        loadingDialog = null;
        if (datePickerView != null && datePickerView.isShowing()) {
            datePickerView.dismiss();
        }
        datePickerView = null;
        selectPhotoView = null;
    }


    /**
     * 获取用户中心相关数据
     */
    private void getUserCenterInfo() {
        loadingDialog = showLoading();
        UserCenterReq request = new UserCenterReq(AppGlobalUtil.getInstance().getSID() ,  AppGlobalUtil.getInstance().getLoginName());
        HttpReqApp.getInstance().onUserCenterInfo(request, new HttpReqTaskListener() {

            @Override
            public void onPostExecute(JSONObject json) {
                loadingDialog.dismiss();
                LogUtil.d(TAG,   "解析json :" + json);
                UserCenterRsp response = UserCenterRsp.decodeJson(UserCenterRsp.class, json);
                ToastUtil.showToastAtCenterLong(UserCenterActivity.this , response.getmHeader().retCode + response.getmHeader().retMsg );
                if(response.getmHeader().retCode.equals(Constants.RET_CODE_SUCCESS)){
                    dataBinding.setUserCenterInfo(response);
                }
            }

            @Override
            public void onError(JSONObject json) {
                LogUtil.d(TAG,   "解析json onError :" + json);
                loadingDialog.dismiss();
            }
        });
    }

    /**
     * 跳转到其他activity
     * @param cls
     */
    private void intentActivity(Class<?> cls) {
        if (cls == null) {
            return;
        }
        Intent intent = new Intent(UserCenterActivity.this, cls);
        startActivity(intent);
    }

}
