package com.ahdi.wallet.app.ui.activities.bankCard;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.sdk.BankCardSdk;
import com.ahdi.wallet.app.main.BankCardSdkMain;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.base.WebBaseActivity;
import com.ahdi.lib.utils.bean.SchemaBean;
import com.ahdi.lib.utils.utils.CleanConfigUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.SchemaUtil;

import java.util.Map;

/***
 * 绑卡页面
 * webview 漏洞分析文章：https://blog.csdn.net/carson_ho/article/details/64904635
 */
public class BankCardPayWebActivity extends WebBaseActivity {

    public static final String TAG = BankCardPayWebActivity.class.getSimpleName();

    // qrindo://bbc/back?result=true  绑卡单点被退出schema
    /**
     * 是否可以回退：当绑卡成功进入结果页时，禁止back键，隐藏返回按钮
     */
    private boolean isCanBack = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        getWindow().setBackgroundDrawableResource(R.color.CC_F1F2F6); //代码设置bg

        LogUtil.e(TAG, "---onCreate()---");
    }

    @Override
    protected void initTitle() {
        initTitleTextView(getString(R.string.BankCardBindH5_A0));
        initTitleBack().setBackgroundResource(R.drawable.selector_btn_title_close);
    }

    @Override
    protected void initData() {
        if (SchemaUtil.isHttpSchema(url)){
            initWebViewSetting(url);
        }else {
            doScheme(url);
        }
    }

    @Override
    public boolean isShowCloseBtn(){
        return false;
    }

    @Override
    public void doScheme(String url) {
        LogUtil.d(TAG, "---doSchemeURL解析----url:" + url);
        try {
            SchemaBean schemaBean = SchemaUtil.doScheme(url);
            if (schemaBean.getType() == SchemaUtil.TYPE_BBC_SSO) {
                CleanConfigUtil.cleanAllConfig();//清理登录信息
                ActivityManager.getInstance().openLoginActivity(this);
                finish();
            } else if (schemaBean.getType() == SchemaUtil.TYPE_BBC_BACK) {
                queryBankInfo(schemaBean.getParams());
            }
        } catch (Exception e) {
            LogUtil.d(TAG, "URL解析失败：" + e.toString());
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    protected void initWebSettings() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        mWebView.addJavascriptInterface(new JsToAndroid(), "altopay");
    }

    @Override
    protected void setWebViewTitle(String title) {
        // 标题不用动态设置为webView标题, 使用默认标题.
    }

    public void queryBankInfo(String query) {
        String result = "";
        Map<String, Object> map = SchemaUtil.parsUrlParams(query);
        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = (String) entry.getValue();
                if (TextUtils.equals(key, "result")) {
                    result = value;
                }
            }
        }
        if (QUERY_URL_TRUE.equals(result)) {
            ActivityManager.getInstance().finishBindBankCardActivity();
            onCallBack(BankCardSdk.LOCAL_PAY_SUCCESS);
            finish();
        }
    }

    private void onCallBack(String bindState) {
        BankCardSdkMain.getInstance().onResultBack(bindState, "", null);
    }

    @Override
    public void onBackPressed() {
        if (!isCanBack) {
            return;
        }
        super.onBackPressed();
        onBack();
    }

    private void onBack() {
        if (!isCanBack) {
            return;
        }
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            onCallBack(BankCardSdk.LOCAL_PAY_QUERY_CANCEL);
            finish();
        }
    }

    /**
     * js与android代码的交互类
     */
    public class JsToAndroid extends Object {

        // 定义JS需要调用的方法
        // 被JS调用的方法必须加入@JavascriptInterface注解

        /**
         * 隐藏title上的返回
         */
        @JavascriptInterface
        public void hideBack() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    title_bar_back.setVisibility(View.INVISIBLE);
                    isCanBack = false;
                }
            });
        }

        /**
         * 账号被锁定，弹窗退出
         *
         * @param code
         * @param msg
         */
        @JavascriptInterface
        public void logout(String code, String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showOutDialog(msg);
                }
            });
        }
    }
}