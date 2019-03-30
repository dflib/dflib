package com.nhl.dflib;

import com.nhl.dflib.row.TransformingIterable;
import com.nhl.dflib.series.ArraySeries;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Helper class implementing various DataFrame creation methods called from {@link DataFrame} static factory
 * methods.
 */
class DataFrameFactory {

    public static DataFrame forRows(Index columns, Object[]... rows) {

        int width = columns.size();
        if (width == 0) {
            throw new IllegalArgumentException("Empty columns");
        }

        List<Object>[] data = new List[width];
        for (int i = 0; i < width; i++) {
            data[i] = new ArrayList<>();
        }

        for (Object[] r : rows) {
            for (int i = 0; i < width; i++) {
                data[i].add(r[i]);
            }
        }

        int height = data[0].size();
        Series[] columnsData = new Series[width];
        for (int i = 0; i < width; i++) {
            Object[] columnData = new Object[height];
            data[i].toArray(columnData);
            columnsData[i] = new ArraySeries(columnData);
        }

        return new ColumnDataFrame(columns, columnsData);
    }

    public static DataFrame forListOfRows(Index columns, List<Object[]> sources) {
        return forRows(columns, sources);
    }

    public static DataFrame forRows(Index columns, Iterable<Object[]> sources) {

        int width = columns.size();
        if (width == 0) {
            throw new IllegalArgumentException("Empty columns");
        }

        List<Object>[] data = new List[width];
        for (int i = 0; i < width; i++) {
            data[i] = new ArrayList<>();
        }

        for (Object[] r : sources) {
            for (int i = 0; i < width; i++) {
                data[i].add(r[i]);
            }
        }

        int height = data[0].size();
        Series[] columnsData = new Series[width];
        for (int i = 0; i < width; i++) {
            Object[] columnData = new Object[height];
            data[i].toArray(columnData);
            columnsData[i] = new ArraySeries(columnData);
        }

        return new ColumnDataFrame(columns, columnsData);
    }

    public static <T> DataFrame forStreamFoldByRow(Index columns, Stream<T> stream) {

        int width = columns.size();
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

        return new ColumnDataFrame(columns, columnsData);
    }

    /**
     * Creates a columnar DataFrame from the provided array that stores data column-by-column.
     */
    public static DataFrame forSequenceFoldByColumn(Index columns, Object... sequence) {

        int w = columns.size();
        int lastColumnH = sequence.length % w;
        boolean partialLastColumn = lastColumnH > 0;
        int fullColumnsW = partialLastColumn
                ? w - 1
                : w;

        int h = partialLastColumn
                ? 1 + sequence.length / w
                : sequence.length / w;

        Object[][] data = new Object[w][h];

        for (int i = 0; i < fullColumnsW; i++) {
            System.arraycopy(sequence, i * h, data[i], 0, h);
        }

        if (partialLastColumn) {
            System.arraycopy(sequence, fullColumnsW * h, data[fullColumnsW], 0, lastColumnH);
        }

        Series[] series = columnarDataToSeries(data);
        return new ColumnDataFrame(columns, series);
    }

    /**
     * Creates a columnar DataFrame from the provided array that stores data row-by-row. This is NOT a very efficient
     * way to initialize a columnar DataFrame. Use {@link #forSequenceFoldByColumn(Index, Object...)} when possible.
     */
    public static DataFrame forSequenceFoldByRow(Index columns, Object... sequence) {

        int width = columns.size();
        int lastRowWidth = sequence.length % width;

        int minHeight = sequence.length / width;
        int fullHeight = lastRowWidth > 0 ? minHeight + 1 : minHeight;

        Object[][] data = new Object[width][fullHeight];

        for (int i = 0; i < minHeight; i++) {
            for (int j = 0; j < width; j++) {
                data[j][i] = sequence[i * width + j];
            }
        }

        if (lastRowWidth > 0) {
            int lastRowIndex = minHeight;
            for (int j = 0; j < lastRowWidth; j++) {
                data[j][lastRowIndex] = sequence[lastRowIndex * width + j];
            }
        }

        Series[] series = columnarDataToSeries(data);
        return new ColumnDataFrame(columns, series);
    }

    /**
     * Creates a DataFrame from an iterable over arbitrary objects. Each object will be converted to a row by applying
     * a function passed as the last argument.
     */
    public static <T> DataFrame forObjects(Index columns, Iterable<T> rows, Function<T, Object[]> rowMapper) {
        return forRows(columns, new TransformingIterable<>(rows, rowMapper)).materialize();
    }

    private static Series<?>[] columnarDataToSeries(Object[][] columnarData) {

        int w = columnarData.length;

        Series[] series = new Series[w];

        for (int i = 0; i < w; i++) {
            series[i] = new ArraySeries(columnarData[i]);
        }

        return series;
    }

}
