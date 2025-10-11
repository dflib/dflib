package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.dflib.Exp.*;

public class ColumnSet_AggVarianceTest {

    static final DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
            3, 3.55, new BigDecimal("3.55"),
            28, 28.9, new BigDecimal("28.9"),
            15, 15.1, new BigDecimal("15.1"),
            -4, -4.7, new BigDecimal("-4.7"),
            3, 2.01, new BigDecimal("2.01"),
            11, 11.003, new BigDecimal("11.003"));

    @Test
    public void sample_empty() {
        DataFrame agg = DataFrame.empty("a", "b", "c")
                .cols().agg(
                        $int("a").variance(false),
                        $double("b").variance(false),
                        $decimal("c").variance(false));

        new DataFrameAsserts(agg, "variance(a)", "variance(b)", "variance(c)")
                .expectHeight(1)
                .expectRow(0, Double.NaN, Double.NaN, null);
    }

    @Test
    public void population_empty() {
        DataFrame agg = DataFrame.empty("a", "b", "c")
                .cols().agg(
                        $int("a").variance(true),
                        $double("b").variance(true),
                        $decimal("c").variance(true));

        new DataFrameAsserts(agg, "variance(a)", "variance(b)", "variance(c)")
                .expectHeight(1)
                .expectRow(0, Double.NaN, Double.NaN, null);
    }

    @Test
    public void sample_one() {
        DataFrame agg = DataFrame.foldByRow("a", "b", "c").of(20, 10.1, new BigDecimal("12.34"))
                .cols().agg(
                        $int("a").variance(false),
                        $double("b").variance(false),
                        $decimal("c").variance(false));

        new DataFrameAsserts(agg, "variance(a)", "variance(b)", "variance(c)")
                .expectHeight(1)
                .expectRow(0, Double.NaN, Double.NaN, null);
    }

    @Test
    public void population_one() {
        DataFrame agg = DataFrame.foldByRow("a", "b", "c").of(20, 10.1, new BigDecimal("12.34"))
                .cols().agg(
                        $int("a").variance(true),
                        $double("b").variance(true),
                        $decimal("c").variance(true));

        new DataFrameAsserts(agg, "variance(a)", "variance(b)", "variance(c)")
                .expectHeight(1)
                .expectRow(0, 0., 0., BigDecimal.ZERO);
    }

    @Test
    public void population_implicit() {
        DataFrame agg = df.cols().agg(
                $int("a").variance(),
                $double(1).variance(),
                $decimal(2).variance());

        new DataFrameAsserts(agg, "variance(a)", "variance(b)", "variance(c)")
                .expectHeight(1)
                .expectRow(0, 106.88888888888887d, 117.15102458333335d, new BigDecimal("117.151024583333"));
    }

    @Test
    public void population() {
        DataFrame agg = df.cols().agg(
                $int("a").variance(true),
                $double(1).variance(true),
                $decimal(2).variance(true));

        new DataFrameAsserts(agg, "variance(a)", "variance(b)", "variance(c)")
                .expectHeight(1)
                .expectRow(0, 106.88888888888887d, 117.15102458333335d, new BigDecimal("117.151024583333"));
    }

    @Test
    public void sample() {
        DataFrame agg = df.cols().agg(
                $int("a").variance(false),
                $double(1).variance(false),
                $decimal(2).variance(false));

        new DataFrameAsserts(agg, "variance(a)", "variance(b)", "variance(c)")
                .expectHeight(1)
                .expectRow(0, 128.26666666666665, 140.5812295, new BigDecimal("140.58122950"));
    }

}
