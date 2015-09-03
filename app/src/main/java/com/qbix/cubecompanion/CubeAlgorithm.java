package com.qbix.cubecompanion;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;

public class CubeAlgorithm {

    //region #VARIABLES
    private int id;
    public String algCase;
    public Integer resId;
    public LinkedList<String> alg;
    public String alg_desc;
    //endregion

    public CubeAlgorithm(int i, String ac, Integer rId, String t1[], String t2) {
        id = i;
        algCase = ac;
        resId = rId;
        if (t1 == null) alg = null;
        else alg = new LinkedList<>(Arrays.asList(t1));
        alg_desc = t2;
    }

    public int getId() { return id; }

    public String getAlg() {
        if (alg == null) return null;
        return alg.getFirst();
    }

    public String getNextAlg() {
        moveFirstToLast();
        return getAlg();
    }

    public String getSerializedAlgList() {
        if (alg == null) return null;
        ListIterator<String> a = alg.listIterator();
        String s = a.next();
        while (a.hasNext()) { s += "|"+a.next(); }
        return s;
    }

    public void moveFirstToLast() {
        if (alg == null) return;
        alg.addLast(alg.removeFirst());
    }
}