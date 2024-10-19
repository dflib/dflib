package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.dflib.Exp.$col;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColumnSet_AggCollectionsTest {

    @Test
    public void set() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "x",
                1, "a");

        DataFrame agg = df.cols("A", "B").agg($col("a").set(), $col(1).set());

        new DataFrameAsserts(agg, "A", "B")
                .expectHeight(1)
                .expectRow(0, Set.of(1, 2), Set.of("x", "a"));
    }

    @Test
    public void list() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "x",
                1, "a");

        DataFrame agg = df.cols("A", "B").agg($col("a").list(), $col(1).list());

        new DataFrameAsserts(agg, "A", "B")
                .expectHeight(1)
                .expectRow(0, List.of(1, 2, 1), List.of("x", "x", "a"));
    }

    @Test
    public void array() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "x",
                1, "a");

        DataFrame agg = df.cols("A", "B").agg($col("a").array(new Integer[0]), $col(1).array(new String[0]));

        new DataFrameAsserts(agg, "A", "B")
                .expectHeight(1)
                .expectRow(0, new Integer[]{1, 2, 1}, new String[]{"x", "x", "a"});
    }

    @Test
    public void set_toQL() {
        assertEquals("set(a)", $col("a").set().toQL());
    }

    @Test
    public void list_toQL() {
        assertEquals("list(a)", $col("a").list().toQL());
    }

    @Test
    public void array_toQL() {
        assertEquals("array(a)", $col("a").array(new String[0]).toQL());
    }
}
