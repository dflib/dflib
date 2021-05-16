package com.nhl.dflib.exp.num;

import com.nhl.dflib.DecimalExp;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.map.MapExpScalar2;

import java.math.BigDecimal;
import java.util.function.BiFunction;

/**
 * @since 0.11
 */
public class DecimalExpScalar2<R> extends MapExpScalar2<BigDecimal, R, BigDecimal> implements DecimalExp {

    public static <R> DecimalExpScalar2<R> mapVal(String opName, Exp<BigDecimal> left, R right, BiFunction<BigDecimal, R, BigDecimal> op) {
        return new DecimalExpScalar2<>(opName, left, right, valToSeries(op));
    }

    public DecimalExpScalar2(String opName, Exp<BigDecimal> left, R right, BiFunction<Series<BigDecimal>, R, Series<BigDecimal>> op) {
        super(opName, BigDecimal.class, left, right, op);
    }
}
