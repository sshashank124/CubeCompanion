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

public class HelpFragment extends Fragment implements AdapterView.OnItemClickListener {

    //region #VARIABLES
    private TextView pagePicker;
    private ListPopupWindow lpw;

    private int currentFragmentIndex = -1;
    private Fragment currentPage;
    private FragmentManager fragmentManager;

    private Activity activity;
    //endregion

    //region #CONSTANTS
    public static final int HELP = 0;
    public static final int ABOUT = 1;

    public static final String[] pageTitles = {"Help", "About"};
    //endregion

    public static HelpFragment newInstance() {
        return new HelpFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View vi = inflater.inflate(R.layout.fragment_help, container, false);
        MainActivity.setFont((ViewGroup) vi);
        activity = getActivity();

        int initial_page = HELP;
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
            case HELP:
                currentPage = HelpHelpPage.newInstance();
                break;
            case ABOUT:
                currentPage = HelpAboutPage.newInstance();
                break;
        }
        fragmentManager.beginTransaction().replace(R.id.page_content_frame, currentPage).commit();
        currentFragmentIndex = position;
        pagePicker.setText(pageTitles[position]);
    }
}