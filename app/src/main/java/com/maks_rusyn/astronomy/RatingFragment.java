package com.maks_rusyn.astronomy;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maks_rusyn.astronomy.adapters.RatingListAdapter;
import com.maks_rusyn.astronomy.objects.ResultData;
import com.maks_rusyn.astronomy.utils.Util;

import java.util.ArrayList;
import java.util.Collections;


public class RatingFragment extends Fragment {

    private ArrayList<ResultData> resultDataList;
    private ListView resultList;
    private TextView txtEmptyList;
    private int allResults = -1,  downloadedResults = 0;
    private DatabaseReference databaseReference;
    private ValueEventListener valueListener;
    private MainActivity mainActivity;
    private Toast toast;

    public RatingFragment() {
        // Required empty public constructor
    }

    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        resultList = getView().findViewById(R.id.resultList);
        txtEmptyList = getView().findViewById(R.id.txtEmptyList);
        resultDataList = new ArrayList<>();
        mainActivity = (MainActivity) getActivity();
        if(mainActivity.getResultData().isEmpty()){
            getRatingList();
        } else{
            resultDataList = mainActivity.getResultData();
            includeCustomAdapter();
        }

    }

    
    @Override
    public void onDestroy() {
        if(databaseReference != null) {
            databaseReference.removeEventListener(valueListener);
        }
        super.onDestroy();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rating, container, false);
    }

    
    private void getRatingList() {
        String userName = Util.getDeviceId(getContext());
         databaseReference = FirebaseDatabase.getInstance().getReference("Results").child(userName);
         valueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allResults = (int) snapshot.getChildrenCount();
                boolean showListFlag = true;
                if(allResults == 0){
                    showListFlag = false;
                    downloadedResults = 0;
                    txtEmptyList.setVisibility(View.VISIBLE);
                }
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ResultData resultData = ds.getValue(ResultData.class);
                    resultDataList.add(resultData);
                    downloadedResults++;
                }
                if(allResults == downloadedResults && showListFlag){
                    includeCustomAdapter();
                }
                mainActivity.setResultData(resultDataList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };

        databaseReference.addValueEventListener(valueListener);
        boolean connected = Util.isNetworkConnected(getActivity().getApplicationContext());
        if (!connected && isToastNotRunning()) {
            toast = Toast.makeText(getContext(), "Перевірте підключення до мережі", Toast.LENGTH_SHORT);
            toast.show();
            databaseReference.removeEventListener(valueListener);
        }

    }

    
    private boolean isToastNotRunning() {
        return (toast == null || toast.getView().getWindowVisibility() != View.VISIBLE);
    }

    
    private void includeCustomAdapter() {
        Collections.reverse(resultDataList);
        RatingListAdapter customCountryList = new RatingListAdapter(this, resultDataList, getTitlesForList());
        resultList.setAdapter(customCountryList);
    }

    
    private String[] getTitlesForList(){
        String [] titleMassive = new String[resultDataList.size()];
        for (int i = 0; i < resultDataList.size(); i++) {
            ResultData section = resultDataList.get(i);
            titleMassive[i] = section.getSection();
        }
        return titleMassive;
    }
}