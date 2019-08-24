package com.ahdi.wallet.module.QRCode.ui.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ahdi.wallet.cashier.PayCashierSdk;
import com.ahdi.wallet.module.QRCode.bean.PayCodePINConfirmBean;
import com.ahdi.wallet.module.QRCode.bean.PayCodeInitBean;
import com.ahdi.wallet.module.QRCode.response.PayCodePINConfirmRsp;
import com.ahdi.wallet.module.QRCode.response.PayCodeInitRsp;
import com.ahdi.wallet.module.QRCode.response.PayCodeUseQueryRsp;
import com.ahdi.wallet.cashier.response.pay.PayResultQueryResponse;
import com.ahdi.wallet.module.QRCode.schemas.AuthCodePaySchema;
import com.ahdi.wallet.module.QRCode.QRCodeSdk;
import com.ahdi.wallet.module.QRCode.callback.QRCodeSdkCallBack;
import com.ahdi.wallet.app.schemas.PayOrderSchema;
import com.ahdi.wallet.app.schemas.TouchSchema;
import com.ahdi.wallet.app.utils.ConstantsPayment;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.cryptor.EncryptUtil;
import com.ahdi.lib.utils.cryptor.aes.AesKeyCryptor;
import com.ahdi.lib.utils.cryptor.aes.MD5;
import com.ahdi.lib.utils.cryptor.rsa.RSAHelper;
import com.ahdi.lib.utils.fingerprint.FingerprintSDK;
import com.ahdi.lib.utils.fingerprint.callback.FingerprintPaymentCallback;
import com.ahdi.lib.utils.imagedown.ImageDownUtil;
import com.ahdi.lib.utils.paycode.PayCodeBean;
import com.ahdi.lib.utils.paycode.PayCodeUtil;
import com.ahdi.lib.utils.utils.AmountUtil;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.DeviceUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.QRCodeUtil;
import com.ahdi.lib.utils.utils.StatusBarUtil;
import com.ahdi.lib.utils.utils.StringUtil;
import com.ahdi.lib.utils.utils.ToolUtils;
import com.ahdi.lib.utils.utils.TouchIDStateUtil;
import com.ahdi.lib.utils.widgets.dialog.CommonDialog;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;
import com.ahdi.wallet.module.QRCode.ui.widgets.PayCodeDialog;
import com.ahdi.wallet.util.ScreenShotListenManager;
import com.ahdi.wallet.zbar.CaptureActivity;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.json.JSONObject;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class PayQRCodeActivity extends AppBaseActivity {

    private static final String TAG = PayQRCodeActivity.class.getSimpleName();
    /**
     * 相机权限的请求码
     */
    private static final int REQUEST_CAMERA_PERMISSION = 101;
    private static final int REQUEST_OPEN_RECEIVE = 102;
    public static final int RESULT_CLOSE_RECEIVE = 103;
    private static final int REQUEST_OPEN_INPUT_PWD = 104;
    public static final int RESULT_CLOSE_INPUT_PWD = 105;
    public static final int RESULT_CLOSE_BCA_OTP = 106;
    public static final int REQUEST_OPEN_BCA_OTP = 107;

    private static final int PAY_CODE_REFRESH_TIME = 60 * 1000; // 二维码刷新间隔时间
    private static final int PAY_CODE_IS_TRADE_TIME = 3 * 1000; // 查询二维码是否使用间隔时间
    private static final int QUERY_INTERVAL_TIME = 5 * 1000;    // 支付结果查询间隔时间
    private volatile int queryCount = 2;                        // 支付结果查询失败之后继续查询的次数

    private static final int REFRESH_PAY_CODE = 1;              // 刷新pay码
    private static final int QUERY_PAY_CODE_IS_TRADE = 2;       // 查询pay码是否使用
    private static final int CREATE_QR_CODE = 3;                // 初始化pay码
    private static final int CONTINUE_QUERY_PAY_RESULT = 4;     // 继续查询支付结果

    private ImageView iv_bar_code, iv_qr_code, iv_bank_icon_select, iv_qr_refresh, iv_select_arrow;
    private RelativeLayout rl_qr_refresh_bg, rl_select_pay_mode;
    private QRCodeHandler handler;
    private String key;
    private long client;
    private long timeDif;
    private int position = 0;
    private String payTypeName;
    private TextView tv_select_mode;
    private LoadingDialog loadingDialog;
    private LoadingDialog loadingDialog_S;
    private PayCodeDialog dialog;
    private String bankIcon;
    private ScreenShotListenManager manager;

    //title  scan bg
    private static final int RES_ID_BACK_BACK = R.drawable.selector_btn_title_scan;
    private ArrayList<String> countList;
    private ArrayList<String> newCountList;

    private String publicKeyStr;
    private String privateKeyStr;
    private ArrayList<AuthCodePaySchema> authCodePaySchemas;
    private String OT;
    private String TT;

    private PayCodeUseQueryListener payCodeUseQueryListener = new PayCodeUseQueryListener();
    private String ex;
    private boolean isVerifyFingerprint;
    private String iv;
    private String transID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.activity_pay_qr_code);
        StatusBarUtil.setStatusBar(this, R.color.CC_D63031, false);
        initTitleView();
        handler = new QRCodeHandler();
        initView();
        onCreateRsaKey();
        initScreenShotListener();
    }


    //创建本地rsa密钥对
    private void onCreateRsaKey() {
        Map<String, Object> keys;
        try {
            keys = RSAHelper.getKeys();
            for (Map.Entry<String, Object> entry : keys.entrySet()) {
                String key = entry.getKey();
                Object obj = entry.getValue();
                if (TextUtils.equals(key, RSAHelper.PUBLIC_KEY)) {
                    PublicKey publicKey = (PublicKey) obj;
                    publicKeyStr = Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
                } else if (TextUtils.equals(key, RSAHelper.PRIVATE_KEY)) {
                    PrivateKeyInfo pki = (PrivateKeyInfo) obj;
                    RSAPrivateKey pkcs1Key = RSAPrivateKey.getInstance(pki.parsePrivateKey());
                    privateKeyStr = Base64.encodeToString(pkcs1Key.getEncoded(), Base64.DEFAULT);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "onCreateRsaKey Exception: " + e.toString());
        }
        onInitPayCode(showLoading());
    }

    public void onInitPayCode(LoadingDialog loadingDialog) {
        String randomKey = StringUtil.getStringRandom(32);
        String key = randomKey + "|" + MD5.md5(publicKeyStr);
        String clientKey = AesKeyCryptor.encodeClientKey(key); // RSA
        clientKey = "2|1|" + clientKey;
        PayCodeInitBean bean = new PayCodeInitBean(randomKey, clientKey, publicKeyStr, privateKeyStr);

        QRCodeSdk.initPayCode(this, bean, GlobalApplication.getApplication().getSID(), new QRCodeSdkCallBack() {

            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);
                PayCodeInitRsp response = PayCodeInitRsp.decodeJson(PayCodeInitRsp.class, jsonObject);
                onParsePayCodeData(code, errorMsg, response, privateKeyStr, randomKey);
            }
        });
    }

    private void onParsePayCodeData(String code, String errorMsg, PayCodeInitRsp response, String finalPrivateKey, String randomKey) {
        String serverKey = "";
        String payPwdVer = "";
        String clientStr = "";
        if (response != null && QRCodeSdk.LOCAL_PAY_SUCCESS.equals(code)) {

            rl_qr_refresh_bg.setVisibility(View.GONE);
            rl_select_pay_mode.setEnabled(true);
            iv_select_arrow.setEnabled(true);

            if (authCodePaySchemas == null) {
                authCodePaySchemas = new ArrayList<>();
            }
            Collections.addAll(authCodePaySchemas, response.authCodePaySchemas);
            if (authCodePaySchemas != null && authCodePaySchemas.size() > 0) {
                payTypeName = authCodePaySchemas.get(0).Bank;
                bankIcon = authCodePaySchemas.get(0).BankIcon;
            } else {
                showToast(getString(R.string.Toast_B0).concat(Constants.LOCAL_PAY_CODE_PAY_METHOD_NULL));
                showRefreshQRCode();
                return;
            }
            try {
                String serverData = RSAHelper.decryptByPrivateKey(response.serverData, finalPrivateKey);

                if (TextUtils.isEmpty(serverData)) {
                    showToast(getString(R.string.Toast_B0).concat(Constants.LOCAL_SERVER_DATA_DECODE_FAIL));
                    return;
                }

                if (serverData.length() < 3) {
                    showToast(getString(R.string.Toast_B0).concat(Constants.LOCAL_SERVER_DATA_INVALID_FORMAT));
                    return;
                }

                String[] strs = serverData.split("\\|");
                for (int i = 0; i < strs.length; i++) {
                    if (i == 0) {
                        serverKey = strs[i];
                    } else if (i == 1) {
                        payPwdVer = strs[i];
                    } else if (i == 2) {
                        clientStr = strs[i];
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(TAG, "Exception: " + e.toString());

            }

            String keySha256 = EncryptUtil.SHA256(randomKey + serverKey);
            key = EncryptUtil.SHA256(keySha256 + payPwdVer);

            if (TextUtils.isEmpty(keySha256) || TextUtils.isEmpty(key)) {
                showToast(getString(R.string.Toast_B0).concat(Constants.LOCAL_SHA_EXCEPTION));
                return;
            }

            if (!TextUtils.isEmpty(clientStr)) {
                client = AmountUtil.parseLong(clientStr);
            }
            long currentTime = System.currentTimeMillis();
            timeDif = response.timeStamp * 1000 - currentTime;
            // 初始化二维码
            firstInitPayCode();

        } else if (TextUtils.equals(code, Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION)) {
            showToast(errorMsg);
            showRefreshQRCode();

        } else {
            showErrorDialog(errorMsg);
            showRefreshQRCode();
        }
    }

    private void showRefreshQRCode() {
        closeLoading(loadingDialog);
        destroyHandler();
        rl_qr_refresh_bg.setVisibility(View.VISIBLE);
        iv_qr_refresh.setVisibility(View.VISIBLE);
        tv_select_mode.setTextColor(ToolUtils.getColor(this, R.color.CC_919399));
        rl_select_pay_mode.setEnabled(false);
        iv_select_arrow.setEnabled(false);
    }

    private void initTitleView() {
        // 初始化title控件
        View view = findViewById(R.id.title);
        view.setBackgroundResource(R.color.CC_00000000);
        TextView tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.ShowPayQR_A0));
        tv_title.setTextColor(ToolUtils.getColor(this, R.color.CC_FFFFFF));
        View btn_backView = view.findViewById(R.id.btn_back);
        btn_backView.setBackgroundResource(R.drawable.selector_btn_title_back_white);
        btn_backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCanClick()) {
                    return;
                }
                onCloseToHome();
            }
        });

        View btn_scanView = view.findViewById(R.id.btn_next);
        btn_scanView.setBackgroundResource(RES_ID_BACK_BACK);
        btn_scanView.setVisibility(View.VISIBLE);
        btn_scanView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCanClick()) {
                    return;
                }
                checkCameraPer();
            }
        });
    }

    public void initView() {
        iv_bar_code = findViewById(R.id.iv_bar_code);
        iv_qr_code = findViewById(R.id.iv_qr_code);
        rl_qr_refresh_bg = findViewById(R.id.rl_qr_refresh_bg);
        rl_qr_refresh_bg.setVisibility(View.GONE);
        iv_qr_refresh = findViewById(R.id.iv_qr_refresh);
        iv_qr_refresh.setOnClickListener(this);

        iv_select_arrow = findViewById(R.id.iv_select_arrow);
        iv_select_arrow.setEnabled(false);

        tv_select_mode = findViewById(R.id.tv_pay_code_pay_mode);
        iv_bank_icon_select = findViewById(R.id.iv_bank_icon_select);

        findViewById(R.id.rl_receive_code).setOnClickListener(this);

        rl_select_pay_mode = findViewById(R.id.rl_select_pay_mode);
        rl_select_pay_mode.setOnClickListener(this);
    }

    private void firstInitPayCode() {
        loadingDialog = showLoading();
        initPayCode();
    }

    private void initPayCode() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onCreateFirstPayCode();
            }
        });
    }

    private String getPayCode() {
        String nextStr = AppGlobalUtil.getInstance().getString(this, Constants.LOCAL_PAY_CODE_NEXT_KEY);
        int nextLocal = ToolUtils.parseInt(nextStr);
        PayCodeBean payCodeBean = PayCodeUtil.getInstance().getPayCode(key, client, timeDif, position, nextLocal);
        if (payCodeBean == null) {
            return "";
        }
        int next = payCodeBean.getNext();
        AppGlobalUtil.getInstance().putString(this, Constants.LOCAL_PAY_CODE_NEXT_KEY, String.valueOf(next + 1));
        addCounter(payCodeBean.getCounter(), next);
        LogUtil.e(TAG, "initPayCode clientId: " + client);
        return payCodeBean.getPayCode();
    }

    private String updatePayCode() {
        String nextStr = AppGlobalUtil.getInstance().getString(this, Constants.LOCAL_PAY_CODE_NEXT_KEY);
        int nextLocal = ToolUtils.parseInt(nextStr);
        PayCodeBean payCodeBean = PayCodeUtil.getInstance().updatePayCode(key, client, timeDif, position, nextLocal);
        if (payCodeBean == null) {
            return "";
        }
        int next = payCodeBean.getNext();
        addCounter(payCodeBean.getCounter(), next);
        AppGlobalUtil.getInstance().putString(this, Constants.LOCAL_PAY_CODE_NEXT_KEY, String.valueOf(next + 1));
        LogUtil.e(TAG, "updatePayCode clientId: " + client);
        return payCodeBean.getPayCode();
    }

    private void addCounter(long counter, int next) {
        String timeSlot = counter + "_" + next;
        LogUtil.e(TAG, "timeSlot: " + timeSlot);
        if (countList == null) {
            countList = new ArrayList<>();
        }
        if (newCountList == null) {
            newCountList = new ArrayList<>();
        }
        countList.add(timeSlot);
        if (countList.size() > 10) {
            newCountList.clear();
            for (int i = 0; i < countList.size(); i++) {
                if (i >= countList.size() - 10) {
                    newCountList.add(countList.get(i));
                }
            }
        } else {
            if (newCountList.size() > 0) {
                newCountList.clear();
            }
            newCountList.addAll(countList);
        }
    }

    @Override
    public void onClick(View view) {
        if (!isCanClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.rl_receive_code:
                // 打开收款码
                openReceive();
                break;
            case R.id.rl_select_pay_mode:
                // 打开选择支付方式
                showPayModeDialog();
                break;
            case R.id.iv_qr_refresh:
                // 初始化二维码
                clearTimeSlot();
                onInitPayCode(showLoading());
                iv_qr_refresh.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    private void openReceive() {
        Intent intent = new Intent(this, ReceiveActivity.class);
        startActivityForResult(intent, REQUEST_OPEN_RECEIVE);
        destroyHandler();
    }

    /**
     * 检查摄像头权限，做相应的处理
     */
    private void checkCameraPer() {

        if (Build.VERSION.SDK_INT < 23 || checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openScanUI();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            openAppSettingDetails(getString(R.string.DialogMsg_B0));
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    private void openScanUI() {
        if (DeviceUtil.cameraIsCanUse()) {
            Intent intent = new Intent(this, CaptureActivity.class);
            startActivity(intent);
            destroyHandler();
            finish();
        } else {
            openAppSettingDetails(getString(R.string.DialogMsg_B0));
        }
    }

    /**
     * 查询二维码是否已被扫描, 如果已扫描返回 TT, 查询支付结果
     *
     * @param clientId 客户端pay码生成使用的clientid
     */
    private void queryPayQRCodeTrade(long clientId) {
        if (!TextUtils.isEmpty(GlobalApplication.getApplication().getSID())) {
            QRCodeSdk.payCodeUseQuery(this, clientId, newCountList, GlobalApplication.getApplication().getSID(), payCodeUseQueryListener);
            // 不管上一次有没有响应, 直接继续发一个消息 继续查询.
            onStartPayCodeQueryTask(PAY_CODE_IS_TRADE_TIME);
        } else {
            destroyHandler();
        }

    }

    private class QRCodeHandler extends Handler {

        public QRCodeHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == QUERY_PAY_CODE_IS_TRADE) {
                // 查询二维码是否被使用
                LogUtil.e(TAG, "查询二维码是否被使用");
                queryPayQRCodeTrade(client);

            } else if (msg.what == REFRESH_PAY_CODE) {
                // 刷新pay码: 一分钟刷新一次
                onRefreshPayCode(updatePayCode());

            } else if (msg.what == CREATE_QR_CODE) {
                toCreateQR(msg.obj.toString());

            } else if (msg.what == CONTINUE_QUERY_PAY_RESULT) {
                onStartQueryPayResult();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyHandler();
    }

    private void destroyHandler() {
        if (handler != null) {
            LogUtil.e(TAG, "pay码已停止查询!!!");
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    /**
     * 弹窗关闭之后刷新数据
     *
     * @param positionSelect
     * @param payType
     * @param iconUrl
     */
    private void refreshUI(int positionSelect, String payType, String iconUrl) {
        if (position == positionSelect) {
            return;
        }
        this.position = positionSelect;
        this.payTypeName = payType;
        this.bankIcon = iconUrl;
        tv_select_mode.setText(payTypeName);
        tv_select_mode.setTextColor(ToolUtils.getColor(this, R.color.CC_1A1B24));
        if (!TextUtils.isEmpty(iconUrl)) {
            ImageDownUtil.downBankIconImage(this, iconUrl, iv_bank_icon_select);
        } else {
            iv_bank_icon_select.setImageResource(R.mipmap.common_pay_type_icon_default);
        }
        onRefreshPayCode(updatePayCode());
    }

    /**
     * 获取生成pay码的信息
     */
    private void onCreateFirstPayCode() {
        String payCode = getPayCode();
        LogUtil.e(TAG, "CreateFirst payCode: " + payCode);
        if (TextUtils.isEmpty(payCode)) {
            // 本地刷新pay码失败, 重新初始化pay码接口
            showToast(getString(R.string.Toast_B0).concat(Constants.LOCAL_SHA_EXCEPTION));
            showRefreshQRCode();
            return;
        }
        if (handler == null) {
            handler = new QRCodeHandler();
        }
        handler.obtainMessage(CREATE_QR_CODE, payCode).sendToTarget();

        tv_select_mode.setText(payTypeName);
        tv_select_mode.setTextColor(ToolUtils.getColor(this, R.color.CC_1A1B24));
        ImageDownUtil.downBankIconImage(this, bankIcon, iv_bank_icon_select);
    }

    /**
     * 开启刷新pay码任务, 一分钟刷新一次
     */
    private void onStartPayCodeRefreshTask() {
        if (handler == null) {
            handler = new QRCodeHandler();
        }
        handler.sendEmptyMessageDelayed(REFRESH_PAY_CODE, PAY_CODE_REFRESH_TIME);
    }

    /**
     * 首次生成一维码和二维码Bitmap, 之后关闭loading
     *
     * @param payCode
     */
    private void toCreateQR(String payCode) {
        LogUtil.e(TAG, "---------toCreateQR 首次生成pay码------->");
        checkBitmap(payCode);
        closeLoading(loadingDialog);
    }

    /**
     * 本地刷新QR 等, 一分钟刷新一次
     *
     * @param payCode
     */
    private void onRefreshPayCode(String payCode) {
        LogUtil.e(TAG, "onRefreshPayCode: " + payCode);
        if (TextUtils.isEmpty(payCode)) {
            // 本地刷新pay码失败, 重新初始化pay码接口
            showToast(getString(R.string.Toast_B0).concat(Constants.LOCAL_SHA_EXCEPTION));
            showRefreshQRCode();
            return;
        }
        LogUtil.e(TAG, "-----onRefreshPayCode 本地刷新pay码----->");
        checkBitmap(payCode);
    }

    private void checkBitmap(String payCode) {
        // 先停止之前的任务
        destroyHandler();
        Bitmap barBitmap = QRCodeUtil.getBarBitmap(payCode, iv_bar_code);
        Bitmap qrBitmap = QRCodeUtil.getQRBitmap(payCode, iv_qr_code);
        if (barBitmap == null || qrBitmap == null) { //备注：简单处理 任何一个失败都当作失败  同onCheckPayCode()
            // pay码生成失败展示刷新按钮
            showToast(getString(R.string.Toast_B0).concat(Constants.LOCAL_SHA_EXCEPTION));
            showRefreshQRCode();
            return;
        }

        if (iv_bar_code != null) {
            iv_bar_code.setImageBitmap(barBitmap);
        }

        if (iv_qr_code != null) {
            iv_qr_code.setImageBitmap(qrBitmap);
        }
        // 第一次生成或者是本地一分钟刷新pay码, 立即开始查询.
        onStartPayCodeQueryTask(0);
        onStartPayCodeRefreshTask();
    }

    /**
     * 开启定时任务, 查询pay码是否使用, 三秒钟查询一次
     */
    private void onStartPayCodeQueryTask(long delayMillis) {
        if (handler == null) {
            handler = new QRCodeHandler();
        }
        handler.sendEmptyMessageDelayed(QUERY_PAY_CODE_IS_TRADE, delayMillis);
    }

    private void onBack() {
        if (dialog != null && dialog.isShowing()) {
            onCloseDialog();
        } else {
            onCloseToHome();
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        onBack();
    }

    private void onCloseToHome() {
        destroyHandler();
        finish();
    }

    private void showPayModeDialog() {
        int screenHeight = DeviceUtil.getScreenHeight(this);
        dialog = new PayCodeDialog(this, R.style.ActionSheetDialogStyle, position, authCodePaySchemas, screenHeight);
        dialog.show();// 显示对话框
        dialog.setOnPayCodeDialogListener(new PayCodeDialog.OnPayCodeDialogListener() {
            @Override
            public void onCallBack(int positionSelect, String payTypeName, String iconUrl) {
                if (position != positionSelect) {
                    refreshUI(positionSelect, payTypeName, iconUrl);
                }
            }
        });
    }

    private void onCloseDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void initScreenShotListener() {
        manager = ScreenShotListenManager.newInstance(this);
        manager.setListener(new ScreenShotListenManager.OnScreenShotListener() {
                                public void onShot(String imagePath) {
                                    LogUtil.e(TAG, "*****ScreenShot---->");
                                    onShowDialogScreenShot();
                                }
                            }
        );
    }

    private void onShowDialogScreenShot() {
        new CommonDialog
                .Builder(this)
                .setMessage(getString(R.string.DialogMsg_F0))
                .setCancelable(false)
                .setPositiveButton(getString(com.ahdi.lib.utils.R.string.DialogButton_B0), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (manager != null) {
            manager.startListen();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (manager != null) {
            manager.stopListen();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openScanUI();
        } else if (requestCode == REQUEST_CAMERA_PERMISSION) {
            openAppSettingDetails(getString(R.string.DialogMsg_B0));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OPEN_RECEIVE && resultCode == RESULT_CLOSE_RECEIVE) {
            // 收款码界面返回时, 如果之前是成功的，则刷新
            if (rl_qr_refresh_bg.getVisibility() != View.VISIBLE) {
                onContinueRefreshPayCode();
            }
        } else if (requestCode == REQUEST_OPEN_INPUT_PWD && resultCode == RESULT_CLOSE_INPUT_PWD && data != null) {
            // 支付密码验证界面返回时的处理
            PayPWDConfirmCallback(data);

        } else if (requestCode == REQUEST_OPEN_BCA_OTP && resultCode == RESULT_CLOSE_BCA_OTP && data != null) {
            // bca otp界面返回时的处理
            BCAOtpPayCallback(data);
        }
    }

    private void clearTimeSlot() {
        if (countList != null) {
            countList.clear();
        }
        if (newCountList != null) {
            newCountList.clear();
        }
    }

    private void openInputPwdDialog(String payEx, String tt, String ot, String errMsg) {
        if (!isFirstJumpPIN) {
            return;
        }
        onStopJumpPIN();
        Intent intent = new Intent(this, PayCodeVerifyPINActivity.class);
        if (!TextUtils.isEmpty(payEx)) {
            intent.putExtra(Constants.LOCAL_PAY_EX_KEY, payEx);
        }
        if (!TextUtils.isEmpty(tt)) {
            intent.putExtra(Constants.LOCAL_TT_KEY, tt);
        }
        if (!TextUtils.isEmpty(ot)) {
            intent.putExtra(Constants.LOCAL_OT_KEY, ot);
        }
        if (!TextUtils.isEmpty(errMsg)) {
            intent.putExtra(Constants.MSG_KEY, errMsg);
        }
        if (!TextUtils.isEmpty(iv)) {
            intent.putExtra("iv", iv);
        }
        startActivityForResult(intent, REQUEST_OPEN_INPUT_PWD);
        onBottom_in_Activity();
    }


    private boolean isStartQuery;

    /**
     * 开始查询支付结果
     */
    protected void onStartQueryPayResult() {

        if (loadingDialog_S == null) {
            loadingDialog_S = showWhitePointLoading();
        } else {
            if (!loadingDialog_S.isShowing()) {
                loadingDialog_S = showWhitePointLoading();
            }
        }

        QRCodeSdk.queryPayCodePayResult(this, TT, OT, GlobalApplication.getApplication().getSID(),
                new QRCodeSdkCallBack() {
                    @Override
                    public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                        // 查询支付结果 状态处理
                        handlerQueryResult(code, errorMsg, jsonObject);
                    }
                });
    }

    /**
     * 处理支付结果:
     * 成功,失败, 第三次查询返回支付中:              显示状态以及条目信息
     * 网络异常, 服务端异常(retCode !0), 其他异常:   展示try again 界面, 展示对应错误信息(本地定义)
     *
     * @param code
     * @param errorMsg
     * @param jsonObject
     */
    private void handlerQueryResult(String code, String errorMsg, JSONObject jsonObject) {
        if (jsonObject == null) {
            onContinueQuery(code, errorMsg);
            return;
        }
        PayResultQueryResponse rsp = PayResultQueryResponse.decodeJson(PayResultQueryResponse.class, jsonObject);
        if (rsp != null) {
            if (PayCashierSdk.LOCAL_PAY_SUCCESS.equals(code)) {
                if (TextUtils.equals(rsp.payResult, ConstantsPayment.PAY_OK)
                        || TextUtils.equals(rsp.payResult, ConstantsPayment.PAY_FAIL)
                        || TextUtils.equals(rsp.payResult, ConstantsPayment.PLAT_FAIL)) {
                    // 支付成功 支付失败 平台失败处理
                    closeLoading(loadingDialog_S);
                    openEndingActivity(rsp.payResult, jsonObject.toString());

                } else {
                    // 查不到结果, 继续查询
                    onContinueQuery(ConstantsPayment.PAY_ING, errorMsg);
                }
            } else {
                onContinueQuery(code, errorMsg);
            }
        } else {
            onContinueQuery(code, errorMsg);
        }
    }

    private void openEndingActivity(String payResult, String data) {
        Intent intent = new Intent(this, EndingPayCodeActivity.class);
        intent.putExtra(Constants.LOCAL_RESULT_KEY, payResult);
        if (payResult.equals(PayCashierSdk.LOCAL_PAY_QUERY_CANCEL)) {
            intent.putExtra(Constants.MSG_KEY, data);
        } else {
            intent.putExtra(Constants.DATA_KEY, data);
        }
        intent.putExtra("transID", transID);
        startActivity(intent);
        finish();
    }

    /**
     * 继续查询
     *
     * @param code
     * @param errorMsg
     */
    private void onContinueQuery(String code, String errorMsg) {
        if (queryCount > 0) {
            queryCount--;
            onQueryPayResult();
        } else {
            closeLoading(loadingDialog_S);
            if (TextUtils.equals(code, PayCashierSdk.LOCAL_PAY_NETWORK_EXCEPTION)) {
                // 网络异常 code
                showDialog(getString(R.string.DialogMsg_D0));
            } else if (TextUtils.equals(code, ConstantsPayment.PAY_ING) || TextUtils.equals(code, ConstantsPayment.WAIT)) {
                // code = 0, 查不到结果
                showDialog(getString(R.string.DialogMsg_E0));
            } else {
                // 后台返回的!0 code
                showDialog(errorMsg);
            }
        }
    }

    private void onQueryPayResult() {
        if (handler == null) {
            handler = new QRCodeHandler();
        }
        Message msg = handler.obtainMessage(CONTINUE_QUERY_PAY_RESULT);
        handler.sendMessageDelayed(msg, QUERY_INTERVAL_TIME);
    }

    private void showDialog(String msg) {
        new CommonDialog
                .Builder(this)
                .setMessage(msg)
                .setMessageCenter(true)
                .setCancelable(false)
                .setNegativeButton(getString(R.string.DialogButton_A0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        openEndingActivity(PayCashierSdk.LOCAL_PAY_QUERY_CANCEL, msg);
                    }
                })
                .setPositiveButton(getString(R.string.DialogButton_C0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        queryCount = 2;
                        onStartQueryPayResult();
                        dialog.dismiss();
                    }
                }).show();
    }

    public void showDialogGoBackToHome(String msg) {
        new CommonDialog
                .Builder(this)
                .setMessage(msg)
                .setMessageCenter(true)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.DialogButton_B0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ActivityManager.getInstance().openMainActivity(PayQRCodeActivity.this);
                    }
                }).show();
    }

    /**
     * 打开BCA otp界面
     *
     * @param payOrder
     */
    private void openBCAOtp(PayOrderSchema payOrder) {
        if (!isFirstJumpBCA) {
            return;
        }
        onStopJumpBCA();
        Intent intent = new Intent(this, PayCodeBcaOTPActivity.class);
        intent.putExtra(Constants.LOCAL_OT_KEY, OT);
        intent.putExtra(Constants.LOCAL_TT_KEY, TT);
        intent.putExtra(Constants.LOCAL_PAY_ORDER_KEY, payOrder);
        startActivityForResult(intent, REQUEST_OPEN_BCA_OTP);
    }

    private void BCAOtpPayCallback(Intent data) {
        String result = data.getStringExtra(Constants.LOCAL_RESULT_KEY);
        OT = data.getStringExtra(Constants.LOCAL_OT_KEY);
        TT = data.getStringExtra(Constants.LOCAL_TT_KEY);
        if (TextUtils.equals(result, PayCodeBcaOTPActivity.BCA_OTP_PAY_SUCCESS)) {
            // 查询支付结果
            onStartQueryPayResult();
        } else {
            onContinueRefreshPayCode();
        }
    }

    private void PayPWDConfirmCallback(Intent data) {
        String result = data.getStringExtra(Constants.LOCAL_RESULT_KEY);
        OT = data.getStringExtra(Constants.LOCAL_OT_KEY);
        TT = data.getStringExtra(Constants.LOCAL_TT_KEY);
        if (TextUtils.equals(result, PayCodeVerifyPINActivity.CONFIRM_PAY_PWD_SUCCESS)) {
            // 查询支付结果
            onStartQueryPayResult();

        } else if (TextUtils.equals(result, PayCodeVerifyPINActivity.CONFIRM_PAY_PWD_VERIFY_OTP)) {
            // 打开BCA otp界面
            openBCAOtp((PayOrderSchema) data.getSerializableExtra(Constants.LOCAL_PAY_ORDER_KEY));

        } else {
            onContinueRefreshPayCode();
        }
    }

    /**
     * 是否可以跳转其他界面, 默认是true可以跳转
     * 用于pay码查询的多次响应, 避免多次打开同一个界面
     */
    private boolean isFirstJumpBCA = true;
    private boolean isFirstJumpPIN = true;
    private boolean isFirstJumpTouchID = true;

    /**
     * 查询pay码是否使用
     * 网络延迟的情况, 只要响应结果有跳转界面的操作, 后续的响应不会再做界面跳转.
     */
    private class PayCodeUseQueryListener implements QRCodeSdkCallBack {

        @Override
        public void onResult(String code, String errorMsg, JSONObject jsonObject) {
            PayCodeUseQueryRsp resp = PayCodeUseQueryRsp.decodeJson(PayCodeUseQueryRsp.class, jsonObject);
            if (resp != null && QRCodeSdk.LOCAL_PAY_SUCCESS.equals(code)) {
                TT = resp.tt;
                OT = resp.ot;
                ex = resp.Ex;
                transID = resp.transID;
                if (!TextUtils.isEmpty(TT) && !TextUtils.isEmpty(OT)) {
                    // 停止二维码是否使用查询
                    destroyHandler();
                    if (resp.isConfirmPayPwd) {
                        String lName = AppGlobalUtil.getInstance().getLName(PayQRCodeActivity.this);
                        // 支付凭证版本号没变化
                        if (resp.touchFlag == Constants.TOUCH_FLAG_NOT_CHANGE
                                // 设备支持指纹
                                && FingerprintSDK.isSupport(PayQRCodeActivity.this)
                                // 设备已开启指纹支付
                                && TouchIDStateUtil.isStartTouchIDPayment(PayQRCodeActivity.this, lName)
                                // 设备存在至少一个指纹
                                && FingerprintSDK.isHasFingerprints(PayQRCodeActivity.this)) {
                            if (isVerifyFingerprint) {
                                return;
                            }

                            // 新增指纹时, 验证通过并且支付下单成功, 才会更新指纹sdk的iv, 否则为init_iv_error
                            String iv = FingerprintSDK.getIV(PayQRCodeActivity.this);
                            if (TextUtils.equals(iv, FingerprintSDK.INIT_IV_ERROR)) {
                                LogUtil.e(TAG, "iv 不匹配");
                                onStartTouchIDPay(lName, resp.transID, resp.Ex, FingerprintSDK.FINGERPRINT_PAY_RE_START);
                            } else {
                                onStartTouchIDPay(lName, resp.transID, resp.Ex, FingerprintSDK.FINGERPRINT_PAY_VERIFY);
                            }
                        } else {
                            // 需要采集支付密码
                            openInputPwdDialog(ex, TT, OT, "");
                        }

                    } else if (!TextUtils.isEmpty(resp.PayParam)) {
                        // 不需要采集支付密码, 但是需要打开bca otp
                        openBCAOtp(resp.getPayOrder());

                    } else {
                        // 查询支付结果
                        if (!isStartQuery) {
                            isStartQuery = true;
                            onStartQueryPayResult();
                        }
                    }
                } else {
                    LogUtil.e(TAG, "TT 或者 OT 为空, 继续查询中...");
                }
            } else if (TextUtils.equals(code, Constants.RET_CODE_A206)
                    || TextUtils.equals(code, Constants.RET_CODE_A010)) {
                destroyHandler();
                showDialogGoBackToHome(errorMsg);

            } else {
                LogUtil.e(TAG, "errorMsg: " + errorMsg);
            }
        }
    }

    /**
     * 停止界面跳转标记
     */
    private void onStopJumpBCA() {
        isFirstJumpBCA = false;
    }

    /**
     * 开启界面跳转标记
     */
    private void onStartJumpBCA() {
        isFirstJumpBCA = true;
    }


    /**
     * 停止界面跳转标记
     */
    private void onStopJumpPIN() {
        isFirstJumpPIN = false;
    }

    /**
     * 开启界面跳转标记
     */
    private void onStartJumpPIN() {
        isFirstJumpPIN = true;
    }

    /**
     * 停止界面跳转标记
     */
    private void onStopJumpTouchID() {
        isFirstJumpTouchID = false;
    }

    /**
     * 开启界面跳转标记
     */
    private void onStartJumpTouchID() {
        isFirstJumpTouchID = true;
    }


    /**
     * 继续刷新pay码, 清理TimeSlot, 重新开启界面跳转标记
     */
    private void onContinueRefreshPayCode() {
        isVerifyFingerprint = false;
        clearTimeSlot();
        onStartJumpBCA();
        onStartJumpPIN();
        onStartJumpTouchID();
        onRefreshPayCode(updatePayCode());
    }

    private void onStartTouchIDPay(String lName, String transID, String payEx, int verifyType) {
        if (!isFirstJumpTouchID) {
            return;
        }
        onStopJumpTouchID();
        isVerifyFingerprint = true;
        FingerprintSDK.startAuthenticatePayment(this, lName, verifyType,
                new FingerprintPaymentCallback() {
                    @Override
                    public void onSuccess(String iv, String publicKey, String privateKey) {
                        PayQRCodeActivity.this.iv = iv;
                        try {
                            String touchKeyCipher = TouchIDStateUtil.getTouchIDPayKey(PayQRCodeActivity.this, lName);
                            String touchKey = RSAHelper.decryptByPrivateKey(touchKeyCipher, privateKey);
                            PayCodePINConfirmBean bean = new PayCodePINConfirmBean();
                            bean.setOT(OT);
                            bean.setTT(TT);
                            bean.setPayEx(payEx);
                            bean.setCancel(0);
                            String data = transID + "&" + payEx + "&" + touchKey;
                            String payAuthData = RSAHelper.signWithSHA(data, privateKey);
                            bean.setPayAuth(payAuthData);
                            // 支付确认
                            onAuthCode(bean);
                        } catch (Exception e) {
                            // 新增指纹, 有变化, 重新验证指纹, 验证成功但是解密失败, 打开PIN界面输入密码, 支付下单成功后, 更新指纹支付token
                            openInputPwdDialog(ex, TT, OT, "");
                            LogUtil.e(TAG, "touchKey解密失败/payAuthData签名失败: " + e.toString());
                        }
                    }

                    @Override
                    public void onFailed(int code, String errMsg) {
                        LogUtil.e(TAG, "指纹验证errCode: " + code);
                        if (code == FingerprintSDK.CODE_0) {
                            // 连续验证失败超过3次, 切换到支付密码验证
                            openInputPwdDialog(ex, TT, OT, getString(R.string.PayQRInputPWD_B0));

                        } else if (code == FingerprintSDK.CODE_1
                                || code == FingerprintSDK.CODE_10) {
                            // 点击指纹弹窗的取消按钮或者前两次验证失败，页面弹窗提示用户是否要退出"Are you sure to quit?"
                            showDialogQuit();

                        } else if (code == FingerprintSDK.CODE_5) {
                            // 系统锁定, 失败5次
                            openInputPwdDialog(ex, TT, OT, "");

                        } else if (code == FingerprintSDK.CODE_7) {
                            // 指纹弹窗点击PIN, 主动打开验证密码界面
                            openInputPwdDialog(ex, TT, OT, "");

                        } else if (code == FingerprintSDK.CODE_8) {
                            onStartJumpTouchID(); //当出现新增指纹 需要开启JumpTouchID
                            // 新增指纹, 有变化, 重新验证指纹, 验证成功输入支付密码, 支付下单成功后, 更新指纹支付token
                            onStartTouchIDPay(lName, transID, payEx, FingerprintSDK.FINGERPRINT_PAY_RE_START);

                        }
                    }
                });
    }

    private void showDialogQuit() {
        new CommonDialog.
                Builder(this)
                .setCancelable(false)
                .setMessage(getString(R.string.DialogMsg_H0))
                .setMessageCenter(true)
                .setNegativeButton(getString(R.string.DialogButton_E0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 调用上报接口--支付取消
                        onAuthCodeCancel();
                        onContinueRefreshPayCode();
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(getString(R.string.DialogButton_J0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openInputPwdDialog(ex, TT, OT, "");
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * 支付授权码取消
     */
    private void onAuthCodeCancel() {
        PayCodePINConfirmBean bean = new PayCodePINConfirmBean();
        bean.setOT(OT);
        bean.setTT(TT);
        bean.setPayEx(ex);
        bean.setCancel(1);
        onAuthCode(bean);
    }

    private void onAuthCode(PayCodePINConfirmBean bean) {
        if (bean.getCancel() != 1) {
            loadingDialog = showLoading();
        }
        QRCodeSdk.payCodePINConfirm(this, bean, Constants.PAY_AUTH_TYPE_TK, GlobalApplication.getApplication().getSID(),
                new QRCodeSdkCallBack() {
                    @Override
                    public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                        if (bean.getCancel() == 1) {
                            // 取消上报响应时直接return, 防止网络超时 空指针异常
                            return;
                        }
                        loadingDialog.dismiss();

                        PayCodePINConfirmRsp response = PayCodePINConfirmRsp.decodeJson(PayCodePINConfirmRsp.class, jsonObject);
                        if (response != null && TextUtils.equals(code, QRCodeSdk.LOCAL_PAY_SUCCESS)) {
                            if (!TextUtils.isEmpty(response.PayParam)) {
                                // 不需要采集支付密码, 但是需要打开bca otp
                                openBCAOtp(response.getPayOrder());
                            } else {
                                TouchSchema touchSchema = response.getTouchSchema();
                                if (touchSchema != null) {
                                    String lName = AppGlobalUtil.getInstance().getLName(PayQRCodeActivity.this);
                                    TouchIDStateUtil.saveTouchIDPayKey(PayQRCodeActivity.this, lName, touchSchema.Key);
                                    TouchIDStateUtil.saveTouchIDPayVer(PayQRCodeActivity.this, lName, touchSchema.Ver);
                                }
                                // 开始查询支付结果逻辑
                                onStartQueryPayResult();
                            }
                        } else if (code.equals(Constants.RET_CODE_A206)/*密码被锁定*/
                                || code.equals(Constants.RET_CODE_A010)/*账户被锁定*/) {
                            showDialogGoBackToHome(errorMsg);

                        } else if (code.equals(Constants.RET_CODE_6013)/*pay码支付超时*/
                                || code.equals(Constants.RET_CODE_A203)/*支付密码错误*/) {
                            showDialogError(errorMsg);

                        } else {
                            LogUtil.e(TAG, errorMsg);
                            showToast(errorMsg);
                            // 刷新pay码 继续查询是否使用
                            onContinueRefreshPayCode();
                        }
                    }
                });
    }

    private void showDialogError(String msg) {
        new CommonDialog
                .Builder(this)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.DialogButton_B0), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // 刷新pay码 继续查询是否使用
                        onContinueRefreshPayCode();
                        dialog.dismiss();

                    }
                }).show();
    }
}
