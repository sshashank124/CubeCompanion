package com.qbix.cubecompanion;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class AlgsGroupsPage extends Fragment {

    //region #VARIABLES
    private Activity activity;

    private ImageView startStateImage;
    private ImageView endStateImage;

    private GridView subGroupSelector;

    private LinearLayout algorithmsLayout;
    private Button collapseList;
    private ListView algListView;
    private AlgorithmsListAdapter algAdapter;

    private int orientation;
    private int PAGE;
    //endregion

    //region #CONSTANTS
    private static final SparseArray<Integer> startStates = new SparseArray<>();
    private static final SparseArray<Integer> endStates = new SparseArray<>();
    static {
        startStates.put(AlgorithmsFragment.CMLL, R.drawable.cube_f2l_no_m);
        startStates.put(AlgorithmsFragment.COLL, R.drawable.cube_yellow_cross);
        startStates.put(AlgorithmsFragment.CLL, R.drawable.cube_2_first_layer);
        startStates.put(AlgorithmsFragment.EG1, R.drawable.cube_2_first_layer_eg1);
        startStates.put(AlgorithmsFragment.EG2, R.drawable.cube_2_first_layer_eg2);

        endStates.put(AlgorithmsFragment.CMLL, R.drawable.cube_l6e);
        endStates.put(AlgorithmsFragment.COLL, R.drawable.cube_pll_corners);
        endStates.put(AlgorithmsFragment.CLL, R.drawable.cube_2_solved);
        endStates.put(AlgorithmsFragment.EG1, R.drawable.cube_2_solved);
        endStates.put(AlgorithmsFragment.EG2, R.drawable.cube_2_solved);
    }

    private static final SparseArray<Integer[]> subGroups = new SparseArray<>();
    static {
        Integer[] t = new Integer[8];
        t[0] = R.drawable.cube_cmll_o; t[1] = R.drawable.cube_cmll_as;
        t[2] = R.drawable.cube_cmll_s; t[3] = R.drawable.cube_cmll_l;
        t[4] = R.drawable.cube_cmll_u; t[5] = R.drawable.cube_cmll_t;
        t[6] = R.drawable.cube_cmll_pi; t[7] = R.drawable.cube_cmll_h;
        subGroups.put(AlgorithmsFragment.CMLL, t);

        t = new Integer[7];
        t[0] = R.drawable.cube_oll_24; t[1] = R.drawable.cube_oll_23;
        t[2] = R.drawable.cube_oll_25; t[3] = R.drawable.cube_oll_21;
        t[4] = R.drawable.cube_oll_22; t[5] = R.drawable.cube_oll_27;
        t[6] = R.drawable.cube_oll_26;
        subGroups.put(AlgorithmsFragment.COLL, t);

        t = new Integer[7];
        t[0] = R.drawable.cube_2_cll_s; t[1] = R.drawable.cube_2_cll_as;
        t[2] = R.drawable.cube_2_cll_pi; t[3] = R.drawable.cube_2_cll_u;
        t[4] = R.drawable.cube_2_cll_l; t[5] = R.drawable.cube_2_cll_t;
        t[6] = R.drawable.cube_2_cll_h;
        subGroups.put(AlgorithmsFragment.CLL, t);
        subGroups.put(AlgorithmsFragment.EG1, t);
        subGroups.put(AlgorithmsFragment.EG2, t);
    }
    //endregion

    public static AlgsGroupsPage newInstance(int page) {
        AlgsGroupsPage f = new AlgsGroupsPage();
        Bundle args = new Bundle();
        args.putInt("page", page);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vi = inflater.inflate(R.layout.algs_groups_page, container, false);
        MainActivity.setFont((ViewGroup) vi);

        PAGE = getArguments().getInt("page", AlgorithmsFragment.COLL);

        activity = getActivity();
        orientation = getResources().getConfiguration().orientation;

        findViews(vi);
        bindViews();

        return vi;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (algAdapter != null) algAdapter.updatePreferences(PAGE);
    }

    private void findViews(View vi) {
        algListView = (ListView) vi.findViewById(R.id.algorithms_list);

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) return;

        subGroupSelector = (GridView) vi.findViewById(R.id.sub_group_selector);

        algorithmsLayout = (LinearLayout) vi.findViewById(R.id.algorithms_layout);
        collapseList = (Button) vi.findViewById(R.id.show_sub_group_selector);

        startStateImage = (ImageView) vi.findViewById(R.id.start_state);
        endStateImage = (ImageView) vi.findViewById(R.id.end_state);
    }

    private void bindViews() {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            new GetAllPageAlgs().execute();
            return;
        }

        ImageAdapter imageAdapter = new ImageAdapter(activity, subGroups.get(PAGE));
        subGroupSelector.setAdapter(imageAdapter);

        subGroupSelector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showAlgorithms(position);
            }
        });

        collapseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGroupSelector();
            }
        });

        Picasso mPicasso = Picasso.with(activity);
        mPicasso.load(startStates.get(PAGE)).fit().centerInside().into(startStateImage);
        mPicasso.load(endStates.get(PAGE)).fit().centerInside().into(endStateImage);
    }

    private void showAlgorithms(int position) {
        new GetSpecificGroupAlgs().execute(position);
        subGroupSelector.setVisibility(View.GONE);
        algorithmsLayout.setVisibility(View.VISIBLE);
    }

    private void showGroupSelector() {
        algAdapter.updatePreferences(PAGE);
        subGroupSelector.setVisibility(View.VISIBLE);
        algorithmsLayout.setVisibility(View.GONE);
    }

    private class GetSpecificGroupAlgs extends AsyncTask<Integer, Void, List<CubeAlgorithm>> {
        @Override
        protected List<CubeAlgorithm> doInBackground(Integer... position) {
            return DBHandlerAlgorithms.getInstance(activity)
                    .getSpecificGroupAlgs(PAGE, position[0]);
        }

        @Override
        protected void onPostExecute(List<CubeAlgorithm> algList) {
            algAdapter = new AlgorithmsListAdapter(activity, algList);
            algListView.setAdapter(algAdapter);
        }
    }

    private class GetAllPageAlgs extends AsyncTask<Void, Void, List<CubeAlgorithm>> {
        @Override
        protected List<CubeAlgorithm> doInBackground(Void... voids) {
            return DBHandlerAlgorithms.getInstance(activity).getAllPageAlgs(PAGE);
        }

        @Override
        protected void onPostExecute(List<CubeAlgorithm> algList) {
            algAdapter = new AlgorithmsListAdapter(activity, algList);
            algListView.setAdapter(algAdapter);
        }
    }

    private class ImageAdapter extends BaseAdapter {

        //region #VARIABLES
        private Integer[] data;
        private LayoutInflater inflater = null;
        private Activity activity;
        //endregion

        public ImageAdapter(Activity activity, Integer[] data) {
            this.data = data;
            this.activity = activity;
            inflater = (LayoutInflater) this.activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() { return data.length; }

        @Override
        public Integer getItem(int position) { return data[position]; }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(R.layout.grid_image, parent, false);

            Picasso.with(activity).load(data[position])
                    .fit().centerInside().into((ImageView) convertView);

            return convertView;
        }
    }
}