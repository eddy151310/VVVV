package com.ahdi.wallet.app.main;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Base64;

import com.ahdi.wallet.app.sdk.AccountSdk;
import com.ahdi.wallet.app.callback.AccountSdkCallBack;
import com.ahdi.wallet.app.response.OpenTouchIDPayRsp;
import com.ahdi.wallet.app.schemas.TouchSchema;
import com.ahdi.lib.utils.cryptor.EncryptUtil;
import com.ahdi.lib.utils.fingerprint.FingerprintSDK;
import com.ahdi.lib.utils.fingerprint.callback.FingerprintPaymentCallback;
import com.ahdi.lib.utils.fingerprint.callback.FingerprintUnlockCallback;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.TouchIDStateUtil;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.lib.utils.widgets.dialog.CommonDialog;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.callback.OpenTouchIDCallback;

import org.json.JSONObject;

import javax.crypto.Cipher;

/**
 * Date: 2019/1/8 下午3:53
 * Author: kay lau
 * Description:
 */
public class TouchIDManager {

    private static final String TAG = TouchIDManager.class.getSimpleName();
    private static TouchIDManager instance;

    private TouchIDManager() {
    }

    public synchronized static TouchIDManager getInstance() {
        if (instance == null) {
            instance = new TouchIDManager();
        }
        return instance;
    }

    /**
     * 开启指纹解锁登录
     */
    public void onStartTouchIDUnlock(Activity activity, String lName, OpenTouchIDCallback callback) {
        if (activity == null) {
            LogUtil.e(TAG, "activity == null");
            return;
        }
        FingerprintSDK.startAuthenticateUnlock(activity, lName, FingerprintSDK.FINGERPRINT_LOGIN_START, new FingerprintUnlockCallback() {
            @Override
            public void onSuccess(Cipher cipher) {
                String iv = Base64.encodeToString(cipher.getIV(), Base64.URL_SAFE).trim();
                if (callback != null) {
                    callback.onCallback(FingerprintSDK.CODE_SUCCESS);
                }
                ToastUtil.showToastCustom(activity, activity.getString(R.string.Toast_Z0));
                saveUnlockLoginState(activity, iv);
                LogUtil.e(TAG, "------开启指纹解锁成功------");
            }

            @Override
            public void onFailed(int code, String errMsg) {
                saveUnlockLoginState(activity, "");
                if (callback != null) {
                    callback.onCallback(code);
                }
                if (code == FingerprintSDK.CODE_0) {
                    // 指纹验证失败3次, 吐司提示
                    ToastUtil.showToastAtCenterShort(activity, activity.getString(R.string.Toast_B1));

                } else if (code == FingerprintSDK.CODE_5) {
                    // 指纹验证失败5次系统锁定
                    showErrorDialog(activity, activity.getString(R.string.DialogMsg_U0));

                }
                LogUtil.e(TAG, "------开启指纹解锁失败------");
            }
        });
    }

    /**
     * 验证指纹解锁登录
     *
     * @param activity
     * @param lName
     * @param callBack
     */
    public void onVerifyTouchIDUnlock(Activity activity, String lName, OpenTouchIDCallback callBack) {
        FingerprintSDK.startAuthenticateUnlock(activity, lName, FingerprintSDK.FINGERPRINT_LOGIN_VERIFY, new FingerprintUnlockCallback() {
            @Override
            public void onSuccess(Cipher cipher) {
                LogUtil.e(TAG, "------验证指纹解锁onSuccess  ------ ");
                if (callBack != null) {
                    callBack.onCallback(FingerprintSDK.CODE_SUCCESS);
                }
            }

            @Override
            public void onFailed(int code, String errMsg) {
                LogUtil.e(TAG, "------验证指纹解锁失败onFailed ------ code = " + code);
                if (callBack != null) {
                    callBack.onCallback(code);
                }
            }
        });
    }

    /**
     * 保存用户开启指纹成功的lname加密值
     *
     * @param context
     * @param lname_encode_str
     */
    private void saveUnlockLoginState(Context context, String lname_encode_str) {
        if (context == null) {
            LogUtil.e(TAG, "context == null");
            return;
        }
        String LName = AppGlobalUtil.getInstance().getLName(context);
        TouchIDStateUtil.setTouchIDUnlockState(context, LName, lname_encode_str);
    }

    /**
     * 保存用户开启指纹成功的rsa私钥
     *
     * @param context
     * @param privateKey
     */
    public void savePaymentState(Context context, String privateKey) {
        if (context == null) {
            LogUtil.e(TAG, "context == null");
            return;
        }
        String LName = AppGlobalUtil.getInstance().getLName(context);
        TouchIDStateUtil.setTouchIDPaymentState(context, LName, privateKey);
    }

    /**
     * 关闭指纹解锁登录
     *
     * @param context
     */
    public void closeLoginFinger(Context context) {
        if (context == null) {
            LogUtil.e(TAG, "context == null");
            return;
        }
        saveUnlockLoginState(context, "");
    }

    /**
     * 关闭指纹支付
     *
     * @param context
     */
    public void closePayFinger(Context context) {
        if (context == null) {
            LogUtil.e(TAG, "context == null");
            return;
        }
        String LName = AppGlobalUtil.getInstance().getLName(context);
        TouchIDStateUtil.clearTouchIDPayData(context, LName);
    }

    public void showErrorDialog(Context context, String errorMsg) {
        if (context == null) {
            LogUtil.e(TAG, "context == null");
            return;
        }
        new CommonDialog
                .Builder(context)
                .setMessage(errorMsg)
                .setMessageCenter(true)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.DialogButton_B0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    /**
     * 开启指纹支付---指纹验证
     *
     * @param activity
     * @param payPwdCipher
     * @param random
     * @param callback
     */
    public void onStartVerifyFingerprint(Activity activity, String payPwdCipher, String random, OpenTouchIDCallback callback) {
        if (activity == null) {
            LogUtil.e(TAG, "onStartVerifyFingerprint activity == null");
            return;
        }
        String lName = AppGlobalUtil.getInstance().getLName(activity);
        FingerprintSDK.startAuthenticatePayment(activity, lName, FingerprintSDK.FINGERPRINT_PAY_START,
                new FingerprintPaymentCallback() {

                    @Override
                    public void onSuccess(String iv, String publicKey, String privateKey) {
                        // 指纹验证成功, 调用指纹支付开启接口
                        // sign = sha256Hex(pubKey+&+随机数)
                        String sign = EncryptUtil.SHA256(publicKey + "&" + random);
                        LogUtil.e(TAG, "publicKey: " + publicKey);
                        onOpenTouchIDPay(activity, lName, publicKey, iv, payPwdCipher, sign, callback);
                    }

                    @Override
                    public void onFailed(int code, String errMsg) {
                        LogUtil.e(TAG, "------开启指纹支付失败------" + errMsg);
                        if (code == FingerprintSDK.CODE_0) {
                            // 指纹验证失败3次, 吐司提示
                            ToastUtil.showToastAtCenterShort(activity, activity.getString(R.string.Toast_B1));

                        } else if (code == FingerprintSDK.CODE_5) {
                            // 指纹验证失败5次系统锁定
                            showErrorDialog(activity, activity.getString(R.string.DialogMsg_U0));
                        }
                    }
                });
    }

    /**
     * 开启指纹支付---调用开启指纹接口
     *
     * @param activity
     * @param lname
     * @param publicKey
     * @param iv
     * @param payPwdCipher
     * @param sign
     * @param callback
     */
    private void onOpenTouchIDPay(Activity activity, String lname, String publicKey, String iv,
                                  String payPwdCipher, String sign, OpenTouchIDCallback callback) {
        if (activity == null) {
            LogUtil.e(TAG, "onOpenTouchIDPay activity == null");
            return;
        }
        LoadingDialog loadingDialog = LoadingDialog.showDialogLoading(activity, activity.getString(R.string.DialogTitle_C0));
        AccountSdk.openTouchIDPay(activity, publicKey, payPwdCipher, sign,
                GlobalApplication.getApplication().getSID(), new AccountSdkCallBack() {
                    @Override
                    public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                        OpenTouchIDPayRsp rsp = OpenTouchIDPayRsp.decodeJson(OpenTouchIDPayRsp.class, jsonObject);
                        if (rsp != null && TextUtils.equals(code, AccountSdk.LOCAL_PAY_SUCCESS)) {
                            TouchSchema touchschema = rsp.getTouchSchema();
                            if (touchschema != null) {
                                if (callback != null) {
                                    // 更新指纹支付开关状态
                                    callback.onCallback(FingerprintSDK.CODE_SUCCESS);
                                }
                                ToastUtil.showToastCustom(activity, activity.getString(R.string.Toast_Z0));
                                // 保存支付凭证数据
                                savePaymentState(activity, iv);
                                TouchIDStateUtil.saveTouchIDPayKey(activity, lname, touchschema.Key);
                                TouchIDStateUtil.saveTouchIDPayVer(activity, lname, touchschema.Ver);

                                LogUtil.e(TAG, "------开启指纹支付成功------");
                                loadingDialog.dismiss();
                            } else {
                                openTouchIDPayError(activity, errorMsg, callback);
                                loadingDialog.dismiss();
                            }
                        } else {
                            openTouchIDPayError(activity, errorMsg, callback);
                            loadingDialog.dismiss();
                        }
                    }
                });
    }

    private void openTouchIDPayError(Activity activity, String errorMsg, OpenTouchIDCallback callback) {
        if (callback != null) {
            // 更新指纹支付开关状态
            callback.onCallback(FingerprintSDK.CODE_6);
        }
        closePayFinger(activity);
        ToastUtil.showToastShort(activity, errorMsg);
        LogUtil.e(TAG, errorMsg);
        LogUtil.e(TAG, "------开启指纹支付失败------");
    }

}
