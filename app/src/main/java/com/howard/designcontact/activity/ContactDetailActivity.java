package com.howard.designcontact.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Window;
import android.widget.ImageView;

import com.howard.designcontact.R;
import com.howard.designcontact.adapter.ContactDetailAdapter;
import com.howard.designcontact.helper.ContactOpenHelper;
import com.howard.designcontact.helper.MyDividerItemDecoration;
import com.howard.designcontact.mContact;
import com.howard.designcontact.mPhone;

import java.util.ArrayList;

public class ContactDetailActivity extends AppCompatActivity {
    ContactOpenHelper contactOpenHelper;
    SQLiteDatabase dbRead;
    ImageView imageView;
    mContact contact;
    CardView mCardView;

    private RecyclerView mRecyclerView;
    private ContactDetailAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<mPhone> mPhones = new ArrayList<mPhone>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition explode = TransitionInflater.from(getApplicationContext()).inflateTransition(R.transition.slide);
        getWindow().setEnterTransition(explode);

        super.onCreate(savedInstanceState);
        contactOpenHelper = new ContactOpenHelper(getApplicationContext());
        dbRead = contactOpenHelper.getReadableDatabase();

        contact = getIntent().getParcelableExtra("mContact");

        setContentView(R.layout.activity_contact_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(contact.getName());
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        imageView = (ImageView) findViewById(R.id.pic_detail);
        imageView.setImageBitmap(contact.getPhotoLarge());
        mCardView = (CardView) findViewById(R.id.card_detail);


        initData();
        initView();

    }

    private void initData() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mAdapter = new ContactDetailAdapter(getData());
    }

    private void initView() {

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_contact_detail);
        // 设置布局管理器
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // 设置adapter
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    private ArrayList<mPhone> getData() {
        mPhone temp;
        dbRead = contactOpenHelper.getReadableDatabase();
        String[] COLUMN_NAME = new String[]{"phoneNumber", "phoneType"};

        Cursor cursor = dbRead.query("phoneInfo", COLUMN_NAME, "nameId=?", new String[]{"" + contact.getId()}, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                temp = new mPhone();
                temp.setPhone(cursor.getString(0));
                temp.setType(cursor.getString(1));

                mPhones.add(temp);
            }
        }

        cursor.close();

        return mPhones;
    }
}
