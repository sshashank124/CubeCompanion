package com.qbix.cubecompanion;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class HelpHelpPage extends Fragment {

    //region #VARIABLES
    private static final int[] helpTextsResId = {R.array.help_timer, R.array.help_timer_ms,
            R.array.help_statistics, R.array.help_cfop, R.array.help_beginners,
            R.array.help_settings, R.array.help_feedback, R.array.help_rate};
    //endregion

    public static HelpHelpPage newInstance() {
        return new HelpHelpPage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vi = inflater.inflate(R.layout.fragment_help_help_page, container, false);
        MainActivity.setFont((ViewGroup) vi);

        List<String> helpTitles = Arrays.asList("Timer", "Multi-step Timer", "Statistics",
                "Algorithms", "Beginners", "Settings", "Feedback", "Rate");

        List<List<String>> helpTextsLists = new ArrayList<>();
        for (int resId : helpTextsResId)
            helpTextsLists.add(Arrays.asList(getResources().getStringArray(resId)));

        HashMap<String, List<String>> helpChildren = new HashMap<>();
        for (int i = 0; i < helpTextsLists.size(); ++i)
            helpChildren.put(helpTitles.get(i), helpTextsLists.get(i));

        ExpandableListView explistview = (ExpandableListView) vi.findViewById(R.id.help_list);
        explistview.setAdapter(new HelpExpandableListAdapter(getActivity(),
                helpTitles, helpChildren));

        return vi;
    }
}