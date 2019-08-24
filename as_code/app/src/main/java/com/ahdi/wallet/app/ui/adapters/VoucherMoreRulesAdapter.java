package com.ahdi.wallet.app.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ahdi.wallet.app.utils.ConstantsPayment;
import com.ahdi.wallet.R;

import java.util.List;

/**
 * @author zhaohe
 */

public class VoucherMoreRulesAdapter extends RecyclerView.Adapter<VoucherMoreRulesAdapter.VoucherViewHolder>{

    private Context mContext;
    private List<String> rulesList;
    private int voucherStatus;

    public VoucherMoreRulesAdapter(Context context, List<String> list, int voucherStatus) {
        this.mContext = context;
        this.rulesList = list;
        this.voucherStatus = voucherStatus;
    }

    @Override
    public VoucherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.voucher_more_rules_item, null);
        VoucherViewHolder holder = new VoucherViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(VoucherViewHolder holder, int position) {
        holder.tv_voucher_rule.setText(rulesList.get(position));
        if (voucherStatus == ConstantsPayment.VOUCHER_STATUS_VALID){
            holder.tv_voucher_rule.setTextColor(mContext.getResources().getColor(R.color.CC_5F5F67));
        }else {
            holder.tv_voucher_rule.setTextColor(mContext.getResources().getColor(R.color.CC_919399));
        }
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return (rulesList == null || rulesList.size() == 0) ? 0 : rulesList.size();
    }

    public class VoucherViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_voucher_rule;

        public VoucherViewHolder(View view) {
            super(view);
            tv_voucher_rule = view.findViewById(R.id.tv_voucher_rule);
        }
    }

}
