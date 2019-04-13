package com.nhl.dflib.join;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.GroupBy;
import com.nhl.dflib.Index;
import com.nhl.dflib.collection.IntMutableList;
import com.nhl.dflib.concat.HConcat;
import com.nhl.dflib.map.Hasher;
import com.nhl.dflib.row.RowProxy;
import com.nhl.dflib.series.IntSeries;

import java.util.LinkedHashSet;
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

        IntMutableList li = new IntMutableList();
        IntMutableList ri = new IntMutableList();

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

        return new JoinMerger(li.toSeries(), ri.toSeries());
    }

    private JoinMerger leftJoin(DataFrame lf, DataFrame rf) {

        IntMutableList li = new IntMutableList();
        IntMutableList ri = new IntMutableList();

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

        return new JoinMerger(li.toSeries(), ri.toSeries());
    }

    private JoinMerger rightJoin(DataFrame lf, DataFrame rf) {

        IntMutableList li = new IntMutableList();
        IntMutableList ri = new IntMutableList();

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

        return new JoinMerger(li.toSeries(), ri.toSeries());
    }

    private JoinMerger fullJoin(DataFrame lf, DataFrame rf) {

        IntMutableList li = new IntMutableList();
        IntMutableList ri = new IntMutableList();

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

        return new JoinMerger(li.toSeries(), ri.toSeries());
    }
}
