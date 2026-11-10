package org.dflib.ql.fn;

import org.dflib.Condition;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code count()} and {@code count(filter)} functions.
 *
 * @since 2.0.0
 */
public class CountFunction implements QLFunction {

    public NumExp<Integer> call() {
        return Exp.count();
    }

    public NumExp<Integer> call(Condition filter) {
        return Exp.count(filter);
    }
}
