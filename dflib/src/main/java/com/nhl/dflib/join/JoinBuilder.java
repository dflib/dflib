package com.nhl.dflib.join;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Hasher;
import com.nhl.dflib.JoinType;

import java.util.Objects;

/**
 * @since 0.6
 */
public class JoinBuilder {

    private DataFrame leftFrame;
    private JoinType semantics;
    private Hasher leftHasher;
    private Hasher rightHasher;
    private JoinPredicate predicate;

    private String indicatorColumn;

    public JoinBuilder(DataFrame leftFrame) {
        this.leftFrame = Objects.requireNonNull(leftFrame);
        this.semantics = JoinType.inner;
    }

    public JoinBuilder type(JoinType type) {
        this.semantics = Objects.requireNonNull(type);
        return this;
    }

    public JoinBuilder on(int columnsIndex) {
        return on(columnsIndex, columnsIndex);
    }

    public JoinBuilder on(int leftColumn, int rightColumn) {
        return on(Hasher.of(leftColumn), Hasher.of(rightColumn));
    }

    public JoinBuilder on(String column) {
        return on(column, column);
    }

    public JoinBuilder on(String leftColumn, String rightColumn) {
        return on(Hasher.of(leftColumn), Hasher.of(rightColumn));
    }

    public JoinBuilder on(Hasher hasher) {
        return on(hasher, hasher);
    }

    public JoinBuilder on(Hasher left, Hasher right) {
        // append to the existing hashers
        this.leftHasher = combineHashers(this.leftHasher, left);
        this.rightHasher = combineHashers(this.rightHasher, right);
        this.predicate = null;
        return this;
    }

    /**
     * Sets the join condition to the specified predicate. This will result in a switch to the
     * <a href="https://en.wikipedia.org/wiki/Nested_loop_join">"nested loop join"</a> algorithm, which is rather slow,
     * exhibiting O(N*M) performance. Try to avoid it if possible, using various forms of "hash joins" instead, e.g.
     * {@link #on(String)} or {@link #on(Hasher, Hasher)}.
     *
     * @param predicate a join condition
     * @return this builder instance
     */
    public JoinBuilder predicatedBy(JoinPredicate predicate) {
        this.predicate = predicate;
        this.leftHasher = null;
        this.rightHasher = null;

        return this;
    }

    private Hasher combineHashers(Hasher possiblyNull, Hasher mustBeNotNull) {
        Objects.requireNonNull(mustBeNotNull);
        return possiblyNull != null ? possiblyNull.and(mustBeNotNull) : mustBeNotNull;
    }

    public JoinBuilder indicatorColumn(String name) {
        this.indicatorColumn = name;
        return this;
    }

    public DataFrame with(DataFrame rightFrame) {

        if (predicate != null) {
            return nestedLoopJoin(rightFrame);
        } else if (leftHasher != null && rightHasher != null) {
            return hashJoin(rightFrame);
        } else {
            throw new IllegalStateException("No join condition set. Either join columns / Hashers or a predicate must be specified");
        }
    }

    private DataFrame nestedLoopJoin(DataFrame rightFrame) {
        return new NestedLoopJoiner(predicate, semantics, indicatorColumn).join(leftFrame, rightFrame);
    }

    private DataFrame hashJoin(DataFrame rightFrame) {
        return new HashJoiner(leftHasher, rightHasher, semantics, indicatorColumn).join(leftFrame, rightFrame);
    }
}
