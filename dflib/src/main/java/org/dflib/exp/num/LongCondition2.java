package org.dflib.exp.num;


import org.dflib.exp.map.MapCondition2;
import org.dflib.BooleanSeries;
import org.dflib.Exp;
import org.dflib.LongSeries;
import org.dflib.Series;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * @since 0.11
 */
public class LongCondition2 extends MapCondition2<Long, Long> {

    public static LongCondition2 mapVal(
            String opName,
            Exp<Long> left,
            Exp<Long> right,
            BiPredicate<Long, Long> op,
            BiFunction<LongSeries, LongSeries, BooleanSeries> primitiveOp) {
        return new LongCondition2(opName, left, right, valToSeries(op), primitiveOp);
    }

    private final BiFunction<LongSeries, LongSeries, BooleanSeries> primitiveOp;

    public LongCondition2(
            String opName,
            Exp<Long> left,
            Exp<Long> right,
            BiFunction<Series<Long>, Series<Long>, BooleanSeries> op,
            BiFunction<LongSeries, LongSeries, BooleanSeries> primitiveOp) {

        super(opName, left, right, op);
        this.primitiveOp = primitiveOp;
    }

    @Override
    protected BooleanSeries doEval(Series<Long> left, Series<Long> right) {
        return (left instanceof LongSeries && right instanceof LongSeries)
                ? primitiveOp.apply((LongSeries) left, (LongSeries) right)
                : super.doEval(left, right);
    }
}
