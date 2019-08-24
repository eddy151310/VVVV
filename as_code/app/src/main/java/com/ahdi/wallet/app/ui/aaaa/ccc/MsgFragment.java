package com.ahdi.wallet.app.ui.aaaa.ccc;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.widgets.custom.RefreshRecyclerView;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.ui.aaaa.ServiceDetailsActivity2;
import com.ahdi.wallet.app.ui.aaaa.bbb.BBRecyclerView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MsgFragment extends Fragment {
    private static final String KEY_ARGS_NAME = "key_args_name";
    private BBRecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private List<MsgEntity> mMsgDatas = new ArrayList<>();
    private RvAdapter mRvAdapter;
    private String mSessionName;

    public MsgFragment() {
        // Required empty public constructor
    }

    public static MsgFragment newInstance(String sessionName) {

        Bundle args = new Bundle();
        args.putString(KEY_ARGS_NAME, sessionName);
        MsgFragment fragment = new MsgFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mSessionName = getArguments().getString(KEY_ARGS_NAME);
        return inflater.inflate(R.layout.ccc_fragment_msg, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvAdapter = new RvAdapter();
        for (int i = 0; i < 51; i++) {
            MsgEntity msgEntity = new MsgEntity();
            msgEntity.name = mSessionName + i;
            mMsgDatas.add(msgEntity);
        }
        mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView = view.findViewById(R.id.msg_rv);

        mRecyclerView.setFooterResource(R.layout.layout_view_footer);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mRecyclerView.setAdapter(mRvAdapter);


        // 解决了 https://blog.csdn.net/Yyongchao/article/details/82501200
        // 1、Viewpage 内 RecyclerView 内容不显示，
        // 2、ScrollView默认位置不是最顶部最全解决方案
//        mRecyclerView.setFocusable(false);


        /**解决上下滑动不流畅*/
//        mRecyclerView.setNestedScrollingEnabled(false);

    }

    public void updateData(String name) {
        mSessionName = name;
        for (int i = 0; i < mMsgDatas.size(); i++) {
            MsgEntity msgEntity = mMsgDatas.get(i);
            msgEntity.name = mSessionName + i;
        }
        mRvAdapter.notifyDataSetChanged();
    }

    private class RvAdapter extends RecyclerView.Adapter<RvAdapter.RvViewHolder> {

       public RvAdapter(){

        }

        @NonNull
        @Override
        public RvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RvViewHolder(new TextView(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(@NonNull RvViewHolder holder, int position) {
            MsgEntity msgEntity = mMsgDatas.get(position);
            TextView textView = (TextView) holder.itemView;
            textView.setText(msgEntity.name);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //调用回调方法 传上面的i  索引
                    LogUtil.d("aaa" , "=====================");
                    Intent it = new Intent();
                    it.setClass(getContext(), ServiceDetailsActivity2.class);
                    startActivity(it);
                }
            });

        }

        @Override
        public int getItemCount() {
            return mMsgDatas.size();
        }

        class RvViewHolder extends RecyclerView.ViewHolder {

            public RvViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

}
