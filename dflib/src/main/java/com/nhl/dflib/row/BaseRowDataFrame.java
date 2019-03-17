package com.nhl.dflib.row;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.filter.RowPredicate;
import com.nhl.dflib.filter.ValuePredicate;
import com.nhl.dflib.join.JoinType;
import com.nhl.dflib.map.RowCombiner;
import com.nhl.dflib.map.RowMapper;
import com.nhl.dflib.map.RowToValueMapper;
import com.nhl.dflib.print.InlinePrinter;
import com.nhl.dflib.sort.Sorters;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

/**
 * Superclass of row-oriented DataFrames. Also provides static factory methods for common row-oriented DataFrames.
 */
public abstract class BaseRowDataFrame implements DataFrame {

    protected Index columns;

    public BaseRowDataFrame(Index columns) {
        this.columns = Objects.requireNonNull(columns);
    }

    /**
     * Creates a DataFrame by folding the provided stream of objects into rows and columns row by row.
     */
    public static <T> DataFrame fromStreamFoldByRow(Index columns, Stream<T> stream) {
        int width = columns.size();
        if (width == 0) {
            throw new IllegalArgumentException("Empty columns");
        }

        List<Object[]> folded = new ArrayList<>();
        Iterator<T> it = stream.iterator();

        int i = 0;
        Object[] row = null;

        while (it.hasNext()) {

            // first iteration
            if (row == null) {
                row = new Object[width];
            }
            // previous row finished
            else if (i % width == 0) {
                folded.add(row);
                row = new Object[width];
            }

            row[i % width] = it.next();
            i++;
        }

        // add last row
        folded.add(row);

        return new RowDataFrame(columns, folded);
    }

    /**
     * Creates a DataFrame by folding the provided array of objects into rows and columns row by row.
     */
    public static DataFrame fromSequenceFoldByRow(Index columns, Object... sequence) {
        int width = columns.size();
        int rows = sequence.length / width;

        List<Object[]> folded = new ArrayList<>(rows + 1);
        for (int i = 0; i < rows; i++) {
            Object[] row = new Object[width];
            System.arraycopy(sequence, i * width, row, 0, width);
            folded.add(row);
        }

        // copy partial last row
        int leftover = sequence.length % width;
        if (leftover > 0) {
            Object[] row = new Object[width];
            System.arraycopy(sequence, rows * width, row, 0, leftover);
            folded.add(row);
        }

        return new RowDataFrame(columns, folded);
    }

    public static DataFrame fromRows(Index columns, Object[]... rows) {
        return new RowDataFrame(columns, asList(rows));
    }

    public static DataFrame fromListOfRows(Index columns, List<Object[]> sources) {
        return new RowDataFrame(columns, sources);
    }

    public static DataFrame fromRows(Index columns, Iterable<Object[]> source) {
        return new IterableRowDataFrame(columns, source);
    }

    /**
     * Creates a DataFrame from an iterable over arbitrary objects. Each object will be converted to a row by applying
     * a function passed as the last argument.
     */
    public static <T> DataFrame fromObjects(Index columns, Iterable<T> rows, Function<T, Object[]> rowMapper) {
        return new IterableRowDataFrame(columns, new TransformingIterable<>(rows, rowMapper)).materialize();
    }

    @Override
    public DataFrame dropColumns(String... columnNames) {
        Index index = getColumns();
        Index newIndex = index.dropNames(columnNames);

        // if no columns were dropped (e.g. the names didn't match anything
        if (newIndex.size() == index.size()) {
            return this;
        }

        return new MaterializableRowDataFrame(newIndex, this);
    }

    @Override
    public DataFrame filter(RowPredicate p) {
        return new FilteredRowDataFrame(this, p).materialize();
    }

    @Override
    public <V> DataFrame filterByColumn(int columnPos, ValuePredicate<V> p) {
        RowPredicate drp = r -> p.test((V) r.get(columnPos));
        return new FilteredRowDataFrame(this, drp).materialize();
    }

    @Override
    public Index getColumns() {
        return columns;
    }

    @Override
    public DataFrame hConcat(Index zippedColumns, JoinType how, DataFrame df, RowCombiner c) {
        return new HConcatRowDataFrame(zippedColumns, how, this, df, c).materialize();
    }

    @Override
    public DataFrame head(int len) {
        return new HeadRowDataFrame(this, len);
    }

    /**
     * Returns the number of rows in this DataFrame. Aka the DataFrame "height". Note that depending on the type of
     * the DataFrame this operation may or may not be constant speed. In the worst case it would require a full scan
     * through all rows.
     *
     * @return an int indicating the number of rows in the DataFrame
     */
    @Override
    public int height() {

        // not a very efficient implementation; implementors should provide faster versions when possible
        int count = 0;
        Iterator<?> it = iterator();
        while (it.hasNext()) {
            it.next();
            count++;
        }

        return count;
    }

    @Override
    public DataFrame map(Index mappedColumns, RowMapper rowMapper) {
        return new MappedRowDataFrame(mappedColumns, this, rowMapper).materialize();
    }

    /**
     * Resolves this DataFrame to an implementation that evaluates internal mapping/concat/filter functions no more
     * than once, reusing the first evaluation result for subsequent iterations. Certain operations in DataFrame, such as
     * {@link #map(Index, RowMapper)}, etc. are materialized by default.
     *
     * @return a DataFrame optimized for multiple iterations, calls to {@link #height()}, etc.
     */
    @Override
    public DataFrame materialize() {
        return new MaterializableRowDataFrame(this);
    }

    @Override
    public DataFrame renameColumns(String... columnNames) {
        Index renamed = getColumns().rename(columnNames);
        return new MaterializableRowDataFrame(renamed, this);
    }

    @Override
    public DataFrame renameColumns(Map<String, String> oldToNewNames) {
        Index renamed = getColumns().rename(oldToNewNames);
        return new MaterializableRowDataFrame(renamed, this);
    }

    @Override
    public DataFrame selectColumns(String... columnNames) {
        Index select = getColumns().selectNames(columnNames);
        return new MaterializableRowDataFrame(select, this);
    }

    @Override
    public <V extends Comparable<? super V>> DataFrame sort(RowToValueMapper<V> sortKeyExtractor) {
        Comparator<RowProxy> rowComparator = Sorters.sorter(sortKeyExtractor);
        return new SortedRowDataFrame(this, toArrayComparator(rowComparator));
    }

    @Override
    public <V extends Comparable<? super V>> DataFrame sortByColumns(String... columns) {
        if (columns.length == 0) {
            return this;
        }

        Comparator<RowProxy> rowComparator = Sorters.sorter(getColumns(), columns);
        return new SortedRowDataFrame(this, toArrayComparator(rowComparator));
    }

    @Override
    public <V extends Comparable<? super V>> DataFrame sortByColumns(int... columns) {
        if (columns.length == 0) {
            return this;
        }

        Comparator<RowProxy> rowComparator = Sorters.sorter(getColumns(), columns);
        return new SortedRowDataFrame(this, toArrayComparator(rowComparator));
    }

    private Comparator<Object[]> toArrayComparator(Comparator<RowProxy> rowComparator) {
        ArrayRowProxy rp1 = new ArrayRowProxy(columns);
        ArrayRowProxy rp2 = new ArrayRowProxy(columns);
        return (a1, a2) -> rowComparator.compare(rp1.reset(a1), rp2.reset(a2));
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder(getClass().getSimpleName()).append(" [");
        return InlinePrinter
                .getInstance()
                .print(out, this).append("]")
                .toString();
    }
}
