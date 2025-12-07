package org.dflib.set;

import org.dflib.BooleanSeries;
import org.dflib.DoubleSeries;
import org.dflib.FloatSeries;
import org.dflib.IntSeries;
import org.dflib.LongSeries;
import org.dflib.Series;

import java.util.HashSet;
import java.util.Set;

public class Intersect {

    public static <T> Series<T> intersect(Series<T> s1, Series<? extends T> s2) {

        int l1 = s1.size();
        if (l1 == 0) {
            return s1;
        } else if (s2.size() == 0) {
            return (Series<T>) s2;
        }

        Set<? extends T> s2Vals = s2.toSet();
        return s1.select(s2Vals::contains);
    }

    public static BooleanSeries intersectBool(BooleanSeries s1, Series<? extends Boolean> s2) {

        int l1 = s1.size();
        if (l1 == 0) {
            return s1;
        } else if (s2.size() == 0) {
            return Series.ofBool();
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

        return switch (s2Vals.size()) {
            case 0 -> Series.ofBool();
            case 1 -> s1.select(v -> v == s2Vals.iterator().next());
            default -> s1;
        };
    }

    public static IntSeries intersectInt(IntSeries s1, Series<? extends Integer> s2) {

        int l1 = s1.size();
        if (l1 == 0) {
            return s1;
        } else if (s2.size() == 0) {
            return Series.ofInt();
        }

        Set<? extends Integer> s2Vals = s2.toSet();
        return s1.selectInt(s2Vals::contains);
    }

    public static LongSeries intersectLong(LongSeries s1, Series<? extends Long> s2) {

        int l1 = s1.size();
        if (l1 == 0) {
            return s1;
        } else if (s2.size() == 0) {
            return Series.ofLong();
        }

        Set<? extends Long> s2Vals = s2.toSet();
        return s1.selectLong(s2Vals::contains);
    }

    /**
     * @since 1.1.0
     */
    public static FloatSeries intersectFloat(FloatSeries s1, Series<? extends Float> s2) {

        int l1 = s1.size();
        if (l1 == 0) {
            return s1;
        } else if (s2.size() == 0) {
            return Series.ofFloat();
        }

        Set<? extends Float> s2Vals = s2.toSet();
        return s1.selectFloat(s2Vals::contains);
    }

    public static DoubleSeries intersectDouble(DoubleSeries s1, Series<? extends Double> s2) {

        int l1 = s1.size();
        if (l1 == 0) {
            return s1;
        } else if (s2.size() == 0) {
            return Series.ofDouble();
        }

        Set<? extends Double> s2Vals = s2.toSet();
        return s1.selectDouble(s2Vals::contains);
    }
}
