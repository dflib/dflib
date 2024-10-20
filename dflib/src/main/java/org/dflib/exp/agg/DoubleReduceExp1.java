package org.dflib.exp.agg;

import org.dflib.Condition;
import org.dflib.DataFrame;
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

    private final Function<Series<F>, Double> aggregator;
    private final Condition filter;

    public DoubleReduceExp1(String opName, Exp<F> exp, Function<Series<F>, Double> aggregator, Condition filter) {
        super(opName, Double.class, exp);
        this.aggregator = aggregator;
        this.filter = filter;
    }

    @Override
    public Series<Double> eval(Series<?> s) {
        return super.eval(filter != null ? s.select(filter) : s);
    }

    @Override
    public Series<Double> eval(DataFrame df) {
        return super.eval(filter != null ? df.rows(filter).select() : df);
    }

    @Override
    protected Series<Double> doEval(Series<F> s) {
        // TODO: optimize for primitive series.
        //  E.g. "IntSeries.average()" is faster than "AggregatorFunctions.averageDouble()"
        double val = aggregator.apply(s);
        return new DoubleSingleValueSeries(val, 1);
    }
}
