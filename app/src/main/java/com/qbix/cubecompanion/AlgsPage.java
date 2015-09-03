package com.qbix.cubecompanion;

import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class AlgsPage extends Fragment {

    //region #VARIABLES
    private Context context;

    private ImageView startStateImage;
    private ImageView endStateImage;

    private ListView algListView;
    private AlgorithmsListAdapter algAdapter;

    private int orientation;
    private int PAGE;
    //endregion

    //region #CONSTANTS
    private static final SparseArray<Integer> startStates = new SparseArray<>();
    private static final SparseArray<Integer> endStates = new SparseArray<>();
    static {
        startStates.put(AlgorithmsFragment.ELL, R.drawable.cube_cll);
        startStates.put(AlgorithmsFragment.F2L, R.drawable.cube_white_cross_yellow);
        startStates.put(AlgorithmsFragment.PLL, R.drawable.cube_oll);
        startStates.put(AlgorithmsFragment.WVLS, R.drawable.cube_wvls_25);
        startStates.put(AlgorithmsFragment.ORT_OLL, R.drawable.cube_2_blank);
        startStates.put(AlgorithmsFragment.ORT_PBL, R.drawable.cube_2_oll_ort);

        endStates.put(AlgorithmsFragment.ELL, R.drawable.cube_solved);
        endStates.put(AlgorithmsFragment.F2L, R.drawable.cube_f2l);
        endStates.put(AlgorithmsFragment.PLL, R.drawable.cube_solved);
        endStates.put(AlgorithmsFragment.WVLS, R.drawable.cube_oll);
        endStates.put(AlgorithmsFragment.ORT_OLL, R.drawable.cube_2_oll_ort);
        endStates.put(AlgorithmsFragment.ORT_PBL, R.drawable.cube_2_solved);
    }
    //endregion

    public static AlgsPage newInstance(int page) {
        AlgsPage f = new AlgsPage();
        Bundle args = new Bundle();
        args.putInt("page", page);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vi = inflater.inflate(R.layout.algs_page, container, false);
        MainActivity.setFont((ViewGroup) vi);

        PAGE = getArguments().getInt("page", AlgorithmsFragment.F2L);

        context = getActivity();
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

        startStateImage = (ImageView) vi.findViewById(R.id.start_state);
        endStateImage = (ImageView) vi.findViewById(R.id.end_state);
    }

    private void bindViews() {
        new GetAllPageAlgs().execute();

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) return;

        Picasso mPicasso = Picasso.with(context);
        mPicasso.load(startStates.get(PAGE)).fit().centerInside().into(startStateImage);
        mPicasso.load(endStates.get(PAGE)).fit().centerInside().into(endStateImage);
    }

    private class GetAllPageAlgs extends AsyncTask<Void, Void, List<CubeAlgorithm>> {
        @Override
        protected List<CubeAlgorithm> doInBackground(Void... voids) {
            DBHandlerAlgorithms algDbHandler =
                    DBHandlerAlgorithms.getInstance(context);
            return algDbHandler.getAllPageAlgs(PAGE);
        }

        @Override
        protected void onPostExecute(List<CubeAlgorithm> algList) {
            algAdapter = new AlgorithmsListAdapter(context, algList);
            algListView.setAdapter(algAdapter);
        }
    }
}