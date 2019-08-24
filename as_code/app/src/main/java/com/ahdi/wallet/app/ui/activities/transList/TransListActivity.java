package com.ahdi.wallet.app.ui.activities.transList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ahdi.wallet.app.callback.AccountSdkCallBack;
import com.ahdi.wallet.app.response.TransListRsp;
import com.ahdi.wallet.app.schemas.UserBillSchema;
import com.ahdi.wallet.module.payment.transfer.TransferSdk;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.DateUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.widgets.custom.RefreshRecyclerView;
import com.ahdi.lib.utils.widgets.datepicker.DatePickerView;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.wallet.app.ui.adapters.TransListAdapter;
import com.ahdi.lib.utils.listener.OnItemClickListener;
import com.ahdi.wallet.app.ui.widgets.TransCategoriesView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TransListActivity extends AppBaseActivity {

    private static final String TAG = TransListActivity.class.getSimpleName();

    private int more;//0-没有；1-有
    private List<UserBillSchema> billList = new ArrayList<>();
    private DatePickerView datePickerView;
    private SwipeRefreshLayout refreshLayout;
    private RefreshRecyclerView recyclerView;
    private LinearLayout list_no_data_view;
    private TextView tv_date, tv_filter, tv_trans_date;
    private TransCategoriesView selectCategoriesView = null;
    private TransListListener transListListener = new TransListListener();
    private AsyncTask<Void, Void, Void> transTask = null;
    /**
     * 当正在网络加载时，不可点击
     */
    private boolean isCanClick = true;
    private String lastId = "";
    private int type = -1;
    private long month = -1;
    private DateResultHandler resultHandler = null;
    private SelectCate selectCateListener = null;
    private long serviceTime;
    private int rang;
    /**
     * 手机时区
     */
    private TimeZone timeZone = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.activity_translist);
        initCommonTitle(getString(R.string.Transactions_A0));
        initView();
        startInitData(lastId, -1, -1);
        initListener();
    }

    public void initView() {
        tv_date = findViewById(R.id.tv_date);
        tv_date.setOnClickListener(this);
        tv_filter = findViewById(R.id.tv_filter);
        tv_filter.setOnClickListener(this);
        tv_trans_date = findViewById(R.id.tv_trans_date);
        refreshLayout = findViewById(R.id.list_fresh_layout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.CC_C31617));
        list_no_data_view = findViewById(R.id.list_no_data_footer);
        list_no_data_view.setVisibility(View.GONE);
        recyclerView = findViewById(R.id.trans_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setFooterResource(R.layout.layout_view_footer);
        timeZone = TimeZone.getDefault();
        TransListAdapter adapter = new TransListAdapter(this, billList, timeZone);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!isCanClick()) {
                    return;
                }
                if (refreshLayout.isRefreshing() || recyclerView.isLoadingMore() || billList.size() <= 0) {
                    return;
                }
                openWebCommonActivity(billList.get(position).getUrl());
            }
        });
    }

    public void startInitData(String lastId, int type, long month) {
        recyclerView.setFooterResource(R.layout.layout_view_footer);
        this.lastId = lastId;
        initListDate(lastId, type, month);
    }

    public void initListDate(String lastId, int type, long month) {
        this.lastId = lastId;
        this.type = type;
        this.month = month;
        //id 为空 时，正在下拉刷新
        if (TextUtils.equals(lastId, "")) {
            refreshLayout.setRefreshing(true);
            recyclerView.setLoadMoreEnable(false);
        } else {
            //正在上拉加载时
            refreshLayout.setEnabled(false);
            recyclerView.setLoadMoreEnable(true);
        }
        isCanClick = false;
        transTask = AppMain.getInstance().getTransList(this, lastId, type, month, transListListener);
    }

    public void initListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lastId = "";
                initListDate(lastId, type, month);
            }
        });
        recyclerView.setOnLoadMoreListener(new RefreshRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMoreListener() {
                initListDate(billList.get(billList.size() - 1).getId(), type, month);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    //获取第一个可见view的位置
                    int firstItemPosition = linearManager.findFirstVisibleItemPosition();
                    if (billList != null && firstItemPosition >= 0 && firstItemPosition < billList.size()) {
                        tv_trans_date.setText(billList.get(firstItemPosition).getdTime(timeZone));
                    }

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private class TransListListener implements AccountSdkCallBack {
        @Override
        public void onResult(String code, String errorMsg, JSONObject jsonObject) {
            isCanClick = true;
            TransListRsp resp = TransListRsp.decodeJson(TransListRsp.class, jsonObject);
            if (TextUtils.equals(code, TransferSdk.LOCAL_PAY_SUCCESS) && resp != null) {
                if (resp.getTime() > 0) {
                    serviceTime = resp.getTime();
                }
                if (resp.getRang() > 0) {
                    rang = resp.getRang();
                }
                more = resp.getMore();
                if (lastId.equals("")) {
                    manageRefreshData(resp);
                } else {
                    manageLoadMoreData(resp);
                }
            } else {
                showToast(errorMsg);
            }
            refreshUIStatus(TextUtils.equals(code, Constants.RET_CODE_SUCCESS));
        }
    }

    /**
     * 处理下拉刷新之后处理数据
     *
     * @param resp
     */
    public void manageRefreshData(TransListRsp resp) {
        billList.clear();
        String dTime = null;
        UserBillSchema[] billSchemas = resp.getBills();
        if (billSchemas != null && billSchemas.length > 0) {
            for (int i = 0; i < billSchemas.length; i++) {
                if (!TextUtils.equals(billSchemas[i].getdTime(timeZone), dTime)) {
                    billSchemas[i].setShowDtime(true);
                    dTime = billSchemas[i].getdTime(timeZone);
                } else {
                    billSchemas[i].setShowDtime(false);
                }
                billList.add(billSchemas[i]);
            }
        }
    }

    /**
     * 上拉加载成功之后处理数据
     *
     * @param resp
     */
    public void manageLoadMoreData(TransListRsp resp) {
        String dTime = null;
        UserBillSchema[] billSchemas = resp.getBills();
        if (billSchemas != null && billSchemas.length > 0) {
            dTime = billList.get(billList.size() - 1).getdTime(timeZone);
            for (int i = 0; i < billSchemas.length; i++) {
                if (!TextUtils.equals(billSchemas[i].getdTime(timeZone), dTime)) {
                    billSchemas[i].setShowDtime(true);
                    dTime = billSchemas[i].getdTime(timeZone);
                } else {
                    billSchemas[i].setShowDtime(false);
                }
                billList.add(billSchemas[i]);
            }
        }
    }

    /**
     * 更新界面状态
     *
     * @param isResponseSuccess 响应是否成功
     */
    private void refreshUIStatus(boolean isResponseSuccess) {

        /**加载数据之后更新界面显示状态*/
        if (billList == null || billList.size() == 0) {
            /**响应成功没有数据显示无相关数据的界面，响应失败显示大白屏*/
            list_no_data_view.setVisibility(isResponseSuccess ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(View.GONE);
            tv_trans_date.setVisibility(View.GONE);

        } else {
            tv_trans_date.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            list_no_data_view.setVisibility(View.GONE);
            /**响应成功更新日期按钮的文案*/
            tv_trans_date.setText(billList.get(0).getdTime(timeZone));
        }

        /**加载数据之后更新recyclerView的状态*/
        refreshLayout.setRefreshing(false);
        refreshLayout.setEnabled(true);
        if (more == 0) {
            recyclerView.setLoadMoreEnable(false);
            recyclerView.setHasMoreData(false);
        } else if (more == 1) {
            recyclerView.setLoadMoreEnable(true);
            recyclerView.setHasMoreData(true);
        }
        recyclerView.notifyData();
    }

    @Override
    public void onClick(View view) {
        if (!isCanClick) {
            return;
        }
        int id = view.getId();
        if (id == R.id.tv_date) {
            if (serviceTime == 0) {
                showToast(getString(R.string.Toast_P0));
            } else {
                Date date = new Date(serviceTime);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.MONTH, -(rang - 1));
                date = calendar.getTime();
                selectDate(DateUtil.formatTimeYMDHM(date, timeZone), DateUtil.formatTimeYMDHM(serviceTime, timeZone));
            }
        } else if (id == R.id.tv_filter) {
            if (selectCateListener == null) {
                selectCateListener = new SelectCate();
            }
            selectCategoriesView = new TransCategoriesView(TransListActivity.this, type);
            selectCategoriesView.setListener(selectCateListener);
            selectCategoriesView.show();
        }

    }

    private void selectDate(String startDate, String endDate) {
        if (resultHandler == null) {
            resultHandler = new DateResultHandler();
        }
        datePickerView = new DatePickerView(TransListActivity.this, resultHandler, startDate, endDate);
        if (month > 0) {
            datePickerView.show(DateUtil.formatTimeYMDHM(month, timeZone));
        } else {
            datePickerView.show(DateUtil.formatTimeYMDHM(System.currentTimeMillis(), timeZone));
        }
    }

    class DateResultHandler implements DatePickerView.ResultHandler {
        @Override
        public void handle(long time) {
            LogUtil.d(TAG, DateUtil.formatTimeYMDHM(time, timeZone));
            tv_date.setText(DateUtil.formatTimeNoDHMS(time, timeZone));
            initListDate("", type, time);
        }
    }

    class SelectCate implements TransCategoriesView.TransCategoriesSelect {

        @Override
        public void onSelected(int selectedType, String typeString) {
            type = selectedType;
            tv_filter.setText(typeString);
            initListDate("", type, month);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (transTask != null && transTask.getStatus() != AsyncTask.Status.FINISHED && !transTask.isCancelled()) {
            transTask.cancel(true);
        }
        transTask = null;
        if (selectCategoriesView != null && selectCategoriesView.isShowing()) {
            selectCategoriesView.dismiss();
        }
        selectCategoriesView = null;
        if (datePickerView != null && datePickerView.isShowing()) {
            datePickerView.dismiss();
        }
        selectCategoriesView = null;
    }
}
