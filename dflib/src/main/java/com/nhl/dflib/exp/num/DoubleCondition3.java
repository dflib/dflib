package com.nhl.dflib.exp.num;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.DoubleSeries;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.map.MapCondition3;
import com.nhl.dflib.f.Function3;
import com.nhl.dflib.f.Predicate3;

/**
 * @since 1.0.0-M19
 */
public class DoubleCondition3 extends MapCondition3<Double, Double, Double> {

    public static DoubleCondition3 mapVal(
            String opName1,
            String opName2,
            Exp<Double> one,
            Exp<Double> two,
            Exp<Double> three,
            Predicate3<Double, Double, Double> op,
            Function3<DoubleSeries, DoubleSeries, DoubleSeries, BooleanSeries> primitiveOp) {
        return new DoubleCondition3(opName1, opName2, one, two, three, valToSeries(op), primitiveOp);
    }

    private final Function3<DoubleSeries, DoubleSeries, DoubleSeries, BooleanSeries> primitiveOp;

    public DoubleCondition3(
            String opName1,
            String opName2,
            Exp<Double> one,
            Exp<Double> two,
            Exp<Double> three,
            Function3<Series<Double>, Series<Double>, Series<Double>, BooleanSeries> op,
            Function3<DoubleSeries, DoubleSeries, DoubleSeries, BooleanSeries> primitiveOp) {

        super(opName1, opName2, one, two, three, op);
        this.primitiveOp = primitiveOp;
    }

    @Override
    protected BooleanSeries doEval(Series<Double> one, Series<Double> two, Series<Double> three) {
        return one instanceof DoubleSeries && two instanceof DoubleSeries && three instanceof DoubleSeries
                ? primitiveOp.apply((DoubleSeries) one, (DoubleSeries) two, (DoubleSeries) three)
                : super.doEval(one, two, three);
    }
}
