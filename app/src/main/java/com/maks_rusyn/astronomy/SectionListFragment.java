package com.maks_rusyn.astronomy;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maks_rusyn.astronomy.objects.Section;
import com.maks_rusyn.astronomy.adapters.SectionListAdapter;
import com.maks_rusyn.astronomy.utils.FragmentUtil;

import java.util.ArrayList;

import static com.maks_rusyn.astronomy.utils.ImageUtil.getDrawable;


public class SectionListFragment extends Fragment {

    private ConstraintLayout lytList;
    private ListView sectionsList;
    private TextView txtThemeName;
    private Drawable itemsStyle;
    private ArrayList<Section> currentSections;
    private String rootTheme;
    private MainActivity mainActivity;
    private int countOfNeededItems = 0, pos = -1;
    private boolean fragmentIsPrepared = false;

    public SectionListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentIsPrepared = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_section_list, container, false);
    }

    
    private void setBackground() {
        int themeID = getArguments().getInt("theme_id");
        Drawable bgImage;
        itemsStyle = getDrawable(R.drawable.section_list_item_style, getContext());
        switch (themeID){
            case 0: {
                bgImage = getDrawable(R.drawable.theme1, getContext());
                rootTheme = "Сонячна система";
                break;
            }
            case 1: {
                bgImage = getDrawable(R.drawable.theme2, getContext());
                rootTheme = "Галактика";
                break;
            }
            default: {
                bgImage = getDrawable(R.drawable.theme3, getContext());
                rootTheme = "Дослідження космосу";
                break;
            }
        }
        lytList.setBackground(bgImage);
        txtThemeName.setText(rootTheme);
        currentSections = mainActivity.getSectionList(rootTheme);
        System.out.println(currentSections.toString());
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sectionsList = getView().findViewById(R.id.sectionsList);
        txtThemeName = getView().findViewById(R.id.txtThemeName);
        lytList = getView().findViewById(R.id.lytList);
        mainActivity = (MainActivity) getActivity();
        mainActivity.setLastFragment(this);
        setBackground();
        if(currentSections.size()>0) {
            includeCustomAdapter();
        }
        super.onViewCreated(view, savedInstanceState);
    }

    
    private void includeCustomAdapter() {
        SectionListAdapter customCountryList = new SectionListAdapter(this, currentSections, getTitlesForList(), itemsStyle);
        sectionsList.setAdapter(customCountryList);
        Fragment f = this;
        sectionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(fragmentIsPrepared) {
                    countOfNeededItems = 0;
                    Section pickedSection = currentSections.get(position);
                    pos = position;
                    if (pickedSection.getText() == null) {
                        new Thread(() -> {
                            getExtraInfoForSection(pickedSection);
                            while (countOfNeededItems != 2) { }
                            FragmentUtil.openSection(pickedSection.getName(), rootTheme, f);
                        }).start();
                    } else {
                        FragmentUtil.openSection(pickedSection.getName(), rootTheme, f);
                    }
                    
                }
            }
        });
    }

    
    private void getExtraInfoForSection(Section section) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Information").child(section.getThemeRoot()).child(section.getName()).child("info");
        ValueEventListener valueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    String info = snapshot.child("text").getValue().toString();
                    section.setText(info);
                    mainActivity.addNewSection(rootTheme, section, pos);
                    countOfNeededItems = 2;
                } catch (NullPointerException ex){
                    ex.printStackTrace();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };
        databaseReference.addValueEventListener(valueListener);
    }

    
    private String[] getTitlesForList(){
        String [] titleMassive = new String[currentSections.size()];
        for (int i = 0; i < currentSections.size(); i++) {
            Section section = currentSections.get(i);
            titleMassive[i] = section.getName();
        }
        return titleMassive;
    }
}