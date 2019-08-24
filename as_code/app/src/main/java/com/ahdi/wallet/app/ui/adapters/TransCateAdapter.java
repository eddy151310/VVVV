package com.ahdi.wallet.app.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ahdi.wallet.R;
import com.ahdi.lib.utils.listener.OnItemClickListener;
import com.ahdi.wallet.app.ui.bean.TransCategoriesBean;

import java.util.List;

/**
 * @author admin
 * @date Created by xiaoniu on 2018/1/10.
 * @email zhao_zhaohe@163.com
 */

public class TransCateAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    private OnItemClickListener mOnItemClickListener = null;
    private int selectCateType = -1;

    private List<TransCategoriesBean> datas;
    private Context context;

    public TransCateAdapter(Context context, List<TransCategoriesBean> datas, int selectCate) {
        this.context = context;
        this.datas = datas;
        this.selectCateType = selectCate;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_select_categ_item, null);
        view.setOnClickListener(this);
        RecyclerView.ViewHolder viewHolder = new NormalViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        NormalViewHolder viewHolder = (NormalViewHolder) holder;
        viewHolder.itemView.setTag(position);
        viewHolder.item_btn.setText(datas.get(position).getTypeString());
        if (selectCateType ==  datas.get(position).getType()){
            viewHolder.item_btn.setBackgroundResource(R.drawable.selector_bg_translist_item_pressed);
            viewHolder.item_btn.setTextColor(context.getResources().getColor(R.color.CC_FFFFFF));
        }else{
            viewHolder.item_btn.setBackgroundResource(R.drawable.selector_bg_translist_item_normal);
            viewHolder.item_btn.setTextColor(context.getResources().getColor(R.color.CC_1A1B24));
        }
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view, (int) view.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    /**
     * 普通item
     */
    public class NormalViewHolder extends RecyclerView.ViewHolder {
        public Button item_btn;

        public NormalViewHolder(View itemView) {
            super(itemView);
            item_btn = itemView.findViewById(R.id.item_btn);
        }
    }
}