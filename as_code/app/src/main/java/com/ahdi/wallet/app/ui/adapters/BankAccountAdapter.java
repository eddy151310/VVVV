package com.ahdi.wallet.app.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahdi.wallet.app.schemas.BankAccountSchema;
import com.ahdi.wallet.R;
import com.ahdi.lib.utils.imagedown.ImageDownUtil;
import com.ahdi.lib.utils.listener.OnItemLongClickListener;

import java.util.List;

/**
 * Created by Administrator on 2017/10/16.
 *
 * @author admin
 */
public class BankAccountAdapter extends RecyclerView.Adapter<BankAccountAdapter.BankAccountHolder> implements View.OnLongClickListener {

    private Context mContext;
    private List<BankAccountSchema> bindSchemas;

    public BankAccountAdapter(Context context, List<BankAccountSchema> bindSchemas) {
        this.mContext = context;
        this.bindSchemas = bindSchemas;
    }

    private OnItemLongClickListener onItemLongClickListener = null;

    @Override
    public BankAccountHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bank_account_item, null);
        BankAccountHolder holder = new BankAccountHolder(view);
        view.setOnLongClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(BankAccountHolder holder, int position) {
        BankAccountSchema bindSchema = bindSchemas.get(position);
        ImageDownUtil.downBankIconImage(mContext, bindSchema.icon, holder.iv_icon);
        holder.tv_bank_account_name.setText(bindSchema.bank);
        holder.tv_bank_account_number.setText(bindSchema.accountNo);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return (bindSchemas == null || bindSchemas.size() == 0) ? 0 : bindSchemas.size();
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    @Override
    public boolean onLongClick(View v) {
        if (onItemLongClickListener != null) {
            onItemLongClickListener.onItemLongClick(v, (int) v.getTag());
        }
        return false;
    }

    public class BankAccountHolder extends RecyclerView.ViewHolder {

        ImageView iv_icon;
        TextView tv_bank_account_name;
        TextView tv_bank_account_number;

        public BankAccountHolder(View view) {
            super(view);
            iv_icon = view.findViewById(R.id.iv_icon);
            tv_bank_account_name = view.findViewById(R.id.tv_bank_account_name);
            tv_bank_account_number = view.findViewById(R.id.tv_bank_account_number);
        }
    }
}
