package org.dflib.join;

import org.dflib.DataFrame;
import org.dflib.IntSeries;
import org.dflib.JoinType;
import org.dflib.builder.IntAccum;
import org.dflib.row.RowProxy;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DataFrame joiner based on rows comparing predicate. Should theoretically have O(N * M) performance.
 */
public class NestedLoopJoiner extends BaseJoiner {

    private final JoinPredicate joinPredicate;

    public NestedLoopJoiner(JoinPredicate joinPredicate, JoinType semantics) {
        super(semantics);
        this.joinPredicate = Objects.requireNonNull(joinPredicate);
    }

    @Override
    protected IntSeries[] innerJoin(DataFrame lf, DataFrame rf) {

        IntAccum li = new IntAccum();
        IntAccum ri = new IntAccum();

        // make sure we don't recalculate this frame inside the inner loop
        // TODO: "materialize" is useless in column frames
        DataFrame rfm = rf.materialize();

        int i = 0;
        for (RowProxy lr : lf) {

            int j = 0;
            for (RowProxy rr : rfm) {
                if (joinPredicate.test(lr, rr)) {
                    li.pushInt(i);
                    ri.pushInt(j);
                }

                j++;
            }

            i++;
        }

        return new IntSeries[]{li.toSeries(), ri.toSeries()};
    }

    @Override
    protected IntSeries[] leftJoin(DataFrame lf, DataFrame rf) {

        IntAccum li = new IntAccum();
        IntAccum ri = new IntAccum();

        // make sure we don't recalculate this frame inside the inner loop
        DataFrame rfm = rf.materialize();

        int i = 0;
        for (RowProxy lr : lf) {

            int j = 0;
            boolean hadMatches = false;
            for (RowProxy rr : rfm) {
                if (joinPredicate.test(lr, rr)) {
                    li.pushInt(i);
                    ri.pushInt(j);
                    hadMatches = true;
                }

                j++;
            }

            if (!hadMatches) {
                li.pushInt(i);
                ri.pushInt(-1);
            }

            i++;
        }

        return new IntSeries[]{li.toSeries(), ri.toSeries()};
    }

    @Override
    protected IntSeries[] rightJoin(DataFrame lf, DataFrame rf) {

        IntAccum li = new IntAccum();
        IntAccum ri = new IntAccum();

        // make sure we don't recalculate this frame inside the inner loop
        DataFrame lfm = lf.materialize();

        int i = 0;
        for (RowProxy rr : rf) {

            int j = 0;
            boolean hadMatches = false;
            for (RowProxy lr : lfm) {
                if (joinPredicate.test(lr, rr)) {
                    li.pushInt(j);
                    ri.pushInt(i);
                    hadMatches = true;
                }

                j++;
            }

            if (!hadMatches) {
                li.pushInt(-1);
                ri.pushInt(i);
            }

            i++;
        }

        return new IntSeries[]{li.toSeries(), ri.toSeries()};
    }

    @Override
    protected IntSeries[] fullJoin(DataFrame lf, DataFrame rf) {

        IntAccum li = new IntAccum();
        IntAccum ri = new IntAccum();

        // make sure we don't recalculate this frame inside the inner loop
        DataFrame rfm = rf.materialize();

        Set<Integer> seenRights = new LinkedHashSet<>();

        int i = 0;
        for (RowProxy lr : lf) {

            boolean hadMatches = false;

            int j = 0;
            for (RowProxy rr : rfm) {
                if (joinPredicate.test(lr, rr)) {
                    li.pushInt(i);
                    ri.pushInt(j);
                    seenRights.add(j);
                    hadMatches = true;
                }

                j++;
            }

            if (!hadMatches) {
                li.pushInt(i);
                ri.pushInt(-1);
            }

            i++;
        }

        // add missing right rows
        int rh = rfm.height();
        for (int j = 0; j < rh; j++) {
            if (!seenRights.contains(j)) {
                li.pushInt(-1);
                ri.pushInt(j);
            }
        }

        return new IntSeries[]{li.toSeries(), ri.toSeries()};
    }
}
