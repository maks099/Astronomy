package com.maks_rusyn.astronomy.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.maks_rusyn.astronomy.R;
import com.maks_rusyn.astronomy.objects.Planet;

import java.util.ArrayList;

/**
 * утиліта створення планет для анімації
 */
public class PlanetsUtil {

    private final Context context;
    private static int width, height, halfW, halfH;
    private final int earthSide;
    private final double planetSideCoef;
    private double widthWindowCoef, heightWindowCoef;

    public PlanetsUtil(Context ctx, int width, int height){
        this.context = ctx;
        PlanetsUtil.width = width;
        PlanetsUtil.height = height;
        planetSideCoef = (double) height / width;
        earthSide = width / 10;
        halfW = width / 2;
        halfH = height / 2;
        widthWindowCoef = 720.0 / width;
        heightWindowCoef = 1337.0 / height;
    }

    /**
     * отримання списку об'єктів планет
     * @return
     */
    public ArrayList<Planet> getAllSolarSystemPlanets(){
        ArrayList<Planet> planets = new ArrayList<>();
        planets.add(getMercury());
        planets.add(getVenus());
        planets.add(getEarth());
        planets.add(getMars());
        planets.add(getJupiter());
        planets.add(getSaturn());
        planets.add(getUranus());
        planets.add(getNeptune());
        return planets;
    }

    public Planet getMercury(){
        return new Planet("Меркурій",
                0,
                0,
                getCoefficientForFarPlanet(false, 0.95)* planetSideCoef,
                getCoefficientForFarPlanet(true, 0.95),
                0.015,
                getBitmapForPlanet(R.drawable.mercury, 0.38));
    }


    public Planet getVenus(){
        return new Planet("Венера",
                0,
                0,
                getCoefficientForFarPlanet(false, 1.02)* planetSideCoef,
                getCoefficientForFarPlanet(true, 1.02),
                0.007,
                getBitmapForPlanet(R.drawable.venus, 0.9));
    }

    public Planet getEarth(){
        return new Planet("Земля",
                0,
                0,
                getCoefficientForFarPlanet(false, 1.1)* planetSideCoef,
                getCoefficientForFarPlanet(true, 1.1),
                0.005,
                getBitmapForPlanet(R.drawable.earth, 1));
    }

    public Planet getMars(){
        return new Planet("Марс",
                0,
                0,
                getCoefficientForFarPlanet(false, 1.2)* planetSideCoef,
                getCoefficientForFarPlanet(true, 1.2),
                0.0025,
                getBitmapForPlanet(R.drawable.mars, 0.53));
    }

    public Planet getJupiter(){
        return new Planet("Юпітер",
                0,
                0,
                getCoefficientForFarPlanet(false, 1.6)* planetSideCoef,
                getCoefficientForFarPlanet(true, 1.3),
                0.001,
                getBitmapForPlanet(R.drawable.jupiter, 5.5));
    }

    public Planet getSaturn(){
        return new Planet("Сатурн",
                0,
                0,
                getCoefficientForFarPlanet(false, 1.7)* planetSideCoef,
                getCoefficientForFarPlanet(true, 1.5),
                0.0005,
                getBitmapForPlanet(R.drawable.saturn, 4.5));
    }

    public Planet getUranus(){
        return new Planet("Уран",
                halfW,
                halfH,
                getCoefficientForFarPlanet(false, 1.8)* planetSideCoef,
                getCoefficientForFarPlanet(true, 1.75),
                0.0003,
                getBitmapForPlanet(R.drawable.uran, 3));
    }

    public Planet getNeptune(){
        return new Planet("Нептун",
                halfW,
                halfH,
                getCoefficientForFarPlanet(false, 1.9)* planetSideCoef,
                getCoefficientForFarPlanet(true, 1.9),
                0.00009,
                getBitmapForPlanet(R.drawable.neptune, 2.66));
    }

    /**
     * отримання коефіціентів руху
     * @param vectorY вісь
     * @param mul коефіціент окремої планети
     * @return
     */
    private double getCoefficientForFarPlanet(boolean vectorY, double mul){
        int magicX = 720;
        double magicXC = 1.25;
        int magicY = 1337;
        double magicYC = 2.4;

        if(vectorY){
            return (height * magicYC / magicY) * mul * heightWindowCoef;
        }
        else{
            return (width * magicXC / magicX) * mul * widthWindowCoef;
        }
    }

    /**
     * отримання фото планети
     * @param code код (з ресурсів)
     * @param sizeScaleParameter параметр розміру
     * @return
     */
    private Bitmap getBitmapForPlanet(int code, double sizeScaleParameter){
        Bitmap planetImage = BitmapFactory.decodeResource(context.getResources(), code);
        int sideLength = (int) (earthSide * sizeScaleParameter);//0.38
        return ImageUtil.getResizedBitmap(planetImage, sideLength, sideLength);
    }
}
