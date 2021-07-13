package com.maks_rusyn.astronomy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maks_rusyn.astronomy.objects.Question;
import com.maks_rusyn.astronomy.objects.Section;
import com.maks_rusyn.astronomy.utils.FragmentUtil;
import com.maks_rusyn.astronomy.utils.ImageUtil;
import com.maks_rusyn.astronomy.utils.Util;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static com.maks_rusyn.astronomy.utils.ImageUtil.getDrawable;


public class SectionFragment extends Fragment {

    private int countOfDownloadedQuestion = 0, countOfAllQuestions = -1;
    private int resourceCode = -1;
    private TextView txtThemeName;
    private TextView txtMainInfo;
    private ImageView imgPic;
    private Button btnStartTest;
    private FrameLayout frameSection;
    private Toast toast;

    private Section currentSection;
    private ArrayList<Question> allQuestions;
    private MainActivity mainActivity;
    private String sectionRoot;
    private DatabaseReference databaseReference;
    private ValueEventListener valueListener;

    public SectionFragment() {
        // Required empty public constructor
    }


    
    private void init(){
       String sectionName = getArguments().getString("picked_section");
       sectionRoot = getArguments().getString("picked_theme");
       mainActivity = (MainActivity) getActivity();
       mainActivity.setLastFragment(this);
       for(Section section : mainActivity.getSectionList(sectionRoot)){
           if(section.getName().equals(sectionName)){
               currentSection = section;
               setUI();
               break;
           }
       }

    }

    
    private void ifSectionIsSpecial(String sectionName){
        if(sectionName.equals("Часова шкала досліджень")){
            btnStartTest.setVisibility(View.GONE);
        }
    }

    
    private void setUI() {
        txtThemeName.setText(currentSection.getName());
        String p = "     " + parseText(currentSection.getText());
        txtMainInfo.setText(p);
        btnStartTest = getView().findViewById(R.id.btnTest);
        ifSectionIsSpecial(currentSection.getName());
        btnStartTest.setOnClickListener(l -> startTest());
        setPictureInView();
        setBackgroundOfFragment();
    }

    
    private void setPictureInView() {
        Bitmap sectionPicture = currentSection.getPicture().copy(Bitmap.Config.ARGB_8888, true);
        Point screenSize = Util.getScreenSize(getActivity());
        int length = (int) (screenSize.x * 0.42);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(length, length);
        sectionPicture = ImageUtil.getResizedBitmap(sectionPicture, length, length);
        imgPic.setMinimumHeight(length);
        imgPic.setMinimumWidth(length);
        imgPic.setMaxHeight(length);
        imgPic.setMaxWidth(length);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
            imgPic.setLayoutParams(layoutParams);
        }
        imgPic.setImageBitmap(sectionPicture);
    }

    
    private void setBackgroundOfFragment() {
        String colorCode = "#CC0000";
        switch (currentSection.getThemeRoot()){
            case "Сонячна система":{
                resourceCode = R.drawable.theme1;
                break;
            }
            case "Галактика": {
                resourceCode = R.drawable.theme2;
                colorCode = "#669900";
                break;
            }
            case "Дослідження космосу": {
                resourceCode = R.drawable.theme3;
                colorCode = "#0099CC";
                break;
            }
        }
        frameSection.setBackground(getDrawable(resourceCode, getContext()));
        btnStartTest.setBackgroundColor(Color.parseColor(colorCode));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_section, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        txtThemeName = getView().findViewById(R.id.txtThemeNameS);
        txtMainInfo = getView().findViewById(R.id.txtMainInfoS);
        imgPic = getView().findViewById(R.id.imgPicS);
        frameSection = getView().findViewById(R.id.sectionFrame);
        allQuestions = new ArrayList<>();
        init();
    }

    
    public void startTest(){
        countOfAllQuestions = -1;
        countOfDownloadedQuestion = 0;
        btnStartTest.setEnabled(false);
        boolean bb = mainActivity.getQuestionList(currentSection.getName()) == null;
        if(countOfDownloadedQuestion == countOfAllQuestions || !bb){
            openQuestionFragment();
        } else {
            getAllQuestions();
            showToastIfNotConnection();
        }
    }

    
    private void openQuestionFragment(){
        Fragment fragment = this;
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FragmentUtil.openQuestion(currentSection.getName(), sectionRoot, fragment);
            }
        });
    }

    
    private void getAllQuestions() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Information").child(currentSection.getThemeRoot()).child(currentSection.getName()).child("question");
        valueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                countOfAllQuestions = (int) snapshot.getChildrenCount();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Question question = ds.getValue(Question.class);
                    if(question.getImageUrl().length() != 0) {
                        getBitmapOfQuestionByURL(question);
                    } else{
                        countOfDownloadedQuestion++;
                    }
                    allQuestions.add(question);
                }
                checkIfCanOpenQuestion();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };
        databaseReference.addValueEventListener(valueListener);
    }

    
    private void showToastIfNotConnection(){
        if(countOfDownloadedQuestion!=countOfAllQuestions) {
            boolean connected = Util.isNetworkConnected(getContext());
            if (!connected && isToastNotRunning()) {
                toast = Toast.makeText(getContext(), "Перевірте підключення до мережі", Toast.LENGTH_SHORT);
                toast.show();
            }
            if(!connected){
                btnStartTest.setEnabled(true);
                clearDataListener();
            }
        }
    }

    
    private void clearDataListener(){
        databaseReference.removeEventListener(valueListener);
    }

    
    private boolean isToastNotRunning() {
        return (toast == null || toast.getView().getWindowVisibility() != View.VISIBLE);
    }

    
    private void getBitmapOfQuestionByURL(Question question){
        new Thread(() -> {
            try {
                URL url = new URL(question.getImageUrl());
                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                question.setImage(image);
                countOfDownloadedQuestion++;
                checkIfCanOpenQuestion();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    
    private void checkIfCanOpenQuestion() {
        if (countOfDownloadedQuestion == countOfAllQuestions) {
            mainActivity.addNewQuestionList(currentSection.getName(), allQuestions);
            openQuestionFragment();
        }
    }

    
    private String parseText(String inputText){
        inputText = inputText.replaceAll("<br>", "\n\n\t");
        inputText = inputText.replaceAll("<li>", "\n\n\t\t\u23FA\t"); // + "\t\t\u23FA\t
        inputText = inputText.replaceAll("<ol>", "\n\t⦿\t");
        return inputText;
    }
}