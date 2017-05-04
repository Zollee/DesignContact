package com.howard.designcontact.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.howard.designcontact.R;
import com.howard.designcontact.mContact;

import java.util.ArrayList;

/**
 * Created by zhaohaoran on 2017/4/10.
 */

public class ContactItemAdapter extends RecyclerView.Adapter<ContactItemAdapter.ViewHolder> {
    /**
     * 展示数据
     */
    private ArrayList<mContact> mData;


    private ContactItemAdapter.OnItemClickListener onItemClickListener;

    public ContactItemAdapter(ArrayList<mContact> data) {
        this.mData = data;
    }

    public void updateData(ArrayList<mContact> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    /**
     * 设置回调监听
     *
     * @param listener
     */
    public void setOnItemClickListener(ContactItemAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_contact_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // 绑定数据
        holder.mName.setText(mData.get(position).getName());
        holder.mPic.setImageBitmap(mData.get(position).getPhotoSmall());

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

        TextView mName;
        ImageView mPic;

        public ViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.item_list_name);
            //  mPhone = (TextView) itemView.findViewById(R.id.item_list_phone);
            mPic = (ImageView) itemView.findViewById(R.id.item_list_pic);
        }
    }
}
