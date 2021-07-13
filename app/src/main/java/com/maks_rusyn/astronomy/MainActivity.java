package com.maks_rusyn.astronomy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.maks_rusyn.astronomy.objects.Question;
import com.maks_rusyn.astronomy.objects.ResultData;
import com.maks_rusyn.astronomy.objects.Section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity  {

    private ArrayList<ResultData> resultData;
    private Fragment lastFragment, newFragment;
    private int currentBottomItem = 1, lastBottomItem = 1;
    private Map<String, ArrayList<Question>> questions;
    private Map<String, ArrayList<Section>> sections = new HashMap<>();
    private BottomNavigationView bottomNavigationView;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setListenerForBottomNavigation();
        setStatusBarGradient(this);
        resultData = new ArrayList<>();
        questions = new HashMap<>();
        sections = new HashMap<>();
        switchToThemesFragment();
    }


    
    public void setSelectedNavItem() {
        bottomNavigationView.setSelectedItemId(R.id.action_info);
    }

    
    public void addNewSectionList(String key, ArrayList<Section> sectionsList){
        if(sections.get(key)==null || sections.get(key).isEmpty()) {
            sections.put(key, sectionsList);
            if(key.equals("Сонячна система")){
                makeSpecRightOrder();
            }
        }
    }

    
    public ArrayList<Section> getSectionList(String key){
        if(sections.containsKey(key)) {
            return sections.get(key);
        } else {
            return null;
        }
    }


    
    public void makeSpecRightOrder(){
        String[] solarSystemThemesInOrder = new String[]{"Сонячна система", "Сонце","Меркурій", "Венера", "Земля", "Марс", "Юпітер", "Сатурн", "Уран", "Нептун", "Карликова планета Плутон"};
        ArrayList<Section> solarSystemSections = sections.get("Сонячна система");
        ArrayList<Section> newSolarSyst = new ArrayList<>();
        for(String rightOrderName : solarSystemThemesInOrder){
            for(Section section : solarSystemSections){
                if(section.getName().equals(rightOrderName)){
                    solarSystemSections.remove(section);
                    newSolarSyst.add(section);
                    break;
                }
            }
        }
        sections.remove("Сонячна система");
        sections.put("Сонячна система", newSolarSyst);
    }

    
    public ArrayList<Question> getQuestionList(String key){
        if(questions.containsKey(key)) {
            return questions.get(key);
        } else {
            return null;
        }
    }


    
    private void setListenerForBottomNavigation() {
         bottomNavigationView = findViewById(R.id.bottomNavigationView);
         bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
             switch (item.getItemId()) {
                 case R.id.action_speed:
                     currentBottomItem = 1;
                     newFragment = new SolarSystemFragment();
                     break;
                 case R.id.action_info:
                     currentBottomItem = 2;
                     if(lastFragment != null){
                         newFragment = lastFragment;
                     } else {
                         newFragment = new ThemesFragment();
                     }
                     break;
                 case R.id.action_rating:
                     currentBottomItem = 3;
                     newFragment = new RatingFragment();
                     break;
                 case R.id.action_settings:
                     currentBottomItem = 4;
                     newFragment = new SettingsFragment();
                     break;
             }
             if (currentBottomItem != lastBottomItem) {
                 switchToSomeFragment(newFragment);
                 lastBottomItem = currentBottomItem;
             }
             return true;
         });
     }

    
    public void switchToSomeFragment(Fragment newFragment) {
        FragmentManager manager = getSupportFragmentManager();
        if (newFragment.isAdded()) {
            FragmentTransaction t = manager.beginTransaction();
            t.show(newFragment);
        } else {
            manager.beginTransaction().replace(R.id.frameLayout, newFragment).commit();
        }
    }

    
    public void switchToThemesFragment() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frameLayout, new ThemesFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.action_info);
    }


    
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradient(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.gradient_theme);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(activity.getResources().getColor(R.color.bottomSheetColor));
            window.setBackgroundDrawable(background);
        }
    }

    
    @Override
    public void onBackPressed() {
        boolean handled = false;
        for(Fragment f :  getSupportFragmentManager().getFragments()) {
            if(f instanceof QuestionFragment) {
                handled = ((QuestionFragment)f).onBackPressed();
                if(handled) {
                    break;
                }
            } else if(f instanceof ResultFragment){
                handled = ((ResultFragment)f).onBackPressed();
                if(handled) {
                    break;
                }
            }
        }

        if(!handled) {
            super.onBackPressed();
        }
    }

    
    public void addNewSection(String rootName, Section newSection, int pos){
        boolean isInList = false;
        ArrayList<Section> ss = sections.get(rootName);
        for(Section section : ss){
            if(section.getName().equals(newSection.getName())){
                isInList = true;
                break;
            }
        }
        if(!isInList && sections.get(rootName) != null) {
            if(pos == -1) {
                sections.get(rootName).add(newSection);
            } else {
                sections.get(rootName).set(pos, newSection);
            }
        }
    }

    
    public void addNewQuestionList(String key, ArrayList<Question> arrayList){
        if(questions.get(key) == null || questions.get(key).isEmpty()) {
            questions.put(key, arrayList);
        }
    }

    
    public void setLastFragment(Fragment lastFragment) {
        this.lastFragment = lastFragment;
    }

    
    public ArrayList<ResultData> getResultData() {
        return resultData;
    }

    public void setResultData(ArrayList<ResultData> resultData) {
        this.resultData = resultData;
    }
}