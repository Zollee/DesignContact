package com.howard.designcontact.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.howard.designcontact.R;
import com.howard.designcontact.adapter.ContactEditAdapter;
import com.howard.designcontact.helper.ContactOpenHelper;
import com.howard.designcontact.helper.MyDividerItemDecoration;
import com.howard.designcontact.mContact;
import com.howard.designcontact.mPhone;

import java.util.ArrayList;

public class ContactEditActivity extends AppCompatActivity {
    mContact contact;
    ArrayList<mPhone> mPhones;
    Button mButton_edit_add;
    ContactOpenHelper contactOpenHelper;
    SQLiteDatabase dbWrite;

    ImageView mImageView_photo;
    EditText mEditText_name;

    private RecyclerView mRecyclerView;
    private ContactEditAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_clear);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        contactOpenHelper = new ContactOpenHelper(getApplicationContext());
        dbWrite = contactOpenHelper.getWritableDatabase();

        contact = getIntent().getParcelableExtra("mContact");
        mPhones = getIntent().getParcelableArrayListExtra("mPhone");

        mImageView_photo = (ImageView) findViewById(R.id.imageView_photo);
        mEditText_name = (EditText) findViewById(R.id.edit_name_text);
        mButton_edit_add = (Button) findViewById(R.id.button_edit_add);

        initData();
        initView();

    }

    private void initData() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new ContactEditAdapter(mPhones);
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_contact_edit);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        mImageView_photo.setImageBitmap(contact.getPhotoLarge());
        mEditText_name.setText(contact.getName());

        mButton_edit_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = mAdapter.getItemCount();

                mPhones.clear();

                for (int position = 0; position < number; position++) {
                    View view = mRecyclerView.getChildAt(position);
                    if (null != mRecyclerView.getChildViewHolder(view)) {
                        ContactEditAdapter.ViewHolder holder = (ContactEditAdapter.ViewHolder) mRecyclerView.getChildViewHolder(view);
                        if (!holder.edit_number_text.getText().toString().equals("")) {
                            //记录更改
                            mPhone temp = new mPhone();
                            temp.setPhone(holder.edit_number_text.getText().toString());
                            temp.setType(holder.spinner.getSelectedItemPosition());
                            mPhones.add(temp);
                        }
                    }
                }

                //判断最后一个view是否为空
                View view = mRecyclerView.getChildAt(number - 1);
                if (null != mRecyclerView.getChildViewHolder(view)) {
                    ContactEditAdapter.ViewHolder holder = (ContactEditAdapter.ViewHolder) mRecyclerView.getChildViewHolder(view);
                    if (!holder.edit_number_text.getText().toString().equals("")) {
                        mPhones.add(new mPhone());
                        mAdapter.updateData(mPhones);
                    }
                }
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact_edit, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        ContentValues values;
        switch (item.getItemId()) {
            case R.id.menu_check:
                String name = mEditText_name.getText().toString();
                //检测姓名
                if (name.equals("")) {
                    new AlertDialog.Builder(this)
                            .setMessage("姓名不能为空")
                            .setPositiveButton("返回", null)
                            .show();
                    return true;
                }

                int number = mAdapter.getItemCount();
                mPhones.clear();

                for (int position = 0; position < number; position++) {
                    View view = mRecyclerView.getChildAt(position);
                    if (null != mRecyclerView.getChildViewHolder(view)) {
                        ContactEditAdapter.ViewHolder holder = (ContactEditAdapter.ViewHolder) mRecyclerView.getChildViewHolder(view);
                        if (!holder.edit_number_text.getText().toString().equals("")) {
                            //记录更改
                            mPhone temp = new mPhone();
                            temp.setPhone(holder.edit_number_text.getText().toString());
                            temp.setType(holder.spinner.getSelectedItemPosition());
                            mPhones.add(temp);
                        }
                    }
                }

                //检测电话
                if (mPhones.isEmpty()) {
                    new AlertDialog.Builder(this)
                            .setMessage("电话号码不能为空")
                            .setPositiveButton("返回", null)
                            .show();
                    mPhones.add(new mPhone());
                    return true;
                }

                //插入数据
                if (!name.equals(contact.getName())) {
                    values = new ContentValues();
                    values.put("name", name);
                    dbWrite.update("nameInfo", values, "_id=?", new String[]{"" + contact.getId()});
                }

                dbWrite.delete("phoneInfo", "nameId=" + contact.getId(), null);

                for (int i = 0; i < mPhones.size(); i++) {
                    values = new ContentValues();
                    values.put("nameId", contact.getId());
                    values.put("phoneNumber", mPhones.get(i).getPhone());
                    values.put("phoneType", mPhones.get(i).getType());
                    dbWrite.insert("phoneInfo", null, values);
                }
                dbWrite.close();

                startActivity(new Intent(getApplicationContext(), ContactListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("确认返回？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("否", null)
                .show();
    }

}
