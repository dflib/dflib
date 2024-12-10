package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.dflib.Exp.*;

public class ColumnSet_AggMinMaxTest {

    @Test
    public void ints() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 1,
                -1, 1,
                8, 1);

        DataFrame agg = df.cols().agg(
                $int("a").min(),
                $int("a").max());

        new DataFrameAsserts(agg, "min(a)", "max(a)")
                .expectHeight(1)
                .expectRow(0, -1, 8);
    }


    @Test
    public void longFiltered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1L, 1L,
                2L, 4L,
                -1L, 5L,
                8L, 2L);

        DataFrame agg = df.cols().agg(
                $long(1).max($long(0).mod(2).eq(0L)),
                $long(1).min($long(0).mod(2).eq(0L)),
                $long("a").max($long("b").mod(2).eq(1L)),
                $long("a").min($long("b").mod(2).eq(1L))
        );

        new DataFrameAsserts(agg, "max(b)", "min(b)", "max(a)", "min(a)")
                .expectHeight(1)
                .expectRow(0, 4L, 2L, 1L, -1L);
    }

    @Test
    public void intFiltered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 1,
                2, 4,
                -1, 5,
                8, 2);

        DataFrame agg = df.cols().agg(
                $int(1).max($int(0).mod(2).eq(0)),
                $int(1).min($int(0).mod(2).eq(0)),
                $int("a").max($int("b").mod(2).eq(1)),
                $int("a").min($int("b").mod(2).eq(1))
        );

        new DataFrameAsserts(agg, "max(b)", "min(b)", "max(a)", "min(a)")
                .expectHeight(1)
                .expectRow(0, 4, 2, 1, -1);
    }

    @Test
    public void doubleFiltered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1., 1.01,
                6.5, 15.7,
                -1.2, 5.1,
                8., 2.);

        DataFrame agg = df.cols().agg(
                $double(1).max($double(0).gt(5)),
                $double(1).min($double(0).gt(5)),
                $double("a").max($double("b").gt(5)),
                $double("a").min($double("b").gt(5))
        );

        new DataFrameAsserts(agg, "max(b)", "min(b)", "max(a)", "min(a)")
                .expectHeight(1)
                .expectRow(0, 15.7, 2.0, 6.5, -1.2);
    }

    @Test
    public void min_div_mul() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                2, 1L,
                6, 7L,
                -3, 5L);

        DataFrame agg = df.cols("A", "B").agg(
                $int("a").min().div(2),
                $long(1).min().mul(2));

        new DataFrameAsserts(agg, "A", "B")
                .expectHeight(1)
                .expectRow(0, -1, 2L);
    }

    @Test
    public void max_div_mul() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                2, 1L,
                6, 7L,
                -3, 5L);

        DataFrame agg = df.cols("A", "B").agg(
                $int("a").max().div(2),
                $long(1).max().mul(2));

        new DataFrameAsserts(agg, "A", "B")
                .expectHeight(1)
                .expectRow(0, 3, 14L);
    }

    @Test
    public void date() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                LocalDate.of(2000, 1, 2), LocalDate.of(2001, 5, 10),
                LocalDate.of(2000, 2, 2), LocalDate.of(2001, 6, 10),
                LocalDate.of(2000, 3, 2), LocalDate.of(2001, 3, 2));

        DataFrame agg = df.cols().agg(
                $date("a").min(),
                $date("b").max());

        new DataFrameAsserts(agg, "min(a)", "max(b)")
                .expectHeight(1)
                .expectRow(0, LocalDate.of(2000, 1, 2), LocalDate.of(2001, 6, 10));
    }

    @Test
    public void dateFiltered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                LocalDate.of(2000, 1, 2), LocalDate.of(2001, 5, 10),
                LocalDate.of(2000, 2, 2), LocalDate.of(2001, 6, 10),
                LocalDate.of(2000, 3, 2), LocalDate.of(2001, 3, 2));

        DataFrame agg = df.cols().agg(
                $date("a").min($date("a").gt(LocalDate.of(2000, 1, 2))),
                $date("b").max($date("a").gt(LocalDate.of(2000, 2, 2))));

        new DataFrameAsserts(agg, "min(a)", "max(b)")
                .expectHeight(1)
                .expectRow(0, LocalDate.of(2000, 2, 2), LocalDate.of(2001, 3, 2));
    }

    @Test
    public void time() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                LocalTime.of(2, 1, 2), LocalTime.of(3, 5, 10),
                LocalTime.of(2, 2, 2), LocalTime.of(3, 6, 10),
                LocalTime.of(2, 3, 2), LocalTime.of(3, 3, 2));

        DataFrame agg = df.cols().agg(
                $time("a").min(),
                $time("b").max());

        new DataFrameAsserts(agg, "min(a)", "max(b)")
                .expectHeight(1)
                .expectRow(0, LocalTime.of(2, 1, 2), LocalTime.of(3, 6, 10));
    }

    @Test
    public void timeFiltered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                LocalTime.of(2, 1, 2), LocalTime.of(3, 5, 10),
                LocalTime.of(2, 2, 2), LocalTime.of(3, 6, 10),
                LocalTime.of(2, 3, 2), LocalTime.of(3, 3, 2));

        DataFrame agg = df.cols().agg(
                $time("a").min($time("a").gt(LocalTime.of(2, 1, 2))),
                $time("b").max($time("a").gt(LocalTime.of(2, 2, 2))));

        new DataFrameAsserts(agg, "min(a)", "max(b)")
                .expectHeight(1)
                .expectRow(0, LocalTime.of(2, 2, 2), LocalTime.of(3, 3, 2));
    }

    @Test
    public void dateTime() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                LocalDateTime.of(2000, 1, 2, 2, 1, 2), LocalDateTime.of(2001, 5, 6, 3, 5, 10),
                LocalDateTime.of(2000, 1, 2, 2, 2, 2), LocalDateTime.of(2001, 5, 6, 3, 6, 10),
                LocalDateTime.of(2000, 1, 2, 2, 3, 2), LocalDateTime.of(2001, 5, 6, 3, 3, 2));

        DataFrame agg = df.cols().agg(
                $dateTime("a").min(),
                $dateTime("b").max());

        new DataFrameAsserts(agg, "min(a)", "max(b)")
                .expectHeight(1)
                .expectRow(0, LocalDateTime.of(2000, 1, 2, 2, 1, 2), LocalDateTime.of(2001, 5, 6, 3, 6, 10));
    }

    @Test
    public void str() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                "A", "b",
                "B", "B",
                "C", "A");

        DataFrame agg = df.cols().agg(
                $str("a").min(),
                $str("b").max());

        new DataFrameAsserts(agg, "min(a)", "max(b)")
                .expectHeight(1)
                .expectRow(0, "A", "b");
    }

    @Test
    public void strFiltered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                "A", "b",
                "b", "B",
                "c", "A");

        DataFrame agg = df.cols().agg(
                $str("a").min($str("a").mapBoolVal(s -> Character.isLowerCase(s.charAt(0)))),
                $str("b").max($str("b").mapBoolVal(s -> Character.isUpperCase(s.charAt(0)))));

        new DataFrameAsserts(agg, "min(a)", "max(b)")
                .expectHeight(1)
                .expectRow(0, "b", "B");
    }
}
