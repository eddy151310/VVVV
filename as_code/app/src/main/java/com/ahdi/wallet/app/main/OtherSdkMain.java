package com.ahdi.wallet.app.main;

import android.content.Context;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.HttpReqApp;
import com.ahdi.wallet.app.request.BannerInfoReq;
import com.ahdi.wallet.app.request.ScanCheckReq;
import com.ahdi.wallet.cashier.bean.ReportDataBean;
import com.ahdi.wallet.app.callback.OtherSdkCallBack;
import com.ahdi.wallet.app.listener.other.BannerInfoListener;
import com.ahdi.wallet.app.listener.other.ClientConfigListener;
import com.ahdi.wallet.app.listener.other.GetBankVAListener;
import com.ahdi.wallet.app.listener.other.ScanCheckListener;
import com.ahdi.wallet.app.listener.other.UserAgreementListener;
import com.ahdi.wallet.app.request.ConfigReq;
import com.ahdi.wallet.app.request.GetBankVAReq;
import com.ahdi.wallet.cashier.requset.PayReportReq;
import com.ahdi.wallet.app.request.UserAgreementReq;
import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONObject;

/**
 * @author zhaohe
 */
public class OtherSdkMain {

    private static final String TAG = OtherSdkMain.class.getSimpleName();
    private static OtherSdkMain instance;
    public String default_error = "";

    private OtherSdkMain() {
    }

    public static OtherSdkMain getInstance() {
        if (instance == null) {
            synchronized (OtherSdkMain.class) {
                if (instance == null) {
                    instance = new OtherSdkMain();
                }
            }
        }
        return instance;
    }


    /**
     * 获取配置
     *
     * @param context
     * @param sid
     * @param cfgVer
     */
    public void getConfig(Context context, String sid, String cfgVer, OtherSdkCallBack callBack) {
        initDefaultError(context);
        HttpReqApp.getInstance().getConfig(new ConfigReq(sid, cfgVer), new ClientConfigListener(callBack));
    }


    /**
     * 扫码结果处理
     *
     * @param context
     * @param sid
     * @param scanCode
     */
    public void scanCheck(Context context, String sid, String scanCode, OtherSdkCallBack callBack) {
        initDefaultError(context);
        HttpReqApp.getInstance().onScanCheck(new ScanCheckReq(sid, scanCode), new ScanCheckListener(callBack));
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
     * 清理
     */
    public void onDestroy() {
        instance = null;
    }

    /**
     * 用户协议内容加载
     *
     * @param context
     * @param action
     * @param callBack
     */
    public void getUserAgreement(Context context, String action, OtherSdkCallBack callBack) {
        initDefaultError(context);
        UserAgreementReq userAgreementRequest = new UserAgreementReq(action);
        HttpReqApp.getInstance().onGetUserAgreement(userAgreementRequest, new UserAgreementListener(callBack));
    }

    /**
     * 获取首页banner信息
     *
     * @param context
     * @param param
     * @param callBack
     */
    public void getBannerInfo(Context context, String param, OtherSdkCallBack callBack) {
        initDefaultError(context);
        BannerInfoReq bannerInfoRequest = new BannerInfoReq(param);
        HttpReqApp.getInstance().bannerInfo(bannerInfoRequest, new BannerInfoListener(callBack));
    }

    /**
     * 获取bankva url
     *
     * @param context
     * @param sid
     * @param type
     * @param callBack
     */
    public void getBankVaUrl(Context context, String type, String sid, OtherSdkCallBack callBack) {
        initDefaultError(context);
        GetBankVAReq bankVARequest = new GetBankVAReq(type, sid);
        HttpReqApp.getInstance().getBankVaUrl(bankVARequest, new GetBankVAListener(callBack));
    }

    /**
     * 支付上报
     *
     * @param reportType 上报消息类型:
     *                   pay:  从第三方支付的app或sdk返回后，上报的内容。
     *                   exit: 用户退出收银台时返回的内容。
     * @param reportData
     * @param sid
     */
    public void payReport(String reportType, ReportDataBean reportData, String sid) {
        HttpReqApp.getInstance().onPayReport(new PayReportReq(reportType, reportData, sid), new HttpReqTaskListener() {

            @Override
            public void onPostExecute(JSONObject json) {
                if (json != null) {
                    LogUtil.e(TAG, "支付上报响应数据: " + json.toString());
                }
            }

            @Override
            public void onError(JSONObject json) {
                if (json != null) {
                    LogUtil.e(TAG, "支付上报响应数据: " + json.toString());
                }
            }
        });
    }
}
