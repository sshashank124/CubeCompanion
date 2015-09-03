package com.qbix.cubecompanion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolveTimeStatistics {

    //region #VARIABLES
    private List<SolveTime> stList;
    //endregion

    //region #CONSTANTS
    //endregion

    public SolveTimeStatistics(List<SolveTime> sl) {
        stList = sl;
    }

    public Long[] getBasicStats() {
        Long[] stats = new Long[6];
        stats[0] = (long) getTotalSolves();
        stats[1] = getOverallAverage();
        stats[2] = getExtreme(DBHandlerSolveTimes.MIN).getTime();
        stats[3] = getExtreme(DBHandlerSolveTimes.MAX).getTime();
        stats[4] = getAverageN(5, DBHandlerSolveTimes.LATEST);
        stats[5] = getAverageN(12, DBHandlerSolveTimes.LATEST);
        return stats;
    }

    public int getTotalSolves() {
        return stList.size();
    }

    public Long getOverallAverage() {
        if (stList.isEmpty()) return -1L;
        long sum = 0;
        int i = 0;
        for (SolveTime st : stList) {
            if (!st.isDNF()) {
                sum += st.getTime(); ++i;
            }
        }
        if (i == 0) return -1L;
        return sum / i;
    }

    public SolveTime getExtreme(int MAX_MIN) {
        if(stList.isEmpty()) return new SolveTime(-1L, null, (String) null);
        List<SolveTime> stTemp = new ArrayList<>(stList);
        switch (MAX_MIN) {
            case DBHandlerSolveTimes.MIN:
                Collections.sort(stTemp, SolveTime.sortFastToSlow);
                break;
            case DBHandlerSolveTimes.MAX:
                Collections.sort(stTemp, SolveTime.sortSlowToFast);
                while (stTemp.size() > 1 && stTemp.get(0).isDNF()) {
                    stTemp.remove(0);
                }
                break;
        }
        return stTemp.get(0);
    }

    public Long getAverageN(int n, int time) {
        if (stList.size() < n) return -1L;
        List<SolveTime> stTemp = new ArrayList<>(stList);
        switch (time) {
            case DBHandlerSolveTimes.LATEST:
                Collections.sort(stTemp, SolveTime.sortNewToOld);
                stTemp = stTemp.subList(0, n);
            case DBHandlerSolveTimes.BEST:
                Collections.sort(stTemp, SolveTime.sortFastToSlow);
                break;
            case DBHandlerSolveTimes.WORST:
                Collections.sort(stTemp, SolveTime.sortSlowToFast);
                break;
        }
        return getAverage(stTemp.subList(1, n - 1));
    }

    public Long getAverage(List<SolveTime> stl) {
        long sum = 0;
        for (SolveTime st : stl) {
            if (st.isDNF()) return -2L;
            sum += st.getTime();
        }
        return sum / stl.size();
    }

    public long[] getExponentialMovingAverages() {
        long first = SolveTime.getFirstTime(stList);
        if (first == -1L) return new long[] {-1L, -1L, -1L};
        double k0 = 2.0 / 101;
        double k1 = 2.0 / 1001;
        double k2 = 2.0 / (stList.size() + 1);
        long[] avgs = {first, first, first};
        for (SolveTime st : stList) {
            long l = st.getTime();
            if (l == -2L) continue;
            avgs[0] = avgs[0] + (long) (k0 * (l - avgs[0]));
            avgs[1] = avgs[1] + (long) (k1 * (l - avgs[1]));
            avgs[2] = avgs[2] + (long) (k2 * (l - avgs[2]));
        }
        return avgs;
    }

    public String getTotalSolveTime() {
        long seconds = 0;
        for (SolveTime st : stList) {
            if (st.getTime() != -2L) {
                seconds += st.getTime();
            }
        }
        seconds /= 1000;
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / 3600) % 24;
        long d = (seconds / (3600 * 24));
        return String.format("%d d, %02d h, %02d m, %02d s", d, h, m, s);
    }

    public Integer getExtremeSolvesInADay(int MAX_MIN) {
        Map<Integer, Integer> map = new HashMap<>();
        for (SolveTime st : stList) {
            int date = millisToDays(st.getDate());
            if (map.get(date) == null) {
                map.put(date, 1);
            } else {
                map.put(date, map.get(date) + 1);
            }
        }
        Integer returnEntry = null;
        switch (MAX_MIN) {
            case DBHandlerSolveTimes.MAX:
                for (Map.Entry<Integer, Integer> entry : map.entrySet())
                    if (returnEntry == null || entry.getValue() >= returnEntry)
                        returnEntry = entry.getValue();
                break;
            case DBHandlerSolveTimes.MIN:
                for (Map.Entry<Integer, Integer> entry : map.entrySet())
                    if (returnEntry == null || entry.getValue() <= returnEntry)
                        returnEntry = entry.getValue();
                break;
        }
        return returnEntry;
    }

    public String getAverageSolvesADay(boolean WITH_0) {
        if (stList.isEmpty()) return "0";
        List<SolveTime> stTemp = new ArrayList<>(stList);
        Collections.sort(stTemp, SolveTime.sortOldToNew);
        int days;
        if (WITH_0) {
            List<Long> uniqueDaysLong = new ArrayList<>();
            List<Integer> uniqueDays = new ArrayList<>();
            for (SolveTime st : stList) {
                if (!uniqueDays.contains(millisToDays(st.getDate()))) {
                    uniqueDaysLong.add(st.getDate());
                    uniqueDays.add(millisToDays(st.getDate()));
                }
            }
            days = uniqueDaysLong.size();
        } else {
            days = millisToDays(System.currentTimeMillis() - stTemp.get(0).getDate()) + 1;
        }
        return String.format("%.1f", stList.size() / (days * 1.0));
    }

    public int millisToDays(long millis) {
        return (int) (millis / (1000 * 60 * 60 * 24));
    }
}