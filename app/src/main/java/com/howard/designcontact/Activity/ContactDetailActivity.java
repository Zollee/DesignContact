package com.howard.designcontact.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Window;
import android.widget.ImageView;

import com.howard.designcontact.R;
import com.howard.designcontact.mContact;

public class ContactDetailActivity extends AppCompatActivity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition explode = TransitionInflater.from(getApplicationContext()).inflateTransition(R.transition.slide);
        getWindow().setEnterTransition(explode);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContact contact = (mContact) getIntent().getParcelableExtra("mContact");

        imageView = (ImageView) findViewById(R.id.pic_detail);
        imageView.setImageBitmap(contact.getPhoto());
    }
}
