package org.dflib.slice;

import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.builder.ValueAccum;
import org.dflib.builder.IntAccum;
import org.dflib.builder.ObjectAccum;

import java.util.Arrays;
import java.util.Iterator;

class ColumnExpander {

    private final Series<?> expanded;
    private final IntSeries stretchCounts;

    private ColumnExpander(Series<?> expanded, IntSeries stretchCounts) {
        this.expanded = expanded;
        this.stretchCounts = stretchCounts;
    }

    public Series<?> getExpanded() {
        return expanded;
    }

    public IntSeries getStretchCounts() {
        return stretchCounts;
    }

    public int[] getStretchIndex() {

        int shrunkLen = stretchCounts.size();
        int stretchLen = expanded.size();

        int[] stretchIndex = new int[stretchLen];

        for (int i = 0, si = 0; i < shrunkLen; i++) {
            int stretchBy = stretchCounts.getInt(i);
            if (stretchBy > 1) {
                Arrays.fill(stretchIndex, si, si + stretchBy, i);
                si += stretchBy;
            } else {
                stretchIndex[si++] = i;
            }
        }

        return stretchIndex;
    }

    // TODO: slow.. we check the type of each element. Disallow heterogeneous columns, and set the algorithm
    //   for the entire Series?
    public static ColumnExpander expand(Series<?> s) {

        int h = s.size();
        ValueAccum<Object> expanded = new ObjectAccum<>(h);
        IntAccum stretchCounts = new IntAccum(h);

        for (int i = 0; i < h; i++) {
            Object v = s.get(i);
            if (v == null) {
                expanded.push(null);
                stretchCounts.push(1);
            } else if (v instanceof Iterable) {
                explodeIterable(expanded, stretchCounts, (Iterable<?>) v);
            } else if (v.getClass().isArray()) {
                explodeArray(expanded, stretchCounts, v);
            }
            // scalar
            else {
                expanded.push(v);
                stretchCounts.push(1);
            }
        }

        return new ColumnExpander(expanded.toSeries(), stretchCounts.toSeries());
    }

    private static void explodeIterable(
            ValueAccum<Object> expanded,
            IntAccum stretchCounts,
            Iterable<?> iterable) {

        // empty iterable should generate a single null row
        Iterator<?> it = iterable.iterator();

        if (!it.hasNext()) {
            expanded.push(null);
            stretchCounts.push(1);
        } else {
            int c = 0;
            while (it.hasNext()) {
                expanded.push(it.next());
                c++;
            }

            stretchCounts.push(c);
        }
    }

    private static void explodeArray(
            ValueAccum<Object> expanded,
            IntAccum stretchCounts,
            Object array) {

        if (array instanceof Object[]) {
            Object[] a = (Object[]) array;

            // empty array should generate a single null row
            if (a.length == 0) {
                expanded.push(null);
                stretchCounts.push(1);
            } else {
                for (Object sv : a) {
                    expanded.push(sv);
                }

                stretchCounts.push(a.length);
            }
        } else if (array instanceof int[]) {
            int[] a = (int[]) array;

            // empty array should generate a single null row
            if (a.length == 0) {
                expanded.push(null);
                stretchCounts.push(1);
            } else {
                for (int sv : a) {
                    expanded.push(sv);
                }
                stretchCounts.push(a.length);
            }
        } else if (array instanceof double[]) {
            double[] a = (double[]) array;

            // empty array should generate a single null row
            if (a.length == 0) {
                expanded.push(null);
                stretchCounts.push(1);
            } else {
                for (double sv : a) {
                    expanded.push(sv);
                }
                stretchCounts.push(a.length);
            }
        } else if (array instanceof long[]) {
            long[] a = (long[]) array;

            // empty array should generate a single null row
            if (a.length == 0) {
                expanded.push(null);
                stretchCounts.push(1);
            } else {
                for (long sv : a) {
                    expanded.push(sv);
                }

                stretchCounts.push(a.length);
            }
        } else if (array instanceof boolean[]) {
            boolean[] a = (boolean[]) array;

            // empty array should generate a single null row
            if (a.length == 0) {
                expanded.push(null);
                stretchCounts.push(1);
            } else {
                for (boolean sv : a) {
                    expanded.push(sv);
                }

                stretchCounts.push(a.length);
            }
        } else if (array instanceof byte[]) {
            byte[] a = (byte[]) array;

            // empty array should generate a single null row
            if (a.length == 0) {
                expanded.push(null);
                stretchCounts.push(1);
            } else {
                for (byte sv : a) {
                    expanded.push(sv);
                }
                stretchCounts.push(a.length);
            }
        }
        // TODO: short[], float[]?
        else {
            throw new IllegalArgumentException("Unrecognized array type: " + array.getClass().getName());
        }
    }
}
