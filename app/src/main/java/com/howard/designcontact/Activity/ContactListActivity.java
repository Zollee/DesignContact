package com.howard.designcontact.Activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.howard.designcontact.Adapter.ContactItemAdapter;
import com.howard.designcontact.Helper.ContactOpenHelper;
import com.howard.designcontact.Helper.MyDividerItemDecoration;
import com.howard.designcontact.R;
import com.howard.designcontact.mContact;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import static android.support.v4.app.ActivityOptionsCompat.makeSceneTransitionAnimation;

public class ContactListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ContactOpenHelper contactOpenHelper;
    SQLiteDatabase dbRead;
    private RecyclerView mRecyclerView;
    private ContactItemAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<mContact> mContacts = new ArrayList<mContact>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        contactOpenHelper = new ContactOpenHelper(getApplicationContext());


        initData();
        initView();
    }

    private void initData() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new ContactItemAdapter(getData());
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // 设置布局管理器
        mRecyclerView.setLayoutManager(mLayoutManager);
        // 设置adapter
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        mAdapter.setOnItemClickListener(new ContactItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getApplicationContext(), ContactDetailActivity.class);
                intent.putExtra("mContact", mContacts.get(position));
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ContactListActivity.this).toBundle());
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(ContactListActivity.this, "long click " + position + " item", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ArrayList<mContact> getData() {

        dbRead = contactOpenHelper.getReadableDatabase();
        String[] COLUMN_NAME = new String[]{"_id", "name", "photo", "isStarred"};

        mContact temp;
        Cursor cursor = dbRead.query("nameInfo", COLUMN_NAME, null, null, null, null, null, null);
        String phoneName;

        if (cursor != null) {
            while (cursor.moveToNext()) {
                temp = new mContact();

                phoneName = cursor.getString(1);

                byte[] in = cursor.getBlob(2);

                temp.photoCore = in;
                temp.name = phoneName;
                temp.photo = temp.getPhoto();

                mContacts.add(temp);
            }
        }

        //按中文排序
        Collections.sort(mContacts, new Comparator<mContact>() {
            public int compare(mContact o1, mContact o2) {
                return Collator.getInstance(Locale.CHINESE).compare(o1.getName(), o2.getName());
            }
        });

        cursor.close();

        return mContacts;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}