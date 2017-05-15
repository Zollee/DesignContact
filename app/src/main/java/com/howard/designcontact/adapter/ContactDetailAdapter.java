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
    private ArrayList<mPhone> mPhones;

    private OnItemClickListener onItemClickListener;

    public ContactDetailAdapter(ArrayList<mPhone> mPhones) {
        this.mPhones = mPhones;
    }

    public void updateData(ArrayList<mPhone> data) {
        this.mPhones = data;
        notifyDataSetChanged();
    }

    /**
     * 设置回调监听
     *
     * @param listener
     */
    public void setOnItemClickListener(final OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_contact_detail, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ContactDetailAdapter.ViewHolder holder, final int position) {
        // 绑定数据
        holder.phoneNumber.setText(mPhones.get(position).getPhone());

        switch (mPhones.get(position).getType()) {
            case 0:
                holder.phoneType.setText("手机");
                break;
            case 1:
                holder.phoneType.setText("家庭");
                break;
            case 2:
                holder.phoneType.setText("工作");
                break;
            case 3:
                holder.phoneType.setText("其他");
                break;
        }

        holder.phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, pos);
                }
            }
        });

        holder.phoneNumber.setOnLongClickListener(new View.OnLongClickListener() {
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

        holder.icon_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onIconClick(holder.itemView, pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPhones == null ? 0 : mPhones.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onIconClick(View view, int position);

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
