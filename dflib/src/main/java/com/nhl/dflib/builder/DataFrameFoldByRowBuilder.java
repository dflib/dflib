package com.nhl.dflib.builder;

import com.nhl.dflib.ColumnDataFrame;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.series.ArraySeries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

        return new ColumnDataFrame(columnsIndex, series);
    }

    public <T> DataFrame iterable(Iterable<T> iterable) {
        // since we can't know the exact size of the Iterable in a general case, convert it to array and fold that by
        // column
        return array(toCollection(iterable).toArray());
    }

    public <T> DataFrame stream(Stream<T> stream) {
        return array(stream.toArray());
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

    <T> Collection<T> toCollection(Iterable<T> iterable) {

        if (iterable instanceof Collection) {
            return (Collection) iterable;
        }

        List<T> values = new ArrayList<>();
        iterable.forEach(values::add);
        return values;
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
