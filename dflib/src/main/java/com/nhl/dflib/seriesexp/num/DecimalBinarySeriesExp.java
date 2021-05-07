package com.nhl.dflib.seriesexp.num;

import com.nhl.dflib.NumericSeriesExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.seriesexp.BinarySeriesExp;

import java.math.BigDecimal;
import java.util.function.BiFunction;

/**
 * @since 0.11
 */
public class DecimalBinarySeriesExp extends BinarySeriesExp<BigDecimal, BigDecimal, BigDecimal> implements NumericSeriesExp<BigDecimal> {

    protected DecimalBinarySeriesExp(
            String opName,
            SeriesExp<BigDecimal> left,
            SeriesExp<BigDecimal> right,
            BiFunction<Series<BigDecimal>, Series<BigDecimal>, Series<BigDecimal>> op) {

        super(opName, BigDecimal.class, left, right, op);
    }
}
