package com.nhl.dflib.window;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.sort.IntComparator;
import com.nhl.dflib.sort.DataFrameSorter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * @since 0.8
 */
public class Ranker {

    protected IntComparator sorter;

    public Ranker(IntComparator sorter) {
        this.sorter = Objects.requireNonNull(sorter);
    }

    public static IntSeries sameRank(int size) {
        // no sort order means each row is equivalent from the ranking perspective, so return a Series of 1's
        int[] ints = new int[size];
        Arrays.fill(ints, RowNumberer.START_NUMBER);
        return IntSeries.forInts(ints);
    }

    public IntSeries rank(DataFrame dataFrame) {
        IntSeries sortIndex = new DataFrameSorter(dataFrame).sortIndex(sorter);
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
        return (i, row, prow) -> sorter.compare(row, prow) == 0 ? rank[prow] : i + 1;
    }
}
