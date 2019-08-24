package com.ahdi.wallet.bca.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RelativeLayout;

import com.ahdi.wallet.R;
import com.ahdi.wallet.bca.main.BcaSdkMain;
import com.ahdi.wallet.app.schemas.PayBindSchema;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.bca.xco.widget.BCAEditXCOWidget;
import com.bca.xco.widget.BCARegistrasiXCOWidget;
import com.bca.xco.widget.BCAXCOListener;
import com.bca.xco.widget.XCOEnum;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * BCA绑卡界面
 *
 * 1、绑卡  BCA成功、注册回调之后同步上报
 * 2、修改限额  BCA成功、注册回调异步上报
 * 3、closeWigdet、token过期、BCA界面onBackPressed异步上报（不论绑卡和修改限额）
 */
public class BCAXCOActivity extends AppBaseActivity implements BCAXCOListener {

    private static final String TAG = "BCAXCOActivity";
    private final String KEY_XCOID = "xcoID";
    private final String KEY_CREDENTIALTYPE = "credentialType";
    private final String KEY_CREDENTIALNO = "credentialNo";
    private final String KEY_MAXLIMIT = "maxLimit";

    private final String KEY_INIT_BIND = "BIND";
    private final String KEY_INIT_UPDATE = "UPDATE";
    private final String KEY_INIT_REGISTERED = "REGISTERED";
    private final String KEY_INIT_EXIT = "EXIT";

    private RelativeLayout layoutWidget;
    private BCARegistrasiXCOWidget widgetAdd;
    private BCAEditXCOWidget widgetEdit;

    private PayBindSchema payBCBindSchema;
    private int pageType;//1-绑卡;2-修改卡限额

    //绑卡、修改限额结果
    private String init = "";
    private String method = "";
    private String data = "";
    private String bid = "";//绑卡id
    private boolean isSuccess = false;//绑卡或者修改限额是否成功

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bca);

        Intent intent = getIntent();
        if (intent != null) {
            payBCBindSchema = (PayBindSchema) intent.getSerializableExtra(Constants.LOCAL_KEY_PAY_BC_SCHEMA);
            pageType = intent.getIntExtra(Constants.LOCAL_KEY_BCA_PAGE_TYPE,0);
            bid = intent.getStringExtra(Constants.LOCAL_KEY_BANK_CARD_ID);
        }
        initView();
    }

    private void initView() {

        layoutWidget = findViewById(R.id.layoutWidget);
        if (pageType == Constants.LOCAL_BCA_PAGE_BIND_CARD){
            initCommonTitle(getString(R.string.BankCardBindSDK_A0));
            init = KEY_INIT_BIND;
            if (payBCBindSchema != null){
                widgetAdd = new BCARegistrasiXCOWidget(this, getBcaEnv(payBCBindSchema.getEnv()));
                widgetAdd.setListener(this);
                layoutWidget.addView(widgetAdd);
                LogUtil.e(TAG, "accessToken: " + payBCBindSchema.getAccessToken());
                LogUtil.e(TAG, "userID: " + String.valueOf(payBCBindSchema.getUid()));
                widgetAdd.openWidget(payBCBindSchema.getAccessToken(),
                        payBCBindSchema.getApiKey(),
                        payBCBindSchema.getApiSecret(),
                        String.valueOf(payBCBindSchema.getUid()),
                        payBCBindSchema.getMerchantId());
            }else {
                LogUtil.d(TAG,"---服务端返回参数错误----");
                finish();
            }

        }else if (pageType == Constants.LOCAL_BCA_PAGE_MODIFY_LIMIT){
            initCommonTitle(getString(R.string.BankCardBindSDKEdit_A0));
            init = KEY_INIT_UPDATE;
            if (payBCBindSchema != null){
                widgetEdit = new BCAEditXCOWidget(this, getBcaEnv(payBCBindSchema.getEnv()));
                widgetEdit.setListener(this);
                layoutWidget.addView(widgetEdit);
                LogUtil.e(TAG, "accessToken: " + payBCBindSchema.getAccessToken());
                LogUtil.e(TAG, "userID: " + String.valueOf(payBCBindSchema.getUid()));
                widgetEdit.openWidget(payBCBindSchema.getAccessToken(),
                        payBCBindSchema.getApiKey(),
                        payBCBindSchema.getApiSecret(),
                        String.valueOf(payBCBindSchema.getUid()),
                        payBCBindSchema.getMerchantId(),
                        payBCBindSchema.getXcoid());
            }else {
                LogUtil.d(TAG,"---服务端返回参数错误----");
                finish();
            }
        }
    }

    /**
     * 获取bca的开发环境
     * @param type
     * @return
     */
    private String getBcaEnv(String type){
        if (TextUtils.equals(type, Constants.BCA_WIDGET_ENV_PROD)){
            return XCOEnum.ENVIRONMENT.PROD;
        }else {
            return XCOEnum.ENVIRONMENT.DEV;
        }
    }

    /**
     * @param XCOID          XCOID是一个有效的标识符的凭据BCA成功注册。
     * @param credentialType 凭证类型:DC:借记卡
     * @param credentialNo   debitcard CredentialNo:借记卡号码或信用卡或信用卡号码
     * @param maxLimit       每天最大限制为这个XCO ID
     */
    @Override
    public void onBCASuccess(String XCOID, String credentialType, String credentialNo, String maxLimit) {
        LogUtil.d(TAG, "onBCASuccess  XCOID: " + XCOID
                + " ,onBCASuccess credentialType: " + credentialType
                + " ,onBCASuccess credentialNo: " + credentialNo
                + " ,onBCASuccess maxLimit: " + maxLimit);

        isSuccess = true;
        method = "onBCASuccess";
        try {
            JSONObject object = new JSONObject();
            object.put(KEY_XCOID, XCOID);
            object.put(KEY_CREDENTIALTYPE, credentialType);
            object.put(KEY_CREDENTIALNO, credentialNo);
            object.put(KEY_MAXLIMIT, maxLimit);
            data = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d(TAG,"onBCASuccess Bdata json 异常");
        }
    }

    /**
     * @param tokenStatus 这个值表示令牌使用的部件是过期的。
     */
    @Override
    public void onBCATokenExpired(String tokenStatus) {
        LogUtil.e(TAG, "onBCATokenExpired tokenStatus: " + tokenStatus);
        //此时是BCA界面异常，此时应关闭BCA界面
        init = KEY_INIT_EXIT;
        method = "onBCATokenExpired";
    }

    /**
     * @param XCOID XCOID是一个有效的标识符的凭据BCA成功注册。
     */
    @Override
    public void onBCARegistered(String XCOID) {
        LogUtil.d(TAG, " onBCARegistered-------> XCOID:" + XCOID);

        if (pageType == Constants.LOCAL_BCA_PAGE_BIND_CARD){
            isSuccess = true;
        }
        init = KEY_INIT_REGISTERED;
        method = "onBCARegistered";
        if (!TextUtils.isEmpty(XCOID)) {
            try {
                JSONObject object = new JSONObject();
                object.put(KEY_XCOID, XCOID);
                data = object.toString();
            } catch (JSONException e) {
                e.printStackTrace();
                LogUtil.d(TAG, "onBCARegistered Bdata json 异常");
            }
        }
    }

    @Override
    public void onBCACloseWidget() {
        LogUtil.e(TAG, " onBCACloseWidget------->");

        if(TextUtils.isEmpty(method)){
            method = "onBCACloseWidget";
        }
        report();
    }

    @Override
    public void onBackPressed() {
        LogUtil.e(TAG, " onBackPressed------->");

        if (!TextUtils.isEmpty(init) && !TextUtils.isEmpty(method)){
            report();
        }else {
            init = KEY_INIT_EXIT;
            method = "userExit";
            BcaSdkMain.getInstance().bcaResultAsyncNotify(init,method, "",bid);
            BcaSdkMain.getInstance().onBankCardResultFail("onBackPressed");
            finish();
        }
    }

    /**
     * 开始上报
     */
    private void report(){
        //绑卡时成功或者已注册都需要同步上报
        if (isSuccess){
            BcaSdkMain.getInstance().bcaResultSyncNotify(this, init, method, data, bid);
        }else {
            BcaSdkMain.getInstance().bcaResultAsyncNotify(init, method, data, bid);
            BcaSdkMain.getInstance().onBankCardResultFail("fail");
            finish();
        }
    }
}
