package com.howard.designcontact.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.howard.designcontact.mPhone;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ContactInsertActivity extends AppCompatActivity {
    ArrayList<mPhone> mPhones;
    Button mButton_edit_add;
    ContactOpenHelper contactOpenHelper;
    SQLiteDatabase dbRead;
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
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        contactOpenHelper = new ContactOpenHelper(getApplicationContext());
        dbRead = contactOpenHelper.getReadableDatabase();
        dbWrite = contactOpenHelper.getWritableDatabase();

        mImageView_photo = (ImageView) findViewById(R.id.imageView_photo);
        mEditText_name = (EditText) findViewById(R.id.edit_name_text);
        mButton_edit_add = (Button) findViewById(R.id.button_edit_add);

        mPhones = new ArrayList<mPhone>();
        mPhones.add(new mPhone());

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

        Bitmap contactPhoto;
        ByteArrayOutputStream baos;
        byte[] img_small = null;
        byte[] img_large = null;

        switch (item.getItemId()) {
            case R.id.menu_check:
                String name = mEditText_name.getText().toString();
                //检测姓名为空
                if (name.equals("")) {
                    new AlertDialog.Builder(this)
                            .setMessage("姓名不能为空")
                            .setPositiveButton("返回", null)
                            .show();
                    return true;
                }

                //检测重名
                Cursor cursorTemp;
                cursorTemp = dbRead.query("nameInfo", new String[]{"_id"}, "name=?", new String[]{"" + name}, null, null, null);

                //插入姓名
                if (cursorTemp.getCount() == 0) {
                    contactPhoto = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                    baos = new ByteArrayOutputStream();
                    contactPhoto.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    img_small = baos.toByteArray();
                    img_large = img_small;

                    values = new ContentValues();
                    values.put("name", name);
                    values.put("photoSmall", img_small);
                    values.put("photoLarge", img_large);
                    dbWrite.insert("nameInfo", null, values);
                } else {
                    new AlertDialog.Builder(this)
                            .setMessage("姓名与已有重复")
                            .setPositiveButton("返回", null)
                            .show();
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

                cursorTemp = dbRead.query("nameInfo", new String[]{"_id"}, "name=?", new String[]{"" + name}, null, null, null);
                cursorTemp.moveToFirst();

                //插入数据
                for (int i = 0; i < mPhones.size(); i++) {
                    values = new ContentValues();
                    values.put("nameId", cursorTemp.getInt(0));
                    values.put("phoneNumber", mPhones.get(i).getPhone());
                    values.put("phoneType", mPhones.get(i).getType());
                    dbWrite.insert("phoneInfo", null, values);
                }
                dbWrite.close();
                dbRead.close();
                cursorTemp.close();

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
