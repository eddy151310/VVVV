package com.ahdi.wallet.app.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahdi.wallet.app.schemas.CardBindSchema;
import com.ahdi.lib.utils.imagedown.ImageDownUtil;
import com.ahdi.wallet.R;
import com.ahdi.lib.utils.listener.OnItemClickListener;

import java.util.List;

/**
 * Created by Administrator on 2017/10/16.
 *
 * @author admin
 */
public class BankCardsAdapter extends RecyclerView.Adapter<BankCardsAdapter.BankCardsHolder> implements View.OnClickListener {

    private Context mContext;
    private List<CardBindSchema> bindSchemas;

    public BankCardsAdapter(Context context, List<CardBindSchema> bindSchemas) {
        this.mContext = context;
        this.bindSchemas = bindSchemas;
    }

    private OnItemClickListener onItemClickListener = null;

    @Override
    public BankCardsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bank_card_item, null);
        BankCardsHolder holder = new BankCardsHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(BankCardsHolder holder, int position) {
        CardBindSchema bindSchema = bindSchemas.get(position);

        ImageDownUtil.downBankIconImage(mContext, bindSchema.icon, holder.bank_account_icon);

        holder.tv_card_bank.setText(bindSchema.bank);
        holder.tv_card_type.setText(bindSchema.type);
        holder.tv_card_number.setText(bindSchema.card);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return (bindSchemas == null || bindSchemas.size() == 0) ? 0 : bindSchemas.size();
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

    public class BankCardsHolder extends RecyclerView.ViewHolder {
        ImageView bank_account_icon;
        TextView tv_card_bank;
        TextView tv_card_type;
        TextView tv_card_number;

        public BankCardsHolder(View view) {
            super(view);
            bank_account_icon = view.findViewById(R.id.img_card_icon);
            tv_card_bank = view.findViewById(R.id.tv_card_bank);
            tv_card_type = view.findViewById(R.id.tv_card_type);
            tv_card_number = view.findViewById(R.id.tv_card_number);
        }
    }
}
