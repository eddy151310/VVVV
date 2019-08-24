package com.ahdi.wallet.app.ui.fragments.home.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ahdi.lib.utils.R;
import com.ahdi.wallet.app.ui.fragments.home.bean.ShoppingBean;
import com.ahdi.wallet.app.ui.fragments.home.interfaces.OnItem;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

//RecyclerView适配器，如果自己的项目适配器多种样式，自己可以创建
public class ReclAdapter extends RecyclerView.Adapter<ReclAdapter.ViewHolder> {
    Context context;
    List<ShoppingBean> shoppingBeanList=new ArrayList<>();
    public ReclAdapter(Context context){
        this.context=context;
    }
    //RecyclerView添加数据
    public void addData(List<ShoppingBean> shoppingBeanList){
        this.shoppingBeanList=shoppingBeanList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.fff_recl_adapter,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Glide.with(context).load(shoppingBeanList.get(position).getPic()).into(holder.iv_1);
        holder.tv_1.setText(shoppingBeanList.get(position).getPage()+"");
        holder.ll_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItem.onItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shoppingBeanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_1;
        ImageView iv_1;
        TextView tv_1;
        public ViewHolder(View itemView) {
            super(itemView);
            ll_1=itemView.findViewById(R.id.ll_1);
            iv_1=itemView.findViewById(R.id.iv_1);
            tv_1=itemView.findViewById(R.id.tv_1);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    //RecyclerView的点击回调
    OnItem onItem;
    public void onItemClick(OnItem onItem){
        this.onItem=onItem;
    }
}
