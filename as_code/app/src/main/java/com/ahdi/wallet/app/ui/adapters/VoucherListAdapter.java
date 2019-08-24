package com.ahdi.wallet.app.ui.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahdi.wallet.app.schemas.VoucherSchema;
import com.ahdi.wallet.app.utils.ConstantsPayment;
import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.imagedown.ImageDownUtil;
import com.ahdi.lib.utils.utils.AmountUtil;
import com.ahdi.wallet.R;
import com.ahdi.lib.utils.listener.OnItemClickListener;

import java.util.List;

/**
 * @author zhaohe
 */

public class VoucherListAdapter extends RecyclerView.Adapter<VoucherListAdapter.VoucherViewHolder> implements View.OnClickListener {

    private Context mContext;
    private List<VoucherSchema> voucherList;
    private int voucherType;

    public VoucherListAdapter(Context context, List<VoucherSchema> list, int voucherType) {
        this.mContext = context;
        this.voucherList = list;
        this.voucherType = voucherType;
    }

    private OnItemClickListener mOnItemClickListener = null;

    @Override
    public VoucherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.voucher_list_item, null);
        VoucherViewHolder holder = new VoucherViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(VoucherViewHolder holder, int position) {
        VoucherSchema voucherSchema = voucherList.get(position);

        ImageDownUtil.downVoucherIcon(mContext, voucherSchema.getIcon(), holder.iv_voucher_icon);
        holder.tv_voucher_name.setText(voucherSchema.getName());
        if (voucherSchema.getStatus() == ConstantsPayment.VOUCHER_STATUS_USED){
            holder.tv_voucher_status.setVisibility(View.VISIBLE);
            holder.tv_voucher_status.setText(mContext.getString(R.string.VoucherList_F0));
        }else if (voucherSchema.getStatus() == ConstantsPayment.VOUCHER_STATUS_EXPIRED){
            holder.tv_voucher_status.setVisibility(View.VISIBLE);
            holder.tv_voucher_status.setText(mContext.getString(R.string.VoucherList_E0));
        }else{
            holder.tv_voucher_status.setVisibility(View.GONE);
        }
        holder.tv_voucher_amount_unit.setText(ConfigCountry.KEY_CURRENCY_SYMBOL);
        holder.tv_voucher_amount.setText(AmountUtil.formatAmount(voucherSchema.getMoney()));

        holder.rv_limits.setAdapter(new VoucherLimitAdapter(mContext, voucherSchema.getLimits(), voucherType == ConstantsPayment.VOUCHER_TYPE_VALID));
        holder.rv_limits.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return holder.itemView.onTouchEvent(event);
            }
        });
        changeTextColor(holder);
        holder.itemView.setTag(position);
    }

    private void changeTextColor(VoucherViewHolder holder) {
        if (voucherType == ConstantsPayment.VOUCHER_TYPE_VALID){

            holder.tv_voucher_name.setTextColor(mContext.getResources().getColor(R.color.CC_1A1B24));
            holder.tv_voucher_amount_unit.setTextColor(mContext.getResources().getColor(R.color.CC_1A1B24));
            holder.tv_voucher_amount.setTextColor(mContext.getResources().getColor(R.color.CC_1A1B24));
        }else if (voucherType == ConstantsPayment.VOUCHER_TYPE_HISTORY){

            holder.tv_voucher_name.setTextColor(mContext.getResources().getColor(R.color.CC_5F5F67));
            holder.tv_voucher_amount_unit.setTextColor(mContext.getResources().getColor(R.color.CC_5F5F67));
            holder.tv_voucher_amount.setTextColor(mContext.getResources().getColor(R.color.CC_5F5F67));
        }
    }

    @Override
    public int getItemCount() {
        return (voucherList == null || voucherList.size() == 0) ? 0 : voucherList.size();
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

    public class VoucherViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_voucher_icon;
        private TextView tv_voucher_name,tv_voucher_status,tv_voucher_amount_unit,tv_voucher_amount;
        private RecyclerView rv_limits;

        public VoucherViewHolder(View view) {
            super(view);
            iv_voucher_icon = view.findViewById(R.id.iv_voucher_icon);
            tv_voucher_name = view.findViewById(R.id.tv_voucher_name);
            tv_voucher_status = view.findViewById(R.id.tv_voucher_status);
            tv_voucher_amount_unit = view.findViewById(R.id.tv_voucher_amount_unit);
            tv_voucher_amount = view.findViewById(R.id.tv_voucher_amount);

            rv_limits = view.findViewById(R.id.rv_limits);
            rv_limits.setLayoutManager(new LinearLayoutManager(mContext));
        }
    }

}
