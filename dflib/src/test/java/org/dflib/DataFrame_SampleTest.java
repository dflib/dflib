package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.Random;

@Deprecated
public class DataFrame_SampleTest {

    @Test
    public void sampleRows() {

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "x",
                        2, "y",
                        4, "z",
                        5, "a",
                        -1, "x",
                        -100, "m")
                // using fixed seed to get reproducible result
                .sampleRows(3, new Random(7));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 5, "a")
                .expectRow(1, 1, "x")
                .expectRow(2, 2, "y");
    }

    @Test
    public void sampleColumns() {

        DataFrame df = DataFrame
                .foldByRow("a", "b", "c", "d").of(
                        1, "x", "m", "z",
                        2, "y", "x", "E")
                // using fixed seed to get reproducible result
                .sampleColumns(2, new Random(11));

        new DataFrameAsserts(df, "b", "a")
                .expectHeight(2)
                .expectRow(0, "x", 1)
                .expectRow(1, "y", 2);
    }
}
