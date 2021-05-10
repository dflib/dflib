package com.nhl.dflib.select;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.RowPredicate;
import com.nhl.dflib.accumulator.IntAccumulator;

public class RowIndexer {

    public static IntSeries index(DataFrame source, RowPredicate p) {

        IntAccumulator index = new IntAccumulator();
        int[] i = new int[1];
        source.forEach(rp -> {
            if (p.test(rp)) {
                index.addInt(i[0]);
            }

            i[0]++;
        });

        return index.toSeries();
    }
}
