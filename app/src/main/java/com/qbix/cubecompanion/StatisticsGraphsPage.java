package com.qbix.cubecompanion;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

public class StatisticsGraphsPage extends Fragment {

    //region #VARIABLES
    private TextView puzzleType;
    private GraphView graph1;
    private GraphView graph2;

    private Activity activity;
    //endregion

    public static StatisticsGraphsPage newInstance() {
        return new StatisticsGraphsPage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vi = inflater.inflate(R.layout.fragment_statistics_graphs_page, container, false);
        MainActivity.setFont((ViewGroup) vi);

        activity = getActivity();

        findViews(vi);
        bindViews();

        return vi;
    }

    private void findViews(View vi) {
        puzzleType = (TextView) vi.findViewById(R.id.puzzle_type);
        graph1 = (GraphView) vi.findViewById(R.id.graph_1);
        graph2 = (GraphView) vi.findViewById(R.id.graph_2);
    }

    private void bindViews() {
        puzzleType.setText(SettingsFragment.getPuzzle(activity));
        graph1.setTitle("Solve Times Progress");
        StaticLabelsFormatter labels = new StaticLabelsFormatter(graph1);
        labels.setHorizontalLabels(new String[]{"past", "recent"});
        GridLabelRenderer glr = graph1.getGridLabelRenderer();
        glr.setLabelFormatter(labels);
        glr.setGridColor(getResources().getColor(R.color.gray_0_75));
        glr.setHighlightZeroLines(false);
        glr.setVerticalAxisTitle("Solve Time (sec)");

        graph2.setTitle("Current Session");
        StaticLabelsFormatter labels2 = new StaticLabelsFormatter(graph2);
        labels2.setHorizontalLabels(new String[]{"past", "recent"});
        GridLabelRenderer glr2 = graph2.getGridLabelRenderer();
        glr2.setLabelFormatter(labels2);
        glr2.setGridColor(getResources().getColor(R.color.gray_0_75));
        glr2.setHighlightZeroLines(false);
        glr2.setVerticalAxisTitle("Solve Time (sec)");
        new UpdateGraphs().execute();
    }

    private class UpdateGraphs extends AsyncTask<Void, Void, List<LineGraphSeries<DataPoint>>> {
        @Override
        protected List<LineGraphSeries<DataPoint>> doInBackground(Void... voids) {
            List<LineGraphSeries<DataPoint>> seriesList = new ArrayList<>();
            List<SolveTime> solveTimes = DBHandlerSolveTimes.getInstance(activity).getAllSolveTimes();
            List<DataPoint> data = new ArrayList<>();
            for (int i = 0; i < solveTimes.size(); ++i) {
                SolveTime st = solveTimes.get(i);
                if (!st.isDNF()) {
                    data.add(new DataPoint(data.size(), st.getTime() / 1000.0));
                }
            }
            seriesList.add(new LineGraphSeries<>(data.toArray(new DataPoint[data.size()])));
            List<SolveTime> solveTimes2 = DBHandlerSolveTimes.getInstance(activity)
                    .getAllSolveTimesAfter(SettingsFragment.getSessionStartTime(activity));
            data = new ArrayList<>();
            for (int i = 0; i < solveTimes2.size(); ++i) {
                SolveTime st = solveTimes2.get(i);
                if (!st.isDNF()) {
                    data.add(new DataPoint(data.size(), st.getTime() / 1000.0));
                }
            }
            seriesList.add(new LineGraphSeries<>(data.toArray(new DataPoint[data.size()])));
            return seriesList;
        }

        @Override
        protected void onPostExecute(List<LineGraphSeries<DataPoint>> seriesList) {
            seriesList.get(0).setThickness(3);
            graph1.addSeries(seriesList.get(0));
            seriesList.get(1).setThickness(3);
            graph2.addSeries(seriesList.get(1));
        }
    }
}