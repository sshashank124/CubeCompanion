package com.qbix.cubecompanion;

import android.app.Activity;
import android.text.Html;
import android.text.Spanned;

import org.kociemba.twophase.PruneTableLoader;
import org.kociemba.twophase.Search;
import org.kociemba.twophase.Tools;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Scrambler {

    //region #VARIABLES
    private Random rand = new Random();
    private int currPuzzle;
    //endregion

    //region #CONSTANTS
    private static final String[] MOVES = {"F", "B", "R", "L", "U", "D"};
    private static final String[] MOVES2 = {"F", "f", "B", "b", "R",  "r", "L", "l",
            "U", "u", "D", "d"};
    private static final String[] MOVES3 = {"F", "F<sub><small>2</small></sub>",
            "F<sub><small>3</small></sub>", "B", "B<sub><small>2</small></sub>",
            "B<sub><small>3</small></sub>", "R", "R<sub><small>2</small></sub>",
            "R<sub><small>3</small></sub>", "L", "L<sub><small>2</small></sub>",
            "L<sub><small>3</small></sub>", "U", "U<sub><small>2</small></sub>",
            "U<sub><small>3</small></sub>", "D", "D<sub><small>2</small></sub>",
            "D<sub><small>3</small></sub>"};
    private static final String[] MOVES4 = {"R", "B", "U", "L"};
    private static final String[] TYPES = {"&nbsp;", "'&nbsp;", "2&nbsp;"};
    //endregion

    public Scrambler(Activity activity) {
        currPuzzle = SettingsFragment.getPuzzleIndex(activity);
    }

    public Spanned getRandomScramble() {
        switch (currPuzzle) {
            case DBHandlerSolveTimes.P_2x2:
                return getRandomScramble2();
            case DBHandlerSolveTimes.P_3x3:
            case DBHandlerSolveTimes.P_3x3_BLD:
            case DBHandlerSolveTimes.P_3x3_OH:
                return getRandomScramble3();
            case DBHandlerSolveTimes.P_4x4:
            case DBHandlerSolveTimes.P_4x4_BLD:
            case DBHandlerSolveTimes.P_5x5:
            case DBHandlerSolveTimes.P_5x5_BLD:
                return getRandomScramble45();
            case DBHandlerSolveTimes.P_6x6:
            case DBHandlerSolveTimes.P_7x7:
                return getRandomScramble67();
            case DBHandlerSolveTimes.P_SQ_1:
                return getRandomScrambleSQ1();
            case DBHandlerSolveTimes.P_PYRAMX:
                return getRandomScramblePyraminx();
            case DBHandlerSolveTimes.P_MEGAMX:
                return getRandomScrambleMegaminx();
            case DBHandlerSolveTimes.P_SKEWB:
                return getRandomScrambleSkewb();
        }
        return Html.fromHtml("");
    }

    private Spanned getRandomScramble2() {
        int p2 = rand.nextInt(6);
        String scramble = MOVES[p2] + TYPES[rand.nextInt(3)];
        int p = (p2 + rand.nextInt(5) + 1) % 6;
        scramble += MOVES[p] + TYPES[rand.nextInt(3)];
        for (int i = 0; i < 9; ++i) {
            int c;
            if ((p/2) == (p2/2)) {
                c = (((p2 > p) ? p2 : p) + rand.nextInt(4) + 1) % 6;
            } else {
                c = (p + rand.nextInt(5) + 1) % 6;
            }
            scramble += MOVES[c] + TYPES[rand.nextInt(3)];
            p2 = p;
            p = c;
        }
        return Html.fromHtml(scramble.trim());
    }

    private Spanned getRandomScramble3() {
        if (PruneTableLoader.allLoaded())
            return Html.fromHtml(Search.solution(Tools.randomCube(), 25, 4, false));
        int p2 = rand.nextInt(6);
        String scramble = MOVES[p2] + TYPES[rand.nextInt(3)];
        int p = (p2 + rand.nextInt(5) + 1) % 6;
        scramble += MOVES[p] + TYPES[rand.nextInt(3)];
        for (int i = 0; i < 23; ++i) {
            int c;
            if ((p/2) == (p2/2)) {
                c = (((p2 > p) ? p2 : p) + rand.nextInt(4) + 1) % 6;
            } else {
                c = (p + rand.nextInt(5) + 1) % 6;
            }
            scramble += MOVES[c] + TYPES[rand.nextInt(3)];
            p2 = p;
            p = c;
        }
        return Html.fromHtml(scramble.trim());
    }

    private Spanned getRandomScramble45() {
        int scramble_length = 44;
        if (currPuzzle == DBHandlerSolveTimes.P_5x5) scramble_length = 59;
        Integer[] def = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        List<Integer> moves_ind = new LinkedList<>(Arrays.asList(def));
        int p = moves_ind.remove(rand.nextInt(moves_ind.size()));
        String scramble = MOVES2[p] + TYPES[rand.nextInt(3)];
        int t;
        for (int i = 0; i < scramble_length; ++i) {
            t = moves_ind.remove(rand.nextInt(moves_ind.size()));
            scramble += MOVES2[t] + TYPES[rand.nextInt(3)];
            if ((p/4) != (t/4)) {
                moves_ind = new LinkedList<>(Arrays.asList(def));
                moves_ind.remove(t);
            }
            p = t;
        }
        return Html.fromHtml(scramble.trim());
    }

    private Spanned getRandomScramble67() {
        int scramble_length = 64;
        if (currPuzzle == DBHandlerSolveTimes.P_5x5) scramble_length = 79;
        Integer[] def = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
        List<Integer> moves_ind = new LinkedList<>(Arrays.asList(def));
        int p = moves_ind.remove(rand.nextInt(moves_ind.size()));
        String scramble = MOVES3[p] + TYPES[rand.nextInt(3)];
        int t;
        for (int i = 0; i < scramble_length; ++i) {
            t = moves_ind.remove(rand.nextInt(moves_ind.size()));
            scramble += MOVES3[t] + TYPES[rand.nextInt(3)];
            if ((p/6) != (t/6)) {
                moves_ind = new LinkedList<>(Arrays.asList(def));
                moves_ind.remove(t);
            }
            p = t;
        }
        return Html.fromHtml(scramble.trim());
    }

    private Spanned getRandomScramblePyraminx() {
        int p = rand.nextInt(4);
        String scramble = MOVES4[p] + TYPES[rand.nextInt(2)];
        for (int i = 0; i < 12; ++i) {
            int c = (p + rand.nextInt(3) + 1) % 4;
            scramble += MOVES4[c] + TYPES[rand.nextInt(2)];
            p = c;
        }
        List<String> corners = new LinkedList<>(Arrays.asList("r", "b", "u", "l"));
        for (int i = 0; i < rand.nextInt(5); ++i) {
            scramble += corners.remove(rand.nextInt(corners.size())) + TYPES[rand.nextInt(2)];
        }
        return Html.fromHtml(scramble.trim());
    }

    private Spanned getRandomScrambleMegaminx() {
        String scramble = "";
        String[] types = {"++&nbsp;&nbsp;", "--&nbsp;&nbsp;"};
        for (int i = 0; i < 7; ++i) {
            for (int j = 0; j < 5; ++j) {
                scramble += "R"+types[rand.nextInt(2)];
                scramble += "D"+types[rand.nextInt(2)];
            }
            scramble += "U"+TYPES[rand.nextInt(2)]+"&nbsp;&nbsp;";
        }
        return Html.fromHtml(scramble.trim());
    }

    private Spanned getRandomScrambleSkewb() {
        int p = rand.nextInt(4);
        String scramble = MOVES[p] + TYPES[rand.nextInt(2)];
        for (int i = 0; i < 24; ++i) {
            int c = (p + rand.nextInt(3) + 1) % 4;
            scramble += MOVES[c] + TYPES[rand.nextInt(2)];
            p = c;
        }
        return Html.fromHtml(scramble.trim());
    }

    private Spanned getRandomScrambleSQ1() {
        String scramble = "";
        for (int i = 0; i < 16; ++i) {
            scramble += ""+(rand.nextInt(12)-5)+","+(rand.nextInt(12)-5)+"&nbsp;/&nbsp;";
        }
        return Html.fromHtml(scramble.trim());
    }
}