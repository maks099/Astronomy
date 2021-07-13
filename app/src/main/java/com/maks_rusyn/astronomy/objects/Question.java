package com.maks_rusyn.astronomy.objects;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class Question {

    private String sectionName, imageUrl, question;
    private Bitmap image;
    private ArrayList<String> probablyCorrectAnswersList, correctAnswerList;
    private TYPE_OF_QUESTION typeOfQuestion;

    
    public Question(){

    }

    public Question(String sectionName, String imageUrl, String question, ArrayList<String> probablyCorrectAnswersList, ArrayList<String> correctAnswerList, TYPE_OF_QUESTION typeOfQuestion) {
        this.sectionName = sectionName;
        this.imageUrl = imageUrl;
        this.question = question;
        this.probablyCorrectAnswersList = probablyCorrectAnswersList;
        this.correctAnswerList = correctAnswerList;
        this.typeOfQuestion = typeOfQuestion;
    }

    
    public String getQuestion() {
        return question;
    }


    public enum TYPE_OF_QUESTION { pickOneAnswer, pickManyAnswers, enterAnswer }


    
    public String getSectionName() {
        return sectionName;
    }

    
    public String getImageUrl() {
        return imageUrl;
    }

    
    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    
    public ArrayList<String> getProbablyCorrectAnswersList() {
        return probablyCorrectAnswersList;
    }

    
    public ArrayList<String> getCorrectAnswerList() {
        return correctAnswerList;
    }

    
    public TYPE_OF_QUESTION getTypeOfQuestion() {
        return typeOfQuestion;
    }
}
