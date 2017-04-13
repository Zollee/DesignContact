package com.howard.designcontact;

import android.graphics.Bitmap;

/**
 * Created by zhaohaoran on 2017/4/13.
 */

public class mContact {
    public String name;
    public String number;
    public String type;
    public Bitmap photo;

    public String getName(){
        return name;
    }

    public String getNumber(){
        return number;
    }

    public String getType(){
        return type;
    }
    public Bitmap getPhoto(){
        return photo;
    }
}
