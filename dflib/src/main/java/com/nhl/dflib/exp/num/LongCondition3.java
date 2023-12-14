package com.nhl.dflib.exp.num;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.Exp;
import com.nhl.dflib.LongSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.map.MapCondition3;
import com.nhl.dflib.f.Function3;
import com.nhl.dflib.f.Predicate3;

/**
 * @since 1.0.0-M19
 */
public class LongCondition3 extends MapCondition3<Long, Long, Long> {

    public static LongCondition3 mapVal(
            String opName1,
            String opName2,
            Exp<Long> one,
            Exp<Long> two,
            Exp<Long> three,
            Predicate3<Long, Long, Long> op,
            Function3<LongSeries, LongSeries, LongSeries, BooleanSeries> primitiveOp) {
        return new LongCondition3(opName1, opName2, one, two, three, valToSeries(op), primitiveOp);
    }

    private final Function3<LongSeries, LongSeries, LongSeries, BooleanSeries> primitiveOp;

    public LongCondition3(
            String opName1,
            String opName2,
            Exp<Long> one,
            Exp<Long> two,
            Exp<Long> three,
            Function3<Series<Long>, Series<Long>, Series<Long>, BooleanSeries> op,
            Function3<LongSeries, LongSeries, LongSeries, BooleanSeries> primitiveOp) {

        super(opName1, opName2, one, two, three, op);
        this.primitiveOp = primitiveOp;
    }

    @Override
    protected BooleanSeries doEval(Series<Long> one, Series<Long> two, Series<Long> three) {
        return one instanceof LongSeries && two instanceof LongSeries && three instanceof LongSeries
                ? primitiveOp.apply((LongSeries) one, (LongSeries) two, (LongSeries) three)
                : super.doEval(one, two, three);
    }
}
