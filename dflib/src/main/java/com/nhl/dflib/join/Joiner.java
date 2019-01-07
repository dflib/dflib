package com.nhl.dflib.join;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.zip.Zipper;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A DataFrame joiner based on rows comparing predicate. Should theoretically have O(N * M) performance.
 */
public class Joiner extends BaseJoiner {

    private DataRowJoinPredicate joinPredicate;
    private JoinSemantics semantics;

    public Joiner(DataRowJoinPredicate joinPredicate, JoinSemantics semantics) {
        this.joinPredicate = Objects.requireNonNull(joinPredicate);
        this.semantics = Objects.requireNonNull(semantics);
    }

    public Index joinIndex(Index li, Index ri) {
        return Zipper.zipIndex(li, ri);
    }

    public DataFrame joinRows(Index joinedColumns, DataFrame lf, DataFrame rf) {

        JoinContext context = new JoinContext(lf.getColumns(), rf.getColumns(), joinedColumns);

        switch (semantics) {
            case inner:
                return innerJoin(context, lf, rf);
            case left:
                return leftJoin(context, lf, rf);
            case right:
                return rightJoin(context, lf, rf);
            case full:
                return fullJoin(context, lf, rf);
            default:
                throw new IllegalStateException("Unsupported join semantics: " + semantics);
        }
    }

    private DataFrame innerJoin(JoinContext context, DataFrame lf, DataFrame rf) {

        List<Object[]> lRows = new ArrayList<>();
        List<Object[]> rRows = new ArrayList<>();

        List<Object[]> allRRows = toList(rf);

        for (Object[] lr : lf) {
            for (Object[] rr : allRRows) {
                if (joinPredicate.test(context, lr, rr)) {
                    lRows.add(lr);
                    rRows.add(rr);
                }
            }
        }

        return zipJoinSides(context.getJoinIndex(),
                DataFrame.create(context.getLeftIndex(), lRows),
                DataFrame.create(context.getRightIndex(), rRows));
    }

    private DataFrame leftJoin(JoinContext context, DataFrame lf, DataFrame rf) {

        List<Object[]> lRows = new ArrayList<>();
        List<Object[]> rRows = new ArrayList<>();

        List<Object[]> allRRows = toList(rf);

        for (Object[] lr : lf) {

            boolean hadMatches = false;
            for (Object[] rr : allRRows) {
                if (joinPredicate.test(context, lr, rr)) {
                    lRows.add(lr);
                    rRows.add(rr);
                    hadMatches = true;
                }
            }

            if (!hadMatches) {
                lRows.add(lr);
                rRows.add(null);
            }
        }

        return zipJoinSides(context.getJoinIndex(),
                DataFrame.create(context.getLeftIndex(), lRows),
                DataFrame.create(context.getRightIndex(), rRows));
    }

    private DataFrame rightJoin(JoinContext context, DataFrame lf, DataFrame rf) {

        List<Object[]> lRows = new ArrayList<>();
        List<Object[]> rRows = new ArrayList<>();

        List<Object[]> allLRows = toList(lf);

        for (Object[] rr : rf) {

            boolean hadMatches = false;
            for (Object[] lr : allLRows) {
                if (joinPredicate.test(context, lr, rr)) {
                    lRows.add(lr);
                    rRows.add(rr);
                    hadMatches = true;
                }
            }

            if (!hadMatches) {
                lRows.add(null);
                rRows.add(rr);
            }
        }

        return zipJoinSides(context.getJoinIndex(),
                DataFrame.create(context.getLeftIndex(), lRows),
                DataFrame.create(context.getRightIndex(), rRows));
    }

    private DataFrame fullJoin(JoinContext context, DataFrame lf, DataFrame rf) {

        List<Object[]> lRows = new ArrayList<>();
        Set<Object[]> rRows = new LinkedHashSet<>();

        List<Object[]> allRRows = toList(rf);
        Set<Object[]> seenRights = new LinkedHashSet<>();

        for (Object[] lr : lf) {

            boolean hadMatches = false;

            for (Object[] rr : allRRows) {
                if (joinPredicate.test(context, lr, rr)) {
                    lRows.add(lr);
                    rRows.add(rr);
                    hadMatches = true;
                    seenRights.add(rr);
                }
            }

            if (!hadMatches) {
                lRows.add(lr);
                rRows.add(null);
            }
        }

        // add missing right rows
        for (Object[] rr : allRRows) {
            if (!seenRights.contains(rr)) {
                lRows.add(null);
                rRows.add(rr);
            }
        }

        return zipJoinSides(context.getJoinIndex(),
                DataFrame.create(context.getLeftIndex(), lRows),
                DataFrame.create(context.getRightIndex(), rRows));
    }

    // "materialize" frame rows to avoid recalculation of each row on multiple iterations.

    // TODO: should we just add a caching / materialization wrapper around the DataFrame and assume elsewhere that DF
    //  iterator performance is decent?

    private List<Object[]> toList(DataFrame df) {
        List<Object[]> materialized = new ArrayList<>();
        df.forEach(materialized::add);
        return materialized;
    }
}
