package com.nhl.dflib.select;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.RowPredicate;
import com.nhl.dflib.builder.IntAccum;

public class RowIndexer {

    public static IntSeries index(DataFrame source, RowPredicate p) {

        IntAccum index = new IntAccum();
        int[] i = new int[1];
        source.forEach(rp -> {
            if (p.test(rp)) {
                index.pushInt(i[0]);
            }

            i[0]++;
        });

        return index.toSeries();
    }
}
