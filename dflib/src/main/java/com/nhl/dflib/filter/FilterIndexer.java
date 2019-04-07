package com.nhl.dflib.filter;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.collection.IntMutableList;
import com.nhl.dflib.series.IntSeries;

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

        return index.toSeries();
    }

    public static <T> IntSeries filteredIndex(Series<T> source, ValuePredicate<T> p) {
        IntMutableList index = new IntMutableList();

        int len = source.size();

        for (int i = 0; i < len; i++) {
            if (p.test(source.get(i))) {
                index.add(i);
            }
        }

        return index.toSeries();
    }
}
