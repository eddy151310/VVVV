package com.ahdi.wallet.app.main;

import android.content.Context;
import android.content.Intent;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.bean.PhoneAreaCodeBean;
import com.ahdi.wallet.app.callback.PhoneAreaCodeCallBack;
import com.ahdi.wallet.app.schemas.PhoneAreaCodeSchema;
import com.ahdi.wallet.app.ui.activities.login.PhoneAreaCodeActivity;

/**
 * @author zhaohe
 */
public class PhoneAreaCodeSdkMain {

    private static PhoneAreaCodeSdkMain instance;

    public String default_error = "";
    public PhoneAreaCodeSchema[] phoneAreaCodeSchemas;
    public PhoneAreaCodeCallBack callBack;

    private PhoneAreaCodeSdkMain() {
    }

    public static PhoneAreaCodeSdkMain getInstance() {
        if (instance == null) {
            synchronized (PhoneAreaCodeSdkMain.class) {
                if (instance == null) {
                    instance = new PhoneAreaCodeSdkMain();
                }
            }
        }
        return instance;
    }

    /**
     * 选择电话区号
     *
     * @param context
     */
    public void getAreaCodeData(Context context) {
        initDefaultError(context);
        context.startActivity(new Intent(context, PhoneAreaCodeActivity.class));
    }

    /**
     * 初始化默认的错误原因
     *
     * @param context
     */
    private void initDefaultError(Context context) {
        if (context != null) {
            default_error = context.getString(R.string.LocalError_C0);
        }
    }

    /**
     * 统一对外回调
     *
     * @param code     “0”成功 其他失败
     * @param errorMsg 错误描述
     * @param bean     选择的区号
     */
    public void onResultBack(String code, String errorMsg, PhoneAreaCodeBean bean) {
        if (callBack != null) {
            callBack.onResult(code, errorMsg, bean);
            onDestroy();
        }
    }

    /**
     * 清理
     */
    public void onDestroy() {
        callBack = null;
    }

}
