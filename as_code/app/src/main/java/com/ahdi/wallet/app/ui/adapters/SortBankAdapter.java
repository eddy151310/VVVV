package com.ahdi.wallet.app.ui.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.schemas.BankSchema;

import java.util.List;


public class SortBankAdapter extends BaseAdapter implements SectionIndexer {

    private List<BankSchema> list;
    private Context mContext;

    public SortBankAdapter(Context mContext, List<BankSchema> list) {
        this.mContext = mContext;
        this.list = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<BankSchema> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder = null;
        final BankSchema cv = list.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_bank, null);
            mHolder = new ViewHolder();
            mHolder.name = convertView.findViewById(R.id.tv_bankName);
            mHolder.code = convertView.findViewById(R.id.tv_bankCode);
            mHolder.line_view = convertView.findViewById(R.id.line_view);
            mHolder.tvLetter = convertView
                    .findViewById(R.id.tv_catagory);

            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        mHolder.name.setText(cv.getName());
        mHolder.code.setText(cv.getCode());
        int section = getSectionForPosition(position);

        if (position == getPositionForSection(section)) {
            mHolder.tvLetter.setVisibility(View.VISIBLE);
            mHolder.tvLetter.setText(cv.getSortLetters());
            mHolder.line_view.setVisibility(View.GONE);
        } else {
            mHolder.tvLetter.setVisibility(View.GONE);
            mHolder.line_view.setVisibility(View.VISIBLE);
        }
        return convertView;

    }


    final static class ViewHolder {
        TextView tvLetter;
        TextView name, code;
        View line_view;
    }

    @Override
    public int getSectionForPosition(int position) {
        String str = list.get(position).getSortLetters();
        if(!TextUtils.isEmpty(str)){
            char[] arr = str.toCharArray();
            return arr[0];
        }
        return -1;
    }

    @Override
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            if(!TextUtils.isEmpty(sortStr)){
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}