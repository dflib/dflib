package org.dflib.sort;

// private utils for IntComparator
class IntComparators {

    public static <V extends Comparable<? super V>> int nullsLastCompare(V a, V b) {

        if (a == null) {
            return (b == null) ? 0 : 1;
        } else if (b == null) {
            return -1;
        } else {
            return a.compareTo(b);
        }
    }

    public static int[] sequence(int h) {
        int[] rn = new int[h];
        for (int i = 0; i < h; i++) {
            rn[i] = i;
        }

        return rn;
    }
}
