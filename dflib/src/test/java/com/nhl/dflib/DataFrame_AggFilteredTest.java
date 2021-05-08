package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataFrame_AggFilteredTest {

    @Test
    public void testSumInt() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 1,
                -1, 5,
                2, 6,
                -4, 5);

        DataFrame agg = df.agg(
                // filter is applied to column 0, sum is applied to column 1
                Aggregator.filterRows(0, (Integer i) -> i % 2 == 0).sumInt(1),
                // filter is applied to column 1, sum is applied to column 0
                Aggregator.filterRows("b", (Integer i) -> i % 2 == 1).sumInt("a"));

        new DataFrameAsserts(agg, "b", "a")
                .expectHeight(1)
                .expectRow(0, 11, -4);
    }

    @Test
    public void testSumLong() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 1,
                -1, 5,
                2, 6,
                -4, 5);

        DataFrame agg = df.agg(
                // filter is applied to column 0, sum is applied to column 1
                Aggregator.filterRows(0, (Integer i) -> i % 2 == 0).sumLong(1),
                // filter is applied to column 1, sum is applied to column 0
                Aggregator.filterRows("b", (Integer i) -> i % 2 == 1).sumLong("a"));

        new DataFrameAsserts(agg, "b", "a")
                .expectHeight(1)
                .expectRow(0, 11L, -4L);
    }

    @Test
    public void testSumDouble() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1.1, 1.8,
                -1., 5.8,
                2.35, 6.5,
                4.6, 5.1);

        DataFrame agg = df.agg(
                Aggregator.filterRows(0, (Double i) -> i < 4).sumDouble(1),
                Aggregator.filterRows("b", (Double i) -> i > 5).sumDouble("a"));

        new DataFrameAsserts(agg, "b", "a").expectHeight(1);

        assertEquals(14.1, (Double) agg.getColumn("b").get(0), 0.000000001);
        assertEquals(5.95, (Double) agg.getColumn("a").get(0), 0.000000001);
    }

    @Test
    public void testFirst() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                7, 1,
                -1, 5,
                -4, 5);

        DataFrame agg = df.agg(
                Aggregator.filterRows(0, (Integer i) -> i % 2 == 0).first(1),
                Aggregator.filterRows("b", (Integer i) -> i % 2 == 1).first("a"));

        new DataFrameAsserts(agg, "b", "a")
                .expectHeight(1)
                .expectRow(0, 5, 7);
    }

    @Test
    public void testCountInt_CountLong() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                7, 1,
                -1, 5,
                -4, 5);

        DataFrame agg = df.agg(
                Aggregator.filterRows(0, (Integer i) -> i % 2 == 0).countInt(),
                Aggregator.filterRows("b", (Integer i) -> i % 2 == 1).countLong());

        new DataFrameAsserts(agg, "_int_count", "_long_count")
                .expectHeight(1)
                .expectRow(0, 1, 3L);
    }

    @Test
    public void test_MinMaxLong() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1L, 1L,
                2L, 4L,
                -1L, 5L,
                8L, 2L);

        DataFrame agg = df.agg(
                Aggregator.filterRows(0, (Long i) -> i % 2 == 0).max(1),
                Aggregator.filterRows(0, (Long i) -> i % 2 == 0).min(1),
                Aggregator.filterRows("b", (Long i) -> i % 2 == 1).max("a"),
                Aggregator.filterRows("b", (Long i) -> i % 2 == 1).min("a")
        );

        new DataFrameAsserts(agg, "b", "b_", "a", "a_")
                .expectHeight(1)
                .expectRow(0, 4L, 2L, 1L, -1L);
    }

    @Test
    public void test_MinMaxInt() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 1,
                2, 4,
                -1, 5,
                8, 2);

        DataFrame agg = df.agg(
                Aggregator.filterRows(0, (Integer i) -> i % 2 == 0).max(1),
                Aggregator.filterRows(0, (Integer i) -> i % 2 == 0).min(1),
                Aggregator.filterRows("b", (Integer i) -> i % 2 == 1).max("a"),
                Aggregator.filterRows("b", (Integer i) -> i % 2 == 1).min("a")
        );

        new DataFrameAsserts(agg, "b", "b_", "a", "a_")
                .expectHeight(1)
                .expectRow(0, 4, 2, 1, -1);
    }

    @Test
    public void test_MinMaxDouble() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1., 1.01,
                6.5, 15.7,
                -1.2, 5.1,
                8., 2.);

        DataFrame agg = df.agg(
                Aggregator.filterRows(0, (Double d) -> d > 5).max(1),
                Aggregator.filterRows(0, (Double d) -> d > 5).min(1),
                Aggregator.filterRows("b", (Double d) -> d > 5).max("a"),
                Aggregator.filterRows("b", (Double d) -> d > 5).min("a")
        );

        new DataFrameAsserts(agg, "b", "b_", "a", "a_")
                .expectHeight(1)
                .expectRow(0, 15.7, 2.0, 6.5, -1.2);
    }

    @Test
    public void test_AverageDouble() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 4L,
                5, 8L,
                0, 55.5);

        DataFrame agg = df.agg(
                Aggregator.filterRows(0, (Integer i) -> i != 5).averageDouble("a"),
                Aggregator.filterRows(0, (Integer i) -> i != 5).averageDouble(1));

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, 0.5, 29.75);
    }

}
