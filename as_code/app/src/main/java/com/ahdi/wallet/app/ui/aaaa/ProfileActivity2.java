package com.ahdi.wallet.app.ui.aaaa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.bean.UserData;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.imagedown.ImageDownUtil;
import com.ahdi.lib.utils.takephoto.TakePhotoMain;
import com.ahdi.lib.utils.takephoto.callback.TakePhotoCallBack;
import com.ahdi.lib.utils.takephoto.util.ImageUtil;
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
import com.ahdi.wallet.app.callback.UserSdkCallBack;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.wallet.app.response.GetPhotoUpUrlRsp;
import com.ahdi.wallet.app.response.UpUserPhotoRsp;
import com.ahdi.wallet.app.response.UpdateUserInfoRsp;
import com.ahdi.wallet.app.schemas.AvatarSchema;
import com.ahdi.wallet.app.schemas.UserSchema;
import com.ahdi.wallet.app.sdk.UserSdk;
import com.ahdi.wallet.app.ui.activities.userInfo.ModifyUserInfoActivity;
import com.ahdi.wallet.app.ui.widgets.SelectPhotoView;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 用户信息页面
 *
 * @author zhaohe
 */
public class ProfileActivity2 extends AppBaseActivity {

    private static final String TAG = "ProfileActivity";

    private RelativeLayout rl_user_photo_area;
    private LinearLayout rl_user_name_area, ll_user_gender_area, ll_user_email_area, ll_user_birth_area;
    private TextView user_nname, user_phone, user_id, user_gender, user_email, user_birthday;
    private ImageView iv_user_photo;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.activity_profile);
        initCommonTitle(getString(R.string.Profile_A0));
        initView();
    }

    public void initView() {
        rl_user_photo_area = findViewById(R.id.rl_user_photo_area);
        rl_user_name_area = findViewById(R.id.rl_user_name_area);
        ll_user_gender_area = findViewById(R.id.ll_user_gender_area);
        ll_user_email_area = findViewById(R.id.ll_user_email_area);
        ll_user_birth_area = findViewById(R.id.ll_user_birth_area);
        rl_user_photo_area.setOnClickListener(this);
        rl_user_name_area.setOnClickListener(this);
        ll_user_gender_area.setOnClickListener(this);
        ll_user_email_area.setOnClickListener(this);
        ll_user_birth_area.setOnClickListener(this);

        iv_user_photo = findViewById(R.id.iv_user_photo);
        user_nname = findViewById(R.id.user_nname);
        user_gender = findViewById(R.id.user_gender);
        user_phone = findViewById(R.id.user_phone);
        user_email = findViewById(R.id.user_email);
        user_birthday = findViewById(R.id.user_birthday);
        user_id = findViewById(R.id.user_id);
    }

    private void initData() {
        UserData userData = ProfileUserUtil.getInstance().getUserData();
        if (userData == null) {
            return;
        }
        if (!TextUtils.isEmpty(userData.getAvatar())) {
            ImageDownUtil.downMySelfPhoto(ProfileActivity2.this, userData.getAvatar(), iv_user_photo);
        }
        user_nname.setText(userData.getNName());
        user_phone.setText(userData.getsLName());
        user_id.setText(userData.getUID());
        if (!TextUtils.isEmpty(userData.getEmail())) {
            user_email.setHint("");
            user_email.setText(userData.getEmail());
        }
        String birthday = userData.getBirthday();
        if (!TextUtils.isEmpty(birthday)) {
            user_birthday.setHint("");
            user_birthday.setText(birthday);
        }
        gender = userData.getGender();
        if (gender == Constants.LOCAL_GENDER_FEMALE) {
            user_gender.setHint("");
            user_gender.setText(R.string.Profile_F0);
        } else if (gender == Constants.LOCAL_GENDER_MALE) {
            user_gender.setHint("");
            user_gender.setText(R.string.Profile_G0);
        }
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
        selectPhotoView = new SelectPhotoView(ProfileActivity2.this);
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
        TakePhotoMain.getInstance().takePhoto(ProfileActivity2.this, type, new TakePhotoCallBack() {

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
        UserSdk.getPhotoUpUrl(ProfileActivity2.this, GlobalApplication.getApplication().getSID(), new UserSdkCallBack() {
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
                        ToastUtil.showToastShort(ProfileActivity2.this, errorMsg);
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

        UserSdk.upUserPhoto(ProfileActivity2.this, upUrl, imgByte, new UserSdkCallBack() {
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
                        ToastUtil.showToastShort(ProfileActivity2.this, errorMsg);
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
        AppMain.getInstance().setUserIcon(ProfileActivity2.this, schema, new UserSdkCallBack() {
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
                        ToastUtil.showToastShort(ProfileActivity2.this, errorMsg);
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
        Intent intent = new Intent(ProfileActivity2.this, ModifyUserInfoActivity.class);
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

        AppMain.getInstance().updateUserInfo(ProfileActivity2.this, content, type, new UserSdkCallBack() {
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
                        ToastUtil.showToastShort(ProfileActivity2.this, errorMsg);
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

}
