package com.nhl.dflib.join;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.zip.Zipper;

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
public class IndexedJoiner<K> extends BaseJoiner {

    private JoinKeyMapper<K> leftKeyMapper;
    private JoinKeyMapper<K> rightKeyMapper;
    private JoinSemantics semantics;

    public IndexedJoiner(
            JoinKeyMapper<K> leftKeyMapper,
            JoinKeyMapper<K> rightKeyMapper,
            JoinSemantics semantics) {

        this.leftKeyMapper = leftKeyMapper;
        this.rightKeyMapper = rightKeyMapper;
        this.semantics = semantics;
    }

    public Index joinIndex(Index li, Index ri) {
        return Zipper.zipIndex(li, ri);
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

        Map<K, List<Object[]>> rightIndex = groupByKey(rightKeyMapper, rf);

        for (Object[] lr : lf) {

            K lKey = leftKeyMapper.map(lColumns, lr);
            List<Object[]> rightMatches = rightIndex.get(lKey);
            if (rightMatches != null) {
                for (Object[] rr : rightMatches) {
                    lRows.add(lr);
                    rRows.add(rr);
                }
            }
        }

        return zipJoinSides(joinedColumns,
                DataFrame.fromList(lf.getColumns(), lRows),
                DataFrame.fromList(rf.getColumns(), rRows));
    }

    private DataFrame leftJoin(Index joinedColumns, DataFrame lf, DataFrame rf) {

        List<Object[]> lRows = new ArrayList<>();
        List<Object[]> rRows = new ArrayList<>();

        Index lColumns = lf.getColumns();

        Map<K, List<Object[]>> rightIndex = groupByKey(rightKeyMapper, rf);

        for (Object[] lr : lf) {

            K lKey = leftKeyMapper.map(lColumns, lr);
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
                DataFrame.fromList(lf.getColumns(), lRows),
                DataFrame.fromList(rf.getColumns(), rRows));
    }

    private DataFrame rightJoin(Index joinedColumns, DataFrame lf, DataFrame rf) {

        List<Object[]> lRows = new ArrayList<>();
        List<Object[]> rRows = new ArrayList<>();

        Index rColumns = rf.getColumns();

        Map<K, List<Object[]>> leftIndex = groupByKey(leftKeyMapper, lf);

        for (Object[] rr : rf) {

            K rKey = rightKeyMapper.map(rColumns, rr);
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
                DataFrame.fromList(lf.getColumns(), lRows),
                DataFrame.fromList(rf.getColumns(), rRows));
    }

    private DataFrame fullJoin(Index joinedColumns, DataFrame lf, DataFrame rf) {

        List<Object[]> lRows = new ArrayList<>();
        Set<Object[]> rRows = new LinkedHashSet<>();

        Index lColumns = lf.getColumns();

        Map<K, List<Object[]>> rightIndex = groupByKey(rightKeyMapper, rf);
        Set<Object[]> seenRights = new LinkedHashSet<>();

        for (Object[] lr : lf) {

            K lKey = leftKeyMapper.map(lColumns, lr);
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
        for (List<Object[]> rrl : rightIndex.values()) {
            for (Object[] rr : rrl) {
                if (!seenRights.contains(rr)) {
                    lRows.add(null);
                    rRows.add(rr);
                }
            }
        }

        return zipJoinSides(joinedColumns,
                DataFrame.fromList(lf.getColumns(), lRows),
                DataFrame.fromRows(rf.getColumns(), rRows));
    }

    private Map<K, List<Object[]>> groupByKey(JoinKeyMapper<K> keyMapper, DataFrame df) {

        Index columns = df.getColumns();

        Map<K, List<Object[]>> index = new HashMap<>();

        for (Object[] r : df) {
            K key = keyMapper.map(columns, r);
            index.computeIfAbsent(key, k -> new ArrayList<>()).add(r);
        }

        return index;
    }
}
