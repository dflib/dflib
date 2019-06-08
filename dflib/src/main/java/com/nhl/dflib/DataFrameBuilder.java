package com.nhl.dflib;

import com.nhl.dflib.series.ArraySeries;
import com.nhl.dflib.series.builder.ObjectAccumulator;
import com.nhl.dflib.series.builder.SeriesBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Assembles a DataFrame from various in-memory data structures. Usually created via {@link DataFrame#newFrame(Index)}
 * or {@link DataFrame#newFrame(String...)}.
 *
 * @since 0.6
 */
public class DataFrameBuilder {

    private Index columnsIndex;

    protected DataFrameBuilder(Index columnsIndex) {
        this.columnsIndex = Objects.requireNonNull(columnsIndex);
    }

    public static DataFrameBuilder builder(String... columnLabels) {
        return builder(Index.forLabels(Objects.requireNonNull(columnLabels)));
    }

    public static DataFrameBuilder builder(Index columnsIndex) {
        return new DataFrameBuilder(columnsIndex);
    }

    public DataFrame empty() {
        return new ColumnDataFrame(columnsIndex);
    }

    public DataFrame columns(Series<?>... columns) {
        Objects.requireNonNull(columns);
        return new ColumnDataFrame(columnsIndex, columns);
    }

    public DataFrame rows(Object[]... rows) {
        int w = columnsIndex.size();
        int h = rows.length;

        // convert array of rows into an array of columns
        Object[][] columnarData = new Object[w][h];
        for (int i = 0; i < h; i++) {

            if (rows[i].length < w) {
                throw new IllegalArgumentException("Row must be at least " + w + " elements long: " + rows[i].length);
            }

            for (int j = 0; j < w; j++) {
                columnarData[j][i] = rows[i][j];
            }
        }

        return fromColumnarData(columnarData);
    }

    public DataFrameByRowBuilder addRow(Object... row) {
        return new DataFrameByRowBuilder(columnsIndex).addRow(row);
    }

    public DataFrame foldByRow(Object... data) {

        int width = columnsIndex.size();
        if (width == 0) {
            throw new IllegalArgumentException("Empty columns");
        }

        int lastRowWidth = data.length % width;

        int minHeight = data.length / width;
        int fullHeight = lastRowWidth > 0 ? minHeight + 1 : minHeight;

        Object[][] columnarData = new Object[width][fullHeight];

        for (int i = 0; i < minHeight; i++) {
            for (int j = 0; j < width; j++) {
                columnarData[j][i] = data[i * width + j];
            }
        }

        if (lastRowWidth > 0) {
            int lastRowIndex = minHeight;
            for (int j = 0; j < lastRowWidth; j++) {
                columnarData[j][lastRowIndex] = data[lastRowIndex * width + j];
            }
        }

        return fromColumnarData(columnarData);
    }

    public DataFrame foldByColumn(Object... data) {

        int w = columnsIndex.size();
        if (w == 0) {
            throw new IllegalArgumentException("Empty columns");
        }

        int missingInLastColumn = data.length % w;
        boolean partialLastColumn = missingInLastColumn > 0;
        int fullColumnsW = partialLastColumn
                ? w - 1
                : w;

        int h = partialLastColumn
                ? 1 + data.length / w
                : data.length / w;

        Object[][] columnarData = new Object[w][h];

        for (int i = 0; i < fullColumnsW; i++) {
            System.arraycopy(data, i * h, columnarData[i], 0, h);
        }

        if (partialLastColumn) {
            System.arraycopy(data, fullColumnsW * h, columnarData[fullColumnsW], 0, h - missingInLastColumn);
        }

        return fromColumnarData(columnarData);
    }

    public <T> DataFrame foldStreamByRow(Stream<T> stream) {

        int width = columnsIndex.size();
        if (width == 0) {
            throw new IllegalArgumentException("Empty columns");
        }

        List<Object>[] data = new List[width];
        for (int i = 0; i < width; i++) {
            data[i] = new ArrayList<>();
        }

        Iterator<T> it = stream.iterator();

        int p = 0;
        while (it.hasNext()) {
            data[p % width].add(it.next());
            p++;
        }

        // 'height' is max height; some columns may be smaller
        int height = data[0].size();
        Series[] columnsData = new Series[width];
        for (int i = 0; i < width; i++) {
            Object[] columnData = new Object[height];
            data[i].toArray(columnData);
            columnsData[i] = new ArraySeries(columnData);
        }

        return new ColumnDataFrame(columnsIndex, columnsData);
    }

    public <T> DataFrame foldStreamByColumn(Stream<T> stream) {
        // since we can't guess the height from the Stream, convert it to array and fold that by column
        return foldByColumn(stream.toArray());
    }

    public <T> DataFrame foldIterableByRow(Iterable<T> iterable) {

        int width = columnsIndex.size();
        if (width == 0) {
            throw new IllegalArgumentException("Empty columns");
        }

        int heightEstimate = (iterable instanceof Collection) ? ((Collection) iterable).size() : 10;

        SeriesBuilder<Object, Object>[] columnBuilders = new ObjectAccumulator[width];
        for (int i = 0; i < width; i++) {
            columnBuilders[i] = new ObjectAccumulator<>(heightEstimate);
        }

        int p = 0;
        for (Object o : iterable) {
            columnBuilders[p % width].add(o);
            p++;
        }

        // fill the last row to the end
        int pl = p % width;
        if (pl > 0) {
            for (; pl < width; pl++) {
                columnBuilders[pl].add(null);
            }
        }

        Series<?>[] series = new Series[columnBuilders.length];
        for (int i = 0; i < columnBuilders.length; i++) {
            series[i] = columnBuilders[i].toSeries();
        }

        return new ColumnDataFrame(columnsIndex, series);
    }

    public <T> DataFrame foldIterableByColumn(Iterable<T> iterable) {

        // since we can't know the exact size of the Iterable in a general case, convert it to array and fold that by
        // column
        return foldByColumn(toCollection(iterable).toArray());
    }

    private <T> Collection<T> toCollection(Iterable<T> iterable) {

        if (iterable instanceof Collection) {
            return (Collection) iterable;
        }

        List<T> values = new ArrayList<>();
        iterable.forEach(values::add);
        return values;
    }

    public <T> DataFrame objectsToRows(Iterable<T> objects, Function<T, Object[]> rowMapper) {
        DataFrameByRowBuilder byRowBuilder = new DataFrameByRowBuilder(columnsIndex);
        objects.forEach(o -> byRowBuilder.addRow(rowMapper.apply(o)));
        return byRowBuilder.create();
    }

    protected DataFrame fromColumnarData(Object[][] columnarData) {

        int w = columnarData.length;
        Series[] series = new Series[w];

        for (int i = 0; i < w; i++) {
            series[i] = new ArraySeries(columnarData[i]);
        }

        return new ColumnDataFrame(columnsIndex, series);
    }

}
