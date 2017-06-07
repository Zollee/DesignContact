package com.howard.designcontact.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.howard.designcontact.R;
import com.howard.designcontact.adapter.ContactDetailAdapter;
import com.howard.designcontact.helper.AsynNetUtils;
import com.howard.designcontact.helper.ContactOpenHelper;
import com.howard.designcontact.helper.MyDividerItemDecoration;
import com.howard.designcontact.mContact;
import com.howard.designcontact.mPhone;

import java.util.ArrayList;

public class ContactDetailActivity extends AppCompatActivity {
    ContactOpenHelper contactOpenHelper;
    SQLiteDatabase dbRead;
    SQLiteDatabase dbWrite;
    ImageView imageView;
    mContact contact;
    CardView mCardView;
    CollapsingToolbarLayout mToolBarLayout;
    private SharedPreferences preferences;

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

        String[] COLUMN_NAME = new String[]{"_id", "name", "photoLarge", "isStarred"};

        Cursor cursor = dbRead.query("nameInfo", COLUMN_NAME, "_id=?", new String[]{"" + contact.getId()}, null, null, null, null);
        cursor.moveToFirst();

        contact.setName(cursor.getString(1));
        contact.setPhotoDisplay(cursor.getBlob(2));
        contact.setIsStarred(cursor.getInt(3));

        //设置标题
        setContentView(R.layout.activity_contact_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(contact.getName());
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new ContactDetailAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Uri uri = Uri.parse("tel:" + mPhones.get(position).getPhone());
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
            }

            public void onIconClick(View view, int position) {
                Uri uri = Uri.parse("smsto:" + mPhones.get(position).getPhone());
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                //长按复制到剪贴板
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Text Label", mPhones.get(position).getPhone());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "" + mPhones.get(position).getPhone() + "已复制到剪贴板", Toast.LENGTH_SHORT).show();
            }
        });

        mRecyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    private ArrayList<mPhone> getData() {
        mPhone temp;
        String[] COLUMN_NAME = new String[]{"phoneNumber", "phoneType"};

        Cursor cursor = dbRead.query("phoneInfo", COLUMN_NAME, "nameId=?", new String[]{"" + contact.getId()}, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                temp = new mPhone();
                temp.setPhone(cursor.getString(0));
                temp.setType(cursor.getInt(1));

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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact_detail, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_detail_star);
        if (contact.getIsStarred() == 0)
            menuItem.setIcon(R.drawable.ic_star_border);
        else
            menuItem.setIcon(R.drawable.ic_star);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        dbWrite = contactOpenHelper.getWritableDatabase();

        switch (item.getItemId()) {
            case R.id.menu_detail_delete:
                new AlertDialog.Builder(this)
                        .setMessage("确认删除？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                preferences = getSharedPreferences("phone", Context.MODE_PRIVATE);
                                AsynNetUtils.post("http://47.94.97.91/demo/delete", "username=" + preferences.getString("username", "") + "&Id=" + contact.getId(), new AsynNetUtils.Callback() {
                                    @Override
                                    public void onResponse(String response) {
                                        if (response.equals("删除成功")) {
                                            dbWrite.delete("phoneInfo", "nameId=" + contact.getId(), null);
                                            dbWrite.delete("nameInfo", "_id=" + contact.getId(), null);
                                            dbWrite.close();
                                            onBackPressed();
                                        } else
                                            Toast.makeText(getApplicationContext(), "删除失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("否", null)
                        .show();
                return true;

            case R.id.menu_detail_star:
                if (contact.getIsStarred() == 0) {
                    item.setIcon(R.drawable.ic_star);
                    preferences = getSharedPreferences("phone", Context.MODE_PRIVATE);
                    ContentValues values = new ContentValues();
                    values.put("isStarred", 1);
                    dbWrite.update("nameInfo", values, "_id=?", new String[]{"" + contact.getId()});

                    AsynNetUtils.post("http://47.94.97.91/demo/changeStar", "user=" + preferences.getString("username", "") + "&id=" + contact.getId() + "&star=1", new AsynNetUtils.Callback() {
                        @Override
                        public void onResponse(String response) {
                        }
                    });
                } else {
                    preferences = getSharedPreferences("phone", Context.MODE_PRIVATE);

                    item.setIcon(R.drawable.ic_star_border);
                    ContentValues values = new ContentValues();
                    values.put("isStarred", 0);
                    dbWrite.update("nameInfo", values, "_id=?", new String[]{"" + contact.getId()});

                    AsynNetUtils.post("http://47.94.97.91/demo/changeStar", "user=" + preferences.getString("username", "") + "&id=" + contact.getId() + "&star=0", new AsynNetUtils.Callback() {
                        @Override
                        public void onResponse(String response) {
                        }
                    });
                }
                return true;

            case R.id.menu_detail_edit:
                intent = new Intent(getApplicationContext(), ContactEditActivity.class);
                intent.putExtra("mContact", contact);
                intent.putExtra("mPhone", mPhones);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
