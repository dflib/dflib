package com.nhl.dflib.join;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Hasher;
import com.nhl.dflib.Index;
import com.nhl.dflib.JoinType;

import java.util.Objects;

/**
 * @since 0.6
 */
public class HashJoinBuilder {

    private DataFrame leftFrame;
    private JoinType type;
    private Hasher leftHasher;
    private Hasher rightHasher;

    public HashJoinBuilder(DataFrame leftFrame) {
        this.leftFrame = Objects.requireNonNull(leftFrame);
        this.type = JoinType.inner;
    }

    public HashJoinBuilder type(JoinType type) {
        this.type = Objects.requireNonNull(type);
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

    public DataFrame with(DataFrame rightFrame) {

        Objects.requireNonNull(leftHasher);
        Objects.requireNonNull(rightHasher);

        HashJoiner joiner = new HashJoiner(leftHasher, rightHasher, type);
        JoinMerger merger = joiner.joinMerger(leftFrame, rightFrame);
        Index index = joiner.joinIndex(leftFrame.getColumnsIndex(), rightFrame.getColumnsIndex());
        return merger.join(index, leftFrame, rightFrame);
    }
}
