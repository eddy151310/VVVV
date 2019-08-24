package com.ahdi.wallet.app.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahdi.wallet.app.schemas.UserBillSchema;
import com.ahdi.lib.utils.utils.AmountUtil;
import com.ahdi.lib.utils.utils.DateUtil;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.wallet.R;
import com.ahdi.lib.utils.listener.OnItemClickListener;

import java.math.BigDecimal;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by ktt on 2018/5/25.
 */
public class TransListAdapter extends RecyclerView.Adapter<TransListAdapter.BillViewHolder> implements View.OnClickListener {

    private TimeZone timeZone;
    private Context mContext;
    private List<UserBillSchema> billList;

    public TransListAdapter(Context context, List<UserBillSchema> list, TimeZone timeZone) {
        this.mContext = context;
        this.billList = list;
        this.timeZone = timeZone;
    }

    private OnItemClickListener mOnItemClickListener = null;

    @Override
    public BillViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.translist_item, null);
        BillViewHolder holder = new BillViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(BillViewHolder holder, int position) {
        UserBillSchema bill = billList.get(position);
        int bisType = bill.getBizType();
        if (bill.isShowDtime()) {
            if (position == 0) {
                holder.tv_transaction_date.setVisibility(View.GONE);
            } else {
                holder.tv_transaction_date.setVisibility(View.VISIBLE);
            }
            holder.view_top_divide.setVisibility(View.GONE);
            holder.tv_transaction_date.setText(bill.getdTime(timeZone));
        } else {
            holder.view_top_divide.setVisibility(View.VISIBLE);
            holder.tv_transaction_date.setVisibility(View.GONE);
        }
        if (bisType == Constants.TRANS_TYPE_BALANCE) {
            holder.iv_transaction_icon.setImageResource(R.mipmap.transactions_icon_balance);

        } else if (bisType == Constants.TRANS_TYPE_REFUND) {
            holder.iv_transaction_icon.setImageResource(R.mipmap.transactions_icon_refund);

        } else if (bisType == Constants.TRANS_TYPE_RECEIVE_PAY) {
            holder.iv_transaction_icon.setImageResource(R.mipmap.transactions_icon_shops);

        } else if (bisType == Constants.TRANS_TYPE_TRANSFER) {
            holder.iv_transaction_icon.setImageResource(R.mipmap.transactions_icon_transfer);

        } else {
            holder.iv_transaction_icon.setImageResource(R.mipmap.common_pay_type_icon_default);
        }
        holder.tv_transaction_title.setText(bill.getTitle());
        holder.tv_transaction_time.setText(DateUtil.formatTimeDMYHM(bill.getTime(), timeZone));

        String money = "";
        String inOut = bill.getInOut();
        if (!TextUtils.isEmpty(bill.getMoney())) {
            money = AmountUtil.formatAmount(String.valueOf(new BigDecimal(bill.getMoney()).longValue()));
        }
        if (!TextUtils.isEmpty(inOut)) {
            if (TextUtils.equals("+", inOut)) {
                holder.tv_transaction_money.setTextColor(mContext.getResources().getColor(R.color.CC_D63031));

            } else {
                holder.tv_transaction_money.setTextColor(mContext.getResources().getColor(R.color.CC_1A1B24));
            }
            holder.tv_transaction_money.setText(inOut.concat(money));
        } else {
            holder.tv_transaction_money.setTextColor(mContext.getResources().getColor(R.color.CC_1A1B24));
            holder.tv_transaction_money.setText(money);
        }
        if (!TextUtils.isEmpty(bill.getStatus())) {
            holder.tv_transaction_status.setText(bill.getStatus());
        } else {
            holder.tv_transaction_status.setText("");
        }
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return (billList == null || billList.size() == 0) ? 0 : billList.size();
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

    public class BillViewHolder extends RecyclerView.ViewHolder {
        View view_top_divide;
        TextView tv_transaction_title;
        TextView tv_transaction_time;
        TextView tv_transaction_money;
        TextView tv_transaction_status;
        ImageView iv_transaction_icon;
        TextView tv_transaction_date;

        public BillViewHolder(View view) {
            super(view);
            view_top_divide = view.findViewById(R.id.view_top_divide);
            tv_transaction_title = view.findViewById(R.id.tv_transaction_title);
            tv_transaction_time = view.findViewById(R.id.tv_transaction_time);
            tv_transaction_money = view.findViewById(R.id.tv_transaction_money);
            tv_transaction_status = view.findViewById(R.id.tv_transaction_status);
            iv_transaction_icon = view.findViewById(R.id.iv_transaction_icon);
            tv_transaction_date = view.findViewById(R.id.tv_transaction_date);
        }
    }
}
