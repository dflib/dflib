package org.dflib.exp.agg;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.FloatSeries;
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
    public FloatSeries eval(Series<?> s) {
        return new FloatSingleValueSeries(reduce(s), s.size());
    }

    @Override
    public FloatSeries eval(DataFrame df) {
        return new FloatSingleValueSeries(reduce(df), df.height());
    }

    @Override
    public Float reduce(DataFrame df) {
        // TODO: optimize for primitive series.
        //  E.g. "DoubleSeries.avg()" is faster than "AggregatorFunctions.averageDouble()"
        return op.apply(exp.eval(filter != null ? df.rows(filter).select() : df));
    }

    @Override
    public Float reduce(Series<?> s) {
        // TODO: optimize for primitive series.
        //  E.g. "DoubleSeries.avg()" is faster than "AggregatorFunctions.averageDouble()"
        return op.apply(exp.eval(filter != null ? s.select(filter) : s));
    }
}
