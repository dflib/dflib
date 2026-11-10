package org.dflib.ql.fn;

import org.dflib.Condition;
import org.dflib.Exp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code vConcat(e, delimiter)}, {@code vConcat(e, filter, delimiter)}, {@code vConcat(e, delimiter, prefix,
 * suffix)} and {@code vConcat(e, filter, delimiter, prefix, suffix)} functions. Every arity delegates to the
 * four-argument {@code Exp.vConcat}, with a null filter and an empty prefix and suffix.
 *
 * @since 2.0.0
 */
public class VConcatFunction implements QLFunction {

    public Exp<?> call(Exp<?> e, String delimiter) {
        return e.vConcat(null, delimiter, "", "");
    }

    public Exp<?> call(Exp<?> e, Condition filter, String delimiter) {
        return e.vConcat(filter, delimiter, "", "");
    }

    public Exp<?> call(Exp<?> e, String delimiter, String prefix, String suffix) {
        return e.vConcat(null, delimiter, prefix, suffix);
    }

    public Exp<?> call(Exp<?> e, Condition filter, String delimiter, String prefix, String suffix) {
        return e.vConcat(filter, delimiter, prefix, suffix);
    }
}
