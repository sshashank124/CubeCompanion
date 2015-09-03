package com.qbix.cubecompanion;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class TimerMultiStepFragment extends Fragment implements ShakeDetector.OnShakeListener {

    //region #VARIABLES
    private Handler mHandler = new Handler();
    private long startTime;
    private long elapsedTime;

    private TextView pagePicker;

    private Button timerCancel;
    private Button timerClear;

    private LinearLayout timerDetails;

    private TextView puzzleType;
    private TextView stepInputMode;
    private TextView stepCount;

    private ShakeDetector sd;
    private long lastStepTime;
    private int steps = 0;
    private Toast toast;
    private List<String> stepTimes = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ListView stepList;

    private TextView timerStopped;
    private TextView timerRunning;
    private RelativeLayout timerStoppedLayout;
    private RelativeLayout timerRunningLayout;
    private TextView scrambleText;

    private Activity activity;
    private int orientation;
    private Scrambler scrambler;
    //endregion

    //region #CONSTANTS
    public static final int MANUAL = 0;
    public static final int SHAKE = 1;
    public static final String[] inputTypesTexts = {"Tap", "Shake"};
    //endregion

    //region #RUNNABLES
    private Runnable startTimer = new Runnable() {
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;
            timerRunning.setText(Helper.millisToShortTime(elapsedTime));
            mHandler.post(this);
        }
    };
    //endregion

    //region #PREFERENCES
    private int START_TIMER_WAIT_TIME;
    private boolean SHOW_SCRAMBLE;
    private int PUZZLE_INDEX;
    private int MS_INPUT_TYPE;
    private int MS_COUNT;
    //endregion

    public static TimerMultiStepFragment newInstance() {
        return new TimerMultiStepFragment ();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View vi = inflater.inflate(R.layout.fragment_timer_ms, container, false);
        MainActivity.setFont((ViewGroup) vi);

        init(vi);
        bindViews();

        return vi;
    }

    @Override
    public void onPause() {
        super.onPause();
        sd.unregister();
    }

    private void init(View vi) {
        activity = getActivity();
        orientation = getResources().getConfiguration().orientation;
        START_TIMER_WAIT_TIME = SettingsFragment.getTimerDelay(activity);
        SHOW_SCRAMBLE = SettingsFragment.showScramble(activity);
        MS_INPUT_TYPE = SettingsFragment.getMSIMType(activity);
        MS_COUNT = SettingsFragment.getMSIMCount(activity);
        scrambler = new Scrambler(activity);

        timerCancel = (Button) vi.findViewById(R.id.timer_cancel);
        timerClear = (Button) vi.findViewById(R.id.timer_clear);

        timerDetails = (LinearLayout) vi.findViewById(R.id.timer_details);
        stepList = (ListView) vi.findViewById(R.id.steps_list);

        timerStopped = (TextView) vi.findViewById(R.id.timer_display_stopped);
        timerRunning = (TextView) vi.findViewById(R.id.timer_display_running);
        timerStoppedLayout = (RelativeLayout) vi.findViewById(R.id.timer_display_stopped_layout);
        timerRunningLayout = (RelativeLayout) vi.findViewById(R.id.timer_display_running_layout);
        scrambleText = (TextView) vi.findViewById(R.id.scramble_text);

        if (isLandscape()) return;

        PUZZLE_INDEX = SettingsFragment.getPuzzleIndex(activity);

        pagePicker = (TextView) activity.findViewById(R.id.page_picker);

        puzzleType = (TextView) vi.findViewById(R.id.puzzle_type);
        stepInputMode = (TextView) vi.findViewById(R.id.timer_ms_input_mode);
        stepCount = (TextView) vi.findViewById(R.id.timer_ms_steps_count);
    }

    private void bindViews() {
        new UpdateScramble().execute();

        sd = new ShakeDetector(activity, this);

        //region #CONFIGURE timerStopped BUTTON
        timerStoppedLayout.setOnTouchListener(new View.OnTouchListener() {
            long last_time;
            TimerWaitPeriod twp;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    timerStoppedLayout.setPressed(true);
                    last_time = System.currentTimeMillis();
                    twp = new TimerWaitPeriod(timerStoppedLayout);
                    twp.execute();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    timerStoppedLayout.setPressed(false);
                    if (System.currentTimeMillis() - last_time < START_TIMER_WAIT_TIME) {
                        twp.cancel(true);
                    } else {
                        startTiming();
                    }
                }
                return true;
            }
        });
        //endregion

        //region #CONFIGURE timerRunning BUTTON
        timerRunningLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (MS_INPUT_TYPE == MANUAL) {
                        addStepTime();
                        return true;
                    }
                    stopTiming(SolveTime.getFormattedTime(elapsedTime));
                }
                return true;
            }
        });
        //endregion

        //region #CONFIGURE timerCancel BUTTON
        timerCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTiming(SolveTime.getFormattedTime(0L));
            }
        });
        //endregion

        //region #CONFIGURE timerClear BUTTON
        timerClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerStopped.setText(SolveTime.getFormattedTime(0L));
                timerStoppedLayout.setPressed(false);
                resetStepTimes();
                adapter.notifyDataSetChanged();
            }
        });
        //endregion

        adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, stepTimes);
        stepList.setAdapter(adapter);

        if (isLandscape()) return;

        puzzleType.setText(DBHandlerSolveTimes.PUZZLE_NAMES[PUZZLE_INDEX]);
        stepInputMode.setText(inputTypesTexts[MS_INPUT_TYPE]+" mode");
        stepCount.setText("Steps: "+MS_COUNT);
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
        timerStoppedLayout.setVisibility(View.VISIBLE);
        timerRunningLayout.setVisibility(View.GONE);
        timerDetails.setVisibility(View.VISIBLE);
        showClearButton();
        adapter.notifyDataSetChanged();
        sd.unregister();
        if (isLandscape()) return;
        pagePicker.setVisibility(View.VISIBLE);
    }

    private void startTiming() {
        startTime = System.currentTimeMillis();
        mHandler.post(startTimer);
        timerRunningLayout.setVisibility(View.VISIBLE);
        timerStoppedLayout.setVisibility(View.GONE);
        timerDetails.setVisibility(View.GONE);
        showCancelButton();
        new UpdateScramble().execute();
        if (MS_INPUT_TYPE == SHAKE) sd.register();
        resetStepTimes();
        lastStepTime = startTime;
        if (isLandscape()) return;
        pagePicker.setVisibility(View.GONE);
    }

    @Override
    public void onShake() {
        addStepTime();
    }

    private void addStepTime() {
        long now = System.currentTimeMillis();
        if (now - lastStepTime < 1000) return;
        ++steps;
        stepTimes.add(SolveTime.getFormattedTime(elapsedTime));
        lastStepTime = now;
        toast = Toast.makeText(activity, "Step time added", Toast.LENGTH_SHORT);
        toast.show();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 800);
        if (steps == MS_COUNT) stopTiming(SolveTime.getFormattedTime(elapsedTime));
    }

    private void resetStepTimes() {
        steps = 0;
        stepTimes.clear();
    }

    private boolean isLandscape() { return orientation == Configuration.ORIENTATION_LANDSCAPE; }

    private class TimerWaitPeriod extends AsyncTask<Void, Void, Void> {
        WeakReference<RelativeLayout> t;

        public TimerWaitPeriod(RelativeLayout tsl) {
            t = new WeakReference<>(tsl);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(START_TIMER_WAIT_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (t != null && t.get().isPressed())
                timerStopped.setText("Ready!");
        }
    }

    private class UpdateScramble extends AsyncTask<Void, Void, Spanned> {
        @Override
        protected Spanned doInBackground(Void... voids) {
            if (!SHOW_SCRAMBLE) cancel(true);
            return scrambler.getRandomScramble();
        }

        @Override
        protected void onPostExecute(Spanned scramble) {
            if (scrambleText == null) return;
            scrambleText.setText(scramble);
        }
    }
}