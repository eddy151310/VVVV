package com.ahdi.wallet.module.payment.transfer.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahdi.wallet.module.payment.transfer.schemas.TAContactSchema;
import com.ahdi.wallet.R;
import com.ahdi.lib.utils.imagedown.ImageDownUtil;
import com.ahdi.lib.utils.listener.OnItemClickListener;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/10/16.
 *
 * @author admin
 */
public class TransferContactAdapter extends RecyclerView.Adapter<TransferContactAdapter.TransferHolder> implements View.OnClickListener {

    private Context mContext;
    private ArrayList<TAContactSchema> list;
    private OnItemClickListener onItemClickListener;

    public TransferContactAdapter(Context context, ArrayList<TAContactSchema> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    public TransferHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transfer_view, null);
        TransferHolder transferHolder = new TransferHolder(view);
        view.setOnClickListener(this);
        return transferHolder;
    }

    @Override
    public void onBindViewHolder(TransferHolder holder, int position) {
        // 最底下那个item设置外边距30px
        if (position == list.size() - 1) {
            holder.view_bottom.setVisibility(View.VISIBLE);

        } else {
            holder.view_bottom.setVisibility(View.GONE);
        }
        TAContactSchema contactSchema = list.get(position);
        if (TextUtils.isEmpty(contactSchema.nName)) {
            holder.tv_nname.setText(contactSchema.lName);
            holder.tv_sname.setText("");
        } else {
            holder.tv_nname.setText(contactSchema.nName);
            holder.tv_sname.setText(contactSchema.sName);
        }
        ImageDownUtil.downOtherUserPhotoImage(mContext, contactSchema.avatar, holder.iv_icon);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return (list == null || list.size() == 0) ? 0 : list.size();
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

    class TransferHolder extends RecyclerView.ViewHolder {

        ImageView iv_icon;
        TextView tv_sname;
        TextView tv_nname;
        View view_bottom;

        public TransferHolder(View itemView) {
            super(itemView);
            iv_icon = itemView.findViewById(R.id.iv_icon);
            tv_sname = itemView.findViewById(R.id.tv_sname);
            tv_nname = itemView.findViewById(R.id.tv_nname);
            view_bottom = itemView.findViewById(R.id.view_bottom);
        }
    }
}
