package org.dflib.exp.agg;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.IntSeries;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.exp.Exp1;
import org.dflib.series.IntSingleValueSeries;

import java.util.function.Function;

/**
 * @since 2.0.0
 */
public class IntReduceExp1<F> extends Exp1<F, Integer> implements NumExp<Integer> {

    private final Function<Series<F>, Integer> op;
    private final Condition filter;

    public IntReduceExp1(String opName, Exp<F> exp, Function<Series<F>, Integer> op, Condition filter) {
        super(opName, Integer.class, exp);
        this.op = op;
        this.filter = filter;
    }

    @Override
    public IntSeries eval(Series<?> s) {
        return new IntSingleValueSeries(reduce(s), s.size());
    }

    @Override
    public IntSeries eval(DataFrame df) {
        return new IntSingleValueSeries(reduce(df), df.height());
    }

    @Override
    public Integer reduce(DataFrame df) {
        // TODO: optimize for primitive series.
        //  E.g. "DoubleSeries.avg()" is faster than "AggregatorFunctions.averageDouble()"
        return op.apply(exp.eval(filter != null ? df.rows(filter).select() : df));
    }

    @Override
    public Integer reduce(Series<?> s) {
        // TODO: optimize for primitive series.
        //  E.g. "DoubleSeries.avg()" is faster than "AggregatorFunctions.averageDouble()"
        return op.apply(exp.eval(filter != null ? s.select(filter) : s));
    }
}
