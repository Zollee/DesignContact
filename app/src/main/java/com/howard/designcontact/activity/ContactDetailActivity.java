package com.howard.designcontact.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.howard.designcontact.R;
import com.howard.designcontact.helper.ContactOpenHelper;
import com.howard.designcontact.mContact;

public class ContactDetailActivity extends AppCompatActivity {
    ContactOpenHelper contactOpenHelper;
    SQLiteDatabase dbRead;
    TextView phoneNumber;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition explode = TransitionInflater.from(getApplicationContext()).inflateTransition(R.transition.slide);
        getWindow().setEnterTransition(explode);

        super.onCreate(savedInstanceState);
        contactOpenHelper = new ContactOpenHelper(getApplicationContext());
        dbRead = contactOpenHelper.getReadableDatabase();

        String[] COLUMN_NAME = new String[]{"phoneNumber"};

        mContact contact = getIntent().getParcelableExtra("mContact");

        setContentView(R.layout.activity_contact_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(contact.getName());
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        imageView = (ImageView) findViewById(R.id.pic_detail);
        imageView.setImageBitmap(contact.getPhotoLarge());

        Cursor cursor = dbRead.query("phoneInfo", COLUMN_NAME, "nameId=?", new String[]{"" + contact.getId()}, null, null, null, null);

        cursor.moveToFirst();
        contact.setNumber(cursor.getString(0));

        phoneNumber = (TextView) findViewById(R.id.textView2);
        phoneNumber.setText(contact.getNumber());
    }
}
