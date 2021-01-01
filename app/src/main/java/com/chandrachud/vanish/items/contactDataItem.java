package com.chandrachud.vanish.items;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class contactDataItem {

    private String name;
    private String number;
    private String picture;

    public contactDataItem(String name, String number, String picture) {
        this.name = name;
        this.number = number;
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
