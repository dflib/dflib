package org.dflib.ql.fn;

import org.dflib.DateExp;
import org.dflib.DateTimeExp;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.TimeExp;
import org.dflib.Udf2;
import org.dflib.ql.TypeClassifier;

import static org.dflib.ql.fn.FunctionUtils.scalar;
import static org.dflib.ql.fn.FunctionUtils.unchecked;
import static org.dflib.ql.fn.FunctionUtils.unsupportedArg;

/**
 * QL {@code quantile(e, q)} function.
 *
 * @since 2.0.0
 */
public class Quantile2Function implements Udf2<Object, Number, Object> {

    @Override
    public Exp<Object> call(Exp<Object> e, Exp<Number> q) {
        double p = scalar(q, double.class);
        return unchecked(switch ((Exp<?>) e) {
            case NumExp<?> n -> n.quantile(p);
            case DateExp d -> d.quantile(p);
            case TimeExp t -> t.quantile(p);
            case DateTimeExp dt -> dt.quantile(p);
            default -> throw unsupportedArg("quantile", e,
                    TypeClassifier.NUMERIC, TypeClassifier.DATE, TypeClassifier.TIME, TypeClassifier.DATETIME);
        });
    }
}
