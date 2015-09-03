package com.qbix.cubecompanion;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class BeginnersNotationPage extends Fragment {

    //region #VARIABLES
    private TextView[] texts = new TextView[3];
    private ImageView cube_beg_1_1;
    private ImageView cube_beg_1_2;
    private ImageView cube_beg_1_3;
    private ImageView cube_beg_1_4;
    private ImageView cube_beg_1_5;

    private Activity activity;
    //endregion

    public static BeginnersNotationPage newInstance() {
        return new BeginnersNotationPage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vi = inflater.inflate(R.layout.beginners_notation_page, container, false);
        MainActivity.setFont((ViewGroup) vi);

        activity = getActivity();

        findViews(vi);
        bindViews();

        return vi;
    }

    private void findViews(View vi) {
        texts[0] = (TextView) vi.findViewById(R.id.notation_text0);
        texts[1] = (TextView) vi.findViewById(R.id.notation_text1);
        texts[2] = (TextView) vi.findViewById(R.id.notation_text2);
        cube_beg_1_1 = (ImageView) vi.findViewById(R.id.notation_cube_beg_1_1);
        cube_beg_1_2 = (ImageView) vi.findViewById(R.id.notation_cube_beg_1_2);
        cube_beg_1_3 = (ImageView) vi.findViewById(R.id.notation_cube_beg_1_3);
        cube_beg_1_4 = (ImageView) vi.findViewById(R.id.notation_cube_beg_1_4);
        cube_beg_1_5 = (ImageView) vi.findViewById(R.id.notation_cube_beg_1_5);
    }

    private void bindViews() {
        String[] t = getActivity().getResources().getStringArray(R.array.beg_notation_page);
        for (int i = 0; i < texts.length; ++i) {
            texts[i].setText(Html.fromHtml(t[i]));
        }

        Picasso mPicasso = Picasso.with(activity);
        mPicasso.load(R.drawable.cube_notation).fit().centerInside().into(cube_beg_1_1);
        mPicasso.load(R.drawable.cube_solved).fit().centerInside().into(cube_beg_1_2);
        mPicasso.load(R.drawable.cube_notation_ex_1).fit().centerInside().into(cube_beg_1_3);
        mPicasso.load(R.drawable.cube_notation_ex_2).fit().centerInside().into(cube_beg_1_4);
        mPicasso.load(R.drawable.cube_notation_ex_3).fit().centerInside().into(cube_beg_1_5);
    }
}