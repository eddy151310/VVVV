package com.ahdi.lib.utils.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ahdi.lib.utils.R;
import com.ahdi.lib.utils.bean.SchemaBean;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.SchemaUtil;

/**
 * Date: 2018/9/13 上午9:48
 * Author: kay lau
 * Description:
 */
public class WebBaseActivity extends AppBaseActivity {

    public static final String TAG = WebBaseActivity.class.getSimpleName();

    protected static final String QUERY_URL_TRUE = "true";

    protected WebView mWebView;
    protected TextView tv_title;
    protected ProgressBar pb_bar;
    protected String url = "";

    protected View web_error_view;
    protected FrameLayout webViewParent;

    protected ImageView iv_web_error_icon;
    protected TextView tv_web_error_info, tv_web_error_code, tv_network_error_handle;

    // strScanResult postData 用于扫描商户码h5页面
    protected String strScanResult;
    protected String postData;
    protected Button title_bar_back;

    // 用于消息界面
    protected boolean isGetMsgHomeUrl = true;

    private Button btn_close;

    protected int receivedErrorCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        //初始化intent数据
        initIntentData(getIntent());

        setContentView(R.layout.activity_web);

        initView();

    }

    private void initIntentData(Intent intent) {
        if (intent != null) {
            url = intent.getStringExtra(Constants.LOCAL_WEB_VIEW_URL_KEY);
            strScanResult = intent.getStringExtra(Constants.LOCAL_QRCODE_SCAN_RESULT);
            postData = intent.getStringExtra(Constants.LOCAL_QRCODE_RESULT_POST_DATA);
        }
    }

    protected void initView() {

        initTitle();

        initTitleBar();

        //进度条
        pb_bar = findViewById(R.id.pb_bar);

        //h5加载错误页面
        web_error_view = findViewById(R.id.web_error_view);
        iv_web_error_icon = findViewById(R.id.iv_web_error_icon);
        tv_web_error_info = findViewById(R.id.tv_web_error_info);
        tv_web_error_code = findViewById(R.id.tv_web_error_code);
        tv_network_error_handle = findViewById(R.id.tv_network_error_handle);
        tv_network_error_handle.setOnClickListener(this);

        webViewParent = findViewById(R.id.fl_webView);
        mWebView = new WebView(getApplicationContext());
        webViewParent.addView(mWebView);

        initData();
    }

    protected void initTitle() {
        tv_title = initTitleTextView("");
    }

    protected void initTitleBar() {
        title_bar_back = initTitleBack();
        title_bar_back.setOnClickListener(this);

        btn_close = findViewById(R.id.btn_next);
        btn_close.setBackgroundResource(R.drawable.selector_btn_title_close);
        btn_close.setOnClickListener(this);
    }

    protected void initData() {
        LogUtil.d(TAG, "打开网页，url=" + url);
        if (SchemaUtil.isHttpSchema(url)) {
            initWebViewSetting(url);
        } else {
            finish();
        }
    }

    protected void initWebViewSetting(String url) {

        initWebSettings();

        // 内容的渲染需要webviewChromClient去实现，
        initWebChromeClient();

        initWebViewClient();

        loadUrl(url);
    }

    protected void loadUrl(String url) {
        LogUtil.e(TAG, "loadUrl: " + url);
        mWebView.loadUrl(url);
    }

    protected void initWebViewClient() {
        mWebView.setWebViewClient(new CustomWebViewClient());
    }

    /**
     * 展示页面加载的进度条
     * 通过标题判断http错误码, 用于展示自定义错误界面
     */
    private void initWebChromeClient() {
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                LogUtil.e(TAG, "titleText: " + title);
                setWebViewTitle(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                onRefreshProgress(newProgress);
                if (isShowCloseBtn()){
                    onShowCloseBtn();
                }
            }
        });
    }

    /**
     * 是否显示右边的关闭按钮.(绑卡页面不需要)
     * @return
     */
    public boolean isShowCloseBtn(){
        return true;
    }

    protected void setWebViewTitle(String title) {
        if (tv_title != null) {
            tv_title.setText(title);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected void initWebSettings() {
        WebSettings settings = mWebView.getSettings();

        //获取本地UserAgent;
        String userAgentString = settings.getUserAgentString();
        LogUtil.d(TAG, "UserAgent: " + settings.getUserAgentString());
        //更新 UserAgent;   每次打开webview 更新本地的 userAgent
        settings.setUserAgentString(AppGlobalUtil.getInstance().upDateUserAgent(userAgentString));
        LogUtil.d(TAG, "UserAgent 重新赋值后: " + settings.getUserAgentString());
        // 启用javascript
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDomStorageEnabled(true);
        settings.setTextZoom(100);

    }

    /**
     * 刷新进度
     */
    private void onRefreshProgress(int newProgress) {
        if (pb_bar == null) {
            return;
        }
        if (newProgress == 100) {
            pb_bar.setVisibility(View.GONE);

        } else {
            pb_bar.setVisibility(View.VISIBLE);
            pb_bar.setProgress(newProgress);
        }
    }

    /**
     * 显示制自定义错误界面
     */
    protected void showLoadErrorPage() {
        // 加载页面失败时, 显示错误布局.
        showErrorPageCommon();
    }

    protected void showErrorPageCommon() {
        webViewParent.setVisibility(View.GONE);
        web_error_view.setVisibility(View.VISIBLE);
        tv_web_error_info.setVisibility(View.VISIBLE);
        tv_web_error_code.setVisibility(View.VISIBLE);
        tv_network_error_handle.setVisibility(View.VISIBLE);
        iv_web_error_icon.setVisibility(View.VISIBLE);

        tv_web_error_info.setText(getString(R.string.General_M0));
        // 显示Please try again
        tv_web_error_code.setText(getString(R.string.General_N0));
        // 显示刷新按钮
        tv_network_error_handle.setText(getString(R.string.General_P0));
        iv_web_error_icon.setImageResource(R.mipmap.common_network_error);
    }

    @Override
    public void onClick(View view) {
        if (!isCanClick()) {
            return;
        }
        int id = view.getId();
        if (id == R.id.btn_back) {
            onBackPressed();

        } else if (id == R.id.tv_network_error_handle) {
            onHandleNetWorkError();

        } else if (id == R.id.btn_next) {
            finish();
        }
    }

    private void onHandleNetWorkError() {
        if (!isGetMsgHomeUrl) {
            isGetMsgHomeUrl = true;
            getMsgHomeUrl();

        } else {
            // 尝试重新加载
            if (receivedErrorCode < 0) {
                // 隐藏错误页的控件, 显示空白页
                hiddenErrorPageView();
                receivedErrorCode = 0;
            }
            LogUtil.e(TAG, "mWebView.reload()-------->");
            mWebView.reload();
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            ViewGroup parent = (ViewGroup) mWebView.getParent();
            if (parent != null) {
                parent.removeView(mWebView);
            }
            ((ViewGroup) getWindow().getDecorView()).removeAllViews();
            mWebView.setVisibility(View.GONE);
            mWebView.stopLoading();
            mWebView.getSettings().setJavaScriptEnabled(false);
            mWebView.setTag(null);
            mWebView.clearHistory();
            mWebView.clearCache(true);
            mWebView.clearView();
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
    }

    /**
     * 获取个人消息首页的url
     */
    protected void getMsgHomeUrl() {

    }

    /**
     * 刷新title上关闭按钮的显示与隐藏
     */
    private void onShowCloseBtn() {
        if (mWebView == null || btn_close == null) {
            return;
        }
        if (mWebView.canGoBack() && btn_close.getVisibility() == View.INVISIBLE) {
            btn_close.setVisibility(View.VISIBLE);

        } else if (!mWebView.canGoBack() && btn_close.getVisibility() == View.VISIBLE) {
            btn_close.setVisibility(View.INVISIBLE);
        }
    }

    protected void hiddenErrorPageView() {
        tv_web_error_info.setVisibility(View.INVISIBLE);
        tv_web_error_code.setVisibility(View.INVISIBLE);
        tv_network_error_handle.setVisibility(View.INVISIBLE);
        iv_web_error_icon.setVisibility(View.INVISIBLE);
    }

    /**
     * 执行ahdipay的操作。
     * WebBaseActivity里面doScheme是空方法，需要在各个子类中自己实现具体操作
     *
     * @param schema
     */
    public void doScheme(String schema) {

    }

    /**
     * 打开第三方app
     *
     * @param schema
     */
    public void openApp(String schema) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(schema));
            startActivity(intent);
        } catch (Exception e) {
            LogUtil.d(TAG, "打开第三方应用失败： " + e.getMessage());
        }
    }

    public class CustomWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LogUtil.d(TAG, "---loadURL解析----url:" + url);

            if (SchemaUtil.isHttpSchema(url)){
                return false;
            }else {
                SchemaBean mSchemaBean = SchemaUtil.doScheme(url);
                if (mSchemaBean.getType() == SchemaUtil.TYPE_DEFAULT) {
                    openApp(url);
                    return true;
                } else {
                    doScheme(url);
                    return true;
                }
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            LogUtil.e(TAG, "onPageStarted------------> ");
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            LogUtil.e(TAG, "---------- M- ------------errorCode: " + errorCode);
            LogUtil.e(TAG, "---------- M- ------------failingUrl: " + failingUrl);
            onShowError(errorCode);
        }

        private void onShowError(int errorCode) {
            receivedErrorCode = errorCode;
            showLoadErrorPage();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            int errorCode = error.getErrorCode();
            LogUtil.e(TAG, "---------- M+ ------------errorCode: " + errorCode);
            onShowError(errorCode);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            LogUtil.e(TAG, "onPageFinished------------> ");
            if (receivedErrorCode >= 0) {
                // 加载页面成功时, 显示webView, 隐藏错误布局.
                webViewParent.setVisibility(View.VISIBLE);
                web_error_view.setVisibility(View.GONE);
            }
        }
    }
}
