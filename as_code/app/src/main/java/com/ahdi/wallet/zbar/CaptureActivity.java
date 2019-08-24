package com.ahdi.wallet.zbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.ahdi.wallet.app.sdk.OtherSdk;
import com.ahdi.wallet.app.callback.OtherSdkCallBack;
import com.ahdi.wallet.app.response.ScanCheckRsp;
import com.ahdi.wallet.app.schemas.ExtSchema;
import com.ahdi.wallet.app.schemas.ScanCheckSchema;
import com.ahdi.wallet.module.payment.transfer.TransferSdk;
import com.ahdi.wallet.module.payment.transfer.callback.TransferResultCallBack;
import com.ahdi.wallet.module.payment.transfer.response.QueryQRTransferResp;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.qrcode.scan.ScanBaseActivity;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.StatusBarUtil;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.wallet.app.ui.activities.other.EndingScanCopyActivity;
import com.ahdi.wallet.app.ui.activities.scanMerchant.EndingScanWebActivity;
import com.ahdi.wallet.module.payment.transfer.ui.activities.TransPayConfirmActivity;

import org.json.JSONObject;

/**
 * Desc: 1:启动一个SurfaceView作为取景预览
 * 2:开启camera,在后台独立线程中完成扫描任务
 * 3:对解码返回的结果进行处理.
 * 4:释放资源
 * Update by znq on 2016/11/9.
 */
public class CaptureActivity extends ScanBaseActivity {

    private static final String TAG = "CaptureActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatusBar(this, R.color.CC_801A1B24, false);
    }

    private void startActivityCopy(String strScanResult) {
        Intent intent = new Intent(CaptureActivity.this, EndingScanCopyActivity.class);
        intent.putExtra(Constants.LOCAL_QRCODE_SCAN_RESULT, strScanResult);
        startActivity(intent);
        finish();
    }

    private void startActivityWeb(String strScanResult, String postData) {
        Intent intent = new Intent(CaptureActivity.this, EndingScanWebActivity.class);
        intent.putExtra(Constants.LOCAL_QRCODE_SCAN_RESULT, strScanResult);
        if (!TextUtils.isEmpty(postData)) {
            intent.putExtra(Constants.LOCAL_QRCODE_RESULT_POST_DATA, postData);
        }
        startActivity(intent);
        finish();
    }

    @Override
    protected String initTitleText() {
        // 此版本扫码界面无标题, 返回 null.
        return null;
    }

    @Override
    protected String initTitleRightBtnText() {
        return getString(R.string.Scan_A0);
    }

    /**
     * 扫码结果处理
     *
     * @param scanResult
     */
    @Override
    protected void handleScanResult(String scanResult) {
        showLoading("");
        OtherSdk.scanCheck(CaptureActivity.this, GlobalApplication.getApplication().getSID(), scanResult, new OtherSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);
                if (TextUtils.equals(code, OtherSdk.LOCAL_SUCCESS)) {
                    ScanCheckRsp resp = ScanCheckRsp.decodeJson(ScanCheckRsp.class, jsonObject);
                    ScanCheckSchema mSchema = resp.getSchema();
                    String strScanResult = mSchema.Data;
                    if (mSchema.OP.equals(mSchema.OP_TYPE_B)) {
                        if (mSchema.Type.equals(mSchema.TYPE_PayCode)) {
                            //PayCode 情况  需要dialog 提示
                            showTipDialog(mSchema.Msg);
                        } else {
                            startActivityCopy(strScanResult);
                        }
                    } else if (mSchema.OP.equals(mSchema.OP_TYPE_C)) {

                        String query = "";
                        String body = "";
                        ExtSchema extSchema = resp.getExtSchema();
                        if (null != extSchema) {
                            query = extSchema.query;
                            body = extSchema.body;
                        } else {
                            LogUtil.d(TAG, "ExtSchema is null ");
                        }
                        if (mSchema.Type.equals(mSchema.TYPE_TRANSFER)) {
                            transfer(CaptureActivity.this, query);
                        } else {
                            startActivityWeb(strScanResult, body);
                        }
                    }
                } else {
                    showToast(errorMsg);
                    reInitCamera();
                }
            }
        });
    }

    /**
     * 扫他人收款码--- 查询转账对象信息
     *
     * @param transfer?
     */
    public void transfer(Activity mActivity, String transfer) {
        LoadingDialog loadingDialog = showLoading();
        AppMain.getInstance().onQRTransferTarget(mActivity, transfer, new TransferResultCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject, String ID) {
                closeLoading(loadingDialog);
                if (TextUtils.equals(code, TransferSdk.LOCAL_PAY_SUCCESS)) {
                    QueryQRTransferResp resp = QueryQRTransferResp.decodeJson(QueryQRTransferResp.class, jsonObject);
                    Intent intent = new Intent(mActivity, TransPayConfirmActivity.class);
                    intent.putExtra(Constants.LOCAL_NNAME_KEY, resp.getnName());
                    intent.putExtra(Constants.LOCAL_SNAME_KEY, resp.getsName());
                    intent.putExtra(Constants.LOCAL_AMOUNT_KEY, resp.getAmount());
                    intent.putExtra(Constants.LOCAL_UT_KEY, resp.getUt());
                    intent.putExtra(Constants.LOCAL_AVATAR_KEY, resp.getAvatar());
                    startActivity(intent);
                    finish();
                } else {
                    // 转账吐司
                    showToast(errorMsg);
                    reInitCamera();
                }
            }
        });
    }

    @Override
    protected void onBack() {
        ActivityManager.getInstance().finishToMainActivity();
    }


}