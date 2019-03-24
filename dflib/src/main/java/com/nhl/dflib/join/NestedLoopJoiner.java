package com.nhl.dflib.join;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.concat.HConcat;
import com.nhl.dflib.row.RowProxy;
import com.nhl.dflib.series.ListSeries;

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

    public JoinMerger joinMerger(DataFrame lf, DataFrame rf) {

        switch (semantics) {
            case inner:
                return innerJoin(lf, rf);
            case left:
                return leftJoin(lf, rf);
            case right:
                return rightJoin(lf, rf);
            case full:
                return fullJoin(lf, rf);
            default:
                throw new IllegalStateException("Unsupported join semantics: " + semantics);
        }
    }

    private JoinMerger innerJoin(DataFrame lf, DataFrame rf) {

        List<Integer> li = new ArrayList<>();
        List<Integer> ri = new ArrayList<>();

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

        return new JoinMerger(new ListSeries<>(li), new ListSeries<>(ri));
    }

    private JoinMerger leftJoin(DataFrame lf, DataFrame rf) {

        List<Integer> li = new ArrayList<>();
        List<Integer> ri = new ArrayList<>();

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

        return new JoinMerger(new ListSeries<>(li), new ListSeries<>(ri));
    }

    private JoinMerger rightJoin(DataFrame lf, DataFrame rf) {

        List<Integer> li = new ArrayList<>();
        List<Integer> ri = new ArrayList<>();

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

        return new JoinMerger(new ListSeries<>(li), new ListSeries<>(ri));
    }

    private JoinMerger fullJoin(DataFrame lf, DataFrame rf) {

        List<Integer> li = new ArrayList<>();
        List<Integer> ri = new ArrayList<>();

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

        return new JoinMerger(new ListSeries<>(li), new ListSeries<>(ri));
    }
}
