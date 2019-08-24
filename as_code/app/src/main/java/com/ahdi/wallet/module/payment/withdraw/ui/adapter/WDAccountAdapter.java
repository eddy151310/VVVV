package com.ahdi.wallet.module.payment.withdraw.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahdi.wallet.module.payment.withdraw.schemas.WDBankAccountSchema;
import com.ahdi.wallet.R;
import com.ahdi.lib.utils.imagedown.ImageDownUtil;
import com.ahdi.lib.utils.listener.OnItemClickListener;

import java.util.List;

/**
 * Created by Administrator on 2017/10/16.
 *
 * @author admin
 */
public class WDAccountAdapter extends RecyclerView.Adapter<WDAccountAdapter.BankAccountHolder> implements View.OnClickListener {

    private Context mContext;
    private List<WDBankAccountSchema> bindSchemas;
    private int mPosition;

    public WDAccountAdapter(Context context, List<WDBankAccountSchema> bindSchemas, int position) {
        this.mContext = context;
        this.bindSchemas = bindSchemas;
        this.mPosition = position;
    }

    private OnItemClickListener mOnItemClickListener = null;

    @Override
    public BankAccountHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_select_bank_account_item, null);
        BankAccountHolder holder = new BankAccountHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(BankAccountHolder holder, int position) {
        WDBankAccountSchema bindSchema = bindSchemas.get(position);
        ImageDownUtil.downBankIconImage(mContext, bindSchema.Icon, holder.bank_account_icon);
        holder.tv_bank_account_name.setText(bindSchema.Bank);
        holder.tv_bank_account_number.setText(bindSchema.Account);
        if (mPosition == position) {
            holder.iv_bank_account_check.setVisibility(View.VISIBLE);
        } else {
            holder.iv_bank_account_check.setVisibility(View.INVISIBLE);
        }
        if (position == bindSchemas.size() - 1) {
            holder.view_bottom_divide.setVisibility(View.GONE);
        } else {
            holder.view_bottom_divide.setVisibility(View.VISIBLE);
        }
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return (bindSchemas == null || bindSchemas.size() == 0) ? 0 : bindSchemas.size();
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


    public class BankAccountHolder extends RecyclerView.ViewHolder {
        ImageView bank_account_icon;
        TextView tv_bank_account_name;
        TextView tv_bank_account_number;
        ImageView iv_bank_account_check;
        View view_bottom_divide;

        public BankAccountHolder(View view) {
            super(view);
            bank_account_icon = view.findViewById(R.id.bank_account_icon);
            tv_bank_account_name = view.findViewById(R.id.tv_bank_account_name);
            tv_bank_account_number = view.findViewById(R.id.tv_bank_account_number);
            iv_bank_account_check = view.findViewById(R.id.iv_bank_account_check);
            view_bottom_divide = view.findViewById(R.id.view_bottom_divide);
        }
    }
}
