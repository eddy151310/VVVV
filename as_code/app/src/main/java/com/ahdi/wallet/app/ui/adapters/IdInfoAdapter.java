package com.ahdi.wallet.app.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.bean.IdInfoBean;

import java.util.List;

/**
 * @author admin
 * @date Created by xiaoniu on 2018/1/10.
 * @email zhao_zhaohe@163.com
 */

public class IdInfoAdapter extends RecyclerView.Adapter<IdInfoAdapter.IDViewHolder>{

    private List<IdInfoBean> datas;
    public IdInfoAdapter(List<IdInfoBean> datas) {
        this.datas = datas;
    }

    @Override
    public IDViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_id_info_item, null);
        IDViewHolder viewHolder = new IDViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(IDViewHolder holder, int position) {
            IDViewHolder viewHolder = (IDViewHolder) holder;
            if (position == datas.size() - 1){
                viewHolder.item_bottom_divider.setVisibility(View.GONE);
            }
            IdInfoBean idInfoBean = datas.get(position);
            viewHolder.item_key.setText(idInfoBean.getKey());
            viewHolder.item_value.setText(idInfoBean.getValue());
            if (idInfoBean.isShowRegister()){
                viewHolder.item_registered.setVisibility(View.VISIBLE);
            }else{
                viewHolder.item_registered.setVisibility(View.GONE);
            }
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public class IDViewHolder extends RecyclerView.ViewHolder{
        public TextView item_key,item_value;
        public ImageView item_registered;
        public View item_bottom_divider;
        public IDViewHolder(View itemView) {
            super(itemView);
            item_key = itemView.findViewById(R.id.item_key);
            item_value = itemView.findViewById(R.id.item_value);
            item_registered = itemView.findViewById(R.id.item_registered);
            item_bottom_divider = itemView.findViewById(R.id.item_bottom_divider);
        }
    }
}