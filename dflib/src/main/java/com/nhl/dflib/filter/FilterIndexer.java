package com.nhl.dflib.filter;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.series.ListSeries;

import java.util.ArrayList;
import java.util.List;

public class FilterIndexer {

    public static Series<Integer> filteredIndex(DataFrame source, RowPredicate p) {

        List<Integer> index = new ArrayList<>();
        int[] i = new int[1];
        source.forEach(rp -> {
            if (p.test(rp)) {
                index.add(i[0]);
            }

            i[0]++;
        });

        return new ListSeries<>(index);
    }

    public static <T> Series<Integer> filteredIndex(Series<T> source, ValuePredicate<T> p) {
        List<Integer> index = new ArrayList<>();

        int len = source.size();

        for (int i = 0; i < len; i++) {
            if (p.test(source.get(i))) {
                index.add(i);
            }
        }

        return new ListSeries<>(index);
    }
}
