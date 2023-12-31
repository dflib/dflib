package com.nhl.dflib.join;

import com.nhl.dflib.ColumnDataFrame;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Hasher;
import com.nhl.dflib.Index;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.JoinType;
import com.nhl.dflib.Series;
import com.nhl.dflib.builder.ObjectAccum;
import com.nhl.dflib.series.IndexedSeries;

import java.util.Map;
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
        Index index = JoinIndexer.simpleIndex(leftFrame, rightFrame, indicatorColumn);
        IntSeries[] selectors = rowSelectors();
        Series<?>[] data = merge(selectors[0], selectors[1]);

        return new ColumnDataFrame(null, index, data);
    }

    public DataFrame select(int... columns) {
        Index index = JoinIndexer.simpleIndex(leftFrame, rightFrame, indicatorColumn);
        IntSeries[] selectors = rowSelectors();

        return new ColumnDataFrame(null,
                index.selectPositions(columns),
                merge(selectors[0], selectors[1], columns));
    }

    public DataFrame select(String... columns) {
        Map<String, Integer> positions = JoinIndexer.multiLabelPositions(leftFrame, rightFrame, indicatorColumn);
        int len = columns.length;
        int[] columnsPos = new int[len];

        for (int i = 0; i < len; i++) {
            Integer pos = positions.get(columns[i]);
            if (pos == null) {
                throw new IllegalArgumentException("Unknown join column reference: " + columns[i]);
            }

            columnsPos[i] = pos;
        }

        return select(columnsPos)
                // renaming is counterintuitive, but needed to produce column labels matching the method
                // arguments in respect to label prefixes (e.g. "a_" -> "a_" instead of "df1.a")
                .cols().rename(columns);
    }

    public DataFrame selectExcept(int... columns) {
        Index index = JoinIndexer.simpleIndex(leftFrame, rightFrame, indicatorColumn);
        int[] includeColumns = index.positionsExcept(columns);

        IntSeries[] selectors = rowSelectors();

        return new ColumnDataFrame(null,
                index.selectPositions(includeColumns),
                merge(selectors[0], selectors[1], includeColumns));
    }

    public DataFrame selectExcept(String... columns) {

        Index index = JoinIndexer.simpleIndex(leftFrame, rightFrame, indicatorColumn);
        int[] includeColumns = index.positionsExcept(columns);

        IntSeries[] selectors = rowSelectors();

        return new ColumnDataFrame(null,
                index.selectPositions(includeColumns),
                merge(selectors[0], selectors[1], includeColumns));
    }

    public DataFrame select(Predicate<String> labelCondition) {

        Index index = JoinIndexer.simpleIndex(leftFrame, rightFrame, indicatorColumn);
        int[] includeColumns = index.positions(labelCondition);

        IntSeries[] selectors = rowSelectors();

        return new ColumnDataFrame(null,
                index.selectPositions(includeColumns),
                merge(selectors[0], selectors[1], includeColumns));
    }

    public DataFrame select(Exp<?>... exps) {
        MultiNameIndex index = JoinIndexer.multiNameIndex(leftFrame, rightFrame, indicatorColumn);
        IntSeries[] selectors = rowSelectors();
        Series<?>[] data = merge(selectors[0], selectors[1]);

        return new ColumnDataFrame(null, index, data).cols().select(exps);
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

    private Series<?>[] merge(IntSeries leftIndex, IntSeries rightIndex) {

        int llen = leftFrame.width();
        int rlen = rightFrame.width();
        int len = indicatorColumn != null
                ? llen + rlen + 1
                : llen + rlen;

        Series[] data = new Series[len];

        for (int i = 0; i < llen; i++) {
            data[i] = new IndexedSeries<>(leftFrame.getColumn(i), leftIndex);
        }

        for (int i = 0; i < rlen; i++) {
            data[llen + i] = new IndexedSeries<>(rightFrame.getColumn(i), rightIndex);
        }

        if (indicatorColumn != null) {
            data[llen + rlen] = buildIndicator(leftIndex, rightIndex);
        }

        return data;
    }

    private Series<?>[] merge(IntSeries leftIndex, IntSeries rightIndex, int[] columns) {

        int llen = leftFrame.width();
        int rlen = rightFrame.width();
        int lrlen = llen + rlen;
        int len = columns.length;

        Series[] data = new Series[len];
        for (int i = 0; i < len; i++) {
            int si = columns[i];
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
