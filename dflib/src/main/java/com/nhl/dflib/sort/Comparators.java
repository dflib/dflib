package com.nhl.dflib.sort;

class Comparators {

    static <V extends Comparable<? super V>> int nullsLastCompare(V a, V b) {

        if (a == null) {
            return (b == null) ? 0 : 1;
        } else if (b == null) {
            return -1;
        } else {
            return a.compareTo(b);
        }
    }

    static <V extends Comparable<? super V>> int nullsFirstCompare(V a, V b) {

        if (a == null) {
            return (b == null) ? 0 : -1;
        } else if (b == null) {
            return 1;
        } else {
            return a.compareTo(b);
        }
    }
}
