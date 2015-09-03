package com.qbix.cubecompanion;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class StatisticsFullStatsPage extends Fragment {

    //region #VARIABLES
    private Activity activity;

    private TextView puzzleType;

    private TextView totalSolves;
    private TextView overallAverage;
    private TextView best;
    private TextView worst;
    private TextView average5Latest;
    private TextView average5Best;
    private TextView average5Worst;
    private TextView average12Latest;
    private TextView average12Best;
    private TextView average12Worst;
    private TextView ema100;
    private TextView ema1000;
    private TextView emaOverall;
    private TextView totalSolveTime;
    private TextView mostSolvesDay;
    private TextView leastSolvesDay;
    private TextView averageSolvesWith0;
    private TextView averageSolvesWithout0;
    //endregion

    //region #CONSTANTS
    public static final int TOTAL_SOLVES = 0;
    public static final int OVERALL_AVERAGE = 1;
    public static final int BEST = 2;
    public static final int WORST = 3;
    public static final int AVERAGE_5_LATEST = 4;
    public static final int AVERAGE_5_BEST = 5;
    public static final int AVERAGE_5_WORST = 6;
    public static final int AVERAGE_12_LATEST = 7;
    public static final int AVERAGE_12_BEST = 8;
    public static final int AVERAGE_12_WORST = 9;
    public static final int EMA_100 = 10;
    public static final int EMA_1000 = 11;
    public static final int EMA_OVERALL = 12;
    public static final int TOTAL_SOLVE_TIME = 13;
    public static final int MOST_SOLVES_DAY = 14;
    public static final int LEAST_SOLVES_DAY = 15;
    public static final int AVG_SOLVES_WITH0 = 16;
    public static final int AVG_SOLVES_WITHOUT0 = 17;
    //endregion

    public static StatisticsFullStatsPage newInstance() {
        return new StatisticsFullStatsPage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vi = inflater.inflate(R.layout.fragment_statistics_full_stats_page, container, false);
        MainActivity.setFont((ViewGroup) vi);
        activity = getActivity();

        findViews(vi);
        bindViews();

        return vi;
    }

    private void findViews(View vi) {
        puzzleType = (TextView) vi.findViewById(R.id.puzzle_type);

        totalSolves = (TextView) vi.findViewById(R.id.full_stats_total_solves);
        overallAverage = (TextView) vi.findViewById(R.id.full_stats_overall_average);
        best = (TextView) vi.findViewById(R.id.full_stats_best);
        worst = (TextView) vi.findViewById(R.id.full_stats_worst);
        average5Latest = (TextView) vi.findViewById(R.id.full_stats_average_5_latest);
        average5Best = (TextView) vi.findViewById(R.id.full_stats_average_5_best);
        average5Worst = (TextView) vi.findViewById(R.id.full_stats_average_5_worst);
        average12Latest = (TextView) vi.findViewById(R.id.full_stats_average_12_latest);
        average12Best = (TextView) vi.findViewById(R.id.full_stats_average_12_best);
        average12Worst = (TextView) vi.findViewById(R.id.full_stats_average_12_worst);
        ema100 = (TextView) vi.findViewById(R.id.full_stats_ema_100);
        ema1000 = (TextView) vi.findViewById(R.id.full_stats_ema_1000);
        emaOverall = (TextView) vi.findViewById(R.id.full_stats_ema_overall);
        totalSolveTime = (TextView) vi.findViewById(R.id.full_stats_total_solve_time);
        mostSolvesDay = (TextView) vi.findViewById(R.id.full_stats_most_solves_day);
        leastSolvesDay = (TextView) vi.findViewById(R.id.full_stats_least_solves_day);
        averageSolvesWith0 = (TextView) vi.findViewById(R.id.full_stats_average_solves_with0);
        averageSolvesWithout0 = (TextView) vi.findViewById(R.id.full_stats_average_solves_without0);
    }

    private void bindViews() {
        puzzleType.setText(SettingsFragment.getPuzzle(activity));
        new UpdateStats().execute();
    }

    private class UpdateStats extends AsyncTask<Void, Void, String[]> {
        @Override
        public String[] doInBackground(Void... voids) {
            List<SolveTime> stList = DBHandlerSolveTimes.getInstance(activity).getAllSolveTimes();
            SolveTimeStatistics sts = new SolveTimeStatistics(stList);
            String[] values = new String[18];
            values[TOTAL_SOLVES] = ""+sts.getTotalSolves();
            values[OVERALL_AVERAGE] = SolveTime.getFormattedTime(sts.getOverallAverage());
            SolveTime st = sts.getExtreme(DBHandlerSolveTimes.MIN);
            values[BEST] = st.getFormattedTime()+"\n"+st.getLongFormattedDate();
            st = sts.getExtreme(DBHandlerSolveTimes.MAX);
            values[WORST] = st.getFormattedTime()+"\n"+st.getLongFormattedDate();
            values[AVERAGE_5_LATEST] = SolveTime.getFormattedTime(sts.getAverageN(5,
                    DBHandlerSolveTimes.LATEST));
            values[AVERAGE_5_BEST] = SolveTime.getFormattedTime(sts.getAverageN(5,
                    DBHandlerSolveTimes.BEST));
            values[AVERAGE_5_WORST] = SolveTime.getFormattedTime(sts.getAverageN(5,
                    DBHandlerSolveTimes.WORST));
            values[AVERAGE_12_LATEST] = SolveTime.getFormattedTime(sts.getAverageN(12,
                    DBHandlerSolveTimes.LATEST));
            values[AVERAGE_12_BEST] = SolveTime.getFormattedTime(sts.getAverageN(12,
                    DBHandlerSolveTimes.BEST));
            values[AVERAGE_12_WORST] = SolveTime.getFormattedTime(sts.getAverageN(12,
                    DBHandlerSolveTimes.WORST));
            long[] avgs = sts.getExponentialMovingAverages();
            values[EMA_100] = SolveTime.getFormattedTime(avgs[0]);
            values[EMA_1000] = SolveTime.getFormattedTime(avgs[1]);
            values[EMA_OVERALL] = SolveTime.getFormattedTime(avgs[2]);
            values[TOTAL_SOLVE_TIME] = sts.getTotalSolveTime();
            Integer entry = sts.getExtremeSolvesInADay(DBHandlerSolveTimes.MAX);
            if (entry != null)
                values[MOST_SOLVES_DAY] = ""+entry;
            else
                values[MOST_SOLVES_DAY] = "---\n--- --, ----";
            entry = sts.getExtremeSolvesInADay(DBHandlerSolveTimes.MIN);
            if (entry != null)
                values[LEAST_SOLVES_DAY] = ""+entry;
            else
                values[LEAST_SOLVES_DAY] = "---\n--- --, ----";
            values[AVG_SOLVES_WITH0] = sts.getAverageSolvesADay(true);
            values[AVG_SOLVES_WITHOUT0] = sts.getAverageSolvesADay(false);
            return values;
        }

        @Override
        public void onPostExecute(String[] values) {
            totalSolves.setText(values[TOTAL_SOLVES]);
            overallAverage.setText(values[OVERALL_AVERAGE]);
            best.setText(values[BEST]);
            worst.setText(values[WORST]);
            average5Latest.setText(values[AVERAGE_5_LATEST]);
            average5Best.setText(values[AVERAGE_5_BEST]);
            average5Worst.setText(values[AVERAGE_5_WORST]);
            average12Latest.setText(values[AVERAGE_12_LATEST]);
            average12Best.setText(values[AVERAGE_12_BEST]);
            average12Worst.setText(values[AVERAGE_12_WORST]);
            ema100.setText(values[EMA_100]);
            ema1000.setText(values[EMA_1000]);
            emaOverall.setText(values[EMA_OVERALL]);
            totalSolveTime.setText(values[TOTAL_SOLVE_TIME]);
            mostSolvesDay.setText(values[MOST_SOLVES_DAY]);
            leastSolvesDay.setText(values[LEAST_SOLVES_DAY]);
            averageSolvesWith0.setText(values[AVG_SOLVES_WITH0]);
            averageSolvesWithout0.setText(values[AVG_SOLVES_WITHOUT0]);
        }
    }
}