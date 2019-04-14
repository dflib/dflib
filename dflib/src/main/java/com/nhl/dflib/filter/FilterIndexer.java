package com.nhl.dflib.filter;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.collection.IntMutableList;

public class FilterIndexer {

    public static IntSeries filteredIndex(DataFrame source, RowPredicate p) {

        IntMutableList index = new IntMutableList();
        int[] i = new int[1];
        source.forEach(rp -> {
            if (p.test(rp)) {
                index.add(i[0]);
            }

            i[0]++;
        });

        return index.toIntSeries();
    }
}
