package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$double;
import static org.dflib.Exp.$int;

public class ColumnSet_AggVarianceTest {

    @Test
    public void population_implicit() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                3, 3.55,
                28, 28.9,
                15, 15.1,
                -4, -4.7,
                3, 2.01,
                11, 11.003);

        DataFrame agg = df.cols().agg(
                $int("a").variance(),
                $double(1).variance());

        new DataFrameAsserts(agg, "variance(a)", "variance(b)")
                .expectHeight(1)
                .expectRow(0, 106.88888888888887d, 117.15102458333335d);
    }

    @Test
    public void population() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                3, 3.55,
                28, 28.9,
                15, 15.1,
                -4, -4.7,
                3, 2.01,
                11, 11.003);

        DataFrame agg = df.cols().agg(
                $int("a").variance(true),
                $double(1).variance(true));

        new DataFrameAsserts(agg, "variance(a)", "variance(b)")
                .expectHeight(1)
                .expectRow(0, 106.88888888888887d, 117.15102458333335d);
    }

    @Test
    public void sample() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                3, 3.55,
                28, 28.9,
                15, 15.1,
                -4, -4.7,
                3, 2.01,
                11, 11.003);

        DataFrame agg = df.cols().agg(
                $int("a").variance(false),
                $double(1).variance(false));

        new DataFrameAsserts(agg, "variance(a)", "variance(b)")
                .expectHeight(1)
                .expectRow(0, 128.26666666666665, 140.5812295);
    }

}
