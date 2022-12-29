package com.nhl.dflib.builder;

import com.nhl.dflib.ColumnDataFrame;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.series.ArraySeries;

import java.util.Collection;
import java.util.PrimitiveIterator;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * @since 0.16
 */
public class DataFrameFoldByRowBuilder {

    private final Index columnsIndex;

    public DataFrameFoldByRowBuilder(Index columnsIndex) {
        this.columnsIndex = columnsIndex;
    }

    public DataFrame array(Object... data) {

        int width = columnsIndex.size();
        if (width == 0) {
            throw new IllegalArgumentException("Empty columns");
        }

        DataFrameDimensions dims = dimensions(data.length, width);

        Object[][] columnarData = new Object[width][dims.height];

        for (int i = 0; i < dims.fullRowsHeight; i++) {
            for (int j = 0; j < width; j++) {
                columnarData[j][i] = data[i * width + j];
            }
        }

        if (dims.partialLastRow()) {
            int lastRowIndex = dims.fullRowsHeight;
            for (int j = 0; j < dims.lastRowWidth; j++) {
                columnarData[j][lastRowIndex] = data[lastRowIndex * width + j];
            }
        }

        int w = columnarData.length;
        Series[] series = new Series[w];

        for (int i = 0; i < w; i++) {
            series[i] = new ArraySeries(columnarData[i]);
        }

        return new ColumnDataFrame(columnsIndex, series);
    }

    public <T> DataFrame iterable(Iterable<T> iterable) {

        int width = columnsIndex.size();
        if (width == 0) {
            throw new IllegalArgumentException("Empty columns");
        }

        int heightEstimate = (iterable instanceof Collection)
                ? dimensions(((Collection) iterable).size(), width).height
                : 10;

        ValueAccum<Object>[] columnBuilders = new ValueAccum[width];
        for (int i = 0; i < width; i++) {
            columnBuilders[i] = new ObjectAccum<>(heightEstimate);
        }

        int p = 0;
        for (Object o : iterable) {
            columnBuilders[p % width].push(o);
            p++;
        }

        // fill the last row to the end
        int pl = p % width;
        if (pl > 0) {
            for (; pl < width; pl++) {
                columnBuilders[pl].push(null);
            }
        }

        Series<?>[] series = new Series[columnBuilders.length];
        for (int i = 0; i < columnBuilders.length; i++) {
            series[i] = columnBuilders[i].toSeries();
        }

        return new ColumnDataFrame(columnsIndex, series);
    }

    public <T> DataFrame stream(Stream<T> stream) {
        return iterable(stream::iterator);
    }

    public DataFrame doubleStream(DoubleStream stream) {
        return doubleStream(0., stream);
    }

    public DataFrame doubleStream(double padWith, DoubleStream stream) {

        int width = columnsIndex.size();
        if (width == 0) {
            throw new IllegalArgumentException("Empty columns");
        }

        DoubleAccum[] columnBuilders = new DoubleAccum[width];
        for (int i = 0; i < width; i++) {
            columnBuilders[i] = new DoubleAccum();
        }

        PrimitiveIterator.OfDouble it = stream.iterator();

        int p = 0;
        while (it.hasNext()) {
            columnBuilders[p % width].pushDouble(it.nextDouble());
            p++;
        }

        // fill the last row to the end
        int pl = p % width;
        if (pl > 0) {
            for (; pl < width; pl++) {
                columnBuilders[pl].pushDouble(padWith);
            }
        }

        Series[] columnsData = new Series[width];
        for (int i = 0; i < width; i++) {
            columnsData[i] = columnBuilders[i].toSeries();
        }

        return new ColumnDataFrame(columnsIndex, columnsData);
    }

    public DataFrame intStream(IntStream stream) {
        return intStream(0, stream);
    }

    public DataFrame intStream(int padWith, IntStream stream) {

        int width = columnsIndex.size();
        if (width == 0) {
            throw new IllegalArgumentException("Empty columns");
        }

        IntAccum[] columnBuilders = new IntAccum[width];
        for (int i = 0; i < width; i++) {
            columnBuilders[i] = new IntAccum();
        }

        PrimitiveIterator.OfInt it = stream.iterator();

        int p = 0;
        while (it.hasNext()) {
            columnBuilders[p % width].pushInt(it.nextInt());
            p++;
        }

        // fill the last row to the end
        int pl = p % width;
        if (pl > 0) {
            for (; pl < width; pl++) {
                columnBuilders[pl].pushInt(padWith);
            }
        }

        Series[] columnsData = new Series[width];
        for (int i = 0; i < width; i++) {
            columnsData[i] = columnBuilders[i].toSeries();
        }

        return new ColumnDataFrame(columnsIndex, columnsData);
    }

    public DataFrame longStream(LongStream stream) {
        return longStream(0L, stream);
    }

    public DataFrame longStream(long padWith, LongStream stream) {

        int width = columnsIndex.size();
        if (width == 0) {
            throw new IllegalArgumentException("Empty columns");
        }

        LongAccum[] columnBuilders = new LongAccum[width];
        for (int i = 0; i < width; i++) {
            columnBuilders[i] = new LongAccum();
        }

        PrimitiveIterator.OfLong it = stream.iterator();

        int p = 0;
        while (it.hasNext()) {
            columnBuilders[p % width].pushLong(it.nextLong());
            p++;
        }

        // fill the last row to the end
        int pl = p % width;
        if (pl > 0) {
            for (; pl < width; pl++) {
                columnBuilders[pl].pushLong(padWith);
            }
        }

        Series[] columnsData = new Series[width];
        for (int i = 0; i < width; i++) {
            columnsData[i] = columnBuilders[i].toSeries();
        }

        return new ColumnDataFrame(columnsIndex, columnsData);
    }

    protected DataFrame fromColumnarData(Object[][] columnarData) {

        int w = columnarData.length;
        Series[] series = new Series[w];

        for (int i = 0; i < w; i++) {
            series[i] = new ArraySeries(columnarData[i]);
        }

        return new ColumnDataFrame(columnsIndex, series);
    }

    protected DataFrameDimensions dimensions(int size, int width) {
        int lastRowWidth = size % width;
        int completeRowsHeight = size / width;
        int fullHeight = lastRowWidth > 0 ? completeRowsHeight + 1 : completeRowsHeight;

        return new DataFrameDimensions(fullHeight, completeRowsHeight, lastRowWidth);
    }

    static class DataFrameDimensions {
        final int height;
        final int fullRowsHeight;
        final int lastRowWidth;

        public DataFrameDimensions(int height, int fullRowsHeight, int lastRowWidth) {
            this.height = height;
            this.fullRowsHeight = fullRowsHeight;
            this.lastRowWidth = lastRowWidth;
        }

        public boolean partialLastRow() {
            return lastRowWidth > 0;
        }
    }
}
