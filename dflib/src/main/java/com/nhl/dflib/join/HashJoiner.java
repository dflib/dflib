package com.nhl.dflib.join;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.concat.HConcat;
import com.nhl.dflib.map.Hasher;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A DataFrame joiner using <a href="https://en.wikipedia.org/wiki/Hash_join">"hash join"</a> algorithm. It requires
 * two custom "hash" functions for the rows on the left and the right sides of the join, each producing values, whose
 * equality can be used as a join condition. Should theoretically have O(N + M) performance.
 */
public class HashJoiner extends BaseJoiner {

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

        List<Object[]> lRows = new ArrayList<>();
        List<Object[]> rRows = new ArrayList<>();

        Index lColumns = lf.getColumns();

        Map<Object, List<Object[]>> rightIndex = groupByKey(rightHasher, rf);

        for (Object[] lr : lf) {

            Object lKey = leftHasher.map(lColumns, lr);
            List<Object[]> rightMatches = rightIndex.get(lKey);
            if (rightMatches != null) {
                for (Object[] rr : rightMatches) {
                    lRows.add(lr);
                    rRows.add(rr);
                }
            }
        }

        return zipJoinSides(joinedColumns,
                DataFrame.fromRowsList(lf.getColumns(), lRows),
                DataFrame.fromRowsList(rf.getColumns(), rRows));
    }

    private DataFrame leftJoin(Index joinedColumns, DataFrame lf, DataFrame rf) {

        List<Object[]> lRows = new ArrayList<>();
        List<Object[]> rRows = new ArrayList<>();

        Index lColumns = lf.getColumns();

        Map<Object, List<Object[]>> rightIndex = groupByKey(rightHasher, rf);

        for (Object[] lr : lf) {

            Object lKey = leftHasher.map(lColumns, lr);
            List<Object[]> rightMatches = rightIndex.get(lKey);

            if (rightMatches != null) {
                for (Object[] rr : rightMatches) {
                    lRows.add(lr);
                    rRows.add(rr);
                }
            } else {
                lRows.add(lr);
                rRows.add(null);
            }
        }

        return zipJoinSides(joinedColumns,
                DataFrame.fromRowsList(lf.getColumns(), lRows),
                DataFrame.fromRowsList(rf.getColumns(), rRows));
    }

    private DataFrame rightJoin(Index joinedColumns, DataFrame lf, DataFrame rf) {

        List<Object[]> lRows = new ArrayList<>();
        List<Object[]> rRows = new ArrayList<>();

        Index rColumns = rf.getColumns();

        Map<Object, List<Object[]>> leftIndex = groupByKey(leftHasher, lf);

        for (Object[] rr : rf) {

            Object rKey = rightHasher.map(rColumns, rr);
            List<Object[]> leftMatches = leftIndex.get(rKey);

            if (leftMatches != null) {
                for (Object[] lr : leftMatches) {
                    lRows.add(lr);
                    rRows.add(rr);
                }
            } else {
                lRows.add(null);
                rRows.add(rr);
            }
        }

        return zipJoinSides(joinedColumns,
                DataFrame.fromRowsList(lf.getColumns(), lRows),
                DataFrame.fromRowsList(rf.getColumns(), rRows));
    }

    private DataFrame fullJoin(Index joinedColumns, DataFrame lf, DataFrame rf) {

        List<Object[]> lRows = new ArrayList<>();
        List<Object[]> rRows = new ArrayList<>();

        Index lColumns = lf.getColumns();

        Map<Object, List<Object[]>> rightIndex = groupByKey(rightHasher, rf);
        Set<Object> seenRightKeys = new LinkedHashSet<>();

        for (Object[] lr : lf) {

            Object lKey = leftHasher.map(lColumns, lr);
            List<Object[]> rightMatches = rightIndex.get(lKey);

            if (rightMatches != null) {
                seenRightKeys.add(lKey);

                for (Object[] rr : rightMatches) {
                    lRows.add(lr);
                    rRows.add(rr);
                }
            } else {
                lRows.add(lr);
                rRows.add(null);
            }
        }

        // add missing right rows
        for (Map.Entry<Object, List<Object[]>> e : rightIndex.entrySet()) {
            if (!seenRightKeys.contains(e.getKey())) {
                for (Object[] rr : e.getValue()) {
                    lRows.add(null);
                    rRows.add(rr);
                }
            }
        }

        return zipJoinSides(joinedColumns,
                DataFrame.fromRowsList(lf.getColumns(), lRows),
                DataFrame.fromRowsList(rf.getColumns(), rRows));
    }


    // this is the same exact logic as in Grouper, only without wrapping the Map in a DataFrame. In benchmarks not using
    // Grouper gives a small (5-10%) speed advantage
    private Map<Object, List<Object[]>> groupByKey(Hasher hasher, DataFrame df) {

        Index columns = df.getColumns();

        Map<Object, List<Object[]>> index = new LinkedHashMap<>();

        for (Object[] r : df) {
            Object key = hasher.map(columns, r);
            index.computeIfAbsent(key, k -> new ArrayList<>()).add(r);
        }

        return index;
    }

}
