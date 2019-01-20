package com.nhl.dflib.join;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.map.KeyMapper;
import com.nhl.dflib.concat.HConcat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A DataFrame joiner based on a pair of functions that can calculate comparable keys for the left and right row.
 * Should theoretically have O(N + M) performance.
 */
public class MappedJoiner extends BaseJoiner {

    private KeyMapper leftKeyMapper;
    private KeyMapper rightKeyMapper;
    private JoinType semantics;

    public MappedJoiner(
            KeyMapper leftKeyMapper,
            KeyMapper rightKeyMapper,
            JoinType semantics) {

        this.leftKeyMapper = leftKeyMapper;
        this.rightKeyMapper = rightKeyMapper;
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

        Map<Object, List<Object[]>> rightIndex = groupByKey(rightKeyMapper, rf);

        for (Object[] lr : lf) {

            Object lKey = leftKeyMapper.map(lColumns, lr);
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

        Map<Object, List<Object[]>> rightIndex = groupByKey(rightKeyMapper, rf);

        for (Object[] lr : lf) {

            Object lKey = leftKeyMapper.map(lColumns, lr);
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

        Map<Object, List<Object[]>> leftIndex = groupByKey(leftKeyMapper, lf);

        for (Object[] rr : rf) {

            Object rKey = rightKeyMapper.map(rColumns, rr);
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

        Map<Object, List<Object[]>> rightIndex = groupByKey(rightKeyMapper, rf);
        Set<Object[]> seenRights = new LinkedHashSet<>();

        for (Object[] lr : lf) {

            Object lKey = leftKeyMapper.map(lColumns, lr);
            List<Object[]> rightMatches = rightIndex.get(lKey);

            if (rightMatches != null) {
                for (Object[] rr : rightMatches) {
                    lRows.add(lr);
                    rRows.add(rr);
                    seenRights.add(rr);
                }
            } else {
                lRows.add(lr);
                rRows.add(null);
            }
        }

        // add missing right rows
        for (Object[] rr : rf) {
            if (!seenRights.contains(rr)) {
                lRows.add(null);
                rRows.add(rr);
            }
        }

        return zipJoinSides(joinedColumns,
                DataFrame.fromRowsList(lf.getColumns(), lRows),
                DataFrame.fromRowsList(rf.getColumns(), rRows));
    }

    // TODO: this is the same exact logic as in Grouper, only without wrapping the Map in a DataFrame.. Also it uses
    //  HashMap instead of LinkedHashMap which is somewhat faster for "get". Is it worth
    //  reusing the Grouper here vs. small overhead it introduces?
    private Map<Object, List<Object[]>> groupByKey(KeyMapper keyMapper, DataFrame df) {

        Index columns = df.getColumns();

        Map<Object, List<Object[]>> index = new HashMap<>();

        for (Object[] r : df) {
            Object key = keyMapper.map(columns, r);
            index.computeIfAbsent(key, k -> new ArrayList<>()).add(r);
        }

        return index;
    }
}
