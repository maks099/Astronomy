package com.maks_rusyn.astronomy.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.view.Display;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * утиліта з різними додадтковими функціями
 */
public class Util {

    /**
     * отримання унікального ідентифікатору пристрою
     * @param context контекст
     * @return
     */
    public static String getDeviceId(Context context){
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * отримання дати та часу
     * @return
     */
    public static String getDateTime(){
        return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date());
    }

    /**
     * перевірка зв'язку з мережею
     * @param context контекст
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    /**
     * отримання розмірів екрану
     * @param activity
     * @return
     */
    public static Point getScreenSize(Activity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    /**
     * хеш [назва планети, назва фото]
     * @return
     */
    public static HashMap<String, String> getPictureHashes() {
        HashMap<String, String> planetsWithImportedPicture = new HashMap<>();
        planetsWithImportedPicture.put("Меркурій", "mercury");
        planetsWithImportedPicture.put("Венера", "venus");
        planetsWithImportedPicture.put("Земля", "earth");
        planetsWithImportedPicture.put("Марс", "mars");
        planetsWithImportedPicture.put("Юпітер", "jupiter");
        planetsWithImportedPicture.put("Сатурн", "saturn");
        planetsWithImportedPicture.put("Уран", "uran");
        planetsWithImportedPicture.put("Нептун", "neptune");
        return planetsWithImportedPicture;
    }
}
