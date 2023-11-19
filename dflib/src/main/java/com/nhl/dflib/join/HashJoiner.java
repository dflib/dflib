package com.nhl.dflib.join;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.GroupBy;
import com.nhl.dflib.Hasher;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.JoinType;
import com.nhl.dflib.builder.IntAccum;
import com.nhl.dflib.row.RowProxy;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A DataFrame joiner using <a href="https://en.wikipedia.org/wiki/Hash_join">"hash join"</a> algorithm. It requires
 * two custom "hash" functions for the rows on the left and the right sides of the join, each producing values, whose
 * equality can be used as a join condition. Should theoretically have O(N + M) performance.
 */
public class HashJoiner extends BaseJoiner {

    private final Hasher leftHasher;
    private final Hasher rightHasher;

    public HashJoiner(
            Hasher leftHasher,
            Hasher rightHasher,
            JoinType semantics,
            String indicatorColumn) {

        super(semantics, indicatorColumn);
        this.leftHasher = leftHasher;
        this.rightHasher = rightHasher;
    }

    @Override
    protected IntSeries[] innerJoin(DataFrame lf, DataFrame rf) {

        IntAccum li = new IntAccum();
        IntAccum ri = new IntAccum();

        GroupBy rightIndex = rf.group(rightHasher);

        int i = 0;
        for (RowProxy lr : lf) {

            Object lKey = leftHasher.map(lr);
            IntSeries rgi = rightIndex.getGroupIndex(lKey);

            if (rgi != null) {
                int js = rgi.size();
                for (int j = 0; j < js; j++) {
                    li.pushInt(i);
                    ri.pushInt(rgi.getInt(j));
                }
            }

            i++;
        }

        return new IntSeries[]{li.toSeries(), ri.toSeries()};
    }

    @Override
    protected IntSeries[] leftJoin(DataFrame lf, DataFrame rf) {

        IntAccum li = new IntAccum();
        IntAccum ri = new IntAccum();

        GroupBy rightIndex = rf.group(rightHasher);

        int i = 0;
        for (RowProxy lr : lf) {

            Object lKey = leftHasher.map(lr);
            IntSeries rgi = rightIndex.getGroupIndex(lKey);

            if (rgi != null) {
                int js = rgi.size();
                for (int j = 0; j < js; j++) {
                    li.pushInt(i);
                    ri.pushInt(rgi.getInt(j));
                }
            } else {
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

        GroupBy leftIndex = lf.group(leftHasher);

        int i = 0;
        for (RowProxy rr : rf) {

            Object rKey = rightHasher.map(rr);
            IntSeries lgi = leftIndex.getGroupIndex(rKey);

            if (lgi != null) {
                int js = lgi.size();
                for (int j = 0; j < js; j++) {
                    li.pushInt(lgi.getInt(j));
                    ri.pushInt(i);
                }
            } else {
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

        GroupBy rightIndex = rf.group(rightHasher);
        Set<Object> seenRightKeys = new LinkedHashSet<>();

        int i = 0;
        for (RowProxy lr : lf) {

            Object lKey = leftHasher.map(lr);

            IntSeries rgi = rightIndex.getGroupIndex(lKey);
            if (rgi != null) {
                seenRightKeys.add(lKey);
                int js = rgi.size();
                for (int j = 0; j < js; j++) {
                    li.pushInt(i);
                    ri.pushInt(rgi.getInt(j));
                }
            } else {
                li.pushInt(i);
                ri.pushInt(-1);
            }

            i++;
        }

        // add missing right rows
        for (Object key : rightIndex.getGroups()) {
            if (!seenRightKeys.contains(key)) {
                IntSeries rgi = rightIndex.getGroupIndex(key);

                int js = rgi.size();
                for (int j = 0; j < js; j++) {
                    li.pushInt(-1);
                    ri.pushInt(rgi.getInt(j));
                }
            }
        }

        return new IntSeries[]{li.toSeries(), ri.toSeries()};
    }
}
