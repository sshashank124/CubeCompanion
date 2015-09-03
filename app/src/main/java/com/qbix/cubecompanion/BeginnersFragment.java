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

public class BeginnersFragment extends Fragment implements AdapterView.OnItemClickListener {

    //region #VARIABLES
    private TextView pagePicker;
    private ListPopupWindow lpw;

    private int currentFragmentIndex = -1;
    private Fragment currentPage;
    private FragmentManager fragmentManager;

    private Activity activity;
    //endregion

    //region #CONSTANTS
    public static final int NOTATION = 0;
    public static final int W_CROSS = 1;
    public static final int W_CORNERS = 2;
    public static final int M_EDGES = 3;
    public static final int O_Y_CROSS = 4;
    public static final int O_Y_CORNERS = 5;
    public static final int P_Y_CORNERS = 6;
    public static final int P_Y_EDGES = 7;

    public static final String[] pageTitles = {"Notation", "White Cross", "White Corners",
            "Middle Edges", "Orient Yellow Cross", "Orient Yellow Corners",
            "Permute Yellow Corners", "Permute Yellow Edges"};
    //endregion

    public static BeginnersFragment newInstance() {
        return new BeginnersFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View vi = inflater.inflate(R.layout.fragment_beginners, container, false);
        MainActivity.setFont((ViewGroup) vi);
        activity = getActivity();

        int initial_page = NOTATION;
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

        switch (position) {
            case NOTATION:
                currentPage = BeginnersNotationPage.newInstance();
                break;
            case W_CROSS:
                currentPage = MethodStepPage.newInstance(MethodStepPage.BEG_WHITE_CROSS);
                break;
            case W_CORNERS:
                currentPage = MethodStepPage.newInstance(MethodStepPage.BEG_WHITE_CORNERS);
                break;
            case M_EDGES:
                currentPage = MethodStepPage.newInstance(MethodStepPage.BEG_MIDDLE_EDGES);
                break;
            case O_Y_CROSS:
                currentPage = MethodStepPage.newInstance(MethodStepPage.BEG_ORIENT_CROSS);
                break;
            case O_Y_CORNERS:
                currentPage = MethodStepPage.newInstance(MethodStepPage.BEG_ORIENT_CORNERS);
                break;
            case P_Y_CORNERS:
                currentPage = MethodStepPage.newInstance(MethodStepPage.BEG_PERMUTE_CORNERS);
                break;
            case P_Y_EDGES:
                currentPage = MethodStepPage.newInstance(MethodStepPage.BEG_PERMUTE_EDGES);
                break;
        }
        fragmentManager.beginTransaction().replace(R.id.page_content_frame, currentPage).commit();
        currentFragmentIndex = position;
        pagePicker.setText(pageTitles[position]);
    }
}