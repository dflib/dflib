package com.nhl.dflib.window;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.row.DataFrameRowProxy;
import com.nhl.dflib.row.RowProxy;

import java.util.Comparator;

/**
 * @since 0.8
 */
public class DenseRanker extends Ranker {

    public DenseRanker(Comparator<RowProxy> sorter) {
        super(sorter);
    }

    @Override
    protected RankResolver createRankResolver(DataFrame dataFrame, int[] rank) {
        DataFrameRowProxy pproxy = new DataFrameRowProxy(dataFrame);
        DataFrameRowProxy rproxy = new DataFrameRowProxy(dataFrame);
        return (i, row, prow) -> sorter.compare(rproxy.rewind(row), pproxy.rewind(prow)) == 0 ? rank[prow] : rank[prow] + 1;
    }
}
