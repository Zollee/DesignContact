package com.howard.designcontact.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.howard.designcontact.Debug;
import com.howard.designcontact.helper.AsynNetUtils;
import com.howard.designcontact.helper.NetUtils;
import com.howard.designcontact.R;
import com.howard.designcontact.helper.ContactOpenHelper;
import com.howard.designcontact.mTemp;
import com.howard.designcontact.proto.Data;
import com.howard.designcontact.proto.Person;
import com.howard.designcontact.proto.Phone;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class SignupActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */

    private static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.TYPE,
            ContactsContract.CommonDataKinds.Photo.PHOTO_ID,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
    };
    ContactOpenHelper contactOpenHelper;
    SQLiteDatabase dbWrite;
    SQLiteDatabase dbRead;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        contactOpenHelper = new ContactOpenHelper(getApplicationContext());

        setContentView(R.layout.activity_signup);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            AsynNetUtils.get("http://47.94.97.91/demo/signup?username=" + email + "&password=" + password, new AsynNetUtils.Callback() {
                @Override
                public void onResponse(String response) {
                    Log.d("info", response);

                    if (response.equals("注册成功")) {
                        preferences = getSharedPreferences("phone", Context.MODE_PRIVATE);

                        editor = preferences.edit();
                        editor.putBoolean("login", true);
                        editor.putString("username", email);
                        editor.putString("password", password);
                        editor.apply();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                addContact();
                                updateToCloud();
                            }
                        }).start();
                    } else
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(SignupActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    //添加联系人到本地
    public void addContact() {
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
        String phoneName;
        String phoneNumber;
        int typeTemp;
        int phoneType;

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
                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                        phoneType = 0;
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                        phoneType = 1;
                        break;

                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                        phoneType = 2;
                        break;
                    default:
                        phoneType = 3;
                }

                //获得头像
                if (photoId > 0) {
                    Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
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
                    contactPhoto = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_person_white_48dp);
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

                    dbWrite.delete("phoneInfo", "nameId>20", null);
                    dbWrite.delete("nameInfo", "_id>20", null);

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

    //上传联系人数据
    public void updateToCloud() {
        dbRead = contactOpenHelper.getReadableDatabase();

        String[] COLUMN_NAME = new String[]{"_id", "name", "photoSmall", "photoLarge", "isStarred"};
        String[] COLUMN_PHONE = new String[]{"id", "nameId", "phoneNumber", "phoneType"};

        Cursor cursor = dbRead.query("nameInfo", COLUMN_NAME, null, null, null, null, null, null);

        List<Person> personList = new ArrayList<>();
        List<mTemp> tempList = new ArrayList<>();
        if (cursor != null) {
            preferences = getSharedPreferences("phone", Context.MODE_PRIVATE);

            while (cursor.moveToNext()) {
                Person temp = new Person.Builder()
                        .id(cursor.getInt(0))
                        .name(cursor.getString(1))
                        .isStarred(cursor.getInt(4))
                        .build();
                mTemp temp1 = new mTemp();
                temp1.setId(cursor.getInt(0));
                temp1.setPhotoSmall(Base64.encodeToString(cursor.getBlob(2),Base64.URL_SAFE | Base64.NO_WRAP).replace("%", "%25").replace("&","%26"));
                temp1.setPhotoLarge(Base64.encodeToString(cursor.getBlob(3),Base64.URL_SAFE | Base64.NO_WRAP).replace("%", "%25").replace("&","%26"));

                personList.add(temp);
                tempList.add(temp1);
            }
        }

        cursor = dbRead.query("phoneInfo", COLUMN_PHONE, null, null, null, null, null, null);
        List<Phone> phoneList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Phone temp = new Phone.Builder()
                        .id(cursor.getInt(0))
                        .nameId(cursor.getInt(1))
                        .number(cursor.getString(2))
                        .type(cursor.getInt(3))
                        .build();
                phoneList.add(temp);
            }
        }

        cursor.close();

        Data data = new Data.Builder()
                .user(preferences.getString("username", ""))
                .persons(personList)
                .phoned(phoneList)
                .build();
        Log.d("data", data.user);

        byte[] dataBytes = Data.ADAPTER.encode(data);

        String dataString = new String(dataBytes).replace("%", "%25");
        Log.d("DataString", dataString);

        String response = NetUtils.post("http://47.94.97.91/demo/updateDatabase", "key=" + dataString);
        if (response.equals("注册成功")) {
            for (mTemp temp:tempList) {
                NetUtils.post("http://47.94.97.91/demo/updatePhoto", "user=" + preferences.getString("username", "")+"&id="+temp.getId()+"&small="+ temp.getPhotoSmall()+"&large=" + temp.getPhotoLarge());
            }
            startActivity(new Intent(getApplicationContext(), ContactListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        } else
            Log.d("response", response);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
    }
}

