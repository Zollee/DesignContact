package com.howard.designcontact;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhaohaoran on 2017/4/24.
 */

public class mPhone implements Parcelable {
    public static final Creator CREATOR = new Creator() {
        public mPhone createFromParcel(Parcel source) {
            // 必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
            mPhone p = new mPhone();
            p.setPhone(source.readString());
            p.setType(source.readString());

            return p;
        }

        public mPhone[] newArray(int size) {
            // TODO Auto-generated method stub
            return new mPhone[size];
        }
    };
    private String phone;
    private String type;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        // 1.必须按成员变量声明的顺序封装数据，不然会出现获取数据出错
        // 2.序列化对象
        dest.writeString(phone);
        dest.writeString(type);

    }


    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }
}
