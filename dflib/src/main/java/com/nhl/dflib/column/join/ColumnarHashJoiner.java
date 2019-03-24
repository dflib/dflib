package com.nhl.dflib.column.join;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.GroupBy;
import com.nhl.dflib.Index;
import com.nhl.dflib.column.ColumnDataFrame;
import com.nhl.dflib.column.map.MultiListRowBuilder;
import com.nhl.dflib.concat.HConcat;
import com.nhl.dflib.join.JoinType;
import com.nhl.dflib.map.Hasher;
import com.nhl.dflib.map.RowCombiner;
import com.nhl.dflib.row.RowProxy;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A DataFrame joiner using <a href="https://en.wikipedia.org/wiki/Hash_join">"hash join"</a> algorithm. It requires
 * two custom "hash" functions for the rows on the left and the right sides of the join, each producing values, whose
 * equality can be used as a join condition. Should theoretically have O(N + M) performance.
 */
public class ColumnarHashJoiner {


    private Hasher leftHasher;
    private Hasher rightHasher;
    private JoinType semantics;

    public ColumnarHashJoiner(
            Hasher leftHasher,
            Hasher rightHasher,
            JoinType semantics) {

        this.leftHasher = leftHasher;
        this.rightHasher = rightHasher;
        this.semantics = semantics;
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

        RowCombiner combiner = RowCombiner.zip(lf.width());
        MultiListRowBuilder rowBuilder = new MultiListRowBuilder(joinedColumns, 10);

        GroupBy rightIndex = rf.groupBy(rightHasher);

        for (RowProxy lr : lf) {

            Object lKey = leftHasher.map(lr);
            DataFrame rightMatches = rightIndex.getGroup(lKey);
            if (rightMatches != null) {

                for (RowProxy rr : rightMatches) {
                    rowBuilder.startRow();
                    combiner.combine(lr, rr, rowBuilder);
                }
            }
        }

        return new ColumnDataFrame(joinedColumns, rowBuilder.getData());
    }

    private DataFrame leftJoin(Index joinedColumns, DataFrame lf, DataFrame rf) {

        RowCombiner combiner = RowCombiner.zip(lf.width());
        MultiListRowBuilder rowBuilder = new MultiListRowBuilder(joinedColumns, lf.height());

        GroupBy rightIndex = rf.groupBy(rightHasher);

        for (RowProxy lr : lf) {

            Object lKey = leftHasher.map(lr);
            DataFrame rightMatches = rightIndex.getGroup(lKey);

            if (rightMatches != null) {
                for (RowProxy rr : rightMatches) {
                    rowBuilder.startRow();
                    combiner.combine(lr, rr, rowBuilder);
                }
            } else {
                rowBuilder.startRow();
                combiner.combine(lr, null, rowBuilder);
            }
        }

        return new ColumnDataFrame(joinedColumns, rowBuilder.getData());
    }

    private DataFrame rightJoin(Index joinedColumns, DataFrame lf, DataFrame rf) {

        RowCombiner combiner = RowCombiner.zip(lf.width());
        MultiListRowBuilder rowBuilder = new MultiListRowBuilder(joinedColumns, rf.height());

        GroupBy leftIndex = lf.groupBy(leftHasher);

        for (RowProxy rr : rf) {

            Object rKey = rightHasher.map(rr);
            DataFrame leftMatches = leftIndex.getGroup(rKey);

            if (leftMatches != null) {
                for (RowProxy lr : leftMatches) {
                    rowBuilder.startRow();
                    combiner.combine(lr, rr, rowBuilder);
                }
            } else {
                rowBuilder.startRow();
                combiner.combine(null, rr, rowBuilder);
            }
        }

        return new ColumnDataFrame(joinedColumns, rowBuilder.getData());
    }

    private DataFrame fullJoin(Index joinedColumns, DataFrame lf, DataFrame rf) {

        RowCombiner combiner = RowCombiner.zip(lf.width());
        MultiListRowBuilder rowBuilder = new MultiListRowBuilder(joinedColumns, Math.max(rf.height(), lf.height()));

        GroupBy rightIndex = rf.groupBy(rightHasher);
        Set<Object> seenRightKeys = new LinkedHashSet<>();

        int i = 0;
        for (RowProxy lr : lf) {

            Object lKey = leftHasher.map(lr);
            DataFrame rightMatches = rightIndex.getGroup(lKey);

            if (rightMatches != null) {

                seenRightKeys.add(lKey);

                for (RowProxy rr : rightMatches) {
                    rowBuilder.startRow();
                    combiner.combine(lr, rr, rowBuilder);
                }
            } else {
                rowBuilder.startRow();
                combiner.combine(lr, null, rowBuilder);
            }
        }

        // add missing right rows
        for (Object key : rightIndex.getGroups()) {
            if (!seenRightKeys.contains(key)) {
                for (RowProxy rr : rightIndex.getGroup(key)) {
                    rowBuilder.startRow();
                    combiner.combine(null, rr, rowBuilder);
                }
            }
        }

        return new ColumnDataFrame(joinedColumns, rowBuilder.getData());
    }
}
