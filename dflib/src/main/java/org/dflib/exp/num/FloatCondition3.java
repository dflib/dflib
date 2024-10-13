package org.dflib.exp.num;

import org.dflib.BooleanSeries;
import org.dflib.Exp;
import org.dflib.FloatSeries;
import org.dflib.Series;
import org.dflib.exp.map.MapCondition3;
import org.dflib.f.Function3;
import org.dflib.f.Predicate3;

/**
 * @since 1.1.0
 */
public class FloatCondition3 extends MapCondition3<Float, Float, Float> {

    public static FloatCondition3 mapVal(
            String opName1,
            String opName2,
            Exp<Float> one,
            Exp<Float> two,
            Exp<Float> three,
            Predicate3<Float, Float, Float> op,
            Function3<FloatSeries, FloatSeries, FloatSeries, BooleanSeries> primitiveOp) {
        return new FloatCondition3(opName1, opName2, one, two, three, valToSeries(op), primitiveOp);
    }

    private final Function3<FloatSeries, FloatSeries, FloatSeries, BooleanSeries> primitiveOp;

    public FloatCondition3(
            String opName1,
            String opName2,
            Exp<Float> one,
            Exp<Float> two,
            Exp<Float> three,
            Function3<Series<Float>, Series<Float>, Series<Float>, BooleanSeries> op,
            Function3<FloatSeries, FloatSeries, FloatSeries, BooleanSeries> primitiveOp) {

        super(opName1, opName2, one, two, three, op);
        this.primitiveOp = primitiveOp;
    }

    @Override
    protected BooleanSeries doEval(Series<Float> one, Series<Float> two, Series<Float> three) {
        return one instanceof FloatSeries && two instanceof FloatSeries && three instanceof FloatSeries
                ? primitiveOp.apply((FloatSeries) one, (FloatSeries) two, (FloatSeries) three)
                : super.doEval(one, two, three);
    }
}
