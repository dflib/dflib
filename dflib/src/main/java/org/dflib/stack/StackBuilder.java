package org.dflib.stack;

import org.dflib.ColumnDataFrame;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.concat.SeriesConcat;
import org.dflib.series.IndexedSeries;
import org.dflib.series.IntSequenceSeries;
import org.dflib.series.SingleValueSeries;

import java.util.Arrays;

/**
 * Configures and executes a "stack" operation.
 *
 * @since 2.0.0
 */
public class StackBuilder {

    private static final String ROW_LABEL = "row";
    private static final String COLUMN_LABEL = "column";
    private static final String VALUE_LABEL = "value";

    private final DataFrame dataFrame;

    private boolean includeNulls;
    private int columnForRows;

    public StackBuilder(DataFrame dataFrame) {
        this.dataFrame = dataFrame;
        this.columnForRows = -1;
    }

    /**
     * Returns a new DataFrame with 3 columns. By default, they are called "row", "column", "value". The "row" column
     * would contain value indices in the source. "column" and "value" will contain source column names and their values
     * for all columns of the source DataFrame.
     *
     * <p>If the "rows(..)" column was explicitly provided, the first column will
     * use that name (instead of "rows"), and the values will come from it instead of simple numeric indices. That
     * column will also be excluded from the data in the "column" and "value" columns</p>
     */
    public DataFrame select() {
        if (includeNulls) {
            return columnForRows >= 0 ? rowsNulls() : nulls();
        } else {
            return columnForRows >= 0 ? rowsNoNulls() : noNulls();
        }
    }

    private Index stackedIndex() {
        String col0 = columnForRows >= 0 ? dataFrame.getColumnsIndex().get(columnForRows) : ROW_LABEL;
        return Index.of(col0, COLUMN_LABEL, VALUE_LABEL);
    }

    public StackBuilder includeNulls() {
        this.includeNulls = true;
        return this;
    }

    public StackBuilder rows(String columnForRows) {
        this.columnForRows = validateColumn(columnForRows);
        return this;
    }

    public StackBuilder rows(int columnForRows) {
        this.columnForRows = validateColumn(columnForRows);
        return this;
    }

    private int validateColumn(String name) {
        // side effect of "position" is to validate "name" presence
        return dataFrame.getColumnsIndex().position(name);
    }

    private int validateColumn(int pos) {
        if (pos < 0) {
            throw new IllegalArgumentException("Negative column position: " + pos);
        }

        if (dataFrame.width() <= pos) {
            throw new IllegalArgumentException("Column position " + pos + " is out of bounds. DataFrame width: " + dataFrame.width());
        }

        return pos;
    }

    private DataFrame noNulls() {
        Index columnsIndex = dataFrame.getColumnsIndex();
        int w = columnsIndex.size();

        IntSeries[] rows = new IntSeries[w];
        Series<String>[] columns = new Series[w];
        Series<Object>[] values = new Series[w];

        for (int i = 0; i < w; i++) {
            IntSeries noNullsIndex = dataFrame.getColumn(i).index(v -> v != null);

            rows[i] = noNullsIndex;
            columns[i] = new SingleValueSeries<>(columnsIndex.get(i), noNullsIndex.size());
            values[i] = new IndexedSeries<>(dataFrame.getColumn(i), noNullsIndex);
        }

        return new ColumnDataFrame(null, stackedIndex(),
                SeriesConcat.intConcat(rows),
                SeriesConcat.concat(columns),
                SeriesConcat.concat(values)
        );
    }

    private DataFrame rowsNoNulls() {
        Index columnsIndex = dataFrame.getColumnsIndex();
        int w = columnsIndex.size();
        int rw = w - 1;

        Series<Object>[] rows = new Series[rw];
        Series<String>[] columns = new Series[rw];
        Series<Object>[] values = new Series[rw];

        Series<Object> rowsCol = dataFrame.getColumn(columnForRows);

        for (int i = 0, j = 0; i < w; i++) {

            if (columnForRows == i) {
                continue;
            }

            IntSeries noNullsIndex = dataFrame.getColumn(i).index(v -> v != null);

            rows[j] = new IndexedSeries<>(rowsCol, noNullsIndex);
            columns[j] = new SingleValueSeries<>(columnsIndex.get(i), noNullsIndex.size());
            values[j] = new IndexedSeries<>(dataFrame.getColumn(i), noNullsIndex);
            j++;
        }

        return new ColumnDataFrame(null, stackedIndex(),
                SeriesConcat.concat(rows),
                SeriesConcat.concat(columns),
                SeriesConcat.concat(values)
        );
    }

    private DataFrame nulls() {
        Index columnsIndex = dataFrame.getColumnsIndex();
        int w = columnsIndex.size();
        int h = dataFrame.height();

        IntSeries[] rows = new IntSeries[w];
        Series<Object>[] values = new Series[w];
        // "columns" can be built without concat
        String[] columns = new String[w * h];

        IntSeries sequence = new IntSequenceSeries(0, h);

        for (int i = 0; i < w; i++) {
            rows[i] = sequence;
            int start = i * h;
            Arrays.fill(columns, start, start + h, columnsIndex.get(i));
            values[i] = dataFrame.getColumn(i);
        }

        return new ColumnDataFrame(null, stackedIndex(),
                SeriesConcat.intConcat(rows),
                Series.of(columns),
                SeriesConcat.concat(values)
        );
    }

    private DataFrame rowsNulls() {
        Index columnsIndex = dataFrame.getColumnsIndex();
        int w = columnsIndex.size();
        int rw = w - 1;
        int h = dataFrame.height();

        Series<Object>[] rows = new Series[rw];
        Series<Object>[] values = new Series[rw];
        // "columns" can be built without concat
        String[] columns = new String[rw * h];

        Series<Object> rowsCol = dataFrame.getColumn(columnForRows);

        for (int i = 0, j = 0; i < w; i++) {
            if (columnForRows == i) {
                continue;
            }

            rows[j] = rowsCol;
            int start = j * h;
            Arrays.fill(columns, start, start + h, columnsIndex.get(i));
            values[j] = dataFrame.getColumn(i);
            j++;
        }

        return new ColumnDataFrame(null, stackedIndex(),
                SeriesConcat.concat(rows),
                Series.of(columns),
                SeriesConcat.concat(values)
        );
    }
}
