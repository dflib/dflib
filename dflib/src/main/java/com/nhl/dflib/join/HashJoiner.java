package com.nhl.dflib.join;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.GroupBy;
import com.nhl.dflib.Hasher;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.JoinType;
import com.nhl.dflib.seriesbuilder.IntAccumulator;
import com.nhl.dflib.row.RowProxy;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A DataFrame joiner using <a href="https://en.wikipedia.org/wiki/Hash_join">"hash join"</a> algorithm. It requires
 * two custom "hash" functions for the rows on the left and the right sides of the join, each producing values, whose
 * equality can be used as a join condition. Should theoretically have O(N + M) performance.
 */
public class HashJoiner extends BaseJoiner {

    private Hasher leftHasher;
    private Hasher rightHasher;

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

        IntAccumulator li = new IntAccumulator();
        IntAccumulator ri = new IntAccumulator();

        GroupBy rightIndex = rf.group(rightHasher);

        int i = 0;
        for (RowProxy lr : lf) {

            Object lKey = leftHasher.map(lr);
            IntSeries rgi = rightIndex.getGroupIndex(lKey);

            if (rgi != null) {
                int js = rgi.size();
                for (int j = 0; j < js; j++) {
                    li.add(i);
                    ri.add(rgi.getInt(j));
                }
            }

            i++;
        }

        return new IntSeries[]{li.toIntSeries(), ri.toIntSeries()};
    }

    @Override
    protected IntSeries[] leftJoin(DataFrame lf, DataFrame rf) {

        IntAccumulator li = new IntAccumulator();
        IntAccumulator ri = new IntAccumulator();

        GroupBy rightIndex = rf.group(rightHasher);

        int i = 0;
        for (RowProxy lr : lf) {

            Object lKey = leftHasher.map(lr);
            IntSeries rgi = rightIndex.getGroupIndex(lKey);

            if (rgi != null) {
                int js = rgi.size();
                for (int j = 0; j < js; j++) {
                    li.add(i);
                    ri.add(rgi.getInt(j));
                }
            } else {
                li.add(i);
                ri.add(-1);
            }

            i++;
        }

        return new IntSeries[]{li.toIntSeries(), ri.toIntSeries()};
    }

    @Override
    protected IntSeries[] rightJoin(DataFrame lf, DataFrame rf) {

        IntAccumulator li = new IntAccumulator();
        IntAccumulator ri = new IntAccumulator();

        GroupBy leftIndex = lf.group(leftHasher);

        int i = 0;
        for (RowProxy rr : rf) {

            Object rKey = rightHasher.map(rr);
            IntSeries lgi = leftIndex.getGroupIndex(rKey);

            if (lgi != null) {
                int js = lgi.size();
                for (int j = 0; j < js; j++) {
                    li.add(lgi.getInt(j));
                    ri.add(i);
                }
            } else {
                li.add(-1);
                ri.add(i);
            }

            i++;
        }

        return new IntSeries[]{li.toIntSeries(), ri.toIntSeries()};
    }

    @Override
    protected IntSeries[] fullJoin(DataFrame lf, DataFrame rf) {

        IntAccumulator li = new IntAccumulator();
        IntAccumulator ri = new IntAccumulator();

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
                    li.add(i);
                    ri.add(rgi.getInt(j));
                }
            } else {
                li.add(i);
                ri.add(-1);
            }

            i++;
        }

        // add missing right rows
        for (Object key : rightIndex.getGroups()) {
            if (!seenRightKeys.contains(key)) {
                IntSeries rgi = rightIndex.getGroupIndex(key);

                int js = rgi.size();
                for (int j = 0; j < js; j++) {
                    li.add(-1);
                    ri.add(rgi.getInt(j));
                }
            }
        }

        return new IntSeries[]{li.toIntSeries(), ri.toIntSeries()};
    }
}
