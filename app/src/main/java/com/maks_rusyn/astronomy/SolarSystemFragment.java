package com.maks_rusyn.astronomy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.maks_rusyn.astronomy.objects.Planet;
import com.maks_rusyn.astronomy.objects.Section;
import com.maks_rusyn.astronomy.utils.FragmentUtil;
import com.maks_rusyn.astronomy.utils.ImageUtil;
import com.maks_rusyn.astronomy.utils.PlanetsUtil;
import com.maks_rusyn.astronomy.utils.Util;

import java.util.ArrayList;


public class SolarSystemFragment extends Fragment implements View.OnTouchListener,
        SurfaceHolder.Callback {

    private SurfaceView mSurface;
    private DrawingThread mThread;
    public double speedAcceleration = 1, prevClickYCoordinate = 0;
    private int screenW = 0;
    private int screenH = 0;
    public Bitmap background, sun;
    private boolean showPlanetName;

    public SolarSystemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mSurface = (SurfaceView) getView().findViewById(R.id.surfaceView);
        SharedPreferences settingsPref = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        showPlanetName = settingsPref.getBoolean("show_planet_name", true);
        mSurface.setOnTouchListener(this);
        mSurface.getHolder().addCallback(this);
        pickBackgroundImage();
        super.onViewCreated(view, savedInstanceState);
    }

    
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if(event.getY() > prevClickYCoordinate + 5 && speedAcceleration < 5){
                speedAcceleration += 0.1;
            } else if(event.getY() < prevClickYCoordinate - 5 && speedAcceleration >= 0.1) {
                speedAcceleration -= 0.1;
            }
            prevClickYCoordinate = (int) event.getY();
        } else if(event.getAction() == MotionEvent.ACTION_DOWN){
            mThread.clickOnItem((int)event.getX(), (int)event.getY());
        }
        return true;
    }

    
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        mThread = new DrawingThread(holder, mSurface.getWidth(), mSurface.getHeight(), this, showPlanetName);
        ArrayList<Planet> planets = new PlanetsUtil(this.getContext(), screenW, screenH).getAllSolarSystemPlanets();
        for(Planet planet : planets){
            mThread.addItem(planet);
        }
        mThread.start();
    }


    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) { }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        mThread.quit();
        mThread = null;
    }

    
    private void pickBackgroundImage(){
        background = BitmapFactory.decodeResource(getResources(), R.drawable.vertical_space);
        Point size = Util.getScreenSize(getActivity());
        screenW = size.x;
        screenH = size.y;
        background = ImageUtil.getResizedBitmap(background, screenW, screenH);
        prevClickYCoordinate = screenH / 2;
        sun = BitmapFactory.decodeResource(getResources(), R.drawable.sun5);
        double g = (screenW + 0.0) / sun.getWidth();

        int hh = (int)(sun.getHeight() * g);
        sun = ImageUtil.getResizedBitmap(sun, screenW, hh);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_solar_system, container, false);
    }


    
    private static class DrawingThread extends HandlerThread implements Handler.Callback {
        private static final int MSG_MOVE = 101;
        private final int mDrawingWidth;
        private final int mDrawingHeight;
        private final SurfaceHolder mDrawingSurface;
        private  Paint mPaint, fontPaint, planetPaint;
        private Handler mReceiver;
        private final ArrayList<Planet> planets;
        private final SolarSystemFragment theFragment;
        private final boolean showPlanetNames;

        public DrawingThread(SurfaceHolder holder, int width, int height, SolarSystemFragment activity, boolean showPlanetNames) {
            super("DrawingThread");
            mDrawingSurface = holder;
            planets = new ArrayList<>();
            mDrawingWidth = width;
            mDrawingHeight = height;
            theFragment = activity;
            this.showPlanetNames = showPlanetNames;

        }

        @Override
        protected void onLooperPrepared() {
            mReceiver = new Handler(getLooper(), this);
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            planetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mReceiver.sendEmptyMessage(MSG_MOVE);
            fontPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            fontPaint.setTextSize(50);
            fontPaint.setStyle(Paint.Style.FILL);
            fontPaint.setColor(Color.parseColor("#ECECEC"));
        }

        @Override
        public boolean quit() {
            if(mReceiver!=null) {
                mReceiver.removeCallbacksAndMessages(null);
            }
            return super.quit();
        }

        
        @Override
        public boolean handleMessage(Message msg) {
            Canvas c = mDrawingSurface.lockCanvas();
            if (c == null) {
                return true;
            }
            c.drawBitmap(theFragment.background, 0, 0, mPaint);
            int halfWidth = mDrawingWidth / 2, halfHeight = mDrawingHeight / 2;
            Bitmap b = theFragment.sun;
            c.drawBitmap(b, 0, 0, mPaint);
            for (Planet item : planets) {
                double angle = item.getAngle();
                double r = 0.75 * Math.min(halfWidth, halfHeight);
                int x = (int) (r * Math.cos(angle) * item.getCoefX()) - item.getBitmap().getWidth() / 2;
                int y = (int) (r * Math.sin(angle) * item.getCoefY());

                item.setX(x);
                item.setY(y);

                angle += item.getSpeed() * theFragment.speedAcceleration;
                item.setAngle(angle);
                c.drawBitmap(item.getBitmap(), item.getX(), item.getY(), planetPaint);

                if (showPlanetNames) {
                    String text = item.getName();
                    float[] textWidth = new float[text.length()];
                    fontPaint.getTextWidths(text, textWidth);
                    c.drawText(item.getName(), item.getX() + item.getBitmap().getWidth() / 2, item.getY(), fontPaint);
                }
            }
            mDrawingSurface.unlockCanvasAndPost(c);
            mReceiver.sendEmptyMessage(0);
            return true;
        }

        
        public void addItem(Planet planet) {
            planets.add(planet);
        }

        
        public void clickOnItem(int x, int y) {
            if(x < theFragment.sun.getWidth() * 0.7 && y < theFragment.sun.getHeight() * 0.7){
               checkIfSectionWasDownload("Сонце");
            }
            for (Planet planet : planets) {
                int pX = planet.getX();
                if (x > pX && x < pX + planet.getBitmap().getWidth()) {
                    int pY = planet.getY();
                    if (y > pY && y < pY + planet.getBitmap().getHeight()) {
                          checkIfSectionWasDownload(planet.getName());
                    }
                }
            }
        }

        
        private void checkIfSectionWasDownload(String planetName) {
            MainActivity mainActivity = (MainActivity) theFragment.getActivity();
            ArrayList<Section> downloadedSections = mainActivity.getSectionList("Сонячна система");
            if (downloadedSections != null && !downloadedSections.isEmpty()) {
                for (Section section : downloadedSections) {
                    if (section.getName().equals(planetName)) {
                        if (section.getText() != null) {
                            openSectionFragment(planetName);
                            return;
                        }
                    }
                }
            }
        }

        
        private void openSectionFragment(String sectionName){
            ((MainActivity) theFragment.getActivity()).setSelectedNavItem();
            FragmentUtil.openSection(sectionName, "Сонячна система", theFragment);
        }
    }
}