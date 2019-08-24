package com.ahdi.wallet.app.ui.activities.IDAuth;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.sdk.IDVerifySdk;
import com.ahdi.wallet.app.bean.IdInfoBean;
import com.ahdi.wallet.app.main.IDVerifySdkMain;
import com.ahdi.wallet.app.schemas.RnaInfoSchema;
import com.ahdi.wallet.app.ui.adapters.IdInfoAdapter;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;

import java.util.ArrayList;
import java.util.List;
/**
 * @author zhaohe@iapppay.com
 */

public class IdInfoActivity extends AppBaseActivity {
    
    private RecyclerView recyclerView = null;
    private IdInfoAdapter adapter = null;
    private RnaInfoSchema rnaInfoSchema = null;
    private TextView tv_auth_type;
    private List<IdInfoBean> infoBeanList = new ArrayList<>();
    private String rnaType = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.activity_id_info);
        initTitle();
        initView();
        initData();
    }

    void initTitle(){
        initTitleTextView(getString(R.string.IdInfo_A0));
        initTitleBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCanClick()){
                    return;
                }
                back();
            }
        });

    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(IdInfoActivity.this));
        tv_auth_type = findViewById(R.id.tv_auth_type);
    }


    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            rnaInfoSchema = (RnaInfoSchema) intent.getSerializableExtra(Constants.LOCAL_RNA_INFO_KEY);
            rnaType = intent.getStringExtra(Constants.LOCAL_TYPE_KEY);
        }
        if (rnaInfoSchema == null){
            return;
        }
        //显示实名认证类型
        tv_auth_type.setText(rnaType);

        infoBeanList.clear();
        IdInfoBean idInfoBean = null;
        if (!TextUtils.isEmpty(rnaInfoSchema.getName())){
            idInfoBean = new IdInfoBean(getString(R.string.IdInfo_C0),rnaInfoSchema.getName(),true);
            infoBeanList.add(idInfoBean);
        }
        if (!TextUtils.isEmpty(rnaInfoSchema.getId())){
            idInfoBean = new IdInfoBean(getString(R.string.IdInfo_D0),rnaInfoSchema.getId(),false);
            infoBeanList.add(idInfoBean);
        }
        if (!TextUtils.isEmpty(rnaInfoSchema.getProvince())){
            idInfoBean = new IdInfoBean(getString(R.string.IdInfo_E0),rnaInfoSchema.getProvince(),false);
            infoBeanList.add(idInfoBean);
        }
        if (!TextUtils.isEmpty(rnaInfoSchema.getCity())){
            idInfoBean = new IdInfoBean(getString(R.string.IdInfo_F0),rnaInfoSchema.getCity(),false);
            infoBeanList.add(idInfoBean);
        }
        if (!TextUtils.isEmpty(rnaInfoSchema.getDistrict())){
            idInfoBean = new IdInfoBean(getString(R.string.IdInfo_G0),rnaInfoSchema.getDistrict(),false);
            infoBeanList.add(idInfoBean);
        }
        if (!TextUtils.isEmpty(rnaInfoSchema.getVillage())){
            idInfoBean = new IdInfoBean(getString(R.string.IdInfo_H0),rnaInfoSchema.getVillage(),false);
            infoBeanList.add(idInfoBean);
        }
        adapter = new IdInfoAdapter(infoBeanList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back(){
        IDVerifySdkMain.getInstance().onResultBack(IDVerifySdk.LOCAL_SUCCESS, "", IDVerifySdkMain.getInstance().auditResultObject);
        finish();
    }
}
