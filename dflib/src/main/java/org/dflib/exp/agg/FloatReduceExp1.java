package org.dflib.exp.agg;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.exp.Exp1;
import org.dflib.series.FloatSingleValueSeries;

import java.util.function.Function;

/**
 * @since 2.0.0
 */
public class FloatReduceExp1<F> extends Exp1<F, Float> implements NumExp<Float> {

    private final Function<Series<F>, Float> op;
    private final Condition filter;

    public FloatReduceExp1(String opName, Exp<F> exp, Function<Series<F>, Float> op, Condition filter) {
        super(opName, Float.class, exp);
        this.op = op;
        this.filter = filter;
    }

    @Override
    public Series<Float> eval(Series<?> s) {
        return super.eval(filter != null ? s.select(filter) : s);
    }

    @Override
    public Series<Float> eval(DataFrame df) {
        return super.eval(filter != null ? df.rows(filter).select() : df);
    }

    @Override
    protected Series<Float> doEval(Series<F> s) {
        // TODO: optimize for primitive series.
        //  E.g. "IntSeries.average()" is faster than "AggregatorFunctions.averageDouble()"
        float val = op.apply(s);
        return new FloatSingleValueSeries(val, 1);
    }
}
