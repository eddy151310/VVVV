package com.ahdi.wallet.cashier.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.schemas.DescSchema;
import com.ahdi.wallet.cashier.schemas.PayTypeSchema;
import com.ahdi.wallet.app.utils.ConstantsPayment;
import com.ahdi.lib.utils.imagedown.ImageDownUtil;
import com.ahdi.lib.utils.listener.OnItemClickListener;
import com.ahdi.lib.utils.utils.ToolUtils;

import java.util.List;

/**
 * Date: 2019/6/6 上午8:52
 * Author: kay lau
 * Description:
 */
public class CashierPayModeSelectAdapter extends RecyclerView.Adapter<CashierPayModeSelectAdapter.PayModeHolder> implements View.OnClickListener {

    private OnItemClickListener onItemClickListener;
    private Context mContext;
    private List<PayTypeSchema> mPayTypesList;
    private int mPosition;

    public CashierPayModeSelectAdapter(Context mContext, List<PayTypeSchema> payTypesList, int position) {
        this.mContext = mContext;
        this.mPayTypesList = payTypesList;
        this.mPosition = position;
    }

    @Override
    public PayModeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_select_paymode_item, null);
        PayModeHolder holder = new PayModeHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(PayModeHolder holder, int position) {
        PayTypeSchema schema = mPayTypesList.get(position);
        String iconUrl = schema.IconUrl;
        ImageDownUtil.downBankIconImage(mContext, iconUrl, holder.iv_PayTypeIcon);

        holder.tv_PayTypeName.setText(schema.Name);

        if (position == mPosition && TextUtils.isEmpty(schema.Block)) {
            holder.iv_check.setVisibility(View.VISIBLE);
        } else {
            holder.iv_check.setVisibility(View.GONE);
        }

        // 支付方式描述: 银行卡费率描述文案 余额描述文案
        DescSchema descSchema = schema.getDescSchema();
        if (descSchema != null) {
            if (schema.ID == ConstantsPayment.PAY_TYPE_BANK
                    && !TextUtils.isEmpty(descSchema.fee)) {
                holder.tv_payType_fee.setVisibility(View.VISIBLE);
                holder.tv_payType_fee.setText(descSchema.fee);

            } else if (schema.ID == ConstantsPayment.PAY_TYPE_BALANCE
                    && !TextUtils.isEmpty(descSchema.balance)) {
                holder.tv_payType_fee.setVisibility(View.VISIBLE);
                holder.tv_payType_fee.setText(descSchema.balance);

            } else {
                holder.tv_payType_fee.setVisibility(View.GONE);
            }
        } else {
            holder.tv_payType_fee.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(schema.Block)) {
            holder.item_bg_view.setBackgroundColor(ToolUtils.getColor(mContext, R.color.CC_F1F2F6));
            holder.tv_PayTypeName.setTextColor(ToolUtils.getColor(mContext, R.color.CC_919399));
            holder.tv_payType_fee.setTextColor(ToolUtils.getColor(mContext, R.color.CC_919399));
            holder.item_bg_view.setEnabled(false);
            holder.tv_payType_fee.setText(schema.Block);
            holder.tv_payType_fee.setVisibility(View.VISIBLE);
        }
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return (mPayTypesList == null || mPayTypesList.size() == 0) ? 0 : mPayTypesList.size();
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class PayModeHolder extends RecyclerView.ViewHolder {

        View item_bg_view;
        TextView tv_PayTypeName;
        TextView tv_payType_fee;
        ImageView iv_PayTypeIcon;
        ImageView iv_check;

        public PayModeHolder(View itemView) {
            super(itemView);
            item_bg_view = itemView.findViewById(R.id.item_bg_view);
            tv_PayTypeName = itemView.findViewById(R.id.tv_payTypeName);
            iv_PayTypeIcon = itemView.findViewById(R.id.iv_icon);
            tv_payType_fee = itemView.findViewById(R.id.tv_payType_fee);
            iv_check = itemView.findViewById(R.id.iv_check);
        }
    }
}
