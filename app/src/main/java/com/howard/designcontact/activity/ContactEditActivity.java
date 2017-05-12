package com.howard.designcontact.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.howard.designcontact.R;
import com.howard.designcontact.mContact;
import com.howard.designcontact.mPhone;

import java.util.ArrayList;

public class ContactEditActivity extends AppCompatActivity {
    mContact contact;
    ArrayList<mPhone> mPhones;

    ImageView mImageView_photo;
    EditText mEditText_name;
    EditText mEditText_phone;
    Spinner mSpinner;

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

        contact = getIntent().getParcelableExtra("mContact");
        mPhones = getIntent().getParcelableArrayListExtra("mPhone");

        mImageView_photo = (ImageView) findViewById(R.id.imageView_photo);
        mEditText_name = (EditText) findViewById(R.id.edit_name_text);
        mEditText_phone = (EditText) findViewById(R.id.edit_number_text);
        mSpinner = (Spinner) findViewById(R.id.spinner);

        initView();

    }

    private void initView() {
        mImageView_photo.setImageBitmap(contact.getPhotoLarge());
        mEditText_name.setText(contact.getName());
        mEditText_phone.setText(mPhones.get(0).getPhone());

        switch (mPhones.get(0).getType()) {
            case "手机":
                mSpinner.setSelection(0);
                break;
            case "家庭":
                mSpinner.setSelection(1);
                break;
            case "工作":
                mSpinner.setSelection(2);
                break;
            case "其他":
                mSpinner.setSelection(3);
                break;
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_edit, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_check:
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

}
