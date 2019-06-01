package com.nhl.dflib.join;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Hasher;
import com.nhl.dflib.Index;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.JoinType;
import com.nhl.dflib.Series;
import com.nhl.dflib.collection.MutableList;

import java.util.Objects;

/**
 * @since 0.6
 */
public class HashJoinBuilder {

    private DataFrame leftFrame;
    private JoinType semantics;
    private Hasher leftHasher;
    private Hasher rightHasher;
    private String indicatorColumn;

    public HashJoinBuilder(DataFrame leftFrame) {
        this.leftFrame = Objects.requireNonNull(leftFrame);
        this.semantics = JoinType.inner;
    }

    public HashJoinBuilder type(JoinType type) {
        this.semantics = Objects.requireNonNull(type);
        return this;
    }

    public HashJoinBuilder on(int columnsIndex) {
        return on(columnsIndex, columnsIndex);
    }

    public HashJoinBuilder on(int leftColumn, int rightColumn) {
        return on(Hasher.forColumn(leftColumn), Hasher.forColumn(rightColumn));
    }

    public HashJoinBuilder on(String column) {
        return on(column, column);
    }

    public HashJoinBuilder on(String leftColumn, String rightColumn) {
        return on(Hasher.forColumn(leftColumn), Hasher.forColumn(rightColumn));
    }

    public HashJoinBuilder on(Hasher hasher) {
        return on(hasher, hasher);
    }

    public HashJoinBuilder on(Hasher left, Hasher right) {
        // append to the existing hashers
        this.leftHasher = combine(this.leftHasher, left);
        this.rightHasher = combine(this.rightHasher, right);
        return this;
    }

    private Hasher combine(Hasher possiblyNull, Hasher mustBeNotNull) {
        Objects.requireNonNull(mustBeNotNull);
        return possiblyNull != null ? possiblyNull.and(mustBeNotNull) : mustBeNotNull;
    }

    public HashJoinBuilder indicatorColumn(String name) {
        this.indicatorColumn = name;
        return this;
    }

    public DataFrame with(DataFrame rightFrame) {

        Objects.requireNonNull(leftHasher);
        Objects.requireNonNull(rightHasher);

        HashJoiner joiner = new HashJoiner(leftHasher, rightHasher, semantics);
        IntSeries[] indexPair = joiner.joinLeftRightIndices(leftFrame, rightFrame);
        Index index = joiner.joinIndex(leftFrame.getColumnsIndex(), rightFrame.getColumnsIndex());

        DataFrame joined = new JoinMerger(indexPair[0], indexPair[1]).join(index, leftFrame, rightFrame);

        return indicatorColumn != null
                ? joined.addColumn(indicatorColumn, buildIndicator(indexPair[0], indexPair[1]))
                : joined;
    }

    private Series<JoinIndicator> buildIndicator(IntSeries leftIndex, IntSeries rightIndex) {

        int h = leftIndex.size();
        MutableList<JoinIndicator> appender = new MutableList<>(h);

        for (int i = 0; i < h; i++) {
            appender.add(
                    leftIndex.getInt(i) < 0
                            ? JoinIndicator.right_only
                            : rightIndex.getInt(i) < 0 ? JoinIndicator.left_only : JoinIndicator.both
            );
        }

        return appender.toSeries();
    }
}
