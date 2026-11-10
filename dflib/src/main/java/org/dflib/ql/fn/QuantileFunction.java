package org.dflib.ql.fn;

import org.dflib.Condition;
import org.dflib.DateExp;
import org.dflib.DateTimeExp;
import org.dflib.NumExp;
import org.dflib.TimeExp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code quantile(e, q)} and {@code quantile(e, q, filter)} functions.
 *
 * @since 2.0.0
 */
public class QuantileFunction implements QLFunction {

    public NumExp<?> call(NumExp<?> e, double q) {
        return e.quantile(q);
    }

    public NumExp<?> call(NumExp<?> e, double q, Condition filter) {
        return e.quantile(q, filter);
    }

    public DateExp call(DateExp e, double q) {
        return e.quantile(q);
    }

    public DateExp call(DateExp e, double q, Condition filter) {
        return e.quantile(q, filter);
    }

    public TimeExp call(TimeExp e, double q) {
        return e.quantile(q);
    }

    public TimeExp call(TimeExp e, double q, Condition filter) {
        return e.quantile(q, filter);
    }

    public DateTimeExp call(DateTimeExp e, double q) {
        return e.quantile(q);
    }

    public DateTimeExp call(DateTimeExp e, double q, Condition filter) {
        return e.quantile(q, filter);
    }
}
