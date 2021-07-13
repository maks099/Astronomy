package com.maks_rusyn.astronomy.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.StandardGifDecoder;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.maks_rusyn.astronomy.BuildConfig;
import com.maks_rusyn.astronomy.R;
import com.waynejo.androidndkgif.GifDecoder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * утиліта роботи з фотками
 */
public class ImageUtil {

    /**
     * зміна розміру фото
     * @param bm картинка
     * @param newWidth нова ширина
     * @param newHeight нова висота
     * @return фото з новими розмірами
     */
    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        bm = null;
        return resizedBitmap;
    }

    /**
     * отримання фото по id
     * @param id
     * @param ctx контекст
     * @return фото
     */
    public static Drawable getDrawable(int id, Context ctx) {
        return ctx.getDrawable(id);
    }
}
