package com.howard.designcontact;

import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;

    private MyAdapter mAdapter;

    private RecyclerView.LayoutManager mLayoutManager;

    private static final String[] PHONES_PROJECTION = new String[] {
            Phone.DISPLAY_NAME, Phone.NUMBER, Phone.TYPE, Photo.PHOTO_ID, Phone.CONTACT_ID };

    private ArrayList<mContact> mContacts = new ArrayList<mContact>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        initData();
        initView();
    }

    private void initData() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new MyAdapter(getData());
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // 设置布局管理器
        mRecyclerView.setLayoutManager(mLayoutManager);
        // 设置adapter
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        mAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(MainActivity.this,"click " + position + " item", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(MainActivity.this,"long click " + position + " item", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ArrayList<mContact> getData() {
        mContact temp;
        Cursor cursor = getContentResolver().query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
        String phoneName;
     //   String phoneNumber;
     //   String phoneType = " ";
        Long contactId;
        Long photoId;
        Bitmap contactPhoto = null;

        if (cursor != null) {
            while (cursor.moveToNext()) {
                temp = new mContact();

                //获取名称
                phoneName = cursor.getString(0);
             //   phoneNumber = cursor.getString(1);
                //int type = cursor.getInt(2);
                photoId = cursor.getLong(3);
                contactId = cursor.getLong(4);

                if(photoId > 0 ) {
                    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contactId);
                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), uri);
                    contactPhoto = BitmapFactory.decodeStream(input);
                }else {
                    contactPhoto = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                }

                /*
                switch (type) {
                    case Phone.TYPE_HOME:
                        phoneType = "HOME";
                        break;
                    case Phone.TYPE_MOBILE:
                        phoneType = "MOBILE";
                        // do something with the Mobile number here...
                        break;
                    case Phone.TYPE_WORK:
                        phoneType = "WORK";
                        break;

                    case Phone.TYPE_FAX_WORK:
                        phoneType = "FAX_WORK";
                        break;

                    case Phone.TYPE_FAX_HOME:
                        phoneType = "FAX_HOME";
                        break;

                    case Phone.TYPE_PAGER:
                        phoneType = "PAGER";
                        break;

                    case Phone.TYPE_OTHER:
                        phoneType = "OTHER";
                        break;

                    case Phone.TYPE_CALLBACK:
                        phoneType = "CALLBACK";
                        break;

                    case Phone.TYPE_CAR:
                        phoneType = "CAR";
                        break;

                    case Phone.TYPE_COMPANY_MAIN:
                        phoneType = "COMPANY_MAIN";
                        break;

                    case Phone.TYPE_ISDN:
                        phoneType = "ISDN";
                        break;

                    case Phone.TYPE_MAIN:
                        phoneType = "MAIN";
                        break;

                    case Phone.TYPE_OTHER_FAX:
                        phoneType = "OTHER_FAX";
                        break;

                    case Phone.TYPE_RADIO:
                        phoneType = "RADIO";
                        break;

                    case Phone.TYPE_TELEX:
                        phoneType = "TELEX";
                        break;

                    case Phone.TYPE_TTY_TDD:
                        phoneType = "TTY_TDD";
                        break;

                    case Phone.TYPE_WORK_MOBILE:
                        phoneType = "WORK_MOBILE";
                        break;

                    case Phone.TYPE_WORK_PAGER:
                        phoneType = "WORK_PAGER";
                        break;

                    case Phone.TYPE_ASSISTANT:
                        phoneType = "ASSISTANT";
                        break;

                    case Phone.TYPE_MMS:
                        phoneType = "MMS";
                        break;

                    default:
                        phoneType = "默认";

                }
*/
                temp.name = phoneName;
              //  temp.number = phoneNumber;
                //temp.type = phoneType;
                temp.photo = contactPhoto;

                mContacts.add(temp);
            }
        }

        //按中文排序
        Collections.sort(mContacts, new Comparator<mContact>() {
            public int compare(mContact o1, mContact o2) {
                return Collator.getInstance(Locale.CHINESE).compare(o1.getName(), o2.getName());
            }
        });

        //删除重复姓名
        for (int i = 0, j = 1; j < mContacts.size(); i++, j++){
            if (mContacts.get(i).getName().equals(mContacts.get(j).getName()))
                mContacts.remove(i);
        }

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
