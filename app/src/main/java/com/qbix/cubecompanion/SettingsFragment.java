package com.qbix.cubecompanion;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingsFragment extends Fragment {

    //region #VARIABLES
    private SharedPreferences settings;

    private LinearLayout puzzleType;
    private TextView puzzleTypeText;
    private LinearLayout msimType;
    private TextView msimTypeText;
    private SeekBar msimCount;
    private TextView msimCountText;
    private CheckBox deleteConfirmation;
    private CheckBox showScramble;
    private CheckBox startTimerDelay;
    private CheckBox inspectionTime;
    private CheckBox exitConfirmation;

    private Activity activity;
    //endregion

    //region #CONSTANTS
    public static final String CURRENT_TABLE = "currentPuzzleTable";
    public static final int CT_DEFAULT = DBHandlerSolveTimes.P_3x3;

    public static final String CONFIRM_DELETE = "deleteConfirmation";
    public static final boolean CD_DEFAULT = true;

    public static final String SHOW_SCRAMBLE = "showScramble";
    public static final boolean SS_DEFAULT = true;

    public static final String INSPECTION_TIME = "inspectionTime";
    public static final boolean IT_DEFAULT = false;

    public static final String TIMER_DELAY = "startTimerDelay";
    public static final int TD_DEFAULT = 500;

    public static final String CONFIRM_EXIT = "exitConfirmation";
    public static final boolean CE_DEFAULT = true;

    public static final String SESSION_START_TIME_ROOT = "sessionStartTime_";
    public static final long SSTR_DEFAULT = 0L;

    public static final String MULTI_STEP_INPUT_METHOD = "timerMultiStepInputMethod";
    public static final int MSIM_DEFAULT = TimerMultiStepFragment.MANUAL;
    public static final String MSIM_COUNT = "timerMSIMCount";
    public static final int MSIMC_DEFAULT = 4;

    public static final String SAVED_PAGE = "savedPage";
    //endregion

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View vi = inflater.inflate(R.layout.fragment_settings, container, false);
        MainActivity.setFont((ViewGroup) vi);

        activity = getActivity();
        settings = activity.getPreferences(Context.MODE_PRIVATE);

        findViews(vi);
        bindViews();

        return vi;
    }

    public void findViews(View vi) {
        puzzleType = (LinearLayout) vi.findViewById(R.id.settings_puzzle_type);
        puzzleTypeText = (TextView) puzzleType.findViewById(R.id.settings_puzzle_text);
        msimType = (LinearLayout) vi.findViewById(R.id.settings_msim);
        msimTypeText = (TextView) msimType.findViewById(R.id.settings_msim_text);
        msimCount = (SeekBar) vi.findViewById(R.id.settings_msim_count);
        msimCountText = (TextView) vi.findViewById(R.id.settings_msim_count_text);
        deleteConfirmation = (CheckBox) vi.findViewById(R.id.settings_delete_confirmation);
        showScramble = (CheckBox) vi.findViewById(R.id.settings_show_scramble);
        startTimerDelay = (CheckBox) vi.findViewById(R.id.settings_timer_start_delay);
        inspectionTime = (CheckBox) vi.findViewById(R.id.settings_timer_inspection_time);
        exitConfirmation = (CheckBox) vi.findViewById(R.id.settings_exit_confirmation);
    }

    public void bindViews() {
        puzzleTypeText.setText(DBHandlerSolveTimes
                .PUZZLE_NAMES[settings.getInt(CURRENT_TABLE, CT_DEFAULT)]);
        msimTypeText.setText(TimerMultiStepFragment.inputTypesTexts[getMSIMType(activity)]);
        int mSIMCount = getMSIMCount(activity);
        msimCount.setProgress(mSIMCount-1);
        msimCountText.setText("" + mSIMCount);
        deleteConfirmation.setChecked(settings.getBoolean(CONFIRM_DELETE, CD_DEFAULT));
        showScramble.setChecked(settings.getBoolean(SHOW_SCRAMBLE, SS_DEFAULT));
        startTimerDelay.setChecked(settings.getInt(TIMER_DELAY, TD_DEFAULT) > 1);
        inspectionTime.setChecked(settings.getBoolean(INSPECTION_TIME, IT_DEFAULT));
        exitConfirmation.setChecked(settings.getBoolean(CONFIRM_EXIT, CE_DEFAULT));

        puzzleType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupDialog pd = new PopupDialog(activity, "Select Puzzle",
                        DBHandlerSolveTimes.PUZZLE_NAMES);
                final AlertDialog ad = pd.getDialog();
                pd.options.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        settings.edit().putInt(CURRENT_TABLE, position).apply();
                        puzzleTypeText.setText(DBHandlerSolveTimes.PUZZLE_NAMES[position]);
                        ad.dismiss();
                    }
                });
                ad.show();
            }
        });

        msimType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupDialog pd = new PopupDialog(activity, "Multi-step Timer Input Method",
                        TimerMultiStepFragment.inputTypesTexts);
                final AlertDialog ad = pd.getDialog();
                pd.options.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        settings.edit().putInt(MULTI_STEP_INPUT_METHOD, position).apply();
                        msimTypeText.setText(TimerMultiStepFragment.inputTypesTexts[position]);
                        ad.dismiss();
                    }
                });
                ad.show();
            }
        });

        msimCount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                settings.edit().putInt(MSIM_COUNT, progress + 1).apply();
                msimCountText.setText("" + (progress + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        deleteConfirmation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.edit().putBoolean(CONFIRM_DELETE, isChecked).apply();
            }
        });

        showScramble.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.edit().putBoolean(SHOW_SCRAMBLE, isChecked).apply();
            }
        });

        startTimerDelay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    settings.edit().putInt(TIMER_DELAY, TD_DEFAULT).apply();
                } else {
                    settings.edit().putInt(TIMER_DELAY, 1).apply();
                }
            }
        });

        inspectionTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.edit().putBoolean(INSPECTION_TIME, isChecked).apply();
            }
        });

        exitConfirmation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.edit().putBoolean(CONFIRM_EXIT, isChecked).apply();
            }
        });
    }

    public static int getPuzzleIndex(Activity activity) {
        return activity.getPreferences(Context.MODE_PRIVATE)
                .getInt(CURRENT_TABLE, DBHandlerSolveTimes.P_3x3);
    }

    public static String getPuzzle(Activity activity) {
        return DBHandlerSolveTimes.PUZZLE_NAMES[getPuzzleIndex(activity)];
    }

    public static boolean confirmBeforeDelete(Activity activity) {
        return activity.getPreferences(Context.MODE_PRIVATE).getBoolean(CONFIRM_DELETE, CD_DEFAULT);
    }

    public static boolean showScramble(Activity activity) {
        return activity.getPreferences(Context.MODE_PRIVATE).getBoolean(SHOW_SCRAMBLE, SS_DEFAULT);
    }

    public static int getTimerDelay(Activity activity) {
        return activity.getPreferences(Context.MODE_PRIVATE).getInt(TIMER_DELAY, TD_DEFAULT);
    }

    public static boolean useInspectionTime(Activity activity) {
        return activity.getPreferences(Context.MODE_PRIVATE)
                .getBoolean(INSPECTION_TIME, IT_DEFAULT);
    }

    public static boolean confirmBeforeExit(Activity activity) {
        return activity.getPreferences(Context.MODE_PRIVATE).getBoolean(CONFIRM_EXIT, CE_DEFAULT);
    }

    public static long getSessionStartTime(Activity activity) {
        return activity.getPreferences(Context.MODE_PRIVATE)
                .getLong(SESSION_START_TIME_ROOT + getPuzzleIndex(activity), SSTR_DEFAULT);
    }

    public static long resetSessionStartTime(Activity activity) {
        long now = System.currentTimeMillis();
        activity.getPreferences(Context.MODE_PRIVATE).edit()
                .putLong(SESSION_START_TIME_ROOT+getPuzzleIndex(activity), now).commit();
        return now;
    }

    public static int getMSIMType(Activity activity) {
        return activity.getPreferences(Context.MODE_PRIVATE)
                .getInt(MULTI_STEP_INPUT_METHOD, MSIM_DEFAULT);
    }

    public static int getMSIMCount(Activity activity) {
        return activity.getPreferences(Context.MODE_PRIVATE)
                .getInt(MSIM_COUNT, MSIMC_DEFAULT);
    }
}