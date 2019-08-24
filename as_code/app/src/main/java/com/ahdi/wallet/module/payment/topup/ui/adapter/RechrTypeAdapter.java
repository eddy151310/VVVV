package com.ahdi.wallet.module.payment.topup.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahdi.wallet.module.payment.topup.schemas.RechrTypeSchema;
import com.ahdi.lib.utils.imagedown.ImageDownUtil;
import com.ahdi.lib.utils.listener.OnItemClickListener;
import com.ahdi.wallet.R;

import java.util.List;

/**
 * Created by Administrator on 2017/10/16.
 *
 * @author admin
 */
public class RechrTypeAdapter extends RecyclerView.Adapter<RechrTypeAdapter.RechrTypeHolder> implements View.OnClickListener {

    private Context mContext;
    private List<RechrTypeSchema> rechrTypeSchemas;
    private OnItemClickListener onItemClickListener = null;


    public RechrTypeAdapter(Context context, List<RechrTypeSchema> rechrTypeSchemas) {
        this.mContext = context;
        this.rechrTypeSchemas = rechrTypeSchemas;
    }

    @Override
    public RechrTypeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rechr_type_item, null);
        RechrTypeHolder holder = new RechrTypeHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(RechrTypeHolder holder, int position) {
        RechrTypeSchema rechrTypeSchema = rechrTypeSchemas.get(position);
        if (rechrTypeSchema != null) {
            ImageDownUtil.downTopupTypeIconImage(mContext, rechrTypeSchema.Icon, holder.iv_icon);

            String rechrTypeGroupName = rechrTypeSchema.getRechrTypeGroupName();

            // 是否显示组名称
            if (!TextUtils.isEmpty(rechrTypeGroupName)) {
                holder.tv_rechr_type_group_name.setVisibility(View.VISIBLE);
                holder.tv_rechr_type_group_name.setText(rechrTypeGroupName);

            } else {
                holder.tv_rechr_type_group_name.setVisibility(View.GONE);
            }
            holder.tv_rechr_type_name.setText(rechrTypeSchema.Title);

            holder.tv_rechr_type_desc.setText(rechrTypeSchema.Desc);

            // 是否可点击
            if (TextUtils.equals(rechrTypeSchema.Invoke, "disable")) {
                holder.rechr_type_item.setEnabled(false);

            } else {
                holder.rechr_type_item.setEnabled(true);
            }

            // 是否隐藏底线
            if (rechrTypeSchema.isHiddenBottomLine()) {
                holder.v_bottom_line.setVisibility(View.GONE);

            } else {
                holder.v_bottom_line.setVisibility(View.VISIBLE);
            }
        }

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return (rechrTypeSchemas == null || rechrTypeSchemas.size() == 0) ? 0 : rechrTypeSchemas.size();
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

    public class RechrTypeHolder extends RecyclerView.ViewHolder {

        View rechr_type_item, v_bottom_line;
        TextView tv_rechr_type_group_name;
        ImageView iv_icon;
        TextView tv_rechr_type_name;
        TextView tv_rechr_type_desc;
        TextView tv_card_number;

        public RechrTypeHolder(View view) {
            super(view);
            rechr_type_item = view.findViewById(R.id.rechr_type_item);
            tv_rechr_type_group_name = view.findViewById(R.id.tv_rechr_type_group_name);
            iv_icon = view.findViewById(R.id.iv_icon);
            tv_rechr_type_name = view.findViewById(R.id.tv_rechr_type_name);
            tv_rechr_type_desc = view.findViewById(R.id.tv_rechr_type_desc);
            tv_card_number = view.findViewById(R.id.tv_card_number);
            v_bottom_line = view.findViewById(R.id.v_bottom_line);
        }
    }
}
