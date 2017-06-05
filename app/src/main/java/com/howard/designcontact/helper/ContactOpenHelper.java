package com.howard.designcontact.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zhaohaoran on 2017/4/14.
 */

public class ContactOpenHelper extends SQLiteOpenHelper {
    public ContactOpenHelper(Context context) {
        super(context, "contact.dbWrite", null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table nameInfo(" +
                "_id integer primary key," +
                "name text UNIQUE," +
                "photoSmall BLOB," +
                "photoLarge BLOB," +
                "isStarred integer DEFAULT 0)");

        db.execSQL("create table phoneInfo(" +
                "id integer primary key," +
                "nameId integer," +
                "phoneNumber text," +
                "phoneType integer," +
                "FOREIGN KEY(nameId) REFERENCES nameInfo(_id))");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop if table exists nameInfo");
        db.execSQL("drop if table exists  PhoneInfo");
        onCreate(db);
    }

    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
