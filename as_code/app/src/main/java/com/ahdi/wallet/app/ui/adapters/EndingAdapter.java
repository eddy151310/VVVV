package com.ahdi.wallet.app.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.ui.bean.EndingItemBean;

import java.util.List;

/**
 * Date: 2018/5/28 上午10:21
 * Author: kay lau
 * Description:
 */
public class EndingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_DIVIDER = 1;

    private List<EndingItemBean> itemBeanList;
    private Context context;

    public EndingAdapter(Context context, List<EndingItemBean> bindSchemas) {
        this.itemBeanList = bindSchemas;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder holder = null;
        if (viewType == VIEW_TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ending_item_layout, null);
            holder = new EndingHolder(view);
        }else if (viewType == VIEW_TYPE_DIVIDER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ending_item_divider_layout, null);
            holder = new EndingDividerHolder(view);
        }
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        return itemBeanList.get(position).isDivider() ? VIEW_TYPE_DIVIDER : VIEW_TYPE_ITEM ;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EndingHolder){
            EndingItemBean itemBean = itemBeanList.get(position);

            EndingHolder itemHolder = (EndingHolder) holder;
            itemHolder.tv_ending_item_key.setText(itemBean.getEndingKey());
            itemHolder.tv_ending_item_value.setText(itemBean.getEndingValue());
            if (itemBean.getEndingValueColor() != -1){
                itemHolder.tv_ending_item_value.setTextColor(context.getResources().getColor(itemBean.getEndingValueColor()));
            }else {
                itemHolder.tv_ending_item_value.setTextColor(context.getResources().getColor(R.color.CC_5F5F67));
            }
        }
    }

    @Override
    public int getItemCount() {
        return (itemBeanList == null || itemBeanList.size() == 0) ? 0 : itemBeanList.size();
    }

    class EndingHolder extends RecyclerView.ViewHolder {
        TextView tv_ending_item_key;
        TextView tv_ending_item_value;

        public EndingHolder(View view) {
            super(view);
            tv_ending_item_key = view.findViewById(R.id.tv_ending_item_key);
            tv_ending_item_value = view.findViewById(R.id.tv_ending_item_value);
        }
    }

    class EndingDividerHolder extends RecyclerView.ViewHolder {

        public EndingDividerHolder(View view) {
            super(view);
        }
    }

}