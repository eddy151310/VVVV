package com.ahdi.lib.utils.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ahdi.lib.utils.R;

import java.util.List;

/**
 * @author admin
 */

public class PhoneNumberListAdapter extends BaseAdapter {

    private List<String> phoneList;
    private Context context;

    public PhoneNumberListAdapter(Context context, List<String> list) {
        this.context = context;
        this.phoneList = list;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_item_phone_list, null,true);
            holder.tv_phone = convertView.findViewById(R.id.tv_phone);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_phone.setText(phoneList.get(position));
        return convertView;
    }

    @Override
    public int getCount() {
        return phoneList != null ? phoneList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return phoneList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder{
        TextView tv_phone;
    }
}
