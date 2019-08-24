package com.ahdi.wallet.app.ui.activities.msg;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.ahdi.wallet.app.sdk.MsgSdk;
import com.ahdi.wallet.app.callback.MsgSdkCallBack;
import com.ahdi.wallet.app.response.MsgListRsp;
import com.ahdi.lib.utils.base.WebBaseActivity;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.app.main.AppMain;

import org.json.JSONObject;

/**
 * @author admin
 * 消息界面
 */
public class MessageActivity extends WebBaseActivity {

    private LoadingDialog loadingDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        getMsgHomeUrl();
    }

    @Override
    protected void initTitle() {
        tv_title = initTitleTextView("");
    }

    @Override
    protected void initData() {
        // 获取个人消息首页url失败时, 不关闭当前界面, 显示对应的错误界面
    }

    @Override
    protected void getMsgHomeUrl() {
        isGetMsgHomeUrl = true;
        loadingDialog = showLoading();
        webViewParent.setVisibility(View.VISIBLE);
        web_error_view.setVisibility(View.GONE);
        AppMain.getInstance().getMsgHomeUrl(this, new MsgSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);
                if (TextUtils.equals(code, MsgSdk.LOCAL_SUCCESS)) {
                    MsgListRsp rsp = MsgListRsp.decodeJson(MsgListRsp.class, jsonObject);
                    if (rsp != null && !TextUtils.isEmpty(rsp.url)) {
                        initWebViewSetting(rsp.url);
                    } else {
                        showRefreshView();
                    }
                } else if (TextUtils.equals(code, MsgSdk.LOCAL_NETWORK_EXCEPTION)
                        || TextUtils.equals(code, MsgSdk.LOCAL_SYSTEM_EXCEPTION)) {
                    showRefreshView();
                }
            }
        });
    }

    private void showRefreshView() {
        isGetMsgHomeUrl = false;
        // 显示错误界面---refresh
        showErrorPageCommon();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
        loadingDialog = null;
    }
}
