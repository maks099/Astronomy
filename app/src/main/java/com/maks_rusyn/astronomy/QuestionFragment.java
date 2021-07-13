package com.maks_rusyn.astronomy;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.maks_rusyn.astronomy.objects.Question;
import com.maks_rusyn.astronomy.objects.ResultData;
import java.util.ArrayList;
import java.util.Random;

/**
 * фрагмент тестування
 */
public class QuestionFragment extends Fragment {

    private TextView txtQuestion;
    private ImageView imgQuestion;
    private LinearLayout lytQuestions;
    private int numOfCurrentQuestion = 0;
    private int countOfRightQuestions = 0;
    private String rootName;
    private int textColor;
    private String sectionName;
    private  ArrayList<Integer> arrOfQuestionIndexes;
    private MainActivity mainActivity;
    private ArrayList<Question> setOfQuestions;
    private Question curQuestion;
    private boolean clearFragmentsStack = true;
    private EditText edtUserAnswer;
    private ArrayList<CheckBox> checkBoxes;
    private RadioGroup rg;


    public QuestionFragment() {
        // Required empty public constructor
    }


    private void init(){
        TextView txtSectionName = getView().findViewById(R.id.txtSectionNameQ);
        txtQuestion = getView().findViewById(R.id.txtQuestionQ);
        imgQuestion = getView().findViewById(R.id.imgViewQuestion);
        lytQuestions = getView().findViewById(R.id.lytQuestions);
        Button btnExit = getView().findViewById(R.id.btnBackToTheme);
        Button btnNext = getView().findViewById(R.id.btnPassTestAgain);
        txtSectionName.setText(setOfQuestions.get(0).getSectionName());
        textColor = ContextCompat.getColor(getContext(), R.color.textColor);
        btnExit.setOnClickListener(l -> exitFromQuestion());
        btnNext.setOnClickListener(e -> nextQuestion());
        showQuestion();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(clearFragmentsStack){
            FragmentManager manager = this.getFragmentManager();
            for(int i = numOfCurrentQuestion; i > -1; i--){
                manager.popBackStack();
            }
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        sectionName = getArguments().getString("section");
        rootName = getArguments().getString("root");
        ArrayList<Question> qq = mainActivity.getQuestionList(sectionName);
        setOfQuestions = getRandomOrderQuestions(qq);
        init();
    }


    private void initRandIndexesMas(int k){
       arrOfQuestionIndexes = new ArrayList<>();
        for(int i = 0; i < k; i++){
            arrOfQuestionIndexes.add(i);
        }
    }


    private ArrayList<Question> getRandomOrderQuestions(ArrayList<Question> rightOrderedQuestions){
        initRandIndexesMas(rightOrderedQuestions.size());
        ArrayList<Question> randomOrderedQuestions = new ArrayList<>();
        int j = rightOrderedQuestions.size();
        for(int ii = 0; ii < j; ii++) {
            int max = arrOfQuestionIndexes.size();
            Random random = new Random();
            int i = random.nextInt(max);
            int ind = arrOfQuestionIndexes.get(i);
            randomOrderedQuestions.add(rightOrderedQuestions.get(ind));
            arrOfQuestionIndexes.remove(i);
        }
        return randomOrderedQuestions;
    }


    private ArrayList<String> getRandomOrderVariants(ArrayList<String> rightOrderedQuestions){
        initRandIndexesMas(rightOrderedQuestions.size());
        ArrayList<String> randomOrderedQuestions = new ArrayList<>();
        int j = rightOrderedQuestions.size();
        for(int ii = 0; ii < j; ii++) {
            int max = arrOfQuestionIndexes.size();
            Random random = new Random();
            int i = random.nextInt(max);
            int ind = arrOfQuestionIndexes.get(i);
            randomOrderedQuestions.add(rightOrderedQuestions.get(ind));
            arrOfQuestionIndexes.remove(i);
        }
        return randomOrderedQuestions;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_question, container, false);
    }


    private void showQuestion(){
        Question curQuestion = setOfQuestions.get(numOfCurrentQuestion);
        this.curQuestion = curQuestion;
        int realNum = 1 + numOfCurrentQuestion;
        txtQuestion.setText("\tПитання № " + (realNum) + "\n" + curQuestion.getQuestion());
        if(curQuestion.getImageUrl().length() != 0){
            imgQuestion.setVisibility(View.VISIBLE);
            imgQuestion.setImageBitmap(curQuestion.getImage());
        } else{
            imgQuestion.setVisibility(View.GONE);
        }
        showProbablyValues(curQuestion);
    }


    private void nextQuestion(){
        checkUserAnswers(curQuestion);
        if(numOfCurrentQuestion < setOfQuestions.size()-1) {
            numOfCurrentQuestion+=1;
            showQuestion();
        } else{
            clearFragmentsStack = false;
            openResultFragment();
        }
    }


    private void openResultFragment(){
        ResultData resultData = new ResultData(sectionName, rootName, setOfQuestions.size(), countOfRightQuestions);
        Bundle resultBundle = new Bundle();
        resultBundle.putParcelable("result", resultData);
        ResultFragment resultFragment = new ResultFragment();
        resultFragment.setArguments(resultBundle);
        mainActivity.getSupportFragmentManager().beginTransaction() // буває exception
                .replace(R.id.frameLayout, resultFragment, null)
                .addToBackStack(null)
                .commit();
    }


    private void exitFromQuestion(){
        if(numOfCurrentQuestion == 0){
            getFragmentManager().popBackStack();
        } else {
            showAlertDialog();
        }
    }


    private void showAlertDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_layout, null);
        dialogBuilder.setView(dialogView);

        TextView txtAlertTitle = dialogView.findViewById(R.id.txtAlertTitle);
        TextView txtAlertMessage = dialogView.findViewById(R.id.txtAlertMessage);
        txtAlertTitle.setText("Попередження");
        txtAlertMessage.setText("Якщо Ви зараз покинете тестування, то Ваш результат не буде збереженим. Ви справді бажаєте завершити проходження тесту?");

        Button btnAlertYes = dialogView.findViewById(R.id.btnAlertYes);
        Button btnAlertCancel = dialogView.findViewById(R.id.btnAlertCancel);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        btnAlertYes.setOnClickListener(l -> {
                alertDialog.cancel();
                getFragmentManager().popBackStack();
            }
        );
        btnAlertCancel.setOnClickListener(l -> alertDialog.cancel());
    }


    private void backToPrevFragment(){
        //;
    }


    private void checkUserAnswers(Question curQuestion){
        switch (curQuestion.getTypeOfQuestion()){
            case pickManyAnswers:{
                int rightChecks = 0;
                for(CheckBox checkBox : checkBoxes){
                    if(checkBox.isChecked()){
                        if(curQuestion.getCorrectAnswerList().contains(checkBox.getText())){
                            rightChecks += 1;
                        }
                        else {
                            rightChecks = 0;
                            break;
                        }
                    }
                }
                countOfRightQuestions += rightChecks / curQuestion.getCorrectAnswerList().size();
                break;
            }
            case pickOneAnswer:{
                int radioButtonID = rg.getCheckedRadioButtonId();
                RadioButton radioButton = rg.findViewById(radioButtonID);
                if(radioButton.getText().equals(curQuestion.getCorrectAnswerList().get(0))){
                    countOfRightQuestions += 1;
                }
                break;
            }
            case enterAnswer:{
                String userAnswer = edtUserAnswer.getText().toString().toLowerCase();
                if(curQuestion.getCorrectAnswerList().get(0).toLowerCase().equals(userAnswer)){
                    countOfRightQuestions += 1;
                }
                break;
            }
        }
    }


    private void showProbablyValues(Question curQuestion){
        lytQuestions.removeAllViews();
        switch (curQuestion.getTypeOfQuestion()){
            case enterAnswer:{
                edtUserAnswer = new EditText(getContext());
                edtUserAnswer.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                setTextSizeForView(edtUserAnswer);
                edtUserAnswer.setHintTextColor(textColor);
                int maxLengthofEditText = curQuestion.getCorrectAnswerList().get(0).length() + 2;
                edtUserAnswer.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthofEditText)});
                edtUserAnswer.setHint("Введіть Вашу відповідь");
                setTintColorForView(edtUserAnswer);
                lytQuestions.addView(edtUserAnswer);
                edtUserAnswer.setMaxLines(1);
                break;
            }
            case pickOneAnswer:{
                ArrayList<RadioButton> radioButtons = new ArrayList<>();
                rg = new RadioGroup(getContext());
                ArrayList<String> vars = getRandomOrderVariants(curQuestion.getProbablyCorrectAnswersList());
                for(int i = 0; i < curQuestion.getProbablyCorrectAnswersList().size(); i++){
                    RadioButton radioButton = new RadioButton(getContext());
                    radioButton.setText(vars.get(i));
                    setTintColorForView(radioButton);
                    setTextSizeForView(radioButton);
                    radioButtons.add(radioButton);
                    rg.addView(radioButton);
                    if(rg.getParent() != null) {
                        ((ViewGroup)rg.getParent()).removeView(rg); // <- fix
                    }
                }
                ((RadioButton)rg.getChildAt(0)).setChecked(true);
                lytQuestions.addView(rg);
                break;
            }
            case pickManyAnswers:{
                checkBoxes = new ArrayList<>();
                ArrayList<String> vars = getRandomOrderVariants(curQuestion.getProbablyCorrectAnswersList());
                for(int i = 0; i < curQuestion.getProbablyCorrectAnswersList().size(); i++){
                    CheckBox checkBox = new CheckBox(getContext());
                    setTextSizeForView(checkBox);
                    setTintColorForView(checkBox);
                    checkBox.setText(vars.get(i));
                    lytQuestions.addView(checkBox);
                    checkBoxes.add(checkBox);
                }
                break;
            }
        }
    }


    public boolean onBackPressed() {
        if(numOfCurrentQuestion > 0){
            showAlertDialog();
            return true;
        }
        else{
            return false;
        }

    }


    private void setTextSizeForView(TextView view){
        view.setTextSize(14f);
        view.setTextColor(textColor);
        view.setMinimumWidth(75);
    }


    private void setTintColorForView(RadioButton compoundView){
        compoundView.setButtonTintList(ColorStateList.valueOf(textColor));
    }


    private void setTintColorForView(CheckBox compoundView){
        compoundView.setButtonTintList(ColorStateList.valueOf(textColor));
    }


    private void setTintColorForView(EditText editText){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            editText.setBackgroundTintList(ColorStateList.valueOf(textColor));
        } else{
            DrawableCompat.setTint(editText.getBackground(), ContextCompat.getColor(getContext(), R.color.white));
        }
    }


}