package com.maks_rusyn.astronomy.objects;

import android.graphics.Bitmap;

import java.util.ArrayList;


public class Planet {

    private String name;
    private double coefX;
    private double coefY;
    private double speed;
    private double angle;
    private int x;
    private int y;
    private Bitmap bitmap;


    public Planet(String name, int x, int y, double coefX, double coefY, double angle, Bitmap bitmap) {
        this.bitmap = bitmap;
        this.coefX = coefX;
        this.coefY = coefY;
        this.speed = angle;
        this.name = name;
        this.x = x - bitmap.getWidth()/2;
        this.y = y - bitmap.getHeight()/2;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public double getCoefX() {
        return coefX;
    }


    public double getCoefY() {
        return coefY;
    }

 
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }


    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    
    public Bitmap getBitmap() {
        return bitmap;
    }



    
    public double getSpeed() {
        return speed;
    }

    
    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
}