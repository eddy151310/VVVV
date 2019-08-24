package com.ahdi.wallet.app.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.ahdi.wallet.R;
import com.baidu.mapapi.search.core.PoiInfo;

import java.util.List;


public class PoiAdapter extends BaseAdapter implements SectionIndexer {

    private List poi_list;
    private Context mContext;

    public PoiAdapter(Context mContext, List mList) {
        this.mContext = mContext;
        this.poi_list = mList;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param mList
     */
    public void updateListView(List mList) {
        this.poi_list = mList;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return this.poi_list.size();
    }

    @Override
    public Object getItem(int position) {
        return poi_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder = null;
        final PoiInfo mPoiInfo = (PoiInfo) poi_list.get(position);
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

        mHolder.name.setText(mPoiInfo.name + "\n" + mPoiInfo.address  );
        mHolder.code.setText(mPoiInfo.city);
        int section = getSectionForPosition(position);

        if (position == getPositionForSection(section)) {
            mHolder.tvLetter.setVisibility(View.VISIBLE);
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
//        String str = mArrayList.get(position).getSortLetters();//选中后的处理
//        if(!TextUtils.isEmpty(str)){
//            char[] arr = str.toCharArray();
//            return arr[0];
//        }
        return -1;
    }

    @Override
    public int getPositionForSection(int section) {
        return -1;
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}