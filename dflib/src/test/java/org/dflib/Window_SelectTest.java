package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;

public class Window_SelectTest {

    static final DataFrame TEST_DF = DataFrame.foldByRow("a", "b").of(
            1, "x",
            2, "y",
            1, "z",
            0, "a",
            1, "x");

    @Test
    public void empty() {
        DataFrame df = DataFrame.empty("a", "b", "c");
        DataFrame r = df.over().select($int("a").sum());
        new DataFrameAsserts(r, "sum(a)").expectHeight(0);
    }

    @Test
    public void implicit() {
        DataFrame r = TEST_DF.over().select($int("a").sum());
        new DataFrameAsserts(r, "sum(a)")
                .expectHeight(5)
                .expectRow(0, 5L)
                .expectRow(1, 5L)
                .expectRow(2, 5L)
                .expectRow(3, 5L)
                .expectRow(4, 5L);
    }

    @Test
    public void byName() {

        DataFrame r = TEST_DF.over()
                .cols("a")
                .select($int("a").sum());

        new DataFrameAsserts(r, "a")
                .expectHeight(5)
                .expectRow(0, 5L)
                .expectRow(1, 5L)
                .expectRow(2, 5L)
                .expectRow(3, 5L)
                .expectRow(4, 5L);
    }

    @Test
    public void byName_MultiExp() {

        DataFrame r = TEST_DF.over()
                .cols("a", "la", "s", "rn", "cs")
                .select(
                        $col("a"), // non-aggregating
                        $col("a").first(), // aggregating
                        $int("a").sum(), // aggregating
                        rowNum(), // non-aggregating
                        $int("a").cumSum() // non-aggregating
                );

        new DataFrameAsserts(r, "a", "la", "s", "rn", "cs")
                .expectHeight(5)
                .expectRow(0, 1, 1, 5L, 1, 1L)
                .expectRow(1, 2, 1, 5L, 2, 3L)
                .expectRow(2, 1, 1, 5L, 3, 4L)
                .expectRow(3, 0, 1, 5L, 4, 4L)
                .expectRow(4, 1, 1, 5L, 5, 5L);
    }

    @Test
    public void byName_MultiExp_StrExp() {

        DataFrame r = TEST_DF.over()
                .select("a, first(a) as la, sum(int(a)) as s, rowNum() as rn, cumSum(int(a)) as cs");

        new DataFrameAsserts(r, "a", "la", "s", "rn", "cs")
                .expectHeight(5)
                .expectRow(0, 1, 1, 5L, 1, 1L)
                .expectRow(1, 2, 1, 5L, 2, 3L)
                .expectRow(2, 1, 1, 5L, 3, 4L)
                .expectRow(3, 0, 1, 5L, 4, 4L)
                .expectRow(4, 1, 1, 5L, 5, 5L);
    }

    @Test
    public void byPos() {
        DataFrame r = TEST_DF.over()
                .cols(0, 1)
                .select(
                        $int("a").sum(),
                        $col("b").first()
                );

        new DataFrameAsserts(r, "a", "b")
                .expectHeight(5)
                .expectRow(0, 5L, "x")
                .expectRow(1, 5L, "x")
                .expectRow(2, 5L, "x")
                .expectRow(3, 5L, "x")
                .expectRow(4, 5L, "x");
    }

    @Test
    public void byPredicate() {
        DataFrame r = TEST_DF.over()
                .cols("a"::equals)
                .select($int("a").sum());

        new DataFrameAsserts(r, "a")
                .expectHeight(5)
                .expectRow(0, 5L)
                .expectRow(1, 5L)
                .expectRow(2, 5L)
                .expectRow(3, 5L)
                .expectRow(4, 5L);
    }

    @Test
    public void except_ByName() {
        DataFrame r = TEST_DF.over()
                .colsExcept("b")
                .select($int("a").sum());

        new DataFrameAsserts(r, "a")
                .expectHeight(5)
                .expectRow(0, 5L)
                .expectRow(1, 5L)
                .expectRow(2, 5L)
                .expectRow(3, 5L)
                .expectRow(4, 5L);
    }

    @Test
    public void except_ByIndex() {
        DataFrame r = TEST_DF.over()
                .colsExcept(1)
                .select($int("a").sum());

        new DataFrameAsserts(r, "a")
                .expectHeight(5)
                .expectRow(0, 5L)
                .expectRow(1, 5L)
                .expectRow(2, 5L)
                .expectRow(3, 5L)
                .expectRow(4, 5L);
    }

    @Test
    public void except_ByPredicate() {

        DataFrame r = TEST_DF.over()
                .colsExcept("b"::equals)
                .select($int("a").sum());

        new DataFrameAsserts(r, "a")
                .expectHeight(5)
                .expectRow(0, 5L)
                .expectRow(1, 5L)
                .expectRow(2, 5L)
                .expectRow(3, 5L)
                .expectRow(4, 5L);
    }

}
