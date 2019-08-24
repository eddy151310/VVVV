package com.ahdi.wallet.module.QRCode.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahdi.wallet.module.QRCode.schemas.AuthCodePaySchema;
import com.ahdi.lib.utils.listener.OnItemClickListener;
import com.ahdi.wallet.R;
import com.ahdi.lib.utils.imagedown.ImageDownUtil;

import java.util.List;

/**
 * Date: 2018/5/25 下午4:56
 * Author: kay lau
 * Description:
 */
public class SelectPayModeAdapter extends RecyclerView.Adapter<SelectPayModeAdapter.PayModeHolder> implements View.OnClickListener {

    private List<AuthCodePaySchema> mPayTypesList;
    private Context context;
    private int mPosition;
    private OnItemClickListener onItemClickListener;

    public SelectPayModeAdapter(Context context, List<AuthCodePaySchema> list, int position) {
        this.context = context;
        this.mPosition = position;
        this.mPayTypesList = list;
    }

    @Override
    public PayModeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_paycode_select_paymode_item, null);
        PayModeHolder holder = new PayModeHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(PayModeHolder holder, int position) {
        AuthCodePaySchema schema = mPayTypesList.get(position);
        // 第一个条目显示描述文案,  其他隐藏
        if (position == 0) {
            holder.tv_tip_text.setVisibility(View.VISIBLE);

        } else {
            holder.tv_tip_text.setVisibility(View.GONE);
        }

        if (schema != null) {
            // 银行名称
            holder.tv_PayTypeName.setText(schema.Bank);
            // 交易手续费
            if (!TextUtils.isEmpty(schema.FeeDesc)) {
                holder.tv_bank_fee.setText(schema.FeeDesc);
                holder.tv_bank_fee.setVisibility(View.VISIBLE);
            } else {
                holder.tv_bank_fee.setVisibility(View.GONE);
            }
            // 银行icon
            if (!TextUtils.isEmpty(schema.BankIcon)) {
                ImageDownUtil.downBankIconImage(context, schema.BankIcon, holder.iv_PayTypeIcon);
            } else {
                holder.iv_PayTypeIcon.setImageResource(R.mipmap.common_pay_type_icon_default);
            }
        }
        // 列表条目的右边icon是否显示
        if (mPosition == position) {
            holder.iv_check.setVisibility(View.VISIBLE);
        } else {
            holder.iv_check.setVisibility(View.INVISIBLE);
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

        TextView tv_PayTypeName;
        TextView tv_bank_fee;
        TextView tv_tip_text;
        ImageView iv_PayTypeIcon;
        ImageView iv_check;

        public PayModeHolder(View itemView) {
            super(itemView);
            tv_PayTypeName = itemView.findViewById(R.id.tv_payTypeName);
            tv_bank_fee = itemView.findViewById(R.id.tv_bank_fee);
            tv_tip_text = itemView.findViewById(R.id.tv_tip_text);
            iv_PayTypeIcon = itemView.findViewById(R.id.iv_icon);
            iv_check = itemView.findViewById(R.id.iv_check);
        }
    }
}
