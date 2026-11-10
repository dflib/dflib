package org.dflib.ql.fn;

import org.dflib.Condition;
import org.dflib.DateExp;
import org.dflib.DateTimeExp;
import org.dflib.NumExp;
import org.dflib.StrExp;
import org.dflib.TimeExp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code max(e)} and {@code max(e, filter)} functions.
 *
 * @since 2.0.0
 */
public class MaxFunction implements QLFunction {

    public NumExp<?> call(NumExp<?> e) {
        return e.max();
    }

    public NumExp<?> call(NumExp<?> e, Condition filter) {
        return e.max(filter);
    }

    public StrExp call(StrExp e) {
        return e.max();
    }

    public StrExp call(StrExp e, Condition filter) {
        return e.max(filter);
    }

    public DateExp call(DateExp e) {
        return e.max();
    }

    public DateExp call(DateExp e, Condition filter) {
        return e.max(filter);
    }

    public TimeExp call(TimeExp e) {
        return e.max();
    }

    public TimeExp call(TimeExp e, Condition filter) {
        return e.max(filter);
    }

    public DateTimeExp call(DateTimeExp e) {
        return e.max();
    }

    public DateTimeExp call(DateTimeExp e, Condition filter) {
        return e.max(filter);
    }
}
