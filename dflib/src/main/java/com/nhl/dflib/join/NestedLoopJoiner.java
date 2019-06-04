package com.nhl.dflib.join;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.JoinType;
import com.nhl.dflib.seriesbuilder.IntAccumulator;
import com.nhl.dflib.row.RowProxy;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DataFrame joiner based on rows comparing predicate. Should theoretically have O(N * M) performance.
 */
public class NestedLoopJoiner extends BaseJoiner {

    private JoinPredicate joinPredicate;

    public NestedLoopJoiner(JoinPredicate joinPredicate, JoinType semantics, String indicatorColumn) {
        super(semantics, indicatorColumn);
        this.joinPredicate = Objects.requireNonNull(joinPredicate);
    }

    @Override
    protected IntSeries[] innerJoin(DataFrame lf, DataFrame rf) {

        IntAccumulator li = new IntAccumulator();
        IntAccumulator ri = new IntAccumulator();

        // make sure we don't recalculate this frame inside the inner loop
        // TODO: "materialize" is useless in column frames
        DataFrame rfm = rf.materialize();

        int i = 0;
        for (RowProxy lr : lf) {

            int j = 0;
            for (RowProxy rr : rfm) {
                if (joinPredicate.test(lr, rr)) {
                    li.add(i);
                    ri.add(j);
                }

                j++;
            }

            i++;
        }

        return new IntSeries[]{li.toIntSeries(), ri.toIntSeries()};
    }

    @Override
    protected IntSeries[] leftJoin(DataFrame lf, DataFrame rf) {

        IntAccumulator li = new IntAccumulator();
        IntAccumulator ri = new IntAccumulator();

        // make sure we don't recalculate this frame inside the inner loop
        DataFrame rfm = rf.materialize();

        int i = 0;
        for (RowProxy lr : lf) {

            int j = 0;
            boolean hadMatches = false;
            for (RowProxy rr : rfm) {
                if (joinPredicate.test(lr, rr)) {
                    li.add(i);
                    ri.add(j);
                    hadMatches = true;
                }

                j++;
            }

            if (!hadMatches) {
                li.add(i);
                ri.add(-1);
            }

            i++;
        }

        return new IntSeries[]{li.toIntSeries(), ri.toIntSeries()};
    }

    @Override
    protected IntSeries[] rightJoin(DataFrame lf, DataFrame rf) {

        IntAccumulator li = new IntAccumulator();
        IntAccumulator ri = new IntAccumulator();

        // make sure we don't recalculate this frame inside the inner loop
        DataFrame lfm = lf.materialize();

        int i = 0;
        for (RowProxy rr : rf) {

            int j = 0;
            boolean hadMatches = false;
            for (RowProxy lr : lfm) {
                if (joinPredicate.test(lr, rr)) {
                    li.add(j);
                    ri.add(i);
                    hadMatches = true;
                }

                j++;
            }

            if (!hadMatches) {
                li.add(-1);
                ri.add(i);
            }

            i++;
        }

        return new IntSeries[]{li.toIntSeries(), ri.toIntSeries()};
    }

    @Override
    protected IntSeries[] fullJoin(DataFrame lf, DataFrame rf) {

        IntAccumulator li = new IntAccumulator();
        IntAccumulator ri = new IntAccumulator();

        // make sure we don't recalculate this frame inside the inner loop
        DataFrame rfm = rf.materialize();

        Set<Integer> seenRights = new LinkedHashSet<>();

        int i = 0;
        for (RowProxy lr : lf) {

            boolean hadMatches = false;

            int j = 0;
            for (RowProxy rr : rfm) {
                if (joinPredicate.test(lr, rr)) {
                    li.add(i);
                    ri.add(j);
                    seenRights.add(j);
                    hadMatches = true;
                }

                j++;
            }

            if (!hadMatches) {
                li.add(i);
                ri.add(-1);
            }

            i++;
        }

        // add missing right rows
        int rh = rfm.height();
        for (int j = 0; j < rh; j++) {
            if (!seenRights.contains(j)) {
                li.add(-1);
                ri.add(j);
            }
        }

        return new IntSeries[]{li.toIntSeries(), ri.toIntSeries()};
    }
}
