package org.dflib.exp.agg;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.LongSeries;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.exp.Exp1;
import org.dflib.series.LongSingleValueSeries;

import java.util.function.Function;

/**
 * @since 2.0.0
 */
public class LongReduceExp1<F> extends Exp1<F, Long> implements NumExp<Long> {

    private final Function<Series<F>, Long> op;
    private final Condition filter;

    public LongReduceExp1(String opName, Exp<F> exp, Function<Series<F>, Long> op, Condition filter) {
        super(opName, Long.class, exp);
        this.op = op;
        this.filter = filter;
    }

    @Override
    public LongSeries eval(Series<?> s) {
        return new LongSingleValueSeries(reduce(s), s.size());
    }

    @Override
    public LongSeries eval(DataFrame df) {
        return new LongSingleValueSeries(reduce(df), df.height());
    }

    @Override
    public Long reduce(DataFrame df) {
        // TODO: optimize for primitive series.
        //  E.g. "DoubleSeries.avg()" is faster than "AggregatorFunctions.averageDouble()"
        return op.apply(exp.eval(filter != null ? df.rows(filter).select() : df));
    }

    @Override
    public Long reduce(Series<?> s) {
        // TODO: optimize for primitive series.
        //  E.g. "DoubleSeries.avg()" is faster than "AggregatorFunctions.averageDouble()"
        return op.apply(exp.eval(filter != null ? s.select(filter) : s));
    }
}
