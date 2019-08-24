package com.ahdi.wallet.app.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ahdi.wallet.R;

import java.util.List;

/**
 * @author zhaohe
 */

public class VoucherLimitAdapter extends RecyclerView.Adapter<VoucherLimitAdapter.VoucherViewHolder>{

    private Context mContext;
    private List<String> limitsList;
    private boolean voucherAvailable;

    public VoucherLimitAdapter(Context context, List<String> list, boolean voucherAvailable) {
        this.mContext = context;
        this.limitsList = list;
        this.voucherAvailable = voucherAvailable;
    }

    @Override
    public VoucherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.voucher_limits_item, null);
        VoucherViewHolder holder = new VoucherViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(VoucherViewHolder holder, int position) {
        holder.tv_voucher_limits.setText(limitsList.get(position));
        if (voucherAvailable){
            holder.tv_voucher_limits.setTextColor(mContext.getResources().getColor(R.color.CC_5F5F67));
        }else {
            holder.tv_voucher_limits.setTextColor(mContext.getResources().getColor(R.color.CC_919399));
        }
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return (limitsList == null || limitsList.size() == 0) ? 0 : limitsList.size();
    }

    public class VoucherViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_voucher_limits;

        public VoucherViewHolder(View view) {
            super(view);
            tv_voucher_limits = view.findViewById(R.id.tv_voucher_limits);
        }
    }

}
