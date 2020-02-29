package com.nhl.dflib.filter;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.RowPredicate;
import com.nhl.dflib.accumulator.IntAccumulator;

public class FilterIndexer {

    public static IntSeries filteredIndex(DataFrame source, RowPredicate p) {

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
