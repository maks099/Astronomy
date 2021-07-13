package com.maks_rusyn.astronomy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maks_rusyn.astronomy.objects.Section;
import com.maks_rusyn.astronomy.utils.Util;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class ThemesFragment extends Fragment {

    private DatabaseReference databaseReference;
    private MainActivity activity;
    private Bundle dataForSend;
    private int curDownloadedSectionIndex = 0, themeID = -1;
    private long allSubSectionsCount = -1;
    private ValueEventListener valueListener;
    private LinearLayout linLytSolarSystem, linLytGalaxy, linLytExploration;
    private ArrayList<Section> themeSections;
    private String themeName;
    private Toast toast;

    private HashMap<String, String> planetsWithImportedPicture;


    public ThemesFragment() {
        // Required empty public constructor
    }
    
    @Override
    public void onResume() {
        super.onResume();
        curDownloadedSectionIndex = 0;
        allSubSectionsCount = -1;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dataForSend = new Bundle();
        themeSections = new ArrayList<>();
        planetsWithImportedPicture = Util.getPictureHashes();

        activity = (MainActivity) getActivity();
        activity.setLastFragment(this);
        setListenersForUIElements();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_themes, container, false);
    }

    
    private void setListenersForUIElements(){
        linLytSolarSystem = getView().findViewById(R.id.linLytSolarSystem);
        linLytGalaxy = getView().findViewById(R.id.linLytGalaxy);
        linLytExploration = getView().findViewById(R.id.linLytExploration);

        linLytSolarSystem.setOnClickListener(e -> openSolarSystemCategory());
        linLytGalaxy.setOnClickListener(e -> openGalaxyCategory());
        linLytExploration.setOnClickListener(e -> openExplorationCategory());
    }

    
    private void clearData(){
        curDownloadedSectionIndex = 0;
        allSubSectionsCount = -1;
        try {
            databaseReference.removeEventListener(valueListener);
            themeSections.clear();
        } catch (NullPointerException ignored){ }
    }


    
    public void openSolarSystemCategory(){
        linLytSolarSystem.setEnabled(false);
        themeID = 0;
        themeName = "Сонячна система";
        dataForSend.putInt("theme_id", 0);
        startGoToListFragment(dataForSend);
    }

    
    public void openGalaxyCategory(){
        linLytGalaxy.setEnabled(false);
        themeID = 1;
        themeName = "Галактика";
        dataForSend.putInt("theme_id", 1);
        startGoToListFragment(dataForSend);
    }

    
    public void openExplorationCategory(){
        linLytExploration.setEnabled(false);
        themeID = 2;
        themeName = "Дослідження космосу";
        dataForSend.putInt("theme_id", 2);
        startGoToListFragment(dataForSend);
    }

    
    private void startGoToListFragment(Bundle dataForNewFragment){
        clearData();
        ArrayList<Section> sectionArrayList = activity.getSectionList(themeName);
        if(sectionArrayList == null || sectionArrayList.isEmpty() ) {
            getListOfSubThemes();
            showToastIfNotConnection();
        } else {
            openSectionListFragment(dataForSend);
        }
        this.dataForSend = dataForNewFragment;
        if(curDownloadedSectionIndex == allSubSectionsCount) {
            openSectionListFragment(dataForSend);
        }
    }

    
    private boolean isToastNotRunning() {
        return (toast == null || toast.getView().getWindowVisibility() != View.VISIBLE);
    }


    
    private void openSectionListFragment(Bundle dataForNewFragment) {
        activity.runOnUiThread(() -> {
            activity.addNewSectionList(themeName, themeSections);
            SectionListFragment sectionListFragment = new SectionListFragment();
            sectionListFragment.setArguments(dataForNewFragment);
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, sectionListFragment, null)
                    .addToBackStack(null)
                    .commit();
        });
    }

    
    private void showToastIfNotConnection(){
        boolean connected = Util.isNetworkConnected(activity.getApplicationContext());
        if (!connected && isToastNotRunning()) {
            toast = Toast.makeText(getContext(), "Перевірте підключення до мережі", Toast.LENGTH_SHORT);
            toast.show();

            databaseReference.removeEventListener(valueListener);
            linLytSolarSystem.setEnabled(true);
            linLytGalaxy.setEnabled(true);
            linLytExploration.setEnabled(true);
        }
        if(!connected){
            if(themeID == 0){
                linLytSolarSystem.setEnabled(true);
            } else if(themeID == 1){
                linLytGalaxy.setEnabled(true);
            } else{
                linLytExploration.setEnabled(true);
            }
        }
    }

    
    private void getListOfSubThemes() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Information").child(themeName);
        valueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    allSubSectionsCount = snapshot.getChildrenCount();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String itemOfTheme = ds.getKey();
                        Section section = new Section();
                        section.setName(itemOfTheme);
                        section.setThemeRoot(themeName);
                        String picName = planetsWithImportedPicture.get(itemOfTheme);
                        if(themeName.equals("Сонячна система") && picName != null){
                            int id = getContext().getResources().getIdentifier(planetsWithImportedPicture.get(itemOfTheme), "drawable", getContext().getPackageName());
                            Bitmap image = BitmapFactory.decodeResource(getResources(), id);
                            setBitmapForSection(image, section);
                        }else {
                            getBitmapOfSectionByURL(ds.child("picture").getValue().toString(), section);
                        }
                    }
                } catch (NullPointerException ex){
                    ex.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                registerUser();

            }
        };
        databaseReference.addValueEventListener(valueListener);
    }

    
    private void registerUser(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String email = "someemail@domain.com";

        auth.signInWithEmailAndPassword(email, "012345")
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

            }
        });
    }

    
    private void getBitmapOfSectionByURL(String urlString, Section section){
         new Thread(() -> {
             try {
                 URL url = new URL(urlString);
                 Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                 setBitmapForSection(image, section);
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }).start();
    }

    
    private void setBitmapForSection(Bitmap bitmapForSection, Section section){
        section.setPicture(bitmapForSection);
        themeSections.add(section);
        curDownloadedSectionIndex++;
        if(curDownloadedSectionIndex == allSubSectionsCount){
            openSectionListFragment(dataForSend);
        }
    }
}