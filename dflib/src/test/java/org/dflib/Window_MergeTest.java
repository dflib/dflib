package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;

public class Window_MergeTest {

    static final DataFrame EMPTY_TEST_DF = DataFrame.empty("a", "b", "c");

    static final DataFrame TEST_DF = DataFrame.foldByRow("a", "b").of(
            1, "x",
            2, "y",
            1, "z",
            0, "a",
            1, "x");

    @Test
    public void all_Empty_AddCol() {
        DataFrame r = EMPTY_TEST_DF.over().cols("a", "d").merge($col("a"), $int("a").sum());
        new DataFrameAsserts(r, "a", "b", "c", "d").expectHeight(0);
    }

    @Test
    public void implicit() {

        DataFrame r = TEST_DF.over().merge($int("a").sum());
        new DataFrameAsserts(r, "a", "b", "sum(a)")
                .expectHeight(5)
                .expectRow(0, 1, "x", 5L)
                .expectRow(1, 2, "y", 5L)
                .expectRow(2, 1, "z", 5L)
                .expectRow(3, 0, "a", 5L)
                .expectRow(4, 1, "x", 5L);
    }

    @Test
    public void byName() {
        DataFrame r = TEST_DF.over()
                .cols("c")
                .merge($int("a").sum());

        new DataFrameAsserts(r, "a", "b", "c")
                .expectHeight(5)
                .expectRow(0, 1, "x", 5L)
                .expectRow(1, 2, "y", 5L)
                .expectRow(2, 1, "z", 5L)
                .expectRow(3, 0, "a", 5L)
                .expectRow(4, 1, "x", 5L);
    }

    @Test
    public void byName_MultiExp() {

        DataFrame r = TEST_DF.over()
                .cols("a", "s", "rn", "cs")
                .merge(
                        $col("a"), // non-aggregating
                        $int("a").sum(), // aggregating
                        rowNum(), // non-aggregating
                        $int("a").cumSum() // non-aggregating
                );

        new DataFrameAsserts(r, "a", "b", "s", "rn", "cs")
                .expectHeight(5)
                .expectRow(0, 1, "x", 5L, 1, 1L)
                .expectRow(1, 2, "y", 5L, 2, 3L)
                .expectRow(2, 1, "z", 5L, 3, 4L)
                .expectRow(3, 0, "a", 5L, 4, 4L)
                .expectRow(4, 1, "x", 5L, 5, 5L);
    }

    @Test
    public void byName_MultiExp_StrExp() {

        DataFrame r = TEST_DF.over()
                .merge("a, sum(int(a)) as s, rowNum() as rn, cumSum(int(a)) as cs");

        new DataFrameAsserts(r, "a", "b", "s", "rn", "cs")
                .expectHeight(5)
                .expectRow(0, 1, "x", 5L, 1, 1L)
                .expectRow(1, 2, "y", 5L, 2, 3L)
                .expectRow(2, 1, "z", 5L, 3, 4L)
                .expectRow(3, 0, "a", 5L, 4, 4L)
                .expectRow(4, 1, "x", 5L, 5, 5L);
    }

    @Test
    public void byPos() {

        DataFrame r = TEST_DF.over()
                .cols(1, 2)
                .merge(
                        $col("b").first(),
                        $int("a").sum()
                );

        new DataFrameAsserts(r, "a", "b", "2")
                .expectHeight(5)
                .expectRow(0, 1, "x", 5L)
                .expectRow(1, 2, "x", 5L)
                .expectRow(2, 1, "x", 5L)
                .expectRow(3, 0, "x", 5L)
                .expectRow(4, 1, "x", 5L);
    }

    @Test
    public void byPredicate() {

        DataFrame r = TEST_DF.over()
                .cols("a"::equals)
                .merge($int("a").sum());

        new DataFrameAsserts(r, "a", "b")
                .expectHeight(5)
                .expectRow(0, 5L, "x")
                .expectRow(1, 5L, "y")
                .expectRow(2, 5L, "z")
                .expectRow(3, 5L, "a")
                .expectRow(4, 5L, "x");
    }

    @Test
    public void except_ByName() {

        DataFrame r = TEST_DF.over()
                .colsExcept("b")
                .merge($int("a").sum());

        new DataFrameAsserts(r, "a", "b")
                .expectHeight(5)
                .expectRow(0, 5L, "x")
                .expectRow(1, 5L, "y")
                .expectRow(2, 5L, "z")
                .expectRow(3, 5L, "a")
                .expectRow(4, 5L, "x");
    }

    @Test
    public void except_ByIndex() {
        DataFrame r = TEST_DF.over()
                .colsExcept(1)
                .merge($int("a").sum());

        new DataFrameAsserts(r, "a", "b")
                .expectHeight(5)
                .expectRow(0, 5L, "x")
                .expectRow(1, 5L, "y")
                .expectRow(2, 5L, "z")
                .expectRow(3, 5L, "a")
                .expectRow(4, 5L, "x");
    }

    @Test
    public void except_ByPredicate() {

        DataFrame r = TEST_DF.over()
                .colsExcept("b"::equals)
                .merge($int("a").sum());

        new DataFrameAsserts(r, "a", "b")
                .expectHeight(5)
                .expectRow(0, 5L, "x")
                .expectRow(1, 5L, "y")
                .expectRow(2, 5L, "z")
                .expectRow(3, 5L, "a")
                .expectRow(4, 5L, "x");
    }

}
