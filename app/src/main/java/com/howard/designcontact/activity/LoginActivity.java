package com.howard.designcontact.activity;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.howard.designcontact.R;
import com.howard.designcontact.helper.ContactOpenHelper;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;


public class LoginActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final String[] PHONES_PROJECTION = new String[]{
            Phone.DISPLAY_NAME,
            Phone.NUMBER,
            Phone.TYPE,
            Photo.PHOTO_ID,
            Phone.CONTACT_ID
    };
    ContactOpenHelper contactOpenHelper;
    SQLiteDatabase dbWrite;
    SQLiteDatabase dbRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        contactOpenHelper = new ContactOpenHelper(getApplicationContext());

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(getApplicationContext(), "请提供联系人权限", Toast.LENGTH_LONG).show();

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        } else {
            addContact();
            startActivity(new Intent(getApplicationContext(), ContactListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    public void addContact() {
        Cursor cursor = getContentResolver().query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
        String phoneName;
        String phoneNumber;
        int typeTemp;
        String phoneType;

        Long contactId;
        Long photoId;

        Bitmap contactPhoto;
        ByteArrayOutputStream baos;
        byte[] img_small = null;
        byte[] img_large = null;
        ContentValues values;

        String[] COLUMN_NAME = new String[]{"_id", "name", "photoSmall", "photoLarge", "isStarred"};
        String[] COLUMN_PHONE = new String[]{"id", "nameId", "phoneNumber", "phoneType"};

        if (cursor != null) {
            while (cursor.moveToNext()) {
                //获取姓名
                phoneName = cursor.getString(0);

                //获取电话
                phoneNumber = cursor.getString(1);

                //获取分类
                typeTemp = cursor.getInt(2);

                //获取头像id
                photoId = cursor.getLong(3);
                contactId = cursor.getLong(4);

                //将分类转换
                switch (typeTemp) {
                    case Phone.TYPE_HOME:
                        phoneType = "家庭";
                        break;
                    case Phone.TYPE_MOBILE:
                        phoneType = "手机";
                        break;
                    case Phone.TYPE_WORK:
                        phoneType = "工作";
                        break;
                    default:
                        phoneType = "其他";
                }

                //获得头像
                if (photoId > 0) {
                    Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), contactUri);

                    try {
                        img_small = IOUtils.toByteArray(input);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    input = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), contactUri, true);

                    try {
                        img_large = IOUtils.toByteArray(input);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    contactPhoto = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                    baos = new ByteArrayOutputStream();
                    contactPhoto.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    img_small = baos.toByteArray();
                    img_large = img_small;
                }

                dbRead = contactOpenHelper.getReadableDatabase();
                dbWrite = contactOpenHelper.getWritableDatabase();

                try {
                    values = new ContentValues();

                    Cursor cursorTemp;
                    cursorTemp = dbRead.query("nameInfo", COLUMN_NAME, "name=?", new String[]{"" + phoneName}, null, null, null);

                    if (cursorTemp.getCount() == 0) {
                        //无重名
                        values.put("name", phoneName);
                        values.put("photoSmall", img_small);
                        values.put("photoLarge", img_large);
                        dbWrite.insert("nameInfo", null, values);

                        cursorTemp = dbRead.query("nameInfo", COLUMN_NAME, "name=?", new String[]{"" + phoneName}, null, null, null, null);

                        cursorTemp.moveToFirst();
                        values = new ContentValues();
                        values.put("nameId", cursorTemp.getInt(0));
                        values.put("phoneNumber", phoneNumber);
                        values.put("phoneType", phoneType);
                        dbWrite.insert("phoneInfo", null, values);
                    } else {
                        //有重名，检测电话
                        cursorTemp = dbRead.query("phoneInfo", COLUMN_PHONE, "phoneNumber=?", new String[]{"" + phoneNumber}, null, null, null, null);
                        if (cursorTemp.getCount() == 0) {
                            //重名，新号码
                            cursorTemp = dbRead.query("nameInfo", COLUMN_NAME, "name=?", new String[]{"" + phoneName}, null, null, null, null);

                            cursorTemp.moveToFirst();
                            values = new ContentValues();
                            values.put("nameId", cursorTemp.getInt(0));
                            values.put("phoneNumber", phoneNumber);
                            values.put("phoneType", phoneType);
                            dbWrite.insert("phoneInfo", null, values);
                        }
                    }
                    cursorTemp.close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    dbWrite.close();
                    dbRead.close();
                }
            }
            cursor.close();
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addContact();
                    startActivity(new Intent(getApplicationContext(), ContactListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }
    }


}
