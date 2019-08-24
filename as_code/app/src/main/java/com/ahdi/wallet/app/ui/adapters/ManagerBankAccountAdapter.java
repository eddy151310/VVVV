package com.ahdi.wallet.app.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahdi.wallet.app.schemas.BankAccountSchema;
import com.ahdi.lib.utils.imagedown.ImageDownUtil;
import com.ahdi.wallet.R;
import com.ahdi.lib.utils.listener.OnItemClickListener;

import java.util.List;

/**
 * Created by Administrator on 2017/10/16.
 *
 * @author admin
 */
public class ManagerBankAccountAdapter extends RecyclerView.Adapter<ManagerBankAccountAdapter.BankAccountHolder> implements View.OnClickListener {

    private Context mContext;
    private List<BankAccountSchema> bindSchemas;
    private OnItemClickListener onItemClickListener = null;

    public ManagerBankAccountAdapter(Context context, List<BankAccountSchema> bindSchemas) {
        this.mContext = context;
        this.bindSchemas = bindSchemas;
    }

    @Override
    public BankAccountHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bank_account_item, null);
        return new BankAccountHolder(view);
    }

    @Override
    public void onBindViewHolder(BankAccountHolder holder, int position) {
        BankAccountSchema bindSchema = bindSchemas.get(position);
        ImageDownUtil.downBankIconImage(mContext, bindSchema.icon, holder.iv_icon);
        holder.tv_bank_account_name.setText(bindSchema.bank);
        holder.tv_bank_account_delete.setText(mContext.getString(R.string.ManagerBankAccount_B0));
        holder.tv_bank_account_number.setText(bindSchema.accountNo);

        holder.tv_bank_account_delete.setTag(position);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return (bindSchemas == null || bindSchemas.size() == 0) ? 0 : bindSchemas.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        //注意这里使用getTag方法获取数据
        int position = (int) v.getTag();
        int id = v.getId();
        if (onItemClickListener != null) {
            if (R.id.tv_bank_account_delete == id) {
                onItemClickListener.onItemClick(v, position);
            }
        }
    }

    public class BankAccountHolder extends RecyclerView.ViewHolder {

        ImageView iv_icon;
        TextView tv_bank_account_name;
        TextView tv_bank_account_delete;
        TextView tv_bank_account_number;

        public BankAccountHolder(View view) {
            super(view);
            iv_icon = view.findViewById(R.id.iv_icon);
            tv_bank_account_name = view.findViewById(R.id.tv_bank_account_name);
            tv_bank_account_delete = view.findViewById(R.id.tv_bank_account_delete);
            tv_bank_account_number = view.findViewById(R.id.tv_bank_account_number);
            tv_bank_account_delete.setOnClickListener(ManagerBankAccountAdapter.this);
        }
    }
}
