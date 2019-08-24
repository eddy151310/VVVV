package com.ahdi.wallet.app.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.schemas.PhoneAreaCodeSchema;
import com.ahdi.lib.utils.listener.OnItemClickListener;

import java.util.List;

/**
 * 选择区号adapter
 *
 * @author zhaohe
 */
public class PhoneAreaCodeAdapter extends RecyclerView.Adapter<PhoneAreaCodeAdapter.PhoneCodeHolder> implements View.OnClickListener {

    private List<PhoneAreaCodeSchema> list = null;
    private OnItemClickListener mOnItemClickListener = null;

    public PhoneAreaCodeAdapter(List<PhoneAreaCodeSchema> list) {
        this.list = list;
    }

    @Override
    public PhoneCodeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_phone_area_code, null);
        view.setOnClickListener(this);
        PhoneCodeHolder phoneCodeHolder = new PhoneCodeHolder(view);
        return phoneCodeHolder;
    }

    @Override
    public void onBindViewHolder(PhoneCodeHolder holder, int position) {
        PhoneAreaCodeSchema phoneAreaCodeSchema = list.get(position);
        holder.tv_name.setText(phoneAreaCodeSchema.name);
        holder.tv_code.setText(phoneAreaCodeSchema.formatCode);
        //是否显示首字母区域
        if (phoneAreaCodeSchema.isShowFirstLetter) {
            holder.tv_first_letter.setVisibility(View.VISIBLE);
            holder.tv_first_letter.setText(phoneAreaCodeSchema.firstLetter);
        } else {
            holder.tv_first_letter.setVisibility(View.GONE);
        }
        //首字母区域上方的横线不显示
        if (position < list.size() - 1 && list.get(position + 1).isShowFirstLetter) {
            holder.v_divider.setVisibility(View.INVISIBLE);
        } else {
            holder.v_divider.setVisibility(View.VISIBLE);
        }
        holder.itemView.setTag(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
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

    public class PhoneCodeHolder extends RecyclerView.ViewHolder {

        public TextView tv_first_letter;
        public TextView tv_name;
        public TextView tv_code;
        public View v_divider;

        public PhoneCodeHolder(View itemView) {
            super(itemView);
            tv_first_letter = itemView.findViewById(R.id.tv_first_letter);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_code = itemView.findViewById(R.id.tv_code);
            v_divider = itemView.findViewById(R.id.v_divider);
        }
    }
}