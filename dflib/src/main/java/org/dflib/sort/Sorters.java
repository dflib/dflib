package org.dflib.sort;

import org.dflib.Sorter;

/**
 * @since 2.0.0
 */
public class Sorters {

    public static Sorter[] asSorters(String[] sorterExps) {
        int len = sorterExps.length;
        Sorter[] cs = new Sorter[len];

        for (int i = 0; i < len; i++) {
            cs[i] = Sorter.parseSorter(sorterExps[i]);
        }

        return cs;
    }
}
