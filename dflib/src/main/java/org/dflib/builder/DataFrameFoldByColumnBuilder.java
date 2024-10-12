package org.dflib.builder;

import org.dflib.ColumnDataFrame;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.Series;
import org.dflib.series.ArraySeries;
import org.dflib.series.DoubleArraySeries;
import org.dflib.series.IntArraySeries;
import org.dflib.series.LongArraySeries;

import java.util.Arrays;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class DataFrameFoldByColumnBuilder extends BaseDataFrameBuilder {

    public DataFrameFoldByColumnBuilder(Index columnsIndex) {
        super(columnsIndex);
    }

    public DataFrame of(Object... data) {

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

    public <T> DataFrame ofStream(Stream<T> stream) {
        // since we can't guess the height from the Stream, convert it to array and fold the array by column
        return of(stream.toArray());
    }

    public DataFrame ofDoubles(double padWith, double... data) {

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

        return new ColumnDataFrame(null, columnsIndex, series);
    }

    public DataFrame ofStream(LongStream stream) {
        return ofStream(0L, stream);
    }

    public DataFrame ofStream(long padWith, LongStream stream) {
        // since we can't guess the height from the Stream, convert it to array and fold the array by column
        return ofLongs(padWith, stream.toArray());
    }

    public <T> DataFrame ofIterable(Iterable<T> iterable) {
        // since we can't know the exact size of the Iterable in a general case, convert it to array and fold that by
        // column
        return of(toCollection(iterable).toArray());
    }

    protected DataFrame fromColumnarData(Object[][] columnarData) {

        int w = columnarData.length;
        Series[] series = new Series[w];

        for (int i = 0; i < w; i++) {
            series[i] = new ArraySeries(columnarData[i]);
        }

        return new ColumnDataFrame(null, columnsIndex, series);
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
