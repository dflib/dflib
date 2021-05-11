package com.nhl.dflib.exp.num;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.LongSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.Exp;
import com.nhl.dflib.exp.condition.BinaryCondition;

import java.util.function.BiFunction;

/**
 * @since 0.11
 */
public class LongBinaryCondition extends BinaryCondition<Long, Long> {

    private final BiFunction<LongSeries, LongSeries, BooleanSeries> primitiveOp;

    public LongBinaryCondition(
            String opName,
            Exp<Long> left,
            Exp<Long> right,
            BiFunction<Series<Long>, Series<Long>, BooleanSeries> op,
            BiFunction<LongSeries, LongSeries, BooleanSeries> primitiveOp) {

        super(opName, left, right, op);
        this.primitiveOp = primitiveOp;
    }

    @Override
    protected BooleanSeries eval(Series<Long> ls, Series<Long> rs) {
        return (ls instanceof LongSeries && rs instanceof LongSeries)
                ? primitiveOp.apply((LongSeries) ls, (LongSeries) rs)
                : super.eval(ls, rs);
    }
}
