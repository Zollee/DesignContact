package com.howard.designcontact.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.Window;
import android.view.WindowManager;
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
    CollapsingToolbarLayout mToolBarLayout;

    private RecyclerView mRecyclerView;
    private ContactDetailAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<mPhone> mPhones = new ArrayList<mPhone>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(TransitionInflater.from(getApplicationContext()).inflateTransition(R.transition.slide));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        super.onCreate(savedInstanceState);
        contactOpenHelper = new ContactOpenHelper(getApplicationContext());
        dbRead = contactOpenHelper.getReadableDatabase();

        contact = getIntent().getParcelableExtra("mContact");

        //设置标题
        setContentView(R.layout.activity_contact_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(contact.getName());
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        Palette p = Palette.from(contact.getPhotoLarge()).generate();
        int vibrant;
        int vibrantDark;

        if (p.getVibrantSwatch() != null) {
            vibrant = p.getVibrantColor(0x000000);
            vibrantDark = colorBurn(vibrant);
        } else {
            vibrant = getResources().getColor(R.color.colorPrimary);
            vibrantDark = getResources().getColor(R.color.colorPrimaryDark);
        }

        //设置StatusBar颜色
        getWindow().setStatusBarColor(vibrantDark);

        //设置ToolBar颜色
        mToolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        mToolBarLayout.setContentScrimColor(vibrant);

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

    public int colorBurn(int RGBValues) {
        int red = RGBValues >> 16 & 0xFF;
        int green = RGBValues >> 8 & 0xFF;
        int blue = RGBValues & 0xFF;
        red = (int) Math.floor(red * (1 - 0.1));
        green = (int) Math.floor(green * (1 - 0.1));
        blue = (int) Math.floor(blue * (1 - 0.1));
        return Color.rgb(red, green, blue);
    }
}
