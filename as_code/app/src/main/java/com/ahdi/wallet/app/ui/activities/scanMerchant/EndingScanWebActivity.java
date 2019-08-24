package com.ahdi.wallet.app.ui.activities.scanMerchant;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.ahdi.wallet.cashier.PayCashierSdk;
import com.ahdi.wallet.cashier.callback.PaymentSdkCallBack;
import com.ahdi.wallet.cashier.response.pay.PayResultQueryResponse;
import com.ahdi.lib.utils.base.WebBaseActivity;
import com.ahdi.lib.utils.bean.SchemaBean;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.SchemaUtil;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.GlobalApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * 扫码结果处理: 扫merchant二维码
 */
public class EndingScanWebActivity extends WebBaseActivity {

    private static final String TAG = EndingScanWebActivity.class.getSimpleName();
    private static final String PAY_RESULT_OK = "ok";
    private static final String PAY_RESULT_FAIL = "fail";
    private static final String PAY_RESULT_CANCEL = "cancel";
    private static final String PAY_RESULT_ERROR = "error";
    private static final String PAY_RESULT_SYSERR = "syserr";
    private static final String PAY_RESULT_OTHER = "other";

    private static final String URL_PARAMS_KEY_PAY_PARAMS = "t";
    private static final String URL_PARAMS_KEY_CALLBACK = "callback";
    private static final String URL_PARAMS_KEY_CONTEXT = "_context";
    private static final String URL_PARAMS_KEY_CLOSE = "_close";
    private String urlParamsCallback;
    private String urlParamsContext;
    private String urlParamsClose;
    private String urlParamsPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        LogUtil.e(TAG, "---onCreate()---");
    }

    @Override
    protected void initData() {
        if (SchemaUtil.isHttpSchema(strScanResult)) {
            initWebViewSetting(strScanResult);
        } else {
            mWebView.setVisibility(View.GONE);
            doScheme(strScanResult);
        }
    }

    @Override
    protected void loadUrl(String url) {
        LogUtil.e(TAG, "loadUrl: " + url);
        if (!TextUtils.isEmpty(postData)) {
            mWebView.postUrl(url, postData.getBytes());
        } else {
            mWebView.loadUrl(url);
        }
    }

    /**
     * 发起支付--扫merchant二维码
     *
     * @param params
     * @paramd sid
     */
    public void pay(String params, String sid) {
        LoadingDialog loadingDialog = showLoading();
        PayCashierSdk.startPay(this, params, sid, Constants.LOCAL_FROM_PAY, new PaymentSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);
                String payStatus = "";
                PayResultQueryResponse response = PayResultQueryResponse.decodeJson(PayResultQueryResponse.class, jsonObject);
                if (response != null && TextUtils.equals(PayCashierSdk.LOCAL_PAY_SUCCESS, code)) {
                    // 主扫商户码支付完成, 打开ending页面
                    openEndingScanMer(response.payResult, jsonObject.toString());
                    payStatus = PAY_RESULT_OK;
                } else if (TextUtils.equals(PayCashierSdk.LOCAL_PAY_USER_CANCEL, code)) {
                    //"<-------用户主动关闭收银台, 支付取消（ 查询结果时，pay_fail 、 plat_fail会引导跳转到支付方式列表 ）-----> "
                    payStatus = PAY_RESULT_CANCEL;
                } else if (TextUtils.equals(PayCashierSdk.LOCAL_PAY_QUERY_CANCEL, code)) {
                    //<-------支付结果查询 , 弹窗点击了取消查询 （支付中、查询时断网）----------->
                    // 支付结果查询 弹窗点击取消, 打开ending页面
                    openEndingScanMer(PayCashierSdk.LOCAL_PAY_QUERY_CANCEL, errorMsg);
                    payStatus = PAY_RESULT_OTHER;
                } else if (TextUtils.equals(PayCashierSdk.LOCAL_PAY_NETWORK_EXCEPTION, code)) {
                    //<-------本地网络异常，批价失败----------->
                    showToast(errorMsg);
                } else {
                    //<-------  服务器返回的 批价失败 ---------->
                    LogUtil.e(TAG, "<-------  服务器返回的 批价失败 ---------->  " + errorMsg);
                    payStatus = PAY_RESULT_ERROR;
                    showToast(errorMsg);
                }

                //调用JS
                if (!TextUtils.isEmpty(urlParamsCallback) && !TextUtils.isEmpty(payStatus)) {
                    onLoadJS(payStatus);
                } else {
                    LogUtil.e(TAG, "主扫商户码 urlParamsCallback is null");
                }
            }
        });
    }

    private void openEndingScanMer(String payResult, String data) {
        Intent intent = new Intent(this, EndingScanMerchantActivity.class);
        intent.putExtra(Constants.LOCAL_RESULT_KEY, payResult);
        if (payResult.equals(PayCashierSdk.LOCAL_PAY_QUERY_CANCEL)) {
            intent.putExtra(Constants.MSG_KEY, data);
        } else {
            intent.putExtra(Constants.DATA_KEY, data);
        }
        intent.putExtra(Constants.LOCAL_IS_OPEN_WEB_VIEW_KEY, TextUtils.equals(urlParamsClose, "false"));
        startActivity(intent);
        finish();
    }

    @Override
    public void doScheme(String url) {
        SchemaBean schemaBean = SchemaUtil.doScheme(url);
        if (schemaBean.getType() == SchemaUtil.TYPE_SCAN_PAY && !TextUtils.isEmpty(schemaBean.getParams())) {
            parsUrlParams(schemaBean.getParams());
            pay(urlParamsPay, GlobalApplication.getApplication().getSID());
        } else {
            initWebViewSetting(url);
        }
    }

    private void parsUrlParams(String param) {
        Map<String, Object> map = SchemaUtil.parsUrlParams(param);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = (String) entry.getValue();
            if (TextUtils.equals(key, URL_PARAMS_KEY_CALLBACK)) {
                urlParamsCallback = value;
            } else if (TextUtils.equals(key, URL_PARAMS_KEY_CONTEXT)) {
                urlParamsContext = value;
            } else if (TextUtils.equals(key, URL_PARAMS_KEY_CLOSE)) {
                urlParamsClose = value;
            } else if (TextUtils.equals(key, URL_PARAMS_KEY_PAY_PARAMS)) {
                urlParamsPay = value;
            }
        }
    }

    private String buildJSRequestParams(String payStatus, String context) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("pay_result", payStatus);
            jsonObject.put("_context", context);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String JsonStr = jsonObject.toString();
        try {
            JsonStr = new JSONObject(JsonStr).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return JsonStr;
    }

    private void onLoadJS(String payStatus) {
        String JSRequestParamsJson = buildJSRequestParams(payStatus, urlParamsContext);
        String js = "javascript:" + urlParamsCallback + "(" + JSRequestParamsJson + ");";
        LogUtil.e(TAG, "<------------后台调用JS------------>");
        LogUtil.e(TAG, js);
        mWebView.loadUrl(js);
    }
}