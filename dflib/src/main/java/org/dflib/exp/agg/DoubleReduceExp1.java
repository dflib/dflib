package org.dflib.exp.agg;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.DoubleSeries;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.exp.Exp1;
import org.dflib.series.DoubleSingleValueSeries;

import java.util.function.Function;

/**
 * @since 2.0.0
 */
public class DoubleReduceExp1<F> extends Exp1<F, Double> implements NumExp<Double> {

    private final Function<Series<F>, Double> op;
    private final Condition filter;

    public DoubleReduceExp1(String opName, Exp<F> exp, Function<Series<F>, Double> op, Condition filter) {
        super(opName, Double.class, exp);
        this.op = op;
        this.filter = filter;
    }

    @Override
    public DoubleSeries eval(Series<?> s) {
        return new DoubleSingleValueSeries(reduce(s), s.size());
    }

    @Override
    public DoubleSeries eval(DataFrame df) {
        return new DoubleSingleValueSeries(reduce(df), df.height());
    }

    @Override
    public Double reduce(DataFrame df) {
        // TODO: optimize for primitive series.
        //  E.g. "DoubleSeries.avg()" is faster than "AggregatorFunctions.averageDouble()"
        return op.apply(exp.eval(filter != null ? df.rows(filter).select() : df));
    }

    @Override
    public Double reduce(Series<?> s) {
        // TODO: optimize for primitive series.
        //  E.g. "DoubleSeries.avg()" is faster than "AggregatorFunctions.averageDouble()"
        return op.apply(exp.eval(filter != null ? s.select(filter) : s));
    }
}
