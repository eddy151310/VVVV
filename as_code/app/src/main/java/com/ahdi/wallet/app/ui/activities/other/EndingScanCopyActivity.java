package com.ahdi.wallet.app.ui.activities.other;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.wallet.R;
import com.ahdi.lib.utils.base.AppBaseActivity;

/**
 * 扫码结果处理  copy 处理
 */
public class EndingScanCopyActivity extends AppBaseActivity {

    public static final String TAG = EndingScanCopyActivity.class.getSimpleName();

    private String strScanResult ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.ending_activity_scan_cppy);
        Intent intent = getIntent();
        if (intent != null) {
            strScanResult = intent.getStringExtra(Constants.LOCAL_QRCODE_SCAN_RESULT);
        }
        initView(strScanResult);
    }

    private void initView(final String resultScan) {

        initCommonTitle(getString(R.string.ScanResultCopy_A0));
        TextView tv_scan_result = findViewById(R.id.tv_scan_result);
        tv_scan_result.setText(resultScan);
        TextView tv_copy = findViewById(R.id.tv_copy);
        tv_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toCopy(resultScan);
                showToast(getString(R.string.Toast_I0));
                finish();
            }
        });
    }


    /**
     * copy
     */
    private void toCopy(String str) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", str);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}