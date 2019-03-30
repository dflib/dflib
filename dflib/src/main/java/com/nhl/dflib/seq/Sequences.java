package com.nhl.dflib.seq;

public class Sequences {

    public static Integer[] numberSequence(int h) {
        Integer[] rn = new Integer[h];
        for (int i = 0; i < h; i++) {
            rn[i] = i;
        }

        return rn;
    }
}
