package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.*;

public class ColumnSet_AggVConcatTest {

    @Test
    public void agg() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                0, "a");

        DataFrame agg = df.cols("a", "b").agg(
                $col("a").vConcat("_"),
                $col(1).vConcat(" ", "[", "]"));

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, "1_0", "[x a]");
    }

    @Test
    public void aggFiltered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                7, 1,
                -1, 5,
                -4, 5,
                8, 8);

        DataFrame agg = df.cols("b", "a").agg(
                $col(1).vConcat($int(0).mod(2).eq(0), "_"),
                $col("a").vConcat($int("b").mod(2).eq(1), ", ", "[", "]"));

        new DataFrameAsserts(agg, "b", "a")
                .expectHeight(1)
                .expectRow(0, "5_8", "[7, -1, -4]");
    }

    @Test
    public void toQL() {
        assertEquals("vConcat(a,'_')", $col("a").vConcat("_").toQL());
        assertEquals("vConcat(a,'_','[',']')", $col("a").vConcat("_", "[", "]").toQL());
        assertEquals("vConcat(a,'_')", $col("a").vConcat($bool("b"), "_").toQL());
    }
}
