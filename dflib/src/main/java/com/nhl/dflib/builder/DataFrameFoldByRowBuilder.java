package com.nhl.dflib.builder;

import com.nhl.dflib.ColumnDataFrame;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.series.ArraySeries;
import com.nhl.dflib.series.DoubleArraySeries;
import com.nhl.dflib.series.IntArraySeries;
import com.nhl.dflib.series.LongArraySeries;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * @since 0.16
 */
public class DataFrameFoldByRowBuilder extends BaseDataFrameBuilder {

    public DataFrameFoldByRowBuilder(Index columnsIndex) {
        super(columnsIndex);
    }

    public DataFrame of(Object... data) {
        Geometry g = geometry(data.length);
        Object[][] columnarData = new Object[g.width][g.height];

        for (int i = 0; i < g.fullRowsHeight; i++) {
            for (int j = 0; j < g.width; j++) {
                columnarData[j][i] = data[i * g.width + j];
            }
        }

        if (g.isLastRowPartial()) {
            int lastRowIndex = g.fullRowsHeight;
            for (int j = 0; j < g.partialRowWidth; j++) {
                columnarData[j][lastRowIndex] = data[lastRowIndex * g.width + j];
            }
        }

        Series[] series = new Series[g.width];

        for (int i = 0; i < g.width; i++) {
            series[i] = new ArraySeries(columnarData[i]);
        }

        return new ColumnDataFrame(null, columnsIndex, series);
    }

    public <T> DataFrame ofIterable(Iterable<T> iterable) {
        // since we can't know the exact size of the Iterable in a general case, convert it to array and fold that by
        // column
        return of(toCollection(iterable).toArray());
    }

    public <T> DataFrame ofStream(Stream<T> stream) {
        return of(stream.toArray());
    }

    public DataFrame ofDoubles(double padWith, double... data) {

        Geometry g = geometry(data.length);
        double[][] columnarData = new double[g.width][g.height];

        for (int i = 0; i < g.fullRowsHeight; i++) {
            for (int j = 0; j < g.width; j++) {
                columnarData[j][i] = data[i * g.width + j];
            }
        }

        if (g.isLastRowPartial()) {
            int lastRowIndex = g.fullRowsHeight;
            for (int j = 0; j < g.partialRowWidth; j++) {
                columnarData[j][lastRowIndex] = data[lastRowIndex * g.width + j];
            }

            for (int j = g.partialRowWidth; j < g.width; j++) {
                columnarData[j][lastRowIndex] = padWith;
            }
        }

        Series[] series = new Series[g.width];

        for (int i = 0; i < g.width; i++) {
            series[i] = new DoubleArraySeries(columnarData[i]);
        }

        return new ColumnDataFrame(null, columnsIndex, series);
    }

    public DataFrame ofStream(DoubleStream stream) {
        return ofStream(0., stream);
    }

    public DataFrame ofStream(double padWith, DoubleStream stream) {
        // since we can't guess the height from the Stream, convert it to array and fold the array by column
        return ofDoubles(padWith, stream.toArray());
    }

    public DataFrame ofInts(int padWith, int... data) {

        Geometry g = geometry(data.length);
        int[][] columnarData = new int[g.width][g.height];

        for (int i = 0; i < g.fullRowsHeight; i++) {
            for (int j = 0; j < g.width; j++) {
                columnarData[j][i] = data[i * g.width + j];
            }
        }

        if (g.isLastRowPartial()) {
            int lastRowIndex = g.fullRowsHeight;
            for (int j = 0; j < g.partialRowWidth; j++) {
                columnarData[j][lastRowIndex] = data[lastRowIndex * g.width + j];
            }

            for (int j = g.partialRowWidth; j < g.width; j++) {
                columnarData[j][lastRowIndex] = padWith;
            }
        }

        Series[] series = new Series[g.width];

        for (int i = 0; i < g.width; i++) {
            series[i] = new IntArraySeries(columnarData[i]);
        }

        return new ColumnDataFrame(null, columnsIndex, series);
    }

    public DataFrame ofStream(IntStream stream) {
        return ofStream(0, stream);
    }

    public DataFrame ofStream(int padWith, IntStream stream) {
        // since we can't guess the height from the Stream, convert it to array and fold the array by column
        return ofInts(padWith, stream.toArray());
    }

    public DataFrame ofLongs(long padWith, long... data) {

        Geometry g = geometry(data.length);
        long[][] columnarData = new long[g.width][g.height];

        for (int i = 0; i < g.fullRowsHeight; i++) {
            for (int j = 0; j < g.width; j++) {
                columnarData[j][i] = data[i * g.width + j];
            }
        }

        if (g.isLastRowPartial()) {
            int lastRowIndex = g.fullRowsHeight;
            for (int j = 0; j < g.partialRowWidth; j++) {
                columnarData[j][lastRowIndex] = data[lastRowIndex * g.width + j];
            }

            for (int j = g.partialRowWidth; j < g.width; j++) {
                columnarData[j][lastRowIndex] = padWith;
            }
        }

        Series[] series = new Series[g.width];

        for (int i = 0; i < g.width; i++) {
            series[i] = new LongArraySeries(columnarData[i]);
        }

        return new ColumnDataFrame(null, columnsIndex, series);
    }

    public DataFrame ofStream(LongStream stream) {
        return ofStream(0L, stream);
    }

    public DataFrame ofStream(long padWith, LongStream stream) {

        // since we can't guess the height from the Stream, convert it to array and fold the array by column
        return ofLongs(padWith, stream.toArray());
    }

    protected Geometry geometry(int dataLength) {
        int w = columnsIndex.size();
        if (w == 0) {
            throw new IllegalArgumentException("Empty columns");
        }

        int lastRowWidth = dataLength % w;
        int completeRowsHeight = dataLength / w;
        int fullHeight = lastRowWidth > 0 ? completeRowsHeight + 1 : completeRowsHeight;

        return new Geometry(w, fullHeight, completeRowsHeight, lastRowWidth);
    }

    static class Geometry {
        final int width;
        final int height;
        final int fullRowsHeight;
        final int partialRowWidth;

        public Geometry(int width, int height, int fullRowsHeight, int partialRowWidth) {
            this.width = width;
            this.height = height;
            this.fullRowsHeight = fullRowsHeight;
            this.partialRowWidth = partialRowWidth;
        }

        public boolean isLastRowPartial() {
            return partialRowWidth > 0;
        }
    }
}
