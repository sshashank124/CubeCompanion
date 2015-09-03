package com.qbix.cubecompanion;

import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AlgsOLLPage extends Fragment {

    //region #VARIABLES
    private Context context;

    private ImageView startStateImage;
    private ImageView endStateImage;

    private ListView algListView;
    private AlgorithmsListAdapter algAdapter;

    private LinearLayout OLLSelector;
    private LinearLayout algorithmsLayout;

    private Button collapseList;

    private SquareView[] OLLButtons = new SquareView[9];
    private boolean[] alreadySelected = {
            false, false, false,
            false, true, false,
            false, false, false };

    private int orientation;
    //endregion

    public static AlgsOLLPage newInstance() {
        return new AlgsOLLPage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vi = inflater.inflate(R.layout.algs_oll, container, false);
        MainActivity.setFont((ViewGroup) vi);

        context = getActivity();
        orientation = getResources().getConfiguration().orientation;

        findViews(vi);
        bindViews();

        return vi;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (algAdapter != null) algAdapter.updatePreferences(AlgorithmsFragment.OLL);
    }

    private void findViews(View vi) {
        algListView = (ListView) vi.findViewById(R.id.algorithms_list);

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) return;

        OLLSelector = (LinearLayout) vi.findViewById(R.id.oll_selector);
        algorithmsLayout = (LinearLayout) vi.findViewById(R.id.algorithms_layout);

        collapseList = (Button) vi.findViewById(R.id.show_oll_selector);

        OLLButtons[0] = (SquareView) vi.findViewById(R.id.sv0);
        OLLButtons[1] = (SquareView) vi.findViewById(R.id.sv1);
        OLLButtons[2] = (SquareView) vi.findViewById(R.id.sv2);
        OLLButtons[3] = (SquareView) vi.findViewById(R.id.sv3);
        OLLButtons[4] = (SquareView) vi.findViewById(R.id.sv4);
        OLLButtons[5] = (SquareView) vi.findViewById(R.id.sv5);
        OLLButtons[6] = (SquareView) vi.findViewById(R.id.sv6);
        OLLButtons[7] = (SquareView) vi.findViewById(R.id.sv7);
        OLLButtons[8] = (SquareView) vi.findViewById(R.id.sv8);

        startStateImage = (ImageView) vi.findViewById(R.id.start_state);
        endStateImage = (ImageView) vi.findViewById(R.id.end_state);
    }

    private void bindViews() {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            new GetAllOLLAlgs().execute();
            return;
        }

        OLLSelector.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int pos = getIndexFromCoords(event.getX(), event.getY());
                    if (pos < 0 || pos > 8 || pos == 4) return true;
                    if (alreadySelected[pos]) {
                        OLLButtons[pos].setActivated(false);
                        alreadySelected[pos] = false;
                    } else {
                        OLLButtons[pos].setActivated(true);
                        alreadySelected[pos] = true;
                    }
                }
                return true;
            }
        });

        OLLButtons[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlgorithms();
            }
        });

        collapseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOLLSelector();
            }
        });

        Picasso mPicasso = Picasso.with(context);
        mPicasso.load(R.drawable.cube_f2l).fit().centerInside().into(startStateImage);
        mPicasso.load(R.drawable.cube_oll).fit().centerInside().into(endStateImage);
    }

    private void showAlgorithms() {
        new GetSpecificOLLAlgs().execute();
        OLLSelector.setVisibility(View.GONE);
        algorithmsLayout.setVisibility(View.VISIBLE);
    }

    private void showOLLSelector() {
        algAdapter.updatePreferences(AlgorithmsFragment.OLL);
        OLLSelector.setVisibility(View.VISIBLE);
        algorithmsLayout.setVisibility(View.GONE);
    }

    private int getIndexFromCoords(float x, float y) {
        int w = OLLSelector.getWidth(); int h = OLLSelector.getHeight();
        return ((int) (y * 3 / h) * 3) + ((int) (x * 3 / w));
    }

    private int[] getIndices() {
        List<Integer> rval = new ArrayList<>();
        for (int i = 0; i < 9; ++i) {
            if (alreadySelected[i]) rval.add(i);
        }
        int[] rv = new int[rval.size()];
        for (int i = 0; i < rv.length; ++i) {
            rv[i] = rval.get(i);
        }
        return rv;
    }

    private int[] rotate(int[] ind) {
        int[] rv = new int[ind.length];
        int[] rotator = {6, 3, 0, 7, 4, 1, 8, 5, 2};
        for (int i = 0; i < ind.length; ++i) {
            rv[i] = rotator[ind[i]];
        }
        return rv;
    }

    private String intArrayToString(int[] arr) {
        String rv = "";
        for (int elem : arr) {
            rv += elem;
        }
        return rv;
    }

    private class GetAllOLLAlgs extends AsyncTask<Void, Void, List<CubeAlgorithm>> {
        @Override
        protected List<CubeAlgorithm> doInBackground(Void... voids) {
            DBHandlerAlgorithms algDbHandler =
                    DBHandlerAlgorithms.getInstance(context);
            return algDbHandler.getAllOLLAlgs();
        }

        @Override
        protected void onPostExecute(List<CubeAlgorithm> algList) {
            algAdapter = new AlgorithmsListAdapter(context, algList);
            algListView.setAdapter(algAdapter);
        }
    }

    private class GetSpecificOLLAlgs extends AsyncTask<Void, Void, List<CubeAlgorithm>> {
        @Override
        protected List<CubeAlgorithm> doInBackground(Void... voids) {
            int[] o1 = getIndices();
            int[] o2 = rotate(o1);
            int[] o3 = rotate(o2);
            int[] o4 = rotate(o3);
            Arrays.sort(o1);
            Arrays.sort(o2);
            Arrays.sort(o3);
            Arrays.sort(o4);
            String f = Collections.min(Arrays.asList(intArrayToString(o1), intArrayToString(o2),
                    intArrayToString(o3), intArrayToString(o4)));
            return DBHandlerAlgorithms.getInstance(context).getOLLAlgs(f);
        }

        @Override
        protected void onPostExecute(List<CubeAlgorithm> algList) {
            algAdapter = new AlgorithmsListAdapter(context, algList);
            algListView.setAdapter(algAdapter);
        }
    }
}