package com.qbix.cubecompanion;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class StatisticsTimesListPage extends Fragment {

    //region #VARIABLES
    public TextView puzzleType;
    public Button manualAddST;
    public Button deleteAllTimes;
    public ListView timesList;
    public ProgressBar deleteTimesProgress;

    private Activity activity;
    //endregion

    public static StatisticsTimesListPage newInstance() {
        return new StatisticsTimesListPage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vi = inflater.inflate(R.layout.fragment_statistics_times_list_page, container, false);
        MainActivity.setFont((ViewGroup) vi);

        activity = getActivity();

        findViews(vi);
        bindViews();

        return vi;
    }

    private void findViews(View vi) {
        puzzleType = (TextView) vi.findViewById(R.id.puzzle_type);
        manualAddST = (Button) vi.findViewById(R.id.manual_add_solve_time);
        deleteAllTimes = (Button) vi.findViewById(R.id.times_list_delete_all);
        timesList = (ListView) vi.findViewById(R.id.times_list_grid);
        deleteTimesProgress = (ProgressBar) vi.findViewById(R.id.times_list_delete_all_progressbar);
    }

    private void bindViews() {
        puzzleType.setText(SettingsFragment.getPuzzle(activity));
        new PopulateTimesList().execute();

        //region #CONFIGURE deleteAllTimes BUTTON
        deleteAllTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupDialog pd = new PopupDialog(activity, "You will lose all your solve times",
                        "Cancel", "Delete");
                final AlertDialog ad = pd.getDialog();
                pd.button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ad.dismiss();
                    }
                });
                pd.button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DeleteAllSolveTimes().execute();
                        ad.dismiss();
                    }
                });
                ad.show();
            }
        });
        //endregion

        //region #CONFIGURE manualAddST BUTTON
        manualAddST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout view = (LinearLayout) activity.getLayoutInflater()
                        .inflate(R.layout.dialog_input, null);
                MainActivity.setFont(view);
                final EditText input = (EditText) view.findViewById(R.id.dialog_input);
                input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (textToMillis(s.toString()) == -1L) {
                            input.setTextColor(getResources().getColor(R.color.yellow));
                        } else {
                            input.setTextColor(getResources().getColor(R.color.white));
                        }
                    }
                });
                Button button1 = (Button) view.findViewById(R.id.dialog_button1);
                button1.setText("DNF");
                Button button2 = (Button) view.findViewById(R.id.dialog_button2);
                button2.setText("Add");
                final AlertDialog ad = new AlertDialog.Builder(activity).setView(view).create();
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new StoreSolveTime().execute(-2L);
                        ad.dismiss();
                        new PopulateTimesList().execute();
                    }
                });
                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long t = textToMillis(input.getText().toString());
                        if (t == -1L) return;
                        new StoreSolveTime().execute(t);
                        ad.dismiss();
                        new PopulateTimesList().execute();
                    }
                });
                ad.show();
            }
        });
        //endregion
    }

    private long textToMillis(String t) {
        if (!t.contains(".")) return -1L;
        try {
            int m = 0;
            int colon = t.indexOf(":");
            if (colon != -1) m = Integer.parseInt(t.substring(0, colon));
            double s = Double.parseDouble(t.substring(colon+1, t.length()));
            if (s < 0 || s >= 60) return -1L;
            return (m*60000)+(long)(s*1000);
        } catch (Exception e) {
            e.printStackTrace();
            return -1L;
        }
    }

    private class PopulateTimesList extends AsyncTask<Void, Void, List<SolveTime>> {
        @Override
        protected List<SolveTime> doInBackground(Void... voids) {
            return DBHandlerSolveTimes.getInstance(activity).getAllSolveTimes();
        }

        @Override
        protected void onPostExecute(List<SolveTime> stList) {
            Collections.reverse(stList);
            timesList.setAdapter(new StatisticsTimesListAdapter(activity, stList));
        }
    }

    private class DeleteAllSolveTimes extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            deleteAllTimes.setVisibility(View.GONE);
            timesList.setVisibility(View.GONE);
            deleteTimesProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DBHandlerSolveTimes.getInstance(activity).deleteAllSolveTimes();
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            deleteAllTimes.setVisibility(View.VISIBLE);
            timesList.setVisibility(View.VISIBLE);
            deleteTimesProgress.setVisibility(View.GONE);
            new PopulateTimesList().execute();
        }
    }

    private class StoreSolveTime extends AsyncTask<Long, Void, Void> {
        @Override
        protected Void doInBackground(Long... t) {
            DBHandlerSolveTimes.getInstance(activity)
                    .addSolveTime(new SolveTime(t[0], System.currentTimeMillis(), (String) null));
            return null;
        }
    }
}