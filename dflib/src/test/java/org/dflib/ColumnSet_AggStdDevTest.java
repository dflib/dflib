package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.dflib.Exp.*;

public class ColumnSet_AggStdDevTest {

    static final DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
            3, 3.55, new BigDecimal("3.55"),
            28, 28.9, new BigDecimal("28.9"),
            15, 15.1, new BigDecimal("15.1"),
            -4, -4.7, new BigDecimal("-4.7"),
            3, 2.01, new BigDecimal("2.01"),
            11, 11.003, new BigDecimal("11.003"));

    @Test
    public void population_implicit() {

        DataFrame agg = df.cols().agg(
                $int("a").stdDev(),
                $double(1).stdDev(),
                $decimal(2).stdDev());

        new DataFrameAsserts(agg, "stdDev(a)", "stdDev(b)", "stdDev(c)")
                .expectHeight(1)
                .expectRow(0, 10.338708279513881d, 10.823632688858826, new BigDecimal("10.8236326888588"));
    }

    @Test
    public void population() {

        DataFrame agg = df.cols().agg(
                $int("a").stdDev(true),
                $double(1).stdDev(true),
                $decimal(2).stdDev(true));

        new DataFrameAsserts(agg, "stdDev(a)", "stdDev(b)", "stdDev(c)")
                .expectHeight(1)
                .expectRow(0, 10.338708279513881d, 10.823632688858826, new BigDecimal("10.8236326888588"));
    }

    @Test
    public void sample() {

        DataFrame agg = df.cols().agg(
                $int("a").stdDev(false),
                $double(1).stdDev(false),
                $decimal(2).stdDev(false));

        new DataFrameAsserts(agg, "stdDev(a)", "stdDev(b)", "stdDev(c)")
                .expectHeight(1)
                .expectRow(0, 11.325487480310358d, 11.856695555676549d, new BigDecimal("11.8566955556765"));
    }
}
