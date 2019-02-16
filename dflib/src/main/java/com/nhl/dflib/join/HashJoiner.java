package com.nhl.dflib.join;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.GroupBy;
import com.nhl.dflib.Index;
import com.nhl.dflib.concat.HConcat;
import com.nhl.dflib.map.Hasher;
import com.nhl.dflib.map.RowCombiner;
import com.nhl.dflib.row.ArrayRowBuilder;
import com.nhl.dflib.row.RowProxy;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A DataFrame joiner using <a href="https://en.wikipedia.org/wiki/Hash_join">"hash join"</a> algorithm. It requires
 * two custom "hash" functions for the rows on the left and the right sides of the join, each producing values, whose
 * equality can be used as a join condition. Should theoretically have O(N + M) performance.
 */
public class HashJoiner {

    private Hasher leftHasher;
    private Hasher rightHasher;
    private JoinType semantics;

    public HashJoiner(
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

        List<Object[]> joined = new ArrayList<>();
        RowCombiner combiner = RowCombiner.zip(lf.width());
        ArrayRowBuilder rowBuilder = new ArrayRowBuilder(joinedColumns);

        GroupBy rightIndex = rf.groupBy(rightHasher);

        for (RowProxy lr : lf) {

            Object lKey = leftHasher.map(lr);
            DataFrame rightMatches = rightIndex.getGroup(lKey);
            if (rightMatches != null) {
                for (RowProxy rr : rightMatches) {
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

        GroupBy rightIndex = rf.groupBy(rightHasher);

        for (RowProxy lr : lf) {

            Object lKey = leftHasher.map(lr);
            DataFrame rightMatches = rightIndex.getGroup(lKey);

            if (rightMatches != null) {
                for (RowProxy rr : rightMatches) {
                    combiner.combine(lr, rr, rowBuilder);
                    joined.add(rowBuilder.reset());
                }
            } else {
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

        GroupBy leftIndex = lf.groupBy(leftHasher);

        for (RowProxy rr : rf) {

            Object rKey = rightHasher.map(rr);
            DataFrame leftMatches = leftIndex.getGroup(rKey);

            if (leftMatches != null) {
                for (RowProxy lr : leftMatches) {
                    combiner.combine(lr, rr, rowBuilder);
                    joined.add(rowBuilder.reset());
                }
            } else {
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

        GroupBy rightIndex = rf.groupBy(rightHasher);
        Set<Object> seenRightKeys = new LinkedHashSet<>();

        for (RowProxy lr : lf) {

            Object lKey = leftHasher.map(lr);
            DataFrame rightMatches = rightIndex.getGroup(lKey);

            if (rightMatches != null) {

                seenRightKeys.add(lKey);

                for (RowProxy rr : rightMatches) {
                    combiner.combine(lr, rr, rowBuilder);
                    joined.add(rowBuilder.reset());
                }

            } else {
                combiner.combine(lr, null, rowBuilder);
                joined.add(rowBuilder.reset());
            }
        }

        // add missing right rows
        for (Object key : rightIndex.getGroups()) {
            if (!seenRightKeys.contains(key)) {
                for (RowProxy rr : rightIndex.getGroup(key)) {
                    combiner.combine(null, rr, rowBuilder);
                    joined.add(rowBuilder.reset());
                }
            }
        }

        return DataFrame.fromRowsList(joinedColumns, joined);
    }
}
