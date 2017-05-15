package com.howard.designcontact.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.howard.designcontact.R;
import com.howard.designcontact.adapter.ContactEditAdapter;
import com.howard.designcontact.helper.ContactOpenHelper;
import com.howard.designcontact.helper.MyDividerItemDecoration;
import com.howard.designcontact.mContact;
import com.howard.designcontact.mPhone;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class ContactEditActivity extends AppCompatActivity {

    public final static int ALBUM_REQUEST_CODE = 1;
    public final static int CROP_REQUEST = 2;
    public final static int CAMERA_REQUEST_CODE = 3;
    mContact contact;
    ArrayList<mPhone> mPhones;
    Button mButton_edit_add;
    ImageButton mImageButton_select_photo;
    ContactOpenHelper contactOpenHelper;
    SQLiteDatabase dbWrite;
    SQLiteDatabase dbRead;
    ImageView mImageView_photo;
    EditText mEditText_name;
    private Uri imageUri;
    private RecyclerView mRecyclerView;
    private ContactEditAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

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

        contactOpenHelper = new ContactOpenHelper(getApplicationContext());
        dbWrite = contactOpenHelper.getWritableDatabase();
        dbRead = contactOpenHelper.getReadableDatabase();

        contact = getIntent().getParcelableExtra("mContact");
        mPhones = getIntent().getParcelableArrayListExtra("mPhone");

        String[] COLUMN_NAME = new String[]{"_id", "name", "photoLarge", "isStarred"};

        Cursor cursor = dbRead.query("nameInfo", COLUMN_NAME, "_id=?", new String[]{"" + contact.getId()}, null, null, null, null);
        cursor.moveToFirst();

        contact.setName(cursor.getString(1));
        contact.setPhotoDisplay(cursor.getBlob(2));

        mImageView_photo = (ImageView) findViewById(R.id.imageView_photo);
        mEditText_name = (EditText) findViewById(R.id.edit_name_text);
        mButton_edit_add = (Button) findViewById(R.id.button_edit_add);
        mImageButton_select_photo = (ImageButton) findViewById(R.id.imageButton_select_photo);

        initData();
        initView();

    }

    private void initData() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new ContactEditAdapter(mPhones);
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_contact_edit);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        mImageView_photo.setImageBitmap(contact.getPhotoLarge());
        mEditText_name.setText(contact.getName());

        mImageButton_select_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ContactEditActivity.this)
                        .setItems(new String[]{"拍照", "从相册选取照片", "清除照片"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File file = new File(Environment.getExternalStorageDirectory(), "./img_test.jpg");
                                imageUri = FileProvider.getUriForFile(getApplicationContext(), "com.howard.designcontact.provider", file);//通过FileProvider创建一个content类型的Uri
                                switch (which) {
                                    case 0:
                                        Intent intent = new Intent();
                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
                                        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
                                        startActivityForResult(intent, CAMERA_REQUEST_CODE);
                                        break;
                                    case 1:
                                        Intent selectIntent = new Intent(Intent.ACTION_PICK);
                                        selectIntent.setType("image/*");//从所有图片中进行选择
                                        startActivityForResult(selectIntent, ALBUM_REQUEST_CODE);
                                        break;
                                    case 2:
                                        mImageView_photo.setImageResource(R.mipmap.ic_person_white_48dp);
                                        break;
                                }
                            }
                        })
                        .show();
            }
        });
        mButton_edit_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = mAdapter.getItemCount();

                mPhones.clear();

                for (int position = 0; position < number; position++) {
                    View view = mRecyclerView.getChildAt(position);
                    if (null != mRecyclerView.getChildViewHolder(view)) {
                        ContactEditAdapter.ViewHolder holder = (ContactEditAdapter.ViewHolder) mRecyclerView.getChildViewHolder(view);
                        if (!holder.edit_number_text.getText().toString().equals("")) {
                            //记录更改
                            mPhone temp = new mPhone();
                            temp.setPhone(holder.edit_number_text.getText().toString());
                            temp.setType(holder.spinner.getSelectedItemPosition());
                            mPhones.add(temp);
                        }
                    }
                }

                //判断最后一个view是否为空
                View view = mRecyclerView.getChildAt(number - 1);
                if (null != mRecyclerView.getChildViewHolder(view)) {
                    ContactEditAdapter.ViewHolder holder = (ContactEditAdapter.ViewHolder) mRecyclerView.getChildViewHolder(view);
                    if (!holder.edit_number_text.getText().toString().equals("")) {
                        mPhones.add(new mPhone());
                        mAdapter.updateData(mPhones);
                    }
                }
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact_edit, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Bitmap contactPhoto;
        ByteArrayOutputStream baos;
        byte[] img_small = null;
        byte[] img_large = null;

        ContentValues values;
        switch (item.getItemId()) {
            case R.id.menu_check:
                String name = mEditText_name.getText().toString();
                //检测姓名
                if (name.equals("")) {
                    new AlertDialog.Builder(this)
                            .setMessage("姓名不能为空")
                            .setPositiveButton("返回", null)
                            .show();
                    return true;
                }

                //检测重名
                Cursor cursorTemp;
                cursorTemp = dbRead.query("nameInfo", new String[]{"_id"}, "name=?", new String[]{"" + name}, null, null, null);

                //插入姓名
                if (cursorTemp.getCount() == 1) {
                    contactPhoto = ((BitmapDrawable) mImageView_photo.getDrawable()).getBitmap();
                    baos = new ByteArrayOutputStream();
                    contactPhoto.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    img_large = baos.toByteArray();
                    contactPhoto.compress(Bitmap.CompressFormat.PNG, 50, baos);
                    img_small = baos.toByteArray();

                    values = new ContentValues();
                    values.put("name", name);
                    values.put("photoSmall", img_small);
                    values.put("photoLarge", img_large);
                    dbWrite.update("nameInfo", values, "_id=?", new String[]{"" + contact.getId()});
                } else {
                    new AlertDialog.Builder(this)
                            .setMessage("姓名与已有重复")
                            .setPositiveButton("返回", null)
                            .show();
                    return true;
                }

                int number = mAdapter.getItemCount();
                mPhones.clear();

                for (int position = 0; position < number; position++) {
                    View view = mRecyclerView.getChildAt(position);
                    if (null != mRecyclerView.getChildViewHolder(view)) {
                        ContactEditAdapter.ViewHolder holder = (ContactEditAdapter.ViewHolder) mRecyclerView.getChildViewHolder(view);
                        if (!holder.edit_number_text.getText().toString().equals("")) {
                            //记录更改
                            mPhone temp = new mPhone();
                            temp.setPhone(holder.edit_number_text.getText().toString());
                            temp.setType(holder.spinner.getSelectedItemPosition());
                            mPhones.add(temp);
                        }
                    }
                }

                //检测电话
                if (mPhones.isEmpty()) {
                    new AlertDialog.Builder(this)
                            .setMessage("电话号码不能为空")
                            .setPositiveButton("返回", null)
                            .show();
                    mPhones.add(new mPhone());
                    return true;
                }

                dbWrite.delete("phoneInfo", "nameId=" + contact.getId(), null);

                for (int i = 0; i < mPhones.size(); i++) {
                    values = new ContentValues();
                    values.put("nameId", contact.getId());
                    values.put("phoneNumber", mPhones.get(i).getPhone());
                    values.put("phoneType", mPhones.get(i).getType());
                    dbWrite.insert("phoneInfo", null, values);
                }
                dbWrite.close();

                startActivity(new Intent(getApplicationContext(), ContactListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("确认返回？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("否", null)
                .show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (data != null)
                startImageZoom(imageUri);
        } else if (requestCode == ALBUM_REQUEST_CODE) {
            if (data != null)
                startImageZoom(data.getData());
        } else if (requestCode == CROP_REQUEST) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                mImageView_photo.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void startImageZoom(Uri uri) {
        File file = new File(Environment.getExternalStorageDirectory(), "./img_test.jpg");
        imageUri = FileProvider.getUriForFile(getApplicationContext(), "com.howard.designcontact.provider", file);//通过FileProvider创建一个content类型的Uri

        Intent intent = new Intent("com.android.camera.action.CROP");//调用Android系统自带的一个图片剪裁页面
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("outputX", 400);
        intent.putExtra("outputY", 400);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, CROP_REQUEST);
    }

}
