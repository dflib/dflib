package com.nhl.dflib.builder;

import com.nhl.dflib.ColumnDataFrame;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.series.ArraySeries;
import com.nhl.dflib.series.DoubleArraySeries;
import com.nhl.dflib.series.IntArraySeries;
import com.nhl.dflib.series.LongArraySeries;

import java.util.Arrays;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * @since 0.16
 */
public class DataFrameFoldByColumnBuilder extends BaseDataFrameBuilder {

    public DataFrameFoldByColumnBuilder(Index columnsIndex) {
        super(columnsIndex);
    }

    public DataFrame array(Object... data) {

        FoldByColumnGeometry g = geometry(data.length);

        Object[][] columnarData = new Object[g.width][g.height];

        for (int i = 0; i < g.fullColumns; i++) {
            System.arraycopy(data, i * g.height, columnarData[i], 0, g.height);
        }

        if (g.isLastColumnPartial()) {
            System.arraycopy(data, g.cellsInFullColumns(), columnarData[g.fullColumns], 0, g.partialColumnHeight);
        }

        return fromColumnarData(columnarData);
    }

    public <T> DataFrame stream(Stream<T> stream) {
        // since we can't guess the height from the Stream, convert it to array and fold the array by column
        return array(stream.toArray());
    }

    public DataFrame doubleArray(double padWith, double... data) {

        FoldByColumnGeometry g = geometry(data.length);

        double[][] columnarData = new double[g.width][g.height];

        for (int i = 0; i < g.fullColumns; i++) {
            System.arraycopy(data, i * g.height, columnarData[i], 0, g.height);
        }

        if (g.isLastColumnPartial()) {
            System.arraycopy(data, g.cellsInFullColumns(), columnarData[g.fullColumns], 0, g.partialColumnHeight);

            if (padWith != 0.) {
                Arrays.fill(columnarData[g.fullColumns], g.partialColumnHeight, g.height, padWith);
            }
        }

        Series[] series = new Series[g.width];

        for (int i = 0; i < g.width; i++) {
            series[i] = new DoubleArraySeries(columnarData[i]);
        }

        return new ColumnDataFrame(columnsIndex, series);
    }

    public DataFrame doubleStream(DoubleStream stream) {
        return doubleStream(0., stream);
    }

    public DataFrame doubleStream(double padWith, DoubleStream stream) {
        // since we can't guess the height from the Stream, convert it to array and fold the array by column
        return doubleArray(padWith, stream.toArray());
    }

    public DataFrame intArray(int padWith, int... data) {

        FoldByColumnGeometry g = geometry(data.length);

        int[][] columnarData = new int[g.width][g.height];

        for (int i = 0; i < g.fullColumns; i++) {
            System.arraycopy(data, i * g.height, columnarData[i], 0, g.height);
        }

        if (g.isLastColumnPartial()) {
            System.arraycopy(data, g.cellsInFullColumns(), columnarData[g.fullColumns], 0, g.partialColumnHeight);

            if (padWith != 0) {
                Arrays.fill(columnarData[g.fullColumns], g.partialColumnHeight, g.height, padWith);
            }
        }

        Series[] series = new Series[g.width];

        for (int i = 0; i < g.width; i++) {
            series[i] = new IntArraySeries(columnarData[i]);
        }

        return new ColumnDataFrame(columnsIndex, series);
    }

    public DataFrame intStream(IntStream stream) {
        return intStream(0, stream);
    }

    public DataFrame intStream(int padWith, IntStream stream) {
        // since we can't guess the height from the Stream, convert it to array and fold the array by column
        return intArray(padWith, stream.toArray());
    }

    public DataFrame longArray(long padWith, long... data) {

        FoldByColumnGeometry g = geometry(data.length);

        long[][] columnarData = new long[g.width][g.height];

        for (int i = 0; i < g.fullColumns; i++) {
            System.arraycopy(data, i * g.height, columnarData[i], 0, g.height);
        }

        if (g.isLastColumnPartial()) {
            System.arraycopy(data, g.cellsInFullColumns(), columnarData[g.fullColumns], 0, g.partialColumnHeight);

            if (padWith != 0L) {
                Arrays.fill(columnarData[g.fullColumns], g.partialColumnHeight, g.height, padWith);
            }
        }

        Series[] series = new Series[g.width];

        for (int i = 0; i < g.width; i++) {
            series[i] = new LongArraySeries(columnarData[i]);
        }

        return new ColumnDataFrame(columnsIndex, series);
    }

    public DataFrame longStream(LongStream stream) {
        return longStream(0L, stream);
    }

    public DataFrame longStream(long padWith, LongStream stream) {
        // since we can't guess the height from the Stream, convert it to array and fold the array by column
        return longArray(padWith, stream.toArray());
    }

    public <T> DataFrame iterable(Iterable<T> iterable) {
        // since we can't know the exact size of the Iterable in a general case, convert it to array and fold that by
        // column
        return array(toCollection(iterable).toArray());
    }

    protected DataFrame fromColumnarData(Object[][] columnarData) {

        int w = columnarData.length;
        Series[] series = new Series[w];

        for (int i = 0; i < w; i++) {
            series[i] = new ArraySeries(columnarData[i]);
        }

        return new ColumnDataFrame(columnsIndex, series);
    }

    FoldByColumnGeometry geometry(int dataLength) {
        int w = columnsIndex.size();
        if (w == 0) {
            throw new IllegalArgumentException("Empty columns");
        }

        // check whether "dataLength" is partial or not against the width,
        // but calculate the las column offset against the height

        boolean partialLastColumn = dataLength % w > 0;
        int fullColumns = partialLastColumn
                ? w - 1
                : w;

        int h = partialLastColumn
                ? 1 + dataLength / w
                : dataLength / w;

        int partialColumnHeight = partialLastColumn ? dataLength % h : 0;

        return new FoldByColumnGeometry(w, h, partialColumnHeight, fullColumns);
    }

    static final class FoldByColumnGeometry {

        int width;
        int height;
        int fullColumns;
        int partialColumnHeight;

        FoldByColumnGeometry(int width, int height, int partialColumnHeight, int fullColumns) {
            this.width = width;
            this.height = height;
            this.fullColumns = fullColumns;
            this.partialColumnHeight = partialColumnHeight;
        }

        boolean isLastColumnPartial() {
            return partialColumnHeight > 0;
        }

        int cellsInFullColumns() {
            return fullColumns * height;
        }
    }
}
