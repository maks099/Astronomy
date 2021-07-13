package com.maks_rusyn.astronomy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.maks_rusyn.astronomy.utils.Util;

import java.util.ArrayList;


public class SettingsFragment extends Fragment {

    private SharedPreferences settingsPref;
    private Toast toast;
    private MainActivity mainActivity;

    public SettingsFragment() {
        // Required empty public constructor
    }

    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        settingsPref = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        CheckBox showPlanetNames = getView().findViewById(R.id.chbShowPlanetName);
        showPlanetNames.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                   SharedPreferences.Editor editor = settingsPref.edit();
                   editor.putBoolean("show_planet_name", isChecked);
                   editor.apply();
               }
           }
        );

        mainActivity = (MainActivity) getActivity();
        boolean isShownPlanetNames = settingsPref.getBoolean("show_planet_name", true);
        showPlanetNames.setChecked(isShownPlanetNames);
        Button btnRemoveRating = getView().findViewById(R.id.btnRemoveRating);
        btnRemoveRating.setOnClickListener(l -> removeRating());
    }

    
    private void removeRating(){
        FirebaseDatabase.getInstance().getReference().getRoot().child("Results").child(Util.getDeviceId(getContext())).removeValue();
        mainActivity.setResultData(new ArrayList<>());
        showToast();
    }

    
    private void showToast(){
        if (isToastNotRunning()) {
            toast = Toast.makeText(getContext(), "Ваш рейтинг очищено", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    
    private boolean isToastNotRunning() {
        return (toast == null || toast.getView().getWindowVisibility() != View.VISIBLE);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
}