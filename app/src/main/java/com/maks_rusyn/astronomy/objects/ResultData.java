package com.maks_rusyn.astronomy.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.maks_rusyn.astronomy.utils.Util;

import java.util.Calendar;


public class ResultData implements Parcelable {

    private String section;
    private String dateTime;

    public ResultData(){

    }

    protected ResultData(Parcel in) {
        section = in.readString();
        complexityLevel = in.readString();
        allQuestions = in.readInt();
        rightAnswers = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(section);
        dest.writeString(complexityLevel);
        dest.writeInt(allQuestions);
        dest.writeInt(rightAnswers);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ResultData> CREATOR = new Creator<ResultData>() {
        @Override
        public ResultData createFromParcel(Parcel in) {
            return new ResultData(in);
        }

        @Override
        public ResultData[] newArray(int size) {
            return new ResultData[size];
        }
    };

    
    public String getComplexityLevel() {
        return complexityLevel;
    }

    
    public int getAllQuestions() {
        return allQuestions;
    }

    
    public int getRightAnswers() {
        return rightAnswers;
    }

    private String complexityLevel;
    private int allQuestions, rightAnswers;


    public ResultData(String section, String complexityLevel, int allQuestions, int rightAnswers) {
        this.section = section;
        this.complexityLevel = complexityLevel;
        this.allQuestions = allQuestions;
        this.rightAnswers = rightAnswers;
        this.dateTime = Util.getDateTime();
    }

    
    public String getSection() {
        return section;
    }

    
    public String getDateTime() {
        return dateTime;
    }
}
