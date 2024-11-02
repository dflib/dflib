package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.dflib.Exp.*;

public class ColumnSet_AggMedianTest {

    @Test
    public void odd() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 100.,
                0, 55.5,
                4, 0.);

        DataFrame agg = df.cols().agg(
                $int("a").median(),
                $double(1).median());

        new DataFrameAsserts(agg, "median(a)", "median(b)")
                .expectHeight(1)
                .expectRow(0, 1., 55.5);
    }

    @Test
    public void even() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 100.,
                0, 55.5,
                4, 0.,
                3, 5.);

        DataFrame agg = df.cols().agg(
                $int("a").median(),
                $double(1).median());

        new DataFrameAsserts(agg, "median(a)", "median(b)")
                .expectHeight(1)
                .expectRow(0, 2., 30.25);
    }

    @Test
    public void zero() {
        DataFrame df = DataFrame.empty("a", "b");

        DataFrame agg = df.cols().agg(
                $int("a").median(),
                $double(1).median());

        new DataFrameAsserts(agg, "median(a)", "median(b)")
                .expectHeight(1)
                .expectRow(0, 0., 0.);
    }

    @Test
    public void one() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(1, 100);

        DataFrame agg = df.cols().agg(
                $int("a").median(),
                $int(1).median());

        new DataFrameAsserts(agg, "median(a)", "median(b)")
                .expectHeight(1)
                .expectRow(0, 1., 100.);
    }

    @Test
    public void nulls() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, null,
                0, 55.5,
                4, 0.,
                null, 5.);

        DataFrame agg = df.cols().agg(
                $int("a").median(),
                $double(1).median());

        new DataFrameAsserts(agg, "median(a)", "median(b)")
                .expectHeight(1)
                .expectRow(0, 1., 5.);
    }

    @Test
    public void date_odd() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                LocalDate.of(2000, 2, 1), LocalDate.of(2001, 5, 10),
                LocalDate.of(2000, 2, 2), LocalDate.of(2001, 5, 14),
                LocalDate.of(2000, 2, 3), LocalDate.of(2001, 5, 2));

        DataFrame agg = df.cols().agg(
                $date("a").median(),
                $date(1).median());

        new DataFrameAsserts(agg, "median(a)", "median(b)")
                .expectHeight(1)
                .expectRow(0, LocalDate.of(2000, 2, 2), LocalDate.of(2001, 5, 10));
    }

    @Test
    public void date_even() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                LocalDate.of(2000, 2, 1), LocalDate.of(2001, 5, 10),
                LocalDate.of(2000, 2, 2), LocalDate.of(2001, 5, 14),
                LocalDate.of(2000, 2, 4), LocalDate.of(2001, 5, 2),
                LocalDate.of(2000, 2, 5), LocalDate.of(2001, 5, 1));

        DataFrame agg = df.cols().agg(
                $date("a").median(),
                $date(1).median());

        new DataFrameAsserts(agg, "median(a)", "median(b)")
                .expectHeight(1)
                .expectRow(0, LocalDate.of(2000, 2, 3), LocalDate.of(2001, 5, 6));
    }

    @Test
    public void time_odd() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                LocalTime.of(2, 2, 1), LocalTime.of(3, 5, 10),
                LocalTime.of(2, 2, 2), LocalTime.of(3, 5, 14),
                LocalTime.of(2, 2, 3), LocalTime.of(3, 5, 2));

        DataFrame agg = df.cols().agg(
                $time("a").median(),
                $time(1).median());

        new DataFrameAsserts(agg, "median(a)", "median(b)")
                .expectHeight(1)
                .expectRow(0, LocalTime.of(2, 2, 2), LocalTime.of(3, 5, 10));
    }

    @Test
    public void time_even() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                LocalTime.of(2, 2, 1), LocalTime.of(3, 5, 10),
                LocalTime.of(2, 2, 2), LocalTime.of(3, 5, 14),
                LocalTime.of(2, 2, 4), LocalTime.of(3, 5, 2, 1_000_004),
                LocalTime.of(2, 2, 5), LocalTime.of(3, 5, 1));

        DataFrame agg = df.cols().agg(
                $time("a").median(),
                $time(1).median());

        new DataFrameAsserts(agg, "median(a)", "median(b)")
                .expectHeight(1)
                .expectRow(0, LocalTime.of(2, 2, 3), LocalTime.of(3, 5, 6, 500_002));
    }

    @Test
    public void dateTime_odd() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                LocalDateTime.of(2000, 1, 1, 2, 2, 1), LocalDateTime.of(2001, 2, 2, 3, 5, 10),
                LocalDateTime.of(2000, 1, 1, 2, 2, 2), LocalDateTime.of(2001, 2, 2, 3, 5, 14),
                LocalDateTime.of(2000, 1, 1, 2, 2, 3), LocalDateTime.of(2001, 2, 2, 3, 5, 2));

        DataFrame agg = df.cols().agg(
                $dateTime("a").median(),
                $dateTime(1).median());

        new DataFrameAsserts(agg, "median(a)", "median(b)")
                .expectHeight(1)
                .expectRow(0, LocalDateTime.of(2000, 1, 1, 2, 2, 2), LocalDateTime.of(2001, 2, 2, 3, 5, 10));
    }

    @Test
    public void dateTime_even() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                LocalDateTime.of(2000, 1, 1, 2, 2, 1), LocalDateTime.of(2001, 2, 2, 3, 5, 10),
                LocalDateTime.of(2000, 1, 1, 2, 2, 2), LocalDateTime.of(2000, 2, 2, 3, 5, 14),
                LocalDateTime.of(2000, 1, 1, 2, 2, 4), LocalDateTime.of(2001, 2, 1, 3, 5, 2, 1_000_004),
                LocalDateTime.of(2000, 1, 1, 2, 2, 5), LocalDateTime.of(2002, 2, 2, 3, 5, 1));

        DataFrame agg = df.cols().agg(
                $dateTime("a").median(),
                $dateTime(1).median());

        new DataFrameAsserts(agg, "median(a)", "median(b)")
                .expectHeight(1)
                .expectRow(0, LocalDateTime.of(2000, 1, 1, 2, 2, 3), LocalDateTime.of(2001, 2, 1, 15, 5, 6, 500_002));
    }
}
