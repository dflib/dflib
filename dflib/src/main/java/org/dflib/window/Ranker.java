package org.dflib.window;

import org.dflib.DataFrame;
import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.series.IntSingleValueSeries;
import org.dflib.sort.DataFrameSorter;
import org.dflib.sort.IntComparator;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class Ranker {

    protected final IntComparator sorter;

    public Ranker(IntComparator sorter) {
        this.sorter = Objects.requireNonNull(sorter);
    }

    public static IntSeries sameRank(int size) {
        return new IntSingleValueSeries(RowNumberer.START_NUMBER, size);
    }

    public IntSeries rank(DataFrame dataFrame) {
        IntSeries sortIndex = DataFrameSorter.sort(sorter, dataFrame.height());
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

        return Series.ofInt(rank);
    }

    protected RankResolver createRankResolver(DataFrame dataFrame, int[] rank) {
        return (i, row, prow) -> sorter.compare(row, prow) == 0 ? rank[prow] : i + 1;
    }
}
