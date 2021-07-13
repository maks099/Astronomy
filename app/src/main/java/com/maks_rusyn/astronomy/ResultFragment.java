package com.maks_rusyn.astronomy;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.maks_rusyn.astronomy.objects.ResultData;
import com.maks_rusyn.astronomy.utils.FragmentUtil;
import com.maks_rusyn.astronomy.utils.Util;


public class ResultFragment extends Fragment {

    private  ResultData resultData;


    public ResultFragment() {
        // Required empty public constructor
    }

    
    private void clearBackStack(){
        FragmentManager manager = getActivity().getSupportFragmentManager();
        for(int i = resultData.getAllQuestions(); i > -1; i--){
            manager.popBackStack();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        TextView txtSectionName = getView().findViewById(R.id.txtSectionNameR);
        TextView txtComplexity = getView().findViewById(R.id.txtComplexityItem);
        TextView txtMark = getView().findViewById(R.id.txtMark);

        Button btnBackToTheme = getView().findViewById(R.id.btnBackToTheme);
        btnBackToTheme.setOnClickListener(e -> backToTheme());

        Button btnPassTestAgain = getView().findViewById(R.id.btnPassTestAgain);
        btnPassTestAgain.setOnClickListener(e -> passTestAgain());

        resultData = getArguments().getParcelable("result");

        txtSectionName.setText(resultData.getSection());
        txtComplexity.setText(resultData.getComplexityLevel());

        String mark = "Результат\n" + resultData.getRightAnswers() + " / " + resultData.getAllQuestions();
        txtMark.setText(mark);
        sendResultToServer();
        super.onViewCreated(view, savedInstanceState);
    }

    
    public boolean onBackPressed(){
        backToTheme();
        return true;
    }

    
    private void backToTheme(){
        clearBackStack();
        FragmentUtil.openSection(resultData.getSection(), resultData.getComplexityLevel(), this);
    }

    
    private void passTestAgain(){
        clearBackStack();
        FragmentUtil.openQuestion(resultData.getSection(), resultData.getComplexityLevel(),this);
    }

    
    private void sendResultToServer(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Results");
        databaseReference.child(Util.getDeviceId(getContext())).push().setValue(resultData);
    }
}