package org.dflib.set;

import org.dflib.BooleanSeries;
import org.dflib.DoubleSeries;
import org.dflib.IntSeries;
import org.dflib.LongSeries;
import org.dflib.Series;

import java.util.HashSet;
import java.util.Set;

public class Diff {

    public static <T> Series<T> diff(Series<T> s1, Series<? extends T> s2) {

        int l1 = s1.size();
        if (l1 == 0 || s2.size() == 0) {
            return s1;
        }

        Set<? extends T> s2Vals = s2.toSet();
        return s1.select(v -> !s2Vals.contains(v));
    }

    public static BooleanSeries diffBool(BooleanSeries s1, Series<? extends Boolean> s2) {

        int l1 = s1.size();
        if (l1 == 0 || s2.size() == 0) {
            return s1;
        }

        Set<Boolean> s2Vals = new HashSet<>(3);
        for (Boolean b : s2) {
            if (b != null) {
                s2Vals.add(b);
                if (s2Vals.size() == 2) {
                    break;
                }
            }
        }

        switch (s2Vals.size()) {
            case 0:
                return s1;
            case 1:
                Boolean b = s2Vals.iterator().next();
                return s1.select(v -> v != b);
            default:
                return Series.ofBool();
        }
    }

    public static IntSeries diffInt(IntSeries s1, Series<? extends Integer> s2) {

        int l1 = s1.size();
        if (l1 == 0 || s2.size() == 0) {
            return s1;
        }

        // TODO: need an IntSet to avoid int boxing (nulls can be ignored for this op)
        Set<? extends Integer> s2Vals = s2.toSet();
        return s1.selectInt(v -> !s2Vals.contains(v));
    }

    public static LongSeries diffLong(LongSeries s1, Series<? extends Long> s2) {

        int l1 = s1.size();
        if (l1 == 0 || s2.size() == 0) {
            return s1;
        }

        // TODO: need an LongSet to avoid int boxing (nulls can be ignored for this op)
        Set<? extends Long> s2Vals = s2.toSet();
        return s1.selectLong(v -> !s2Vals.contains(v));
    }

    public static DoubleSeries diffDouble(DoubleSeries s1, Series<? extends Double> s2) {

        int l1 = s1.size();
        if (l1 == 0 || s2.size() == 0) {
            return s1;
        }

        // TODO: need an LongSet to avoid int boxing (nulls can be ignored for this op)
        Set<? extends Double> s2Vals = s2.toSet();
        return s1.selectDouble(v -> !s2Vals.contains(v));
    }
}
