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
import org.dflib.series.SingleValueSeries;

import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

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

    private boolean userColumns;
    private UnaryOperator<JoinIndex> colSelector = UnaryOperator.identity();

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

    public Join cols(int... columns) {
        this.colSelector = i -> i.cols(columns);
        this.userColumns = true;
        return this;
    }

    public Join cols(String... columns) {
        this.colSelector = i -> i.cols(columns);
        this.userColumns = true;
        return this;
    }

    public Join cols(Predicate<String> labelCondition) {
        this.colSelector = i -> i.cols(labelCondition);
        this.userColumns = true;
        return this;
    }

    public Join colsExcept(int... columns) {
        this.colSelector = i -> i.colsExcept(columns);
        this.userColumns = true;
        return this;
    }

    public Join colsExcept(String... columns) {
        this.colSelector = i -> i.colsExcept(columns);
        this.userColumns = true;
        return this;
    }

    public Join colsExcept(Predicate<String> labelCondition) {
        this.colSelector = i -> i.colsExcept(labelCondition);
        this.userColumns = true;
        return this;
    }

    public DataFrame select() {
        JoinIndex index = colSelector.apply(defaultJoinIndex());
        IntSeries[] selectors = rowSelectors();

        return new ColumnDataFrame(null,
                index.getIndex(),
                merge(selectors[0], selectors[1], index.getPositions()));
    }

    public DataFrame selectAs(UnaryOperator<String> renamer) {
        JoinIndex index = colSelector.apply(defaultJoinIndex());
        IntSeries[] selectors = rowSelectors();

        return new ColumnDataFrame(null,
                index.getIndex().rename(renamer),
                merge(selectors[0], selectors[1], index.getPositions()));
    }

    public DataFrame selectAs(String... newColumnNames) {
        JoinIndex index = colSelector.apply(defaultJoinIndex());
        IntSeries[] selectors = rowSelectors();

        return new ColumnDataFrame(null,
                index.getIndex().rename(newColumnNames),
                merge(selectors[0], selectors[1], index.getPositions()));
    }

    public DataFrame selectAs(Map<String, String> oldToNewNames) {
        JoinIndex index = colSelector.apply(defaultJoinIndex());
        IntSeries[] selectors = rowSelectors();

        return new ColumnDataFrame(null,
                index.getIndex().rename(oldToNewNames),
                merge(selectors[0], selectors[1], index.getPositions()));
    }

    public DataFrame select(Exp<?>... exps) {

        JoinIndex defaultIndex = defaultJoinIndex();
        JoinIndex resultIndex = colSelector.apply(defaultIndex);

        // if there was no explicit column set defined, exps can be of any width. If there was a selection, they must match
        if (userColumns) {
            int w = exps.length;
            if (w != resultIndex.size()) {
                throw new IllegalArgumentException(
                        "Can't perform 'select': Exp[] size is different from the ColumnSet size: " + w + " vs. " + resultIndex.size());
            }
        }

        JoinIndex allAliasesIndex = defaultIndex.colsExpandAliases();
        IntSeries[] selectors = rowSelectors();

        // Select the full DataFrame first, and then apply expressions, as exps can reference columns in the join that are not
        // a part of the result

        // Since "merge" doesn't check for duplicate columns, first merge unique columns, and then expand them to
        // include aliases via "pick"

        Series<?>[] uniqueColumns = merge(selectors[0], selectors[1], defaultIndex.getPositions());

        DataFrame allAliasesDf = new ColumnDataFrame(
                null,
                allAliasesIndex.getIndex(),
                pick(uniqueColumns, allAliasesIndex.getPositions()));

        return userColumns
                // picking cols by index instead of by positions to preserve the names
                ? allAliasesDf.cols(resultIndex.getIndex()).select(exps)

                // important to use no-arg "cols()" if no user columns were specified. Any other form of "cols()"
                // would explode due to "exps" and cols size mismatch
                : allAliasesDf.cols().select(exps);
    }

    private JoinIndex defaultJoinIndex() {
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
        int h = leftIndex.size();

        // We do not check for duplicate column positions here and recalculate each column. So to expand columns with
        // aliases, first do "merge" on a set of unique columns, and then call "pick" to arrange / duplicate columns.
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
                data[i] = new SingleValueSeries<>(null, h);
            }
        }

        return data;
    }

    private Series<?>[] pick(Series<?>[] cols, int[] positions) {

        int len = positions.length;

        Series[] data = new Series[len];
        for (int i = 0; i < len; i++) {
            data[i] = cols[positions[i]];
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
