package com.nhl.dflib;

import com.nhl.dflib.series.ArraySeries;
import com.nhl.dflib.series.ColumnMappedSeries;
import com.nhl.dflib.series.ListSeries;
import com.nhl.dflib.series.RangeSeries;

import java.util.Collection;
import java.util.Objects;

import static java.util.Arrays.asList;

/**
 * A parameterized test superclass for Series that are subclasses of {@link com.nhl.dflib.series.ObjectSeries}.
 */
public abstract class BaseObjectSeriesTest {

    protected static Collection<Object[]> ALL_SERIES_TYPES = asList(
            new Object[][]{
                    {SeriesTypes.ARRAY},
                    {SeriesTypes.COLUMN_MAPPED},
                    {SeriesTypes.LIST},
                    {SeriesTypes.RANGE}}
    );

    protected SeriesTypes seriesType;

    public BaseObjectSeriesTest(SeriesTypes seriesType) {
        this.seriesType = Objects.requireNonNull(seriesType);
    }

    protected <T> Series<T> createSeries(T... data) {
        switch (seriesType) {

            // TODO: other types of supported Series
            case ARRAY:
                return new ArraySeries<>(data);
            case COLUMN_MAPPED:
                return new ColumnMappedSeries<>(new ArraySeries<>(data), v -> v);
            case LIST:
                return new ListSeries<>(asList(data));
            case RANGE:
                return new RangeSeries<>(new ArraySeries<>(data), 0, data.length);
            default:
                throw new IllegalStateException("Unknown series type: " + seriesType);
        }
    }

    protected enum SeriesTypes {
        ARRAY, COLUMN_MAPPED, LIST, RANGE
    }
}
