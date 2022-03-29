package com.nhl.dflib.exp.num;

import com.nhl.dflib.DecimalExp;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.map.MapExp2;

import java.math.BigDecimal;
import java.util.function.BiFunction;

/**
 * @since 0.11
 */
public class DecimalExp2 extends MapExp2<BigDecimal, BigDecimal, BigDecimal> implements DecimalExp {

    public static DecimalExp2 mapVal(String opName, Exp<BigDecimal> left, Exp<BigDecimal> right, BiFunction<BigDecimal, BigDecimal, BigDecimal> op) {
        return new DecimalExp2(opName, left, right, valToSeries(op,BigDecimal.class));
    }

    protected DecimalExp2(
            String opName,
            Exp<BigDecimal> left,
            Exp<BigDecimal> right,
            BiFunction<Series<BigDecimal>, Series<BigDecimal>, Series<BigDecimal>> op) {
        super(opName, BigDecimal.class, left, right, op);
    }
}
