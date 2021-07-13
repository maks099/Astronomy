package com.maks_rusyn.astronomy.utils;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.maks_rusyn.astronomy.QuestionFragment;
import com.maks_rusyn.astronomy.R;
import com.maks_rusyn.astronomy.SectionFragment;

/**
 * утиліта роботи з фрагментами
 */
public class FragmentUtil {

    /**
     * функція запуску фрагменту секції
     * @param sectionName назва секції
     * @param root назва теми
     * @param currentFragment поточний фрагмент
     */
    public static void openSection(String sectionName, String root, Fragment currentFragment){
        SectionFragment fragment = new SectionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("picked_section", sectionName);
        bundle.putString("picked_theme", root);
        fragment.setArguments(bundle);
        currentFragment.getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment, null)
                .addToBackStack(null)
                .commit();

    }

    /**
     * відкриття фрагменту тестів
     * @param sectionName назва секції
     * @param root
     * @param currentFragment
     */
    public static void openQuestion(String sectionName, String root, Fragment currentFragment){
        Bundle questionData = new Bundle();
        questionData.putString("section", sectionName);
        questionData.putString("root", root);
        QuestionFragment questionFragment = new QuestionFragment();
        questionFragment.setArguments(questionData);
        currentFragment.getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, questionFragment, null)
                .addToBackStack(null)
                .commit();
    }
}
