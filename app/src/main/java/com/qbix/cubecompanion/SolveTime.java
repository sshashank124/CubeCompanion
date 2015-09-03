package com.qbix.cubecompanion;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class SolveTime implements Comparable<SolveTime> {

    //region #VARIABLES
    private Long _id;
    private Long _time;
    private Long _date;
    private String _scramble;
    //endregion

    //region #CUSTOM SOLVE_TIME COMPARATORS
    public static Comparator<SolveTime> sortOldToNew =  new Comparator<SolveTime>() {
        public int compare(SolveTime st1, SolveTime st2) {
            return st1.getDate().compareTo(st2.getDate());
        }
    };
    public static Comparator<SolveTime> sortNewToOld =  new Comparator<SolveTime>() {
        public int compare(SolveTime st1, SolveTime st2) {
            return st2.getDate().compareTo(st1.getDate());
        }
    };
    public static Comparator<SolveTime> sortFastToSlow =  new Comparator<SolveTime>() {
        public int compare(SolveTime st1, SolveTime st2) {
            if (st1.getTime() == -2L && st2.getTime() != -2L) return 1;
            else if (st1.getTime() == -2L) return 0;
            else if (st2.getTime() == -2L) return -1;
            else return st1.getTime().compareTo(st2.getTime());
        }
    };
    public static Comparator<SolveTime> sortSlowToFast =  new Comparator<SolveTime>() {
        public int compare(SolveTime st1, SolveTime st2) {
            if (st2.getTime() == -2L && st1.getTime() != -2L) return 1;
            else if (st2.getTime() == -2L) return 0;
            else if (st1.getTime() == -2L) return -1;
            return st2.getTime().compareTo(st1.getTime());
        }
    };
    //endregion

    public SolveTime(Long id, Long time, Long date) {
        this._id = id;
        this._time = time;
        this._date = date;
    }

    public SolveTime(Long time, Long date, String scramble) {
        this._time = time;
        this._date = date;
        this._scramble = scramble;
    }

    public Long getID() { return this._id; }

    public Long getTime() { return this._time; }

    public void setTime(Long t) { this._time = t; }

    public Long getDate() { return this._date; }

    public String getScramble() {
        if (_scramble == null) return "No scramble recorded for this solve";
        else return _scramble;
    }

    public boolean isDNF() {
        return this._time == -2L;
    }

    public static String getFormattedTime(Long millis) {
        if (millis == -2L) return "DNF";
        if (millis == -1L) return "--:--.--";
        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        return String.format("%02d:%02d.%02d", minute, second, (millis / 10) % 100);
    }

    public String getFormattedTime() {
        return getFormattedTime(_time);
    }

    public String getLongFormattedDate() {
        if (this._date == null) return "--- --, ----";
        return new SimpleDateFormat("MMM dd, yyyy").format(new Date(this._date));
    }

    public String getShortFormattedDate() {
        return new SimpleDateFormat("MMM dd").format(new Date(this._date));
    }

    public static long getFirstTime(List<SolveTime> stl) {
        for(SolveTime st : stl) {
            if (st.getTime() != -2L) return st.getTime();
        }
        return -1L;
    }

    @Override
    public int compareTo(@NonNull SolveTime st) {
        return this.getID().compareTo(st.getID());
    }
}