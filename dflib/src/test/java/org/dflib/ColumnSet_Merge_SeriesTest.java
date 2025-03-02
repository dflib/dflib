package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class ColumnSet_Merge_SeriesTest {

    @Test
    public void all() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols()
                .merge(Series.of("A", "B"));

        new DataFrameAsserts(df, "a", "b", "2")
                .expectHeight(2)
                .expectRow(0, 1, "x", "A")
                .expectRow(1, 2, "y", "B");
    }

    @Test
    public void all_NameOverlap() {
        DataFrame df = DataFrame.foldByRow("a", "2")
                .of(1, "x", 2, "y")
                .cols()
                .merge(Series.of("A", "B"));

        new DataFrameAsserts(df, "a", "2", "2_")
                .expectHeight(2)
                .expectRow(0, 1, "x", "A")
                .expectRow(1, 2, "y", "B");
    }

    @Test
    public void byName() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("b")
                .merge(Series.of("A", "B"));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, "A")
                .expectRow(1, 2, "B");
    }

    @Test
    public void byName_Duplicate() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("b", "b").merge(
                        Series.of("A", "B"),
                        Series.of("C", "D")
                );

        new DataFrameAsserts(df, "a", "b", "b_")
                .expectHeight(2)
                .expectRow(0, 1, "A", "C")
                .expectRow(1, 2, "B", "D");
    }
}
