package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class ColumnSet_ColsSampl_SelectTest {

    @Test
    public void byName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c", "d").of(
                        1, "x", "m", "z",
                        2, "y", "x", "E")
                // using fixed seed to get reproducible result
                .colsSample(2, new Random(11)).select();

        new DataFrameAsserts(df, "b", "a")
                .expectHeight(2)
                .expectRow(0, "x", 1)
                .expectRow(1, "y", 2);
    }
}
