package com.qbix.cubecompanion;

import android.app.Activity;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.List;

public class TimerMainFragment extends Fragment {

    //region #VARIABLES
    private List<SolveTime> solveTimes;

    private Handler mHandler = new Handler();
    private boolean startWithPenalty;
    private long startTime;
    private long elapsedTime;

    private TextView pagePicker;

    private Button timerCancel;
    private Button timerClear;

    private LinearLayout timerStats;

    private TextView puzzleType;
    private TextView resetSession;

    private TextView penaltyDNF;
    private TextView[] statsTextViews = new TextView[6];
    private TextView penalty2;

    private TextView timerStopped;
    private TextView timerInspection;
    private TextView timerRunning;
    private RelativeLayout timerStoppedLayout;
    private RelativeLayout timerInspectionLayout;
    private RelativeLayout timerRunningLayout;
    private TextView scrambleText;

    private MediaPlayer timerInspectionNotification;
    private boolean played8Sound = false, played12Sound = false;

    private Activity activity;
    private DBHandlerSolveTimes db;
    private int orientation;
    private Scrambler scrambler;
    private String currentScramble;
    private long lastSolveTimeId;
    //endregion

    //region #RUNNABLES
    private Runnable startTimer = new Runnable() {
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;
            timerRunning.setText(Helper.millisToShortTime(elapsedTime));
            mHandler.post(this);
        }
    };
    private Runnable startInspecting = new Runnable() {
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;
            boolean keepGoing = true;
            if (elapsedTime > INSPECTION_TIME_MS + 2000) {
                timerCancel.performClick();
                keepGoing = false;
            } else if (elapsedTime > INSPECTION_TIME_MS) {
                timerInspection.setText("+2");
                startWithPenalty = true;
            } else {
                timerInspection.setText(""+(((INSPECTION_TIME_MS - elapsedTime) / 1000)+1));
                if (elapsedTime > 8000 && !played8Sound)
                    new PlayInspectionTimerSound().execute();
                else if (elapsedTime > 12000 && !played12Sound)
                    new PlayInspectionTimerSound().execute();
            }
            if (keepGoing) mHandler.postDelayed(this, 50);
            else mHandler.removeCallbacks(this);
        }
    };
    //endregion

    //region #PREFERENCES
    private boolean SHOW_SCRAMBLE;
    private int START_TIMER_WAIT_TIME_MS;
    private boolean SHOW_INSPECTION_TIME;
    private int PUZZLE_INDEX;
    private long SESSION_START_TIME;
    //endregion

    //region #CONSTANTS
    public static final int INSPECTION_TIME_MS = 15000;
    //endregion

    public static TimerMainFragment newInstance() {
        return new TimerMainFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View vi = inflater.inflate(R.layout.fragment_timer_main, container, false);
        MainActivity.setFont((ViewGroup) vi);

        init(vi);
        bindViews();

        new UpdateScramble().execute();

        return vi;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (SHOW_INSPECTION_TIME) {
            timerInspectionNotification.reset();
            timerInspectionNotification.release();
        }
        mHandler.removeCallbacks(startInspecting);
        mHandler.removeCallbacks(startTimer);
    }

    private void init(View vi) {
        activity = getActivity();
        db = DBHandlerSolveTimes.getInstance(activity);
        orientation = getResources().getConfiguration().orientation;
        START_TIMER_WAIT_TIME_MS = SettingsFragment.getTimerDelay(activity);
        SHOW_SCRAMBLE = SettingsFragment.showScramble(activity);
        PUZZLE_INDEX = SettingsFragment.getPuzzleIndex(activity);
        SHOW_INSPECTION_TIME = SettingsFragment.useInspectionTime(activity);
        if (SHOW_INSPECTION_TIME)
            timerInspectionNotification = MediaPlayer.create(activity,
                R.raw.timer_inspection_notification);
        scrambler = new Scrambler(activity);

        timerStopped = (TextView) vi.findViewById(R.id.timer_display_stopped);
        timerRunning = (TextView) vi.findViewById(R.id.timer_display_running);
        timerInspection = (TextView) vi.findViewById(R.id.timer_display_inspection);
        timerStoppedLayout = (RelativeLayout) vi.findViewById(R.id.timer_display_stopped_layout);
        timerRunningLayout = (RelativeLayout) vi.findViewById(R.id.timer_display_running_layout);
        timerInspectionLayout = (RelativeLayout)
                vi.findViewById(R.id.timer_display_inspection_layout);
        scrambleText = (TextView) vi.findViewById(R.id.scramble_text);

        if (isLandscape()) return;

        SESSION_START_TIME = SettingsFragment.getSessionStartTime(activity);

        pagePicker = (TextView) activity.findViewById(R.id.page_picker);

        timerCancel = (Button) vi.findViewById(R.id.timer_cancel);
        timerClear = (Button) vi.findViewById(R.id.timer_clear);

        puzzleType = (TextView) vi.findViewById(R.id.puzzle_type);
        resetSession = (TextView) vi.findViewById(R.id.timer_new_session);
        timerStats = (LinearLayout) vi.findViewById(R.id.timer_stats);

        statsTextViews[0] = (TextView) vi.findViewById(R.id.timer_statistics_solves);
        statsTextViews[1] = (TextView) vi.findViewById(R.id.timer_statistics_average);
        statsTextViews[2] = (TextView) vi.findViewById(R.id.timer_statistics_best);
        statsTextViews[3] = (TextView) vi.findViewById(R.id.timer_statistics_worst);
        statsTextViews[4] = (TextView) vi.findViewById(R.id.timer_statistics_average_5);
        statsTextViews[5] = (TextView) vi.findViewById(R.id.timer_statistics_average_12);

        penaltyDNF = (TextView) vi.findViewById(R.id.penalty_DNF);
        penalty2 = (TextView) vi.findViewById(R.id.penalty_2);
    }

    private void bindViews() {

        //region #CONFIGURE timerStopped BUTTON
        if (!SHOW_INSPECTION_TIME) {
            timerStoppedLayout.setOnTouchListener(new View.OnTouchListener() {
                long last_time;
                TimerWaitPeriod twp;
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        timerStoppedLayout.setPressed(true);
                        last_time = System.currentTimeMillis();
                        twp = new TimerWaitPeriod(timerStoppedLayout);
                        twp.execute(timerStopped);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        timerStoppedLayout.setPressed(false);
                        if (System.currentTimeMillis() - last_time < START_TIMER_WAIT_TIME_MS) {
                            twp.cancel(true);
                            return true;
                        }
                        startTiming();
                    }
                    return true;
                }
            });
        } else {
            timerStoppedLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startInspectionTimer();
                }
            });
        }
        //endregion

        //region #CONFIGURE timerInspection BUTTON
        if (SHOW_INSPECTION_TIME) {
            timerInspectionLayout.setOnTouchListener(new View.OnTouchListener() {
                long last_time;
                TimerWaitPeriod twp;
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        timerInspectionLayout.setPressed(true);
                        last_time = System.currentTimeMillis();
                        twp = new TimerWaitPeriod(timerInspectionLayout);
                        twp.execute(timerInspection);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        timerInspectionLayout.setPressed(false);
                        if (System.currentTimeMillis() - last_time < START_TIMER_WAIT_TIME_MS) {
                            twp.cancel(true);
                            return true;
                        }
                        startTiming();
                    }
                    return true;
                }
            });
        }
        //endregion

        //region #CONFIGURE timerRunning BUTTON
        timerRunningLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    new StoreSolveTime().execute();
                    stopTiming(SolveTime.getFormattedTime(elapsedTime));
                    if (isLandscape()) return true;
                    penaltyDNF.setEnabled(true);
                    penalty2.setEnabled(true);
                    new UpdateStats().execute();
                }
                return true;
            }
        });
        //endregion

        if (isLandscape()) return;

        new GetAllSolveTimes().execute();
        new UpdateStats().execute();

        puzzleType.setText(DBHandlerSolveTimes.PUZZLE_NAMES[PUZZLE_INDEX]);

        //region #CONFIGURE resetSession BUTTON
        resetSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SESSION_START_TIME = SettingsFragment.resetSessionStartTime(activity);
                new GetAllSolveTimes().execute();
                new UpdateStats().execute();
                timerClear.performClick();
                Toast.makeText(activity, "New Session Started", Toast.LENGTH_SHORT).show();
            }
        });
        //endregion

        //region #CONFIGURE timerCancel BUTTON
        timerCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTiming(SolveTime.getFormattedTime(0L));
                mHandler.removeCallbacks(startInspecting);
                penaltyDNF.setEnabled(false);
                penalty2.setEnabled(false);
            }
        });
        //endregion

        //region #CONFIGURE timerClear BUTTON
        timerClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerStopped.setText(SolveTime.getFormattedTime(0L));
                timerStopped.setTextColor(getResources().getColor(R.color.white));
                timerStoppedLayout.setPressed(false);
                penaltyDNF.setEnabled(false);
                penalty2.setEnabled(false);
            }
        });
        //endregion

        //region #CONFIGURE penalty2 BUTTON
        penalty2.setEnabled(false);
        penalty2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ModifySolveTime().execute(false);
                new UpdateStats().execute();
            }
        });
        //endregion

        //region #CONFIGURE penaltyDNF BUTTON
        penaltyDNF.setEnabled(false);
        penaltyDNF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ModifySolveTime().execute(true);
                new UpdateStats().execute();
            }
        });
        //endregion
    }

    private void showCancelButton() {
        timerCancel.setVisibility(View.VISIBLE);
        timerClear.setVisibility(View.GONE);
    }

    private void showClearButton() {
        timerClear.setVisibility(View.VISIBLE);
        timerCancel.setVisibility(View.GONE);
    }

    private void stopTiming(String text) {
        mHandler.removeCallbacks(startTimer);
        timerStopped.setText(text);
        timerRunning.setText(Helper.millisToShortTime(0L));
        timerInspectionLayout.setVisibility(View.GONE);
        timerRunningLayout.setVisibility(View.GONE);
        timerStoppedLayout.setVisibility(View.VISIBLE);
        startWithPenalty = false;
        played8Sound = false; played12Sound = false;
        new UpdateScramble().execute();
        if (isLandscape()) return;
        pagePicker.setVisibility(View.VISIBLE);
        timerStats.setVisibility(View.VISIBLE);
        showClearButton();
    }

    private void startInspectionTimer() {
        startTime = System.currentTimeMillis();
        mHandler.post(startInspecting);
        timerStoppedLayout.setVisibility(View.GONE);
        timerRunningLayout.setVisibility(View.GONE);
        timerInspectionLayout.setVisibility(View.VISIBLE);
        new UpdateScramble().execute();
        if (isLandscape()) return;
        pagePicker.setVisibility(View.GONE);
        timerStats.setVisibility(View.GONE);
        penaltyDNF.setEnabled(false);
        penalty2.setEnabled(false);
        showCancelButton();
    }

    private void startTiming() {
        mHandler.removeCallbacks(startInspecting);
        if (startWithPenalty) startTime = System.currentTimeMillis() - 2000;
        else startTime = System.currentTimeMillis();
        mHandler.post(startTimer);
        timerStoppedLayout.setVisibility(View.GONE);
        timerInspectionLayout.setVisibility(View.GONE);
        timerRunningLayout.setVisibility(View.VISIBLE);
        timerStopped.setTextColor(getResources().getColor(R.color.white));
        timerInspection.setTextColor(getResources().getColor(R.color.white));
        if (isLandscape()) return;
        pagePicker.setVisibility(View.GONE);
        timerStats.setVisibility(View.GONE);
        penaltyDNF.setEnabled(false);
        penalty2.setEnabled(false);
        showCancelButton();
    }

    private boolean isLandscape() {
        return orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    private class PlayInspectionTimerSound extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            timerInspectionNotification.start();
            if (elapsedTime > 12000) played12Sound = true;
            else if (elapsedTime > 8000) played8Sound = true;
            return null;
        }
    }

    private class TimerWaitPeriod extends AsyncTask<TextView, Void, TextView> {
        WeakReference<RelativeLayout> t;

        public TimerWaitPeriod(RelativeLayout tsl) {
            t = new WeakReference<>(tsl);
        }

        @Override
        protected TextView doInBackground(TextView... textViews) {
            try {
                Thread.sleep(START_TIMER_WAIT_TIME_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return textViews[0];
        }

        @Override
        protected void onPostExecute(TextView textView) {
            if (t != null && t.get().isPressed())
                textView.setTextColor(getResources().getColor(R.color.yellow));
        }
    }

    private class GetAllSolveTimes extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            solveTimes = DBHandlerSolveTimes.getInstance(activity)
                    .getAllSolveTimesAfter(SESSION_START_TIME);
            return null;
        }
    }

    private class StoreSolveTime extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            SolveTime st = new SolveTime(
                    elapsedTime,
                    System.currentTimeMillis(),
                    currentScramble);
            lastSolveTimeId = db.addSolveTime(st);
            if (isLandscape()) return null;
            solveTimes.add(st);
            return null;
        }
    }

    private class ModifySolveTime extends AsyncTask<Boolean, Void, Void> {
        @Override
        protected Void doInBackground(Boolean... b) {
            if (b[0]) elapsedTime = -2L;
            else elapsedTime += 2000;
            SolveTime st = new SolveTime(lastSolveTimeId, elapsedTime, System.currentTimeMillis());
            db.modifySolveTime(st);
            solveTimes.get(solveTimes.size() - 1).setTime(elapsedTime);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            timerStopped.setText(SolveTime.getFormattedTime(elapsedTime));
            penaltyDNF.setEnabled(false);
            penalty2.setEnabled(false);
        }
    }

    private class UpdateStats extends AsyncTask<Void, Void, Long[]> {
        @Override
        protected Long[] doInBackground(Void... voids) {
            SolveTimeStatistics sts = new SolveTimeStatistics(solveTimes);
            return sts.getBasicStats();
        }

        @Override
        protected void onPostExecute(Long[] stats) {
            statsTextViews[0].setText("" + stats[0]);
            for (int i = 1; i < 6; ++i) {
                statsTextViews[i].setText(SolveTime.getFormattedTime(stats[i]));
            }
        }
    }

    private class UpdateScramble extends AsyncTask<Void, Void, Spanned> {
        @Override
        protected void onPreExecute() {
            scrambleText.setText("Generating Scramble...");
        }

        @Override
        protected Spanned doInBackground(Void... voids) {
            if (!SHOW_SCRAMBLE) cancel(true);
            return scrambler.getRandomScramble();
        }

        @Override
        protected void onPostExecute(Spanned scramble) {
            if (scrambleText == null) return;
            scrambleText.setText(scramble);
            currentScramble = scramble.toString();
        }
    }
}