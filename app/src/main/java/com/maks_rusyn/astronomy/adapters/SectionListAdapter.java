package com.maks_rusyn.astronomy.adapters;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import androidx.fragment.app.Fragment;
import com.maks_rusyn.astronomy.R;
import com.maks_rusyn.astronomy.objects.Section;
import java.util.ArrayList;


public class SectionListAdapter extends ArrayAdapter {

    private ArrayList<Section> sections;
    private ArrayList<Bitmap> bitmaps;
    private Fragment context;
    private Drawable drawForItem;

    
    public SectionListAdapter(Fragment context, ArrayList<Section> sections, String[] titles, Drawable drawable) {
        super(context.getContext(), R.layout.list_item_view, titles);
        this.context = context;
        this.sections = sections;
        this.bitmaps = bitmaps;
        this.drawForItem = drawable;
    }

    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row=convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if(convertView == null) {
            row = inflater.inflate(R.layout.list_item_view, null, true);
        }
        row.setBackground(drawForItem);

        TextView textViewCountry = (TextView) row.findViewById(R.id.sectionNameL);
        ImageView imageFlag = (ImageView) row.findViewById(R.id.imvSectionImageL);

        Section section = sections.get(position);

        textViewCountry.setText(section.getName());
        imageFlag.setImageBitmap(section.getPicture());
        return  row;
    }



}
