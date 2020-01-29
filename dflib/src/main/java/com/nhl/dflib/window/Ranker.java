package com.nhl.dflib.window;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.row.DataFrameRowProxy;
import com.nhl.dflib.row.RowProxy;
import com.nhl.dflib.sort.IndexSorter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

/**
 * @since 0.8
 */
public class Ranker {

    protected Comparator<RowProxy> sorter;

    public Ranker(Comparator<RowProxy> sorter) {
        this.sorter = Objects.requireNonNull(sorter);
    }

    public static IntSeries sameRank(int size) {
        // no sort order means each row is equivalent from the ranking perspective, so return a Series of 1's
        int[] ints = new int[size];
        Arrays.fill(ints, RowNumberer.START_NUMBER);
        return IntSeries.forInts(ints);
    }

    public IntSeries rank(DataFrame dataFrame) {
        IntSeries sortIndex = new IndexSorter(dataFrame).sortIndex(sorter);
        return rank(dataFrame, Collections.singletonList(sortIndex));
    }

    public IntSeries rank(DataFrame dataFrame, Collection<IntSeries> partitionsIndex) {

        int[] rank = new int[dataFrame.height()];
        RankResolver resolver = createRankResolver(dataFrame, rank);

        for (IntSeries s : partitionsIndex) {

            int len = s.size();
            for (int i = 0; i < len; i++) {

                int row = s.getInt(i);

                if (i == 0) {
                    rank[row] = 1;
                } else {
                    int prow = s.getInt(i - 1);
                    rank[row] = resolver.resolve(i, row, prow);
                }
            }
        }

        return IntSeries.forInts(rank);
    }

    protected RankResolver createRankResolver(DataFrame dataFrame, int[] rank) {

        DataFrameRowProxy pproxy = new DataFrameRowProxy(dataFrame);
        DataFrameRowProxy rproxy = new DataFrameRowProxy(dataFrame);

        return (i, row, prow) -> sorter.compare(rproxy.rewind(row), pproxy.rewind(prow)) == 0 ? rank[prow] : i + 1;
    }
}
