package com.ahdi.wallet.app.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.schemas.BindCardTypeSchema;
import com.ahdi.lib.utils.listener.OnItemClickListener;

import java.util.List;

/**
 * Created by Administrator on 2017/10/16.
 *
 * @author admin
 */


public class BindCardTypeAdapter extends RecyclerView.Adapter<BindCardTypeAdapter.BindCardTypeHolder> implements View.OnClickListener {

    private List<BindCardTypeSchema> bindCardTypeSchemas;

    public BindCardTypeAdapter(List<BindCardTypeSchema> bindTypeSchemas) {
        this.bindCardTypeSchemas = bindTypeSchemas;
    }

    private OnItemClickListener onItemClickListener = null;

    @Override
    public BindCardTypeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bind_card_type_item, null);
        BindCardTypeHolder holder = new BindCardTypeHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(BindCardTypeHolder holder, int position) {
        BindCardTypeSchema bindSchema = bindCardTypeSchemas.get(position);
        holder.tv_card_type_name.setText(bindSchema.title);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return (bindCardTypeSchemas == null || bindCardTypeSchemas.size() == 0) ? 0 : bindCardTypeSchemas.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public class BindCardTypeHolder extends RecyclerView.ViewHolder {
        TextView tv_card_type_name;

        public BindCardTypeHolder(View view) {
            super(view);
            tv_card_type_name = view.findViewById(R.id.tv_card_type_name);
        }
    }
}
