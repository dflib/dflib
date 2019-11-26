package com.nhl.dflib;

import com.nhl.dflib.unit.DoubleSeriesAsserts;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

public class DataFrame_AggFilteredTest {

    @Test
    public void testSumInt() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 1,
                -1, 5,
                2, 6,
                -4, 5);

        Series<?> s = df.agg(
                // filter is applied to column 0, sum is applied to column 1
                Aggregator.filterRows(0, (Integer i) -> i % 2 == 0).sumInt(1),
                // filter is applied to column 1, sum is applied to column 0
                Aggregator.filterRows("b", (Integer i) -> i % 2 == 1).sumInt("a"));

        new SeriesAsserts(s).expectData(11, -4);
    }

    @Test
    public void testSumLong() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 1,
                -1, 5,
                2, 6,
                -4, 5);

        Series<?> s = df.agg(
                // filter is applied to column 0, sum is applied to column 1
                Aggregator.filterRows(0, (Integer i) -> i % 2 == 0).sumLong(1),
                // filter is applied to column 1, sum is applied to column 0
                Aggregator.filterRows("b", (Integer i) -> i % 2 == 1).sumLong("a"));

        new SeriesAsserts(s).expectData(11L, -4L);
    }

    @Test
    public void testSumDouble() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1.1, 1.8,
                -1., 5.8,
                2.35, 6.5,
                4.6, 5.1);

        Series<?> s = df.agg(
                Aggregator.filterRows(0, (Double i) -> i < 4).sumDouble(1),
                Aggregator.filterRows("b", (Double i) -> i > 5).sumDouble("a"));

        DoubleSeries ds = DoubleSeries.forSeries(s, DoubleValueMapper.fromObject(0.));
        new DoubleSeriesAsserts(ds).expectData(14.1, 5.95);
    }

    @Test
    public void testFirst() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                7, 1,
                -1, 5,
                -4, 5);

        Series<?> s = df.agg(
                Aggregator.filterRows(0, (Integer i) -> i % 2 == 0).first(1),
                Aggregator.filterRows("b", (Integer i) -> i % 2 == 1).first("a"));

        new SeriesAsserts(s).expectData(5, 7);
    }

    @Test
    public void testCountInt_CountLong() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                7, 1,
                -1, 5,
                -4, 5);

        Series<?> s = df.agg(
                Aggregator.filterRows(0, (Integer i) -> i % 2 == 0).countInt(),
                Aggregator.filterRows("b", (Integer i) -> i % 2 == 1).countLong());

        new SeriesAsserts(s).expectData(1, 3L);
    }

    @Test
    public void test_MinMax() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 1,
                2, 4,
                -1, 5,
                8, 2);

        Series<?> s = df.agg(
                Aggregator.filterRows(0, (Integer i) -> i % 2 == 0).max(1),
                Aggregator.filterRows(0, (Integer i) -> i % 2 == 0).min(1),
                Aggregator.filterRows("b", (Integer i) -> i % 2 == 1).max("a"),
                Aggregator.filterRows("b", (Integer i) -> i % 2 == 1).min("a")
        );

        new SeriesAsserts(s).expectData(4, 2, 1, -1);
    }


    @Test
    public void test_MinMaxLong() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1L, 1L,
                2L, 4L,
                -1L, 5L,
                8L, 2L);

        Series<?> s = df.agg(
                Aggregator.filterRows(0, (Long i) -> i % 2 == 0).maxLong(1),
                Aggregator.filterRows(0, (Long i) -> i % 2 == 0).minLong(1),
                Aggregator.filterRows("b", (Long i) -> i % 2 == 1).maxLong("a"),
                Aggregator.filterRows("b", (Long i) -> i % 2 == 1).minLong("a")
        );

        new SeriesAsserts(s).expectData(4L, 2L, 1L, -1L);
    }

    @Test
    public void test_MinMaxInt() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 1,
                2, 4,
                -1, 5,
                8, 2);

        Series<?> s = df.agg(
                Aggregator.filterRows(0, (Integer i) -> i % 2 == 0).maxInt(1),
                Aggregator.filterRows(0, (Integer i) -> i % 2 == 0).minInt(1),
                Aggregator.filterRows("b", (Integer i) -> i % 2 == 1).maxInt("a"),
                Aggregator.filterRows("b", (Integer i) -> i % 2 == 1).minInt("a")
        );

        new SeriesAsserts(s).expectData(4, 2, 1, -1);
    }

    @Test
    public void test_MinMaxDouble() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1., 1.01,
                6.5, 15.7,
                -1.2, 5.1,
                8., 2.);

        Series<?> s = df.agg(
                Aggregator.filterRows(0, (Double d) -> d > 5).maxDouble(1),
                Aggregator.filterRows(0, (Double d) -> d > 5).minDouble(1),
                Aggregator.filterRows("b", (Double d) -> d > 5).maxDouble("a"),
                Aggregator.filterRows("b", (Double d) -> d > 5).minDouble("a")
        );

        new SeriesAsserts(s).expectData(15.7, 2.0, 6.5, -1.2);
    }

    @Test
    public void test_AverageDouble() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 4L,
                5, 8L,
                0, 55.5);

        Series<?> s = df.agg(
                Aggregator.filterRows(0, (Integer i) -> i != 5).averageDouble("a"),
                Aggregator.filterRows(0, (Integer i) -> i != 5).averageDouble(1));

        new SeriesAsserts(s).expectData(0.5, 29.75);
    }

}
