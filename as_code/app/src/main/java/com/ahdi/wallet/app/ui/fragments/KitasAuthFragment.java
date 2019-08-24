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
import android.widget.RelativeLayout;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.sdk.IDVerifySdk;
import com.ahdi.wallet.app.callback.IDVerifyCallBack;
import com.ahdi.wallet.app.main.IDVerifySdkMain;
import com.ahdi.wallet.app.response.RNASubmitAllRsp;
import com.ahdi.wallet.app.schemas.RnaInfoSchema;
import com.ahdi.wallet.app.ui.activities.IDAuth.EndingIDVerifyActivity;
import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.lib.utils.base.BaseFragment;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.takephoto.TakePhotoMain;
import com.ahdi.lib.utils.takephoto.callback.TakePhotoCallBack;
import com.ahdi.lib.utils.takephoto.util.ImageUtil;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;

import org.json.JSONObject;

/**
 * @author zhaohe@iapppay.com
 * @date 2018/6/28.
 *
 * 临时居住证识别
 */

public class KitasAuthFragment extends BaseFragment implements View.OnClickListener{

    public static final String TAG = KitasAuthFragment.class.getSimpleName();
    private static final String rnaType = "KITAS";

    /**证件姓名和证件号码填写区域*/
    private EditText et_name,et_card_number;
    private ImageView iv_name_delete,iv_number_delete;
    /**id card 显示区域*/
    private RelativeLayout rl_kitas_area;
    private LinearLayout ll_take_kitas_photo;
    private ImageView iv_card, iv_retake_card;

    /**手持身份证显示区域*/
    private RelativeLayout rl_hold_kitas_area;
    private LinearLayout ll_take_hold_kitas_photo;
    private ImageView iv_hold_card, iv_retake_hold_card;

    private Button btn_confirm;
    private LoadingDialog loadingDialog = null;

    private Bitmap kitasCardBitmap = null;
    private Bitmap holdPhotoBitmap = null;

    @Override
    protected View initRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_kitas_auth, container, false);
    }

    @Override
    protected void initView(View view) {
        //信息填写区域
        et_card_number = view.findViewById(R.id.et_card_number);
        iv_number_delete = view.findViewById(R.id.iv_number_delete);
        iv_number_delete.setOnClickListener(this);
        et_name = view.findViewById(R.id.et_name);
        iv_name_delete = view.findViewById(R.id.iv_name_delete);
        iv_name_delete.setOnClickListener(this);
        //拍证件区域
        rl_kitas_area = view.findViewById(R.id.rl_kitas_area);
        rl_kitas_area.setOnClickListener(this);
        ll_take_kitas_photo = view.findViewById(R.id.ll_take_photo);
        iv_card = view.findViewById(R.id.iv_card);
        iv_retake_card = view.findViewById(R.id.iv_retake_card);
        iv_retake_card.setOnClickListener(this);
        //拍手持证件区域
        rl_hold_kitas_area = view.findViewById(R.id.rl_hold_kitas_area);
        rl_hold_kitas_area.setOnClickListener(this);
        ll_take_hold_kitas_photo = view.findViewById(R.id.ll_take_hold_photo);
        iv_hold_card = view.findViewById(R.id.iv_hold_card);
        iv_retake_hold_card = view.findViewById(R.id.iv_retake_hold_card);
        iv_retake_hold_card.setOnClickListener(this);

        btn_confirm = view.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
    }

    @Override
    protected void initData(View view) {
        et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    iv_name_delete.setVisibility(View.VISIBLE);
                } else {
                    iv_name_delete.setVisibility(View.GONE);
                }
                submitBtnStatus();
            }
        });
        et_name.setKeyListener(new NumberKeyListener() {
            @NonNull
            @Override
            protected char[] getAcceptedChars() {
                return Constants.COMMON_ACCEPT_CHAR.toCharArray();
            }

            @Override
            public int getInputType() {
                return InputType.TYPE_CLASS_TEXT;
            }
        });
        et_card_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    iv_number_delete.setVisibility(View.VISIBLE);
                } else {
                    iv_number_delete.setVisibility(View.GONE);
                }
                submitBtnStatus();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!isCanClick()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.btn_confirm) {
            submit();
        }else if (id == R.id.rl_kitas_area || id == R.id.iv_retake_card) {
            TakePhotoMain.getInstance().takePhoto(mActivity, TakePhotoMain.TYPE_KITAS_CARD, photoCallBack);
        }else if (id == R.id.iv_name_delete) {
            et_name.setText("");
            iv_name_delete.setVisibility(View.GONE);
        }else if (id == R.id.iv_number_delete) {
            et_card_number.setText("");
            iv_number_delete.setVisibility(View.GONE);
        }else if (id == R.id.rl_hold_kitas_area || id == R.id.iv_retake_hold_card) {
            TakePhotoMain.getInstance().takePhoto(mActivity, TakePhotoMain.TYPE_HOLD_KITAS_PHOTO, photoCallBack);
        }
    }

    /**
     * 拍照结果回调
     */
    private TakePhotoCallBack photoCallBack = new TakePhotoCallBack() {
        @Override
        public void onResult(String Code, int type, String reason, Bitmap bitmap, String photoPath) {
            if (!TextUtils.equals(Code, TakePhotoMain.RESULT_SUCCESS)){
                LogUtil.d(TAG, "拍照失败，回调：" + reason);
            }
            if (bitmap == null){
                LogUtil.d(TAG, "拍照回调 bitmap == null");
                return;
            }
            if (type == TakePhotoMain.TYPE_KITAS_CARD){
                onIDCardPhoto(bitmap);
            }else if (type == TakePhotoMain.TYPE_HOLD_KITAS_PHOTO){
                onHoldIDPhoto(bitmap);
            }
        }
    };

    /**
     * 拍完身份证照片之后处理相关逻辑
     * @param bitmap
     */
    private void onIDCardPhoto(Bitmap bitmap){
        if (bitmap == null){
            return;
        }
        kitasCardBitmap = bitmap;
        rl_kitas_area.setOnClickListener(null);
        ll_take_kitas_photo.setVisibility(View.GONE);
        iv_retake_card.setVisibility(View.VISIBLE);
        btn_confirm.setVisibility(View.VISIBLE);
        iv_card.setImageBitmap(bitmap);
    }
    /**
     * 手持身份证照片
     * @param bitmap
     */
    private void onHoldIDPhoto(Bitmap bitmap) {
        if (bitmap == null){
            return;
        }
        holdPhotoBitmap = bitmap;
        rl_hold_kitas_area.setOnClickListener(null);
        ll_take_hold_kitas_photo.setVisibility(View.GONE);
        iv_retake_hold_card.setVisibility(View.VISIBLE);
        btn_confirm.setVisibility(View.VISIBLE);
        iv_hold_card.setImageBitmap(bitmap);
        submitBtnStatus();
    }

    /**
     * 检查提交按钮的状态
     */
    private void submitBtnStatus() {
        String cardNumber = et_card_number.getText().toString();
        if (!TextUtils.isEmpty(et_name.getText().toString()) &&
                !TextUtils.isEmpty(cardNumber) &&
                cardNumber.length() >= 8 &&
                kitasCardBitmap != null && holdPhotoBitmap != null) {
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
        if (TextUtils.isEmpty(name)){
            ToastUtil.showToastShort(getActivity(), getString(R.string.Toast_U0));
            return;
        }
        loadingDialog =  showLoading();
        RnaInfoSchema rnaInfoSchema = new RnaInfoSchema(et_card_number.getText().toString(), name);
        IDVerifySdkMain.getInstance().onRnaSubmitAll(rnaInfoSchema, ImageUtil.bitmapToString(kitasCardBitmap),ImageUtil.bitmapToString(holdPhotoBitmap) , rnaType, null,ProfileUserUtil.getInstance().getSID(),new IDVerifyCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);
                if (TextUtils.equals(code, IDVerifySdk.LOCAL_SUCCESS)){
                    RNASubmitAllRsp resp = RNASubmitAllRsp.decodeJson(RNASubmitAllRsp.class, jsonObject);
                    Intent intent = new Intent(mActivity, EndingIDVerifyActivity.class);
                    intent.putExtra(EndingIDVerifyActivity.PAGE_TYPE, IDVerifySdkMain.RESULT_WAIT);
                    intent.putExtra(Constants.LOCAL_PROMPT_KEY, resp.getTipsSchemas());
                    mActivity.startActivity(intent);
                    mActivity.finish();
                }else {
                    showErrorDialog(errorMsg);
                }
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (kitasCardBitmap != null && !kitasCardBitmap.isRecycled()){
            kitasCardBitmap.recycle();
        }
        if (holdPhotoBitmap != null && !holdPhotoBitmap.isRecycled()){
            holdPhotoBitmap.recycle();
        }
        kitasCardBitmap = null;
        holdPhotoBitmap = null;
    }
}
