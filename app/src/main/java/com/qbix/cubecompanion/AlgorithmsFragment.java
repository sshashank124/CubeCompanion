package com.qbix.cubecompanion;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.ListPopupWindow;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

public class AlgorithmsFragment extends Fragment implements AdapterView.OnItemClickListener {

    //region #VARIABLES
    private TextView pagePicker;
    private ListPopupWindow lpw;

    private int currentFragmentIndex = -1;
    private Fragment currentPage;
    private FragmentManager fragmentManager;

    private Activity activity;
    //endregion

    //region #CONSTANTS
    public static final int NUM_PAGES = 12;
    public static final int CMLL = 0;
    public static final int COLL = 1;
    public static final int ELL = 2;
    public static final int F2L = 3;
    public static final int OLL = 4;
    public static final int PLL = 5;
    public static final int WVLS = 6;
    public static final int CLL = 7;
    public static final int EG1 = 8;
    public static final int EG2 = 9;
    public static final int ORT_OLL = 10;
    public static final int ORT_PBL = 11;

    public static final String[] pageTitles = {"CMLL", "COLL", "ELL", "F2L", "OLL", "PLL",
            "WVLS", "CLL", "EG-1", "EG-2", "ORTEGA OLL", "ORTEGA PBL"};
    //endregion

    public static AlgorithmsFragment newInstance() { return new AlgorithmsFragment(); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View vi = inflater.inflate(R.layout.fragment_algs, container, false);
        MainActivity.setFont((ViewGroup) vi);
        activity = getActivity();

        int initial_page = 0;
        if (savedInstanceState != null) {
            initial_page = savedInstanceState.getInt(SettingsFragment.SAVED_PAGE);
        }

        init(vi);
        bindViews(initial_page);

        return vi;
    }

    @Override
    public void onPause() {
        super.onPause();
        lpw.dismiss();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SettingsFragment.SAVED_PAGE, currentFragmentIndex);
    }

    private void init(View vi) {
        pagePicker = (TextView) vi.findViewById(R.id.page_picker);
        fragmentManager = getChildFragmentManager();
    }

    private void bindViews(int page) {
        selectItem(page);
        lpw = Helper.createPageSelectorPopup(activity, pageTitles, pagePicker);
        lpw.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
        lpw.dismiss();
    }

    public void selectItem(int position) {
        if (position == currentFragmentIndex) return;

        if (position == OLL) currentPage = AlgsOLLPage.newInstance();
        else if (isBaseAlgPage(position)) currentPage = AlgsPage.newInstance(position);
        else if (isGroupAlgPage(position)) currentPage = AlgsGroupsPage.newInstance(position);

        fragmentManager.beginTransaction().replace(R.id.page_content_frame, currentPage).commit();
        currentFragmentIndex = position;
        pagePicker.setText(pageTitles[position]);
    }

    public boolean isBaseAlgPage(int pos) {
        return (pos == ELL) || (pos == F2L) || (pos == PLL) || (pos == WVLS)
                || (pos == ORT_OLL) || (pos == ORT_PBL);
    }

    public boolean isGroupAlgPage(int pos) {
        return (pos == CMLL) || (pos == COLL) || (pos == CLL)
                || (pos == EG1) || (pos == EG2);
    }
}