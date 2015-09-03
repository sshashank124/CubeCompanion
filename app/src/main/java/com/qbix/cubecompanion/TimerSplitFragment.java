package com.qbix.cubecompanion;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TimerSplitFragment extends Fragment {

    //region #VARIABLES
    private Activity activity;
    private Handler mHandler = new Handler();
    private long startTime;
    private long elapsedTime;
    private Scrambler scrambler;

    private TextView pagePicker;

    private TextView ts1;
    private TextView tr1;
    private RelativeLayout ts1Layout;
    private RelativeLayout tr1Layout;
    private TextView scrambleText1;

    private TextView ts2;
    private TextView tr2;
    private RelativeLayout ts2Layout;
    private RelativeLayout tr2Layout;
    private TextView scrambleText2;
    //endregion

    //region #RUNNABLES
    private Runnable startTimer = new Runnable() {
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;
            tr1.setText(Helper.millisToShortTime(elapsedTime));
            mHandler.post(this);
        }
    };
    //endregion

    //region #PREFERENCES
    private boolean SHOW_SCRAMBLE;
    //endregion

    public static TimerSplitFragment newInstance() {
        return new TimerSplitFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View vi = inflater.inflate(R.layout.fragment_timer_split, container, false);
        MainActivity.setFont((ViewGroup) vi);

        init(vi);
        bindViews();
        new UpdateScramble().execute();

        return vi;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(startTimer);
    }

    private void init(View vi) {
        activity = getActivity();
        SHOW_SCRAMBLE = SettingsFragment.showScramble(activity);
        scrambler = new Scrambler(activity);

        ts1 = (TextView) vi.findViewById(R.id.ts_1);
        ts1Layout = (RelativeLayout) vi.findViewById(R.id.ts_layout_1);
        tr1 = (TextView) vi.findViewById(R.id.tr_1);
        tr1Layout = (RelativeLayout) vi.findViewById(R.id.tr_layout_1);
        scrambleText1 = (TextView) vi.findViewById(R.id.scramble_text_1);

        ts2 = (TextView) vi.findViewById(R.id.ts_2);
        ts2Layout = (RelativeLayout) vi.findViewById(R.id.ts_layout_2);
        tr2 = (TextView) vi.findViewById(R.id.tr_2);
        tr2Layout = (RelativeLayout) vi.findViewById(R.id.tr_layout_2);
        scrambleText2 = (TextView) vi.findViewById(R.id.scramble_text_2);

        pagePicker = (TextView) activity.findViewById(R.id.page_picker);
    }

    private void bindViews() {
        //region #CONFIGURE ts1 BUTTON
        //endregion

        //region #CONFIGURE tr1 BUTTON
        //endregion
    }

    private void stopTiming(String text) {

    }

    private void startTiming() {

    }

    private class UpdateScramble extends AsyncTask<Void, Void, Spanned> {
        @Override
        protected Spanned doInBackground(Void... voids) {
            if (!SHOW_SCRAMBLE) cancel(true);
            return scrambler.getRandomScramble();
        }

        @Override
        protected void onPostExecute(Spanned scramble) {
            if (scrambleText1 == null) return;
            scrambleText1.setText(scramble);
            scrambleText2.setText(scramble);
        }
    }
}