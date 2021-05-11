package com.nhl.dflib.exp.agg;

import com.nhl.dflib.Series;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @since 0.11
 */
public class BigDecimalSeriesSum {

    // TODO: these generic limits are dumb. Not planning to extend BigDecimal
    //   yet needed to satisfy SimpleSeriesAggregator constructor
    public static <T extends BigDecimal> BigDecimal sum(Series<T> s) {

        int size = s.size();
        if (size == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = s.get(0);
        for (int i = 1; i < size; i++) {
            sum = sum.add(s.get(i));
        }

        return sum;
    }

    public static <T extends BigDecimal> BigDecimal sum(Series<T> s, int resultScale, RoundingMode resultRoundingMode) {
        BigDecimal r = sum(s);
        return r != null ? r.setScale(resultScale, resultRoundingMode) : null;
    }
}
