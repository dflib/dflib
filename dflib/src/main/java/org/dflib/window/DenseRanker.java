package org.dflib.window;

import org.dflib.DataFrame;
import org.dflib.sort.IntComparator;

public class DenseRanker extends Ranker {

    public DenseRanker(IntComparator sorter) {
        super(sorter);
    }

    @Override
    protected RankResolver createRankResolver(DataFrame dataFrame, int[] rank) {
        return (i, row, prow) -> sorter.compare(row, prow) == 0 ? rank[prow] : rank[prow] + 1;
    }
}
