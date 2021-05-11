package com.nhl.dflib.exp.num;

import com.nhl.dflib.NumericExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.exp.BinarySeriesExp;

import java.math.BigDecimal;
import java.util.function.BiFunction;

/**
 * @since 0.11
 */
public class DecimalBinaryExp extends BinarySeriesExp<BigDecimal, BigDecimal, BigDecimal> implements NumericExp<BigDecimal> {

    protected DecimalBinaryExp(
            String opName,
            SeriesExp<BigDecimal> left,
            SeriesExp<BigDecimal> right,
            BiFunction<Series<BigDecimal>, Series<BigDecimal>, Series<BigDecimal>> op) {

        super(opName, BigDecimal.class, left, right, op);
    }
}
