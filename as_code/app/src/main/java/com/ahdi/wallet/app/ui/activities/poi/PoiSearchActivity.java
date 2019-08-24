package com.ahdi.wallet.app.ui.activities.poi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.ConfigSP;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.LBSInstance;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.ui.adapters.PoiAdapter;
import com.ahdi.wallet.app.ui.fragments.TestFragment;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import java.util.ArrayList;


public class PoiSearchActivity extends AppBaseActivity implements View.OnClickListener {

    private ListView listView;
    private PoiAdapter mPoiAdapter;
    private EditText mEtSearchName;
    private ImageView iv_clear_edt;
    private String strCity = "北京";  //街道对应的城市
    private String strStreet ; //街道名称

    private TextView tv_currentPOI;
    private Button btn_reLocation;


    private PoiSearch poiSearch;
    private OnGetPoiSearchResultListener poilistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.activity_poi);
        initCommonTitle(getString(R.string.BankAccountSelectBank_A0));
        initView();
        initIntentData(getIntent());
        initPOI();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void initView() {
        listView = findViewById(R.id.lv_bank);
        mEtSearchName = findViewById(R.id.edt_search);
        iv_clear_edt = findViewById(R.id.iv_clear_edt);
        TextView dialog = findViewById(R.id.dialog);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (!isCanClick()) {
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra(TestFragment.KEY_PUT_EXTRA_POI, ((PoiInfo)mPoiAdapter.getItem(position)).getName());
                setResult(TestFragment.FROM_REQUEST , intent);
                finish();
            }
        });
        //根据输入框输入值的改变来过滤搜索
        mEtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                if( mPoiAdapter  != null ){
                    mPoiAdapter.updateListView(new ArrayList());
                }

                if(TextUtils.isEmpty(s.toString().trim())){
                    listView.setVisibility(View.INVISIBLE);
                }else{
                    listView.setVisibility(View.VISIBLE);
                    poisearch(s.toString().trim());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        iv_clear_edt.setOnClickListener(this);


        tv_currentPOI = findViewById(R.id.tv_current_poi);
        btn_reLocation = findViewById(R.id.btn_reLocation);
        btn_reLocation.setOnClickListener(this);

    }

    private void initIntentData(Intent intent) {
        if (intent != null) {
            strStreet = intent.getStringExtra(TestFragment.KEY_PUT_EXTRA_POI);
            tv_currentPOI.setText(strStreet);
        }
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId() ;
        if(vid == R.id.edt_search){
            mEtSearchName.setText("");
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(mEtSearchName.getWindowToken(), 0);
            }
            mEtSearchName.clearFocus();
        }else if(vid == R.id.btn_reLocation){
            LBSInstance.getInstance().getLBS(new LBSInstance.LBSCallBack() {
                @Override
                public void onSuccess(BDLocation location) {
                    if(!TextUtils.isEmpty(location.getStreet())){
                        AppGlobalUtil.getInstance().putString(getApplicationContext(), ConfigSP.SP_KEY_STREET_NAME, location.getStreet() );
                        tv_currentPOI.setText(location.getStreet());
                    }
                }
                @Override
                public void onFail(int errorCode, String errorMsg) {

                }
            });
        }
    }

    public void initPOI() {
        //初始化百度地图 , 必须是Application
        SDKInitializer.initialize(this.getApplication());
        //创建POI检索实例
        poiSearch = PoiSearch.newInstance();
        //创建POI检索监听器
        poilistener = new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {

                if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {// 没有找到检索结果
                    LogUtil.d("", "========DDDDDD=====: " + "没有对应 POI ");
                } else if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    LogUtil.d("", "========CCCCC=====: " + poiResult.getAllPoi().get(0).getAddress());
                    if(mPoiAdapter == null ){
                        mPoiAdapter = new PoiAdapter(PoiSearchActivity.this, poiResult.getAllPoi());
                        listView.setAdapter(mPoiAdapter);
                        mPoiAdapter.notifyDataSetChanged();
                    }else{
                        mPoiAdapter.updateListView(poiResult.getAllPoi());
                    }
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }

            //废弃
            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }
        };

        //设置poi检索监听器
        poiSearch.setOnGetPoiSearchResultListener(poilistener);

    }


    void poisearch(String keyword) {
        /**
         *  PoiCiySearchOption 设置检索属性
         *  city 检索城市
         *  keyword 检索内容关键字
         *  pageNum	分页编号，默认返回第【0】页结果
         *  pageCapacity	设置每页容量，默认为【10】条结果
         */
        if (poiSearch != null) {
            poiSearch.searchInCity((new PoiCitySearchOption()).city(strCity).keyword(keyword));
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (poiSearch != null) {
            poiSearch.destroy();
        }
    }
}
