package com.nhl.dflib.column.sort;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.series.ListSeries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ColumnarSortIndexer {

    public static Series<Integer> sortedIndex(DataFrame source, Comparator<Integer> indexComparator) {

        List<Integer> index = new ArrayList<>();
        int[] i = new int[1];
        source.forEach(rp -> index.add(i[0]++));

        Collections.sort(index, indexComparator);

        return new ListSeries<>(index);
    }
}