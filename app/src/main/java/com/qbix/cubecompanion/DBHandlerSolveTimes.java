package com.qbix.cubecompanion;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHandlerSolveTimes extends SQLiteOpenHelper {

    //region #CONSTANTS
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "solves.db";

    private static final String KEY_ID = "id";
    private static final String KEY_TIME = "time";
    private static final String KEY_DATE = "date";
    private static final String KEY_SCRAMBLE = "scramble";

    public static final String[] TABLE_PUZZLE_TIMES = {"puzzle2", "puzzle3", "puzzle3OH",
            "puzzle3BLD", "puzzle4", "puzzle4BLD", "puzzle5", "puzzle5BLD", "puzzle6",
            "puzzle7", "puzzleSQ1", "puzzlePRMX", "puzzleMGMX", "puzzleSKEWB"};

    public static final String[] PUZZLE_NAMES = {"2 X 2", "3 X 3", "3 X 3 One Handed",
            "3 X 3 Blindfolded", "4 X 4", "4 X 4 Blindfolded", "5 X 5", "5 X 5 Blindfolded",
            "6 X 6", "7 X 7", "Square-1", "Pyraminx", "Megaminx", "Skewb"};

    public static final int P_2x2 = 0;
    public static final int P_3x3 = 1;
    public static final int P_3x3_OH = 2;
    public static final int P_3x3_BLD = 3;
    public static final int P_4x4 = 4;
    public static final int P_4x4_BLD = 5;
    public static final int P_5x5 = 6;
    public static final int P_5x5_BLD = 7;
    public static final int P_6x6 = 8;
    public static final int P_7x7 = 9;
    public static final int P_SQ_1 = 10;
    public static final int P_PYRAMX = 11;
    public static final int P_MEGAMX = 12;
    public static final int P_SKEWB = 13;

    public static final int MAX = 0;
    public static final int MIN = 1;
    public static final int LATEST = 0;
    public static final int BEST = 1;
    public static final int WORST = 2;

    private static DBHandlerSolveTimes mInstance = null;
    private static int CURRENT_TABLE;
    //endregion

    public static DBHandlerSolveTimes getInstance(Activity activity) {
        if (mInstance == null) {
            mInstance = new DBHandlerSolveTimes(activity.getApplicationContext());
        }
        CURRENT_TABLE = activity.getPreferences(Context.MODE_PRIVATE)
                .getInt(SettingsFragment.CURRENT_TABLE, SettingsFragment.CT_DEFAULT);
        return mInstance;
    }

    public DBHandlerSolveTimes(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String puzzle_table : TABLE_PUZZLE_TIMES) {
            db.execSQL(String.format(
                    "CREATE TABLE IF NOT EXISTS %s(%s INTEGER PRIMARY KEY,%s INTEGER,%s INTEGER" +
                            ",%s TEXT)",
                    puzzle_table, KEY_ID, KEY_TIME, KEY_DATE, KEY_SCRAMBLE));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3 && newVersion == 3)
        for (String puzzle_table : TABLE_PUZZLE_TIMES) {
            db.execSQL(String.format(
                    "ALTER TABLE %s ADD COLUMN %s TEXT",
                    puzzle_table, KEY_SCRAMBLE));
        }
        onCreate(db);
    }

    public long addSolveTime(SolveTime st) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TIME, st.getTime());
        values.put(KEY_DATE, st.getDate());
        values.put(KEY_SCRAMBLE, st.getScramble());

        long id = db.insert(TABLE_PUZZLE_TIMES[CURRENT_TABLE], null, values);
        db.close();
        return id;
    }

    public void modifySolveTime(SolveTime st) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TIME, st.getTime());
        values.put(KEY_DATE, st.getDate());

        db.update(TABLE_PUZZLE_TIMES[CURRENT_TABLE], values, KEY_ID + "=" + st.getID(), null);
        db.close();
    }

    public void deleteSolveTime(SolveTime st) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PUZZLE_TIMES[CURRENT_TABLE], KEY_ID + "=?",
                new String[]{String.valueOf(st.getID())});
        db.close();
    }

    public List<SolveTime> getAllSolveTimes() {
        return getAllSolveTimesAfter(0L);
    }

    public List<SolveTime> getAllSolveTimesAfter(Long date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(String.format("SELECT %s,%s,%s FROM %s WHERE %s > %d",
                KEY_ID, KEY_TIME, KEY_DATE, TABLE_PUZZLE_TIMES[CURRENT_TABLE], KEY_DATE, date)
                , null);

        List<SolveTime> solveTimesList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                solveTimesList.add(new SolveTime(
                        cursor.getLong(0),
                        cursor.getLong(1),
                        cursor.getLong(2)));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return solveTimesList;
    }

    public String getScramble(Long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(String.format("SELECT %s FROM %s WHERE %s = %d",
                KEY_SCRAMBLE, TABLE_PUZZLE_TIMES[CURRENT_TABLE], KEY_ID, id), null);

        if (!cursor.moveToFirst()) return null;
        String scramble = cursor.getString(0);
        cursor.close();
        return scramble;
    }

    public void deleteAllSolveTimes() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PUZZLE_TIMES[CURRENT_TABLE], null, null);
    }
}