package com.howard.designcontact.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.howard.designcontact.R;
import com.howard.designcontact.mPhone;

import java.util.ArrayList;

/**
 * Created by zhaohaoran on 2017/4/24.
 */

public class ContactDetailAdapter extends RecyclerView.Adapter<ContactDetailAdapter.ViewHolder> {
    /**
     * 展示数据
     */
    private ArrayList<mPhone> mData;

    private ContactDetailAdapter.OnItemClickListener onItemClickListener;

    public ContactDetailAdapter(ArrayList<mPhone> data) {
        this.mData = data;
    }

    public void updateData(ArrayList<mPhone> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    /**
     * 设置回调监听
     *
     * @param listener
     */
    public void setOnItemClickListener(ContactDetailAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_contact_detail, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ContactDetailAdapter.ViewHolder holder, int position) {
        // 绑定数据
        holder.phoneNumber.setText(mData.get(position).getPhone());
        holder.phoneType.setText(mData.get(position).getType());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, pos);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemLongClick(holder.itemView, pos);
                }
                //表示此事件已经消费，不会触发单击事件
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView icon_msg;
        TextView phoneNumber;
        TextView phoneType;

        public ViewHolder(View itemView) {
            super(itemView);
            icon_msg = (ImageView) itemView.findViewById(R.id.image_msg);
            phoneNumber = (TextView) itemView.findViewById(R.id.number_detail);
            phoneType = (TextView) itemView.findViewById(R.id.type_detail);
        }
    }
}
