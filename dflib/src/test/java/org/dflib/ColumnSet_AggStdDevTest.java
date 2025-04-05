package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$double;
import static org.dflib.Exp.$int;

public class ColumnSet_AggStdDevTest {

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
                $int("a").stdDev(),
                $double(1).stdDev());

        new DataFrameAsserts(agg, "variance(a)", "variance(b)")
                .expectHeight(1)
                .expectRow(0, 10.338708279513881d, 10.823632688858826);
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
                $int("a").stdDev(true),
                $double(1).stdDev(true));

        new DataFrameAsserts(agg, "variance(a)", "variance(b)")
                .expectHeight(1)
                .expectRow(0, 10.338708279513881d, 10.823632688858826);
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
                $int("a").stdDev(false),
                $double(1).stdDev(false));

        new DataFrameAsserts(agg, "variance(a)", "variance(b)")
                .expectHeight(1)
                .expectRow(0, 11.325487480310358d, 11.856695555676549d);
    }

}
