package com.howard.designcontact;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhaohaoran on 2017/4/13.
 */

public class mContact implements Parcelable {
    public static final Creator CREATOR = new Creator() {
        public mContact createFromParcel(Parcel source) {
            // 必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
            mContact p = new mContact();
            p.setId(source.readInt());
            p.setName(source.readString());
            p.photoCore = new byte[source.readInt()];
            source.readByteArray(p.photoCore);

            return p;
        }

        public mContact[] newArray(int size) {
            // TODO Auto-generated method stub
            return new mContact[size];
        }
    };

    public int id;
    public String name;
    public Bitmap photoSmall;
    public Bitmap photoLarge;
    public byte[] photoCore;
    public byte[] photoDisplay;

    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        // 1.必须按成员变量声明的顺序封装数据，不然会出现获取数据出错
        // 2.序列化对象
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(photoCore.length);
        dest.writeByteArray(photoCore);
    }


    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhotoCore(byte[] photoCore) {
        this.photoCore = photoCore;
    }

    public void setPhotoDisplay(byte[] photoDisplay) {
        this.photoDisplay = photoDisplay;
    }

    public Bitmap getPhotoSmall() {
        return BitmapFactory.decodeByteArray(photoCore, 0, photoCore.length);
    }

    public Bitmap getPhotoLarge() {
        return BitmapFactory.decodeByteArray(photoDisplay, 0, photoDisplay.length);
    }

}
