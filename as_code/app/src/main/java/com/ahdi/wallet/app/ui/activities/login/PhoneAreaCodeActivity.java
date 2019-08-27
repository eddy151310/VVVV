package com.ahdi.wallet.app.ui.activities.login;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.sdk.PhoneAreaCodeSdk;
import com.ahdi.wallet.app.sdk.UserSdk;
import com.ahdi.wallet.app.bean.PhoneAreaCodeBean;
import com.ahdi.wallet.app.main.PhoneAreaCodeSdkMain;
import com.ahdi.wallet.app.main.UserSdkMain;
import com.ahdi.wallet.app.HttpReqApp;
import com.ahdi.wallet.app.request.PhoneAreaCodeReq;
import com.ahdi.wallet.app.response.PhoneAreaCodeRsp;
import com.ahdi.wallet.app.schemas.PhoneAreaCodeSchema;
import com.ahdi.wallet.app.ui.adapters.PhoneAreaCodeAdapter;
import com.ahdi.lib.utils.widgets.SideBarIndex;
import com.ahdi.lib.utils.listener.OnItemClickListener;
import com.ahdi.lib.utils.utils.AmountUtil;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.ToolUtils;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 选择电话区号
 *
 * @author zhaohe
 */
public class PhoneAreaCodeActivity extends AppBaseActivity {

    private static final String TAG = PhoneAreaCodeActivity.class.getSimpleName();

    private RecyclerView phone_code_list;

    private PhoneAreaCodeAdapter adapter;
    private List<PhoneAreaCodeSchema> phoneCodeList = new ArrayList<>();
    private Map<String, Integer> indexMap = new HashMap<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.activity_phone_area_code);
        initCommonTitle(getString(R.string.PhoneAreaCode_A0));
        initView();
        initData();
    }

    public void initView() {
        phone_code_list = findViewById(R.id.phone_code_list);
        phone_code_list.setLayoutManager(new LinearLayoutManager(PhoneAreaCodeActivity.this));
        adapter = new PhoneAreaCodeAdapter(phoneCodeList);
        phone_code_list.setAdapter(adapter);

        SideBarIndex sideBar = findViewById(R.id.sidrbar);
        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBarIndex.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                if (TextUtils.isEmpty(s) || indexMap == null) {
                    return;
                }
                if (indexMap.containsKey(s)) {
                    int position = indexMap.get(s);
                    if (position >= 0) {
                        phone_code_list.scrollToPosition(position);
                    }
                }
            }
        });

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!ToolUtils.isCanClick()) {
                    return;
                }
                PhoneAreaCodeSchema schema = phoneCodeList.get(position);
                onResultBack(PhoneAreaCodeSdk.LOCAL_SUCCESS, "", new PhoneAreaCodeBean(schema.name, schema.code));
                finish();
            }
        });
    }

    public void initData() {
        updateUI();

        long version = 0;
        try {
            version = AmountUtil.parseLong(AppGlobalUtil.getInstance().getString(this, Constants.LOCAL_PHONE_AREA_KEY));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PhoneAreaCodeReq request = new PhoneAreaCodeReq(version);
        LoadingDialog loadingDialog = showLoading();
        HttpReqApp.getInstance().getPhoneAreaCode(request, new HttpReqTaskListener() {
            @Override
            public void onPostExecute(JSONObject json) {
                loadingDialog.dismiss();
                if (json != null) {
                    LogUtil.e(TAG, TAG, json.toString());
                }
                PhoneAreaCodeRsp resp = PhoneAreaCodeRsp.decodeJson(PhoneAreaCodeRsp.class, json);
                if (resp != null) {
                    if (TextUtils.equals(resp.getmHeader().retCode, UserSdk.LOCAL_PAY_SUCCESS)) {
                        if (resp.version > 0 && resp.phoneAreaCodeSchemas != null && resp.phoneAreaCodeSchemas.length > 0) {
                            AppGlobalUtil.getInstance().putString(AppGlobalUtil.getInstance().getContext(), Constants.LOCAL_PHONE_AREA_KEY, resp.version + "");
                            PhoneAreaCodeSdkMain.getInstance().phoneAreaCodeSchemas = resp.phoneAreaCodeSchemas;
                            AppGlobalUtil.getInstance().putString(AppGlobalUtil.getInstance().getContext(), Constants.LOCAL_PHONE_AREA_DATA_KEY, json.toString());
                        }
                        updateUI();
                    } else {
                        showToast(resp.getmHeader().retMsg);
                    }
                } else {
                    LogUtil.d(TAG, TAG + "解析之后为空");
                    showToast(UserSdkMain.getInstance().default_error);
                }
            }

            @Override
            public void onError(JSONObject json) {
                loadingDialog.dismiss();
                LogUtil.e(TAG, json == null ? TAG + "onError(JSONObject e)" : json.toString());
                showToast(json.optString(Constants.MSG_KEY));
            }
        });
    }

    private void updateUI() {
        indexMap.clear();

        PhoneAreaCodeSchema[] phoneAreaCodeList = PhoneAreaCodeSdkMain.getInstance().phoneAreaCodeSchemas;
        if (phoneAreaCodeList == null || phoneCodeList.size() == 0) {
            try {
                String respStr = AppGlobalUtil.getInstance().getString(AppGlobalUtil.getInstance().getContext(), Constants.LOCAL_PHONE_AREA_DATA_KEY);
                PhoneAreaCodeRsp resp = PhoneAreaCodeRsp.decodeJson(PhoneAreaCodeRsp.class, new JSONObject(respStr));
                if (resp != null) {
                    phoneAreaCodeList = resp.phoneAreaCodeSchemas;
                }
            } catch (Exception e) {
                AppGlobalUtil.getInstance().putString(AppGlobalUtil.getInstance().getContext(), Constants.LOCAL_PHONE_AREA_KEY, "0");
                e.printStackTrace();
            }
        }
        if (phoneAreaCodeList == null) {
            return;
        }
        phoneCodeList.clear();
        String firstLetter = "";
        PhoneAreaCodeSchema schema = null;
        for (int i = 0; i < phoneAreaCodeList.length; i++) {
            schema = phoneAreaCodeList[i];
            if (!TextUtils.equals(firstLetter, schema.firstLetter)) {
                schema.isShowFirstLetter = true;
                firstLetter = schema.firstLetter;
                indexMap.put(firstLetter, i);
            }
            phoneCodeList.add(schema);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onResultBack(PhoneAreaCodeSdk.LOCAL_USER_CANCEL, "", null);
    }


    public void onResultBack(String code, String errorMsg, PhoneAreaCodeBean bean) {
        PhoneAreaCodeSdkMain.getInstance().onResultBack(code, errorMsg, bean);
        finish();
    }
}
