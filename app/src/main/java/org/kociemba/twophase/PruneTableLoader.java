package org.kociemba.twophase;

import android.content.res.Resources;
import com.qbix.cubecompanion.R;
import java.io.ObjectInputStream;

import static org.kociemba.twophase.CoordCube.*;

public class PruneTableLoader {
    private int tablesLoaded;
    private Resources res;
    private ObjectInputStream dataReader;

    //region #CONSTANTS
    private static final int NUM_TABLES = 12;
    private static final int FILE_TWIST = R.raw.twist_moves;
    private static final int FILE_FLIP = R.raw.flip_moves;
    private static final int FILE_FR_BR = R.raw.fr_br_moves;
    private static final int FILE_URF_DLF = R.raw.urf_dlf_moves;
    private static final int FILE_UR_DF = R.raw.ur_df_moves;
    private static final int FILE_UR_UL = R.raw.ur_ul_moves;
    private static final int FILE_UB_DF = R.raw.ub_df_moves;
    private static final int FILE_MERGE = R.raw.merge;
    private static final int FILE_URF_DLF_PARITY = R.raw.urf_dlf_prune;
    private static final int FILE_UR_DF_PARITY = R.raw.ur_df_prune;
    private static final int FILE_SLICE_TWIST = R.raw.slice_twist_prune;
    private static final int FILE_SLICE_FLIP = R.raw.slice_flip_prune;
    //endregion #CONSTANTS

    public PruneTableLoader(Resources r) {
        res = r;
        tablesLoaded = 0;
    }

    public static boolean allLoaded() {
        return Slice_Flip_Prun != null;
    }

    public void loadNext() { loadNext(false); }

    public void loadNext(boolean force) {
        switch(tablesLoaded++) {
            case 0:  loadTwistMoves(force);		 		 break;
            case 1:  loadFlipMoves(force);  		 	 break;
            case 2:  loadFRtoBRMoves(force);		 	 break;
            case 3:  loadURFtoDLFMoves(force);	 		 break;
            case 4:  loadURtoDFMoves(force);		 	 break;
            case 5:  loadURtoULMoves(force);		 	 break;
            case 6:  loadUBtoDFMoves(force);		 	 break;
            case 7:  mergeURtoULandUBtoDF(force); 		 break;
            case 8:  loadSliceURFtoDLFParityPrun(force); break;
            case 9:  loadSliceURtoDFParityPrun(force);	 break;
            case 10: loadSliceTwistPrune(force);		 break;
            case 11: loadSliceFlipPrune(force);			 break;
            default: break;
        }
    }

    public boolean loadingFinished() {
        return tablesLoaded >= NUM_TABLES;
    }

    private void loadTwistMoves(boolean force) {
        if (!force && twistMove != null)
            return;

        try {
            dataReader = new ObjectInputStream(res.openRawResource(FILE_TWIST));
            twistMove = (short[][]) dataReader.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFlipMoves(boolean force) {
        if (!force && flipMove != null)
            return;

        try {
            dataReader = new ObjectInputStream(res.openRawResource(FILE_FLIP));
            flipMove = (short[][]) dataReader.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFRtoBRMoves(boolean force) {
        if (!force && FRtoBR_Move != null)
            return;

        try {
            dataReader = new ObjectInputStream(res.openRawResource(FILE_FR_BR));
            FRtoBR_Move = (short[][]) dataReader.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadURFtoDLFMoves(boolean force) {
        if (!force && URFtoDLF_Move != null)
            return;

        try {
            dataReader = new ObjectInputStream(res.openRawResource(FILE_URF_DLF));
            URFtoDLF_Move = (short[][]) dataReader.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadURtoDFMoves(boolean force) {
        if (!force && URtoDF_Move != null)
            return;

        try {
            dataReader = new ObjectInputStream(res.openRawResource(FILE_UR_DF));
            URtoDF_Move = (short[][]) dataReader.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadURtoULMoves(boolean force) {
        if (!force && URtoUL_Move != null)
            return;

        try {
            dataReader = new ObjectInputStream(res.openRawResource(FILE_UR_UL));
            URtoUL_Move = (short[][]) dataReader.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUBtoDFMoves(boolean force) {
        if (!force && UBtoDF_Move != null)
            return;

        try {
            dataReader = new ObjectInputStream(res.openRawResource(FILE_UB_DF));
            UBtoDF_Move = (short[][]) dataReader.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mergeURtoULandUBtoDF(boolean force) {
        if (!force && MergeURtoULandUBtoDF != null)
            return;

        try {
            dataReader = new ObjectInputStream(res.openRawResource(FILE_MERGE));
            MergeURtoULandUBtoDF = (short[][]) dataReader.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSliceURFtoDLFParityPrun(boolean force) {
        if (!force && Slice_URFtoDLF_Parity_Prun != null)
            return;

        try {
            dataReader = new ObjectInputStream(res.openRawResource(FILE_URF_DLF_PARITY));
            Slice_URFtoDLF_Parity_Prun = (byte[]) dataReader.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSliceURtoDFParityPrun(boolean force) {
        if (!force && Slice_URtoDF_Parity_Prun != null)
            return;

        try {
            dataReader = new ObjectInputStream(res.openRawResource(FILE_UR_DF_PARITY));
            Slice_URtoDF_Parity_Prun = (byte[]) dataReader.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSliceTwistPrune(boolean force) {
        if (!force && Slice_Twist_Prun != null)
            return;

        try {
            dataReader = new ObjectInputStream(res.openRawResource(FILE_SLICE_TWIST));
            Slice_Twist_Prun = (byte[]) dataReader.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSliceFlipPrune(boolean force) {
        if (!force && Slice_Flip_Prun != null)
            return;

        try {
            dataReader = new ObjectInputStream(res.openRawResource(FILE_SLICE_FLIP));
            Slice_Flip_Prun = (byte[]) dataReader.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}