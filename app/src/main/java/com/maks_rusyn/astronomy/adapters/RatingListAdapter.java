package com.maks_rusyn.astronomy.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.view.View;
import androidx.fragment.app.Fragment;
import com.maks_rusyn.astronomy.R;
import com.maks_rusyn.astronomy.objects.ResultData;
import java.util.ArrayList;


public class RatingListAdapter extends ArrayAdapter {

    private ArrayList<ResultData> resultDataList;
    private Fragment context;

    
    public RatingListAdapter(Fragment context, ArrayList<ResultData> datas, String[] titles) {
        super(context.getContext(), R.layout.result_item_view, titles);
        this.context = context;
        this.resultDataList = datas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row=convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if(convertView==null) {
            row = inflater.inflate(R.layout.result_item_view, null, true);
        }

        TextView txtSectionName = (TextView) row.findViewById(R.id.txtSectionNameR);
        TextView txtMark = (TextView) row.findViewById(R.id.txtMarkR);
        TextView txtDateTime = (TextView) row.findViewById(R.id.txtDateTimeR);

        ResultData resultData = resultDataList.get(position);
        txtSectionName.setText(resultData.getSection());
        txtMark.setText(resultData.getRightAnswers() +" / " + resultData.getAllQuestions());
        txtDateTime.setText(resultData.getDateTime());

        return  row;
    }



}
