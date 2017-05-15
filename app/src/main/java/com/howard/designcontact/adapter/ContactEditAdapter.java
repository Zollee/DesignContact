package com.howard.designcontact.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.howard.designcontact.R;
import com.howard.designcontact.mPhone;

import java.util.ArrayList;

/**
 * Created by Howard on 05/14/2017.
 */

public class ContactEditAdapter extends RecyclerView.Adapter<ContactEditAdapter.ViewHolder> {
    /**
     * 展示数据
     */
    private ArrayList<mPhone> mPhones;

    public ContactEditAdapter(ArrayList<mPhone> mPhones) {
        this.mPhones = mPhones;
    }

    public void updateData(ArrayList<mPhone> data) {
        this.mPhones = data;
        notifyDataSetChanged();
    }

    @Override
    public ContactEditAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_contact_edit, parent, false);
        return new ContactEditAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ContactEditAdapter.ViewHolder holder, final int position) {
        // 绑定数据
        holder.edit_number_text.setText(mPhones.get(position).getPhone());
        holder.spinner.setSelection(mPhones.get(position).getType());
    }

    @Override
    public int getItemCount() {
        return mPhones == null ? 0 : mPhones.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public EditText edit_number_text;
        public Spinner spinner;

        public ViewHolder(View itemView) {
            super(itemView);
            edit_number_text = (EditText) itemView.findViewById(R.id.edit_number_text);
            spinner = (Spinner) itemView.findViewById(R.id.spinner);
        }
    }
}
