package org.dflib.join;

import org.dflib.ColumnDataFrame;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Hasher;
import org.dflib.IntSeries;
import org.dflib.JoinType;
import org.dflib.Series;
import org.dflib.builder.ObjectAccum;
import org.dflib.series.IndexedSeries;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Join builder
 *
 * @since 1.0.0-M19
 */
public class Join {

    // TODO: change to multi-join of an arbitrary number of frames
    private final JoinType type;
    private final DataFrame leftFrame;
    private final DataFrame rightFrame;

    private Hasher leftHasher;
    private Hasher rightHasher;
    private JoinPredicate predicate;
    private String indicatorColumn;

    public Join(JoinType type, DataFrame leftFrame, DataFrame rightFrame) {
        this.type = type;
        this.leftFrame = leftFrame;
        this.rightFrame = rightFrame;
    }

    public Join on(int columnsIndex) {
        return on(columnsIndex, columnsIndex);
    }

    public Join on(int leftColumn, int rightColumn) {
        return on(Hasher.of(leftColumn), Hasher.of(rightColumn));
    }

    public Join on(String column) {
        return on(column, column);
    }

    public Join on(String leftColumn, String rightColumn) {
        return on(Hasher.of(leftColumn), Hasher.of(rightColumn));
    }

    public Join on(Hasher hasher) {
        return on(hasher, hasher);
    }

    public Join on(Hasher left, Hasher right) {
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
    public Join predicatedBy(JoinPredicate predicate) {
        this.predicate = predicate;
        this.leftHasher = null;
        this.rightHasher = null;

        return this;
    }

    public Join indicatorColumn(String name) {
        this.indicatorColumn = name;
        return this;
    }

    public DataFrame select() {
        JoinIndex index = createJoinIndex();
        IntSeries[] selectors = rowSelectors();

        return new ColumnDataFrame(null,
                index.getIndex(),
                merge(selectors[0], selectors[1], index.getPositions()));
    }

    public DataFrame select(int... positions) {
        JoinIndex index = createJoinIndex().select(positions);
        IntSeries[] selectors = rowSelectors();

        return new ColumnDataFrame(null,
                index.getIndex(),
                merge(selectors[0], selectors[1], index.getPositions()));
    }

    public DataFrame select(String... columns) {
        JoinIndex index = createJoinIndex().select(columns);
        IntSeries[] selectors = rowSelectors();

        return new ColumnDataFrame(null,
                index.getIndex(),
                merge(selectors[0], selectors[1], index.getPositions()));
    }

    public DataFrame selectExcept(Predicate<String> labelCondition) {
        // due to aliases "selectExcept(c)" is not the same as "select(!c)", so need to process it explicitly

        JoinIndex index = createJoinIndex().selectExcept(labelCondition);
        IntSeries[] selectors = rowSelectors();

        return new ColumnDataFrame(null,
                index.getIndex(),
                merge(selectors[0], selectors[1], index.getPositions()));
    }

    public DataFrame selectExcept(int... columns) {
        JoinIndex index = createJoinIndex().selectExcept(columns);
        IntSeries[] selectors = rowSelectors();

        return new ColumnDataFrame(null,
                index.getIndex(),
                merge(selectors[0], selectors[1], index.getPositions()));
    }

    public DataFrame selectExcept(String... columns) {
        JoinIndex index = createJoinIndex().selectExcept(columns);
        IntSeries[] selectors = rowSelectors();

        return new ColumnDataFrame(null,
                index.getIndex(),
                merge(selectors[0], selectors[1], index.getPositions()));
    }

    public DataFrame select(Predicate<String> labelCondition) {
        JoinIndex index = createJoinIndex().select(labelCondition);
        IntSeries[] selectors = rowSelectors();

        return new ColumnDataFrame(null,
                index.getIndex(),
                merge(selectors[0], selectors[1], index.getPositions()));
    }

    public DataFrame select(Exp<?>... exps) {
        JoinIndex index = createJoinIndex().selectAllAliases();
        IntSeries[] selectors = rowSelectors();

        return new ColumnDataFrame(null,
                index.getIndex(),
                merge(selectors[0], selectors[1], index.getPositions())).cols().select(exps);
    }

    private JoinIndex createJoinIndex() {
        return JoinIndex.of(
                leftFrame.getName(),
                rightFrame.getName(),
                leftFrame.getColumnsIndex(),
                rightFrame.getColumnsIndex(),
                indicatorColumn);
    }

    private Hasher combineHashers(Hasher possiblyNull, Hasher mustBeNotNull) {
        Objects.requireNonNull(mustBeNotNull);
        return possiblyNull != null ? possiblyNull.and(mustBeNotNull) : mustBeNotNull;
    }

    private IntSeries[] rowSelectors() {
        if (predicate != null) {
            return new NestedLoopJoiner(predicate, type).rowSelectors(leftFrame, rightFrame);
        } else if (leftHasher != null && rightHasher != null) {
            return new HashJoiner(leftHasher, rightHasher, type).rowSelectors(leftFrame, rightFrame);
        } else {
            throw new IllegalStateException("No join condition set. Either join columns, Hashers or a predicate must be specified");
        }
    }

    private Series<?>[] merge(IntSeries leftIndex, IntSeries rightIndex, int[] positions) {

        int llen = leftFrame.width();
        int rlen = rightFrame.width();
        int lrlen = llen + rlen;
        int len = positions.length;

        Series[] data = new Series[len];
        for (int i = 0; i < len; i++) {
            int si = positions[i];
            if (si < llen) {
                data[i] = new IndexedSeries<>(leftFrame.getColumn(si), leftIndex);
            } else if (si < lrlen) {
                data[i] = new IndexedSeries<>(rightFrame.getColumn(si - llen), rightIndex);
            } else if (si == lrlen && indicatorColumn != null) {
                data[i] = buildIndicator(leftIndex, rightIndex);
            } else {
                throw new IllegalArgumentException("Join result index is out of bounds: " + si);
            }
        }

        return data;
    }
    
    private Series<JoinIndicator> buildIndicator(IntSeries leftIndex, IntSeries rightIndex) {

        int h = leftIndex.size();
        ObjectAccum<JoinIndicator> appender = new ObjectAccum<>(h);

        for (int i = 0; i < h; i++) {
            appender.push(
                    leftIndex.getInt(i) < 0
                            ? JoinIndicator.right_only
                            : rightIndex.getInt(i) < 0 ? JoinIndicator.left_only : JoinIndicator.both
            );
        }

        return appender.toSeries();
    }
}
