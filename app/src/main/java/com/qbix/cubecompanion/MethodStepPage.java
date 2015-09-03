package com.qbix.cubecompanion;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MethodStepPage extends Fragment {

    //region #VARIABLES
    private int PAGE_INDEX;
    private ImageView startStateImage;
    private ImageView endStateImage;
    private TextView mainText;
    private LinearLayout algListView;

    private Activity activity;
    //endregion

    //region #CONSTANTS
    public static final int BEG_WHITE_CROSS = 0;
    public static final int BEG_WHITE_CORNERS = 1;
    public static final int BEG_MIDDLE_EDGES = 2;
    public static final int BEG_ORIENT_CROSS = 3;
    public static final int BEG_ORIENT_CORNERS = 4;
    public static final int BEG_PERMUTE_CORNERS = 5;
    public static final int BEG_PERMUTE_EDGES = 6;

    private static final int[] START_STATE =
            {R.drawable.cube_unsolved_white, R.drawable.cube_white_cross_white,
             R.drawable.cube_first_layer_yellow, R.drawable.cube_f2l,
             R.drawable.cube_yellow_cross, R.drawable.cube_oll,
             R.drawable.cube_pll_corners};

    private static final int[] END_STATE =
            {R.drawable.cube_white_cross_white, R.drawable.cube_first_layer_white,
             R.drawable.cube_f2l, R.drawable.cube_yellow_cross,
             R.drawable.cube_oll, R.drawable.cube_pll_corners,
             R.drawable.cube_solved};

    private final static int[] MAIN_TEXT_RES_ID =
            {R.string.beg_white_cross, R.string.beg_white_corners,
             R.string.beg_middle_edges, R.string.beg_o_yellow_cross,
             R.string.beg_o_yellow_corners, R.string.beg_p_yellow_corners,
             R.string.beg_p_yellow_edges};
    //endregion

    public static MethodStepPage newInstance(int page) {
        MethodStepPage f = new MethodStepPage();
        Bundle args = new Bundle();
        args.putInt("page", page);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vi = inflater.inflate(R.layout.method_step_page, container, false);
        MainActivity.setFont((ViewGroup) vi);

        PAGE_INDEX = getArguments().getInt("page", BEG_WHITE_CROSS);

        activity = getActivity();

        findViews(vi);
        bindViews();

        return vi;
    }

    private void findViews(View vi) {
        startStateImage = (ImageView) vi.findViewById(R.id.start_state);
        endStateImage = (ImageView) vi.findViewById(R.id.end_state);
        mainText = (TextView) vi.findViewById(R.id.main_text);
        algListView = (LinearLayout) vi.findViewById(R.id.algorithms_list);
    }

    private void bindViews() {
        new GetStepNAlgs().execute();

        Picasso mPicasso = Picasso.with(activity);
        mPicasso.load(START_STATE[PAGE_INDEX]).fit().centerInside().into(startStateImage);
        mPicasso.load(END_STATE[PAGE_INDEX]).fit().centerInside().into(endStateImage);

        mainText.setText(Html.fromHtml(getResources().getString(MAIN_TEXT_RES_ID[PAGE_INDEX])));
    }

    private class GetStepNAlgs extends AsyncTask<Void, Void, List<CubeAlgorithm>> {
        @Override
        protected List<CubeAlgorithm> doInBackground(Void... voids) {
            DBHandlerAlgorithms algDbHandler =
                    DBHandlerAlgorithms.getInstance(getActivity());
            return algDbHandler.getBegStepNAlgs(PAGE_INDEX);
        }

        @Override
        protected void onPostExecute(List<CubeAlgorithm> algList) {
            if (getActivity() == null) return;
            BegAlgViewInflater algViewInflater = new BegAlgViewInflater(getActivity(), algList);
            for (int i = 0; i < algList.size(); ++i) {
                algListView.addView(algViewInflater.getView(i));
            }
        }
    }

    private class BegAlgViewInflater {

        //region #VARIABLES
        private List<CubeAlgorithm> data;
        private LayoutInflater inflater = null;
        //endregion

        public BegAlgViewInflater(Context context, List<CubeAlgorithm> d) {
            data = d;
            if (inflater == null)
                inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public View getView(final int position) {
            View vi = inflater.inflate(R.layout.algorithm_beg, null, false);

            CubeAlgorithm ca = data.get(position);

            ImageView imageView = (ImageView) vi.findViewById(R.id.cube_state);
            if (ca.resId != null) {
                Picasso.with(activity).load(ca.resId).fit().centerInside().into(imageView);
            } else {
                imageView.setVisibility(View.GONE);
            }

            TextView alg = (TextView) vi.findViewById(R.id.algorithm);
            if (ca.alg != null) {
                alg.setText(ca.getAlg());
                alg.setTypeface(MainActivity.typeface_thin);
            } else {
                alg.setVisibility(View.GONE);
            }

            TextView desc = (TextView) vi.findViewById(R.id.algorithm_description);
            if (ca.alg_desc != null) {
                desc.setText(Html.fromHtml(ca.alg_desc));
                desc.setTypeface(MainActivity.typeface_thin);
            } else {
                desc.setVisibility(View.GONE);
            }

            return vi;
        }
    }
}