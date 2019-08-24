package com.ahdi.wallet.app.ui.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ahdi.wallet.app.callback.VoucherSdkCallBack;
import com.ahdi.wallet.app.response.VoucherListRsp;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.VoucherSchema;
import com.ahdi.wallet.app.utils.ConstantsPayment;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.ToolUtils;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.lib.utils.widgets.custom.RefreshRecyclerView;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.wallet.app.ui.activities.voucher.VoucherDetailActivity;
import com.ahdi.wallet.app.ui.adapters.VoucherListAdapter;
import com.ahdi.lib.utils.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaohe
 */

public class VoucherFragment extends Fragment {
    private static final String TAG = "VoucherFragment";

    private SwipeRefreshLayout refreshLayout;
    private RefreshRecyclerView recyclerView;
    private LinearLayout list_no_data_view;
    private AsyncTask<Void, Void, Void> voucherTask = null;

    private int more;//0-没有；1-有
    private VoucherListAdapter adapter;
    private List<VoucherSchema> voucherList = new ArrayList<>();
    private VoucherListListener voucherListListener = new VoucherListListener();
    private String lastId = "";
    private int current_voucher_type = ConstantsPayment.VOUCHER_TYPE_VALID;//有效券
    private boolean isNeedLoadData = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null){
            current_voucher_type = arguments.getInt(ConstantsPayment.KEY_VOUCHER_TYPE, ConstantsPayment.VOUCHER_TYPE_VALID);
        }
        LogUtil.d(TAG, "VoucherFragment#onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_valid_voucher, container, false);
        initView(contentView);
        if (getUserVisibleHint()){
            initListDate("");
            isNeedLoadData = false;
        }else {
            isNeedLoadData = true;
        }
        initListener();
        return contentView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isNeedLoadData){
            isNeedLoadData = false;
            initListDate("");
            LogUtil.d(TAG, "加载历史券列表...");
        }
    }

    /**
     * 对外提供刷新列表的功能
     */
    public void onRefreshList(){
        //先停止页面正在加载数据
        if (voucherTask != null && voucherTask.getStatus() != AsyncTask.Status.FINISHED && !voucherTask.isCancelled()) {
            voucherTask.cancel(true);
        }
        refreshLayout.setRefreshing(false);
        recyclerView.notifyData();
        recyclerView.scrollToPosition(0);
        initListDate("");
    }

    public void initView(View view) {
        refreshLayout = view.findViewById(R.id.list_fresh_layout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.CC_C31617));
        list_no_data_view = view.findViewById(R.id.list_no_data_footer);
        list_no_data_view.setVisibility(View.GONE);
        recyclerView = view.findViewById(R.id.trans_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setFooterResource(R.layout.layout_view_footer);//考虑放在refreshLayout类中直接设置
        adapter = new VoucherListAdapter(getActivity(), voucherList, current_voucher_type);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!ToolUtils.isCanClick()) {
                    return;
                }
                if (refreshLayout.isRefreshing() || recyclerView.isLoadingMore() || voucherList.size() <= 0) {
                    return;
                }
                Intent intent = new Intent(getActivity(), VoucherDetailActivity.class);
                intent.putExtra(Constants.LOCAL_ID_KEY, voucherList.get(position).getId());
                startActivity(intent);
            }
        });
    }

    public void initListDate(String lastId) {
        this.lastId = lastId;
        //id 为空 时，正在下拉刷新
        if (TextUtils.equals(lastId, "")) {
            refreshLayout.setRefreshing(true);
            recyclerView.setLoadMoreEnable(false);
        } else {
            //正在上拉加载时
            refreshLayout.setEnabled(false);
            recyclerView.setLoadMoreEnable(true);
        }
        voucherTask = AppMain.getInstance().getVoucherList(getActivity(), lastId, current_voucher_type, voucherListListener);
    }

    public void initListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lastId = "";
                initListDate(lastId);
            }
        });
        recyclerView.setOnLoadMoreListener(new RefreshRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMoreListener() {
                initListDate(voucherList.get(voucherList.size() - 1).getId());
            }
        });
    }

    private class VoucherListListener implements VoucherSdkCallBack {
        @Override
        public void onResult(String code, String errorMsg, Response response) {
            if (TextUtils.equals(code, Constants.RET_CODE_SUCCESS) && response != null) {
                VoucherListRsp resp = (VoucherListRsp) response;
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
    public void manageRefreshData(VoucherListRsp resp) {
        voucherList.clear();
        VoucherSchema[] voucherSchemas = resp.getVoucherSchemas();
        if (voucherSchemas != null && voucherSchemas.length > 0) {
            for (int i = 0; i < voucherSchemas.length; i++) {
                voucherList.add(voucherSchemas[i]);
            }
        }
    }

    /**
     * 上拉加载成功之后处理数据
     *
     * @param resp
     */
    public void manageLoadMoreData(VoucherListRsp resp) {
        VoucherSchema[] voucherSchemas = resp.getVoucherSchemas();
        if (voucherSchemas != null && voucherSchemas.length > 0) {
            for (int i = 0; i < voucherSchemas.length; i++) {
                voucherList.add(voucherSchemas[i]);
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
        if (voucherList == null || voucherList.size() == 0) {
            /**响应成功没有数据显示无相关数据的界面，响应失败显示大白屏*/
            list_no_data_view.setVisibility(isResponseSuccess ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            list_no_data_view.setVisibility(View.GONE);
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

    /**
     * 统一消息
     * @param msg
     */
    public void showToast(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            ToastUtil.showToastShort(getActivity(), msg);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (voucherTask != null && voucherTask.getStatus() != AsyncTask.Status.FINISHED && !voucherTask.isCancelled()) {
            voucherTask.cancel(true);
        }
        voucherTask = null;
    }
}
