package com.qbix.cubecompanion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;


public class DBHandlerAlgorithms extends SQLiteAssetHelper {

    //region #CONSTANTS
    private static final String DATABASE_NAME = "algs.db";
    private static final int DATABASE_VERSION = 6;

    private static final String KEY_ID = "id";
    //private static final String KEY_CASE = "case";
    //private static final String KEY_ALG_RES_ID = "algResId";
    private static final String KEY_ALG = "alg";
    private static final String KEY_ALG_TYPE = "algType";
    private static final String KEY_ALG_CODE = "algCode";

    private static final String TABLE_BEG_ALGS = "begAlgs";

    private static final String TABLE_CMLL_ALGS = "cmllAlgs";
    private static final String TABLE_COLL_ALGS = "collALgs";
    private static final String TABLE_ELL_ALGS = "ellAlgs";
    private static final String TABLE_F2L_ALGS = "f2lAlgs";
    private static final String TABLE_OLL_ALGS = "ollAlgs";
    private static final String TABLE_PLL_ALGS = "pllAlgs";
    private static final String TABLE_WVLS_ALGS = "wvlsAlgs";
    private static final String TABLE_CLL_ALGS = "cllAlgs";
    private static final String TABLE_EG1_ALGS = "eg1Algs";
    private static final String TABLE_EG2_ALGS = "eg2Algs";
    private static final String TABLE_ORT_OLL_ALGS = "ollOrtAlgs";
    private static final String TABLE_ORT_PBL_ALGS = "pblOrtAlgs";

    private static final String[] ALG_TABLES = new String[AlgorithmsFragment.NUM_PAGES];
    static {
        ALG_TABLES[AlgorithmsFragment.CMLL] = TABLE_CMLL_ALGS;
        ALG_TABLES[AlgorithmsFragment.COLL] = TABLE_COLL_ALGS;
        ALG_TABLES[AlgorithmsFragment.ELL] = TABLE_ELL_ALGS;
        ALG_TABLES[AlgorithmsFragment.F2L] = TABLE_F2L_ALGS;
        ALG_TABLES[AlgorithmsFragment.OLL] = TABLE_OLL_ALGS;
        ALG_TABLES[AlgorithmsFragment.PLL] = TABLE_PLL_ALGS;
        ALG_TABLES[AlgorithmsFragment.WVLS] = TABLE_WVLS_ALGS;
        ALG_TABLES[AlgorithmsFragment.CLL] = TABLE_CLL_ALGS;
        ALG_TABLES[AlgorithmsFragment.EG1] = TABLE_EG1_ALGS;
        ALG_TABLES[AlgorithmsFragment.EG2] = TABLE_EG2_ALGS;
        ALG_TABLES[AlgorithmsFragment.ORT_OLL] = TABLE_ORT_OLL_ALGS;
        ALG_TABLES[AlgorithmsFragment.ORT_PBL] = TABLE_ORT_PBL_ALGS;
    }

    private static DBHandlerAlgorithms mInstance = null;
    //endregion

    public static DBHandlerAlgorithms getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DBHandlerAlgorithms(ctx.getApplicationContext());
        }
        return mInstance;
    }

    public DBHandlerAlgorithms(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
    }

    public List<CubeAlgorithm> getBegStepNAlgs(int n) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(String.format("SELECT * FROM %s WHERE %s = %d ORDER BY %s",
                TABLE_BEG_ALGS, KEY_ALG_TYPE, n, KEY_ID), null);

        List<CubeAlgorithm> algs = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Integer resId = null;
                try {
                    resId = R.drawable.class.getField(cursor.getString(1)).getInt(null);
                }
                catch (Exception e) { e.printStackTrace(); }
                algs.add(new CubeAlgorithm(cursor.getInt(0), null, resId,
                        getAlgList(cursor.getString(2)), cursor.getString(3)));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return algs;
    }

    public List<CubeAlgorithm> getAllPageAlgs(int page) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(String.format("SELECT * FROM %s ORDER BY %s",
                ALG_TABLES[page], KEY_ID), null);

        List<CubeAlgorithm> algs = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Integer resId = null;
                try {
                    resId = R.drawable.class.getField(cursor.getString(2)).getInt(null);
                }
                catch (Exception e) { e.printStackTrace(); }
                algs.add(new CubeAlgorithm(cursor.getInt(0), cursor.getString(1), resId,
                        getAlgList(cursor.getString(3)), null));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return algs;
    }

    public List<CubeAlgorithm> getSpecificGroupAlgs(int page, int group) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(String.format("SELECT * FROM %s WHERE %s=%s ORDER BY %s",
                ALG_TABLES[page], KEY_ALG_CODE, group, KEY_ID), null);

        List<CubeAlgorithm> algs = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Integer resId = null;
                try {
                    resId = R.drawable.class.getField(cursor.getString(2)).getInt(null);
                }
                catch (Exception e) { e.printStackTrace(); }
                algs.add(new CubeAlgorithm(cursor.getInt(0), cursor.getString(1), resId,
                        getAlgList(cursor.getString(3)), null));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return algs;
    }

    public List<CubeAlgorithm> getAllOLLAlgs() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(String.format("SELECT * FROM %s ORDER BY %s",
                TABLE_OLL_ALGS, KEY_ID), null);

        List<CubeAlgorithm> algs = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Integer resId = null;
                try {
                    resId = R.drawable.class.getField(cursor.getString(1)).getInt(null);
                }
                catch (Exception e) { e.printStackTrace(); }
                algs.add(new CubeAlgorithm(cursor.getInt(0), cursor.getString(0), resId,
                        getAlgList(cursor.getString(2)), null));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return algs;
    }

    public List<CubeAlgorithm> getOLLAlgs(String algCode) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = String.format("SELECT * FROM %s WHERE %s = '%s' ORDER BY %s",
                TABLE_OLL_ALGS, KEY_ALG_CODE, algCode, KEY_ID);

        Cursor cursor = db.rawQuery(query, null);

        List<CubeAlgorithm> algs = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Integer resId = null;
                try {
                    resId = R.drawable.class.getField(cursor.getString(1)).getInt(null);
                }
                catch (Exception e) { e.printStackTrace(); }
                algs.add(new CubeAlgorithm(cursor.getInt(0), cursor.getString(0), resId,
                        getAlgList(cursor.getString(2)), null));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return algs;
    }

    public void updateAlgorithms(int page, List<CubeAlgorithm> algs) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values;

        for (CubeAlgorithm ca : algs) {
            values = new ContentValues();
            values.put(KEY_ALG, ca.getSerializedAlgList());
            db.update(ALG_TABLES[page], values, KEY_ID + "=" + ca.getId(), null);
        }
    }

    public String[] getAlgList(String s) {
        if (s == null) return null;
        return s.split("\\|");
    }
}