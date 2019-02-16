package com.nhl.dflib.join;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.concat.HConcat;
import com.nhl.dflib.map.RowCombiner;
import com.nhl.dflib.row.ArrayRowBuilder;
import com.nhl.dflib.row.RowProxy;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A DataFrame joiner based on rows comparing predicate. Should theoretically have O(N * M) performance.
 */
public class NestedLoopJoiner {

    private JoinPredicate joinPredicate;
    private JoinType semantics;

    public NestedLoopJoiner(JoinPredicate joinPredicate, JoinType semantics) {
        this.joinPredicate = Objects.requireNonNull(joinPredicate);
        this.semantics = Objects.requireNonNull(semantics);
    }

    public Index joinIndex(Index li, Index ri) {
        return HConcat.zipIndex(li, ri);
    }

    public DataFrame joinRows(Index joinedColumns, DataFrame lf, DataFrame rf) {

        switch (semantics) {
            case inner:
                return innerJoin(joinedColumns, lf, rf);
            case left:
                return leftJoin(joinedColumns, lf, rf);
            case right:
                return rightJoin(joinedColumns, lf, rf);
            case full:
                return fullJoin(joinedColumns, lf, rf);
            default:
                throw new IllegalStateException("Unsupported join semantics: " + semantics);
        }
    }

    private DataFrame innerJoin(Index joinedColumns, DataFrame lf, DataFrame rf) {

        List<Object[]> joined = new ArrayList<>();
        RowCombiner combiner = RowCombiner.zip(lf.width());
        ArrayRowBuilder rowBuilder = new ArrayRowBuilder(joinedColumns);

        // make sure we don't recalculate this frame inside the inner loop
        DataFrame rfm = rf.materialize();

        for (RowProxy lr : lf) {
            for (RowProxy rr : rfm) {
                if (joinPredicate.test(lr, rr)) {
                    combiner.combine(lr, rr, rowBuilder);
                    joined.add(rowBuilder.reset());
                }
            }
        }

        return DataFrame.fromRowsList(joinedColumns, joined);
    }

    private DataFrame leftJoin(Index joinedColumns, DataFrame lf, DataFrame rf) {

        List<Object[]> joined = new ArrayList<>();
        RowCombiner combiner = RowCombiner.zip(lf.width());
        ArrayRowBuilder rowBuilder = new ArrayRowBuilder(joinedColumns);

        // make sure we don't recalculate this frame inside the inner loop
        DataFrame rfm = rf.materialize();

        for (RowProxy lr : lf) {

            boolean hadMatches = false;
            for (RowProxy rr : rfm) {
                if (joinPredicate.test(lr, rr)) {
                    combiner.combine(lr, rr, rowBuilder);
                    joined.add(rowBuilder.reset());
                    hadMatches = true;
                }
            }

            if (!hadMatches) {
                combiner.combine(lr, null, rowBuilder);
                joined.add(rowBuilder.reset());
            }
        }

        return DataFrame.fromRowsList(joinedColumns, joined);
    }

    private DataFrame rightJoin(Index joinedColumns, DataFrame lf, DataFrame rf) {

        List<Object[]> joined = new ArrayList<>();
        RowCombiner combiner = RowCombiner.zip(lf.width());
        ArrayRowBuilder rowBuilder = new ArrayRowBuilder(joinedColumns);

        // make sure we don't recalculate this frame inside the inner loop
        DataFrame lfm  = lf.materialize();

        for (RowProxy rr : rf) {

            boolean hadMatches = false;
            for (RowProxy lr : lfm) {
                if (joinPredicate.test(lr, rr)) {
                    combiner.combine(lr, rr, rowBuilder);
                    joined.add(rowBuilder.reset());
                    hadMatches = true;
                }
            }

            if (!hadMatches) {
                combiner.combine(null, rr, rowBuilder);
                joined.add(rowBuilder.reset());
            }
        }

        return DataFrame.fromRowsList(joinedColumns, joined);
    }

    private DataFrame fullJoin(Index joinedColumns, DataFrame lf, DataFrame rf) {

        List<Object[]> joined = new ArrayList<>();
        RowCombiner combiner = RowCombiner.zip(lf.width());
        ArrayRowBuilder rowBuilder = new ArrayRowBuilder(joinedColumns);

        // make sure we don't recalculate this frame inside the inner loop
        DataFrame rfm = rf.materialize();

        Set<Integer> seenRights = new LinkedHashSet<>();

        for (RowProxy lr : lf) {

            boolean hadMatches = false;

            int i = 0;
            for (RowProxy rr : rfm) {
                if (joinPredicate.test(lr, rr)) {
                    combiner.combine(lr, rr, rowBuilder);
                    joined.add(rowBuilder.reset());
                    seenRights.add(i);
                    hadMatches = true;
                }

                i++;
            }

            if (!hadMatches) {
                combiner.combine(lr, null, rowBuilder);
                joined.add(rowBuilder.reset());
            }
        }

        // add missing right rows
        int i = 0;
        for (RowProxy rr : rfm) {
            if (!seenRights.contains(i)) {
                combiner.combine(null, rr, rowBuilder);
                joined.add(rowBuilder.reset());
            }

            i++;
        }

        return DataFrame.fromRowsList(joinedColumns, joined);
    }
}
