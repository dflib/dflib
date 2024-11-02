package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.dflib.Exp.*;

public class ColumnSet_AggAvgTest {

    @Test
    public void intDouble() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 4L,
                0, 55.5);

        DataFrame agg = df.cols().agg(
                $int("a").avg(),
                $double(1).avg());

        new DataFrameAsserts(agg, "avg(a)", "avg(b)")
                .expectHeight(1)
                .expectRow(0, 0.5, 29.75);
    }

    @Test
    public void doubleFiltered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 4L,
                5, 8L,
                0, 55.5);

        DataFrame agg = df.cols().agg(
                $double("a").avg($int(0).ne(5)),
                $double(1).avg($int(0).ne(5)));

        new DataFrameAsserts(agg, "avg(a)", "avg(b)")
                .expectHeight(1)
                .expectRow(0, 0.5, 29.75);
    }

    @Test
    public void date() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                LocalDate.of(2000, 2, 1), LocalDate.of(2001, 5, 10),
                LocalDate.of(2000, 2, 2), LocalDate.of(2001, 5, 14),
                LocalDate.of(2000, 2, 3), LocalDate.of(2001, 5, 2));

        DataFrame agg = df.cols().agg(
                $date("a").avg(),
                $date(1).avg());

        new DataFrameAsserts(agg, "avg(a)", "avg(b)")
                .expectHeight(1)
                .expectRow(0, LocalDate.of(2000, 2, 2), LocalDate.of(2001, 5, 9));
    }

    @Test
    public void time() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                LocalTime.of(2, 2, 1), LocalTime.of(3, 5, 10),
                LocalTime.of(2, 2, 2), LocalTime.of(3, 5, 14),
                LocalTime.of(2, 2, 3), LocalTime.of(3, 5, 2));

        DataFrame agg = df.cols().agg(
                $time("a").avg(),
                $time(1).avg());

        new DataFrameAsserts(agg, "avg(a)", "avg(b)")
                .expectHeight(1)
                .expectRow(0, LocalTime.of(2, 2, 2), LocalTime.of(3, 5, 8, 666_666_667));
    }

    @Test
    public void dateTime() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                LocalDateTime.of(2000, 1, 1, 2, 2, 1), LocalDateTime.of(2001, 2, 2, 3, 5, 10),
                LocalDateTime.of(2000, 1, 1, 2, 2, 2), LocalDateTime.of(2001, 2, 2, 3, 5, 14),
                LocalDateTime.of(2000, 1, 1, 2, 2, 3), LocalDateTime.of(2001, 2, 2, 3, 5, 2));

        DataFrame agg = df.cols().agg(
                $dateTime("a").avg(),
                $dateTime(1).avg());

        new DataFrameAsserts(agg, "avg(a)", "avg(b)")
                .expectHeight(1)
                .expectRow(0, LocalDateTime.of(2000, 1, 1, 2, 2, 2), LocalDateTime.of(2001, 2, 2, 3, 5, 8, 666_666_667));
    }
}
