package com.nhl.dflib.window;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.row.DataFrameRowProxy;
import com.nhl.dflib.row.RowProxy;
import com.nhl.dflib.sort.IndexSorter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

/**
 * @since 0.8
 */
public class Ranker {

    private Comparator<RowProxy> sorter;

    public Ranker(Comparator<RowProxy> sorter) {
        this.sorter = Objects.requireNonNull(sorter);
    }

    public static IntSeries rankUnsorted(int size) {
        // no sort order means each row is equivalent from the ranking perspective, so return a Series of 1's
        int[] ints = new int[size];
        Arrays.fill(ints, RowNumberer.START_NUMBER);
        return IntSeries.forInts(ints);
    }

    public IntSeries rank(DataFrame dataFrame) {
        IntSeries sortIndex = new IndexSorter(dataFrame).sortIndex(sorter);
        DataFrameRowProxy pproxy = new DataFrameRowProxy(dataFrame);
        DataFrameRowProxy rproxy = new DataFrameRowProxy(dataFrame);
        int len = dataFrame.height();

        int[] rank = new int[len];

        for (int i = 0; i < len; i++) {

            int row = sortIndex.getInt(i);

            if (i == 0) {
                rank[row] = 1;
            } else {
                int prow = sortIndex.getInt(i - 1);
                rank[row] = sorter.compare(rproxy.rewind(row), pproxy.rewind(prow)) == 0 ? rank[prow] : i + 1;
            }
        }

        return IntSeries.forInts(rank);
    }

    public IntSeries denseRank(DataFrame dataFrame) {

        IntSeries sortIndex = new IndexSorter(dataFrame).sortIndex(sorter);
        DataFrameRowProxy pproxy = new DataFrameRowProxy(dataFrame);
        DataFrameRowProxy rproxy = new DataFrameRowProxy(dataFrame);
        int len = dataFrame.height();

        int[] rank = new int[len];

        for (int i = 0; i < len; i++) {

            int row = sortIndex.getInt(i);

            if (i == 0) {
                rank[row] = 1;
            } else {
                int prow = sortIndex.getInt(i - 1);
                rank[row] = sorter.compare(rproxy.rewind(row), pproxy.rewind(prow)) == 0 ? rank[prow] : rank[prow] + 1;
            }
        }

        return IntSeries.forInts(rank);
    }
}
