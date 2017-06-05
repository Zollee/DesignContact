package com.howard.designcontact.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.howard.designcontact.helper.AsynNetUtils;
import com.howard.designcontact.R;
import com.howard.designcontact.adapter.ContactEditAdapter;
import com.howard.designcontact.helper.ContactOpenHelper;
import com.howard.designcontact.helper.MyDividerItemDecoration;
import com.howard.designcontact.mPhone;
import com.howard.designcontact.proto.Data;
import com.howard.designcontact.proto.Person;
import com.howard.designcontact.proto.Phone;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ContactInsertActivity extends AppCompatActivity {

    public final static int ALBUM_REQUEST_CODE = 1;
    public final static int CROP_REQUEST = 2;
    public final static int CAMERA_REQUEST_CODE = 3;
    ArrayList<mPhone> mPhones;
    ContactOpenHelper contactOpenHelper;
    SQLiteDatabase dbRead;
    SQLiteDatabase dbWrite;
    Button mButton_edit_add;
    ImageView mImageView_photo;
    EditText mEditText_name;
    ImageButton mImageButton_select_photo;
    private SharedPreferences preferences;

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
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        contactOpenHelper = new ContactOpenHelper(getApplicationContext());
        dbRead = contactOpenHelper.getReadableDatabase();
        dbWrite = contactOpenHelper.getWritableDatabase();

        mImageView_photo = (ImageView) findViewById(R.id.imageView_photo);
        mEditText_name = (EditText) findViewById(R.id.edit_name_text);
        mButton_edit_add = (Button) findViewById(R.id.button_edit_add);
        mImageButton_select_photo = (ImageButton) findViewById(R.id.imageButton_select_photo);

        mPhones = new ArrayList<mPhone>();
        mPhones.add(new mPhone());

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
        mImageButton_select_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ContactInsertActivity.this)
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
        ContentValues values;

        Bitmap contactPhoto;
        ByteArrayOutputStream baos;
        byte[] img_small = null;
        byte[] img_large = null;

        switch (item.getItemId()) {
            case R.id.menu_check:
                String name = mEditText_name.getText().toString();
                //检测姓名为空
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
                if (cursorTemp.getCount() == 0) {
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
                    dbWrite.insert("nameInfo", null, values);
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

                cursorTemp = dbRead.query("nameInfo", new String[]{"_id"}, "name=?", new String[]{"" + name}, null, null, null);
                cursorTemp.moveToFirst();
                int nameId = cursorTemp.getInt(0);
                //插入数据
                for (int i = 0; i < mPhones.size(); i++) {
                    values = new ContentValues();
                    values.put("nameId", nameId);
                    values.put("phoneNumber", mPhones.get(i).getPhone());
                    values.put("phoneType", mPhones.get(i).getType());
                    dbWrite.insert("phoneInfo", null, values);
                }

                Person temp = new Person.Builder()
                        .id(nameId)
                        .name(name)
                        .isStarred(0)
                        .build();

                List<Person> personList = new ArrayList<>();
                personList.add(temp);

                cursorTemp = dbRead.query("phoneInfo", new String[]{"id", "phoneNumber", "phoneType"}, "nameId=?", new String[]{"" + nameId}, null, null, null);
                List<Phone> phoneList = new ArrayList<>();
                while (cursorTemp.moveToNext()) {
                    Phone temp2 = new Phone.Builder()
                            .id(cursorTemp.getInt(0))
                            .nameId(nameId)
                            .number(cursorTemp.getString(1))
                            .type(cursorTemp.getInt(2))
                            .build();
                    phoneList.add(temp2);
                }
                cursorTemp.close();

                preferences = getSharedPreferences("phone", Context.MODE_PRIVATE);
                Data data = new Data.Builder()
                        .user(preferences.getString("username", null))
                        .persons(personList)
                        .phoned(phoneList)
                        .build();

                byte[] dataBytes = Data.ADAPTER.encode(data);

                String dataString = new String(dataBytes).replace("%", "%25");

                AsynNetUtils.post("http://47.94.97.91/demo/updateDatabase", "key=" + dataString, new AsynNetUtils.Callback() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("注册成功")) {
                            dbRead.close();
                            dbWrite.close();
                            startActivity(new Intent(getApplicationContext(), ContactListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        } else
                            Log.d("response", response);
                    }
                });

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
