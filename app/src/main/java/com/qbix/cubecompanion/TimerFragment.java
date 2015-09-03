package com.qbix.cubecompanion;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.ListPopupWindow;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

public class TimerFragment extends Fragment implements AdapterView.OnItemClickListener {

    //region #VARIABLES
    private TextView pagePicker;
    private ListPopupWindow lpw;

    private int currentFragmentIndex = -1;
    private Fragment currentPage;
    private FragmentManager fragmentManager;

    private Activity activity;
    private int orientation;
    //endregion

    //region #CONSTANTS
    public static final int TIMER_MAIN = 0;
    public static final int TIMER_MS = 1;
    public static final int TIMER_SPLIT = 2;

    public static final String[] pageTitles = new String[2];
    static {
        pageTitles[TIMER_MAIN] = "Regular";
        pageTitles[TIMER_MS] = "Multi-step";
        //pageTitles[TIMER_SPLIT] = "Split";
    }
    //endregion

    public static TimerFragment newInstance() {
        return new TimerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View vi = inflater.inflate(R.layout.fragment_timer, container, false);
        MainActivity.setFont((ViewGroup) vi);
        activity = getActivity();
        orientation = getResources().getConfiguration().orientation;

        int initial_page = TIMER_MAIN;
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
        if (isLandscape()) return;
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
        if (isLandscape()) {
            pagePicker.setVisibility(View.GONE);
        }
    }

    private void bindViews(int page) {
        selectItem(page);
        if (isLandscape()) return;

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
            case TIMER_MAIN:
                currentPage = TimerMainFragment.newInstance();
                break;
            case TIMER_MS:
                currentPage = TimerMultiStepFragment.newInstance();
                break;
            case TIMER_SPLIT:
                currentPage = TimerSplitFragment.newInstance();
                break;
        }

        fragmentManager.beginTransaction().replace(R.id.content_frame, currentPage).commit();
        currentFragmentIndex = position;
        if (isLandscape()) return;
        pagePicker.setText(pageTitles[position]);
    }

    private boolean isLandscape() {
        return orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
}