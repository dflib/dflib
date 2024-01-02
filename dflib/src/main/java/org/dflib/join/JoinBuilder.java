package org.dflib.join;

import org.dflib.ColumnDataFrame;
import org.dflib.DataFrame;
import org.dflib.Hasher;
import org.dflib.Index;
import org.dflib.IntSeries;
import org.dflib.JoinType;
import org.dflib.Series;
import org.dflib.builder.ObjectAccum;
import org.dflib.concat.HConcat;

import java.util.Objects;

/**
 * @since 0.6
 * @deprecated in favor of more advanced join builder obtained via {@link DataFrame#innerJoin(DataFrame)}, etc.
 */
@Deprecated(since = "1.0.0-M19", forRemoval = true)
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

        IntSeries[] conditions = rowConditions(rightFrame);

        DataFrame joined = merge(conditions[0], conditions[1], rightFrame);

        return indicatorColumn != null
                ? joined.addColumn(indicatorColumn, buildIndicator(conditions[0], conditions[1]))
                : joined;
    }

    private IntSeries[] rowConditions(DataFrame rightFrame) {
        if (predicate != null) {
            return new NestedLoopJoiner(predicate, semantics).rowSelectors(leftFrame, rightFrame);
        } else if (leftHasher != null && rightHasher != null) {
            return new HashJoiner(leftHasher, rightHasher, semantics).rowSelectors(leftFrame, rightFrame);
        } else {
            throw new IllegalStateException("No join condition set. Either join columns, Hashers or a predicate must be specified");
        }
    }

    protected Series<JoinIndicator> buildIndicator(IntSeries leftIndex, IntSeries rightIndex) {

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

    protected DataFrame merge(IntSeries leftIndex, IntSeries rightIndex, DataFrame rf) {

        Index index = joinIndex(leftFrame, rf);

        int w = index.size();
        int wl = leftFrame.width();

        Series[] data = new Series[w];

        for (int i = 0; i < wl; i++) {
            data[i] = leftFrame.getColumn(i).select(leftIndex);
        }

        for (int i = wl; i < w; i++) {
            data[i] = rf.getColumn(i - wl).select(rightIndex);
        }

        return new ColumnDataFrame(null, index, data);
    }

    protected Index joinIndex(DataFrame lf, DataFrame rf) {
        String lp = lf.getName() != null ? lf.getName() + "." : null;
        String rp = rf.getName() != null ? rf.getName() + "." : null;

        Index li = lp != null ? lf.getColumnsIndex().rename(s -> lp + s) : lf.getColumnsIndex();
        Index ri = rp != null ? rf.getColumnsIndex().rename(s -> rp + s) : rf.getColumnsIndex();

        return HConcat.zipIndex(li, ri.getLabels());
    }
}
