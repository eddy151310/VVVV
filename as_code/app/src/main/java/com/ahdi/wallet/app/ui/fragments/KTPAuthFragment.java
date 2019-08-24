package com.ahdi.wallet.app.ui.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.sdk.IDVerifySdk;
import com.ahdi.wallet.app.callback.IDVerifyCallBack;
import com.ahdi.wallet.app.main.IDVerifySdkMain;
import com.ahdi.wallet.app.response.RNASubmitAllRsp;
import com.ahdi.wallet.app.schemas.RnaExtraInfoSchema;
import com.ahdi.wallet.app.schemas.RnaInfoSchema;
import com.ahdi.wallet.app.ui.activities.IDAuth.EndingIDVerifyActivity;
import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.lib.utils.base.BaseFragment;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.takephoto.TakePhotoMain;
import com.ahdi.lib.utils.takephoto.callback.TakePhotoCallBack;
import com.ahdi.lib.utils.takephoto.util.ImageUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;

import org.json.JSONObject;

/**
 * @author zhaohe@iapppay.com
 * @date 2018/6/28.
 * <p>
 * 印尼身份证识别
 */

public class KTPAuthFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = KTPAuthFragment.class.getSimpleName();
    private static final String rnaType = "KTP";

    private EditText et_name, et_card_number,et_nationality,et_birth_place,et_occupation,et_mother_name;
    /**
     * id card 显示区域
     */
    private LinearLayout ll_take_id_photo;
    private ImageView iv_id_card, iv_retake_id_card;

    /**
     * 手持身份证显示区域
     */
    private LinearLayout ll_take_hold_id_photo;
    private ImageView iv_hold_id_card, iv_retake_hold_id_card;

    private Button btn_confirm;
    private LoadingDialog loadingDialog = null;
    //输入监听
    private MyTextWatcher textWatcher = null;
    //输入字符限制
    private MyNumberKeyListener numberKeyListener = null;

    private Bitmap idPhotoBitmap = null;
    private Bitmap holdPhotoBitmap = null;

    @Override
    protected View initRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_ktp_auth, container, false);
    }

    @Override
    protected void initView(View view) {
        //拍证件区域
        ll_take_id_photo = view.findViewById(R.id.ll_take_photo);
        view.findViewById(R.id.iv_take_card).setOnClickListener(this);
        iv_id_card = view.findViewById(R.id.iv_card);
        iv_retake_id_card = view.findViewById(R.id.iv_retake_card);
        iv_retake_id_card.setOnClickListener(this);
        //拍手持证件区域
        ll_take_hold_id_photo = view.findViewById(R.id.ll_take_hold_photo);
        view.findViewById(R.id.iv_take_hold_card).setOnClickListener(this);
        iv_hold_id_card = view.findViewById(R.id.iv_hold_card);
        iv_retake_hold_id_card = view.findViewById(R.id.iv_retake_hold_card);
        iv_retake_hold_id_card.setOnClickListener(this);
        //信息填写区域
        et_card_number = view.findViewById(R.id.et_card_number);
        et_name = view.findViewById(R.id.et_name);
        et_nationality = view.findViewById(R.id.et_nationality);
        et_birth_place = view.findViewById(R.id.et_birth_place);
        et_occupation = view.findViewById(R.id.et_occupation);
        et_mother_name = view.findViewById(R.id.et_mother_name);

        btn_confirm = view.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
    }

    @Override
    protected void initData(View view) {
        textWatcher = new MyTextWatcher();
        et_name.setKeyListener(numberKeyListener);
        et_card_number.addTextChangedListener(textWatcher);
        et_name.addTextChangedListener(textWatcher);
        et_nationality.addTextChangedListener(textWatcher);
        et_birth_place.addTextChangedListener(textWatcher);
        et_occupation.addTextChangedListener(textWatcher);
        et_mother_name.addTextChangedListener(textWatcher);

        numberKeyListener = new MyNumberKeyListener();
        et_name.setKeyListener(numberKeyListener);
        et_nationality.setKeyListener(numberKeyListener);
        et_birth_place.setKeyListener(numberKeyListener);
        et_occupation.setKeyListener(numberKeyListener);
        et_mother_name.setKeyListener(numberKeyListener);
    }

    @Override
    public void onClick(View v) {
        if (!isCanClick()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.btn_confirm) {
            submit();
        } else if (id == R.id.iv_take_card || id == R.id.iv_retake_card) {
            TakePhotoMain.getInstance().takePhoto(mActivity, TakePhotoMain.TYPE_ID_CARD, photoCallBack);
        }else if (id == R.id.iv_take_hold_card || id == R.id.iv_retake_hold_card) {
            TakePhotoMain.getInstance().takePhoto(mActivity, TakePhotoMain.TYPE_HOLD_ID_PHOTO, photoCallBack);
        }
    }

    /**
     * 拍照结果回调
     */
    private TakePhotoCallBack photoCallBack = new TakePhotoCallBack() {
        @Override
        public void onResult(String Code, int type, String reason, Bitmap bitmap, String photoPath) {
            if (!TextUtils.equals(Code, TakePhotoMain.RESULT_SUCCESS)) {
                LogUtil.d(TAG, "拍照失败，回调：" + reason);
            }
            if (bitmap == null) {
                LogUtil.d(TAG, "拍照回调 bitmap == null");
                return;
            }
            if (type == TakePhotoMain.TYPE_ID_CARD) {
                onIDCardPhoto(bitmap);
            } else if (type == TakePhotoMain.TYPE_HOLD_ID_PHOTO) {
                onHoldIDPhoto(bitmap);
            }
        }
    };

    /**
     * 拍完身份证照片之后处理相关逻辑
     *
     * @param bitmap
     */
    private void onIDCardPhoto(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        idPhotoBitmap = bitmap;
        ll_take_id_photo.setVisibility(View.GONE);
        iv_retake_id_card.setVisibility(View.VISIBLE);
        btn_confirm.setVisibility(View.VISIBLE);
        iv_id_card.setImageBitmap(bitmap);
        submitBtnStatus();
    }

    /**
     * 手持身份证照片
     *
     * @param bitmap
     */
    private void onHoldIDPhoto(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        holdPhotoBitmap = bitmap;
        ll_take_hold_id_photo.setVisibility(View.GONE);
        iv_retake_hold_id_card.setVisibility(View.VISIBLE);
        btn_confirm.setVisibility(View.VISIBLE);
        iv_hold_id_card.setImageBitmap(bitmap);
        submitBtnStatus();
    }

    private class MyTextWatcher implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            submitBtnStatus();
        }
    }

    private class MyNumberKeyListener extends NumberKeyListener{

        @NonNull
        @Override
        protected char[] getAcceptedChars() {
            return Constants.COMMON_ACCEPT_CHAR.toCharArray();
        }

        @Override
        public int getInputType() {
            return InputType.TYPE_CLASS_TEXT;
        }
    }

    /**
     * 检查提交按钮的状态
     */
    private void submitBtnStatus() {
        String cardNumber = et_card_number.getText().toString();
        if (!TextUtils.isEmpty(et_name.getText().toString())
                && !TextUtils.isEmpty(cardNumber) && cardNumber.length() == 16
                && !TextUtils.isEmpty(et_nationality.getText().toString())
                &&!TextUtils.isEmpty(et_birth_place.getText().toString())
                &&!TextUtils.isEmpty(et_occupation.getText().toString())
                &&!TextUtils.isEmpty(et_mother_name.getText().toString())
                && idPhotoBitmap != null && holdPhotoBitmap != null) {
            btn_confirm.setEnabled(true);
        } else {
            btn_confirm.setEnabled(false);
        }
    }

    /**
     * 提交信息
     */
    private void submit() {
        String name = et_name.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ToastUtil.showToastShort(getActivity(), getString(R.string.Toast_U0));
            return;
        }
        String nationality = et_nationality.getText().toString().trim();
        if (TextUtils.isEmpty(nationality)) {
            ToastUtil.showToastShort(getActivity(), getString(R.string.Toast_V0));
            return;
        }
        String birthPlace = et_birth_place.getText().toString().trim();
        if (TextUtils.isEmpty(birthPlace)) {
            ToastUtil.showToastShort(getActivity(), getString(R.string.Toast_W0));
            return;
        }
        String occupation = et_occupation.getText().toString().trim();
        if (TextUtils.isEmpty(occupation)) {
            ToastUtil.showToastShort(getActivity(), getString(R.string.Toast_X0));
            return;
        }
        String motherName = et_mother_name.getText().toString().trim();
        if (TextUtils.isEmpty(motherName)) {
            ToastUtil.showToastShort(getActivity(), getString(R.string.Toast_Y0));
            return;
        }

        loadingDialog = showLoading();
        RnaInfoSchema rnaInfoSchema = new RnaInfoSchema(et_card_number.getText().toString(), name);
        RnaExtraInfoSchema rnaExtraInfoSchema = new RnaExtraInfoSchema(nationality, birthPlace, occupation, motherName);
        IDVerifySdkMain.getInstance().onRnaSubmitAll(rnaInfoSchema, ImageUtil.bitmapToString(idPhotoBitmap), ImageUtil.bitmapToString(holdPhotoBitmap), rnaType,rnaExtraInfoSchema, ProfileUserUtil.getInstance().getSID(), new IDVerifyCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);
                if (TextUtils.equals(code, IDVerifySdk.LOCAL_SUCCESS)) {
                    RNASubmitAllRsp resp = RNASubmitAllRsp.decodeJson(RNASubmitAllRsp.class, jsonObject);
                    Intent intent = new Intent(mActivity, EndingIDVerifyActivity.class);
                    intent.putExtra(EndingIDVerifyActivity.PAGE_TYPE, IDVerifySdkMain.RESULT_WAIT);
                    intent.putExtra(Constants.LOCAL_PROMPT_KEY, resp.getTipsSchemas());
                    mActivity.startActivity(intent);
                    mActivity.finish();
                } else {
                    showErrorDialog(errorMsg);
                }
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (idPhotoBitmap != null && !idPhotoBitmap.isRecycled()) {
            idPhotoBitmap.recycle();
        }
        if (holdPhotoBitmap != null && !holdPhotoBitmap.isRecycled()) {
            holdPhotoBitmap.recycle();
        }
        idPhotoBitmap = null;
        holdPhotoBitmap = null;
    }
}
