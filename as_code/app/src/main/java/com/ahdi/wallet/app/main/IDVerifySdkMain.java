package com.ahdi.wallet.app.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.HttpReqApp;
import com.ahdi.wallet.app.request.AuditQRReq;
import com.ahdi.wallet.app.sdk.IDVerifySdk;
import com.ahdi.wallet.app.callback.IDVerifyCallBack;
import com.ahdi.wallet.app.listener.idverify.AuditQRListener;
import com.ahdi.wallet.app.listener.idverify.RNASubmitAllListener;
import com.ahdi.wallet.app.listener.idverify.RnaTypesListener;
import com.ahdi.wallet.app.request.RNASubmitAllReq;
import com.ahdi.wallet.app.request.RnaTypeReq;
import com.ahdi.wallet.app.response.AuditQRRsp;
import com.ahdi.wallet.app.response.RnaTypeRsp;
import com.ahdi.wallet.app.schemas.RnaExtraInfoSchema;
import com.ahdi.wallet.app.schemas.RnaInfoSchema;
import com.ahdi.wallet.app.ui.activities.IDAuth.EndingIDVerifyActivity;
import com.ahdi.wallet.app.ui.activities.IDAuth.IDAuthActivity;
import com.ahdi.wallet.app.ui.activities.IDAuth.IdInfoActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.lib.utils.widgets.dialog.IDGuideDialog;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;

import org.json.JSONObject;

/**
 * @author zhaohe@iapppay.com
 */

public class IDVerifySdkMain {

    private static final String TAG = "IDVerifySdkMain";
    /**
     * 未提交审核
     */
    public static final int RESULT_UNSUBMIT = 0;
    /**
     * 审核成功
     */
    public static final int RESULT_SUCCESS = 1;
    /**
     * 审核未通过
     */
    public static final int RESULT_FAIL = 2;
    /**
     * 审核中
     */
    public static final int RESULT_WAIT = 3;

    private static IDVerifySdkMain instance;
    /**
     * 对外的结果回调
     */
    public IDVerifyCallBack callBack;
    public boolean showSuccessEnding;
    /**
     * 默认的错误原因
     */
    public String default_error = "";
    public Context context = null;
    public String sid;
    public Bitmap bitmap = null;
    public JSONObject auditResultObject = null;
    private IDGuideDialog mIDGuideDialog = null;

    private IDVerifySdkMain() {
    }

    public static IDVerifySdkMain getInstance() {
        if (instance == null) {
            synchronized (IDVerifySdkMain.class) {
                if (instance == null) {
                    instance = new IDVerifySdkMain();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化默认的错误原因
     *
     * @param context
     */
    private void initDefaultError(Context context) {
        if (context != null) {
            default_error = context.getString(com.ahdi.lib.utils.R.string.LocalError_C0);
        }
    }

    /**
     * 查询实名认证类型
     */
    public void onRnaType() {
        LoadingDialog loadingDialog = LoadingDialog.showDialogLoading(context, context.getString(R.string.DialogTitle_C0));
        RnaTypeReq request = new RnaTypeReq(sid);
        HttpReqApp.getInstance().onRnaType(request, new RnaTypesListener(new IDVerifyCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                LoadingDialog.dismissDialog(loadingDialog);
                if (TextUtils.equals(code, IDVerifySdk.LOCAL_SUCCESS)) {
                    RnaTypeRsp response = RnaTypeRsp.decodeJson(RnaTypeRsp.class, jsonObject);
                    if (response != null && response.getTypesSchemas() != null && response.getTypesSchemas().length > 0) {
                        try {
                            Intent intent = new Intent(context, IDAuthActivity.class);
                            intent.putExtra(Constants.LOCAL_TYPES_KEY, response.getTypesSchemas());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            LogUtil.e(TAG, ex.getMessage());
                        }
                        if (mIDGuideDialog != null && mIDGuideDialog.isShowing()) {
                            mIDGuideDialog.dismiss();
                        }
                    }
                } else {
                    ToastUtil.showToastShort(context, errorMsg);
                }
            }
        }));
    }

    /**
     * 查询实名认证审核结果接口
     */
    public void onAuditQR(Context context, String sid) {
        initDefaultError(context);
        this.context = context;
        this.sid = sid;
        AuditQRReq request = new AuditQRReq(sid);
        HttpReqApp.getInstance().onAuditQR(request, new AuditQRListener());
    }

    /**
     * 护照或者暂住证
     *
     * @param rnaInfoSchema
     * @param ImageID       证件照
     * @param imgHandheID   手持证件照
     * @param type          实名认证类型：KTP、Passport或KITAS
     * @param sid
     * @param callBack
     */
    public void onRnaSubmitAll(RnaInfoSchema rnaInfoSchema, String ImageID, String imgHandheID, String type, RnaExtraInfoSchema rnaExtraInfoSchema, String sid, IDVerifyCallBack callBack) {
        RNASubmitAllReq request = new RNASubmitAllReq(rnaInfoSchema, ImageID, imgHandheID, type, rnaExtraInfoSchema, sid);
        HttpReqApp.getInstance().onRnaSubmitAll(request, new RNASubmitAllListener(callBack));
    }

    /**
     * 实名认证审核结果处理
     */
    public void onAuditResult(JSONObject jsonObject, AuditQRRsp response) {
        this.auditResultObject = jsonObject;
        if (response != null) {
            // 0: 未提交1：审核通过2：审核未通过3：审核中      如果有新增状态暂定：~showGuideDialog()  后续和产品商量
            int type = response.getAudit();
            switch (type) {
                case RESULT_SUCCESS:
                    dealSuccess(response);
                    break;
                case RESULT_WAIT:
                    showEndingPage(type, response);
                    break;
                case RESULT_UNSUBMIT:
                case RESULT_FAIL:
                default:
                    showGuideDialog();
                    break;
            }
        }
    }

    /**
     * 显示引导弹窗
     */
    private void showGuideDialog() {
        LogUtil.d(TAG, "显示显示实名认证引导弹窗");
        if (context == null) {
            return;
        }
        mIDGuideDialog = new IDGuideDialog
                .Builder(context)
                .setMessageCenter(false)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.IdDialog_B0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onRnaType();
                    }
                })
                .setNegativeButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onResultBack(IDVerifySdk.LOCAL_USER_CANCEL, "", null);
                        dialog.dismiss();
                    }
                }).showIDDialog();
    }


    /**
     * 实名认证结果成功时的处理逻辑
     * Me界面查询结果成功展示详情页
     * 其他界面查询成功直接callBack
     */
    private void dealSuccess(AuditQRRsp response) {
        if (showSuccessEnding) {
            try {
                Intent intent = new Intent(context, IdInfoActivity.class);
                intent.putExtra(Constants.LOCAL_RNA_INFO_KEY, response.getRnaInfoSchema());
                if (response.getRnaTypeSchema() != null) {
                    intent.putExtra(Constants.LOCAL_TYPE_KEY, response.getRnaTypeSchema().getAbbr());
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception ex) {
                ex.printStackTrace();
                LogUtil.e(TAG, ex.getMessage());
            }
        } else {
            onResultBack(IDVerifySdk.LOCAL_SUCCESS, "", auditResultObject);
        }
    }

    /**
     * 展示实名认证结果页
     *
     * @param pageType 2：审核未通过3：审核中
     */
    private void showEndingPage(int pageType, AuditQRRsp response) {
        try {
            Intent intent = new Intent(context, EndingIDVerifyActivity.class);
            intent.putExtra(EndingIDVerifyActivity.PAGE_TYPE, pageType);
            intent.putExtra(Constants.LOCAL_PROMPT_KEY, response.getTipsSchemas());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
            LogUtil.e(TAG, ex.getMessage());
        }
    }

    /**
     * 统一对外回调
     *
     * @param code
     * @param errorMsg   错误描述
     * @param jsonObject 响应成功时的json数据
     */
    public void onResultBack(String code, String errorMsg, JSONObject jsonObject) {
        if (TextUtils.isEmpty(errorMsg)) {
            errorMsg = jsonObject == null ? "" : jsonObject.optString(Constants.MSG_KEY);
        }
        if (callBack != null) {
            callBack.onResult(code, errorMsg, jsonObject);
        }
        onDestroy();
    }

    /**
     * 清理
     */
    public void onDestroy() {
        callBack = null;
        bitmap = null;
        context = null;
        auditResultObject = null;
        instance = null;
    }

}