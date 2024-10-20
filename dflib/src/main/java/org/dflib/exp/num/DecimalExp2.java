package org.dflib.exp.num;

import org.dflib.DecimalExp;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.map.MapExp2;

import java.math.BigDecimal;
import java.util.function.BiFunction;

public class DecimalExp2<R> extends MapExp2<BigDecimal, R, BigDecimal> implements DecimalExp {

    public static <R> DecimalExp2 mapVal(String opName, Exp<BigDecimal> left, Exp<R> right, BiFunction<BigDecimal, R, BigDecimal> op) {
        return new DecimalExp2(opName, left, right, valToSeries(op));
    }

    protected DecimalExp2(
            String opName,
            Exp<BigDecimal> left,
            Exp<R> right,
            BiFunction<Series<BigDecimal>, Series<R>, Series<BigDecimal>> op) {
        super(opName, BigDecimal.class, left, right, op);
    }
}
