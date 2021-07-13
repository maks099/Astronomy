package com.maks_rusyn.astronomy.objects;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class Section {

    private Bitmap picture;
    private String name, themeRoot, text;

    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
    public String getThemeRoot() {
        return themeRoot;
    }

    public void setThemeRoot(String themeRoot) {
        this.themeRoot = themeRoot;
    }

    
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    
    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }


    public Section(String name, String themeRoot, String text) {
        this.name = name;
        this.themeRoot = themeRoot;
        this.text = text;
    }

    public Section() {

    }
}
